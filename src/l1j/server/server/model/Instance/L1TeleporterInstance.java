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

import java.util.Calendar;
import java.util.Random;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.gametime.RealTime;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

// Referenced classes of package l1j.server.server.model:
// L1NpcInstance, L1Teleport, L1NpcTalkData, L1PcInstance,
// L1TeleporterPrices, L1TeleportLocations

public class L1TeleporterInstance extends L1NpcInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GeneralThreadPool _threadPool = GeneralThreadPool.getInstance();
	
	public L1TeleporterInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		L1Attack attack = new L1Attack(player, this);
		attack.calcHit();
		attack.action();
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		if (player == null || this == null)
			return;
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		int npcid = getNpcTemplate().get_npcId();
		L1Quest quest = player.getQuest();
		String htmlid = null;

		if (talking != null) {
			switch (npcid) {
			case 50001:
				if (player.isElf()) {
					htmlid = "barnia3";
				} else if (player.isKnight() || player.isCrown()) {
					htmlid = "barnia2";
				} else if (player.isWizard() || player.isDarkelf()) {
					htmlid = "barnia1";
				}
				break;
			case 50014:
				if (player.isWizard()) { // 위저드
					if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1
							&& !player.getInventory().checkItem(40579)) { // 안
						// 데드의
						// 뼈
						htmlid = "dilong1";
					} else {
						htmlid = "dilong3";
					}
				}
				break;
			case 50016:
				if (player.getLevel() >= 13)
					htmlid = "zeno2";
				break;
			case 50031:
				if (player.isElf()) { // 에르프
					if (quest.get_step(L1Quest.QUEST_LEVEL45) == 2) {
						if (!player.getInventory().checkItem(40602)) { // 블루 플룻
							htmlid = "sepia1";
						}
					}
				}
				break;
			case 50020: //스텐리 - 켄트 추가
			case 50024: //아스터 - 글루디오
			case 50036: //윌마 - 은말
			case 50039: //레슬리 - 웰던
			case 50044: //시리우스-아덴
			case 50046: //엘레리스 - ??
			case 50051: //키리우스 - 오렌
			case 50054: //트레이 - 윈다우드
			case 50066: //하이네 - 리올
			if (player.getLevel() < 45){
			htmlid = "starttel1";
			} else if (player.getLevel() >= 45 && player.getLevel() <= 51){
			htmlid = "starttel2";
			} else {
			htmlid = "starttel3";
			}
			break;
				
				
			case 50043:
				if (quest.get_step(L1Quest.QUEST_LEVEL50) == L1Quest.QUEST_END) {
					htmlid = "ramuda2";
				} else if (quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // 디가르딘
					// 동의가
					// 끝난
					// 상태
					if (player.isCrown()) { // 군주
						if (_isNowDely) { // 텔레포트 지연중
							htmlid = "ramuda4";
						} else {
							htmlid = "ramudap1";
						}
					} else { // 군주 이외
						htmlid = "ramuda1";
					}
				} else {
					htmlid = "ramuda3";
				}
				break;
			case 50055:
				if (player.getLevel() >= 13)
					htmlid = "drist1";
				break;
			case 50069:
				if (!player.isDarkelf())
					htmlid = "enya2";
				else if (player.getLevel() >= 13)
					htmlid = "enya4";
				break;
			case 50056: // 메트 by존
			    if (player.getLevel() < 45){
			     htmlid = "telesilver4";
			    } else if (player.getLevel() >= 45 && player.getLevel() <= 99){
			     htmlid = "telesilver5";
			    }  else  {
			     htmlid = "telesilver1";
			    }
			    break;

			case 70779:
				if (player.getGfxId().getTempCharGfx() == 1037) { // 쟈이안트안트 변신
					htmlid = "ants3";
				} else if (player.getGfxId().getTempCharGfx() == 1039) {// 쟈이안트안트소르쟈
					// 변신
					if (player.isCrown()) { // 군주
						if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
							if (player.getInventory().checkItem(40547)) { // 주민들의
								// 유품
								htmlid = "antsn";
							} else {
								htmlid = "ants1";
							}
						} else { // Step1 이외
							htmlid = "antsn";
						}
					} else { // 군주 이외
						htmlid = "antsn";
					}
				}
				break;
			case 70853:
				if (player.isElf()) { // 에르프
					if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
						if (!player.getInventory().checkItem(40592)) { // 저주해진
							// 정령서
							Random random = new Random(System.nanoTime());
							if (random.nextInt(100) < 50) { // 50%로 다크마르단젼
								htmlid = "fairyp2";
							} else { // 다크 에르프 지하 감옥
								htmlid = "fairyp1";
							}
						}
					}
				}
				break;
			}
			// html 표시
			if (htmlid != null) { // htmlid가 지정되고 있는 경우
				player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
			} else {
				if (player.getLawful() < -1000) { // 플레이어가 카오틱
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		} else {
			_log.finest((new StringBuilder())
					.append("No actions for npc id : ").append(objid)
					.toString());
		}
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
		if (this == null || player == null)
			return;
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		if (action.equalsIgnoreCase("teleportURL")) {
			L1NpcHtml html = new L1NpcHtml(talking.getTeleportURL());
			String[] price = null;
			int npcid = getNpcTemplate().get_npcId();
			switch (npcid) {
			case 50015: // 말하는섬 루카
				price = new String[] { "1500" };
				break;
			case 50017: // 말하는 섬 케이스
				price = new String[] { "50" };
				break;
			case 50020: // 켄트 스탠리
				price = new String[] { "50", "50", "50", "120", "120", "120",
						"120", "180", "180", "200", "200", "600", "7100", "10000" };
				break;
			case 50024: // 글루디오 아스터
				price = new String[] { "75", "75", "75", "180", "180", "270",
						"270", "270", "360", "360", "360", "300", "300", "750",
						"10200", "10000" };
				break;
			case 50026: // 그르딘 시장⇒기란 시장, 오렌 시장, 실버 나이트 타운 시장
				price = new String[] { "550", "700", "810" };
				break;
			case 50033: // 기란 시장⇒그르딘 시장, 오렌 시장, 실버 나이트 타운 시장
				price = new String[] { "560", "720", "560" };
				break;
			case 50035: // 기란성 게이트 기퍼
				price = new String[] { "210", "210", "420", "210" };
				break;
			case 50036: // 기란 윌마
				price = new String[] { "75", "75", "75", "180", "180", "180",
						"180", "270", "270", "450", "450", "450","1050", "11100", "10000" };
				break;
			case 50039: // 웰던 레슬리
				price = new String[] { "72", "72", "174", "174", "261", "261",
						"261", "348", "348", "580", "580", "1160", "11165", "10000" };
				break;
			case 50040: // 난성 게이트 키퍼
				price = new String[] { "210", "420", "210" };
				break;
			case 50044: // 아덴 시리우스
			case 50046: // 아덴 엘레리스
				price = new String[] { "70", "168", "168", "252", "252", "252",
						"336", "336", "420", "700", "700", "1260", "10360", "10000" };
				break;
			case 50049: // 오렌 시장⇒그르딘 시장, 기란 시장, 실버 나이트 타운 시장
				price = new String[] { "1150", "980", "590" };
				break;
			case 50051: // 오렌키리우스
				price = new String[] { "75", "180", "270", "270", "360", "360",
						"360", "450", "450", "750", "750", "1350", "12000", "10000" };
				break;
			case 50054: // 윈다우드트레이
				price = new String[] { "75", "75", "180", "180", "180", "270",
						"270", "360", "450", "300", "300", "750", "9750", "10000" };
				break;
			case 50056: // 은기사마을 메트
				price = new String[] { "75", "75", "75", "180", "180", "180",
						"270", "270", "270", "360", "360", "450", "450",
						"1050", "10200", "10000" };
				break;
			case 50059: // 실버 나이트 타운 시장⇒그르딘 시장, 기란 시장, 오렌 시장
				price = new String[] { "580", "680", "680" };
				break;
			case 50063: // 오크 요새 게이트 키퍼
				price = new String[] { "210", "420", "210" };
				break;
			case 50066: // 하이네리올
				price = new String[] { "990", "450", "400", "550", "400",
						"710", "350", "680", "1000", "180", "180", "3200",
						"6900", "10000" };
				break;
			case 50068: // 디아노스
				price = new String[] { "1500", "800", "600", "1800", "1800",
						"1000" };
				break;
			case 50072: // 공간이동사 디아루즈
				price = new String[] { "2200", "1800", "1000", "1600", "2200",
						"1200", "1300", "2000", "2000" };
				break;
			case 50073: // 공간이동사 디아베스
				price = new String[] { "380", "850", "290", "290", "290",
						"180", "480", "150", "150", "380", "480", "380", "850",
						"1000" };
				break;
			case 50079: // 마법사 다니엘
				price = new String[] { "550", "550", "550", "600", "600",
						"600", "650", "700", "750", "750", "500", "500", "700" };
				break;
			case 4208002: // 마법사멀린
				break;
                /** 상아 리뉴얼 **/

			case 40108:
				break; //추가
            case 40109:
                price = new String[] { "7000", "7000", "7000", "14000", "14000" };
                break; //추가

                        /** 상아 리뉴얼 **/
			case 4918000: // 데카비아 베히모스
				price = new String[] { "50", "50", "50", "50", "120", "120",
						"180", "180", "180", "240", "240", "400", "400", "800",
						"7700" };
				break;
			case 4919000: // 실베리아 샤리엘
				price = new String[] { "50", "50", "50", "120", "180", "180",
						"240", "240", "240", "300", "300", "500", "500", "900",
						"8000" };
				break;
			case 6000014: // 시종장 맘몬
				price = new String[] { "14000" };
				break;
			case 6000016: // 신녀 플로라
				price = new String[] { "1000" };
				break;
			default:
				price = new String[] { "" };
				break;
			}
			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLA")) {

			String html = "";
			String[] price = null;
			int npcid = getNpcTemplate().get_npcId();
			if (npcid == 50079) {
				html = "telediad3";
				price = new String[] { "700", "800", "800", "1000", "10000" };
			} else if (npcid == 4918000) {
				html = "dekabia3";
				price = new String[] { "100", "220", "220", "220", "330",
						"330", "330", "330", "440", "440" };
			} else if (npcid == 4919000) {
				html = "sharial3";
				price = new String[] { "220", "330", "330", "330", "440",
						"440", "550", "550", "550", "550" };
			} else {
				price = new String[] { "" };
			}
			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLB")) {
			String html = "guide_1_1";
			String[] price = null;

			price = new String[] { "450", "450", "450", "450" };

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLC")) {
			String html = "guide_1_2";
			String[] price = null;

			price = new String[] { "465", "465", "465", "465", "1065", "1065" };

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLD")) {
			String html = "guide_1_3";
			String[] price = null;

			price = new String[] { "480", "480", "480", "480", "630", "1080",
					"630" };

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLE")) {
			String html = "guide_2_1";
			String[] price = null;

			price = new String[] { "600", "600", "750", "750" };

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLF")) {
			String html = "guide_2_2";
			String[] price = null;

			price = new String[] { "615", "615", "915", "765" };

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLG")) {
			String html = "guide_2_3";
			String[] price = null;

			price = new String[] { "630", "780", "630", "1080", "930" };

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLH")) {
			String html = "guide_3_1";
			String[] price = null;

			price = new String[] { "750", "750", "750", "1200", "1050" };

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLI")) {
			String html = "guide_3_2";
			String[] price = null;

			price = new String[] { "765", "765", "765", "765", "1515", "1215",
					"915" };

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLJ")) {
			String html = "guide_3_3";
			String[] price = null;

			price = new String[] { "780", "780", "780", "780", "780", "1230",
					"1080" };

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		} else if (action.equalsIgnoreCase("teleportURLK")) {
			String html = "guide_4";
			String[] price = null;

			price = new String[]{ "780","780","780","780","780","1080","1080","1080" };

			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
			

		} else if (action.equalsIgnoreCase("teleportURLL")) {
			int npcid = getNpcTemplate().get_npcId();
			String html = "";
			String[] price = null; 

			switch(npcid){
			case 50056: // 메트 
			html = "guide_0_1";
			price = new String[]{ "30","30","30","70","80","90","100" };
			break;
			default:
			html = "guide_6"; //패치
			price = new String[] { "700", "700" };
			break;
			}
			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
			} else if (action.equalsIgnoreCase("teleportURLM")){
			int npcid = getNpcTemplate().get_npcId();
			String html = "";
			String[] price = null;
			switch(npcid){
			case 50056: // 메트
			html = "hp_storm1"; // 폭풍 수련 지역
			break;
			default:

			html = "guide_7"; //패치
			price = new String[] { "500", "500", "500", "500", "500", "500", "500",
			"500", "500", "500", "500" };
			break;
			}
			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
			}


			
		if (action.startsWith("teleport")) {
			_log.finest((new StringBuilder()).append("Setting action to : ")
					.append(action).toString());
			doFinalAction(player, action);
		}
	}

	private void doFinalAction(L1PcInstance player, String action) {
		if (this == null || player == null)
			return;
		int objid = getId();

		int npcid = getNpcTemplate().get_npcId();
		String htmlid = null;
		boolean isTeleport = true;

		if (npcid == 50014) { // 디 론
			if (!player.getInventory().checkItem(40581)) { // 안 데드의 키
				isTeleport = false;
				htmlid = "dilongn";
			}
		} else if (npcid == 50043) { // Lambda
			if (_isNowDely) { // 텔레포트 지연중
				isTeleport = false;
			}
		} else if (npcid == 50625) { // 고대인(Lv50 퀘스트 고대의 공간 2 F)
			if (_isNowDely) { // 텔레포트 지연중
				isTeleport = false;
			}
		}

		if (isTeleport) { // 텔레포트 실행
			try {
				// 뮤탄트안트단젼(군주 Lv30 퀘스트)
				if (action.equalsIgnoreCase("teleport mutant-dungen_la")) {
					// 3 매스 이내의 Pc
					for (L1PcInstance otherPc : L1World.getInstance()
							.getVisiblePlayer(player, 3)) {
						if (otherPc == null)
							continue;
						if (otherPc.getClanid() != 0
								&& otherPc.getClanid() == player.getClanid()
								&& otherPc.getId() != player.getId()) {
							L1Teleport.teleport(otherPc, 32740, 32800,
									(short) 217, 5, true);
						}
					}
					L1Teleport.teleport(player, 32740, 32800, (short) 217, 5,
							true);
				}
				// 시련의 지하 감옥(위저드 Lv30 퀘스트)
				else if (action
						.equalsIgnoreCase("teleport mage-quest-dungen_la")) {
					L1Teleport.teleport(player, 32791, 32788, (short) 201, 5,
							true);
				} else if (action.equalsIgnoreCase("teleport 29_la")) { // Lambda
					L1PcInstance kni = null;
					L1PcInstance elf = null;
					L1PcInstance wiz = null;
					// 3 매스 이내의 Pc
					L1Quest quest = null;
					for (L1PcInstance otherPc : L1World.getInstance()
							.getVisiblePlayer(player, 3)) {
						if (otherPc == null)
							continue;
						quest = otherPc.getQuest();
						if (otherPc.isKnight() // 나이트
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // 디가르딘
							// 동의가
							// 끝난
							// 상태
							if (kni == null) {
								kni = otherPc;
							}
						} else if (otherPc.isElf() // 요정
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // 디가르딘
							// 동의가
							// 끝난
							// 상태
							if (elf == null) {
								elf = otherPc;
							}
						} else if (otherPc.isWizard() // 마법사
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // 디가르딘
							// 동의가
							// 끝난
							// 상태
							if (wiz == null) {
								wiz = otherPc;
							}
						}
					}
					if (kni != null && elf != null && wiz != null) { // 전클래스
						// 갖추어져
						// 있다
						L1Teleport.teleport(player, 32723, 32850, (short) 2000,
								2, true);
						L1Teleport.teleport(kni, 32750, 32851, (short) 2000, 6,
								true);
						L1Teleport.teleport(elf, 32878, 32980, (short) 2000, 6,
								true);
						L1Teleport.teleport(wiz, 32876, 33003, (short) 2000, 0,
								true);
						TeleportDelyTimer timer = new TeleportDelyTimer();
						_threadPool.execute(timer);
					}
				} else if (action.equalsIgnoreCase("teleport barlog_la")) { // 고대인(Lv50
					// 퀘스트
					// 고대의
					// 공간 2
					// F)
					L1Teleport.teleport(player, 32755, 32844, (short) 2002, 5,
							true);
					TeleportDelyTimer timer = new TeleportDelyTimer();
					_threadPool.execute(timer);
				     /** 상아 리뉴얼 **/
				} else if (action.equalsIgnoreCase("teleport ivorytower4")) {
				     RealTime time = RealTimeClock.getInstance().getRealTime();
				     int entertime = player.getTkddkdungeonTime() % 1000;
				     int enterday = player.getTkddkdungeonTime() / 1000;
				     int dayofyear = time.get(Calendar.DAY_OF_YEAR);

				     if (entertime > 60 && enterday == dayofyear) {
				    	 player.sendPackets(new S_SystemMessage("상아탑 사용 시간이 남아 있지 않습니다."));
				      htmlid = "";
				      return;
				     } else {
				      if (enterday < dayofyear)
				       player.setTkddkdungeonTime(dayofyear * 1000);
				      L1Teleport.teleport(player, 32898, 32768, (short) 78,
				        5, true);
				      player.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, (60 - entertime)*60));
				      /**안전모드**/
						player.getSkillEffectTimerSet().setSkillEffect(
								L1SkillId.ABSOLUTE_BARRIER, 5000);
						player.Safe_Teleport = true;
						player.sendPackets(new S_SystemMessage("안전 모드가 활성화 되었습니다."));
						/**안전모드**/
				      int a = entertime % 60;
				      if (a == 0) {
				       int b = (60 - entertime) / 60;
						player.sendPackets(new S_SystemMessage("상아탑 사용 시간이 " + b
							      + "시간 남았습니다."));// 분
				      } else if ((60 - entertime) < 60) {
				       int c = 60 - entertime;
				       player.sendPackets(new S_ServerMessage(1527, "" + c
				         + ""));
				      }
				     }
				    } else if (action.equalsIgnoreCase("teleport ivorytower7")) {
				     RealTime time = RealTimeClock.getInstance().getRealTime();
				     int entertime = player.getTkddkdungeonTime() % 1000;
				     int enterday = player.getTkddkdungeonTime() / 1000;
				     int dayofyear = time.get(Calendar.DAY_OF_YEAR);

				     if (entertime > 60 && enterday == dayofyear) {
				    	 player.sendPackets(new S_SystemMessage("상아탑 사용 시간이 남아 있지 않습니다."));
				      htmlid = "";
				      return;
				     } else {
				      if (enterday < dayofyear)
				       player.setTkddkdungeonTime(dayofyear * 1000);
				      L1Teleport.teleport(player, 32815, 32868, (short) 81,
				        5, true);
				      player.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, (60 - entertime)*60));
				      /**안전모드**/
						player.getSkillEffectTimerSet().setSkillEffect(
								L1SkillId.ABSOLUTE_BARRIER, 5000);
						player.Safe_Teleport = true;
						player.sendPackets(new S_SystemMessage("안전 모드가 활성화 되었습니다."));
						/**안전모드**/
				      int a = entertime % 60;
				      if (a == 0) {
				       int b = (60 - entertime) / 60;
						player.sendPackets(new S_SystemMessage("상아탑 사용 시간이 " + b
							      + "시간 남았습니다."));// 분
				      } else if ((60 - entertime) < 60) {
				       int c = 60 - entertime;
				       player.sendPackets(new S_ServerMessage(1527, "" + c
				         + ""));
				      }
				     }
				} else if (action.equalsIgnoreCase("teleport ivoryTower")) { //적당한곳에 추가
					RealTime time = RealTimeClock.getInstance().getRealTime();
					int entertime = player.getTkddkdungeonTime() % 1000;
					int enterday = player.getTkddkdungeonTime() / 1000;
					int dayofyear = time.get(Calendar.DAY_OF_YEAR);

					if (entertime > 60 && enterday == dayofyear) {
						player.sendPackets(new S_SystemMessage("상아탑 사용 시간이 남아 있지 않습니다."));
						htmlid = "";
						return;
					} else {
						if (enterday < dayofyear)
							player.setTkddkdungeonTime(dayofyear * 1000);
						L1Teleport.teleport(player, 32898 , 32768, (short) 78, 5, true);
						player.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, (60 - entertime)*60));
						/**안전모드**/
						player.getSkillEffectTimerSet().setSkillEffect(
								L1SkillId.ABSOLUTE_BARRIER, 5000);
						player.Safe_Teleport = true;
						player.sendPackets(new S_SystemMessage("안전 모드가 활성화 되었습니다."));
						/**안전모드**/
						int a = entertime % 60;
						if (a == 0) {
							int b = (60 - entertime) / 60;
							player.sendPackets(new S_SystemMessage("상아탑 사용 시간이 " + b
								      + "시간 남았습니다."));// 분
						} else if ((60 - entertime) < 60) {
							int c = 60 - entertime;
							player.sendPackets(new S_ServerMessage(1527, "" + c
									+ ""));
						}
					} //추가

                                 /** 상아 리뉴얼 **/
				} else if (action.equalsIgnoreCase("teleport giranD")) { // 기란던전
					RealTime time = RealTimeClock.getInstance().getRealTime();
					int entertime = player.getGdungeonTime() % 1000;
					int enterday = player.getGdungeonTime() / 1000;
					int dayofyear = time.get(Calendar.DAY_OF_YEAR);

					if (entertime > 180 && enterday == dayofyear) {
						player.sendPackets(new S_ServerMessage(1522, "3"));// 3시간
						// 모두
						// 사용했다.
						htmlid = "";
						return;
					} else {
						if (enterday < dayofyear)
							player.setGdungeonTime(dayofyear * 1000);
						L1Teleport.teleport(player, 32806, 32735, (short) 53,5, true);
						player.sendPackets(new S_PacketBox(S_PacketBox.MAP_TIMER, (180 - entertime)*60));
						/**안전모드**/
						player.getSkillEffectTimerSet().setSkillEffect(
								L1SkillId.ABSOLUTE_BARRIER, 5000);
						player.Safe_Teleport = true;
						player.sendPackets(new S_SystemMessage("안전 모드가 활성화 되었습니다."));
						/**안전모드**/
						int a = entertime % 60;
						if (a == 0) {
							int b = (180 - entertime) / 60;
							player.sendPackets(new S_ServerMessage(1526, "" + b
									+ ""));// b 시간
							// 남았다.
						} else if ((180 - entertime) < 60) {
							int c = 180 - entertime;
							player.sendPackets(new S_ServerMessage(1527, "" + c
									+ ""));// 분
							// 남았다.
						}
					}
				}
			} catch (Exception e) {
			}
		}
		if (htmlid != null) { // 표시하는 html가 있는 경우
			player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
		}
	}

	class TeleportDelyTimer implements Runnable {

		public TeleportDelyTimer() {
		}

		public void run() {
			try {
				_isNowDely = true;
				Thread.sleep(900000); // 15분
			} catch (Exception e) {
				_isNowDely = false;
			}
			_isNowDely = false;
		}
	}

	private boolean _isNowDely = false;

	private static Logger _log = Logger
			.getLogger(l1j.server.server.model.Instance.L1TeleporterInstance.class
					.getName());

}