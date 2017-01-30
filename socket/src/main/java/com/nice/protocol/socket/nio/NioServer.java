package com.nice.protocol.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {

	private ServerSocketChannel channel;
	private SocketChannel client;
	private Selector selector;
	private ByteBuffer sendbuffer = ByteBuffer.allocate(1024);
	private ByteBuffer readbuffer = ByteBuffer.allocate(1024);
	
	private String ip;
	private int port;
	
	public void open() {
		
		//1.初始化通道
		initChannel();
		System.out.println(1);
		//2.打开选择器
		try {
			selector = Selector.open();
			System.out.println(2);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Open selector error");
			return;
		}
		//3.
		try {
			
			channel.bind(new InetSocketAddress(getPort()));
			channel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println(3);
			System.out.println("Welcome to Nice's chatting room!");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Connect error or register error!");
		}
		
	}
	
	public void listen() {
		
		try {
			if(selector.select(10000)>0) {
						
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while(iterator.hasNext()){
							
					SelectionKey selectionKey = iterator.next();
					iterator.remove();
					if(selectionKey.isAcceptable())
					{
						client = channel.accept();
						client.configureBlocking(false);
						Selector selector1 = NioConnectCenter.getInstance().registerChannel(client);
						client.register(selector1, SelectionKey.OP_READ | SelectionKey.OP_WRITE, new Integer(1));
						System.out.println("Connect a client!");
					}
							
				}
						
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Socket accepting time out!");
		}
		
	}
	
	public void close() {
		//1.注销通道与选择器的关系
		channel.keyFor(selector).cancel();
		//2.关闭通道
		try {
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Close channel error!");
		}
		//3.关闭选择器
		try {
			selector.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Close selector error!");
		}	
	}
	
	public void send(String data, SocketChannel client) {
		
		//1.判断通道是否为空，为空这说明该通道已被关闭
		if(client == null) {
			System.out.println("Client-channel is closed!");
			return;
		}
		//2.判断通道是否还处于连接状态
		if(!client.isConnected()) {
			System.out.println("Client-channel is diconnected!");
			return;
		}
		//3.读取要发送的数据，并将其写入缓冲区
		byte[] bytes = data.getBytes();
		if(bytes == null) {
			System.out.println("Error: The sending data is null!");
			return;
		}
		sendbuffer.clear();
		sendbuffer.put(bytes);
		sendbuffer.flip();
		//4.将缓冲区的数据写入通道
		try {
			while(sendbuffer.hasRemaining()){
				client.write(sendbuffer);
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Write client-channel error!");
		}
		
	}
	
	public String read(SocketChannel client) {
		
		String data = null;
		int num = -1;
		readbuffer.clear();
		
		if(!client.isConnected()) {
			System.out.println("The client-channel isn't connected!");
		}
		
		try {
			num = client.read(readbuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Read client-channel error!");
			data = "err";
		}
		
		if(num == -1){  // 客户端已经断开连接
			
			System.out.println("The client has disconnected!");
			NioConnectCenter.getInstance().deregisterChannel(client);  //要在连接中心注销该客户端通道
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return data;
			
		} else if(num > 0) {  //说明有读到数据
			
			readbuffer.flip();
			byte[] bytes = new byte[readbuffer.remaining()];
			readbuffer.get(bytes);
			data = new String(readbuffer.array(), 0, num);
			
		}
		
		return data;
		
	}
	
	private void initChannel() {
		
		//1.看到通道是否为空，若通道非空，则先关闭它
		if(channel != null) {
			
			try {
				channel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Close channel error!");
			}
			
		}
		//2.打开通道，并设置非阻塞模式
		try {
			
			channel = ServerSocketChannel.open();
			channel.configureBlocking(false);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Open channel error or nonBlocking configuring error!");
		}
		
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}
	
}
