/* Eva Pack -http://eva.gg.gg
 * ���� ������� ��Ÿ�� ���̵� �ý���
 */

package l1j.server.GameSystem.Rind;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javolution.util.FastTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.L1SpawnUtil;

public class RindRaidSystem {
	private static Logger _log = Logger.getLogger(RindRaidSystem.class
			.getName());

	private static RindRaidSystem _instance;

	private final Map<Integer, RindRaid> _list = new ConcurrentHashMap<Integer, RindRaid>();

	private final FastTable<Integer> _map = new FastTable<Integer>();

	public static RindRaidSystem getInstance() {
		if (_instance == null) {
			_instance = new RindRaidSystem();
		}
		return _instance;
	}

	public RindRaidSystem(){
		for(int i = 1011; i < 1018; i++ ){
			_map.add(i);
		}
	}

	static class RindMsgTimer implements Runnable {
		private int _mapid = 0;

		private int _type = 0;

		public RindMsgTimer(int mapid, int type) {
			_mapid = mapid;
			_type = type;
		}

		public void run() {
			try {
				FastTable<L1PcInstance> roomlist = RindRaidSystem
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
				RindRaidSpawn.getInstance().fillSpawnTable(_mapid, _type);
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
		RindRaid ar = new RindRaid(id);
		L1SpawnUtil.spawn2(pc.getX(), pc.getY(), pc.getMapId(), 4212017, 0,
				7200 * 1000, id);
		_list.put(id, ar);
		
	}

	public RindRaid getAR(int id) {
		return _list.get(id);
	}

	/**
	 * �� �� ���̵� �����´�
	 * 
	 * @return
	 */
	public int blankMapId() {
		int mapid = 0;
			if(_list.size() == 0) return 1017;		
		mapid = 1017 + _list.size();
		return mapid;
	}

	public int countRaidPotal() {
		return _list.size();
	}

	/**
	 * 
	 */
}
