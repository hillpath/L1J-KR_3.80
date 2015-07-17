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
package l1j.server.server.model.shop;

import static l1j.server.server.model.skill.L1SkillId.ADVANCE_SPIRIT;
import static l1j.server.server.model.skill.L1SkillId.BLESS_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.EARTH_SKIN;
import static l1j.server.server.model.skill.L1SkillId.FIRE_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;
import static l1j.server.server.model.skill.L1SkillId.WIND_SHOT;

import java.util.List;
import java.util.Random;

import javolution.util.FastTable;
import l1j.server.Config;
import l1j.server.Warehouse.PackageWarehouse;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.TownTable;
import l1j.server.server.model.L1BugBearRace;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1TaxCalculator;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.utils.IntRange;

public class L1Shop {
	private final int _npcId;

	private final List<L1ShopItem> _sellingItems;

	private final List<L1ShopItem> _purchasingItems;

	public L1Shop(int npcId, List<L1ShopItem> sellingItems,
			List<L1ShopItem> purchasingItems) {
		if (sellingItems == null || purchasingItems == null) {
			throw new NullPointerException();
		}
		_npcId = npcId;
		_sellingItems = sellingItems;
		_purchasingItems = purchasingItems;
	}

	public void sellItems(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		// 세율 안들어가는 NPC
		if (getNpcId() == 70068 || getNpcId() == 70020 || getNpcId() == 70056
				|| getNpcId() == 70051 || getNpcId() == 70055
				|| getNpcId() == 4213002 || getNpcId() == 70017
				|| getNpcId() == 4200105 || getNpcId() == 777835
				|| getNpcId() == 70035 || getNpcId() == 70041
				|| getNpcId() == 154824	|| getNpcId() == 70042
				|| getNpcId() == 877831 || getNpcId() == 154826
				|| getNpcId() == 877832 || getNpcId() == 877833
				|| getNpcId() == 877834 || getNpcId() == 877835
				|| getNpcId() == 877836 || getNpcId() == 877837
				|| getNpcId() == 97905  //N코인상인
				|| getNpcId() == 877838) {
			if (!NoTaxEnsureSell(pc, orderList)) {
				return;
			}
			NoTaxSellItems(pc.getInventory(), orderList);
			return;
		}
		
		if (getNpcId() == 880587 || getNpcId() == 880595) {//깃털 겜블상점 sunny
			if (!NoTaxEnsureSell2(pc, orderList)){
				return;
			}
			NoTaxSellItems2(pc.getInventory(), orderList);
			return;
		}
		
		// 1차 코인 상인
		if (getNpcId() >= 9000001 && getNpcId() <= 9000012) {
			if (!ensureCashSell1(pc, orderList, getNpcId())) {
				return;
			}
			sellCashItems1(pc, pc.getInventory(), orderList, getNpcId());
			return;
		}
		
		// 2차 코인 상인
		if (getNpcId() >= 9000013 && getNpcId() <= 9000024) {
			if (!ensureCashSell2(pc, orderList, getNpcId())) {
				return;
			}
			sellCashItems2(pc, pc.getInventory(), orderList, getNpcId());
			return;
		}
		
		// 3차 코인 상인
		if (getNpcId() >= 9000025 && getNpcId() <= 9000036) {
			if (!ensureCashSell3(pc, orderList, getNpcId())) {
				return;
			}
			sellCashItems3(pc, pc.getInventory(), orderList, getNpcId());
			return;
		}
		// 영자쫄따구 엔피씨무인상점
		if (getNpcId() >= 8000001 && getNpcId() <=8000484) { //
			if (!NoTaxEnsureSell(pc, orderList)) {
				return;
			}

			NpcShopSellItems(pc.getInventory(), orderList);
			return;

		}

		// 고대의 금화 상인 (트릭)
		if (getNpcId() == 4208001) {
			if (!AGEnsureSell(pc, orderList)) {
				return;
			}
			AGSellItems(pc.getInventory(), orderList);
			return;
		}
		// 프리미엄 상점
		if (getNpcId() == 4220000 || getNpcId() == 4220001 || getNpcId() == 402031
				|| getNpcId() == 4220002 || getNpcId() == 4220003
				|| getNpcId() == 154824 || getNpcId() == 154826 //덩키, 피에로
				|| getNpcId() == 880587 || getNpcId() == 880595
				|| getNpcId() == 4220700 || getNpcId() == 5000401) {
			if (!ensurePremiumSell(pc, orderList)) {
				return;
			}
			sellPremiumItems(pc.getInventory(), orderList);
			return;
		}
		// 전쟁물자 상인(징표)
		if (getNpcId() == 4200104) {
			if (!ensureMarkSell(pc, orderList)) {
				return;
			}
			sellMarkItems(pc.getInventory(), orderList);
			return;
		}
		// N코인
	if (getNpcId() == 97905) {
		if (!NcoinSell(pc, orderList)) {
			return;
		}
		NcoinSellItems(pc, orderList);
		return;
	}
		if (!ensureSell(pc, orderList)) {
			return;
		} else {
			sellItems(pc.getInventory(), orderList);
			payTax(orderList);
		}
	}

