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

package l1j.server.server.model.item.function;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.datatables.LogEnchantTable;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ItemStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import server.CodeLogger;
import server.manager.eva;

@SuppressWarnings("serial")
public class Enchant extends L1ItemInstance {

	private static Random _random = new Random(System.nanoTime());

	Calendar currentDate = Calendar.getInstance();

	SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd h:mm:ss a");

	String time = dateFormat.format(currentDate.getTime());

	public Enchant(L1Item item) {
		super(item);
	}

	public void SuccessEnchant(L1PcInstance pc, L1ItemInstance item, int i) {
        item.setProtection(0);//장비보호추가
		String s = "";
		String sa = "";
		String sb = "";
		String s1 = item.getName();
		String pm = "";
		if (item.getEnchantLevel() > 0) {
			pm = "+";
		}
		if (item.getItem().getType2() == 1) {
			if (!item.isIdentified() || item.getEnchantLevel() == 0) {
				switch (i) {
				case -1:
					s = s1;
					sa = "$246";
					sb = "$247";
					break;

				case 1: // '\001'
					s = s1;
					sa = "$245";
					sb = "$247";
					break;

				case 2: // '\002'
					s = s1;
					sa = "$245";
					sb = "$248";
					break;

				case 3: // '\003'
					s = s1;
					sa = "$245";
					sb = "$248";
					break;
				}
			} else {
				switch (i) {
				case -1:
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$246";
					sb = "$247";
					break;

				case 1: // '\001'
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$245";
					sb = "$247";
					break;

				case 2: // '\002'
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$245";
					sb = "$248";
					break;

				case 3: // '\003'
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$245";
					sb = "$248";
					break;
				}
			}
		} else if (item.getItem().getType2() == 2) {
			if (!item.isIdentified() || item.getEnchantLevel() == 0) {
				switch (i) {
				case -1:
					s = s1;
					sa = "$246";
					sb = "$247";
					break;

				case 1: // '\001'
					if (item.getItem().getGrade() < 0) {
						s = s1;
						sa = "$252";
						sb = "$247 ";
					} else {
						s = s1;
						sa = "$245";
						sb = "$248 ";
					}
					break;
				case 2: // '\002'
					s = s1;
					sa = "$252";
					sb = "$248 ";
					break;

				case 3: // '\003'
					s = s1;
					sa = "$252";
					sb = "$248 ";
					break;
				}
			} else {
				switch (i) {
				case -1:
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$246";
					sb = "$247";
					break;

				case 1: // '\001'
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					if (item.getItem().getGrade() < 0) {
						sa = "$252";
						sb = "$247 ";
					} else {
						sa = "$245";
						sb = "$248 ";
					}
					break;

				case 2: // '\002'
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$252";
					sb = "$248 ";
					break;

				case 3: // '\003'
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$252";
					sb = "$248 ";
					break;
				}
			}
		}
		pc.setLastEnchantItemid(0, null);
		pc.sendPackets(new S_ServerMessage(161, s, sa, sb));
		int oldEnchantLvl = item.getEnchantLevel();
		int newEnchantLvl = item.getEnchantLevel() + i;
		int safe_enchant = item.getItem().get_safeenchant();
		item.setEnchantLevel(newEnchantLvl);
		pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
		pc.saveInventory();
		if (newEnchantLvl > safe_enchant) {
			pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
		}
		if (item.getItem().getType2() == 1
				&& Config.LOGGING_WEAPON_ENCHANT != 0) {
			if (safe_enchant == 0
					|| newEnchantLvl >= Config.LOGGING_WEAPON_ENCHANT) {
				LogEnchantTable logenchant = new LogEnchantTable();
				logenchant.storeLogEnchant(pc.getId(), item.getId(),
						oldEnchantLvl, newEnchantLvl);
			}
		}
		if (item.getItem().getType2() == 2 && Config.LOGGING_ARMOR_ENCHANT != 0) {
			if (safe_enchant == 0
					|| newEnchantLvl >= Config.LOGGING_ARMOR_ENCHANT) {
				LogEnchantTable logenchant = new LogEnchantTable();
				logenchant.storeLogEnchant(pc.getId(), item.getId(),
						oldEnchantLvl, newEnchantLvl);
			}
		}
		if (item.getItem().getType2() == 1) {
			if (newEnchantLvl >= 8) {
				CodeLogger.getInstance().enchantlog(
						"성공 무기",
						"캐릭=" + pc.getName() + " 인챈 +" + oldEnchantLvl + " -> +"
								+ newEnchantLvl +" ",item);
			//	eva.writeMessage(8,"[성공:무기]"+"<"+pc.getName()+">:"+item.getName() +"("+oldEnchantLvl+")"+"->"+"("+newEnchantLvl+")");   // 추가
				eva.LogEnchantAppend("성공:무기", pc.getName(), oldEnchantLvl + "->" + newEnchantLvl, item.getName(), item.getId());
			}
		}
		if (item.getItem().getType2() == 2) {
			if (newEnchantLvl >= 7) {
				CodeLogger.getInstance().enchantlog(
						"성공 방어구",
						"캐릭=" + pc.getName() + " 인챈 +" + oldEnchantLvl + " -> +"
								+ newEnchantLvl +" ",item);
				//eva.writeMessage(8,"[성공:방어구]"+"<"+pc.getName()+">:"+item.getName() +"("+oldEnchantLvl+")"+"->"+"("+newEnchantLvl+")");   // 추가
				eva.LogEnchantAppend("성공:방어구", pc.getName(), oldEnchantLvl + "->" + newEnchantLvl, item.getName(), item.getId());
			}
		}

		if (item.getItem().getType2() == 2) {
			if (item.isEquipped()) {
				if (item.getItem().getType() >= 8
						&& item.getItem().getType() <= 12) {
				} else {
					pc.getAC().addAc(-i);
				}
				int i2 = item.getItem().getItemId();
				if (i2 == 20011 || i2 == 20110 || i2 == 120011 
						|| i2 == 490008 || i2 == 490017) { // 인나티
					// 헤룸, 매직
					// 체인 메일
					pc.getResistance().addMr(i);
					pc.sendPackets(new S_SPMR(pc));
		    	 }else if (i2 == 20117) { //바포갑옷
                   pc.getResistance().addMr(i * 1);
                   pc.sendPackets(new S_SPMR(pc));
                }else if (i2 == 76796 || i2 == 20117) { //상아탑리뉴얼
					pc.getResistance().addMr(i * 1);
					pc.sendPackets(new S_SPMR(pc));
				}else if (i2 == 500012||i2 == 20078) { //오만리뉴얼
					pc.getResistance().addMr(i * 3);
					pc.sendPackets(new S_SPMR(pc));
				}else if (i2 == 20056 || i2 == 120056 || i2 == 220056) { // 매직 클로크
					pc.getResistance().addMr(i * 2);
					pc.sendPackets(new S_SPMR(pc));
				}
			}
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
	}

	public void FailureEnchant(L1PcInstance pc, L1ItemInstance item) {
		///////////////장비보호///////////////
		 if (item.getProtection() == 1){
            if(item.getItem().getType2()==2 && item.isEquipped()) {
                pc.getAC().addAc(+item.getEnchantLevel());
            }
            item.setProtection(0);
            SuccessEnchant(pc, item, -1);
            pc.sendPackets(new S_ItemStatus(item));
			pc.getInventory().updateItem(item,L1PcInventory.COL_ENCHANTLVL);
			pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
            pc.sendPackets(new S_ServerMessage(1310));
            return;
        }
    	///////////////장비보호///////////////
		String s = "";
		String sa = "";
		int itemId = item.getItem().getItemId(); // 신묘땀시추가
		int itemType = item.getItem().getType2();
		String nameId = item.getName();
		String pm = "";
		if (itemType == 1) { // 무기
			if (!item.isIdentified() || item.getEnchantLevel() == 0) {
				s = nameId; // \f1%0%s 강렬하게%1 빛나더니 증발되어 사라집니다.
				sa = "$245";
			} else {
				if (item.getEnchantLevel() > 0) {
					pm = "+";
				}
				s = (new StringBuilder()).append(pm + item.getEnchantLevel())
						.append(" ").append(nameId).toString(); // \f1%0%s
																// 강렬하게%1
				// 빛나더니 증발되어 사라집니다.
				sa = "$245";
			}
		} else if (itemType == 2) { // 방어용 기구
			if (!item.isIdentified() || item.getEnchantLevel() == 0) {
				s = nameId; // \f1%0%s 강렬하게%1 빛나더니 증발되어 사라집니다.
				if (item.getItem().getGrade() < 0)
					sa = " $252";
				else
					sa = "$245";
			} else {
				if (item.getEnchantLevel() > 0) {
					pm = "+";
				}
				s = (new StringBuilder()).append(pm + item.getEnchantLevel())
						.append(" ").append(nameId).toString(); // \f1%0%s
																// 강렬하게%1 빛나더니
				// 증발되어 사라집니다.
				if (item.getItem().getGrade() < 0)
					sa = " $252";
				else
					sa = "$245";
			}
		}
		if ((itemId >= 4500081 && itemId <= 4500111)
				|| (itemId >= 900007 && itemId <= 900009)) { // 특정아이템
			pc.sendPackets(new S_ServerMessage(1310)); // 강렬하게 빛났지만 장비가 증발 되지는
														// 않았습니다.

			pc.getInventory().setEquipped(item, false); // 실패시 방어력 중첩안되게 장비해제.
			item.setEnchantLevel(0);
			pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
			pc.saveInventory();

			if (itemType == 1) {
			} else if (itemType == 2) {
			}
		} else {
			pc.setLastEnchantItemid(item.getId(), item);
			pc.sendPackets(new S_ServerMessage(164, s, sa));
			pc.getInventory().removeItem(item, item.getCount());
			pc.saveInventory();
			CodeLogger.getInstance().enchantlog("인첸 실패","캐릭=" + pc.getName() + "[증발]",item);
			eva.LogEnchantAppend("실패:W", pc.getName(), Integer.toString(item.getEnchantLevel()), item.getName(), item.getId());
			//eva.writeMessage(9," "+ "<"+pc.getName()+">:"+ item.getName()+ "("+Integer.toString(item.getEnchantLevel())+")"+"[에서 증발]"); //추가

		}
	}
	public int RandomELevel(L1ItemInstance item, int itemId) {
		int j = _random.nextInt(100) + 1;
		if (itemId == L1ItemId.B_SCROLL_OF_ENCHANT_ARMOR
				|| itemId == L1ItemId.B_SCROLL_OF_ENCHANT_WEAPON
				|| itemId == L1ItemId.Inadril_T_ScrollB) {// 인나티
			if (item.getEnchantLevel() < 0) {
				if (j < 30) {
					return 2;
				} else {
					return 1;
				}
			} else if (item.getEnchantLevel() <= 2) {
				if (j < 32) {
					return 1;
				} else if (j >= 33 && j <= 76) {
					return 2;
				} else if (j >= 77 && j <= 100) {
					return 3;
				}
			} else if (item.getEnchantLevel() >= 3
					&& item.getEnchantLevel() <= 5) {
				if (j < 50) {
					return 2;
				} else {
					return 1;
				}
			}
			return 1;
		} else if (itemId == 140129 || itemId == 140130) {
			if (item.getEnchantLevel() < 0) {
				if (j < 30) {
					return 2;
				} else {
					return 1;
				}
			} else if (item.getEnchantLevel() <= 2) {
				if (j < 32) {
					return 1;
				} else if (j >= 33 && j <= 60) {
					return 2;
				} else if (j >= 61 && j <= 100) {
					return 3;
				}
			} else if (item.getEnchantLevel() >= 3
					&& item.getEnchantLevel() <= 5) {
				if (j < 60) {
					return 2;
				} else {
					return 1;
				}
			}
			return 1;
		}
		return 1;
	}

	public void RegistEnchant(L1PcInstance pc, L1ItemInstance item, int item_id) {
		int level = item.getRegistLevel();
		int chance = _random.nextInt(20) + 1;
		if (item_id == L1ItemId.Inadril_T_ScrollD) {
			switch (level) {
			case 0:
				if (chance <= 5) {
					pc.sendPackets(new S_SystemMessage("" + item.getLogName()
							+ "에 속성 저항력이 스며들었습니다."));
					item.setRegistLevel(1);
				} else {
					pc.sendPackets(new S_SystemMessage("" + item.getLogName()
							+ "에 속성 저항력이 스며들지 못하였습니다."));
				}
				break;
			case 1:
				if (chance <= 4) {
					pc.sendPackets(new S_SystemMessage("" + item.getLogName()
							+ "에 속성 저항력이 스며들었습니다."));
					item.setRegistLevel(2);
				} else {
					pc.sendPackets(new S_SystemMessage("" + item.getLogName()
							+ "에 속성 저항력이 스며들지 못하였습니다."));
				}
				break;
			case 2:
				if (chance <= 3) {
					pc.sendPackets(new S_SystemMessage("" + item.getLogName()
							+ "에 속성 저항력이 스며들었습니다."));
					item.setRegistLevel(3);
				} else {
					pc.sendPackets(new S_SystemMessage("" + item.getLogName()
							+ "에 속성 저항력이 스며들지 못하였습니다."));
				}
				break;
			case 3:
				if (chance <= 2) {
					pc.sendPackets(new S_SystemMessage("" + item.getLogName()
							+ "에 속성 저항력이 스며들었습니다."));
					item.setRegistLevel(4);
				} else {
					pc.sendPackets(new S_SystemMessage("" + item.getLogName()
							+ "에 속성 저항력이 스며들지 못하였습니다."));
				}
				break;
			case 4:
				if (chance <= 1) {
					pc.sendPackets(new S_SystemMessage("" + item.getLogName()
							+ "에 속성 저항력이 스며들었습니다."));
					item.setRegistLevel(5);
				} else {
					pc.sendPackets(new S_SystemMessage("" + item.getLogName()
							+ "에 속성 저항력이 스며들지 못하였습니다."));
				}
				break;
			default:
				pc.sendPackets(new S_ServerMessage(79));
				break;
			}
			pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
			pc.getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
			pc.saveInventory();
		}
	}

	public void AttrEnchant(L1PcInstance pc, L1ItemInstance item, int item_id) {
		int attr_level = item.getAttrEnchantLevel();
		int chance = _random.nextInt(100) + 1;
		if (item_id == L1ItemId.FIRE_ENCHANT_WEAPON_SCROLL) { // 불의 무기 강화 주문서
			if (attr_level == 0 || (attr_level >= 4)) {
				if (chance < 25) {
					pc
							.sendPackets(new S_ServerMessage(1410, item
									.getLogName()));
					item.setAttrEnchantLevel(1);
				} else {
					pc
							.sendPackets(new S_ServerMessage(1411, item
									.getLogName()));
				}
			} else if (attr_level == 1) {
				if (chance < 15) {
					pc
							.sendPackets(new S_ServerMessage(1410, item
									.getLogName()));
					item.setAttrEnchantLevel(2);
				} else {
					pc
							.sendPackets(new S_ServerMessage(1411, item
									.getLogName()));
				}
			} else if (attr_level == 2) {
				if (chance < 5) {
					pc
							.sendPackets(new S_ServerMessage(1410, item
									.getLogName()));
					item.setAttrEnchantLevel(3);
				} else {
					pc
							.sendPackets(new S_ServerMessage(1411, item
									.getLogName()));
				}
			} else if (attr_level == 3) {
				pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
				// 않았습니다.
				return;
			}
		} else if (item_id == L1ItemId.WATER_ENCHANT_WEAPON_SCROLL) { // 물의 무기
			// 강화
			// 주문서
			if ((attr_level >= 0 && attr_level <= 3) || attr_level >= 7) {
				if (chance < 25) {
					pc
							.sendPackets(new S_ServerMessage(1410, item
									.getLogName()));
					item.setAttrEnchantLevel(4);
				} else {
					pc
							.sendPackets(new S_ServerMessage(1411, item
									.getLogName()));
				}
			} else if (attr_level == 4) {
				if (chance < 15) {
					pc
							.sendPackets(new S_ServerMessage(1410, item
									.getLogName()));
					item.setAttrEnchantLevel(5);
				} else {
					pc
							.sendPackets(new S_ServerMessage(1411, item
									.getLogName()));
				}
			} else if (attr_level == 5) {
				if (chance < 5) {
					pc
							.sendPackets(new S_ServerMessage(1410, item
									.getLogName()));
					item.setAttrEnchantLevel(6);
				} else {
					pc
							.sendPackets(new S_ServerMessage(1411, item
									.getLogName()));
				}
			} else if (attr_level == 6) {
				pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
				// 않았습니다.
				return;
			}
		} else if (item_id == L1ItemId.WIND_ENCHANT_WEAPON_SCROLL) { // 바람의
			// 무기 강화
			// 주문서
			if ((attr_level >= 0 && attr_level <= 6) || attr_level >= 10) {
				if (chance < 25) {
					pc
							.sendPackets(new S_ServerMessage(1410, item
									.getLogName()));
					item.setAttrEnchantLevel(7);
				} else {
					pc
							.sendPackets(new S_ServerMessage(1411, item
									.getLogName()));
				}
			} else if (attr_level == 7) {
				if (chance < 15) {
					pc
							.sendPackets(new S_ServerMessage(1410, item
									.getLogName()));
					item.setAttrEnchantLevel(8);
				} else {
					pc
							.sendPackets(new S_ServerMessage(1411, item
									.getLogName()));
				}
			} else if (attr_level == 8) {
				if (chance < 5) {
					pc
							.sendPackets(new S_ServerMessage(1410, item
									.getLogName()));
					item.setAttrEnchantLevel(9);
				} else {
					pc
							.sendPackets(new S_ServerMessage(1411, item
									.getLogName()));
				}
			} else if (attr_level == 9) {
				pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
				// 않았습니다.
				return;
			}
		} else if (item_id == L1ItemId.EARTH_ENCHANT_WEAPON_SCROLL) { // 땅의 무기
			// 강화
			// 주문서
			if (attr_level >= 0 && attr_level <= 9) {
				if (chance < 25) {
					pc
							.sendPackets(new S_ServerMessage(1410, item
									.getLogName()));
					item.setAttrEnchantLevel(10);
				} else {
					pc
							.sendPackets(new S_ServerMessage(1411, item
									.getLogName()));
				}
			} else if (attr_level == 10) {
				if (chance < 15) {
					pc
							.sendPackets(new S_ServerMessage(1410, item
									.getLogName()));
					item.setAttrEnchantLevel(11);
				} else {
					pc
							.sendPackets(new S_ServerMessage(1411, item
									.getLogName()));
				}
			} else if (attr_level == 11) {
				if (chance < 5) {
					pc
							.sendPackets(new S_ServerMessage(1410, item
									.getLogName()));
					item.setAttrEnchantLevel(12);
				} else {
					pc
							.sendPackets(new S_ServerMessage(1411, item
									.getLogName()));
				}
			} else if (attr_level == 12) {
				pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
				// 않았습니다.
				return;
			}
		}
		pc.getInventory().updateItem(item, L1PcInventory.COL_ATTRENCHANTLVL);
		pc.getInventory().saveItem(item, L1PcInventory.COL_ATTRENCHANTLVL);
		pc.saveInventory();
	}
	public void AttrEnchantByGM(L1PcInstance pc, L1ItemInstance item, int item_id) {
		if (item_id == 600006) { // 이그니스 주문서
			item.setAttrEnchantLevel(3);
			pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		} else if (item_id == 600007) { // 운디네 주문서
			item.setAttrEnchantLevel(6);
			pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		} else if (item_id == 600008) { // 실프 주문서
			item.setAttrEnchantLevel(9);
			pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		} else if (item_id == 600009) { // 클레이 주문서
			item.setAttrEnchantLevel(12);
			pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		}
		pc.getInventory().updateItem(item, L1PcInventory.COL_ATTRENCHANTLVL);
		pc.getInventory().saveItem(item, L1PcInventory.COL_ATTRENCHANTLVL);
		pc.saveInventory();
	}
}
