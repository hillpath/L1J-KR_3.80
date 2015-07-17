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
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class BlindPotion extends L1ItemInstance {

	public BlindPotion(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			useBlindPotion(pc);
			pc.getInventory().removeItem(useItem, 1);
		}
	}

	private void useBlindPotion(L1PcInstance pc) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(DECAY_POTION)) {
			pc.sendPackets(new S_ServerMessage(698)); // \f1마력에 의해 아무것도 마실 수가
			// 없습니다.
			return;
		}

		// 아브소르트바리아의 해제
		pc.cancelAbsoluteBarrier();

		int time = 450;
		if (pc.getSkillEffectTimerSet().hasSkillEffect(CURSE_BLIND)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(CURSE_BLIND);
		} else if (pc.getSkillEffectTimerSet().hasSkillEffect(DARKNESS)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(DARKNESS);
		}

		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_FLOATING_EYE)) {
			pc.sendPackets(new S_CurseBlind(2));
		} else {
			pc.sendPackets(new S_CurseBlind(1));
		}

		pc.getSkillEffectTimerSet().setSkillEffect(CURSE_BLIND, time * 1000);
	}
}
