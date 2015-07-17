package server.controller.Npc;

import javolution.util.FastTable;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.GeneralThreadPool;

public class NpcHPController implements Runnable {
	private static Logger _log = Logger.getLogger(NpcHPController.class
			.getName());

	private static NpcHPController _instance;

	private FastTable<L1NpcInstance> list;

	public static NpcHPController getInstance() {
		if (_instance == null)
			_instance = new NpcHPController();
		return _instance;
	}

	public NpcHPController() {
		list = new FastTable<L1NpcInstance>();
		GeneralThreadPool.getInstance().execute(this);
	}

	private FastTable<L1NpcInstance> li = null;

	public void run() {
		int hpr = 0;
		while (true) {
			try {

				li = list;
				for (L1NpcInstance _npc : li) {
					if (_npc == null || !_npc._hprRunning) {
						removeNpc(_npc);
						continue;
					}
					if (_npc.HPregenTime <= System.currentTimeMillis()) {
						if ((!_npc._destroyed && !_npc.isDead())
								&& (_npc.getCurrentHp() > 0 && _npc
										.getCurrentHp() < _npc.getMaxHp())) {
							hpr = _npc.getNpcTemplate().get_hpr();
							_npc.setCurrentHp(_npc.getCurrentHp() + hpr);
							_npc.HPregenTime = _npc.getNpcTemplate()
									.get_hprinterval()
									+ System.currentTimeMillis();
						} else {
							removeNpc(_npc);
						}
					}
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "NpcHPController[]Error", e);
			} finally {
				try {
					hpr = 0;
					li = null;
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		}
	}

	public void addNpc(L1NpcInstance npc) {
		if (!list.contains(npc))
			list.add(npc);
	}

	public void removeNpc(L1NpcInstance npc) {
		if (list.contains(npc)) {
			list.remove(npc);
			if (npc != null) {
				npc._hprRunning = false;
				npc.HPregenTime = 0;
			}
		}
	}

	public int getSize() {
		return list.size();
	}

}