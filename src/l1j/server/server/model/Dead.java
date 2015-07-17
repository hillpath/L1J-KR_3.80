package l1j.server.server.model;

import java.util.Collection;

import l1j.server.GameSystem.CrockSystem;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;

public class Dead /*implements Runnable*/ {
	private L1Character lastAttacker;

	private L1MonsterInstance mob;

	public Dead(L1MonsterInstance mob, L1Character attacker) {
		setAttacker(attacker);
		this.mob = mob;
	}

	protected L1Character getAttacker() {
		return lastAttacker;
	}

	protected L1MonsterInstance getDeadMob() {
		return mob;
	}

	public void setAttacker(L1Character a) {
		lastAttacker = a;
	}

	public void run() {
		try{
		mob.die(lastAttacker);		
		doSomething();		
		calcDamageInCrystalCave();		
		openAntDoor();
		}catch(Exception e){}
	}

	public void doSomething() {

		if (mob.getNpcTemplate().get_npcId() == 400016
				|| mob.getNpcTemplate().get_npcId() == 400017// 테베
				|| mob.getNpcTemplate().get_npcId() == 4036016
				|| mob.getNpcTemplate().get_npcId() == 4036017) {// 티칼
			int dieCount = CrockSystem.getInstance().dieCount();
			if (!CrockSystem.getInstance().isBossTime())
				return;
			switch (dieCount) {
			// 2명의 보스중 한명도 죽이지 않았을때 둘중 하나를 죽였다면 +1
			case 0:
				CrockSystem.getInstance().dieCount(1);
				break;
			// 2명의 보스중 이미 한명이 죽였고. 이제 또한명이 죽으니 2
			case 1:
				CrockSystem.getInstance().dieCount(2);
				break;
			}
		}
	}

	private void openAntDoor() {
		if (mob.getSpawn() != null) {
			switch (mob.getSpawn().getId()) {
			case 54100001:
				openDoorCave(7200);
				break;
			case 54200001:
				openDoorCave(7300);
				break;
			case 54300001:
				openDoorCave(7510);
				openDoorCave(7511);
				break;
			case 54300002:
				openDoorCave(7520);
				break;
			case 54300003:
				openDoorCave(7530);
				break;
			case 54300004:
				openDoorCave(7540);
				break;
			case 54300005:
				openDoorCave(7550);
				break;
			default:
				break;
			}
		}
	}

	private void calcDamageInCrystalCave() {
		switch (mob.getNpcTemplate().get_npcId()) {
		case 9999999:
			openDoorCave(5000);
			break;
		case 9999998:
			openDoorCave(5001);
			break;
		case 9999997:
			openDoorCave(5002);
			break;
		case 9999996:
			openDoorCave(5003);
			break;
		case 9999995:
			openDoorCave(5004);//여왕도어
			break;
		case 9999994:
			openDoorCave(5005);
			break;
		case 9999993:
			openDoorCave(5006);
			break;
		case 9999992:
			openDoorCave(5007);
			break;
		case 9999991:
			openDoorCave(5008);
			break;
		case 9999990:
			openDoorCave(5009);//데몬도어
			break;
		default:
			break;
		}
	}

	private void openDoorCave(int doorId) {
		L1DoorInstance door = null;
		Collection<L1Object> list = L1World.getInstance().getObject();
		for (L1Object object : list) {
			if (object instanceof L1DoorInstance) {
				door = (L1DoorInstance) object;
				if (door.getDoorId() == doorId) {
					door.open();
				}
			}

		}
	}
}
