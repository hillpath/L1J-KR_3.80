package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;

public class MindBreak {

	public static int runSkill(L1Character _target) {
		if (_target.getCurrentMp() >= 5) {
			_target.setCurrentMp(_target.getCurrentMp() - 5);
			return 25;
		}
		return 0;
	}
}
