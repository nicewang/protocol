package com.nice.protocol.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 
 * @author NiceWang
 *
 */
public class NioClient {
	
	private SocketChannel channel;
	private Selector selector;
	private ByteBuffer sendbuffer = ByteBuffer.allocate(1024);
	private ByteBuffer readbuffer = ByteBuffer.allocate(1024);
	
	/**
	 * （客户端）采用nio的方式通过socket与服务器建立连接
	 */
	public void connect() {
		
		//1.初始化通道
		initChannel();
		//2.打开选择器
		try {
			selector = Selector.open();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Open selector error");
		}
		//3.
		try {
			
			channel.connect(new InetSocketAddress(getIp(), getPort()));
			channel.register(selector, SelectionKey.OP_CONNECT);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Connect error or register error!");
		}
		//4.	
		try {
			if(selector.select(10000)>0) {
				
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while(iterator.hasNext()){
					
					SelectionKey selectionKey = iterator.next();
					iterator.remove();
					if(selectionKey.isConnectable())
					{
						if(channel.isConnectionPending()){  //此处完成TCP建立连接的三次握手
							channel.finishConnect();
						}
					}
					
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Socket connecting time out!");
		}
		
	}
	
	/**
	 * （客户端）断开与服务器的连接
	 */
	public void disconnect() {
		
		//1.注销通道与选择器的关系
		channel.keyFor(selector).cancel();
		//2.关闭通道
		try {
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Close channel error!");
		}
		//3.关闭选择器（因为客户端一个通道对应一个选择器，断开连接通道关闭，选择器也就可以关闭了）
		try {
			selector.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Close selector error!");
		}
		
	}
	
	/**
	 * （客户端）向服务器发送数据
	 * @param data
	 */
	public void send(String data) {
		
		//1.判断通道是否为空，为空这说明该通道已被关闭
		if(channel == null) {
			System.out.println("Channel is closed!");
			return;
		}
		//2.判断通道是否还处于连接状态
		if(!channel.isConnected()) {
			System.out.println("Channel is diconnected!");
			return;
		}
		//3.读取要发送的数据，并将其写入缓冲区
		byte[] bytes = data.getBytes();
		if(bytes == null) {
			System.out.println("Error: The sending data is null!");
		}
		sendbuffer.clear();
		sendbuffer.put(bytes);
		sendbuffer.flip();
		//4.将缓冲区的数据写入通道
		try {
			while(sendbuffer.hasRemaining()){
				channel.write(sendbuffer);
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Write channel error!");
		}
		
	}
	
	/**
	 * （客户端）接收服务器发送过来的数据
	 * @return
	 */
	public String read() {
		int num = -1;
		String data = null;
		readbuffer.clear();
		
		try {
			num = channel.read(readbuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Read channel error!");
		}
		
		if(num > 0) { //说明有读到数据
			
			readbuffer.flip();
			byte[] bytes = new byte[readbuffer.remaining()];
			readbuffer.get(bytes);
			data = bytes.toString();
			
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
			
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Open channel error or nonBlocking configuring error!");
		}
		
	}
	
	public SocketChannel getChannel() {
		return channel;
	}
	
	public void setChannel(SocketChannel channel) {
		this.channel = channel;
	}
	
	public String getIp() {
		
		String ip = "127.0.0.1";
		return ip;
		
	}
	
	public int getPort() {
		
		int port = 6666;
		return port;
		
	}
	
	public Selector getSelector() {
		return selector;
	}

}
