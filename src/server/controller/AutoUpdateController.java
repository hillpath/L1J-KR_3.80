package server.controller;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

public class AutoUpdateController implements Runnable {
	private static Logger _log = Logger.getLogger(AutoUpdateController.class
			.getName());

	private static AutoUpdateController _instance;

	public static AutoUpdateController getInstance() {
		if (_instance == null)
			_instance = new AutoUpdateController();
		
		return _instance;
	}

	public AutoUpdateController() {
		GeneralThreadPool.getInstance().execute(this);
	}

	private Collection<L1PcInstance> list = null;

	public void run() {
		
		while (true) {
			try {

				list = L1World.getInstance().getAllPlayers();
				for (L1PcInstance pc : list) {
					if (pc == null || (pc.getNetConnection() == null && !pc.noPlayerCK)) {
						continue;
					} else {
						// System.out.println("통합 AutoUpdateController 사용");
						pc.updateObject();
					}
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "AutoUpdateController[]Error", e);
				// cancel();
			} finally {
				try {
					list = null;
					Thread.sleep(300);
				} catch (Exception e) {
				}
			}
		}
	}

}