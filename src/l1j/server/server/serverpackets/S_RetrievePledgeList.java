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

import l1j.server.Warehouse.ClanWarehouse;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_RetrievePledgeList extends ServerBasePacket {
	public S_RetrievePledgeList(int objid, L1PcInstance pc) {
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan == null) {
			return;
		}

		ClanWarehouse clanWarehouse = WarehouseManager.getInstance()
				.getClanWarehouse(clan.getClanName());

		if (!clanWarehouse.lock(pc.getId())) {
			// \f1 혈맹원이 창고를 사용중입니다.당분간 지나고 나서 이용해 주세요.
			pc.sendPackets(new S_ServerMessage(209));
			return;
		}

		if (pc.getInventory().getSize() < 180) {
			int size = clanWarehouse.getSize();
			if (size > 0) {
				// clanWarehouse.setWarehouseUsingChar(pc.getId());
				writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
				writeD(objid);
				writeH(size);
				writeC(5); // 혈맹 창고
				L1ItemInstance item = null;
				for (Object itemObject : clanWarehouse.getItems()) {
					item = (L1ItemInstance) itemObject;
					writeD(item.getId());
						writeC(item.getItem().getType2());//탬타입..돌려주기 응헉
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
			pc.sendPackets(new S_ServerMessage(263)); // \f1한사람의 캐릭터가 가지고 걸을 수
			// 있는 아이템은 최대 180개까지입니다.
		}
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}
}
