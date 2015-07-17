package server.controller.Doll;

import javolution.util.FastTable;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1DollInstance;

public class DollDeleteController implements Runnable {
	private static Logger _log = Logger.getLogger(DollDeleteController.class
			.getName());

	private FastTable<L1DollInstance> list;

	private static DollDeleteController _instance;

	public static DollDeleteController getInstance() {
		if (_instance == null)
			_instance = new DollDeleteController();
		return _instance;
	}

	public DollDeleteController() {
		list = new FastTable<L1DollInstance>();
		GeneralThreadPool.getInstance().execute(this);
	}

	private FastTable<L1DollInstance> li = null;

	public void run() {
		while (true) {
			try {
				li = list;
				for (L1DollInstance doll : li) {
					if (doll == null)
						continue;
					/** delete 호출되어 파기된 상태 * */
					if (doll._destroyed) {
						removeNpcDelete(doll);
						continue;
					}
					/** 주인이 없을 경우(deleteDoll() 호출 후 * */
					if (doll.getMaster() == null) {
						removeNpcDelete(doll);
						continue;
					}
					/** 인형 시간 다됨 * */
					if (doll.DollTime <= System.currentTimeMillis()) {
						doll.deleteDoll();
						removeNpcDelete(doll);
					}
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "DollDeleteController[]Error", e);
				// cancel();
			} finally {
				try {
					li = null;
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		}
	}

	public void addNpcDelete(L1DollInstance npc) {
		if (!list.contains(npc))
			list.add(npc);
	}

	public void removeNpcDelete(L1DollInstance npc) {
		if (list.contains(npc))
			list.remove(npc);
	}

	public int getSize() {
		return list.size();
	}

}
