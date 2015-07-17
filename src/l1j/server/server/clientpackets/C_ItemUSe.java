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

import static l1j.server.server.model.skill.L1SkillId.ERASE_MAGIC;
import static l1j.server.server.model.skill.L1SkillId.EXP_POTION;
import static l1j.server.server.model.skill.L1SkillId.EXP_POTION2;
import static l1j.server.server.model.skill.L1SkillId.EXP_POTION3;
import static l1j.server.server.model.skill.L1SkillId.POLLUTE_WATER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL2;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CASHSCROLL3;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_MITHRIL_POWDER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_WATER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_WATER_OF_EVA;
import static l1j.server.server.model.skill.L1SkillId.benz_TIMER;
import static l1j.server.server.model.skill.L1SkillId.benz_TIMER1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.CrockSystem;
import l1j.server.server.Account;
import l1j.server.server.ActionCodes;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MapFixKeyTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1ItemDelay;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1BoardInstance;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1GuardianInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_Board;
import l1j.server.server.serverpackets.S_ChangeName;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DRAGONPERL;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Sound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_UseMap;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Pet;
import l1j.server.server.utils.L1SpawnUtil;
import server.LineageClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacke

public class C_ItemUSe extends ClientBasePacket {

	private static final String C_ITEM_USE = "[C] C_ItemUSe";

	private static Logger _log = Logger.getLogger(C_ItemUSe.class.getName());

	private static Random _random = new Random(System.nanoTime());

	Calendar currentDate = Calendar.getInstance();

	SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd h:mm:ss a");

	String time = dateFormat.format(currentDate.getTime());

	public C_ItemUSe(byte abyte0[], LineageClient client) throws Exception {
		super(abyte0);
		int itemObjid = readD();
		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}
		if (pc.isGhost()) {
			return;
		}
		
		if (client.getActiveChar().getLevel() > Config.LimitLevel) { // 제한 레벨 초과
			client.kick();
			Account.ban(pc.getAccountName());//현재 PC의 계정을 압류한다.
			System.out.println("( 아이템 ) : 아이템으로 레벨버그 감지 차단완료!");
			return;
		}
		if (L1World.getInstance().getPlayer(client.getActiveChar().getName()) == null) {
			try {
				System.out.println("월드Null : "
						+ client.getActiveChar().getName() + " / "
						+ client.getIp());
			} catch (Exception e) {
			}
			client.kick();
			return;
		}
		if (pc.isGhost()) {
			return;
		}
		
		// //중복 접속 버그방지 by 마트무사 for only 포더서버만!
		if (pc.getOnlineStatus() == 0) {
			client.kick();
			return;
		}
		// //중복 접속 버그방지 by 마트무사 for only 포더서버만!
		L1ItemInstance useItem = pc.getInventory().getItem(itemObjid);

		if (useItem.getItem().getUseType() == -1) { // none:사용할 수 없는 아이템
			pc.sendPackets(new S_ServerMessage(74, useItem.getLogName())); // \f1%0은 사용할 수 없습니다.
			return;
		}
		if (pc.isTeleport()) { // 텔레포트 처리중
			return;
		}
		// 존재버그 관련 추가
		L1PcInstance jonje = L1World.getInstance().getPlayer(pc.getName());
		if (jonje == null && pc.getAccessLevel() != Config.GMCODE) {
			pc.sendPackets(new S_SystemMessage("존재버그 강제종료! 재접속하세요"));
			client.kick();
			return;
		}

		if (pc.isDead() == true) {
			return;
		}
		if (!pc.getMap().isUsableItem()) {
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"여기에서는 사용할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
			pc.sendPackets(s_chatpacket);
			return;
		}
		int itemId;
		try {
			itemId = useItem.getItem().getItemId();
		} catch (Exception e) {
			return;
		}
		if (useItem.isWorking()) {
			if (pc.getCurrentHp() > 0) {
			/**안전모드**/
				pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
				/**안전모드**/
				useItem.clickItem(pc, this);
			}
			return;
		}
		int l = 0;
		int spellsc_objid = 0;
		int spellsc_x = 0;
		int spellsc_y = 0;

		int use_type = useItem.getItem().getUseType();
		if (itemId == 41029 // 소환공의 조각
				|| itemId == 40317
				|| itemId == 41036
				|| itemId == L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_DOWN
				|| itemId == L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_DOWN
				|| itemId == L1ItemId.LOWER_TIKAL_PRESENT_PIECE_DOWN
				|| itemId == L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_DOWN
				|| itemId == L1ItemId.TIMECRACK_CORE
				|| itemId == 40964
				|| itemId == 41030 || itemId == 40925
				|| itemId == 5000083 || itemId == 5000084//엉뚱한시계
				|| itemId == 40926
				|| itemId == 40927 // 정화·신비적인
				|| itemId == 35086 // 위탁 판매 문서
				// 일부
				|| itemId == 40928 || itemId == 40929 || itemId == 500231
				|| itemId == L1ItemId.MAGIC_BREATH|| itemId == L1ItemId.PROTECTION_SCROLL) {//장비보호
			l = readD();
		} else if (use_type == 30 || itemId == 40870 || itemId == 40879) { // spell_buff
			spellsc_objid = readD();
		} else if (use_type == 5 || use_type == 17) { // spell_long
			spellsc_objid = readD();
			spellsc_x = readH();
			spellsc_y = readH();
		} else {
			l = readC();
		}

