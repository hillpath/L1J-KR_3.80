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

package l1j.server.server.clientpackets;

import server.LineageClient;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.templates.L1Castle;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_SecurityStatus extends ClientBasePacket {

	private static final String C_SECURITYSTATUS = "[C] C_SecurityStatus";

	public C_SecurityStatus(byte abyte0[], LineageClient client) {
		super(abyte0);

		int objid = readD();

		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());

		if (clan == null || clan.getCastleId() == 0)
			return;

		int castle_id = clan.getCastleId();
		String npcName = null;
		String status = null;
		L1Castle castle = CastleTable.getInstance().getCastleTable(castle_id);

		switch (castle_id) {
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			npcName = "$1238";
			break;
		case 5:
			break;
		default:
			break;
		}

		if (castle.getCastleSecurity() == 0)
			status = "$1118";
		else
			status = "$1117";

		// System.out.println("µø¿€: " +npcName);
		String[] htmldata = new String[] { npcName, status };

		pc.sendPackets(new S_NPCTalkReturn(objid, "CastleS", htmldata));
	}

	@Override
	public String getType() {
		return C_SECURITYSTATUS;
	}
}
