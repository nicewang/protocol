package com.nice.protocol.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioClient {
	
	protected SocketChannel channel;
	protected Selector selector;
	
	public void connect() {
		
		initChannel();
		try {
			
			channel.connect(new InetSocketAddress(getIp(), getPort()));
			channel.register(selector, SelectionKey.OP_CONNECT);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Connecting error for channel or registering error for channel and selector!");
		}
		
		
		while(true) {
			
			try {
				while (selector.select(10000)>0) {
					
					Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
					while(iterator.hasNext()){
						
						if(channel.isConnectionPending()){
							
							channel.finishConnect();
							
						}
						iterator.remove();
						
					}
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Socket connecting time");
			}
			
		}
		
		
	}
	
	private void initChannel() {
		
		if(channel != null) {
			
			try {
				
				channel.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Close channel error!");
			}
			
		}
		
		try {
			
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Open channel error or nonBlocking configuring error!");
		}
		
	}
	
	String getIp() {
		
		String ip = "127.0.0.1";
		return ip;
		
	}
	
	int getPort() {
		
		int port = 6666;
		return port;
		
	}

}
