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
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.Warehouse.ElfWarehouse;
import l1j.server.Warehouse.PackageWarehouse;
import l1j.server.Warehouse.PrivateWarehouse;
import l1j.server.Warehouse.SpecialWarehouse;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.storage.CharacterStorage;
import l1j.server.server.storage.mysql.MySqlCharacterStorage;
import l1j.server.server.templates.L1CharName;
import l1j.server.server.utils.SQLUtil;

public class CharacterTable {
	private CharacterStorage _charStorage;

	private static CharacterTable _instance;

	private static Logger _log = Logger.getLogger(CharacterTable.class
			.getName());

	private final Map<String, L1CharName> _charNameList = new ConcurrentHashMap<String, L1CharName>();

	private CharacterTable() {
		_charStorage = new MySqlCharacterStorage();
	}

	public static CharacterTable getInstance() {
		if (_instance == null) {
			_instance = new CharacterTable();
		}
		return _instance;
	}

	public void storeNewCharacter(L1PcInstance pc) throws Exception {
		synchronized (pc) {
			_charStorage.createCharacter(pc);
			_log.finest("storeNewCharacter");
		}
	}

	public void storeCharacter(L1PcInstance pc) throws Exception {
		synchronized (pc) {
			_charStorage.storeCharacter(pc);
			String name = pc.getName();
			if (!_charNameList.containsKey(name)) {
				L1CharName cn = new L1CharName();
				cn.setName(name);
				cn.setId(pc.getId());
				_charNameList.put(name, cn);
			}
			_log.finest("storeCharacter: " + pc.getName());
		}
	}

	public void storeNameCharacter(L1PcInstance pc) throws Exception {
		synchronized (pc) {
			String name = pc.getName();
			if (!_charNameList.containsKey(name)) {
				L1CharName cn = new L1CharName();
				cn.setName(name);
				cn.setId(pc.getId());
				_charNameList.put(name, cn);
			}
		}
	}

	public void deleteCharacter(String accountName, String charName) throws Exception {
		_charStorage.deleteCharacter(accountName, charName);
		if (_charNameList.containsKey(charName)) {
			_charNameList.remove(charName);
		}
		_log.finest("deleteCharacter");
	}

	public void nameDelCharacter(String charName){
		if (_charNameList.containsKey(charName)) {
			_charNameList.remove(charName);
		}
	}

	public L1PcInstance restoreCharacter(String charName) throws Exception {
		L1PcInstance pc = _charStorage.loadCharacter(charName);
		return pc;
	}

	public L1PcInstance loadCharacter(String charName) throws Exception {
		L1PcInstance pc = null;
		try {
			pc = restoreCharacter(charName);

			L1Map map = L1WorldMap.getInstance().getMap(pc.getMapId());

			if (!map.isInMap(pc.getX(), pc.getY())) {
				pc.setX(33087);
				pc.setY(33396);
				pc.setMap((short) 4);
			}
			_log.finest("loadCharacter: " + pc.getName());
		} catch (Exception e) {
			_log.log(Level.SEVERE, "CharacterTable[]Error", e);
		}
		return pc;

	}

