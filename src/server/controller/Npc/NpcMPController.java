package server.controller.Npc;

import javolution.util.FastTable;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.GeneralThreadPool;

public class NpcMPController implements Runnable {
	private static Logger _log = Logger.getLogger(NpcMPController.class
			.getName());

	private static NpcMPController _instance;

	private FastTable<L1NpcInstance> list;

	public static NpcMPController getInstance() {
		if (_instance == null)
			_instance = new NpcMPController();
		return _instance;
	}

	public NpcMPController() {
		list = new FastTable<L1NpcInstance>();
		GeneralThreadPool.getInstance().execute(this);
	}

	private FastTable<L1NpcInstance> li = null;

	public void run() {
		int mpr = 0;
		while (true) {
			try {

				li = list;
				for (L1NpcInstance _npc : li) {
					if (_npc == null || !_npc._mprRunning) {
						removeNpc(_npc);
						continue;
					}
					if (_npc.MPregenTime <= System.currentTimeMillis()) {
						if ((!_npc._destroyed && !_npc.isDead())
								&& (_npc.getCurrentHp() > 0 && _npc
										.getCurrentMp() < _npc.getMaxMp())) {
							mpr = _npc.getNpcTemplate().get_mpr();
							_npc.setCurrentMp(_npc.getCurrentMp() + mpr);
							_npc.MPregenTime = _npc.getNpcTemplate()
									.get_mprinterval()
									+ System.currentTimeMillis();
						} else {
							removeNpc(_npc);
						}
					}
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "NpcMPController[]Error", e);
			} finally {
				try {
					mpr = 0;
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
				npc._mprRunning = false;
				npc.MPregenTime = 0;
			}
		}
	}

	public int getSize() {
		return list.size();
	}

}