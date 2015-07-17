package l1j.server.GameSystem.Lastabard;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.model.L1MobGroupSpawn;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.templates.L1Npc;

public class LastabardSpawn extends L1Spawn {
	private static Logger _log = Logger.getLogger(L1Spawn.class.getName());

	private final L1Npc template;

	private int id; // just to find this in the spawn table

	private String name;

	private String location;

	private int maximumCount;

	private int npcid;

	private int groupId;

	private int locx;

	private int locy;

	private int randomx;

	private int randomy;

	private int locx1;

	private int locy1;

	private int locx2;

	private int locy2;

	private int heading;

	private int minRespawnDelay;

	private int maxRespawnDelay;

	private int movementDistance;

	private int delayInterval;

	private short mapid;

	private boolean rest;

	private boolean spawnUsingThread;

	private boolean respawnScreen;

	private int doorId;

	private int countMapId;

	private static Random _random = new Random(System.nanoTime());

	public LastabardSpawn(L1Npc mobTemplate) throws SecurityException,
			ClassNotFoundException {
		super(mobTemplate);
		template = mobTemplate;
	}

	private int calcRespawnDelay() {
		int respawnDelay = minRespawnDelay * 1000;

		if (delayInterval > 0) {
			respawnDelay += _random.nextInt(delayInterval) * 1000;
		}

		return respawnDelay;
	}

	public void executeSpawnTask(int spawnNumber, int objectId) {
		SpawnTask task = new SpawnTask(spawnNumber, objectId);
		GeneralThreadPool.getInstance().schedule(task, calcRespawnDelay());
	}

