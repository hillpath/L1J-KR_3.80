package l1j.server.server.model.item.function;


import java.sql.Timestamp;

import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class PandoraT extends L1ItemInstance{
	
	public PandoraT(L1Item item){
		super(item);
	}
	
	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet){
		try{
			if(cha instanceof L1PcInstance){
				L1PcInstance pc = (L1PcInstance)cha;
				L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
				L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());
				int targetItemId = targetItem.getItemId();
				int itemId = this.getItemId();
				int EndTime = 360000 * 1000; // 5일
				switch(itemId){
				/** 
				 * 스탯티 변환
				 * 
				 * **/
				case 89531:  // 판도라의 완력꽃 향수:티[30일]
					switch (targetItemId) {
					case 20085: // 티셔츠
						PandoraTransform(pc, useItem, targetItem , 22440, EndTime); // 판도라의 완력꽃향 티
						break;						
					case 20084: // 요정족 티셔츠
						PandoraTransform(pc, useItem, targetItem , 22450, EndTime); // 판도라의 완력꽃향 요정족 티
						break;
					default:
						pc.sendPackets(new S_ServerMessage(328));
					}
					break;
				case 89532: // 판도라의 민첩꽃 향수:티[30일]
					switch (targetItemId) {
					case 20085: // 티셔츠
						PandoraTransform(pc, useItem, targetItem , 22441, EndTime); // 판도라의 민첩꽃향 티
						break;						
					case 20084: // 요정족 티셔츠
						PandoraTransform(pc, useItem, targetItem , 22451, EndTime); // 판도라의 민첩꽃향 요정족 티
						break;
					default:
						pc.sendPackets(new S_ServerMessage(328));
					}
					break;
				case 89533: // 판도라의 지식꽃 향수:티[30일]
					switch (targetItemId) {
					case 20085: // 티셔츠
						PandoraTransform(pc, useItem, targetItem , 22442, EndTime); // 판도라의 지식꽃향 티
						break;						
					case 20084: // 요정족 티셔츠
						PandoraTransform(pc, useItem, targetItem , 22452, EndTime); // 판도라의 지식꽃향 요정족 티
						break;
					default:
						pc.sendPackets(new S_ServerMessage(328));
					}
					break;
				case 89534: // 판도라의 지혜꽃 향수:티[30일]
					switch (targetItemId) {
					case 20085: // 티셔츠
						PandoraTransform(pc, useItem, targetItem , 22443, EndTime); // 판도라의 지혜꽃향 티
						break;						
					case 20084: // 요정족 티셔츠
						PandoraTransform(pc, useItem, targetItem , 22453, EndTime); // 판도라의 지혜꽃향 요정족 티
						break;
					default:
						pc.sendPackets(new S_ServerMessage(328));
					}
					break;
				case 89535: // 판도라의 체력꽃 향수:티[30일]
					switch (targetItemId) {
					case 20085: // 티셔츠
						PandoraTransform(pc, useItem, targetItem , 22444, EndTime); // 판도라의 체력꽃향 티
						break;						
					case 20084: // 요정족 티셔츠
						PandoraTransform(pc, useItem, targetItem , 22454, EndTime); // 판도라의 체력꽃향 요정족 티
						break;
					default:
						pc.sendPackets(new S_ServerMessage(328));
					}
					break;
				case 89536: // 판도라의 매력꽃 향수:티[30일]
					switch (targetItemId) {
					case 20085: // 티셔츠
						PandoraTransform(pc, useItem, targetItem , 22445, EndTime); // 판도라의 매력꽃향 티
						break;						
					case 20084: // 요정족 티셔츠
						PandoraTransform(pc, useItem, targetItem , 22455, EndTime); // 판도라의 매력꽃향 요정족 티
						break;
					default:
						pc.sendPackets(new S_ServerMessage(328));
					}
					break;
				/** 
				 * 천연 비누
				 * 
				 * **/
				case 89530: // 천연 비누:판도라 향기티
					switch (targetItemId) {
					// 일반 티셔츠
					case 22440: 
					case 22441:
					case 22442:
					case 22443:
					case 22444:
					case 22445:
						Soap(pc,useItem, targetItem, 20085);
						break;
					// 요정족 티셔츠
					case 22450:
					case 22451:
					case 22452:
					case 22453:
					case 22454:
					case 22455:
						Soap(pc,useItem, targetItem, 20084);
						break;
					default:
						pc.sendPackets(new S_ServerMessage(328));
					}
					break;
				/** 
				 * 판도라의 문양:티
				 * 
				 * **/
				case 89550: // 판도라의 스턴 문양:티
					PatternIn(pc, useItem, targetItem, 1);
					break;
				case 89551: // 판도라의 홀드 문양:티
					PatternIn(pc, useItem, targetItem, 2);
					break;
				case 89552: // 판도라의 회복 문양:티
					PatternIn(pc, useItem, targetItem, 3);
					break;
				case 89553: // 판도라의 강철 문양:티
					PatternIn(pc, useItem, targetItem, 4);
					break;
				case 89554: // 판도라의 멸마 문양:티
					PatternIn(pc, useItem, targetItem, 5);
					break;
				case 89555: // 판도라의 체력 문양:티
					PatternIn(pc, useItem, targetItem, 6);
					break;
				case 89556: // 판도라의 마나 문양:티
					PatternIn(pc, useItem, targetItem, 7);
					break;
				case 89557: // 판도라의 정령 문양:티
					PatternIn(pc, useItem, targetItem, 8);
					break;
				case 89558: // 판도라의 석화 문양:티
					PatternIn(pc, useItem, targetItem, 9);
					break;
				/** 
				 * 변환석 : 스탯티에서 +1 상승하여 일반 티셔츠로 변환.
				 *  
				 * **/
				case 46030: // 변환석: +0 티
					Transform(pc, useItem, targetItem, 0);
					break;
				case 46031: // 변환석: +1 티
					Transform(pc, useItem, targetItem, 1);
					break;
				case 46032: // 변환석: +2 티
					Transform(pc, useItem, targetItem, 2);
					break;
				case 46033: // 변환석: +3 티
					Transform(pc, useItem, targetItem, 3);
					break;
				case 46034: // 변환석: +4 티
					Transform(pc, useItem, targetItem, 4);
					break;
				case 46035: // 변환석: +5 티
					Transform(pc, useItem, targetItem, 5);
					break;
				case 46036: // 변환석: +6 티
					Transform(pc, useItem, targetItem, 6);
					break;
				case 46037: // 변환석: +7 티
					Transform(pc, useItem, targetItem, 7);
					break;
				case 46038: // 변환석: +8 티
					Transform(pc, useItem, targetItem, 8);
					break;
				case 46039: // 변환석: +9 티
					Transform(pc, useItem, targetItem, 9);
					break;
				case 46040: // 변환석: +10 티
					Transform(pc, useItem, targetItem, 10);
					break;
				case 46041: // 변환석: +11 티
					Transform(pc, useItem, targetItem, 11);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 스탯티 생성
	private boolean createNewItemR(L1PcInstance pc, int item_id, int count, int enchant, int EndTime) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		item.setCount(count);
		Timestamp deleteTime = null;
		deleteTime = new Timestamp(System.currentTimeMillis() + EndTime);//
		item.setEndTime(deleteTime);
		item.setIdentified(true);
		item.setEnchantLevel(enchant);
		if (item != null) {
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
				pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
			} else {
				L1World.getInstance().getInventory(pc.getX(), pc.getY(),pc.getMapId()).storeItem(item);
			}
			return true;
		} else {
			return false;
		}
	}
	
	// 일반티 생성
	private boolean createNewItemR(L1PcInstance pc, int item_id, int count, int enchant) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		item.setCount(count);
		item.setIdentified(true);
		item.setEnchantLevel(enchant);
		if (item != null) {
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
				pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
			} else {
				L1World.getInstance().getInventory(pc.getX(), pc.getY(),pc.getMapId()).storeItem(item);
			}
			return true;
		} else {
			return false;
		}
	}
	
	// 스탯티로 변환
	private void PandoraTransform(L1PcInstance pc, L1ItemInstance useItem, L1ItemInstance targetItem, int itemid, int EndTime){
		createNewItemR(pc, itemid,1, targetItem.getEnchantLevel() ,EndTime);
		pc.getInventory().removeItem(useItem, 1);
		pc.getInventory().removeItem(targetItem, 1);
	}
	
	// 비누
	private void Soap(L1PcInstance pc, L1ItemInstance useItem, L1ItemInstance targetItem, int itemid){
		if (targetItem.isEquipped()){
			pc.sendPackets(new S_ServerMessage(1317));
			return;
		}
		createNewItemR(pc, itemid, 1, targetItem.getEnchantLevel());
		pc.getInventory().removeItem(useItem, 1);
		pc.getInventory().removeItem(targetItem, 1);
		pc.saveInventory();
	}
	
	// 문양
	private void PatternIn(L1PcInstance pc, L1ItemInstance useItem, L1ItemInstance targetItem, int pandoraset){
		int targetItemId = targetItem.getItem().getItemId();
		if (targetItem.getPandoraT() == pandoraset) {
			pc.sendPackets(new S_SystemMessage("이미 같은 문양이 부여되어 있습니다."));
			return;
		}
		if (targetItem.isEquipped()){
			pc.sendPackets(new S_ServerMessage(1317));
			return;
		}
		if (targetItemId >= 22440 && targetItemId <= 22445 || targetItemId >= 22405 && targetItemId <= 22455){ // 판도라 티셔츠
			targetItem.setPandoraT(pandoraset);
			pc.getInventory().updateItem(targetItem, L1PcInventory.COL_PANDORA);
			pc.getInventory().saveItem(targetItem, L1PcInventory.COL_PANDORA);
			pc.getInventory().removeItem(useItem, 1);
			pc.saveInventory();
		} else {
			pc.sendPackets(new S_ServerMessage(328));
		}
	}
	
	// 변환석
	private void Transform(L1PcInstance pc, L1ItemInstance useItem, L1ItemInstance targetItem, int enchant_check){
		int itemid = targetItem.getItem().getItemId();
		if (itemid >= 21028 && itemid <= 21033 || itemid >= 490000 && itemid <= 490017){ // 기존 스탯티
			if (targetItem.getEnchantLevel() == enchant_check){
				createNewItemR(pc, 20085, 1, targetItem.getEnchantLevel() + 1);
				pc.getInventory().removeItem(useItem, 1);
				pc.getInventory().removeItem(targetItem, 1);
			} else {
				pc.sendPackets(new S_ServerMessage(328));
			}
		} else {
			pc.sendPackets(new S_ServerMessage(328));
		}
	}
}