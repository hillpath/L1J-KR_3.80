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
package l1j.server.server.storage.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javolution.util.FastTable;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.SQLUtil;

public class MySqlCharactersItemStorage extends CharactersItemStorage {

	private static final Logger _log = Logger
			.getLogger(MySqlCharactersItemStorage.class.getName());

	@Override
	public FastTable<L1ItemInstance> loadItems(int objId) throws Exception {
		FastTable<L1ItemInstance> items = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1ItemInstance item = null;
		L1Item itemTemplate = null;

		try {
			items = new FastTable<L1ItemInstance>();
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM character_items WHERE char_id = ?");
			pstm.setInt(1, objId);

			rs = pstm.executeQuery();

			while (rs.next()) {
				int itemId = rs.getInt("item_id");
				itemTemplate = ItemTable.getInstance().getTemplate(itemId);
				if (itemTemplate == null) {
					_log.warning(String.format("item id:%d not found", itemId));
					continue;
				}
				item = ItemTable.getInstance().FunctionItem(itemTemplate);
				item.setId(rs.getInt("id"));
				item.setItem(itemTemplate);
				item.setCount(rs.getInt("count"));
				item.setEquipped(rs.getInt("Is_equipped") != 0 ? true : false);
				item.setEnchantLevel(rs.getInt("enchantlvl"));
				item.setIdentified(rs.getInt("is_id") != 0 ? true : false);
				item.set_durability(rs.getInt("durability"));
				item.setChargeCount(rs.getInt("charge_count"));
				item.setRemainingTime(rs.getInt("remaining_time"));
				item.setLastUsed(rs.getTimestamp("last_used"));
				item.setBless(rs.getInt("bless"));
				item.setAttrEnchantLevel(rs.getInt("attr_enchantlvl"));
				item.setEndTime(rs.getTimestamp("end_time"));
				item.setSecondId(rs.getInt("second_id"));
				item.setRoundId(rs.getInt("round_id"));
				item.setTicketId(rs.getInt("ticket_id"));
				item.setRegistLevel(rs.getInt("regist_level"));// 인나티
				item.setClock(rs.getInt("clock"));
			    item.setProtection(rs.getInt("protection")); //장비 보호
			    item.setPandoraT(rs.getInt("PandoraT"));                        // 판도라 티셔츠
			    item.setPackage(rs.getInt("package") != 0 ? true : false);
			    item.getLastStatus().updateAll();
				items.add(item);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
			item = null;
			itemTemplate = null;
		}
		return items;
	}

	@Override
	public void storeItem(int objId, L1ItemInstance item) throws Exception {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO character_items SET id = ?, item_id = ?, char_id = ?, item_name = ?, count = ?, is_equipped = 0, enchantlvl = ?, is_id = ?, durability = ?, charge_count = ?, remaining_time = ?, last_used = ?, bless = ?, attr_enchantlvl = ?, end_time = ?, second_id=?, round_id=?, ticket_id=?,regist_level=?,clock = ?,protection=?, PandoraT=?, package = ?");
			pstm.setInt(1, item.getId());
			pstm.setInt(2, item.getItem().getItemId());
			pstm.setInt(3, objId);
			pstm.setString(4, item.getItem().getName());
			pstm.setInt(5, item.getCount());
			pstm.setInt(6, item.getEnchantLevel());
			pstm.setInt(7, item.isIdentified() ? 1 : 0);
			pstm.setInt(8, item.get_durability());
			pstm.setInt(9, item.getChargeCount());
			pstm.setInt(10, item.getRemainingTime());
			pstm.setTimestamp(11, item.getLastUsed());
			pstm.setInt(12, item.getBless());
			pstm.setInt(13, item.getAttrEnchantLevel());
			pstm.setTimestamp(14, item.getEndTime());
			pstm.setInt(15, item.getSecondId());
			pstm.setInt(16, item.getRoundId());
			pstm.setInt(17, item.getTicketId());
			pstm.setInt(18, item.getRegistLevel());// 인나티
			pstm.setInt(19, item.getClock());
			pstm.setInt(20, item.getProtection()); //추가장비보호
			pstm.setInt(21, item.getPandoraT());                  // 판도라 티셔츠
			pstm.setInt(22, item.isPackage() == false ? 0 : 1);
			pstm.execute();

		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		item.getLastStatus().updateAll();
	}

	@Override
	public void deleteItem(L1ItemInstance item) throws Exception {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM character_items WHERE id = ?");
			pstm.setInt(1, item.getId());
			pstm.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void updateItemId(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET item_id = ? WHERE id = ?", item
						.getItemId());
		item.getLastStatus().updateItemId();
	}
	@Override
	  public void updateClock(L1ItemInstance item) throws Exception {
	   executeUpdate(item.getId(),
	     "UPDATE character_items SET clock = ? WHERE id = ?", item
	       .getClock());
	   item.getLastStatus().updateClock();
	  }

	  @Override
	  public void updateEndTime(L1ItemInstance item) throws Exception {
	   executeUpdate(item.getId(),
	     "UPDATE character_items SET end_time = ? WHERE id = ?", item
	       .getEndTime());
	   item.getLastStatus().updateEndTime();
	  }  //스크롤 좀만 내리면 오버라이드 많은데 있는데 거기다 추가
	@Override
	public void updateItemRegistLevel(L1ItemInstance item) throws Exception {// 인나티
		executeUpdate(item.getId(),
				"UPDATE character_items SET regist_level = ? WHERE id = ?",
				item.getRegistLevel());
		item.getLastStatus().updateRegistLevel();
	}
	 @Override
	 public void updateItemProtection(L1ItemInstance item) throws Exception {
	  executeUpdate(item.getId(), "UPDATE character_items SET protection = ? WHERE id = ?", item.getProtection());
	  item.getLastStatus().updateProtection();
	 } //추가장비 보호
		/** 판도라 티셔츠 */
		@Override
		public void updateItemPandoraT(L1ItemInstance item) throws Exception {
		   executeUpdate(item.getId(), "UPDATE character_items SET PandoraT = ? WHERE id = ?", item.getPandoraT());
		   item.getLastStatus().updatePandoraT();
		}
	@Override
	public void updateItemCount(L1ItemInstance item) throws Exception {
		//executeUpdate(item.getId(),"UPDATE character_items SET count = ? WHERE id = ?", item.getCount());
		executeUpdate(item.getId(), "UPDATE character_items SET count = ?, package = ? WHERE id = ?", item.getCount(), item.isPackage() == false ? 0 : 1);

		item.getLastStatus().updateCount();
	}

	@Override
	public void updateItemDurability(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET durability = ? WHERE id = ?", item
						.get_durability());
		item.getLastStatus().updateDuraility();
	}

