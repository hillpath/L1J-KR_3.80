/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.model;

import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastMap;
import javolution.util.FastTable;
import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.types.Point;

public class L1Spawn {
	private static Logger _log = Logger.getLogger(L1Spawn.class.getName());
	private GeneralThreadPool _threadPool = GeneralThreadPool.getInstance();
	private final L1Npc _template;

	private int _id; // just to find this in the spawn table

	private String _location;

	private int _maximumCount;

	private int _npcid;

	private int _groupId;

	private int _locx;

	private int _locy;

	private int _randomx;

	private int _randomy;

	private int _locx1;

	private int _locy1;

	private int _locx2;

	private int _locy2;

	private int _heading;

	private int _minRespawnDelay;

	private int _maxRespawnDelay;

	private final Constructor<?> _constructor;

	private short _mapid;

	private boolean _respaenScreen;

	private int _movementDistance;

	private boolean _rest;

	private int _spawnType;

	private int _delayInterval;

	private FastMap<Integer, Point> _homePoint = null;

	private boolean _initSpawn = false;

	private boolean _spawnHomePoint;

	private static Random _random = new Random(System.nanoTime());

	private String _name;

	private static final int SPAWN_TYPE_PC_AROUND = 1;

	private static final int PC_AROUND_DISTANCE = 30;

	private class SpawnTask implements Runnable {
		private int _spawnNumber;

		private int _objectId;

		private SpawnTask(int spawnNumber, int objectId) {
			_spawnNumber = spawnNumber;
			_objectId = objectId;
		}

		public void run() {
			doSpawn(_spawnNumber, _objectId);
		}
	}

