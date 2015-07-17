package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SkillIconAura;

public class GlowingAura {

	public static void runSkill(L1Character cha, int buffIconDuration) {
		L1PcInstance pc = (L1PcInstance) cha;
		pc.addHitup(5);
		pc.addBowHitup(5);
		pc.getResistance().addMr(20);
		pc.sendPackets(new S_SPMR(pc));
		pc.sendPackets(new S_SkillIconAura(113, buffIconDuration));
	}

}
