package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

public class CreateMagicalWeapon {

	public static void runSkill(L1Character cha, int _itemobjid) {
		L1PcInstance pc = (L1PcInstance) cha;
		L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
		if (item != null && item.getItem().getType2() == 1) {
			int item_type = item.getItem().getType2();
			int safe_enchant = item.getItem().get_safeenchant();
			int enchant_level = item.getEnchantLevel();
			String item_name = item.getName();
			if (safe_enchant < 0) {
				pc.sendPackets(new S_ServerMessage(79));
			} else if (safe_enchant == 0) {
				pc.sendPackets(new S_ServerMessage(79));
			} else if (item_type == 1 && enchant_level == 0) {
				if (!item.isIdentified()) {
					pc.sendPackets(new S_ServerMessage(161, item_name, "$245",
							"$247"));
				} else {
					item_name = "+0 " + item_name;
					pc.sendPackets(new S_ServerMessage(161, "+0 " + item_name,
							"$245", "$247"));
				}
				item.setEnchantLevel(1);
				pc.getInventory()
						.updateItem(item, L1PcInventory.COL_ENCHANTLVL);
			} else {
				pc.sendPackets(new S_ServerMessage(79));
			}
		} else {
			pc.sendPackets(new S_ServerMessage(79));
		}
	}

}
