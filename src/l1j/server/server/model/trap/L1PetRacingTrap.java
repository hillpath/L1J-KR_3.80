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

import l1j.server.GameSystem.PetRacing;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.storage.TrapStorage;

public class L1PetRacingTrap extends L1Trap {
	private final int POLY_TRAP = 85;

	private final int POLY_NOEFFECT_TRAP = 86;

	private final int ACCEL_TRAP = 87;

	private final int ACCEL_NOEFFECT_TRAP = 88;

	private final int POLYStarter_TRAP = 98;

	private final int ACCELStarter_TRAP = 99;

	public L1PetRacingTrap(TrapStorage storage) {
		super(storage);
	}

	@Override
	public void onTrod(L1PcInstance pc, L1Object trapObj) {

		sendEffect(trapObj);

		// L1TrapInstance trap = (L1TrapInstance) trapObj;

		switch (this.getId()) {
		case POLY_TRAP:
		case POLY_NOEFFECT_TRAP:
			PetRacing.getInstance().pushPolyTrap(pc);
			break;
		case ACCEL_TRAP:
		case ACCEL_NOEFFECT_TRAP:
			PetRacing.getInstance().pushAccelTrap(pc);
			break;
		case POLYStarter_TRAP:
			PetRacing.getInstance().pushPolyStarterTrap(pc);
			break;
		case ACCELStarter_TRAP:
			PetRacing.getInstance().pushAccelStarterTrap(pc);
			break;
		default:
			break;
		}
	}
}
