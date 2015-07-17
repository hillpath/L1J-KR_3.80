// Decompiled by DJ v3.9.9.91 Copyright 2005 Atanas Neshkov  Date: 2007/05/06 22:06:37
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Logins.java

package l1j.server.server;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class Logins {
	private static Logger _log = Logger.getLogger(Logins.class.getName());
	
	
	/**
	 * 접속기 사용 유무체크
	 * @param Ip
	 * 			사용중이라면 true
	 */
	public synchronized boolean ConnectCheck(String Ip, int use) {
		//if(Ip.startsWith("192.168.0.")) return false;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "SELECT count(ip) FROM access_ip WHERE ip=? AND use_yn=? ORDER BY no ASC LIMIT 1";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, Ip);
			pstm.setInt(2, use);
			rs = pstm.executeQuery();
			if(rs.next()){
				if(rs.getInt(1) >= 1) {
					return false;
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally{
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		System.out.println("IP : " + Ip + " >> 등록되어 있지 않습니다.");
		return true;
	}

	public static boolean loginValid(String account, String password,
			String ip, String host) {
		boolean flag1 = false;
		_log.info("Connect from : " + account);

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String pas = null;
			String pas2 = password;

			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con
						.prepareStatement("SELECT password FROM accounts WHERE login=? LIMIT 1");
				pstm.setString(1, account);
				rs = pstm.executeQuery();
				if (rs.next()) {
					pas = rs.getString(1);
					_log.fine("account exists");
				}
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			} catch (Exception e) {
			}

			if (pas == null) {
				if (Config.AUTO_CREATE_ACCOUNTS) {
					con = L1DatabaseFactory.getInstance().getConnection();
					pstm = con
							.prepareStatement("INSERT INTO accounts SET login=?,password=?,lastactive=?,access_level=?,ip=?,host=?");
					pstm.setString(1, account);
					pstm.setString(2, pas2);
					pstm.setLong(3, 0L);
					pstm.setInt(4, 0);
					pstm.setString(5, ip);
					pstm.setString(6, host);
					pstm.execute();
					_log.info("created new account for " + account);
					return true;
				} else {
					_log.warning("account missing for user " + account);
					return false;
				}
			} else if (pas2 == pas) {
				flag1 = true;
			} else {
				flag1 = false;
			}

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return flag1;
	}
}