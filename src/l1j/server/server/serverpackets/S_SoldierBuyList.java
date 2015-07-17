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

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.SoldierTable;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.templates.L1Soldier;

public class S_SoldierBuyList extends ServerBasePacket {

	/**
	 * 성의 용병 리스트를 출력한다
	 */
	public S_SoldierBuyList(int objId, int castle_id) {
		L1Soldier soldier = SoldierTable.getInstance().getSoldierTable(
				castle_id);
		L1Castle l1castle = CastleTable.getInstance().getCastleTable(castle_id);
		writeC(Opcodes.S_OPCODE_SOLDIERBUYLIST);
		writeD(objId);
		writeD(l1castle.getPublicMoney());
		writeH(castle_id);
		writeH(0);
		writeS(soldier.getSoldier1Name());
		writeH(10000);
		writeH(1);
		writeS(soldier.getSoldier2Name());
		writeH(10000);
		writeH(2);
		writeS(soldier.getSoldier3Name());
		writeH(10000);
		writeH(3);
		writeS(soldier.getSoldier4Name());
		writeH(15000);
	}

	@Override
	public byte[] getContent() throws IOException {
		return _bao.toByteArray();
	}
}
