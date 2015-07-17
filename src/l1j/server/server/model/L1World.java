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

import javolution.util.FastTable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javolution.util.FastMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1NpcShopInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MerchantInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.types.Point;

public class L1World {
	private static Logger _log = Logger.getLogger(L1World.class.getName());

	private final FastMap<String, L1PcInstance> _allPlayers;

	private final FastMap<Integer, L1PetInstance> _allPets;

	private final FastMap<Integer, L1SummonInstance> _allSummons;

	private final FastMap<Integer, L1Object> _allObjects;

	private final FastMap<Integer, L1MerchantInstance> _allMerchant;

	private final FastMap<Integer, L1ItemInstance> _allitem;

	private final FastMap<Integer, L1Object>[] _visibleObjects;

	private final CopyOnWriteArrayList<L1War> _allWars;


	private final FastMap<String, L1NpcShopInstance> _allNpcShop;


	private final FastMap<String, L1Clan> _allClans;

	private final FastMap<Integer, L1FieldObjectInstance> _allFieldObject;

	private int _weather = 4;

	private boolean _worldChatEnabled = true;

	private boolean _processingContributionTotal = false;

	private static final int MAX_MAP_ID = 25088;

	private static L1World _instance;

	private L1PetMember _PetMember = null;

	@SuppressWarnings("unchecked")
	private L1World() {
		_allPlayers = new FastMap<String, L1PcInstance>().setShared(true); // 모든
																			// 플레이어
		_allPets = new FastMap<Integer, L1PetInstance>().setShared(true); // 모든
																			// 애완동물
		_allSummons = new FastMap<Integer, L1SummonInstance>().setShared(true); // 모든
																				// 사몬몬스타
		_allMerchant = new FastMap<Integer, L1MerchantInstance>()
				.setShared(true); // 모든 상점NPC
		_allitem = new FastMap<Integer, L1ItemInstance>().setShared(true); // 모든
																			// ItemObject
		_allObjects = new FastMap<Integer, L1Object>().setShared(true); // 모든
																		// 오브젝트(L1ItemInstance
																		// 들어가,
																		// L1Inventory는
																		// 없음).setShared(true)
		_visibleObjects = new FastMap[MAX_MAP_ID + 1]; // MAP 마다의
														// 오브젝트(L1Inventory 들어가,
														// L1ItemInstance는 없음)
		_allWars = new CopyOnWriteArrayList<L1War>(); // 모든 전쟁
		_allClans = new FastMap<String, L1Clan>().setShared(true); // 모든
																	// 크란(Online/Offline
																	// 어느쪽이나)
		_allFieldObject = new FastMap<Integer, L1FieldObjectInstance>()
				.setShared(true); // 모든 필드오브젝트

		_allNpcShop = new FastMap<String, L1NpcShopInstance>().setShared(true);

		for (int i = 0; i <= MAX_MAP_ID; i++) {
			_visibleObjects[i] = new FastMap<Integer, L1Object>()
					.setShared(true);
		}
	}

	public static L1World getInstance() {
		if (_instance == null) {
			_instance = new L1World();
		}
		return _instance;
	}

	/**
	 * 모든 상태를 클리어 한다.<br>
	 * 디버그, 테스트등이 특수한 목적 이외로 호출해서는 안 된다.
	 */
	public void clear() {
		_instance = new L1World();
	}

	public void storeObject(L1Object object) {
		if (object == null) {
			throw new NullPointerException();
		}

		_allObjects.put(object.getId(), object);
		if (object instanceof L1PcInstance){
			_allPlayers.put(((L1PcInstance) object).getName().toUpperCase(),(L1PcInstance) object);
			}


		else if (object instanceof L1NpcShopInstance) {
				_allNpcShop.put(((L1NpcShopInstance) object).getName(),
						(L1NpcShopInstance) object);

			}  else if (object instanceof L1ItemInstance) {
				_allitem.put(object.getId(), (L1ItemInstance) object);
			}  else if (object instanceof L1MerchantInstance) {
				_allMerchant.put(object.getId(), (L1MerchantInstance) object);
			}  else if (object instanceof L1PetInstance) {
				_allPets.put(object.getId(), (L1PetInstance) object);
			}  else if (object instanceof L1SummonInstance) {
				_allSummons.put(object.getId(), (L1SummonInstance) object);
			}  else if (object instanceof L1FieldObjectInstance) {
				_allFieldObject.put(object.getId(),
						(L1FieldObjectInstance) object);
			}
		}
	public void removeObject(L1Object object) {
		if (object == null) {
			throw new NullPointerException();
		}

		_allObjects.remove(object.getId());
		if (object instanceof L1PcInstance){
			_allPlayers.remove(((L1PcInstance) object).getName().toUpperCase());
	     }

		else if (object instanceof L1NpcShopInstance) {
				_allNpcShop.remove(((L1NpcShopInstance) object).getName());

			}  else if (object instanceof L1ItemInstance) {
				_allitem.remove(object.getId());
			} else if (object instanceof L1MerchantInstance) {
				_allMerchant.remove(object.getId());
			} else if (object instanceof L1PetInstance) {
				_allPets.remove(object.getId());
			} else if (object instanceof L1SummonInstance) {
				_allSummons.remove(object.getId());
			} else if (object instanceof L1FieldObjectInstance) {
				_allFieldObject.remove(object.getId());
			}
		}
	public L1Object findObject(int oID) {
		return _allObjects.get(oID);
	}

