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

package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Character;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_MoveCharPacket extends ServerBasePacket {

	private static final String _S__1F_MOVECHARPACKET = "[S] S_MoveCharPacket";

	private byte[] _byte = null;

	private static final byte HEADING_TABLE_X[] = { 0, -1, -1, -1, 0, 1, 1, 1 };

	private static final byte HEADING_TABLE_Y[] = { 1, 1, 0, -1, -1, -1, 0, 1 };

	public S_MoveCharPacket(L1Character cha) {
		int x = cha.getX();
		int y = cha.getY();

		final int heading = cha.getMoveState().getHeading();

		x += HEADING_TABLE_X[heading];
		y += HEADING_TABLE_Y[heading];

		writeC(Opcodes.S_OPCODE_MOVEOBJECT);
		writeD(cha.getId());
		writeH(x);
		writeH(y);
		writeC(heading);
		writeC(129);
		writeD(0);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return _S__1F_MOVECHARPACKET;
	}
}