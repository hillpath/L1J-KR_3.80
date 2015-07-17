package l1j.server.GameSystem.Lastabard;

import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.Dead;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;

public class LastabardDead extends Dead {
	private int doorId;

	private int countMapId;

	private int locX;

	private int locY;

	public LastabardDead(L1MonsterInstance mob, int locX, int locY, int doorId,
			int countMapId) {
		super(mob, null);
		setDoorId(doorId);
		setCountMapId(countMapId);
		setLocX(locX);
		setLocY(locY);
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

	@Override
	public void doSomething() {
		try{
		L1MonsterInstance mob = getDeadMob();
		int mobMapId = mob.getNpcTemplate().getMapId();
		int doorId = getDoorId();
		int countMapId = getCountMapId();
		
		if(LastabardData.isFourthFloor(mobMapId)) { // ¶ó´ø 4Ãþ
			int pos = LastabardData.getPos(mobMapId, getLocX(), getLocY());
			LastabardController.getInstance().die(mobMapId, pos);
			if(LastabardController.getInstance().getMobCount(mobMapId, pos) == 0) {
				openDoor(doorId);
			}
		}
		else if(doorId > 0){ 
			LastabardController.getInstance().addDelayTime(countMapId, doorId);
			openDoor(doorId);
		}
		}catch(Exception e){}
	}
	
	public void openDoor(int doorId) {
		L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(doorId);
		if (door != null) {
			synchronized (this) {
				door.setDead(false);
				door.open();
			}
		}
	}

	public void setLocX(int locX) {
		this.locX = locX;
	}

	public void setLocY(int locY) {
		this.locY = locY;
	}

	public int getLocX() {
		return locX;
	}

	public int getLocY() {
		return locY;
	}
}
