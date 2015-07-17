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
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PrivateShop;
import l1j.server.server.model.Instance.L1NpcShopInstance;
import l1j.server.server.serverpackets.S_PrivateShopforNpc;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_ShopList extends ClientBasePacket {

	private static final String C_SHOP_LIST = "[C] C_ShopList";

	public C_ShopList(byte abyte0[], LineageClient clientthread) {
		super(abyte0);

		int type = readC();
		int objectId = readD();

		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null) {
			return;
		}
		if (pc.isGhost()) {
			return;
		}
		L1Object obj = L1World.getInstance().findObject(objectId);

		if (obj instanceof L1PcInstance)
			pc.sendPackets(new S_PrivateShop(pc, objectId, type));
		else if (obj instanceof L1NpcShopInstance)
			pc.sendPackets(new S_PrivateShopforNpc(pc, objectId, type));
		else
			return;
	}

	@Override
	public String getType() {
		return C_SHOP_LIST;
	}

}
