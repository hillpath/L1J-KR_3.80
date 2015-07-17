package l1j.server.server.model.skill.skills;

import java.util.Random;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_Paralysis;

public class ShockStun {

	public static void runSkill(Random random, L1Character cha,
			int _shockStunDuration) {
		L1Character _user = null;
		int targetLevel = 0;
		int diffLevel = 0;
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			targetLevel = pc.getLevel();
		} else if (cha instanceof L1MonsterInstance
				|| cha instanceof L1SummonInstance
				|| cha instanceof L1PetInstance) {
			L1NpcInstance npc = (L1NpcInstance) cha;
			targetLevel = npc.getLevel();
		}
		diffLevel = _user.getLevel() - targetLevel;
		if (diffLevel < -2) {// 1~2렙이상차이
			int[] stunTimeArray = { 1100, 1400, 1700, 2000, 2300,
					2600, 2900, 3200, 3500 };
			_shockStunDuration = stunTimeArray[random
					.nextInt(stunTimeArray.length)];
		} else if (diffLevel >= -2 && diffLevel <= 3) {// 아래2랩위로4렙차이동랩기준
			int[] stunTimeArray = { 1000, 1300, 1600, 1900, 2200,
					2500, 2800, 3100, 3400 };
			_shockStunDuration = stunTimeArray[random
					.nextInt(stunTimeArray.length)];
		} else if (diffLevel >= 4 && diffLevel <= 10) {// 4렙~10렙이하차이
			int[] stunTimeArray = { 1800, 2100, 2400, 2700, 3000,
					3300, 3600, 3900, 4200 };
			_shockStunDuration = stunTimeArray[random
					.nextInt(stunTimeArray.length)];
		} else if (diffLevel > 10) {// 10렙이하케릭차이
			int[] stunTimeArray = { 2300, 2600, 2900, 3200, 3500,
					3800, 4100, 4400, 4700 };
			_shockStunDuration = stunTimeArray[random
					.nextInt(stunTimeArray.length)];
		} else if (diffLevel > -4) {// 4 렙이상케릭차이 스턴넣을시..
			int[] stunTimeArray = { 1100, 1400, 1700, 2000, 2300,
					2600 };// 최고3초
			_shockStunDuration = stunTimeArray[random
					.nextInt(stunTimeArray.length)];
		}
		L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration,
				cha.getX(), cha.getY(), cha.getMapId());
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
		} else if (cha instanceof L1MonsterInstance
				|| cha instanceof L1SummonInstance
				|| cha instanceof L1PetInstance) {
			L1NpcInstance npc = (L1NpcInstance) cha;
			npc.setParalyzed(true);
			npc.setParalysisTime(_shockStunDuration);
		}
	}
}
