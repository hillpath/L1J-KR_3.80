package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ServerMessage;

public class ShadowFang {

	public static void runSkill(L1Character cha, int _itemobjid,
			int buffDuration) {
		L1PcInstance pc = (L1PcInstance) cha;
		L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
		if (item != null && item.getItem().getType2() == 1) {
			item.setSkillWeaponEnchant(pc, L1SkillId.SHADOW_FANG,
					buffDuration * 1000);
		} else {
			pc.sendPackets(new S_ServerMessage(79));
		}
	}

}
