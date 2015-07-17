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

package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

public class AutoShopBuyTable {
	public class ItemInfo {
		public int id = 0;
		public String name;
		public int bless = 0;
		public int enchantlvl = 0;
		public int attrEnchantlvl = 0;
		public int price = 0;
	}

	private static Logger _log = Logger.getLogger(AutoShopBuyTable.class.getName());

	private static AutoShopBuyTable _instance;

	private final Map<Integer, ItemInfo> _idlist = new HashMap<Integer, ItemInfo>();

	public static AutoShopBuyTable getInstance() {
		if (_instance == null) {
			_instance = new AutoShopBuyTable();
		}
		return _instance;
	}

	private AutoShopBuyTable() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("[AutoShopBuyTable] loading AutoShopBuyItemPrice...");		
		weaponAddDamage();
		System.out.println("OK! " + timer.get() + " ms");		
	}

	public void weaponAddDamage() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select item_id, name, bless, enchantlvl, attr_enchantlvl, price from autoshopbuyitemlist where price > 0");
			rs = pstm.executeQuery();

			int index = 0;
			ItemInfo itemInfo = null;
			while (rs.next()) {
				itemInfo = new ItemInfo();

				itemInfo.id = rs.getInt("item_id");
				itemInfo.name = rs.getString("name");
				itemInfo.bless = rs.getInt("bless");
				itemInfo.enchantlvl = rs.getInt("enchantlvl");
				itemInfo.attrEnchantlvl = rs.getInt("attr_enchantlvl");
				itemInfo.price = rs.getInt("price");

				_idlist.put(index++, itemInfo);
			}

		} catch (SQLException e) {
			_log.log(Level.SEVERE, "AutoShopBuyTable[]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public static void reload() {
		AutoShopBuyTable oldInstance = _instance;
		_instance = new AutoShopBuyTable();
		if (oldInstance != null)
			oldInstance._idlist.clear();
	}

	public ItemInfo getBuyItemInfo(L1ItemInstance item) {		
		for (ItemInfo itemInfo : _idlist.values()) {
			if (itemInfo.id == item.getItemId() && itemInfo.bless == item.getBless() && itemInfo.enchantlvl == item.getEnchantLevel()) {				
				return itemInfo;
			}
		}

		return null;
	}
}
