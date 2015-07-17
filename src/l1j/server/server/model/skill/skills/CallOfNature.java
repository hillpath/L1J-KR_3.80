package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;

public class CallOfNature {
	public static void runSkill(L1Character cha, L1PcInstance _player) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (_player.getId() != pc.getId()) {
				if (L1World.getInstance().getVisiblePlayer(pc, 0).size() > 0) {
					for (L1PcInstance visiblePc : L1World.getInstance()
							.getVisiblePlayer(pc, 0)) {
						if (!visiblePc.isDead()) {
							_player.sendPackets(new S_ServerMessage(592));
							return;
						}
					}
				}
				if (pc.getCurrentHp() == 0 && pc.isDead()) {
					pc.setTempID(_player.getId());
					pc.sendPackets(new S_Message_YN(322, ""));
				}
			}
		}
		if (cha instanceof L1NpcInstance) {
			if (!(cha instanceof L1TowerInstance)) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				if (npc instanceof L1PetInstance
						&& L1World.getInstance().getVisiblePlayer(npc, 0)
								.size() > 0) {
					for (L1PcInstance visiblePc : L1World.getInstance()
							.getVisiblePlayer(npc, 0)) {
						if (!visiblePc.isDead()) {
							_player.sendPackets(new S_ServerMessage(592));
							return;
						}
					}
				}
				if (npc.getCurrentHp() == 0 && npc.isDead()) {
					npc.resurrect(cha.getMaxHp());
					npc.resurrect(cha.getMaxMp() / 100);
					npc.setResurrect(true);
				}
			}
		}
	}
}
