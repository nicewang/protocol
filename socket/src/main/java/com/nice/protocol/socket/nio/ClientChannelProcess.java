package com.nice.protocol.socket.nio;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.nice.protocol.socket.util.Operations;

public class ClientChannelProcess extends Thread{
	
	private Selector selector;
	private Operations operationtype;
	private NioClient nioClient;
	private boolean clientFlag = true;
	private String sendData;
	private SocketChannel channel;
	
	public void run() {
		
		//1.获取操作类型：读通道/写通道
		if(operationtype == null) {
			System.out.println("Please set operation type first!");
			return;
		}
		//2.判断是否被提供了一个已经被实例化的客户端
		if(nioClient == null) {
			System.out.println("Please set client first!");
			return;
		}
		//3.判断选择器是否为空
		if(selector == null) {
			System.out.println("Process client channel error: selector is null!");
			return;
		}
		//4.进入主循环
		while(clientFlag) {
			
			if(sendData != null) {
				channel = nioClient.getChannel();
				try {
					channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, new Integer(1));
				} catch (ClosedChannelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
			}
			
			
			try {
				while(selector.select(100) > 0) {  //阻塞100毫秒
					Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
					while(iterator.hasNext()) {
						if(operationtype == Operations.ReadChannel) {
							//读通道里的数据
							String receiveData = nioClient.read();
							if(receiveData != null) {
								System.out.println("Receive from server: " + receiveData);;
							}
						} else if(operationtype == Operations.WriteChannel) {
							//向通道里写入数据
							if(sendData != null) {
								nioClient.send(sendData);
								sendData = null;
								channel.register(selector, SelectionKey.OP_CONNECT);
							}
						}
						SelectionKey sk = iterator.next();
						iterator.remove();
					}
				}
				Thread.sleep(1000);
				sendData = "Hello, Nice!";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Select error in client channel process!");
			} catch (InterruptedException interrupt) {
				System.out.println("Thread interrupt error!");
			} catch (Throwable t) {
				System.out.println("Thread throwable error!");
			}
		}
		
	}
	
	/**
	 * 提供给其它类调用的函数，设置是读通道还是写通道操作
	 * @param operationtype
	 */
	public void setOperationType(Operations operationtype) {
		this.operationtype = operationtype;
	}
	
	public Operations getOperationType() {
		return operationtype;
	}
	
	/**
	 * 提供给其它类调用的函数，在创建一个客户端实例之后调用
	 * @param nioClient
	 */
	public void setNioClient(NioClient nioClient) {
		this.nioClient = nioClient;
	}
	
	public NioClient getNioClient() {
		return nioClient;
	}
	
	/**
	 * 提供给其它类调用的函数，设置要通过通道发送的数据
	 * @param sendData
	 */
	public void setSendData(String sendData) {
		this.sendData = sendData;
	}
	
	public String getSendData() {
		return sendData;
	}
	
	/**
	 * 一般在客户端的入口函数里创建客户端实例建立连接并获取到selector之后调用，接着NioClient类的getSelector()使用
	 * @param selector
	 */
	public void setSelector(Selector selector) {
		this.selector = selector;
	}
	
	/**
	 * 退出处理客户端通道（读/写通道）操作
	 */
	public void exit() {
		
		clientFlag = false;
		try {
			selector.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Close selector error when process client channel!");
		}
		this.interrupt();
		
	}

}
