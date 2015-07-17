package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;

public class TeleportToMother {
	public static void runSkill(L1Character cha) {
		L1PcInstance pc = (L1PcInstance) cha;
		if (pc.getMap().isEscapable() || pc.isGm()) {
			L1Location _loc = new L1Location(33051, 32337, (short) 4);
			L1Map map = _loc.getMap();

			L1Location loc = L1Location.randomLocation2(33051, 32337, map,
					(short) _loc.getMapId(), 1, 3, false);
			L1Teleport.teleport(pc, loc.getX(), loc.getY(), (short) 4, 5, true);

		} else {
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK,
					false));
			pc.sendPackets(new S_ServerMessage(647));

		}
	}
}
