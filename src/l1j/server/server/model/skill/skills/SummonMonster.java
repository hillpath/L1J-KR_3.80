package l1j.server.server.model.skill.skills;

import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShowSummonList;
import l1j.server.server.templates.L1Npc;

public class SummonMonster {
	public static void runSkill(L1Character cha) {
		L1PcInstance pc = (L1PcInstance) cha;
		int level = pc.getLevel();

		if (pc.getMap().isRecallPets() || pc.isGm()) {
			if (pc.getInventory().checkEquipped(20284)) { // 20284 : 소환조종반지
				pc.sendPackets(new S_ShowSummonList(pc.getId()));
			} else {
				int[] summons = new int[] { 81083, 81084, 81085, 81086, 81087,
						81088, 81089 };
				int summonid = 0, summoncost = 6, levelRange = 32;

				for (int i = 0; i < summons.length; i++) {
					if (level < levelRange || i == summons.length - 1) {
						summonid = summons[i];
						break;
					}
					levelRange += 4;
				}

				int petcost = 0;
				Object[] petlist = pc.getPetList().values().toArray();
				for (Object pet : petlist) {
					petcost += ((L1NpcInstance) pet).getPetcost();
				}

				int charisma = pc.getAbility().getTotalCha() + 6 - petcost;
				int summoncount = charisma / summoncost;
				L1Npc npcTemp = NpcTable.getInstance().getTemplate(summonid);

				for (int i = 0; i < summoncount; i++) {
					L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
					summon.setPetcost(summoncost);
				}
			}
		} else {
			pc.sendPackets(new S_ServerMessage(79));
		}
	}
}
