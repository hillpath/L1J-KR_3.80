package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;

public class Insight {
	public static void runSkill(L1Character cha) {
		L1PcInstance pc = (L1PcInstance) cha;
		pc.getAbility().addAddedStr((byte) 1);
		pc.getAbility().addAddedDex((byte) 1);
		pc.getAbility().addAddedCon((byte) 1);
		pc.getAbility().addAddedInt((byte) 1);
		pc.getAbility().addAddedWis((byte) 1);
		pc.resetBaseMr();
	}
}
