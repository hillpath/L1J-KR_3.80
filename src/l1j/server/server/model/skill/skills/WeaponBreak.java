package l1j.server.server.model.skill.skills;

import java.util.Random;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

public class WeaponBreak {
	public static void runSkill(int _calcType, L1Character cha,
			L1Character _user, Random random) {
		if (_calcType == 1/* PC_PC */|| _calcType == 3/* NPC_PC */) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ItemInstance weapon = pc.getWeapon();
				if (weapon != null) {
					int weaponDamage = random.nextInt(_user.getAbility()
							.getTotalInt() / 3) + 1;
					pc
							.sendPackets(new S_ServerMessage(268, weapon
									.getLogName()));
					pc.getInventory().receiveDamage(weapon, weaponDamage);
				}
			}
		} else {
			((L1NpcInstance) cha).setWeaponBreaked(true);
		}
	}
}
