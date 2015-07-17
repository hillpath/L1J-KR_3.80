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
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class BravePotion extends L1ItemInstance {

	public BravePotion(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.getSkillEffectTimerSet().hasSkillEffect(71) == true) { // 디케이포션
				// 상태
				pc.sendPackets(new S_ServerMessage(698));// \f1마력에 의해 아무것도 마실
				// 수가 없습니다.
				return;
			}
			pc.cancelAbsoluteBarrier();

			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = this.getItemId();

			if ((itemId == L1ItemId.POTION_OF_EMOTION_BRAVERY // 치우침 이브 일부
					|| itemId == L1ItemId.B_POTION_OF_EMOTION_BRAVERY // 축복된
					// 치우침
					// 이브
					|| itemId == 41415 || itemId == 50014) && pc.isKnight()) {
				useBravePotion(pc, itemId);
			} else if ((itemId == 40068 || itemId == 140068 || itemId == 50015)
					&& pc.isElf()) {
				useBravePotion(pc, itemId);
			} else if (itemId == 40031 && pc.isCrown()) { // 악마의피
				useBravePotion(pc, itemId);
			} else if (itemId == L1ItemId.UGDRA_FRUIT
					&& pc.isIllusionist()) { // 유그드라열매
				useFruit(pc, itemId);
			} else {
				pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
				// 않았습니다.
				return;
			}
			pc.getInventory().removeItem(useItem, 1);
		}
	}

	private void useFruit(L1PcInstance pc, int item_id) {
		int time = 0;
		time = 480;

		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_BRAVE);
			pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
			pc.getMoveState().setBraveSpeed(0);
		}

		pc.sendPackets(new S_SkillSound(pc.getId(), 7110));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7110));
		pc.getSkillEffectTimerSet().setSkillEffect(STATUS_FRUIT, time * 1000);
		pc.getMoveState().setBraveSpeed(1);
	}

	private void useBravePotion(L1PcInstance pc, int item_id) {
		int time = 0;

		switch (item_id) {
		case L1ItemId.POTION_OF_EMOTION_BRAVERY:
			time = 300;
			break;
		case 40031:
			time = 600;
			break;
		case 40068:
			time = 600;
			if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE)) { // 용기와는
				// 중복
				// 하지
				// 않는다.
				pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_BRAVE);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0,
						0));
				pc.getMoveState().setBraveSpeed(0);
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(WIND_WALK)) { // 윈드워크와는
				// 중복
				// 하지
				// 않는다
				pc.getSkillEffectTimerSet().killSkillEffectTimer(WIND_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0,
						0));
				pc.getMoveState().setBraveSpeed(0);
			}
			break;
		case 40733:
			time = 600;
			if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_ELFBRAVE)) {
				pc.getSkillEffectTimerSet().killSkillEffectTimer(
						STATUS_ELFBRAVE);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0,
						0));
				pc.getMoveState().setBraveSpeed(0);
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(HOLY_WALK)) { // 호-리
				// 워크와는
				// 중복
				// 하지
				// 않는다
				pc.getSkillEffectTimerSet().killSkillEffectTimer(HOLY_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0,
						0));
				pc.getMoveState().setBraveSpeed(0);
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(MOVING_ACCELERATION)) { // 무빙 악
				// 세레이션과는
				// 중복
				// 하지
				// 않는다
				pc.getSkillEffectTimerSet().killSkillEffectTimer(
						MOVING_ACCELERATION);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0,
						0));
				pc.getMoveState().setBraveSpeed(0);
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(WIND_WALK)) { // 윈드워크와는
				// 중복
				// 하지
				// 않는다
				pc.getSkillEffectTimerSet().killSkillEffectTimer(WIND_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0,
						0));
				pc.getMoveState().setBraveSpeed(0);
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_FRUIT)) { // 유그드라열매와는
				// 중복안됨
				pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_FRUIT);
				// pc.sendPackets(new S_SkillFruit(pc.getId(), 0, 0));
				pc.getMoveState().setBraveSpeed(0);
			}
			break;
		case 41415:
			time = 1800;
			break;
		case 50014:// 복지용기물약
			time = 1200;
			break;
		case L1ItemId.B_POTION_OF_EMOTION_BRAVERY:
			time = 350;
			break;
		case 50015:// 복지집중물약
			time = 1920;
			if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE)) { // 용기와는
				// 중복
				// 하지
				// 않는다.
				pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_BRAVE);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0,
						0));
				pc.getMoveState().setBraveSpeed(0);
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(WIND_WALK)) { // 윈드워크와는
				// 중복
				// 하지
				// 않는다
				pc.getSkillEffectTimerSet().killSkillEffectTimer(WIND_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0,
						0));
				pc.getMoveState().setBraveSpeed(0);
			}
			break;
		case 140068:
			time = 700;
			if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE)) { // 용기
				// 효과와는
				// 중복
				// 하지
				// 않는다.
				pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_BRAVE);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0,
						0));
				pc.getMoveState().setBraveSpeed(0);
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(WIND_WALK)) { // 윈드워크와는
				// 중복
				// 하지
				// 않는다
				pc.getSkillEffectTimerSet().killSkillEffectTimer(WIND_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0,
						0));
				pc.getMoveState().setBraveSpeed(0);
			}
			break;
		}

		if (item_id == 40068 || item_id == 140068 || item_id == 50015) { // 엘븐
																			// 와퍼
			pc.sendPackets(new S_SkillBrave(pc.getId(), 3, time));
			Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 3, 0));
			pc.getSkillEffectTimerSet().setSkillEffect(STATUS_ELFBRAVE,
					time * 1000);
		} else {
			pc.sendPackets(new S_SkillBrave(pc.getId(), 1, time));
			Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 1, 0));
			pc.getSkillEffectTimerSet().setSkillEffect(STATUS_BRAVE,
					time * 1000);
		}
		pc.sendPackets(new S_SkillSound(pc.getId(), 751));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 751));
		pc.getMoveState().setBraveSpeed(1);
	}
}
