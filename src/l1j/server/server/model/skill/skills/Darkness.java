package l1j.server.server.model.skill.skills;

import static l1j.server.server.model.skill.L1SkillId.*;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CurseBlind;

public class Darkness {

	public static void runSkill(L1Character cha) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_FLOATING_EYE)) {
				pc.sendPackets(new S_CurseBlind(2));
			} else {
				pc.sendPackets(new S_CurseBlind(1));
			}
		}
	}
}
