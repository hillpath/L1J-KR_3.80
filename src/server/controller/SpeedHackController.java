package server.controller;

import java.util.Collection;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

public class SpeedHackController implements Runnable {// extends Thread{
	private static SpeedHackController _instance;

	public static SpeedHackController getInstance() {
		if (_instance == null) {
			_instance = new SpeedHackController();
		}
		return _instance;
	}

	public SpeedHackController() {
		GeneralThreadPool.getInstance().scheduleAtFixedRate(this, 0, 1000);
	}

	private Collection<L1PcInstance> list = null;

	public void hack_timer() {
		try {
			list = L1World.getInstance().getAllPlayers();
			for (L1PcInstance pc : list) {
				if (pc == null || pc.getNetConnection() == null
						|| pc.isPrivateShop())
					continue;
				else
					pc.increase_hackTimer();
			}
		} catch (Exception e) {
		} finally {
			list = null;
		}
	}

	public void run() {
		// do{
		try {
			hack_timer();
			// speedHackEngine_timer();
			// sleep(1000);
		} catch (Exception e) {
		}
		// } while(true);
	}

}
