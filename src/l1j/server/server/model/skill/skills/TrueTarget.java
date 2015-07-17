package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_TrueTarget;

public class TrueTarget {

	public static void runSkill(L1Character _user, String _message, L1Character _target, int _targetID, int _targetX, int _targetY) {
		if (_user instanceof L1PcInstance) {
			L1PcInstance pri = (L1PcInstance) _user;
			pri.sendPackets(new S_TrueTarget(_targetID,_targetX,_targetY, _message));
			for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(_target)) {
				if(pri.getClanid() == pc.getClanid()){
					pc.sendPackets(new S_TrueTarget(_targetID,_targetX,_targetY, _message));
				}
			}
		}
	}
}
