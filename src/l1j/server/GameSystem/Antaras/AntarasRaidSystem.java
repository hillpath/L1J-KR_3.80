/* Eva Pack -http://eva.gg.gg
 * ���� ������� ��Ÿ�� ���̵� �ý���
 */

package l1j.server.GameSystem.Antaras;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javolution.util.FastTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.L1SpawnUtil;

public class AntarasRaidSystem {
	private static Logger _log = Logger.getLogger(AntarasRaidSystem.class
			.getName());

	private static AntarasRaidSystem _instance;

	private final Map<Integer, AntarasRaid> _list = new ConcurrentHashMap<Integer, AntarasRaid>();

	private final FastTable<Integer> _map = new FastTable<Integer>();

	public static AntarasRaidSystem getInstance() {
		if (_instance == null) {
			_instance = new AntarasRaidSystem();
		}
		return _instance;
	}

	public AntarasRaidSystem(){
		for(int i = 1005; i < 1016; i++ ){
			_map.add(i);
		}
	}

	static class AntarasMsgTimer implements Runnable {
		private int _mapid = 0;

		private int _type = 0;

		public AntarasMsgTimer(int mapid, int type) {
			_mapid = mapid;
			_type = type;
		}

		public void run() {
			try {
				FastTable<L1PcInstance> roomlist = AntarasRaidSystem
						.getInstance().getAR(_mapid).getRoomList(_type);
				for (int i = 0; i < roomlist.size(); i++) {
					roomlist.get(i).sendPackets(new S_ServerMessage(1588));
				}
				Thread.sleep(2000);
				for (int i = 0; i < roomlist.size(); i++) {
					roomlist.get(i).sendPackets(new S_ServerMessage(1589));
				}
				Thread.sleep(2000);
				for (int i = 0; i < roomlist.size(); i++) {
					roomlist.get(i).sendPackets(new S_ServerMessage(1590));
				}
				Thread.sleep(2000);
				for (int i = 0; i < roomlist.size(); i++) {
					roomlist.get(i).sendPackets(new S_ServerMessage(1591));
				}
				Thread.sleep(10000);
				AntarasRaidSpawn.getInstance().fillSpawnTable(_mapid, _type);
			} catch (Exception exception) {
			}
		}
	}

	/*
	 * 1588 �������� ��ħ : �� �ڸ����� �̰��� ��Ű�� ���� ������ ���Դϴ�. 1589 �������� ��ħ : �ڸ����� ������ �԰� ���
	 * �����ε�, ������ �� ���������� �ſ� ������ ���� ���ϰ� �ֽ��ϴ�. 1590 �������� ��ħ : ���� ��Ÿ���� �Ƚ��� ��Ű�� ����
	 * ����� ���Ľ��� �İ��� ���ϵ� �Դϴ�. 1591 �������� ��ħ : �ƾ�..�׵��� �Խ��ϴ�. ��� �����Ͻñ� �ٶ��ϴ�.!!
	 * 
	 * 1592 �������� ��ħ : �����Ⱑ ��Ÿ�����ϴ�.! �׸� �����ľ� �̰��� ����� �� �ֽ��ϴ�.
	 * 
	 * 1593 �������� ��ħ : ��Ÿ���� ���� ������ ���߰� �� ������ ź�� �Ͽ����ϴ�.!!
	 */

	public void startRaid(L1PcInstance pc) {
		if (pc.getMap().isSafetyZone(pc.getLocation())) {
			return;
		}
		for(L1PcInstance allpc : L1World.getInstance().getAllPlayers()){ 
			   allpc.sendPackets(new S_SystemMessage("Steel dwarf guild: coming ... oh hear the cry of the dragon up here. Hao probably sure that someone opened the portals Dragon! Dragon Slayer prepared to glory and blessing!"));
			  } 
		int id = blankMapId();
	//	if (id != 1005)
		//	L1WorldMap.getInstance().cloneMap(1005, id);
		AntarasRaid ar = new AntarasRaid(id);
		L1SpawnUtil.spawn2(pc.getX(), pc.getY(), pc.getMapId(), 4212015, 0,
				7200 * 1000, id);
		_list.put(id, ar);
	}

	public AntarasRaid getAR(int id) {
		return _list.get(id);
	}

	/**
	 * �� �� ���̵� �����´�
	 * 
	 * @return
	 */
	public int blankMapId() {
		int mapid = 0;
		//if (_list.size() == 0)
		//	return 1005;
		//mapid = _map.get(1);
		//_map.remove(1);
	//	System.out.println("�ʾ��̵�" + mapid);
			if(_list.size() == 0) return 1005;		
		mapid = 1005 + _list.size();
		return mapid;
	}

	public int countRaidPotal() {
		return _list.size();
	}

	/**
	 * 
	 */
}