	// _allObjects의 뷰
	private Collection<L1Object> _allValues;

	public Collection<L1Object> getObject() {
		Collection<L1Object> vs = _allValues;
		return (vs != null) ? vs : (_allValues = Collections
				.unmodifiableCollection(_allObjects.values()));
	}

	public L1GroundInventory getInventory(int x, int y, short map) {
		int inventoryKey = ((x - 30000) * 10000 + (y - 30000)) * -1; // xy의
		// 마이너스치를
		// 인벤트리
		// 키로서
		// 사용

		Object object = _visibleObjects[map].get(inventoryKey);
		if (object == null) {
			return new L1GroundInventory(inventoryKey, x, y, map);
		} else {
			return (L1GroundInventory) object;
		}
	}

	public L1GroundInventory getInventory(L1Location loc) {
		return getInventory(loc.getX(), loc.getY(), (short) loc.getMap()
				.getId());
	}

	public void addVisibleObject(L1Object object) {
		if (object == null)
			return;
		if (object.getMapId() <= MAX_MAP_ID) {
			_visibleObjects[object.getMapId()].put(object.getId(), object);
		}
	}

	public void removeVisibleObject(L1Object object) {
		if (object == null)
			return;
		if (object.getMapId() <= MAX_MAP_ID) {
			_visibleObjects[object.getMapId()].remove(object.getId());
		}
	}

	public void moveVisibleObject(L1Object object, int newMap) // set_Map로 새로운
	// Map로 하기 전에
	// 부르는 것
	{
		if (object == null)
			return;
		if (object.getMapId() != newMap) {
			if (object.getMapId() <= MAX_MAP_ID) {
				_visibleObjects[object.getMapId()].remove(object.getId());
			}
			if (newMap <= MAX_MAP_ID) {
				_visibleObjects[newMap].put(object.getId(), object);
			}
		}
	}

	private FastMap<Integer, Integer> createLineMap(Point src, Point target) {
		FastMap<Integer, Integer> lineMap = new FastMap<Integer, Integer>()
				.setShared(true);

		/*
		 * http://www2.starcat.ne.jp/~fussy/algo/algo1-1.htm보다
		 */
		int E;
		int x;
		int y;
		int key;
		int i;
		int x0 = src.getX();
		int y0 = src.getY();
		int x1 = target.getX();
		int y1 = target.getY();
		int sx = (x1 > x0) ? 1 : -1;
		int dx = (x1 > x0) ? x1 - x0 : x0 - x1;
		int sy = (y1 > y0) ? 1 : -1;
		int dy = (y1 > y0) ? y1 - y0 : y0 - y1;

		x = x0;
		y = y0;
		/* 기울기가 1 이하의 경우 */
		if (dx >= dy) {
			E = -dx;
			for (i = 0; i <= dx; i++) {
				key = (x << 16) + y;
				lineMap.put(key, key);
				x += sx;
				E += 2 * dy;
				if (E >= 0) {
					y += sy;
					E -= 2 * dx;
				}
			}
			/* 기울기가 1보다 큰 경우 */
		} else {
			E = -dy;
			for (i = 0; i <= dy; i++) {
				key = (x << 16) + y;
				lineMap.put(key, key);
				y += sy;
				E += 2 * dx;
				if (E >= 0) {
					x += sx;
					E -= 2 * dy;
				}
			}
		}

		return lineMap;
	}

