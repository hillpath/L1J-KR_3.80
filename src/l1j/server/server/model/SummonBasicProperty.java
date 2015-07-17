package l1j.server.server.model;

import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_HPMeter;

public class SummonBasicProperty extends NpcBasicProperty {
	public SummonBasicProperty(L1NpcInstance character) {
		super(character);
	}

	public void setCurrentHp(int i) {
		super.setCurrentHp(i);

		if (((L1SummonInstance) character).getMaster() instanceof L1PcInstance) {
			int HpRatio = 100 * getCurrentHp() / getMaxHp();
			L1PcInstance Master = (L1PcInstance) ((L1SummonInstance) character)
					.getMaster();
			Master.sendPackets(new S_HPMeter(character.getId(), HpRatio));
		}
	}
}
