package server.monitor;

public class MessageMonitor implements Monitor {
	
	public void update(Object o) {
		System.out.println((String)o);
		
	}
}
