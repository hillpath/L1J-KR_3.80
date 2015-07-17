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

package l1j.server.server.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import l1j.server.Warehouse.ClanWarehouse;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_DRAGONPERL;
import l1j.server.server.serverpackets.S_DollPack;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_MapID;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharPack;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PetPack;
import l1j.server.server.serverpackets.S_PinkName;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_SummonPack;

// Referenced classes of package l1j.server.server.utils:
// FaceToFace

public class Teleportation {

	private static Random _random = new Random(System.nanoTime());

	private Teleportation() {
	}
	public static void doTeleportation(L1PcInstance pc) {
		doTeleportation(pc, false);
	}

	public static void doTeleportation(L1PcInstance pc, boolean type) {
		if (pc.isDead() || pc.isTeleport()) {
			return;
		}

		int x = pc.getTeleportX();
		int y = pc.getTeleportY();
		short mapId = pc.getTeleportMapId();
		int head = pc.getTeleportHeading();

		L1Map map = L1WorldMap.getInstance().getMap(mapId);

		int tile = map.getTile(x, y);
		if (!type && (tile == 0 || tile == 4 || tile == 12 || !map.isInMap(x, y)) && !pc.isGm() && mapId != 4) {//원본
		//	if ((tile == 0 || tile == 4 || tile == 12 || !map.isInMap(x, y)) && !pc.isGm() && mapId != 4) {
			x = pc.getX();
			y = pc.getY();
			mapId = pc.getMapId();
		}

		pc.setTeleport(true);

		ClanWarehouse clanWarehouse = null;
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null)
			clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(
					clan.getClanName());
		if (clanWarehouse != null)
			clanWarehouse.unlock(pc.getId());

		List<L1PcInstance> list = pc.getNearObjects().getKnownPlayers();
		for (L1PcInstance target : list) {
			target.sendPackets(new S_RemoveObject(pc.getId()));
		}

		L1World.getInstance().moveVisibleObject(pc, mapId);
		pc.setLocation(x, y, mapId);
		pc.getMoveState().setHeading(head);
		pc.sendPackets(new S_MapID(pc.getMapId(), pc.getMap().isUnderwater()));