	@Override
	public void updateItemChargeCount(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET charge_count = ? WHERE id = ?",
				item.getChargeCount());
		item.getLastStatus().updateChargeCount();
	}

	@Override
	public void updateItemRemainingTime(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET remaining_time = ? WHERE id = ?",
				item.getRemainingTime());
		item.getLastStatus().updateRemainingTime();
	}

	@Override
	public void updateItemEnchantLevel(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET enchantlvl = ? WHERE id = ?", item
						.getEnchantLevel());
		item.getLastStatus().updateEnchantLevel();
	}

	@Override
	public void updateItemEquipped(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET is_equipped = ? WHERE id = ?",
				(item.isEquipped() ? 1 : 0));
		item.getLastStatus().updateEquipped();
	}

	@Override
	public void updateItemIdentified(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET is_id = ? WHERE id = ?", (item
						.isIdentified() ? 1 : 0));
		item.getLastStatus().updateIdentified();
	}

	@Override
	public void updateItemDelayEffect(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET last_used = ? WHERE id = ?", item
						.getLastUsed());
		item.getLastStatus().updateLastUsed();
	}

	@Override
	public void updateItemBless(L1ItemInstance item) throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET bless = ? WHERE id = ?", item
						.getBless());
		item.getLastStatus().updateBless();
	}

	@Override
	public void updateItemAttrEnchantLevel(L1ItemInstance item)
			throws Exception {
		executeUpdate(item.getId(),
				"UPDATE character_items SET attr_enchantlvl = ? WHERE id = ?",
				item.getAttrEnchantLevel());
		item.getLastStatus().updateAttrEnchantLevel();
	}

	@Override
	public int getItemCount(int objId) throws Exception {
		int count = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM character_items WHERE char_id = ?");
			pstm.setInt(1, objId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				count++;
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return count;
	}

	private void executeUpdate(int objId, String sql, int updateNum)
			throws SQLException {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(sql.toString());
			pstm.setInt(1, updateNum);
			pstm.setInt(2, objId);
			pstm.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	private void executeUpdate(int objId, String sql, int updateNum, int updatePackage) throws SQLException {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(sql.toString());
			pstm.setInt(1, updateNum);
			pstm.setInt(2, updatePackage);
			pstm.setInt(3, objId);
			pstm.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	private void executeUpdate(int objId, String sql, Timestamp ts)
			throws SQLException {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(sql.toString());
			pstm.setTimestamp(1, ts);
			pstm.setInt(2, objId);
			pstm.execute();
		} catch (SQLException e) {
			throw e;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
