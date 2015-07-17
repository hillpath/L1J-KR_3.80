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
import l1j.server.server.model.Instance.L1ModelInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.L1SpawnUtil;
import l1j.server.server.utils.SQLUtil;

public class ModelSpawnTable {
	private static Logger _log = Logger.getLogger(ModelSpawnTable.class
			.getName());

	private static ModelSpawnTable _instance;

	public static ModelSpawnTable getInstance() {
		if (_instance == null) {
			_instance = new ModelSpawnTable();
		}
		return _instance;
	}

	private ModelSpawnTable() {
		ModelInsertWorld();
	}

	public void ModelInsertWorld() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_model");
			rs = pstm.executeQuery();
			do {
				if (!rs.next()) {
					break;
				}
				L1Npc l1npc = NpcTable.getInstance().getTemplate(rs.getInt(2));
				if (l1npc != null) {
					L1ModelInstance field;
					try {
						field = (L1ModelInstance) (NpcTable.getInstance()
								.newNpcInstance(rs.getInt(2)));
						field.setId(ObjectIdFactory.getInstance().nextId());
						field.setX(rs.getInt("locx"));
						field.setY(rs.getInt("locy"));
						field.setMap((short) rs.getInt("mapid"));
						field.setHomeX(field.getX());
						field.setHomeY(field.getY());
						field.getMoveState().setHeading(rs.getInt("heading"));
						field.setLightSize(l1npc.getLightSize());
						field.getLight().turnOnOffLight();

						// L1World.getInstance().storeObject(field);
						L1World.getInstance().addVisibleObject(field);
					} catch (Exception e) {
						_log.log(Level.SEVERE, "ModelSpawnTable[]Error", e);
					}
				}
			} while (true);
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "ModelSpawnTable[]Error1", e);
		} catch (SecurityException e) {
			_log.log(Level.SEVERE, "ModelSpawnTable[]Error2", e);
		} catch (IllegalArgumentException e) {
			_log.log(Level.SEVERE, "ModelSpawnTable[]Error3", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void insertmodel(L1PcInstance pc, int type) {
		Connection con = null;
		PreparedStatement pstm = null;
		int npcid = 0;
		int x = pc.getX();
		int y = pc.getY();
		switch (type) {
		case 0:
			npcid = 4500318;
			break;
		case 1:
			npcid = 4500317;
			x += 1;
			y -= 1;
			break;
		default:
			break;
		}
		int floor = pc.getMapId() - 100;
		String note = "¿À¸¸(" + floor + "Ãþ) È¶ºÒ";

		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO spawnlist_model SET npcid=?,note=?,locx=?,locy=?,mapid=?");

			pstm.setInt(1, npcid);
			pstm.setString(2, note);
			pstm.setInt(3, x);
			pstm.setInt(4, y);
			pstm.setInt(5, pc.getMapId());
			pstm.execute();

			L1SpawnUtil.spawn2(x, y, (short) pc.getMapId(), npcid, 0, 0, 0);
		} catch (Exception e) {
			_log.log(Level.SEVERE, "ModelSpawnTable[]Error4", e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

	}
}
