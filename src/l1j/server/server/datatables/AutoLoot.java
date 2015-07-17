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
import l1j.server.server.utils.SQLUtil;

/*
drop table autoloot;
create table autoloot (item_id int unsigned not null, primary key(item_id));
insert into autoloot values
(40308),
(40087),
(40074),
(140074),
(140087),
(240074),
(240087),
(90000),
(90001),
(140100),
(40076),
(40466),
(40044),
(40045),
(40046),
(40047),
(40048),
(40049),
(40050),
(40051),
(40052),
(40053),
(40054),
(40055);
*/


public class AutoLoot {

    private static Logger _log = Logger.getLogger(AutoLoot.class.getName());

    private static AutoLoot _instance;

	private static FastTable<Integer> _idlist = new FastTable<Integer>();

    public static AutoLoot getInstance()
    {
        if (_instance == null) {
            _instance = new AutoLoot();
        }
        return _instance;
    }

    private AutoLoot()
    {
        _idlist = allIdList();
    }

	private FastTable<Integer> allIdList()
	{
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		
		FastTable<Integer> idlist = new FastTable<Integer>();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from autoloot");
			rs = pstm.executeQuery();
			while (rs.next()) {
				idlist.add(rs.getInt("item_id"));
			}

		} catch (SQLException e) {
            _log.log(Level.SEVERE, "AutoLoot[]Error", e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }

		return idlist;
	}
	
	public void storeId(int itemid)
	{
		int index = _idlist.indexOf(itemid);
		if (index != -1)
			return;

        Connection con = null;
        PreparedStatement pstm = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("INSERT INTO autoloot SET item_id=?");
            pstm.setInt(1, itemid);
            pstm.execute();
			_idlist.add(itemid);
        } catch (Exception e) {
            NpcTable._log.log(Level.SEVERE, "AutoLoot[:storeId:]Error", e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
	}

	public void deleteId(int itemid)
	{
		Connection con = null;
		PreparedStatement pstm = null;
		int index = _idlist.indexOf(itemid);
		if (index == -1)
			return;
	
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("DELETE FROM autoloot WHERE item_id=?");
			pstm.setInt(1, itemid);
			pstm.execute();
			_idlist.remove(index);
		} catch (Exception e) {
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void reload()
	{
		_idlist.clear();
		_idlist = allIdList();
	}

	public FastTable<Integer> getIdList()
	{
		return _idlist;
	}

	public boolean isAutoLoot(int itemId)
	{
		for (int id : _idlist) {
			if (itemId == id) {
				return true;
			}
		}
		return false;
	}
}