	public L1Spawn(L1Npc mobTemplate) throws SecurityException,
			ClassNotFoundException {
		_template = mobTemplate;
		String implementationName = _template.getImpl();
		_constructor = Class.forName(
				"l1j.server.server.model.Instance." + implementationName
						+ "Instance").getConstructors()[0];
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public short getMapId() {
		return _mapid;
	}

	public void setMapId(short _mapid) {
		this._mapid = _mapid;
	}

	public boolean isRespawnScreen() {
		return _respaenScreen;
	}

	public void setRespawnScreen(boolean flag) {
		_respaenScreen = flag;
	}

	public int getMovementDistance() {
		return _movementDistance;
	}

	public void setMovementDistance(int i) {
		_movementDistance = i;
	}

	public int getAmount() {
		return _maximumCount;
	}

	public void setAmount(int amount) {
		_maximumCount = amount;
	}

	public int getGroupId() {
		return _groupId;
	}

	public void setGroupId(int i) {
		_groupId = i;
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		_id = id;
	}

	public String getLocation() {
		return _location;
	}

	public void setLocation(String location) {
		_location = location;
	}

	public int getLocX() {
		return _locx;
	}

	public void setLocX(int locx) {
		_locx = locx;
	}

	public int getLocY() {
		return _locy;
	}

	public void setLocY(int locy) {
		_locy = locy;
	}

	public int getNpcId() {
		return _npcid;
	}

	public void setNpcid(int npcid) {
		_npcid = npcid;
	}

	public int getHeading() {
		return _heading;
	}

	public void setHeading(int heading) {
		_heading = heading;
	}

	public int getRandomx() {
		return _randomx;
	}

	public void setRandomx(int randomx) {
		_randomx = randomx;
	}

	public int getRandomy() {
		return _randomy;
	}

	public void setRandomy(int randomy) {
		_randomy = randomy;
	}

	public int getLocX1() {
		return _locx1;
	}

	public void setLocX1(int locx1) {
		_locx1 = locx1;
	}

	public int getLocY1() {
		return _locy1;
	}

	public void setLocY1(int locy1) {
		_locy1 = locy1;
	}

	public int getLocX2() {
		return _locx2;
	}

	public void setLocX2(int locx2) {
		_locx2 = locx2;
	}

	public int getLocY2() {
		return _locy2;
	}

	public void setLocY2(int locy2) {
		_locy2 = locy2;
	}

	public int getMinRespawnDelay() {
		return _minRespawnDelay;
	}

	public void setMinRespawnDelay(int i) {
		_minRespawnDelay = i;
	}

	public int getMaxRespawnDelay() {
		return _maxRespawnDelay;
	}

	public void setMaxRespawnDelay(int i) {
		_maxRespawnDelay = i;
	}

	private int calcRespawnDelay() {
		int respawnDelay = _minRespawnDelay * 1000;
		if (_delayInterval > 0) {
			respawnDelay += _random.nextInt(_delayInterval) * 1000;
		}
		return respawnDelay;
	}

	public void executeSpawnTask(int spawnNumber, int objectId) {
		SpawnTask task = new SpawnTask(spawnNumber, objectId);
		_threadPool.schedule(task, calcRespawnDelay());
	}

	public void init() {
		_delayInterval = _maxRespawnDelay - _minRespawnDelay;
		_initSpawn = true;
		if (Config.SPAWN_HOME_POINT
				&& Config.SPAWN_HOME_POINT_COUNT <= getAmount()
				&& Config.SPAWN_HOME_POINT_DELAY >= getMinRespawnDelay()
				&& isAreaSpawn()) {
			_spawnHomePoint = true;
			_homePoint = new FastMap<Integer, Point>();
		}

		int spawnNum = 0;

		while (spawnNum < _maximumCount) {
			doSpawn(++spawnNum);
		}
		_initSpawn = false;
	}

	protected void doSpawn(int spawnNumber) {
		doSpawn(spawnNumber, 0);
	}

	/**
	 * @param spawnNumber
	 * @param objectId
	 */
	/**
	 * @param spawnNumber
	 * @param objectId
	 */
	/**
	 * @param spawnNumber
	 * @param objectId
	 */
	protected void doSpawn(int spawnNumber, int objectId) {
		L1NpcInstance mob = null;
		try {
			Object parameters[] = { _template };

			int newlocx = getLocX();
			int newlocy = getLocY();
			int tryCount = 0;
			mob = (L1NpcInstance) _constructor.newInstance(parameters);
			if (objectId == 0) {
				mob.setId(ObjectIdFactory.getInstance().nextId());
			} else {
				mob.setId(objectId);
			}

			int heading = 5;
			if (0 <= getHeading() && getHeading() <= 7) {
				heading = getHeading();
			}
			mob.getMoveState().setHeading(heading);

			int npcId = mob.getNpcTemplate().get_npcId();
			if (npcId == 45488 && getMapId() == 9) {
				mob.setMap((short) (getMapId() + _random.nextInt(2)));
			} else if (npcId == 45601 && getMapId() == 11) {
				mob.setMap((short) (getMapId() + _random.nextInt(3)));
			} else {
				mob.setMap(getMapId());
			}
			mob.setMovementDistance(getMovementDistance());
			mob.setRest(isRest());
			FastTable<L1PcInstance> players = null;
			L1PcInstance pc = null;
			L1Location loc = null;
			Point pt = null;

			while (tryCount <= 50) {
				switch (getSpawnType()) {
				case SPAWN_TYPE_PC_AROUND:
					if (!_initSpawn) {
						players = new FastTable<L1PcInstance>();
						for (L1PcInstance _pc : L1World.getInstance()
								.getAllPlayers()) {
							if (getMapId() == _pc.getMapId()) {
								players.add(_pc);
							}
						}
						if (players.size() > 0) {
							pc = players.get(_random.nextInt(players.size()));
							loc = pc.getLocation().randomLocation(
									PC_AROUND_DISTANCE, false);
							newlocx = loc.getX();
							newlocy = loc.getY();
							players.clear();
							break;
						}
					}
				default:
					if (isAreaSpawn()) {
						if (!_initSpawn && _spawnHomePoint) {
							pt = _homePoint.get(spawnNumber);
							loc = new L1Location(pt, getMapId())
									.randomLocation(
											Config.SPAWN_HOME_POINT_RANGE,
											false);
							newlocx = loc.getX();
							newlocy = loc.getY();
						} else {
							int rangeX = getLocX2() - getLocX1();
							int rangeY = getLocY2() - getLocY1();
							newlocx = _random.nextInt(rangeX) + getLocX1();
							newlocy = _random.nextInt(rangeY) + getLocY1();
						}
						if (tryCount > 50) {
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
				}

				mob.setX(newlocx);
				mob.setHomeX(newlocx);
				mob.setY(newlocy);
				mob.setHomeY(newlocy);

				if (mob.getMap().isInMap(mob.getLocation())
						&& mob.getMap().isPassable(mob.getLocation())) {
					if (mob instanceof L1MonsterInstance) {

						if (isRespawnScreen()) {
							break;
						}
						L1MonsterInstance mobtemp = (L1MonsterInstance) mob;
						if (L1World.getInstance().getVisiblePlayer(mobtemp)
								.size() == 0) {
							break;
						}
						SpawnTask task = new SpawnTask(spawnNumber, mob.getId());
						_threadPool.schedule(task, 3000L);
						return;
					}
				}
				tryCount++;
			}

			if (mob instanceof L1MonsterInstance) {
				((L1MonsterInstance) mob).initHide();
			}

			mob.setSpawn(this);
			mob.setRespawn(true);
			mob.setSpawnNumber(spawnNumber);

			if (_initSpawn && _spawnHomePoint) {
				pt = new Point(mob.getX(), mob.getY());
				_homePoint.put(spawnNumber, pt);
			}

			if (mob instanceof L1MonsterInstance) {
				if (mob.getMapId() == 666) {
					((L1MonsterInstance) mob).set_storeDroped(0);
				}
			}

			doCrystalCave(npcId);
			doAntCaveCloseDoor(getId());

			/**
			 * 광역으로 스폰되는 몹중 홈포인트 업는 몹들 몬스터 : 저주받은허수아비, 붉은오크, 버그베어(추석이벤트용)
			 */
			if (mob.getNpcId() == 100000 || mob.getNpcId() == 4035000
					|| mob.getNpcId() == 4030002) {
				_homePoint.remove(spawnNumber);
				_spawnHomePoint = false;
				if (mob.getMap().isSafetyZone(newlocx, newlocy)
						|| L1CastleLocation
								.checkInAllWarArea(mob.getLocation())) {
					return;
				}
			}

			L1World.getInstance().storeObject(mob);
			L1World.getInstance().addVisibleObject(mob);
			if (mob instanceof L1MonsterInstance) {
				L1MonsterInstance mobtemp = (L1MonsterInstance) mob;
				if (!_initSpawn && mobtemp.getHiddenStatus() == 0) {
					mobtemp.onNpcAI();
				}
			}
			if (getGroupId() != 0) {
				L1MobGroupSpawn.getInstance().doSpawn(mob, getGroupId(),
						isRespawnScreen(), _initSpawn);
			}
			mob.getLight().turnOnOffLight();
			mob.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
			/** 특정 보스몹 출현시 멘트 띄우기 */
			//if (mob.getLevel() > 65 && mob.getMaxHp() > 2000) {
			if(getNpcId()==45601 
			 ||getNpcId()==45617 
			 ||getNpcId()==81047 
			 ||getNpcId()==45752 
			 ||getNpcId()==65498 

					
					){
			    String locName = MapsTable.getInstance().getMapName(mob.getMapId());
			    L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, mob.getName()+"이(가) "+locName+" 지역에 등장하였습니다."));
			}
			/** 특정 보스몹 출현시 멘트 띄우기 */
		} catch (Exception e) {
			System.out.println("엔피씨아이디: " + mob.getNpcId());
			_log.log(Level.SEVERE, "L1Spawn[]Error", e);
		}
		mob = null;
	}

	public void setRest(boolean flag) {
		_rest = flag;
	}

	public boolean isRest() {
		return _rest;
	}

	private int getSpawnType() {
		return _spawnType;
	}

	public void setSpawnType(int type) {
		_spawnType = type;
	}

	private boolean isAreaSpawn() {
		return getLocX1() != 0 && getLocY1() != 0 && getLocX2() != 0
				&& getLocY2() != 0;
	}

	private boolean isRandomSpawn() {
		return getRandomx() != 0 || getRandomy() != 0;
	}

	public static void doAntCaveCloseDoor(int spawnId) {
		int[] antEggWareHouse1 = { 7200, 7201, 7202, 7203, 7204, 7205, 7206 };
		int[] antEggWareHouse2 = { 7300, 7301, 7302, 7303, 7304, 7305, 7306 };
		int[] antCave4F_1 = { 7510, 7511, 7512, 7513, 7514, 7515, 7516, 7517 };
		int[] antCave4F_2 = { 7520, 7521, 7522, 7523, 7524, 7525, 7526 };
		int[] antCave4F_3 = { 7530, 7531, 7532, 7533, 7534, 7535, 7536 };
		int[] antCave4F_4 = { 7540, 7541, 7542, 7543, 7544, 7545, 7546 };
		int[] antCave4F_5 = { 7550, 7551, 7552, 7553, 7554, 7555, 7556 };

		switch (spawnId) {
		case 54100001:
			closeDoorCaveArray(antEggWareHouse1);
			break;
		case 54200001:
			closeDoorCaveArray(antEggWareHouse2);
			break;
		case 54300001:
			closeDoorCaveArray(antCave4F_1);
			break;
		case 54300002:
			closeDoorCaveArray(antCave4F_2);
			break;
		case 54300003:
			closeDoorCaveArray(antCave4F_3);
			break;
		case 54300004:
			closeDoorCaveArray(antCave4F_4);
			break;
		case 54300005:
			closeDoorCaveArray(antCave4F_5);
			break;
		default:
			break;
		}
	}

	public static void doCrystalCave(int npcId) {
		switch (npcId) {
		case 9999999:
			closeDoorCave(5000);
			break;
		case 9999998:
			closeDoorCave(5001);
			break;
		case 9999997:
			closeDoorCave(5002);
			break;
		case 9999996:
			closeDoorCave(5003);
			break;
		case 9999995:
			closeDoorCave(5004);//여왕도어
			break;
		case 9999994:
			closeDoorCave(5005);
			break;
		case 9999993:
			closeDoorCave(5006);
			break;
		case 9999992:
			closeDoorCave(5007);
			break;
		case 9999991:
			closeDoorCave(5008);
			break;
		case 9999990:
			closeDoorCave(5009);//데몬도어
			break;
		default:
			break;
		}
	}

	private static void closeDoorCaveArray(int[] doorId) {
		L1DoorInstance door = null;
		for (int i = 0, a = doorId.length; i < a; i++) {
			for (L1Object obj : L1World.getInstance().getObject()) {
				if (obj instanceof L1DoorInstance) {
					door = (L1DoorInstance) obj;
					if (door.getDoorId() == doorId[i]) {
						if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
							door.close();
						}
					}
				}

			}
		}
		door = null;
	}

	private static void closeDoorCave(int doorId) {
		L1DoorInstance door = null;
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1DoorInstance) {
				door = (L1DoorInstance) object;
				if (door.getDoorId() == doorId) {
					door.close();
				}
			}
		}
		door = null;
	}
}
