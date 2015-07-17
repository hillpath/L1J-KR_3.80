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

import l1j.server.GameSystem.GhostHouse;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1TrapInstance;
import l1j.server.server.storage.TrapStorage;

public class L1GhostHouseTrap extends L1Trap {

	private final int DOOR_OPEN = 70;

	private final int FINISH_LINE = 71;

	public L1GhostHouseTrap(TrapStorage storage) {
		super(storage);
	}

	@Override
	public void onTrod(L1PcInstance pc, L1Object trapObj) {

		sendEffect(trapObj);

		L1TrapInstance trap = (L1TrapInstance) trapObj;

		switch (this.getId()) {
		case DOOR_OPEN:
			GhostHouse.getInstance().pushOpenDoorTrap(trap.getTrapDoorId());
			break;
		case FINISH_LINE:
			GhostHouse.getInstance().pushFinishLineTrap(pc);
			break;
		default:
			break;
		}
	}
}
