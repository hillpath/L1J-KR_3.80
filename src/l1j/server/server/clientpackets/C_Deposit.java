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

import l1j.server.server.datatables.CastleTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Castle;
import server.LineageClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Deposit extends ClientBasePacket {

	private static final String C_DEPOSIT = "[C] C_Deposit";

	public C_Deposit(byte abyte0[], LineageClient clientthread)
			throws Exception {
		super(abyte0);
		int i = readD();
		int j = readD();

		L1PcInstance player = clientthread.getActiveChar();
		if (player == null) {
			return;
		}
		if (player.getOnlineStatus() != 1) {
			player.sendPackets(new S_Disconnect());
			return;
		}
		if (i == player.getId()) {
			L1Clan clan = L1World.getInstance().getClan(player.getClanname());
			if (clan != null) {
				int castle_id = clan.getCastleId();
				/* 성세금 버그 */
				if (!player.isCrown()) {
					if (castle_id == 0) {
						return;
					}
				}
				/* 성세금 버그 */

				if (castle_id != 0) { // 성주 크란

					L1Castle l1castle = CastleTable.getInstance()
							.getCastleTable(castle_id);
					/* 성세금 버그 */
					L1ItemInstance aden = player.getInventory().findItemId(
							L1ItemId.ADENA);
					if (j <= 0 || aden.getCount() < j || aden.getCount() <= 0
							|| j > 2000000000) {
						player.sendPackets(new S_SystemMessage("(" + j
								+ ")아데나는 정상적인 입금액이 아닙니다."));
						return;
					}
					if (aden.getCount() < 0 || aden.getCount() > 2000000000
							|| (aden.getCount() - j <= 0)
							|| (aden.getCount() - j > 2000000000)) {
						player.sendPackets(new S_SystemMessage("(" + j
								+ ")아데나는 정상적인 입금액이 아닙니다."));
						return;
					}
					/* 성세금 버그 */
					synchronized (l1castle) {
						int money = l1castle.getPublicMoney();
						if (!player.getInventory().checkItem(L1ItemId.ADENA, j)
								|| j < 0)
							return;
						if (player.getInventory()
								.consumeItem(L1ItemId.ADENA, j)) {
							money += j;
							/* 성세금 버그 */
							if (money > 2000000000 || money <= 0) {
								money = 0;
							}
							/* 성세금 버그 */
							l1castle.setPublicMoney(money);
							CastleTable.getInstance().updateCastle(l1castle);
							player.sendPackets(new S_SystemMessage("공금 " + j
									+ " 아데나를 입금하였습니다."));
						}
					}
				}
			}
		}
	}

	@Override
	public String getType() {
		return C_DEPOSIT;
	}

}
