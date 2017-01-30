package com.nice.protocol.socket.main;

import com.nice.protocol.socket.nio.NioServer;
import com.nice.protocol.socket.nio.ServerReceiverThread;

public class ServerTest {
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
		ServerReceiverThread receiverThread = new ServerReceiverThread();
		receiverThread.setNioServer(nioServer);
		receiverThread.start();
		
	}
	
}
