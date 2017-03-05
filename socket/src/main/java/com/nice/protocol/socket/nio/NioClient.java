package com.nice.protocol.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 采用nio方式的客户端
 * @author NiceWang
 *
 */
public class NioClient {
	
	private SocketChannel channel;
	private Selector selector;
	private ByteBuffer sendbuffer = ByteBuffer.allocate(1024);
	private ByteBuffer readbuffer = ByteBuffer.allocate(1024);
	
	private String ip;
	private int port;
	
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
			return;
		}
		//3.
		try {
			
			channel.connect(new InetSocketAddress(getIp(), getPort()));
			channel.register(selector, SelectionKey.OP_CONNECT);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Connect error or register error!");
			return;
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
			return;
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
		
		if(!channel.isConnected()) {
			System.out.println("The channel isn't connected!");
		}
		
		try {
			num = channel.read(readbuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Read channel error!");
		}
		
		if(num == -1){  // 客户端已经断开连接
			
			System.out.println("Disconnected with server!");
			NioConnectCenter.getInstance().deregisterChannel(channel);  //要在连接中心注销该客户端通道
			try {
				channel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return data;
			
		}
		
		if(num > 0) { //说明有读到数据
			
			readbuffer.flip();
			byte[] bytes = new byte[readbuffer.remaining()];
			readbuffer.get(bytes);
			data = new String(readbuffer.array(), 0, num);
			
		}
		
		return data;
		
	}
	/**
	 * 初始化通道，主要是先关闭非空通道
	 */
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
	
	//以下的get，set方法是模仿Spring的依赖注入思想
	
	public SocketChannel getChannel() {
		return channel;
	}
	
	public void setChannel(SocketChannel channel) {
		this.channel = channel;
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
	
	public Selector getSelector() {
		return selector;
	}

}
