/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.model;

import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.utils.Teleportation;

public class L1Teleport {

	public static final int TELEPORT = 0;

	public static final int CHANGE_POSITION = 1;

	public static final int ADVANCED_MASS_TELEPORT = 2;

	public static final int CALL_CLAN = 3;

	public static final int DUNGEON_TELEPORT = 4;
	
	public static final int NODELAY_TELEPORT = 5;
	// Â÷·Ê·Î teleport(Èò»ö), change position e(ÆÄ¶û), ad mass teleport e(»¡°­), call
	// clan(ÃÊ·Ï)
	public static final int[] EFFECT_SPR = { 169, 2235, 2236, 2281 };

	public static final int[] EFFECT_TIME = { 280, 440, 440, 1120 };

	private L1Teleport() {
	}

	public static void teleport(L1PcInstance pc, L1Location loc, int head,
			boolean effectable) {
		teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), head,
				effectable, TELEPORT, false);
	}

	public static void teleport(L1PcInstance pc, L1Location loc, int head,
			boolean effectable, int skillType) {
		teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), head,
				effectable, skillType, false);
	}

	public static void teleport(L1PcInstance pc, int x, int y, short mapid,
			int head, boolean effectable) {
		teleport(pc, x, y, mapid, head, effectable, TELEPORT, false);
	}
	
	public static void teleport(L1PcInstance pc, int x, int y, short mapid,
			int head, boolean effectable, boolean thread) {
		teleport(pc, x, y, mapid, head, effectable, TELEPORT, thread);
	}
	public static void teleport(L1PcInstance pc, int x, int y, short mapId,
			int head, boolean effectable, int skillType) {
		teleport(pc, x, y, mapId, head, effectable, false);
	}

	public static void teleport(L1PcInstance pc, int x, int y, short mapId,
			int head, boolean effectable, int skillType, boolean thread) {
		
		 int oldmap = pc.getMapId();//Ãß°¡
		pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK,
						false));

		if (effectable && (skillType >= 0 && skillType <= EFFECT_SPR.length)) {
			S_SkillSound packet = new S_SkillSound(pc.getId(),
					EFFECT_SPR[skillType]);
			pc.sendPackets(packet);
			Broadcaster.broadcastPacket(pc, packet);

				try {
					Thread.sleep((int) (EFFECT_TIME[skillType] * 0.7));
				} catch (Exception e) {
				}
			packet = null;
		}

		pc.setTeleportX(x);
		pc.setTeleportY(y);
		pc.setTeleportMapId(mapId);
		pc.setTeleportHeading(head);
		if (skillType == 4) {
			Teleportation.doTeleportation(pc, true);
		} else {
			Teleportation.doTeleportation(pc);
		}
		int newmap = pc.getMapId();//¿©±âºÎÅÍ ¶Ç Ãß°¡
		

		
	    if (oldmap != newmap) {   
			int entertime = pc.getGdungeonTime() % 1000;
			int entertime1 = pc.getTkddkdungeonTime() % 1000;
			int entertime2 = pc.getLdungeonTime() % 1000;
			int entertime3 = pc.getDdungeonTime() % 1000;
	     switch (newmap) {
	     case 30:
	     case 31:
	     case 32:
	     case 33:
	    	 pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, (120 - entertime3)*60));
	    	 break;
	     case 53:
	     case 54:
	     case 55:
	     case 56:
	    	 pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, (180 - entertime)*60));
	 		break;
	     case 78:
	     case 79:
	     case 80:
	     case 81:
	     case 82:
	    	 pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, (60 - entertime1)*60));
	      break;

	     case 451:case 452:case 453:case 454:case 455:case 456:
	     case 460:case 461:case 462:case 463:case 464:case 465:case 466:
	     case 470:case 471:case 472:case 473:case 474:case 475:case 476:case 477:case 478:
	     case 490:case 491:case 492:case 493:case 494:case 495:case 496:
	     case 530:case 531:case 532:case 533:case 534:
	    	 pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, (180 - entertime2)*60));
	    	 break;

					
	     default:
	      break;
	     }
	    }
	   }
	public static void teleport(L1PcInstance pc, int x, int y, short mapid, int head) {
		

		pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		
		
		S_SkillSound packet = new S_SkillSound(pc.getId(), EFFECT_SPR[0]);
		Broadcaster.broadcastPacket(pc, packet);
		pc.sendPackets(packet);	
		
		try {
			Thread.sleep(EFFECT_TIME[NODELAY_TELEPORT]);
		} catch (Exception e) {
		}
		
		pc.setTeleportX(x);
		pc.setTeleportY(y);
		pc.setTeleportMapId(mapid);
		pc.setTeleportHeading(head);
		if (TELEPORT == 4) {
			Teleportation.doTeleportation(pc, true);
		} else {
			Teleportation.doTeleportation(pc);
		}
	}
	
	public static void teleport(L1PcInstance pc, int x, int y, short mapid, int head, int delay) {

		pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER ,pc.getTkddkdungeonTime(),pc.getLdungeonTime(),pc.getGdungeonTime()));
		
		pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));

		S_SkillSound packet = new S_SkillSound(pc.getId(), EFFECT_SPR[0]);
		Broadcaster.broadcastPacket(pc, packet);
		pc.sendPackets(packet);	

		try {
			Thread.sleep(delay);
		} catch (Exception e) {
		}

		
		pc.setTeleportX(x);
		pc.setTeleportY(y);
		pc.setTeleportMapId(mapid);
		pc.setTeleportHeading(head);
		if (TELEPORT == 4) {
			Teleportation.doTeleportation(pc, true);
		} else {
			Teleportation.doTeleportation(pc);
		}
	}
	public static void teleportToTargetFront(L1Character cha,
			L1Character target, int distance) {
		teleportToTargetFront(cha, target, distance, false);
	}

	public static void teleportToTargetFront(L1Character cha,
			L1Character target, int distance, boolean ck) {
		int locX = target.getX();
		int locY = target.getY();
		int heading = target.getMoveState().getHeading();
		L1Map map = target.getMap();
		short mapId = target.getMapId();

		switch (heading) {
		case 1:
			locX += distance;
			locY -= distance;
			break;
		case 2:
			locX += distance;
			break;
		case 3:
			locX += distance;
			locY += distance;
			break;
		case 4:
			locY += distance;
			break;
		case 5:
			locX -= distance;
			locY += distance;
			break;
		case 6:
			locX -= distance;
			break;
		case 7:
			locX -= distance;
			locY -= distance;
			break;
		case 0:
			locY -= distance;
			break;
		default:
			break;
		}

		if (map.isPassable(locX, locY)) {
			if (cha instanceof L1PcInstance) {
				teleport((L1PcInstance) cha, locX, locY, mapId, cha
						.getMoveState().getHeading(), true, ck);
			} else if (cha instanceof L1NpcInstance) {
			}
		}
		map = null;
	}

	public static void randomBookmarkTeleport(L1PcInstance pc,
			L1BookMark bookm, int heading, boolean effectable) {
		L1Location newLocation = pc.getLocation().randomBookmarkLocation(bookm,
				true);
		int newX = newLocation.getX();
		int newY = newLocation.getY();
		int newHeading = pc.getMoveState().getHeading();
		short mapId = (short) newLocation.getMapId();

		L1Teleport.teleport(pc, newX, newY, mapId, newHeading, effectable);
		newLocation = null;
	}

	public static void randomTeleport(L1PcInstance pc, boolean effectable) {
		L1Location newLocation = pc.getLocation().randomLocation(200, true);
		int newX = newLocation.getX();
		int newY = newLocation.getY();
		int newHeading = pc.getMoveState().getHeading();
		short mapId = (short) newLocation.getMapId();

		L1Teleport.teleport(pc, newX, newY, mapId, newHeading, effectable);
		newLocation = null;
	}
}
