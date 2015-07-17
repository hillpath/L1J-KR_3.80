package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillIconAura;

public class StormShot {

	public static void runSkill(L1Character cha, int buffIconDuration) {
		L1PcInstance pc = (L1PcInstance) cha;
		pc.addBowDmgup(5);
		pc.addBowHitup(-3);
		pc.sendPackets(new S_SkillIconAura(165, buffIconDuration));
	}

}
