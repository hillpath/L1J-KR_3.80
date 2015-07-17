/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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
import java.util.Random;

import javolution.util.FastTable;
import l1j.server.server.TimeController.HellDevilController;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.RealTime;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_Lawful;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import server.LineageClient;



public class C_NPCAction1 extends ClientBasePacket {

	private static final String C_NPC_ACTION1 = "[C] C_NPCAction1";	
	private static Random _random = new Random(System.nanoTime());


	public C_NPCAction1(byte abyte0[], LineageClient client) throws Exception {
		super(abyte0);
		int objid = readD();
		String s = readS();
		L1Object obj = L1World.getInstance().findObject(objid);
		
		if(s.equalsIgnoreCase("deadTrans") || s.equalsIgnoreCase("pvpSet") || s.equalsIgnoreCase("ShowHPMPRecovery") ||
				s.equalsIgnoreCase("showDisableEffectIcon") || s.equalsIgnoreCase("showDungeonTimeLimit") ){
			return;
		}


		int[] materials = null;
		int[] counts = null;
		int[] createitem = null;
		int[] createcount = null;

		String htmlid = null;
		String success_htmlid = null;
		String failure_htmlid = null;
		String[] htmldata = null;
		

		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}

		
	// 미니 게임 코마
		
	 if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4206003) {
		L1NpcInstance npc = (L1NpcInstance) obj;
		if (pc.isInvisble()) {
			pc.sendPackets(new S_NpcChatPacket(npc,"코마 버프를 받기 위해서는 투명상태가 아니어야 합니다.", 0));
			return;
		}
		if (s.equalsIgnoreCase("1")) {
			comaCheck(pc, 3, objid);
		} else if (s.equalsIgnoreCase("2")) {
			comaCheck(pc, 5, objid);
		} else if (s.equalsIgnoreCase("a")) {
			pc.setDeathMatchPiece(pc.getDeathMatchPiece() + 1);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("b")) {
			pc.setDeathMatchPiece(pc.getDeathMatchPiece() + 2);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("c")) {
			pc.setDeathMatchPiece(pc.getDeathMatchPiece() + 3);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("d")) {
			pc.setDeathMatchPiece(pc.getDeathMatchPiece() + 4);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("e")) {
			pc.setDeathMatchPiece(pc.getDeathMatchPiece() + 5);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("f")) {
			pc.setGhostHousePiece(pc.getGhostHousePiece() + 1);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("g")) {
			pc.setGhostHousePiece(pc.getGhostHousePiece() + 2);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("h")) {
			pc.setGhostHousePiece(pc.getGhostHousePiece() + 3);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("i")) {
			pc.setGhostHousePiece(pc.getGhostHousePiece() + 4);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("j")) {
			pc.setGhostHousePiece(pc.getGhostHousePiece() + 5);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("k")) {
			pc.setPetRacePiece(pc.getPetRacePiece() + 1);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("l")) {
			pc.setPetRacePiece(pc.getPetRacePiece() + 2);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("m")) {
			pc.setPetRacePiece(pc.getPetRacePiece() + 3);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("n")) {
			pc.setPetRacePiece(pc.getPetRacePiece() + 4);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("o")) {
			pc.setPetRacePiece(pc.getPetRacePiece() + 5);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("p")) {
			pc.setPetMatchPiece(pc.getPetMatchPiece() + 1);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("q")) {
			pc.setPetMatchPiece(pc.getPetMatchPiece() + 2);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("s")) {
			pc.setPetMatchPiece(pc.getPetMatchPiece() + 3);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("t")) {
			pc.setPetMatchPiece(pc.getPetMatchPiece() + 4);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("u")) {
			pc.setPetMatchPiece(pc.getPetMatchPiece() + 5);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("v")) {
			pc.setUltimateBattlePiece(pc.getUltimateBattlePiece() + 1);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("w")) {
			pc.setUltimateBattlePiece(pc.getUltimateBattlePiece() + 2);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("x")) {
			pc.setUltimateBattlePiece(pc.getUltimateBattlePiece() + 3);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("y")) {
			pc.setUltimateBattlePiece(pc.getUltimateBattlePiece() + 4);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("z")) {
			pc.setUltimateBattlePiece(pc.getUltimateBattlePiece() + 5);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("3")) {
			resetPiece(pc);
			selectComa(pc, objid);
		} else if (s.equalsIgnoreCase("4")) {
			giveComaBuff(pc, objid);
		}
		
		
	 } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50025  
		      || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50032 
		      || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50048 
		      || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50058 ){
		             if (s.equalsIgnoreCase("EnterSeller")) { // 포비: 상인을 찾는다
		          String s2 = readS(); 
		       L1PcInstance targetShop = L1World.getInstance().getPlayer(s2);
		       if (targetShop == null) {
		        pc.sendPackets(new S_ServerMessage(73, s2));
		       }
		        if (!targetShop.isPrivateShop()) {
		        pc.sendPackets(new S_SystemMessage(targetShop.getName()+"님은 상점을 열고 있지 않습니다."));
		        }
		          if (pc.getMapId() != targetShop.getMapId()) {
		        pc.sendPackets(new S_SystemMessage(targetShop.getName()+"님은 이 시장안에 없는 상점입니다."));
		       } else{ 
		       L1Teleport.teleportToTargetFront(pc, targetShop, 1);
		      }
		     }
	

		  	} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 94250) {// 스냅퍼
		    	   if (s.equals("A")){ // 76부터
		    	    if (pc.getQuest().isEnd(L1Quest.QUEST_SLOT76)){
		    	     pc.sendPackets(new S_ServerMessage(3255)); // 해당 슬롯은 이미 확장 되었습니다.
		    	    } else {
		    	     if (pc.getInventory().checkItem(40308, 10000000) && pc.getLevel() >=76){
		    	      pc.getInventory().consumeItem(40308, 10000000);
		    	      pc.getQuest().set_end(L1Quest.QUEST_SLOT76);
		    	      pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
		    	     } else {
		    	      pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot6"));
		    	     }
		    	    }
		    	   } else if (s.equals("B")){ // 81부터
		    	    if (pc.getQuest().isEnd(L1Quest.QUEST_SLOT81)){
		    	     pc.sendPackets(new S_ServerMessage(3255)); // 해당 슬롯은 이미 확장 되었습니다.
		    	    } else {
		    	     if (pc.getInventory().checkItem(40308, 30000000) && pc.getLevel() >= 81){
		    	      pc.getInventory().consumeItem(40308, 30000000);
		    	      pc.getQuest().set_end(L1Quest.QUEST_SLOT81);
		    	      pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
		    	     } else {
		    	      pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot6"));
		    	     }
		    	    }
		    	   }
		    
            
		             
		             
		
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71126) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			if (s.equalsIgnoreCase("0")) { // 라스타바드로 보내주세요
				RealTime time = RealTimeClock.getInstance().getRealTime();
				int entertime = pc.getLdungeonTime() % 1000;
				int enterday = pc.getLdungeonTime() / 1000;
				int dayofyear = time.get(Calendar.DAY_OF_YEAR);
				if (entertime > 180 && enterday == dayofyear) {
					pc.sendPackets(new S_NpcChatPacket(npc,	"라스타바드 사용 시간이 남아 있지 않습니다.", 0));
					htmlid = "";
					return;
				} else {
					if (enterday < dayofyear)
						pc.setLdungeonTime(dayofyear * 1000);
					L1Teleport.teleport(pc, 32728, 32848, (short) 451, 5, true); // 라스타바드 던전 1층집회장으로 텔레포트, 위치 임의
					int a = entertime % 60;
					if (a == 0) {
						int b = (180 - entertime) / 60;
						pc.sendPackets(new S_SystemMessage("라스타바드 사용 시간이 " + b + "시간 남았습니다."));// 시간
					} else if ((180 - entertime) < 60) {
						int c = 180 - entertime;
						pc.sendPackets(new S_SystemMessage("라스타바드 사용 시간이 " + c + "분 남았습니다."));// 분
					}
				}
			} else if (s.equalsIgnoreCase("1")) { // 라스타바드로 보내주세요
					RealTime time = RealTimeClock.getInstance().getRealTime();
					int entertime = pc.getLdungeonTime() % 1000;
					int enterday = pc.getLdungeonTime() / 1000;
					int dayofyear = time.get(Calendar.DAY_OF_YEAR);
					if (entertime > 180 && enterday == dayofyear) {
						pc.sendPackets(new S_NpcChatPacket(npc,	"라스타바드 사용 시간이 남아 있지 않습니다.", 0));
						htmlid = "";
						return;
					} else {
						if (enterday < dayofyear)
							pc.setLdungeonTime(dayofyear * 1000);
						L1Teleport.teleport(pc, 32675, 32836, (short) 475, 5, true); // 라스타바드 던전 1층집회장으로 텔레포트, 위치 임의
						int a = entertime % 60;
						if (a == 0) {
							int b = (180 - entertime) / 60;
							pc.sendPackets(new S_SystemMessage("라스타바드 사용 시간이 " + b + "시간 남았습니다."));// 시간
						} else if ((180 - entertime) < 60) {
							int c = 180 - entertime;
							pc.sendPackets(new S_SystemMessage("라스타바드 사용 시간이 " + c + "분 남았습니다."));// 분
						}
					}
			} else if (s.equalsIgnoreCase("2")) { // 라스타바드로 보내주세요
				RealTime time = RealTimeClock.getInstance().getRealTime();
				int entertime = pc.getLdungeonTime() % 1000;
				int enterday = pc.getLdungeonTime() / 1000;
				int dayofyear = time.get(Calendar.DAY_OF_YEAR);
				if (entertime > 180 && enterday == dayofyear) {
					pc.sendPackets(new S_NpcChatPacket(npc,	"라스타바드 사용 시간이 남아 있지 않습니다.", 0));
					htmlid = "";
					return;
				} else {
					if (enterday < dayofyear)
						pc.setLdungeonTime(dayofyear * 1000);
					L1Teleport.teleport(pc, 32684, 32833, (short) 462, 5, true); // 라스타바드 던전 1층집회장으로 텔레포트, 위치 임의
					int a = entertime % 60;
					if (a == 0) {
						int b = (180 - entertime) / 60;
						pc.sendPackets(new S_SystemMessage("라스타바드 사용 시간이 " + b + "시간 남았습니다."));// 시간
					} else if ((180 - entertime) < 60) {
						int c = 180 - entertime;
						pc.sendPackets(new S_SystemMessage("라스타바드 사용 시간이 " + c + "분 남았습니다."));// 분
					}
				}
			} else if (s.equalsIgnoreCase("3")) { // 라스타바드로 보내주세요
				RealTime time = RealTimeClock.getInstance().getRealTime();
				int entertime = pc.getLdungeonTime() % 1000;
				int enterday = pc.getLdungeonTime() / 1000;
				int dayofyear = time.get(Calendar.DAY_OF_YEAR);
				if (entertime > 180 && enterday == dayofyear) {
					pc.sendPackets(new S_NpcChatPacket(npc,	"라스타바드 사용 시간이 남아 있지 않습니다.", 0));
					htmlid = "";
					return;
				} else {
					if (enterday < dayofyear)
						pc.setLdungeonTime(dayofyear * 1000);
					L1Teleport.teleport(pc, 32725, 32727, (short) 453, 5, true); // 라스타바드 던전 1층집회장으로 텔레포트, 위치 임의
					int a = entertime % 60;
					if (a == 0) {
						int b = (180 - entertime) / 60;
						pc.sendPackets(new S_SystemMessage("라스타바드 사용 시간이 " + b + "시간 남았습니다."));// 시간
					} else if ((180 - entertime) < 60) {
						int c = 180 - entertime;
						pc.sendPackets(new S_SystemMessage("라스타바드 사용 시간이 " + c + "분 남았습니다."));// 분
					}
				}
			} else if (s.equalsIgnoreCase("4")) { // 라스타바드로 보내주세요
				RealTime time = RealTimeClock.getInstance().getRealTime();
				int entertime = pc.getLdungeonTime() % 1000;
				int enterday = pc.getLdungeonTime() / 1000;
				int dayofyear = time.get(Calendar.DAY_OF_YEAR);
				if (entertime > 180 && enterday == dayofyear) {
					pc.sendPackets(new S_NpcChatPacket(npc,	"라스타바드 사용 시간이 남아 있지 않습니다.", 0));
					htmlid = "";
					return;
				} else {
					if (enterday < dayofyear)
						pc.setLdungeonTime(dayofyear * 1000);
	
					L1Teleport.teleport(pc, 32786, 32814, (short) 492, 5, true); // 라스타바드 던전 1층집회장으로 텔레포트, 위치 임의
					int a = entertime % 60;
					if (a == 0) {
						int b = (180 - entertime) / 60;
						pc.sendPackets(new S_SystemMessage("라스타바드 사용 시간이 " + b + "시간 남았습니다."));// 시간
					} else if ((180 - entertime) < 60) {
						int c = 180 - entertime;
						pc.sendPackets(new S_SystemMessage("라스타바드 사용 시간이 " + c + "분 남았습니다."));// 분
					}
				}
				
				
			} else if (s.equalsIgnoreCase("B")) {
				if (pc.getInventory().checkItem(41007, 1)) {
					htmlid = "eris10";
				} else {
					L1ItemInstance item = pc.getInventory().storeItem(41007, 1);
					String npcName = npc.getNpcTemplate().get_name();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
					htmlid = "eris6";
				}
			} else if (s.equalsIgnoreCase("C")) {
				if (pc.getInventory().checkItem(41009, 1)) {
					htmlid = "eris10";
				} else {
					L1ItemInstance item = pc.getInventory().storeItem(41009, 1);
					String npcName = npc.getNpcTemplate().get_name();
					String itemName = item.getItem().getName();
					pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
					htmlid = "eris8";
				}
			} else if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(41007, 1)) {
					if (pc.getInventory().checkItem(40969, 20)) {
						htmlid = "eris18";
						materials = new int[] { 40969, 41007 };
						counts = new int[] { 20, 1 };
						createitem = new int[] { 41008 };
						createcount = new int[] { 1 };
					} else {
						htmlid = "eris5";
					}
				} else {
					htmlid = "eris2";
				}
			} else if (s.equalsIgnoreCase("E")) {
				if (pc.getInventory().checkItem(41010, 1)) {
					htmlid = "eris19";
				} else {
					htmlid = "eris7";
				}
			} else if (s.equalsIgnoreCase("D")) {
				if (pc.getInventory().checkItem(41010, 1)) {
					htmlid = "eris19";
				} else {
					if (pc.getInventory().checkItem(41009, 1)) {
						if (pc.getInventory().checkItem(40959, 1)) {
							htmlid = "eris17";
							materials = new int[] { 40959, 41009 };
							counts = new int[] { 1, 1 };
							createitem = new int[] { 41010 };
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40960, 1)) {
							htmlid = "eris16";
							materials = new int[] { 40960, 41009 };
							counts = new int[] { 1, 1 };
							createitem = new int[] { 41010 };
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40961, 1)) {
							htmlid = "eris15";
							materials = new int[] { 40961, 41009 };
							counts = new int[] { 1, 1 };
							createitem = new int[] { 41010 };
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40962, 1)) {
							htmlid = "eris14";
							materials = new int[] { 40962, 41009 };
							counts = new int[] { 1, 1 };
							createitem = new int[] { 41010 };
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40635, 10)) {
							htmlid = "eris12";
							materials = new int[] { 40635, 41009 };
							counts = new int[] { 10, 1 };
							createitem = new int[] { 41010 };
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40638, 10)) {
							htmlid = "eris11";
							materials = new int[] { 40638, 41009 };
							counts = new int[] { 10, 1 };
							createitem = new int[] { 41010 };
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40642, 10)) {
							htmlid = "eris13";
							materials = new int[] { 40642, 41009 };
							counts = new int[] { 10, 1 };
							createitem = new int[] { 41010 };
							createcount = new int[] { 1 };
						} else if (pc.getInventory().checkItem(40667, 10)) {
							htmlid = "eris13";
							materials = new int[] { 40667, 41009 };
							counts = new int[] { 10, 1 };
							createitem = new int[] { 41010 };
							createcount = new int[] { 1 };
						} else {
							htmlid = "eris8";
						}
					} else {
						htmlid = "eris7";
					}
				}
			}
			
			
			//윈말리뉴얼 카너스
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 9425) {
			if (s.equalsIgnoreCase("a")) {
				htmlid = "";
				L1Teleport.teleport(pc, 32850, 32854, (short)537, 5, true);
				return;
			} else if (s.equalsIgnoreCase("b")) {
				htmlid = "";
				L1Teleport.teleport(pc, 32813, 32870, (short)537, 5, true);
				return;
			}

		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 9422) {
			if (s.equalsIgnoreCase("a")) {
				htmlid = "";
				L1Teleport.teleport(pc, 32617, 33213, (short)4, 5, true);
				return;
			}

		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 9423) {
			if (s.equalsIgnoreCase("a")) {
				htmlid = "";
				L1Teleport.teleport(pc, 32621, 33201, (short)4, 5, true);
				return;
			}
			
			
			
			
			
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 9421) {/// 비자야
			pc.sendPackets(new S_SystemMessage("\\fY+8무기와 아데나(2천) 필요합니다."));
			pc.sendPackets(new S_SystemMessage("\\fY+9무기와 아데나(3천) 필요합니다."));
		      if (s.equals("A")){ // +7 공명의 키링크   
		      if (pc.getInventory().MakeCheckEnchant(410003, 8)//아이템체크
		        && pc.getInventory().checkItem(40308, 20000000)) {
		       pc.getInventory().MakeDeleteEnchant(410003, 8);
		       pc.getInventory().consumeItem(40308, 20000000);
		       createNewItem2(pc, 423, 1, 7);
		       htmlid = "vjaya05";
		      } else {
		       htmlid = "vjaya04";
		      }
		     }
		      if (s.equals("B")){ // +8 공명의 키링크
		       if (pc.getInventory().MakeCheckEnchant(410003, 9)
		         && pc.getInventory().checkItem(40308, 30000000)) {
		        pc.getInventory().MakeDeleteEnchant(410003, 9);
		        pc.getInventory().consumeItem(40308, 30000000);
		        createNewItem2(pc, 423, 1, 8);
		        htmlid = "vjaya05";
		       } else {
		        htmlid = "vjaya04";
		       }
		      }
		      if (s.equals("C")){ // +7 공명의 키링크
		      if (pc.getInventory().MakeCheckEnchant(410004, 8)
		        && pc.getInventory().checkItem(40308, 20000000)) {
		       pc.getInventory().MakeDeleteEnchant(410004, 8);
		       pc.getInventory().consumeItem(40308, 20000000);
		       createNewItem2(pc, 423, 1, 7);
		       htmlid = "vjaya05";
		      } else {
		       htmlid = "vjaya04";
		      }
		     }
		      if (s.equals("D")){ // +8 공명의 키링크
		       if (pc.getInventory().MakeCheckEnchant(410004, 9)
		         && pc.getInventory().checkItem(40308, 30000000)) {
		        pc.getInventory().MakeDeleteEnchant(410004, 9);
		        pc.getInventory().consumeItem(40308, 30000000);
		        createNewItem2(pc, 423, 1, 8);
		        htmlid = "vjaya05";
		       } else {
		        htmlid = "vjaya04";
		       }
		      }


		
		
	
	 } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 42120113) {//용던 리뉴얼
	        if (s.equalsIgnoreCase("1") && pc.getLevel() > 45 && pc.getLevel() < 90) { //66이하
				RealTime time = RealTimeClock.getInstance().getRealTime();
				int entertime = pc.getDdungeonTime() % 1000;
				int enterday = pc.getDdungeonTime() / 1000;
				int dayofyear = time.get(Calendar.DAY_OF_YEAR);
				if(entertime > 120 && enterday == dayofyear){
					pc.sendPackets(new S_ServerMessage(1522, "2"));// 3시간 모두 사용했다.
					htmlid ="";
					return;
				} else {
					if(enterday < dayofyear)pc.setDdungeonTime(dayofyear * 1000);
					L1Teleport.teleport(pc, 32740, 32777, (short) 30, 6, true);
					 pc.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, (120 - entertime)*60));
						//pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "표기상은 컴뱃존으로나오지만 실제로 쌈하거나(보라)죽거나(템드랍,경치드랍)노말존으로 적용됨"));
						//  pc.sendPackets(new S_SystemMessage("표기상은 컴뱃존으로나오지만 실제로 쌈하거나(보라)죽거나(템드랍,경치드랍)노말존으로 적용됨"));
					int a = entertime % 60;
					if(a == 0){
						int b = (120 - entertime) / 60;
						pc.sendPackets(new S_ServerMessage(1526, ""+b+""));// b 시간 남았다.
					} else if ((120 - entertime) < 60){
						int c = 120 - entertime;
						pc.sendPackets(new S_ServerMessage(1527, ""+c+""));// 분 남았다.
					}
				}
			} else {
				htmlid="dvdgate2";
			}
	

	// 신녀 유리스 (속죄)
	} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4213001) {
	if (s.equalsIgnoreCase("0")) { // 1번
		Yuris(pc, objid, 1, 3000);
	} else if (s.equalsIgnoreCase("1")) { // 3번
		Yuris(pc, objid, 3, 9000);
	} else if (s.equalsIgnoreCase("2")) { // 5번
		Yuris(pc, objid, 5, 15000);
	} else if (s.equalsIgnoreCase("3")) { // 10번
		Yuris(pc, objid, 10, 30000);
	}
	}
	
	}
		
	
	
	
		// 신녀 유리스
			private void Yuris(L1PcInstance pc, int objid, int count, int lawful) {
				if (pc.getInventory().checkItem(L1ItemId.REDEMPTION_BIBLE, count)) {
					pc.getInventory().consumeItem(L1ItemId.REDEMPTION_BIBLE, count);
					pc.addLawful(lawful);
					pc.sendPackets(new S_Lawful(pc.getId(), pc.getLawful()));
					pc.sendPackets(new S_NPCTalkReturn(objid, "yuris2"));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(objid, "yuris3"));
				}
			}
			
			private void selectComa(L1PcInstance pc, int objid) {
				String[] htmldata = new String[] {
						String.valueOf(pc.getDeathMatchPiece()),
						String.valueOf(pc.getGhostHousePiece()),
						String.valueOf(pc.getPetRacePiece()),
						String.valueOf(pc.getPetMatchPiece()),
						String.valueOf(pc.getUltimateBattlePiece()) };
				pc.sendPackets(new S_NPCTalkReturn(objid, "coma5", htmldata));
			}
			
			private void comaCheck(L1PcInstance pc, int type, int objid) {
				FastTable<Integer> list = new FastTable<Integer>();
				if (pc.getInventory().checkItem(435010, 1)) {
					list.add(435010);
				}
				if (pc.getInventory().checkItem(435009, 1)) {
					list.add(435009);
				}
				if (pc.getInventory().checkItem(435011, 1)) {
					list.add(435011);
				}
				if (pc.getInventory().checkItem(435012, 1)) {
					list.add(435012);
				}
				if (pc.getInventory().checkItem(435013, 1)) {
					list.add(435013);
				}
				if (list.size() >= type) {
					for (int i = 0; i < type; i++) {
						pc.getInventory().consumeItem(list.get(i), 1);
					}
					if (pc.getSkillEffectTimerSet().hasSkillEffect(
							L1SkillId.STATUS_COMA_3)
							|| pc.getSkillEffectTimerSet().hasSkillEffect(
									L1SkillId.STATUS_COMA_5)) {
						pc.getSkillEffectTimerSet().removeSkillEffect(
								L1SkillId.STATUS_COMA_3);
						pc.getSkillEffectTimerSet().removeSkillEffect(
								L1SkillId.STATUS_COMA_5);
					}
					if (type == 3) {
						new L1SkillUse().handleCommands(pc, L1SkillId.STATUS_COMA_3, pc
								.getId(), pc.getX(), pc.getY(), null, 0,
								L1SkillUse.TYPE_SPELLSC);
					} else if (type == 5) {
						new L1SkillUse().handleCommands(pc, L1SkillId.STATUS_COMA_5, pc
								.getId(), pc.getX(), pc.getY(), null, 0,
								L1SkillUse.TYPE_SPELLSC);
					}
					pc.sendPackets(new S_NPCTalkReturn(objid, ""));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(objid, "coma3"));
				}
				list.clear();
			}
				
				private void resetPiece(L1PcInstance pc) {
					pc.setDeathMatchPiece(0);
					pc.setGhostHousePiece(0);
					pc.setPetRacePiece(0);
					pc.setPetMatchPiece(0);
					pc.setUltimateBattlePiece(0);
				}
				
				private void giveComaBuff(L1PcInstance pc, int objid) {
					int amount = pc.getUltimateBattlePiece() + pc.getDeathMatchPiece()
							+ pc.getGhostHousePiece() + pc.getPetRacePiece()
							+ pc.getPetMatchPiece();
					if (amount < 3 || amount == 4) {
						pc.sendPackets(new S_NPCTalkReturn(objid, "coma3_3"));
					} else if (amount == 3) {
						if (isComaBuff(pc)) {
							consumePiece(pc);
							new L1SkillUse().handleCommands(pc, L1SkillId.STATUS_COMA_3, pc
									.getId(), pc.getX(), pc.getY(), null, 0,
									L1SkillUse.TYPE_GMBUFF);
							pc.sendPackets(new S_NPCTalkReturn(objid, ""));
						} else {
							pc.sendPackets(new S_NPCTalkReturn(objid, "coma3_2"));
						}
					} else if (amount == 5) {
						if (isComaBuff(pc)) {
							consumePiece(pc);
							new L1SkillUse().handleCommands(pc, L1SkillId.STATUS_COMA_5, pc
									.getId(), pc.getX(), pc.getY(), null, 0,
									L1SkillUse.TYPE_GMBUFF);
							pc.sendPackets(new S_NPCTalkReturn(objid, ""));
						} else {
							pc.sendPackets(new S_NPCTalkReturn(objid, "coma3_2"));
						}
					} else if (amount > 5) {
						pc.sendPackets(new S_NPCTalkReturn(objid, "coma3_1"));
					}
					resetPiece(pc);
				}
				 private boolean createNewItem2(L1PcInstance pc, int item_id, int count,
						   int EnchantLevel) {
						  L1ItemInstance item = ItemTable.getInstance().createItem(item_id);

						  item.setCount(count);
						  item.setEnchantLevel(EnchantLevel);
						  item.setIdentified(true);
						  if (item != null) {
						   if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
						    pc.getInventory().storeItem(item);
						   } else { // 가질 수 없는 경우는 지면에 떨어뜨리는 처리의 캔슬은 하지 않는다(부정 방지)
						    L1World.getInstance().getInventory(pc.getX(), pc.getY(),
						      pc.getMapId()).storeItem(item);
						   }
						   pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0를
						                   // 손에
						                   // 넣었습니다.
						   return true;
						  } else {
						   return false;
						  }
						 }



				
				
				private boolean isComaBuff(L1PcInstance pc) {
					if (pc.getInventory().checkItem(435010, pc.getUltimateBattlePiece())
							&& pc.getInventory().checkItem(435009, pc.getDeathMatchPiece())
							&& pc.getInventory().checkItem(435011, pc.getGhostHousePiece())
							&& pc.getInventory().checkItem(435012, pc.getPetRacePiece())
							&& pc.getInventory().checkItem(435013, pc.getPetMatchPiece())) {
						return true;
					}
					return false;
				}
				
				private void consumePiece(L1PcInstance pc) {
					pc.getInventory().consumeItem(435010, pc.getUltimateBattlePiece());// 무대
					pc.getInventory().consumeItem(435009, pc.getDeathMatchPiece()); // 데스매치
					pc.getInventory().consumeItem(435011, pc.getGhostHousePiece());// 유령집
					pc.getInventory().consumeItem(435012, pc.getPetRacePiece());// 펫레이스
					pc.getInventory().consumeItem(435013, pc.getPetMatchPiece());// 펫매치
				}

				
		
		@Override
		public String getType() {
			return C_NPC_ACTION1;
		}

	}
