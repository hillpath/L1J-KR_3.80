package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;

public class RunClan {

	public static void runSkill(L1Character cha, int _targetID) {
		L1PcInstance pc = (L1PcInstance) cha;
		L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(
				_targetID);
		if (clanPc != null) {
			if (pc.getMap().isEscapable() || pc.isGm()) {
				boolean castle_area = L1CastleLocation.checkInAllWarArea(clanPc
						.getX(), clanPc.getY(), clanPc.getMapId());
				if ((clanPc.getMapId() == 0 || clanPc.getMapId() == 4 || clanPc
						.getMapId() == 304)
						&& castle_area == false) {
					L1Teleport.teleport(pc, clanPc.getX(), clanPc.getY(),
							clanPc.getMapId(), 5, true);
				} else {
					pc.sendPackets(new S_ServerMessage(547));
				}
			} else {
				pc.sendPackets(new S_Paralysis(
						S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				pc.sendPackets(new S_ServerMessage(647));
			}
		}
	}

}
