package l1j.server.server.model.skill.skills;

import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillSound;

public class TripleArrow {

	public static void runSkill(L1PcInstance _player, L1Character _target) {
		boolean gfxcheck = false;
		int[] BowGFX = { 138, 37, 3860, 3126, 3420, 2284, 3105, 3145, 3148,
				3151, 3871, 4125, 2323, 3892, 3895, 3898, 3901, 4917, 4918,
				4919, 4950, 6140, 6145, 6150, 6155, 6269, 6272, 6275, 6278,
				6826, 6827, 6836, 6837, 6846, 6847, 6856, 6857, 6866, 6867,
				6876, 6877, 6886, 6887, 6400, 5645, 6399, 7039, 7040, 7041,
				7140, 7144, 7148, 7152, 7156, 7160, 7164, 7139, 7143, 7147,
				7151, 7155, 7159, 7163 };

		int playerGFX = _player.getGfxId().getTempCharGfx();

		for (int gfx : BowGFX) {
			if (playerGFX == gfx) {
				gfxcheck = true;
				break;
			}
		}

		if (!gfxcheck)
			return;

		for (int i = 3; i > 0; i--) {
			_target.onAction(_player);
		}
		_player.sendPackets(new S_SkillSound(_player.getId(), 4394));
		Broadcaster.broadcastPacket(_player, new S_SkillSound(_player.getId(),
				4394));
	}
}
