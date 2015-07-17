package l1j.server.server.model.skill.skills;

import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;

public class ScalesEarthDragon {

	public static void runSkill(L1Character cha) {
		L1PcInstance pc = (L1PcInstance) cha;
		pc.addMaxHp(127);
		pc.getAC().addAc(-12);
		pc.sendPackets(new S_OwnCharAttrDef(pc));
		pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
		if (pc.isInParty()) {
			pc.getParty().updateMiniHP(pc);
		}
		pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		pc.getGfxId().setTempCharGfx(6894);
		pc.sendPackets(new S_ChangeShape(pc.getId(), 6894));
		if (!pc.isGmInvis() && !pc.isInvisble()) {
			Broadcaster
					.broadcastPacket(pc, new S_ChangeShape(pc.getId(), 6894));
		}
		pc.startMpDecreaseByScales();
	}

}
