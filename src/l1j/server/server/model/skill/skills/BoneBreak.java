package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_Paralysis;

public class BoneBreak {

	public static void runSkill(L1Character cha) {
		int bonetime = 3000;
		L1EffectSpawn.getInstance().spawnEffect(4500500, bonetime, cha.getX(),
				cha.getY(), cha.getMapId());
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
		} else if (cha instanceof L1MonsterInstance
				|| cha instanceof L1SummonInstance
				|| cha instanceof L1PetInstance) {
			L1NpcInstance npc = (L1NpcInstance) cha;
			npc.setParalyzed(true);
			npc.setParalysisTime(bonetime);
		}
	}

}
