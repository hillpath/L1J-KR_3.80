package server.controller.pc;

import javolution.util.FastTable;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1PcInstance;

public class PcInvisDelayController implements Runnable {

	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(PcInvisDelayController.class.getName());

	private FastTable<L1PcInstance> list;

	private static PcInvisDelayController _instance;

	public static PcInvisDelayController getInstance() {
		if (_instance == null)
			_instance = new PcInvisDelayController();
		return _instance;
	}

	public PcInvisDelayController() {
		list = new FastTable<L1PcInstance>();
		GeneralThreadPool.getInstance().execute(this);
	}

	private FastTable<L1PcInstance> li;

	public void run() {
		while (true) {
			try {
				li = list;
				for (L1PcInstance pc : li) {
					if (pc == null || pc.getNetConnection() == null) {
						removePc(pc);
						continue;
					}
					if (pc.InvisDelayTime <= System.currentTimeMillis()) {
						pc.addInvisDelayCounter(-1);
						removePc(pc);
					}
				}
			} catch (Exception e) {
			} finally {
				try {
					li = null;
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		}
	}

	public void addPc(L1PcInstance npc) {
		if (!list.contains(npc))
			list.add(npc);
	}

	public void removePc(L1PcInstance npc) {
		if (list.contains(npc))
			list.remove(npc);
	}

}
