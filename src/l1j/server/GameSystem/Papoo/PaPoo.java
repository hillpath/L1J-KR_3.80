package l1j.server.GameSystem.Papoo;

import java.lang.reflect.Constructor;
import java.util.Random;

import javolution.util.FastTable;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.templates.L1Npc;

public class PaPoo {
	/*
	 * 1. 스킬쓴다
	 * 
	 */
	public FastTable<String> marble = new FastTable<String>();

	public FastTable<String> marble2 = new FastTable<String>();

	public FastTable<String> tro = new FastTable<String>();

	private L1NpcInstance _attacker = null;

	L1NpcInstance mob;

	Random rnd = new Random();

	int chance = rnd.nextInt(100) + 1;

	int randcount = rnd.nextInt(5) + 1;

	int npcId = mob.getNpcTemplate().get_npcId();

	public void Pa(L1NpcInstance npc) {
		// System.out.println("파 왔음");
		_attacker = npc;
		if (npcId == 4039000) {
			if (chance < 100) {
				// 소환한다 오색구슬 가진 NPC를
				for (int i = 0; i < randcount; i++) {
					mobspawn(4039001, 1);
					mobspawn(4039002, 1);
					mobspawn(4039003, 1);
				}
			}
		}
		// System.out.println("파 끝");
	}

	private void mobspawn(int summonId, int count) {
		int i;

		for (i = 0; i < count; i++) {
			mobspawn(summonId);
		}
	}

	private void mobspawn(int summonId) {
		try {
			L1Npc spawnmonster = NpcTable.getInstance().getTemplate(summonId);
			if (spawnmonster != null) {
				L1NpcInstance mob = null;
				try {
					String implementationName = spawnmonster.getImpl();
					Constructor<?> _constructor = Class.forName(
							(new StringBuilder()).append(
									"l1j.server.server.model.Instance.")
									.append(implementationName).append(
											"Instance").toString())
							.getConstructors()[0];
					mob = (L1NpcInstance) _constructor
							.newInstance(new Object[] { spawnmonster });
					mob.setId(ObjectIdFactory.getInstance().nextId());
					L1Location loc = _attacker.getLocation().randomLocation(8,
							false);
					int heading = rnd.nextInt(8);
					mob.setX(loc.getX());
					mob.setY(loc.getY());
					mob.setHomeX(loc.getX());
					mob.setHomeY(loc.getY());
					short mapid = _attacker.getMapId();
					mob.setMap(mapid);
					mob.getMoveState().setHeading(heading);
					L1World.getInstance().storeObject(mob);
					L1World.getInstance().addVisibleObject(mob);
					L1Object object = L1World.getInstance().findObject(
							mob.getId());
					L1MonsterInstance newnpc = (L1MonsterInstance) object;
					newnpc.set_storeDroped(0);
					newnpc.onNpcAI();
					newnpc.getLight().turnOnOffLight();
					newnpc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
				} catch (Exception e) {

				}
			}
		} catch (Exception e) {
		}
	}

}
