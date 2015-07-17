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
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_SoldierGive extends ServerBasePacket {

	private static final String S_SOLDIER_GIVE = "[S] S_SoldierGive";

	private byte[] _byte = null;

	public S_SoldierGive(L1PcInstance pc, int objid, int type, int count,
			int iscount) {

		writeC(Opcodes.S_OPCODE_SOLDIERGIVE);
		writeD(objid);// objid
		writeH(9);// ????(아무숫자나넣음)
		writeH(count);// 해당용병 고용된 수
		writeH(type);// 순번
		writeS(pc.getName());
		writeD(pc.getId());// pc.getId();
		writeH(iscount);// 배치가능 용병수
		writeC(0);
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
		return S_SOLDIER_GIVE;
	}
}