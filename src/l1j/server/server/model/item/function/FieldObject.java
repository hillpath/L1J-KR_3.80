/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.model.item.function;

import l1j.server.server.ActionCodes;
import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class FieldObject extends L1ItemInstance{//10월1일엔피씨제거막대
	
	public FieldObject(L1Item item){
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet){
		if(cha instanceof L1PcInstance){
			L1PcInstance pc = (L1PcInstance)cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = this.getItemId();
		    if (itemId == 46160) { 
				useFieldObjectRemovalWand(pc, packet.readD(), useItem);
			}
		}
	}
	
	

	private void useFieldObjectRemovalWand(L1PcInstance pc, int targetId, L1ItemInstance item) {
		S_AttackPacket s_attackStatus = new S_AttackPacket(pc, 0, ActionCodes.ACTION_Wand);
		pc.sendPackets(s_attackStatus);
		Broadcaster.broadcastPacket(pc, s_attackStatus);
		L1Object target = L1World.getInstance().findObject(targetId);
		if (target != null && target instanceof L1NpcInstance) {
			L1NpcInstance npc = (L1NpcInstance) target;
			//npc.deleteMe();
			NpcSpawnTable.getInstance().removeSpawn(npc);
			npc.setRespawn(false);
			new L1NpcDeleteTimer(npc, 2 * 1000).begin();
			pc.sendPackets(new S_SystemMessage(npc.getNpcId() + npc.getName() + "을(를) 2초 뒤에 삭제 합니다."));
			pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
		}
	}
}