	/** N코인 */
	private boolean NcoinSell(L1PcInstance pc,	L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		if (!IntRange.includes(price, 0, 100000)) {
			pc.sendPackets(new S_SystemMessage("N코인은 한번에 십만개 이상 사용할수 없습니다."));
			return false;
		}
		if (!pc.getInventory().checkItem(46100, price)) { // 구입할 수 있을까 체크
			pc.sendPackets(new S_SystemMessage("N코인이 부족합니다. "));
			return false;
		}
		
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}
	
	/** N코인 */
	private void NcoinSellItems(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		if (!pc.getInventory().consumeItem(46100, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 N코인을 소비할 수 없었습니다.");
		}
		
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int enchant = order.getItem().getEnchant();
			int count = order.getCount();
			
			PackageWarehouse.itemshop(pc.getAccountName(), itemId, enchant, count);
		}
		pc.sendPackets(new S_SystemMessage("구입하신 아이템은 부가 서비스 창고에서 찾으실 수 있습니다."));
	}
	
	private boolean ensureSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPriceTaxIncluded();
		if (!IntRange.includes(price, 0, 2000000000)) {
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}
		if (!pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아데나가 충분치 않습니다.", Opcodes.S_OPCODE_MSG, 20); 
			pc.sendPackets(s_chatpacket);
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		// 구매수량한계 by
		int Count = 0;// 구매수량
		int new_price = 0;// 구매 금액
		int set_count = 0;// 가능 구매수량
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			Count += 1;
			new_price += orderList.getTotalPrice();
		}
		set_count = 2000000000 / new_price;
		if (Count > set_count) {
			pc.sendPackets(new S_ServerMessage(936));
			return false;
		}
		// 구매수량한계 by end
		// (슈크림)버그 방지
		// # (버그 방지) 상점 버그 방지

