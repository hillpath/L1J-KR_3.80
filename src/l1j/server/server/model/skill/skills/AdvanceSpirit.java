package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;

public class AdvanceSpirit {
	public static void runSkill(L1Character cha) {
		L1PcInstance pc = (L1PcInstance) cha;
		pc.setAdvenHp(pc.getBaseMaxHp() / 5);
		pc.setAdvenMp(pc.getBaseMaxMp() / 5);
		pc.addMaxHp(pc.getAdvenHp());
		pc.addMaxMp(pc.getAdvenMp());
		pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
		if (pc.isInParty()) {
			pc.getParty().updateMiniHP(pc);
		}
		pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
	}
}
