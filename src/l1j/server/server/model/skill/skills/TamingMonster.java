package l1j.server.server.model.skill.skills;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

public class TamingMonster {
	public static void runSkill(L1PcInstance _user, L1PcInstance _player, L1Character _target, L1NpcInstance npc) {
		int petcost = 0;
		Object[] petlist = _user.getPetList().values().toArray();
		for (Object pet : petlist) {
			petcost += ((L1NpcInstance) pet).getPetcost();
		}
		int charisma = _user.getAbility().getTotalCha();
		if (_player.isElf()) { 
			charisma += 12;
		} else if (_player.isWizard()) { 
			charisma += 6;
		}
		charisma -= petcost;
		if (charisma >= 6) { 
			L1SummonInstance summon = new L1SummonInstance(npc, _user, false);
			_target = summon; 
		} else {
			_player.sendPackets(new S_ServerMessage(319)); 
		}
	}
}
