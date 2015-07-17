package server.controller.pc;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_SystemMessage;

public class ItemEndTimeCheckController extends Thread {
	private static ItemEndTimeCheckController _instance = null;

	private ItemEndTimeCheckController() {
	}

	public static ItemEndTimeCheckController getInstance() {
		if (_instance == null) {
			_instance = new ItemEndTimeCheckController();
		}
		return _instance;
	}

	public void run() {
		//System.out.println(ItemEndTimeCheckController.class.getName() + " ON");
		while (true) {
			long currentTimeMillis = System.currentTimeMillis();
			try {
				for (L1ItemInstance item : L1World.getInstance().getAllItem()) {
					if (item == null) {
						continue;
					}

					if (item.getEndTime() == null) {
						continue;
					}

					int itemX = item.getX();
					int itemY = item.getY();
					short itemMapId = item.getMapId();

					if (itemMapId == 88 || itemMapId == 98 || itemMapId == 91
							|| itemMapId == 92 || itemMapId == 95) { // 무한대전
						continue;
					}
					if (L1HouseLocation.isInHouse(itemX, itemY, itemMapId)) { // 아지트내
						continue;
					}

					if (itemX == 0 && itemY == 0) { // 지면상의 아이템은 아니고, 누군가의 소유물
						continue;
					}

					if (currentTimeMillis > item.getEndTime().getTime()) {
						L1Inventory groundInventory = L1World.getInstance()
								.getInventory(itemX, itemY, itemMapId);
						groundInventory.removeItem(item);
						L1World.getInstance().removeVisibleObject(item);
						L1World.getInstance().removeObject(item);
					}
				}

				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					if (pc == null)
						continue;

					L1Inventory pcInventory = pc.getInventory();
					for (L1ItemInstance item : pcInventory.getItems()) {
						if (item == null) {
							continue;
						}

						if (item.getEndTime() == null) {
							continue;
						}

						if (currentTimeMillis > item.getEndTime().getTime()) {
							 int itemId = item.getItem().getItemId();
							   /** 판도라 티셔츠 삭제 */
							   if (itemId >= 22440 && itemId <= 22445){
							   createNewItemR(pc, 20085, 1,  item.getEnchantLevel()); // 일반 티셔츠로 변환
							   pc.sendPackets(new S_SystemMessage(item.getName() + "의 모든 능력이 제거되었습니다."));
							   pc.saveInventory();
							  } else if (itemId >= 22450 && itemId <= 22455){
							   createNewItemR(pc, 20084, 1,  item.getEnchantLevel()); // 요정족 티셔츠로 변환
							   pc.sendPackets(new S_SystemMessage(item.getName() + "의 모든 능력이 제거되었습니다."));
							   pc.saveInventory();
							  }
							switch (item.getItemId()) {
							case L1ItemId.DRAGON_KEY:
								break;
							case 490018:
								break;
							case 500144:
							case 500145:
							case 500146:
								break;
							case 500031:
							case 500032:
							case 500033:
							case 500034:
							case 500035:
							case 500036:
							case 500037:
							case 500038:
								break;
							case 303:
							case 304:
							case 305:
							case 306:
							case 307:
							case 308:
							case 309:
							case 310:
								break;
							case 76767:
							case 76768:
							case 76769:
							case 76770:
							case 76771:
							case 76772:
							case 76773:
							case 76774:
							case 76775:
							case 76776:
							case 76777:
							case 76778:
							case 76779:
							case 76780:
							case 76781:
							case 76782:
							case 76783:
							case 76784:
								break;
							}
							pcInventory.removeItem(item);
							pc.sendPackets(new S_SystemMessage(item.getName()
									+ "의 사용시간이 만료 되었습니다."));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(1000 * 60 * 1); // 1분마다 체크
			} catch (Exception e) {
			}
		}
	}
	 private boolean createNewItemR(L1PcInstance pc, int item_id, int count, int enchant) {
		  L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		  item.setCount(count);
		  item.setIdentified(true);
		  item.setEnchantLevel(enchant);
		  if (item != null) {
		   if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
		    pc.getInventory().storeItem(item);
		    pc.getInventory().updateItem(item, L1PcInventory.COL_BLESS);
		    pc.getInventory().saveItem(item, L1PcInventory.COL_BLESS);
		   } else {
		    L1World.getInstance().getInventory(pc.getX(), pc.getY(),pc.getMapId()).storeItem(item);
		   }
		   return true;
		  } else {
		   return false;
		  }
		 }
	}
