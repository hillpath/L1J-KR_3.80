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

import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1ItemDelay;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class FireWand extends L1ItemInstance{
	public FireWand(L1Item item){
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet){
		if(cha instanceof L1PcInstance){
			L1PcInstance pc = (L1PcInstance)cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int spellsc_objid = 0;
			int spellsc_x = 0;
			int spellsc_y = 0;			
			spellsc_objid = packet.readD();
			spellsc_x = packet.readH();
			spellsc_y = packet.readH();
			pc.cancelAbsoluteBarrier();
			//pc.cancelAvata();
			
			int itemId = this.getItemId();
			int delay_id = 0;
			if (itemId == 46113) {
				delay_id = ((L1EtcItem) useItem.getItem()).get_delayid();
			}
			if (delay_id != 0) {
				if (pc.hasItemDelay(delay_id) == true) {
					return;
				}
			}
			
			if(pc.isInvisble()){
				pc.sendPackets(new S_ServerMessage(1003));
				return;
			}
			int chargeCount = useItem.getChargeCount();
			if (chargeCount <= 0) {
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}
			if (pc.getMap().isSafetyZone(pc.getLocation())) {
				pc.sendPackets(new S_SystemMessage(
						"마을안에서는 화염 막대를 사용 할 수 없습니다."));
				return;
			}
			if (pc.getMapId() != 2101 && pc.getMapId() != 2151){
				pc.sendPackets(new S_SystemMessage("얼음 여왕의 성에서만 사용 할 수 있습니다."));
				return;
			}
			L1Object target = L1World.getInstance().findObject(spellsc_objid);

			int heding = CharPosUtil.targetDirection(pc, spellsc_x, spellsc_y);
			pc.getMoveState().setHeading(heding);
			if (target != null) {
				doWandAction(pc, target);
			} else {
				pc.sendPackets(new S_UseAttackSkill(pc, 0, 762, spellsc_x, spellsc_y, 18));
				Broadcaster.broadcastPacket(pc, new S_UseAttackSkill(pc, 0, 762, spellsc_x, spellsc_y, 18));
			}
			useItem.setChargeCount(useItem.getChargeCount() - 1);
			pc.getInventory().updateItem(useItem, L1PcInventory.COL_CHARGE_COUNT);
			if (useItem.getChargeCount() == 0){
				pc.getInventory().removeItem(useItem);
			}
			if (itemId == 46113) {
				L1ItemDelay.onItemUse(pc, useItem);
			}
		}
	}

	private void doWandAction(L1PcInstance user, L1Object target) {
		if (CharPosUtil.glanceCheck(user, target.getX(), target.getY()) == false) {
			return; // 직선상에 장애물이 있다
		}
		// XXX 적당한 데미지 계산, 요점 수정
		for (L1Object object : L1World.getInstance().getVisibleObjects(target, 3)) {
			if (object instanceof L1MonsterInstance) {
				L1NpcInstance npc = (L1NpcInstance) object;
				npc.receiveDamage(user, 100);
			}	
		}
		if (target instanceof L1MonsterInstance) {
			L1NpcInstance npc = (L1NpcInstance) target;
			npc.receiveDamage(user, 100);
		}
		user.sendPackets(new S_UseAttackSkill(user, target.getId(), 762, target.getX(), target.getY(), 18));
		Broadcaster.broadcastPacket(user, new S_UseAttackSkill(user, target.getId(), 762, target.getX(), target.getY(), 18));
	}
}

