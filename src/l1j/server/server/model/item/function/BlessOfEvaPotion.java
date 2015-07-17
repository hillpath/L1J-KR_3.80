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
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class BlessOfEvaPotion extends L1ItemInstance {

	public BlessOfEvaPotion(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = this.getItemId();
			useBlessOfEva(pc, itemId);
			pc.getInventory().removeItem(useItem, 1);
		}
	}

	private void useBlessOfEva(L1PcInstance pc, int item_id) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(71) == true) { // 디케이포션
			// 상태
			pc.sendPackets(new S_ServerMessage(698)); // \f1마력에 의해 아무것도 마실 수가
			// 없습니다.
			return;
		}

		// 아브소르트바리아의 해제
		pc.cancelAbsoluteBarrier();

		int time = 0;
		if (item_id == 40032) { // 에바의 축복
			time = 1800;
		} else if (item_id == 50019) { // 복지호흡물약
			time = 7200;
		} else if (item_id == 40041) { // mermaid의 비늘
			time = 300;
		} else if (item_id == 41344) { // 물의 정수
			time = 2100;
		} else {
			return;
		}

		if (pc.getSkillEffectTimerSet()
				.hasSkillEffect(STATUS_UNDERWATER_BREATH)) {
			int timeSec = pc.getSkillEffectTimerSet().getSkillEffectTimeSec(
					STATUS_UNDERWATER_BREATH);
			time += timeSec;
			if (time > 3600) {
				time = 3600;
			}
		}
		pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), time));
		pc.sendPackets(new S_SkillSound(pc.getId(), 190));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 190));
		pc.getSkillEffectTimerSet().setSkillEffect(STATUS_UNDERWATER_BREATH,
				time * 1000);
	}
}