	public FastTable<L1Object> getVisibleLineObjects(L1Object src,
			L1Object target) {
		FastMap<Integer, Integer> lineMap = createLineMap(src.getLocation(),
				target.getLocation()).setShared(true);

		int map = target.getMapId();
		FastTable<L1Object> result = new FastTable<L1Object>();

		if (map <= MAX_MAP_ID) {
			Collection<L1Object> df;
			df = _visibleObjects[map].values();
			for (L1Object element : df) {
				if (element == null || element.equals(src)) {
					continue;
				}

				int key = (element.getX() << 16) + element.getY();
				if (lineMap.containsKey(key)) {
					result.add(element);
				}
			}
		}

		return result;
	}

	public FastTable<L1Object> getVisibleBoxObjects(L1Object object,
			int heading, int width, int height) {
		int x = object.getX();
		int y = object.getY();
		int map = object.getMapId();
		L1Location location = object.getLocation();
		FastTable<L1Object> result = new FastTable<L1Object>();
		int headingRotate[] = { 6, 7, 0, 1, 2, 3, 4, 5 };
		double cosSita = Math.cos(headingRotate[heading] * Math.PI / 4);
		double sinSita = Math.sin(headingRotate[heading] * Math.PI / 4);

		if (map <= MAX_MAP_ID) {
			Collection<L1Object> df = null;
			df = _visibleObjects[map].values();
			for (L1Object element : df) {
				if (element == null || element.equals(object)) {
					continue;
				}
				if (map != element.getMapId()) {
					continue;
				}
				if (location.isSamePoint(element.getLocation())) {
					result.add(element);
					continue;
				}
				int distance = location.getTileLineDistance(element
						.getLocation());
				// 직선 거리가 높이, 폭어느 쪽보다 큰 경우, 계산할 것도 없이 범위외
				if (distance > height && distance > width) {
					continue;
				}

				// object의 위치를 원점과하기 위한 좌표 보정
				int x1 = element.getX() - x;
				int y1 = element.getY() - y;

				// Z축회전시키고 각도를 0번으로 한다.
				int rotX = (int) Math.round(x1 * cosSita + y1 * sinSita);
				int rotY = (int) Math.round(-x1 * sinSita + y1 * cosSita);

				int xmin = 0;
				int xmax = height;
				int ymin = -width;
				int ymax = width;

				// 깊이가 사정과 맞물리지 않기 때문에 직선 거리로 판정하도록(듯이) 변경.
				// if (rotX > xmin && rotX <= xmax && rotY >= ymin && rotY <=
				// ymax) {
				if (rotX > xmin && distance <= xmax && rotY >= ymin
						&& rotY <= ymax) {
					result.add(element);
				}
			}
		}

		return result;
	}

	public FastTable<L1Object> getVisibleObjects(L1Object object) {
		return getVisibleObjects(object, -1);
	}

	public FastTable<L1Object> getVisibleObjects(L1Object object, int radius) {

		L1Map map = object.getMap();
		Point pt = object.getLocation();
		FastTable<L1Object> result = new FastTable<L1Object>();
		if (map.getId() <= MAX_MAP_ID) {
			Collection<L1Object> df = null;
			df = _visibleObjects[map.getId()].values();
			for (L1Object element : df) {
				if (element == null || element.equals(object)) {
					continue;
				}
				if (map != element.getMap()) {
					continue;
				}

				if (radius == -1) {
					if (pt.isInScreen(element.getLocation())) {
						result.add(element);
					}
				} else if (radius == 0) {
					if (pt.isSamePoint(element.getLocation())) {
						result.add(element);
					}
				} else {
					if (pt.getTileLineDistance(element.getLocation()) <= radius) {
						result.add(element);
					}
				}
			}
		}

		return result;
	}

	public FastTable<L1Object> getVisiblePoint(L1Location loc, int radius) {
		FastTable<L1Object> result = new FastTable<L1Object>();
		int mapId = loc.getMapId(); // 루프내에서 부르면(자) 무겁기 때문에

		if (mapId <= MAX_MAP_ID) {
			Collection<L1Object> df = null;
			df = _visibleObjects[mapId].values();
			for (L1Object element : df) {
				if (element == null || mapId != element.getMapId()) {
					continue;
				}

				if (loc.getTileLineDistance(element.getLocation()) <= radius) {
					result.add(element);
				}
			}
		}

		return result;
	}

	public FastTable<L1PcInstance> getVisiblePlayer(L1Object object) {
		return getVisiblePlayer(object, -1);
	}

