package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Dexup;

public class PhysicalEnchantDex {

	public static void runSkill(L1Character cha, int buffIconDuration) {
		L1PcInstance pc = (L1PcInstance) cha;
		pc.getAbility().addAddedDex((byte) 5);
		pc.sendPackets(new S_Dexup(pc, 5, buffIconDuration));
	}

}
