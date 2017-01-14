package com.nice.protocol.socket.nio;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NioConnectCenter {
	
	private static NioConnectCenter nioConnectCenter = new NioConnectCenter();

	private Map<SocketChannel, Selector> connections = new ConcurrentHashMap<SocketChannel, Selector>();
	private List<Selector> selectors = new ArrayList<Selector>();
	
	public static NioConnectCenter getInstance() {
		return nioConnectCenter;
	}
	
	private NioConnectCenter() {
		addNewSelector();
	}
	
	public Selector register(SocketChannel channel) {
		
		Selector selector;
		try {
			selector = Selector.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Open Selector error when register channel");
		}
		
		selector = connections.get(channel);
		try {
			if(selector.select(10000) > 0) {
				
				
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Socket connecting time out when register channel!");
		}
		return selector;
	}
	
	private Selector addNewSelector() {
		
		Selector selector = null;
		selectors.add(selector);
		return selector;
		
	}
	
}
