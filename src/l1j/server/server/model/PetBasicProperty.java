package l1j.server.server.model;

import l1j.server.server.model.Instance.L1NpcInstance;

public class PetBasicProperty extends NpcBasicProperty {

	public PetBasicProperty(L1NpcInstance character) {
		super(character);
	}

	public void setCurrentHp(int i) {
		super.setCurrentHp(i);

		/*
		 * if (((L1PetInstance)character).getPetMaster() != null) { int HpRatio =
		 * 100 * getCurrentHp() / getMaxHp(); L1PcInstance Master =
		 * ((L1PetInstance)character).getPetMaster(); Master.sendPackets(new
		 * S_HPMeter(((L1SummonInstance)character).getId(), HpRatio)); }
		 */
	}
}
