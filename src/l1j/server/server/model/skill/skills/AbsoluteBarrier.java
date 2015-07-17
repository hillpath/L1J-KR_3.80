package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;

public class AbsoluteBarrier {

	public static void runSkill(L1Character cha) {
		L1PcInstance pc = (L1PcInstance) cha;
		// pc.stopHpRegeneration();
		// pc.stopMpRegeneration();
		pc.stopMpRegenerationByDoll();
	}

}
