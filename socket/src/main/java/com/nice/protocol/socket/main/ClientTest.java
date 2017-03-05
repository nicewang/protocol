package com.nice.protocol.socket.main;

import com.nice.protocol.socket.nio.ClientChannelProcess;
import com.nice.protocol.socket.nio.NioClient;
import com.nice.protocol.socket.util.Operations;

/**
 * 发送客户端
 * 开一个线程进行发送操作（连接操作不需要单独开线程）
 * @author NiceWang
 *
 */
public class ClientTest {

	private static NioClient nioClient = new NioClient(); 
	
	public static void main(String[] args) {
		
		nioClient.setIp("127.0.0.1");
		nioClient.setPort(6666);
		nioClient.connect();
		
		ClientChannelProcess clientThread = new ClientChannelProcess();
		clientThread.setOperationType(Operations.WriteChannel);
		clientThread.setNioClient(nioClient);
		clientThread.setSendData("Hello, Nice!");
		clientThread.setSelector(nioClient.getSelector());
		clientThread.start();
		
	}
	
}
