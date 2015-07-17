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
package l1j.server.server.model.Instance;

import java.util.Collection;

import javolution.util.FastTable;
import l1j.server.GameSystem.Antaras.AntarasRaid;
import l1j.server.GameSystem.Antaras.AntarasRaidSystem;
import l1j.server.GameSystem.Antaras.AntarasRaidTimer;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Npc;

public class L1FieldObjectInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private int moveMapId;
	
	//private boolean seeon = false;

	public L1FieldObjectInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance pc) {
	}
	@Override
	public void onTalkAction(L1PcInstance pc) {
		int npcid = getNpcTemplate().get_npcId();
		switch (npcid) {
		/*case 4212015: // 드래곤 포탈
			if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGONBLOOD_ANTA)) {
				pc.sendPackets(new S_ServerMessage(1626)); 
			}else{
			//	pc.sendPackets(new S_Message_YN(2923, ""));
				pc.DragonPortalLoc[0] = 32600;
				pc.DragonPortalLoc[1] = 32741;
				pc.DragonPortalLoc[2] = 1005;
			}
			break;
		case 4212016: // 파푸레이드
			if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGONBLOOD_PAP)) {
				pc.sendPackets(new S_ServerMessage(1626)); 
			}else{
			//	pc.sendPackets(new S_Message_YN(2923, ""));
				pc.DragonPortalLoc[0] = 32860;
				pc.DragonPortalLoc[1] = 32801;
				pc.DragonPortalLoc[2] = 1011;
			}
			break;
		case 4212017: // 린드레이드
			if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGONBLOOD_RIND)) {
				pc.sendPackets(new S_ServerMessage(1626)); 
			}else{
			//	pc.sendPackets(new S_Message_YN(2923, ""));
				pc.DragonPortalLoc[0] = 32673;
				pc.DragonPortalLoc[1] = 32926;
				pc.DragonPortalLoc[2] = 1017;
			}
			break;*/
		case 4500101: // 안타시작->동굴
			L1Teleport.teleport(pc, 32935, 32610, (short) 1005, 5, true);
			break;
		case 4500102: // 안타시작 -> 안타대기하는곳
			L1Teleport.teleport(pc, 32671, 32668, (short) 1005, 5, true);
			break;
		case 4500103: // 안타대기->안타방
			L1Teleport.teleport(pc, 32796, 32664, (short) 1005, 5, true);
			break;
		case 4500112: // 파푸대기->파푸방//이거추가함
			L1Teleport.teleport(pc, 32988, 32842, (short) 1011, 5, true);
			break;
		case 4500114: // 린드대기->린드방 린드레이드
			L1Teleport.teleport(pc, 32858, 32883, (short) 1017, 5, true);
			break;
		default:
			break;
		}
	}

	@Override
	public void deleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeObject(this);
		FastTable<L1PcInstance> list = null;
		list = L1World.getInstance().getRecognizePlayer(this);
		for (L1PcInstance pc : list) {
			if (pc == null)
				continue;
			pc.getNearObjects().removeKnownObject(this);
			pc.sendPackets(new S_RemoveObject(this));
		}
		getNearObjects().removeAllKnownObjects();
	}
	/**
	 * 지정된 맵의 32명이 넘는지 체크해서 텔시킨다
	 * 
	 * @param pc
	 * @param mapid
	 */
	private void telAntarasRaidMap(L1PcInstance pc, int mapid) {
		int count = 0;
		if (pc.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.DRAGONBLOOD_ANTA)) {
			pc.sendPackets(new S_ServerMessage(1626));
			return;
		}
		Collection<L1PcInstance> list = null;
		list = L1World.getInstance().getAllPlayers();
		for (L1PcInstance player : list) {
			if (player == null)
				continue;
			if (player.getMapId() == mapid) {
				count += 1;
				if (count > 31)
					return;
			}
		}
		L1Teleport.teleport(pc, 32600, 32741, (short) mapid, 5, true);
	}

	/**
	 * 이동할 맵을 설정한다.
	 * 
	 * @param id
	 */
	public void setMoveMapId(int id) {
		moveMapId = id;
	}

	/***/
	private void tel4room(L1PcInstance pc, int mapid) {
		AntarasRaid ar = AntarasRaidSystem.getInstance().getAR(mapid);
		int room = ar.getRoomNum();

		switch (room) {
		case 0:
			AntarasRaidTimer art = new AntarasRaidTimer(ar, 0, 1320 * 1000);// 22분
			// 체크
			art.begin();
		case 1:
			AntarasRaidTimer roomstart1 = new AntarasRaidTimer(ar, 1,
					120 * 1000);// 2분
			// 체크
			roomstart1.begin();
			L1Teleport.teleport(pc, 32935, 32611, (short) mapid, 5, true);
			break;
		case 2:
			AntarasRaidTimer roomstart2 = new AntarasRaidTimer(ar, 2,
					120 * 1000);// 2분
			// 체크
			roomstart2.begin();
			L1Teleport.teleport(pc, 32935, 32803, (short) mapid, 5, true);
			break;
		case 3:
			AntarasRaidTimer roomstart3 = new AntarasRaidTimer(ar, 3,
					120 * 1000);// 2분
			// 체크
			roomstart3.begin();
			L1Teleport.teleport(pc, 32807, 32803, (short) mapid, 5, true);
			break;
		case 4:
			AntarasRaidTimer roomstart4 = new AntarasRaidTimer(ar, 4,
					120 * 1000);// 2분
			// 체크
			roomstart4.begin();
			L1Teleport.teleport(pc, 32679, 32803, (short) mapid, 5, true);
			break;
		case 5:
			pc.sendPackets(new S_ServerMessage(1599));
			return;
		default:
			break;
		}
		ar.addUser4room(pc, room);
	}
	/** 1536: 인원이 가득차서 더 이상 입장할 수 없습니다.
	 *  1537: 드래곤이 깨어나서, 지금은 입장할 수 없습니다.*/
	private void telAntarasLair(L1PcInstance pc, int moveMapId2) {
		int count = 0;
		AntarasRaid ar = AntarasRaidSystem.getInstance().getAR(moveMapId2);
		count = ar.countLairUser();
		if (count >= 32)
			return;
		if (ar.isAntaras()) {
			pc.sendPackets(new S_ServerMessage(1537));// 드래곤이 깨서 진입 못한다
			return;
	}
		if (count == 0) {
			AntarasRaidTimer antastart = new AntarasRaidTimer(ar, 5, 120 * 1000);// 2분
			// 체크
			antastart.begin();
			AntarasRaidTimer antaendtime = new AntarasRaidTimer(ar, 6,
					1320 * 1000);// 22분
			// 체크
			antaendtime.begin();
		}
	}

}
