package l1j.server.server.model.skill.skills;

import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_SkillSound;

public class ThunderGrab {
	public static void runSkill(L1Character cha, boolean _isFreeze,
			L1Magic _magic, int buffDuration) {
		_isFreeze = _magic.calcProbabilityMagic(L1SkillId.THUNDER_GRAB);
		if (_isFreeze) {
			int time = buffDuration * 1000;
			L1EffectSpawn.getInstance().spawnEffect(81182, time, cha.getX(),
					cha.getY(), cha.getMapId());
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getSkillEffectTimerSet().setSkillEffect(
						L1SkillId.STATUS_FREEZE, time);
				pc.sendPackets(new S_SkillSound(pc.getId(), 4184));
				Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(),
						4184));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.getSkillEffectTimerSet().setSkillEffect(
						L1SkillId.STATUS_FREEZE, time);
				Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(),
						4184));
				npc.setParalyzed(true);
			}
		}
	}
}
