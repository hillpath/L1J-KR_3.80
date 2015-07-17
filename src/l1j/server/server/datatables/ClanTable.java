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
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javolution.util.FastMap;

import l1j.server.L1DatabaseFactory;
import l1j.server.Warehouse.ClanWarehouse;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server:
// IdFactory

public class ClanTable {
	private static Logger _log = Logger.getLogger(ClanTable.class.getName());

	private static ClanTable _instance;

	private final FastMap<Integer, L1Clan> _clans = new FastMap<Integer, L1Clan>();

	private final FastMap<Integer, L1Clan> _clancastle = new FastMap<Integer, L1Clan>();

	public static ClanTable getInstance() {
		if (_instance == null) {
			_instance = new ClanTable();
		}
		return _instance;
	}

	private ClanTable() {
		{
			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;

			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con
						.prepareStatement("SELECT * FROM clan_data ORDER BY clan_id");

				rs = pstm.executeQuery();
				L1Clan clan = null;
				while (rs.next()) {
					clan = new L1Clan();
					int clan_id = rs.getInt(1);
					int castle_id = rs.getInt(5);
					clan.setClanId(clan_id);
					clan.setClanName(rs.getString(2));
					clan.setLeaderId(rs.getInt(3));
					clan.setLeaderName(rs.getString(4));
					clan.setCastleId(castle_id);
					clan.setHouseId(rs.getInt(6));
					clan.setAlliance(rs.getInt(7));
					clan.setClanBirthDay(rs.getString(8));
					/**Ç÷¸ÍÀÚµ¿°¡ÀÔ*/
					clan.setBot(rs.getString(9).equalsIgnoreCase("true"));
					clan.setBotStyle(rs.getInt(10));
					clan.setBotLevel(rs.getInt(11));
					/**Ç÷¸ÍÀÚµ¿°¡ÀÔ*/
					clan.setOnlineMaxUser(rs.getInt(12));
					L1World.getInstance().storeClan(clan);
					_clans.put(clan_id, clan);
					if (castle_id > 0) {
						_clancastle.put(castle_id, clan);
					}
				}

			} catch (SQLException e) {
				_log.log(Level.SEVERE, "ClanTable[]Error", e);
			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}

		String name;
		int rank;
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;

			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con
						.prepareStatement("SELECT char_name, ClanRank FROM characters WHERE ClanID = ?");
				pstm.setInt(1, clan.getClanId());
				rs = pstm.executeQuery();

				while (rs.next()) {

					name = rs.getString("char_name");
					rank = rs.getInt(2);

					clan.addClanMember(name, rank);
				}
			} catch (SQLException e) {
				_log.log(Level.SEVERE, "ClanTable[]Error1", e);
			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}

