package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

public class ElementalFallDown {
	public static void runSkill(L1Character _user, L1PcInstance _player,
			L1Character cha) {
		if (_user instanceof L1PcInstance) {
			int playerAttr = _player.getElfAttr();
			int i = -50;
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				switch (playerAttr) {
				case 0:
					_player.sendPackets(new S_ServerMessage(79));
					break;
				case 1:
					pc.getResistance().addEarth(i);
					pc.setAddAttrKind(1);
					break;
				case 2:
					pc.getResistance().addFire(i);
					pc.setAddAttrKind(2);
					break;
				case 4:
					pc.getResistance().addWater(i);
					pc.setAddAttrKind(4);
					break;
				case 8:
					pc.getResistance().addWind(i);
					pc.setAddAttrKind(8);
					break;
				default:
					break;
				}
			} else if (cha instanceof L1MonsterInstance) {
				L1MonsterInstance mob = (L1MonsterInstance) cha;
				switch (playerAttr) {
				case 0:
					_player.sendPackets(new S_ServerMessage(79));
					break;
				case 1:
					mob.getResistance().addEarth(i);
					mob.setAddAttrKind(1);
					break;
				case 2:
					mob.getResistance().addFire(i);
					mob.setAddAttrKind(2);
					break;
				case 4:
					mob.getResistance().addWater(i);
					mob.setAddAttrKind(4);
					break;
				case 8:
					mob.getResistance().addWind(i);
					mob.setAddAttrKind(8);
					break;
				default:
					break;
				}
			}
		}
	}
}
