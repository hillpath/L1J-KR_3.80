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
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1LittleBugInstance;
import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server:
// RaceRecordTable

public class RaceRecordTable {

	private static Logger _log = Logger.getLogger(RaceRecordTable.class
			.getName());

	private static RaceRecordTable _instance;

	public RaceRecordTable() {
	}

	public static RaceRecordTable getInstance() {
		if (_instance == null) {
			_instance = new RaceRecordTable();
		}
		return _instance;
	}

	public void updateRaceRecord(int number, int win, int lose) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE race_record SET win=?,lose=? WHERE number=?");
			pstm.setInt(1, win);
			pstm.setInt(2, lose);
			pstm.setInt(3, number);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void getRaceRecord(int number, L1LittleBugInstance bug) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT win, lose FROM race_record WHERE number=?");
			pstm.setInt(1, number);
			rs = pstm.executeQuery();
			if (rs.next()) {
				bug.setWin(rs.getInt("win"));
				bug.setLose(rs.getInt("lose"));
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}