package com.nice.protocol.socket.nio;

import java.nio.channels.Selector;

public class ClientChannelProcess extends Thread{
	
	private Selector selector;
	
	public void run() {
		
	}
	
	public void setSelector(Selector selector) {
		this.selector = selector;
	}

}
