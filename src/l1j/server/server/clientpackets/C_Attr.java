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

import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.GameSystem.GhostHouse;
import l1j.server.GameSystem.PetRacing;
import l1j.server.GameSystem.MiniGame.DeathMatch;
import l1j.server.server.Announcements;
import l1j.server.server.Opcodes;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.datatables.BoardTable;
import l1j.server.server.datatables.BuddyTable;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Buddy;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1ChatParty;
import l1j.server.server.model.L1ClanJoin;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1BuffNpcInstance;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_BonusStats;
import l1j.server.server.serverpackets.S_ChangeName;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Resurrection;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Trade;
import l1j.server.server.templates.L1House;
import l1j.server.server.templates.L1Pet;
import server.LineageClient;

public class C_Attr extends ClientBasePacket {

	private static final Logger _log = Logger.getLogger(C_Attr.class.getName());

	private static final String C_ATTR = "[C] C_Attr";

	private static final int HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final int HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	public C_Attr(byte abyte0[], LineageClient clientthread) throws Exception {
		super(abyte0);


		 int i = readH(); // 3.51C
         int attrcode;
         if (i == 479) {
             attrcode = i;
         } else {
             @SuppressWarnings("unused")
             int count = readD(); // 
             attrcode = readH();
             if(i == 65535) i = 622;
         }
         int c;
         String name;

		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null) {
			return;
		}
		switch (attrcode) {
		case 97: // %0가 혈맹에 가입했지만은 있습니다. 승낙합니까? (Y/N)
			c = readH();
			L1PcInstance joinPc = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (joinPc != null) {
				if (c == 0) { // No
					joinPc.sendPackets(new S_ServerMessage(96, pc.getName())); // \f1%0은
					// 당신의
					// 요청을
					// 거절했습니다.
				} else if (c == 1) { // Yes
					L1ClanJoin.getInstance().ClanJoin(pc, joinPc);
				
					
				}
			}
			break;

		case 217: // %0혈맹의%1가 당신의 혈맹과의 전쟁을 바라고 있습니다. 전쟁에 응합니까? (Y/N)
		case 221: // %0혈맹이 항복을 바라고 있습니다. 받아들입니까? (Y/N)
		case 222: // %0혈맹이 전쟁의 종결을 바라고 있습니다. 종결합니까? (Y/N)
			c = readH();
			L1PcInstance enemyLeader = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			if (enemyLeader == null) {
				return;
			}
			pc.setTempID(0);
			String clanName = pc.getClanname();
			String enemyClanName = enemyLeader.getClanname();
			if (c == 0) { // No
				if (i == 217) {
					enemyLeader.sendPackets(new S_ServerMessage(236, clanName)); // %0혈맹이
					// 당신의
					// 혈맹과의
					// 전쟁을
					// 거절했습니다.
				} else if (i == 221 || i == 222) {
					enemyLeader.sendPackets(new S_ServerMessage(237, clanName)); // %0혈맹이
					// 당신의
					// 제안을
					// 거절했습니다.
				}
			} else if (c == 1) { // Yes
				if (i == 217) {
					L1War war = new L1War();
					war.handleCommands(2, enemyClanName, clanName); // 모의전 개시
				} else if (i == 221 || i == 222) {
					for (L1War war : L1World.getInstance().getWarList()) { // 전쟁
						// 리스트를
						// 취득
						if (war.CheckClanInWar(clanName)) { // 자크란이 가고 있는 전쟁을 발견
							if (i == 221) {
								war.SurrenderWar(enemyClanName, clanName); // 항복
							} else if (i == 222) {
								war.CeaseWar(enemyClanName, clanName); // 종결
							}
							break;
						}
					}
				}
			}
			break;

		case 223: // %0%s 동맹을 원합니다. 받아들이시겠습니까? (Y/N)
		case 1210: // 정말로 동맹을 탈퇴하시겠습니까? (Y/N)
			pc.sendPackets(new S_ServerMessage(2503)); // 전투 서버에서는 동맹 가입/탈퇴가 불가능합니다.
			break;

		case 252: // %0%s가 당신과 아이템의 거래를 바라고 있습니다. 거래합니까? (Y/N)
			   c = readH();
			   L1Object trading_partner = L1World.getInstance().findObject(
			     pc.getTradeID());
			   if (trading_partner != null) {
			    if (trading_partner instanceof L1PcInstance) {
					L1PcInstance target = (L1PcInstance) trading_partner;
					if (c == 0) { // No
						target.sendPackets(new S_ServerMessage(253, pc
								.getName())); // %0%d는
						// 당신과의
						// 거래에
						// 응하지
						// 않았습니다.
						pc.setTradeID(0);
						target.setTradeID(0);
					} else if (c == 1) { // Yes
						pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"사기 방지를 위해 상대방과 마주본후 거래 해주세요."));
						target.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"사기 방지를 위해 상대방과 마주본후 거래 해주세요."));
						pc.sendPackets(new S_Trade(target.getName()));
						target.sendPackets(new S_Trade(pc.getName()));
					}
				} else if (trading_partner instanceof L1BuffNpcInstance) {
					L1BuffNpcInstance target = (L1BuffNpcInstance) trading_partner;
					if (c == 0) { // No
						pc.setTradeID(0);
						target.setTradeID(0);
					} else if (c == 1) { // Yes
						pc.sendPackets(new S_Trade(target.getName()));
						target.setTradeID(pc.getId()); // 상대의 오브젝트 ID를 보존해 둔다
					}
				}
			}
			break;

		case 321: // 또 부활하고 싶습니까? (Y/N)
			c = readH();
			L1PcInstance resusepc1 = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (resusepc1 != null) { // 부활 스크롤
				if (c == 0) { // No
					;
				} else if (c == 1) { // Yes
					 if (pc.isInParty()){//파티
						   if (pc.isDead()){
						    pc.getParty().refresh(pc);
						   }
					 }
					pc.sendPackets(new S_SkillSound(pc.getId(), '\346'));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(
							pc.getId(), '\346'));
					pc.resurrect(pc.getMaxHp() / 2);
					pc.setCurrentHp(pc.getMaxHp() / 2);
					pc.startMpRegenerationByDoll();
					pc.sendPackets(new S_Resurrection(pc, resusepc1, 0));
					Broadcaster.broadcastPacket(pc, new S_Resurrection(pc,
							resusepc1, 0));
					pc.sendPackets(new S_CharVisualUpdate(pc));
					Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
				}
			}
			break;

		case 322: // 또 부활하고 싶습니까? (Y/N)
			c = readH();
			L1PcInstance resusepc2 = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (resusepc2 != null) { // 축복된 부활 스크롤, 리자레크션, 그레이타리자레크션
				if (c == 0) { // No
					;
				} else if (c == 1) { // Yes
					 if (pc.isInParty()){//파티
						   if (pc.isDead()){
						    pc.getParty().refresh(pc);
						   }
					 }
					pc.sendPackets(new S_SkillSound(pc.getId(), '\346'));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(
							pc.getId(), '\346'));
					pc.resurrect(pc.getMaxHp());
					pc.setCurrentHp(pc.getMaxHp());
					pc.startMpRegenerationByDoll();
					pc.startMpRegenerationByDoll();
					pc.sendPackets(new S_Resurrection(pc, resusepc2, 0));
					Broadcaster.broadcastPacket(pc, new S_Resurrection(pc,
							resusepc2, 0));
					pc.sendPackets(new S_CharVisualUpdate(pc));
					Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
					// EXP 로스트 하고 있는, G-RES를 걸 수 있던, EXP 로스트 한 사망
					// 모두를 채우는 경우만 EXP 복구
					if (pc.getExpRes() == 1 && pc.isGres() && pc.isGresValid()) {
						pc.resExp();
						pc.setExpRes(0);
						pc.setGres(false);
					}
				}
			}
			break;

		case 325: // 동물의 이름을 결정해 주세요：
			c = readH(); // ?
			name = readS();
			L1PetInstance pet = (L1PetInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			renamePet(pet, name);
			break;

		case 512: // 가의 이름은?
			c = readH(); // ?
			name = readS();
			int houseId = pc.getTempID();
			pc.setTempID(0);
			if (name.length() <= 16) {
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				house.setHouseName(name);
				HouseTable.getInstance().updateHouse(house); // DB에 기입해
			} else {
				pc.sendPackets(new S_ServerMessage(513)); // 가의 이름이 너무 깁니다.
			}
			break;
		case 1565: //드루가 베일에게 드래곤 키를 획득했다는 사실을 알리겠습니까?
			c = readH();
			if (c == 1) {
				if (checkdragonkey(pc)){
					String date = currentTime();
					L1ItemInstance dragonkey = pc.getInventory().findItemId(L1ItemId.DRAGON_KEY);
					if(dragonkey != null){
						BoardTable.getInstance().writeDragonKey(pc, dragonkey, date,4212014);
						pc.sendPackets(new S_ServerMessage(1567));// 등록되었다
					}
					return;
				}
			}
			break;
		case 2923: //레이드
			c = readH();
				if(c == 0){
					pc.sendPackets(new S_SystemMessage("드래곤 포탈 입장이 취소되었습니다."));
				} else if (c == 1) {
					if(pc.DragonPortalLoc[0] != 0){
						Collection<L1PcInstance> templist = L1World.getInstance().getAllPlayers();
						L1PcInstance[] list = templist.toArray(new L1PcInstance[templist.size()]);
						int count = 0;
						for (L1PcInstance player : list) {
							if (player == null)
								continue;
							if (player.getMapId() == pc.DragonPortalLoc[2]) {
								count += 1;
							}
						}
						if(count >= 32){
							pc.sendPackets(new S_ServerMessage(1536));// 인원이 가득차서 더 이상 입장할 수 없습니다.
							return;
						}
						L1Teleport.teleport(pc, pc.DragonPortalLoc[0], pc.DragonPortalLoc[1], (short) pc.DragonPortalLoc[2], 5, true);
					}
				}
				pc.DragonPortalLoc[0] = 0;
				pc.DragonPortalLoc[1] = 0;
				pc.DragonPortalLoc[2] = 0;
				BossClan(pc);
				break;
		case 622: 
			c = readC();   
			   BuddyTable buddyTable = BuddyTable.getInstance();
			   L1Buddy buddyList = buddyTable.getBuddyTable(pc.getId());
			   L1PcInstance target2 = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
			   pc.setTempID(0);
			   String name2 = pc.getName();
			   if (target2 != null) { // 있다면
			    if (c == 0) { // No
			     target2.sendPackets(new S_SystemMessage(pc.getName() + "님이 친구 요청을 거절하였습니다."));
			    } else if (c == 1) { // Yes
			     buddyList.add(pc.getId(), name2);
			     buddyTable.addBuddy(target2.getId(), pc.getId(), name2);
			     target2.sendPackets(new S_SystemMessage(pc.getName() + "님이 친구 등록 되었습니다."));
			     pc.sendPackets(new S_SystemMessage(target2.getName() + "님에게 친구 등록이 되었습니다."));
			    }
			   } else {
			    target2.sendPackets(new S_SystemMessage("그러한 케릭명을 가진 사람이 없습니다."));
			   }
			   break; 
		case 630:
			c = readH();
			L1PcInstance fightPc = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getFightId());
			if (c == 0) {
				pc.setFightId(0);
				fightPc.setFightId(0);
				fightPc.sendPackets(new S_ServerMessage(631, pc.getName()));
			} else if (c == 1) {
				fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL,
						fightPc.getFightId(), fightPc.getId()));
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, pc
						.getFightId(), pc.getId()));
			}
			break;

		case 653: // 이혼을 하면(자) 링은 사라져 버립니다. 이혼을 바랍니까? (Y/N)
			c = readH();
			L1PcInstance target653 = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getPartnerId());
			if (c == 0) { // No
				return;
			} else if (c == 1) { // Yes
				if(!pc.getInventory().checkItem(40308, 2000000)){
					 pc.sendPackets(new S_SystemMessage("\\fC이혼을 하려면 위자료  200만 아데나가 필요합니다."));	//위자료 BY호꾸
					return;
				}
				
				
				if (target653 != null) {
					target653.setPartnerId(0);
					target653.save();
					target653.sendPackets(new S_ServerMessage(662));
				} else {
					CharacterTable.getInstance().updatePartnerId(
							pc.getPartnerId());
				}
			}

				
			    pc.setPartnerId(0);
				pc.save(); // DB에 캐릭터 정보를 기입한다
				pc.getInventory().consumeItem(40308, 2000000);
				pc.sendPackets(new S_ServerMessage(662)); 
			break;

		case 654: // %0%s당신과 결혼 하고 싶어하고 있습니다. %0과 결혼합니까? (Y/N)
			c = readH();
			L1PcInstance partner = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (partner != null) {
				if (c == 0) { // No
					partner.sendPackets(new S_ServerMessage(656, pc.getName())); // %0%s는
					// 당신과의
					// 결혼을
					// 거절했습니다.
				} else if (c == 1) { // Yes
					pc.setPartnerId(partner.getId());
					pc.save();
					pc.sendPackets(new S_ServerMessage(790)); // 모두의 축복
					// 중(안)에서, 두 명의
					// 결혼을 했습니다.
					pc.sendPackets(new S_ServerMessage(655, partner.getName())); // 축하합니다!
					// %0과
					// 결혼했습니다.
					pc.sendPackets(new S_SkillSound(pc.getId(), 2059));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(
							pc.getId(), 2059));
					Announcements.getInstance().announceToAll(
							("축하합니다! " + pc.getName() + "님과 "
									+ partner.getName() + "님이 결혼하셨습니다."));
					partner.setPartnerId(pc.getId());
					partner.save();
					partner.sendPackets(new S_ServerMessage(790)); // 모두의 축복
					// 중(안)에서, 두
					// 명의 결혼을
					// 했습니다.
					partner.sendPackets(new S_ServerMessage(655, pc.getName())); // 축하합니다!
					// %0과
					// 결혼했습니다.
					partner
							.sendPackets(new S_SkillSound(partner.getId(), 2059));
					Broadcaster.broadcastPacket(partner, new S_SkillSound(
							partner.getId(), 2059));
				}
			}
			break;

		case 729: // 혈맹원이 당신을 텔레포트 시키려고 하고 있습니다. 응합니까? (Y/N)
			c = readH();
			if (c == 0) {
			} else if (c == 1) { // Yes
				callClan(pc);
			}
			break;
		case 2551: //경험치 회복에는 구호 증서가 소모됩니다. 경험치를 회복하시겠습니까? (Y/N)
			   c = readH();
			   if (c == 0) {}
			   else if (c == 1 && pc.getExpRes() == 1) {
			    pc.getInventory().consumeItem(701235, 1);
			    int needExp = ExpTable.getNeedExpNextLevel(pc.getLevel());
			    double PobyExp = needExp * 0.05;
			    pc.addExp((int) PobyExp);
			    pc.setExpRes(0);
			   }
			   break;
		case 738:// 경험치를 회복하려면%0의 아데나가 필요합니다. 경험치를 회복합니까?
			c = readH();
			if (c == 0) {
			} else if (c == 1 && pc.getExpRes() == 1) { // Yes
				int cost = 0;
				int level = pc.getLevel();
				int lawful = pc.getLawful();
				if (level < 45) {
					cost = level * level * 50;
				} else {
					cost = level * level * 100;
				}
				if (lawful >= 0) {
					cost = (cost / 2);
				}
				cost *= 2;
				if (pc.getLevel() > 9)
					if (pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
						pc.resExpToTemple();
						pc.setExpRes(0);
					} else {
						//pc.sendPackets(new S_ServerMessage(189));
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아데나가 충분치 않습니다.", Opcodes.S_OPCODE_MSG, 20); 
						pc.sendPackets(s_chatpacket);
					}
				else {
				//	pc.sendPackets(new S_SystemMessage("경험치를 회복하려면 레벨10부터 사용하실수 있습니다."));
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"경험치를 회복하려면 레벨10부터 사용하실수 있습니다.", Opcodes.S_OPCODE_MSG, 20); 
					pc.sendPackets(s_chatpacket);
					// 부족합니다.
				}
			}
			break;

		case 951: // 채팅 파티 초대를 허가합니까? (Y/N)
			c = readH();
			L1PcInstance chatPc = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getPartyID());
			if (chatPc != null) {
				if (c == 0) { // No
					chatPc.sendPackets(new S_ServerMessage(423, pc.getName())); // %0가 초대를거부했습니다.
					pc.setPartyID(0);
				} else if (c == 1) { // Yes
					if (chatPc.isInChatParty()) {
						if (chatPc.getChatParty().isVacancy() || chatPc.isGm()) {
							chatPc.getChatParty().addMember(pc);
						} else {
							chatPc.sendPackets(new S_ServerMessage(417)); // 더 이상 파티 멤버를 받아들일 수 없습니다.
						}
					} else {
						L1ChatParty chatParty = new L1ChatParty();
						chatParty.addMember(chatPc);
						chatParty.addMember(pc);
						chatPc.sendPackets(new S_ServerMessage(424, pc
								.getName())); // %0가파티에 들어갔습니다.
					}
				}
			}
			break;

		case 953: // 파티 초대를 허가합니까? (Y/N)
		case 954: // 분배 파티 초대 한다~
			c = readH();
			L1PcInstance target = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getPartyID());
			if (target != null) {
				if (c == 0) { // No
					target.sendPackets(new S_ServerMessage(423, pc.getName())); // %0가
					// 초대를
					// 거부했습니다.
					pc.setPartyID(0);
				} else if (c == 1) { // Yes
				//////////////////배틀존파티/////////////
					if(target.getMapId() == 5153 || target.getMapId() == 5083
							|| pc.getMapId() == 5153 || pc.getMapId() == 5083){
						target.sendPackets(new S_ServerMessage(423, pc.getName())); // %0가 초대를 거부했습니다.
						return;
					}
					//////////////////배틀존파티/////////////
					if (target.isInParty()) { // 초대주가 파티중
						if (target.getParty().isVacancy() || target.isGm()) { // 파티에
							// 빈 곳이
							// 있다
							target.getParty().addMember(pc);
						} else { // 파티에 빈 곳이 없다
							target.sendPackets(new S_ServerMessage(417)); // 더 이상
							// 파티
							// 멤버를
							// 받아들일
							// 수
							// 없습니다.
						}
					} else {
						// 초대주가 파티중이 아니다
						L1Party party = new L1Party();
						party.addMember(target);
						party.addMember(pc);
						target.sendPackets(new S_ServerMessage(424, pc
								.getName())); // %0가
						// 파티에
						// 들어갔습니다.
					}
				}
			}
			break;

		case 1256: // 경기장에 입장하시겠습니까? (Y/N)
			c = readH();
			if (c == 0) {
				miniGameRemoveEnterMember(pc);
			} else if (c == 1) {
				if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
					pc.getInventory().consumeItem(L1ItemId.ADENA, 1000);
					if (pc.isInParty())
						pc.getParty().leaveMember(pc);
					L1SkillUse l1skilluse = new L1SkillUse();
					l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc
							.getId(), pc.getX(), pc.getY(), null, 0,
							L1SkillUse.TYPE_LOGIN);

					if (GhostHouse.getInstance().isEnterMember(pc)) {
						if (GhostHouse.getInstance().isPlayingNow()) {
							pc.sendPackets(new S_ServerMessage(1182));
							return;
						}
						for (L1DollInstance doll : pc.getDollList().values()) {
							doll.deleteDoll();
						}
						GhostHouse.getInstance().addPlayMember(pc);
						L1Teleport.teleport(pc, 32722, 32830, (short) 5140, 2,
								true);
					} else if (PetRacing.getInstance().isEnterMember(pc)) {
						if (PetRacing.getInstance().isPlay()) {
							pc.sendPackets(new S_ServerMessage(1182));
							return;
						}
						for (L1DollInstance doll : pc.getDollList().values()) {
							doll.deleteDoll();
						}
						pc.setPetRacing(true);
						PetRacing.getInstance().removeEnterMember(pc);
						PetRacing.getInstance().addPlayMember(pc);
						L1Teleport.teleport(pc, 32768, 32848, (short) 5143, 5,
								true);
					}
				} else {
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아데나가 충분치 않습니다.", Opcodes.S_OPCODE_MSG, 20); 
					pc.sendPackets(s_chatpacket);
					miniGameRemoveEnterMember(pc);
				}
			}
			break;

		case 1268: // 데스매치에 입장하시겠습니까? (Y/N)
			c = readH();
			if (c == 0)
				DeathMatch.getInstance().giveBackAdena(pc);
			else if (c == 1)
				DeathMatch.getInstance().addPlayMember(pc);
			break;

		case 479: // 어느 능력치를 향상시킵니까? (str, dex, int, con, wis, cha)
			if (readC() == 1) {
				String s = readS();
				final int BONUS_ABILITY = pc.getAbility().getBonusAbility();

				if (!(pc.getLevel() - 50 > BONUS_ABILITY))
					return;

				if (s.toLowerCase().equals("str".toLowerCase())) {
					if (pc.getAbility().getStr() < 35) {
						pc.getAbility().addStr((byte) 1); // 소의 STR치에+1
						pc.getAbility().setBonusAbility(BONUS_ABILITY + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));		
						pc.save(); // DB에 캐릭터 정보를 기입한다
						pc.sendPackets(new S_SystemMessage("       * 보너스 스텟을 확인 합니다 *    "));
						pc.sendPackets(new S_SystemMessage(" STR: " +pc.getAbility().getStr()+
								" DEX:"+pc.getAbility().getDex()+
								" CON:"+pc.getAbility().getCon()+
								" IMT:"+pc.getAbility().getInt()+
								" WIS:"+pc.getAbility().getWis()+
								" CHA:"+pc.getAbility().getCha()));
					} else {
						pc.sendPackets(new S_ServerMessage(481)); // 하나의 능력치의 최대치는 25입니다. 다른 능력치를 선택해 주세요
					}
				} else if (s.toLowerCase().equals("dex".toLowerCase())) {
					if (pc.getAbility().getDex() < 35) {
						pc.getAbility().addDex((byte) 1);
						pc.resetBaseAc();
						pc.getAbility().setBonusAbility(BONUS_ABILITY + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save();
						pc.sendPackets(new S_SystemMessage("       * 보너스 스텟을 확인 합니다 *    "));
						pc.sendPackets(new S_SystemMessage(" STR: " +pc.getAbility().getStr()+
								" DEX:"+pc.getAbility().getDex()+
								" CON:"+pc.getAbility().getCon()+
								" IMT:"+pc.getAbility().getInt()+
								" WIS:"+pc.getAbility().getWis()+
								" CHA:"+pc.getAbility().getCha()));
					} else {
						pc.sendPackets(new S_ServerMessage(481));
					}
				} else if (s.toLowerCase().equals("con".toLowerCase())) {
					if (pc.getAbility().getCon() < 35) {
						pc.getAbility().addCon((byte) 1);
						pc.getAbility().setBonusAbility(BONUS_ABILITY + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save();
						pc.sendPackets(new S_SystemMessage("       * 보너스 스텟을 확인 합니다 *    "));
						pc.sendPackets(new S_SystemMessage(" STR: " +pc.getAbility().getStr()+
								" DEX:"+pc.getAbility().getDex()+
								" CON:"+pc.getAbility().getCon()+
								" IMT:"+pc.getAbility().getInt()+
								" WIS:"+pc.getAbility().getWis()+
								" CHA:"+pc.getAbility().getCha()));
					} else {
						pc.sendPackets(new S_ServerMessage(481));
					}
				} else if (s.toLowerCase().equals("int".toLowerCase())) {
					if (pc.getAbility().getInt() < 35) {
						pc.getAbility().addInt((byte) 1);
						pc.getAbility().setBonusAbility(BONUS_ABILITY + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save();
						pc.sendPackets(new S_SystemMessage("       * 보너스 스텟을 확인 합니다 *    "));
						pc.sendPackets(new S_SystemMessage(" STR: " +pc.getAbility().getStr()+
								" DEX:"+pc.getAbility().getDex()+
								" CON:"+pc.getAbility().getCon()+
								" IMT:"+pc.getAbility().getInt()+
								" WIS:"+pc.getAbility().getWis()+
								" CHA:"+pc.getAbility().getCha()));
					} else {
						pc.sendPackets(new S_ServerMessage(481));
					}
				} else if (s.toLowerCase().equals("wis".toLowerCase())) {
					if (pc.getAbility().getWis() < 35) {
						pc.getAbility().addWis((byte) 1);
						pc.resetBaseMr();
						pc.getAbility().setBonusAbility(BONUS_ABILITY + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save();
						pc.sendPackets(new S_SystemMessage("       * 보너스 스텟을 확인 합니다 *    "));
						pc.sendPackets(new S_SystemMessage(" STR: " +pc.getAbility().getStr()+
								" DEX:"+pc.getAbility().getDex()+
								" CON:"+pc.getAbility().getCon()+
								" IMT:"+pc.getAbility().getInt()+
								" WIS:"+pc.getAbility().getWis()+
								" CHA:"+pc.getAbility().getCha()));
					} else {
						pc.sendPackets(new S_ServerMessage(481));
					}
				} else if (s.toLowerCase().equals("cha".toLowerCase())) {
					if (pc.getAbility().getCha() < 35) {
						pc.getAbility().addCha((byte) 1);
						pc.getAbility().setBonusAbility(BONUS_ABILITY + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save();
						pc.sendPackets(new S_SystemMessage("       * 보너스 스텟을 확인 합니다 *    "));
						pc.sendPackets(new S_SystemMessage(" STR: " +pc.getAbility().getStr()+
								" DEX:"+pc.getAbility().getDex()+
								" CON:"+pc.getAbility().getCon()+
								" IMT:"+pc.getAbility().getInt()+
								" WIS:"+pc.getAbility().getWis()+
								" CHA:"+pc.getAbility().getCha()));
					} else {
						pc.sendPackets(new S_ServerMessage(481));
					}
				}
				pc.CheckStatus();
						if (pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getAbility().getBonusAbility()) {
						    if ((pc.getAbility().getStr() + pc.getAbility().getDex() + pc.getAbility().getCon()
						    		+ pc.getAbility().getInt() + pc.getAbility().getWis() + pc.getAbility().getCha()) < 150) {
						          pc.sendPackets(new S_BonusStats(pc.getId(), 1));
					}
				}// 스텟리스없이 찍기
			}
			break;
		default:
			break;
		}
	}
	private static String currentTime() {
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(tz);
		int year = cal.get(Calendar.YEAR) - 2000;
		String year2;
		if (year < 10) {
			year2 = "0" + year;
		} else {
			year2 = Integer.toString(year);
		}
		int Month = cal.get(Calendar.MONTH) + 1;
		String Month2 = null;
		if (Month < 10) {
			Month2 = "0" + Month;
		} else {
			Month2 = Integer.toString(Month);
		}
		int date = cal.get(Calendar.DATE);
		String date2 = null;
		if (date < 10) {
			date2 = "0" + date;
		} else {
			date2 = Integer.toString(date);
		}
		return year2 + "/" + Month2 + "/" + date2;
	}
	private void BossClan(L1PcInstance pc) {
		try {
			if (pc.getMapId() == 1005) {
				Thread.sleep(100L);
				pc.sendPackets(new S_SystemMessage("난쟁이의 외침 : 곧 코마들이 이곳을 지키기 위해 몰려올 것입니다."));
				pc.sendPackets(new S_SystemMessage("난쟁이의 외침 : 코마들은 광물을 먹고 사는 종족인데, 지능은 좀 떨어지지만 매우 강력한 힘을 지니고 있습니다."));
				pc.sendPackets(new S_SystemMessage("난쟁이의 외침 : 지룡 안타라스의 안식을 지키기 위해 암흑용 할파스가 파견한 수하들 입니다."));
				pc.sendPackets(new S_SystemMessage("난쟁이의 외침 : 아아..그들이 왔습니다. 모두 조심하시기 바랍니다.!!"));
			}
			if (pc.getMapId() == 1011) {
				Thread.sleep(100L);
				pc.sendPackets(new S_ServerMessage(1679));
				pc.sendPackets(new S_ServerMessage(1680));
				pc.sendPackets(new S_ServerMessage(1681));	
				pc.sendPackets(new S_ServerMessage(1662));	
			}
			if (pc.getMapId() == 1017) {//린드레이드
				Thread.sleep(100L);
				pc.sendPackets(new S_SystemMessage("린드비오르 : 누가 나의 단잠을 방해 하는가?"));
				pc.sendPackets(new S_SystemMessage("린드비오르 : 케레니스 또 나를 화나게 하려는 건가?"));
				pc.sendPackets(new S_SystemMessage("린드비오르 : 어리석은 인간들 이군..."));
				pc.sendPackets(new S_SystemMessage("린드비오르 : 나 린드비오르를 화나게 한 대가를 치룰 것이다."));
			}
			} catch (Exception e) {
			}
	}
	private boolean checkdragonkey(L1PcInstance pc){
		if(pc.getInventory().checkItem(L1ItemId.DRAGON_KEY)){
			if (BoardTable.getInstance().checkExistName(pc.getName(), 4212014)) {
				pc.sendPackets(new S_ServerMessage(1568));// 이미 등록되어 있어
				return false;
			}else{
				return true;
			}
		}else{
			pc.sendPackets(new S_ServerMessage(1566));// 드래곤 키 있어야 해
			return false;
		}
	
	}


	private static void renamePet(L1PetInstance pet, String name) {
		if (pet == null || name == null) {
			throw new NullPointerException();
		}

		int petItemObjId = pet.getItemObjId();
		L1Pet petTemplate = PetTable.getInstance().getTemplate(petItemObjId);
		if (petTemplate == null) {
			throw new NullPointerException();
		}

		L1PcInstance pc = (L1PcInstance) pet.getMaster();
		if (PetTable.isNameExists(name)) {
			pc.sendPackets(new S_ServerMessage(327)); // 같은 이름이 벌써 존재하고 있습니다.
			return;
		}
		pet.setName(name);
		petTemplate.set_name(name);
		PetTable.getInstance().storePet(petTemplate); // DB에 기입해
		L1ItemInstance item = pc.getInventory().getItem(pet.getItemObjId());
		pc.getInventory().updateItem(item);
		pc.sendPackets(new S_ChangeName(pet.getId(), name));
		Broadcaster.broadcastPacket(pc, new S_ChangeName(pet.getId(), name));
	}

	private void callClan(L1PcInstance pc) {
		L1PcInstance callClanPc = (L1PcInstance) L1World.getInstance()
				.findObject(pc.getTempID());
		boolean isInWarArea = false;
		short mapId = callClanPc.getMapId();
		int castleId = L1CastleLocation.getCastleIdByArea(callClanPc);

		pc.setTempID(0);


		if (!pc.getMap().isEscapable() && !pc.isGm()) {
			pc.sendPackets(new S_ServerMessage(647));
			L1Teleport.teleport(pc, pc.getLocation(), pc.getMoveState()
					.getHeading(), false);
			return;
		}

		if (pc.getId() != callClanPc.getCallClanId()) {
			return;
		}

		if (castleId != 0) {
			isInWarArea = true;
			if (WarTimeController.getInstance().isNowWar(castleId)) {
				isInWarArea = false;
			}
		}

		if (mapId != 0 && mapId != 4 && mapId != 304 || isInWarArea) {
			pc.sendPackets(new S_ServerMessage(547));
			return;
		}

		L1Map map = callClanPc.getMap();
		int locX = callClanPc.getX();
		int locY = callClanPc.getY();
		int heading = callClanPc.getCallClanHeading();
		locX += HEADING_TABLE_X[heading];
		locY += HEADING_TABLE_Y[heading];
		heading = (heading + 4) % 4;

		boolean isExsistCharacter = false;
		L1Character cha = null;
		for (L1Object object : L1World.getInstance().getVisibleObjects(
				callClanPc, 1)) {
			if (object instanceof L1Character) {
				cha = (L1Character) object;
				if (cha.getX() == locX && cha.getY() == locY
						&& cha.getMapId() == mapId) {
					isExsistCharacter = true;
					break;
				}
			}
		}

		if (locX == 0 && locY == 0 || !map.isPassable(locX, locY)
				|| isExsistCharacter) {
			pc.sendPackets(new S_ServerMessage(627));
			return;
		}
		L1Teleport.teleport(pc, locX, locY, mapId, heading, true,
				L1Teleport.CALL_CLAN);
	}

	private void miniGameRemoveEnterMember(L1PcInstance pc) {
		if (GhostHouse.getInstance().isEnterMember(pc))
			GhostHouse.getInstance().removeEnterMember(pc);
		else if (PetRacing.getInstance().isEnterMember(pc))
			PetRacing.getInstance().removeEnterMember(pc);
	}

	@Override
	public String getType() {
		return C_ATTR;
	}
}