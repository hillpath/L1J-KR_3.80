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
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.utils.SQLUtil;

public class S_BoardRead extends ServerBasePacket {

	private static final String S_BoardRead = "[C] S_BoardRead";

	private static Logger _log = Logger.getLogger(S_BoardRead.class.getName());

	private byte[] _byte = null;

	public S_BoardRead(L1NpcInstance board, int number) {
		if (board.getNpcId() == 42000201)
			buildPacketUser(board, number);
		else if (board.getNpcId() == 42000161)
			buildPacketHunt(board, number);
		else
			buildPacket(board, number);
	}

	private void buildPacket(L1NpcInstance board, int number) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM board WHERE id=?");
			pstm.setInt(1, number);
			rs = pstm.executeQuery();
			while (rs.next()) {
				writeC(Opcodes.S_OPCODE_BOARDREAD);
				writeD(number);
				writeS(rs.getString(2));
				writeS(rs.getString(4));
				writeS(rs.getString(3));
				writeS(rs.getString(5));
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "S_BoardRead[]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void buildPacketUser(L1NpcInstance board, int number) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM board_user WHERE id=?");
			pstm.setInt(1, number);
			rs = pstm.executeQuery();
			while (rs.next()) {
				writeC(Opcodes.S_OPCODE_BOARDREAD);
				writeD(number);
				writeS(rs.getString(2));
				writeS(rs.getString(4));
				writeS(rs.getString(3));
				writeS(rs.getString(5));
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "S_BoardRead[]Error1", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void buildPacketHunt(L1NpcInstance board, int number) {
		writeC(Opcodes.S_OPCODE_BOARDREAD);
		writeD(number);
		Connection c = null;
		PreparedStatement p = null;
		ResultSet r = null;
		try {
			c = L1DatabaseFactory.getInstance().getConnection();
			p = c
					.prepareStatement("select * from characters where HuntCount=1 and objid=?");
			p.setInt(1, number);
			r = p.executeQuery();
			if (r.next()) {
				writeS("");
				writeS("공개수배 -> " + r.getString(3) + "");
				writeS("");
				StringBuffer sb = new StringBuffer();
				sb.append("\r\n\r\n");
				sb.append(" 수배자 ->  ").append(r.getString(3))
						.append("\r\n\r\n");
				//sb.append(" 이유 : \r\n\r\n  ").append(r.getString(60))
				//.append("\r\n\r\n");
				writeS(sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return S_BoardRead;
	}
}
