package l1j.server.server.model.skill.skills;

import l1j.server.server.model.Instance.L1PcInstance;

public class JoyOfPain {

	public static int runSkill(L1PcInstance _player) {
		int selldmg = _player.getMaxHp() - _player.getCurrentHp();
		return selldmg / 3;
	}

}
