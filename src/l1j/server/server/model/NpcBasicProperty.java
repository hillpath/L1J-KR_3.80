package l1j.server.server.model;

import l1j.server.server.model.Instance.L1NpcInstance;

public class NpcBasicProperty extends BasicProperty {
	public NpcBasicProperty(L1NpcInstance character) {
		super(character);
	}

	@Override
	public void setCurrentHp(int i) {
		super.setCurrentHp(i);

		if (getMaxHp() > getCurrentHp()) {
			((L1NpcInstance) character).startHpRegeneration();
		}
	}

	@Override
	public void setCurrentMp(int i) {
		super.setCurrentMp(i);

		if (getMaxMp() > getCurrentMp()) {
			((L1NpcInstance) character).startMpRegeneration();
		}
	}
}
