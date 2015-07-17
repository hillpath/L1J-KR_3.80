package server.controller.Doll;

import java.util.Collection;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.model.Broadcaster;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;

public class DollobserverController implements Runnable {
	private static Logger _log = Logger.getLogger(DollobserverController.class
			.getName());

	Random rnd = new Random(System.nanoTime());

	private final int _sendgfxtime;

	public DollobserverController(int sendgfxtime) {
		_sendgfxtime = sendgfxtime;
	}

	public void start() {
		GeneralThreadPool.getInstance().scheduleAtFixedRateLong(
				DollobserverController.this, 0, _sendgfxtime);
	}

	private Collection<L1PcInstance> _list = null;

	public void run() {
		try {
			_list = L1World.getInstance().getAllPlayers();
			for (L1PcInstance _client : _list) {
				if (_client == null) {
					continue;
				} else {
					for (L1DollInstance doll : _client.getDollList().values()) {
						_client.sendPackets(new S_DoActionGFX(doll.getId(),
								66 + rnd.nextInt(2)));
						Broadcaster.broadcastPacket(_client, new S_DoActionGFX(
								doll.getId(), 66 + rnd.nextInt(2)));
					}
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "DollobserverController[]Error", e);

		} finally {
			_list = null;
		}
	}
}
