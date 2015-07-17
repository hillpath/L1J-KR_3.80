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
package l1j.server.server.model;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_AddItem;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DeleteInventoryItem;
import l1j.server.server.serverpackets.S_ItemColor;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_ItemStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1Item;

public class L1PcInventory extends L1Inventory {

	/** 날짜 및 시간 기록 * */
	Calendar rightNow = Calendar.getInstance();

	int day = rightNow.get(Calendar.DATE);

	int hour = rightNow.get(Calendar.HOUR);

	int min = rightNow.get(Calendar.MINUTE);

	int sec = rightNow.get(Calendar.SECOND);

	int year = rightNow.get(Calendar.YEAR);

	int month = rightNow.get(Calendar.MONTH) + 1;

	String totime = "[" + year + ":" + month + ":" + day + "]";

	String totime1 = "[" + hour + ":" + min + ":" + sec + "]";

	String date = +year + "_" + month + "_" + day;

	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger
			.getLogger(L1PcInventory.class.getName());

	private static final int MAX_SIZE = 180;

	private final L1PcInstance _owner;

	private int _arrowId;

	private int _stingId;

	private long timeVisible = 0;

	private long timeVisibleDelay = 3000;

	public L1PcInventory(L1PcInstance owner) {
		_owner = owner;
		_arrowId = 0;
		_stingId = 0;
	}

	public L1PcInstance getOwner() {
		return _owner;
	}

	// 240단계의 무게 단위
	public int getWeight240() {
		return calcWeight240(getWeight());
	}

	// 240단계의 무게를 계산한다
	public int calcWeight240(int weight) {
		int weight240 = 0;
		if (Config.RATE_WEIGHT_LIMIT != 0) {
			double maxWeight = _owner.getMaxWeight();
			if (weight > maxWeight) {
				weight240 = 240;
			} else {
				double wpTemp = (weight * 100 / maxWeight) * 240.00 / 100.00;
				DecimalFormat df = new DecimalFormat("00.##");
				df.format(wpTemp);
				wpTemp = Math.round(wpTemp);
				weight240 = (int) (wpTemp);
			}
		} else { // 웨이트 레이트가 0이라면 중량 항상 0
			weight240 = 0;
		}
		return weight240;
	}

	@Override
	public int checkAddItem(L1ItemInstance item, int count) {
		return checkAddItem(item, count, true);
	}

