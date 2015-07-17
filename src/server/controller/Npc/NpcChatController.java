package server.controller.Npc;

import javolution.util.FastTable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collection;

import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.GeneralThreadPool;

public class NpcChatController implements Runnable {
	private static Logger _log = Logger.getLogger(NpcChatController.class
			.getName());

	private static NpcChatController _instance;

	private FastTable<L1NpcInstance> list;

	public static NpcChatController getInstance() {
		if (_instance == null)
			_instance = new NpcChatController();
		return _instance;
	}

	public NpcChatController() {
		list = new FastTable<L1NpcInstance>();
		GeneralThreadPool.getInstance().execute(this);
	}

	private FastTable<L1NpcInstance> li = null;

	private int chatTiming = 0;

	private int chatInterval = 0;

	private boolean isShout;

	private boolean isWorldChat;

	private String chatId1 = "";

	private String chatId2 = "";

	private String chatId3 = "";

	private String chatId4 = "";

	private String chatId5 = "";

	private int repeatInterval = 0;

	public void run() {
		while (true) {
			try {
				li = list;
				for (L1NpcInstance _npc : li) {
					if (_npc == null) {
						removeNpcDelete(_npc);
						continue;
					}
					if (_npc.npcChat == null) {
						removeNpcDelete(_npc);
						continue;
					}
					if (_npc.getHiddenStatus() != L1NpcInstance.HIDDEN_STATUS_NONE
							|| _npc._destroyed) {
						removeNpcDelete(_npc);
						continue;
					}

					chatTiming = _npc.npcChat.getChatTiming();
					chatInterval = _npc.npcChat.getChatInterval();
					isShout = _npc.npcChat.isShout();
					isWorldChat = _npc.npcChat.isWorldChat();
					chatId1 = _npc.npcChat.getChatId1();
					chatId2 = _npc.npcChat.getChatId2();
					chatId3 = _npc.npcChat.getChatId3();
					chatId4 = _npc.npcChat.getChatId4();
					chatId5 = _npc.npcChat.getChatId5();
					repeatInterval = _npc.npcChat.getRepeatInterval();

					if (!chatId1.equals("")) {
						if (_npc.npcChatTime == 0 && _npc.npcChatType == 0) {
							chat(_npc, chatTiming, chatId1, isShout,
									isWorldChat);
							_npc.npcChatType = 1;
						} else if (_npc.npcChatType == 5) {
							if (_npc.npcChatTime <= System.currentTimeMillis()) {
								chat(_npc, chatTiming, chatId1, isShout,
										isWorldChat);
								_npc.npcChatTime = System.currentTimeMillis()
										+ repeatInterval;
							} else
								continue;
						}
					}
					if (chatId2.equals("") && !_npc.npcChat.isRepeat()) {
						removeNpcDelete(_npc);
						continue;
					} else if (_npc.npcChat.isRepeat()) {
						if (_npc.npcChatTime == 0) {
							_npc.npcChatType = 5;
							_npc.npcChatTime = System.currentTimeMillis()
									+ repeatInterval;
						}
						continue;
					}
					if (!chatId2.equals("") && _npc.npcChatType == 1) {
						if (_npc.npcChatTime == 0) {
							_npc.npcChatTime = System.currentTimeMillis()
									+ chatInterval;
							continue;
						}
						if (_npc.npcChatTime <= System.currentTimeMillis()) {
							chat(_npc, chatTiming, chatId2, isShout,
									isWorldChat);
							_npc.npcChatTime = System.currentTimeMillis()
									+ chatInterval;
							_npc.npcChatType = 2;
						} else
							continue;
					} else if (chatId2.equals("")) {
						removeNpcDelete(_npc);
						continue;
					}
					if (!chatId3.equals("") && _npc.npcChatType == 2) {
						if (_npc.npcChatTime <= System.currentTimeMillis()) {
							chat(_npc, chatTiming, chatId3, isShout,
									isWorldChat);
							_npc.npcChatTime = System.currentTimeMillis()
									+ chatInterval;
							_npc.npcChatType = 3;
						} else
							continue;
					} else if (chatId3.equals("")) {
						removeNpcDelete(_npc);
						continue;
					}

					if (!chatId4.equals("") && _npc.npcChatType == 3) {
						if (_npc.npcChatTime <= System.currentTimeMillis()) {
							chat(_npc, chatTiming, chatId4, isShout,
									isWorldChat);
							_npc.npcChatTime = System.currentTimeMillis()
									+ chatInterval;
							_npc.npcChatType = 4;
						} else
							continue;
					} else if (chatId4.equals("")) {
						removeNpcDelete(_npc);
						continue;
					}

					if (!chatId5.equals("") && _npc.npcChatType == 4) {
						if (_npc.npcChatTime <= System.currentTimeMillis()) {
							chat(_npc, chatTiming, chatId5, isShout,
									isWorldChat);
							removeNpcDelete(_npc);
						} else
							continue;
					} else if (chatId5.equals("")) {
						removeNpcDelete(_npc);
						continue;
					}
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "NpcChatController[]Error", e);
			} finally {
				try {
					chatTiming = 0;
					chatInterval = 0;
					isShout = false;
					isWorldChat = false;
					chatId1 = "";
					chatId2 = "";
					chatId3 = "";
					chatId4 = "";
					chatId5 = "";
					repeatInterval = 0;
					li = null;
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		}
	}

	public void addNpcDelete(L1NpcInstance npc) {
		if (!list.contains(npc))
			list.add(npc);
	}

	public void removeNpcDelete(L1NpcInstance npc) {
		if (list.contains(npc)) {
			list.remove(npc);
			if (npc != null) {
				npc.npcChatTime = 0;
				npc.npcChatType = 0;
			}
		}
	}

	public int getSize() {
		return list.size();
	}

	private void chat(L1NpcInstance npc, int chatTiming, String chatId,
			boolean isShout, boolean isWorldChat) {
		if (chatTiming == L1NpcInstance.CHAT_TIMING_APPEARANCE && npc.isDead()) {
			return;
		}
		if (chatTiming == L1NpcInstance.CHAT_TIMING_DEAD && !npc.isDead()) {
			return;
		}
		if (chatTiming == L1NpcInstance.CHAT_TIMING_HIDE && npc.isDead()) {
			return;
		}

		if (!isShout) {
			Broadcaster.broadcastPacket(npc,
					new S_NpcChatPacket(npc, chatId, 0));
		} else {
			Broadcaster.wideBroadcastPacket(npc, new S_NpcChatPacket(npc,
					chatId, 2));
		}

		if (isWorldChat) {
			Collection<L1PcInstance> list = L1World.getInstance()
					.getAllPlayers();
			for (L1PcInstance pc : list) {
				if (pc != null) {
					pc.sendPackets(new S_NpcChatPacket(npc, chatId, 3));
				}
				break;
			}
		}
	}

}