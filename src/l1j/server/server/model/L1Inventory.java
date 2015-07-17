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
package l1j.server.server.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import l1j.server.Config;
import l1j.server.GameSystem.CrockSystem;
import l1j.server.Warehouse.Warehouse;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.BoardTable;
import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_EquipmentWindow;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

public class L1Inventory extends L1Object {
	private static final long serialVersionUID = 1L;

	protected List<L1ItemInstance> _items = new CopyOnWriteArrayList<L1ItemInstance>();

	public static final int MAX_AMOUNT = 2000000000; // 2G

	public static final int MAX_WEIGHT = 1500;

	public static final int OK = 0;

	public static final int SIZE_OVER = 1;

	public static final int WEIGHT_OVER = 2;

	public static final int AMOUNT_OVER = 3;

	public static final int WAREHOUSE_TYPE_PERSONAL = 0;

	public static final int WAREHOUSE_TYPE_CLAN = 1;


	// 아이템패킷추가
		public int[] slot_ring = new int[4];
		public int[] slot_rune = new int[3];

		public L1Inventory() {
			//
			for(int i=0 ; i<slot_ring.length ; ++i)
				slot_ring[i] = 0;
			for(int i=0 ; i<slot_rune.length ; ++i)
				slot_rune[i] = 0;
		}
		
		
		public int getTypeAndItemIdEquipped(int type2, int type, int itemId) { //
			int equipeCount = 0;
			L1ItemInstance item = null;
			for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item.getItem().getType2() == type2 && item.getItem().getType() == type 
            && item.getItem().getItemId() == itemId  
            //&& item.getItem().getItemId() == 5000042 && item.getItem().getItemId() == 5000043// && item.getItem().getItemId() == 5000045 && item.getItem().getItemId() == 5000046
            && item.isEquipped()) {
			equipeCount++;
			}
			}
			return equipeCount;
			}
		
		
		public void toSlotPacket(L1PcInstance pc, L1ItemInstance item) {
			//
			if(pc.isWorld == false)
				return;
			//
			int select_idx = -1;
			int idx = 0;
			if(item.getItem().getType2() == 2) {
				switch( item.getItem().getType() ) {
					case 1:
						idx = S_EquipmentWindow.EQUIPMENT_INDEX_HEML;
						break;
					case 2:
						idx = S_EquipmentWindow.EQUIPMENT_INDEX_ARMOR;
						break;
					case 3:
						idx = S_EquipmentWindow.EQUIPMENT_INDEX_T;
						break;
					case 4:
						idx = S_EquipmentWindow.EQUIPMENT_INDEX_CLOAK;
						break;
					case 5:
						idx = S_EquipmentWindow.EQUIPMENT_INDEX_GLOVE;
						break;
					case 6:
						idx = S_EquipmentWindow.EQUIPMENT_INDEX_BOOTS;
						break;
					case 7:
						idx = S_EquipmentWindow.EQUIPMENT_INDEX_SHIELD;
						break;
					case 8:
						idx = S_EquipmentWindow.EQUIPMENT_INDEX_NECKLACE;
						break;
					case 9:		// ring1
					case 11:	// ring2
						// 기존에 착용중인게 잇는지 검색.
						for(int i=0 ; i<slot_ring.length ; ++i) {
							if(slot_ring[i] == item.getId())
								select_idx = i;
						}
						// 착용해야할경우 기존에 착용중인게 없을때만 메모리 갱신.
						if(item.isEquipped() && select_idx==-1) {
							// 착용중이라면 빈 슬롯에 넣기.
							for(int i=0 ; i<slot_ring.length ; ++i) {
								if(slot_ring[i] == 0) {
									slot_ring[i] = item.getId();
									idx = S_EquipmentWindow.EQUIPMENT_INDEX_RING1 + i;
									break;
								}
							}
						}
						// 착용해제해야할 경우 기존에 착용중인게 잇을땜나 메모리 갱신.
						if(!item.isEquipped() && select_idx!=-1) {
							// 해제중이라면 이전에 적용되잇던 위치에 값을 제거.
							slot_ring[select_idx] = 0;
							idx = S_EquipmentWindow.EQUIPMENT_INDEX_RING1 + select_idx;
						}
						break;
					case 10:
						idx = S_EquipmentWindow.EQUIPMENT_INDEX_BELT;
						break;
					case 12:
						idx = S_EquipmentWindow.EQUIPMENT_INDEX_EARRING;
						break;
					case 13:	// garder
						idx = S_EquipmentWindow.EQUIPMENT_INDEX_SHIELD;
						// 가더가 들어가야할 슬롯 번호 확인해야함.
						break;
					case 14:	// rune
						// 기존에 착용중인게 잇는지 검색.
						for(int i=0 ; i<slot_rune.length ; ++i) {
							if(slot_rune[i] == item.getId())
								select_idx = i;
						}
						// 착용해야할경우 기존에 착용중인게 없을때만 메모리 갱신.
						if(item.isEquipped() && select_idx==-1) {
							// 착용중이라면 빈 슬롯에 넣기.
							for(int i=0 ; i<slot_rune.length ; ++i) {
								if(slot_rune[i] == 0) {
									slot_rune[i] = item.getId();
									idx = S_EquipmentWindow.EQUIPMENT_INDEX_RUNE1 + i;
									break;
								}
							}
						}
						// 착용해제해야할 경우 기존에 착용중인게 잇을땜나 메모리 갱신.
						if(!item.isEquipped() && select_idx!=-1) {
							// 해제중이라면 이전에 적용되잇던 위치에 값을 제거.
							slot_rune[select_idx] = 0;
							idx = S_EquipmentWindow.EQUIPMENT_INDEX_RUNE1 + select_idx;
						}
						break;
				}
			} else {
				idx = S_EquipmentWindow.EQUIPMENT_INDEX_WEAPON;
			}
			//
			if(idx != 0)
				pc.sendPackets(new S_EquipmentWindow(pc, item.getId(), idx, item.isEquipped()));
		}
		// 아이템패킷추가

	public int getSize() {
		return _items.size();
	}

	public List<L1ItemInstance> getItems() {
		return _items;
	}

	public int getWeight() {
		int weight = 0;

		for (L1ItemInstance item : _items) {
			weight += item.getWeight();
		}

		return weight;
	}

	public int checkAddItem(L1ItemInstance item, int count) {
		if (item == null) {
			return -1;
		}
		if (item.getCount() <= 0 || count <= 0 || item.getCount() < count) {
			return -1;
		}
		if (getSize() > Config.MAX_NPC_ITEM
				|| (getSize() == Config.MAX_NPC_ITEM && (!item.isStackable() || !checkItem(item
						.getItem().getItemId())))
				|| (getSize() == Config.MAX_NPC_ITEM && item.getItem()
						.getItemId() == 40309)) { // 용량 확인
			return SIZE_OVER;
		}
		if (getSize() > Config.MAX_NPC_ITEM
				|| (getSize() == Config.MAX_NPC_ITEM && (!item.isStackable() || !checkItem(item
						.getItem().getItemId())))) {
			return SIZE_OVER;
		}

		int weight = getWeight() + item.getItem().getWeight() * count / 1000
				+ 1;
		if (weight < 0 || (item.getItem().getWeight() * count / 1000) < 0) {
			return WEIGHT_OVER;
		}
		if (weight > (MAX_WEIGHT * Config.RATE_WEIGHT_LIMIT_PET)) {
			return WEIGHT_OVER;
		}

		L1ItemInstance itemExist = findItemId(item.getItemId());
		if (itemExist != null && (itemExist.getCount() + count) > MAX_AMOUNT) {
			return AMOUNT_OVER;
		}

		return OK;
	}

	public int checkAddItemToWarehouse(L1ItemInstance item, int count, int type) {
		if (item == null) {
			return -1;
		}
		if (item.getCount() <= 0 || count <= 0 || item.getCount() < count) {
			return -1;
		}
		int maxSize = 100;
		if (type == WAREHOUSE_TYPE_PERSONAL) {
			maxSize = Config.MAX_PERSONAL_WAREHOUSE_ITEM;
		} else if (type == WAREHOUSE_TYPE_CLAN) {
			maxSize = Config.MAX_CLAN_WAREHOUSE_ITEM;
		}
		if (getSize() > maxSize
				|| (getSize() == maxSize && (!item.isStackable() || !checkItem(item
						.getItem().getItemId())))) {
			return SIZE_OVER;
		}

		return OK;
	}

	public synchronized L1ItemInstance storeItem(int id, int count, int enchant) {
		if (count <= 0) {
			return null;
		}
		L1Item temp = ItemTable.getInstance().getTemplate(id);
		if (temp == null) {
			return null;
		}

		if (temp.isStackable()) {
			L1ItemInstance item = ItemTable.getInstance().FunctionItem(temp);
			item.setCount(count);

			if (findItemId(id) == null) {
				item.setId(ObjectIdFactory.getInstance().nextId());
				L1World.getInstance().storeObject(item);
			}
			return storeItem(item);
		}

		L1ItemInstance result = null;
		L1ItemInstance item = null;
		for (int i = 0; i < count; i++) {
			item = ItemTable.getInstance().FunctionItem(temp);
			item.setId(ObjectIdFactory.getInstance().nextId());
			item.setEnchantLevel(enchant);
			L1World.getInstance().storeObject(item);
			storeItem(item);
			result = item;
		}
		return result;
	}

	public synchronized L1ItemInstance storeItem(int id, int count) {
		if (count <= 0) {
			return null;
		}
		L1Item temp = ItemTable.getInstance().getTemplate(id);
		if (temp == null) {
			return null;
		}

		if (temp.isStackable()) {
			L1ItemInstance item = ItemTable.getInstance().FunctionItem(temp);
			item.setCount(count);

			if (findItemId(id) == null) {
				item.setId(ObjectIdFactory.getInstance().nextId());
				L1World.getInstance().storeObject(item);
			}
			return storeItem(item);
		}

		L1ItemInstance result = null;
		L1ItemInstance item = null;
		for (int i = 0; i < count; i++) {
			item = ItemTable.getInstance().FunctionItem(temp);
			item.setId(ObjectIdFactory.getInstance().nextId());
			L1World.getInstance().storeObject(item);
			storeItem(item);
			result = item;
		}
		return result;
	}

	public synchronized L1ItemInstance storeItem(L1ItemInstance item) {
		if (item.getCount() <= 0) {
			return null;
		}
		
		int itemId = item.getItem().getItemId();

		    
		if (item.isStackable() && item.getItem().getItemId() != 40309) {
			L1ItemInstance findItem = findItemId(itemId);
			if (findItem != null) {
				findItem.setCount(findItem.getCount() + item.getCount());
				updateItem(findItem);
				return findItem;
			}
		} else if (item.getItem().getItemId() == 40309) {
			L1ItemInstance findItem = findItemTicketId(40309, item
					.getSecondId(), item.getTicketId());
			if (findItem != null) {
				findItem.setCount(findItem.getCount() + item.getCount());
				updateItem(findItem);
				return findItem;
			}
		}
		item.setX(getX());
		item.setY(getY());
		item.setMap(getMapId());
		int chargeCount = item.getItem().getMaxChargeCount();
		switch (itemId) {
		case 20383:
			chargeCount = 50;
			break;
		case 40006:
		case 40007:
		case 46091:	
		case 40008:
		case 45464://픽시의 변신막대
		case 41401:
		case 140006:
		case 140008:
			L1ItemInstance findItem = findItemId(itemId);
			if (findItem != null) {
				Random random = new Random(System.nanoTime());
				chargeCount -= random.nextInt(5);
				findItem.setChargeCount(findItem.getChargeCount() + chargeCount);
				updateItem(findItem);
				return findItem;
			}
			break;
			/** 흑단막대 아이템갯수는 1개지만 중첩되게.. 끝*/ 
			/** 생일 시스템 **/
		case 5000121:
			/** 생일 시스템 **/
			Random random = new Random(System.nanoTime());
			chargeCount -= random.nextInt(5);
			break;
		case 40903:
		case 40904:
		case 40905:
			chargeCount = itemId - 40902;
			break;
		case 40906:
			chargeCount = 5;
			break;
		case 40907:
		case 40908:
			chargeCount = 20;
			break;
		}
		item.setChargeCount(chargeCount);
		if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) { // light
			item.setRemainingTime(item.getItem().getLightFuel());
		} else {
			item.setRemainingTime(item.getItem().getMaxUseTime());
		}
		item.setBless(item.getItem().getBless());
		 /** 모포의 계약서 **/
		 if (itemId == L1ItemId.MOPO_JUiCE_CONTRACT1 || itemId == L1ItemId.MOPO_JUiCE_CONTRACT4){
			 Timestamp deleteTime = null;
			   deleteTime = new Timestamp(System.currentTimeMillis() + 32400000);
			   item.setEndTime(deleteTime); 
		 }
		 if (itemId == L1ItemId.MOPO_JUiCE_CONTRACT2 || itemId == L1ItemId.MOPO_JUiCE_CONTRACT5){
			 Timestamp deleteTime = null;
			   deleteTime = new Timestamp(System.currentTimeMillis() + 61200000);
			   item.setEndTime(deleteTime);
		 }
		 if (itemId == L1ItemId.MOPO_JUiCE_CONTRACT3 || itemId == L1ItemId.MOPO_JUiCE_CONTRACT6){
			 Timestamp deleteTime = null;
			   deleteTime = new Timestamp(System.currentTimeMillis() + 86400000);
			   item.setEndTime(deleteTime);
		 }
		 /**호랑이배송**/
		 if (itemId == L1ItemId.KILLTON_CONTRACT || itemId == L1ItemId.MERIN_CONTRACT){
			   SetDeleteTime(item, 60);//분으로계산1시간후에배송
			  }
		 /**호랑이배송**/
		if (itemId == L1ItemId.DRAGON_KEY) {// 드래곤 키
			Timestamp deleteTime = null;
			deleteTime = new Timestamp(System.currentTimeMillis() + 259200000);// 3일
			item.setEndTime(deleteTime);
			 for(L1PcInstance pc : L1World.getInstance().getAllPlayers()){
				    pc.sendPackets(new S_SystemMessage("강철 길드 난쟁이: 마법사 게렝님께서 방금 아덴 대륙에 드래곤 키가 나타났다고 하십니다. 선택 받은 드래곤 슬레이어에게 영광과 축복을!")); 
				   }
		}
		if (itemId == 490018) { //요리사의모자
			Timestamp deleteTime = null;
			deleteTime = new Timestamp(System.currentTimeMillis() + 259200000);// 3일
			item.setEndTime(deleteTime);
		}
		if (itemId >= 76767 && itemId <= 76776) { // 룬 5분
			Timestamp deleteTime = null;
			deleteTime = new Timestamp(System.currentTimeMillis()+ (1000 * 60 * 20)); // 5분
			item.setEndTime(deleteTime);
		}
				if (itemId >= 303 && itemId <= 310 || itemId >= 500031 && itemId <= 500038 || itemId == 5000297) { // 베테르랑 무기, 갑옷, 인증서 (물약, 장비 지급서는 시간제한 제외)
			Timestamp deleteTime = null;
			deleteTime = new Timestamp(System.currentTimeMillis()+ 86400000 );// 1일
			item.setEndTime(deleteTime);
		}
		if (itemId >= 76777 && itemId <= 76784) { // 그외 10분
			Timestamp deleteTime = null;
			deleteTime = new Timestamp(System.currentTimeMillis()+ (1000 * 60 * 20)); // 10분
			item.setEndTime(deleteTime);
		}
		if (itemId >= 500144 && itemId <= 500146) { //눈사람인형
			Timestamp deleteTime = null;
			deleteTime = new Timestamp(System.currentTimeMillis()+ (1000 * 60 * 10)); // 10분
			item.setEndTime(deleteTime);
		}
		if (itemId >= 50056 && itemId <= 50058) { //블레그4종셋트
			Timestamp deleteTime = null;
			deleteTime = new Timestamp(System.currentTimeMillis()+ (1000 * 60 * 600)); // 10시간
			item.setEndTime(deleteTime);
		}
		if (itemId >= 720 && itemId <= 722) { //싸이3종
			Timestamp deleteTime = null;
			deleteTime = new Timestamp(System.currentTimeMillis()+ (1000 * 60 * 600)); // 10시간
			item.setEndTime(deleteTime);
		}
		
		
		
		

		
	
		
		
		if (item.isIdentified() != true) {
			item.setIdentified(false);
		}
		_items.add(item);
		insertItem(item);
		return item;
	}

	public synchronized L1ItemInstance storeTradeItem(L1ItemInstance item) {
		
		
		if (item.isStackable() && item.getItem().getItemId() != 40309) {
			L1ItemInstance findItem = findItemId(item.getItem().getItemId());
			if (findItem != null) {
				findItem.setCount(findItem.getCount() + item.getCount());
				updateItem(findItem);
				return findItem;
			}
		} else if (item.getItem().getItemId() == 40309) {
			L1ItemInstance findItem = findItemTicketId(40309, item
					.getSecondId(), item.getTicketId());
			if (findItem != null) {
				findItem.setCount(findItem.getCount() + item.getCount());
				updateItem(findItem);
				return findItem;
			}
		}
		/** 흑단막대 아이템갯수는 1개지만 중첩되게.. 시작*/
		switch(item.getItem().getItemId()) {
		case 40006:
		case 40007:
		case 40008:
		case 41401:
		case 140006:
		case 140008:
		case 46091:	
			L1ItemInstance findItem = findItemId(item.getItem().getItemId());
			if (findItem != null) {
				int chargeCount = item.getChargeCount();
				findItem.setChargeCount(findItem.getChargeCount() + chargeCount);
				updateItem(findItem);
				return findItem;
			}
			break;
		}
		/** 흑단막대 아이템갯수는 1개지만 중첩되게.. 끝*/
		item.setX(getX());
		item.setY(getY());
		item.setMap(getMapId());
		_items.add(item);
		insertItem(item);
		return item;
	}

	public boolean consumeItem(int itemid, int count) {
		if (count <= 0) {
			return false;
		}
		if (ItemTable.getInstance().getTemplate(itemid).isStackable()) {
			L1ItemInstance item = findItemId(itemid);
			if (item != null && item.getCount() >= count) {
				removeItem(item, count);
				return true;
			}
		} else {
			L1ItemInstance[] itemList = findItemsId(itemid);
			if (itemList.length == count) {
				for (int i = 0; i < count; i++) {
					removeItem(itemList[i], 1);
				}
				return true;
			} else if (itemList.length > count) {
				DataComparator dc = new DataComparator();
				extracted(itemList, dc);
				for (int i = 0; i < count; i++) {
					removeItem(itemList[i], 1);
				}
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private void extracted(L1ItemInstance[] itemList, DataComparator dc) {
		Arrays.sort(itemList, dc);
	}


	@SuppressWarnings("rawtypes")
	public class DataComparator implements java.util.Comparator {
		public int compare(Object item1, Object item2) {
			return ((L1ItemInstance) item1).getEnchantLevel()
					- ((L1ItemInstance) item2).getEnchantLevel();
		}
	}

	public int removeItem(int objectId, int count) {
		L1ItemInstance item = getItem(objectId);
		return removeItem(item, count);
	}

	public int removeItem(L1ItemInstance item) {
		return removeItem(item, item.getCount());
	}

	public int removeItem(L1ItemInstance item, int count) {
		if (item == null) {
			return 0;
		}
		if (item.getCount() <= 0 || count <= 0) {
			return 0;
		}
		if (item.getCount() < count) {
			count = item.getCount();
		}
		if (item.getCount() == count) {
			int itemId = item.getItem().getItemId();
			if (itemId == 40314 || itemId == 40316) {
				PetTable.getInstance().deletePet(item.getId());
			} else if (itemId >= 41383 && itemId <= 41400) {
				Collection<L1Object> ob = null;
				ob = L1World.getInstance().getObject();
				for (L1Object l1object : ob) {
					if (l1object == null)
						continue;
					if (l1object instanceof L1FurnitureInstance) {
						L1FurnitureInstance obj = (L1FurnitureInstance) l1object;
						if (obj.getItemObjId() == item.getId()) {
							FurnitureSpawnTable.getInstance().deleteFurniture(
									obj);
						}
					}
				}
			} else if (itemId == L1ItemId.DRAGON_KEY) {
				BoardTable.getInstance().delDayExpire(item.getId());
			}
			deleteItem(item);
			L1World.getInstance().removeObject(item);
		} else {
			item.setCount(item.getCount() - count);
			updateItem(item);
		}
		return count;
	}

	public void deleteItem(L1ItemInstance item) {
		_items.remove(item);
	}

	public synchronized L1ItemInstance tradeItem(int objectId, int count,
			Warehouse inventory) {
		L1ItemInstance item = getItem(objectId);
		return tradeItem(item, count, inventory);
	}

	public synchronized L1ItemInstance tradeItem(int objectId, int count,
			L1Inventory inventory) {
		L1ItemInstance item = getItem(objectId);
		return tradeItem(item, count, inventory);
	}

	public synchronized L1ItemInstance tradeItem(L1ItemInstance item,
			int count, Warehouse inventory) {
		if (item == null) {
			return null;
		}
		if (item.getCount() <= 0 || count <= 0) {
			return null;
		}
		if (item.isEquipped()) {
			return null;
		}
		if (!checkItem(item.getItem().getItemId(), count)) {
			return null;
		}
		L1ItemInstance carryItem;
		// 엔진관련 버그 방지 추가
		if (item.getCount() <= count || count < 0) {
			deleteItem(item);
			carryItem = item;
		} else {
			item.setCount(item.getCount() - count);
			updateItem(item);
			carryItem = ItemTable.getInstance().createItem(
					item.getItem().getItemId());
			carryItem.setCount(count);
			carryItem.setEnchantLevel(item.getEnchantLevel());
			carryItem.setIdentified(item.isIdentified());
			carryItem.set_durability(item.get_durability());
			carryItem.setChargeCount(item.getChargeCount());
			carryItem.setRemainingTime(item.getRemainingTime());
			carryItem.setLastUsed(item.getLastUsed());
			carryItem.setBless(item.getItem().getBless());
			carryItem.setAttrEnchantLevel(item.getAttrEnchantLevel());
			carryItem.setRegistLevel(item.getRegistLevel());// 인나티
			carryItem.setProtection(item.getProtection());//추가장비보호
			carryItem.setSecondId(item.getSecondId());
			carryItem.setRoundId(item.getRoundId());
			carryItem.setTicketId(item.getTicketId());
		}
		return inventory.storeTradeItem(carryItem);
	}

	public synchronized L1ItemInstance tradeItem(L1ItemInstance item,
			int count, L1Inventory inventory) {
		if (item == null) {
			return null;
		}
		if (item.getCount() <= 0 || count <= 0) {
			return null;
		}
		if (item.isEquipped()) {
			return null;
		}
		if (!checkItem(item.getItem().getItemId(), count)) {
			return null;
		}
		if (item.getItemId() == L1ItemId.DRAGON_KEY)
			BoardTable.getInstance().delDayExpire(item.getId());

		L1ItemInstance carryItem;
		// 엔진관련 버그 방지 추가
		if (item.getCount() <= count || count < 0) {
			deleteItem(item);
			carryItem = item;
		} else {
			item.setCount(item.getCount() - count);
			updateItem(item);
			carryItem = ItemTable.getInstance().createItem(
					item.getItem().getItemId());
			carryItem.setCount(count);
			carryItem.setEnchantLevel(item.getEnchantLevel());
			carryItem.setIdentified(item.isIdentified());
			carryItem.set_durability(item.get_durability());
			carryItem.setChargeCount(item.getChargeCount());
			carryItem.setRemainingTime(item.getRemainingTime());
			carryItem.setLastUsed(item.getLastUsed());
			carryItem.setBless(item.getItem().getBless());
			carryItem.setAttrEnchantLevel(item.getAttrEnchantLevel());
			carryItem.setRegistLevel(item.getRegistLevel());// 인나티
			carryItem.setProtection(item.getProtection()); //추가장비 보호


		}
		return inventory.storeTradeItem(carryItem);
	}

	public L1ItemInstance receiveDamage(int objectId) {
		L1ItemInstance item = getItem(objectId);
		return receiveDamage(item);
	}

	public L1ItemInstance receiveDamage(L1ItemInstance item) {
		return receiveDamage(item, 1);
	}

	public L1ItemInstance receiveDamage(L1ItemInstance item, int count) {
		int itemType = item.getItem().getType2();
		int currentDurability = item.get_durability();


		if ((currentDurability == 0 && itemType == 0) || currentDurability < 0) {
			item.set_durability(0);
			return null;
		}

		if (itemType == 0) {
			int minDurability = (item.getEnchantLevel() + 5) * -1;
			int durability = currentDurability - count;
			if (durability < minDurability) {
				durability = minDurability;
			}
			if (currentDurability > durability) {
				item.set_durability(durability);
			}
		} else {
			int maxDurability = item.getEnchantLevel() + 5;
			int durability = currentDurability + count;
			if (durability > maxDurability) {
				durability = maxDurability;
			}
			if (currentDurability < durability) {
				item.set_durability(durability);
			}
		}

		updateItem(item, L1PcInventory.COL_DURABILITY);
		return item;
	}

	public L1ItemInstance recoveryDamage(L1ItemInstance item) {
		int itemType = item.getItem().getType2();
		int durability = item.get_durability();



		if ((durability == 0 && itemType != 0) || durability < 0) {
			item.set_durability(0);
			return null;
		}

		if (itemType == 0) {
			item.set_durability(durability + 1);
		} else {
			item.set_durability(durability - 1);
		}

		updateItem(item, L1PcInventory.COL_DURABILITY);
		return item;
	}

	public L1ItemInstance findItemId(int id) {
		for (L1ItemInstance item : _items) {
			if (item == null)
				continue;
			if (item.getItem().getItemId() == id) {
				return item;
			}
		}
		return null;
	}

	public L1ItemInstance[] findItemsId(int id) {
		ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (L1ItemInstance item : _items) {
			if (item == null)
				continue;
			if (item.getItemId() == id) {
				itemList.add(item);
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}

	public L1ItemInstance[] findItemsIdNotEquipped(int id) {
		ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (L1ItemInstance item : _items) {
			if (item == null)
				continue;
			if (item.getItemId() == id) {
				if (!item.isEquipped()) {
					itemList.add(item);
				}
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}

	public L1ItemInstance getItem(int objectId) {
		L1ItemInstance item = null;
		for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item == null)
				continue;
			if (item.getId() == objectId) {
				return item;
			}
		}
		return null;
	}

	/** 조우의 돌골렘 (인챈트 아이템 삭제)
	 *
	 * @param itemid 
	 * 			- 제련시 필요한 무기번호
	 * 
	 * @param enchantLevel -
	 *            제련시 필요한 무기의 인챈트레벨
	 */
	public boolean MakeDeleteEnchant(int itemid, int enchantLevel) {
		L1ItemInstance item = findItemId(itemid);
		if (item != null && item.getEnchantLevel() == enchantLevel) {
			removeItem(item, 1);
			return true;
		}
		return false;
	}
	
	

	/**
	 * 조우의 돌골렘 (인챈트 아이템 검사)
	 * 
	 * @param id -
	 *            제련시 필요한 무기번호
	 * 
	 * @param enchantLevel -
	 *            제련시 필요한 무기의 인챈트 레벨
	 * 
	 */
	public boolean MakeCheckEnchant(int id, int enchantLevel) {
		L1ItemInstance item = findItemId(id);
	   if(item.getCount() <= 0){
		   return false;
	   }
		if (item != null && item.getEnchantLevel() == enchantLevel
				&& item.getCount() >= 1) {
			return true;
		}
		return false;
	}
	public boolean checkItem(int id) {
		return checkItem(id, 1);
	}

	public boolean checkItem(int id, int count) {
		if (count == 0) {
			return true;
		}
		L1Item tem = ItemTable.getInstance().getTemplate(id);
		if(tem == null)
			System.out.println("아이템 체크 에러 없는 아이템 : "+id);
		if (tem.isStackable()) {
			L1ItemInstance item = findItemId(id);
			if (item != null && item.getCount() >= count) {
				return true;
			}
		} else {
			Object[] itemList = findItemsId(id);
			if (itemList.length >= count) {
				return true;
			}
		}
		return false;
	}

	public boolean checkItemNotEquipped(int id, int count) {
		if (count == 0) {
			return true;
		}
		return count <= countItems(id);
	}

	public boolean checkItem(int[] ids) {
		int len = ids.length;
		int[] counts = new int[len];
		for (int i = 0; i < len; i++) {
			counts[i] = 1;
		}
		return checkItem(ids, counts);
	}

	public boolean checkItem(int[] ids, int[] counts) {
		for (int i = 0; i < ids.length; i++) {
			if (!checkItem(ids[i], counts[i])) {
				return false;
			}
		}
		return true;
	}

	public int countItems(int id) {
		if (ItemTable.getInstance().getTemplate(id).isStackable()) {
			L1ItemInstance item = findItemId(id);
			if (item != null) {
				return item.getCount();
			}
		} else {
			Object[] itemList = findItemsIdNotEquipped(id);
			return itemList.length;
		}
		return 0;
	}

	public void shuffle() {
		Collections.shuffle(_items);
	}

	public void clearItems() {
		L1ItemInstance item = null;
		for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item == null)
				continue;
			L1World.getInstance().removeObject(item);
		}
		_items.clear();
	}

	public void loadItems() {
	}

	public void insertItem(L1ItemInstance item) {
	}

	public void updateItem(L1ItemInstance item) {
	}

	public void updateItem(L1ItemInstance item, int colmn) {
	}
	 /** 아이템 삭제시간 셋팅 메서드 추가 **/
	 private void SetDeleteTime(L1ItemInstance item, int minute) {
	  Timestamp deleteTime = null;
	  deleteTime = new Timestamp(System.currentTimeMillis() + (60000 * minute));
	  item.setEndTime(deleteTime);
	 }
	// 새로운 아이템의 격납 : 쪼꼬 재코딩
	public L1ItemInstance storeItem(int id, int count, String name) {
		L1Item sTemp = ItemTable.getInstance().getTemplate(id);
		String sname = "테베 오시리스 제단 열쇠 [" + CrockSystem.getInstance().OpenTime()
				+ "]";
		L1Item temp = ItemTable.getInstance().clone(sTemp, sname);
		if (temp == null)
			return null;
		if (temp.isStackable()) {
			L1ItemInstance item = new L1ItemInstance(temp, count);
			item.setItem(temp);
			item.setCount(count);
			item.setBless(temp.getBless());
			item.setAttrEnchantLevel(0);
			if (!temp.isStackable() || findItemId(id) == null) {// 새롭게 생성할 필요가
				// 있는 경우만 ID의
				// 발행과 L1World에의
				// 등록을 실시한다
				item.setId(ObjectIdFactory.getInstance().nextId());
				L1World.getInstance().storeObject(item);
			}
			return storeItem(item);
		}

		// 스택 할 수 없는 아이템의 경우
		L1ItemInstance result = null;
		L1ItemInstance item = null;
		for (int i = 0; i < count; i++) {
			item = new L1ItemInstance(temp);
			item.setId(ObjectIdFactory.getInstance().nextId());
			item.setBless(temp.getBless());
			item.setAttrEnchantLevel(0);
			L1World.getInstance().storeObject(item);
			storeItem(item);
			result = item;
		}
		// 마지막에 만든 아이템을 돌려준다. 배열을 되돌리도록(듯이) 메소드 정의를 변경하는 편이 좋을지도 모른다.
		return result;
	}

	// 아이템 ID, second_id, ticketId로부터 검색
	public L1ItemInstance findItemTicketId(int id, int secid, int ticketid) {
		for (L1ItemInstance item : _items) {
			if (item == null)
				continue;
			if (item.getItem().getItemId() == id && item.getSecondId() == secid
					&& item.getTicketId() == ticketid) {
				return item;
			}
		}
		return null;
	}

	/** 펫 물약 먹기 */	
	public L1ItemInstance getItemOne(int[] ids){
		int len = ids.length;
		L1ItemInstance item = null;
		for(int i = 0; i < len; ++i){
			item = getItem(ids[i]);
		}
		return item;
	}
	/** 펫 물약 먹기 */
}
