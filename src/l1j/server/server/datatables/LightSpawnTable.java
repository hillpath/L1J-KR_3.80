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
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.SQLUtil;

public class LightSpawnTable {
	private static Logger _log = Logger.getLogger(LightSpawnTable.class
			.getName());

	private static LightSpawnTable _instance;

	public static LightSpawnTable getInstance() {
		if (_instance == null) {
			_instance = new LightSpawnTable();
		}
		return _instance;
	}

	private LightSpawnTable() {
	}

	public void FillLightSpawnTable() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_light");
			rs = pstm.executeQuery();
			do {
				if (!rs.next()) {
					break;
				}
				L1Npc l1npc = NpcTable.getInstance().getTemplate(rs.getInt(2));
				if (l1npc != null) {
					L1FieldObjectInstance field;
					try {
						field = (L1FieldObjectInstance) (NpcTable.getInstance()
								.newNpcInstance(rs.getInt(2)));
						field.setId(ObjectIdFactory.getInstance().nextId());
						field.setX(rs.getInt("locx"));
						field.setY(rs.getInt("locy"));
						field.setMap((short) rs.getInt("mapid"));
						field.setHomeX(field.getX());
						field.setHomeY(field.getY());
						field.getMoveState().setHeading(0);
						field.setLightSize(l1npc.getLightSize());
						field.getLight().turnOnOffLight();

						L1World.getInstance().storeObject(field);
						L1World.getInstance().addVisibleObject(field);
					} catch (Exception e) {
						_log.log(Level.SEVERE, "LightSpawnTable[]Error", e);
					}
				}
			} while (true);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "LightSpawnTable[]Error1", e);
		} catch (SecurityException e) {
			_log.log(Level.SEVERE, "LightSpawnTable[]Error2", e);
		}/*
			 * catch (ClassNotFoundException e) { _log.log(Level.SEVERE,
			 * e.getLocalizedMessage(), e); }
			 */catch (IllegalArgumentException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} /*
			 * catch (InstantiationException e) { _log.log(Level.SEVERE,
			 * e.getLocalizedMessage(), e); } catch (IllegalAccessException e) {
			 * _log.log(Level.SEVERE, e.getLocalizedMessage(), e); } catch
			 * (InvocationTargetException e) { _log.log(Level.SEVERE,
			 * e.getLocalizedMessage(), e); }
			 */finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
