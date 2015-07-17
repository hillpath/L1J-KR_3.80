package l1j.server.server.model.skill.skills;

import static l1j.server.server.model.skill.L1SkillId.*;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Cube;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class CubeQuake {

	public static void runSkill(L1Character _user, L1PcInstance _player,
			int buffDuration) {
		L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(
				4500502, buffDuration * 1000, _user.getX(), _user.getY(),
				_user.getMapId());
		_player.getSkillEffectTimerSet().setSkillEffect(CUBE_QUAKE,
				buffDuration * 1000);
		effect.setCubeTime(4);
		effect.setCubePc(_player);
		L1Cube.getInstance().add(1, effect);

	}

}
