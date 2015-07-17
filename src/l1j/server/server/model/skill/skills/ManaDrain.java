package l1j.server.server.model.skill.skills;

import java.util.Random;

import l1j.server.server.model.L1Character;

public class ManaDrain {
	public static int runSkill(Random random, L1Character _user, L1Character cha) {
		int chance = random.nextInt(10) + 5;
		int drainMana = chance + (_user.getAbility().getTotalInt() / 2);
		if (cha.getCurrentMp() < drainMana) {
			drainMana = cha.getCurrentMp();
		}
		return drainMana;
	}
}
