package com.nice.protocol.socket.main;

import com.nice.protocol.socket.nio.NioServer;
import com.nice.protocol.socket.nio.ServerReceiverThread;
import com.nice.protocol.socket.nio.ServerSend;

/**
 * 服务端的转发中心
 * 三线程：一个线程监听客户端连接，一个线程读多个客户端数据（在客户但通道就绪时），一个线程向所有就绪客户端转发所接收到的消息
 * @author NiceWang
 *
 */
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
		
		final ServerReceiverThread receiverThread = new ServerReceiverThread();
		receiverThread.setNioServer(nioServer);
		receiverThread.start();
		
		final ServerSend send = new ServerSend();
		send.setNioServer(nioServer);
		
		new Thread() {
			
			public void run() {
				while(true) {
					String recvData = receiverThread.getRecvData();
					if(recvData != null) {
						send.setSendData(recvData);
						send.sending();
						recvData = null;
						receiverThread.setRecvData(null);
					}
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
