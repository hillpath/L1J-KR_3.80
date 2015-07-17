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
import l1j.server.server.datatables.SoldierTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.templates.L1Soldier;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_SoldierBuy extends ClientBasePacket {

	private static final String C_SOLDIER_BUY = "[C] C_SoldierBuy";

	public static class SelectedSoldierInfo {
		int number;

		int count;

		int price;
	}

	public C_SoldierBuy(byte abyte0[], LineageClient clientthread) {
		super(abyte0);
		@SuppressWarnings("unused")
		int npcid = readD(); // 말 건 npc id
		int count = readH(); // 목록에서 몇개 선택?

		SelectedSoldierInfo[] soldierInfor = new SelectedSoldierInfo[count];

		int totalCount = 0, totalPrice = 0;
		for (int i = 0; i < soldierInfor.length; i++) {
			soldierInfor[i] = new SelectedSoldierInfo();
			soldierInfor[i].number = readH(); // 목록에서 몇번째 용병인가
			soldierInfor[i].count = readH(); // 용병 마릿수
			soldierInfor[i].price = readH(); // 가격

			totalCount += soldierInfor[i].count;
			totalPrice += soldierInfor[i].price;
		}

		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null) {
			return;
		}
		int castle_id = pc.getClan().getCastleId();

		L1Soldier soldier = SoldierTable.getInstance().getSoldierTable(
				castle_id);
		L1Castle l1catle = CastleTable.getInstance().getCastleTable(castle_id);

		int totalsoldier = soldier.getSoldier1() + soldier.getSoldier2()
				+ soldier.getSoldier3() + soldier.getSoldier4();
		int clanBossChar = pc.getAbility().getTotalCha();

		if (totalsoldier + totalCount > clanBossChar) {
			// 카리 부족으로 실패
			// 메세지 보내나?
			return;
		}

		// 모든 검사 했으니까, 용병 구입 처리

		for (int i = 0; i < count; i++) {
			switch (soldierInfor[i].number) {
			case 0:
				int sum1 = soldier.getSoldier1() + soldierInfor[i].count;
				soldier.setSoldier1(sum1);
				break;
			case 1:
				int sum2 = soldier.getSoldier2() + soldierInfor[i].count;
				soldier.setSoldier2(sum2);
				break;
			case 2:
				int sum3 = soldier.getSoldier3() + soldierInfor[i].count;
				soldier.setSoldier3(sum3);
				break;
			case 3:
				int sum4 = soldier.getSoldier4() + soldierInfor[i].count;
				soldier.setSoldier4(sum4);
				break;
			default:
				// do nothing;
			}
		}

		int castleMoney = l1catle.getPublicMoney() - totalPrice;
		l1catle.setPublicMoney(castleMoney);
		CastleTable.getInstance().updateCastle(l1catle);
		SoldierTable.getInstance().updateSoldier(soldier);

	}

	@Override
	public String getType() {
		return C_SOLDIER_BUY;
	}
}
