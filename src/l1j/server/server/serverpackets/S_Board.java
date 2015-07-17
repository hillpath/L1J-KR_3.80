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

package l1j.server.server.serverpackets;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.BoardTable;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.utils.SQLUtil;

public class S_Board extends ServerBasePacket {

	private static final String S_BOARD = "[S] S_Board";

	private static Logger _log = Logger.getLogger(S_Board.class.getName());

	private byte[] _byte = null;

	public S_Board(L1NpcInstance board) {
		if (board.getNpcId() == 4212014)
			buildPacket2(board, 0);
		else if (board.getNpcId() == 42000201)
			buildPacketUser(board, 0);
		else if (board.getNpcId() == 42000161)
			buildPacketHunt(board, 0);
		else
			buildPacket(board, 0);
	}

	public S_Board(L1NpcInstance board, int number) {
		buildPacket(board, number);
	}
	
	

	private void buildPacket(L1NpcInstance board, int number) {
		int count = 0;
		String[][] db = null;
		int[] id = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			db = new String[8][3];
			id = new int[8];
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM board order by id desc");
			rs = pstm.executeQuery();
			while (rs.next() && count < 8) {
				if (board.getNpcId() != rs.getInt(6)) {
					continue;
				}
				if (rs.getInt("id") <= number || number == 0) {
					id[count] = rs.getInt(1);
					db[count][0] = rs.getString(2);// 이름
					db[count][1] = rs.getString(3);// 날짜
					db[count][2] = rs.getString(4);// 제목
					count++;
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "S_Board[]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0);// type
		writeD(board.getId());
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0x7F); // ?
		writeH(count);
		writeH(300);
		for (int i = 0; i < count; ++i) {
			writeD(id[i]);
			writeS(db[i][0]);// 이름
			writeS(db[i][1]);// 날짜
			writeS(db[i][2]);// 제목
		}
	}

	private void buildPacket2(L1NpcInstance board, int number) {// 드래곤키 알림 게시판
		int count = 0;
		long a = 0;
		String[][] db = null;
		int[] id = null;
		int[] time = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			db = new String[8][2];
			id = new int[8];
			time = new int[8];
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM board order by id desc");
			rs = pstm.executeQuery();
			while (rs.next() && count < 8) {
				if (board.getNpcId() != rs.getInt(6)) {
					continue;
				}
				a = rs.getTimestamp(7).getTime() - System.currentTimeMillis();
				if (a < 0) {
					BoardTable.getInstance().delDayExpire(rs.getInt(8));
					continue;
				}
				if (rs.getInt("id") <= number || number == 0) {
					id[count] = rs.getInt(1);
					db[count][0] = rs.getString(2);// 이름
					db[count][1] = rs.getString(3);// 날짜
					time[count] = (int) a / 60000 * 60;
					count++;
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "S_Board[]Error1", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(1);// type
		writeD(board.getId());
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0x7F); // ?
		writeC(count);
		for (int i = 0; i < count; ++i) {
			writeD(id[i]);
			writeS(db[i][0]);// 이름
			writeS(db[i][1]);// 날짜
			writeD(time[i]);
		}
	}

	private void buildPacketUser(L1NpcInstance board, int number) {
		int count = 0;
		String[][] db = null;
		int[] id = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			db = new String[8][3];
			id = new int[8];
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM board_user order by id desc");
			rs = pstm.executeQuery();
			while (rs.next() && count < 8) {
				if (rs.getInt("id") <= number || number == 0) {
					id[count] = rs.getInt(1);
					db[count][0] = "[***]님이";
					db[count][1] = rs.getString(3);
					db[count][2] = "고정신청 합니다.";
					count++;
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "S_Board[]Error2", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0);
		writeD(board.getId());
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0x7F); // ?
		writeH(count);
		writeH(300);
		for (int i = 0; i < count; ++i) {
			writeD(id[i]);
			writeS(db[i][0]);
			writeS(db[i][1]);
			writeS(db[i][2]);
		}
	}

	private void buildPacketHunt(L1NpcInstance board, int number) {
		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0x00); //
		writeD(board.getId());
		writeC(0xFF);
		writeC(0xFF);
		writeC(0xFF);
		writeC(0x7F);
		Connection c = null;
		PreparedStatement p = null;
		PreparedStatement pp = null;
		ResultSet r = null;
		ResultSet rr = null;
		try {
			c = L1DatabaseFactory.getInstance().getConnection();
			pp = c
					.prepareStatement("select count(*) as cnt from characters where HuntCount=1");
			rr = pp.executeQuery();
			int count = 0;
			if (rr.next())
				count = rr.getInt("cnt");
			p = c
					.prepareStatement("select * from characters where HuntPrice > 0 order by account_name desc");
			r = p.executeQuery();
			writeH(count);
			writeH(300);
			while (r.next()) {
				writeD(r.getInt(2));
				writeS("");
				writeS("");
				writeS("공개수배 -> " + r.getString(3) + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rr);
			SQLUtil.close(pp);
			SQLUtil.close(r);
			SQLUtil.close(p);
			SQLUtil.close(c);
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_BOARD;
	}
}
