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

import java.lang.reflect.Constructor;

import l1j.server.server.ActionCodes;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;

@SuppressWarnings("serial")
public class FurnitureItem extends L1ItemInstance {

	public FurnitureItem(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = this.getItemId();
			if (itemId >= 41383 && itemId <= 41400) { // 가구
				useFurnitureItem(pc, itemId, this.getId());
			} else if (itemId == 41401) {
				useFurnitureRemovalWand(pc, packet.readD(), useItem);
			}
		}
	}

	private void useFurnitureItem(L1PcInstance pc, int itemId, int itemObjectId) {
		if (!L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1 여기에서는 사용할 수 없습니다.
			return;
		}

		boolean isAppear = true;
		L1FurnitureInstance furniture = null;

		for (L1Object l1object : L1World.getInstance().getObject()) {
			if (l1object instanceof L1FurnitureInstance) {
				furniture = (L1FurnitureInstance) l1object;
				if (furniture.getItemObjId() == itemObjectId) { // 이미 꺼내고 있는 가구
					isAppear = false;
					break;
				}
			}

		}

		if (isAppear) {
			if (pc.getMoveState().getHeading() != 0
					&& pc.getMoveState().getHeading() != 2) {
				return;
			}
			int npcId = 0;
			switch (itemId) {
			case 41383:
				npcId = 80109;
				break;
			case 41384:
				npcId = 80110;
				break;
			case 41385:
				npcId = 80113;
				break;
			case 41386:
				npcId = 80114;
				break;
			case 41387:
				npcId = 80115;
				break;
			case 41388:
				npcId = 80124;
				break;
			case 41389:
				npcId = 80118;
				break;
			case 41390:
				npcId = 80118;
				break;
			case 41391:
				npcId = 80120;
				break;
			case 41392:
				npcId = 80121;
				break;
			case 41393:
				npcId = 80126;
				break;
			case 41394:
				npcId = 80125;
				break;
			case 41395:
				npcId = 80111;
				break;
			case 41396:
				npcId = 80112;
				break;
			case 41397:
				npcId = 80116;
				break;
			case 41398:
				npcId = 80117;
				break;
			case 41399:
				npcId = 80122;
				break;
			case 41400:
				npcId = 80123;
				break;
			}

			try {
				L1Npc l1npc = NpcTable.getInstance().getTemplate(npcId);
				if (l1npc != null) {
					try {
						String s = l1npc.getImpl();
						Constructor<?> constructor = Class.forName(
								"l1j.server.server.model.Instance." + s
										+ "Instance").getConstructors()[0];
						Object aobj[] = { l1npc };
						furniture = (L1FurnitureInstance) constructor
								.newInstance(aobj);
						furniture.setId(ObjectIdFactory.getInstance().nextId());
						furniture.setMap(pc.getMapId());
						if (pc.getMoveState().getHeading() == 0) {
							furniture.setX(pc.getX());
							furniture.setY(pc.getY() - 1);
						} else if (pc.getMoveState().getHeading() == 2) {
							furniture.setX(pc.getX() + 1);
							furniture.setY(pc.getY());
						}
						furniture.setHomeX(furniture.getX());
						furniture.setHomeY(furniture.getY());
						furniture.getMoveState().setHeading(0);
						furniture.setItemObjId(itemObjectId);

						L1World.getInstance().storeObject(furniture);
						L1World.getInstance().addVisibleObject(furniture);
						FurnitureSpawnTable.getInstance().insertFurniture(
								furniture);
					} catch (Exception e) {
					}
				}
			} catch (Exception exception) {
			}
		} else {
			furniture.deleteMe();
			FurnitureSpawnTable.getInstance().deleteFurniture(furniture);
		}
	}

	private void useFurnitureRemovalWand(L1PcInstance pc, int targetId,
			L1ItemInstance item) {
		S_AttackPacket s_attackStatus = new S_AttackPacket(pc, 0,
				ActionCodes.ACTION_Wand);
		pc.sendPackets(s_attackStatus);
		Broadcaster.broadcastPacket(pc, s_attackStatus);
		int chargeCount = item.getChargeCount();
		if (chargeCount <= 0) {
			return;
		}
		L1Object target = L1World.getInstance().findObject(targetId);
		if (target != null && target instanceof L1FurnitureInstance) {
			L1FurnitureInstance furniture = (L1FurnitureInstance) target;
			furniture.deleteMe();
			FurnitureSpawnTable.getInstance().deleteFurniture(furniture);
			item.setChargeCount(item.getChargeCount() - 1);
			pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
		}
	}
}
