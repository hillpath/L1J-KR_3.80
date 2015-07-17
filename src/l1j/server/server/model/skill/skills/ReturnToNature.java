package l1j.server.server.model.skill.skills;

import l1j.server.Config;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;

public class ReturnToNature {
	public static void runSkill(L1Character cha, L1Character _user,
			L1PcInstance _player) {
		if (Config.RETURN_TO_NATURE && cha instanceof L1SummonInstance) {
			L1SummonInstance summon = (L1SummonInstance) cha;
			Broadcaster.broadcastPacket(summon, new S_SkillSound(
					summon.getId(), 2245));
			summon.returnToNature();
		} else {
			if (_user instanceof L1PcInstance) {
				_player.sendPackets(new S_ServerMessage(79));
			}
		}
	}
}
