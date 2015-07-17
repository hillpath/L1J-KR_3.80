package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;

public class WindShackle {

	public static void runSkill(L1Character cha, int buffIconDuration) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			pc.sendPackets(new S_SkillIconWindShackle(pc.getId(),
					buffIconDuration));
		}
	}

}
