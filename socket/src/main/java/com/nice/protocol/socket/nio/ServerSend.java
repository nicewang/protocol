package com.nice.protocol.socket.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

/**
 * 服务端的发送线程
 * @author NiceWang
 *
 */
public class ServerSend{
	
	private NioServer nioServer;
	private String sendData = null;
	
	public void sending() {
			if(nioServer == null) {
				System.out.println("Please set server first!");
				return;
			}
			List<Selector> selectors = NioConnectCenter.getInstance().getSelector();
			Iterator<Selector> iterator = selectors.iterator();
			while(iterator.hasNext()) {
				Selector selector = iterator.next();
//				iterator.remove();
				try {
					if(selector.select(100) > 0) {
						Iterator<SelectionKey> iterator1 = selector.selectedKeys().iterator();
						while(iterator1.hasNext()) {
							SelectionKey sk = iterator1.next();
							iterator1.remove();
							SocketChannel channel = (SocketChannel) sk.channel();
							if(sendData != null) {
								nioServer.send(sendData, channel);
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			sendData = null;
	}
	
	public void setNioServer(NioServer nioServer) {
		this.nioServer = nioServer;
	}

	public void setSendData(String sendData) {
		this.sendData = sendData;
	}
	
}
