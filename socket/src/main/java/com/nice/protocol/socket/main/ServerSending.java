package com.nice.protocol.socket.main;

import com.nice.protocol.socket.nio.NioServer;
import com.nice.protocol.socket.nio.ServerSend;

public class ServerSending {
	private static NioServer nioServer = new NioServer();	
	
	public static void main(String[] args) {
		
		nioServer.setPort(6666);
		nioServer.open();
		
		new Thread(){
			public void run() {
				while(true) {
					nioServer.listen();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		final ServerSend send = new ServerSend();
		send.setNioServer(nioServer);
		
		new Thread() {
			
			public void run() {
				while(true) {
					send.setSendData("Ahahah!");
					send.sending();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}.start();
		
	}

}
