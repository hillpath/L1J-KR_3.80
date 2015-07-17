package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;

public class BloodySoul {

	public static void runSkill(L1Character cha) {
		L1PcInstance pc = (L1PcInstance) cha;
		pc.setCurrentMp(pc.getCurrentMp() + 12);
	}

}