		if (pc.getCurrentHp() > 0) {
			int delay_id = 0;
			if (useItem.getItem().getType2() == 0) { // 종별：그 외의 아이템
				delay_id = ((L1EtcItem) useItem.getItem()).get_delayid();
			}
			if (delay_id != 0) { // 지연 설정 있어
				if (pc.hasItemDelay(delay_id) == true) {
					return;
				}
			}
			/**안전모드**/
			pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
			/**안전모드**/
			L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(l);
			_log.finest("request item use (obj) = " + itemObjid + " action = " + l);
			if (useItem.getItem().getType2() == 0) { // 종별：그 외의 아이템
				int min = useItem.getItem().getMinLevel();
			    int max = useItem.getItem().getMaxLevel();
			    if ((min != 0) && (min > pc.getLevel())) {
			     pc.sendPackets(new S_ServerMessage(318, String.valueOf(min)));
			     return;
			    }
			    else if ((max != 0) && (max < pc.getLevel())) {
			     pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_LEVEL_OVER, max));
			     return;
			    }
				if ((itemId == 40576 && !pc.isElf())
						|| (itemId == 40577 && !pc.isWizard()) // 영혼의 결정의 파편(흑)
						|| (itemId == 40578 && !pc.isKnight())) { // 영혼의 결정의
					// 파편(빨강)
					//	pc.sendPackets(new S_ServerMessage(264)); // \f1당신의 클래스에서는 이 아이템은 사용할 수없습니다.
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"당신의 클래스는 이 아이템을 사용할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
					pc.sendPackets(s_chatpacket);
					return;
				}
				if (itemId == 40003) {
					for (L1ItemInstance lightItem : pc.getInventory()
							.getItems()) {
						if (lightItem.getItem().getItemId() == 40002) {
							lightItem.setRemainingTime(useItem.getItem()
									.getLightFuel());
							pc.sendPackets(new S_ItemName(lightItem));
							S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"랜턴에 기름을 새로 채웠습니다.", Opcodes.S_OPCODE_MSG, 20); 
							pc.sendPackets(s_chatpacket);
							break;
						}
					}
					pc.getInventory().removeItem(useItem, 1);
					
				} else if (itemId == 54658) { // 메티스의 가호
					int objid = pc.getId();
					pc.sendPackets(new S_SkillSound(objid, 759));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(objid, 759));
					for (L1PcInstance tg : L1World.getInstance(). getVisiblePlayer(pc)) {
						if (tg.getCurrentHp() == 0 && tg.isDead()) {
							tg.sendPackets(new S_SystemMessage("GM이 부활을 해주었습니다. "));
							Broadcaster.broadcastPacket(tg, new S_SkillSound(tg.getId(), 3944));
							tg.sendPackets(new S_SkillSound(tg.getId(), 3944));
							// 축복된 부활 스크롤과 같은 효과
							tg.setTempID(objid);
							tg.sendPackets(new S_Message_YN(322, "")); // 또 부활하고 싶습니까? (Y/N)
						} else {
							tg.sendPackets(new S_SystemMessage("GM이 HP,MP를 회복해주었습니다."));
							Broadcaster.broadcastPacket(tg, new S_SkillSound(tg.getId(), 832));
							tg.sendPackets(new S_SkillSound(tg.getId(), 832));
							tg.setCurrentHp(tg.getMaxHp());
							tg.setCurrentMp(tg.getMaxMp());
						}
					}
					
					/** 드래곤의 눈물 */
				} else if (itemId == L1ItemId.DRAGON_TEARS){
					if (pc.getMapId() >= 1002 && pc.getMapId() <= 1017){
						pc.setCurrentHp(pc.getCurrentHp() + (191 + _random.nextInt(323)));
						pc.sendPackets(new S_SkillSound(pc.getId(), 189));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 189));
						pc.getInventory().removeItem(useItem, 1);
					} else {
						//	pc.sendPackets(new S_ServerMessage(1891));
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"드래곤의 숨결이 깃든 땅에서만 사용할 수 있습니다.", Opcodes.S_OPCODE_MSG, 20); 
						pc.sendPackets(s_chatpacket);
					}
					/** 신비한 물약 */
				} else if (itemId == L1ItemId.POTION_OF_mysterious){
					if (pc.getMapId() == 2101 || pc.getMapId() == 2151){
						pc.setCurrentHp(pc.getCurrentHp() + (100 + _random.nextInt(191)));
						pc.sendPackets(new S_SkillSound(pc.getId(), 189));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 189));
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_SystemMessage("얼음 여왕의 성에서만 사용 할 수 있습니다."));
					}
					
				} else if (itemId == 285000) { // 가속주머니
					if (pc.getInventory().getSize() > 100) {
						String chatText = "소지창의 아이템갯수가 100개가 넘어서 사용할수 없습니다.";
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_NORMALCHAT, 2);
						pc.sendPackets(s_chatpacket);
						return;
					}
					if (pc.getInventory().getWeight240() > 180) { // 28 == 100%

						String chatText = "짐이 너무 무거워서 사용하실수 없습니다.";
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,chatText, Opcodes.S_OPCODE_NORMALCHAT, 2);
						pc.sendPackets(s_chatpacket);
						return;
					}
					if (pc.getInventory().checkItem(285000, 1)) {
						pc.getInventory().consumeItem(285000, 1);
						createNewItem2(pc, 40018, 3, 0); // 강촐
						if (pc.isKnight()) {
							createNewItem2(pc, 40014, 3, 0); // 용기의물약
						}
						if (pc.isDragonknight()) {
							createNewItem2(pc, 430006, 3, 0); // 열매
						}
						if (pc.isCrown()) {
							createNewItem2(pc, 40031, 3, 0); // 악마의피
						}					
						if (pc.isIllusionist()) {
							createNewItem2(pc, 430006, 3, 0); // 열매						
						}
						if (pc.isElf()) {
							createNewItem2(pc, 40068, 10, 0); // 와퍼
						}				
					}
		
					/** 드래곤의 돌 */
				 } else if (itemId == L1ItemId.DRAGON_STONE) {
					if (pc.getMapId() >= 1002 && pc.getMapId() <= 1017){
						useCashScroll(pc, itemId);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						//	pc.sendPackets(new S_ServerMessage(1891));
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"드래곤의 숨결이 깃든 땅에서만 사용할 수 있습니다.", Opcodes.S_OPCODE_MSG, 20); 
						pc.sendPackets(s_chatpacket);
					}
					
			    } else if (itemId == L1ItemId.INCRESE_HP_SCROLL
			      || itemId == L1ItemId.INCRESE_MP_SCROLL
			      || itemId == L1ItemId.INCRESE_ATTACK_SCROLL
			      || itemId == L1ItemId.CHUNSANG_HP_SCROLL
			      || itemId == L1ItemId.CHUNSANG_MP_SCROLL
			      || itemId == L1ItemId.CHUNSANG_ATTACK_SCROLL) {
			     if (pc.getSkillEffectTimerSet().hasSkillEffect(
			       STATUS_CASHSCROLL)
			       || pc.getSkillEffectTimerSet().hasSkillEffect(
			         STATUS_CASHSCROLL2)
			       || pc.getSkillEffectTimerSet().hasSkillEffect(
			         STATUS_CASHSCROLL3)) {
					   //   pc.sendPackets(new S_ServerMessage(939));
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아직은 사용할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
						pc.sendPackets(s_chatpacket);
			      return;
			     }
			     useCashScroll(pc, itemId);
			     pc.getInventory().removeItem(useItem, 1);
			     /**전강주문서**/
				} else if (itemId == 40858) { // liquor(술)
					pc.setDrink(true);
					pc.sendPackets(new S_Liquor(pc.getId()));
					pc.getInventory().removeItem(useItem, 1);

				} else if (itemId == L1ItemId.EXP_POTION
						|| itemId == L1ItemId.EXP_POTION2) { // 천상의물약
					UseExpPotion(pc, itemId);
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == L1ItemId.EXP_POTION3) { // 게렝전투
			UseExpPotion2(pc, itemId);
			pc.getInventory().removeItem(useItem, 1);
			
		} else if (itemId == L1ItemId.EXP_POTION4) { // 게렝행운
			UseExpPotion3(pc, itemId);
			pc.getInventory().removeItem(useItem, 1);
					 /** 위탁 판매 증서 **/

					/** 창고 관리인의 열쇠  */
				} else if (itemId == 89136) {
					if (pc.get_SpecialSize() == 40) {
						pc.sendPackets(new S_ServerMessage(1622));
						return;
					}
					if (pc.get_SpecialSize() == 0) {
						pc.set_SpecialSize(20);
						pc.getInventory().consumeItem(89136, 1);
						pc.sendPackets(new S_ServerMessage(1624, "20"));
					} else if (pc.get_SpecialSize() == 20) {
						pc.set_SpecialSize(40);
						pc.getInventory().consumeItem(89136, 1);
						pc.sendPackets(new S_ServerMessage(1624, "40"));
					}
					/** 창고 관리인의 열쇠  */
				} else if (itemId == L1ItemId.POTION_OF_CURE_POISON
						|| itemId == 40507) { // 시션
					// 일부,
					// 엔트의
					// 가지
					if (pc.getSkillEffectTimerSet().hasSkillEffect(71) == true) { // 디케이포션
						// 상태
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"마력에 의해아무것도 마실수가 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
						pc.sendPackets(s_chatpacket);
					} else {
						pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
						pc.sendPackets(new S_SkillSound(pc.getId(), 192));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc
								.getId(), 192));
						if (itemId == L1ItemId.POTION_OF_CURE_POISON) {
							pc.getInventory().removeItem(useItem, 1);
						} else if (itemId == 40507) {
							pc.getInventory().removeItem(useItem, 1);
						}
						pc.curePoison();
					}
											
				} else if (itemId >= 30240 && itemId <= 30243) {//케레니스이벤트
					/** 인던 변신 안되도록 */
					if (pc.getMapId() == 5302|| pc.getMapId() == 5083|| pc.getMapId() == 5153) { // 배틀/인던/낚시터
						//	pc.sendPackets(new S_ServerMessage(1170)); // 이곳에서 변신할수 없습니다.
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"이곳에서 변신할수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
						pc.sendPackets(s_chatpacket);
						return;
					}
					/** 인던 변신 안되도록 */
////////////////////////////////////배틀존
					if (pc.getMapId() == 5153) {
						if (itemId == 560025
								|| itemId == 560026
								|| itemId == 40089
								|| itemId == 140089
								|| itemId == 560027|| itemId == 560028){
							S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"여기에서는 사용할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
							pc.sendPackets(s_chatpacket);
							return;
						}
					}
////////////////////////////////////					배틀존
					switch (itemId) {
					case 30240:
						L1PolyMorph.doPoly(pc, 7847, 1200,
								L1PolyMorph.MORPH_BY_NPC);
						break;
					case 30241:
						L1PolyMorph.doPoly(pc, 7845, 1200,
								L1PolyMorph.MORPH_BY_NPC);
						break;
					case 30242:
						L1PolyMorph.doPoly(pc, 7848, 1200,
								L1PolyMorph.MORPH_BY_NPC);
						break;
					case 30243:
						L1PolyMorph.doPoly(pc, 7846, 1200,
								L1PolyMorph.MORPH_BY_NPC);
						break;
					}
					pc.getInventory().removeItem(useItem, 1);

				} else if (itemId == 30245) {
					if (pc.getMap().isSafetyZone(pc.getLocation())) {
//						pc.sendPackets(new S_SystemMessage("마을안에서는 사용 할 수 없습니다."));
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"마을안에서는 사용 할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
						pc.sendPackets(s_chatpacket);
						return;
					}
					pc.getInventory().removeItem(useItem, 1);
					L1SpawnUtil.spawn(pc, 40106, 0, 1200000, true);

				} else if (itemId == 30246) {
					if (pc.getMap().isSafetyZone(pc.getLocation())) {
//						pc.sendPackets(new S_SystemMessage("마을안에서는 사용 할 수 없습니다."));
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"마을안에서는 사용 할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
						pc.sendPackets(s_chatpacket);
						return;
					}
					pc.getInventory().removeItem(useItem, 1);
					L1SpawnUtil.spawn(pc, 40107, 0, 1200000, true);//케레니스이벤트
					
					
				} else if (itemId == 400253) { // 1억아덴교환템
					if (pc.getInventory().checkItem(40308, 100000000)) { // 1억아덴이 인벤에 있나 체크
						pc.getInventory().consumeItem(40308, 100000000); // 1억아데나를 소모
						pc.getInventory().storeItem(400254, 1); // 수표를 1개 생성
						pc.sendPackets(new S_SystemMessage("\\fY100,000,000원 아데나 수표를 얻었습니다.")); // 성공시 멘트전송
					} else {
						pc.sendPackets(new S_SystemMessage("100,000,000원 아데나가 부족합니다.")); // 실패시 멘트전송

					}
				} else if (itemId == 400254) { // 1억아덴수표
					if (pc.getInventory().checkItem(L1ItemId.ADENA, 1500000000)) { //15억 이상일때
						pc.sendPackets(new S_SystemMessage("\\fY보유하신 아데나가 너무 많아 사용하실수 없습니다."));
					} else {  // 
						pc.getInventory().storeItem(40308, 100000000); // 1억아덴 생성
						pc.getInventory().consumeItem(400254, 1); // 수표 1개 소모
						pc.sendPackets(new S_SystemMessage("100,000,000원 아데나를 얻었습니다."));
					}
					
				} else if (itemId == 127002) { // 아이돌물약줌서
					if (pc.getInventory().checkItem(127000, 5)) { // 
						pc.getInventory().consumeItem(127000, 5); //
						pc.getInventory().storeItem(127001, 1); // 수표를 1개 생성
						pc.sendPackets(new S_SystemMessage("\\fY(축)아이돌의 물약을 얻었습니다.")); // 성공시 멘트전송
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_SystemMessage("아이돌의 물약(5)가 필요합니다.")); // 실패시 멘트전송

					}
					
					
					// ////////경험치물약 ---------------------------------
				} else if (itemId == 54012) { // 경험치물약
					pc.setExp(pc.getExp() + 2000000);
					pc.getInventory().removeItem(useItem, 1);
					// ////////////////////룬//////////////////
					
					//벤츠물약
				} else if (itemId == L1ItemId.CHUNSANG_ATTACK) { // 아이돌물약
					if (pc.getSkillEffectTimerSet().hasSkillEffect(benz_TIMER1)) {
						pc.sendPackets(new S_SystemMessage("중복 사용은 되지 않았습니다."));
						return;
					}
					int[] allBuffSkill = { benz_TIMER };//스킬아디
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,L1SkillUse.TYPE_GMBUFF); // 버프 모션 없애기
					}
					pc.sendPackets(new S_SystemMessage("아이돌효과(추타 5  공성 5  스펠 3  피통  200  엠통 100)"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"아이돌물약효과(추타 5  공성 5  스펠 3  피통  200  엠통 100)"));
					pc.sendPackets(new S_SkillIconGFX(71, 600)); // 미니게임타이머 10분간 창에 뜨게
					pc.sendPackets(new S_SkillSound(pc.getId(), 7626));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7626));
					pc.getInventory().removeItem(useItem, 1);
				
				} else if (itemId == L1ItemId.CHUNSANG_ATTACK1) { // 아이돌물약
					if (pc.getSkillEffectTimerSet().hasSkillEffect(benz_TIMER)) {
						pc.sendPackets(new S_SystemMessage("중복 사용은 되지 않았습니다."));
						return;
					}
					int[] allBuffSkill = { benz_TIMER1 };//스킬아디
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,L1SkillUse.TYPE_GMBUFF); // 버프 모션 없애기
					}
					pc.sendPackets(new S_SystemMessage("축아이돌효과(추타 8  공성 8  스펠 6  피통  400  엠통 200)"));
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"축아이돌물약효과(추타 8  공성 8  스펠 6  피통  400  엠통 200)"));
					pc.sendPackets(new S_SkillIconGFX(71, 900)); // 미니게임타이머 10분간 창에 뜨게
					pc.sendPackets(new S_SkillSound(pc.getId(), 7626));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7626));
					pc.getInventory().removeItem(useItem, 1);
				
					
					
					
					
					
					
				} else if (itemId == L1ItemId.MAGIC_BREATH) {   // 마력의 숨결
				     if (pc.getInventory().checkItem(L1ItemId.MAGIC_BREATH, 1)) {
				      int[] last = { 76776 , 76775 , 76774 , 76773 , 76772 , 76771 , 76770 , 76769 , 76768 , 76767 , 
				    		               76777 , 76778 , 76779 , 76780 , 76781 , 76782 , 76783 , 76784 };  // 봉인해제 후(번호는 본인팩에 맞게 수정)
				      int i = 0;
				      int choiceItem = l1iteminstance1.getItem().getItemId();
				      switch(choiceItem){  // 봉인해제 전(번호는 본인팩에 맞게 수정)
				       case 500056: i = 0; break;   
				       case 500057: i = 1; break;   
				       case 500058: i = 2; break;
				       case 500059: i = 3; break;   
				       case 500060: i = 4; break;   
				       case 500061: i = 5; break;
				       case 500062: i = 6; break;   
				       case 500063: i = 7; break;   
				       case 500064: i = 8; break;
				       case 500065: i = 9; break;   
				       case 500080: i = 10; break;   
				       case 500081: i = 11; break;
				       case 500082: i = 12; break;   
				       case 500083: i = 13; break;   
				       case 500084: i = 14; break;
				       case 500085: i = 15; break;   
				       case 500086: i = 16; break;   
				       case 500087: i = 17; break;
				       default:  i = 18; break;
				      }
				      if(i==18) {
						     //  pc.sendPackets(new S_ServerMessage(79));
							S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아무일도 일어나지 않았습니다.", Opcodes.S_OPCODE_MSG, 20); 
							pc.sendPackets(s_chatpacket);
				       return;
				      } else {
				       pc.getInventory().consumeItem(L1ItemId.MAGIC_BREATH, 1);
				       pc.getInventory().consumeItem(choiceItem, 1);
				       pc.getInventory().storeItem(last[i], 1);
				       pc.sendPackets(new S_SystemMessage(""+l1iteminstance1.getItem().getName()+"의 봉인이 해제 되었습니다."));
				      }
				     }
			    } else if (itemId >= 87885 && itemId <= 87889) {
					/** 인던 변신 안되도록 */
					if (pc.getMapId() == 5302|| pc.getMapId() == 5083|| pc.getMapId() == 5153) { // 배틀/인던/낚시터
						//	pc.sendPackets(new S_ServerMessage(1170)); // 이곳에서 변신할수 없습니다.
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"이곳에서 변신할수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
						pc.sendPackets(s_chatpacket);
						return;
					}
					/** 인던 변신 안되도록 */
////////////////////////////////////배틀존
					if (pc.getMapId() == 5153) {
						if (itemId == 560025
								|| itemId == 560026
								|| itemId == 40089
								|| itemId == 140089
								|| itemId == 560027|| itemId == 560028){
							//	pc.sendPackets(new S_ServerMessage(563)); // \f1 여기에서는 사용할 수 없습니다.
							S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"여기에서는 사용할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
							pc.sendPackets(s_chatpacket);
							return;
						}
					}
////////////////////////////////////					배틀존
			        switch(itemId){
			        case 87885: // 할로윈 딸기 캔디 (뱀파이어)
			         L1PolyMorph.doPoly(pc, 8679, 900, L1PolyMorph.MORPH_BY_NPC);
			         break;
			        case 87886: // 할로윈 밀크 캔디 (마녀)
			         L1PolyMorph.doPoly(pc, 8676, 900, L1PolyMorph.MORPH_BY_NPC);
			         break;
			        case 87887: // 할로윈 바나나 캔디 (데스)
			         L1PolyMorph.doPoly(pc, 8678, 900, L1PolyMorph.MORPH_BY_NPC);
			         break;
			        case 87888: // 할로윈 초콜릿 캔디 (고양이)
			         L1PolyMorph.doPoly(pc, 8677, 900, L1PolyMorph.MORPH_BY_NPC);
			         break;
			        case 87889: // 할로윈 호박 캔디 (허수아비)
			         L1PolyMorph.doPoly(pc, 5645, 900, L1PolyMorph.MORPH_BY_NPC);
			         break;
			        }
			        pc.getInventory().removeItem(useItem, 1);
			        
				} else if (itemId == 400251) { // 상아탑의모약
					int[] allBuffSkill = { 26, 42, 48, 79, 168, 14 };//스킬아디
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,L1SkillUse.TYPE_GMBUFF); // 버프 모션 없애기
					}
					pc.getInventory().removeItem(useItem, 1);
					pc.sendPackets(new S_SystemMessage("상아탑의묘약을 사용 하였습니다!"));
					
					
					
					
					
				} else if (itemId == 400252) { // 라우풀물약 <<<400259
						pc.setLawful(30000);
						pc.sendPackets(new S_ServerMessage(674));
						pc.getInventory().removeItem(useItem, 1);
						pc.save();; // DB에 캐릭터 정보를 기입한다
						pc.sendPackets(new S_SystemMessage("라우풀성향이 +30000 되었습니다."));
				} else if (itemId == 400259) { // 라우풀물약 <<<400259
						pc.setLawful(-30000);
						pc.sendPackets(new S_ServerMessage(674));
						pc.getInventory().removeItem(useItem, 1);
						pc.save();
						; // DB에 캐릭터 정보를 기입한다
						pc.sendPackets(new S_SystemMessage("카오틱성향이 -30000 되었습니다.."));
					
			/*	} else if (itemId == 7474) {
					   if (pc.getMark_count() < 100) {
					       int booksize = pc.getMark_count() + 10;
					       pc.setMark_count(booksize);
					       pc.sendPackets(new S_PacketBox(S_PacketBox.BOOKMARK_SIZE_PLUS_10, booksize));
					       pc.getInventory().removeItem(useItem, 1);
					       pc.save();
					      } else {
					       pc.sendPackets(new S_ServerMessage(2930));
					      }		*/
				     /** 생존의외침 아이템화 **/					 
					 
					 
			   } else if (itemId == 46193){
			        int NewHp = 0;
			        int Enchantlvl = 0;
			        Random random = new Random();
			        if (pc.get_food() >= 225) {
			         try {
			          Enchantlvl = pc.getEquipSlot().getWeapon().getEnchantLevel();
			         } catch (Exception e) {
							S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"무기를 착용해야 사용할 수 있습니다.", Opcodes.S_OPCODE_MSG, 20); 
							pc.sendPackets(s_chatpacket);
			          return;
			         }
			         if (1800000L < System.currentTimeMillis() - pc.getSurvivalCry()) {
			          if (Enchantlvl <= 6) {
			           int[] probability = { 20, 30, 40 };
			           int percent = probability[random.nextInt(probability.length)];
			           NewHp = pc.getCurrentHp() + pc.getMaxHp() / 100 * percent;
			         
			           if (NewHp > pc.getMaxHp()) {
			            NewHp = pc.getMaxHp();
			           }
			         
			           pc.setCurrentHp(NewHp);
			           pc.sendPackets(new S_SystemMessage("\\fY생존의 외침을 사용하여 최대HP " + percent + "%를 회복하였습니다."));
			           pc.sendPackets(new S_SkillSound(pc.getId(), 8684));
			           Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8684));
			           pc.set_food(0);
			           pc.sendPackets(new S_PacketBox(11, pc.get_food()));
			           pc.setSurvivalCry(System.currentTimeMillis());
			          } else if ((Enchantlvl >= 7) && (Enchantlvl <= 8)) {
			           int[] probability = { 30, 40, 50 };
			           int percent = probability[random.nextInt(probability.length)];
			         
			           NewHp = pc.getCurrentHp() + pc.getMaxHp() / 100 * percent;
			         
			           if (NewHp > pc.getMaxHp()) {
			            NewHp = pc.getMaxHp();
			           }
			         
			           pc.setCurrentHp(NewHp);
			           pc.sendPackets(new S_SystemMessage("\\fY생존의 외침을 사용하여 최대HP " + percent + "%를 회복하였습니다."));
			           pc.sendPackets(new S_SkillSound(pc.getId(), 8685));
			           Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8685));
			           pc.set_food(0);
			           pc.sendPackets(new S_PacketBox(11, pc.get_food()));
			           pc.setSurvivalCry(System.currentTimeMillis());
			          } else if ((Enchantlvl >= 9) && (Enchantlvl <= 10)) {
			           int[] probability = { 50, 60 };
			           int percent = probability[random.nextInt(probability.length)];
			         
			           NewHp = pc.getCurrentHp() + pc.getMaxHp() / 100 * percent;
			        
			           if (NewHp > pc.getMaxHp()) {
			            NewHp = pc.getMaxHp();
			           }
			         
			           pc.setCurrentHp(NewHp);
			           pc.sendPackets(new S_SystemMessage("\\fY생존의 외침을 사용하여 최대HP " + percent + "%를 회복하였습니다."));
			           pc.sendPackets(new S_SkillSound(pc.getId(), 8773));
			           Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8773));
			           pc.set_food(0);
			           pc.sendPackets(new S_PacketBox(11, pc.get_food()));
			           pc.setSurvivalCry(System.currentTimeMillis());
			          } else if (Enchantlvl >= 11) {
			           int[] probability = { 70, 80 };
			           int percent = probability[random.nextInt(probability.length)];
			           
			           NewHp = pc.getCurrentHp() + pc.getMaxHp() / 100 * percent;
			          
			           if (NewHp > pc.getMaxHp()) {
			            NewHp = pc.getMaxHp();
			           }
			           
			           pc.setCurrentHp(NewHp);
			           pc.sendPackets(new S_SystemMessage("\\fY생존의 외침을 사용하여 최대HP " + percent + "%를 회복하였습니다."));
			           pc.sendPackets(new S_SkillSound(pc.getId(), 8686));
			           Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8686));
			           pc.set_food(0);
			           pc.sendPackets(new S_PacketBox(11, pc.get_food()));
			           pc.setSurvivalCry(System.currentTimeMillis());
			          }
			         } else {
			          long time = 1800L - (System.currentTimeMillis() - pc.getSurvivalCry()) / 1000L;
			         
			          long minute = time / 60L;
			          long second = time % 60L;
			         
			          if (minute >= 29L) {
			           pc.sendPackets(new S_SystemMessage("\\fY생존의 외침은 " + minute + "분 " + second + "초 후에 재사용 가능합니다."));
			           return;
			          }
			         
			          NewHp = pc.getCurrentHp() + pc.getMaxHp() / 100 * (30 - (int)minute);
			         
			          if (NewHp > pc.getMaxHp()) {
			           NewHp = pc.getMaxHp();
			          }
			         
			          pc.setCurrentHp(NewHp);
			          pc.sendPackets(new S_SystemMessage("\\fY생존의 외침을 사용하여 최대HP " + (30 - (int)minute) + "%를 회복하였습니다."));
			          pc.sendPackets(new S_SkillSound(pc.getId(), 8683));
			          Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8683));
			          pc.set_food(0);
			          pc.sendPackets(new S_PacketBox(11, pc.get_food()));
			          pc.setSurvivalCry(System.currentTimeMillis());
			         }
			        } else {
			         pc.sendPackets(new S_SystemMessage("\\fY생존의 외침은 배고픔게이지 100% 채운 시점부터,"));
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아직 생존의 외침을 사용할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
						pc.sendPackets(s_chatpacket);
			        }			
					
					/** 초보 지원 상자 */
				} else if (itemId == 50008) {
					if (pc.getInventory().checkItem(50008, 1)) { // 체크 되는 아이템과
						pc.getInventory().consumeItem(50008, 1); // 삭제되는 아이템과
						createNewItem2(pc, 40308, 30000, 0); // 아데나
						createNewItem2(pc, 46193, 1, 0); // 생존의외침
						createNewItem2(pc, 30458, 1, 0); // 루돌프모자
						createNewItem2(pc, 430005, 1, 0); // 회상의촛불
						createNewItem2(pc, 438000, 10, 0); // 캐릭터복구주문서
						createNewItem2(pc, 500233, 10, 0); // 버경이동주문서
						//createNewItem2(pc, 560025, 10, 0); // 마을기억책
						createNewItem2(pc, 140100, 50, 0); // 순간이동주문서
						createNewItem2(pc, 437010, 1, 0); // 드래곤다이아몬드
						createNewItem2(pc, 40099, 100, 0); // 상아탑의순간이동주문서
						createNewItem2(pc, 435006, 5, 0); // 천상의물약
						createNewItem2(pc, 40029, 300, 0); // 상아탑의빨간물약
						createNewItem2(pc, 40030, 20, 0); // 상아탑의초록물약
						//createNewItem2(pc, 437002, 3, 0); // 체력증강주문서
						//createNewItem2(pc, 437003, 3, 0); // 마력증강주문서
						//createNewItem2(pc, 437004, 3, 0); // 전투강화의주문서
						createNewItem2(pc, 41246, 1000, 0); // 결정체
						createNewItem2(pc, 40081, 20, 0); // 기란이동주문서
						createNewItem2(pc, 40096, 20, 0); // 상아탑의변신주문서
						createNewItem2(pc, 40098, 20, 0); // 상아탑의확인주문서
						createNewItem2(pc, 437018, 1, 0); // 쫄법사 인형
		                createNewItem2(pc,20028, 1, 8); // 상아탑 투구
		                createNewItem2(pc,20082, 1, 8); // 상아탑 티셔츠
		                createNewItem2(pc,20126, 1, 8); // 상아탑 갑옷
		                createNewItem2(pc,20173, 1, 8); // 상아탑 장갑
		                createNewItem2(pc,20206, 1, 8); // 상아탑 부츠
		                createNewItem2(pc,20232, 1, 8); // 상아탑 방패
		                createNewItem2(pc,20282, 1, 0); // 상아탑 반지
						
		                if (pc.isKnight()) {
							 createNewItem2(pc, 35, 1, 9); //상아탑의 한손검
							 createNewItem2(pc, 48, 1, 9); //상아탑의 양손검
							 createNewItem2(pc, 147, 1, 9); //상아탑의 도끼
							 createNewItem2(pc, 105, 1, 9); //상아탑의 창
							 createNewItem2(pc, 7, 1, 9); //상아탑의 단검
							createNewItem2(pc, 40014, 20, 0); // 용기의물약
							pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
						}
						if (pc.isDragonknight()) {
							createNewItem2(pc, 35, 1, 9); //상아탑의 한손검
							 createNewItem2(pc, 48, 1, 9); //상아탑의 양손검
							 createNewItem2(pc, 147, 1, 9); //상아탑의 도끼
							createNewItem2(pc, 430007, 20, 0); // 각인의 뼈조각
							pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
						}
						if (pc.isCrown()) {
							createNewItem2(pc, 35, 1, 9); //상아탑의 한손검
							 createNewItem2(pc, 48, 1, 9); //상아탑의 양손검
							 createNewItem2(pc, 147, 1, 9); //상아탑의 도끼
							 createNewItem2(pc, 7, 1, 9); //상아탑의 단검
							createNewItem2(pc, 40031, 20, 0); // 악마의 피
							pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
						}
						if (pc.isWizard()) {
							 createNewItem2(pc, 120, 1, 9); //상아탑의 지팡이
							 createNewItem2(pc, 7, 1, 9); //상아탑의 단검
							pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
						}
						if (pc.isIllusionist()) {
							 createNewItem2(pc, 147, 1, 9); //상아탑의 도끼
							 createNewItem2(pc, 224, 1, 9); //상아탑의 지팡이
							createNewItem2(pc, 430006, 20, 0); // 유그드라열매
							pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
						}
						if (pc.isElf()) {
							 createNewItem2(pc, 35, 1, 9); //상아탑의 한손검
							 createNewItem2(pc, 175, 1, 9); //상아탑의 활
							 createNewItem2(pc, 40744, 5000, 0); //은화살
							createNewItem2(pc, 40068, 20, 0); // 엘븐와퍼
							createNewItem2(pc, 40114, 10, 0); // 요숲 귀환주문서
							pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
						}
						if (pc.isDarkelf()) {
							 createNewItem2(pc, 73, 1, 9); //상아탑의 이도류
							 createNewItem2(pc, 156, 1, 9); //상아탑의 크로우
							pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
						}
					}
					/** 초보 지원 상자 */
				} else if (itemId == 42104) { // 10검 9셋 상자
					if (pc.getInventory().checkItem(42104, 1)) { // 체크 되는
						// 아이템과 수량
						pc.getInventory().consumeItem(42104, 1); // 삭제되는 아이템과
						// 수량

						createNewItem2(pc, 20011, 1, 9);
						createNewItem2(pc, 20187, 1, 9);
						createNewItem2(pc, 20085, 1, 9);
						createNewItem2(pc, 20194, 1, 9);
						createNewItem2(pc, 120056, 1, 9);
						createNewItem2(pc, 20280, 1, 0);
						createNewItem2(pc, 20280, 1, 0);
						createNewItem2(pc, 20317, 1, 0);
						createNewItem2(pc, 21027, 1, 0);
						if (pc.isKnight()) {
							createNewItem2(pc, 20095, 1, 0);
							createNewItem2(pc, 20230, 1, 9);
							createNewItem2(pc, 62, 1, 10);
							createNewItem2(pc, 9, 1, 10);
							createNewItem2(pc, 20264, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+10검9셋 이 지급 되었습니다."));
						}
						if (pc.isDragonknight()) {
							createNewItem2(pc, 20094, 1, 0);
							createNewItem2(pc, 62, 1, 10);
							createNewItem2(pc, 410001, 1, 10);
							createNewItem2(pc, 20264, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+10검9셋 이 지급 되었습니다."));
						}
						if (pc.isCrown()) {

							createNewItem2(pc, 20094, 1, 0);
							createNewItem2(pc, 51, 1, 10);
							createNewItem2(pc, 151, 1, 10);
							createNewItem2(pc, 20264, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+10검9셋 이 지급 되었습니다."));
						}
						if (pc.isWizard()) {

							createNewItem2(pc, 20093, 1, 0);
							createNewItem2(pc, 20225, 1, 9);
							createNewItem2(pc, 126, 1, 10);
							createNewItem2(pc, 131, 1, 10);
							createNewItem2(pc, 20266, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+10검9셋 이 지급 되었습니다."));
						}
						if (pc.isIllusionist()) {

							createNewItem2(pc, 20093, 1, 0);
							createNewItem2(pc, 420006, 1, 9);
							createNewItem2(pc, 410003, 1, 10);
							createNewItem2(pc, 410004, 1, 10);
							createNewItem2(pc, 20266, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+10검9셋 이 지급 되었습니다."));
						}
						if (pc.isElf()) {

							createNewItem2(pc, 20092, 1, 0);
							createNewItem2(pc, 20236, 1, 9);
							createNewItem2(pc, 188, 1, 10);
							createNewItem2(pc, 9, 1, 10);
							createNewItem2(pc, 20256, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+10검9셋 이 지급 되었습니다."));
						}
						if (pc.isDarkelf()) {

							createNewItem2(pc, 20092, 1, 0);
							createNewItem2(pc, 81, 1, 10);
							createNewItem2(pc, 162, 1, 10);
							createNewItem2(pc, 20256, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+10검9셋 이 지급 되었습니다.")); // 메세지 출력

						}
					}
				} else if (itemId == 590031) {
					if (pc.getInventory().checkItem(590031, 1)) { // 체크 되는 아이템과 수량
						pc.getInventory().consumeItem(590031, 1); // 삭제되는 아이템과 수량
						createNewItem3(pc, 5000045, 1, 5,128);//체반
						createNewItem3(pc, 5000045, 1, 5,128);//체반
						createNewItem3(pc, 120056, 1, 7,128);//축마망
						createNewItem3(pc, 120011, 1, 7,128);//축마투
						createNewItem3(pc, 20200, 1, 7,128);//마부
						createNewItem3(pc, 20178, 1, 7,128);//암장
						createNewItem3(pc, 20085, 1, 7,128);//티
						createNewItem3(pc, 21027, 1, 0,128);//노귀
						createNewItem3(pc, 20156, 1, 7,128);//풍갑
						createNewItem2(pc, 40308, 20000000, 0);//아데나
						if (pc.isKnight()) {//기사
							createNewItem3(pc, 420003, 1, 0,128);//고투사
							createNewItem3(pc, 20314, 1, 0,128);//에반
							createNewItem3(pc, 62, 1, 9,128);//무양
							pc.sendPackets(new S_SystemMessage(
									"+9검7셋 이 지급 되었습니다."));
						}
						if (pc.isDragonknight()) {//용기사
							createNewItem3(pc, 410001, 1, 9,128);//파체
							createNewItem3(pc, 420003, 1, 0,128);//고투사
							createNewItem3(pc, 20314, 1, 0,128);//에반
							pc.sendPackets(new S_SystemMessage(
									"+9검7셋 이 지급 되었습니다."));
						}
						if (pc.isCrown()) {//군주
							createNewItem3(pc, 256, 1, 9,128);//군검
							createNewItem3(pc, 420003, 1, 0,128);//고투사
							createNewItem3(pc, 20314, 1, 0,128);//에반
							pc.sendPackets(new S_SystemMessage(
									"+9검7셋 이 지급 되었습니다."));
						}
						if (pc.isWizard()) {//법사
							createNewItem3(pc, 415013, 1, 9,128);//테지
							createNewItem3(pc, 420007, 1, 0,128);//테벨
							createNewItem3(pc, 420000, 1, 0,128);//명궁
							pc.sendPackets(new S_SystemMessage(
									"+9검7셋 이 지급 되었습니다."));
						}
						if (pc.isIllusionist()) {//환술
							createNewItem3(pc, 415013, 1, 9,128);//테지
							createNewItem3(pc, 420007, 1, 0,128);//테벨
							createNewItem3(pc, 420000, 1, 0,128);//명궁
							pc.sendPackets(new S_SystemMessage(
									"+9검7셋 이 지급 되었습니다."));
						}
						if (pc.isElf()) {//요정
							createNewItem3(pc, 415012, 1, 9,128);//테활
							createNewItem3(pc, 420007, 1, 0,128);//테벨
							createNewItem3(pc, 420000, 1, 0,128);//명궁
							pc.sendPackets(new S_SystemMessage(
									"+9검7셋 이 지급 되었습니다."));
						}
						if (pc.isDarkelf()) {//다엘
							createNewItem3(pc, 415011, 1, 9,128);//테이
							createNewItem3(pc, 420003, 1, 0,128);//고투사
							createNewItem3(pc, 20314, 1, 0,128);//에반
							pc.sendPackets(new S_SystemMessage(
									"+9검7셋 이 지급 되었습니다.")); // 메세지 출력

						}
					}
				} else if (itemId == 42103) { // 8검 7셋 상자
					if (pc.getInventory().checkItem(42103, 1)) { // 체크 되는
																	// 아이템과 수량
						pc.getInventory().consumeItem(42103, 1); // 삭제되는 아이템과
																	// 수량

						createNewItem2(pc, 20011, 1, 7);
						createNewItem2(pc, 20187, 1, 7);
						createNewItem2(pc, 20085, 1, 7);
						createNewItem2(pc, 20194, 1, 7);
						createNewItem2(pc, 120056, 1, 7);
						createNewItem2(pc, 20280, 1, 0);
						createNewItem2(pc, 20280, 1, 0);
						createNewItem2(pc, 20317, 1, 0);
						createNewItem2(pc, 21027, 1, 0);
						if (pc.isKnight()) {

							createNewItem2(pc, 20095, 1, 0);
							createNewItem2(pc, 20230, 1, 7);
							createNewItem2(pc, 62, 1, 8);
							createNewItem2(pc, 9, 1, 8);
							createNewItem2(pc, 20264, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+8검7셋 이 지급 되었습니다."));
						}
						if (pc.isDragonknight()) {

							createNewItem2(pc, 20094, 1, 0);
							createNewItem2(pc, 62, 1, 8);
							createNewItem2(pc, 410001, 1, 8);
							createNewItem2(pc, 20264, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+8검7셋 이 지급 되었습니다."));
						}
						if (pc.isCrown()) {

							createNewItem2(pc, 20094, 1, 0);
							createNewItem2(pc, 51, 1, 8);
							createNewItem2(pc, 151, 1, 8);
							createNewItem2(pc, 20264, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+8검7셋 이 지급 되었습니다."));
						}
						if (pc.isWizard()) {

							createNewItem2(pc, 20093, 1, 0);
							createNewItem2(pc, 20225, 1, 7);
							createNewItem2(pc, 126, 1, 8);
							createNewItem2(pc, 131, 1, 8);
							createNewItem2(pc, 20266, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+8검7셋 이 지급 되었습니다."));
						}
						if (pc.isIllusionist()) {

							createNewItem2(pc, 20093, 1, 0);
							createNewItem2(pc, 420006, 1, 7);
							createNewItem2(pc, 410003, 1, 8);
							createNewItem2(pc, 410004, 1, 8);
							createNewItem2(pc, 20266, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+8검7셋 이 지급 되었습니다."));
						}
						if (pc.isElf()) {

							createNewItem2(pc, 20092, 1, 0);
							createNewItem2(pc, 20236, 1, 7);
							createNewItem2(pc, 188, 1, 8);
							createNewItem2(pc, 9, 1, 8);
							createNewItem2(pc, 20256, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+8검7셋 이 지급 되었습니다."));
						}
						if (pc.isDarkelf()) {

							createNewItem2(pc, 20092, 1, 0);
							createNewItem2(pc, 81, 1, 8);
							createNewItem2(pc, 162, 1, 8);
							createNewItem2(pc, 20256, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+8검7셋 이 지급 되었습니다.")); // 메세지 출력

						}
					}
				} else if (itemId == 42105) { // 9검 8셋 상자
					if (pc.getInventory().checkItem(42105, 1)) { // 체크 되는
																	// 아이템과 수량
						pc.getInventory().consumeItem(42105, 1); // 삭제되는 아이템과
																	// 수량

						createNewItem2(pc, 20011, 1, 8);
						createNewItem2(pc, 20187, 1, 8);
						createNewItem2(pc, 20085, 1, 8);
						createNewItem2(pc, 20194, 1, 8);
						createNewItem2(pc, 120056, 1, 8);
						createNewItem2(pc, 20280, 1, 0);
						createNewItem2(pc, 20280, 1, 0);
						createNewItem2(pc, 20317, 1, 0);
						createNewItem2(pc, 21027, 1, 0);
						if (pc.isKnight()) {

							createNewItem2(pc, 20095, 1, 0);
							createNewItem2(pc, 20230, 1, 8);
							createNewItem2(pc, 62, 1, 9);
							createNewItem2(pc, 9, 1, 9);
							createNewItem2(pc, 20264, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+9검8셋 이 지급 되었습니다."));
						}
						if (pc.isDragonknight()) {

							createNewItem2(pc, 20094, 1, 0);
							createNewItem2(pc, 62, 1, 9);
							createNewItem2(pc, 410001, 1, 9);
							createNewItem2(pc, 20264, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+9검8셋 이 지급 되었습니다."));
						}
						if (pc.isCrown()) {

							createNewItem2(pc, 20094, 1, 0);
							createNewItem2(pc, 51, 1, 9);
							createNewItem2(pc, 151, 1, 9);
							createNewItem2(pc, 20264, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+9검8셋 이 지급 되었습니다."));
						}
						if (pc.isWizard()) {

							createNewItem2(pc, 20093, 1, 0);
							createNewItem2(pc, 20225, 1, 8);
							createNewItem2(pc, 126, 1, 9);
							createNewItem2(pc, 131, 1, 9);
							createNewItem2(pc, 20266, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+9검8셋 이 지급 되었습니다."));
						}
						if (pc.isIllusionist()) {

							createNewItem2(pc, 20093, 1, 0);
							createNewItem2(pc, 420006, 1, 8);
							createNewItem2(pc, 410003, 1, 9);
							createNewItem2(pc, 410004, 1, 9);
							createNewItem2(pc, 20266, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+9검8셋 이 지급 되었습니다."));
						}
						if (pc.isElf()) {

							createNewItem2(pc, 20092, 1, 0);
							createNewItem2(pc, 20236, 1, 8);
							createNewItem2(pc, 188, 1, 9);
							createNewItem2(pc, 9, 1, 9);
							createNewItem2(pc, 20256, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+9검8셋 이 지급 되었습니다."));
						}
						if (pc.isDarkelf()) {

							createNewItem2(pc, 20092, 1, 0);
							createNewItem2(pc, 81, 1, 9);
							createNewItem2(pc, 162, 1, 9);
							createNewItem2(pc, 20256, 1, 0);
							pc.sendPackets(new S_SystemMessage(
									"+9검8셋 이 지급 되었습니다.")); // 메세지 출력

						}
					}
					 /** 배송 미믹 피리 **/
	             } else if (itemId >= L1ItemId.MOPO_JUiCE_PIPE1 && itemId <= L1ItemId.MOPO_JUiCE_PIPE6) {
	              switch (itemId) {
	              case L1ItemId.MOPO_JUiCE_PIPE1:
	               if (pc.getInventory().checkItem(L1ItemId.MOPO_JUiCE_PIPE1, 1)) {
	       pc.getInventory().consumeItem(L1ItemId.MOPO_JUiCE_PIPE1, 1);
	       pc.getInventory().storeItem(L1ItemId.MOPO_BOX1, 1);
	               }
	               break;
	              case L1ItemId.MOPO_JUiCE_PIPE2:
	               if (pc.getInventory().checkItem(L1ItemId.MOPO_JUiCE_PIPE2, 1)) {
	       pc.getInventory().consumeItem(L1ItemId.MOPO_JUiCE_PIPE2, 1);
	       pc.getInventory().storeItem(L1ItemId.MOPO_BOX2, 1);
	               }
	               break;
	              case L1ItemId.MOPO_JUiCE_PIPE3:
	               if (pc.getInventory().checkItem(L1ItemId.MOPO_JUiCE_PIPE3, 1)) {
	       pc.getInventory().consumeItem(L1ItemId.MOPO_JUiCE_PIPE3, 1);
	       pc.getInventory().storeItem(L1ItemId.MOPO_BOX3, 1);
	               }
	               break;
	              case L1ItemId.MOPO_JUiCE_PIPE4:
	               if (pc.getInventory().checkItem(L1ItemId.MOPO_JUiCE_PIPE4, 1)) {
	       pc.getInventory().consumeItem(L1ItemId.MOPO_JUiCE_PIPE4, 1);
	       pc.getInventory().storeItem(L1ItemId.MOPO_BOX4, 1);
	               }
	               break;
	              case L1ItemId.MOPO_JUiCE_PIPE5:
	               if (pc.getInventory().checkItem(L1ItemId.MOPO_JUiCE_PIPE5, 1)) {
	       pc.getInventory().consumeItem(L1ItemId.MOPO_JUiCE_PIPE5, 1);
	       pc.getInventory().storeItem(L1ItemId.MOPO_BOX5, 1);
	               }
	               break;
	              case L1ItemId.MOPO_JUiCE_PIPE6:
	               if (pc.getInventory().checkItem(L1ItemId.MOPO_JUiCE_PIPE6, 1)) {
	       pc.getInventory().consumeItem(L1ItemId.MOPO_JUiCE_PIPE6, 1);
	       pc.getInventory().storeItem(L1ItemId.MOPO_BOX6, 1);
	               }
	               break;
	              }
	              pc.sendPackets(new S_SkillSound(pc.getId(), 8473)); //모포 금빛 미믹이펙
	              pc.broadcastPacket(new S_SkillSound(pc.getId(), 8473));
	             } else if (itemId == 87052) {
	                 if (pc.getInventory().checkItem(87052,1)){
	                  pc.getInventory().consumeItem(87052,1);
	                  pc.getInventory().storeItem(87054, 1);
	                  pc.sendPackets(new S_SkillSound(pc.getId(), 8473));
	                  Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8473));
	                  pc.sendPackets(new S_SystemMessage("호랑이 사육장을 얻었습니다."));
	                 }
	                } else if (itemId == 87053) {
	                 if (pc.getInventory().checkItem(87053,1)){
	                  pc.getInventory().consumeItem(87053,1);
	                  pc.getInventory().storeItem(87055, 1);
	                  pc.sendPackets(new S_SkillSound(pc.getId(), 8473));
	                  Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8473));
	                  pc.sendPackets(new S_SystemMessage("진돗개 바구니를 얻었습니다."));
	                 }
	                } else if (itemId == 87054){ // 호랑이 사육장ㅓ
	                 pc.getInventory().removeItem(useItem, 1);
	                 L1SpawnUtil.spawn(pc, 45313, 0, 1200000, true);  // 78160 펫id
	                 
	                } else if (itemId == 87055){ // 진돗개 바구니
	                 pc.getInventory().removeItem(useItem, 1);
	                 L1SpawnUtil.spawn(pc, 45711, 0, 1200000, true); // 78161 펫id
	                } else if (itemId == 5000083) { //엉뚱한시계 사용부분
	                    int Mopo = l1iteminstance1.getItem().getItemId();
	                    int newClock = l1iteminstance1.getClock() + 1;       
	                    if (Mopo == 5000063 && l1iteminstance1.getClock() == 0) {
	                     Timestamp deleteTime = null;
	                     deleteTime = new Timestamp(l1iteminstance1.getEndTime().getTime() - 7200000);        
	                     l1iteminstance1.setEndTime(deleteTime);
	                     pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CLOCK);   //추가
	                     pc.getInventory().removeItem(useItem, 1);
	                     l1iteminstance1.setClock(1);        
	                     if (System.currentTimeMillis() > l1iteminstance1.getEndTime().getTime()) {
	                      pc.getInventory().removeItem(l1iteminstance1, 1);
	                      pc.getInventory().storeItem(5000069, 1); 
	                      pc.sendPackets(new S_ServerMessage(1823));
	                     } 
	                     CharactersItemStorage storage = CharactersItemStorage.create();
	                     storage.updateClock(l1iteminstance1);
	                     storage.updateEndTime(l1iteminstance1);  //추가
	                    } else if (Mopo == 5000064 && l1iteminstance1.getClock() != 2) {
	                     Timestamp deleteTime = null;
	                     deleteTime = new Timestamp(l1iteminstance1.getEndTime().getTime() - 7200000);
	                     l1iteminstance1.setEndTime(deleteTime); 
	                     pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CLOCK);   //추가
	                     pc.getInventory().removeItem(useItem, 1);        
	                     l1iteminstance1.setClock(newClock);  
	                     if (System.currentTimeMillis() > l1iteminstance1.getEndTime().getTime()) {
	                      pc.getInventory().removeItem(l1iteminstance1, 1);
	                      pc.getInventory().storeItem(5000070, 1); 
	                      pc.sendPackets(new S_ServerMessage(1823));
	                     }
	                     CharactersItemStorage storage = CharactersItemStorage.create();
	                     storage.updateClock(l1iteminstance1);
	                     storage.updateEndTime(l1iteminstance1);  //추가
	                    } else if (Mopo == 5000065 && l1iteminstance1.getClock() != 3) { 
	                     Timestamp deleteTime = null;
	                     deleteTime = new Timestamp(l1iteminstance1.getEndTime().getTime() - 7200000);
	                     l1iteminstance1.setEndTime(deleteTime);  
	                     pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CLOCK);   //추가
	                     pc.getInventory().removeItem(useItem, 1);        
	                     l1iteminstance1.setClock(newClock);     
	                     if (System.currentTimeMillis() > l1iteminstance1.getEndTime().getTime()) {
	                      pc.getInventory().removeItem(l1iteminstance1, 1);
	                      pc.getInventory().storeItem(5000071, 1); 
	                      pc.sendPackets(new S_ServerMessage(1823));
	                     }
	                     CharactersItemStorage storage = CharactersItemStorage.create();
	                     storage.updateClock(l1iteminstance1);
	                     storage.updateEndTime(l1iteminstance1);  //추가
	                    } else {
	                     pc.sendPackets(new S_ServerMessage(79)); // 아무것도 일어나지않았습니다.
	                    }      
	                   } else if (itemId == 5000084) { 
	                    int Mopo = l1iteminstance1.getItem().getItemId();
	                    int newClock = l1iteminstance1.getClock() + 1;
	                    Timestamp deleteTime = null;
	                    if (Mopo == 5000066 && l1iteminstance1.getClock() == 0) {        
	                     deleteTime = new Timestamp(l1iteminstance1.getEndTime().getTime() - 14400000);
	                     l1iteminstance1.setEndTime(deleteTime); 
	                     pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CLOCK);   //추가
	                     pc.getInventory().removeItem(useItem, 1);
	                     l1iteminstance1.setClock(1);   
	                     if (System.currentTimeMillis() > l1iteminstance1.getEndTime().getTime()) {
	                      pc.getInventory().removeItem(l1iteminstance1, 1);
	                      pc.getInventory().storeItem(5000072, 1); 
	                      pc.sendPackets(new S_ServerMessage(1823));
	                     } 
	                     CharactersItemStorage storage = CharactersItemStorage.create();
	                     storage.updateClock(l1iteminstance1);
	                     storage.updateEndTime(l1iteminstance1);  //추가
	                    } else if (Mopo == 5000067 && l1iteminstance1.getClock() != 2) {         
	                     deleteTime = new Timestamp(l1iteminstance1.getEndTime().getTime() - 14400000);
	                     l1iteminstance1.setEndTime(deleteTime); 
	                     pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CLOCK);   //추가
	                     pc.getInventory().removeItem(useItem, 1);        
	                     l1iteminstance1.setClock(newClock);        
	                     if (System.currentTimeMillis() > l1iteminstance1.getEndTime().getTime()) {
	                      pc.getInventory().removeItem(l1iteminstance1, 1);
	                      pc.getInventory().storeItem(5000073, 1); 
	                      pc.sendPackets(new S_ServerMessage(1823));
	                     }
	                     CharactersItemStorage storage = CharactersItemStorage.create();
	                     storage.updateClock(l1iteminstance1);
	                     storage.updateEndTime(l1iteminstance1);  //추가
	                    } else if (Mopo == 5000068 && l1iteminstance1.getClock() != 3) {        
	                     deleteTime = new Timestamp(l1iteminstance1.getEndTime().getTime() - 14400000);
	                     l1iteminstance1.setEndTime(deleteTime); 
	                     pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_CLOCK);   //추가
	                     pc.getInventory().removeItem(useItem, 1);        
	                     l1iteminstance1.setClock(newClock);          
	                     if (System.currentTimeMillis() > l1iteminstance1.getEndTime().getTime()) {
	                      pc.getInventory().removeItem(l1iteminstance1, 1);
	                      pc.getInventory().storeItem(5000074, 1); 
	                      pc.sendPackets(new S_ServerMessage(1823));
	                     }
	                     CharactersItemStorage storage = CharactersItemStorage.create();
	                     storage.updateClock(l1iteminstance1);
	                     storage.updateEndTime(l1iteminstance1);  //추가
	                    } else {
	                     pc.sendPackets(new S_ServerMessage(79)); // 아무것도 일어나지않았습니다.
	                    }
	                
	                   
						
	               	} else if (itemId == 70723) { //2차 패키지
						if (pc.getInventory().checkItem(70723, 1)) { // 체크 되는 아이템과																	
							pc.getInventory().consumeItem(70723, 1); // 삭제되는 아이템과 수량	
							if (pc.isKnight()) {
								createNewItem2(pc, 62, 1, 9); // 무양
								createNewItem2(pc, 20422, 1, 0); //고목
								createNewItem2(pc, 5000045, 1, 3); //체반
								createNewItem2(pc, 5000045, 1, 3); //체반
								createNewItem2(pc, 20317, 1, 0); //오벨
								createNewItem2(pc, 21027, 1, 0); //노귀
								createNewItem2(pc, 20011, 1, 8); // 마투
								createNewItem2(pc, 20056, 1, 8); // 마법망토
								createNewItem2(pc, 490015, 1, 7); //스턴 내성의 티셔츠		
								createNewItem2(pc, 20095, 1, 0); //고판
								
								createNewItem2(pc, 20200, 1, 8); // 마부
								createNewItem2(pc, 20178, 1, 8); // 암살군왕의장갑												
								createNewItem2(pc, 420003, 1, 3);//고투사																							
								pc.sendPackets(new S_SystemMessage("\\fW2차 기사 클래스 아이템이 지급되었습니다."));
							}
							if (pc.isCrown()) {
								createNewItem2(pc, 412000, 1, 10); // 10뇌신
								createNewItem2(pc, 9, 1, 9); // 오단
								createNewItem2(pc, 20011, 1, 8); // 마투
								createNewItem2(pc, 490015, 1, 7); // 스턴 내성의 티셔츠
								createNewItem2(pc, 20200, 1, 8); // 마수군왕의 부츠	
								createNewItem2(pc, 20178, 1, 8); // 암살군왕의 장갑
								createNewItem2(pc, 20094, 1, 0); // 고비늘갑옷
								createNewItem2(pc, 20056, 1, 8); // 마법망토																				
								createNewItem2(pc, 420003, 1, 3);//고투사
								createNewItem2(pc, 20422, 1, 0);//빛고목
								createNewItem2(pc, 5000045, 1, 3);//체반
								createNewItem2(pc, 5000045, 1, 3);//체반
								createNewItem2(pc, 20317, 1, 0);//오벨
								createNewItem2(pc, 21027, 1, 0);//노귀
								pc.sendPackets(new S_SystemMessage("\\fW2차 군주 클래스 아이템이 지급되었습니다."));
							}
							if (pc.isWizard()) {
								createNewItem2(pc, 415013, 1, 10); // 테지
								createNewItem2(pc, 119, 1, 0); // 데지
								createNewItem2(pc, 20011, 1, 8); // 마투
								createNewItem2(pc, 20018, 1, 8); // 메키
								createNewItem2(pc, 20056, 1, 8); // 마망	
								createNewItem2(pc, 20055, 1, 8); // 마나망토
								createNewItem2(pc, 490015, 1, 7); // 스턴 내성의 티셔츠
								createNewItem2(pc, 20093, 1, 0); // 고롭	
								createNewItem2(pc, 20218, 1, 7); // 흑샌	
								createNewItem2(pc, 20178, 1, 8); // 암장	
								createNewItem2(pc, 420000, 1, 3); // 고대명궁가더
								createNewItem2(pc, 20422, 1, 0); // 빛고목	
								createNewItem2(pc, 5000045, 1, 3); // 체반	
								createNewItem2(pc, 5000045, 1, 3); // 체반	
								createNewItem2(pc, 20317, 1, 0); // 오벨
								createNewItem2(pc, 21027, 1, 0); // 노규ㅟ

								pc.sendPackets(new S_SystemMessage("\\fW2차 법사 클래스 아이템이 지급되었습니다."));
							}
							if (pc.isElf()) {
								createNewItem2(pc, 311, 1, 8); // 파괴의장궁
								createNewItem2(pc, 20422, 1, 0); // 빛고목
								createNewItem2(pc, 5000045, 1, 3); // 체반
								createNewItem2(pc, 5000045, 1, 3); // 체반
								createNewItem2(pc, 20317, 1, 0); // 오벨
								createNewItem2(pc, 21027, 1, 0); // 노귀
								createNewItem2(pc, 20011, 1, 8); // 마투
								createNewItem2(pc, 20056, 1, 8); // 마법망토	
								createNewItem2(pc, 490015, 1, 7); // 스턴 내성의 티셔츠
								createNewItem2(pc, 20092, 1, 0); // 고가죽
								createNewItem2(pc, 20200, 1, 8); // 마부	
								createNewItem2(pc, 20167, 1, 4); // 리자드
								createNewItem2(pc, 420000, 1, 3); // 명가더
								pc.sendPackets(new S_SystemMessage("\\fW2차 요정 클래스 아이템이 지급되었습니다."));
							}
							if (pc.isDarkelf()) {
								createNewItem2(pc, 84, 1, 8); // 흑왕도
                                createNewItem2(pc, 20422, 1, 0); // 빛고목
                                createNewItem2(pc, 5000045, 1, 3); // 체반
                                createNewItem2(pc, 5000045, 1, 3); // 체반
                                createNewItem2(pc, 20317, 1, 0); // 오벨
                                createNewItem2(pc, 21027, 1, 0); // 노귀
								createNewItem2(pc, 20011, 1, 8); // 마투
								createNewItem2(pc, 20056, 1, 8); // 마법망토	
								createNewItem2(pc, 490015, 1, 7); // 스턴 내성의 티셔츠
								createNewItem2(pc, 20092, 1, 0); // 고가죽
								createNewItem2(pc, 20200, 1, 8); // 마수군왕의 부츠	
								createNewItem2(pc, 20178, 1, 8); // 암살군왕의장갑
								createNewItem2(pc, 420003, 1, 3);//고투사			
								pc.sendPackets(new S_SystemMessage("\\fW2차 다크엘프 클래스아이템이 지급되었습니다."));
							}
							if (pc.isDragonknight()) {
								createNewItem2(pc, 410001, 1, 10); // 파멸자의 체인소드
								createNewItem2(pc, 20422, 1, 0); // 빛고목
								createNewItem2(pc, 5000045, 1, 3); // 체반
								createNewItem2(pc, 5000045, 1, 3); // 체반
								createNewItem2(pc, 20317, 1, 0); // 오벨
								createNewItem2(pc, 21027, 1, 0); // 노귀
								createNewItem2(pc, 20011, 1, 8); // 마투
								createNewItem2(pc, 20056, 1, 8); // 마법망토
								createNewItem2(pc, 490015, 1, 7); // 스턴 내성의 티셔츠
								createNewItem2(pc, 20094, 1, 0); // 고비늘갑옷		
								createNewItem2(pc, 20200, 1, 8); // 마수군왕의 부츠									
								createNewItem2(pc, 20178, 1, 8); // 암살군왕의 장갑
								createNewItem2(pc, 420003, 1, 3);//고투사					
								pc.sendPackets(new S_SystemMessage("\\fW2차 용기사 클래스아이템이 지급되었습니다."));
							}
							if (pc.isIllusionist()) {
								createNewItem2(pc, 410004, 1, 10); // 흑키
								createNewItem2(pc, 20422, 1, 0); // 빛고목
								createNewItem2(pc, 5000045, 1, 3); // 체반
								createNewItem2(pc, 5000045, 1, 3); // 체반
								createNewItem2(pc, 20317, 1, 0); // 오벨
								createNewItem2(pc, 21027, 1, 0); // 노귀
								createNewItem2(pc, 20011, 1, 8); // 마투
								createNewItem2(pc, 20056, 1, 8); // 마법망토	
								createNewItem2(pc, 490015, 1, 7); // 스턴 내성의 티셔츠
								createNewItem2(pc, 20092, 1, 0); // 고가죽		
								createNewItem2(pc, 20200, 1, 8); // 마수군왕의 부츠
								createNewItem2(pc, 20178, 1, 8); // 암살군왕의장갑
								createNewItem2(pc, 420006, 1, 7);//환술마법서				
								pc.sendPackets(new S_SystemMessage("\\fW2차 환술사 클래스 아이템이 지급되었습니다."));
							}
						}
						
	               	} else if (itemId == 70724) { //3차 패키지
							if (pc.getInventory().checkItem(70724, 1)) { // 체크 되는 아이템과																	
								pc.getInventory().consumeItem(70724, 1); // 삭제되는 아이템과 수량	
								if (pc.isKnight()) {
									createNewItem2(pc, 59, 1, 9); // 나이트발드의검
									createNewItem2(pc, 20011, 1, 9); // 마투
									createNewItem2(pc, 490015, 1, 9); //스턴 내성의 티셔츠							
									createNewItem2(pc, 420100, 1, 9); //안타라스의 완력
									createNewItem2(pc, 20056, 1, 9); // 마법망토
									createNewItem2(pc, 20200, 1, 9); // 마부
									createNewItem2(pc, 20178, 1, 9); // 암살군왕의장갑												
									createNewItem2(pc, 420003, 1, 5);//고투사																								
									pc.sendPackets(new S_SystemMessage("\\fW3차 기사 클래스 아이템이 지급되었습니다."));
								}
								if (pc.isCrown()) {
									createNewItem2(pc, 9, 1, 10); // 오단
									createNewItem2(pc, 20011, 1, 9); // 마투
									createNewItem2(pc, 490015, 1, 9); // 스턴 내성의 티셔츠
									createNewItem2(pc, 420101, 1, 9); // 안타라스의 예지력
									createNewItem2(pc, 20056, 1, 9); // 마법망토																				
									createNewItem2(pc, 20200, 1, 9); // 마수군왕의 부츠																											
									createNewItem2(pc, 20178, 1, 9); // 암살군왕의 장갑
									createNewItem2(pc, 420003, 1, 5);//고투사
									pc.sendPackets(new S_SystemMessage("\\fW3차 군주 클래스 아이템이 지급되었습니다."));
								}
								if (pc.isWizard()) {
									createNewItem2(pc, 415013, 1, 11); // 테지
									createNewItem2(pc, 119, 1, 0); // 데지
									createNewItem2(pc, 20011, 1, 9); // 마투
									createNewItem2(pc, 490015, 1, 9); // 스턴 내성의 티셔츠
									createNewItem2(pc, 420103, 1, 9); // 안타라스의 마력	
									createNewItem2(pc, 20056, 1, 9); // 마망																		
									createNewItem2(pc, 20055, 1, 9); // 마나망토
									createNewItem2(pc, 20225, 1, 9); // 마나수정구
									createNewItem2(pc, 20018, 1, 9); // 메키
									createNewItem2(pc, 20218, 1, 9); // 흑샌	
									createNewItem2(pc, 20178, 1, 9); // 암장	
									createNewItem2(pc, 420000, 1, 5); // 명가더	
									pc.sendPackets(new S_SystemMessage("\\fW3차 법사 클래스 아이템이 지급되었습니다."));
								}
								if (pc.isElf()) {//완료
									createNewItem2(pc, 311, 1, 10); // 파괴의장궁
									createNewItem2(pc, 20011, 1, 9); // 마투
									createNewItem2(pc, 490015, 1, 9); // 스턴 내성의 티셔츠
									createNewItem2(pc, 420102, 1, 9); // 안타라스의 인내력
									createNewItem2(pc, 20056, 1, 9); // 마법망토																				
									createNewItem2(pc, 20216, 1, 9); // 타락의부츠	
									createNewItem2(pc, 20190, 1, 8); // 혼돈의손길
									createNewItem2(pc, 420000, 1, 5); // 명가더
									pc.sendPackets(new S_SystemMessage("\\fW3차 요정 클래스 아이템이 지급되었습니다."));
								}
								if (pc.isDarkelf()) {
									createNewItem2(pc, 84, 1, 10); // 흑왕도
									createNewItem2(pc, 20011, 1, 9); // 마투
									createNewItem2(pc, 490015, 1, 9); // 스턴 내성의 티셔츠
									createNewItem2(pc, 420102, 1, 9); // 안타라스의 인내력
									createNewItem2(pc, 20056, 1, 9); // 마법망토							
									createNewItem2(pc, 20200, 1, 9); // 마수군왕의 부츠	
									createNewItem2(pc, 20186, 1, 9); // 타락의장갑
									createNewItem2(pc, 420003, 1, 5);//고투사			
									pc.sendPackets(new S_SystemMessage("\\fW3차 다크엘프 클래스아이템이 지급되었습니다."));
								}
								if (pc.isDragonknight()) {
									createNewItem2(pc, 58, 1, 9); //데스나이트의불검
									createNewItem2(pc, 20011, 1, 9); // 마투
									createNewItem2(pc, 490015, 1, 9); // 스턴 내성의 티셔츠
									createNewItem2(pc, 420101, 1, 9); // 안타라스의 예지력
									createNewItem2(pc, 20056, 1, 9); // 마법망토							
									createNewItem2(pc, 20200, 1, 9); // 마수군왕의 부츠									
									createNewItem2(pc, 20178, 1, 9); // 암살군왕의 장갑
									createNewItem2(pc, 420003, 1, 5);//고투사					
									pc.sendPackets(new S_SystemMessage("\\fW3차 용기사 클래스아이템이 지급되었습니다."));
								}
								if (pc.isIllusionist()) {
									createNewItem2(pc, 421, 1, 10); // 냉키
									createNewItem2(pc, 20011, 1, 9); // 마투
									createNewItem2(pc, 490015, 1, 9); // 스턴 내성의 티셔츠
									createNewItem2(pc, 420102, 1, 9); // 안타라스의 인내
									createNewItem2(pc, 20056, 1, 9); // 마법망토														
									createNewItem2(pc, 20200, 1, 9); // 마수군왕의 부츠
									createNewItem2(pc, 20178, 1, 9); // 암살군왕의장갑
									createNewItem2(pc, 420003, 1, 5);//고투사			
									pc.sendPackets(new S_SystemMessage("\\fW3차 환술사 클래스 아이템이 지급되었습니다."));
								}
							}
								
	                    ////////////////////////////////////////////////////////////
					/** 결정의 상자 by.미니님a * */
				} else if (itemId == 43907) { //
					if (pc.getInventory().checkItem(43907, 1)) {
						pc.getInventory().consumeItem(43907, 1);
						int chance = _random.nextInt(100);
						if (chance <= 30) {
							createNewItem2(pc, 43908, 1, 0);
						} else if (chance > 31 && chance <= 60) {
							createNewItem2(pc, 43909, 1, 0);
						} else if (chance > 61 && chance <= 89) {
							createNewItem2(pc, 43910, 1, 0);
						} else if (chance >= 90 && chance <= 100) {
							pc.sendPackets(new S_SystemMessage("결정의 상자가 빈 상자임을 확인 했습니다."));

						}
					}
					/** 결정의 상자 by.미니님a * */
				} else if (itemId == 500231) {
					int dollId = l1iteminstance1.getItem().getItemId();
					boolean isAppear = true;
					L1DollInstance doll = null;
					Object[] dollList = pc.getDollList().values().toArray();
					for (Object dollObject : dollList) {
						doll = (L1DollInstance) dollObject;
						if (doll.getItemObjId() == itemId) {
							isAppear = false;
							break;
						}
					}
					if (isAppear) {
						if (dollList.length >= 1) {
							pc.sendPackets(new S_SystemMessage(
									"사용 중인 인형은 변경 할 수 없습니다."));
							return;
						}
						if (dollId == 430002 || dollId == 430004
								|| dollId == 430003 || dollId == 41250
								|| dollId == 430000 || dollId == 41917
								|| dollId == 430001 || dollId == 41248
								|| dollId == 41249 || dollId == 430500
								|| dollId == 430505 || dollId == 500400
								|| dollId == 41916) { // 인형
							// 번호
							Random random = new Random();
							int MD = random.nextInt(100);
							if (MD <= 14) {
								pc.getInventory().storeItem(430003, 1);
								pc.sendPackets(new S_SystemMessage(
										"마법인형 : 시댄서를 얻었습니다."));
							}
							if (MD >= 15 && MD <= 22) {
								pc.getInventory().storeItem(41917, 1);
								pc.sendPackets(new S_SystemMessage(
										"마법인형 : 에틴을 얻었습니다."));
							}
							if (MD >= 23 && MD <= 36) {
								pc.getInventory().storeItem(41250, 1);
								pc.sendPackets(new S_SystemMessage(
										"마법인형 : 늑대인간을 얻었습니다."));
							}
							if (MD >= 37 && MD <= 46) {
								pc.getInventory().storeItem(430000, 1);
								pc.sendPackets(new S_SystemMessage(
										"마법인형 : 돌골렘을 얻었습니다."));
							}
							if (MD >= 47 && MD <= 56) {
								pc.getInventory().storeItem(430001, 1);
								pc.sendPackets(new S_SystemMessage(
										"마법인형 : 장로를 얻었습니다."));
							}
							if (MD >= 57 && MD <= 60) {
								pc.getInventory().storeItem(430002, 1);
								pc.sendPackets(new S_SystemMessage(
										"마법인형 : 크러스트시안을 얻었습니다."));
							}
							if (MD >= 61 && MD <= 70) {
								pc.getInventory().storeItem(41249, 1);
								pc.sendPackets(new S_SystemMessage(
										"마법인형 : 서큐버스를 얻었습니다."));
							}
							if (MD >= 71 && MD <= 84) {
								pc.getInventory().storeItem(430500, 1);
								pc.sendPackets(new S_SystemMessage(
										"마법인형 : 코카트리스를 얻었습니다."));
							}// //
							if (MD >= 85 && MD <= 89) {
								pc.getInventory().storeItem(500400, 1);
								pc.sendPackets(new S_SystemMessage(
										"마법인형 : 스켈레톤을 얻었습니다."));
							}
							if (MD >= 90 && MD <= 94) {
								pc.getInventory().storeItem(41916, 1);
								pc.sendPackets(new S_SystemMessage(
										"마법인형 : 허수아비를 얻었습니다."));
							}
							if (MD >= 95 && MD <= 100) {
								pc.getInventory().storeItem(430505, 1);
								pc.sendPackets(new S_SystemMessage(
										"마법인형 : 라미아를 얻었습니다."));
							}
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
						}
					}
                } else if (itemId == 5000085) {
                    EventT(pc, useItem);
                    int special = _random.nextInt(20);
                         switch(special) {
                          case 0:  pc.getInventory().storeItem(490009, 1); break;
                          default:  pc.getInventory().storeItem(490000, 1); break;
                         }
                   } else if (itemId == 5000086) {
                    EventT(pc, useItem);
                    int special = _random.nextInt(20);
                         switch(special) {
                          case 0:  pc.getInventory().storeItem(490010, 1); break;
                          default:  pc.getInventory().storeItem(490001, 1); break;
                         }
                   } else if (itemId == 5000087) {
                    EventT(pc, useItem);
                    int special = _random.nextInt(20);
                         switch(special) {
                          case 0:  pc.getInventory().storeItem(490011, 1); break;
                          default:  pc.getInventory().storeItem(490002, 1); break;
                         }
                   } else if (itemId == 5000088) {
                    EventT(pc, useItem);
                    int special = _random.nextInt(20);
                         switch(special) {
                          case 0:  pc.getInventory().storeItem(490012, 1); break;
                          default:  pc.getInventory().storeItem(490003, 1); break;
                         }
                   } else if (itemId == 5000089) {
                    EventT(pc, useItem);
                    int special = _random.nextInt(20);
                         switch(special) {
                          case 0:  pc.getInventory().storeItem(490013, 1); break;
                          default:  pc.getInventory().storeItem(490004, 1); break;
                         }
                   } else if (itemId == 5000090) {
                    EventT(pc, useItem);
                    int special = _random.nextInt(20);
                         switch(special) {
                          case 0:  pc.getInventory().storeItem(490014, 1); break;
                          default:  pc.getInventory().storeItem(490005, 1); break;
                         }
                   } else if (itemId == 5000091) {
                    EventT(pc, useItem);
                    int special = _random.nextInt(20);
                         switch(special) {
                          case 0:  pc.getInventory().storeItem(490015, 1); break;
                          default:  pc.getInventory().storeItem(490006, 1); break;
                         }
                   } else if (itemId == 5000092) {
                    EventT(pc, useItem);
                    int special = _random.nextInt(20);
                         switch(special) {
                          case 0:  pc.getInventory().storeItem(490016, 1); break;
                          default:  pc.getInventory().storeItem(490007, 1); break;
                         }
                   } else if (itemId == 5000093) {
                    EventT(pc, useItem);
                    int special = _random.nextInt(20);
                         switch(special) {
                          case 0:  pc.getInventory().storeItem(490017, 1); break;
                          default:  pc.getInventory().storeItem(490008, 1); break;
                         }
                   } else if (itemId == 5000094) {
                         pc.getInventory().removeItem(useItem, 1);
                         pc.getInventory().storeItem(5000098, 5);
                 //////////////////////////////////////////////////////////////

				} else if (itemId == 40066 || itemId == 41413) { // 떡, 월병
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
					// 회복해 갈
					// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp()
							+ (7 + _random.nextInt(6))); // 7~12
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 40067 || itemId == 41414) { // 쑥떡, 복떡
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
					// 회복해 갈
					// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp()
							+ (15 + _random.nextInt(16))); // 15~30
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 410002) { // 빛나는 나뭇잎
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
					// 회복해 갈
					// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp() + 44);
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 40735) { // 용기의 코인
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
					// 회복해 갈
					// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp() + 60);
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 40042) { // 스피릿 일부
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
					// 회복해 갈
					// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp() + 50);
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41404) { // 쿠쟈크의 영약
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
					// 회복해 갈
					// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp()
							+ (80 + _random.nextInt(21))); // 80~100
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41412) { // 금의 톨즈
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가
					// 회복해 갈
					// 것입니다.
					pc.setCurrentMp(pc.getCurrentMp() + (5 + _random.nextInt(16))); // 5~20
					pc.getInventory().removeItem(useItem, 1);
					/** 결정의 상자 by.미니님a * */
				} else if (itemId == 43908) { // 생명의 결정
					pc.sendPackets(new S_ServerMessage(77, "$197"));
					pc.sendPackets(new S_SystemMessage("물약 재사용 시간이  1분 지연됩니다."));
					pc.setCurrentHp(pc.getCurrentHp() + 500); // 피 500채운다
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 43909) { // 정신의 결정
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가회복해 갈 것입니다.
					pc.sendPackets(new S_SystemMessage("물약 재사용 시간이  1분 지연됩니다."));
					pc.setCurrentMp(pc.getCurrentMp() + 100); // 엠100 채운다
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 43910) { // 회복의 결정
					pc.sendPackets(new S_ServerMessage(77, "$197"));
					pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가/ 회복해 갈 것입니다.
					pc.sendPackets(new S_SystemMessage("물약 재사용 시간이  1분 지연됩니다."));
					pc.setCurrentMp(pc.getCurrentMp() + 500); // 피 500채운다
					pc.setCurrentHp(pc.getCurrentHp() + 100); // 엠 100채운다
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == L1ItemId.PROTECTION_SCROLL) {
				     if (l1iteminstance1.getItem().getType2() != 0 && l1iteminstance1.getProtection() == 0){
				      l1iteminstance1.setProtection(1);
				      pc.getInventory().removeItem(useItem, 1);
				      pc.sendPackets(new S_SystemMessage(""+l1iteminstance1.getLogName()+"이(가) 마력의 기운이 스며들었습니다."));
				     } else {
				      pc.sendPackets(new S_ServerMessage(79));
				     }
				     pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_ENCHANTLVL);
				     pc.getInventory().saveItem(l1iteminstance1, L1PcInventory.COL_ENCHANTLVL);
				     pc.saveInventory();           //장비 보호	 
				}
				/** 결정의 상자 by.미니님a * */
				else if (itemId == 40317) { // 숫돌
					// 무기나 방어용 기구의 경우만
					if (l1iteminstance1.getItem().getType2() != 0
							&& l1iteminstance1.get_durability() > 0) {
						String msg0;
						pc.getInventory().recoveryDamage(l1iteminstance1);
						msg0 = l1iteminstance1.getLogName();
						if (l1iteminstance1.get_durability() == 0) {
							pc.sendPackets(new S_ServerMessage(464, msg0)); // %0%s는
							// 신품
							// 같은
							// 상태가
							// 되었습니다.
						} else {
							pc.sendPackets(new S_ServerMessage(463, msg0)); // %0
							// 상태가
							// 좋아졌습니다.
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_DOWN
						|| itemId == L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_DOWN) { // 오시리스의
					// 조각
					// (하)
					int itemId2 = l1iteminstance1.getItem().getItemId();
					if (itemId == L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_DOWN
							&& itemId2 == L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_UP) {
						if (pc.getInventory().checkItem(
								L1ItemId.LOWER_OSIRIS_PRESENT_PIECE_UP)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(
									L1ItemId.CLOSE_LOWER_OSIRIS_PRESENT, 1);
						}
					} else if (itemId == L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_DOWN
							&& itemId2 == L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_UP) {
						if (pc.getInventory().checkItem(
								L1ItemId.HIGHER_OSIRIS_PRESENT_PIECE_UP)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(
									L1ItemId.CLOSE_HIGHER_OSIRIS_PRESENT, 1);
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else if (itemId == 41246) { // 결정체
					if (pc.getInventory().checkItem(41246, 10000)) {// 결정체 갯수
						pc.getInventory().consumeItem(41246, 10000); // 결정체
																		// 1000개
																		// 소모
						pc.getInventory().storeItem(40308, 50000); // 아데나
																	// 5000나옴
						pc.sendPackets(new S_SystemMessage(
								"결정체 10000가 아데나 50000로 변환 되었습니다. ")); // 성공 멘트
					} else {
						pc.sendPackets(new S_SystemMessage(
								"결정체 10000개가 있어야 변환가능합니다")); // 실패시 멘트
					}
					// //////////////////////////////////////////////////////////////////////////////////임시혈맹가입주문서
				} else if (itemId == 400077) { // / 임시혈맹 가입주문서
					if (pc.getClanid() == 0) {
						if (pc.isCrown()) {
							pc.sendPackets(new S_SystemMessage(
									"군주캐릭은 사용할수 없습니다."));
							return;
						}
						pc.getInventory().consumeItem(400077, 1);
						pc.setClanid(287720168); // 여기서 이번호는 혈맹번호입니다.
						pc.setTitle("신규\\f>Non-PvP");
						pc.save(); // DB에 캐릭터 정보를 기입한다
						pc.sendPackets(new S_SystemMessage("\\fW신규 혈맹에 가입완료!"));
						pc.sendPackets(new S_SystemMessage(
								"\\fW신규 혈맹에 가입하시면 PK가 불가능합니다!"));
						pc.getInventory().removeItem(useItem, 1);
						pc.save(); // DB에 캐릭터 정보를 기입한다
					} else {
						pc.sendPackets(new S_SystemMessage(
								"당신은 이미 혈맹에 가입하였습니다."));
					}
					// /////////////////////////////////////////////////
					// 자기 계정 군주 혈맹에 가입하기
				} else if (itemId == 4000) {
					if (pc.getInventory().checkItem(4000, 1)) { // 인벤에 있나 체크
						pc.getInventory().consumeItem(4000, 1); // 소모
						if (pc.isCrown()) { // 군주라면
							if (pc.get_sex() == 0) { // 왕자라면
								pc.sendPackets(new S_ServerMessage(87)); // 당신은
																			// 왕자입니다
							} else { // 공주라면
								pc.sendPackets(new S_ServerMessage(88)); // 당신은
																			// 공주입니다
							}
							return;
						}
						if (pc.getClanid() != 0) { // 혈맹이 있다면
							pc.sendPackets(new S_ServerMessage(89)); // 이미
																		// 혈맹이
																		// 있습니다
							return;
						}
						Connection con = null;
						con = L1DatabaseFactory.getInstance().getConnection();
						Statement pstm2 = con.createStatement();
						ResultSet rs2 = pstm2
								.executeQuery("SELECT `account_name`, `char_name`, `ClanID`, `Clanname` FROM `characters` WHERE Type = 0"); // 케릭터
																																			// 테이블에서
																																			// 군주만
																																			// 골라와서
						while (rs2.next()) {
							if (pc.getNetConnection()
									.getAccountName()
									.equalsIgnoreCase(
											rs2.getString("account_name"))) {
								if (rs2.getInt("ClanID") != 0) { // 군주의 혈맹이 있다면
									L1Clan clan = L1World.getInstance().getClan(rs2.getString("Clanname"));
									L1PcInstance clanMember[] = clan.getOnlineClanMember();
									for (int cnt = 0; cnt < clanMember.length; cnt++) {
										clanMember[cnt].sendPackets(new S_ServerMessage(94, pc.getName()));
									}
									pc.setClanid(rs2.getInt("ClanID"));
									pc.setClanRank(2);
									pc.setClanname(rs2.getString("Clanname"));
									pc.save(); // DB에 캐릭터 정보를 기입한다
									clan.addClanMember(pc.getName(),pc.getClanRank());
									pc.sendPackets(new S_ServerMessage(95, rs2.getString("Clanname")));
									pc.getInventory().removeItem(useItem, 1);
									break;
								}
							}
						}
						rs2.first(); // 쿼리를 처음으로 되돌리고
						rs2.close();// 여기부터 아래까지 리소스삭제부분
						pstm2.close();
						con.close();
						if (pc.getClanid() == 0) { // 혈맹이 있다면
							pc.sendPackets(new S_SystemMessage("계정내에 군주가 없거나 혈맹이 창설되지 않았습니다.")); // 메세지
																		// 보내고
						}
					}
					// /////////////////////////////////////////////////////////////
                } else if (itemId == 520000) { 
                    pc.getInventory().removeItem(useItem , 1);
                    L1PolyMorph.doPoly(pc, 11232, 1200, L1PolyMorph.MORPH_BY_ITEMMAGIC);
                } else if (itemId == 520001) { 
                    pc.getInventory().removeItem(useItem , 1);
                    L1PolyMorph.doPoly(pc, 11234, 1200, L1PolyMorph.MORPH_BY_ITEMMAGIC);
                } else if (itemId == 520002) { 
                    pc.getInventory().removeItem(useItem , 1);
                    L1PolyMorph.doPoly(pc, 11236, 1200, L1PolyMorph.MORPH_BY_ITEMMAGIC);
					
					
				} else if (itemId == 5500101) { // 1차 캐쉬템 이동 주문서
					pc.setCashStep(1);
					L1Teleport.teleport(pc, 32866, 32878, (short)631, 5, true); 
					pc.getInventory().consumeItem(5500101, 1);
				} else if (itemId == 5500102) { // 2차 캐쉬템 이동 주문서
					pc.setCashStep(2);
					L1Teleport.teleport(pc, 32866, 32878, (short)631, 5, true); 
					pc.getInventory().consumeItem(5500102, 1);
				} else if (itemId == 5500103) { // 3차 캐쉬템 이동 주문서
					pc.setCashStep(3);
					L1Teleport.teleport(pc, 32866, 32878, (short)631, 5, true); 
					pc.getInventory().consumeItem(5500103, 1); 
                    
				} else if (itemId == 5500104) { // 캣츠1차
					if (pc.getInventory().checkItem(5500104, 1)) { // 체크 되는 아이템과																	
						pc.getInventory().consumeItem(5500104, 1); // 삭제되는 아이템과 수량																								
						createNewItem2(pc, 4500003, 1, 0); //패키지게시판!
					//	createNewItem2(pc, 6000036, 1, 0); //복주머니
					//	createNewItem2(pc, 6000032, 3, 0); //100%무기강화주문서
						createNewItem2(pc, 5500001, 1, 0); //1차코인
						createNewItem2(pc, 5500002, 1, 0); //
						createNewItem2(pc, 5500003, 1, 0); //
						createNewItem2(pc, 5500004, 1, 0); //
						createNewItem2(pc, 5500005, 1, 0); //
						createNewItem2(pc, 5500006, 1, 0); //
						createNewItem2(pc, 5500007, 1, 0); //
						createNewItem2(pc, 5500008, 1, 0); //
						createNewItem2(pc, 5500009, 1, 0); //
						createNewItem2(pc, 5500010, 1, 0); //
						createNewItem2(pc, 5500011, 2, 0); //
						createNewItem2(pc, 5500012, 1, 0); //
						createNewItem2(pc, 5500101, 10, 0); //1차이동줌서
						if (pc.isKnight()) {
							createNewItem2(pc, 500400, 1, 0); // 스파							
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 1차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isCrown()) {
							createNewItem2(pc, 500400, 1, 0); // 스파
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 1차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isWizard()) {
							createNewItem2(pc, 430505, 1, 0); // 라미아
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 1차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isElf()) {
							createNewItem2(pc, 430500, 1, 0); // 코카
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 1차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isDarkelf()) {			
							createNewItem2(pc, 500400, 1, 0); // 스파
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 1차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isDragonknight()) {						
							createNewItem2(pc, 500400, 1, 0); // 스파				
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 1차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isIllusionist()) {
							createNewItem2(pc, 430505, 1, 0); // 라미아					
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 1차템 이동 주문서를 클릭해주세요."));
						}
					}
					// //////////캣츠2차셋
				} else if (itemId == 5500105) { // 캣츠2차
					if (pc.getInventory().checkItem(5500105, 1)) { // 체크 되는 아이템과																	
						pc.getInventory().consumeItem(5500105, 1); // 삭제되는 아이템과 수량	
						createNewItem2(pc, 4500003, 1, 0); //패키지게시판!
					//	createNewItem2(pc, 6000036, 1, 0); //복주머니	
					//	createNewItem2(pc, 6000032, 3, 0); //100%무기강화주문서
						createNewItem2(pc, 5500013, 1, 0); //2차코인
						createNewItem2(pc, 5500014, 1, 0); //
						createNewItem2(pc, 5500015, 1, 0); //
						createNewItem2(pc, 5500016, 1, 0); //
						createNewItem2(pc, 5500017, 1, 0); //
						createNewItem2(pc, 5500018, 1, 0); //
						createNewItem2(pc, 5500019, 1, 0); //
						createNewItem2(pc, 5500020, 1, 0); //
						createNewItem2(pc, 5500021, 1, 0); //
						createNewItem2(pc, 5500022, 1, 0); //
						createNewItem2(pc, 5500023, 2, 0); //
						createNewItem2(pc, 5500024, 1, 0); //
						createNewItem2(pc, 5500102, 10, 0); //2차이동줌서
						if (pc.isKnight()) {
							createNewItem2(pc, 430104, 1, 0); //마안
							createNewItem2(pc, 430105, 1, 0); //마안
							createNewItem2(pc, 430106, 1, 0); //마안
							createNewItem2(pc, 430107, 1, 0); //마안
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 2차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isCrown()) {
							createNewItem2(pc, 430104, 1, 0); //마안
							createNewItem2(pc, 430105, 1, 0); //마안
							createNewItem2(pc, 430106, 1, 0); //마안
							createNewItem2(pc, 430107, 1, 0); //마안
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 2차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isWizard()) {
							createNewItem2(pc, 430104, 1, 0); //마안
							createNewItem2(pc, 430105, 1, 0); //마안
							createNewItem2(pc, 430106, 1, 0); //마안
							createNewItem2(pc, 430107, 1, 0); //마안
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 2차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isElf()) {
							createNewItem2(pc, 430104, 1, 0); //마안
							createNewItem2(pc, 430105, 1, 0); //마안
							createNewItem2(pc, 430106, 1, 0); //마안
							createNewItem2(pc, 430107, 1, 0); //마안
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 2차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isDarkelf()) {			
							createNewItem2(pc, 430104, 1, 0); //마안
							createNewItem2(pc, 430105, 1, 0); //마안
							createNewItem2(pc, 430106, 1, 0); //마안
							createNewItem2(pc, 430107, 1, 0); //마안
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 2차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isDragonknight()) {						
							createNewItem2(pc, 430104, 1, 0); //마안
							createNewItem2(pc, 430105, 1, 0); //마안
							createNewItem2(pc, 430106, 1, 0); //마안
							createNewItem2(pc, 430107, 1, 0); //마안				
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 2차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isIllusionist()) {
							createNewItem2(pc, 430104, 1, 0); //마안
							createNewItem2(pc, 430105, 1, 0); //마안
							createNewItem2(pc, 430106, 1, 0); //마안
							createNewItem2(pc, 430107, 1, 0); //마안				
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 2차템 이동 주문서를 클릭해주세요."));
						}
					}
					// //////////캣츠3차셋
				} else if (itemId == 5500106) { // 캣츠3차
					if (pc.getInventory().checkItem(5500106, 1)) { // 체크 되는 아이템과																	
						pc.getInventory().consumeItem(5500106, 1); // 삭제되는 아이템과 수량	
						createNewItem2(pc, 4500003, 1, 0); //패키지게시판!
					//	createNewItem2(pc, 6000036, 1, 0); //복주머니	
					//	createNewItem2(pc, 6000032, 3, 0); //100%무기강화주문서
						createNewItem2(pc, 5500025, 1, 0); //3차코인
						createNewItem2(pc, 5500026, 1, 0); //
						createNewItem2(pc, 5500027, 1, 0); //
						createNewItem2(pc, 5500028, 1, 0); //
						createNewItem2(pc, 5500029, 1, 0); //
						createNewItem2(pc, 5500030, 1, 0); //
						createNewItem2(pc, 5500031, 1, 0); //
						createNewItem2(pc, 5500032, 1, 0); //
						createNewItem2(pc, 5500033, 1, 0); //
						createNewItem2(pc, 5500034, 1, 0); //
						createNewItem2(pc, 5500035, 2, 0); //
						createNewItem2(pc, 5500036, 1, 0); //
						createNewItem2(pc, 5500103, 10, 0); //3차이동줌서
						if (pc.isKnight()) {
							createNewItem2(pc, 430110, 1, 0); //생명의 마안
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 3차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isCrown()) {
							createNewItem2(pc, 430110, 1, 0); //생명의 마안
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 3차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isWizard()) {
							createNewItem2(pc, 430110, 1, 0); //생명의 마안
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 3차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isElf()) {
							createNewItem2(pc, 430110, 1, 0); //생명의 마안
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 3차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isDarkelf()) {			
							createNewItem2(pc, 430110, 1, 0); //생명의 마안
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 3차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isDragonknight()) {						
							createNewItem2(pc, 430110, 1, 0); //생명의 마안		
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 3차템 이동 주문서를 클릭해주세요."));
						}
						if (pc.isIllusionist()) {
							createNewItem2(pc, 430110, 1, 0); //생명의 마안			
							pc.sendPackets(new S_SystemMessage(
									"\\fW감사합니다. 3차템 이동 주문서를 클릭해주세요."));
						}
					}	
					
				} else if (itemId == 5000341) {// 기란감옥 3시간충전
				     int entertime = pc.getGdungeonTime() % 1000;
				     if(entertime > 61){
				      pc.setGdungeonTime(pc.getGdungeonTime() - 176);
				      pc.getInventory(). consumeItem(5000341, 1); 
						pc.sendPackets(new S_SystemMessage("" + pc.getName() + "님의 기란 감옥 시간이 충전되었습니다."));
						pc.save(); // 저장
					} else {
						pc.sendPackets(new S_SystemMessage("기란 감옥 시간 충전이 필요치 않습니다."));
					}
				} else if (itemId == 5000351) {// 라바던전 3시간충전
				     int entertime = pc.getLdungeonTime() % 1000;
				     if(entertime > 61){
				      pc.setLdungeonTime(pc.getLdungeonTime() - 176);
				      pc.getInventory(). consumeItem(5000351, 1); 
						pc.sendPackets(new S_SystemMessage("" + pc.getName() + "님의 라스타바드 시간이 충전되었습니다."));
						pc.save(); // 저장
					} else {
						pc.sendPackets(new S_SystemMessage("라스타바드 시간 충전이 필요치 않습니다."));
					}
			} else if (itemId == 5000361) {// 상아탑 1시간충전
				     int entertime = pc.getTkddkdungeonTime() % 1000;
				     if(entertime > 30){
				      pc.setTkddkdungeonTime(pc.getTkddkdungeonTime() - 50);
				      pc.getInventory(). consumeItem(5000361, 1); 
						pc.sendPackets(new S_SystemMessage("" + pc.getName() + "님의 상아탑 시간이 충전되었습니다."));
						pc.save(); // 저장
					} else {
						pc.sendPackets(new S_SystemMessage("상아탑 시간 충전이 필요치 않습니다."));
					}
			} else if (itemId == 5000349) {// 용던 3시간충전
			     int entertime = pc.getDdungeonTime() % 1000;
			     if(entertime > 61){
			      pc.setDdungeonTime(pc.getDdungeonTime() - 120);
			      pc.getInventory(). consumeItem(5000349, 1); 
					pc.sendPackets(new S_SystemMessage("" + pc.getName() + "님의 용의던전 시간이 충전되었습니다."));
					pc.save(); // 저장
				} else {
					pc.sendPackets(new S_SystemMessage("용의던전 시간 충전이 필요치 않습니다."));
				}
			     
			} else if (itemId == 5000350) {// 전초기지 3시간충전
			     int entertime = pc.getoptTime() % 1000;
			     if(entertime > 61){
			      pc.setoptTime(pc.getoptTime() - 176);
			      pc.getInventory(). consumeItem(5000350, 1); 
					pc.sendPackets(new S_SystemMessage("" + pc.getName() + "님의 오크전초기지 시간이 충전되었습니다."));
					pc.save(); // 저장
				} else {
					pc.sendPackets(new S_SystemMessage("오크전초기지 시간 충전이 필요치 않습니다."));
				}
			} else if (itemId == 50011) { //몬스터소환줌서
				int castle_id = L1CastleLocation.getCastleIdByArea(pc);//추가   
			     if (castle_id != 0){   //공성존에서 사용 불가
			         pc.sendPackets(new S_SystemMessage("\\fY몬스터: 공성지역에서는 사용 할 수 없다."));
			         return;
			       }
				if (CharPosUtil.getZoneType(pc) == 0){
					useMobEventSpownWand(pc, l1iteminstance1);
				pc.getInventory().removeItem(useItem, 1);
				}else{
					pc.sendPackets(new S_SystemMessage("\\fY몬스터: 노멀 존에서만 사용이 가능합니다."));
				}
				} else if (itemId == 437008) { // 속죄의 성서 사용법
					pc.sendPackets(new S_SystemMessage("이아이템은 신녀 유리스에게 가져가면 라우풀은 3천씩 받을수 있습니다."));
				 } else if (itemId == 467009) { // 캐릭명변경주문서
                     pc.sendPackets(new S_SystemMessage("[.캐릭명변경] [변경할아이디] 쳐주시면 됩니다."));
				 } else if (itemId == 550008) { // 퍼센트물약
				     int persent = 10;
				     pc.addExp((ExpTable.getNeedExpNextLevel(pc.getLevel() + 1) / 100) * persent);
				     pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 400246) { // 킬뎃 초기화
					pc.setKills(0);
					pc.setDeaths(0);
                    pc.sendPackets(new S_SystemMessage("\\fY카드: 당신의 승률이 초기화 되었습니다."));
					pc.getInventory().removeItem(useItem, 1);
					// 마안 7종
				} else if (itemId >= 430104 && itemId <= 430110) {
					if (pc.getSkillEffectTimerSet().hasSkillEffect(7670)) {
						pc.sendPackets(new S_SystemMessage("재사용 시간이 되지 않았습니다."));
						return;
					} else {
						pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
						if (itemId == 430106) { // 지룡의마안
							int[] allBuffSkill = { 7670, 7671 };
							pc.setBuffnoch(1);
							L1SkillUse l1skilluse = new L1SkillUse();
							for (int i = 0; i < allBuffSkill.length; i++) {
								l1skilluse.handleCommands(pc, allBuffSkill[i],
										pc.getId(), pc.getX(), pc.getY(), null,
										0, L1SkillUse.TYPE_GMBUFF);
							}
							pc.setBuffnoch(0);

						} else if (itemId == 430104) { // 수룡의마안
							int[] allBuffSkill = { 7670, 7672 };
							pc.setBuffnoch(1);
							L1SkillUse l1skilluse = new L1SkillUse();
							for (int i = 0; i < allBuffSkill.length; i++) {
								l1skilluse.handleCommands(pc, allBuffSkill[i],
										pc.getId(), pc.getX(), pc.getY(), null,
										0, L1SkillUse.TYPE_GMBUFF);
							}
							pc.setBuffnoch(0);

						} else if (itemId == 430107) { // 화룡의마안
							int[] allBuffSkill = { 7670, 7673 };
							pc.setBuffnoch(1);
							L1SkillUse l1skilluse = new L1SkillUse();
							for (int i = 0; i < allBuffSkill.length; i++) {
								l1skilluse.handleCommands(pc, allBuffSkill[i],
										pc.getId(), pc.getX(), pc.getY(), null,
										0, L1SkillUse.TYPE_GMBUFF);
							}
							pc.setBuffnoch(0);

						} else if (itemId == 430105) { // 풍룡의마안
							int[] allBuffSkill = { 7670, 7674 };
							pc.setBuffnoch(1);
							L1SkillUse l1skilluse = new L1SkillUse();
							for (int i = 0; i < allBuffSkill.length; i++) {
								l1skilluse.handleCommands(pc, allBuffSkill[i],
										pc.getId(), pc.getX(), pc.getY(), null,
										0, L1SkillUse.TYPE_GMBUFF);
							}
							pc.setBuffnoch(0);

						} else if (itemId == 430108) { // 탄생의마안
							int[] allBuffSkill = { 7670, 7675 };
							pc.setBuffnoch(1);
							L1SkillUse l1skilluse = new L1SkillUse();
							for (int i = 0; i < allBuffSkill.length; i++) {
								l1skilluse.handleCommands(pc, allBuffSkill[i],
										pc.getId(), pc.getX(), pc.getY(), null,
										0, L1SkillUse.TYPE_GMBUFF);
							}
							pc.setBuffnoch(0);

						} else if (itemId == 430109) { // 형상의마안
							int[] allBuffSkill = { 7670, 7676 };
							pc.setBuffnoch(1);
							L1SkillUse l1skilluse = new L1SkillUse();
							pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "형상의마안적용 : 지룡, 수룡, 풍룡의 마안효과발휘"));
							// pc.sendPackets(new S_SystemMessage("\\fY표기상에는 생마로 뜨지만 버프창에 나오는게 실제적으로 적용됩니다."));
							
							for (int i = 0; i < allBuffSkill.length; i++) {
								l1skilluse.handleCommands(pc, allBuffSkill[i],
										pc.getId(), pc.getX(), pc.getY(), null,
										0, L1SkillUse.TYPE_GMBUFF);
							}
							pc.setBuffnoch(0);

						} else if (itemId == 430110) { // 생명의마안
							int[] allBuffSkill = { 7670, 7677 };

							pc.setBuffnoch(1);
							L1SkillUse l1skilluse = new L1SkillUse();
							pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "생명의마안적용 : 지룡, 수룡, 풍룡, 화룡의 마안효과발휘"));
							// pc.sendPackets(new S_SystemMessage("\\fY표기상에는 형상으로 뜨지만 버프창에 나오는게 실제적으로 적용됩니다."));
							
							for (int i = 0; i < allBuffSkill.length; i++) {
								l1skilluse.handleCommands(pc, allBuffSkill[i],pc.getId(), pc.getX(), pc.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
							}
							pc.setBuffnoch(0);
						}
					}
				} else if (itemId == L1ItemId.LOWER_TIKAL_PRESENT_PIECE_DOWN
						|| itemId == L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_DOWN) { // 오시리스의
					// 조각
					// (하)
					int itemId2 = l1iteminstance1.getItem().getItemId();
					if (itemId == L1ItemId.LOWER_TIKAL_PRESENT_PIECE_DOWN
							&& itemId2 == L1ItemId.LOWER_TIKAL_PRESENT_PIECE_UP) {
						if (pc.getInventory().checkItem(
								L1ItemId.LOWER_TIKAL_PRESENT_PIECE_UP)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(
									L1ItemId.CLOSE_LOWER_TIKAL_PRESENT, 1);
						}
					} else if (itemId == L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_DOWN
							&& itemId2 == L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_UP) {
						if (pc.getInventory().checkItem(
								L1ItemId.HIGHER_TIKAL_PRESENT_PIECE_UP)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(
									L1ItemId.CLOSE_HIGHER_TIKAL_PRESENT, 1);
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else if (itemId == L1ItemId.ANCIENT_ROYALSEAL) { // 태고의 옥쇄
					if (client.getAccount().getCharSlot() < 8) {
						client.getAccount().setCharSlot(client,
								client.getAccount().getCharSlot() + 1);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else if (itemId == L1ItemId.TIMECRACK_CORE) { // 균열의 핵
					int itemId2 = l1iteminstance1.getItem().getItemId();
					if (itemId2 == L1ItemId.CLOSE_LOWER_OSIRIS_PRESENT) {
						if (pc.getInventory().checkItem(
								L1ItemId.CLOSE_LOWER_OSIRIS_PRESENT)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(
									L1ItemId.OPEN_LOWER_OSIRIS_PRESENT, 1);
						}
					} else if (itemId2 == L1ItemId.CLOSE_HIGHER_OSIRIS_PRESENT) {
						if (pc.getInventory().checkItem(
								L1ItemId.CLOSE_HIGHER_OSIRIS_PRESENT)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(
									L1ItemId.OPEN_HIGHER_OSIRIS_PRESENT, 1);
						}
					} else if (itemId2 == L1ItemId.CLOSE_LOWER_TIKAL_PRESENT) {
						if (pc.getInventory().checkItem(
								L1ItemId.CLOSE_LOWER_TIKAL_PRESENT)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(
									L1ItemId.OPEN_LOWER_TIKAL_PRESENT, 1);
						}
					} else if (itemId2 == L1ItemId.CLOSE_HIGHER_TIKAL_PRESENT) {
						if (pc.getInventory().checkItem(
								L1ItemId.CLOSE_HIGHER_TIKAL_PRESENT)) {
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(
									L1ItemId.OPEN_HIGHER_TIKAL_PRESENT, 1);
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else if (itemId == 40097 || itemId == 40119
						|| itemId == 140119 || itemId == 140329) { // 해주스크롤,
					// 원주민의 토템
					L1Item template = null;
					for (L1ItemInstance eachItem : pc.getInventory().getItems()) {
						if (eachItem.getItem().getBless() != 2) {
							continue;
						}
						if (!eachItem.isEquipped()
								&& (itemId == 40119 || itemId == 40097)) {
							// n해주는 장비 하고 있는 것 밖에 해주 하지 않는다
							continue;
						}
						int id_normal = eachItem.getItemId() - 200000;
						template = ItemTable.getInstance().getTemplate(
								id_normal);
						if (template == null) {
							continue;
						}
						if (pc.getInventory().checkItem(id_normal)
								&& template.isStackable()) {
							pc.getInventory().storeItem(id_normal,
									eachItem.getCount());
							pc.getInventory().removeItem(eachItem,
									eachItem.getCount());
						} else {
							eachItem.setItem(template);
							pc.getInventory().updateItem(eachItem,
									L1PcInventory.COL_ITEMID);
							pc.getInventory().saveItem(eachItem,
									L1PcInventory.COL_ITEMID);
							eachItem.setBless(eachItem.getBless() - 1);
							pc.getInventory().updateItem(eachItem,
									L1PcInventory.COL_BLESS);
							pc.getInventory().saveItem(eachItem,
									L1PcInventory.COL_BLESS);
						}
					}
					pc.getInventory().removeItem(useItem, 1);
					pc.sendPackets(new S_ServerMessage(155)); // \f1누군가가 도와 준
					// 것 같습니다.
				} else if (itemId == 41036) { // 풀
					int diaryId = l1iteminstance1.getItem().getItemId();
					if (diaryId >= 41038 && 41047 >= diaryId) {
						if ((_random.nextInt(99) + 1) <= Config.CREATE_CHANCE_DIARY) {
							createNewItem(pc, diaryId + 10, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(158,
									l1iteminstance1.getName())); // \f1%0이
							// 증발하고
							// 있지
							// 않게
							// 되었습니다.
						}
						pc.getInventory().removeItem(l1iteminstance1, 1);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else if (itemId == 40964) { // 흑마법 가루
					int historybookId = l1iteminstance1.getItem().getItemId();
					if (historybookId >= 41011 && 41018 >= historybookId) {
						if ((_random.nextInt(99) + 1) <= Config.CREATE_CHANCE_HISTORY_BOOK) {
							createNewItem(pc, historybookId + 8, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(158,
									l1iteminstance1.getName()));
						}
						pc.getInventory().removeItem(l1iteminstance1, 1);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40925) { // 정화의 일부
					int earingId = l1iteminstance1.getItem().getItemId();
					if (earingId >= 40987 && 40989 >= earingId) { // 저주해진 블랙 귀
						// 링
						if (_random.nextInt(100) < Config.CREATE_CHANCE_RECOLLECTION) {
							createNewItem(pc, earingId + 186, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(158,
									l1iteminstance1.getName())); // \f1%0이
							// 증발하고
							// 있지
							// 않게
							// 되었습니다.
						}
						pc.getInventory().removeItem(l1iteminstance1, 1);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else if (itemId >= 40926 && 40929 >= itemId) {
					// 신비적인 일부(1~4 단계)
					int earing2Id = l1iteminstance1.getItem().getItemId();
					int potion1 = 0;
					int potion2 = 0;
					if (earing2Id >= 41173 && 41184 >= earing2Id) {
						// 귀 링류
						if (itemId == 40926) {
							potion1 = 247;
							potion2 = 249;
						} else if (itemId == 40927) {
							potion1 = 249;
							potion2 = 251;
						} else if (itemId == 40928) {
							potion1 = 251;
							potion2 = 253;
						} else if (itemId == 40929) {
							potion1 = 253;
							potion2 = 255;
						}
						if (earing2Id >= (itemId + potion1)
								&& (itemId + potion2) >= earing2Id) {
							if ((_random.nextInt(99) + 1) < Config.CREATE_CHANCE_MYSTERIOUS) {
								createNewItem(pc, (earing2Id - 12), 1);
								pc.getInventory()
										.removeItem(l1iteminstance1, 1);
								pc.getInventory().removeItem(useItem, 1);
							} else {
								pc.sendPackets(new S_ServerMessage(160,
										l1iteminstance1.getName()));
								// \f1%0이%2 강렬하게%1 빛났습니다만, 다행히 무사하게 살았습니다.
								pc.getInventory().removeItem(useItem, 1);
							}
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1
							// 아무것도
							// 일어나지
							// 않았습니다.
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}

				} else if (itemId == 41029) { // 소환구 조각
					int dantesId = l1iteminstance1.getItem().getItemId();
					if (dantesId >= 41030 && 41034 >= dantesId) { // 소환공의 코어·
						// 각 단계
						if ((_random.nextInt(99) + 1) < Config.CREATE_CHANCE_DANTES) {
							createNewItem(pc, dantesId + 1, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(158,
									l1iteminstance1.getName())); // \f1%0이
							// 증발하고
							// 있지
							// 않게
							// 되었습니다.
						}
						pc.getInventory().removeItem(l1iteminstance1, 1);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
					// 스펠 스크롤
				} else if ((itemId >= 40859 && itemId <= 40898)
						&& itemId != 40863) { // 40863은 텔레포트 스크롤로서 처리된다
					if (spellsc_objid == pc.getId()
							&& useItem.getItem().getUseType() != 30) {
						// spell_buff
						pc.sendPackets(new S_ServerMessage(281)); // \f1마법이
						// 무효가
						// 되었습니다.
						return;
					}
					pc.getInventory().removeItem(useItem, 1);
					if (spellsc_objid == 0
							&& useItem.getItem().getUseType() != 0
							&& useItem.getItem().getUseType() != 26
							&& useItem.getItem().getUseType() != 27) {
						return;
						// 타겟이 없는 경우에 handleCommands송가 되기 (위해)때문에 여기서 return
						// handleCommands 쪽으로 판단＆처리해야 할 부분일지도 모른다
					}
					pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
					int skillid = itemId - 40858;
					L1SkillUse l1skilluse = new L1SkillUse();
					l1skilluse.handleCommands(client.getActiveChar(), skillid,
							spellsc_objid, spellsc_x, spellsc_y, null, 0,
							L1SkillUse.TYPE_SPELLSC);
					
				} else if (itemId == 5000677){ ///운영자세트
					if (pc.getInventory().getSize() > 100) {
						pc.sendPackets(new S_SystemMessage("소지창의 아이템갯수가 100개가 넘어서 사용할수 없습니다."));    
						return;
					}        
					if (pc.getInventory().getWeight240() > 180) { // 28 == 100% 
						pc.sendPackets(new S_SystemMessage("짐이 너무 무거워서 사용하실수 없습니다."));
						return;
					}
					if (pc.getInventory().checkItem(5000677, 1) ){
						//pc.getInventory().consumeItem(5000079, 1); //삭제안되게
						createNewItem3(pc, 40889, 2000, 0, 1); //카운터매직40879
						createNewItem3(pc, 40879, 1000, 0, 1); //카운터매직5000678		
						createNewItem3(pc, 140100, 50, 0, 0); // 축순간이동
						createNewItem3(pc, 600006, 100, 0, 1); // 이그니스의 주문서
						createNewItem3(pc, 600007, 100, 0, 1); // 운디네의 주문서
						createNewItem3(pc, 600008, 100, 0, 1); // 실프의 주문서
						createNewItem3(pc, 600009, 100, 0, 1); // 클레이의 주문서
						createNewItem3(pc, 42501, 1, 0, 1); // 스톰워크	
						createNewItem3(pc, 5000682, 1, 0, 1); // 메티스가호
						createNewItem3(pc, 46161, 1, 0, 1); //몬스터정리
						createNewItem3(pc, 5000683, 1, 0, 1); //정보막대
						createNewItem3(pc, 5000684, 1, 0, 1); //계정막대
						createNewItem3(pc, 5000685, 1, 0, 1); //장비막대
						//createNewItem3(pc, 5000686, 1, 0, 1); //채팅막대
						createNewItem3(pc, 46160, 1, 0, 1); //삭제막대
						pc.sendPackets(new S_SystemMessage(
						"\\fW운영자 아이템이 지급되었습니다."));
					}

				} else if (itemId == 500000) { // 4.5.6단계 마법서 상자
					if (pc.getInventory().checkItem(500000, 1)) {
						pc.getInventory().consumeItem(500000, 1);
						pc.getInventory().storeItem(40170, 1);
						pc.getInventory().storeItem(40171, 1);
						pc.getInventory().storeItem(40172, 1);
						pc.getInventory().storeItem(40173, 1);
						pc.getInventory().storeItem(40174, 1);
						pc.getInventory().storeItem(40175, 1);
						pc.getInventory().storeItem(40176, 1);
						pc.getInventory().storeItem(40177, 1);
						pc.getInventory().storeItem(40178, 1);
						pc.getInventory().storeItem(40179, 1);
						pc.getInventory().storeItem(40180, 1);
						pc.getInventory().storeItem(40181, 1);
						pc.getInventory().storeItem(40182, 1);
						pc.getInventory().storeItem(40183, 1);
						pc.getInventory().storeItem(40184, 1);
						pc.getInventory().storeItem(40185, 1);
						pc.getInventory().storeItem(40186, 1);
						pc.getInventory().storeItem(40187, 1);
						pc.getInventory().storeItem(40188, 1);
						pc.getInventory().storeItem(40189, 1);
						pc.getInventory().storeItem(40190, 1);
						pc.getInventory().storeItem(40191, 1);
						pc.getInventory().storeItem(40192, 1);
						pc.getInventory().storeItem(40193, 1);
						pc.sendPackets(new S_SystemMessage("4.5.6단계 마법서가 지급되었습니다.")); // 메세지
						// 출력
					}
				} else if (itemId == 500001) { // 정령의 수정 [공통]
					if (pc.getInventory().checkItem(500001, 1)) {
						pc.getInventory().consumeItem(500001, 1);
						pc.getInventory().storeItem(40232, 1);
						pc.getInventory().storeItem(40233, 1);
						pc.getInventory().storeItem(40234, 1);
						pc.getInventory().storeItem(40235, 1);
						pc.getInventory().storeItem(40236, 1);
						pc.getInventory().storeItem(40237, 1);
						pc.getInventory().storeItem(40239, 1);
						pc.getInventory().storeItem(40240, 1);
						pc.getInventory().storeItem(40241, 1);
						pc.sendPackets(new S_SystemMessage("정령의 수정이 지급 되었습니다.")); // 메세지
						// 출력
					}
				} else if (itemId == 500002) { // 흑정령 수정
					if (pc.getInventory().checkItem(500002, 1)) {
						pc.getInventory().consumeItem(500002, 1);
						pc.getInventory().storeItem(40265, 1);
						pc.getInventory().storeItem(40266, 1);
						pc.getInventory().storeItem(40269, 1);
						pc.getInventory().storeItem(40267, 1);
						pc.getInventory().storeItem(40268, 1);
						pc.getInventory().storeItem(40279, 1);
						pc.getInventory().storeItem(40270, 1);
						pc.getInventory().storeItem(40271, 1);
						pc.getInventory().storeItem(40272, 1);
						pc.getInventory().storeItem(40273, 1);
						pc.getInventory().storeItem(40274, 1);
						pc.getInventory().storeItem(40275, 1);
						pc.getInventory().storeItem(40276, 1);
						pc.getInventory().storeItem(40277, 1);
						pc.getInventory().storeItem(40278, 1);
						pc.getInventory().storeItem(40279, 1);
						pc.sendPackets(new S_SystemMessage("흑정령 수정이 지급 되었습니다.")); // 메세지
						// 출력
					}
				} else if (itemId == 500003) { // 환술사 수정 지급상자
					if (pc.getInventory().checkItem(500003, 1)) {
						pc.getInventory().consumeItem(500003, 1);
						pc.getInventory().storeItem(439000, 1);
						pc.getInventory().storeItem(439001, 1);
						pc.getInventory().storeItem(439002, 1);
						pc.getInventory().storeItem(439003, 1);
						pc.getInventory().storeItem(439004, 1);
						pc.getInventory().storeItem(439005, 1);
						pc.getInventory().storeItem(439006, 1);
						pc.getInventory().storeItem(439007, 1);
						pc.getInventory().storeItem(439008, 1);
						pc.getInventory().storeItem(439009, 1);
						pc.getInventory().storeItem(439010, 1);
						pc.getInventory().storeItem(439011, 1);
						pc.getInventory().storeItem(439012, 1);
						pc.getInventory().storeItem(439013, 1);
						pc.getInventory().storeItem(439014, 1);
						pc.getInventory().storeItem(439015, 1);
						pc.getInventory().storeItem(439016, 1);
						pc.getInventory().storeItem(439017, 1);
						pc.getInventory().storeItem(439018, 1);
						pc.getInventory().storeItem(439019, 1);
						pc.sendPackets(new S_SystemMessage("기억의 수정이 지급 되었습니다.")); // 메세지
						// 출력
					}
				} else if (itemId == 500004) { // 용기사 서판 지급 상자
					if (pc.getInventory().checkItem(500004, 1)) {
						pc.getInventory().consumeItem(500004, 1);
						pc.getInventory().storeItem(439100, 1);
						pc.getInventory().storeItem(439101, 1);
						pc.getInventory().storeItem(439102, 1);
						pc.getInventory().storeItem(439103, 1);
						pc.getInventory().storeItem(439104, 1);
						pc.getInventory().storeItem(439105, 1);
						pc.getInventory().storeItem(439106, 1);
						pc.getInventory().storeItem(439107, 1);
						pc.getInventory().storeItem(439108, 1);
						pc.getInventory().storeItem(439109, 1);
						pc.getInventory().storeItem(439110, 1);
						pc.getInventory().storeItem(439111, 1);
						pc.getInventory().storeItem(439112, 1);
						pc.getInventory().storeItem(439113, 1);
						pc.getInventory().storeItem(439114, 1);
						pc.sendPackets(new S_SystemMessage("용기사 서판이 지급 되었습니다.")); // 메세지
						// 출력
					}
				} else if (itemId == 500005) { // 기사 기술서 지급 상자
					if (pc.getInventory().checkItem(500005, 1)) {
						pc.getInventory().consumeItem(500005, 1);
						pc.getInventory().storeItem(40164, 1);
						pc.getInventory().storeItem(40165, 1);
						pc.sendPackets(new S_SystemMessage("기사 기술서 가  지급 되었습니다.")); // 메세지 출력
					}
				} else if (itemId == 500006) { // 군주 기술 지급 상자
					if (pc.getInventory().checkItem(500006, 1)) {
						pc.getInventory().consumeItem(500006, 1);
						pc.getInventory().storeItem(40226, 1);
						pc.getInventory().storeItem(40227, 1);
						pc.getInventory().storeItem(40228, 1);
						pc.getInventory().storeItem(40229, 1);
						pc.getInventory().storeItem(40230, 1);
						pc.getInventory().storeItem(40231, 1);
						pc.sendPackets(new S_SystemMessage("군주 기술서 가 지급 되었습니다.")); // 메세지 출력
					}
					
		
				/*	} else if (itemId == 90729) {// 악세사리
						if (pc.getInventory().checkItem(90729, 1)) { // 체크 되는 아이템과																	
							pc.getInventory().consumeItem(90729, 1); // 삭제되는 아이템과 수량	
							createNewItem2(pc, 420013, 1, 0); //제브송곳니
							createNewItem2(pc, 20314, 1, 0);//에반
							createNewItem2(pc, 420009, 1, 0);//힘반
							createNewItem2(pc, 420009, 1, 0);//힘반
							createNewItem2(pc, 420008, 1, 0); //인반
							createNewItem2(pc, 420008, 1, 0); //인반	
							createNewItem2(pc, 20279, 1, 0); //라반
							createNewItem2(pc, 20279, 1, 0); //라반	
							createNewItem2(pc, 420007, 1, 0); //테벨
							createNewItem2(pc, 20257, 1, 0); //불목
							pc.sendPackets(new S_SystemMessage("\\fW1악세사리 지급완료"));
						}
						
	           	} else if (itemId == 90721) {// 기사1차
					if (pc.getInventory().checkItem(90721, 1)) { // 체크 되는 아이템과																	
						pc.getInventory().consumeItem(90721, 1); // 삭제되는 아이템과 수량	
					//	if (pc.isKnight()) {
							createNewItem2(pc, 62, 1, 9); // 무양
							createNewItem2(pc, 20011, 1, 7); // 마투
							createNewItem2(pc, 490015, 1, 7); //스턴 내성의 티셔츠							
							createNewItem2(pc, 20095, 1, 0); //고판
							createNewItem2(pc, 20056, 1, 7); // 마법망토
							createNewItem2(pc, 20200, 1, 7); // 마부
							createNewItem2(pc, 20178, 1, 7); // 암살군왕의장갑												
							createNewItem2(pc, 420003, 1, 2);//고투사																		
							createNewItem2(pc, 20422, 1, 0);//빛고목
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20317, 1, 0); //오벨
							createNewItem2(pc, 21027, 1, 0); // 노예의귀걸이							
							pc.sendPackets(new S_SystemMessage("\\fW1차 기사 클래스 아이템이 지급되었습니다."));
					}
					} else if (itemId == 90723) {// 군주1차
						if (pc.getInventory().checkItem(90723, 1)) { // 체크 되는 아이템과																	
							pc.getInventory().consumeItem(90723, 1); // 삭제되는 아이템과 수량	
							createNewItem2(pc, 9, 1, 9); // 오단
							createNewItem2(pc, 20011, 1, 7); // 마투
							createNewItem2(pc, 490015, 1, 7); // 스턴 내성의 티셔츠
							createNewItem2(pc, 20094, 1, 0); // 고비늘
							createNewItem2(pc, 20056, 1, 7); // 마법망토																				
							createNewItem2(pc, 20200, 1, 7); // 마수군왕의 부츠																											
							createNewItem2(pc, 20178, 1, 7); // 암살군왕의 장갑
							createNewItem2(pc, 420003, 1, 2);//고투사
							createNewItem2(pc, 20422, 1, 0);//빛고목
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20317, 1, 0); //오벨
							createNewItem2(pc, 21027, 1, 0); // 노예의귀걸이		
							pc.sendPackets(new S_SystemMessage("\\fW1차 군주 클래스 아이템이 지급되었습니다."));
						}
					} else if (itemId == 90724) {// 마법사1차
						if (pc.getInventory().checkItem(90724, 1)) { // 체크 되는 아이템과																	
							pc.getInventory().consumeItem(90724, 1); // 삭제되는 아이템과 수량		
							createNewItem2(pc, 126, 1, 9); // 마나의지팡이
							createNewItem2(pc, 119, 1, 0); // 데지
							createNewItem2(pc, 20011, 1, 7); // 마투
							createNewItem2(pc, 490015, 1, 7); // 스턴 내성의 티셔츠
							createNewItem2(pc, 20093, 1, 0); // 고롭	
							createNewItem2(pc, 20056, 1, 7); // 마망																		
							createNewItem2(pc, 20055, 1, 7); // 마나망토
							createNewItem2(pc, 20018, 1, 7); // 메키
							createNewItem2(pc, 20218, 1, 7); // 흑샌	
							createNewItem2(pc, 20178, 1, 7); // 암장	
							createNewItem2(pc, 420000, 1, 2); // 명가더
							createNewItem2(pc, 20422, 1, 0);//빛고목
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20317, 1, 0); //오벨
							createNewItem2(pc, 21027, 1, 0); // 노예의귀걸이		
							pc.sendPackets(new S_SystemMessage("\\fW1차 법사 클래스 아이템이 지급되었습니다."));
						}
					} else if (itemId == 90725) {// 요정1차
						if (pc.getInventory().checkItem(90725, 1)) { // 체크 되는 아이템과																	
							pc.getInventory().consumeItem(90725, 1); // 삭제되는 아이템과 수량	
							createNewItem2(pc, 189, 1, 8); // 흑왕궁
							createNewItem2(pc, 20011, 1, 7); // 마투
							createNewItem2(pc, 490015, 1, 7); // 스턴 내성의 티셔츠
							createNewItem2(pc, 20092, 1, 0); // 고가죽
							createNewItem2(pc, 20056, 1, 7); // 마법망토																				
							createNewItem2(pc, 20200, 1, 7); // 마수군왕의 부츠	
							createNewItem2(pc, 20167, 1, 7); // 리장
							createNewItem2(pc, 20314, 1, 0); // 에이션트 자이언트의 반지						
							createNewItem2(pc, 20422, 1, 0);//빛고목
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20317, 1, 0); //오벨
							createNewItem2(pc, 21027, 1, 0); // 노예의귀걸이		
							pc.sendPackets(new S_SystemMessage("\\fW1차 요정 클래스 아이템이 지급되었습니다."));
						}
					} else if (itemId == 90726) {// 다크엘프1차
						if (pc.getInventory().checkItem(90726, 1)) { // 체크 되는 아이템과																	
							pc.getInventory().consumeItem(90726, 1); // 삭제되는 아이템과 수량	
							createNewItem2(pc, 84, 1, 8); // 흑왕도
							createNewItem2(pc, 20011, 1, 7); // 마투
							createNewItem2(pc, 490015, 1, 7); // 스턴 내성의 티셔츠
							createNewItem2(pc, 20092, 1, 0); // 고가죽
							createNewItem2(pc, 20056, 1, 7); // 마법망토							
							createNewItem2(pc, 20200, 1, 7); // 마수군왕의 부츠	
							createNewItem2(pc, 20178, 1, 7); // 암살군왕의장갑
							createNewItem2(pc, 420003, 1, 2);//고투사
							createNewItem2(pc, 20314, 1, 0); // 에이션트 자이언트의 반지							
							createNewItem2(pc, 20422, 1, 0);//빛고목
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20317, 1, 0); //오벨
							createNewItem2(pc, 21027, 1, 0); // 노예의귀걸이					
							pc.sendPackets(new S_SystemMessage("\\fW1차 다크엘프 클래스아이템이 지급되었습니다."));
						}
					} else if (itemId == 90727) {// 용기사1차
						if (pc.getInventory().checkItem(90727, 1)) { // 체크 되는 아이템과																	
							pc.getInventory().consumeItem(90727, 1); // 삭제되는 아이템과 수량	
							createNewItem2(pc, 410001, 1, 9); // 파멸자의 체인소드
							createNewItem2(pc, 20011, 1, 7); // 마투
							createNewItem2(pc, 490015, 1, 7); // 스턴 내성의 티셔츠
							createNewItem2(pc, 20094, 1, 0); // 고비늘
							createNewItem2(pc, 20056, 1, 7); // 마법망토							
							createNewItem2(pc, 20200, 1, 7); // 마수군왕의 부츠									
							createNewItem2(pc, 20178, 1, 7); // 암살군왕의 장갑
							createNewItem2(pc, 420003, 1, 2);//고투사
							createNewItem2(pc, 20314, 1, 0); // 에이션트 자이언트의 반지
							createNewItem2(pc, 20422, 1, 0);//빛고목
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20317, 1, 0); //오벨
							createNewItem2(pc, 21027, 1, 0); // 노예의귀걸이							
							pc.sendPackets(new S_SystemMessage("\\fW1차 용기사 클래스아이템이 지급되었습니다."));
						}
					} else if (itemId == 90728) {// 환술사1차
						if (pc.getInventory().checkItem(90728, 1)) { // 체크 되는 아이템과																	
							pc.getInventory().consumeItem(90728, 1); // 삭제되는 아이템과 수량	
							createNewItem2(pc, 410004, 1, 9); // 흑키
							createNewItem2(pc, 20011, 1, 7); // 마투
							createNewItem2(pc, 490015, 1, 7); // 스턴 내성의 티셔츠
							createNewItem2(pc, 20092, 1, 0); // 고가죽
							createNewItem2(pc, 20056, 1, 7); // 마법망토														
							createNewItem2(pc, 20200, 1, 7); // 마수군왕의 부츠
							createNewItem2(pc, 20178, 1, 7); // 암살군왕의장갑
							createNewItem2(pc, 420003, 1, 2);//고투사
							createNewItem2(pc, 20422, 1, 0);//빛고목
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20280, 1, 0);//멸마
							createNewItem2(pc, 20317, 1, 0); //오벨
							createNewItem2(pc, 21027, 1, 0); // 노예의귀걸이					
							pc.sendPackets(new S_SystemMessage("\\fW1차 환술사 클래스 아이템이 지급되었습니다."));
						}
					}*/
					
				} else if (itemId >= 40373 && itemId <= 40384 // 지도 각종
						|| itemId >= 40385 && itemId <= 40390) {
					pc.sendPackets(new S_UseMap(pc, useItem.getId(), useItem
							.getItem().getItemId()));
				} else if (itemId == 40314 || itemId == 40316) { // 펫의 아뮤렛트
					if (pc.getInventory().checkItem(41160)) { // 소환의 피리
						if (withdrawPet(pc, itemObjid)) {
							pc.getInventory().consumeItem(41160, 1);
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else if (itemId == 40315) { // 펫의 피리
					pc.sendPackets(new S_Sound(437));
					Broadcaster.broadcastPacket(pc, new S_Sound(437));
					Object[] petList = pc.getPetList().values().toArray();
					for (Object petObject : petList) {
						if (petObject instanceof L1PetInstance) { // 펫
							L1PetInstance pet = (L1PetInstance) petObject;
							pet.call();
						}
					}
				} else if (itemId == 40493) { // 매직 플룻
					pc.sendPackets(new S_Sound(165));
					Broadcaster.broadcastPacket(pc, new S_Sound(165));
					L1GuardianInstance guardian = null;
					for (L1Object visible : pc.getNearObjects()
							.getKnownObjects()) {
						if (visible instanceof L1GuardianInstance) {
							guardian = (L1GuardianInstance) visible;
							if (guardian.getNpcTemplate().get_npcId() == 70850) { // 빵
								if (createNewItem(pc, 88, 1)) {
									pc.getInventory().removeItem(useItem, 1);
								}
							}
						}
					}
				} else if (itemId == 40325) {
					if (pc.getInventory().checkItem(40318, 1)) {
						int gfxid = 3237 + _random.nextInt(2);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc
								.getId(), gfxid));
						pc.getInventory().consumeItem(40318, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else if (itemId == 40326) {
					if (pc.getInventory().checkItem(40318, 1)) {
						int gfxid = 3229 + _random.nextInt(3);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc
								.getId(), gfxid));
						pc.getInventory().consumeItem(40318, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else if (itemId == 40327) {
					if (pc.getInventory().checkItem(40318, 1)) {
						int gfxid = 3241 + _random.nextInt(4);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc
								.getId(), gfxid));
						pc.getInventory().consumeItem(40318, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else if (itemId == 40328) {
					if (pc.getInventory().checkItem(40318, 1)) {
						int gfxid = 3204 + _random.nextInt(6);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc
								.getId(), gfxid));
						pc.getInventory().consumeItem(40318, 1);
					} else {
						// \f1 아무것도 일어나지 않았습니다.
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == L1ItemId.CHARACTER_REPAIR_SCROLL) {
					Connection connection = null;
					connection = L1DatabaseFactory.getInstance()
							.getConnection();
					PreparedStatement preparedstatement = connection
					.prepareStatement("UPDATE characters SET LocX=33087,LocY=33399,MapID=4 WHERE account_name=? and MapID not in (99,997,5166)");
					preparedstatement.setString(1, client.getAccountName());
					preparedstatement.execute();
					preparedstatement.close();
					connection.close();
					pc.getInventory().removeItem(useItem, 1);
					pc.sendPackets(new S_SystemMessage("모든 케릭터의 좌표가 정상적으로 복구 되었습니다."));

				} else if (itemId >= 40903 && itemId <= 40908) { // 각종 약혼 반지
					L1PcInstance partner = null;
					boolean partner_stat = false;
					if (pc.getPartnerId() != 0) { // 결혼중
						partner = (L1PcInstance) L1World.getInstance()
								.findObject(pc.getPartnerId());
						if (partner != null && partner.getPartnerId() != 0
								&& pc.getPartnerId() == partner.getId()
								&& partner.getPartnerId() == pc.getId()) {
							partner_stat = true;
						}
					} else {
						pc.sendPackets(new S_ServerMessage(662)); // \f1당신은
						// 결혼하지
						// 않았습니다.
						return;
					}

					if (useItem.getChargeCount() <= 0) {
						return;
					}
					if (pc.getMapId() == 666) {
						return;
					}
					if (partner_stat) {
						boolean castle_area = L1CastleLocation
								.checkInAllWarArea(partner.getX(), partner
										.getY(), partner.getMapId());

						if ((partner.getMapId() == 0 || partner.getMapId() == 4
								|| partner.getMapId() == 304 || partner
								.getMapId() == 70)
								&& castle_area == false) {
							useItem
									.setChargeCount(useItem.getChargeCount() - 1);
							pc.getInventory().updateItem(useItem,
									L1PcInventory.COL_CHARGE_COUNT);
							L1Teleport.teleport(pc, partner.getX(), partner
									.getY(), partner.getMapId(), 5, true);
						} else {
							pc.sendPackets(new S_ServerMessage(547)); // \f1당신의
							// 파트너는
							// 지금
							// 당신이 갈
							// 수 없는
							// 곳에서
							// 플레이중입니다.
						}
					} else {
						pc.sendPackets(new S_ServerMessage(546)); // \f1당신의
						// 파트너는 지금
						// 플레이를 하고
						// 있지 않습니다.
					}
				} else if (itemId == 40555) { // 비밀의 방의 키
					// 오림 방
					if (pc.isKnight()
							&& (pc.getX() >= 32806 && pc.getX() <= 32814)
							&& (pc.getY() >= 32798 && pc.getY() <= 32807)
							&& pc.getMapId() == 13) {
						L1Teleport.teleport(pc, 32815, 32810, (short) 13, 5,
								false);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else if (itemId == 40417) { // 정령의결정
					if ((pc.getX() >= 32667 && pc.getX() <= 32673)
							&& (pc.getY() >= 32978 && pc.getY() <= 32984)
							&& pc.getMapId() == 440) {
						L1Teleport.teleport(pc, 32922, 32812, (short) 430, 5,
								true);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else if (itemId == 40566) { // 신비적인 쉘
					// 상아의 탑의 마을의 남쪽에 있는 매직 스퀘어의 좌표
					if (pc.isElf()
							&& (pc.getX() >= 33971 && pc.getX() <= 33975)
							&& (pc.getY() >= 32324 && pc.getY() <= 32328)
							&& pc.getMapId() == 4
							&& !pc.getInventory().checkItem(40548)) { // 망령의
						// 봉투
						boolean found = false;
						L1MonsterInstance mob = null;
						for (L1Object obj : L1World.getInstance()
								.getVisibleObjects(4).values()) {
							if (obj instanceof L1MonsterInstance) {
								mob = (L1MonsterInstance) obj;
								if (mob != null) {
									if (mob.getNpcTemplate().get_npcId() == 45300) {
										found = true;
										break;
									}
								}
							}
						}
						if (found) {
							pc.sendPackets(new S_ServerMessage(79)); // \f1
							// 아무것도
							// 일어나지
							// 않았습니다.
						} else {
							L1SpawnUtil.spawn(pc, 45300, 0, 0, false); // 고대인의
							// 망령
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else if (itemId == 40557) {
					if (pc.getX() == 32620 && pc.getY() == 32641
							&& pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45883) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45883, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40563) {
					if (pc.getX() == 32730 && pc.getY() == 32426
							&& pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45884) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45884, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40561) {
					if (pc.getX() == 33046 && pc.getY() == 32806
							&& pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45885) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45885, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40560) {
					if (pc.getX() == 32580 && pc.getY() == 33260
							&& pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45886) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45886, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40562) {
					if (pc.getX() == 33447 && pc.getY() == 33476
							&& pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45887) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45887, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40559) {
					if (pc.getX() == 34215 && pc.getY() == 33195
							&& pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45888) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45888, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
					
				} else if (itemId == 5000682) { // 9월20일메티스의 가호
					int objid = pc.getId();
					pc.sendPackets(new S_SkillSound(objid, 4856)); //3944
					Broadcaster.broadcastPacket(pc, new S_SkillSound(objid, 4856));
					for (L1PcInstance tg : L1World.getInstance(). getVisiblePlayer(pc)) {
						if (tg.getCurrentHp() == 0 && tg.isDead()) {
							tg.sendPackets(new S_SystemMessage("GM이 부활을 해주었습니다. "));
							Broadcaster.broadcastPacket(tg, new S_SkillSound(tg.getId(), 3944));
							tg.sendPackets(new S_SkillSound(tg.getId(), 3944));
							// 축복된 부활 스크롤과 같은 효과
							tg.setTempID(objid);
							tg.sendPackets(new S_Message_YN(322, "")); // 또 부활하고 싶습니까? (Y/N)
						} else {
							tg.sendPackets(new S_SystemMessage("GM이 HP,MP를 회복해주었습니다."));
							Broadcaster.broadcastPacket(tg, new S_SkillSound(tg.getId(), 832));
							tg.sendPackets(new S_SkillSound(tg.getId(), 832));
							tg.setCurrentHp(tg.getMaxHp());
							tg.setCurrentMp(tg.getMaxMp());
						}
					}
				} else if (itemId == 46161) { // 10월14일몬스터청소기
					for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 10)) { 
						if (obj instanceof L1MonsterInstance) { // 몬스터라면
							L1NpcInstance npc = (L1NpcInstance) obj;
							npc.receiveDamage(pc, 50000); // 데미지
							if (npc.getCurrentHp() <= 0) {
							} else {
							}
						} else if (obj instanceof L1PcInstance) { // pc라면
							L1PcInstance Player = (L1PcInstance) obj;
							Player.receiveDamage(Player, 0, false); // 데미지
							if (Player.getCurrentHp() <= 0) {
							} else {
							}
						}
					}
					
				} else if (itemId == 40558) {
					if (pc.getX() == 33513 && pc.getY() == 32890
							&& pc.getMapId() == 4) {
						L1NpcInstance object = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1NpcInstance) {
								object = (L1NpcInstance) obj;
								if (object.getNpcTemplate().get_npcId() == 45889) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}

						}
						L1SpawnUtil.spawn(pc, 45889, 0, 300000, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40572) {
					if (pc.getX() == 32778 && pc.getY() == 32738
							&& pc.getMapId() == 21) {
						L1Teleport.teleport(pc, 32781, 32728, (short) 21, 5,
								true);
					} else if (pc.getX() == 32781 && pc.getY() == 32728
							&& pc.getMapId() == 21) {
						L1Teleport.teleport(pc, 32778, 32738, (short) 21, 5,
								true);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40009) {// 추방막대
					int chargeCount = useItem.getChargeCount();
					if (chargeCount <= 0) {
						pc.sendPackets(new S_ServerMessage(79));// \f1 아무것도 일어나지
						// 않았습니다.
						return;
					}

					L1Object target = L1World.getInstance().findObject(
							spellsc_objid);
					if (target != null) {
						int heding = CharPosUtil.targetDirection(pc, spellsc_x,
								spellsc_y);
						pc.getMoveState().setHeading(heding);
						pc.sendPackets(new S_AttackPacket(pc, 0,
								ActionCodes.ACTION_Wand));
						Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc,
								0, ActionCodes.ACTION_Wand));

						if (target instanceof L1PcInstance) {
							L1PcInstance cha = (L1PcInstance) target;
							if (cha.getSkillEffectTimerSet().hasSkillEffect(
									ERASE_MAGIC)) {
								cha.getSkillEffectTimerSet().removeSkillEffect(
										ERASE_MAGIC);
							}
						}
					}

					useItem.setChargeCount(useItem.getChargeCount() - 1);
					pc.getInventory().updateItem(useItem,
							L1PcInventory.COL_CHARGE_COUNT);

				} else if (itemId == L1ItemId.ICECAVE_KEY) {
					L1Object t = L1World.getInstance()
							.findObject(spellsc_objid);
					L1DoorInstance door = (L1DoorInstance) t;
					if (pc.getLocation()
							.getTileLineDistance(door.getLocation()) > 3) {
						return;
					}
					if (door.getDoorId() >= 5000 && door.getDoorId() <= 5009) {
						if (door != null
								&& door.getOpenStatus() == ActionCodes.ACTION_Close) {
							door.open();
							pc.getInventory().removeItem(useItem, 1);
						}
					}
				} else if (itemId >= 40289 && itemId <= 40297 //&& itemId <= 123007//숨계부적
						|| itemId >= 30260 && itemId <= 30279|| itemId == 590038 || itemId == 590039) {//오만의탑11~91층 부적
					useToiTeleportAmulet(pc, itemId, useItem);
				
					
				} else if (itemId >= 40280 && itemId <= 40288) {
				//} else if (itemId >= 40280 && itemId >= 123007 && itemId <= 40288) {//봉인
					pc.getInventory().removeItem(useItem, 1);
					L1ItemInstance item = pc.getInventory().storeItem(itemId + 9, 1);
					if (item != null) {
						pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
					}				
				} else if (itemId == 40070) {
					pc.sendPackets(new S_ServerMessage(76, useItem.getLogName()));
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41301) { // 샤이닝렛드핏슈
					int chance = _random.nextInt(10);
					if (chance >= 0 && chance < 5) {
						UseHeallingPotion(pc, 15, 189);
					} else if (chance >= 5 && chance < 9) {
						createNewItem(pc, 40019, 1);
					} else if (chance >= 9) {
						int gemChance = _random.nextInt(3);
						if (gemChance == 0) {
							createNewItem(pc, 40045, 1);
						} else if (gemChance == 1) {
							createNewItem(pc, 40049, 1);
						} else if (gemChance == 2) {
							createNewItem(pc, 40053, 1);
						}
					}
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41302) { // 샤이닝그린핏슈
					int chance = _random.nextInt(3);
					if (chance >= 0 && chance < 5) {
						UseHeallingPotion(pc, 15, 189);
					} else if (chance >= 5 && chance < 9) {
						createNewItem(pc, 40018, 1);
					} else if (chance >= 9) {
						int gemChance = _random.nextInt(3);
						if (gemChance == 0) {
							createNewItem(pc, 40047, 1);
						} else if (gemChance == 1) {
							createNewItem(pc, 40051, 1);
						} else if (gemChance == 2) {
							createNewItem(pc, 40055, 1);
						}
					}
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41303) { // 샤이닝브르핏슈
					int chance = _random.nextInt(3);
					if (chance >= 0 && chance < 5) {
						UseHeallingPotion(pc, 15, 189);
					} else if (chance >= 5 && chance < 9) {
						createNewItem(pc, 40015, 1);
					} else if (chance >= 9) {
						int gemChance = _random.nextInt(3);
						if (gemChance == 0) {
							createNewItem(pc, 40046, 1);
						} else if (gemChance == 1) {
							createNewItem(pc, 40050, 1);
						} else if (gemChance == 2) {
							createNewItem(pc, 40054, 1);
						}
					}
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41304) { // 샤이닝화이트핏슈
					int chance = _random.nextInt(3);
					if (chance >= 0 && chance < 5) {
						UseHeallingPotion(pc, 15, 189);
					} else if (chance >= 5 && chance < 9) {
						createNewItem(pc, 40021, 1);
					} else if (chance >= 9) {
						int gemChance = _random.nextInt(3);
						if (gemChance == 0) {
							createNewItem(pc, 40044, 1);
						} else if (gemChance == 1) {
							createNewItem(pc, 40048, 1);
						} else if (gemChance == 2) {
							createNewItem(pc, 40052, 1);
						}
					}
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 40615) { // 그림자의 신전 2층의 열쇠
					if ((pc.getX() >= 32701 && pc.getX() <= 32705)
							&& (pc.getY() >= 32894 && pc.getY() <= 32898)
							&& pc.getMapId() == 522) { // 그림자의 신전 1F
						L1Teleport.teleport(pc, ((L1EtcItem) useItem.getItem())
								.get_locx(), ((L1EtcItem) useItem.getItem())
								.get_locy(), ((L1EtcItem) useItem.getItem())
								.get_mapid(), 5, true);
					} else {
						// \f1 아무것도 일어나지 않았습니다.
						pc.sendPackets(new S_ServerMessage(79));
					}
				}else if (itemId == 437011 || itemId == 50005) { // 아이템번호..
					진주포션사용(pc);
					pc.getInventory().consumeItem(437011, 1);// 해당아이템 삭제
					pc.getInventory().consumeItem(50005, 1);// 해당아이템 삭제
					pc.sendPackets(new S_ServerMessage(1065)); // 드진 멘트

				} else if (itemId == 40616 || itemId == 40782
						|| itemId == 40783) { // 그림자의 신전 3층의 열쇠
					if ((pc.getX() >= 32698 && pc.getX() <= 32702)
							&& (pc.getY() >= 32894 && pc.getY() <= 32898)
							&& pc.getMapId() == 523) { // 그림자의 신전 2층
						L1Teleport.teleport(pc, ((L1EtcItem) useItem.getItem())
								.get_locx(), ((L1EtcItem) useItem.getItem())
								.get_locy(), ((L1EtcItem) useItem.getItem())
								.get_mapid(), 5, true);
					} else {
						// \f1 아무것도 일어나지 않았습니다.
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40692) { // 완성된 보물의 지도
					if (pc.getInventory().checkItem(40621)) {
						// \f1 아무것도 일어나지 않았습니다.
						pc.sendPackets(new S_ServerMessage(79));
					} else if ((pc.getX() >= 32856 && pc.getX() <= 32858)
							&& (pc.getY() >= 32857 && pc.getY() <= 32858)
							&& pc.getMapId() == 443) { // 해적섬의 지하 감옥 3층
						L1Teleport.teleport(pc, ((L1EtcItem) useItem.getItem())
								.get_locx(), ((L1EtcItem) useItem.getItem())
								.get_locy(), ((L1EtcItem) useItem.getItem())
								.get_mapid(), 5, true);
					} else {
						// \f1 아무것도 일어나지 않았습니다.
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 41146) { // 드로몬드의 초대장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei001"));
				} else if (itemId == 41209) { // 포피레아의 의뢰서
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei002"));
				} else if (itemId == 41210) { // 연마재
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei003"));
				} else if (itemId == 436000) { // 허브
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei004"));
				} else if (itemId == 41212) { // 특제 캔디
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei005"));
				} else if (itemId == 41213) { // 티미의 바스켓
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei006"));
				} else if (itemId == 41214) { // 운의 증거
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei012"));
				} else if (itemId == 41215) { // 지의 증거
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei010"));
				} else if (itemId == 41216) { // 력의 증거
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei011"));
				} else if (itemId == 41222) { // 마슈르
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei008"));
				} else if (itemId == 41223) { // 무기의 파편
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei007"));
				} else if (itemId == 41224) { // 배지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei009"));
				} else if (itemId == 41225) { // 케스킨의 발주서
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei013"));
				} else if (itemId == 41226) { // 파고의 약
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei014"));
				} else if (itemId == 41227) { // 알렉스의 소개장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei033"));
				} else if (itemId == 41228) { // 율법박사의 부적
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei034"));
				} else if (itemId == 41229) { // 스켈리턴의 머리
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei025"));
				} else if (itemId == 41230) { // 지난에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei020"));
				} else if (itemId == 41231) { // 맛티에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei021"));
				} else if (itemId == 41233) { // 케이이에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei019"));
				} else if (itemId == 41234) { // 뼈가 들어온 봉투
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei023"));
				} else if (itemId == 41235) { // 재료표
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei024"));
				} else if (itemId == 41236) { // 본아챠의 뼈
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei026"));
				} else if (itemId == 41237) { // 스켈리턴 스파이크의 뼈
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei027"));
				} else if (itemId == 41239) { // 브트에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei018"));
				} else if (itemId == 41240) { // 페다에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei022"));
				} else if (itemId == 41060) { // 노나메의 추천서
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "nonames"));
				} else if (itemId == 41061) { // 조사단의 증서：에르프 지역 두다마라카메
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kames"));
				} else if (itemId == 41062) { // 조사단의 증서：인간 지역 네르가바크모
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bakumos"));
				} else if (itemId == 41063) { // 조사단의 증서：정령 지역 두다마라브카
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bukas"));
				} else if (itemId == 41064) { // 조사단의 증서：오크 지역 네르가후우모
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "huwoomos"));
				} else if (itemId == 41065) { // 조사단의 증서：조사단장 아트바노아
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "noas"));
				} else if (itemId == 41356) { // 파룸의 자원 리스트
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rparum3"));
				} else if (itemId == 40701) { // 작은 보물의 지도
					if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 1) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"firsttmap"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 2) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"secondtmapa"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 3) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"secondtmapb"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 4) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"secondtmapc"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 5) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"thirdtmapd"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 6) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"thirdtmape"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 7) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"thirdtmapf"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 8) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"thirdtmapg"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 9) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"thirdtmaph"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 10) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"thirdtmapi"));
					}
				} else if (itemId == 40663) { // 아들의 편지
					pc
							.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"sonsletter"));
				} else if (itemId == 40630) { // 디에고의 낡은 일기
					pc
							.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"diegodiary"));
				} else if (itemId == 41340) { // 용병단장 티온
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tion"));
				} else if (itemId == 41317) { // 랄슨의 추천장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rarson"));
				} else if (itemId == 41318) { // 쿠엔의 메모
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kuen"));
				} else if (itemId == 41329) { // 박제의 제작 의뢰서
					pc
							.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"anirequest"));
				} else if (itemId == 41346) { // 로빈훗드의 메모 1
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"robinscroll"));
				} else if (itemId == 41347) { // 로빈훗드의 메모 2
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"robinscroll2"));
				} else if (itemId == 41348) { // 로빈훗드의 소개장
					pc
							.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"robinhood"));
				} else if (itemId == 41007) {
					pc
							.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"erisscroll"));
				} else if (itemId == 41009) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"erisscroll2"));
				} else if (itemId == 41019) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory1"));
				} else if (itemId == 41020) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory2"));
				} else if (itemId == 41021) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory3"));
				} else if (itemId == 41022) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory4"));
				} else if (itemId == 41023) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory5"));
				} else if (itemId == 41024) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory6"));
				} else if (itemId == 41025) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory7"));
				} else if (itemId == 41026) {
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory8"));
				} else if (itemId == 210087) { // 프로켈의 첫 번째 지령서
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "first_p"));
				} else if (itemId == 210093) { // 실레인의 첫 번째 편지
					pc
							.sendPackets(new S_NPCTalkReturn(pc.getId(),
									"silrein1lt"));
				} else if (itemId == L1ItemId.TIKAL_CALENDAR) {
					if (CrockSystem.getInstance().isOpen())
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"tcalendaro"));
					else
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"tcalendarc"));
				} else if (itemId == 41208) { // 져 가는 영혼
					if ((pc.getX() >= 32844 && pc.getX() <= 32845)
							&& (pc.getY() >= 32693 && pc.getY() <= 32694)
							&& pc.getMapId() == 550) { // 배의 묘지:지상층
						L1Teleport.teleport(pc, ((L1EtcItem) useItem.getItem())
								.get_locx(), ((L1EtcItem) useItem.getItem())
								.get_locy(), ((L1EtcItem) useItem.getItem())
								.get_mapid(), 5, true);
					} else {
						// \f1 아무것도 일어나지 않았습니다.
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else if (itemId == 40700) { // 실버 플룻
					pc.sendPackets(new S_Sound(10));
					Broadcaster.broadcastPacket(pc, new S_Sound(10));
					if ((pc.getX() >= 32619 && pc.getX() <= 32623)
							&& (pc.getY() >= 33120 && pc.getY() <= 33124)
							&& pc.getMapId() == 440) { // 해적 시마마에반매직 스퀘어 좌표
						boolean found = false;
						L1MonsterInstance mon = null;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1MonsterInstance) {
								mon = (L1MonsterInstance) obj;
								if (mon != null) {
									if (mon.getNpcTemplate().get_npcId() == 45875) {
										found = true;
										break;
									}
								}
							}

						}
						if (found) {
						} else {
							L1SpawnUtil.spawn(pc, 45875, 0, 0, false);
						}
					}
				} else if (itemId == 41121) {
					if (pc.getQuest().get_step(L1Quest.QUEST_SHADOWS) == L1Quest.QUEST_END
							|| pc.getInventory().checkItem(41122, 1)) {
						pc.sendPackets(new S_ServerMessage(79));
					} else {
						createNewItem(pc, 41122, 1);
					}
				} else if (itemId == 41130) {
					if (pc.getQuest().get_step(L1Quest.QUEST_DESIRE) == L1Quest.QUEST_END
							|| pc.getInventory().checkItem(41131, 1)) {
						pc.sendPackets(new S_ServerMessage(79));
					} else {
						createNewItem(pc, 41131, 1);
					}
				} else if (itemId == 42501) { // 스톰 워크
					if (pc.getCurrentMp() < 10) {
						pc.sendPackets(new S_ServerMessage(278)); // \f1MP가
						// 부족해 마법을
						// 사용할 수 있지
						// 않습니다.
						return;
					}
					pc.setCurrentMp(pc.getCurrentMp() - 10);
					L1Teleport.teleport(pc, spellsc_x, spellsc_y,
							pc.getMapId(), pc.getMoveState().getHeading(),
							true, L1Teleport.CHANGE_POSITION);
				} else if (itemId == 50101) { // 위치막대
					IdentMapWand(pc, spellsc_x, spellsc_y);
				} else if (itemId == 50102) { // 위치변경막대
					MapFixKeyWand(pc, spellsc_x, spellsc_y);
				} else if (itemId == L1ItemId.CHANGING_PETNAME_SCROLL) {
					if (l1iteminstance1.getItem().getItemId() == 40314
							|| l1iteminstance1.getItem().getItemId() == 40316) {
						L1Pet petTemplate = PetTable.getInstance().getTemplate(
								l1iteminstance1.getId());
						L1Npc l1npc = NpcTable.getInstance().getTemplate(
								petTemplate.get_npcid());
						/*if (petTemplate == null) {
							throw new NullPointerException();
						}*/
						petTemplate.set_name(l1npc.get_name());
						PetTable.getInstance().storePet(petTemplate);
						L1ItemInstance item = pc.getInventory().getItem(
								l1iteminstance1.getId());
						pc.getInventory().updateItem(item);
						pc.getInventory().removeItem(useItem, 1);
						pc.sendPackets(new S_ServerMessage(1322, l1npc
								.get_name()));
						pc.sendPackets(new S_ChangeName(
								petTemplate.get_objid(), l1npc.get_name()));
						Broadcaster.broadcastPacket(pc, new S_ChangeName(
								petTemplate.get_objid(), l1npc.get_name()));
					} else {
						pc.sendPackets(new S_ServerMessage(1164));
					}
				} else if (itemId == 41260) { // 신
					for (L1Object object : L1World.getInstance()
							.getVisibleObjects(pc, 3)) {
						if (object instanceof L1EffectInstance) {
							if (((L1NpcInstance) object).getNpcTemplate()
									.get_npcId() == 81170) {
								pc.sendPackets(new S_ServerMessage(1162)); // 벌써
								// 주위에
								// 모닥불이
								// 있습니다.
								return;
							}
						}
					}
					int[] loc = new int[2];
					loc = CharPosUtil.getFrontLoc(pc.getX(), pc.getY(), pc.getMoveState().getHeading());
					L1EffectSpawn.getInstance().spawnEffect(81170, 600000, loc[0], loc[1], pc.getMapId());
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41345) { // 산성의 유액
					L1DamagePoison.doInfection(pc, pc, 3000, 5);
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41315) { // 성수
					if (pc.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_HOLY_WATER_OF_EVA)) {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
						return;
					}
					if (pc.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_HOLY_MITHRIL_POWDER)) {
						pc.getSkillEffectTimerSet().removeSkillEffect(
								STATUS_HOLY_MITHRIL_POWDER);
					}
					pc.getSkillEffectTimerSet().setSkillEffect(
							STATUS_HOLY_WATER, 900 * 1000);
					pc.sendPackets(new S_SkillSound(pc.getId(), 190));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(
							pc.getId(), 190));
					pc.sendPackets(new S_ServerMessage(1141));
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41316) { // 신성한 미스리르파우다
					if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도일어나지 않았습니다.
						return;
					}
					if (pc.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_HOLY_WATER)) {
						pc.getSkillEffectTimerSet().removeSkillEffect(
								STATUS_HOLY_WATER);
					}
					pc.getSkillEffectTimerSet().setSkillEffect(
							STATUS_HOLY_MITHRIL_POWDER, 900 * 1000);
					pc.sendPackets(new S_SkillSound(pc.getId(), 190));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(
							pc.getId(), 190));
					pc.sendPackets(new S_ServerMessage(1142));
					pc.getInventory().removeItem(useItem, 1);
				} else if (itemId == 41354) { // 신성한 에바의 물
					if (pc.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_HOLY_WATER)
							|| pc.getSkillEffectTimerSet().hasSkillEffect(
									STATUS_HOLY_MITHRIL_POWDER)) {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도일어나지 않았습니다.
						return;
					}
					pc.getSkillEffectTimerSet().setSkillEffect(
							STATUS_HOLY_WATER_OF_EVA, 900 * 1000);
					pc.sendPackets(new S_SkillSound(pc.getId(), 190));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(
							pc.getId(), 190));
					pc.sendPackets(new S_ServerMessage(1140));
					pc.getInventory().removeItem(useItem, 1);
					
					//버프물약
				} else if (itemId == 400247) { // 근거리
					pc.setBuffnoch(1);
					int[] allBuffSkill = { 43, 79, 151, 160, 206, 216, 211, 107, 148 };
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
					l1skilluse.handleCommands(pc, allBuffSkill[i], 
					pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
					}
					pc.getInventory().removeItem(useItem, 1);
					// 1Teleport.teleport(pc, pc.getX(), pc.getY(),
					// pc.getMapId(), pc.getHeading(), false);
					pc.sendPackets(new S_SystemMessage("\\fS버프: 이제 깔끔하게 사냥이나 PK하러가시죠?" ));
					pc.setBuffnoch(0);
				} else if (itemId == 400248) { // 원거리
					pc.setBuffnoch(1);
					int[] allBuffSkill = { 43, 79, 151, 160, 206, 216, 211, 149 };
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
					l1skilluse.handleCommands(pc, allBuffSkill[i], 
					pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
					}
					pc.getInventory().removeItem(useItem, 1);
					// 1Teleport.teleport(pc, pc.getX(), pc.getY(),
					// pc.getMapId(), pc.getHeading(), false);
					pc.sendPackets(new S_SystemMessage("\\fS버프: 이제 깔끔하게 사냥이나 PK하러가시죠?" ));	
					pc.setBuffnoch(0);
					
				} else if (itemId == 127010) { // 리치
	                  int[] allBuffSkill = { 209 };
	                    L1SkillUse l1skilluse = new L1SkillUse();
	                    for (int i = 0; i < allBuffSkill.length ; i++) {
	                    l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
	                     }pc.getInventory().consumeItem(127010, 1);
				
				} else if (itemId == 127011) { // 버닝
	                  int[] allBuffSkill = { 163 };
	                    L1SkillUse l1skilluse = new L1SkillUse();
	                    for (int i = 0; i < allBuffSkill.length ; i++) {
	                    l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
	                     }pc.getInventory().consumeItem(127011, 1);

				} else if (itemId == 127012) { // 페이
	                  int[] allBuffSkill = { 211 };
	                    L1SkillUse l1skilluse = new L1SkillUse();
	                    for (int i = 0; i < allBuffSkill.length ; i++) {
	                    l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_SPELLSC);
	                     }pc.getInventory().consumeItem(127012, 1);

					
				} else if (itemId == L1ItemId.CHANGING_SEX_POTION) { // 성전환
					// 물약
					int[] MALE_LIST = new int[] { 0, 61, 138, 734, 2786, 6658,
							6671 };
					int[] FEMALE_LIST = new int[] { 1, 48, 37, 1186, 2796,
							6661, 6650 };
					if (pc.get_sex() == 0) {
						pc.set_sex(1);
						pc.setClassId(FEMALE_LIST[pc.getType()]);
					} else {
						pc.set_sex(0);
						pc.setClassId(MALE_LIST[pc.getType()]);
					}
					pc.getGfxId().setTempCharGfx(pc.getClassId());
					pc.sendPackets(new S_ChangeShape(pc.getId(), pc
							.getClassId()));
					Broadcaster.broadcastPacket(pc, new S_ChangeShape(pc
							.getId(), pc.getClassId()));
					pc.getInventory().removeItem(useItem, 1);
					/**
					 * ************************ 안타라스 리뉴얼 New System
					 * ****************************
					 */
				} else if (itemId == L1ItemId.DRAGON_KEY) {
					int castle_id = L1CastleLocation.getCastleIdByArea(pc);
                    if (castle_id != 0) { // 공성존에서 사용 불가
                     pc.sendPackets(new S_SystemMessage("공성지역에서는 사용 할 수 없습니다."));
                     return;
                    }// 드래곤키
                    if (CharPosUtil.getZoneType(pc) == 0) {
    					pc.sendPackets(new S_ServerMessage(625)); // 이동할 포탈을 클릭
    					pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONMENU, useItem));
					pc.getInventory().removeItem(useItem);
                    } else {
                    	pc.sendPackets(new S_ServerMessage(1892));
				}
					/**
					 * ************************ 안타라스 리뉴얼 New System
					 * ****************************
					 */
				} else if (itemId == 540344) {
					/***********************************************************
					 * 사용하면 상아탑의 체력 회복제 100개, 상아탑의 속도 향상 물약 6개, 상아탑의 귀환 주문서 2개,
					 * 상아탑의 변신 주문서 2개를 획득할 수 있다. 단, 레벨 45까지만 사용할 수 있다
					 **********************************************************/
					if (pc.getLevel() < 46) {
						pc.getInventory().storeItem(40029, 100);
						pc.getInventory().storeItem(40030, 6);
						pc.getInventory().storeItem(40095, 2);
						pc.getInventory().storeItem(40096, 2);
						pc.getInventory().removeItem(useItem);
					} else {
						pc.sendPackets(new S_SystemMessage("레벨 45까지만 사용하실수있습니다"));
					}
					/** 생일 시스템 **/
				} else if (itemId == 5000121) {
					if (pc.getInventory().checkItem(5000121, 1)) {
						pc.getInventory().consumeItem(5000121, 1);
						int[] mobArray = { 777860 };
						int rnd = _random.nextInt(mobArray.length);
						L1SpawnUtil.spawn(pc, mobArray[rnd], 0, 300000, true);
					}
				}else if (itemId == L1ItemId.DRAGON_EMERALD_BOX) {//드래곤의에메랄드상자
					 int[] DRAGONSCALE = new int[] { 40393, 40394, 40395, 40396 };
					 int bonus = _random.nextInt(100) + 1;
					 int rullet = _random.nextInt(100) + 1;
					 L1ItemInstance bonusitem = null;
					 pc.getInventory().storeItem(L1ItemId.DRAGON_EMERALD , 1);
					 //pc.sendPackets(new S_ServerMessage(403, "$7969"));
					 pc.getInventory().removeItem(useItem, 1);
					 if (bonus <= 3) {
					  bonusitem = pc.getInventory().storeItem(
					  DRAGONSCALE[rullet % DRAGONSCALE.length], 1);
					  pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
					 } else if (bonus >= 4 && bonus <= 8) {
					  bonusitem = pc.getInventory().storeItem(L1ItemId.DRAGON_PEARL, 1);
					  pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
					 } else if (bonus >= 9 && bonus <= 15) {
					  bonusitem = pc.getInventory().storeItem(L1ItemId.DRAGON_SAPHIRE, 1);
					  pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
					 } else if (bonus >= 16 && bonus <= 25) {
					  bonusitem = pc.getInventory().storeItem(L1ItemId.DRAGON_RUBY, 1);
					  pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
					 }
				}else if (itemId == L1ItemId.DRAGON_JEWEL_BOX) {//드래곤의보물상자
						 int[] DRAGONSCALE = new int[] { 40393, 40394, 40395, 40396 };
						 int bonus = _random.nextInt(100) + 1;
						 int rullet = _random.nextInt(100) + 1;
						 L1ItemInstance bonusitem = null;
						 pc.getInventory().storeItem(L1ItemId.DRAGON_DIAMOND, 1);
						 pc.sendPackets(new S_ServerMessage(403, "$7969"));
						 pc.getInventory().removeItem(useItem, 1);
						 if (bonus <= 3) {
						  bonusitem = pc.getInventory().storeItem(
						  DRAGONSCALE[rullet % DRAGONSCALE.length], 1);
						  pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
						 } else if (bonus >= 4 && bonus <= 8) {
						  bonusitem = pc.getInventory().storeItem(L1ItemId.DRAGON_PEARL, 1);
						  pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
						 } else if (bonus >= 9 && bonus <= 15) {
						  bonusitem = pc.getInventory().storeItem(L1ItemId.DRAGON_SAPHIRE, 1);
						  pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
						 } else if (bonus >= 16 && bonus <= 25) {
						  bonusitem = pc.getInventory().storeItem(L1ItemId.DRAGON_RUBY, 1);
						  pc.sendPackets(new S_ServerMessage(403, bonusitem.getItem().getNameId()));
						 } else {
						 }
				     } else if (itemId == 437010 || itemId == 437013 || itemId == 437012 || itemId == 437059) {
				       if (pc.getLevel() < 49) {
				         pc.sendPackets(new S_ServerMessage(318, "49"));
				         return;
				        }
				       if (itemId == L1ItemId.DRAGON_DIAMOND) {
	
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGON_EMERALD_NO) == true) {
							pc.sendPackets(new S_SystemMessage(
									"드래곤의 에메랄드 기운을받아 다른 보석을 사용할 수 없습니다."));
							return;
						}
						if (pc.getAinHasad() < 1000000) {
						pc.calAinHasad(1000000);
						pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc
								.getAinHasad()));
						pc.sendPackets(new S_SystemMessage(
								"아인하사드의 축복이 100% 추가되었습니다."));
						pc.getInventory().removeItem(useItem, 1);
					} else {
				                         pc.sendPackets(
				                                 new S_SystemMessage(
				                              "축복지수 100미만에서만 사용하실수 있습니다."));

					}
				} else if (itemId == L1ItemId.DRAGON_SAPHIRE) {
			
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGON_EMERALD_NO) == true) {
							pc.sendPackets(new S_SystemMessage(
									"드래곤의 에메랄드 기운을받아 다른 보석을 사용할 수 없습니다."));
							return;
						}
						if (pc.getAinHasad() < 1500000) {
						pc.calAinHasad(500000);
						pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc.getAinHasad()));
						pc.sendPackets(new S_SystemMessage("아인하사드의 축복이 50% 추가되었습니다."));
						pc.getInventory().removeItem(useItem, 1);
				                     } else {
				        pc.sendPackets(  new S_SystemMessage( "축복지수 150미만에서만 사용하실수 있습니다."));
				      }

				} else if (itemId == L1ItemId.DRAGON_RUBY) {
					
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGON_EMERALD_NO) == true) {
							pc.sendPackets(new S_SystemMessage(
									"드래곤의 에메랄드 기운을받아 다른 보석을 사용할 수 없습니다."));
							return;
						}
						if (pc.getAinHasad() < 1700000) {
						pc.calAinHasad(300000);
						pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc.getAinHasad()));
						pc.sendPackets(new S_SystemMessage("아인하사드의 축복이 30% 추가되었습니다."));
						pc.getInventory().removeItem(useItem, 1);
					} else {
				        pc.sendPackets( new S_SystemMessage("축복지수 170미만에서만 사용하실수 있습니다."));

					}
					// /////////////////////////드래곤에메랄드////////////////////////
				} else if (itemId == L1ItemId.DRAGON_EMERALD) {
					 int skill = 0;
					 int packet = 0;
					 int msg = 0;
			
					  if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGON_EMERALD_NO) == true) {
					   pc.sendPackets(new S_ServerMessage(2145));
					   return;
					  } else if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGON_EMERALD_YES) == true) {
					   pc.sendPackets(new S_ServerMessage(2147));
					   return;
					  }
					  skill = 7786;
					  packet = 0x02;
					  msg = 2140;
				     pc.calAinHasad(1000000);
					 pc.getSkillEffectTimerSet().setSkillEffect(skill, 10800 * 1000);
					 pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc.getAinHasad()));
					 pc.sendPackets(new S_PacketBox(S_PacketBox.EMERALD_EVA, packet, 10800));
					 pc.sendPackets(new S_ServerMessage(msg));
					 pc.getInventory().removeItem(useItem, 1);
					}
						/** 서버정보게시판 편지로 불러오기 **/
						}  else if (itemId == 4500011) { // 아이템 번호
			                 for (L1Object obj : L1World.getInstance().getObject()) {
			                      if (obj instanceof L1BoardInstance) {
			                       L1NpcInstance board = (L1NpcInstance)obj;
			                       if (board.getNpcTemplate().get_npcId() == 4500300) {//게시판 번호
			                        pc.sendPackets(new S_Board(board, 0));
			                      //  break;
			                       }
			                      }
			                     }
			                 /** 서버정보게시판 편지로 불러오기 **/
					// /////////////////////////드래곤에메랄드////////////////////////
						} else if (itemId == 4500003) { // 서버 정보
							  for (L1Object obj : L1World.getInstance().getObject()) {
					              if (obj instanceof L1BoardInstance) {
					               L1NpcInstance board = (L1NpcInstance)obj;
					               if (board.getNpcTemplate().get_npcId() == 4200019) {//게시판 번호
					                pc.sendPackets(new S_Board(board, 0));
					               }
					              }
							  }
							
							
							
							
				} else if (itemId == 7060) { // 발록봉헌주문서(우호도)
					if (pc.getKarma() <= 10000000) {
						pc.setKarma(pc.getKarma() + 100000);
						pc.sendPackets(new S_SystemMessage(pc.getName()+ "님의 우호도가 향상되었습니다."));
						pc.getInventory().removeItem(useItem, 1);
					} else
						pc.sendPackets(new S_SystemMessage("8단계 이하에서만 사용할 수 있습니다."));
					pc.sendUhodo();// 우호도표기
				} else if (itemId == 7061) { // 야히봉헌주문서(우호도)
					if (pc.getKarma() >= -10000000) {
						pc.setKarma(pc.getKarma() - 100000);
						pc.sendPackets(new S_SystemMessage(pc.getName()+ "님의 우호도가 향상되었습니다."));
						pc.getInventory().removeItem(useItem, 1);
					} else
						pc.sendPackets(new S_SystemMessage("8단계 이하에서만 사용할 수 있습니다."));
					pc.sendUhodo();// 우호도표기
				} else {
					int locX = ((L1EtcItem) useItem.getItem()).get_locx();
					int locY = ((L1EtcItem) useItem.getItem()).get_locy();
					short mapId = ((L1EtcItem) useItem.getItem()).get_mapid();
					if (locX != 0 && locY != 0) {
						if (pc.getMap().isEscapable() || pc.isGm()) {
							L1Teleport.teleport(pc, locX, locY, mapId, pc
									.getMoveState().getHeading(), true);
											if (((L1EtcItem) useItem.getItem()).getItemId() != 5000297) { // 베테르랑인증서
							pc.getInventory().removeItem(useItem, 1);
							}
						} else {
							pc.sendPackets(new S_ServerMessage(647));
						}
						//변신주문서만들때
					} else if (itemId == 545452 || itemId == 545453 || itemId == 545451) {
						UsePolyPotion(pc, itemId);
					    pc.getInventory().removeItem(useItem, 1);
					//	pc.cancelAbsoluteBarrier(); 
					//	pc.cancelAvata(); 
						
					} else {
						if (useItem.getCount() < 1) {
							pc.sendPackets(new S_ServerMessage(329, useItem.getLogName()));
						} else {
							pc.sendPackets(new S_ServerMessage(74, useItem.getLogName()));
						}
					}
				}
			}
			L1ItemDelay.onItemUse(pc, useItem); // 아이템 지연 개시
			}
		}


	
		// 몬스터소환막대
		private void useMobEventSpownWand(L1PcInstance pc, L1ItemInstance item) {

			try {
				int[][] mobArray = {
						// 일반몹
						{ 45008, 45140, 45016, 45021, 45025, 45033, 45099, 45147,
								45123, 45130, 45046, 45092, 45138, 45098, 45127,
								45143, 45149, 45171, 45040, 45155, 45192, 45173,
								45213, 45079, 45144 },
						// 보스몹 10%
						{ 45488, 45456, 45473, 45497, 45464, 45545, 45529, 45516 },
						// 보스몹 7%
						{ 45601, 45573, 45583, 45609, 45955, 45956, 45957, 45958,
								45959, 45960, 45961, 45962, 45617, 45610, 45600,
								45614, 45618, 45649, 45680, 45654, 45674, 45625,
								45675, 45672 },
						// 보스몹 3%
						{ 45753, 45801, 45673 } };
				int category = 0;
				int rndcategory = _random.nextInt(100) + 1;
				if (rndcategory <= 80)
					category = 0;
				else if (rndcategory <= 90)
					category = 1;
				else if (rndcategory <= 97)
					category = 2;
				else if (rndcategory <= 100)
					category = 3;
				int rnd = _random.nextInt(mobArray[category].length);
				L1SpawnUtil.spawn(pc, mobArray[category][rnd], 0, 180000, true);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	// 변신주문서 추가아이템
			private void UsePolyPotion(L1PcInstance pc, int item_id) {
				int polyId = 0;
				switch (item_id) {
				case 545451: // 꼬꼬마변신막대
					polyId = 8719; //(귤) 변신이미지값
					break;
				case 545452: // 꼬꼬마변신막대
					polyId = 7408; //(귤) 변신이미지값
					break;
				case 545453: // 꼬꼬마변신막대
					polyId = 8551; //(귤) 변신이미지값
					break;
				default:
					break;
				}
				L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_ITEMMAGIC);
			}
	
	private void UseHeallingPotion(L1PcInstance pc, int healHp, int gfxid) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(71) == true) { // 디케이포션
			// 상태
			pc.sendPackets(new S_ServerMessage(698)); // 마력에 의해 아무것도 마실 수가
			// 없습니다.
			return;
		}

		// 앱솔루트베리어의 해제
		pc.cancelAbsoluteBarrier();

		pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), gfxid));
	//	pc.sendPackets(new S_ServerMessage(77)); // \f1기분이 좋아졌습니다.
		healHp *= (_random.nextGaussian() / 5.0D) + 1.0D;
		if (pc.getSkillEffectTimerSet().hasSkillEffect(POLLUTE_WATER)) { // 포르트워타중은
			// 회복량1/2배
			healHp /= 2;
		}
		pc.setCurrentHp(pc.getCurrentHp() + healHp);
	}

	private void EventT(L1PcInstance pc, L1ItemInstance useItem) {// 인나티
		pc.getInventory().removeItem(useItem, 1);
		pc.getInventory().storeItem(L1ItemId.Inadril_T_ScrollA, 10);
		pc.getInventory().storeItem(L1ItemId.Inadril_T_ScrollB, 10);
		pc.getInventory().storeItem(L1ItemId.Inadril_T_ScrollC, 10);
		pc.getInventory().storeItem(L1ItemId.Inadril_T_ScrollD, 5);
	}
	private void 진주포션사용(L1PcInstance pc) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(71) == true) { // 디케이포션
																		// 상태
			pc.sendPackets(new S_ServerMessage(698));
			return;
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.STATUS_DRAGONPERL)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(
					L1SkillId.STATUS_DRAGONPERL);
			pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONPERL, 0, 0));
			Broadcaster.broadcastPacket(pc, new S_DRAGONPERL(pc.getId(), 0));
			pc.sendPackets(new S_DRAGONPERL(pc.getId(), 0));
			pc.set진주속도(0);
		}
		pc.cancelAbsoluteBarrier();// 앱솔해제(팩에 이 메소드없으면 무시)
		int time = 600 * 1000;
		int stime = ((time / 1000) / 4) - 2;
		pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_DRAGONPERL,
				time);
		pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONPERL, stime, 8));
		pc.sendPackets(new S_DRAGONPERL(pc.getId(), 8));
		Broadcaster.broadcastPacket(pc, new S_DRAGONPERL(pc.getId(), 8));
		pc.sendPackets(new S_SkillSound(pc.getId(), 197));// 말갱이 이팩트...
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 197));
		pc.set진주속도(1);
	}
	
	// 천상의 물약
	private void UseExpPotion(L1PcInstance pc, int item_id) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(71) == true) { // 디케이포션
			// 상태
			pc.sendPackets(new S_ServerMessage(698, "")); // 마력에 의해 아무것도 마실 수가
			// 없습니다.
			return;
		}
		pc.cancelAbsoluteBarrier();

		int time = 0;
		if (item_id == L1ItemId.EXP_POTION || item_id == L1ItemId.EXP_POTION2) { // 경험치
																					// 상승
																					// 물약
		 time = 3600; // 60분
		}

		pc.getSkillEffectTimerSet().setSkillEffect(EXP_POTION, time * 1000);
		pc.sendPackets(new S_SkillSound(pc.getId(), 7013));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7013));
		pc.sendPackets(new S_ServerMessage(1313));
	}
	private void UseExpPotion2(L1PcInstance pc , int item_id) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(71) == true) { // 디케이포션 상태
			pc.sendPackets(new S_ServerMessage(698, "")); // 마력에 의해 아무것도 마실 수가 없습니다.
			return;
		}
		pc.cancelAbsoluteBarrier();

		int time = 0;
		if (item_id == L1ItemId.EXP_POTION3 //게렝의 전투물약
				) { // 경험치 상승 물약
			time = 3600; // 60분
		}

		pc.getSkillEffectTimerSet().setSkillEffect(EXP_POTION3, time * 1000);
		pc.sendPackets(new S_SkillSound(pc.getId(), 9278));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId() , 9278));
		pc.sendPackets(new S_ServerMessage(1313));
	}
	private void UseExpPotion3(L1PcInstance pc , int item_id) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(71) == true) { // 디케이포션 상태
			pc.sendPackets(new S_ServerMessage(698, "")); // 마력에 의해 아무것도 마실 수가 없습니다.
			return;
		}
		pc.cancelAbsoluteBarrier();

		int time = 0;
		if (item_id == L1ItemId.EXP_POTION4 //게렝의 행운물약
				) { // 경험치 상승 물약
			time = 3600; // 60분
		}

		pc.getSkillEffectTimerSet().setSkillEffect(EXP_POTION2, time * 1000);
		pc.sendPackets(new S_SkillSound(pc.getId(), 7013));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId() , 7013));
		pc.sendPackets(new S_ServerMessage(1313));
	}
	
	
	
	

	private void useCashScroll(L1PcInstance pc, int item_id) {
		int time = 3600;
		int scroll = 0;

		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CASHSCROLL)) {//체강
			pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_CASHSCROLL);
			scroll = 6993;
			pc.addHpr(-4);
			pc.addMaxHp(-50);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CASHSCROLL2)) {//마증
			pc.getSkillEffectTimerSet()
					.killSkillEffectTimer(STATUS_CASHSCROLL2);
			pc.addMpr(-4);
			pc.addMaxMp(-40);
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_CASHSCROLL3)) {//전강
			pc.getSkillEffectTimerSet()
					.killSkillEffectTimer(STATUS_CASHSCROLL3);
			pc.addDmgup(-3);
			pc.addHitup(-3);
			pc.addBowHitup(-3);
		    pc.addBowDmgup(-3);
			pc.getAbility().addSp(-3);
			pc.sendPackets(new S_SPMR(pc));
		}
		if (item_id == L1ItemId.INCRESE_HP_SCROLL//체강
				|| item_id == L1ItemId.CHUNSANG_HP_SCROLL) {
			scroll = 6993;
			pc.addHpr(4);
			pc.addMaxHp(50);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		} else if (item_id == L1ItemId.INCRESE_MP_SCROLL//마증
				|| item_id == L1ItemId.CHUNSANG_MP_SCROLL) {
			scroll = 6994;
			pc.addMpr(4);
			pc.addMaxMp(40);
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		} else if (item_id == L1ItemId.INCRESE_ATTACK_SCROLL//전강
				|| item_id == L1ItemId.CHUNSANG_ATTACK_SCROLL
				|| item_id == L1ItemId.DRAGON_STONE) {
			scroll = 6995;
			pc.addDmgup(3);
			pc.addHitup(3);
			pc.addBowHitup(3);
		    pc.addBowDmgup(3);
			pc.getAbility().addSp(3);
			pc.sendPackets(new S_SPMR(pc));
			// /////////////////////////////////////////추가
		} else if (item_id == L1ItemId.INCRESE_HP_SCROLL
				|| item_id == L1ItemId.CHUNSANG_HP_SCROLL) {
			if (item_id == L1ItemId.INCRESE_ATTACK_SCROLL
					|| item_id == L1ItemId.CHUNSANG_ATTACK_SCROLL)
				;
			scroll = 7671;
			pc.addHpr(-4);
			pc.addMaxHp(-50);
			pc.addDmgup(-5);
			pc.addHitup(-2);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			pc.sendPackets(new S_SPMR(pc));
		} else if (item_id == L1ItemId.INCRESE_HP_SCROLL
				|| item_id == L1ItemId.CHUNSANG_HP_SCROLL) {
			if (item_id == L1ItemId.INCRESE_ATTACK_SCROLL
					|| item_id == L1ItemId.CHUNSANG_ATTACK_SCROLL)
				;
			scroll = 7672;
			pc.addHpr(4);
			pc.addMaxHp(50);
			pc.addDmgup(5);
			pc.addHitup(2);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			pc.sendPackets(new S_SPMR(pc));
		} else if (item_id == L1ItemId.INCRESE_HP_SCROLL
				|| item_id == L1ItemId.CHUNSANG_HP_SCROLL) {
			if (item_id == L1ItemId.INCRESE_ATTACK_SCROLL
					|| item_id == L1ItemId.CHUNSANG_ATTACK_SCROLL)
				;
			scroll = 7673;
			pc.addHpr(4);
			pc.addMaxHp(50);
			pc.addDmgup(5);
			pc.addHitup(2);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			pc.sendPackets(new S_SPMR(pc));
		} else if (item_id == L1ItemId.INCRESE_HP_SCROLL
				|| item_id == L1ItemId.CHUNSANG_HP_SCROLL) {
			if (item_id == L1ItemId.INCRESE_ATTACK_SCROLL
					|| item_id == L1ItemId.CHUNSANG_ATTACK_SCROLL)
				;
			scroll = 7674;
			pc.addHpr(4);
			pc.addMaxHp(50);
			pc.addDmgup(5);
			pc.addHitup(2);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			pc.sendPackets(new S_SPMR(pc));
		} else if (item_id == L1ItemId.INCRESE_HP_SCROLL
				|| item_id == L1ItemId.CHUNSANG_HP_SCROLL) {
			if (item_id == L1ItemId.INCRESE_ATTACK_SCROLL
					|| item_id == L1ItemId.CHUNSANG_ATTACK_SCROLL)
				;
			scroll = 7675;
			pc.addHpr(4);
			pc.addMaxHp(50);
			pc.addDmgup(5);
			pc.addHitup(2);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			pc.sendPackets(new S_SPMR(pc));
		} else if (item_id == L1ItemId.INCRESE_HP_SCROLL
				|| item_id == L1ItemId.CHUNSANG_HP_SCROLL) {
			if (item_id == L1ItemId.INCRESE_ATTACK_SCROLL
					|| item_id == L1ItemId.CHUNSANG_ATTACK_SCROLL)
				;
			scroll = 7676;
			pc.addHpr(4);
			pc.addMaxHp(50);
			pc.addDmgup(5);
			pc.addHitup(2);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			pc.sendPackets(new S_SPMR(pc));
		} else if (item_id == L1ItemId.INCRESE_HP_SCROLL
				|| item_id == L1ItemId.CHUNSANG_HP_SCROLL) {
			if (item_id == L1ItemId.INCRESE_ATTACK_SCROLL
					|| item_id == L1ItemId.CHUNSANG_ATTACK_SCROLL)
				;
			scroll = 7678;
			pc.addHpr(4);
			pc.addMaxHp(50);
			pc.addDmgup(5);
			pc.addHitup(2);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) {
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			pc.sendPackets(new S_SPMR(pc));
		}
		// /////////////////////////////////////////추가
		pc.sendPackets(new S_SkillSound(pc.getId(), scroll));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), scroll));
		pc.getSkillEffectTimerSet().setSkillEffect(scroll, time * 1000);
	}

	@SuppressWarnings("unused")
	private boolean createNewItem(L1PcInstance pc, int item_id, int count) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		item.setCount(count);
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
	@SuppressWarnings("unused")
	private boolean createNewItem3(L1PcInstance pc, int item_id, int count,
			   int EnchantLevel, int Bless) {
			  L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
			  item.setCount(count);
			  item.setEnchantLevel(EnchantLevel);
			  item.setIdentified(true);
			  if (item != null) {
			   if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
			    pc.getInventory().storeItem(item);
			    item.setBless(Bless);
			    pc.getInventory().updateItem(item, L1PcInventory.COL_BLESS);
			    pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
			   } else { // 가질 수 없는 경우는 지면에 떨어뜨리는 처리의 캔슬은 하지 않는다(부정 방지)
			    L1World.getInstance()
			    .getInventory(pc.getX(), pc.getY(), pc.getMapId())
			    .storeItem(item);
			   }
			   pc.sendPackets(new S_ServerMessage(403, item.getLogName())); //
			   return true;
			  } else {
			   return false;
			  }
			 }
	@SuppressWarnings("unused")
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
				L1World.getInstance().getInventory(pc.getX(), pc.getY(),pc.getMapId()).storeItem(item);
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0를 손에 넣었습니다.
			return true;
		} else {
			return false;
		}
	}

	private void useToiTeleportAmulet(L1PcInstance pc, int itemId,
			L1ItemInstance item) {
		boolean isTeleport = false;
		if (itemId >= 40289 && itemId <= 40297 && itemId >= 123007
				|| itemId >= 30260 && itemId <= 30279|| itemId == 590038 || itemId == 590039) { //추가 by 성공) { // 오만부적 11~91층까지
			if (pc.getMap().isEscapable()) { // 귀환가능지역인가를 검색한다
				isTeleport = true; // 텔레포트 가능하게 // 오만부적 귀환지역만 제외하고 부적 사용 가능하게.
			}
		}
		
	if (itemId >= 123007) {
			if (pc.getMap().isEscapable()) {
			isTeleport = true;
			}
		}
				
		if (itemId == 123007) { //숨계부적
			if (pc.getX() >= 32680 && pc.getX() <= 32861 && pc.getMapId() == 2005) {
				isTeleport = true;
			}
		}
			
		if (itemId == 40289 || itemId == 40293) { //추가 by 성공) { // 11,51Famulet
			if (pc.getX() >= 32816 && pc.getX() <= 32821 && pc.getY() >= 32778
					&& pc.getY() <= 32783 && pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40290 || itemId == 40294) { // 21,61Famulet
			if (pc.getX() >= 32815 && pc.getX() <= 32820 && pc.getY() >= 32815
					&& pc.getY() <= 32820 && pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40291 || itemId == 40295) { // 31,71Famulet
			if (pc.getX() >= 32779 && pc.getX() <= 32784 && pc.getY() >= 32778
					&& pc.getY() <= 32783 && pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40292 || itemId == 40296) { // 41,81Famulet
			if (pc.getX() >= 32779 && pc.getX() <= 32784 && pc.getY() >= 32815
					&& pc.getY() <= 32820 && pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40297) { // 91Famulet
			if (pc.getX() >= 32706 && pc.getX() <= 32710 && pc.getY() >= 32909
					&& pc.getY() <= 32913 && pc.getMapId() == 190) {
				isTeleport = true;
			}
		}

		if (isTeleport) {
			L1Teleport.teleport(pc, item.getItem().get_locx(), item.getItem()
					.get_locy(), item.getItem().get_mapid(), 5, true);
		} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
		}
	}

	private boolean withdrawPet(L1PcInstance pc, int itemObjectId) {
		if (!pc.getMap().isTakePets()) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1 여기에서는 사용할 수 없습니다.
			return false;
		}

		int petCost = 0;
		Object[] petList = pc.getPetList().values().toArray();
		for (Object pet : petList) {
			if (pet instanceof L1PetInstance) {
				if (((L1PetInstance) pet).getItemObjId() == itemObjectId) { // 이미
					// 꺼내고
					// 있는
					// 애완동물
					return false;
				}
			}
			petCost += ((L1NpcInstance) pet).getPetcost();
		}
		int charisma = pc.getAbility().getTotalCha();
		if (pc.isCrown()) { // CROWN
			charisma += 6;
		} else if (pc.isElf()) { // ELF
			charisma += 12;
		} else if (pc.isWizard()) { // WIZ
			charisma += 6;
		} else if (pc.isDarkelf()) { // DE
			charisma += 6;
		} else if (pc.isDragonknight()) { // 용기사
			charisma += 6;
		} else if (pc.isIllusionist()) { // 환술사
			charisma += 6;
		}

		charisma -= petCost;
		int petCount = charisma / 6;
		if (petCount <= 0) {
			pc.sendPackets(new S_ServerMessage(489)); // 물러가려고 하는 애완동물이 너무
			// 많습니다.
			return false;
		}

		L1Pet l1pet = PetTable.getInstance().getTemplate(itemObjectId);
		if (l1pet != null) {
			L1Npc npcTemp = NpcTable.getInstance().getTemplate(
					l1pet.get_npcid());
			L1PetInstance pet = new L1PetInstance(npcTemp, pc, l1pet);
			pet.setPetcost(6);
			pet.getSkillEffectTimerSet().setSkillEffect(
					L1SkillId.STATUS_PET_FOOD, pet.getFoodTime() * 1000);
		}
		return true;
	}
	private void IdentMapWand(L1PcInstance pc, int locX, int locY) {
		pc.sendPackets(new S_SystemMessage("Gab :"
				+ pc.getMap().getOriginalTile(locX, locY) + ",x :" + locX
				+ ",y :" + locY + ", mapId :" + pc.getMapId()));
		if (pc.getMap().isCloseZone(locX, locY)) {
			pc.sendPackets(new S_EffectLocation(locX, locY, 10));
			Broadcaster.broadcastPacket(pc,
					new S_EffectLocation(locX, locY, 10));
			pc.sendPackets(new S_SystemMessage("벽으로 인식중"));
		}
	}
	private void MapFixKeyWand(L1PcInstance pc, int locX, int locY) {
		String key = new StringBuilder().append(pc.getMapId()).append(locX)
				.append(locY).toString();
		if (!pc.getMap().isCloseZone(locX, locY)) {
			if (!MapFixKeyTable.getInstance().isLockey(key)) {
				MapFixKeyTable.getInstance().storeLocFix(locX, locY,
						pc.getMapId());
				pc.sendPackets(new S_EffectLocation(locX, locY, 1815));
				Broadcaster.broadcastPacket(pc, new S_EffectLocation(locX,
						locY, 1815));
				pc.sendPackets(new S_SystemMessage("key추가 ,x :" + locX + ",y :"
						+ locY + ", mapId :" + pc.getMapId()));
			}
		} else {
			pc.sendPackets(new S_SystemMessage("선택좌표는 벽이 아닙니다."));

			if (MapFixKeyTable.getInstance().isLockey(key)) {
				MapFixKeyTable.getInstance().deleteLocFix(locX, locY,
						pc.getMapId());
				pc.sendPackets(new S_EffectLocation(locX, locY, 10));
				Broadcaster.broadcastPacket(pc, new S_EffectLocation(locX,
						locY, 10));
				pc.sendPackets(new S_SystemMessage("key삭제 ,x :" + locX + ",y :"
						+ locY + ", mapId :" + pc.getMapId()));
			}
		}
	}

	@Override
	public String getType() {
		return C_ITEM_USE;
	}
}