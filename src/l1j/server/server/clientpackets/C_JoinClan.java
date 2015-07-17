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

package l1j.server.server.clientpackets;

import server.LineageClient;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_CharTitle;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.utils.FaceToFace;
import l1j.server.Config; // 임포트 추가
import l1j.server.server.serverpackets.S_SystemMessage; // 임포트추가

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_JoinClan extends ClientBasePacket {

	private static final String C_JOIN_CLAN = "[C] C_JoinClan";
	
	public static final int CLAN_RANK_SUB_PRINCE = 3;
	public static final int CLAN_RANK_GUARDIAN = 9;
	

	public C_JoinClan(byte abyte0[], LineageClient clientthread)
			throws Exception {
		super(abyte0);

		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null || pc.isGhost()) {
			return;
		}

		L1PcInstance target = FaceToFace.faceToFace(pc);
		if (target != null) {
			JoinClan(pc, target);
		}
	}

	private void JoinClan(L1PcInstance player, L1PcInstance target) {
	///////////혈맹리뉴얼//////////////
		 if (!target.isCrown() && !(target.getClanRank()== CLAN_RANK_GUARDIAN) && !(target.getClanRank()== CLAN_RANK_SUB_PRINCE)) { 
			player.sendPackets(new S_SystemMessage(target.getName()+"는 왕자나 공주 수호기사가 아닙니다."));//이부분도바꺼야하나
///////////혈맹리뉴얼//////////////
			// 프린세스가
			// 아닙니다.
			return;
		}

		int clan_id = target.getClanid();
		String clan_name = target.getClanname();
		if (clan_id == 0) { // 상대 크란이 없다
			player.sendPackets(new S_ServerMessage(90, target.getName())); // \f1%0은
			// 혈맹을
			// 창설하고
			// 있지
			// 않는
			// 상태입니다.
			return;
		}

		L1Clan clan = L1World.getInstance().getClan(clan_name);
		if (clan == null)
			return;
			///////////혈맹리뉴얼//////////////
		 if (!target.isCrown() && !(target.getClanRank()== CLAN_RANK_GUARDIAN) && !(target.getClanRank()== CLAN_RANK_SUB_PRINCE)) { 
			player.sendPackets(new S_SystemMessage(target.getName()+"는 왕자나 공주 수호기사가 아닙니다.")); //
			///////////혈맹리뉴얼//////////////
			// 프린세스가
			// 아닙니다.
			return;
		}
//////////////////////////혈맹스파이//////////////////////
//		 같은계정에 다른 클란으로 있는지 검색 클란번호를 알아낸다. 
	if(player.getClanid()==0){  //혈에 가입하지 않았으면서 현재캐릭이 클란에 가입 하려하면 
			boolean Checking = player.clanid_search1(player.getNetConnection().getAccountName(),target.getClanid());
		       if(Checking == true){ // 상대의 클랜아디이와 다르면 가입을 거부시킨다. 
		        player.sendPackets(new S_SystemMessage("당신의 계정내 한캐릭은 이미 다른 혈맹에 가입중입니다. "));
		        player.sendPackets(new S_SystemMessage("한 계정내 캐릭들은  같은 혈맹만 가입 가능합니다. "));
		         target.sendPackets(new S_SystemMessage("상대방의 계정에 캐릭터가 다른 혈맹에 가입중입니다."));
		        return ;
		       }
		      }
//////////////////////////혈맹스파이//////////////////////
		// 혈가입 제한 by 떠라에몽
		L1PcInstance clanMember[] = clan.getOnlineClanMember();
		if (clanMember.length >= Config.RATE_JOIN_CLAN_FULL) {
			player.sendPackets(new S_SystemMessage("접속된 인원이 많기때문에 가입을 할수 없습니다."));
			return;

		}

		// 혈가입 제한 by 떠라에몽

		if (player.getClanid() != 0) { // 이미 크란에 가입이 끝난 상태
			if (player.isCrown()) { // 자신이 군주
				String player_clan_name = player.getClanname();
				L1Clan player_clan = L1World.getInstance().getClan(
						player_clan_name);
				if (player_clan == null) {
					return;
				}

				if (player.getId() != player_clan.getLeaderId()) { // 자신이 혈맹주
					// 이외
					player.sendPackets(new S_ServerMessage(89)); // \f1당신은 벌써
					// 혈맹에 가입하고
					// 있습니다.
					return;
				}

				if (player_clan.getCastleId() != 0
						|| player_clan.getHouseId() != 0) {
					player.sendPackets(new S_ServerMessage(665)); // \f1성이나
					// 아지트를 소유한
					// 상태로 혈맹을
					// 해산할 수
					// 없습니다.
					return;
				}
			} else {
				player.sendPackets(new S_ServerMessage(89)); // \f1당신은 벌써 혈맹에
				// 가입하고 있습니다.
				return;
			}
		}
		/**혈맹자동가입*/
		if (target.isAutoKing()) {
			try {
				if (player.getClanid() != 0 && !player.isGm()) {
					player.sendPackets(new S_ServerMessage(89)); // \f1당신은 벌써 혈맹에 가입하고 있습니다.
					return;
				}
				for (L1PcInstance clanMembers : clan.getOnlineClanMember()) {
					clanMembers.sendPackets(new S_ServerMessage(94, player.getName())); // \f1%0이 혈맹의 일원으로서 받아들여졌습니다.
				}
				Thread.sleep(1000);
				int clanId = target.getClanid();
				String clanName = target.getClanname();				
				player.setClanid(clanId);
				player.setClanname(clanName);
				player.setClanRank(L1Clan.CLAN_RANK_PUBLIC);
				player.setTitle(target.getAutoKingTitle());
				player.sendPackets(new S_CharTitle(player.getId(), ""));
				Broadcaster.broadcastPacket(player, new S_CharTitle(player.getId(), ""));
				player.save(); // DB에 캐릭터 정보를 기입한다
				clan.addClanMember(player.getName(), player.getClanRank());
				player.sendPackets(new S_ServerMessage(95, clanName)); // \f1%0 혈맹에 가입했습니다.
				
				for (L1PcInstance gm : L1World.getInstance().getAllPlayers()) {
					if (gm.isGm() && !gm.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_MENT)) {
						gm.sendPackets(new S_SystemMessage("\\fS" + player.getName()+ "님이 " + target.getClanname() + "혈맹에 가입하셨습니다."));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
/**혈맹자동가입*/
		target.setTempID(player.getId()); // 상대의 오브젝트 ID를 보존해 둔다
		target.sendPackets(new S_Message_YN(97, player.getName())); // %0가 혈맹에
		// 가입했지만은
		// 있습니다.
		// 승낙합니까?
		// (Y/N)
	}

	@Override
	public String getType() {
		return C_JOIN_CLAN;
	}
}