		ClanWarehouse clanWarehouse;
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(
					clan.getClanName());
			clanWarehouse.loadItems();
		}
	}

	public L1Clan createClan(L1PcInstance player, String clan_name) {
		for (L1Clan oldClans : L1World.getInstance().getAllClans()) {
			if (oldClans.getClanName().equalsIgnoreCase(clan_name)) {
				return null;
			}
		}
		L1Clan clan = new L1Clan();
		clan.setClanId(ObjectIdFactory.getInstance().nextId());
		clan.setClanName(clan_name);
		clan.setLeaderId(player.getId());
		clan.setLeaderName(player.getName());
		clan.setCastleId(0);
		clan.setHouseId(0);
		clan.setAlliance(0);
clan.setClanBirthDay(currentTime());
		Connection con = null;
		PreparedStatement pstm = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO clan_data SET clan_id=?, clan_name=?, leader_id=?, leader_name=?, hascastle=?, hashouse=?, alliance=?, clan_birthday=?, max_online_user=?");
			pstm.setInt(1, clan.getClanId());
			pstm.setString(2, clan.getClanName());
			pstm.setInt(3, clan.getLeaderId());
			pstm.setString(4, clan.getLeaderName());
			pstm.setInt(5, clan.getCastleId());
			pstm.setInt(6, clan.getHouseId());
			pstm.setInt(7, clan.getAlliance());
			pstm.setString(8, clan.getClanBirthDay());
			pstm.setInt(9, clan.getOnlineMaxUser());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "ClanTable[]Error2", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		L1World.getInstance().storeClan(clan);
		_clans.put(clan.getClanId(), clan);

		player.setClanid(clan.getClanId());
		player.setClanname(clan.getClanName());
		player.setClanRank(L1Clan.CLAN_RANK_PRINCE);
		clan.addClanMember(player.getName(), player.getClanRank());
		try {
			player.save();
		} catch (Exception e) {
			_log.log(Level.SEVERE, "ClanTable[]Error3", e);
		}
		return clan;
	}

	public void updateClan(L1Clan clan) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE clan_data SET clan_id=?, leader_id=?, leader_name=?, hascastle=?, hashouse=?, alliance=?, clan_birthday=?, bot_style=?, bot_level=?, max_online_user=? WHERE clan_name=?");
			pstm.setInt(1, clan.getClanId());
			pstm.setInt(2, clan.getLeaderId());
			pstm.setString(3, clan.getLeaderName());
			pstm.setInt(4, clan.getCastleId());
			pstm.setInt(5, clan.getHouseId());
			pstm.setInt(6, clan.getAlliance());
			pstm.setString(7, clan.getClanBirthDay());
	/**Ç÷¸ÍÀÚµ¿°¡ÀÔ*/
			pstm.setInt(8, clan.getBotStyle());
			pstm.setInt(9, clan.getBotLevel());
	/**Ç÷¸ÍÀÚµ¿°¡ÀÔ*/
			pstm.setInt(10, clan.getOnlineMaxUser());
			pstm.setString(11, clan.getClanName());
			
			
			
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "ClanTable[]Error4", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	/**
     **Ç÷¸ÍÀÚµ¿°¡ÀÔ*
	 * @param player
	 * @param clan_name
	 * @param style
	 * @return
	 */
	public void createClanBot(L1PcInstance player, String clan_name, int style){
		for (L1Clan oldClans : L1World.getInstance().getAllClans()) {
			if (oldClans.getClanName().equalsIgnoreCase(clan_name))
				return;
		}
		
		L1Clan clan = new L1Clan();
		clan.setClanId(ObjectIdFactory.getInstance().nextId());
		clan.setClanName(clan_name);
		clan.setLeaderId(player.getId());
		clan.setLeaderName(player.getName());
		clan.setCastleId(0);
		clan.setHouseId(0);
		clan.setBot(true);
		clan.setBotStyle(style);
		
		player.setClanid(clan.getClanId());
		player.setClanname(clan.getClanName());

		Connection con = null;
		PreparedStatement pstm = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO clan_data SET clan_id=?, clan_name=?, leader_id=?, leader_name=?, hascastle=?, hashouse=?, bot=?, bot_style=?");
			pstm.setInt(1, clan.getClanId());
			pstm.setString(2, clan.getClanName());
			pstm.setInt(3, clan.getLeaderId());
			pstm.setString(4, clan.getLeaderName());
			pstm.setInt(5, clan.getCastleId());
			pstm.setInt(6, clan.getHouseId());
			pstm.setString(7, "true");
			pstm.setInt(8, style);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "ClanTable[]Error5", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	/**Ç÷¸ÍÀÚµ¿°¡ÀÔ*/
		L1World.getInstance().storeClan(clan);
		_clans.put(clan.getClanId(), clan);
			/**Ç÷¸ÍÀÚµ¿°¡ÀÔ*/
	} 

	public void deleteClan(String clan_name) {
		L1Clan clan = L1World.getInstance().getClan(clan_name);
		if (clan == null) {
			return;
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM clan_data WHERE clan_name=?");
			pstm.setString(1, clan_name);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "ClanTable[]Error6", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		ClanWarehouse clanWarehouse = WarehouseManager.getInstance()
				.getClanWarehouse(clan.getClanName());
		clanWarehouse.clearItems();
		clanWarehouse.deleteAllItems();

		L1World.getInstance().removeClan(clan);
		_clans.remove(clan.getClanId());
	}

	public L1Clan getTemplate(int clan_id) {
		return _clans.get(clan_id);
	}
	/**Ç÷¸ÍÀÚµ¿°¡ÀÔ*/
	public static void reload() {
		ClanTable oldInstance = _instance;
		_instance = new ClanTable();
		if (oldInstance != null) {
			oldInstance._clans.clear();
			oldInstance._clancastle.clear();
		}
	}
	

	public L1Clan find(String clan_name){
		for(L1Clan clan : _clans.values()){
			if(clan.getClanName().equalsIgnoreCase(clan_name))
				return clan;
		}
		return null;
	}
		/**Ç÷¸ÍÀÚµ¿°¡ÀÔ*/
	public FastMap<Integer, L1Clan> getClanCastles() {
		return _clancastle;
	}
	private static String currentTime() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9"));
		int year = cal.get(Calendar.YEAR);
		int Month = cal.get(Calendar.MONTH) + 1;
		String Month2 = null;
		if (Month < 10) {
			Month2 = "0" + Month;
		} else {
			Month2 = Integer.toString(Month);
		}
		int date = cal.get(Calendar.DATE);
		String date2 = null;
		if (date < 10) {
			date2 = "0" + date;
		} else {
			date2 = Integer.toString(date);
		}
		return year + "/" + Month2 + "/" + date2;
	}
}
