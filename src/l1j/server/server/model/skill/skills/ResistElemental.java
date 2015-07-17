package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;

public class ResistElemental {

	public static void runSkill(L1Character cha) {
		L1PcInstance pc = (L1PcInstance) cha;
		pc.getResistance().addAllNaturalResistance(10);
		pc.sendPackets(new S_OwnCharAttrDef(pc));
	}

}
