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

import static l1j.server.server.model.skill.L1SkillId.*;
import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class BluePotion extends L1ItemInstance {

	public BluePotion(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = this.getItemId();
			useBluePotion(pc, itemId);
			pc.getInventory().removeItem(useItem, 1);
		}
	}

	private void useBluePotion(L1PcInstance pc, int item_id) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) { // 디케이포션
			// 상태
			pc.sendPackets(new S_ServerMessage(698)); // \f1마력에 의해 아무것도 마실 수가
			// 없습니다.
			return;
		}

		// 아브소르트바리아의 해제
		pc.cancelAbsoluteBarrier();

		int time = 0;
		if (item_id == 40015 || item_id == 40736) { // 블루 일부, 지혜의 코인
			time = 600;
		} else if (item_id == 140015) { // 축복된 블루 일부
			time = 700;
		} else if (item_id == 404082) { // 픽시의 마나 포션
			time = 700;
		} else if (item_id == 50017) { // 복지마나물약
			time = 2400;
		} else if (item_id == 41142) { // 블루 일부, 지혜의 코인
			time = 300;
		} else {
			return;
		}

		pc.sendPackets(new S_SkillIconGFX(34, time));
		pc.sendPackets(new S_SkillSound(pc.getId(), 190));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 190));
		if (item_id == 41142) {
			pc.getSkillEffectTimerSet().setSkillEffect(STATUS_BLUE_POTION2,
					time * 1000);
		} else {
			pc.getSkillEffectTimerSet().setSkillEffect(STATUS_BLUE_POTION,
					time * 1000);
		}

		pc.sendPackets(new S_ServerMessage(1007)); // MP의 회복 속도가 빨라집니다.
	}
}
