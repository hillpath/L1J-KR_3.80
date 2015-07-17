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
package l1j.server.server.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import javolution.util.FastTable;
import l1j.server.L1DatabaseFactory;
import l1j.server.Warehouse.ClanWarehouse;
import l1j.server.Warehouse.ElfWarehouse;
import l1j.server.Warehouse.PackageWarehouse;
import l1j.server.Warehouse.PrivateWarehouse;
import l1j.server.Warehouse.SpecialWarehouse;
import l1j.server.Warehouse.Warehouse;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.BugKick;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ClanWarehouseList;
import l1j.server.server.datatables.NpcCashShopTable;
import l1j.server.server.datatables.NpcShopTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1BugBearRace;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.model.shop.L1ShopBuyOrderList;
import l1j.server.server.model.shop.L1ShopSellOrderList;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.utils.SQLUtil;
import server.CodeLogger;
import server.LineageClient;
import server.manager.eva;
import server.system.autoshop.AutoShopManager;

public class C_ShopAndWarehouse extends ClientBasePacket {
	private final int TYPE_BUY_SHP = 0; // 상점 or 개인 상점 사기

	private final int TYPE_SEL_SHP = 1; // 상점 or 개인 상점 팔기

	private final int TYPE_PUT_PWH = 2; // 개인 창고 맡기기

	private final int TYPE_GET_PWH = 3; // 개인 창고 찾기

	private final int TYPE_PUT_CWH = 4; // 혈맹 창고 맡기기

	private final int TYPE_GET_CWH = 5; // 혈맹 창고 찾기

	private final int TYPE_PUT_EWH = 8; // 엘프 창고 맡기기

	private final int TYPE_GET_EWH = 9; // 엘프 창고 찾기

	private final int TYPE_GET_MWH = 10; // 패키지 창고 찾기
	
	private final int TYPE_PUT_SWH = 17; // 특수창고 맡기기
	
	private final int TYPE_GET_SWH = 19; // 특수창고 찾기

	public C_ShopAndWarehouse(byte abyte0[], LineageClient clientthread)
			throws Exception {
		super(abyte0);
		int npcObjectId = readD();
		int resultType = readC();
		int size = readC();
		@SuppressWarnings("unused")
		int unknown = readC();

		if (size < 0)
			return;
		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null) {
			return;
		}
		// 아이템저장시킴
		pc.saveInventory();
		// 아이템저장시킴
		int level = pc.getLevel();
	//	if (isTwoLogin(pc))
	//		return;
		if (size < 0) {
			pc.sendPackets(new S_Disconnect());

			return;
		}
		if (size > 150) {
			System.out.println("패킷섭폭 ["+pc.getName()+"] IP "+clientthread.getIp());
			 clientthread.kick();
			 clientthread.close();
			 return;
			}
		if (pc.getOnlineStatus() == 0 || isTwoLogin(pc)) {
			clientthread.kick();
			clientthread.close();
			return;
		}

		int npcId = 0;
		String npcImpl = "";
		boolean isPrivateShop = false;
		boolean tradable = true;
		L1Object findObject = L1World.getInstance().findObject(npcObjectId);
		if (findObject == null) {
			   clientthread.kick();
			   clientthread.close();
			   return;
			  }
		if (findObject != null) { // 3셀
			int diffLocX = Math.abs(pc.getX() - findObject.getX());
			int diffLocY = Math.abs(pc.getY() - findObject.getY());
			if (diffLocX > 3 || diffLocY > 3) {
				return;
			}
			if (findObject instanceof L1NpcInstance) {
				L1NpcInstance targetNpc = (L1NpcInstance) findObject;
				npcId = targetNpc.getNpcTemplate().get_npcId();
				npcImpl = targetNpc.getNpcTemplate().getImpl();
			} else if (findObject instanceof L1PcInstance) {
				isPrivateShop = true;
			}
		}
		// //중복 접속 버그방지
		if (pc.getOnlineStatus() == 0) {
			clientthread.kick();
			return;
		}
		// //중복 접속 버그방지

		/** 상점, 창고 사용시 이펙트 * */
/*		if (findObject != null) {
			NpcEffect(pc, findObject, npcId, resultType);
		}*/

		switch (resultType) {
		case TYPE_BUY_SHP: // 상점 or 개인 상점 사기
			if (npcId == 70035 || npcId == 70041 || npcId == 70042) {
				int status = L1BugBearRace.getInstance().getBugRaceStatus();
				boolean chk = L1BugBearRace.getInstance().buyTickets;
				if (status != L1BugBearRace.STATUS_READY || chk == false) {
					return;
				}
			}
			if (size != 0 && npcImpl.equalsIgnoreCase("L1NpcCashShop")){
				buyItemFromNpcCashShop(pc, npcId, size);
				break;
			}
			if (size != 0 && npcImpl.equalsIgnoreCase("L1Merchant")) {
				buyItemFromShop(pc, npcId, size);
				break;
			}
			if (size != 0 && npcImpl.equalsIgnoreCase("L1NpcShop")) {
				buyItemFromNpcShop(pc, npcId, size);
				break;
			}
			
			if (size != 0 && isPrivateShop) {
				buyItemFromPrivateShop(pc, findObject, size);
				break;
			}
		case TYPE_SEL_SHP: // 상점 or 개인 상점 팔기

	      	if(size != 0 && npcImpl.equalsIgnoreCase("L1NpcCashShop")){
	      		sellItemToNpcShop(pc, npcId, size);
				break;
			}
			if (size != 0 && npcImpl.equalsIgnoreCase("L1Merchant"))
				sellItemToShop(pc, npcId, size);
			if (size != 0 && isPrivateShop)
				sellItemToPrivateShop(pc, findObject, size);
			break;
		case TYPE_PUT_PWH: // 개인 창고 맡기기
			if (size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) {// 자신의
																					// 창고에
																					// 격납
				putItemToPrivateWarehouse(pc, size);
				break;
			}
		case TYPE_GET_PWH: // 개인 창고 찾기
			if (size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) {// 자신의
																					// 창고
																					// 찾기
				getItemToPrivateWarehouse(pc, size);
				break;
			}
		case TYPE_PUT_CWH: // 혈맹 창고 맡기기
			if (npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) {

				putItemToClanWarehouse(pc, size);
				break;
			}

		case TYPE_GET_CWH: // 혈맹 창고 찾기
			if (npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) {

				getItemToClanWarehouse(pc, size);
				break;
			}
		case TYPE_PUT_EWH: // 엘프 창고 맡기기
			if (size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 99) {
				pc.sendPackets(new S_SystemMessage("요정 창고는 사용 불가능 합니다."));
				putItemToElfWarehouse(pc, size);
				break;
			}
		case TYPE_GET_EWH: // 엘프 창고 찾기
			if (size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 99) {
				pc.sendPackets(new S_SystemMessage("요정 창고는 사용 불가능 합니다."));
				getItemToElfWarehouse(pc, size);
				break;
			}
		case TYPE_GET_MWH: // 패키지 창고 찾기
			if (size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf"))
				getItemToPackageWarehouse(pc, size);
			break;
		case TYPE_PUT_SWH: // 특수 창고 맡기기 
			if (size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf")
					&& pc.get_SpecialSize() == 0) {
				pc.sendPackets(new S_ServerMessage(1238));
				break;
			}
			if (size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf")) {
				putItemToSpecialWarehouse(pc, size);
				break;
			}
		case TYPE_GET_SWH: // 특수 창고 찾기 
			if (size != 0 && npcImpl.equalsIgnoreCase("L1Dwarf")) {
				getItemToSpecialWarehouse(pc, size);
				break;
			}
		default:
		}
	}

	private void doNothingClanWarehouse(L1PcInstance pc) {
		if (pc == null)
			return;

		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan == null)
			return;

		ClanWarehouse clanWarehouse = WarehouseManager.getInstance()
				.getClanWarehouse(clan.getClanName());
		if (clanWarehouse == null)
			return;

