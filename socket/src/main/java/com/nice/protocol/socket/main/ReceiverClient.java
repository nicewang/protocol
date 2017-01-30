package com.nice.protocol.socket.main;

import com.nice.protocol.socket.nio.ClientChannelProcess;
import com.nice.protocol.socket.nio.NioClient;
import com.nice.protocol.socket.util.Operations;

public class ReceiverClient {

private static NioClient nioClient = new NioClient(); 
	
	public static void main(String[] args) {
		
		nioClient.setIp("127.0.0.1");
		nioClient.setPort(6666);
		nioClient.connect();
		
		ClientChannelProcess clientThread = new ClientChannelProcess();
		clientThread.setOperationType(Operations.ReadChannel);
		clientThread.setNioClient(nioClient);
		clientThread.setSelector(nioClient.getSelector());
		clientThread.start();
		
	}
	
}