	public int checkAddItem(L1ItemInstance item, int count, boolean message) {
		if (item == null) {
			return -1;
		}
		if (getSize() >= MAX_SIZE
				|| (getSize() == MAX_SIZE && (!item.isStackable() || !checkItem(item
						.getItem().getItemId())))) {
			if (message) {
				sendOverMessage(263);
				//sendMessage("버그 시도: ["+_owner.getName()+"]");
			}
			return SIZE_OVER;
		}

		int weight = getWeight() + item.getItem().getWeight() * count / 1000
				+ 1;
		if (weight < 0 || (item.getItem().getWeight() * count / 1000) < 0) {
			if (message) {
				sendOverMessage(82); // 아이템이 너무 무거워, 더 이상 가질 수 없습니다.
				//sendMessage("버그 시도: ["+_owner.getName()+"]");
			}
			return WEIGHT_OVER;
		}
		if (calcWeight240(weight) >= 240) {
			if (message) {
				sendOverMessage(82); // 아이템이 너무 무거워, 더 이상 가질 수 없습니다.
				//sendMessage("버그 시도: ["+_owner.getName()+"]");
			}
			return WEIGHT_OVER;
		}

		L1ItemInstance itemExist = findItemId(item.getItemId());
		if (itemExist != null && (itemExist.getCount() + count) > MAX_AMOUNT) {
			if (message) {
				getOwner().sendPackets(new S_ServerMessage(166, "소지하고 있는 아데나","2,000,000,000을 초과하고 있습니다.")); // \f1%0이%4%1%3%2
				//sendMessage("버그 시도: 아데나초과 > ["+_owner.getName()+"]");
			}
			return AMOUNT_OVER;
		}
		/*얼던리뉴얼*/
		 if (item.getItem().getItemId() == 46111) {
			   L1ItemInstance inventoryItem = _owner.getInventory().findItemId(46111);
			   int inventoryItemCount = 0;
			   if (inventoryItem != null) {
			    inventoryItemCount = inventoryItem.getCount();
			   }
			   if (inventoryItemCount >= 100) {
			    _owner.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "얼음 수정 동굴의 조사가 끝났으니, 마빈에게 가보십시오."));
			   }
		 }
		return OK;
	}

	public void sendOverMessage(int message_id) {
		_owner.sendPackets(new S_ServerMessage(message_id));
	}

	// DB의 character_items의 독입
	@Override
	public void loadItems() {
		try {
			CharactersItemStorage storage = CharactersItemStorage.create();

			for (L1ItemInstance item : storage.loadItems(_owner.getId())) {
				_items.add(item);

				if (item.isEquipped()) {
					item.setEquipped(false);
					setEquipped(item, true, true, false);
				}
				L1World.getInstance().storeObject(item);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1PcInventory[]Error", e);
		}
	}


	// DB의 character_items에 등록
	@Override
	public synchronized void insertItem(L1ItemInstance item) {
		if(_owner.noPlayerCK)
			return;
		_owner.sendPackets(new S_AddItem(item));
		if (item.getItem().getWeight() != 0) {
			_owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT,
					getWeight240()));
		}
		try {
			CharactersItemStorage storage = CharactersItemStorage.create();
			storage.storeItem(_owner.getId(), item);
		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1PcInventory[]Error1", e);
		}
	}
	
	public static int COL_PANDORA = 2048*8;
	
	public static int COL_PROTEC = 2048*4;
	
	public static int COL_INNA = 2048*2;
	
	public static int COL_CLOCK = 2048; //
	
	public static final int COL_ATTRENCHANTLVL = 1024;

	public static final int COL_BLESS = 512;

	public static final int COL_REMAINING_TIME = 256;

	public static final int COL_CHARGE_COUNT = 128;

	public static final int COL_ITEMID = 64;

	public static final int COL_DELAY_EFFECT = 32;

	public static final int COL_COUNT = 16;

	public static final int COL_EQUIPPED = 8;

	public static final int COL_ENCHANTLVL = 4;

	public static final int COL_IS_ID = 2;

	public static final int COL_DURABILITY = 1;

	@Override
	public void updateItem(L1ItemInstance item) {
		updateItem(item, COL_COUNT);
		if (item.getItem().isToBeSavedAtOnce()) {
			saveItem(item, COL_COUNT);
		}
	}

	/**
	 * 목록내의 아이템 상태를 갱신한다.
	 * 
	 * @param item -
	 *            갱신 대상의 아이템
	 * @param column -
	 *            갱신하는 스테이터스의 종류
	 */
	@Override
	public void updateItem(L1ItemInstance item, int column) {
		if (column >= COL_PANDORA) { // 엔챤트
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_PANDORA;
		}
		if (column >= COL_PROTEC) { // 엔챤트
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_PROTEC;
		}
		if (column >= COL_INNA) { // 엔챤트
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_INNA;
		}
		if (column >= COL_ATTRENCHANTLVL) {
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_ATTRENCHANTLVL;
		}
		if (column >= COL_BLESS) {
			_owner.sendPackets(new S_ItemColor(item));
			column -= COL_BLESS;
		}
		if (column >= COL_REMAINING_TIME) { // 사용 가능한 남은 시간
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_REMAINING_TIME;
		}
		if (column >= COL_CLOCK) { 
		     _owner.sendPackets(new S_ItemName(item));
		     column -= COL_CLOCK;
		   }  //추가
		if (column >= COL_CHARGE_COUNT) { // 사용 가능한 횟수
			_owner.sendPackets(new S_ItemName(item));
			// _owner.sendPackets(new S_ItemAmount(item));
			column -= COL_CHARGE_COUNT;
		}
		if (column >= COL_ITEMID) { // 다른 아이템이 되는 경우(편지지를 개봉했을 때 등)
			_owner.sendPackets(new S_ItemStatus(item));
			_owner.sendPackets(new S_ItemColor(item));
			_owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT,
					getWeight240()));
			column -= COL_ITEMID;
		}
		if (column >= COL_DELAY_EFFECT) {
			column -= COL_DELAY_EFFECT;
		}
		if (column >= COL_COUNT) {// 카운트
			int weight = item.getWeight();
			if (weight != item.getLastWeight()) {
				item.setLastWeight(weight);
			} else {
				_owner.sendPackets(new S_ItemName(item));
			}
			_owner.sendPackets(new S_ItemStatus(item));
			if (item.getItem().getWeight() != 0) {
				// 무게가 변하지 않았을 경우 그냥 보내도 된다.
				_owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT,
						getWeight240()));
			}
			column -= COL_COUNT;
		}
		if (column >= COL_EQUIPPED) { // 장비 상태
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_EQUIPPED;
		}
		if (column >= COL_ENCHANTLVL) { // 엔챤트
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_ENCHANTLVL;
		}
		if (column >= COL_IS_ID) { // 확인장태
			_owner.sendPackets(new S_ItemStatus(item));
			_owner.sendPackets(new S_ItemColor(item));
			column -= COL_IS_ID;
		}
		if (column >= COL_DURABILITY) { // 내구성
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_DURABILITY;
		}
	}

	/**
	 * 목록내의 아이템 상태를 DB에 보존한다.
	 * 
	 * @param item -
	 *            갱신 대상의 아이템
	 * @param column -
	 *            갱신하는 스테이터스의 종류
	 */
	public void saveItem(L1ItemInstance item, int column) {
		if (column == 0) {
			return;
		}

		try {
			CharactersItemStorage storage = CharactersItemStorage.create();
			if (column >= COL_PANDORA) { // 엔챤트
				storage.updateItemEnchantLevel(item);
				storage.updateItemRegistLevel(item); // 인나티
				storage.updateItemProtection(item); //추가장비 보호
				storage.updateItemPandoraT(item); // 판도라 티셔츠
				column -= COL_PANDORA;
			}
			if (column >= COL_PROTEC) { // 엔챤트
				storage.updateItemEnchantLevel(item);
				storage.updateItemRegistLevel(item); // 인나티
				storage.updateItemProtection(item); //추가장비 보호
				storage.updateItemPandoraT(item); // 판도라 티셔츠
				column -= COL_PROTEC;
			}
			if (column >= COL_INNA) { // 엔챤트
				storage.updateItemEnchantLevel(item);
				storage.updateItemRegistLevel(item); // 인나티
				storage.updateItemProtection(item); //추가장비 보호
				storage.updateItemPandoraT(item); // 판도라 티셔츠
				column -= COL_INNA;
			}
			if (column >= COL_ATTRENCHANTLVL) {
				storage.updateItemAttrEnchantLevel(item);
				column -= COL_ATTRENCHANTLVL;
			}
			if (column >= COL_BLESS) {
				storage.updateItemBless(item);
				column -= COL_BLESS;
			}
			if (column >= COL_REMAINING_TIME) {
				storage.updateItemRemainingTime(item);
				column -= COL_REMAINING_TIME;
			}
			if (column >= COL_CLOCK) {
			    storage.updateClock(item);
			    storage.updateEndTime(item);
			    column -= COL_CLOCK;
			   } //추가
			if (column >= COL_CHARGE_COUNT) {
				storage.updateItemChargeCount(item);
				column -= COL_CHARGE_COUNT;
			}
			if (column >= COL_ITEMID) {
				storage.updateItemId(item);
				column -= COL_ITEMID;
			}
			if (column >= COL_DELAY_EFFECT) {
				storage.updateItemDelayEffect(item);
				column -= COL_DELAY_EFFECT;
			}
			if (column >= COL_COUNT) {
				storage.updateItemCount(item);
				column -= COL_COUNT;
			}
			if (column >= COL_EQUIPPED) {
				storage.updateItemEquipped(item);
				column -= COL_EQUIPPED;
			}
			if (column >= COL_ENCHANTLVL) {
				storage.updateItemEnchantLevel(item);
				storage.updateItemRegistLevel(item); // 인나티
				storage.updateItemProtection(item); //추가장비 보호
				storage.updateItemPandoraT(item); // 판도라 티셔츠
				column -= COL_ENCHANTLVL;
			}
			if (column >= COL_IS_ID) {
				storage.updateItemIdentified(item);
				column -= COL_IS_ID;
			}
			if (column >= COL_DURABILITY) {
				storage.updateItemDurability(item);
				column -= COL_DURABILITY;
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1PcInventory[]Error2", e);
		}
	}

	// DB의 character_items로부터 삭제
	@Override
	public void deleteItem(L1ItemInstance item) {
		try {
			CharactersItemStorage storage = CharactersItemStorage.create();

			storage.deleteItem(item);
		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1PcInventory[]Error3", e);
		}
		if (item.isEquipped()) {
			setEquipped(item, false);
		}
		_owner.sendPackets(new S_DeleteInventoryItem(item));
		_items.remove(item);
		if (item.getItem().getWeight() != 0) {
			_owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT,
					getWeight240()));
		}
	}

	// 아이템을 장착 탈착시킨다(L1ItemInstance의 변경, 보정치의 설정, character_items의 갱신, 패킷 송신까지
	// 관리)
	public void setEquipped(L1ItemInstance item, boolean equipped) {
		setEquipped(item, equipped, false, false);
	}

	public void setEquipped(L1ItemInstance item, boolean equipped,
			boolean loaded, boolean changeWeapon) {
		if (item.isEquipped() != equipped) {
			L1Item temp = item.getItem();
			if (equipped) {
				if (temp.getItemId() == 20077 || temp.getItemId() == 20062|| temp.getItemId() == 120077) {
					if (System.currentTimeMillis() - timeVisible < timeVisibleDelay) {
						return;
					}
				}
				item.setEquipped(true);
				_owner.getEquipSlot().set(item);
			} else {
				if (!loaded) {
					if (temp.getItemId() == 20077 || temp.getItemId() == 20062
							|| temp.getItemId() == 120077) {
						if (_owner.isInvisble()) {
							_owner.delInvis();
							return;
						}
						timeVisible = System.currentTimeMillis();
					}
				}
				item.setEquipped(false);
				_owner.getEquipSlot().remove(item);
			}
			if (!loaded) {
				_owner.setCurrentHp(_owner.getCurrentHp());
				_owner.setCurrentMp(_owner.getCurrentMp());
				updateItem(item, COL_EQUIPPED);
				_owner.sendPackets(new S_OwnCharStatus(_owner));
				if (temp.getType2() == 1 && changeWeapon == false) {
					_owner.sendPackets(new S_CharVisualUpdate(_owner));
					Broadcaster.broadcastPacket(_owner, new S_CharVisualUpdate(
							_owner));
				}
				// _owner.getNetConnection().saveCharToDisk(_owner); //
			}
			// 아이템 착용 처리에 대한 패킷 처리.
			_owner.getInventory().toSlotPacket(_owner, item);
			// 아이템패킷추가
		}
	}
	
	
	
	
	
	private void sendMessage(String msg) {
		  for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
		   pc.sendPackets(new S_ChatPacket(pc, msg, Opcodes.S_OPCODE_MSG, 18));
		  }
		 }
	public boolean checkEquipped2Ring(int id){//반지착용갯수확인
		  int cnt = 0;
		  L1ItemInstance item = null;  
		  for(Object itemObject : _items){
		   item = (L1ItemInstance) itemObject;
		   if (item.getItem().getItemId() == id && item.isEquipped()) {
		    cnt += 1;
		   }
		  }
		  if(cnt >= 2){
		   return true;
		  }else
		   return false;
		 }
	public boolean getEnchantEquipped(int id, int enchant) { //룸티스 물약효율부분
		L1ItemInstance item = null;
		for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item.getItem().getItemId() == id
					&& item.getEnchantLevel() == enchant && item.isEquipped()) {
				return true;
			}
		}
		return false;
	}

	public boolean checkEquipped(int id) {
		L1ItemInstance item = null;
		for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item.getItem().getItemId() == id && item.isEquipped()) {
				return true;
			}
		}
		return false;
	}

	public boolean checkEquipped(int[] ids) {
		for (int id : ids) {
			if (!checkEquipped(id)) {
				return false;
			}
		}
		return true;
	}
	public int getTypeEquipped(int type2, int type) {
		int equipeCount = 0;
		L1ItemInstance item = null;
		for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item.getItem().getType2() == type2
					&& item.getItem().getType() == type && item.isEquipped()) {
				equipeCount++;
			}
		}
		return equipeCount;
	}

	/**
	 * @return type2=1weapon 2armor enchant
	 */

	public int getItemEnchantCount(int type2, int enchant) {
		int cnt = 0;
		L1ItemInstance item = null;
		for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item.getItem().getType2() == type2
					&& item.getEnchantLevel() >= enchant) {
				cnt++;
			}
		}
		return cnt;
	}

	public int getGarderEquipped(int type2, int type, int gd) {
		int equipeCount = 0;
		L1ItemInstance item = null;
		for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item.getItem().getType2() == type2
					&& item.getItem().getType() == type
					&& item.getItem().getUseType() != gd && item.isEquipped()) {
				equipeCount++;
			}
		}
		return equipeCount;
	}

	public L1ItemInstance getItemEquipped(int type2, int type) {
		L1ItemInstance equipeitem = null;
		L1ItemInstance item = null;
		for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item.getItem().getType2() == type2
					&& item.getItem().getType() == type && item.isEquipped()) {
				equipeitem = item;
				break;
			}
		}
		return equipeitem;
	}

	public L1ItemInstance[] getRingEquipped() {
		L1ItemInstance equipeItem[] = new L1ItemInstance[2];
		int equipeCount = 0;
		L1ItemInstance item = null;
		for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item.getItem().getType2() == 2 && item.getItem().getType() == 9
					&& item.isEquipped()) {
				equipeItem[equipeCount] = item;
				equipeCount++;
				if (equipeCount == 2) {
					break;
				}
			}
		}
		return equipeItem;
	}

	public void takeoffEquip(int polyid) {
		takeoffWeapon(polyid);
		takeoffArmor(polyid);
	}

	private void takeoffWeapon(int polyid) {
		if (_owner.getWeapon() == null) {
			return;
		}

		boolean takeoff = false;
		int weapon_type = _owner.getWeapon().getItem().getType();
		takeoff = !L1PolyMorph.isEquipableWeapon(polyid, weapon_type);

		if (takeoff) {
			setEquipped(_owner.getWeapon(), false, false, false);
		}
	}

	private void takeoffArmor(int polyid) {
		L1ItemInstance armor = null;

		for (int type = 0; type <= 13; type++) {
			if (getTypeEquipped(2, type) != 0
					&& !L1PolyMorph.isEquipableArmor(polyid, type)) {
				if (type == 9) {
					armor = getItemEquipped(2, type);
					if (armor != null) {
						setEquipped(armor, false, false, false);
					}
					armor = getItemEquipped(2, type);
					if (armor != null) {
						setEquipped(armor, false, false, false);
					}
				} else {
					armor = getItemEquipped(2, type);
					if (armor != null) {
						setEquipped(armor, false, false, false);
					}
				}
			}
		}
	}


	/** 2011.11.15 고정수 인공지능 */	
	private L1ItemInstance _arrow; 

	public L1ItemInstance getArrow() {
		if (_owner.getRobotAi() != null) {
			if (_arrow == null) {
				_arrow = ItemTable.getInstance().createItem(40744);
			}
			_arrow.setCount(2);
			return _arrow;
		} else {
			return getBullet(0);
		}
	}

	public L1ItemInstance getSting() {
		return getBullet(15);
	}

	private L1ItemInstance getBullet(int type) {
		L1ItemInstance bullet;
		int priorityId = 0;
		if (type == 0) {
			priorityId = _arrowId;
		}
		if (type == 15) {
			priorityId = _stingId;
		}
		if (priorityId > 0) {
			bullet = findItemId(priorityId);
			if (bullet != null) {
				return bullet;
			} else {
				if (type == 0) {
					_arrowId = 0;
				}
				if (type == 15) {
					_stingId = 0;
				}
			}
		}

		for (Object itemObject : _items) {
			bullet = (L1ItemInstance) itemObject;
			if (bullet.getItem().getType() == type
					&& bullet.getItem().getType1() == 0) {
				if (type == 0) {
					_arrowId = bullet.getItem().getItemId();
				}
				if (type == 15) {
					_stingId = bullet.getItem().getItemId();
				}
				return bullet;
			}
		}
		return null;
	}

	public void setArrow(int id) {
		_arrowId = id;
	}

	public void setSting(int id) {
		_stingId = id;
	}

	public int hpRegenPerTick() {
		int hpr = 0;
		L1ItemInstance item = null;
		for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item.isEquipped()) {
				hpr += item.getItem().get_addhpr();
			}
		}
		return hpr;
	}

	public int mpRegenPerTick() {
		int mpr = 0;
		L1ItemInstance item = null;
		for (Object itemObject : _items) {
			item = (L1ItemInstance) itemObject;
			if (item.isEquipped()) {
				mpr += item.getItem().get_addmpr();
			}
		}
		return mpr;
	}

	public L1ItemInstance CaoPenalty() {//특정템죽어도드랍안되게하기
		Random random = new Random();
		int rnd = random.nextInt(_items.size());
		L1ItemInstance penaltyItem = _items.get(rnd);
		if (penaltyItem.getItem().getItemId() == L1ItemId.ADENA
				|| !penaltyItem.getItem().isTradable()) {//여기에추가
			return null;
		}
		Object[] petlist = _owner.getPetList().values().toArray();
		for (Object petObject : petlist) {
			if (petObject instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) petObject;
				if (penaltyItem.getId() == pet.getItemObjId()) {
					return null;
				}
			}
		}
		setEquipped(penaltyItem, false);
		return penaltyItem;
	}
	
	public boolean MakeDeleteEnchant(int itemid, int enchantLevel) {
		L1ItemInstance item = findItemId(itemid);
		if (item != null && item.getEnchantLevel() == enchantLevel) {
			removeItem(item, 1);
			return true;
		}
		return false;
	}

	public L1ItemInstance KillPenalty() {
		L1ItemInstance Item = findItemId(L1ItemId.ADENA);
		if(Item == null)
			return null;
		return Item;
	}

	/** 인첸트 레벨을 가져오는 함수 */
	public int getEnchantCount(int id){
		int cnt = 0;
		L1ItemInstance item = null;  
		for(Object itemObject : _items){
			item = (L1ItemInstance) itemObject;
			if(item.getItemId() == id){
				cnt = item.getEnchantLevel();
			}
		}
		return cnt;
	}
}
