class HeartBeatDetector extends Thread {
	
	private List<client> clients = new List<client>();
	
	public void run() {
		
		try {
			
			for(client client1 : clients){
				
				if(client == null) {
					
					continue;
					
				}
				
				//检查是否还有连接
				if(!checkConnection(client1)) {
					
					//r如果出现断链现象
					disRegister(client1);
				}
				
			}
			
			
		} catch (Exception e) {
			
			System.out.println(e);
			
		} finally {
			
			
			
		}
		
	}
	
	private void Register(client client1) {
		
		
		
	}
	
	private void disRegister(client client1) {
		
		
		
	}
	
}