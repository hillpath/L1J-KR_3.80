package l1j.server.server.model;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;

public class PcBasicProperty extends BasicProperty {
	public PcBasicProperty(L1PcInstance character) {
		super(character);
	}

	@Override
	public void setCurrentHp(int i) {
		super.setCurrentHp(i);

		L1PcInstance pc = (L1PcInstance) character;
		if (getCurrentHp() == i)
			return;
		if (pc.isGm())
			i = getMaxHp();
		pc.sendPackets(new S_HPUpdate(getCurrentHp(), getMaxHp()));
		if (pc.isInParty())
			pc.getParty().updateMiniHP(pc);

		System.out.println("ÇÇ :" + getCurrentHp());
	}

	@Override
	public void setCurrentMp(int i) {
		super.setCurrentMp(i);

		L1PcInstance pc = (L1PcInstance) character;
		if (getCurrentMp() == i)
			return;
		if (pc.isGm())
			i = getMaxMp();
		pc.sendPackets(new S_MPUpdate(getCurrentMp(), getMaxMp()));
	}

	@Override
	public void healHp(int pt) {
		super.healHp(pt);

		L1PcInstance pc = (L1PcInstance) character;
		pc.sendPackets(new S_HPUpdate(pc));
	}
}
