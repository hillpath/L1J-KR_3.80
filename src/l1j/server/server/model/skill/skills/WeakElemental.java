package l1j.server.server.model.skill.skills;

import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Npc;

public class WeakElemental {
	public static void runSkill(L1Character cha) {
		if (cha instanceof L1MonsterInstance) {
			L1Npc npcTemp = ((L1MonsterInstance) cha).getNpcTemplate();
			int weakAttr = npcTemp.get_weakAttr();
			if ((weakAttr & 1) == 1) {
				Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(),
						2169));
			}
			if ((weakAttr & 2) == 2) {
				Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(),
						2167));
			}
			if ((weakAttr & 4) == 4) {
				Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(),
						2166));
			}
			if ((weakAttr & 8) == 8) {
				Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(),
						2168));
			}
		}
	}
}
