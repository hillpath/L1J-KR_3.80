package server.controller.Npc;

import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastTable;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1NpcInstance;

public class NpcAIController implements Runnable {
	private static Logger _log = Logger.getLogger(NpcAIController.class
			.getName());

	private static NpcAIController _instance;

	private FastTable<L1NpcInstance> list;

	public static NpcAIController getInstance() {
		if (_instance == null)
			_instance = new NpcAIController();
		return _instance;
	}

	public NpcAIController() {
		list = new FastTable<L1NpcInstance>();
		GeneralThreadPool.getInstance().execute(this);
	}

	private FastTable<L1NpcInstance> li = null;

	public void run() {
		while (true) {
			try {
				li = list;
				for (L1NpcInstance npc : li) {

					if (npc == null)
						continue;
					try {
						if (!npc.AiCK) {
							npc.setAiRunning(true);
							if (!npc._destroyed
									&& !npc.isDead()
									&& npc.getCurrentHp() > 0
									&& npc.getHiddenStatus() == npc.HIDDEN_STATUS_NONE) {
								if (npc.AiSleepTime <= System
										.currentTimeMillis()) {
									if (npc.isParalyzed() || npc.isSleeped()) {
										continue;
									}
									if (npc.AIProcess()) {

										npc.AiCK = true;
									} else {
										npc.AiSleepTime = npc.getSleepTime()
												+ System.currentTimeMillis();
										continue;
									}
								} else {
									continue;
								}
							}
						} else {

							if (npc.AiSleepTime > System.currentTimeMillis()) {
								continue;
							} else {
								npc.mobSkill.resetAllSkillUseCount();
								if (npc.isDeathProcessing()) {
									npc.AiSleepTime = npc.getSleepTime()
											+ System.currentTimeMillis();
									continue;
								}
							}
						}
						npc.AiCK = false;
						npc.allTargetClear();
						npc.setAiRunning(false);
						removeNpc(npc);
					} catch (Exception e) {
						// System.out.println("NPC AI ¿¡·¯ : "+e);
						npc.AiCK = false;
						npc.allTargetClear();
						npc.setAiRunning(false);
						removeNpc(npc);
					}
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "NpcAIController[]Error", e);

			} finally {
				try {
					li = null;
					Thread.sleep(100);
				} catch (Exception e) {
				}
			}
		}
	}

	public void addNpc(L1NpcInstance npc) {
		if (!list.contains(npc))
			list.add(npc);
	}

	public void removeNpc(L1NpcInstance npc) {
		if (list.contains(npc))
			list.remove(npc);
	}

	public int getSize() {
		return list.size();
	}

}