package server.connection;

import java.util.Hashtable;

public class ConnectionCounter {
	private Hashtable<String, String> attemptCount;

	public ConnectionCounter() {
		attemptCount = new Hashtable<String, String>();
	}
	
	public boolean addIp(String Ip){
		final int MAX_COUNT = 6;
		if(attemptCount.get(Ip) == null){
			attemptCount.put(Ip, "1");
			return true;
		} else {
			int count = Integer.parseInt((String)attemptCount.get(Ip));
			if(MAX_COUNT < count) return false;
			else {
				attemptCount.put(Ip, new Integer(count  + 1).toString());
				return true;
			} 
		}
	}

	public void removeIp(String Ip){
		attemptCount.remove(Ip); 
	}
}
