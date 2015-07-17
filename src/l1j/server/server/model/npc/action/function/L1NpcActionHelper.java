package l1j.server.server.model.npc.action.function;

import static l1j.server.server.model.skill.L1SkillId.ADVANCE_SPIRIT;
import static l1j.server.server.model.skill.L1SkillId.AQUA_PROTECTER;
import static l1j.server.server.model.skill.L1SkillId.CONCENTRATION;
import static l1j.server.server.model.skill.L1SkillId.EARTH_SKIN;
import static l1j.server.server.model.skill.L1SkillId.FIRE_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.HASTE;
import static l1j.server.server.model.skill.L1SkillId.INSIGHT;
import static l1j.server.server.model.skill.L1SkillId.PATIENCE;
import static l1j.server.server.model.skill.L1SkillId.SHINING_AURA;

import java.util.Random;

import l1j.server.server.Opcodes;
import l1j.server.server.TimeController.InDunController;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1NpcActionHelper {
	private Random _random = new Random(System.nanoTime());
	
	private static L1NpcActionHelper _instance;
	
	public static L1NpcActionHelper getInstance() {
		if (_instance == null) {
			_instance = new L1NpcActionHelper();
		}
		return _instance;
	}
	
	public String NpcAction(L1PcInstance pc,  L1Object obj, String s, String htmlid) {
		try {	
				/** 베테르랑 레벨 선물지급 */
			 if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7000196) { // 베테르랑
				if (s.equalsIgnoreCase("7")) { // 1. 저는 30레벨이 되었습니다
					if (pc.getLevel() >= 30) { // pc의 레벨이 30 이상이라면
						if (pc.getQuest().get_step(L1Quest.QUEST_VETERAN) == 0) { // 처음 받는거라면
							pc.getInventory().storeItem(5000287, 1); // 수련기사 격려 패키지 지급
							pc.sendPackets(new S_SystemMessage("수련기사 격려 패키지를 얻었습니다."));
							pc.getQuest().set_step(L1Quest.QUEST_VETERAN, 1);
							htmlid = "veteranR1"; // 수련기사 격려 패키지를 지급해 드렸습니다.
						} else { // 한번 받았다면
							htmlid = "veteranR4"; // 수련기사의 선물을 이미 지급받으셨습니다
						}
					} else { // pc의 레벨이 30 미만이라면
						htmlid = "veteranR5"; // 지금은 수련기사의 선물을 드릴 수 없습니다.
					}
				}

				if (s.equalsIgnoreCase("8")) { // 1. 저는 40레벨이 되었습니다
					if (pc.getLevel() >= 40) { // pc의 레벨이 40 이상이라면
						if (pc.getQuest().get_step(L1Quest.QUEST_VETERAN) == 1) { // 처음받는거라면
							pc.getInventory().storeItem(5000288, 1); // 수련기사 응원 패키지 지급
							pc.sendPackets(new S_SystemMessage("수련기사 응원 패키지를 얻었습니다."));
							pc.getQuest().set_step(L1Quest.QUEST_VETERAN, 2);
							htmlid = "veteranR2"; // 수련기사 응원 패키지를 지급해 드렸습니다.
						} else { // 한번 받았다면
							htmlid = "veteranR4"; // 수련기사의 선물을 이미 지급받으셨습니다
						}
					} else { // pc의 레벨이 40 미만이라면
						htmlid = "veteranR5"; // 지금은 수련기사의 선물을 드릴 수 없습니다.
					}
				}

				if (s.equalsIgnoreCase("9")) { // 1. 저는 45레벨이 되었습니다
					if (pc.getLevel() >= 45) { // pc의 레벨이 40 이상이라면
						if (pc.getQuest().get_step(L1Quest.QUEST_VETERAN) == 2) { // 처음 받는거라면
							pc.getInventory().storeItem(5000289, 1); // 수련기사 졸업 선물지급
							pc.sendPackets(new S_SystemMessage("수련기사 졸업 선물를 얻었습니다."));
							pc.getQuest().set_step(L1Quest.QUEST_VETERAN,L1Quest.QUEST_END);
							htmlid = "veteranR3"; // 수련기사 졸업 선물을 지급해 드렸습니다.
						} else { // 한번 받았다면
							htmlid = "veteranR6"; // 수련기사의 선물을 모두 지급받으셨습니다.
						}
					} else { // pc의 레벨이 40 미만이라면
						htmlid = "veteranR5"; // 지금은 수련기사의 선물을 드릴 수 없습니다.
					}
				}

				// 무기를 아이템으로
				if (s.equalsIgnoreCase("1") || s.equalsIgnoreCase("2")
						|| s.equalsIgnoreCase("3")) {
					int[] veteranWeapon = { 303, 304, 305, 306, 307, 308, 309,
							310 };
					for (int i = 0; i < veteranWeapon.length; i++) {
						if (pc.getInventory().checkItemNotEquipped(
								veteranWeapon[i], 1)) {
							pc.getInventory().consumeItem(veteranWeapon[i], 1);
							if (s.equalsIgnoreCase("1")) {
								pc.getInventory().storeItem(5000081, 200); // 포도주스20개생성
								pc.sendPackets(new S_ServerMessage(403,	"난쟁이 포도 주스 (200)"));
							} else if (s.equalsIgnoreCase("2")) {
								pc.getInventory().storeItem(40087, 1); // 무기 마법주문서
								pc.sendPackets(new S_ServerMessage(403,	"무기 마법 주문서"));
							} else if (s.equalsIgnoreCase("3")) {
								pc.getInventory().storeItem(40074, 1); // 갑옷 마법 주문서
								pc.sendPackets(new S_ServerMessage(403,	"갑옷 마법 주문서"));
							}
							htmlid = "veteranE3"; // 물약 또는 장비 강화 마법 주문서로교환받으셨습니다.
						} else {
							// htmlid = "veteranE4"; // 교환하실 장비가 없습니다. 혹 창고에 있는
							// 것은
							// 아닌지요?
						}
					}
				}

				// 방어구를 아이템으로
				if (s.equalsIgnoreCase("4") || s.equalsIgnoreCase("5")
						|| s.equalsIgnoreCase("6")) {
					int[] veteranArmor = { 500031, 500032, 500033, 500034,
							500035, 500036, 500037, 500038 };
					for (int i = 0; i < veteranArmor.length; i++) {
						if (pc.getInventory().checkItemNotEquipped(
								veteranArmor[i], 1)) {
							pc.getInventory().consumeItem(veteranArmor[i], 1);
							if (s.equalsIgnoreCase("4")) {
								pc.getInventory().storeItem(5000081, 200); // 포도주스20개생성
								pc.sendPackets(new S_ServerMessage(403,	"난쟁이 포도 주스 (200)"));
							} else if (s.equalsIgnoreCase("5")) {
								pc.getInventory().storeItem(40087, 1); // 무기 마법주문서
								pc.sendPackets(new S_ServerMessage(403,	"무기 마법 주문서"));
							} else if (s.equalsIgnoreCase("6")) {
								pc.getInventory().storeItem(40074, 1); // 갑옷 마법주문서
								pc.sendPackets(new S_ServerMessage(403,	"갑옷 마법 주문서"));
							}
							htmlid = "veteranE3"; // 물약 또는 장비 강화 마법 주문서로 교환받으셨습니다.
						} else {
							// htmlid = "veteranE4"; // 교환하실 장비가 없습니다. 혹 창고에 있는
							// 것은
							// 아닌지요?
						}
					}
				}
				
				
			
			
					
				
				
				
				
			   	//일일퀘스트 토벌대원 
			  } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 401060) {

				   if (s.equalsIgnoreCase("A")){
				    if (!pc.getInventory().checkItem(5438)){ //토벌대원 주머니 없으면~
				    pc.getInventory().storeItem(5438, 1);//토벌대원 주머니 
				    htmlid = "highdaily4"; 
				    pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "토벌대원 을 찾아 가시면 퀘스트를 진행 하실 수 있습니다."));
				    } else {
				    htmlid = "highdaily20";	
				    }
			    	} else if (s.equalsIgnoreCase("B")){
				    if(pc.getInventory().checkItem(5437)) {//빛나는 구슬
				     htmlid ="highdaily5";
				    	} else {
				     htmlid ="highdaily10";
				    }
			    	} else if (s.equalsIgnoreCase("C")){
			    		
			    		
			    		if (pc.getInventory().checkItem(5439, 100)){//토벌의 증표
				     while(pc.getInventory().consumeItem(5439, 1)); //토벌의 증표
				     while(pc.getInventory().consumeItem(5437, 1)); //빛나는 구슬
				        
				     Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 3944));
					     pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
				     int[] allBuffSkill = { HASTE, ADVANCE_SPIRIT, EARTH_SKIN, AQUA_PROTECTER, CONCENTRATION, PATIENCE, INSIGHT, SHINING_AURA, FIRE_WEAPON };
				     pc.setBuffnoch(1);
				     L1SkillUse l1skilluse = new L1SkillUse();
				     for (int i = 0; i < allBuffSkill.length; i++) {
				     l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				     }
				     pc.getInventory().storeItem(437011, 3); //지급
				     pc.getInventory().storeItem(40308, 5000000); //지급
				     pc.sendPackets(new S_SystemMessage("보상:드래곤진주(3)개 얻었습니다."));
				     pc.sendPackets(new S_SystemMessage("보상:아데나(5백만)을 얻었습니다."));
				     
				     
				     pc.save();
				     htmlid = "highdaily7";
				    } else {
				     htmlid = "highdaily31";
				    }
			    	} else if (s.equalsIgnoreCase("D")){
				    if (pc.getInventory().checkItem(5438, 1)){//토벌대원의주머니
				     pc.getInventory().consumeItem(5438, 1);
				     htmlid ="highdaily8";
				     }
				    }	
				   
				    /** 드래곤 뼈 수집꾼 */
					  } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 401240) {
						   int oldLevel = pc.getLevel();
						   int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
						   int exp = 0;
						   if (s.equalsIgnoreCase("A")) {
						   if (!pc.getInventory().checkItem(5455)) {//드래곤뼈 수집꾼의 주머니
						     pc.getInventory().storeItem(5455, 1);
						     htmlid = "highdailyb4";
						     pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "드래곤뼈 수집꾼 을 찾아 가시면 퀘스트를 진행 하실 수 있습니다."));
					 } else {
							 htmlid = "highdailyb20";	
							 }
				     } else if (s.equalsIgnoreCase("B")) {
						    if (pc.getInventory().checkItem(5456)) { // 영롱한 구슬
						        htmlid = "highdailyb5";
					    } else {
						        htmlid = "highdailyb10";
						    }
					 } else if (s.equalsIgnoreCase("C")) {
						    if (pc.getInventory().checkItem(5451, 100)) { // 드래곤 뼈
						     while (pc.getInventory().consumeItem(5451, 1));
						     while (pc.getInventory().consumeItem(5456, 1)); // 영롱한 구슬
						     exp = (int) (needExp * 0.90);
						     pc.addExp(exp);
						     Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 3944));
						     pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
						     int[] allBuffSkill = { HASTE, ADVANCE_SPIRIT, EARTH_SKIN, AQUA_PROTECTER, CONCENTRATION, PATIENCE, INSIGHT, SHINING_AURA, FIRE_WEAPON };
						     pc.setBuffnoch(1);
						     L1SkillUse l1skilluse = new L1SkillUse();
						     for (int i = 0; i < allBuffSkill.length; i++) {
						      l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
						      }
						      pc.save();
						      htmlid = "highdailyb7";
					  } else {
						      htmlid = "highdailyb31";
						    }
				   } else if (s.equalsIgnoreCase("D")) {
						    if (pc.getInventory().checkItem(5455, 1)) {
						          pc.getInventory().consumeItem(5455, 1);
						          htmlid = "highdailyb8";
						          }
						     }
						 //피도르 ( 수상한 하늘정원 )
					  } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 40199) {
								// 불꽃의 마법을 받는다 ( 처리부분 )
								if (s.equalsIgnoreCase("0")) { // 뒤로?
									htmlid = "";
								} else if (s.equalsIgnoreCase("a")) {
									if (pc.getInventory().checkItem(41159) == true) {
										if (pc.getLevel() >= 5) {
											pc.getInventory().consumeItem(41159, 10);
											int[] allBuffSkill = { 43, 79, 151, 160, 206, 216, 211, 115, 107, 148 };
											pc.setBuffnoch(1);
											L1SkillUse l1skilluse = new L1SkillUse();
											for (int i = 0; i < allBuffSkill.length; i++) {
											l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
											}
											htmlid = "";
										} else {
											pc.sendPackets(new S_SystemMessage("5레벨 이상부터 사용 가능합니다."));
										}
										//바람의 마법을 받는다 (처리부분)
									} else if (pc.getInventory().checkItem(41159) == true) {
										if (pc.getLevel() >= 5) {
											pc.getInventory().consumeItem(41159, 10);
											int[] allBuffSkill = { 43, 79, 151, 160, 206, 216, 211, 115, 148 };
											pc.setBuffnoch(1);
											L1SkillUse l1skilluse = new L1SkillUse();
											for (int i = 0; i < allBuffSkill.length; i++) {
												l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
											}
											htmlid = "";
										} else {
											pc.sendPackets(new S_SystemMessage("5레벨 이상부터 사용 가능합니다."));
										}
										//끝
									} else {
										pc.sendPackets(new S_SystemMessage("픽시의깃털이 부족합니다."));
									}
								}
								//영혼의 마법을 받는다 ( 처리부분)
						//	} else if (pc.getInventory().checkItem(41159) == true) {
								if (s.equalsIgnoreCase("b")) {
									if (pc.getInventory().checkItem(41159, 10)) {
										if (pc.getLevel() >= 5) {
											pc.getInventory().consumeItem(41159, 10);
											int[] allBuffSkill = { 43, 79, 151, 160, 206, 216, 211, 115, 149 };
											pc.setBuffnoch(1);
											L1SkillUse l1skilluse = new L1SkillUse();
											for (int i = 0; i < allBuffSkill.length; i++) {
												l1skilluse.handleCommands(pc, allBuffSkill[i],
														pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
											}
											htmlid = "";
										} else {
											pc.sendPackets(new S_SystemMessage("5레벨 이상부터 사용 가능합니다."));
										}
									} else {
										pc.sendPackets(new S_SystemMessage("픽시의깃털이 부족합니다."));
									}
								}
								
								/*** 하늘정원 지구르 ***/
						} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 402030) {
							L1NpcInstance npc = (L1NpcInstance) obj;
								if (pc.getInventory().checkItem(41159, 50) && pc.getInventory().checkItem(5565, 100)) {
									pc.getInventory().consumeItem(41159, 50);//깃털삭제
									pc.getInventory().consumeItem(5565, 100);//영혼삭제
									pc.getInventory().storeItem(46446, 1);//선물주기
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"빛나는 픽시 상자를 얻었습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
								} else {
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"픽시의깃털 또는 영혼이 부족합니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
									pc.sendPackets(new S_NpcChatPacket(npc,"상점 준비중이에유! 나중에오시면 싸게드릴게유!", 0));
								}
			 
								/*** 하늘정원 수상한 방문자 ***/
						} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4200017) {
							L1NpcInstance npc = (L1NpcInstance) obj;
							if (s.equalsIgnoreCase("0")) { //양말 1개와교환
							 	if (pc.getInventory().checkItem(5565, 100)) {
									pc.getInventory().consumeItem(5565, 100);
									pc.getInventory().storeItem(65481, 1);
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"당신은 아데나를 소비하고, 선물을 받았습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
									htmlid = "sustranger4"; // 거래 완료 액션
								} else {
									htmlid = "sustranger5"; // 재료가 부족시 액션
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"당신에게는 '픽시의 영혼'이 부족합니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
									pc.sendPackets(new S_NpcChatPacket(npc,"상점 준비중이에유! 나중에오시면 싸게드릴게유!", 0));
								}
							}
								
								/*** 게렝 수제자 ***/
						} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 76000) {
							if (s.equalsIgnoreCase("0")) { // 게렝의행운의 물약 1개 교환
								if (pc.getInventory().checkItem(5000061, 1)	&& pc.getInventory().checkItem(437017, 2)) {
									pc.getInventory().consumeItem(5000061, 1);
									pc.getInventory().consumeItem(437017, 2);
									pc.getInventory().storeItem(5000059, 1);
									pc.sendPackets(new S_SystemMessage("게렝의 전투의 물약을 얻었습니다."));
									htmlid = "gerengfoll2"; // 거래 완료 액션
								} else {
									htmlid = "gerengfoll3"; // 재료가 부족시 액션
								}
							}
							if (s.equalsIgnoreCase("1")) { // 게렝의 행운의 물약 10개 교환
								if (pc.getInventory().checkItem(5000061, 10) && pc.getInventory().checkItem(437017, 20)) {
									pc.getInventory().consumeItem(5000061, 10);
									pc.getInventory().consumeItem(437017, 20);
									pc.getInventory().storeItem(5000059, 10);
									pc.sendPackets(new S_SystemMessage("게렝의 전투의 물약 (10)개를 얻었습니다."));
									htmlid = "gerengfoll2"; // 거래 완료 액션
								} else {
									htmlid = "gerengfoll3"; // 재료가 부족시 액션
								}
							}
							if (s.equalsIgnoreCase("2")) { // 게렝의행운의 물약 1개 교환
								if (pc.getInventory().checkItem(5000062, 1)	&& pc.getInventory().checkItem(437017, 2)) {
									pc.getInventory().consumeItem(5000062, 1);
									pc.getInventory().consumeItem(437017, 2);
									pc.getInventory().storeItem(5000060, 1);
									pc.sendPackets(new S_SystemMessage("게렝의 행운의 물약을 얻었습니다."));
									htmlid = "gerengfoll2"; // 거래 완료 액션
								} else {
									htmlid = "gerengfoll3"; // 재료가 부족시 액션
								}
							}
							if (s.equalsIgnoreCase("3")) { // 게렝의 행운의 물약 10개 교환
								if (pc.getInventory().checkItem(5000062, 10) && pc.getInventory().checkItem(437017, 20)) {
									pc.getInventory().consumeItem(5000062, 10);
									pc.getInventory().consumeItem(437017, 20);
									pc.getInventory().storeItem(5000060, 10);
									pc.sendPackets(new S_SystemMessage("게렝의 행운의 물약 (10)개를 얻었습니다."));
									htmlid = "gerengfoll2"; // 거래 완료 액션
								} else {
									htmlid = "gerengfoll3"; // 재료가 부족시 액션
								}
							}
							
								/** 오크 전초기지 텔레포터 **/
						} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 170823) {
							if (s.equalsIgnoreCase("a")) {
								if (pc.getLevel() >= 45) {//45이상
									L1Teleport.teleport(pc, 32750, 32892, (short) 261, 5, true,
											false);
									htmlid = "";
								} else {
									htmlid = "islandev2";
								}
							
						} else if (s.equalsIgnoreCase("b")) {
							if (pc.getLevel() >= 45) {//45이상
								L1Teleport.teleport(pc, 32750, 32892, (short) 263, 5, true,
										false);
								htmlid = "";
							} else {
								htmlid = "islandev2";
							}
						} else if (s.equalsIgnoreCase("c")) {
							if (pc.getLevel() >= 45) {//45이상
								L1Teleport.teleport(pc, 32750, 32892, (short) 264, 5, true,
										false);
								htmlid = "";
							} else {
								htmlid = "islandev2";
							}
						} else if (s.equalsIgnoreCase("d")) {
							if (pc.getLevel() >= 45) {//45이상
								L1Teleport.teleport(pc, 32750, 32892, (short) 265, 5, true,
										false);
								htmlid = "";
							} else {
								htmlid = "islandev2";
							}
						} else if (s.equalsIgnoreCase("e")) {
							if (pc.getLevel() >= 45) {//45이상
								L1Teleport.teleport(pc, 32750, 32892, (short) 266, 5, true,
										false);
								htmlid = "";
							} else {
								htmlid = "islandev2";
							}
						} else if (s.equalsIgnoreCase("f")) {
							if (pc.getLevel() >= 45) {//45이상
								L1Teleport.teleport(pc, 32750, 32892, (short) 268, 5, true,
										false);
								htmlid = "";
							} else {
								htmlid = "islandev2";
							}
						} else if (s.equalsIgnoreCase("g")) {
							if (pc.getLevel() >= 45) {//45이상
								L1Teleport.teleport(pc, 32750, 32892, (short) 269, 5, true,
										false);
								htmlid = "";
							} else {
								htmlid = "islandev2";
							}
						} else if (s.equalsIgnoreCase("h")) {
							if (pc.getLevel() >= 45) {//45이상
								L1Teleport.teleport(pc, 32750, 32892, (short) 279, 5, true,
										false);
								htmlid = "";
							} else {
								htmlid = "islandev2";
							}
						}/** 여기까지! **/

							// 경험치 지급단
						} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4200008) {
							if (s.equalsIgnoreCase("0")) {
								if (pc.getLevel() < 51) {
									pc.addExp((ExpTable.getExpByLevel(52) - 1) - pc.getExp()
											- ((ExpTable.getExpByLevel(52) - 1) / 100));
								} else if (pc.getLevel() >= 51 && pc.getLevel() < 70) {
									pc.addExp((ExpTable.getExpByLevel(pc.getLevel() + 2) - 1)
											- pc.getExp() - 100);
									pc.setCurrentHp(pc.getMaxHp());
									pc.setCurrentMp(pc.getMaxMp());
								}
								if (ExpTable.getLevelByExp(pc.getExp()) >= 70) {
									htmlid = "expgive3";
								} else {
									htmlid = "expgive1";
								}
							}
						} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4200088) {
							if (s.equalsIgnoreCase("0")) {
								if (pc.getLevel() < 51) {
									pc.addExp((ExpTable.getExpByLevel(52) - 1) - pc.getExp()
											- ((ExpTable.getExpByLevel(52) - 1) / 100));
								} else if (pc.getLevel() >= 51 && pc.getLevel() < 85) {
									pc.addExp((ExpTable.getExpByLevel(pc.getLevel() + 2) - 1)
											- pc.getExp() - 100);
									pc.setCurrentHp(pc.getMaxHp());
									pc.setCurrentMp(pc.getMaxMp());
								}
								if (ExpTable.getLevelByExp(pc.getExp()) >= 85) {
									htmlid = "expgive3";
								} else {
									htmlid = "expgive1";
								}
							}
							// 드루가 베일
						} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4212001) {
							L1ItemInstance item = null;
							if (s.equalsIgnoreCase("0")) {
								if (pc.getInventory().checkItem(L1ItemId.DRUGA_POKET)) { // 이미
									// 주머니가
									// 있다.
									htmlid = "veil3";
								} else if (!pc.getInventory().checkItem(L1ItemId.ADENA, 100000)) { // 100만
									// 아데나가
									// 없다.
									htmlid = "veil4";
								} else { // 주머니가 없고 100만 아데나가 있을때 (받을수 있는 조건이 된다)
									pc.getInventory().consumeItem(L1ItemId.ADENA, 100000);
									item = pc.getInventory().storeItem(L1ItemId.DRUGA_POKET, 1);
									pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
									htmlid = "veil7";
								}
							}
							
							// 크레이
						} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4212012) {
							if (s.equalsIgnoreCase("a")) {
								new L1SkillUse().handleCommands(pc, L1SkillId.BUFF_CRAY, pc
										.getId(), pc.getX(), pc.getY(), null, 0,
										L1SkillUse.TYPE_SPELLSC);
								htmlid = "grayknight2";
							}
							
							/*** 오크 산타 ***/
						} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 57002) {
							if (s.equalsIgnoreCase("1")) { //양말1000개와교환
								if (pc.getInventory().checkItem(65487, 1000)) {//양말체크
									pc.getInventory().consumeItem(65487, 1000);//양말삭제
									pc.getInventory().storeItem(6548, 10);//선물주기
								//	pc.sendPackets(new S_SystemMessage("양말(1000장)과 교환 하였습니다."));
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"크리스마스 양말 (1000장)과 교환되었습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
									htmlid = "santaorc01"; // 거래 완료 액션
								} else {
									htmlid = "santaorc03"; // 재료가 부족시 액션
								}
							}
							if (s.equalsIgnoreCase("2")) { //양말 500개와 교환
								if (pc.getInventory().checkItem(65487, 500)) {
									pc.getInventory().consumeItem(65487, 500);
									pc.getInventory().storeItem(6548, 7);
								//	pc.sendPackets(new S_SystemMessage("양말(500장)과 교환 하였습니다."));
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"크리스마스 양말 (500장)과 교환되었습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
									htmlid = "santaorc01"; // 거래 완료 액션
								} else {
									htmlid = "santaorc03"; // 재료가 부족시 액션
								}
							}
							if (s.equalsIgnoreCase("3")) { //양말 100개와 교환
								if (pc.getInventory().checkItem(65487, 100)) {
									pc.getInventory().consumeItem(65487, 100);
									pc.getInventory().storeItem(6548, 5);
								//	pc.sendPackets(new S_SystemMessage("양말(100장)과 교환 하였습니다."));
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"크리스마스 양말 (100장)과 교환되었습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
									htmlid = "santaorc01"; // 거래 완료 액션
								} else {
									htmlid = "santaorc03"; // 재료가 부족시 액션
								}
							}
							if (s.equalsIgnoreCase("4")) { //양말 50개와교환
								if (pc.getInventory().checkItem(65487, 50)) {
									pc.getInventory().consumeItem(65487, 50);
									pc.getInventory().storeItem(6548, 3);
							//		pc.sendPackets(new S_SystemMessage("양말(50장)과 교환 하였습니다."));
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"크리스마스 양말 (50장)과 교환되었습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
									htmlid = "santaorc01"; // 거래 완료 액션
								} else {
									htmlid = "santaorc03"; // 재료가 부족시 액션
								}
							}
							if (s.equalsIgnoreCase("5")) { //양말 10개와 교환
								if (pc.getInventory().checkItem(65487, 10)) {
									pc.getInventory().consumeItem(65487, 10);
									pc.getInventory().storeItem(6548, 3);
								//	pc.sendPackets(new S_SystemMessage("양말(10장)과 교환 하였습니다."));
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"크리스마스 양말 (10장)과 교환되었습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
									htmlid = "santaorc01"; // 거래 완료 액션
								} else {
									htmlid = "santaorc03"; // 재료가 부족시 액션
								}
							}
							if (s.equalsIgnoreCase("6")) { //양말 1개와교환
								if (pc.getInventory().checkItem(65487, 1)) {
									pc.getInventory().consumeItem(65487, 1);
									pc.getInventory().storeItem(6548, 1);
								//	pc.sendPackets(new S_SystemMessage("양말(50장)과 교환 하였습니다."));
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"크리스마스 양말 (1장)과 교환되었습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
									htmlid = "santaorc01"; // 거래 완료 액션
								} else {
									htmlid = "santaorc03"; // 재료가 부족시 액션
								}
							}
							if (s.equalsIgnoreCase("b")) { //오크산타 버프관련
										if (pc.getInventory().checkItem(40308, 0)) {
										if (pc.getLevel() >= 5) {
											pc.getInventory().consumeItem(40308, 0);
											int[] allBuffSkill = { 43, 79, 151, 160, 206, 216, 211, 115, 107, 148 };//이거두 파이어웨폰 중복이네여..
											pc.setBuffnoch(1);
											L1SkillUse l1skilluse = new L1SkillUse();
											for (int i = 0; i < allBuffSkill.length; i++) {
												l1skilluse.handleCommands(pc, allBuffSkill[i],
														pc.getId(), pc.getX(), pc.getY(), null, 0,
														L1SkillUse.TYPE_GMBUFF);
											}
											S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"당신에게 '오크산타'가 버프를 제공합니다.", Opcodes.S_OPCODE_MSG, 20); 
											pc.sendPackets(s_chatpacket);
											htmlid = "santaorc01"; // 거래 완료 액션
										} else {			
										S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"당신은 Lv.5 이하 입니다.", Opcodes.S_OPCODE_MSG, 20); 
										pc.sendPackets(s_chatpacket);
										}
								} else {
									htmlid = "santaorc03"; // 재료가 부족시 액션
								}
							}
							
							/** 성혈버프사 **/
						} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7000065) {
								if (s.equalsIgnoreCase("a")) { //근거리버프
									if (pc.getInventory().checkItem(40308, 0)) {
									if (pc.getLevel() >= 5) {
										pc.getInventory().consumeItem(40308, 0);
										int[] allBuffSkill = { 43, 79, 151, 160, 206, 216, 211,115, 107, 148 };
										pc.setBuffnoch(1);
										L1SkillUse l1skilluse = new L1SkillUse();
										for (int i = 0; i < allBuffSkill.length; i++) {
										l1skilluse.handleCommands(pc, allBuffSkill[i],pc.getId(), pc.getX(), pc.getY(), null, 0,
													L1SkillUse.TYPE_GMBUFF);
										}
										htmlid = "";
									} else {
										S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"당신은 Lv.5 이하 입니다.", Opcodes.S_OPCODE_MSG, 20); 
										pc.sendPackets(s_chatpacket);
									}					
									} else {
										//pc.sendPackets(new S_ServerMessage(189));
										S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아데나가 충분치 않습니다.", Opcodes.S_OPCODE_MSG, 20); 
										pc.sendPackets(s_chatpacket);
										
									}
							}
							if (s.equalsIgnoreCase("b")) { //원거리버프
								if (pc.getInventory().checkItem(40308, 0)) {
									if (pc.getLevel() >= 5) {
										pc.getInventory().consumeItem(40308, 0);
										int[] allBuffSkill = { 43, 79, 151, 160, 206, 216, 211, 115, 149 };
										pc.setBuffnoch(1);
										L1SkillUse l1skilluse = new L1SkillUse();
										for (int i = 0; i < allBuffSkill.length; i++) {
											l1skilluse.handleCommands(pc, allBuffSkill[i],
													pc.getId(), pc.getX(), pc.getY(), null, 0,
													L1SkillUse.TYPE_GMBUFF);
										}
										htmlid = "";
									} else {
										S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"당신은 Lv.5 이하 입니다.", Opcodes.S_OPCODE_MSG, 20); 
										pc.sendPackets(s_chatpacket);
									}
								} else {
									//pc.sendPackets(new S_ServerMessage(189));
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아데나가 충분치 않습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
								}
							}
															
				/** 초보자 도우미 추가 - SRC팀 본섭화 **/
			} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 705141) {
					if (s.equalsIgnoreCase("D")) {
						if (pc.getLevel() == 2) {
							htmlid = "tutorm1_bs";
						} else if (pc.isWizard()) {
							if (pc.getLevel() <= 3) {
								htmlid = "tutorm1_bs";
							} else if (pc.getLevel() <= 5) {
								htmlid = "tutorm2_bs";
							} else if (pc.getLevel() <= 7) {
								htmlid = "tutorm3_bs";
							} else if (pc.getLevel() <= 11) {
								htmlid = "tutorm4_bs";
							} else if (pc.getLevel() <= 12) {
								htmlid = "tutorm5_bs";
							}
						} else if (pc.isCrown()) {
							if (pc.getLevel() <= 4) {
								htmlid = "tutorp1_bs";
							} else if (pc.getLevel() <= 7) {
								htmlid = "tutorp2_bs";
							} else if (pc.getLevel() <= 9) {
								htmlid = "tutorp3_bs";
							} else if (pc.getLevel() <= 11) {
								htmlid = "tutorp4_bs";
							} else if (pc.getLevel() <= 12) {
								htmlid = "tutorp5_bs";
							}
						} else if (pc.isElf()) {
							if (pc.getLevel() <= 4) {
								htmlid = "tutore1_bs";
							} else if (pc.getLevel() <= 7) {
								htmlid = "tutore2_bs";
							} else if (pc.getLevel() == 8) {
								htmlid = "tutore3_bs";
							} else if (pc.getLevel() == 9) {
								htmlid = "tutore4_bs";
							} else if (pc.getLevel() <= 11) {
								htmlid = "tutore5_bs";
							} else if (pc.getLevel() == 12) {
								htmlid = "tutore6_bs";
							}
						} else if (pc.isDarkelf()) {
							if (pc.getLevel() <= 4) {
								htmlid = "tutord1_bs";
							} else if (pc.getLevel() <= 6) {
								htmlid = "tutord2_bs";
							} else if (pc.getLevel() <= 8) {
								htmlid = "tutord3_bs";
							} else if (pc.getLevel() <= 10) {
								htmlid = "tutord4_bs";
							} else if (pc.getLevel() == 12) {
								htmlid = "tutord5_bs";
							}
						} else if (pc.isIllusionist()) {
							if (pc.getLevel() <= 4) {
								htmlid = "tutori1_bs";
							} else if (pc.getLevel() <= 6) {
								htmlid = "tutori2_bs";
							} else if (pc.getLevel() <= 9) {
								htmlid = "tutori3_bs";
							} else if (pc.getLevel() <= 11) {
								htmlid = "tutori4_bs";
							} else if (pc.getLevel() == 12) {
								htmlid = "tutori5_bs";
							}
						} else {
							if (pc.getLevel() <= 4) {
								htmlid = "tutordk1_bs";
							} else if (pc.getLevel() <= 6) {
								htmlid = "tutordk2_bs";
							} else if (pc.getLevel() <= 8) {
								htmlid = "tutordk3_bs";
							} else if (pc.getLevel() <= 10) {
								htmlid = "tutordk4_bs";
							} else if (pc.getLevel() <= 12) {
								htmlid = "tutordk5_bs";
							}
						}
					} else if (s.equalsIgnoreCase("O")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32605, 32837, (short) 2005, 4, true); // 마을서쪽근교
					} else if (s.equalsIgnoreCase("P")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32733, 32902, (short) 2005, 4, true); // 마을동쪽근교
					} else if (s.equalsIgnoreCase("Q")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32559, 32843, (short) 2005, 4, true); // 마을남서쪽사냥터
					} else if (s.equalsIgnoreCase("R")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32677, 32982, (short) 2005, 4, true); // 마을남동쪽 사냥터
					} else if (s.equalsIgnoreCase("S")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32781, 32854, (short) 2005, 4, true); // 마을 북동쪽 사냥터
					} else if (s.equalsIgnoreCase("T")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32674, 32739, (short) 2005, 4, true); // 마을북서쪽 사냥터
					} else if (s.equalsIgnoreCase("U")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32578, 32737, (short) 2005, 4, true); // 마을서쪽 사냥터
					} else if (s.equalsIgnoreCase("V")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32542, 32996, (short) 2005, 4, true); // 마을 남쪽사냥터
					} else if (s.equalsIgnoreCase("W")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32794, 32973, (short) 2005, 4, true); // 마을 동쪽 사냥터
					} else if (s.equalsIgnoreCase("X")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32803, 32789, (short) 2005, 4, true); // 마을 북쪽

					} else if (s.equalsIgnoreCase("L")) { // 상아탑 정령 배우기
						if (pc.getLevel() < 3) {
							htmlid = "";
						} else if (pc.getLevel() > 9 && pc.isElf()) {
							htmlid = "";
							pc.getInventory().storeItem(40101, 1);
							pc.sendPackets(new S_SystemMessage("초보자 편의 초보자 도우미가 당신에게 숨겨진 계곡 귀환 주문서를 주었습니다."));
							L1Teleport.teleport(pc, 34041, 32155, (short) 4, 4,	true);
						}
					} else if (s.equalsIgnoreCase("M")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32878, 32905, (short) 304, 4, true);
						pc.getInventory().storeItem(40101, 1);
						pc.sendPackets(new S_SystemMessage("초보자 편의 초보자 도우미가 당신에게 숨겨진 계곡 귀환 주문서를 주었습니다."));
					} else if (s.equalsIgnoreCase("I")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32635, 33182, (short) 4, 4, true); // 혈맹
						pc.getInventory().storeItem(40101, 1);
						pc.sendPackets(new S_SystemMessage("초보자 편의 초보자 도우미가 당신에게 숨겨진 계곡 귀환 주문서를 주었습니다."));
					} else if (s.equalsIgnoreCase("N")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32760, 32885, (short) 1000, 4, true);
						pc.getInventory().storeItem(40101, 1);
						pc.sendPackets(new S_SystemMessage("초보자 편의 초보자 도우미가 당신에게 숨겨진 계곡 귀환 주문서를 주었습니다."));
					} else if (s.equalsIgnoreCase("H")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32575, 32944, (short) 0, 4, true); // 말하는섬창고
						pc.getInventory().storeItem(40101, 1);
						pc.sendPackets(new S_SystemMessage("초보자 편의 초보자 도우미가 당신에게 숨겨진 계곡 귀환 주문서를 주었습니다."));
					} else if (s.equalsIgnoreCase("K")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32585, 32956, (short) 0, 4, true); // 게렝
						pc.getInventory().storeItem(40101, 1);
						pc.sendPackets(new S_SystemMessage("초보자 편의 초보자 도우미가 당신에게 숨겨진 계곡 귀환 주문서를 주었습니다."));
					} else if (s.equalsIgnoreCase("J")) {
						htmlid = "";
						L1Teleport.teleport(pc, 32675, 32813, (short) 2005, 4, true); // 숨계던젼
						pc.getInventory().storeItem(40101, 1);
						pc.sendPackets(new S_SystemMessage("초보자 편의 초보자 도우미가 당신에게 숨겨진 계곡 귀환 주문서를 주었습니다."));
					}
					
					/** 수련장 관리인 추가 - SRC팀 본섭화 **/
				} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 710141) {
					if (s.equalsIgnoreCase("A")) {
						pc.setExp(pc.getExp() + 546);
						/** [본섭화를 위해 소스 새로 변경] 멘트가 달라서 이렇게 수정 **/
						pc.getInventory().storeItem(20028, 1); // 미작업 차이?
						pc.sendPackets(new S_SystemMessage("초보자 편의 수련장 관리인이 당신에게 상아탑의 가죽 투구를 주었습니다."));
						pc.getInventory().storeItem(20126, 1);
						pc.sendPackets(new S_SystemMessage("초보자 편의 수련장 관리인이 당신에게 상아탑의 가죽 갑옷을 주었습니다."));
						pc.getInventory().storeItem(20206, 1);
						pc.sendPackets(new S_SystemMessage("초보자 편의 수련장 관리인이 당신에게 상아탑의 가죽 샌달을 주었습니다."));
						pc.getInventory().storeItem(20173, 1);
						pc.sendPackets(new S_SystemMessage("초보자 편의 수련장 관리인이 당신에게 상아탑의 가죽 장갑을 주었습니다."));
						pc.getInventory().storeItem(20232, 1);
						pc.sendPackets(new S_SystemMessage("초보자 편의 수련장 관리인이 당신에게 상아탑의 가죽 방패를 주었습니다."));
						int[] item_ids = { 40099, 40101, 40098, 40029, 40030, }; // 스레드작업
						int[] item_amounts = { 30, 5, 20, 50, 5, };
						for (int i = 0; i < item_ids.length; i++) {
							L1ItemInstance item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
							pc.sendPackets(new S_ServerMessage(143, "$8447", item.getItem().getName()+ "("+ item_amounts[i] + ")"));
						}
						htmlid = "";
					}			
						
				/** 해상전 입장관련 */
				}else if(((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 460000040) {
				if(s.equalsIgnoreCase("a")) {
				if (pc.getInventory().checkItem(40308, 5000000)){ //입장비용 천만아데나
					if (pc.getLevel() >= 60) { //파장 레벨 제한
						if (pc.isInParty() && pc.getParty().isLeader(pc)){
							if (pc.getParty().getNumOfMembers() >= 8){ //파티 멤버수
								pc.getInventory().consumeItem(40308, 5000000); //입장비용 아데나
								pc.getParty().getLeader().getName();
								L1Party party = pc.getParty();
								L1PcInstance[] players = party.getMembers();
								for(L1PcInstance pc1:players){
									InDunController.getInstance().addPlayMember(pc1);
									Random random = new Random(); 
									int i13 = 32799 + random.nextInt(3); 
									int k19 = 32808 + random.nextInt(3);
									L1Teleport.teleport(pc1, i13, k19, (short) 9101, 5, true);
								}
								htmlid = "";
							} else {
								pc.sendPackets(new S_SystemMessage("최소 인원은 8명 임니다."));
							}
						} else {
							pc.sendPackets(new S_SystemMessage("파티의 리더만이 안내를 할수 있습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("파티장 레벨이 60이상이 아닙니다."));
					}
				} else {
					pc.sendPackets(new S_SystemMessage("입장료가 부족합니다(오백만아데나)"));
				}
				}
									/** 유령선 입장관련 */
				}else if(((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 460000041) {
				if(s.equalsIgnoreCase("a")) {
				if (pc.getInventory().checkItem(40308, 5000000)){ //입장비용 천만아데나
					if (pc.getLevel() >= 60) { //파장 레벨 제한
						if (pc.isInParty() && pc.getParty().isLeader(pc)){
							if (pc.getParty().getNumOfMembers() >= 8){ //파티 멤버수
								pc.getInventory().consumeItem(40308, 5000000); //입장비용 아데나
								pc.getParty().getLeader().getName();
								L1Party party = pc.getParty();
								L1PcInstance[] players = party.getMembers();
								for(L1PcInstance pc1:players){
									InDunController.getInstance().addPlayMember(pc1);
									Random random = new Random(); 
									int i13 = 32796 + random.nextInt(3); 
									int k19 = 32802 + random.nextInt(3);
									L1Teleport.teleport(pc1, i13, k19, (short) 9102, 5, true);
								}
								htmlid = "";
							} else {
								pc.sendPackets(new S_SystemMessage("최소 인원은 8명 임니다."));
							}
						} else {
							pc.sendPackets(new S_SystemMessage("파티의 리더만이 안내를 할수 있습니다."));
						}
					} else {
						pc.sendPackets(new S_SystemMessage("파티장 레벨이 60이상이 아닙니다."));
					}
				} else {
					pc.sendPackets(new S_SystemMessage("입장료가 부족합니다(5백만아데나)"));
				}
				}
				
				} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 4039031) {
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (s.equalsIgnoreCase("a")) {
						if (pc.getInventory().checkItem(5000136, 1)) {
							htmlid = "birthday2";
						} else {
							pc.getInventory().storeItem(5000136, 1);
							htmlid = "birthday3";
						}
					}

					if (s.equalsIgnoreCase("b")) {
						if (pc.getInventory().checkItem(5000141, 1)) {
							pc.getInventory().consumeItem(5000141, 1);
							new L1SkillUse().handleCommands(pc,
								L1SkillId.STATUS_COMA_5, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
							htmlid = "birthday4";
						} else { // 재료가 부족한 경우
							pc.sendPackets(new S_NpcChatPacket(npc,"생일 버프 주머니가 필요합니다.", 0));
						}
					}
				
				//기초훈련교관
			 } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 40090) { 
	          	   if (s.equalsIgnoreCase("0")) { 
	          		   if (pc.getsub() == 0) {
	          			    pc.getInventory().storeItem(5445, 1); //교관의 선물 주머니
	          			    pc.getInventory().storeItem(427131, 1); //수련자의 벨트
							pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 $14171 를  주었습니다."));
							pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 $14187 를  주었습니다."));
	          	          if (pc.isCrown()) {
	          	        	 pc.getInventory().storeItem(40226, 1); //마법서 (트루 타겟)
	 						pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 마법서 (트루 타겟) 를  주었습니다."));
	                      } else if (pc.isElf()) {
	          	          pc.getInventory().storeItem(40233, 1); //정령의 수정 (바디 투 마인드)
	          	          pc.getInventory().storeItem(40234, 1);//정령의 수정 (텔레포트 투 마더)
							pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 정령의 수정 (바디 투 마인드) 를  주었습니다."));
	 						pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 정령의 수정 (텔레포트 투 마더) 를  주었습니다."));
	                      } else if (pc.isWizard()) {
	          	          pc.getInventory().storeItem(40188, 1); //마법서 (헤이스트)
	          	          pc.getInventory().storeItem(40176, 1); //마법서 (메디테이션)
							pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 마법서 (헤이스트) 를  주었습니다."));
	 						pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 마법서 (메디테이션) 를  주었습니다."));
	                      } else if (pc.isDarkelf()) {
	          	          pc.getInventory().storeItem(40268, 1);//흑정령의 수정 (브링 스톤)
							pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 흑정령의 수정 (브링 스톤) 를  주었습니다."));
	                      } else if (pc.isDragonknight()) {
	          	          pc.getInventory().storeItem(439100, 1); //용기사의 서판(드래곤 스킨)
	          	          pc.getInventory().storeItem(439106, 1); //용기사의 서판(포우 슬레이어)
	          	          pc.getInventory().storeItem(439101, 1);//용기사의 서판(버닝 슬래쉬)
							pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 용기사의 서판(드래곤 스킨)) 를  주었습니다."));
							pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 용기사의 서판(포우 슬레이어) 를  주었습니다."));
							pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 용기사의 서판(버닝 슬래쉬) 를  주었습니다."));
	                      } else if (pc.isIllusionist()) {
	          	          pc.getInventory().storeItem(439004, 1); //기억의 수정(큐브:이그니션)
	          	          pc.getInventory().storeItem(439000, 1); //기억의 수정(미러 이미지)
	          	          pc.getInventory().storeItem(439001, 1); //기억의 수정(컨퓨전)
							pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 기억의 수정(큐브:이그니션) 를  주었습니다.."));
							pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 기억의 수정(미러 이미지) 를  주었습니다."));
							pc.sendPackets(new S_SystemMessage("기초 훈련 교관 이 기억의 수정(컨퓨전) 를  주었습니다."));
	          	          }
	          	          pc.setsub(pc.getsub() + 1);  
	          	          pc.save();
	          	        htmlid = "hpass20";
	            } else {
	          	        htmlid = "";
	          	      }
	           } else if (s.equalsIgnoreCase("1")) {// LV:20
	        	    if (pc.getsub() == 1) {
	          	    if (pc.getInventory().checkItem(5440, 20)) { //몬스터의 발톱 check
	          	        while (pc.getInventory().consumeItem(5440, 1)); //몬스터의 발톱 delete
	          	      pc.getInventory().storeItem(5445, 1);//교관의 선물 주머니
	          	      pc.setsub(pc.getsub() + 1);
	          	      pc.save();
	          	       htmlid = "hpass9";
	           } else {
	          	       htmlid = "hpass20";
	          	      }
	           } else {
	          	       htmlid = "hpass20";
	          	      }
	           } else if (s.equalsIgnoreCase("2")) {// LV:25
	        	   if (pc.getsub() == 2) {
	          	    if (pc.getInventory().checkItem(5441, 20)) {// 몬스터의 이빨 check
	          	        while (pc.getInventory().consumeItem(5441, 1));//몬스터의 이빨 delete
	          	      pc.getInventory().storeItem(5445, 1); //교관의 선물 주머니
	          	      pc.setsub(pc.getsub() + 1);
	          	      pc.save();
	          	        htmlid = "hpass9";
	            } else {
	          	        htmlid = "hpass21";
	          	       }
	            } else {
	          	        htmlid = "hpass21";
	          	       }
	            } else if (s.equalsIgnoreCase("3")) {// LV:30
	            	 if (pc.getsub() == 3) {
	          	     if (pc.getInventory().checkItem(5442, 20)) { //녹슨투구 check
	          	         while (pc.getInventory().consumeItem(5442, 1));//녹슨투구 delete
	          	                pc.getInventory().storeItem(5445, 1); //교관의 선물 주머니
	          	          if (pc.isCrown()) { //군주
	          	              //  pc.getInventory().storeItem(40228, 1); //마법서(콜 클렌)
	        					final int[] item_ids = { 40228 };
	        					final int[] item_amounts = { 1 };					
	        					L1ItemInstance item = null;
	        					for (int i = 0; i < item_ids.length; i++) {
	        						item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
	        						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
	        				     }
	                      } else if (pc.isElf()) { //요정
	          	              //  pc.getInventory().storeItem(40243, 1); //정령의 수정 (서먼 레서 엘리멘탈)
	        					final int[] item_ids = { 40243 };
	        					final int[] item_amounts = { 1 };					
	        					L1ItemInstance item = null;
	        					for (int i = 0; i < item_ids.length; i++) {
	        						item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
	        						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
	        				     }
	                      } else if (pc.isDarkelf()) {
	          	             //   pc.getInventory().storeItem(40270, 1);//흑정령의 수정 (무빙 악셀레이션)
	        					final int[] item_ids = { 40270 };
	        					final int[] item_amounts = { 1 };					
	        					L1ItemInstance item = null;
	        					for (int i = 0; i < item_ids.length; i++) {
	        						item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
	        						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
	        				     }
	                      } else if (pc.isDragonknight()) {
	          	               // pc.getInventory().storeItem(439105, 1);//용기사의 서판(블러드러스트)
	        					final int[] item_ids = { 439105 };
	        					final int[] item_amounts = { 1 };					
	        					L1ItemInstance item = null;
	        					for (int i = 0; i < item_ids.length; i++) {
	        						item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
	        						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
	        				  }
	                      } else if (pc.isIllusionist()) {
	          	              //  pc.getInventory().storeItem(439014, 1);//기억의 수정(큐브:쇼크)
	        					final int[] item_ids = { 439014 };
	        					final int[] item_amounts = { 1 };					
	        					L1ItemInstance item = null;
	        					for (int i = 0; i < item_ids.length; i++) {
	        						item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
	        						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
	        				  }
	          	          }
	          	      pc.setsub(pc.getsub() + 1);
	          	      pc.save();
	          	      htmlid = "hpass9";
	            } else {
	          	      htmlid = "hpass22";
	          	     }
	            } else {
	          	      htmlid = "hpass22";
	          	     }
	            } else if (s.equalsIgnoreCase("4")) {// LV:35
	            	 if (pc.getsub() == 4) {
	          	     if (pc.getInventory().checkItem(5443, 20)) { //녹슨 장갑 
	          	         while (pc.getInventory().consumeItem(5443, 1));//녹슨 장갑 
	          	    //  pc.getInventory().storeItem(15445, 1);//교관의 선물 주머니
	          	  //    pc.getInventory().storeItem(520301, 1); //수련자의 목걸이 
						final int[] item_ids = { 5445, 427129 };
						final int[] item_amounts = { 1, 1 };					
						L1ItemInstance item = null;
						for (int i = 0; i < item_ids.length; i++) {
							item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
							pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
					  }
	          	      pc.setsub(pc.getsub() + 1);
	          	      pc.save();
	          	        htmlid = "hpass9";
	            } else {
	          	        htmlid = "hpass23";
	          	        }
	            } else {
	          	        htmlid = "hpass23";
	          	       }
	            } else if (s.equalsIgnoreCase("5")) {// LV:45
	            	 if (pc.getsub() == 6) {
	          	     if (pc.getInventory().checkItem(5444, 20)) { //녹슨 장화 
	          	         while (pc.getInventory().consumeItem(5444, 1)); //녹슨 장화 
	          	   //   pc.getInventory().storeItem(15446, 1);//기초 수련 증표
	          	    //  pc.getInventory().storeItem(420010, 1); //상아탑의 귀걸이 
						final int[] item_ids = { 5446, 420010 };
						final int[] item_amounts = { 1, 1 };					
						L1ItemInstance item = null;
						for (int i = 0; i < item_ids.length; i++) {
							item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
							pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
					  }
	          	      pc.setsub(pc.getsub() + 1);
	          	      pc.save();
	          	      htmlid = "hpass9";
	            } else {
	          	      htmlid = "hpass24";
	          	     }
	            } else {
	          	     htmlid = "hpass24";
	          	     }
	            } else if (s.equalsIgnoreCase("u")) {// Lv:40
	          	     if (pc.getLevel() >= 40 && pc.getsub() == 5) {
	          	    	//  pc.getInventory().storeItem(15445, 1);//교관의 선물 주머니
	  					final int[] item_ids = { 5445 };
	  					final int[] item_amounts = { 1 };					
	  					L1ItemInstance item = null;
	  					for (int i = 0; i < item_ids.length; i++) {
	  						item = pc.getInventory().storeItem(item_ids[i], item_amounts[i]);
	  						pc.sendPackets(new S_ServerMessage(143, ((L1NpcInstance) obj).getNpcTemplate().get_name(), item.getItem().getName()));
	  					  }
	          	          pc.setsub(pc.getsub() + 1);
	          	          pc.save();
	          	                htmlid = "hpass7";
	          	        } else {
	          	                htmlid = "hpass12";
	          	               }  
	                    } 
	              }				 
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		return htmlid;
	}
	
}