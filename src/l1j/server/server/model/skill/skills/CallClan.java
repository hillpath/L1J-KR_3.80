package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;

public class CallClan {

	public static void runSkill(L1Character cha, int _targetID) {
		L1PcInstance pc = (L1PcInstance) cha;
		L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(
				_targetID);
		if (clanPc != null) {
			clanPc.setTempID(pc.getId());
			clanPc.sendPackets(new S_Message_YN(729, ""));
		}
	}

}
