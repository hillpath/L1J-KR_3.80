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

import java.util.Random;

import l1j.server.Config;
import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class Choiceitem extends L1ItemInstance {

	private static Random _random = new Random(System.nanoTime());

	public Choiceitem(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(
					packet.readD());
			int itemId = this.getItemId();
			if (itemId >= 40931 && 40942 >= itemId) {
				// 가공된 보석류(사파이어·루비·에메랄드)
				int earing3Id = l1iteminstance1.getItem().getItemId();
				int earinglevel = 0;
				if (earing3Id >= 41161 && 41172 >= earing3Id) {
					// 신비적인 귀 링류
					if (earing3Id == (itemId + 230)) {
						if ((_random.nextInt(99) + 1) < Config.CREATE_CHANCE_PROCESSING) {
							switch (earing3Id) {
							case 41161:
								earinglevel = 21014;
								break;
							case 41162:
								earinglevel = 21006;
								break;
							case 41163:
								earinglevel = 21007;
								break;
							case 41164:
								earinglevel = 21015;
								break;
							case 41165:
								earinglevel = 21009;
								break;
							case 41166:
								earinglevel = 21008;
								break;
							case 41167:
								earinglevel = 21016;
								break;
							case 41168:
								earinglevel = 21012;
								break;
							case 41169:
								earinglevel = 21010;
								break;
							case 41170:
								earinglevel = 21017;
								break;
							case 41171:
								earinglevel = 21013;
								break;
							case 41172:
								earinglevel = 21011;
								break;
							}
							createNewItem(pc, earinglevel, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(158,
									l1iteminstance1.getName()));
							// \f1%0이 증발하고 있지 않게 되었습니다.
						}
						pc.getInventory().removeItem(l1iteminstance1, 1);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
					// 않았습니다.
				}
			} else if (itemId >= 40943 && 40958 >= itemId) {
				// 가공된 다이아몬드(워타·지구·파이어·윈드)
				int ringId = l1iteminstance1.getItem().getItemId();
				int ringlevel = 0;
				int gmas = 0;
				int gmam = 0;
				if (ringId >= 41185 && 41200 >= ringId) {
					// 세공된 링류
					if (itemId == 40943 || itemId == 40947 || itemId == 40951
							|| itemId == 40955) {
						gmas = 443;
						gmam = 447;
					} else if (itemId == 40944 || itemId == 40948
							|| itemId == 40952 || itemId == 40956) {
						gmas = 442;
						gmam = 446;
					} else if (itemId == 40945 || itemId == 40949
							|| itemId == 40953 || itemId == 40957) {
						gmas = 441;
						gmam = 445;
					} else if (itemId == 40946 || itemId == 40950
							|| itemId == 40954 || itemId == 40958) {
						gmas = 444;
						gmam = 448;
					}
					if (ringId == (itemId + 242)) {
						if ((_random.nextInt(99) + 1) < Config.CREATE_CHANCE_PROCESSING_DIAMOND) {
							switch (ringId) {
							case 41185:
								ringlevel = 20435;
								break;
							case 41186:
								ringlevel = 20436;
								break;
							case 41187:
								ringlevel = 20437;
								break;
							case 41188:
								ringlevel = 20438;
								break;
							case 41189:
								ringlevel = 20439;
								break;
							case 41190:
								ringlevel = 20440;
								break;
							case 41191:
								ringlevel = 20441;
								break;
							case 41192:
								ringlevel = 20442;
								break;
							case 41193:
								ringlevel = 20443;
								break;
							case 41194:
								ringlevel = 20444;
								break;
							case 41195:
								ringlevel = 20445;
								break;
							case 41196:
								ringlevel = 20446;
								break;
							case 41197:
								ringlevel = 20447;
								break;
							case 41198:
								ringlevel = 20448;
								break;
							case 41199:
								ringlevel = 20449;
								break;
							case 41200:
								ringlevel = 20450;
								break;
							}

							pc.sendPackets(new S_ServerMessage(gmas,
									l1iteminstance1.getName()));
							createNewItem(pc, ringlevel, 1);
							pc.getInventory().removeItem(l1iteminstance1, 1);
							pc.getInventory().removeItem(useItem, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(gmam, useItem
									.getName()));
							pc.getInventory().removeItem(useItem, 1);
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도
						// 일어나지
						// 않았습니다.
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
					// 않았습니다.
				}
			} else if (itemId >= 41048 && 41055 >= itemId) {
				// 풀먹임 된 항해 일지 페이지：1~8 페이지
				int logbookId = l1iteminstance1.getItem().getItemId();
				if (logbookId == (itemId + 8034)) {
					createNewItem(pc, logbookId + 2, 1);
					pc.getInventory().removeItem(l1iteminstance1, 1);
					pc.getInventory().removeItem(useItem, 1);
				} else {
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
					// 않았습니다.
				}
			} else if (itemId == 41056 || itemId == 41057) {
				// 풀먹임 된 항해 일지 페이지：9, 10 페이지
				int logbookId = l1iteminstance1.getItem().getItemId();
				if (logbookId == (itemId + 8034)) {
					createNewItem(pc, 41058, 1);
					pc.getInventory().removeItem(l1iteminstance1, 1);
					pc.getInventory().removeItem(useItem, 1);
				} else {
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
					// 않았습니다.
				}
			}
		}
	}

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
}
