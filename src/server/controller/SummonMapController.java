package server.controller;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SummonInstance;

/** 플레이어 및 서먼몬스터가 특정맵에 있을 경우 서먼몬스터 삭제 * */
public class SummonMapController implements Runnable {
	private static Logger _log = Logger.getLogger(SummonMapController.class
			.getName());

	private static SummonMapController _instance;

	public static SummonMapController getInstance() {
		if (_instance == null)
			_instance = new SummonMapController();
		return _instance;
	}

	public SummonMapController() {
		GeneralThreadPool.getInstance().execute(this);
	}

	private Collection<L1PcInstance> list = null;

	private Object[] petlist = null;

	public void run() {
		while (true) {
			try {
				list = L1World.getInstance().getAllPlayers();
				for (L1PcInstance pc : list) {
					if (pc == null || pc.getNetConnection() == null) {
						continue;
					} else {
						/** 지저 호수 * */
						if (pc.getMapId() == 420) {
							try {
								petlist = pc.getPetList().values().toArray();
								for (Object pet : petlist) {
									L1SummonInstance sum = (L1SummonInstance) pet;
									sum.receiveDamage(pc, 100000);
								}
							} catch (Exception e) {
							} finally {
								petlist = null;
							}
						}
					}
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "SummonMapController[]Error", e);
				// cancel();
			} finally {
				try {
					list = null;
					Thread.sleep(5000);
				} catch (Exception e) {
				}
			}
		}
	}

}