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
package l1j.server.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class ObjectIdFactory {
	private static Logger _log = Logger.getLogger(ObjectIdFactory.class
			.getName());

	private static ObjectIdFactory uniqueInstance;

	private static final int FIRST_ID = 0x10000000;

	private int _curId;

	private ObjectIdFactory() {
		loadState();
	}

	public static ObjectIdFactory getInstance() {
		if (uniqueInstance != null) {
			return uniqueInstance;
		}

		return createInstance();
	}

	public static ObjectIdFactory createInstance() {
		if (uniqueInstance == null) {
			synchronized (ObjectIdFactory.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new ObjectIdFactory();
				}
			}
		}
		return uniqueInstance;
	}

	public synchronized int nextId() {
		return _curId++;
	}

	private void loadState() {
		// DB로부터 MAXID를 요구한다
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("select max(id)+1 as nextid from (select id from character_items union all select id from character_teleport union all select id from character_warehouse union all select id from character_elf_warehouse union all select id from character_package_warehouse union all select objid as id from characters union all select clan_id as id from clan_data union all select id from clan_warehouse union all select objid as id from pets) t");
			rs = pstm.executeQuery();

			int id = 0;
			if (rs.next()) {
				id = rs.getInt("nextid");
			}
			if (id < FIRST_ID) {
				id = FIRST_ID;
			}
			_curId = id;

			System.out.println("[System] Current Object ID: " + _curId);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "ObjectIdFactory Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
