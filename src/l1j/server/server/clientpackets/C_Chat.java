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

import java.util.Random;
import java.util.StringTokenizer;

import l1j.server.Config;
import l1j.server.server.AutoCheckTimer;
import l1j.server.server.GMCommands;
import l1j.server.server.Opcodes;
import l1j.server.server.UserCommands;
import l1j.server.server.TimeController.WitsTimeController;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SystemMessage;
import server.CodeLogger;
import server.LineageClient;
import server.manager.eva;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

// chat opecode type
// 통상 0x44 0x00
// 절규(! ) 0x44 0x00
// 속삭임(") 0x56 charname
// 전체(&) 0x72 0x03
// 트레이드($) 0x44 0x00
// PT(#) 0x44 0x0b
// 혈맹(@) 0x44 0x04
// 연합(%) 0x44 0x0d
// CPT(*) 0x44 0x0e
// 군주전용 (+)
public class C_Chat extends ClientBasePacket {

	private static final String C_CHAT = "[C] C_Chat";

	public C_Chat(byte abyte0[], LineageClient clientthread) {
		super(abyte0);
try{
		L1PcInstance pc = clientthread.getActiveChar();
		
		int chatType = readC();
		String chatText = readS();
		
		if(pc == null || clientthread.getActiveChar() == null){ // Null 처리
			   clientthread.kick();
			   return;
			  }
		 if(L1World.getInstance().getPlayer(clientthread.getActiveChar().getName()) == null){ // 월드 오브젝트에서 찾을 수 없다면
			   try {
			    System.out.println("월드Null : " + clientthread.getActiveChar().getName() + " / " + clientthread.getIp());
			   } catch (Exception e) {
			   }
			   clientthread.kick();
			   return;
			  }
			if (pc.getMapId() == 631 && !pc.isGm()) {
				pc.sendPackets(new S_ServerMessage(912)); // 채팅을 할 수 없습니다.
				return;
			}
		 if(pc.getMapId() == 5153){//배틀존 같은 라인끼리 채팅
				if(!pc.isGm()){
			   for (L1PcInstance battlemember : L1World.getInstance().getRecognizePlayer(pc)) {
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_NORMALCHAT, 0);
						if(pc.get_DuelLine() == battlemember.get_DuelLine()){
							if (!battlemember.getExcludingList().contains(pc.getName())) {
								pc.sendPackets(s_chatpacket);
								battlemember.sendPackets(s_chatpacket);
								return;
						}
					}
				}
		     }
		 }

		int a_code = AutoCheckTimer.aCode;

		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.AREA_OF_SILENCE)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.STATUS_POISON_SILENCE)) {
			return;
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.STATUS_CHAT_PROHIBITED)) { // 채팅
			// 금지중
			pc.sendPackets(new S_ServerMessage(242)); // 현재 채팅 금지중입니다.
			return;
		}

		if (pc.isDeathMatch() && !pc.isGhost()) {
			pc.sendPackets(new S_ServerMessage(912)); // 채팅을 할 수 없습니다.
			return;
		}

		switch (chatType) {
		case 0: 
			if (pc.isGhost() && !(pc.isGm() || pc.isMonitor())) {
				return;
			}

			// 자동사냥
			if (pc.오토확인이필요한상태인지함수()) {// pc는 L1PcInstance를 호출하는 객체, 즉 그냥
				// L1PcInstance클래스에
				int CompareCode = Integer.parseInt(chatText);
				if (a_code == CompareCode) {
					pc.오토확인불필요상태로바꾸기();
					pc
							.sendPackets(new S_SystemMessage(
									"오토 사용유저가 아닌게 인증되었습니다."));
				} else if (a_code != CompareCode) {// pc는 L1PcInstance를 호출하는
													// 객체, 즉
					// 그냥 L1PcInstance클래스에
					pc.sendPackets(new S_SystemMessage("인증 코드 를 잘못 입력 하였습니다."));// (2)
					pc.sendPackets(new S_SystemMessage("인증 코드는 : " + a_code
							+ " 입니다."));// (3)
				}
			}
			// / 자동사냥

			// GM커멘드
			if (chatText.startsWith(".")
					&& (pc.getAccessLevel() == Config.GMCODE || pc
							.getAccessLevel() == 1)) {
				String cmd = chatText.substring(1);
				GMCommands.getInstance().handleCommands(pc, cmd);
				return;
			}
			if (chatText.startsWith("$")) {
				String text = chatText.substring(1);
				chatWorld(pc, text, 12);
				if (!pc.isGm()) {
					pc.checkChatInterval();
				}
				return;
			}
			if (chatText.startsWith(".")) { // 유저코멘트
				String cmd = chatText.substring(1);

				UserCommands.getInstance().handleCommands(pc, cmd);
				return;
			}

			if (chatText.startsWith("$")) { // 월드채팅
				String text = chatText.substring(1);

				chatWorld(pc, text, 12);
				if (!pc.isGm()) {
					pc.checkChatInterval();
				}
				return;
			}


			S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
					Opcodes.S_OPCODE_NORMALCHAT, 0);
			if (!pc.getExcludingList().contains(pc.getName())) {
				pc.sendPackets(s_chatpacket);
			}
			for (L1PcInstance listner : L1World.getInstance()
					.getRecognizePlayer(pc)) {
				if (!listner.getExcludingList().contains(pc.getName())) {
					listner.sendPackets(s_chatpacket);
				}
			}
			

			// 돕펠 처리
			L1MonsterInstance mob = null;
			for (L1Object obj : pc.getNearObjects().getKnownObjects()) {
				if (obj instanceof L1MonsterInstance) {
					mob = (L1MonsterInstance) obj;
					if (mob.getNpcTemplate().is_doppel()
							&& mob.getName().equals(pc.getName())) {
						Broadcaster.broadcastPacket(mob, new S_NpcChatPacket(
								mob, chatText, 0));
					}
				}
			}
			eva.LogChatNormalAppend("[일반]", pc.getName(), chatText);
			break;
		case 2: 
			if (pc.isGhost()) {
				return;
			}
			S_ChatPacket s_chatpacket2 = new S_ChatPacket(pc, chatText,
					Opcodes.S_OPCODE_NORMALCHAT, 2);
			if (!pc.getExcludingList().contains(pc.getName())) {
				pc.sendPackets(s_chatpacket2);
			}
			for (L1PcInstance listner : L1World.getInstance().getVisiblePlayer(
					pc, 50)) {
				if (!listner.getExcludingList().contains(pc.getName())) {
					listner.sendPackets(s_chatpacket2);
				}
			}
			eva.LogChatNormalAppend("[일반]", pc.getName(), chatText);
			// 돕펠 처리
			for (L1Object obj : pc.getNearObjects().getKnownObjects()) {
				if (obj instanceof L1MonsterInstance) {
					mob = (L1MonsterInstance) obj;
					if (mob.getNpcTemplate().is_doppel()
							&& mob.getName().equals(pc.getName())) {
						for (L1PcInstance listner : L1World.getInstance()
								.getVisiblePlayer(mob, 50)) {
							listner.sendPackets(new S_NpcChatPacket(mob,
									chatText, 2));
						}
					}
				}
			}
		
			break;
		case 3: 
			chatWorld(pc, chatText, chatType);
			CodeLogger.getInstance().chatlog("&&",pc.getName() + "	" + chatText);
		//	eva.writeMessage(2, pc.getName() + ":" + chatText);
			String temp = "";
			StringTokenizer values = new StringTokenizer(chatText, " ");
			while (values.hasMoreElements()) {// 공백제거
				temp += values.nextToken();
			}
			String f[] = { "시발", "병신", "씨발", "씨발년", "창녀", "미췬", "애미", "애비",
					"시.발", "아가리", "얘비", "얘미", "섹스", "sex", "섹s", "좆까", "폰섹",
					"좃가", "좃까", "쉬발", "쉬팔", "fuck", "미.친", "니.미", "니미", "씨.발",
					"병.신", "좆망", "빙.신", "빙신", "현질", "현거래", "뻥티기", "현.질", "시볼",
					"비리", "비.리", "측근", "측.근", "ㅅㅂ", "꺼저", "꺼져", "닥치", "시바",
					"개새" };
			for (int i = 0; i < f.length; i++) {
				if (temp.indexOf(f[i]) > -1 && !pc.isGm()
						&& pc.getLevel() >= Config.GLOBAL_CHAT_LEVEL) {
					// 운영자이거나 전체채팅렙 안되면 제외
					int time = 10; // 채금시간설정 분단위
					pc.getSkillEffectTimerSet().setSkillEffect(1005,
							time * 60 * 1000);
					pc.sendPackets(new S_SkillIconGFX(36, time * 60));
					pc.sendPackets(new S_ServerMessage(242)); // 현재 채팅 금지중입니다.
					pc.sendPackets(new S_SystemMessage("금칙어 사용으로 " + time
							+ "분 채팅금지입니다"));

				}
			}
			if(pc.getLevel() >= Config.GLOBAL_CHAT_LEVEL){
				if (WitsTimeController.witsGameStarted) {
					try {
						 if (WitsTimeController._witsWinPlayers.containsKey(pc.getName())) {
							 pc.sendPackets(new S_SystemMessage("\\fV" + pc.getName() + "님은 현재 안정권에 진입하셨습니다."));
							 return;
						 }					 
						 
						 int count = 0;						
						 try {
							 count = Integer.parseInt(temp);
						 } catch (Exception e) {
							pc.sendPackets(new S_SystemMessage("\\fV숫자만 입력하세요."));
							return;
						 }
						 int witsCount = ++WitsTimeController.witsCount;
						 
						 for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
							 if (player.isGm()) {
								 player.sendPackets(new S_SystemMessage("\\fU" + pc.getName() + "님이 입력 : " + count));
									player.sendPackets(new S_SystemMessage("\\fU현재 카운터 : " + witsCount));
							 }
						 }
						 
						 if (count >= witsCount && witsCount <= 5) {
							 WitsTimeController._witsWinPlayers.put(pc.getName(), pc);
						 } else {
							 WitsTimeController.witsGameStarted = false;
							 WitsTimeController.witsCount = 0;
							 // 상품의 종류
							 String[] itemId = Config.ALT_WITS_PRESENT.split(",");
							 // 상품의 갯수
							 String[] itemCount = Config.ALT_WITS_PRESENT_COUNT.split(",");
							 					   
							 for (L1PcInstance player : WitsTimeController.getInstance().getWitsWinPlayers()) {
								 try {
									 if (player == null || player.getNetConnection() == null) {
											continue;
									 }
									 Random _random = new Random();
									 int rnd = _random.nextInt(5);//0~5
									 pc.getInventory().storeItem(Integer.parseInt(itemId[rnd]), Integer.parseInt(itemCount[rnd])); // 상품의 종류과 갯수

									 L1ItemInstance item = ItemTable.getInstance().createItem(Integer.parseInt(itemId[rnd]));
								   
									 player.sendPackets(new S_SystemMessage("\\fV" + item.getName() + "을 " + itemCount[rnd] + "개 얻었습니다."));	
									 
									  for (L1PcInstance gm : L1World.getInstance().getAllPlayers()) {
											if (gm.isGm()) {
											 gm.sendPackets(new S_SystemMessage(player.getName() + "님께 상품 : " + item.getName() + "을(를) " + itemCount[rnd] + "개 지급하였습니다."));
										 }
									 }
									 
									 WitsTimeController._witsWinPlayers.remove(player.getName());
								 } catch (Exception e) { }
							 }
							 
							 L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fY눈치 게임이 종료되었습니다." ));
							 
							 if (WitsTimeController.witsGameCount == 0) {
								 WitsTimeController.getInstance().stopcheckChatTime();
							 }
						 }
					} catch (Exception e) { }				 
				}  
			}
			break;
		case 4: 
			if (pc.getClanid() != 0) { // 크란 소속중
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				int rank = pc.getClanRank();
				if (clan != null&& (rank == L1Clan.CLAN_RANK_PROBATION || rank == L1Clan.CLAN_RANK_PUBLIC|| rank == L1Clan.CLAN_RANK_GUARDIAN 
								|| rank == L1Clan.CLAN_RANK_SUB_PRINCE || rank == L1Clan.CLAN_RANK_PRINCE )) {

					S_ChatPacket s_chatpacket4 = new S_ChatPacket(pc, chatText,
							Opcodes.S_OPCODE_MSG, 4);

						CodeLogger.getInstance().chatlog("@@", pc.getName()+"["+pc.getClanname()+"]: "+chatText);

						eva.LogChatClanAppend("[혈맹]", pc.getName(), pc.getClanname(), chatText);
					for (L1PcInstance listner : clan.getOnlineClanMember()) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							listner.sendPackets(s_chatpacket4);
						}
					}
				}
			}
		
			break;
		case 11: 
			if (pc.isInParty()) { // 파티중

				S_ChatPacket s_chatpacket11 = new S_ChatPacket(pc, chatText,
						Opcodes.S_OPCODE_MSG, 11);
				for (L1PcInstance listner : pc.getParty().getMembers()) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						listner.sendPackets(s_chatpacket11);
					}
				}

			}
			CodeLogger.getInstance().chatlog("##",
					pc.getName() + "	" + chatText);
			eva.LogChatPartyAppend("[파티]", pc.getName(), chatText);
		
			break;
		case 12: 
			chatWorld(pc, chatText, chatType);
		
			break;
		case 13:  // 연합 채팅
			if (pc.getClanid() != 0) { // 혈맹 소속중
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				int rank = pc.getClanRank();
				if (clan != null&& (rank == L1Clan.CLAN_RANK_GUARDIAN || rank == L1Clan.CLAN_RANK_PRINCE)) {
					S_ChatPacket s_chatpacket13 = new S_ChatPacket(pc, chatText,Opcodes.S_OPCODE_MSG, 13);// 13
					for (L1PcInstance listner : clan.getOnlineClanMember()) {
						int listnerRank = listner.getClanRank();
						if (!listner.getExcludingList().contains(pc.getName())
								&& (listnerRank == L1Clan.CLAN_RANK_GUARDIAN || listnerRank == L1Clan.CLAN_RANK_PRINCE)) {
							listner.sendPackets(s_chatpacket13);
						}
					}
				}
			}
			CodeLogger.getInstance().chatlog("%%",
					pc.getName() + "	" + chatText);
		
			eva.LogChatPartyAppend("[연합]", pc.getName(), chatText);
		
			break;
		case 14:  // 채팅 파티
			if (pc.isInChatParty()) { // 채팅 파티중

				S_ChatPacket s_chatpacket14 = new S_ChatPacket(pc, chatText,
						Opcodes.S_OPCODE_NORMALCHAT, 14);
				for (L1PcInstance listner : pc.getChatParty().getMembers()) {
					if (!listner.getExcludingList().contains(pc.getName())) {
						listner.sendPackets(s_chatpacket14);
					}
				}
			}
			CodeLogger.getInstance().chatlog("**",
					pc.getName() + "	" + chatText);
			eva.LogChatPartyAppend("[파티]", pc.getName(), chatText);
		
			break;
		case 15: 
			if (pc.getClanid() != 0) { // 혈맹 소속중
				L1PcInstance allianceLeader = (L1PcInstance) L1World
						.getInstance().findObject(pc.getTempID());
				String TargetClanName = allianceLeader.getClanname();
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				L1Clan TargetClan = L1World.getInstance().getClan(
						TargetClanName);
				if (clan != null) {

					S_ChatPacket s_chatpacket15 = new S_ChatPacket(pc, chatText,
							Opcodes.S_OPCODE_NORMALCHAT, 15);
					// 원래는 온라인중인 자기의 혈원과 온라인중인 동맹의 혈원한테 쏘아주어야함. (현재는 대처용)
					for (L1PcInstance listner : clan.getOnlineClanMember()) {
						int AllianceClan = listner.getClanid();
						if (pc.getClanid() == AllianceClan) {
							listner.sendPackets(s_chatpacket15);
						}
					} // 자기혈맹 전송용
					for (L1PcInstance alliancelistner : TargetClan.getOnlineClanMember()) {

						int AllianceClan = alliancelistner.getClanid();
						if (pc.getClanid() == AllianceClan) {
							alliancelistner.sendPackets(s_chatpacket15);
						}
					} // 동맹혈맹 전송용
				
				}
			}
			break;
		case 17:
			if (pc.getClanid() != 0) { // 혈맹 소속중
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				if (clan != null
						&& (pc.isCrown() && pc.getId() == clan.getLeaderId())) {
					S_ChatPacket s_chatpacket17 = new S_ChatPacket(pc, chatText,Opcodes.S_OPCODE_MSG, 17);
					for (L1PcInstance listner : clan.getOnlineClanMember()) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							listner.sendPackets(s_chatpacket17);
							
							
							
							
	
							
							
						}
					}
				}
			}
			break;
		default:
			break;
		}
		if (!pc.isGm()) {
			pc.checkChatInterval();
		}
     }catch(Exception e){}
	}
	
	
	private void chatWorld(L1PcInstance pc, String chatText, int chatType) {
		if (pc.isGm() || pc.getAccessLevel() == 1) {			
			if (chatType == 3) {
				L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_MSG, chatType));
				eva.LogChatAppend("[전체]", pc.getName(), chatText);
			} else if (chatType == 12) {
				L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_MSG, 3));
				eva.LogChatTradeAppend("[장사]", pc.getName(), chatText);

			}			
		} else if (pc.isCrown() && pc.getClanid() != 0) { // 혈맹군주는 렙제한없이 전창가능
			if (L1World.getInstance().isWorldChatElabled()) {
				if (pc.get_food() >= 12) { // 5%
					pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));
					for (L1PcInstance listner : L1World.getInstance().getAllPlayers()) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							if (listner.isShowTradeChat() && chatType == 12) {
								listner.sendPackets(new S_ChatPacket(pc,chatText, Opcodes.S_OPCODE_MSG,chatType));
							} else if (listner.isShowWorldChat()&& chatType == 3) {
								listner.sendPackets(new S_ChatPacket(pc,chatText, Opcodes.S_OPCODE_MSG,chatType));
							}
						}
					}
				} else {
					pc.sendPackets(new S_ServerMessage(462));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(510));
			}
		} else if (pc.getLevel() >= Config.GLOBAL_CHAT_LEVEL) {
			if (L1World.getInstance().isWorldChatElabled()) {
				if (pc.get_food() >= 12) { // 5%
					if (chatType == 3) {
						pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));					
						eva.LogChatAppend("[전체]", pc.getName(), chatText);
					} else if (chatType == 12) {
						pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));
						eva.LogChatTradeAppend("[장사]", pc.getName(), chatText);
					}
					pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));
					for (L1PcInstance listner : L1World.getInstance()
							.getAllPlayers()) {
						if (!listner.getExcludingList().contains(pc.getName())) {
							if (listner.isShowTradeChat() && chatType == 12) {
								listner.sendPackets(new S_ChatPacket(pc,
										chatText, Opcodes.S_OPCODE_MSG,
										chatType));
							} else if (listner.isShowWorldChat()
									&& chatType == 3) {
								listner.sendPackets(new S_ChatPacket(pc,
										chatText, Opcodes.S_OPCODE_MSG,
										chatType));
							}
						}
					}
				} else {
					pc.sendPackets(new S_ServerMessage(462));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(510));
			}
		} else {
			pc.sendPackets(new S_ServerMessage(195, String
					.valueOf(Config.GLOBAL_CHAT_LEVEL)));
		}
	}

	@Override
	public String getType() {
		return C_CHAT;
	}
}
