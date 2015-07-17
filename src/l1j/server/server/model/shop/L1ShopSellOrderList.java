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
package l1j.server.server.model.shop;

import javolution.util.FastTable;
import java.util.List;

import l1j.server.server.model.L1BugBearRace;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.BugKick;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

class L1ShopSellOrder {
	private final L1AssessedItem _item;

	private final int _count;
	private final float _dividend;

	public L1ShopSellOrder(L1AssessedItem item, int count, float dividend) {
		_item = item;
		_count = count;
		_dividend = dividend;
	}

	public L1AssessedItem getItem() { return _item; }
	public int getCount() {	return _count; }
	public float getDividend() {
		return _dividend;

	}
}

public class L1ShopSellOrderList {
	private final L1Shop _shop;

	private final L1PcInstance _pc;

	private final List<L1ShopSellOrder> _list = new FastTable<L1ShopSellOrder>();

	private int bugok = 0;

	L1ShopSellOrderList(L1Shop shop, L1PcInstance pc) {
		_shop = shop;
		_pc = pc;
	}

	public void add(int itemObjectId, int count, L1PcInstance pc) {
		L1ItemInstance item;
		item = pc.getInventory().getItem(itemObjectId);

		if (itemObjectId != item.getId()) {
			bugok = 1;
			return;
		}

		int itemType = item.getItem().getType2();

		if ((itemType == 1 && count != 1) || (itemType == 2 && count != 1)) {
			bugok = 1;
			return;
		}

		if (item.getCount() < 0 || item.getCount() < count) {
			bugok = 1;
			return;
		}

		if (count <= 0) {
			BugKick.getInstance().KickPlayer(pc);
			bugok = 1;
			return;
		}

		if (count > 50000) {
			pc.sendPackets(new S_SystemMessage("5만개 이상은 판매하지 못합니다."));
			return;
		}

		if (item.getCount() < count) {
			bugok = 1;
			return;
		}

		if (!item.isStackable() && count != 1) {
			bugok = 1;
			return;
		}

		if (item.getCount() <= 0 || count <= 0) {
			bugok = 1;
			return;
		}

		if (item.getBless() >= 128) {
			return;
		}

		L1AssessedItem assessedItem = _shop.assessItem(_pc.getInventory()
				.getItem(itemObjectId));
		if (assessedItem == null) {
			throw new IllegalArgumentException();
		}

		float dividend = 1;
		if (item.getItem().getItemId() == 40309) {
			dividend = L1BugBearRace.getInstance().getTicketPrice(item);
		}
		_list.add(new L1ShopSellOrder(assessedItem, count,dividend));
	}

	public int BugOk() {
		return bugok;
	}

	L1PcInstance getPc() {
		return _pc;
	}

	List<L1ShopSellOrder> getList() {
		return _list;
	}
}
