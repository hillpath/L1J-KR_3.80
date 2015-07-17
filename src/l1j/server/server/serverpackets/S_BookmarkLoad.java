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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.utils.SQLUtil;

public class S_BookmarkLoad extends ServerBasePacket {

	private static final String S_BookMarkLoad = "[S] S_BookMarkLoad";
	private byte[] _byte = null;

	public S_BookmarkLoad(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_teleport WHERE char_id=? ORDER BY name ASC");
			pstm.setInt(1, pc.getId());
			rs = pstm.executeQuery();
			rs.last();
			int count = rs.getRow();
			rs.beforeFirst();

			writeC(Opcodes.S_OPCODE_RETURNEDSTAT);
			writeC(42); // type
			writeC(105); // 아래에 포문에 사용될 총 숫자...
			writeC(0x00); 
			writeC(0x02);
			int b = 0;
			for (int i=0; i<104; i++) {
				if (i < count) {
					writeC(b++);
				} else {
					writeC(0xff);
				}
			}
			writeC(100); // 저장할 수 있는 총 공간
			writeC(0);
			writeC(count); // 기억창에 저장된 갯수
			writeC(0);

			L1BookMark bookmark = null;
			while (rs.next()) {
				bookmark = new L1BookMark();
				bookmark.setId(rs.getInt("id"));
				bookmark.setCharId(rs.getInt("char_id"));
				bookmark.setName(rs.getString("name"));
				bookmark.setLocX(rs.getInt("locx"));
				bookmark.setLocY(rs.getInt("locy"));
				bookmark.setMapId(rs.getShort("mapid"));
				bookmark.setRandomX(rs.getShort("randomX"));
				bookmark.setRandomY(rs.getShort("randomY"));

				writeH(bookmark.getLocX());
				writeH(bookmark.getLocY());

				writeS(bookmark.getName()); // 저장된 이름
				writeH(bookmark.getMapId());  // mapid
				writeD(bookmark.getId()); // objid

				pc.addBookMark(bookmark);
			}
		} catch (SQLException e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}


	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_BookMarkLoad;
	}
}