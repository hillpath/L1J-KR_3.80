package server.controller;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

public class CharacterAutoSaveController implements Runnable {// extends
																// TimerTask{
	private static Logger _log = Logger
			.getLogger(CharacterAutoSaveController.class.getName());

	// private static Timer _characterAutoSaveTimer = new Timer();
	private final int _saveTime;

	private long lastSavedTime = System.currentTimeMillis();

	private long lastSavedTime_inventory = System.currentTimeMillis();

	public CharacterAutoSaveController(int saveTime) {
		_saveTime = saveTime;
	}

	public void start() {
		// _characterAutoSaveTimer.scheduleAtFixedRate(CharacterAutoSaveController.this,
		// 0, _saveTime);
		GeneralThreadPool.getInstance().scheduleAtFixedRateLong(
				CharacterAutoSaveController.this, 0, _saveTime);
	}

	private Collection<L1PcInstance> _list = null;

	public void run() {
		try {
			_list = L1World.getInstance().getAllPlayers();
			for (L1PcInstance _client : _list) {
				if (_client == null) {
					continue;
				} else {
					try {
						// 캐릭터 정보
						if (_saveTime * 1000 < System.currentTimeMillis()
								- lastSavedTime) {
							_client.save();
							lastSavedTime = System.currentTimeMillis();
						}

						// 소지 아이템 정보
						if (300 * 1000 < System.currentTimeMillis()
								- lastSavedTime_inventory) {
							_client.saveInventory();
							lastSavedTime_inventory = System
									.currentTimeMillis();
						}
						// System.out.println("Client autosave success");
					} catch (Exception e) {
						_log.warning("Client autosave failure.");
						_log.log(Level.SEVERE, "CharacterAutoSaveController[]Error", e);
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
