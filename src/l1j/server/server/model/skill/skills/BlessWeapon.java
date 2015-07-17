package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ServerMessage;

public class BlessWeapon {

	public static boolean runSkill(L1Character cha, int buffDuration) {
		if (!(cha instanceof L1PcInstance)) {
			return false;
		}
		L1PcInstance pc = (L1PcInstance) cha;
		if (pc.getWeapon() == null) {
			pc.sendPackets(new S_ServerMessage(79));
			return false;
		}
		for (L1ItemInstance item : pc.getInventory().getItems()) {
			if (pc.getWeapon().equals(item)) {
				pc.sendPackets(new S_ServerMessage(161, item.getLogName(),
						"$245", "$247"));
				item.setSkillWeaponEnchant(pc, L1SkillId.BLESS_WEAPON,
						buffDuration * 1000);
				return false;
			}
		}

		return true;
	}
}
