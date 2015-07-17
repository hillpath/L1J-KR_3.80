package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class Detection {
	public static void runSkill(L1Character cha, L1PcInstance _player) {
		if (cha instanceof L1NpcInstance) {
			L1NpcInstance npc = (L1NpcInstance) cha;
			int hiddenStatus = npc.getHiddenStatus();
			if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
				npc.appearOnGround(_player);
			}
		}
	}
}
