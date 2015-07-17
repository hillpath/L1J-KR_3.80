package server;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;

public class DecoderManager {
	private static DecoderManager _instance;

	public static DecoderManager getInstance() {
		if (_instance == null) {
			_instance = new DecoderManager();
		}
		return _instance;
	}
	private LineageDecoderThread[] dts = new LineageDecoderThread[Config.MAX_ONLINE_USERS / 100]; 
	
	private DecoderManager(){
		for(int i=0; i<dts.length; i++){
			LineageDecoderThread dt = new LineageDecoderThread();
			GeneralThreadPool.getInstance().execute(dt);
			dts[i] = dt;
		}
	}
	
	public int getRowIndex(){
		int temp = 9999;
		int index = 0;
		for(int i=0; i<dts.length; i++){
			int temp1 = dts[i].ClientCount();
			if(temp1 < temp){
				temp = temp1;
				index = i;
			}
		}
		return index;
	}

	public void putClient(LineageClient lc, int ix) {
		dts[ix].putClient(lc);
	}

	public void removeClient(LineageClient lc, int ix) {
		dts[ix].removeClient(lc);
	}

}