		if (pc.isReserveGhost()) {
			pc.endGhost();
		}
		if (pc.getMapId() != 631) {
			Broadcaster.broadcastPacket(pc, new S_OtherCharPacks(pc));
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BUFF_CRAY)) {
			if (mapId == 1005) { // 용땅으로 텔 될경우
				if (!pc.iscraybuff()) { // 크레이 버프가 안받아져 있으면
					pc.addHpr(3);
					pc.addMpr(3);
					pc.addMaxHp(100);
					pc.addMaxMp(50);
					pc.addHitup(5);
					pc.addDmgup(1);
					pc.getResistance().addEarth(50);
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
							.getMaxHp()));
					pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
							.getMaxMp()));
					if (pc.isInParty()) {
						pc.getParty().updateMiniHP(pc);
					}
					pc.setcraybuff(true);
				}
			} else { // 용땅이 아닌맵으로 텔할경우
				if (pc.iscraybuff()) { // 크레이 버프가 받아져 있으면
					pc.addHpr(-3);
					pc.addMpr(-3);
					pc.addMaxHp(-100);
					pc.addMaxMp(-50);
					pc.addHitup(-5);
					pc.addDmgup(-1);
					pc.getResistance().addEarth(-50);
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
							.getMaxHp()));
					pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
							.getMaxMp()));
					if (pc.isInParty()) {
						pc.getParty().updateMiniHP(pc);
					}
					pc.setcraybuff(false);
				}
			}
		}
		// if (!pc.isGhost() && !pc.isGmInvis() && !pc.isInvisble()) {
		if (pc.getMapId() != 631) {
		Broadcaster.broadcastPacket(pc, new S_OtherCharPacks(pc));
		 }
		pc.sendPackets(new S_OwnCharPack(pc));

		if (pc.isPinkName()) {
			pc.sendPackets(new S_PinkName(pc.getId(), pc
					.getSkillEffectTimerSet().getSkillEffectTimeSec(
							L1SkillId.STATUS_PINK_NAME)));
			Broadcaster.broadcastPacket(pc, new S_PinkName(pc.getId(), pc
					.getSkillEffectTimerSet().getSkillEffectTimeSec(
							L1SkillId.STATUS_PINK_NAME)));
		}

		pc.getNearObjects().removeAllKnownObjects();
		pc.sendVisualEffectAtTeleport();
		pc.updateObject();
		pc.sendPackets(new S_CharVisualUpdate(pc));

		pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.MEDITATION);
		pc.setCallClanId(0);
		HashSet<L1PcInstance> subjects = new HashSet<L1PcInstance>();
		subjects.add(pc);

		if (!pc.isGhost()) {
			if (pc.getMap().isTakePets()) {
				for (L1NpcInstance petNpc : pc.getPetList().values()) {
					L1Location loc = pc.getLocation().randomLocation(3, false);
					int nx = loc.getX();
					int ny = loc.getY();
					if (pc.getMapId() == 5125 || pc.getMapId() == 5131
							|| pc.getMapId() == 5132 || pc.getMapId() == 5133
							|| pc.getMapId() == 5134) {
						nx = 32799 + _random.nextInt(5) - 3;
						ny = 32864 + _random.nextInt(5) - 3;
					}
					teleport(petNpc, nx, ny, mapId, head);
					if (petNpc instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) petNpc;
						pc.sendPackets(new S_SummonPack(summon, pc));
					} else if (petNpc instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petNpc;
						pc.sendPackets(new S_PetPack(pet, pc));
					}

					for (L1PcInstance visiblePc : L1World.getInstance()
							.getVisiblePlayer(petNpc)) {
						visiblePc.getNearObjects().removeKnownObject(petNpc);
						subjects.add(visiblePc);
					}
				}

				for (L1DollInstance doll : pc.getDollList().values()) {
					L1Location loc = pc.getLocation().randomLocation(3, false);
					int nx = loc.getX();
					int ny = loc.getY();

					teleport(doll, nx, ny, mapId, head);
					pc.sendPackets(new S_DollPack(doll, pc));
					/**에틴인형**/
					if(doll.getDollType() == L1DollInstance.DOLLTYPE_ETTIN){
						getDollType(pc);
					}

					for (L1PcInstance visiblePc : L1World.getInstance()
							.getVisiblePlayer(doll)) {
						visiblePc.getNearObjects().removeKnownObject(doll);
						subjects.add(visiblePc);
					}
				}
			} else {
				for (L1DollInstance doll : pc.getDollList().values()) {
					L1Location loc = pc.getLocation().randomLocation(3, false);
					int nx = loc.getX();
					int ny = loc.getY();

					teleport(doll, nx, ny, mapId, head);
					pc.sendPackets(new S_DollPack(doll, pc));
					/**에틴인형**/
					if(doll.getDollType() == L1DollInstance.DOLLTYPE_ETTIN){
						getDollType(pc);
					}

					for (L1PcInstance visiblePc : L1World.getInstance()
							.getVisiblePlayer(doll)) {
						visiblePc.getNearObjects().removeKnownObject(doll);
						subjects.add(visiblePc);
					}
				}
			}
		}

		for (L1PcInstance updatePc : subjects) {
			updatePc.updateObject();
		}
		pc.setTeleport(false);
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.WIND_SHACKLE)) {
			pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), pc
					.getSkillEffectTimerSet().getSkillEffectTimeSec(
							L1SkillId.WIND_SHACKLE)));
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.STATUS_DRAGONPERL)) {
			int reminingtime = pc.getSkillEffectTimerSet()
					.getSkillEffectTimeSec(L1SkillId.STATUS_DRAGONPERL);
			pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONPERL, 8,
					(reminingtime / 4) - 2));
			pc.sendPackets(new S_DRAGONPERL(pc.getId(), 8));
			Broadcaster.broadcastPacket(pc, new S_DRAGONPERL(pc.getId(), 8));
			pc.set진주속도(1);
		}
	}
	/**에틴인형**/
	private static void getDollType(L1PcInstance pc) {
		if(pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_HASTE)){
			pc.setMoveSpeed(1);
			pc.sendPackets(new S_SkillHaste(pc.getId(), 1, -1));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
		}
	}
	/**에틴인형**/
	private static void teleport(L1NpcInstance npc, int x, int y, short map,
			int head) {
		L1World.getInstance().moveVisibleObject(npc, map);

		L1WorldMap.getInstance().getMap(npc.getMapId()).setPassable(npc.getX(),
				npc.getY(), true);
		npc.setX(x);
		npc.setY(y);
		npc.setMap(map);
		npc.getMoveState().setHeading(head);
		L1WorldMap.getInstance().getMap(npc.getMapId()).setPassable(npc.getX(),
				npc.getY(), false);
	}

}