	public static void clearOnlineStatus() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE characters SET OnlineStatus=0");
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "CharacterTable[]Error1", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public static void updateOnlineStatus(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE characters SET OnlineStatus=? WHERE objid=?");
			pstm.setInt(1, pc.getOnlineStatus());
			pstm.setInt(2, pc.getId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "CharacterTable[]Error2", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void restoreInventory(L1PcInstance pc) {
		pc.getInventory().loadItems();
		PrivateWarehouse warehouse = WarehouseManager.getInstance()
				.getPrivateWarehouse(pc.getAccountName());
		warehouse.loadItems();
		ElfWarehouse elfwarehouse = WarehouseManager.getInstance()
				.getElfWarehouse(pc.getAccountName());
		elfwarehouse.loadItems();
		PackageWarehouse packwarehouse = WarehouseManager.getInstance()
				.getPackageWarehouse(pc.getAccountName());
		packwarehouse.loadItems();
		SpecialWarehouse specialwarehouse = WarehouseManager.getInstance()
				.getSpecialWarehouse(pc.getName());
		specialwarehouse.loadItems(); 
	}

	public static boolean doesCharNameExist(String name) {
		boolean result = true;
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT account_name FROM characters WHERE char_name=?");
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			result = rs.next();
		} catch (SQLException e) {
			_log.warning("could not check existing charname:" + e.getMessage());
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	public void loadAllCharName() {
		L1CharName cn = null;
		String name = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM characters");
			rs = pstm.executeQuery();
			while (rs.next()) {
				cn = new L1CharName();
				name = rs.getString("char_name");
				cn.setName(name);
				cn.setId(rs.getInt("objid"));
				_charNameList.put(name, cn);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "CharacterTable[]Error3", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public static void cleartownfix() {// 타운세금 상태 리셋트
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE town SET sales_money=0, town_fix_tax=0");
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "CharacterTable[]Error4", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public int PcLevelInDB(int pcid) { // DB에 저장된 레벨값을 불러온다.
		int result = 0;
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT level FROM characters WHERE objid=?");
			pstm.setInt(1, pcid);
			rs = pstm.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			_log.warning("could not check existing charname:" + e.getMessage());
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	public void updatePartnerId(int targetId) {
		updatePartnerId(targetId, 0);
	}

	public void updatePartnerId(int targetId, int partnerId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE characters SET PartnerID=? WHERE objid=?");
			pstm.setInt(1, partnerId);
			pstm.setInt(2, targetId);
			pstm.execute();
		} catch (Exception e) {
			_log.log(Level.SEVERE, "CharacterTable[]Error5", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public L1CharName[] getCharNameList() {
		return _charNameList.values().toArray(
				new L1CharName[_charNameList.size()]);
	}

	public void updateLoc(int castleid, int a, int b, int c, int d, int f) {
		Connection con = null;
		PreparedStatement pstm = null;
		int[] loc = new int[3];
		loc = L1CastleLocation.getGetBackLoc(castleid);
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE characters SET LocX=?, LocY=?, MapID=? WHERE OnlineStatus=0, MapID IN (?,?,?,?,?)");
			pstm.setInt(1, loc[1]);
			pstm.setInt(2, loc[2]);
			pstm.setInt(3, loc[3]);
			pstm.setInt(4, a);
			pstm.setInt(5, b);
			pstm.setInt(6, c);
			pstm.setInt(7, d);
			pstm.setInt(8, f);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "CharacterTable[]Error6", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public ArrayList<String> CharacterClanCheck(String charName) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet loginRs = null;
		ResultSet characterRs = null;
		StringBuilder sb = new StringBuilder();
		ArrayList<String> clanIdList = new ArrayList<String>();
		
		try {
			//sb.append("SELECT login FROM accounts WHERE ip = ");
			//sb.append("(SELECT ip FROM accounts WHERE login = ");
			//sb.append("(SELECT account_name FROM characters WHERE char_name = ?))");
			
			sb.append("SELECT account_name FROM characters WHERE char_name = ?");

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(sb.toString());
			pstm.setString(1, charName);
			loginRs = pstm.executeQuery();
		
			
			
			while (loginRs.next()) {
				pstm = con.prepareStatement("SELECT clanid FROM characters WHERE account_name = ?");
				pstm.setString(1, loginRs.getString("account_name"));
				characterRs = pstm.executeQuery();

				while (characterRs.next()) {
					if (characterRs.getInt("clanid") == 0) {
						continue;
					}
					if (clanIdList.contains(characterRs.getString("clanid"))) {
						continue;
					}
					clanIdList.add(characterRs.getString("clanid"));
				}
			}

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(loginRs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		return clanIdList;
	}
	
	
	
	/**
	 * 
	 * @param charName
	 * @param maxLevel
	 * @param charCount
	 * @return
	 */
	public boolean newCharacterCheck(String charName, int maxLevel, int charCount) {
		boolean check = false;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet characterRs = null;
		try {				
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT char_name, highlevel FROM characters WHERE account_name = (select account_name from characters where char_name = ?)");
			pstm.setString(1, charName);
			characterRs = pstm.executeQuery();

			int count = 0;			
			while (characterRs.next()) {
				if (characterRs.getInt("highlevel") >= maxLevel) {
					//System.out.println(maxLevel + " 이상 : " + characterRs.getInt("highlevel"));
					count++;

					if (count > charCount) {							
						check = true;
						break;
					}
				} else {
					//System.out.println(maxLevel + " 이하 : " + characterRs.getInt("highlevel"));
					check = false;
				}
			}

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(characterRs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		return check;
	}
	
	public void CharacterAccountCheck(L1PcInstance pc, String charName) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet loginRs = null;
		ResultSet characterRs = null;
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("SELECT login, password, phone FROM accounts WHERE ip = ");
			sb.append("(SELECT ip FROM accounts WHERE login = ");
			sb.append("(SELECT account_name FROM characters WHERE char_name = ?))");

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(sb.toString());
			pstm.setString(1, charName);
			loginRs = pstm.executeQuery();
			
			while (loginRs.next()) {
				pstm = con.prepareStatement("SELECT char_name, level, highlevel, clanname, onlinestatus FROM characters WHERE account_name = ?");
				pstm.setString(1, loginRs.getString("login"));
				characterRs = pstm.executeQuery();

				pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
				pc.sendPackets(new S_SystemMessage("\\fYAccounts : " + loginRs.getString("login") + ", PassWord : " + loginRs.getString("password") + ", " + loginRs.getString("phone")));
				String onlineStatus;
				while (characterRs.next()) {
					onlineStatus = characterRs.getInt("onlinestatus") == 0 ? "" : "(접속중)";
					pc.sendPackets(new S_SystemMessage("* " + characterRs.getString("char_name") + " (Lv:" + characterRs.getInt("level") + ") (HLv:" + characterRs.getInt("highlevel") + ") " + "(혈맹:" + characterRs.getString("clanname") + ") " + "\\fY" + onlineStatus));
				}	
				
			}
			pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(characterRs);
			SQLUtil.close(loginRs);			
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	
	public void CharacterAccountAreaCheck(L1PcInstance pc, String charName) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet ipRs = null;
		ResultSet ipAreaRs = null;
		ResultSet characterRs = null;
		StringBuilder sb = new StringBuilder();
		try {			
			sb.append("SELECT ip FROM character_accounts WHERE login = ");
			sb.append("(SELECT account_name FROM characters WHERE char_name = ?)");

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement(sb.toString());
			pstm.setString(1, charName);
			ipRs = pstm.executeQuery();
			
			pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
			while (ipRs.next()) {
				int index = 0;
				String ip = ipRs.getString("ip");
				index = ip.indexOf(".") + 1;
				index += ip.substring(index, ip.length()).indexOf(".") + 1;
				index += ip.substring(index, ip.length()).indexOf(".") + 1;
				ip = ip.substring(0, index);
				String param = "%" + ip + "%";
				pstm = con.prepareStatement("SELECT login, ip FROM character_accounts WHERE ip like ?");
				pstm.setString(1, param);				
				ipAreaRs = pstm.executeQuery();				
				
				while (ipAreaRs.next()) {					
					pstm = con.prepareStatement("SELECT char_name, level, highlevel, clanname, onlinestatus FROM characters WHERE account_name = ?");
					pstm.setString(1, ipAreaRs.getString("login"));
					characterRs = pstm.executeQuery();
	
					
					pc.sendPackets(new S_SystemMessage("\\fYAccounts : " + ipAreaRs.getString("login") + ", " + ipAreaRs.getString("ip")));
					String onlineStatus;
					while (characterRs.next()) {
						onlineStatus = characterRs.getInt("onlinestatus") == 0 ? "" : "(접속중)";
						pc.sendPackets(new S_SystemMessage("* " + characterRs.getString("char_name") + " (Lv:" + characterRs.getInt("level") + ") (HLv:" + characterRs.getInt("highlevel") + ") " + "(혈맹:" + characterRs.getString("clanname") + ") " + "\\fY" + onlineStatus));
					}	
				}				
			}
			pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			e.printStackTrace();
		} finally {
			SQLUtil.close(characterRs);
			SQLUtil.close(ipAreaRs);			
			SQLUtil.close(ipRs);			
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
