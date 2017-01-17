package com.nice.protocol.socket.nio;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author NiceWang
 *
 */
public class NioConnectCenter {
	
	private static NioConnectCenter nioConnectCenter = new NioConnectCenter();

	private Map<SocketChannel, Selector> connections;
	private List<Selector> selectors;
	
	public static NioConnectCenter getInstance() {
		return nioConnectCenter;
	}
	
	private NioConnectCenter() {
		connections = new ConcurrentHashMap<SocketChannel, Selector>();
		selectors = new ArrayList<Selector>();
		addNewSelector();
	}
	

	/**
	 * 向连接中心注册通道
	 * @param channel
	 * @return
	 */
	public synchronized Selector registerChannel(SocketChannel channel) {
		
		boolean selectorCapacity = false;
		Selector selector = null;
		Iterator<Selector> iterator = selectors.iterator();
		while(iterator.hasNext()) {
			
			selector = iterator.next();
			if(selector != null && selector.keys().size() < 1024) {
				//连接中心的选择器有空间与通道建立联系（一个选择器可以管理1024个channel）
				selectorCapacity = true;
				connections.put(channel, selector);
				break;
			}
			
		}
		if(!selectorCapacity) {
			//否则就要向连接中心添加选择器（因为连接中心现有的选择器都被沾满了）
			selector = addNewSelector();
			selectorCapacity = true;
			connections.put(channel, selector);
		}
		
		
		return selector;
	}
	
	/**
	 * 把通道从连接中心注销
	 * @param channel
	 */
	public synchronized void deregisterChannel(SocketChannel channel) {
		
		Selector selector = connections.remove(channel);
		if(selector != null) {
			
			try {
				channel.keyFor(selector).cancel();
			} catch(Exception e) {
				System.out.println("Channel has disconnected before deregister channel operation!");
			}
			
		} else {
			System.out.println("This Channel didn't be registered in ConnectCenter!");
		}
		
	}
	
	/**
	 * 当连接中心的选择器资源被占满时添加新的选择器
	 * @return
	 */
	private Selector addNewSelector() {
		
		Selector selector = null;
		try {
			selector = Selector.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Open selector error when add it into connect center!");
		}
		selectors.add(selector);
		return selector;
		
	}
	
}
