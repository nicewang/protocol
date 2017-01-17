package com.nice.protocol.socket.main;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author NiceWang
 *
 */
public class Main 
{
    public static void main( String[] args )
    {
    	Map<SocketChannel, Selector> connections = new ConcurrentHashMap<SocketChannel, Selector>();
    	Selector selector = null;
    	SocketChannel channel1 = null;
    	SocketChannel channel2 = null;
    	try {
			selector = Selector.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	try {
			channel1 = SocketChannel.open();
			channel2 = SocketChannel.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	connections.put(channel1, selector);
    	connections.put(channel2, selector);
    	Selector selector1 = connections.get(channel1);
    	Selector selector2 = connections.get(channel2);
    	
        System.out.println("selector1's hashcode: " + selector1.hashCode());
        System.out.println("selector2's hashcode: " + selector2.hashCode());
        
    }
}
