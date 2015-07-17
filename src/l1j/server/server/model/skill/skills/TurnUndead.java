package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;

public class TurnUndead {

	public static int runSkill(int undeadType, L1Character cha) {
		if (undeadType == 1 || undeadType == 3) {
			return cha.getCurrentHp();
		}
		return 0;
	}

}
