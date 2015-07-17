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
 * along with this program; if not, Delete to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.clientpackets;

import server.LineageClient;
import l1j.server.server.datatables.BoardTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1BoardInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_BoardDelete extends ClientBasePacket {

	private static final String C_BOARD_DELETE = "[C] C_BoardDelete";

	public C_BoardDelete(byte decrypt[], LineageClient client) {
		super(decrypt);
		int objId = readD();
		int topicId = readD();
		L1Object obj = L1World.getInstance().findObject(objId);
		L1BoardInstance board = (L1BoardInstance) obj;
		L1PcInstance pc = client.getActiveChar();
		if (board != null) {//�Խ��ǻ���
			if (pc.isGm()) {
			BoardTable.getInstance().deleteTopic(topicId);
			} else {
				pc.sendPackets(new S_SystemMessage(
						"�Խ��� ���� ���� �Ұ��� �մϴ�."));
				return;
		}
	}
	}
	@Override
	public String getType() {
		return C_BOARD_DELETE;
	}

}