	public void init(boolean wannaUsingThread) {
		delayInterval = maxRespawnDelay - minRespawnDelay;
		setSpawnUsingThread(wannaUsingThread);
		int spawnNum = 0;
		L1MonsterInstance mon = null;
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1MonsterInstance) {
				mon = (L1MonsterInstance) object;

				if (mon.getNpcTemplate().get_npcId() == getNpcId()
						&& mon.getMapId() == getMapId()) {
					spawnNum++;
				}
			}
		}

		while (spawnNum < maximumCount) {
			doSpawn(++spawnNum);
		}
	}

	private class SpawnTask implements Runnable {
		private int spawnNumber;

		private int objectId;

		private SpawnTask(final int SPAWN_NUMBER, final int OBJECT_ID) {
			spawnNumber = SPAWN_NUMBER;
			objectId = OBJECT_ID;
		}

		public void run() {
			try{
			doSpawn(spawnNumber, objectId);
		}catch(Exception e){}
	}
	}

	protected void doSpawn(int spawnNumber) {
		doSpawn(spawnNumber, 0);
	}

	protected void doSpawn(int spawnNumber, int objectId) {
		L1MonsterInstance mob = new L1MonsterInstance(template);

		try {
			if (objectId == 0)
				mob.setId(ObjectIdFactory.getInstance().nextId());
			else
				mob.setId(objectId);

			int heading = getHeading();
			if (0 > heading || heading > 7)
				heading = 5;
			mob.getMoveState().setHeading(heading);
			mob.setMap(getMapId());
			mob.setMovementDistance(getMovementDistance());
			mob.setRest(isRest());

			int newlocx, newlocy, tryCount = 0, rangeX, rangeY;
			while (tryCount <= 50) {
				if (isAreaSpawn()) {
					rangeX = getLocX2() - getLocX1();
					rangeY = getLocY2() - getLocY1();

					newlocx = _random.nextInt(rangeX) + getLocX1();
					newlocy = _random.nextInt(rangeY) + getLocY1();

					if (tryCount > 49) {
						newlocx = getLocX();
						newlocy = getLocY();
					}
				} else if (isRandomSpawn()) {
					newlocx = (getLocX() + ((int) (Math.random() * getRandomx()) - (int) (Math
							.random() * getRandomx())));
					newlocy = (getLocY() + ((int) (Math.random() * getRandomy()) - (int) (Math
							.random() * getRandomy())));
				} else {
					newlocx = getLocX();
					newlocy = getLocY();
				}

				mob.setX(newlocx);
				mob.setY(newlocy);
				mob.setHomeX(newlocx);
				mob.setHomeY(newlocy);

				if (mob.getMap().isInMap(mob.getLocation())
						&& mob.getMap().isPassable(mob.getLocation())) {
					if (L1World.getInstance().getVisiblePlayer(mob).size() == 0
							|| isRespawnScreen())
						break;

					SpawnTask task = new SpawnTask(spawnNumber, mob.getId());
					GeneralThreadPool.getInstance().schedule(task, 3000L);
					return;
				}
				tryCount++;
			}
			mob.setSpawn(this);
			mob.setRespawn(isSpawnUsingThread());
			mob.setSpawnNumber(spawnNumber);

			L1World.getInstance().storeObject(mob);
			L1World.getInstance().addVisibleObject(mob);

			if (LastabardData.isFourthFloor(getMapId()))
				LastabardController.getInstance().alive(getMapId(),
						LastabardData.getPos(getMapId(), getLocX(), getLocY()));

			if (mob.getHiddenStatus() == 0)
				mob.onNpcAI();

			if (getGroupId() != 0) {
				L1MobGroupSpawn.getInstance().doSpawn(mob, getGroupId(),
						isRespawnScreen(), true);
			}
			mob.setDeath(new LastabardDead(mob, getLocX(), getLocY(),
					getDoorId(), getCountMapId()));
			mob.getLight().turnOnOffLight();
			mob.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
		} catch (Exception e) {
			System.out.println("[LastabardSpawn - Exception] NpcID: "
					+ mob.getNpcId() + " MapId: " + mob.getMapId());
			_log.log(Level.SEVERE, "LastabardSpawn error occured", e);
		}
	}

	private boolean isAreaSpawn() {
		return getLocX1() != 0 && getLocY1() != 0 && getLocX2() != 0
				&& getLocY2() != 0;
	}

	private boolean isRandomSpawn() {
		return getRandomx() != 0 || getRandomy() != 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int i) {
		id = i;
	}

	public String getName() {
		return name;
	}

	public void setName(String n) {
		name = n;
	}

	public int getNpcId() {
		return npcid;
	}

	public void setNpcid(int id) {
		npcid = id;
	}

	public short getMapId() {
		return mapid;
	}

	public void setMapId(short id) {
		mapid = id;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int i) {
		groupId = i;
	}

	public int getLocX() {
		return locx;
	}

	public int getLocY() {
		return locy;
	}

	public int getLocX1() {
		return locx1;
	}

	public int getLocY1() {
		return locy1;
	}

	public int getLocX2() {
		return locx2;
	}

	public int getLocY2() {
		return locy2;
	}

	public void setLocX(int x) {
		locx = x;
	}

	public void setLocY(int y) {
		locy = y;
	}

	public void setLocX1(int x) {
		locx1 = x;
	}

	public void setLocY1(int y) {
		locy1 = y;
	}

	public void setLocX2(int x) {
		locx2 = x;
	}

	public void setLocY2(int y) {
		locy2 = y;
	}

	public int getHeading() {
		return heading;
	}

	public int getRandomx() {
		return randomx;
	}

	public int getRandomy() {
		return randomy;
	}

	public void setHeading(int h) {
		heading = h;
	}

	public void setRandomx(int rx) {
		randomx = rx;
	}

	public void setRandomy(int ry) {
		randomy = ry;
	}

	public void setRest(boolean flag) {
		rest = flag;
	}

	public boolean isRest() {
		return rest;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String loc) {
		location = loc;
	}

	public int getMinRespawnDelay() {
		return minRespawnDelay;
	}

	public int getMaxRespawnDelay() {
		return maxRespawnDelay;
	}

	public int getAmount() {
		return maximumCount;
	}

	public void setAmount(int amount) {
		maximumCount = amount;
	}

	public void setMinRespawnDelay(int i) {
		minRespawnDelay = i;
	}

	public void setMaxRespawnDelay(int i) {
		maxRespawnDelay = i;
	}

	public int getMovementDistance() {
		return movementDistance;
	}

	public void setMovementDistance(int i) {
		movementDistance = i;
	}

	public boolean isRespawnScreen() {
		return respawnScreen;
	}

	public void setRespawnScreen(boolean flag) {
		respawnScreen = flag;
	}

	public void setSpawnUsingThread(boolean spawnUsingThread) {
		this.spawnUsingThread = spawnUsingThread;
	}

	public boolean isSpawnUsingThread() {
		return spawnUsingThread;
	}

	public void setDoorId(int doorId) {
		this.doorId = doorId;
	}

	public void setCountMapId(int countMapId) {
		this.countMapId = countMapId;
	}

	public int getDoorId() {
		return this.doorId;
	}

	public int getCountMapId() {
		return this.countMapId;
	}
}