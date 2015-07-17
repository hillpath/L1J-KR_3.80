package server.controller;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;

public class ItemDeleteController implements Runnable {
	private static Logger _log = Logger.getLogger(ItemDeleteController.class
			.getName());

	private final int _DeleteTime;

	public ItemDeleteController(int deleteTime) {
		_DeleteTime = deleteTime;
	}

	public void start() {
		GeneralThreadPool.getInstance().scheduleAtFixedRateLong(
				ItemDeleteController.this, 0, _DeleteTime);
	}

	private Collection<L1ItemInstance> objs = null;

	private L1Inventory groundInventory = null;

	private int numOfDeleted = 0;

	public void run() {
		try {
			objs = L1World.getInstance().getAllItem();
			for (L1ItemInstance obj : objs) {
				if (obj == null)
					continue;
				if (obj.getX() == 0 && obj.getY() == 0) { // 지면상의 아이템은 아니고,
															// 누군가의 소유물
					continue;
				}
				if (obj.getItem().getItemId() == 40515) { // 정령의 돌
					continue;
				}
				if (obj.getMapId() == 88 || obj.getMapId() == 98
						|| obj.getMapId() == 91 || obj.getMapId() == 92
						|| obj.getMapId() == 95) { // 무한대전
					continue;
				}
				if (L1HouseLocation.isInHouse(obj.getX(), obj.getY(), obj
						.getMapId())) { // 아지트내
					continue;
				}
				if (obj.get_DeleteItemTime() > 4) {
					groundInventory = L1World.getInstance().getInventory(
							obj.getX(), obj.getY(), obj.getMapId());
					groundInventory.removeItem(obj);
					numOfDeleted++;
				} else {
					obj.add_DeleteItemTime();
				}
			}
			_log.fine("월드 맵상의 아이템을 자동 삭제. 삭제수: " + numOfDeleted);
		} catch (Exception e) {
			_log.log(Level.SEVERE, "ItemDeleteController[]Error", e);
		} finally {
			objs = null;
			numOfDeleted = 0;
			groundInventory = null;
		}
	}
}
