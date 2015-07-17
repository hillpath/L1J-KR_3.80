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
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Skills;

@SuppressWarnings("serial")
public class BlankScroll extends L1ItemInstance {

	public BlankScroll(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int blanksc_skillid = 0;
			blanksc_skillid = packet.readC();
			int itemId = useItem.getItemId();
			if (pc.isWizard()) { // 위저드
				if (itemId == 40090 && blanksc_skillid <= 7 || // 공백
						// 스크롤(Lv1)로 레벨 1 이하의 마법
						itemId == 40091 && blanksc_skillid <= 15 || // 공백
						// 스크롤(Lv2)로 레벨 2 이하의 마법
						itemId == 40092 && blanksc_skillid <= 22 || // 공백
						// 스크롤(Lv3)로 레벨 3 이하의 마법
						itemId == 40093 && blanksc_skillid <= 31 || // 공백
						// 스크롤(Lv4)로 레벨 4 이하의 마법
						itemId == 40094 && blanksc_skillid <= 39) { // 공백
					// 스크롤(Lv5)로 레벨 5 이하의 마법
					L1ItemInstance spellsc = ItemTable.getInstance()
							.createItem(40859 + blanksc_skillid);
					if (spellsc != null) {
						if (pc.getInventory().checkAddItem(spellsc, 1) == L1Inventory.OK) {
							// blanksc_skillid는 0 시작
							L1Skills l1skills = SkillsTable.getInstance()
									.getTemplate(blanksc_skillid + 1);
							if (pc.getCurrentHp() + 1 < l1skills.getHpConsume() + 1) {
								pc.sendPackets(new S_ServerMessage(279));
								// \f1HP가 부족해 마법을 사용할 수 있지 않습니다.
								return;
							}
							if (pc.getCurrentMp() < l1skills.getMpConsume()) {
								pc.sendPackets(new S_ServerMessage(278));
								// \f1MP가 부족해 마법을 사용할 수 있지 않습니다.
								return;
							}
							if (l1skills.getItemConsumeId() != 0) { // 재료가 필요
								if (!pc.getInventory().checkItem(
										l1skills.getItemConsumeId(),
										l1skills.getItemConsumeCount())) {
									pc.sendPackets(new S_ServerMessage(299));
									// \f1마법을 영창하기 위한 재료가 충분하지 않습니다.
									return;
								}
							}
							pc.setCurrentHp(pc.getCurrentHp()
									- l1skills.getHpConsume());
							pc.setCurrentMp(pc.getCurrentMp()
									- l1skills.getMpConsume());
							int lawful = pc.getLawful() + l1skills.getLawful();
							if (lawful > 32767) {
								lawful = 32767;
							}
							if (lawful < -32767) {
								lawful = -32767;
							}
							pc.setLawful(lawful);
							if (l1skills.getItemConsumeId() != 0) { // 재료가 필요
								pc.getInventory().consumeItem(
										l1skills.getItemConsumeId(),
										l1skills.getItemConsumeCount());
							}
							pc.getInventory().removeItem(useItem, 1);
							pc.getInventory().storeItem(spellsc);
						}
					}
				} else {
					pc.sendPackets(new S_ServerMessage(591)); // \f1스크롤이 그렇게
					// 강한 마법을 기록하려면
					// 너무나 약합니다.
				}
			} else {
				pc.sendPackets(new S_ServerMessage(264)); // \f1당신의 클래스에서는 이
				// 아이템은 사용할 수 없습니다.
			}
		}
	}
}
