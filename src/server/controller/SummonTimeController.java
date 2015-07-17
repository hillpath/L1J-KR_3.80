package server.controller;

import javolution.util.FastTable;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.GeneralThreadPool;

public class SummonTimeController implements Runnable {
	private static Logger _log = Logger.getLogger(SummonTimeController.class
			.getName());

	private static SummonTimeController _instance;

	private FastTable<L1SummonInstance> list;

	public static SummonTimeController getInstance() {
		if (_instance == null)
			_instance = new SummonTimeController();
		return _instance;
	}

	public SummonTimeController() {
		list = new FastTable<L1SummonInstance>();
		GeneralThreadPool.getInstance().execute(this);
	}

	private FastTable<L1SummonInstance> li = null;

	public void run() {
		while (true) {
			try {
				// System.out.println("SummonTimeController ¸®½ºÆ® °¹¼ö:
				// "+list.size());
				li = list;
				for (L1SummonInstance npc : li) {
					if (npc == null || npc._destroyed) {
						removeNpc(npc);
						continue;
					}
					if (npc.SumTime <= System.currentTimeMillis()) {
					//	if (npc._tamed) {
					//		npc.liberate();
					//	} else {
							npc.Death(null);
					//	}
						removeNpc(npc);
					}
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "SummonTimeController[]Error", e);
			} finally {
				try {
					li = null;
					Thread.sleep(2000);
				} catch (Exception e) {
				}
			}
		}
	}

	public void addNpc(L1SummonInstance npc) {
		if (!list.contains(npc))
			list.add(npc);
	}

	public void removeNpc(L1SummonInstance npc) {
		if (list.contains(npc))
			list.remove(npc);
	}

	public int getSize() {
		return list.size();
	}

}