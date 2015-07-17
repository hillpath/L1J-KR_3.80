package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;

public class ElementalProtection {

	public static void runSkill(L1Character cha) {
		L1PcInstance pc = (L1PcInstance) cha;
		int attr = pc.getElfAttr();
		if (attr == 1)
			pc.getResistance().addEarth(50);
		else if (attr == 2)
			pc.getResistance().addFire(50);
		else if (attr == 4)
			pc.getResistance().addWater(50);
		else if (attr == 8)
			pc.getResistance().addWind(50);
	}
}
