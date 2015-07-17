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
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class Firecracker extends L1ItemInstance {

	public Firecracker(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = useItem.getItemId();
			if (itemId >= 40136 && itemId <= 40161
					|| itemId == L1ItemId.GEMSTONE_POWDER) { // ºÒ²É
				int soundid = 3198;
				switch (itemId) {
				case 40136:
					soundid = 2046;
					break;
				case 40137:
					soundid = 2047;
					break;
				case 40138:
					soundid = 2048;
					break;
				case 40139:
					soundid = 2040;
					break;
				case 40140:
					soundid = 2051;
					break;
				case 40141:
					soundid = 2028;
					break;
				case 40142:
					soundid = 2036;
					break;
				case 40143:
					soundid = 2041;
					break;
				case 40144:
					soundid = 2053;
					break;
				case 40145:
					soundid = 2029;
					break;
				case 40146:
					soundid = 2039;
					break;
				case 40147:
					soundid = 2045;
					break;
				case 40148:
					soundid = 2043;
					break;
				case 40149:
					soundid = 2034;
					break;
				case 40150:
					soundid = 2055;
					break;
				case 40151:
					soundid = 2032;
					break;
				case 40152:
					soundid = 2031;
					break;
				case 40153:
					soundid = 2038;
					break;
				case 40154:
					soundid = 3198;
					break;
				case 40155:
					soundid = 2044;
					break;
				case 40156:
					soundid = 2042;
					break;
				case 40157:
					soundid = 2035;
					break;
				case 40158:
					soundid = 2049;
					break;
				case 40159:
					soundid = 2033;
					break;
				case 40160:
					soundid = 2030;
					break;
				case 40161:
					soundid = 2037;
					break;
				default:
					soundid = 3198;
					break;
				}

				S_SkillSound s_skillsound = new S_SkillSound(pc.getId(),
						soundid);
				pc.sendPackets(s_skillsound);
				Broadcaster.broadcastPacket(pc, s_skillsound);
				pc.getInventory().removeItem(useItem, 1);
			} else if (itemId >= 41357 && itemId <= 41382) { // ¾ËÆÄºª ºÒ²É
				int soundid = itemId - 34946;
				S_SkillSound s_skillsound = new S_SkillSound(pc.getId(),
						soundid);
				pc.sendPackets(s_skillsound);
				Broadcaster.broadcastPacket(pc, s_skillsound);
				pc.getInventory().removeItem(useItem, 1);
			}
		}
	}
}
