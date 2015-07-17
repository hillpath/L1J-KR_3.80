package l1j.server.server.model.skill.skills;

import java.util.Random;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

public class BringStone {

	public static void runSkill(L1Character cha, int _itemobjid) {
		L1PcInstance pc = (L1PcInstance) cha;
		Random random = new Random();
		L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
		if (item != null) {
			int dark = (int) (10 + (pc.getLevel() * 0.8) + (pc.getAbility()
					.getTotalWis() - 6) * 1.2);
			int brave = (int) (dark / 2.1);
			int wise = (int) (brave / 2.0);
			int kayser = (int) (wise / 1.9);
			int chance = random.nextInt(100) + 1;
			if (item.getItem().getItemId() == 40320) {
				pc.getInventory().removeItem(item, 1);
				if (dark >= chance) {
					pc.getInventory().storeItem(40321, 1);
					pc.sendPackets(new S_ServerMessage(403, "$2475"));
				} else {
					pc.sendPackets(new S_ServerMessage(280));
				}
			} else if (item.getItem().getItemId() == 40321) {
				pc.getInventory().removeItem(item, 1);
				if (brave >= chance) {
					pc.getInventory().storeItem(40322, 1);
					pc.sendPackets(new S_ServerMessage(403, "$2476"));
				} else {
					pc.sendPackets(new S_ServerMessage(280));
				}
			} else if (item.getItem().getItemId() == 40322) {
				pc.getInventory().removeItem(item, 1);
				if (wise >= chance) {
					pc.getInventory().storeItem(40323, 1);
					pc.sendPackets(new S_ServerMessage(403, "$2477"));
				} else {
					pc.sendPackets(new S_ServerMessage(280));
				}
			} else if (item.getItem().getItemId() == 40323) {
				pc.getInventory().removeItem(item, 1);
				if (kayser >= chance) {
					pc.getInventory().storeItem(40324, 1);
					pc.sendPackets(new S_ServerMessage(403, "$2478"));
				} else {
					pc.sendPackets(new S_ServerMessage(280));
				}
			}
		}
	}

}
