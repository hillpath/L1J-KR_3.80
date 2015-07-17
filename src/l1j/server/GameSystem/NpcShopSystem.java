package l1j.server.GameSystem;

import javolution.util.FastTable;

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.NpcShopSpawnTable;
import l1j.server.server.datatables.NpcShopTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1NpcShopInstance;
import l1j.server.server.model.gametime.BaseTime;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.gametime.TimeListener;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.templates.L1NpcShop;

public class NpcShopSystem implements TimeListener {

	private static NpcShopSystem _instance;

	private boolean _power = false;

	// private int Count = 0;
	// private final int Time = 2;

	public static NpcShopSystem getInstance() {
		if (_instance == null) {
			_instance = new NpcShopSystem();
			RealTimeClock.getInstance().addListener(_instance);
		}
		return _instance;
	}

	static class NpcShopTimer implements Runnable {

		public NpcShopTimer() {
		}

		public void run() {
			try {
				FastTable<L1NpcShop> list = NpcShopSpawnTable.getInstance()
						.getList();
				for (int i = 0; i < list.size(); i++) {

					L1NpcShop shop = list.get(i);

					L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(
							shop.getNpcId());
					npc.setId(ObjectIdFactory.getInstance().nextId());
					npc.setMap(shop.getMapId());

					npc.getLocation().set(shop.getX(), shop.getY(),
							shop.getMapId());
					npc.getLocation().forward(5);

					npc.setHomeX(npc.getX());
					npc.setHomeY(npc.getY());
					npc.getMoveState().setHeading(shop.getHeading());

					npc.setName(shop.getName());
					npc.setTitle(shop.getTitle());

					L1NpcShopInstance obj = (L1NpcShopInstance) npc;

					obj.setShopName(shop.getShopName());

					L1World.getInstance().storeObject(npc);
					L1World.getInstance().addVisibleObject(npc);

					npc.getLight().turnOnOffLight();

					Thread.sleep(30);

					obj.setState(1);
					Broadcaster.broadcastPacket(npc, new S_DoActionShop(npc
							.getId(), ActionCodes.ACTION_Shop, shop
							.getShopName().getBytes()));

					Thread.sleep(10);
				}
				list.clear();

			} catch (Exception exception) {
				return;
			}
		}
	}

	public void onMonthChanged(BaseTime time) {
	}

	public void onDayChanged(BaseTime time) {
	}

	public void onHourChanged(BaseTime time) {
		if (isPower())
			NpcShopTable.reloding();
	}

	public void onMinuteChanged(BaseTime time) {
	}

	public void npcShopStart() {
		NpcShopTimer ns = new NpcShopTimer();
		GeneralThreadPool.getInstance().execute(ns);
		_power = true;
	}

	public boolean isPower() {
		return _power;
	}
}
