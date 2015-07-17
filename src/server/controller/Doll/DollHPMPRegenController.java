package server.controller.Doll;

import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastTable;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillSound;

public class DollHPMPRegenController implements Runnable {
	private static Logger _log = Logger.getLogger(DollHPMPRegenController.class
			.getName());

	private static DollHPMPRegenController _instance;

	private FastTable<L1PcInstance> HPlist;

	private FastTable<L1PcInstance> MPlist;

	public static DollHPMPRegenController getInstance() {
		if (_instance == null)
			_instance = new DollHPMPRegenController();
		return _instance;
	}

	public DollHPMPRegenController() {
		HPlist = new FastTable<L1PcInstance>();
		MPlist = new FastTable<L1PcInstance>();
		GeneralThreadPool.getInstance().execute(this);
	}

	private FastTable<L1PcInstance> li = null;

	public void run() {
		while (true) {
			try {
				li = HPlist;
				for (L1PcInstance pc : li) {
					if (pc == null) {
						removeHP(pc);
						continue;
					}
					if (pc.isDead())
						continue;
					if (pc.DollHPRegenTime <= System.currentTimeMillis()) {
						regenHp(pc);
						pc.DollHPRegenTime = System.currentTimeMillis() + 32000;
					}
				}
				li = null;
				li = MPlist;
				for (L1PcInstance pc : li) {
					if (pc == null) {
						removeMP(pc);
						continue;
					}
					if (pc.isDead())
						continue;
					if (pc.DollMPRegenTime <= System.currentTimeMillis()) {
						regenMp(pc);
						pc.DollMPRegenTime = System.currentTimeMillis() + 64000;
					}
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "DollHPMPRegenController[]Error", e);
			} finally {
				try {
					li = null;
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		}
	}

	public void addHP(L1PcInstance npc) {
		if (!HPlist.contains(npc))
			HPlist.add(npc);
	}

	public void removeHP(L1PcInstance npc) {
		if (HPlist.contains(npc))
			HPlist.remove(npc);
	}

	public void addMP(L1PcInstance npc) {
		if (!MPlist.contains(npc))
			MPlist.add(npc);
	}

	public void removeMP(L1PcInstance npc) {
		if (MPlist.contains(npc))
			MPlist.remove(npc);
	}

	public int getHPSize() {
		return HPlist.size();
	}

	public void regenHp(L1PcInstance pc) {
		int newHp = pc.getCurrentHp() + 25;
		if (newHp < 0) {
			newHp = 0;
		}
		pc.setCurrentHp(newHp);
		pc.sendPackets(new S_SkillSound(pc.getId(), 1608));

	}

	public void regenMp(L1PcInstance pc) {
		int regenMp = 0;
		for (L1DollInstance doll : pc.getDollList().values()) {
			regenMp = doll.getMpRegenerationValues();
		}
		int newMp = pc.getCurrentMp() + regenMp;
		pc.setCurrentMp(newMp);
		pc.sendPackets(new S_SkillSound(pc.getId(), 6321));
	}

}
