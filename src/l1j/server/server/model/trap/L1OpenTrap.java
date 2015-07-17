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
package l1j.server.server.model.trap;

import l1j.server.server.TimeController.InDunController;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1TrapInstance;
import l1j.server.server.storage.TrapStorage;

public class L1OpenTrap extends L1Trap {

	private final int START_TEL = 100;

	private final int LAST_TEL = 101;

	private final int END_LINE = 104;

	public L1OpenTrap(TrapStorage storage) {
		super(storage);
	}

	@Override
	public void onTrod(L1PcInstance pc, L1Object trapObj) {

		sendEffect(trapObj);

		L1TrapInstance trap = (L1TrapInstance) trapObj;

		switch (this.getId()) {
		case START_TEL: // 시작 텔로포트 트랩
			if (InDunController.getInstance().getTellTrap() == true) {
				L1Teleport.teleport(pc, 32665, 32797, (short) 9000, 4, true);
			}
			break;
		case LAST_TEL: // 골방전 텔리포트 트랩
			if (InDunController.getInstance().getTellTrap() == true) {
				L1Teleport.teleport(pc, 32789, 32822, (short) 9000, 4, true);
			}
			break;
		case END_LINE: // 아이템 지급 트랩
			if (InDunController.getInstance().getLastTrapFour() == true
					&& InDunController.getInstance().getGiveItem() == false) {
				InDunController.getInstance().setGiveItem(true);
				InDunController.getInstance().spawnGroundItem(500099, 1, 10);
			}
			break;
		default:
			break;
		}
	}
}
