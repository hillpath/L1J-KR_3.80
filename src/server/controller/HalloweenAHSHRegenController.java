package server.controller;

import javolution.util.FastTable;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.GeneralThreadPool;

public class HalloweenAHSHRegenController implements Runnable {
	private static Logger _log = Logger
			.getLogger(HalloweenAHSHRegenController.class.getName());

	private static HalloweenAHSHRegenController _instance;

	private FastTable<L1PcInstance> SHlist;

	private FastTable<L1PcInstance> AHlist;

	private FastTable<L1PcInstance> HWlist;

	public static HalloweenAHSHRegenController getInstance() {
		if (_instance == null)
			_instance = new HalloweenAHSHRegenController();
		return _instance;
	}

	public HalloweenAHSHRegenController() {
		SHlist = new FastTable<L1PcInstance>();
		AHlist = new FastTable<L1PcInstance>();
		HWlist = new FastTable<L1PcInstance>();
		GeneralThreadPool.getInstance().execute(this);
	}

	private FastTable<L1PcInstance> li = null;

	public void run() {

		while (true) {
			try {

				li = SHlist;
				for (L1PcInstance pc : li) {
					if (pc == null) {
						removeSH(pc);
						continue;
					}
					if (pc.isDead())
						continue;
					if (pc.SHRegenTime <= System.currentTimeMillis()) {
						regenSH(pc);
						pc.SHRegenTime = System.currentTimeMillis() + 1800000;
					}
				}

				li = null;
				li = AHlist;
				for (L1PcInstance pc : li) {
					if (pc == null) {
						removeAH(pc);
						continue;
					}
					if (pc.isDead())
						continue;
					if (pc.AHRegenTime <= System.currentTimeMillis()) {
						regenAH(pc);
						pc.AHRegenTime = System.currentTimeMillis() + 600000;
					}
				}
				li = null;
				li = HWlist;
				for (L1PcInstance pc : li) {
					if (pc == null) {
						removeHW(pc);
						continue;
					}
					if (pc.isDead())
						continue;
					if (pc.HalloweenRegenTime <= System.currentTimeMillis()) {
						regenHW(pc);
						pc.HalloweenRegenTime = System.currentTimeMillis() + 900000;
					}
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "HalloweenAHSHRegenController[]Error", e);
			} finally {
				try {
					li = null;
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		}
	}

	public void addSH(L1PcInstance npc) {
		if (!SHlist.contains(npc))
			SHlist.add(npc);
	}

	public void removeSH(L1PcInstance npc) {
		if (SHlist.contains(npc))
			SHlist.remove(npc);
	}

	public void addAH(L1PcInstance npc) {
		if (!AHlist.contains(npc))
			AHlist.add(npc);
	}

	public void removeAH(L1PcInstance npc) {
		if (AHlist.contains(npc))
			AHlist.remove(npc);
	}

	public void addHW(L1PcInstance npc) {
		if (!HWlist.contains(npc))
			HWlist.add(npc);
	}

	public void removeHW(L1PcInstance npc) {
		if (HWlist.contains(npc))
			HWlist.remove(npc);
	}

	public int getAHSize() {
		return AHlist.size();
	}

	public void regenSH(L1PcInstance _pc) {
		_pc.getInventory().storeItem(L1ItemId.SHINY_LEAF, 1);
		_pc.sendPackets(new S_ServerMessage(403, "$6379"));
	}

	public void regenAH(L1PcInstance _pc) {
		_pc.getInventory().storeItem(L1ItemId.MIRACLE_FRAGMENT, 1);
		_pc.sendPackets(new S_ServerMessage(403, "$6383"));
	}

	public void regenHW(L1PcInstance _pc) {
		_pc.getInventory().storeItem(L1ItemId.HALLOWEEN_PUMPKIN_PIE, 1);
		_pc.sendPackets(new S_ServerMessage(403, "$4324"));
	}

}