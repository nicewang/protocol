package com.nice.protocol.socket.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

/**
 * 服务端的接收线程
 * @author NiceWang
 *
 */
public class ServerReceiverThread extends Thread{
	
	private NioServer nioServer;
	private int recErrCount = 0;
	private String receiveData = null;
	private String receiveData1 = null; //用于向入口函数传送该类从客户端接收到的信息
	
	public void run() {
		
		while(true) {
			if(nioServer == null) {
				System.out.println("Please set server first!");
				return;
			}
			List<Selector> selectors = NioConnectCenter.getInstance().getSelector();
			Iterator<Selector> iterator = selectors.iterator();
			while(iterator.hasNext()) {
				Selector selector = iterator.next();
//				iterator.remove();   //要是remove掉，下次就不会再进这个selector了
				try {
					if(selector.select(100) > 0) {
						Iterator<SelectionKey> iterator1 = selector.selectedKeys().iterator();
						while(iterator1.hasNext()) {
							SelectionKey sk = iterator1.next();
							iterator1.remove();   //要是remove掉，下次不管这个通道是否就绪，都不会读这个通道了--你确定其？！
							//这里remove与否没区别啊？！
							SocketChannel channel = (SocketChannel) sk.channel();
							receiveData = nioServer.read(channel);
							if(receiveData != null && receiveData != "err") {
								System.out.println(receiveData);
								System.out.println(selector.keys().size());
								setRecvData(receiveData);
							} else if(receiveData == "err") {
								//类似于心跳检测机制，出现“err”五次则视为客户端已断连
								recErrCount++;
								if(recErrCount == 5) {
									recErrCount = 0;
									NioConnectCenter.getInstance().deregisterChannel(channel);
									channel.close();
									System.out.println("A client channel closed!");
									System.out.println(selector.keys().size());
								}
							}
							receiveData = null;
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void setNioServer(NioServer nioServer) {
		this.nioServer = nioServer;
	}
	
	/**
	 * 把从客户端接收到的信息赋值给receiveData1以便入口函数能够获取该信息的接口
	 * 以及入口函数在接收到该信息之后将receiveData1置为空值的接口
	 * @param receiveData1
	 */
	public void setRecvData(String receiveData1) {
		this.receiveData1 = receiveData1;
	}
	
	/**
	 * 入口函数获取该类中从客户端所接收到的信息的接口
	 * @return
	 */
	public String getRecvData() {
		return receiveData1;
	}

}
