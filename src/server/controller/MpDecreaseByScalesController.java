package server.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastTable;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1PcInstance;

public class MpDecreaseByScalesController implements Runnable {
	private static Logger _log = Logger
			.getLogger(MpDecreaseByScalesController.class.getName());

	private static MpDecreaseByScalesController _instance;

	private FastTable<L1PcInstance> list;

	public static MpDecreaseByScalesController getInstance() {
		if (_instance == null)
			_instance = new MpDecreaseByScalesController();
		return _instance;
	}

	public MpDecreaseByScalesController() {
		list = new FastTable<L1PcInstance>();
		GeneralThreadPool.getInstance().execute(this);
	}

	private FastTable<L1PcInstance> li = null;

	public void run() {
		while (true) {
			try {
				li = list;
				// System.out.println("MpDecreaseByScalesController HP ¸®½ºÆ® °¹¼ö:
				// "+list.size());
				for (L1PcInstance pc : li) {
					if (pc == null) {
						removePc(pc);
						continue;
					}
					if (pc.isDead() || pc.getCurrentMp() < 4) {
						//killSkill(pc);
						removePc(pc);
						continue;
					}
					if (pc.ScalesMpDecreaseTime <= System.currentTimeMillis()) {
						regenMp(pc);
						pc.ScalesMpDecreaseTime = System.currentTimeMillis() + 4000;
					}
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "MpDecreaseByScalesController[]Error", e);
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
		if (list.contains(npc)) {
			list.remove(npc);
			if (npc != null)
				npc.ScalesMpDecreaseTime = 0;
		}
	}

	public int getSize() {
		return list.size();
	}

	private int newMp = 0;

	public void regenMp(L1PcInstance pc) {
		newMp = pc.getCurrentMp() - 4;
		if (newMp < 0)
			newMp = 0;
		pc.setCurrentMp(newMp);
		newMp = 0;
	}

	/*public void killSkill(L1PcInstance pc) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(SCALES_EARTH_DRAGON)) {
			pc.getSkillEffectTimerSet().removeSkillEffect(SCALES_EARTH_DRAGON);
		} else if (pc.getSkillEffectTimerSet().hasSkillEffect(
				SCALES_WATER_DRAGON)) {
			pc.getSkillEffectTimerSet().removeSkillEffect(SCALES_WATER_DRAGON);
		} else if (pc.getSkillEffectTimerSet().hasSkillEffect(
				SCALES_FIRE_DRAGON)) {
			pc.getSkillEffectTimerSet().removeSkillEffect(SCALES_FIRE_DRAGON);
		}
	}*/

}