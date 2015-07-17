package l1j.server.server.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class NearObjects {
	public List<L1Object> knownObjects = new CopyOnWriteArrayList<L1Object>();

	public List<L1PcInstance> knownPlayer = new CopyOnWriteArrayList<L1PcInstance>();
	public List<L1MonsterInstance> knownMonster = new CopyOnWriteArrayList<L1MonsterInstance>();

	/**
	 * 지정된 오브젝트를, 캐릭터가 인식하고 있을까를 돌려준다.
	 * 
	 * @param obj
	 *            조사하는 오브젝트.
	 * @return 오브젝트를 캐릭터가 인식하고 있으면 true, 하고 있지 않으면 false. 자기 자신에 대해서는 false를
	 *         돌려준다.
	 */
	public boolean knownsObject(L1Object obj) {
		return knownObjects.contains(obj);
	}

	/**
	 * 캐릭터가 인식하고 있는 모든 오브젝트를 돌려준다.
	 * 
	 * @return 캐릭터가 인식하고 있는 오브젝트를 나타내는 List<L1Object>.
	 */
	public List<L1Object> getKnownObjects() {
		return knownObjects;
	}

	/**
	 * 캐릭터가 인식하고 있는 모든 플레이어를 돌려준다.
	 * 
	 * @return 캐릭터가 인식하고 있는 오브젝트를 나타내는 List<L1PcInstance>
	 */
	public List<L1PcInstance> getKnownPlayers() {
		return knownPlayer;
	}
	
	/**
	 * 캐릭터가 인식하고 있는 모든 몬스터를 돌려준다.
	 * 
	 * @return 캐릭터가 인식하고 있는 오브젝트를 나타내는 List<L1MonsterInstance>
	 */
	public List<L1MonsterInstance> getKnownMonsters() {
		return knownMonster;
	}
	/**
	 * 캐릭터에, 새롭게 인식하는 오브젝트를 추가한다.
	 * 
	 * @param obj
	 *            새롭게 인식하는 오브젝트.
	 */
	public void addKnownObject(L1Object obj) {
		if (!knownObjects.contains(obj)) {
			knownObjects.add(obj);
			if (obj instanceof L1PcInstance) {
				knownPlayer.add((L1PcInstance) obj);
			} else if(obj instanceof L1MonsterInstance)
				knownMonster.add((L1MonsterInstance)obj);
		}
	}

	/**
	 * 캐릭터로부터, 인식하고 있는 오브젝트를 삭제한다.
	 * 
	 * @param obj
	 *            삭제하는 오브젝트.
	 */
	public void removeKnownObject(L1Object obj) {
		knownObjects.remove(obj);
		if (obj instanceof L1PcInstance) {
			knownPlayer.remove(obj);
		}else if(obj instanceof L1MonsterInstance)
			knownMonster.remove(obj);
	}

	/**
	 * 캐릭터로부터, 모든 인식하고 있는 오브젝트를 삭제한다.
	 */
	public void removeAllKnownObjects() {
		knownObjects.clear();
		knownPlayer.clear();
		knownMonster.clear();
	}
}