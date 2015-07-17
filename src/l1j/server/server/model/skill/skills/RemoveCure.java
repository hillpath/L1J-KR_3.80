package l1j.server.server.model.skill.skills;

import static l1j.server.server.model.skill.L1SkillId.*;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CurseBlind;

public class RemoveCure {

	public static void runSkill(L1Character cha) {
		cha.curePoison();
		if (cha.getSkillEffectTimerSet()
				.hasSkillEffect(STATUS_CURSE_PARALYZING)
				|| cha.getSkillEffectTimerSet().hasSkillEffect(
						STATUS_CURSE_PARALYZED)) {
			cha.cureParalaysis();
		}
		if (cha.getSkillEffectTimerSet().hasSkillEffect(CURSE_BLIND)
				|| cha.getSkillEffectTimerSet().hasSkillEffect(DARKNESS)) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_CurseBlind(0));
			}
		}
	}
}