		return true;
	}

	private void sellItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(L1ItemId.ADENA, orderList
				.getTotalPriceTaxIncluded())) {
			throw new IllegalStateException("구입에 필요한 아데나를 소비 할 수 없습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			item = ItemTable.getInstance().createItem(itemId);
			if (getSellingItems().contains(item)) {
				return;
			}
			item.setCount(amount);
			item.setIdentified(true);
			// 배당을 측정하기 위한 추가 부분
			if (getNpcId() == 70035 || getNpcId() == 70041
					|| getNpcId() == 70042) {
				int[] ticket = L1BugBearRace.getInstance().getTicketInfo(
						order.getOrderNumber());
				item.setSecondId(ticket[0]);
				item.setRoundId(ticket[1]);
				item.setTicketId(ticket[2]);
				L1BugBearRace.getInstance().addBetting(order.getOrderNumber(),
						amount);
			}
			inv.storeItem(item);

		}
	}

	private void NpcShopSellItems(L1PcInventory inv,
			L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(L1ItemId.ADENA, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 아데나를 소비 할 수 없습니다.");
		}

		L1ItemInstance item = null;
		boolean[] isRemoveFromList = new boolean[8];
		for (L1ShopBuyOrder order : orderList.getList()) {
			int orderid = order.getOrderNumber();
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			int enchant = order.getItem().getEnchant();
			int remaindcount = getSellingItems().get(orderid).getCount();

			if (remaindcount < amount)
				return;

			item = ItemTable.getInstance().createItem(itemId);
			if (getSellingItems().contains(item)) {
				return;
			}

			item.setCount(amount);
			item.setIdentified(true);
			item.setEnchantLevel(enchant);

			if (remaindcount == amount)
				isRemoveFromList[orderid] = true;
			else
				_sellingItems.get(orderid).setCount(remaindcount - amount);

			inv.storeItem(item);

			for (int i = 7; i >= 0; i--) {
				if (isRemoveFromList[i]) {
					_sellingItems.remove(i);
				}
			}
		}
	}

	private boolean NoTaxEnsureSell(L1PcInstance pc,
			L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		if (!IntRange.includes(price, 0, 2000000000)) {
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}
		if (!pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아데나가 충분치 않습니다.", Opcodes.S_OPCODE_MSG, 20); 
			pc.sendPackets(s_chatpacket);
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}

	private void NoTaxSellItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(L1ItemId.ADENA, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 아데나를 소비 할 수 없습니다.");
		}
		L1ItemInstance item = null;
		Random random = new Random(System.nanoTime());
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			item = ItemTable.getInstance().createItem(itemId);
			if (getSellingItems().contains(item)) {
				return;
			}
			item.setCount(amount);
			item.setIdentified(true);
			if (_npcId == 70068 || _npcId == 70020 || _npcId == 70056) {
				item.setIdentified(false);
				int chance = random.nextInt(150) + 1;
				if (chance <= 15) {
					item.setEnchantLevel(-2);
				} else if (chance >= 16 && chance <= 30) {
					item.setEnchantLevel(-1);
				} else if (chance >= 31 && chance <= 89) {
					item.setEnchantLevel(0);
				} else if (chance >= 90 && chance <= 141) {
					item.setEnchantLevel(random.nextInt(2)+1);
				} else if (chance >= 142 && chance <= 147) {
					item.setEnchantLevel(random.nextInt(3)+3);
				} else if (chance >= 148 && chance <= 149) {
					item.setEnchantLevel(6);
				} else if (chance == 150) {
					item.setEnchantLevel(7);
				}
			}
			// 배당을 측정하기 위한 추가 부분
			if (_npcId == 4200105) {
				int type = item.getItem().getType2();
				if (type == 1) {
					item.setEnchantLevel(6);
				} else if (type == 2) {
					item.setEnchantLevel(4);
				}
			}
			if (getNpcId() == 70035 || getNpcId() == 70041 || getNpcId() == 70042) { // 세금삭제후 이름 표기안되던문제fix
				int[] ticket = L1BugBearRace.getInstance().getTicketInfo(order.getOrderNumber());
				item.setSecondId(ticket[0]);
				item.setRoundId(ticket[1]); // 이
				// 빨간색
				// 전부
				// 끼워넣어줍니다.
				item.setTicketId(ticket[2]);
				L1BugBearRace.getInstance().addBetting(order.getOrderNumber(),amount);
			}
			L1PcInstance pc = inv.getOwner();
			// int weaponType = 0;
			// L1ItemInstance weapon = pc.getWeapon();
			// weaponType = weapon.getItem().getType1();
			if (itemId == 400247) { // 근거리케릭
				pc.getInventory().removeItem(400247, 1);
				int[] allBuffSkill = { PHYSICAL_ENCHANT_STR,
						PHYSICAL_ENCHANT_DEX, BLESS_WEAPON, ADVANCE_SPIRIT,
						EARTH_SKIN, FIRE_WEAPON };// 힘.덱.칼업.어벤.어스.파폰
				L1SkillUse l1skilluse = null;
				l1skilluse = new L1SkillUse();
				for (int i = 0; i < allBuffSkill.length; i++) {
					l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(),
							pc.getX(), pc.getY(), null, 0,
							L1SkillUse.TYPE_GMBUFF);
				}
			} else if (itemId == 400248) { // 원거리케릭
				pc.getInventory().removeItem(400248, 1);
				int[] allBuffSkill = { PHYSICAL_ENCHANT_STR,
						PHYSICAL_ENCHANT_DEX, BLESS_WEAPON, ADVANCE_SPIRIT,
						EARTH_SKIN, WIND_SHOT };// 힘.덱.칼업.어벤.어스.윈드샷
				L1SkillUse l1skilluse = null;
				l1skilluse = new L1SkillUse();
				for (int i = 0; i < allBuffSkill.length; i++) {
					l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(),
							pc.getX(), pc.getY(), null, 0,
							L1SkillUse.TYPE_GMBUFF);
				}
			} else
				inv.storeItem(item);
		}
	}
	private boolean NoTaxEnsureSell2(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		if (!IntRange.includes(41159, 0, 2000000000)) {
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}

		if (!pc.getInventory().checkItem(41159, price)) {			
			pc.sendPackets(new S_SystemMessage("깃털이 부족합니다."));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		try{
			pc.save();
			pc.saveInventory();
			}catch (Exception e) {
			  }
		return true;
	}
	private void NoTaxSellItems2(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(41159, orderList.getTotalPrice())){
			throw new IllegalStateException("구입에 필요한 깃털을 소비 할 수 없습니다.");
		}
		L1ItemInstance item  = null;
		Random random = new Random();
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			item = ItemTable.getInstance().createItem(itemId);
			if (getSellingItems().contains(item)) {
				return;
			}
			item.setCount(amount);
			item.setIdentified(true);
			
			if (_npcId == 880587) {
				item.setIdentified(false);
				int chance = random.nextInt(150) + 1;
				if (chance <= 15) {
					item.setEnchantLevel(0);
				} else if (chance >= 16 && chance <= 30) {
					item.setEnchantLevel(1);
				} else if (chance >= 31 && chance <= 89) {
					item.setEnchantLevel(2);
				} else if (chance >= 90 && chance <= 141) {
					item.setEnchantLevel(3);
				} else if (chance >= 142 && chance <= 147) {
					item.setEnchantLevel(4);
				} else if (chance >= 148 && chance <= 149) {
					item.setEnchantLevel(5);
				} else if (chance == 150) {
					item.setEnchantLevel(6);
				}
			}
			
			if (_npcId == 880595) {
				item.setIdentified(false);
				int chance = random.nextInt(150) + 1;
				if (chance <= 15) {
					item.setEnchantLevel(1);
				} else if (chance >= 16 && chance <= 30) {
					item.setEnchantLevel(1);
				} else if (chance >= 31 && chance <= 89) {
					item.setEnchantLevel(1);
				} else if (chance >= 90 && chance <= 141) {
					item.setEnchantLevel(1);
				} else if (chance >= 142 && chance <= 147) {
					item.setEnchantLevel(2);
				} else if (chance >= 148 && chance <= 149) {
					item.setEnchantLevel(2);
				} else if (chance == 150) {
					item.setEnchantLevel(3);
				}
			}
			
			inv.storeItem(item);
		}
	}

	private void AGSellItems(L1PcInventory inv, L1ShopBuyOrderList orderList) { //고대 금화
		if (!inv.consumeItem(49026, orderList.getTotalPrice())){
			throw new IllegalStateException("구입에 필요한 고대금화를 소비 할 수 없습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			item = ItemTable.getInstance().createItem(itemId);
			if (getSellingItems().contains(item)) {
				return;
			}
			item.setCount(amount);
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}

	private boolean AGEnsureSell(L1PcInstance pc, L1ShopBuyOrderList orderList) { //고대 금화
		int price = orderList.getTotalPrice();
		if (!IntRange.includes(price, 0, 2000000000)) {
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}
		if (!pc.getInventory().checkItem(49026, price)) {			
			pc.sendPackets(new S_SystemMessage("고대 금화가 부족합니다."));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}

	private void sellMarkItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(L1ItemId.TEST_MARK, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 징표를 소비할 수 없습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(amount);
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}
	private void sellCashItems1(L1PcInstance pc, L1PcInventory inv, L1ShopBuyOrderList orderList, int npcId) {
		if (!inv.consumeItem(npcId - 3500000, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 1차 코인을 소비할 수 없었습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			int enchant = order.getItem().getEnchant();
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(amount);
			item.setIdentified(true);
			item.setEnchantLevel(enchant);
			item.setPackage(true);
			inv.storeItem(item);			
		}
	}	
	private void sellCashItems2(L1PcInstance pc, L1PcInventory inv, L1ShopBuyOrderList orderList, int npcId) {
		if (!inv.consumeItem(npcId - 3500000, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 2차 코인을 소비할 수 없었습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			int enchant = order.getItem().getEnchant();
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(amount);
			item.setIdentified(true);
			item.setEnchantLevel(enchant);
			item.setPackage(true);
			inv.storeItem(item);			
		}
	}
	private void sellCashItems3(L1PcInstance pc, L1PcInventory inv, L1ShopBuyOrderList orderList, int npcId) {
		if (!inv.consumeItem(npcId - 3500000, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 3차 코인을 소비할 수 없었습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			int enchant = order.getItem().getEnchant();
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(amount);
			item.setIdentified(true);
			item.setEnchantLevel(enchant);
			item.setPackage(true);
			inv.storeItem(item);			
		}
	}
	private boolean ensureMarkSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {

		int price = orderList.getTotalPrice();

		if (!pc.getInventory().checkItem(L1ItemId.TEST_MARK, price)) {
			pc.sendPackets(new S_SystemMessage("징표가 부족합니다."));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}
	private boolean ensureCashSell1(L1PcInstance pc, L1ShopBuyOrderList orderList, int npcId) {
		int price = orderList.getTotalPrice();
		//9000001 - 3500000
		if (!pc.getInventory().checkItem(npcId - 3500000, price)) {
			pc.sendPackets(new S_SystemMessage("1차 코인이 부족합니다."));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			// 아이템이 너무 무거워, 더 이상 가질 수 없습니다.
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		// 개수 체크
		int totalCount = pc.getInventory().getSize();
		for (L1ShopBuyOrder order : orderList.getList()) {
			L1Item temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			// \f1한사람의 캐릭터가 가지고 걸을 수 있는 아이템은 최대 180개까지입니다.
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}
	
	private boolean ensureCashSell2(L1PcInstance pc, L1ShopBuyOrderList orderList, int npcId) {
		int price = orderList.getTotalPrice();

		if (!pc.getInventory().checkItem(npcId - 3500000, price)) {
			pc.sendPackets(new S_SystemMessage("2차 코인이 부족합니다."));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			// 아이템이 너무 무거워, 더 이상 가질 수 없습니다.
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		// 개수 체크
		int totalCount = pc.getInventory().getSize();
		for (L1ShopBuyOrder order : orderList.getList()) {
			L1Item temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			// \f1한사람의 캐릭터가 가지고 걸을 수 있는 아이템은 최대 180개까지입니다.
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}
	
	private boolean ensureCashSell3(L1PcInstance pc, L1ShopBuyOrderList orderList, int npcId) {
		int price = orderList.getTotalPrice();

		if (!pc.getInventory().checkItem(npcId - 3500000, price)) {
			pc.sendPackets(new S_SystemMessage("3차 코인이 부족합니다."));
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			// 아이템이 너무 무거워, 더 이상 가질 수 없습니다.
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		// 개수 체크
		int totalCount = pc.getInventory().getSize();
		for (L1ShopBuyOrder order : orderList.getList()) {
			L1Item temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			// \f1한사람의 캐릭터가 가지고 걸을 수 있는 아이템은 최대 180개까지입니다.
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}
		private void sellPremiumItems(L1PcInventory inv,
			L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(41159, orderList.getTotalPrice())) {
			throw new IllegalStateException("구입에 필요한 신비한 깃털을 소비할 수 없었습니다.");
		}
		L1ItemInstance item = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(amount);
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}
			private boolean ensurePremiumSell(L1PcInstance pc,
			L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		if (!IntRange.includes(price, 0, 60000)) {
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"픽시의 깃털은 한번에 60000개 이상 사용할수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
			pc.sendPackets(s_chatpacket);
			return false;
		}
		if (!pc.getInventory().checkItem(41159, price)) {
			//pc.sendPackets(new S_SystemMessage("픽시의 깃털이 부족합니다."));
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"픽시의 깃털이 부족합니다.", Opcodes.S_OPCODE_MSG, 20); 
			pc.sendPackets(s_chatpacket);
			return false;
		}
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"소지품이 너무 무거워서 더 들 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
			pc.sendPackets(s_chatpacket);
			return false;
		}
		int totalCount = pc.getInventory().getSize();
		L1Item temp = null;
		for (L1ShopBuyOrder order : orderList.getList()) {
			temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
			return false;
		}
		return true;
	}

	private void payTax(L1ShopBuyOrderList orderList) {
		payCastleTax(orderList);
		payTownTax(orderList);
		payDiadTax(orderList);
	}

	private void payCastleTax(L1ShopBuyOrderList orderList) {
		L1TaxCalculator calc = orderList.getTaxCalculator();
		int price = orderList.getTotalPrice();
		int castleId = L1CastleLocation.getCastleIdByNpcid(_npcId);
		int castleTax = calc.calcCastleTaxPrice(price);
		int nationalTax = calc.calcNationalTaxPrice(price);

		if (castleId == L1CastleLocation.ADEN_CASTLE_ID
				|| castleId == L1CastleLocation.DIAD_CASTLE_ID) {
			castleTax += nationalTax;
			nationalTax = 0;
		}

		if (castleId != 0 && castleTax > 0) {
			L1Castle castle = CastleTable.getInstance()
					.getCastleTable(castleId);
			synchronized (castle) {
				int money = castle.getPublicReadyMoney();
				if (2000000000 > money) {
					money += castleTax;
					castle.setPublicReadyMoney(money);
					CastleTable.getInstance().updateCastle(castle);
				}
			}
			if (nationalTax > 0) {
				L1Castle aden = CastleTable.getInstance().getCastleTable(
						L1CastleLocation.ADEN_CASTLE_ID);
				synchronized (aden) {
					int money = aden.getPublicReadyMoney();
					if (2000000000 > money) {
						money += nationalTax;
						aden.setPublicReadyMoney(money);
						CastleTable.getInstance().updateCastle(aden);
					}
				}
			}
		}
	}

	private void payDiadTax(L1ShopBuyOrderList orderList) {
		L1Castle castle = CastleTable.getInstance().getCastleTable(
				L1CastleLocation.DIAD_CASTLE_ID);
		L1TaxCalculator calc = orderList.getTaxCalculator();
		int price = orderList.getTotalPrice();
		int diadTax = calc.calcDiadTaxPrice(price);

		if (diadTax <= 0) {
			return;
		}
		synchronized (castle) {
			int money = castle.getPublicReadyMoney();
			if (2000000000 > money) {
				money = money + diadTax;
				castle.setPublicReadyMoney(money);
				CastleTable.getInstance().updateCastle(castle);
			}
		}
	}

	private void payTownTax(L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		if (!L1World.getInstance().isProcessingContributionTotal()) {
			int town_id = L1TownLocation.getTownIdByNpcid(_npcId);
			if (town_id >= 1 && town_id <= 10) {
				TownTable.getInstance().addSalesMoney(town_id, price);
			}
		}
	}

	public void buyItems(L1ShopSellOrderList orderList) {
		L1PcInventory inv = orderList.getPc().getInventory();
		int totalPrice = 0;
		L1Object object = null;
		L1ItemInstance item = null;
		for (L1ShopSellOrder order : orderList.getList()) {
			object = inv.getItem(order.getItem().getTargetId());
			item = (L1ItemInstance) object;
			if (item.getItem().getBless() < 128) {
				int count = inv.removeItem(order.getItem().getTargetId(), order.getCount());
				totalPrice += order.getItem().getAssessedPrice() * count;
			}
		}
		totalPrice = IntRange.ensure(totalPrice, 0, 2000000000);
		if (0 < totalPrice) {
			if (getNpcId() >= 9000001 && getNpcId() <= 9000036) {				
				inv.storeItem(getNpcId() - 3500000, totalPrice);
			       } else if (_npcId == 778818){ // 미엘, 모듬양념소스 교환 NPC번호는 자신에게 알맞은 번호로 한다.
			   		if (0 < totalPrice) {
					inv.storeItem(49048, totalPrice);
					}
					} else if (_npcId == 778817){ // 시올, 모듬양념소스 교환 NPC번호는 자신에게 알맞은 번호로 한다.
					if (0 < totalPrice) {
					inv.storeItem(436000, totalPrice);
					}	
					} else if (_npcId == 95021){ // 방어구매입
						if (0 < totalPrice) {
						inv.storeItem(41159, totalPrice);
						}
	
			} else {					
				inv.storeItem(L1ItemId.ADENA, totalPrice);	
			}
		}
	}
	
	public void buyItemsToNpcShop(L1ShopSellOrderList orderList, int npcId) {
		L1PcInventory inv = orderList.getPc().getInventory();
		int totalPrice = 0;
		L1Object object = null;
		L1ItemInstance item = null;
		for (L1ShopSellOrder order : orderList.getList()) {
			object = inv.getItem(order.getItem().getTargetId());
			item = (L1ItemInstance) object;
			if (item.getItem().getBless() < 128) {
				int count = inv.removeItem(order.getItem().getTargetId(), order.getCount());
				totalPrice += order.getItem().getAssessedPrice() * order.getDividend() * count;
			}
		}
		totalPrice = IntRange.ensure(totalPrice, 0, 2000000000);
		if (0 < totalPrice) {
			inv.storeItem(npcId - 3500000, totalPrice);
		}
	}
	private L1ShopItem getPurchasingItem(int itemId) {
		for (L1ShopItem shopItem : _purchasingItems) {
			if (shopItem.getItemId() == itemId) {
				return shopItem;
			}
		}
		return null;
	}

	private boolean isPurchaseableItem(L1ItemInstance item) {
		/*
		if (item == null || item.isEquipped() || item.getEnchantLevel() != 0 || item.getBless() >= 128) {
			return false;
		}
		*/
		
		if (item == null || item.isEquipped() || item.getBless() >= 128) {
			return false;
		}
		return true;
	}

	public L1AssessedItem assessItem(L1ItemInstance item) {
		L1ShopItem shopItem = getPurchasingItem(item.getItemId());
		if (shopItem == null) {
			return null;
		}
		if (item.getItemId() == 40309) { // /SELL눌렀을때 배당율 계산해서 리스트에

			float dividend = L1BugBearRace.getInstance().getTicketPrice(item);
			return new L1AssessedItem(item.getId(),
					(int) (getAssessedPrice(shopItem) * dividend));
		} else
			return new L1AssessedItem(item.getId(), getAssessedPrice(shopItem));
	}

	private int getAssessedPrice(L1ShopItem item) {
		return (int) (item.getPrice() * Config.RATE_SHOP_PURCHASING_PRICE / item
				.getPackCount());
	}

	public List<L1AssessedItem> assessItems(L1PcInventory inv) {
		List<L1AssessedItem> result = new FastTable<L1AssessedItem>();
		for (L1ShopItem item : _purchasingItems) {
			for (L1ItemInstance targetItem : inv.findItemsId(item.getItemId())) {
				if (!isPurchaseableItem(targetItem)) {
					continue;
				}
				if (item.getEnchant() == targetItem.getEnchantLevel()) { // 인챈트가 같은 아이템만
					result.add(new L1AssessedItem(targetItem.getId(), getAssessedPrice(item)));
				}
			}
		}
		return result;
	}

	public List<L1AssessedItem> assessTickets(L1PcInventory inv) {
		List<L1AssessedItem> result = new FastTable<L1AssessedItem>();
		for (L1ShopItem item : _purchasingItems) {
			for (L1ItemInstance targetItem : inv.findItemsId(item.getItemId())) {
				if (!isPurchaseableItem(targetItem)) {
					continue;
				}
				float dividend = L1BugBearRace.getInstance().getTicketPrice(
						targetItem);

				result.add(new L1AssessedItem(targetItem.getId(),
						(int) (getAssessedPrice(item) * dividend)));
			}
		}
		return result;
	}

	public int getNpcId() {
		return _npcId;
	}

	public List<L1ShopItem> getSellingItems() {
		return _sellingItems;
	}
	public List<L1ShopItem> getBuyingItems() {
		return _purchasingItems;
	}
	public L1ShopBuyOrderList newBuyOrderList() {
		return new L1ShopBuyOrderList(this);
	}

	public L1ShopSellOrderList newSellOrderList(L1PcInstance pc) {
		return new L1ShopSellOrderList(this, pc);
	}

}