	public FastTable<L1PcInstance> getVisiblePlayer(L1Object object, int radius) {
		int map = object.getMapId();
		Point pt = object.getLocation();
		FastTable<L1PcInstance> result = new FastTable<L1PcInstance>();

		Collection<L1PcInstance> pc = null;
		pc = _allPlayers.values();
		for (L1PcInstance element : pc) {
			if (element == null || element.equals(object)
					|| map != element.getMapId())
				continue;

			if (radius == -1) {
				if (pt.isInScreen(element.getLocation())) {
					result.add(element);
				}
			} else if (radius == 0) {
				if (pt.isSamePoint(element.getLocation())) {
					result.add(element);
				}
			} else {
				if (pt.getTileLineDistance(element.getLocation()) <= radius) {
					result.add(element);
				}
			}
		}
		return result;
	}

	public FastTable<L1PcInstance> getVisiblePlayerExceptTargetSight(
			L1Object object, L1Object target) {
		int map = object.getMapId();
		Point objectPt = object.getLocation();
		Point targetPt = target.getLocation();
		FastTable<L1PcInstance> result = new FastTable<L1PcInstance>();

		Collection<L1PcInstance> pc = null;
		pc = _allPlayers.values();
		for (L1PcInstance element : pc) {
			if (element == null || element.equals(object)) {
				continue;
			}

			if (map != element.getMapId()) {
				continue;
			}

			if (Config.PC_RECOGNIZE_RANGE == -1) {
				if (objectPt.isInScreen(element.getLocation())) {
					if (!targetPt.isInScreen(element.getLocation())) {
						result.add(element);
					}
				}
			} else {
				if (objectPt.getTileLineDistance(element.getLocation()) <= Config.PC_RECOGNIZE_RANGE) {
					if (targetPt.getTileLineDistance(element.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
						result.add(element);
					}
				}
			}
		}
		return result;
	}

	/**
	 * object를 인식할 수 있는 범위에 있는 플레이어를 취득한다
	 * 
	 * @param object
	 * @return
	 */
	public FastTable<L1PcInstance> getRecognizePlayer(L1Object object) {
		return getVisiblePlayer(object, Config.PC_RECOGNIZE_RANGE);
	}

	public L1PcInstance[] getAllPlayersToArray() {
		return _allPlayers.values().toArray(
				new L1PcInstance[_allPlayers.size()]);
	}

	private Collection<L1PcInstance> _allPlayerValues;

	public Collection<L1PcInstance> getAllPlayers() {
		Collection<L1PcInstance> vs = _allPlayerValues;
		return (vs != null) ? vs : (_allPlayerValues = Collections
				.unmodifiableCollection(_allPlayers.values()));
	}

	/**
	 * 월드내에 있는 지정된 이름의 플레이어를 취득한다.
	 * 
	 * @param name -
	 *            플레이어명(소문자·대문자는 무시된다)
	 * @return 지정된 이름의 L1PcInstance. 해당 플레이어가 존재하지 않는 경우는 null를 돌려준다.
	 */
	public L1PcInstance getPlayer(String name) {
		  return _allPlayers.get(name.toUpperCase());
	}

	private Collection<L1NpcShopInstance> _allNpcShopValues;

	public Collection<L1NpcShopInstance> getAllNpcShop() {
		Collection<L1NpcShopInstance> vs = _allNpcShopValues;
		return (vs != null) ? vs : (_allNpcShopValues = Collections
				.unmodifiableCollection(_allNpcShop.values()));
	}

	public boolean getNpcShop(String name) {
		Collection<L1NpcShopInstance> pc = null;
		pc = getAllNpcShop();
		for (L1NpcShopInstance each : pc) {
			if (each == null)
				continue;
			if (each.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	// _allPets의 뷰
	private Collection<L1PetInstance> _allPetValues;

	public Collection<L1PetInstance> getAllPets() {
		Collection<L1PetInstance> vs = _allPetValues;
		return (vs != null) ? vs : (_allPetValues = Collections
				.unmodifiableCollection(_allPets.values()));
	}

	// _allSummons의 뷰
	private Collection<L1SummonInstance> _allSummonValues;

	public Collection<L1SummonInstance> getAllSummons() {
		Collection<L1SummonInstance> vs = _allSummonValues;
		return (vs != null) ? vs : (_allSummonValues = Collections
				.unmodifiableCollection(_allSummons.values()));
	}

	// _allMerchant의 뷰
	private Collection<L1MerchantInstance> _allMerchantValues;

	public Collection<L1MerchantInstance> getAllMerchant() {
		Collection<L1MerchantInstance> vs = _allMerchantValues;
		return (vs != null) ? vs : (_allMerchantValues = Collections
				.unmodifiableCollection(_allMerchant.values()));
	}

	// _allFieldObject의 뷰
	private Collection<L1FieldObjectInstance> _allFieldObjectValues;

	public Collection<L1FieldObjectInstance> getAllField() {
		Collection<L1FieldObjectInstance> vs = _allFieldObjectValues;
		return (vs != null) ? vs : (_allFieldObjectValues = Collections
				.unmodifiableCollection(_allFieldObject.values()));
	}

	// _allItem의 뷰
	private Collection<L1ItemInstance> _allItemValues;

	public Collection<L1ItemInstance> getAllItem() {
		Collection<L1ItemInstance> vs = _allItemValues;
		return (vs != null) ? vs : (_allItemValues = Collections
				.unmodifiableCollection(_allitem.values()));
	}

	public final Map<Integer, L1Object> getAllVisibleObjects() {
		return _allObjects;
	}

	public final Map<Integer, L1Object>[] getVisibleObjects() {
		return _visibleObjects;
	}

	public final Map<Integer, L1Object> getVisibleObjects(int mapId) {
		return _visibleObjects[mapId];
	}

	public Object getRegion(Object object) {
		return null;
	}

	public void addWar(L1War war) {
		if (!_allWars.contains(war)) {
			_allWars.add(war);
		}
	}

	public void removeWar(L1War war) {
		if (_allWars.contains(war)) {
			_allWars.remove(war);
		}
	}

	// _allWars의 뷰
	private List<L1War> _allWarList;

	public List<L1War> getWarList() {
		List<L1War> vs = _allWarList;
		return (vs != null) ? vs : (_allWarList = Collections
				.unmodifiableList(_allWars));
	}

	public void storeClan(L1Clan clan) {
		L1Clan temp = getClan(clan.getClanName());
		if (temp == null) {
			_allClans.put(clan.getClanName(), clan);
		}
	}

	public void removeClan(L1Clan clan) {
		L1Clan temp = getClan(clan.getClanName());
		if (temp != null) {
			_allClans.remove(clan.getClanName());
		}
	}

	public L1Clan getClan(String clan_name) {
		return _allClans.get(clan_name);
	}

	// _allClans의 뷰
	private Collection<L1Clan> _allClanValues;

	public Collection<L1Clan> getAllClans() {
		Collection<L1Clan> vs = _allClanValues;
		return (vs != null) ? vs : (_allClanValues = Collections
				.unmodifiableCollection(_allClans.values()));
	}

	public void setWeather(int weather) {
		_weather = weather;
	}

	public int getWeather() {
		return _weather;
	}

	public void set_worldChatElabled(boolean flag) {
		_worldChatEnabled = flag;
	}

	public boolean isWorldChatElabled() {
		return _worldChatEnabled;
	}

	public void setProcessingContributionTotal(boolean flag) {
		_processingContributionTotal = flag;
	}

	public boolean isProcessingContributionTotal() {
		return _processingContributionTotal;
	}

	public L1PetMember getPetMember() {
		return _PetMember;
	}

	public void setPetMember(L1PetMember pm) {
		_PetMember = pm;
	}

	/**
	 * 월드상에 존재하는 모든 플레이어에 패킷을 송신한다.
	 * 
	 * @param packet
	 *            송신하는 패킷을 나타내는 ServerBasePacket 오브젝트.
	 */
	public void broadcastPacketToAll(ServerBasePacket packet) {
		_log.finest("players to notify : " + getAllPlayers().size());
		Collection<L1PcInstance> pclist = null;
		pclist = getAllPlayers();
		for (L1PcInstance pc : pclist) {
			if (pc != null)
				pc.sendPackets(packet);
		}
	}

	/**
	 * 월드상에 존재하는 모든 플레이어에 서버 메세지를 송신한다.
	 * 
	 * @param message
	 *            송신하는 메세지
	 */
	public void broadcastServerMessage(String message) {
		broadcastPacketToAll(new S_SystemMessage(message));
	}

	/*
	 * public L1FieldObjectInstance findFieldObject(int id) { return
	 * _allFieldObject.get(id); }
	 */

}