package l1j.server.server.model.skill.skills;

import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_SPMR;

public class ScalesWaterDragon {

	public static void runSkill(L1Character cha) {
		L1PcInstance pc = (L1PcInstance) cha;
		pc.getResistance().addMr(30);
		pc.getResistance().addAllNaturalResistance(30);
		pc.sendPackets(new S_SPMR(pc));
		pc.sendPackets(new S_OwnCharAttrDef(pc));
		pc.getGfxId().setTempCharGfx(6894);
		pc.sendPackets(new S_ChangeShape(pc.getId(), 6894));
		if (!pc.isGmInvis() && !pc.isInvisble()) {
			Broadcaster
					.broadcastPacket(pc, new S_ChangeShape(pc.getId(), 6894));
		}
		pc.startMpDecreaseByScales();
	}

}
