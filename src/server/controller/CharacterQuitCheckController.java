package server.controller;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

public class CharacterQuitCheckController implements Runnable {// extends
																// TimerTask{
	private static Logger _log = Logger
			.getLogger(CharacterAutoSaveController.class.getName());

	// private static Timer _characterQuitCheckTimer = new Timer();
	private final int _chkTime;

	public CharacterQuitCheckController(int chkTime) {
		_chkTime = chkTime;
	}

	public void start() {
		// _characterQuitCheckTimer.scheduleAtFixedRate(CharacterQuitCheckController.this,
		// 0, _chkTime);
		GeneralThreadPool.getInstance().scheduleAtFixedRateLong(
				CharacterQuitCheckController.this, 0, _chkTime);
	}

	private Collection<L1PcInstance> _list = null;

	public void run() {
		try {
			_list = L1World.getInstance().getAllPlayers();
			for (L1PcInstance _client : _list) {
				if (_client.isPrivateShop()) {
					continue;
				} else {
					try {
						if (_client.getNetConnection().isClosed()) {
							_client.logout();
							_client.getNetConnection().close();
						}

					} catch (Exception e) {
						_log.warning("Quit Character failure.");
						_log.log(Level.SEVERE, "CharacterQuitCheckController[]Error", e);
						throw e;
					}
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			// cancel();
		} finally {
			_list = null;
		}
	}

}
