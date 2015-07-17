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
 * Author: ChrisLiu.2007.07.20
 */
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

// import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server:
// IdFactory

public class ClanWarehouseList {

	private static Logger _log = Logger.getLogger(ClanWarehouseList.class.getName());

	private static ClanWarehouseList _instance;

	public static ClanWarehouseList getInstance() {
		if (_instance == null) {
			_instance = new ClanWarehouseList();
		}
		return _instance;
	}
	
	private ClanWarehouseList(){}

	public String ClanWarehouseList(int id) {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			ps = con.prepareStatement("SELECT * FROM clan_warehouse_list WHERE clanid = ? order by id desc");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while (rs.next()) {
				sb.append(rs.getString("list")+"[ ½Ã°£ ] "+rs.getString("date")+"\n\n");
			}
			
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "ClanWarehouseList[]Error", e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(rs);
			SQLUtil.close(con);
		}
		return sb.toString();
	}

	public synchronized void addList(int id, String text, String d) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO clan_warehouse_list SET clanid=?, list=?, date=?");
			pstm.setInt(1, id);
			pstm.setString(2, text);
			pstm.setString(3, d);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "ClanWarehouseList[]Error1", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void removeClanList(int id) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM clan_warehouse_list WHERE clanid=?");
			pstm.setInt(1, id);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "ClanWarehouseList[]Error2", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
