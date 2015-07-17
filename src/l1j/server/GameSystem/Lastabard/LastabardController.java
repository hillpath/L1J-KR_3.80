package l1j.server.GameSystem.Lastabard;

import javolution.util.FastTable;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.gametime.RealTime;

public class LastabardController {
	private static Logger _log = Logger.getLogger(LastabardController.class
			.getName());

	private FastTable<LastabardTime> timeList = new FastTable<LastabardTime>();

	private volatile RealTime currentTime = new RealTime();

	private static LastabardController _instance;

	private RealTime _previousTime = null;

	private int[][] fourthFloor = new int[2][3]; // ��� 4�� �ι�° ��, �׹�° ��

	public static LastabardController getInstance() {
		if (_instance == null) {
			synchronized(LastabardController.class) {
				if (_instance == null) {
					_instance = new LastabardController();
				}
			}
		}

		return _instance;
	}

	public static void start() {
		getInstance().init();
		LastabardSpawnTable.getInstance().Init();
	}

	private void init() {
		GeneralThreadPool.getInstance().execute(new TimeUpdater());
	}

	private class TimeUpdater implements Runnable {

		public void run() {
			while (true) {
				_previousTime = null;
				_previousTime = currentTime;
				currentTime = new RealTime();

				checkTimes();

				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					_log.log(Level.SEVERE, "LastabardController error occurred", e);
				}
			}
		}
	}

	private class LastabardTime {
		private int mapId;

		private int relatedDoor;

		private int deadline;

		public boolean isTimeOver(int currentTime) {
			if (deadline < currentTime)
				return true;
			else
				return false;
		}

		@SuppressWarnings("unused")
		private LastabardTime() {
		};

		public LastabardTime(int mapId, int startTime, int delayTime,
				int relatedDoor) {
			this.mapId = mapId;
			this.deadline = startTime + delayTime;
			this.relatedDoor = relatedDoor;
		}

		public int getMapId() {
			return mapId;
		}

		public int getDeadline() {
			return deadline;
		}

		public int getRelatedDoor() {
			return relatedDoor;
		}
	}

	private boolean isFieldChanged(int field) {
		return _previousTime.get(field) != currentTime.get(field);
	}

	private void checkTimes() {
		if (isFieldChanged(Calendar.SECOND)) {
			if(timeList.size() == 0 || timeList == null){
				return;
			}
			for(LastabardTime time : timeList) {
				if(time.isTimeOver(getRealTime().getSeconds())) {
					// �ش� ���� �ð��� ����, �ش� ���� ���� �����ϰ� �ش� ������ �����ϱ� ���� ����ϴ� ���� ����
					reset(time.getMapId(), time.getRelatedDoor());
					timeList.remove(time);
				}
			}
		}
	}

	private void reset(int mapId, int relatedDoor) {
		if (mapId != 0) {
			LastabardData.doTeleport(mapId);
			LastabardSpawnTable.getInstance().spawnMobs(mapId);
		}

		if (relatedDoor == 0)
			return;
		L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(relatedDoor);
		if (door != null) {
			door.setDead(false); // ������ ����?
			door.close();
		}
	}

	private RealTime getRealTime() {
		return currentTime;
	}

	public void addDelayTime(int mapId, int doorId) {
		try{
		if(timeList.size() == 0 || timeList == null){
			return;
		}
		for(LastabardTime time : timeList) {
			if(time.getMapId() == mapId && mapId != 0) return;
		}
		
		int delayTime = LastabardData.getDelayTime(mapId);
		if(delayTime <= 0) return;
		
		timeList.add(new LastabardTime(mapId, getRealTime().getSeconds(), delayTime, doorId));
		
		additionalDelayTime(mapId);
		}catch(Exception e){}
	}

	private void additionalDelayTime(int mapId) {
		if (mapId == 0)
			return;
		int mapid = LastabardData.relatedTime(mapId);
		if (mapid != 0)
			addDelayTime(mapid, 0);
	}

	public int getMobCount(int mobMapId, int room) {
		if (room > 4 || room < 0)
			return 0;
		int pos = LastabardData.getPosInMapId(mobMapId);
		if (pos < 0 || pos > 1)
			return -1;
		return fourthFloor[pos][room];
	}

	public synchronized void die(int mobMapId, int room) {
		if (room > 4 || room < 0)
			return;
		int pos = LastabardData.getPosInMapId(mobMapId);
		if (pos < 0 || pos > 1)
			return;
		fourthFloor[pos][room]--;
	}

	public synchronized void alive(int mobMapId, int room) {
		if (room > 4 || room < 0)
			return;
		int pos = LastabardData.getPosInMapId(mobMapId);
		if (pos < 0 || pos > 1)
			return;
		fourthFloor[pos][room]++;
	}
}