/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
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

import java.util.logging.Logger;

import server.LineageClient;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_OwnCharPack;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Ship extends ClientBasePacket {

	private static final String C_SHIP = "[C] C_Ship";

	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(C_Ship.class.getName());

	public C_Ship(byte abyte0[], LineageClient client) {
		super(abyte0);

		int shipMapId = readH();
		int locX = readH();
		int locY = readH();

		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}
		int mapId = pc.getMapId();

		switch (mapId) {
		case 5:
			pc.getInventory().consumeItem(40299, 1);
			break;
		case 6:
			pc.getInventory().consumeItem(40298, 1);
			break;
		case 83:
			pc.getInventory().consumeItem(40300, 1);
			break;
		case 84:
			pc.getInventory().consumeItem(40301, 1);
			break;
		case 446:
			pc.getInventory().consumeItem(40303, 1);
			break;
		case 447:
			pc.getInventory().consumeItem(40302, 1);
			break;
		default:
			break;
		}

		pc.sendPackets(new S_OwnCharPack(pc));
		L1Teleport.teleport(pc, locX, locY, (short) shipMapId, 0, false);
	}

	@Override
	public String getType() {
		return C_SHIP;
	}
}
