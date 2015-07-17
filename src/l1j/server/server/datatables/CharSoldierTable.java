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
import javolution.util.FastTable;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1CharSoldier;
import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server:
// IdFactory

public class CharSoldierTable {

	private static Logger _log = Logger.getLogger(CharSoldierTable.class
			.getName());

	private static CharSoldierTable _instance;

	private final FastTable<L1CharSoldier> _charsoldier = new FastTable<L1CharSoldier>();

	public static CharSoldierTable getInstance() {
		if (_instance == null) {
			_instance = new CharSoldierTable();
		}
		return _instance;
	}

	public CharSoldierTable() {
		charSoldierload();
	}

	private void charSoldierload() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_soldier");

			rs = pstm.executeQuery();
			L1CharSoldier charSoldier = null;
			while (rs.next()) {
				charSoldier = new L1CharSoldier(rs.getInt(1));
				charSoldier.setSoldierNpc(rs.getInt(2));
				charSoldier.setSoldierCount(rs.getInt(3));
				charSoldier.setSoldierCastleId(rs.getInt(4));
				charSoldier.setSoldierTime(rs.getInt(5));

				_charsoldier.add(charSoldier);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "CharSoldierTable[:charSoldierload:]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void addCharSoldier(L1CharSoldier newCharSoldier) {
		_charsoldier.add(newCharSoldier);
	}

	public FastTable<L1CharSoldier> getCharSoldier(int id, int currentTime) {
		FastTable<L1CharSoldier> list = new FastTable<L1CharSoldier>();
		L1CharSoldier t;
		for (int i = 0; i < _charsoldier.size(); i++) {
			t = _charsoldier.get(i);
			if (t.getCharId() == id && t.getSoldierTime() < currentTime)
				list.add(t);
		}
		t = null;

		return list;
	}

	public void storeCharSoldier(L1CharSoldier newCharSoldier) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO character_soldier SET char_id=?, npc_id=?, count=?, castle_id=?, time=?");
			pstm.setInt(1, newCharSoldier.getCharId());
			pstm.setInt(2, newCharSoldier.getSoldierNpc());
			pstm.setInt(3, newCharSoldier.getSoldierCount());
			pstm.setInt(4, newCharSoldier.getSoldierCastleId());
			pstm.setInt(5, newCharSoldier.getSoldierTime());
			pstm.execute();

			addCharSoldier(newCharSoldier);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "CharSoldierTable[:storeCharSoldier:]Error", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 캐릭터가 가지고 있는 용병 갯수를 가져온다.
	 * 
	 * @param id
	 *            는 char_id
	 * @return Cscount 캐릭터가 가지고 있는 용병 갯수
	 */

	public int SoldierCalculate(int id) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int CScount = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT count FROM character_soldier WHERE char_id=?");
			pstm.setInt(1, id);
			rs = pstm.executeQuery();
			while (rs.next()) {
				CScount += (rs.getInt("count"));
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "CharSoldierTable[:SoldierCalculate:]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return CScount;
	}

	/**
	 * 전쟁후 해당성의 용병을 모두 클리어 한다.
	 * 
	 * @param castleid
	 */
	public void delCastleSoldier(int castleid) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM character_soldier WHERE castle_id=?");
			pstm.setInt(1, castleid);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "CharSoldierTable[:delCastleSoldier:]Error", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		L1CharSoldier c;
		for (int i = 0; i < _charsoldier.size(); i++) {
			c = _charsoldier.get(i);
			if (c.getSoldierCastleId() == castleid) {
				_charsoldier.remove(c);
			}
		}
	}
}