/*

 */
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1EvaSystem;
import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server:
// IdFactory

public class EvaSystemTable {

	private static Logger _log = Logger.getLogger(EvaSystemTable.class
			.getName());

	private static EvaSystemTable _instance;

	private final Map<Integer, L1EvaSystem> _evasystem = new ConcurrentHashMap<Integer, L1EvaSystem>();

	public static EvaSystemTable getInstance() {
		if (_instance == null) {
			_instance = new EvaSystemTable();
		}
		return _instance;
	}

	private EvaSystemTable() {
		load();
	}

	public static void reload() {
		EvaSystemTable oldInstance = _instance;
		_instance = new EvaSystemTable();
		oldInstance._evasystem.clear();
	}

	private Calendar timestampToCalendar(Timestamp ts) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts.getTime());
		return cal;
	}

	private void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM evasystem");

			rs = pstm.executeQuery();
			L1EvaSystem eva = null;
			while (rs.next()) {
				eva = new L1EvaSystem(rs.getInt(1));
				eva
						.setEvaTime(timestampToCalendar((Timestamp) rs
								.getObject(3)));
				eva.setOpenLocation(rs.getInt(4));
				eva.setMoveLocation(rs.getInt(5));
				eva.setOpenContinuation(rs.getInt(6));

				_evasystem.put(eva.getSystemTypeId(), eva);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "EvaSystemTable[]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 시스템 값을 가져 온다
	 * 
	 * @param id
	 *            1: 균열
	 * @return
	 */
	public L1EvaSystem getSystem(int id) {
		return _evasystem.get(id);
	}

	public void updateSystem(L1EvaSystem eva) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("UPDATE evasystem SET time=?, openLoc=?, moveLoc=?, extend=? WHERE id=?");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String fm = sdf.format(eva.getEvaTime().getTime());
			pstm.setString(1, fm);
			pstm.setInt(2, eva.getOpenLocation());
			pstm.setInt(3, eva.getMoveLocation());
			pstm.setInt(4, eva.getOpenContinuation());
			pstm.setInt(5, eva.getSystemTypeId());
			pstm.execute();

		} catch (SQLException e) {
			_log.log(Level.SEVERE, "EvaSystemTable[]Error1", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

}
