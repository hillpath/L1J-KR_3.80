package server.controller.pc;

import javolution.util.FastTable;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.model.ItemDelayTimer;
import l1j.server.server.GeneralThreadPool;

public class ItemDelayTimerController implements Runnable {
	private static Logger _log = Logger
			.getLogger(ItemDelayTimerController.class.getName());

	private static ItemDelayTimerController _instance;

	private FastTable<ItemDelayTimer> Ownerlist;

	public static ItemDelayTimerController getInstance() {
		if (_instance == null)
			_instance = new ItemDelayTimerController();
		return _instance;
	}

	public ItemDelayTimerController() {
		Ownerlist = new FastTable<ItemDelayTimer>();
		GeneralThreadPool.getInstance().execute(this);
	}

	private FastTable<ItemDelayTimer> li = null;

	public void run() {
		while (true) {
			try {
				li = Ownerlist;
				for (ItemDelayTimer item : li) {
					if (item == null || item.getCha() == null) {
						remove(item);
						continue;
					}
					if (item.ItemDelayTime() <= System.currentTimeMillis()) {
						item.stopDelayTimer();
						remove(item);
					}
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "ItemDelayTimerController[]Error", e);
			} finally {
				try {
					li = null;
					Thread.sleep(250);
				} catch (Exception e) {
				}
			}
		}
	}

	public void add(ItemDelayTimer item) {
		if (!Ownerlist.contains(item))
			Ownerlist.add(item);
	}

	public void remove(ItemDelayTimer item) {
		if (Ownerlist.contains(item))
			Ownerlist.remove(item);
	}

	public int getSize() {
		return Ownerlist.size();
	}

}