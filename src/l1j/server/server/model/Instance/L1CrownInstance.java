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

import java.util.List;

import java.util.Collection;
import javolution.util.FastTable;

import l1j.server.server.datatables.ClanTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1WarSpawn;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_CastleMaster;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

public class L1CrownInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public L1CrownInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		boolean in_war = false;
		if (player.getClanid() == 0) { // 크란미소속
			return;
		}
		String playerClanName = player.getClanname();
		L1Clan clan = L1World.getInstance().getClan(playerClanName);
		if (clan == null) {
			return;
		}
		if (!player.isCrown()) { // 군주 이외
			return;
		}
		if (player.getLevel() < 50) {
            player.sendPackets(new S_SystemMessage("50렙 이상 군주캐릭만 왕관을 클릭 가능합니다"));
   return; 
        }
		if (player.getGfxId().getTempCharGfx() != 0 && // 변신중
				player.getGfxId().getTempCharGfx() != 1) {
			return;
		}
		if (player.getId() != clan.getLeaderId()) { // 혈맹주 이외
			return;
		}
		if (!checkRange(player)) { // 크라운의 1 셀 이내
			return;
		}
		if (clan.getCastleId() != 0) {
			// 성주 크란
			// 당신은 벌써 성을 소유하고 있으므로, 다른 시로를 잡을 수 없습니다.
			player.sendPackets(new S_ServerMessage(474));
			return;
		}
		player.setCurrentHp(player.getMaxHp());
		new L1SkillUse().handleCommands(player, L1SkillId.ABSOLUTE_BARRIER, player.getId(), player.getX(), player.getY(), null, 0 ,L1SkillUse.TYPE_GMBUFF);


		// 크라운의 좌표로부터 castle_id를 취득
		int castle_id = L1CastleLocation
				.getCastleId(getX(), getY(), getMapId());

		// 포고하고 있을까 체크.단, 성주가 없는 경우는 포고 불요
		boolean existDefenseClan = false;
		L1Clan defence_clan = null;
		for (L1Clan defClan : L1World.getInstance().getAllClans()) {
			if (castle_id == defClan.getCastleId()) {
				// 전의 성주 크란
				defence_clan = L1World.getInstance().getClan(
						defClan.getClanName());
				existDefenseClan = true;
				break;
			}
		}
		List<L1War> wars = L1World.getInstance().getWarList(); // 전전쟁 리스트를 취득
		for (L1War war : wars) {
			if (castle_id == war.GetCastleId()) { // 이마이성의 전쟁
				in_war = war.CheckClanInWar(playerClanName);
				break;
			}
		}
		if (existDefenseClan && in_war == false) { // 성주가 있어, 포고하고 있지 않는 경우
			return;
		}

		// clan_data의 hascastle를 갱신해, 캐릭터에 크라운을 붙인다
		if (existDefenseClan && defence_clan != null) { // 전의 성주 크란이 있다
			defence_clan.setCastleId(0);
			ClanTable.getInstance().updateClan(defence_clan);
			L1PcInstance defence_clan_member[] = defence_clan
					.getOnlineClanMember();
			for (int m = 0; m < defence_clan_member.length; m++) {
				if (defence_clan_member[m].getId() == defence_clan
						.getLeaderId()) { // 전의
					// 성주
					// 크란의
					// 군주
					defence_clan_member[m].sendPackets(new S_CastleMaster(0,
							defence_clan_member[m].getId()));
					L1World.getInstance().broadcastPacketToAll(
							new S_CastleMaster(0, defence_clan_member[m]
									.getId()));
					break;
				}
			}
		} 
		//이쯤에서 성주라는 스위치를 켜주고
		player.setCastleIn(true);
		//
		clan.setCastleId(castle_id);
		ClanTable.getInstance().updateClan(clan);
		player.sendPackets(new S_CastleMaster(castle_id, player.getId()));
		L1World.getInstance().broadcastPacketToAll(
				new S_CastleMaster(castle_id, player.getId()));

		// 크란원 이외를 거리에 강제 텔레포트
		int[] loc = new int[3];
		Collection<L1PcInstance> _list = null;
		_list = L1World.getInstance().getAllPlayers();
		for (L1PcInstance pc : _list) {
			if (pc.getClanid() != player.getClanid() && !pc.isGm()) {
				if (L1CastleLocation.checkInWarArea(castle_id, pc)) { //여기까진 그렇게 오래안걸리는데 
					// 기내에 있다
					loc = L1CastleLocation.getGetBackLoc(castle_id); //이거땜에 오래걸려요
					int locx = loc[0];
					int locy = loc[1];
					short mapid = (short) loc[2];
					L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
				}
			}
		}
		_list = null;
		// 메세지 표시
		for (L1War war : wars) {
			if (war.CheckClanInWar(playerClanName) && existDefenseClan) {
				// 자크란이 참가중에서, 성주가 교대
				war.WinCastleWar(playerClanName);
				break;
			}
		}
		L1PcInstance[] clanMember = clan.getOnlineClanMember();

		if (clanMember.length > 0) {
			// 성을 점거했습니다.
			S_ServerMessage s_serverMessage = new S_ServerMessage(643);
			for (L1PcInstance pc : clanMember) {
				pc.sendPackets(s_serverMessage);
			}
		}
		deleteMe();
		L1TowerInstance lt = null;
		Collection<L1Object> list2 = null;
		list2 = L1World.getInstance().getObject();
		for (L1Object l1object : list2) {
			if (l1object == null)
				continue;
			if (l1object instanceof L1TowerInstance) {
				lt = (L1TowerInstance) l1object;
				if (L1CastleLocation.checkInWarArea(castle_id, lt)) {
					lt.deleteMe();
				}
			}

		}
		// 타워를 spawn 한다
		L1WarSpawn warspawn = new L1WarSpawn();
		warspawn.SpawnTower(castle_id);
		//이쯤에서 스위치를 꺼주면 선포렉이없어질듯함
		player.setCastleIn(false);
	}

	@Override
	public void deleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		allTargetClear();
		_master = null;
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

	private boolean checkRange(L1PcInstance pc) {
		return (getX() - 1 <= pc.getX() && pc.getX() <= getX() + 1
				&& getY() - 1 <= pc.getY() && pc.getY() <= getY() + 1);
	}
}
