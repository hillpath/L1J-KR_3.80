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

import java.io.IOException;

import l1j.server.Warehouse.ElfWarehouse;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_RetrieveElfList extends ServerBasePacket {
	public S_RetrieveElfList(int objid, L1PcInstance pc) {
		if (pc.getInventory().getSize() < 180) {
			ElfWarehouse elfwarehouse = WarehouseManager.getInstance()
					.getElfWarehouse(pc.getAccountName());
			int size = elfwarehouse.getSize();
			if (size > 0) {
				writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
				writeD(objid);
				writeH(size);
				writeC(9);
				L1ItemInstance item = null;
				for (Object itemObject : elfwarehouse.getItems()) {
					item = (L1ItemInstance) itemObject;
					writeD(item.getId());
					writeC(item.getItem().getType2());//≈∆≈∏¿‘..µπ∑¡¡÷±‚ ¿¿«‰
					writeH(item.get_gfxid());
					writeC(item.getBless());
					writeD(item.getCount());
					writeC(item.isIdentified() ? 1 : 0);
					writeS(item.getViewName());
				}
				writeD(30);
				writeD(0x00000000);
				writeH(0x00);
}
			else {
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "noitemret")); 
			}
		} else {
			pc.sendPackets(new S_ServerMessage(263));
		}
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}

}
