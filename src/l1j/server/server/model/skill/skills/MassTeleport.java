package l1j.server.server.model.skill.skills;

import java.util.List;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1BookMark;

public class MassTeleport {

	public static void runSkill(L1Character cha, int _bookmarkId) {
		L1PcInstance pc = (L1PcInstance) cha;
		L1BookMark bookm = pc.getBookMark(_bookmarkId);
		if (bookm != null) {
			if (pc.getMap().isEscapable() || pc.isGm()) {
				int newX = bookm.getLocX();
				int newY = bookm.getLocY();
				short mapId = bookm.getMapId();

				List<L1PcInstance> clanMember = L1World.getInstance()
						.getVisiblePlayer(pc);
				for (L1PcInstance member : clanMember) {
					if (pc.getLocation().getTileLineDistance(
							member.getLocation()) <= 3
							&& member.getClanid() == pc.getClanid()
							&& pc.getClanid() != 0
							&& member.getId() != pc.getId()
							&& !member.isPrivateShop()) {
						L1Teleport.teleport(member, newX, newY, mapId, member
								.getMoveState().getHeading(), true);
					}
				}
				L1Teleport.teleport(pc, newX, newY, mapId, pc.getMoveState()
						.getHeading(), true);
			} else {
				pc.sendPackets(new S_Paralysis(
						S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				pc.sendPackets(new S_ServerMessage(79));
			}
		} else {
			if (pc.getMap().isTeleportable() || pc.isGm()) {
				L1Location newLocation = pc.getLocation().randomLocation(200,
						true);
				int newX = newLocation.getX();
				int newY = newLocation.getY();
				short mapId = (short) newLocation.getMapId();

				List<L1PcInstance> clanMember = L1World.getInstance()
						.getVisiblePlayer(pc);
				for (L1PcInstance member : clanMember) {
					if (pc.getLocation().getTileLineDistance(
							member.getLocation()) <= 3
							&& member.getClanid() == pc.getClanid()
							&& pc.getClanid() != 0
							&& member.getId() != pc.getId()
							&& !pc.isPrivateShop()) {
						L1Teleport.teleport(member, newX, newY, mapId, member
								.getMoveState().getHeading(), true);
					}
				}
				L1Teleport.teleport(pc, newX, newY, mapId, pc.getMoveState()
						.getHeading(), true);
			} else {
				pc.sendPackets(new S_Paralysis(
						S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
				pc.sendPackets(new S_ServerMessage(276));
			}
		}
	}
}
