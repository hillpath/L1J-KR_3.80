package server.controller;

import javolution.util.FastTable;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.serverpackets.S_ItemStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.GeneralThreadPool;

public class ItemTimerController implements Runnable {
	private static Logger _log = Logger.getLogger(ItemTimerController.class
			.getName());

	private static ItemTimerController _instance;

	private FastTable<L1ItemInstance> Ownerlist;

	private FastTable<L1ItemInstance> Enchantlist;

	private FastTable<L1ItemInstance> Equiplist;

	public static ItemTimerController getInstance() {
		if (_instance == null)
			_instance = new ItemTimerController();
		return _instance;
	}

	public ItemTimerController() {
		Ownerlist = new FastTable<L1ItemInstance>();
		Enchantlist = new FastTable<L1ItemInstance>();
		Equiplist = new FastTable<L1ItemInstance>();
		GeneralThreadPool.getInstance().execute(this);
	}

	private FastTable<L1ItemInstance> li = null;

	public void run() {
		while (true) {
			try {
				li = Ownerlist;
				for (L1ItemInstance item : li) {
					if (item == null || item.getItemOwner() == null) {
						removeOwner(item);
						continue;
					}
					if (item.OwnerTime <= System.currentTimeMillis()) {
						item.setItemOwner(null);
						removeOwner(item);
					}
				}
				li = null;
				li = Enchantlist;

				for (L1ItemInstance item : li) {
					if (item == null) {
						removeEnchant(item);
						continue;
					}
					if (item.EnchantTime <= System.currentTimeMillis()
							|| item._pc == null
							|| item._pc.getOnlineStatus() == 0) {
						if (item._pc != null
								&& item._pc.getInventory().checkItem(
										item.getItem().getItemId())) {
							if (item.getItem().getType() == 2
									&& item.getItem().getType2() == 2
									&& item.isEquipped()) {
								item._pc.getAC().addAc(3);
								item._pc.sendPackets(new S_OwnCharStatus(
										item._pc));
							}
						}
						item.setAcByMagic(0);
						item.setDmgByMagic(0);
						item.setHolyDmgByMagic(0);
						item.setHitByMagic(0);
						if (item._pc != null)
							item._pc.sendPackets(new S_ServerMessage(308, item
									.getLogName()));
						item._isRunning = false;
						item._pc = null;
						removeEnchant(item);
					}
				}
				li = null;
				li = Equiplist;
				for (L1ItemInstance item : li) {
					if (item == null || item.EquipPc == null) {
						removeEquip(item);
						continue;
					}
					item.getLastStatus().updateRemainingTime();
					if ((item.getRemainingTime() - 1) > 0) {
						if (item.EquipPc.getOnlineStatus() == 0) {
							item.stopEquipmentTimer();
						}
						item.setRemainingTime(item.getRemainingTime() - 1);
try {
					 item.EquipPc.sendPackets(new S_ItemStatus(item));
					} catch (Exception e) {
						e.printStackTrace();
					}
					} else {
						item.EquipPc.getInventory().removeItem(item, 1);
						item.cencelEquipmentTimer();
					}
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "ItemTimerController[]Error", e);
			} finally {
				try {
					li = null;
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		}
	}

	public void addOwner(L1ItemInstance item) {
		if (!Ownerlist.contains(item))
			Ownerlist.add(item);
	}

	public void removeOwner(L1ItemInstance item) {
		if (Ownerlist.contains(item))
			Ownerlist.remove(item);
	}

	public void addEnchant(L1ItemInstance item) {
		if (!Enchantlist.contains(item))
			Enchantlist.add(item);
	}

	public void removeEnchant(L1ItemInstance item) {
		if (Enchantlist.contains(item))
			Enchantlist.remove(item);
	}

	public void addEquip(L1ItemInstance item) {
		if (!Equiplist.contains(item))
			Equiplist.add(item);
	}

	public void removeEquip(L1ItemInstance item) {
		if (Equiplist.contains(item))
			Equiplist.remove(item);
	}

	public int getSize() {
		return Ownerlist.size();
	}

}