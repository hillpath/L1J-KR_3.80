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
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.templates.L1Castle;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_SecurityStatusSet extends ClientBasePacket {

	private static final String C_SECURITYSTATUSSET = "[C] C_SecurityStatusSet";

	public C_SecurityStatusSet(byte abyte0[], LineageClient client) {
		super(abyte0);

		int objid = readD();
		int type = readC();
		@SuppressWarnings("unused")
		int unknow = readD();// ????

		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());

		L1Castle castle = CastleTable.getInstance().getCastleTable(
				clan.getCastleId());

		int money = castle.getPublicMoney();

		if (castle.getCastleSecurity() == type)
			return;
		if (money < 100000)
			return;

		if (type == 1)
			castle.setPublicMoney(money - 100000);

		castle.setCastleSecurity(type);

		CastleTable.getInstance().updateCastle(castle);

		pc.sendPackets(new S_CloseList(objid));
	}

	@Override
	public String getType() {
		return C_SECURITYSTATUSSET;
	}
}
