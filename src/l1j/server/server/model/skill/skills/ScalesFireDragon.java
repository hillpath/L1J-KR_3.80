package l1j.server.server.model.skill.skills;

import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChangeShape;

public class ScalesFireDragon {

	public static void runSkill(L1Character cha) {
		L1PcInstance pc = (L1PcInstance) cha;
		pc.getAbility().addAddedStr((byte) 5);
		pc.getAbility().addAddedDex((byte) 5);
		pc.getAbility().addAddedCon((byte) 5);
		pc.getAbility().addAddedInt((byte) 5);
		pc.getAbility().addAddedWis((byte) 5);
		pc.getGfxId().setTempCharGfx(6894);
		pc.sendPackets(new S_ChangeShape(pc.getId(), 6894));
		if (!pc.isGmInvis() && !pc.isInvisble()) {
			Broadcaster
					.broadcastPacket(pc, new S_ChangeShape(pc.getId(), 6894));
		}
		pc.startMpDecreaseByScales();
	}

}