		clanWarehouse.unlock(pc.getId());
	}

	private void getItemToPackageWarehouse(L1PcInstance pc, int size) {
		int objectId, count;
		L1ItemInstance item = null;
		PackageWarehouse w = WarehouseManager.getInstance().getPackageWarehouse(pc.getAccountName());
		if (w == null)
			return;

		for (int i = 0; i < size; i++) {
			objectId = readD();
			count = readD();
			item = w.getItem(objectId);

			if (!isAvailableTrade(pc, objectId, item, count))
				break;
			if (count > item.getCount())
				count = item.getCount();
			if (!isAvailablePcWeight(pc, item, count))
				break;
			/*버그방지*/
			int itemType = item.getItem().getType2();
			if(count <= 0){
				pc.sendPackets(new S_Disconnect());
				return;
			}
			if (objectId != item.getId()) {
				pc.sendPackets(new S_Disconnect());
				return;
			}
			if (!item.isStackable() && count != 1) {
				pc.sendPackets(new S_Disconnect());
				return;
			}
			if (item == null || item.getCount() < count) {
		    	 pc.sendPackets(new S_Disconnect());
		         return;
		    }
		    if ((itemType == 1 && item.getCount() != 1) ||	(itemType == 2 && item.getCount() != 1)){
				pc.sendPackets(new S_Disconnect());
				return;
			}
		    if (count <= 0 || count < 1 || item.getCount() <= 0) {
		    	pc.sendPackets(new S_Disconnect());
		        return;
		    }			
		    if (!pc.isGm() && item.getItem().getItemId() == 40308)  {
	        	if (count > 10000000) {
	        		 pc.sendPackets(new S_SystemMessage("아데나는 1000만 단위씩 창고이용이 가능합니다."));
	        		 return;
	        	}
		    }
		    if(item.getItem().getItemId() == 40445 || item.getItem().getItemId() == 41251 ||
		    		item.getItem().getItemId() == 41254){
		    	if (count > 1) {
	        		 pc.sendPackets(new S_SystemMessage("해당 아이템은 1개씩 창고이용이 가능합니다."));
	        		 return;
	        	}
		    }
			if(item.getItem().getItemId() >= 76767 && item.getItem().getItemId() <= 76784 ){
				pc.sendPackets(new S_SystemMessage("봉인이풀린 룬,유물은 창고이용이 불가능 합니다."));
			return;
			}
	        if (item.getItem().getItemId() == 41159 || item.getItem().getItemId() == 41246){ 
				  pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
				  return;
			}
			if (item.getAcByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getDmgByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getHolyDmgByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getHitByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(6524)) { 
				pc.sendPackets(new S_SystemMessage("\\fY리스후 30초간은 창고 이용을 하실수 없습니다."));
				return;
		    }
	        if (item.getCount() > 2000000000) {
			    return;
			}
		    if (count > 2000000000) {
			    return;
			}
			 //** 중계기 노딜버그 막아 보자 **//
			  long nowtime = System.currentTimeMillis();
			  if(item.getItemdelay3() >=  nowtime ){
				  break;
			  }  
			  //** 중계기 노딜버그 막아 보자 **//
				 //신선한우유 수량성버그방지
			   if (!item.getItem().isToBeSavedAtOnce()) {
			   pc.getInventory().saveItem(item, L1PcInventory.COL_COUNT);
			   }	 
		      //신선한우유 수량성버그방지
			w.tradeItem(item, count, pc.getInventory());
			eva.LogWareHouseAppend("패키지:", pc.getName(), "", item, count, objectId);
			CodeLogger.getInstance().warehouselog("찾	패키지", pc.getName()+"["+pc.getAccountName()+"]",item,count);

		}
	}

	private void getItemToElfWarehouse(L1PcInstance pc, int size) {
		if (pc.getLevel() < 5 || !pc.isElf())
			return;

		L1ItemInstance item;
		ElfWarehouse elfwarehouse = WarehouseManager.getInstance()
				.getElfWarehouse(pc.getAccountName());
		if (elfwarehouse == null)
			return;

		for (int i = 0, objectId, count; i < size; i++) {
			objectId = readD();
			count = readD();
			item = elfwarehouse.getItem(objectId);

			/* 버그방지 */
			if (objectId != item.getId()) {
				pc.sendPackets(new S_Disconnect());
				break;
			}
			if (!item.isStackable() && count != 1) {
				pc.sendPackets(new S_Disconnect());
				break;
			}
			/* 버그방지 */

			if (item == null || item.getCount() < count || count <= 0
					|| item.getCount() <= 0) {
				BugKick.getInstance().KickPlayer(pc);
				break;
			}
			if (item.getCount() > 10000000) {
				break;
			}
			if (count > 10000000) {
				break;
			}
			/* 버그방지 */
			if (item.getEnchantLevel() >= 1) { // 1이상 인첸된 아이템은
																// 드롭불가
				pc.sendPackets(new S_SystemMessage("인첸된 아이템은 이용을 할 수 없습니다."));
				return;
			}
			if (item.getItem().getItemId() == 40308) {
				if (count > 10000000) {
					pc.sendPackets(new S_SystemMessage("아데나 (10,000,000) 이하만 창고 이용이 가능합니다."));
					return;
				}
			}
			if (item.getItem().getItemId() == 41159 
					|| item.getItem().getItemId() == 520000
					|| item.getItem().getItemId() == 520001
					|| item.getItem().getItemId() == 520002
					|| item.getItem().getItemId() == 41246) { // 코인(438005) 창고 맡기지 못하게 by 개념탑재 2012.02.06 
				pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
				return;
			}
			
			/** 마법 중첩효과 버그 막기 소스 by 개념탑재 2012.02.23 */
			if (item.getAcByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getDmgByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getHolyDmgByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getHitByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(6524)) { 
				pc.sendPackets(new S_SystemMessage("\\fY리스후 30초간은 창고 이용을 하실수 없습니다."));
				return;
		    }
			if (item.getCount() > 2000000000) {
				return;
			}
			if (count > 2000000000) {
				return;
			}
			if (!isAvailableTrade(pc, objectId, item, count))
				break;
			if (count > item.getCount())
				count = item.getCount();
			if (!isAvailablePcWeight(pc, item, count))
				break;

			if (pc.getInventory().consumeItem(40494, 2)) {
				elfwarehouse.tradeItem(item, count, pc.getInventory());
				eva.LogWareHouseAppend("요정:찾", pc.getName(), "", item, count, objectId);
			} else {
				pc.sendPackets(new S_ServerMessage(337, "$767"));
				break;
			}
		}
	}

	private void putItemToElfWarehouse(L1PcInstance pc, int size) {
		if (pc.getLevel() < 5 || !pc.isElf())
			return;

		L1Object object = null;
		L1ItemInstance item = null;
		ElfWarehouse elfwarehouse = WarehouseManager.getInstance()
				.getElfWarehouse(pc.getAccountName());
		if (elfwarehouse == null)
			return;

		for (int i = 0, objectId, count; i < size; i++) {
			objectId = readD();
			count = readD();

			object = pc.getInventory().getItem(objectId);
			item = (L1ItemInstance) object;
			if (item.getItem().getItemId() == 76778
					|| item.getItem().getItemId() == 555563
					|| item.getItem().getItemId() == 76779
					|| item.getItem().getItemId() == 76780
					|| item.getItem().getItemId() == 76781
					|| item.getItem().getItemId() == 76782
					|| item.getItem().getItemId() == 76783
					|| item.getItem().getItemId() == 76784
					|| item.getItem().getItemId() == 76773
					|| item.getItem().getItemId() == 76772
					|| item.getItem().getItemId() == 76771
					|| item.getItem().getItemId() == 76770
					|| item.getItem().getItemId() == 76769
					|| item.getItem().getItemId() == 76768
					|| item.getItem().getItemId() == 76767
					|| item.getItem().getItemId() == 76777
					|| item.getItem().getItemId() == 76776
					|| item.getItem().getItemId() == 76775
					|| item.getItem().getItemId() == 76774
							|| item.getItem().getItemId() == 767740
					||item.getItem().getItemId() == 41159
					|| item.getItem().getItemId() == 41246
					|| item.getItem().getItemId() == 40308
					|| item.getItem().getItemId() == 720
					|| item.getItem().getItemId() == 721
					|| item.getItem().getItemId() == 722
					
					) {
				pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
				break;
			}
			if (item.getItem().getItemId() == 40074
					|| item.getItem().getItemId() == 40087
					|| item.getItem().getItemId() == 140074
					|| item.getItem().getItemId() == 140087
					|| item.getItem().getItemId() == 240074
					|| item.getItem().getItemId() == 240087) {
				pc.sendPackets(new S_SystemMessage(
						"아이템 강화주문서는 창고 이용을 할 수 없습니다."));
				return;
			}
			
			if (item.getItem().getItemId() == 40308) {
				if (count > 10000000) {
					pc.sendPackets(new S_SystemMessage("아데나 (10,000,000) 이하만 창고 이용이 가능합니다."));
					return;
				}
			}
			if (item.getAcByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getDmgByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getHolyDmgByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getHitByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			
			if(item.getItem().getItemId() >= 76767 && item.getItem().getItemId() <= 76784 ){
				pc.sendPackets(new S_SystemMessage("봉인이풀린 룬,유물은 창고이용이 불가능 합니다."));
			return;
			}
			if (!isAvailableTrade(pc, objectId, item, count))
				break;
			if (count > item.getCount())
				count = item.getCount();
			/* 버그방지 */
			if (objectId != item.getId()) {
				pc.sendPackets(new S_Disconnect());
				break;
			}
			if (!item.isStackable() && count != 1) {
				pc.sendPackets(new S_Disconnect());
				break;
			}
			/* 버그방지 */

			if (item == null || item.getCount() < count || count <= 0
					|| item.getCount() <= 0) {
				break;
			}
			if (item.getCount() > 10000000) {
				break;
			}
			if (count > 10000000) {
				break;
			}

			if (!item.getItem().isTradable()) {
				pc.sendPackets(new S_ServerMessage(210, item.getItem()
						.getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
				break;
			}
			/* 버그방지 */
			if (item.getEnchantLevel() >= 1) { // 1이상 인첸된 아이템은
																// 드롭불가
				pc.sendPackets(new S_SystemMessage("인첸된 아이템은 이용을 할 수 없습니다."));
				return;
			}
			if (!checkPetList(pc, item))
				break;
			if (!isAvailableWhCount(elfwarehouse, pc, item, count))
				break;

			pc.getInventory().tradeItem(objectId, count, elfwarehouse);
			pc.getLight().turnOnOffLight();
			eva.LogWareHouseAppend("요정:맡", pc.getName(), "", item, count, objectId);
		}
	}

	private void getItemToClanWarehouse(L1PcInstance pc, int size) {
		if (pc.getLevel() < 5)
			return;

		if (size == 0) {
			doNothingClanWarehouse(pc);
			return;
		}

		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());

		if (!isAvailableClan(pc, clan))
			return;

		L1ItemInstance item;
		ClanWarehouse clanWarehouse = WarehouseManager.getInstance()
				.getClanWarehouse(clan.getClanName());
		if (clanWarehouse == null)
			return;
		for (int i = 0, objectId, count; i < size; i++) {
			objectId = readD();
			count = readD();
			item = clanWarehouse.getItem(objectId);

			if (!isAvailableTrade(pc, objectId, item, count))
				break;
			if (!hasAdena(pc))
				break;
			if (count >= item.getCount())
				count = item.getCount();
			if (!isAvailablePcWeight(pc, item, count))
				break;
			/* 버그방지 */
			  // int itemType = item.getItem().getType2();
			   if (count <= 0) {
			    pc.sendPackets(new S_Disconnect());
			    return;
			   }
			if (objectId != item.getId()) {
				pc.sendPackets(new S_Disconnect());
				break;
			}
			if (!item.isStackable() && count != 1) {
				pc.sendPackets(new S_Disconnect());
				break;
			}
			if (count > item.getCount()) {
				count = item.getCount();
			}
			/* 버그방지 */
			if (item == null || item.getCount() < count) {
				pc.sendPackets(new S_Disconnect());
				break;
			}
			if (count <= 0 || count < 1 || item.getCount() <= 0) {
				BugKick.getInstance().KickPlayer(pc);
				break;
			}
			if(item.getBless() >= 128){ 
				pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
				return;
			}
			if (item.getItem().getItemId() == 40308) {
				if (count > 10000000) {
					pc.sendPackets(new S_SystemMessage("아데나 (10,000,000) 이하만 창고 이용이 가능합니다."));
					return;
				}
			}
			if (item.getItem().getItemId() == 41159 
					|| item.getItem().getItemId() == 520000
					|| item.getItem().getItemId() == 520001
					|| item.getItem().getItemId() == 520002
					|| item.getItem().getItemId() == 41246) { // 코인(438005) 창고 맡기지 못하게 by 개념탑재 2012.02.06 
				pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
				return;
			}
			if (item.getAcByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getDmgByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getHolyDmgByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getHitByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
						if (pc.getSkillEffectTimerSet().hasSkillEffect(6524)) { 
				pc.sendPackets(new S_SystemMessage("리스후 30초간 창고 이용을 하실수 없습니다."));
				return;
		    }
			/* 버그방지 */
			if (item.getEnchantLevel() >= 1) { // 1이상 인첸된 아이템은
																// 드롭불가
				pc.sendPackets(new S_SystemMessage("인첸된 아이템은 이용을 할 수 없습니다."));
				return;
			}
			 //** 중계기 노딜버그 막아 보자 **//
			  long nowtime = System.currentTimeMillis();
			  if(item.getItemdelay3() >=  nowtime ){
				  break;
			  }  
			  //** 중계기 노딜버그 막아 보자 **//
				 //신선한우유 수량성버그방지
			   if (!item.getItem().isToBeSavedAtOnce()) {
			   pc.getInventory().saveItem(item, L1PcInventory.COL_COUNT);
			   }	 
		      //신선한우유 수량성버그방지
				if (item.getCount() > 2000000000) {
					return;
				}
				if (count > 2000000000) {
					return;
				}
			clanWarehouse.tradeItem(item, count, pc.getInventory());
			ClanWarehouseList.getInstance().addList(pc.getClanid(), pc.getName()+" 이(가) 아이템을 찾았습니다.. \n"
					+"["+(item.getItem().getType2() == 1 || item.getItem().getType2() == 2 ? "+"+item.getEnchantLevel()+" "+item.getName() : item.getName())+"] x "+count+"개\n", currentTime());
			CodeLogger.getInstance().warehouselog("찾	혈맹", pc.getName()+"["+pc.getClanname()+"]",item,count);
			eva.LogWareHouseAppend("혈맹:찾", pc.getName(), pc.getClanname(), item, count, objectId);
			UpdateLog(pc.getName(), pc.getClanname(), item.getName(), count, 1);
	

		}
		clanWarehouse.unlock(pc.getId());
	}

	private void putItemToClanWarehouse(L1PcInstance pc, int size) {
		if (size == 0) {
			doNothingClanWarehouse(pc);
			return;
		}

		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());

		if (!isAvailableClan(pc, clan))
			return;

		L1Object object = null;
		L1ItemInstance item = null;
		ClanWarehouse clanWarehouse = WarehouseManager.getInstance()
				.getClanWarehouse(clan.getClanName());
		if (clanWarehouse == null)
			return;

		for (int i = 0, objectId, count; i < size; i++) {
			objectId = readD();
			count = readD();

			object = pc.getInventory().getItem(objectId);
			item = (L1ItemInstance) object;

			if (item == null)
				break;
			if (count > item.getCount())
				count = item.getCount();
			if (!isAvailableTrade(pc, objectId, item, count))
				break;
			if (item.getItem().getItemId() == 40074
					|| item.getItem().getItemId() == 40087
					|| item.getItem().getItemId() == 140074
					|| item.getItem().getItemId() == 140087
					|| item.getItem().getItemId() == 240074
					|| item.getItem().getItemId() == 240087) {
				pc.sendPackets(new S_SystemMessage(
						"아이템 강화주문서는 창고 이용을 할 수 없습니다."));
				return;
			}
			if(item.getItem().getItemId() >= 76767 && item.getItem().getItemId() <= 76784 ){
				pc.sendPackets(new S_SystemMessage("봉인이풀린 룬,유물은 창고이용이 불가능 합니다."));
			return;
			}
			if (item.getItem().getItemId() == 76778
					|| item.getItem().getItemId() == 76779
					|| item.getItem().getItemId() == 555563
					|| item.getItem().getItemId() == 76780
					|| item.getItem().getItemId() == 76781
					|| item.getItem().getItemId() == 76782
					|| item.getItem().getItemId() == 76783
					|| item.getItem().getItemId() == 76784
					|| item.getItem().getItemId() == 76773
					|| item.getItem().getItemId() == 76772
					|| item.getItem().getItemId() == 76771
					|| item.getItem().getItemId() == 76770
					|| item.getItem().getItemId() == 76769
					|| item.getItem().getItemId() == 76768
					|| item.getItem().getItemId() == 76767
					|| item.getItem().getItemId() == 76777
					|| item.getItem().getItemId() == 76776
					|| item.getItem().getItemId() == 76775
					|| item.getItem().getItemId() == 76774
							|| item.getItem().getItemId() == 767740
					||item.getItem().getItemId() == 41159
					|| item.getItem().getItemId() == 41246
					|| item.getItem().getItemId() == 40308
					|| item.getItem().getItemId() ==500144
					|| item.getItem().getItemId() ==500145
					|| item.getItem().getItemId() ==500146
					|| item.getItem().getItemId() ==490018
					|| item.getItem().getItemId() == 720
					|| item.getItem().getItemId() == 721
					|| item.getItem().getItemId() == 722
					
					) {
				pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
				return;
			}
			if (item.getItem().getItemId() == 40308) {
				if (count > 10000000) {
					pc.sendPackets(new S_SystemMessage("아데나 (10,000,000) 이하만 창고 이용이 가능합니다."));
					return;
				}
			}
			if (item.getAcByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getDmgByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getHolyDmgByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getHitByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getCount() > 2000000000) {
				return;
			}
			if (count > 2000000000) {
				return;
			}
			if (item.getItem().getItemId() == 423012
					|| item.getItem().getItemId() == 423013) { // 10주년티
				pc.sendPackets(new S_ServerMessage(210, item.getItem()
						.getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
				return;
			}
			if(item.getBless() >= 128){ 
				pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
				return;
			}
			if (item.getBless() >= 128 || !item.getItem().isTradable()) {
				// \f1%0은 버리거나 또는 타인에게 양도 할 수 없습니다.
				pc.sendPackets(new S_ServerMessage(210, item.getItem()
						.getName()));
				break;
			}

			/* 버그방지 */
			if (item.getEnchantLevel() >= 1) { // 1이상 인첸된 아이템은
																// 드롭불가
				pc.sendPackets(new S_SystemMessage("인첸된 아이템은 이용을 할 수 없습니다."));
				return;
			}
			if (!checkPetList(pc, item))
				break;
			if (!isAvailableWhCount(clanWarehouse, pc, item, count))
				break;
			 //** 중계기 노딜버그 막아 보자 **//
			  long nowtime = System.currentTimeMillis();
			  if(item.getItemdelay3() >=  nowtime ){
				  break;
			  }  
			  //** 중계기 노딜버그 막아 보자 **//
				 //신선한우유 수량성버그방지
			   if (!item.getItem().isToBeSavedAtOnce()) {
			   pc.getInventory().saveItem(item, L1PcInventory.COL_COUNT);
			   }	 
		      //신선한우유 수량성버그방지
			pc.getInventory().tradeItem(objectId, count, clanWarehouse);
			pc.getLight().turnOnOffLight();
			ClanWarehouseList.getInstance().addList(pc.getClanid(), pc.getName()+" 이(가) 아이템을 맡겼습니다. \n"
					+"["+(item.getItem().getType2() == 1 || item.getItem().getType2() == 2 ? "+"+item.getEnchantLevel()+" "+item.getName() : item.getName())+"] x "+count+"개\n", currentTime());
			CodeLogger.getInstance().warehouselog("맡	혈맹", pc.getName()+"["+pc.getClanname()+"]",item,count);
			eva.LogWareHouseAppend("혈맹:맡", pc.getName(), pc.getClanname(), item, count, objectId);
			UpdateLog(pc.getName(), pc.getClanname(), item.getName(), count, 0);

		}
		clanWarehouse.unlock(pc.getId());
	}

	private void getItemToPrivateWarehouse(L1PcInstance pc, int size) {
		L1ItemInstance item = null;
		PrivateWarehouse warehouse = WarehouseManager.getInstance()
				.getPrivateWarehouse(pc.getAccountName());
		if (warehouse == null)
			return;

		for (int i = 0, objectId, count; i < size; i++) {
			objectId = readD();
			count = readD();
			item = warehouse.getItem(objectId);

			if (!isAvailableTrade(pc, objectId, item, count))
				break;
			/* 버그방지 */
			if (objectId != item.getId()) {
				pc.sendPackets(new S_Disconnect());
				break;
			}
			if (!item.isStackable() && count != 1) {
				pc.sendPackets(new S_Disconnect());
				break;
			}
			if (count > item.getCount()) {
				count = item.getCount();
			}
			/* 버그방지 */
			if (item == null || item.getCount() < count) {
				pc.sendPackets(new S_Disconnect());
				break;
			}
			if (count <= 0 || count < 1 || item.getCount() <= 0) {
				BugKick.getInstance().KickPlayer(pc);
				break;
			}
		    if (!pc.isGm() && item.getItem().getItemId() == 40308)  {
	        	if (count > 10000000) {
	        		 pc.sendPackets(new S_SystemMessage("아데나는 1000만 단위씩 창고이용이 가능합니다."));
	        		 return;
	        	}
		    }
			/* 버그방지 */
			if (item.getItem().getItemId() == 41159
					|| item.getItem().getItemId() == 76778
					|| item.getItem().getItemId() == 76779
					|| item.getItem().getItemId() == 76780
					|| item.getItem().getItemId() == 76781
					|| item.getItem().getItemId() == 76782
					|| item.getItem().getItemId() == 76783
					|| item.getItem().getItemId() == 76784
					|| item.getItem().getItemId() == 76773
					|| item.getItem().getItemId() == 76772
					|| item.getItem().getItemId() == 76771
					|| item.getItem().getItemId() == 76770
					|| item.getItem().getItemId() == 76769
					|| item.getItem().getItemId() == 76768
					|| item.getItem().getItemId() == 76767
					|| item.getItem().getItemId() == 76777
					|| item.getItem().getItemId() == 555563
					|| item.getItem().getItemId() == 76776
					|| item.getItem().getItemId() == 76775
					|| item.getItem().getItemId() == 76774
							|| item.getItem().getItemId() == 767740
					|| item.getItem().getItemId() ==500144
					|| item.getItem().getItemId() ==500145
					|| item.getItem().getItemId() ==500146
					|| item.getItem().getItemId() ==490018
					||item.getItem().getItemId() == 41246) {
				pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
				return;
			}
						if (pc.getSkillEffectTimerSet().hasSkillEffect(6524)) { 
				pc.sendPackets(new S_SystemMessage("리스후 30초간 창고 이용을 하실수 없습니다."));
				return;
		    }
						if (item.getAcByMagic() > 0) {
							pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
							return;
						}
						if (item.getDmgByMagic() > 0) {
							pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
							return;
						}
						if (item.getHolyDmgByMagic() > 0) {
							pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
							return;
						}
						if (item.getHitByMagic() > 0) {
							pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
							return;
						}
						if (item.getCount() > 2000000000) {
							return;
						}
						if (count > 2000000000) {
							return;
						}
			if (count > item.getCount())
				count = item.getCount();
			if (!isAvailablePcWeight(pc, item, count))
				break;
			if (!hasAdena(pc))
				break;
			 //** 중계기 노딜버그 막아 보자 **//
			  long nowtime = System.currentTimeMillis();
			  if(item.getItemdelay3() >=  nowtime ){
				  break;
			  }  
			  //** 중계기 노딜버그 막아 보자 **//
				 //신선한우유 수량성버그방지
			   if (!item.getItem().isToBeSavedAtOnce()) {
			   pc.getInventory().saveItem(item, L1PcInventory.COL_COUNT);
			   }	 
		      //신선한우유 수량성버그방지
			warehouse.tradeItem(item, count, pc.getInventory());
			CodeLogger.getInstance().warehouselog("찾	일반", pc.getName()+"["+pc.getAccountName()+"]",item,count);
			eva.LogWareHouseAppend("일반:찾", pc.getName(), "", item, count, objectId);

		}
	}
	// 특수창고 찾기 
	private void getItemToSpecialWarehouse(L1PcInstance pc, int size) {
		L1ItemInstance item = null;
		SpecialWarehouse warehouse = WarehouseManager.getInstance()
				.getSpecialWarehouse(pc.getName());
		if (warehouse == null)
			return;
		for (int i = 0, objectId, count; i < size; i++) {
			objectId = readD();
			count = readD();
			item = warehouse.getItem(objectId);
			if (!isAvailableTrade(pc, objectId, item, count))
				break;
			if (count > item.getCount())
				count = item.getCount();
			if (!isAvailablePcWeight(pc, item, count))
				break;
			if (!hasAdena(pc))
				break;
			if (pc.getLevel() < 5) {
				pc.sendPackets(new S_SystemMessage("창고는 5레벨 이상 사용 가능 합니다."));
				return;
			}
		    if (!pc.isGm() && item.getItem().getItemId() == 40308)  {
	        	if (count > 20000000) {
	        		 pc.sendPackets(new S_SystemMessage("아데나는 2000만 단위씩 창고이용이 가능합니다."));
	        		 return;
	        	}
		    }
			/* 버그방지 */
			if (item.getItem().getItemId() == 41159
					|| item.getItem().getItemId() == 76778
					|| item.getItem().getItemId() == 76779
					|| item.getItem().getItemId() == 76780
					|| item.getItem().getItemId() == 76781
					|| item.getItem().getItemId() == 76782
					|| item.getItem().getItemId() == 76783
					|| item.getItem().getItemId() == 76784
					|| item.getItem().getItemId() == 76773
					|| item.getItem().getItemId() == 76772
					|| item.getItem().getItemId() == 76771
					|| item.getItem().getItemId() == 76770
					|| item.getItem().getItemId() == 76769
					|| item.getItem().getItemId() == 76768
					|| item.getItem().getItemId() == 76767
					|| item.getItem().getItemId() == 76777
					|| item.getItem().getItemId() == 555563
					|| item.getItem().getItemId() == 76776
					|| item.getItem().getItemId() == 76775
					|| item.getItem().getItemId() == 76774
							|| item.getItem().getItemId() == 767740
					|| item.getItem().getItemId() ==500144
					|| item.getItem().getItemId() ==500145
					|| item.getItem().getItemId() ==500146
					|| item.getItem().getItemId() ==490018
					||item.getItem().getItemId() == 41246) {
				pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
				return;
			}
						if (pc.getSkillEffectTimerSet().hasSkillEffect(6524)) { 
				pc.sendPackets(new S_SystemMessage("리스후 30초간 창고 이용을 하실수 없습니다."));
				return;
		    }
			warehouse.tradeItem(item, count, pc.getInventory());
			CodeLogger.getInstance()
					.warehouselog("특수 찾기	일반",
							pc.getName() + "[" + pc.getAccountName() + "]",
							item, count);
		}
	}

	// 특수창고 맡기기 
	private void putItemToSpecialWarehouse(L1PcInstance pc, int size) {
		L1Object object = null;
		L1ItemInstance item = null;
		SpecialWarehouse warehouse = WarehouseManager.getInstance()
				.getSpecialWarehouse(pc.getName());
		if (warehouse == null)
			return;
		for (int i = 0, objectId, count; i < size; i++) {
			objectId = readD();
			count = readD();
			object = pc.getInventory().getItem(objectId);
			item = (L1ItemInstance) object;
			if (pc.getLevel() < 5) {
				pc.sendPackets(new S_SystemMessage("창고는 5레벨 이상 사용 가능 합니다."));
				return;
			}
			/* 버그방지 */
			// ** 엔진방어 **// By 도우너
			if (pc.getInventory().findItemId(40308).getCount() < 31) {
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아데나가 충분치않습니다.", Opcodes.S_OPCODE_MSG, 20); 
				pc.sendPackets(s_chatpacket);
				return;
			}
			// 월드맵상 내 계정과 같은 동일 한 계정을 가진 캐릭이 접속중이라면
			if (isTwoLogin(pc))
				return;
			if (objectId != item.getId()) {
				pc.sendPackets(new S_Disconnect());
				break;
			}
			if (!item.isStackable() && count != 1) {
				pc.sendPackets(new S_Disconnect());
				break;
			}
			/* 버그방지 */
			if (item == null || item.getCount() < count || count <= 0
					|| item.getCount() <= 0) {
				break;
			}
		    if (!pc.isGm() && item.getItem().getItemId() == 40308)  {
	        	if (count > 20000000) {
	        		 pc.sendPackets(new S_SystemMessage("아데나는 2000만 단위씩 창고이용이 가능합니다."));
	        		 return;
	        	}
		    }
			if(item.getItem().getItemId() >= 76767 && item.getItem().getItemId() <= 76784 ){
				pc.sendPackets(new S_SystemMessage("봉인이풀린 룬,유물은 창고이용이 불가능 합니다."));
			return;
			}
			if (item.getItem().getItemId() == 41159
					|| item.getItem().getItemId() == 76778
					|| item.getItem().getItemId() == 76779
					|| item.getItem().getItemId() == 76780
					|| item.getItem().getItemId() == 76781
					|| item.getItem().getItemId() == 76782
					|| item.getItem().getItemId() == 76783
					|| item.getItem().getItemId() == 76784
					|| item.getItem().getItemId() == 555563
					|| item.getItem().getItemId() == 76773
					|| item.getItem().getItemId() == 76772
					|| item.getItem().getItemId() == 76771
					|| item.getItem().getItemId() == 76770
					|| item.getItem().getItemId() == 76769
					|| item.getItem().getItemId() == 76768
					|| item.getItem().getItemId() == 76767
					|| item.getItem().getItemId() == 76777
					|| item.getItem().getItemId() == 76776
					|| item.getItem().getItemId() == 76775
					|| item.getItem().getItemId() == 76774
							|| item.getItem().getItemId() == 767740
					|| item.getItem().getItemId() ==500144
					|| item.getItem().getItemId() ==500145
					|| item.getItem().getItemId() ==500146
					|| item.getItem().getItemId() ==490018
					||item.getItem().getItemId() == 41246
					|| item.getItem().getItemId() == 720
					|| item.getItem().getItemId() == 721
					|| item.getItem().getItemId() == 722
					) {
				pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
				return;
			}
			if (item.getCount() > 10000000) {
				break;
			}
			if (count > 10000000) {
				break;
			}
			if (!isAvailableTrade(pc, objectId, item, count))
				break;
			if (!checkPetList(pc, item))
				break;
			if (!isAvailableWhCount(warehouse, pc, item, count))
				break;
			if (count > item.getCount())
				count = item.getCount();

			pc.getInventory().tradeItem(objectId, count, warehouse);
			pc.getLight().turnOnOffLight();

			CodeLogger.getInstance()
					.warehouselog("특수 맡기	일반",
							pc.getName() + "[" + pc.getAccountName() + "]",
							item, count);
		}
	}
	// 특수창고 맡기기
	

	private static String currentTime() {
		//TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		//Calendar cal = Calendar.getInstance(tz);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9"));
		int year = cal.get(Calendar.YEAR) - 2000;
		String year2;
		if (year < 10) {
			year2 = "0" + year;
		} else {
			year2 = Integer.toString(year);
		}
		int Month = cal.get(Calendar.MONTH) + 1;
		String Month2 = null;
		if (Month < 10) {
			Month2 = "0" + Month;
		} else {
			Month2 = Integer.toString(Month);
		}
		int date = cal.get(Calendar.DATE);
		String date2 = null;
		if (date < 10) {
			date2 = "0" + date;
		} else {
			date2 = Integer.toString(date);
		}
		int hour = cal.get(Calendar.HOUR);
		String hour2 = null;
		if(hour < 10)
			hour2 = "0" + hour;
		else
			hour2 = Integer.toString(hour);
		int min = cal.get(Calendar.MINUTE);
		String min2 = null;
		if(min < 10)
			min2 = "0" + min;
		else
			min2 = Integer.toString(min);
		return year2 + "/" + Month2 + "/" + date2 + " " + hour2+":"+min2;
	}

	private void putItemToPrivateWarehouse(L1PcInstance pc, int size) {
		L1Object object = null;
		L1ItemInstance item = null;
		PrivateWarehouse warehouse = WarehouseManager.getInstance()
				.getPrivateWarehouse(pc.getAccountName());
		if (warehouse == null)
			return;

		for (int i = 0, objectId, count; i < size; i++) {
			objectId = readD();
			count = readD();

			object = pc.getInventory().getItem(objectId);
			item = (L1ItemInstance) object;

			if (!item.getItem().isTradable()) {
				// \f1%0은 버리거나 또는 타인에게 양도 할 수 없습니다.
				pc.sendPackets(new S_ServerMessage(210, item.getItem()
						.getName()));
				break;
			}
		    if (!pc.isGm() && item.getItem().getItemId() == 40308)  {
	        	if (count > 10000000) {
	        		 pc.sendPackets(new S_SystemMessage("아데나는 1000만 단위씩 창고이용이 가능합니다."));
	        		 return;
	        	}
		    }
			if(item.getItem().getItemId() >= 76767 && item.getItem().getItemId() <= 76784 ){
				pc.sendPackets(new S_SystemMessage("봉인이풀린 룬,유물은 창고이용이 불가능 합니다."));
			return;
			}
			if (item.getItem().getItemId() == 41159
					|| item.getItem().getItemId() == 76778
					|| item.getItem().getItemId() == 76779
					|| item.getItem().getItemId() == 76780
					|| item.getItem().getItemId() == 76781
					|| item.getItem().getItemId() == 76782
					|| item.getItem().getItemId() == 76783
					|| item.getItem().getItemId() == 76784
					|| item.getItem().getItemId() == 555563
					|| item.getItem().getItemId() == 76773
					|| item.getItem().getItemId() == 76772
					|| item.getItem().getItemId() == 76771
					|| item.getItem().getItemId() == 76770
					|| item.getItem().getItemId() == 76769
					|| item.getItem().getItemId() == 76768
					|| item.getItem().getItemId() == 76767
					|| item.getItem().getItemId() == 76777
					|| item.getItem().getItemId() == 76776
					|| item.getItem().getItemId() == 76775
					|| item.getItem().getItemId() == 76774
							|| item.getItem().getItemId() == 767740
					|| item.getItem().getItemId() ==500144
					|| item.getItem().getItemId() ==500145
					|| item.getItem().getItemId() ==500146
					|| item.getItem().getItemId() ==490018
					||item.getItem().getItemId() == 41246
					|| item.getItem().getItemId() == 720
					|| item.getItem().getItemId() == 721
					|| item.getItem().getItemId() == 722
					) {
				pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
				return;
			}
			if (item.getItem().getItemId() == 40308) {
				if (count > 10000000) {
					pc.sendPackets(new S_SystemMessage("아데나 (10,000,000) 이하만 창고 이용이 가능합니다."));
					return;
				}
			}
			if (item.getAcByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getDmgByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getHolyDmgByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getHitByMagic() > 0) {
				pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
				return;
			}
			if (item.getCount() > 2000000000) {
				return;
			}
			if (count > 2000000000) {
				return;
			}
			if (!isAvailableTrade(pc, objectId, item, count))
				break;
			if (!checkPetList(pc, item))
				break;
			if (!isAvailableWhCount(warehouse, pc, item, count))
				break;
			 //** 중계기 노딜버그 막아 보자 **//
			  long nowtime = System.currentTimeMillis();
			  if(item.getItemdelay3() >=  nowtime ){
				  break;
			  }  
			  //** 중계기 노딜버그 막아 보자 **//
				 //신선한우유 수량성버그방지
			   if (!item.getItem().isToBeSavedAtOnce()) {
			   pc.getInventory().saveItem(item, L1PcInventory.COL_COUNT);
			   }	 
		      //신선한우유 수량성버그방지
			if (count > item.getCount())
				count = item.getCount();

			pc.getInventory().tradeItem(objectId, count, warehouse);
			pc.getLight().turnOnOffLight();
			eva.LogWareHouseAppend("일반:맡", pc.getName(), "", item, count, objectId);
			CodeLogger.getInstance().warehouselog("맡	일반", pc.getName()+"["+pc.getAccountName()+"]",item,count);

		}
	}

	private void sellItemToPrivateShop(L1PcInstance pc, L1Object findObject,
			int size) {
		L1PcInstance targetPc = null;

		if (findObject instanceof L1PcInstance) {
			targetPc = (L1PcInstance) findObject;
		}
		if (targetPc.isTradingInPrivateShop())
			return;

		targetPc.setTradingInPrivateShop(true);

		L1PrivateShopBuyList psbl;
		L1ItemInstance item = null;
		boolean[] isRemoveFromList = new boolean[8];
		FastTable<L1PrivateShopBuyList> buyList = targetPc.getBuyList();

		synchronized (buyList) {
			int order, itemObjectId, count, buyPrice, buyTotalCount, buyCount;
			for (int i = 0; i < size; i++) {
				itemObjectId = readD();
				count = readCH();
				order = readC();

				item = pc.getInventory().getItem(itemObjectId);

				if (!isAvailableTrade(pc, itemObjectId, item, count))
					break;
				if (item.getBless() >= 128) {
					// \f1%0은 버리거나 또는 타인에게 양도 할 수 없습니다.
					pc.sendPackets(new S_ServerMessage(210, item.getItem()
							.getName()));
					break;
				}

				psbl = (L1PrivateShopBuyList) buyList.get(order);
				buyPrice = psbl.getBuyPrice();
				buyTotalCount = psbl.getBuyTotalCount(); // 살 예정의 개수
				buyCount = psbl.getBuyCount(); // 산 누계

				if (count > buyTotalCount - buyCount)
					count = buyTotalCount - buyCount;

				if (item.isEquipped()) {
					pc.sendPackets(new S_ServerMessage(905)); // 장비 하고 있는 아이템은
																// 판매할 수 없습니다.
					break;
				}

				if (!isAvailablePcWeight(pc, item, count))
					break;
				if (isOverMaxAdena(targetPc, buyPrice, count))
					return;

				// 개인상점 부분 비셔스 방어 //
				int itemType = item.getItem().getType2();
				/* 버그방지 */
				if (itemObjectId != item.getId()) {
					pc.sendPackets(new S_Disconnect());
					targetPc.sendPackets(new S_Disconnect());
					return;
				}
				if (!item.isStackable() && count != 1) {
					pc.sendPackets(new S_Disconnect());
					targetPc.sendPackets(new S_Disconnect());
					return;
				}
				if (count >= item.getCount()) {
					count = item.getCount();
				}
				/* 버그방지 */
				if ((itemType == 1 && count != 1)
						|| (itemType == 2 && count != 1)) {
					return;
				}
				if (item.getCount() <= 0 || count <= 0
						|| item.getCount() < count) {
					pc.sendPackets(new S_Disconnect());
					targetPc.sendPackets(new S_Disconnect());
					return;
				}
				if (buyPrice * count <= 0 || buyPrice * count > 2000000000) {
					return;
				}
		        
				if (item.getCount() > 2000000000) {
					return;
				}
				if (count > 2000000000) {
					return;
				}
				// ** 개인상점 부분 비셔스 방어 **// by 도우너
				if (count >= item.getCount())
					count = item.getCount();

				if (!targetPc.getInventory().checkItem(L1ItemId.ADENA, count * buyPrice)) {
					targetPc.sendPackets(new S_ServerMessage(189)); // \f1아데나가/ 부족합니다.
					break;
				}

				L1ItemInstance adena = targetPc.getInventory().findItemId(
						L1ItemId.ADENA);
				if (adena == null)
					break;

				targetPc.getInventory().tradeItem(adena, count * buyPrice,
						pc.getInventory());
				pc.getInventory().tradeItem(item, count,
						targetPc.getInventory());
				psbl.setBuyCount(count + buyCount);
				buyList.set(order, psbl);

				if (psbl.getBuyCount() == psbl.getBuyTotalCount()) { // 살 예정의
																		// 개수를
																		// 샀다
					isRemoveFromList[order] = true;
				}

				try {
					pc.saveInventory();
					targetPc.saveInventory();
				} catch (Exception e) {
					// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			}
			// 매점한 아이템을 리스트의 말미로부터 삭제
			for (int i = 7; i >= 0; i--) {
				if (isRemoveFromList[i]) {
					buyList.remove(i);
				}
			}
			targetPc.setTradingInPrivateShop(false);
		}
	}

	private void sellItemToShop(L1PcInstance pc, int npcId, int size) {
		L1Shop shop = ShopTable.getInstance().get(npcId);
		L1ShopSellOrderList orderList = shop.newSellOrderList(pc);
		int itemNumber;
		long itemcount;

		for (int i = 0; i < size; i++) {
			itemNumber = readD();
			itemcount = readD();
			if (itemcount <= 0) {
				return;
			}
			if (npcId >= 9000001 && npcId <= 9000036 && !pc.getInventory().getItem(itemNumber).isPackage()) {
				pc.sendPackets(new S_SystemMessage("\\fY패키지상점에서 구매하지 않은 아이템이 포함되어 있습니다."));
				return;
			}
			orderList.add(itemNumber, (int) itemcount, pc);
			if (orderList.BugOk() != 0) {
			    /*pc.sendPackets(new S_Disconnect());
			    pc.getNetConnection().kick();
			    pc.getNetConnection().close();*/
				for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
					if (player.isGm() || pc == player) {
						player.sendPackets(new S_SystemMessage(pc.getName()+ "님 상점 최대구매 수량초과 ("+itemcount+")"));
					}
				}
			  }
		}
		int bugok = orderList.BugOk();
		if (bugok == 0) {
			shop.buyItems(orderList);
			// 아이템저장시킴
			pc.saveInventory();
			// 아이템저장시킴
		}
	}
	private void sellItemToNpcShop(L1PcInstance pc, int npcId, int size) {
		L1Shop shop = NpcCashShopTable.getInstance().get(npcId);
		L1ShopSellOrderList orderList = shop.newSellOrderList(pc);
		int itemNumber; long itemcount;

		for (int i = 0; i < size; i++) {
			itemNumber = readD();
			//itemcount = readD();
			itemcount = 1;
			//System.out.println(itemNumber + " " + itemcount);
			if(itemcount <= 0){
				return;
			}			
			orderList.add(itemNumber, (int)itemcount , pc);	
		}
		int bugok = orderList.BugOk();
		//System.out.println("ok");
		if (bugok == 0){
			shop.buyItemsToNpcShop(orderList, npcId);
		}		
	}
	/** 영자 상점 엔피씨 샵에서 구입 */
	private void buyItemFromNpcShop(L1PcInstance pc, int npcId, int size) {
		L1Shop shop = NpcShopTable.getInstance().get(npcId);
		L1ShopBuyOrderList orderList = shop.newBuyOrderList();
		int itemNumber;
		long itemcount;

		if (shop.getSellingItems().size() < size) {
			System.out.println("상점이 판매하는 아이템 수(" + shop.getSellingItems().size() + ")보다 더 많이 사려고 함.("+size+")개");
			pc.sendPackets(new S_Disconnect());
			pc.getNetConnection().kick();
			pc.getNetConnection().close();
			return;
		}
		
		for (int i = 0; i < size; i++) {
			itemNumber = readD();
			itemcount = readD();
			if (itemcount <= 0) {
				return;
			}
			if(size >= 2){
				pc.sendPackets(new S_SystemMessage("한번에 서로 다른아이템을 구입할수없습니다."));
				return;
			}
			//if(pc.getMapId() == 360){			
				if(itemcount > 15) {
					pc.sendPackets(new S_SystemMessage("최대구매수량 : 잡템류(15개씩) / 장비류(1개씩)"));
					return;
				//}
			}
			orderList.add(itemNumber, (int) itemcount, pc);
		}
		int bugok = orderList.BugOk();
		if (bugok == 0) {
			shop.sellItems(pc, orderList);
			// 백섭복사 방지 수량성버그방지
			pc.saveInventory();
			// 백섭복사 방지 수량성버그방지
		}
	}
	/** 캐쉬 샵에서 구입 */
	private void buyItemFromNpcCashShop(L1PcInstance pc, int npcId, int size){
		L1Shop shop = NpcCashShopTable.getInstance().get(npcId);
		L1ShopBuyOrderList orderList = shop.newBuyOrderList();
		int itemNumber; long itemcount;
		
		for (int i = 0; i < size; i++) {
			itemNumber = readD();
			itemcount = readD();
			if(itemcount <= 0) {
				return;
			}
			if(size >= 2){ //동시에 다른물건을 살수없게 2개가 선택된다면,
				pc.sendPackets(new S_SystemMessage("\\fY한번에 서로 다른아이템을 구입할수없습니다."));
				return;
			}
			//if(pc.getMapId() == 631){//오렌시장을 잡템만 가능하게했기때문에, 오렌시장에선 15개씩 수량 이상 안사지게			
				if(itemcount > 1) {
					pc.sendPackets(new S_SystemMessage("\\fY최대구매수량 : 장비류(1개씩)"));
					return;
				//}
			}	
			orderList.add(itemNumber, (int)itemcount , pc);	
		}
		int bugok = orderList.BugOk();
		if (bugok == 0){
			shop.sellItems(pc, orderList);
		    //백섭복사 방지 수량성버그방지
		    pc.saveInventory();
		    //백섭복사 방지 수량성버그방지
		}
	}
	

	private void buyItemFromPrivateShop(L1PcInstance pc, L1Object findObject,
			int size) {
		L1PcInstance targetPc = null;
		if (findObject instanceof L1PcInstance) {
			targetPc = (L1PcInstance) findObject;
		}
		if (targetPc.isTradingInPrivateShop())
			return;

		FastTable<L1PrivateShopSellList> sellList = targetPc.getSellList();

		synchronized (sellList) {
			// 품절이 발생해, 열람중의 아이템수와 리스트수가 다르다
			if (pc.getPartnersPrivateShopItemCount() != sellList.size())
				return;
			if (pc.getPartnersPrivateShopItemCount() < sellList.size())
				return;

			targetPc.setTradingInPrivateShop(true);

			L1ItemInstance item;
			L1PrivateShopSellList pssl;
			boolean[] isRemoveFromList = new boolean[8];
			int order, count, price, sellCount, sellPrice, itemObjectId, sellTotalCount;
			for (int i = 0; i < size; i++) { // 구입 예정의 상품
				order = readD();
				count = readD();

				pssl = (L1PrivateShopSellList) sellList.get(order);
				itemObjectId = pssl.getItemObjectId();
				sellPrice = pssl.getSellPrice();
				sellTotalCount = pssl.getSellTotalCount(); // 팔 예정의 개수
				sellCount = pssl.getSellCount(); // 판 누계
				item = targetPc.getInventory().getItem(itemObjectId);

				if (item == null)
					break;
				//** 중계기 노딜버그 막아 보자 **//
				  long nowtime = System.currentTimeMillis();
				  if(item.getItemdelay3() >=  nowtime ){
					  break;
				  }  
				  //** 중계기 노딜버그 막아 보자 **//
				if (item.isEquipped()) {
					pc.sendPackets(new S_ServerMessage(905, "")); // 장비 하고 있는
																	// 아이템 구매
																	// 못하게.
					break;
				}
				if (count > sellTotalCount - sellCount)
					count = sellTotalCount - sellCount;
				if (count == 0)
					break;
				/*버그방지*/
				int itemType = item.getItem().getType2();
				if(size >= 2){
					pc.sendPackets(new S_SystemMessage("한번에 서로 다른아이템을 구입할수없습니다."));
					break;
				}
				//if(targetPc.getMapId() == 360){
					if(count > 15) {
						pc.sendPackets(new S_SystemMessage("최대구매수량 : 잡템류(15개씩) / 장비류(1개씩)"));
						break;
					//}
				}
	            if(count <= 0){
					pc.sendPackets(new S_Disconnect());
					return;
				}
				
				if (!item.isStackable() && count != 1) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (item == null || item.getCount() < count) {
			    	 pc.sendPackets(new S_Disconnect());
			         return;
			    }
			    if ((itemType == 1 && item.getCount() != 1) ||	(itemType == 2 && item.getCount() != 1)){
					pc.sendPackets(new S_Disconnect());
					return;
				}
			    if (count <= 0 || count < 1 || item.getCount() <= 0) {
			    	pc.sendPackets(new S_Disconnect());
			        return;
			    }			
		        if (item.getCount() > 2000000000) {
				    return;
				}
			    if (count > 2000000000) {
				    return;
				}
				if (!isAvailablePcWeight(pc, item, count))
					break;
				if (isOverMaxAdena(pc, sellPrice, count))
					break;

				price = count * sellPrice;
				if (price <= 0 || price > 2000000000)
					break;

				if (!isAvailableTrade(pc, targetPc, itemObjectId, item, count))
					break;

				if (count >= item.getCount())
					count = item.getCount();
				if (item.getCount() > 9999)
					break;

				if (!pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아데나가 충분치않습니다.", Opcodes.S_OPCODE_MSG, 20); 
					pc.sendPackets(s_chatpacket);
					break;
				}

				L1ItemInstance adena = pc.getInventory().findItemId(
						L1ItemId.ADENA);

				if (targetPc == null || adena == null)
					break;
				if (targetPc.getInventory().tradeItem(item, count,
						pc.getInventory()) == null)
					break;

				pc.getInventory().tradeItem(adena, price,
						targetPc.getInventory());

				// %1%o %0에 판매했습니다.
				String message = item.getItem().getName() + " ("
						+ String.valueOf(count) + ")";
				targetPc.sendPackets(new S_ServerMessage(877, pc.getName(),
						message));

				pssl.setSellCount(count + sellCount);
				sellList.set(order, pssl);

				writeLogbuyPrivateShop(pc, targetPc, item, count, price);

				if (pssl.getSellCount() == pssl.getSellTotalCount()) // 해당 템을
																		// 다 팔았다
					isRemoveFromList[order] = true;
				try {
					pc.saveInventory();
					targetPc.saveInventory();
				} catch (Exception e) {
				}
			}

			// 품절된 아이템을 리스트의 말미로부터 삭제
			for (int i = 7; i >= 0; i--) {
				if (isRemoveFromList[i]) {
					sellList.remove(i);
				}
			}
			targetPc.setTradingInPrivateShop(false);
		}
	}

	private void buyItemFromShop(L1PcInstance pc, int npcId, int size) {
		L1Shop shop = ShopTable.getInstance().get(npcId);
		L1ShopBuyOrderList orderList = shop.newBuyOrderList();
		int itemNumber;
		long itemcount;
		if (shop.getSellingItems().size() < size) {
			   System.out.println("상점이 판매하는 아이템 수(" + shop.getSellingItems().size() + ")보다 더 많이 사려고 함.("+size+")개");
			   pc.getNetConnection().kick();
			   pc.getNetConnection().close();
			   return;
			  }
		for (int i = 0; i < size; i++) {
			itemNumber = readD();
			itemcount = readD();
			if (itemcount <= 0) {
				return;
			}
			
			if (npcId >= 9000001 && npcId <= 9000036) {
				if (itemcount > 1) {
					pc.sendPackets(new S_SystemMessage("\\fY상점 아이템은 1개씩 구입가능합니다."));
					return;
				}
			}
			
			
			orderList.add(itemNumber, (int) itemcount, pc);
			if (orderList.BugOk() != 0) {
			  /*  pc.sendPackets(new S_Disconnect());
			    pc.getNetConnection().kick();
			    pc.getNetConnection().close();
			    return;*/
				for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
					if (player.isGm() || pc == player) {
						player.sendPackets(new S_SystemMessage(pc.getName()+ "님 상점 최대구매 수량초과 ("+itemcount+")"));
					}
				}
			  }
		}
		int bugok = orderList.BugOk();
		if (bugok == 0) {
			shop.sellItems(pc, orderList);
			// 아이템저장시킴
			pc.saveInventory();
			// 아이템저장시킴
		}
	}
	
	private static void UpdateLog(String name, String clanname, String itemname, int count, int type) {//적당히 추가
		  Connection con = null;
		  PreparedStatement pstm = null;
		  Timestamp time = new Timestamp(System.currentTimeMillis());
		  try {
		   con = L1DatabaseFactory.getInstance().getConnection();
		   pstm = con.prepareStatement("INSERT INTO clan_warehouse_log SET name=?, clan_name=?, item_name=?, item_count=?, type=?, time=?");
		   pstm.setString(1, name);
		   pstm.setString(2, clanname);
		   pstm.setString(3, itemname);
		   pstm.setInt(4, count);
		   pstm.setInt(5, type);
		   pstm.setTimestamp(6, time);
		   pstm.execute();
		  } catch (SQLException e) {
		  } finally {
		   SQLUtil.close(pstm);
		   SQLUtil.close(con);
		  }
		 }


	private boolean isTwoLogin(L1PcInstance c) {
		for(L1PcInstance target : L1World.getInstance().getAllPlayersToArray()){
			if(target.noPlayerCK) continue;
			int count = 0;
			if(c.getId() == target.getId()){
				count++;
				if(count > 1){
					c.getNetConnection().kick();
					c.getNetConnection().close();
					target.getNetConnection().kick();
					target.getNetConnection().close();
					return true;
				}
			}
			else if(c.getId() != target.getId()){
				if(c.getAccountName().equalsIgnoreCase(target.getAccountName())) {
					if(!AutoShopManager.getInstance().isAutoShop(target.getId())){
						c.getNetConnection().kick();
						c.getNetConnection().close();
						target.getNetConnection().kick();
						target.getNetConnection().close();
						return true;
					}			
				}
			}
		}
		return false;
	}
	

	private void writeLogbuyPrivateShop(L1PcInstance pc, L1PcInstance targetPc,
			L1ItemInstance item, int count, int price) {
		String itemadena = item.getName() + "(" + price + ")";
		eva.LogShopAppend("상점", pc.getName(), targetPc.getName(), item.getEnchantLevel(), itemadena, item.getBless(), count, item.getId());
	}

	private boolean isOverMaxAdena(L1PcInstance pc, int sellPrice, int count) {
		if (sellPrice * count > 2000000000
				||sellPrice * count < 0) {
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return true;
		}
		if(count < 0){
			return true;
		}
		if(sellPrice < 0){
			return true;
		}
		return false;
	}

	private boolean checkPetList(L1PcInstance pc, L1ItemInstance item) {
		L1DollInstance doll = null;
		Object[] dollList = pc.getDollList().values().toArray();
		for (Object dollObject : dollList) {
			doll = (L1DollInstance) dollObject;
			if (item.getId() == doll.getItemObjId()) {
				pc.sendPackets(new S_ServerMessage(1181)); // 
				return false;
			}
		}
		Object[] petlist = pc.getPetList().values().toArray();
		for (Object petObject : petlist) {
			if (petObject instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) petObject;
				if (item.getId() == pet.getItemObjId()) {
					// \f1%0은 버리거나 또는 타인에게 양도 할 수 없습니다.
					pc.sendPackets(new S_ServerMessage(210, item.getItem()
							.getName()));
					return false;
				}
			}
		}
		return true;
	}

	private boolean isAvailableWhCount(Warehouse warehouse, L1PcInstance pc,
			L1ItemInstance item, int count) {
		if (warehouse.checkAddItemToWarehouse(item, count) == L1Inventory.SIZE_OVER) {
			// \f1상대가 물건을 너무 가지고 있어 거래할 수 없습니다.
			pc.sendPackets(new S_ServerMessage(75));
			return false;
		}
		return true;
	}

	private boolean isAvailableClan(L1PcInstance pc, L1Clan clan) {
		if (pc.getClanid() == 0 || clan == null) {
			// \f1혈맹 창고를 사용하려면 혈맹에 가입하지 않으면 안됩니다.
			pc.sendPackets(new S_ServerMessage(208));
			return false;
		}
		return true;
	}

	private boolean isAvailablePcWeight(L1PcInstance pc, L1ItemInstance item,
			int count) {
		if (pc.getInventory().checkAddItem(item, count) != L1Inventory.OK) {
			// \f1 가지고 있는 것이 무거워서 거래할 수 없습니다.
			pc.sendPackets(new S_ServerMessage(270));
			return false;
		}
		return true;
	}

	private boolean hasAdena(L1PcInstance pc) {
		if (!pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
			// \f1아데나가 부족합니다.
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아데나가 충분치 않습니다.", Opcodes.S_OPCODE_MSG, 20); 
			pc.sendPackets(s_chatpacket);
			return false;
		}
		return true;
	}

	private boolean isAvailableTrade(L1PcInstance pc, int objectId,
			L1ItemInstance item, int count) {
		boolean result = true;

		if (item == null)
			result = false;
		if (objectId != item.getId())
			result = false;
		if (!item.isStackable() && count != 1)
			result = false;
		if (item.getCount() <= 0 || item.getCount() > 2000000000)
			result = false;
		if (count <= 0 || count > 2000000000)
			result = false;

		if (!result) {
			pc.sendPackets(new S_Disconnect());
		}

		return result;
	}

	private boolean isAvailableTrade(L1PcInstance pc, L1PcInstance targetPc,
			int itemObjectId, L1ItemInstance item, int count) {
		boolean result = true;

		if (item == null)
			result = false;
		if ((itemObjectId != item.getId())
				|| (!item.isStackable() && count != 1))
			result = false;
		if (count <= 0 || item.getCount() <= 0 || item.getCount() < count)
			result = false;
		if (count > 2000000000 || item.getCount() > 2000000000)
			result = false;

		if (!result) {
			pc.sendPackets(new S_Disconnect());
			targetPc.sendPackets(new S_Disconnect());
		}

		return result;
	}

	/*private void NpcEffect(L1PcInstance pc, L1Object target, int npcId,
			int resultType) {
		try {
			if (pc == null || target == null) {
				return;
			}

			if (resultType == TYPE_BUY_SHP || resultType == TYPE_SEL_SHP) {
				int castgfx = 0;
				if (npcId  >= 9000001 &&  npcId <= 9000014) {
					castgfx = 763;
				} else {
					castgfx = 0;
				}
				if (castgfx != 0) {
					pc.sendPackets(new S_SkillSound(target.getId(), castgfx));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(target
							.getId(), castgfx));
				}
			} else {
			}
		} catch (Exception e) {
		}
	}*/

	@Override
	public String getType() {
		return "[C] C_Result";
	}
}