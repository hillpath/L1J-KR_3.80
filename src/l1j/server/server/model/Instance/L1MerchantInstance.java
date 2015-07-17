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

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import l1j.server.server.BattleZone;
import l1j.server.server.MiniWarGame;
import l1j.server.server.TimeController.HellDevilController;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1BugBearRace;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.gametime.GameTimeClock;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import server.controller.Npc.NpcRestController;

public class L1MerchantInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;
	
	private int _state = 1; //아논망치
	// private GameServerSetting _GameServerSetting;

	/**
	 * @param template
	 */
	public L1MerchantInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance pc) {
		L1Attack attack = new L1Attack(pc, this);
		attack.calcHit();
		attack.action();
	}
	@Override
	 public void onPerceive(L1PcInstance perceivedFrom) { //아논망치
	  if (getGfxId().getGfxId() == 986 || getGfxId().getGfxId() == 727) {
	   Timer timer = new Timer();
	   perceivedFrom.getNearObjects().addKnownObject(this);
	   perceivedFrom.sendPackets(new S_NPCPack(this));
	   if (_state == 1) {
	    timer.schedule(new NPCAction(), 1000, 3000); // 이 부분 3000은 아논의 망치질 반복 주기 1000=1초, 현재3초
	    _state = 0;
	   }
	   
	  } else {
	   perceivedFrom.getNearObjects().addKnownObject(this);
	   perceivedFrom.sendPackets(new S_NPCPack(this));
	  }
	 }
	 public void setAction() {
	 if (getGfxId().getGfxId() == 986 || getGfxId().getGfxId() == 727) {
	   Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), 30));
	  }
	 } //아논망치
	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		int npcid = getNpcTemplate().get_npcId();
		L1Quest quest = player.getQuest();
		String htmlid = null;
		String[] htmldata = null;

		int pcX = player.getX();
		int pcY = player.getY();
		int npcX = getX();
		int npcY = getY();
		if (npcid == 4212015) {// 드래곤 포탈이면이순?
			L1Teleport.teleport(player, 1234, 1234, (short) 1002, 7, true);
		}
		if (getNpcTemplate().getChangeHead()) {
			int heading = 0;
			if (pcX == npcX && pcY < npcY)
				heading = 0;
			else if (pcX > npcX && pcY < npcY)
				heading = 1;
			else if (pcX > npcX && pcY == npcY)
				heading = 2;
			else if (pcX > npcX && pcY > npcY)
				heading = 3;
			else if (pcX == npcX && pcY > npcY)
				heading = 4;
			else if (pcX < npcX && pcY > npcY)
				heading = 5;
			else if (pcX < npcX && pcY == npcY)
				heading = 6;
			else if (pcX < npcX && pcY < npcY)
				heading = 7;

			getMoveState().setHeading(heading);
			Broadcaster.broadcastPacket(this, new S_ChangeHeading(this));

			synchronized (this) {
				if (isRest()) {
					RestTime = System.currentTimeMillis() + REST_MILLISEC;
				} else {
					setRest(true);
					RestTime = System.currentTimeMillis() + REST_MILLISEC;
					NpcRestController.getInstance().addNpc(this);
				}
			}
		}

		
		
	 /*if (npcid == 190009) { // 초보자도우미

		 int quest_step = quest.get_step(L1Quest.QUEST_TUTOR);//
		    int playerLv = player.getLevel();//
		    int raMinLv = 1;//최소레벨
		    int raMaxLv = 13;//최대레벨
		    if (playerLv <= raMaxLv && quest_step == 0) {
		     player.addExp(750);// LV5
		     quest.set_step(L1Quest.QUEST_TUTOR, 1); 
	          htmlid = "tutor";
		    } else if (playerLv <= raMaxLv && quest_step == 1) {
		     htmlid = "tutor";
		    } else if (!(playerLv >= raMinLv && playerLv <= raMaxLv)) {
		     htmlid = "tutorend";
		    } else if (playerLv <= raMaxLv && quest_step > 1) {
		     if (player.isDarkelf()) {
		      htmlid = "tutor2d";
		     } else if (player.isDragonknight()) {
		      htmlid = "tutor2dk"; 
		     } else if (player.isElf()) {
		      htmlid = "tutor2e";
		     } else if (player.isIllusionist()) {
		      htmlid = "tutor2i";
		     } else if (player.isKnight()) {
		      htmlid = "tutor2k";
		     } else if (player.isWizard()) {
		      htmlid = "tutor2m";
		     } else if (player.isCrown()) {
	      htmlid = "tutor2p";
		  }
		    }
	 }
*/
		if (npcid == 95022) { //지옥사냥터
			HellTime(player);
		}
		
		/**NEW 미니공성 시스템**/
		if(npcid == 5325320){
			if(MiniWarGame.getInstance()._BattleEntry == false){
				player.sendPackets(new S_SystemMessage("\\fT미니공성전 입장은 현재 불가능합니다."));
				return;
			}
			if(player.getLevel() < 50){
				player.sendPackets(new S_SystemMessage("\\fT미니공성전 입장은 레벨 50부터 가능합니다."));
				return;
			}
			if(MiniWarGame.getInstance().getBattleStart() == true){
				if(!MiniWarGame.getInstance().isMember(player)){
					MiniWarGame.getInstance().AddMember(player);
					player.set_MiniDuelLine(0);
				}else{
					 player.sendPackets(new S_SystemMessage("\\fT이미 미니공성전에 신청되셧습니다."));
				}
              } else {
            	  player.sendPackets(new S_SystemMessage("\\fT미니공성전이 아직 시작되지 않았습니다."));
      		}
		}
		/**배틀존**/
		if (npcid == 532532) {//배틀존 입장 엔피씨
			if (!BattleZone.getInstance().getDuelOpen()) {
				player.sendPackets(new S_SystemMessage("배틀존이 아직 열려있지 않습니다."));
				return;
			}
			if(!player.isGm() && player.getLevel() < 56){
				player.sendPackets(new S_SystemMessage("배틀존입장은 55부터 가능합니다."));
					return;
				}
			if(player.get_DuelLine() == 100){
				player.sendPackets(new S_SystemMessage("\\fT배틀존 신청후 리스하셔서 이번차 배틀은 입장 못하십니다."));
            	return;
			}
			long curtime = System.currentTimeMillis() / 1000;
			if (player.gettelldelayTime() + 10 > curtime ) {
				 player.sendPackets(new S_SystemMessage("\\fW5초후 다시 입장 클릭을 할 수 있습니다."));
				    return;
				   }
			if (BattleZone.getInstance().getDuelOpen()) {
            	  DuelZone(player);
            	  player.settelldelayTime(curtime);
              }
		}
		/**배틀존**/
		
		
		if (talking != null) {
			switch (npcid) {
			case 50112: // 세리안
				if (player.isCrown() || player.isWizard()
						|| player.isDragonknight()) {
					int talk_step = quest.get_step(L1Quest.QUEST_FIRSTQUEST);
					if (talk_step == 1) {
						if (player.getLevel() >= 5) {
							htmlid = "orenb4";
						} else {
							htmlid = "orenb14";
						}
					} else if (talk_step == 255) {
						htmlid = "orenb11";
					}
				} else {
					htmlid = "orenb12";
				}
				break;
			case 50113: // 레크만
				if (player.isKnight() || player.isElf() || player.isDarkelf()
						|| player.isIllusionist()) {
					int talk_step = quest.get_step(L1Quest.QUEST_FIRSTQUEST);
					if (talk_step == 1) {
						if (player.getLevel() >= 5) {
							htmlid = "orena4";
						} else {
							htmlid = "orena14";
						}
					} else if (talk_step == 255) {
						htmlid = "orena11";
					}
				} else {
					htmlid = "orena12";
				}
				break;
			case 70005:// 파고
				htmlid = "pago";
				break;
			case 70009:
				if (player.isCrown()) {
					htmlid = "gerengp1";
				} else if (player.isKnight()) {
					htmlid = "gerengk1";
				} else if (player.isElf()) {
					htmlid = "gerenge1";
				} else if (player.isWizard()) {
					if (player.getLevel() >= 30) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
							int lv30_step = quest
									.get_step(L1Quest.QUEST_LEVEL30);
							if (lv30_step >= 4) {
								htmlid = "gerengw3";
							} else if (lv30_step >= 3) {
								htmlid = "gerengT4";
							} else if (lv30_step >= 2) {
								htmlid = "gerengT3";
							} else if (lv30_step >= 1) {
								htmlid = "gerengT2";
							} else {
								htmlid = "gerengT1";
							}
						} else {
							htmlid = "gerengw3";
						}
					} else {
						htmlid = "gerengw3";
					}
				} else if (player.isDarkelf()) {
					htmlid = "gerengde1";
				}
				break;
			case 70011:
				int time = GameTimeClock.getInstance().getGameTime()
						.getSeconds() % 86400;
				if (time < 60 * 60 * 6 || time > 60 * 60 * 20) { // 20:00?6:00
					htmlid = "shipEvI6";
				}
				break;
				/** 토벌대원 -켄파치 */
			   case 401060: //토벌대원
				    if (player.getLevel() < 15) {
				            htmlid ="highdaily0";				    
				    } else if (player.getLevel() >= 15 && player.getInventory().checkItem(5439, 100)){ //토벌의 증표
				            htmlid ="highdaily6";
				    } else if (player.getLevel() >= 15 && (player.getInventory().checkItem(5437, 1))) {//구슬있을경우
			                htmlid ="highdaily2";
				    } else if (player.getLevel() >= 15 && (!player.getInventory().checkItem(5438, 1))) { //토벌주머니
				            htmlid ="highdaily1";
				    } else if (!player.getInventory().checkItem(5437) && !player.getInventory().checkItem(5438, 1)){
				            htmlid ="highdaily10";// 빛나는 구슬 과 토벌대원의 주머니가 없을경우
				    } else {
				            htmlid ="highdaily1";
				    } break;
					/** 서브퀘스트  */
			    case 401240: //드래곤뼈 수집꾼
				    if (player.getLevel() < 45) {
				            htmlid ="highdailyb0";
				    } else if (player.getLevel() >= 15 && player.getInventory().checkItem(5451, 100)){//뼈조각
				            htmlid ="highdailyb6";
				    } else if (player.getLevel() >= 15 && (player.getInventory().checkItem(5456, 1))) {//영롱한 구슬
			                htmlid ="highdailyb2";
				    } else if (player.getLevel() >= 15 && (!player.getInventory().checkItem(5455, 1))) { //뼈주머니
				            htmlid ="highdailyb1";
				    } else if (!player.getInventory().checkItem(5456) && !player.getInventory().checkItem(5455, 1)){
				            htmlid ="highdailyb10"; //둘다엄으면
				    } else {
				            htmlid ="highdailyb1";
				    }break;	
					/** 서브퀘스트 종료  */
				case 40090: //기초훈련교관   
				    int first = player.getLevel();    
				    switch (player.getsub()){    
		       case 0:
				     if (first < 15){
				     htmlid ="hpass8";
				     } else if (15 <= first ++){
				     htmlid ="hpass1";
				     } else {
				     htmlid ="hpass8";
				     }
				     break;
		       case 1:
				     if (20 <= first ++){
				     htmlid ="hpass2";
				     } else {
				     htmlid ="hpass8";
				     }
				     break;
		       case 2:
				     if (25 <= first++){
				     htmlid ="hpass3";
				     } else {
				     htmlid ="hpass8";
				     }
				     break;
		       case 3:
				     if (30 <= first++){
				     htmlid ="hpass4";
				     } else {
				     htmlid ="hpass8";
				     }
				     break;
			   case 4:
				     if (35 <= first++){
				     htmlid ="hpass5";
				     } else {
				     htmlid ="hpass8";
				     }
				     break;
		       case 5:
				     if (40 <= first++){
				     htmlid ="hpass7";
				     } else {
				     htmlid ="hpass8";
				     }
				     break;
			   case 6:
				     if (45 <= first++){
				     htmlid ="hpass6";
				     } else {
				     htmlid ="hpass8";
				     }
				     break;
			   case 7:
				     if (50 <= first++) {
				     htmlid ="hpass7";
				     } else {
				     htmlid ="hpass8";
				     }
				     break;
				     }
				     break;
				     
				case 401230://최종 심사관
				    if (player.getsub() == 7 && player.getLevel() >= 52) {
				     if (player.getInventory().checkItem(5446, 1)) { //수련증표 체크
				      while (player.getInventory().consumeItem(5446, 1)) ; // 수련증표 삭제
				      player.getInventory().storeItem(40308, 500000); //엘모어 무기 생성 이부분은 클래스별로 지급인데
				       //저같은 경우는 엘모어 장비 상자로 추가함 상자는 클래스 별로 생성되도록 아이템 유즈에서 처리함
				      player.setsub(player.getsub() + 1); //중복 방지
				      htmlid = "highpass40";
				     } else {
				      htmlid = "highpass41";
				     }
				    } else {
				     htmlid = "highpass41";
				    }
				    break;
			
			case 70041:
			case 70042:
			case 70035:// 버경 상인
				L1BugBearRace bugrace = L1BugBearRace.getInstance();
				if (bugrace.getBugRaceStatus() == L1BugBearRace.STATUS_NONE) {
					htmlid = "maeno5";
				} else if (bugrace.getBugRaceStatus() == L1BugBearRace.STATUS_PLAYING) {
					htmlid = "maeno3";
				}
				break;
			case 70087:
				if (player.isDarkelf()) {
					htmlid = "sedia";
				}
				break;
			case 70099:
				if (!quest.isEnd(L1Quest.QUEST_OILSKINMANT)) {
					if (player.getLevel() > 13) {
						htmlid = "kuper1";
					}
				}
				break;
			case 70522:
				if (player.isCrown()) {
					if (player.getLevel() >= 15) {
						int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
						if (lv15_step == 2 || lv15_step == L1Quest.QUEST_END) {
							htmlid = "gunterp11";
						} else {
							htmlid = "gunterp9";
						}
					} else {
						htmlid = "gunterp12";
					}
				} else if (player.isKnight()) {
					int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
					if (lv30_step == 0) {
						htmlid = "gunterk9";
					} else if (lv30_step == 1) {
						htmlid = "gunterkE1";
					} else if (lv30_step == 2) {
						htmlid = "gunterkE2";
					} else if (lv30_step >= 3) {
						htmlid = "gunterkE3";
					}
				} else if (player.isElf()) {
					htmlid = "guntere1";
				} else if (player.isWizard()) {
					htmlid = "gunterw1";
				} else if (player.isDarkelf()) {
					htmlid = "gunterde1";
				}
				break;
			case 70528:
				htmlid = talkToTownmaster(player,
						L1TownLocation.TOWNID_TALKING_ISLAND);
				break;
			case 70531:
				if (player.isWizard()) {
					if (player.getLevel() >= 15) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
							htmlid = "jem6";
						} else {
							htmlid = "jem1";
						}
					}
				}
				break;
			case 70534:
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_TALKING_ISLAND);
				break;
			case 70545:
				if (player.isCrown()) {
					int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
					if (lv45_step >= 1 && lv45_step != L1Quest.QUEST_END) {
						if (player.getInventory().checkItem(40586)) {
							htmlid = "richard4";
						} else {
							htmlid = "richard1";
						}
					}
				}
				break;
			case 70546:
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_KENT);
				break;
			case 70553:
				boolean hascastle = checkHasCastle(player,
						L1CastleLocation.KENT_CASTLE_ID);
				if (hascastle) {
					if (checkClanLeader(player)) {
						htmlid = "ishmael1";
					} else {
						htmlid = "ishmael6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "ishmael7";
				}
				break;
			case 70554:
				if (player.isCrown()) {
					if (player.getLevel() >= 15) {
						int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
						if (lv15_step == 1) {
							htmlid = "zero5";
						} else if (lv15_step == L1Quest.QUEST_END) {
							htmlid = "zero1";
						} else {
							htmlid = "zero1";
						}
					} else {
						htmlid = "zero6";
					}
				}
				break;
			case 70555:
				if (player.getGfxId().getTempCharGfx() == 2374) {
					if (player.isKnight()) {
						if (quest.get_step(L1Quest.QUEST_LEVEL30) == 6) {
							htmlid = "jim2";
						} else {
							htmlid = "jim4";
						}
					} else {
						htmlid = "jim4";
					}
				}
				break;
			case 70633:
				boolean hascastle04 = checkHasCastle(player,
						L1CastleLocation.GIRAN_CASTLE_ID);
				if (hascastle04) {
					htmlid = "colbert1";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "colbert4";
				}
				break;
			case 70653:
				if (player.isCrown()) {
					if (player.getLevel() >= 45) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL30)) {
							int lv45_step = quest
									.get_step(L1Quest.QUEST_LEVEL45);
							if (lv45_step == L1Quest.QUEST_END) {
								htmlid = "masha4";
							} else if (lv45_step >= 1) {
								htmlid = "masha3";
							} else {
								htmlid = "masha1";
							}
						}
					}
				} else if (player.isKnight()) {
					if (player.getLevel() >= 45) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL30)) {
							int lv45_step = quest
									.get_step(L1Quest.QUEST_LEVEL45);
							if (lv45_step == L1Quest.QUEST_END) {
								htmlid = "mashak3";
							} else if (lv45_step == 0) {
								htmlid = "mashak1";
							} else if (lv45_step >= 1) {
								htmlid = "mashak2";
							}
						}
					}
				} else if (player.isElf()) {
					if (player.getLevel() >= 45) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL30)) {
							int lv45_step = quest
									.get_step(L1Quest.QUEST_LEVEL45);
							if (lv45_step == L1Quest.QUEST_END) {
								htmlid = "mashae3";
							} else if (lv45_step >= 1) {
								htmlid = "mashae2";
							} else {
								htmlid = "mashae1";
							}
						}
					}
				}
				break;
			case 70654:
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_WERLDAN);
				break;
			case 70556:
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_KENT);
				break;
			case 70567:
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_GLUDIO);
				break;
			case 70572:
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_GLUDIO);
				break;
			case 70594:
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_GIRAN);
				break;
			case 70623:
				boolean hascastle3 = checkHasCastle(player,
						L1CastleLocation.GIRAN_CASTLE_ID);
				if (hascastle3) {
					if (checkClanLeader(player)) {
						htmlid = "orville1";
					} else {
						htmlid = "orville6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "orville7";
				}
				break;
			case 70631:
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_GIRAN);
				break;
			case 70663:
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_WERLDAN);
				break;
			case 70665:
				boolean hascastle5 = checkHasCastle(player,
						L1CastleLocation.DOWA_CASTLE_ID);
				if (hascastle5) {
					if (checkClanLeader(player)) {
						htmlid = "potempin1";
					} else {
						htmlid = "potempin6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "potempin7";
				}
				break;
			case 70711:
				if (player.isKnight()) {
					int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
					if (lv45_step == 2) {
						if (player.getInventory().checkItem(20026)) {
							htmlid = "giantk1";
						}
					} else if (lv45_step == 3) {
						htmlid = "giantk2";
					} else if (lv45_step >= 4) {
						htmlid = "giantk3";
					}
				}
				break;
			case 70715:
				if (player.isKnight()) {
					int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
					if (lv45_step == 1) {
						htmlid = "jimuk1";
					} else if (lv45_step >= 2) {
						htmlid = "jimuk2";
					}
				}
				break;
			case 70721:
				boolean hascastle6 = checkHasCastle(player,
						L1CastleLocation.ADEN_CASTLE_ID);
				if (hascastle6) {
					if (checkClanLeader(player)) {
						htmlid = "timon1";
					} else {
						htmlid = "timon6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "timon7";
				}
				break;
			case 70724:
				if (player.isElf()) {
					int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
					if (lv45_step >= 4) {
						htmlid = "heit5";
					} else if (lv45_step >= 3) {
						htmlid = "heit3";
					} else if (lv45_step >= 2) {
						htmlid = "heit2";
					} else if (lv45_step >= 1) {
						htmlid = "heit1";
					}
				}
				break;
			case 70739:
				if (player.getLevel() >= 50) {
					int lv50_step = quest.get_step(L1Quest.QUEST_LEVEL50);
					if (lv50_step == L1Quest.QUEST_END) {
						if (player.isCrown()) {
							htmlid = "dicardingp3";
						} else if (player.isKnight()) {
							htmlid = "dicardingk3";
						} else if (player.isElf()) {
							htmlid = "dicardinge3";
						} else if (player.isWizard()) {
							htmlid = "dicardingw3";
						} else if (player.isDarkelf()) {
							htmlid = "dicarding";
						}
					} else if (lv50_step >= 1) {
						if (player.isCrown()) {
							htmlid = "dicardingp2";
						} else if (player.isKnight()) {
							htmlid = "dicardingk2";
						} else if (player.isElf()) {
							htmlid = "dicardinge2";
						} else if (player.isWizard()) {
							htmlid = "dicardingw2";
						} else if (player.isDarkelf()) {
							htmlid = "dicarding";
						}
					} else if (lv50_step >= 0) {
						if (player.isCrown()) {
							htmlid = "dicardingp1";
						} else if (player.isKnight()) {
							htmlid = "dicardingk1";
						} else if (player.isElf()) {
							htmlid = "dicardinge1";
						} else if (player.isWizard()) {
							htmlid = "dicardingw1";
						} else if (player.isDarkelf()) {
							htmlid = "dicarding";
						}
					} else {
						htmlid = "dicarding";
					}
				} else {
					htmlid = "dicarding";
				}
				break;
			case 70744:
				if (player.isDarkelf()) {
					int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
					if (lv45_step >= 5) {
						htmlid = "roje14";
					} else if (lv45_step >= 4) {
						htmlid = "roje13";
					} else if (lv45_step >= 3) {
						htmlid = "roje12";
					} else if (lv45_step >= 2) {
						htmlid = "roje11";
					} else {
						htmlid = "roje15";
					}
				}
				break;
			case 70748:
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_OREN);
				break;
			case 70761:
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_OREN);
				break;
			case 70762://왕도제작게속가능하게
				if (!player.isDarkelf())
					htmlid = "karif9";
				// int karif_step = quest.get_step(L1Quest.QUEST_KARIF);
				// if(karif_step == L1Quest.QUEST_END){
				// htmlid = "karif9";
				// }
				if (player.getLevel() < 50) {
					htmlid = "karif9";
				}
				break;
			case 70763:
				if (player.isWizard()) {
					int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
					if (lv30_step == L1Quest.QUEST_END) {
						if (player.getLevel() >= 45) {
							int lv45_step = quest
									.get_step(L1Quest.QUEST_LEVEL45);
							if (lv45_step >= 1
									&& lv45_step != L1Quest.QUEST_END) {
								htmlid = "talassmq2";
							} else if (lv45_step <= 0) {
								htmlid = "talassmq1";
							}
						}
					} else if (lv30_step == 4) {
						htmlid = "talassE1";
					} else if (lv30_step == 5) {
						htmlid = "talassE2";
					}
				}
				break;
			case 70774:
				htmlid = talkToTownmaster(player,
						L1TownLocation.TOWNID_WINDAWOOD);
				break;
			case 70775:
				if (player.isKnight()) {
					if (player.getLevel() >= 30) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
							int lv30_step = quest
									.get_step(L1Quest.QUEST_LEVEL30);
							if (lv30_step == 0) {
								htmlid = "mark1";
							} else {
								htmlid = "mark2";
							}
						}
					}
				}
				break;
			case 70776:
				if (player.isCrown()) {
					int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
					if (lv45_step == 1) {
						htmlid = "meg1";
					} else if (lv45_step == 2 && lv45_step <= 3) {
						htmlid = "meg2";
					} else if (lv45_step >= 4) {
						htmlid = "meg3";
					}
				}
				break;
			case 70782:
				if (player.getGfxId().getTempCharGfx() == 1037) {
					if (player.isCrown()) {
						if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
							htmlid = "ant1";
						} else {
							htmlid = "ant3";
						}
					} else {
						htmlid = "ant3";
					}
				}
				break;
			case 70783:
				if (player.isCrown()) {
					if (player.getLevel() >= 30) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
							int lv30_step = quest
									.get_step(L1Quest.QUEST_LEVEL30);
							if (lv30_step == L1Quest.QUEST_END) {
								htmlid = "aria3";
							} else if (lv30_step == 1) {
								htmlid = "aria2";
							} else {
								htmlid = "aria1";
							}
						}
					}
				}
				break;
			case 70784:
				boolean hascastle2 = checkHasCastle(player,
						L1CastleLocation.WW_CASTLE_ID);
				if (hascastle2) {
					if (checkClanLeader(player)) {
						htmlid = "othmond1";
					} else {
						htmlid = "othmond6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "othmond7";
				}
				break;
			case 70788:
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_WINDAWOOD);
				break;
			case 70794:
				if (player.isCrown()) {
					htmlid = "gerardp1";
				} else if (player.isKnight()) {
					int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
					if (lv30_step == L1Quest.QUEST_END) {
						htmlid = "gerardkEcg";
					} else if (lv30_step < 3) {
						htmlid = "gerardk7";
					} else if (lv30_step == 3) {
						htmlid = "gerardkE1";
					} else if (lv30_step == 4) {
						htmlid = "gerardkE2";
					} else if (lv30_step == 5) {
						htmlid = "gerardkE3";
					} else if (lv30_step >= 6) {
						htmlid = "gerardkE4";
					}
				} else if (player.isElf()) {
					htmlid = "gerarde1";
				} else if (player.isWizard()) {
					htmlid = "gerardw1";
				} else if (player.isDarkelf()) {
					htmlid = "gerardde1";
				}
				break;
			case 70796:
				if (!quest.isEnd(L1Quest.QUEST_OILSKINMANT)) {
					if (player.getLevel() > 13) {
						htmlid = "dunham1";
					}
				}
				break;
			case 70798:
				if (player.isKnight()) {
					if (player.getLevel() >= 15) {
						int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
						if (lv15_step >= 1) {
							htmlid = "riky5";
						} else {
							htmlid = "riky1";
						}
					} else {
						htmlid = "riky6";
					}
				}
				break;
			case 70799:
				htmlid = talkToTownmaster(player,
						L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
				break;
			case 70802:
				if (player.isKnight()) {
					if (player.getLevel() >= 15) {
						int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
						if (lv15_step == L1Quest.QUEST_END) {
							htmlid = "aanon7";
						} else if (lv15_step == 1) {
							htmlid = "aanon4";
						}
					}
				}
				break;
			case 70806:
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_SILVER_KNIGHT_TOWN);
				break;
			case 70811:
				if (quest.get_step(L1Quest.QUEST_LYRA) >= 1) {
					htmlid = "lyraEv3";
				} else {
					htmlid = "lyraEv1";
				}
				break;
			case 70815:
				htmlid = talkToTownmaster(player,
						L1TownLocation.TOWNID_ORCISH_FOREST);
				break;
			case 70822:
				boolean hascastle1 = checkHasCastle(player,
						L1CastleLocation.OT_CASTLE_ID);
				if (hascastle1) {
					if (checkClanLeader(player)) {
						htmlid = "seghem1";
					} else {
						htmlid = "seghem6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "seghem7";
				}
				break;
			case 70824:
				if (player.isDarkelf()) {
					if (player.getGfxId().getTempCharGfx() == 3634) {
						int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
						if (lv45_step == 1) {
							htmlid = "assassin1";
						} else if (lv45_step == 2) {
							htmlid = "assassin2";
						} else {
							htmlid = "assassin3";
						}
					} else {
						htmlid = "assassin3";
					}
				}
				break;
			case 70826:
				if (player.isElf()) {
					if (player.getLevel() >= 15) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
							htmlid = "oth5";
						} else {
							htmlid = "oth1";
						}
					} else {
						htmlid = "oth6";
					}
				}
				break;
			case 70830:
				htmlid = talkToTownadviser(player,
						L1TownLocation.TOWNID_ORCISH_FOREST);
				break;
			case 70838: // 네루파
				if (player.isCrown() || player.isKnight() || player.isWizard()
						|| player.isDragonknight() || player.isIllusionist()) {
					htmlid = "nerupam1";
				} else if (player.isDarkelf() && (player.getLawful() <= -1)) {
					htmlid = "nerupaM2";
				} else if (player.isDarkelf()) {
					htmlid = "nerupace1";
				} else if (player.isElf()) {
					htmlid = "nerupae1";
				}
				break;
			case 70841:
				if (player.isElf()) {
					htmlid = "luudielE1";
				} else if (player.isDarkelf()) {
					htmlid = "luudielCE1";
				} else {
					htmlid = "luudiel1";
				}
				break;
			case 70842: // 마르바
				if (player.getLawful() <= -501) {
					htmlid = "marba1";
				} else if (!player.isElf()) {
					htmlid = "marba2";
				} else if (player.getInventory().checkItem(40665)
						&& (player.getInventory().checkItem(40693)
								|| player.getInventory().checkItem(40694)
								|| player.getInventory().checkItem(40695)
								|| player.getInventory().checkItem(40697)
								|| player.getInventory().checkItem(40698) || player
								.getInventory().checkItem(40699))) {
					htmlid = "marba8";
				} else if (player.getInventory().checkItem(40665)) {
					htmlid = "marba17";
				} else if (player.getInventory().checkItem(40664)) {
					htmlid = "marba19";
				} else if (player.getInventory().checkItem(40637)) {
					htmlid = "marba18";
				} else {
					htmlid = "marba3";
				}
				break;
			case 70844:
				if (player.isElf()) {
					if (player.getLevel() >= 30) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
							int lv30_step = quest
									.get_step(L1Quest.QUEST_LEVEL30);
							if (lv30_step == L1Quest.QUEST_END) {
								htmlid = "motherEE3";
							} else if (lv30_step >= 1) {
								htmlid = "motherEE2";
							} else if (lv30_step <= 0) {
								htmlid = "motherEE1";
							}
						} else {
							htmlid = "mothere1";
						}
					} else {
						htmlid = "mothere1";
					}
				}
				break;
			case 70845: // 아라스
				if (player.getLawful() <= -501) {
					htmlid = "aras12";
				} else if (!player.isElf()) {
					htmlid = "aras11";
				} else if (player.getInventory().checkItem(40665)
						&& (player.getInventory().checkItem(40679)
								|| player.getInventory().checkItem(40680)
								|| player.getInventory().checkItem(40681)
								|| player.getInventory().checkItem(40682)
								|| player.getInventory().checkItem(40683) || player
								.getInventory().checkItem(40684))) {
					htmlid = "aras3";
				} else if (player.getInventory().checkItem(40665)) {
					htmlid = "aras8";
				} else if (player.getInventory().checkItem(40679)
						|| player.getInventory().checkItem(40680)
						|| player.getInventory().checkItem(40681)
						|| player.getInventory().checkItem(40682)
						|| player.getInventory().checkItem(40683)
						|| player.getInventory().checkItem(40684)
						|| player.getInventory().checkItem(40693)
						|| player.getInventory().checkItem(40694)
						|| player.getInventory().checkItem(40695)
						|| player.getInventory().checkItem(40697)
						|| player.getInventory().checkItem(40698)
						|| player.getInventory().checkItem(40699)) {
					htmlid = "aras3";
				} else if (player.getInventory().checkItem(40664)) {
					htmlid = "aras6";
				} else if (player.getInventory().checkItem(40637)) {
					htmlid = "aras1";
				} else {
					htmlid = "aras7";
				}
				break;
			case 70860:
				htmlid = talkToTownmaster(player, L1TownLocation.TOWNID_HEINE);
				break;
			case 70876:
				htmlid = talkToTownadviser(player, L1TownLocation.TOWNID_HEINE);
				break;
			case 70880:
				boolean hascastle4 = checkHasCastle(player,
						L1CastleLocation.HEINE_CASTLE_ID);
				if (hascastle4) {
					if (checkClanLeader(player)) {
						htmlid = "fisher1";
					} else {
						htmlid = "fisher6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "fisher7";
				}
				break;
			case 70885:
				if (player.isDarkelf()) {
					if (player.getLevel() >= 15) {
						int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
						if (lv15_step == L1Quest.QUEST_END) {
							htmlid = "kanguard3";
						} else if (lv15_step >= 1) {
							htmlid = "kanguard2";
						} else {
							htmlid = "kanguard1";
						}
					} else {
						htmlid = "kanguard5";
					}
				}
				break;
			case 70892:
				if (player.isDarkelf()) {
					if (player.getLevel() >= 30) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL15)) {
							int lv30_step = quest
									.get_step(L1Quest.QUEST_LEVEL30);
							if (lv30_step == L1Quest.QUEST_END) {
								htmlid = "ronde5";
							} else if (lv30_step >= 2) {
								htmlid = "ronde3";
							} else if (lv30_step >= 1) {
								htmlid = "ronde2";
							} else {
								htmlid = "ronde1";
							}
						} else {
							htmlid = "ronde7";
						}
					} else {
						htmlid = "ronde7";
					}
				}
				break;
			case 70895:
				if (player.isDarkelf()) {
					if (player.getLevel() >= 45) {
						if (quest.isEnd(L1Quest.QUEST_LEVEL30)) {
							int lv45_step = quest
									.get_step(L1Quest.QUEST_LEVEL45);
							if (lv45_step == L1Quest.QUEST_END) {
								if (player.getLevel() < 50) {
									htmlid = "bluedikaq3";
								} else {
									int lv50_step = quest
											.get_step(L1Quest.QUEST_LEVEL50);
									if (lv50_step == L1Quest.QUEST_END) {
										htmlid = "bluedikaq8";
									} else {
										htmlid = "bluedikaq6";
									}
								}
							} else if (lv45_step >= 1) {
								htmlid = "bluedikaq2";
							} else {
								htmlid = "bluedikaq1";
							}
						} else {
							htmlid = "bluedikaq5";
						}
					} else {
						htmlid = "bluedikaq5";
					}
				}
				break;
			case 70904:
				if (player.isDarkelf()) {
					if (quest.get_step(L1Quest.QUEST_LEVEL45) == 1) {
						htmlid = "koup12";
					}
				}
				break;
			case 70906: // 키마
				if (player.getLevel() < 50)
					htmlid = "kima1";
				break;
			case 71013:
				if (player.isDarkelf()) {
					if (player.getLevel() <= 3) {
						htmlid = "karen1";
					} else if (player.getLevel() > 3 && player.getLevel() < 50) {
						htmlid = "karen3";
					} else if (player.getLevel() >= 50) {
						htmlid = "karen4";
					}
				}
				break;
			case 71036:
				if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == L1Quest.QUEST_END) {
					htmlid = "kamyla26";
				} else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 4
						&& player.getInventory().checkItem(40717)) {
					htmlid = "kamyla15";
				} else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 4) {
					htmlid = "kamyla14";
				} else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 3
						&& player.getInventory().checkItem(40630)) {
					htmlid = "kamyla12";
				} else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 3) {
					htmlid = "kamyla11";
				} else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 2
						&& player.getInventory().checkItem(40644)) {
					htmlid = "kamyla9";
				} else if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 1) {
					htmlid = "kamyla8";
				} else if (player.getQuest().get_step(L1Quest.QUEST_CADMUS) == L1Quest.QUEST_END
						&& player.getInventory().checkItem(40621)) {
					htmlid = "kamyla1";
				}
				break;
			case 71038:
				if (player.getInventory().checkItem(41060)) {
					if (player.getInventory().checkItem(41090)
							|| player.getInventory().checkItem(41091)
							|| player.getInventory().checkItem(41092)) {
						htmlid = "orcfnoname7";
					} else {
						htmlid = "orcfnoname8";
					}
				} else {
					htmlid = "orcfnoname1";
				}
				break;
			case 71040:
				if (player.getInventory().checkItem(41060)) {
					if (player.getInventory().checkItem(41065)) {
						if (player.getInventory().checkItem(41086)
								|| player.getInventory().checkItem(41087)
								|| player.getInventory().checkItem(41088)
								|| player.getInventory().checkItem(41089)) {
							htmlid = "orcfnoa6";
						} else {
							htmlid = "orcfnoa5";
						}
					} else {
						htmlid = "orcfnoa2";
					}
				} else {
					htmlid = "orcfnoa1";
				}
				break;
			case 71041:
				if (player.getInventory().checkItem(41060)) {
					if (player.getInventory().checkItem(41064)) {
						if (player.getInventory().checkItem(41081)
								|| player.getInventory().checkItem(41082)
								|| player.getInventory().checkItem(41083)
								|| player.getInventory().checkItem(41084)
								|| player.getInventory().checkItem(41085)) {
							htmlid = "orcfhuwoomo2";
						} else {
							htmlid = "orcfhuwoomo8";
						}
					} else {
						htmlid = "orcfhuwoomo1";
					}
				} else {
					htmlid = "orcfhuwoomo5";
				}
				break;
			case 71042:
				if (player.getInventory().checkItem(41060)) {
					if (player.getInventory().checkItem(41062)) {
						if (player.getInventory().checkItem(41071)
								|| player.getInventory().checkItem(41072)
								|| player.getInventory().checkItem(41073)
								|| player.getInventory().checkItem(41074)
								|| player.getInventory().checkItem(41075)) {
							htmlid = "orcfbakumo2";
						} else {
							htmlid = "orcfbakumo8";
						}
					} else {
						htmlid = "orcfbakumo1";
					}
				} else {
					htmlid = "orcfbakumo5";
				}
				break;
			case 71043:
				if (player.getInventory().checkItem(41060)) {
					if (player.getInventory().checkItem(41063)) {
						if (player.getInventory().checkItem(41076)
								|| player.getInventory().checkItem(41077)
								|| player.getInventory().checkItem(41078)
								|| player.getInventory().checkItem(41079)
								|| player.getInventory().checkItem(41080)) {
							htmlid = "orcfbuka2";
						} else {
							htmlid = "orcfbuka8";
						}
					} else {
						htmlid = "orcfbuka1";
					}
				} else {
					htmlid = "orcfbuka5";
				}
				break;
			case 71044:
				if (player.getInventory().checkItem(41060)) {
					if (player.getInventory().checkItem(41061)) {
						if (player.getInventory().checkItem(41066)
								|| player.getInventory().checkItem(41067)
								|| player.getInventory().checkItem(41068)
								|| player.getInventory().checkItem(41069)
								|| player.getInventory().checkItem(41070)) {
							htmlid = "orcfkame2";
						} else {
							htmlid = "orcfkame8";
						}
					} else {
						htmlid = "orcfkame1";
					}
				} else {
					htmlid = "orcfkame5";
				}
				break;
			case 71055:
				if (player.getQuest().get_step(L1Quest.QUEST_RESTA) == 3) {
					htmlid = "lukein13";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == L1Quest.QUEST_END
						&& player.getQuest().get_step(L1Quest.QUEST_RESTA) == 2
						&& player.getInventory().checkItem(40631)) {
					htmlid = "lukein10";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == L1Quest.QUEST_END) {
					htmlid = "lukein0";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 11) {
					if (player.getInventory().checkItem(40716)) {
						htmlid = "lukein9";
					}
				} else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) >= 1
						&& player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) <= 10) {
					htmlid = "lukein8";
				}
				break;
			case 71056:
				if (player.getQuest().get_step(L1Quest.QUEST_RESTA) == 4) {
					if (player.getInventory().checkItem(40631)) {
						htmlid = "simizz11";
					} else {
						htmlid = "simizz0";
					}
				} else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ) == 2) {
					htmlid = "simizz0";
				} else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ) == L1Quest.QUEST_END) {
					htmlid = "simizz15";
				} else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ) == 1) {
					htmlid = "simizz6";
				}
				break;
			case 71057:
				if (player.getQuest().get_step(L1Quest.QUEST_DOIL) == L1Quest.QUEST_END) {
					htmlid = "doil4b";
				}
				break;
			case 71059:
				if (player.getQuest().get_step(L1Quest.QUEST_RUDIAN) == L1Quest.QUEST_END) {
					htmlid = "rudian1c";
				} else if (player.getQuest().get_step(L1Quest.QUEST_RUDIAN) == 1) {
					htmlid = "rudian7";
				} else if (player.getQuest().get_step(L1Quest.QUEST_DOIL) == L1Quest.QUEST_END) {
					htmlid = "rudian1b";
				} else {
					htmlid = "rudian1a";
				}
				break;
			case 71060:
				if (player.getQuest().get_step(L1Quest.QUEST_RESTA) == L1Quest.QUEST_END) {
					htmlid = "resta1e";
				} else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ) == L1Quest.QUEST_END) {
					htmlid = "resta14";
				} else if (player.getQuest().get_step(L1Quest.QUEST_RESTA) == 4) {
					htmlid = "resta13";
				} else if (player.getQuest().get_step(L1Quest.QUEST_RESTA) == 3) {
					htmlid = "resta11";
					player.getQuest().set_step(L1Quest.QUEST_RESTA, 4);
				} else if (player.getQuest().get_step(L1Quest.QUEST_RESTA) == 2) {
					htmlid = "resta16";
				} else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ) == 2
						&& player.getQuest().get_step(L1Quest.QUEST_CADMUS) == 1
						|| player.getInventory().checkItem(40647)) {
					htmlid = "resta1a";
				} else if (player.getQuest().get_step(L1Quest.QUEST_CADMUS) == 1
						|| player.getInventory().checkItem(40647)) {
					htmlid = "resta1c";
				} else if (player.getQuest().get_step(L1Quest.QUEST_SIMIZZ) == 2) {
					htmlid = "resta1b";
				}
				break;
			case 71061:
				if (player.getQuest().get_step(L1Quest.QUEST_CADMUS) == L1Quest.QUEST_END) {
					htmlid = "cadmus1c";
				} else if (player.getQuest().get_step(L1Quest.QUEST_CADMUS) == 3) {
					htmlid = "cadmus8";
				} else if (player.getQuest().get_step(L1Quest.QUEST_CADMUS) == 2) {
					htmlid = "cadmus1a";
				} else if (player.getQuest().get_step(L1Quest.QUEST_DOIL) == L1Quest.QUEST_END) {
					htmlid = "cadmus1b";
				}
				break;
			case 71063:
				if (player.getQuest().get_step(L1Quest.QUEST_TBOX1) == L1Quest.QUEST_END) {
				} else if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 1) {
					htmlid = "maptbox";
				}
				break;
			case 71064:
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 2) {
					htmlid = talkToSecondtbox(player);
				}
				break;
			case 71065:
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 3) {
					htmlid = talkToSecondtbox(player);
				}
				break;
			case 71066:
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 4) {
					htmlid = talkToSecondtbox(player);
				}
				break;
			case 71067:
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 5) {
					htmlid = talkToThirdtbox(player);
				}
				break;
			case 71068:
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 6) {
					htmlid = talkToThirdtbox(player);
				}
				break;
			case 71069:
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 7) {
					htmlid = talkToThirdtbox(player);
				}
				break;
			case 71070:
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 8) {
					htmlid = talkToThirdtbox(player);
				}
				break;
			case 71071:
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 9) {
					htmlid = talkToThirdtbox(player);
				}
				break;
			case 71072:
				if (player.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 10) {
					htmlid = talkToThirdtbox(player);
				}
				break;
			case 71074:
				if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == L1Quest.QUEST_END) {
					htmlid = "lelder0";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == 3
						&& player.getInventory().checkItem(40634)) {
					htmlid = "lelder12";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == 3) {
					htmlid = "lelder11";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == 2
						&& player.getInventory().checkItem(40633)) {
					htmlid = "lelder7";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == 2) {
					htmlid = "lelder7b";
				} else if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == 1) {
					htmlid = "lelder7b";
				} else if (player.getLevel() >= 40) {
					htmlid = "lelder1";
				}
				break;
			case 71076:
				if (player.getQuest().get_step(L1Quest.QUEST_LIZARD) == L1Quest.QUEST_END) {
					htmlid = "ylizardb";
				} else {
				}
				break;
				//배틀존 추가

			case 71089:
				if (player.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 2) {
					htmlid = "francu12";
				}
				break;
			case 71090:
				if (player.getQuest().get_step(L1Quest.QUEST_CRYSTAL) == 1
						&& player.getInventory().checkItem(40620)) {
					htmlid = "jcrystal2";
				} else if (player.getQuest().get_step(L1Quest.QUEST_CRYSTAL) == 1) {
					htmlid = "jcrystal3";
				}
				break;
			case 71091:
				if (player.getQuest().get_step(L1Quest.QUEST_CRYSTAL) == 2
						&& player.getInventory().checkItem(40654)) {
					htmlid = "jcrystall2";
				}
				break;
			case 71141:
				if (player.getGfxId().getTempCharGfx() == 3887) {
					htmlid = "moumthree1";
				}
				break;
			case 71142:
				if (player.getGfxId().getTempCharGfx() == 3887) {
					htmlid = "moumtwo1";
				}
				break;
			case 71145:
				if (player.getGfxId().getTempCharGfx() == 3887) {
					htmlid = "moumone1";
				}
				break;
			case 71167:
				if (player.getGfxId().getTempCharGfx() == 3887) {
					htmlid = "frim1";
				}
				break;
			case 71168: // 진명황 단테스
				if (player.getInventory().checkItem(41028)) {
					htmlid = "dantes1";
				}
				break;
			case 71180: // 제이프
				if (player.get_sex() == 0)
					htmlid = "jp1";// 남자
				else
					htmlid = "jp3";
			case 71198:
				if (player.getQuest().get_step(71198) == 1) {
					htmlid = "tion4";
				} else if (player.getQuest().get_step(71198) == 2) {
					htmlid = "tion5";
				} else if (player.getQuest().get_step(71198) == 3) {
					htmlid = "tion6";
				} else if (player.getQuest().get_step(71198) == 4) {
					htmlid = "tion7";
				} else if (player.getQuest().get_step(71198) == 5) {
					htmlid = "tion5";
				} else if (player.getInventory().checkItem(21059, 1)) {
					htmlid = "tion19";
				}
				break;
			case 71199:
				if (player.getQuest().get_step(71199) == 1) {
					htmlid = "jeron3";
				} else if (player.getInventory().checkItem(21059, 1)
						|| player.getQuest().get_step(71199) == 255) {
					htmlid = "jeron7";
				}
				break;
			case 71200:
				if (player.isCrown()) {
					int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
					if (lv45_step == 2
							&& player.getInventory().checkItem(41422)) {
						player.getInventory().consumeItem(41422, 1);
						final int[] item_ids = { 40568 };
						final int[] item_amounts = { 1 };
						for (int i = 0; i < item_ids.length; i++) {
							player.getInventory().storeItem(item_ids[i],
									item_amounts[i]);
						}
					}
				}
				break;
			case 80047:
				if (player.getKarmaLevel() > -3) {
					htmlid = "uhelp1";
				} else {
					htmlid = "uhelp2";
				}
				break;
			case 80048:
				int level = player.getLevel();
				if (level <= 44) {
					htmlid = "entgate3";
				} else if (level >= 45 && level <= 69) {//버땅레벨
					htmlid = "entgate2";
				} else {
					htmlid = "entgate";
				}
				break;
			case 80049:
				if (player.getKarma() <= -10000000) {
					htmlid = "betray11";
				} else {
					htmlid = "betray12";
				}
				break;
			case 80050:
				if (player.getKarmaLevel() > -1) {
					htmlid = "meet103";
				} else {
					htmlid = "meet101";
				}
				break;
			case 80053:
				int karmaLevel = player.getKarmaLevel();
				if (karmaLevel == 0) {
					htmlid = "aliceyet";
				} else if (karmaLevel >= 1) {
					if (player.getInventory().checkItem(196)
							|| player.getInventory().checkItem(197)
							|| player.getInventory().checkItem(198)
							|| player.getInventory().checkItem(199)
							|| player.getInventory().checkItem(200)
							|| player.getInventory().checkItem(201)
							|| player.getInventory().checkItem(202)
							|| player.getInventory().checkItem(203)) {
						htmlid = "alice_gd";
					} else {
						htmlid = "gd";
					}
				} else if (karmaLevel <= -1) {
					if (player.getInventory().checkItem(40991)) {
						if (karmaLevel <= -1) {
							htmlid = "Mate_1";
						}
					} else if (player.getInventory().checkItem(196)) {
						if (karmaLevel <= -2) {
							htmlid = "Mate_2";
						} else {
							htmlid = "alice_1";
						}
					} else if (player.getInventory().checkItem(197)) {
						if (karmaLevel <= -3) {
							htmlid = "Mate_3";
						} else {
							htmlid = "alice_2";
						}
					} else if (player.getInventory().checkItem(198)) {
						if (karmaLevel <= -4) {
							htmlid = "Mate_4";
						} else {
							htmlid = "alice_3";
						}
					} else if (player.getInventory().checkItem(199)) {
						if (karmaLevel <= -5) {
							htmlid = "Mate_5";
						} else {
							htmlid = "alice_4";
						}
					} else if (player.getInventory().checkItem(200)) {
						if (karmaLevel <= -6) {
							htmlid = "Mate_6";
						} else {
							htmlid = "alice_5";
						}
					} else if (player.getInventory().checkItem(201)) {
						if (karmaLevel <= -7) {
							htmlid = "Mate_7";
						} else {
							htmlid = "alice_6";
						}
					} else if (player.getInventory().checkItem(202)) {
						if (karmaLevel <= -8) {
							htmlid = "Mate_8";
						} else {
							htmlid = "alice_7";
						}
					} else if (player.getInventory().checkItem(203)) {
						htmlid = "alice_8";
					} else {
						htmlid = "alice_no";
					}
				}
				break;
			case 80055:
				int amuletLevel = 0;
				if (player.getInventory().checkItem(20358)) {
					amuletLevel = 1;
				} else if (player.getInventory().checkItem(20359)) {
					amuletLevel = 2;
				} else if (player.getInventory().checkItem(20360)) {
					amuletLevel = 3;
				} else if (player.getInventory().checkItem(20361)) {
					amuletLevel = 4;
				} else if (player.getInventory().checkItem(20362)) {
					amuletLevel = 5;
				} else if (player.getInventory().checkItem(20363)) {
					amuletLevel = 6;
				} else if (player.getInventory().checkItem(20364)) {
					amuletLevel = 7;
				} else if (player.getInventory().checkItem(20365)) {
					amuletLevel = 8;
				}
				if (player.getKarmaLevel() == -1) {
					if (amuletLevel >= 1) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet1";
					}
				} else if (player.getKarmaLevel() == -2) {
					if (amuletLevel >= 2) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet2";
					}
				} else if (player.getKarmaLevel() == -3) {
					if (amuletLevel >= 3) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet3";
					}
				} else if (player.getKarmaLevel() == -4) {
					if (amuletLevel >= 4) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet4";
					}
				} else if (player.getKarmaLevel() == -5) {
					if (amuletLevel >= 5) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet5";
					}
				} else if (player.getKarmaLevel() == -6) {
					if (amuletLevel >= 6) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet6";
					}
				} else if (player.getKarmaLevel() == -7) {
					if (amuletLevel >= 7) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet7";
					}
				} else if (player.getKarmaLevel() == -8) {
					if (amuletLevel >= 8) {
						htmlid = "uamuletd";
					} else {
						htmlid = "uamulet8";
					}
				} else {
					htmlid = "uamulet0";
				}
				break;
			case 80056:
				if (player.getKarma() <= -10000000) {
					htmlid = "infamous11";
				} else {
					htmlid = "infamous12";
				}
				break;
			case 80057:
				switch (player.getKarmaLevel()) {
				case 0:
					htmlid = "alfons1";
					break;
				case -1:
					htmlid = "cyk1";
					break;
				case -2:
					htmlid = "cyk2";
					break;
				case -3:
					htmlid = "cyk3";
					break;
				case -4:
					htmlid = "cyk4";
					break;
				case -5:
					htmlid = "cyk5";
					break;
				case -6:
					htmlid = "cyk6";
					break;
				case -7:
					htmlid = "cyk7";
					break;
				case -8:
					htmlid = "cyk8";
					break;
				case 1:
					htmlid = "cbk1";
					break;
				case 2:
					htmlid = "cbk2";
					break;
				case 3:
					htmlid = "cbk3";
					break;
				case 4:
					htmlid = "cbk4";
					break;
				case 5:
					htmlid = "cbk5";
					break;
				case 6:
					htmlid = "cbk6";
					break;
				case 7:
					htmlid = "cbk7";
					break;
				case 8:
					htmlid = "cbk8";
					break;
				default:
					htmlid = "alfons1";
					break;
				}
				break;
			case 80058:
				int level5 = player.getLevel();
				if (level5 <= 44) {
					htmlid = "cpass03";
				} else if (level5 <= 69 && 45 <= level5) {//버땅레벨
					htmlid = "cpass02";
				} else {
					htmlid = "cpass01";
				}
				break;
			case 80059:
				if (player.getKarmaLevel() >= 3) {
					htmlid = "cpass03";
				} else if (player.getInventory().checkItem(40921)) {
					htmlid = "wpass02";
				} else if (player.getInventory().checkItem(40917)) {
					htmlid = "wpass14";
				} else if (player.getInventory().checkItem(40912)
						|| player.getInventory().checkItem(40910)
						|| player.getInventory().checkItem(40911)) {
					htmlid = "wpass04";
				} else if (player.getInventory().checkItem(40909)) {
					int count = getNecessarySealCount(player);
					if (player.getInventory().checkItem(40913, count)) {
						createRuler(player, 1, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (player.getInventory().checkItem(40913)) {
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
				break;
			case 80060:
				if (player.getKarmaLevel() >= 3) {
					htmlid = "cpass03";
				} else if (player.getInventory().checkItem(40921)) {
					htmlid = "wpass02";
				} else if (player.getInventory().checkItem(40920)) {
					htmlid = "wpass13";
				} else if (player.getInventory().checkItem(40909)
						|| player.getInventory().checkItem(40910)
						|| player.getInventory().checkItem(40911)) {
					htmlid = "wpass04";
				} else if (player.getInventory().checkItem(40912)) {
					int count = getNecessarySealCount(player);
					if (player.getInventory().checkItem(40916, count)) {
						createRuler(player, 8, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (player.getInventory().checkItem(40916)) {
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
				break;
			case 80061:
				if (player.getKarmaLevel() >= 3) {
					htmlid = "cpass03";
				} else if (player.getInventory().checkItem(40921)) {
					htmlid = "wpass02";
				} else if (player.getInventory().checkItem(40918)) {
					htmlid = "wpass11";
				} else if (player.getInventory().checkItem(40909)
						|| player.getInventory().checkItem(40912)
						|| player.getInventory().checkItem(40911)) {
					htmlid = "wpass04";
				} else if (player.getInventory().checkItem(40910)) {
					int count = getNecessarySealCount(player);
					if (player.getInventory().checkItem(40914, count)) {
						createRuler(player, 4, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (player.getInventory().checkItem(40914)) {
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
				break;
			case 80062:
				if (player.getKarmaLevel() >= 3) {
					htmlid = "cpass03";
				} else if (player.getInventory().checkItem(40921)) {
					htmlid = "wpass02";
				} else if (player.getInventory().checkItem(40919)) {
					htmlid = "wpass12";
				} else if (player.getInventory().checkItem(40909)
						|| player.getInventory().checkItem(40912)
						|| player.getInventory().checkItem(40910)) {
					htmlid = "wpass04";
				} else if (player.getInventory().checkItem(40911)) {
					int count = getNecessarySealCount(player);
					if (player.getInventory().checkItem(40915, count)) {
						createRuler(player, 2, count);
						htmlid = "wpass06";
					} else {
						htmlid = "wpass03";
					}
				} else if (player.getInventory().checkItem(40915)) {
					htmlid = "wpass08";
				} else {
					htmlid = "wpass05";
				}
				break;
			case 80064:
				if (player.getKarmaLevel() < 1) {
					htmlid = "meet003";
				} else {
					htmlid = "meet001";
				}
				break;
			case 80065:
				if (player.getKarmaLevel() < 3) {
					htmlid = "uturn0";
				} else {
					htmlid = "uturn1";
				}
				break;
			case 80066:
				if (player.getKarma() >= 10000000) {
					htmlid = "betray01";
				} else {
					htmlid = "betray02";
				}
				break;
			case 80067: // 첩보원(욕망의 동굴)
				if (player.getQuest().get_step(L1Quest.QUEST_DESIRE) == L1Quest.QUEST_END) {
					htmlid = "minicod10";
				} else if (player.getKarmaLevel() >= 1) {
					htmlid = "minicod07";
				} else if (player.getQuest().get_step(L1Quest.QUEST_DESIRE) == 1
						&& player.getGfxId().getTempCharGfx() == 6034) { // 코라프프리스트
					// 변신
					htmlid = "minicod03";
				} else if (player.getQuest().get_step(L1Quest.QUEST_DESIRE) == 1
						&& player.getGfxId().getTempCharGfx() != 6034) {
					htmlid = "minicod05";
				} else if (player.getQuest().get_step(L1Quest.QUEST_SHADOWS) == L1Quest.QUEST_END // 그림자의
						// 신전측
						// 퀘스트
						// 종료
						|| player.getInventory().checkItem(41121) // 카헬의
						// 지령서
						|| player.getInventory().checkItem(41122)) { // 카헬의
					// 명령서
					htmlid = "minicod01";
				} else if (player.getInventory().checkItem(41130) // 핏자국의
						// 지령서
						&& player.getInventory().checkItem(41131)) { // 핏자국의
					// 명령서
					htmlid = "minicod06";
				} else if (player.getInventory().checkItem(41130)) { // 핏자국의
					// 명령서
					htmlid = "minicod02";
				}
				break;
			case 80071:
				int earringLevel = 0;
				if (player.getInventory().checkItem(21020)) {
					earringLevel = 1;
				} else if (player.getInventory().checkItem(21021)) {
					earringLevel = 2;
				} else if (player.getInventory().checkItem(21022)) {
					earringLevel = 3;
				} else if (player.getInventory().checkItem(21023)) {
					earringLevel = 4;
				} else if (player.getInventory().checkItem(21024)) {
					earringLevel = 5;
				} else if (player.getInventory().checkItem(21025)) {
					earringLevel = 6;
				} else if (player.getInventory().checkItem(21026)) {
					earringLevel = 7;
				} else if (player.getInventory().checkItem(21027)) {
					earringLevel = 8;
				}
				if (player.getKarmaLevel() == 1) {
					if (earringLevel >= 1) {
						htmlid = "lringd";
					} else {
						htmlid = "lring1";
					}
				} else if (player.getKarmaLevel() == 2) {
					if (earringLevel >= 2) {
						htmlid = "lringd";
					} else {
						htmlid = "lring2";
					}
				} else if (player.getKarmaLevel() == 3) {
					if (earringLevel >= 3) {
						htmlid = "lringd";
					} else {
						htmlid = "lring3";
					}
				} else if (player.getKarmaLevel() == 4) {
					if (earringLevel >= 4) {
						htmlid = "lringd";
					} else {
						htmlid = "lring4";
					}
				} else if (player.getKarmaLevel() == 5) {
					if (earringLevel >= 5) {
						htmlid = "lringd";
					} else {
						htmlid = "lring5";
					}
				} else if (player.getKarmaLevel() == 6) {
					if (earringLevel >= 6) {
						htmlid = "lringd";
					} else {
						htmlid = "lring6";
					}
				} else if (player.getKarmaLevel() == 7) {
					if (earringLevel >= 7) {
						htmlid = "lringd";
					} else {
						htmlid = "lring7";
					}
				} else if (player.getKarmaLevel() == 8) {
					if (earringLevel >= 8) {
						htmlid = "lringd";
					} else {
						htmlid = "lring8";
					}
				} else {
					htmlid = "lring0";
				}
				break;
			case 80072:
				int karmaLevel1 = player.getKarmaLevel();
				switch (karmaLevel1) {
				case 1:
					htmlid = "lsmith0";
					break;
				case 2:
					htmlid = "lsmith1";
					break;
				case 3:
					htmlid = "lsmith2";
					break;
				case 4:
					htmlid = "lsmith3";
					break;
				case 5:
					htmlid = "lsmith4";
					break;
				case 6:
					htmlid = "lsmith5";
					break;
				case 7:
					htmlid = "lsmith7";
					break;
				case 8:
					htmlid = "lsmith8";
					break;
				default:
					htmlid = "";
					break;
				}
				break;
			case 80074:
				if (player.getKarma() >= 10000000) {
					htmlid = "infamous01";
				} else {
					htmlid = "infamous02";
				}
				break;
			case 80076: // 넘어진 항해사
				if (player.getInventory().checkItem(41058)) { // 완성한 항해 일지
					htmlid = "voyager8";
				} else if (player.getInventory().checkItem(49082) // 미완성의
						// 항해 일지
						|| player.getInventory().checkItem(49083)) {
					// 페이지를 추가하고 있지 않는 상태
					if (player.getInventory().checkItem(41038) // 항해 일지 1
							// 페이지
							|| player.getInventory().checkItem(41039) // 항해
							// 일지 2
							// 페이지
							|| player.getInventory().checkItem(41039) // 항해
							// 일지 3
							// 페이지
							|| player.getInventory().checkItem(41039) // 항해
							// 일지 4
							// 페이지
							|| player.getInventory().checkItem(41039) // 항해
							// 일지 5
							// 페이지
							|| player.getInventory().checkItem(41039) // 항해
							// 일지 6
							// 페이지
							|| player.getInventory().checkItem(41039) // 항해
							// 일지 7
							// 페이지
							|| player.getInventory().checkItem(41039) // 항해
							// 일지 8
							// 페이지
							|| player.getInventory().checkItem(41039) // 항해
							// 일지 9
							// 페이지
							|| player.getInventory().checkItem(41039)) { // 항해
						// 일지
						// 10
						// 페이지
						htmlid = "voyager9";
					} else {
						htmlid = "voyager7";
					}
				} else if (player.getInventory().checkItem(49082) // 미완성의
						// 항해 일지
						|| player.getInventory().checkItem(49083)
						|| player.getInventory().checkItem(49084)
						|| player.getInventory().checkItem(49085)
						|| player.getInventory().checkItem(49086)
						|| player.getInventory().checkItem(49087)
						|| player.getInventory().checkItem(49088)
						|| player.getInventory().checkItem(49089)
						|| player.getInventory().checkItem(49090)
						|| player.getInventory().checkItem(49091)) {
					// 페이지를 추가한 상태
					htmlid = "voyager7";
				}
				break;
			case 80079:
				if (player.getQuest().get_step(L1Quest.QUEST_KEPLISHA) == L1Quest.QUEST_END
						&& !player.getInventory().checkItem(41312)) {
					htmlid = "keplisha6";
				} else {
					if (player.getInventory().checkItem(41314)) {
						htmlid = "keplisha3";
					} else if (player.getInventory().checkItem(41313)) {
						htmlid = "keplisha2";
					} else if (player.getInventory().checkItem(41312)) {
						htmlid = "keplisha4";
					}
				}
				break;
			case 80104:
				if (!player.isCrown()) {
					htmlid = "horseseller4";
				}
				break;
			case 81105:
				if (player.isWizard()) {
					int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
					if (lv45_step >= 3) {
						htmlid = "stoenm3";
					} else if (lv45_step >= 2) {
						htmlid = "stoenm2";
					} else if (lv45_step >= 1) {
						htmlid = "stoenm1";
					}
				}
				break;
			case 81155:
				boolean hascastle7 = checkHasCastle(player,
						L1CastleLocation.DIAD_CASTLE_ID);
				if (hascastle7) {
					if (checkClanLeader(player)) {
						htmlid = "olle1";
					} else {
						htmlid = "olle6";
						htmldata = new String[] { player.getName() };
					}
				} else {
					htmlid = "olle7";
				}
				break;
			case 81200:
				if (player.getInventory().checkItem(21069)
						|| player.getInventory().checkItem(21074)) {
					htmlid = "c_belt";
				}
				break;
			case 81202: // 첩보원(그림자의 신전)
				if (player.getQuest().get_step(L1Quest.QUEST_SHADOWS) == L1Quest.QUEST_END) {
					htmlid = "minitos10";
				} else if (player.getKarmaLevel() <= -1) {
					htmlid = "minitos07";
				} else if (player.getQuest().get_step(L1Quest.QUEST_SHADOWS) == 1
						&& player.getGfxId().getTempCharGfx() == 6035) { // 렛서데이몬
					// 변신
					htmlid = "minitos03";
				} else if (player.getQuest().get_step(L1Quest.QUEST_SHADOWS) == 1
						&& player.getGfxId().getTempCharGfx() != 6035) {
					htmlid = "minitos05";
				} else if (player.getQuest().get_step(L1Quest.QUEST_DESIRE) == L1Quest.QUEST_END // 욕망의
						// 동굴측
						// 퀘스트
						// 종료
						|| player.getInventory().checkItem(41130) // 핏자국의
						// 지령서
						|| player.getInventory().checkItem(41131)) { // 핏자국의
					// 명령서
					htmlid = "minitos01";
				} else if (player.getInventory().checkItem(41121) // 카헬의
						// 지령서
						&& player.getInventory().checkItem(41122)) { // 카헬의
					// 명령서
					htmlid = "minitos06";
				} else if (player.getInventory().checkItem(41121)) { // 카헬의
					// 명령서
					htmlid = "minitos02";
				}
				break;
			case 81208: // 더러워진 브롭브
				if (player.getInventory().checkItem(41129) // 핏자국의 정수
						|| player.getInventory().checkItem(41138)) { // 카헬의
					// 정수
					htmlid = "minibrob04";
				} else if (player.getInventory().checkItem(41126) // 핏자국의
						// 타락 한
						// 정수
						&& player.getInventory().checkItem(41127) // 핏자국의
						// 무력한
						// 정수
						&& player.getInventory().checkItem(41128) // 핏자국의
						// 아집인
						// 정수
						|| player.getInventory().checkItem(41135) // 카헬의
						// 타락 한
						// 정수
						&& player.getInventory().checkItem(41136) // 카헬의
						// 아집인
						// 정수
						&& player.getInventory().checkItem(41137)) { // 카헬의
					// 아집인
					// 정수
					htmlid = "minibrob02";
				}
				break;
			// 경험치 지급단
			case 4200008:
				if (player.getLevel() >= 72) {
					htmlid = "expgive3";
				}
				break;
			case 4200088:
				if (player.getLevel() >= 84) {
					htmlid = "expgive3";
				}
				break;
			case 777846:
				if(!MopoCheck(player)) {
				htmlid = "mopo4";
				}
				break;
			case 777849: // 킬톤 (호랑이 사육)
			     if (player.getInventory().checkItem(87050)){
			      htmlid ="killton2";
			     }
			     break;
			    case 777848: // 메린 (진돗개 사육)
			     if (player.getInventory().checkItem(87051)){
			      htmlid ="merin2";
			     }
			     break;
			case 4200009: // 보석세공사 데이빗(얼녀귀걸이)
				if (checkItem(player, 49031)) {
					if (checkItem(player, 21081)) { // 얼녀귀걸이 1
						htmlid = "gemout1";
					} else if (player.getQuest().get_step(
							L1Quest.QUEST_ICEQUEENRING) == 1) {
						htmlid = "gemout2";
					} else if (player.getQuest().get_step(
							L1Quest.QUEST_ICEQUEENRING) == 2) {
						htmlid = "gemout3";
					} else if (player.getQuest().get_step(
							L1Quest.QUEST_ICEQUEENRING) == 3) {
						htmlid = "gemout4";
					} else if (player.getQuest().get_step(
							L1Quest.QUEST_ICEQUEENRING) == 4) {
						htmlid = "gemout5";
					} else if (player.getQuest().get_step(
							L1Quest.QUEST_ICEQUEENRING) == 5) {
						htmlid = "gemout6";
					} else if (player.getQuest().get_step(
							L1Quest.QUEST_ICEQUEENRING) == 6) {
						htmlid = "gemout7";
					} else if (player.getQuest().get_step(
							L1Quest.QUEST_ICEQUEENRING) == 7) {
						htmlid = "gemout8";
					} else { // 보석만 가지고있다.
						htmlid = "gemout17";
					}
				}
				break;
			case 4201000: // 환술사 아샤
				if (player.isIllusionist())
					htmlid = "asha1";
				else
					htmlid = "asha2";
				break;
			case 4202000: // 용기사 피에나
				if (player.isDragonknight())
					htmlid = "feaena1";
				else
					htmlid = "feaena2";
				break;
			case 4204000: // 요정 달장퀘스트 로빈후드
				if (!player.isElf()) {
					// int MOONBOW_step =
					// quest.get_step(L1Quest.QUEST_MOONBOW);
				} else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 0) {
					htmlid = "robinhood1";
				} else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 1) {
					htmlid = "robinhood8";
				} else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 2) {
					htmlid = "robinhood13";
				} else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 6) {
					htmlid = "robinhood9";
				} else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 7) {
					htmlid = "robinhood11";
				} else {
					htmlid = "robinhood3";
				}
				break;
			case 4205000: // 기사30퀘 던전 입장 문지기
				if (player.isKnight()) {
					if (player.getLevel() >= 30) {
						int q30step = quest.get_step(L1Quest.QUEST_LEVEL30);
						if (q30step <= 2) {
							htmlid = "dgkeeperk4";
						} else if (q30step == 3 && player.getArmor() != null) {
							htmlid = "dgkeeperk3";
						} else if (q30step == 3 && player.getArmor() == null) {
							htmlid = "dgkeeperk1";
						} else if (q30step >= 4 || q30step == L1Quest.QUEST_END) {
							htmlid = "dgkeeperk5";
						}
					}
				} else {
					htmlid = "dgkeepero1";
				}
				break;
			case 4210000:
				if (!player.isElf()) {
				} else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 2) {
					htmlid = "zybril1";
				} else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 3) {
					htmlid = "zybril7";
				} else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 4) {
					htmlid = "zybril8";
				} else if (player.getQuest().get_step(L1Quest.QUEST_MOONBOW) == 5) {
					htmlid = "zybril18";
				} else {
					htmlid = "zybril16";
				}
				break;
			/**
			 * ***************************** 추가 ******************************
			 */
			/**
			 * ***************************** 시즌3 이후 추가
			 * ******************************
			 */
			// 베히모스 엘라스
			case 4218002:
				if (!player.isDragonknight())
					htmlid = "elas3";
				break;
			case 4218003: // 장로 프로켈
				if (player.isDragonknight()) {
					if (player.getLevel() >= 15) {
						int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
						if (lv15_step == 1) {
							htmlid = "prokel4";
						} else if (lv15_step == 2
								|| lv15_step == L1Quest.QUEST_END) {
							htmlid = "prokel7";
						} else {
							htmlid = "prokel2";
						}
					} else {
						htmlid = "prokel1"; // 레벨 15이하
					}
				}
				break;
			// 베히모스 탈리온
			case 4218004:
				if (!player.isDragonknight())
					htmlid = "talrion4";
				break;
			case 4219004: // 장로 실레인
				if (player.isIllusionist()) {
					if (player.getLevel() >= 15) {
						int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
						if (lv15_step == 1) {
							htmlid = "silrein4";
						} else if (lv15_step == 2
								|| lv15_step == L1Quest.QUEST_END) {
							htmlid = "silrein5";
						} else {
							htmlid = "silrein2";
						}
					} else {
						htmlid = "prokel1"; // 레벨 15이하
					}
				}
				break;
			/**
			 * *************************** 에피소드 2 New System
			 * ****************************
			 */
			// 숨겨진 용들의 땅 입구(노랑포탈)
			case 4212013:
				// if (안타레이드 성공후 오픈상태체크){
				if (player.getLevel() >= 30 && player.getLevel() <= 51) {
					htmlid = "dsecret2";
				} else if (player.getLevel() > 51) {
					htmlid = "dsecret1";
				} else {
					htmlid = "dsecret3";
				}
				// } else {
				// htmlid = "dsecret3";
				// }
				break;
			// 마야의 그림자
			case 6000015:
				if (player.getInventory().checkItem(41158)) {
					htmlid = "adenshadow1";
				} else {
					htmlid = "adenshadow2";
				}
				break;
			default:
				break;
			}

			// html 표시 패킷 송신
			if (htmlid != null) { // htmlid가 지정되고 있는 경우
				if (htmldata != null) { // html 지정이 있는 경우는 표시
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid,
							htmldata));
				} else {
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
				}
			} else {
				if (player.getLawful() < -1000) { // 플레이어가 카오틱
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		}
	}

	private static String talkToTownadviser(L1PcInstance pc, int town_id) {
		String htmlid;
		if (pc.getHomeTownId() == town_id) {
			htmlid = "artisan1";
		} else {
			htmlid = "artisan2";
		}

		return htmlid;
	}

	private static String talkToTownmaster(L1PcInstance pc, int town_id) {
		String htmlid;
		if (pc.getHomeTownId() == town_id) {
			htmlid = "hometown";
		} else {
			htmlid = "othertown";
		}
		return htmlid;
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
	}

	public void doFinalAction(L1PcInstance player) {
	}

	private boolean checkItem(L1PcInstance player, int itemid) {
		return player.getInventory().checkItem(itemid);
	}

	private boolean checkHasCastle(L1PcInstance player, int castle_id) {
		if (player.getClanid() != 0) {
			L1Clan clan = L1World.getInstance().getClan(player.getClanname());
			if (clan != null) {
				if (clan.getCastleId() == castle_id) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkClanLeader(L1PcInstance player) {
		if (player.isCrown()) {
			L1Clan clan = L1World.getInstance().getClan(player.getClanname());
			if (clan != null) {
				if (player.getId() == clan.getLeaderId()) {
					return true;
				}
			}
		}
		return false;
	}

	private int getNecessarySealCount(L1PcInstance pc) {
		int rulerCount = 0;
		int necessarySealCount = 10;
		if (pc.getInventory().checkItem(40917)) {
			rulerCount++;
		}
		if (pc.getInventory().checkItem(40920)) {
			rulerCount++;
		}
		if (pc.getInventory().checkItem(40918)) {
			rulerCount++;
		}
		if (pc.getInventory().checkItem(40919)) {
			rulerCount++;
		}
		if (rulerCount == 0) {
			necessarySealCount = 10;
		} else if (rulerCount == 1) {
			necessarySealCount = 100;
		} else if (rulerCount == 2) {
			necessarySealCount = 200;
		} else if (rulerCount == 3) {
			necessarySealCount = 500;
		}
		return necessarySealCount;
	}

	/**배틀존**/
	private void DuelZone(L1PcInstance pc) {
		if (pc.isInParty()) {
			pc.sendPackets(new S_SystemMessage("파티중에는 배틀존 입장이 불가능합니다."));
			return;
		}

		int total = (BattleZone.getInstance().MemberCount1) + (BattleZone.getInstance().MemberCount2);
		if(total >= 60){
			pc.sendPackets(new S_SystemMessage("배틀존 인원이 꽉 찾습니다. 다음 배틀존을 이용해주세요."));
			return;
		}

		if (BattleZone.getInstance().MemberCount1 <= BattleZone.getInstance().MemberCount2) {
			if(BattleZone.getInstance().MemberCount1 >= 30){
				BattleZone.getInstance().setTeam1Open(false);
				return;
			}
			if(BattleZone.getInstance().getTeam1Open() == false){
				pc.sendPackets(new S_SystemMessage("다크팀 입장이 제한되었습니다."));
				return;
			}
			pc.set_DuelLine(1);
			pc.sendPackets(new S_SystemMessage("배틀존 대기실로 입장하셨습니다. ("
					+ pc.get_DuelLine() + ") 다크 라인"));
			pc.sendPackets(new S_SystemMessage("다크팀 인원"
					+ BattleZone.getInstance().get라인1Count()
					+ " 명 / 아크팀 인원"
					+ BattleZone.getInstance().get라인2Count() + "명"));
			L1Teleport.teleport(pc, 32768, 32773, (short) 5001, 0, true);// <<
			// 대기실
			// 좌표2번라인
			BattleZone.getInstance().add라인1(pc);
			BattleZone.getInstance().MemberCount1 += 1;
			BattleZone.getInstance().DuelCount += 1;

		} else if (BattleZone.getInstance().MemberCount1 > BattleZone.getInstance().MemberCount2) {
			if(BattleZone.getInstance().MemberCount2 >= 30){
				BattleZone.getInstance().setTeam2Open(false);
				return;
			}
			if(BattleZone.getInstance().getTeam2Open() == false){
				pc.sendPackets(new S_SystemMessage("아크팀 입장이 제한되었습니다."));
				return;
			}
			pc.set_DuelLine(2);
			pc.sendPackets(new S_SystemMessage("배틀존 대기실로 입장하셨습니다. ("+ pc.get_DuelLine() + ") 아크 라인"));
			pc.sendPackets(new S_SystemMessage("다크팀 "+ BattleZone.getInstance().get라인1Count()
					+ " 명 대기중 / 아크팀 "
					+ BattleZone.getInstance().get라인2Count() + " 명 대기중"));
			L1Teleport.teleport(pc, 32768, 32773, (short) 5001, 0, true);// <<
			// 대기실
			// 좌표
			// 1번라인
			BattleZone.getInstance().add라인2(pc);
			BattleZone.getInstance().MemberCount2 += 1;
			BattleZone.getInstance().DuelCount += 1;
		}
		BattleZone.getInstance().add배틀존유저(pc);
		Poly(pc, pc.get_DuelLine());

	}

	private void Poly(L1PcInstance pc, int DuelLine) {
		if(pc == null)
			return;
		int polyid = 0;
		int time = 1800;
		if (pc != null ) {
			if (pc.isKnight() || pc.isCrown() || pc.isDarkelf() || pc.isDragonknight()) {
				// 기사 군주 다크엘프 용기사
				if (DuelLine == 1) {
					polyid = 6267;// <<1번라인 변신다크>
				} else {
					polyid = 6270;// 2번라인 아크변신
				}
				L1PolyMorph.doPoly(pc, polyid, time, 2);
			}
			//법사 환술사
			if (pc.isWizard() || pc.isIllusionist()){
				if (DuelLine == 1) {
					polyid = 6268;
				} else {
					polyid = 6271;
				}
				L1PolyMorph.doPoly(pc, polyid, time, 2);
			}
			//요정
			if(pc.isElf()){
				if (DuelLine == 1) {
					polyid = 6269;
				} else {
					polyid = 6272;
				}
				L1PolyMorph.doPoly(pc, polyid, time, 2);
			}
		}
	}

	/**배틀존**/
	
	
	
	public class NPCAction extends TimerTask { //아논망치
		   @Override
		   public void run() {
		    setAction();
		   }
		  }
	

	private void createRuler(L1PcInstance pc, int attr, int sealCount) {
		int rulerId = 0;
		int protectionId = 0;
		int sealId = 0;
		if (attr == 1) {
			rulerId = 40917;
			protectionId = 40909;
			sealId = 40913;
		} else if (attr == 2) {
			rulerId = 40919;
			protectionId = 40911;
			sealId = 40915;
		} else if (attr == 4) {
			rulerId = 40918;
			protectionId = 40910;
			sealId = 40914;
		} else if (attr == 8) {
			rulerId = 40920;
			protectionId = 40912;
			sealId = 40916;
		}
		pc.getInventory().consumeItem(protectionId, 1);
		pc.getInventory().consumeItem(sealId, sealCount);
		L1ItemInstance item = pc.getInventory().storeItem(rulerId, 1);
		if (item != null) {
			pc.sendPackets(new S_ServerMessage(143,
					getNpcTemplate().get_name(), item.getLogName()));
		}
	}

	private String talkToSecondtbox(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getQuest().get_step(L1Quest.QUEST_TBOX1) == L1Quest.QUEST_END) {
			if (pc.getInventory().checkItem(40701)) {
				htmlid = "maptboxa";
			} else {
				htmlid = "maptbox0";
			}
		} else {
			htmlid = "maptbox0";
		}
		return htmlid;
	}
	
	private void HellTime(L1PcInstance pc) {

			if (HellDevilController.getInstance().getDevilStart() == true) {
				Random random = new Random();
				int i13 = 32699 + random.nextInt(4);
				int k19 = 32770 + random.nextInt(4);
				L1Teleport.teleport(pc, i13, k19, (short) 666, 6, true);
                pc.sendPackets(new S_SystemMessage("지옥사냥터가 열린시각으로부터 1시간동안 입장가능합니다."));
				return;
			} else {
				//pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\f<[지옥사냥터:입장시간이아닙니다]"));
				pc.sendPackets(new S_SystemMessage("지옥사냥터:입장시간이아닙니다"));
				return;
			}
		}

	
	
	
	
	/** 모포의 계약상태 확인 **/
	private boolean MopoCheck(L1PcInstance pc) {
	for (int i = L1ItemId.MOPO_JUiCE_CONTRACT1; i <= L1ItemId.MOPO_JUiCE_PIPE6; i++) {
	if (pc.getInventory().checkItem(i)) return false;
	}
	return true;
	} 
	private String talkToThirdtbox(L1PcInstance pc) {
		String htmlid = "";
		if (pc.getQuest().get_step(L1Quest.QUEST_TBOX2) == L1Quest.QUEST_END) {
			if (pc.getInventory().checkItem(40701)) {
				htmlid = "maptboxd";
			} else {
				htmlid = "maptbox0";
			}
		} else {
			htmlid = "maptbox0";
		}
		return htmlid;
	}

	private static final long REST_MILLISEC = 10000;

}
