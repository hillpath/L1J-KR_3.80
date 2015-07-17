/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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
import java.util.TimeZone;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

public class S_Age extends ServerBasePacket {

	private static final String S_Age = "[C] S_Age";

	private static Logger _log = Logger.getLogger(S_Age.class.getName());

	private byte[] _byte = null;

	private int j = 0;

	static String[] name;

	static int[] age;

	public S_Age(L1PcInstance pc, int number) {
		name = new String[60];
		age = new int[60];

		buildPacket(pc, number);
	}

	public S_Age(L1NpcInstance board) {
		buildPacket(board);
	}

	private void buildPacket(L1NpcInstance board) {
		int count = 0;
		String[][] db = null;
		int[] id = null;
		db = new String[4][3];
		id = new int[4];
		while (count < 4) {
			id[count] = count + 1;
			db[count][0] = "[혈맹족보]";
			db[count][1] = "";
			count++;
		}
		db[0][2] = "1. 족보 01-15";
		db[1][2] = "2. 족보 16-30";
		db[2][2] = "3. 족보 31-45";
		db[3][2] = "4. 족보 45-60";

		writeC(Opcodes.S_OPCODE_BOARD);
		writeC(0x00);
		writeD(board.getId());
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0xFF); // ?
		writeC(0x7F); // ?
		writeH(4);
		writeH(300);
		for (int i = 0; i < 4; ++i) {
			writeD(id[i]);
			writeS(db[i][0]);
			writeS(db[i][1]);
			writeS(db[i][2]);
		}
	}

	private void buildPacket(L1PcInstance pc, int number) {
		String date = time();
		String type = null;
		String title = null;
		writeC(Opcodes.S_OPCODE_BOARDREAD);
		writeD(number);
		writeS("혈맹 족보");
		switch (number) {
		case 1:
			title = "족 보 01-15";
			break;
		case 2:
			title = "족 보 16-30";
			break;
		case 3:
			title = "족 보 31-45";
			break;
		case 4:
			title = "족 보 46-60";
			break;

		}
		writeS(title);
		writeS(date);
		int p = Rank(pc, number);

		if (number == 1) { // 추가부분입니다
			writeS("\n\r " + pc.getClanname() + " 혈맹 족보 01-15\n\r" + "\n\r"
					+ "  01. " + name[0] + "(" + age[0] + ")\n\r" + "  02. "
					+ name[1] + "(" + age[1] + ")\n\r" + "  03. " + name[2]
					+ "(" + age[2] + ")\n\r" + "  04. " + name[3] + "("
					+ age[3] + ")\n\r" + "  05. " + name[4] + "(" + age[4]
					+ ")\n\r" + "  06. " + name[5] + "(" + age[5] + ")\n\r"
					+ "  07. " + name[6] + "(" + age[6] + ")\n\r" + "  08. "
					+ name[7] + "(" + age[7] + ")\n\r" + "  09. " + name[8]
					+ "(" + age[8] + ")\n\r" + "  00. " + name[9] + "("
					+ age[9] + ")\n\r" + "  11. " + name[10] + "(" + age[10]
					+ ")\n\r" + "  12. " + name[11] + "(" + age[11] + ")\n\r"
					+ "  13. " + name[12] + "(" + age[12] + ")\n\r" + "  14. "
					+ name[13] + "(" + age[13] + ")\n\r" + "  15. " + name[14]
					+ "(" + age[14] + ")\n\r" + "      ");
		} else if (number == 2) { // 추가부분입니다
			writeS("\n\r " + pc.getClanname() + " 혈맹 족보 16-30\n\r" + "\n\r"
					+ "  16. " + name[15] + "(" + age[15] + ")\n\r" + "  17. "
					+ name[16] + "(" + age[16] + ")\n\r" + "  18. " + name[17]
					+ "(" + age[17] + ")\n\r" + "  19. " + name[18] + "("
					+ age[18] + ")\n\r" + "  20. " + name[19] + "(" + age[19]
					+ ")\n\r" + "  21. " + name[20] + "(" + age[20] + ")\n\r"
					+ "  22. " + name[21] + "(" + age[21] + ")\n\r" + "  23. "
					+ name[22] + "(" + age[22] + ")\n\r" + "  24. " + name[23]
					+ "(" + age[23] + ")\n\r" + "  25. " + name[24] + "("
					+ age[24] + ")\n\r" + "  26. " + name[25] + "(" + age[25]
					+ ")\n\r" + "  27. " + name[26] + "(" + age[26] + ")\n\r"
					+ "  28. " + name[27] + "(" + age[27] + ")\n\r" + "  29. "
					+ name[28] + "(" + age[28] + ")\n\r" + "  30. " + name[29]
					+ "(" + age[29] + ")\n\r" + "      ");
		} else if (number == 3) { // 추가부분입니다
			writeS("\n\r " + pc.getClanname() + " 혈맹 족보 31-45\n\r" + "\n\r"
					+ "  31. " + name[30] + "(" + age[30] + ")\n\r" + "  32. "
					+ name[31] + "(" + age[31] + ")\n\r" + "  33. " + name[32]
					+ "(" + age[32] + ")\n\r" + "  34. " + name[33] + "("
					+ age[33] + ")\n\r" + "  35. " + name[34] + "(" + age[34]
					+ ")\n\r" + "  36. " + name[35] + "(" + age[35] + ")\n\r"
					+ "  37. " + name[36] + "(" + age[36] + ")\n\r" + "  38. "
					+ name[37] + "(" + age[37] + ")\n\r" + "  39. " + name[38]
					+ "(" + age[38] + ")\n\r" + "  40. " + name[39] + "("
					+ age[39] + ")\n\r" + "  41. " + name[40] + "(" + age[40]
					+ ")\n\r" + "  42. " + name[41] + "(" + age[41] + ")\n\r"
					+ "  43. " + name[42] + "(" + age[42] + ")\n\r" + "  44. "
					+ name[43] + "(" + age[43] + ")\n\r" + "  45. " + name[44]
					+ "(" + age[44] + ")\n\r" + "      ");
		} else if (number == 4) { // 추가부분입니다
			writeS("\n\r " + pc.getClanname() + " 혈맹 족보 46-60\n\r" + "\n\r"
					+ "  46. " + name[45] + "(" + age[45] + ")\n\r" + "  47. "
					+ name[46] + "(" + age[46] + ")\n\r" + "  48. " + name[47]
					+ "(" + age[47] + ")\n\r" + "  49. " + name[48] + "("
					+ age[48] + ")\n\r" + "  50. " + name[49] + "(" + age[49]
					+ ")\n\r" + "  51. " + name[50] + "(" + age[50] + ")\n\r"
					+ "  52. " + name[51] + "(" + age[51] + ")\n\r" + "  53. "
					+ name[52] + "(" + age[52] + ")\n\r" + "  54. " + name[53]
					+ "(" + age[53] + ")\n\r" + "  55. " + name[54] + "("
					+ age[54] + ")\n\r" + "  56. " + name[55] + "(" + age[55]
					+ ")\n\r" + "  57. " + name[56] + "(" + age[56] + ")\n\r"
					+ "  58. " + name[57] + "(" + age[57] + ")\n\r" + "  59. "
					+ name[58] + "(" + age[58] + ")\n\r" + "  60. " + name[59]
					+ "(" + age[59] + ")\n\r" + "      ");
		}
	}

	private int Rank(L1PcInstance pc, int number) {

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int objid = pc.getId();
		int i = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			switch (number) {

			case 1: // 추가부분입니다
				String sql = "SELECT Age, char_name FROM characters WHERE ClanID = ";
				sql = (new StringBuilder(String.valueOf(sql))).append(
						pc.getClanid()).toString();
				sql = (new StringBuilder(String.valueOf(sql))).append(
						" And AccessLevel = 0 order by Age desc").toString();
				pstm = con.prepareStatement(sql);

				break;
			case 2: // 추가부분입니다
				String sql2 = "SELECT Age, char_name FROM characters WHERE ClanID = ";
				sql2 = (new StringBuilder(String.valueOf(sql2))).append(
						pc.getClanid()).toString();
				sql2 = (new StringBuilder(String.valueOf(sql2))).append(
						" And AccessLevel = 0 order by Age desc").toString();
				pstm = con.prepareStatement(sql2);

				break;
			case 3:
				String sql3 = "SELECT Age, char_name FROM characters WHERE ClanID = ";
				sql3 = (new StringBuilder(String.valueOf(sql3))).append(
						pc.getClanid()).toString();
				sql3 = (new StringBuilder(String.valueOf(sql3))).append(
						" And AccessLevel = 0 order by Age desc").toString();
				pstm = con.prepareStatement(sql3);

				break;
			case 4: // 추가부분입니다
				String sql4 = "SELECT Age, char_name FROM characters WHERE ClanID = ";
				sql4 = (new StringBuilder(String.valueOf(sql4))).append(
						pc.getClanid()).toString();
				sql4 = (new StringBuilder(String.valueOf(sql4))).append(
						" And AccessLevel = 0 order by Age desc").toString();
				pstm = con.prepareStatement(sql4);

				break;

			default:
				pstm = con
						.prepareStatement("SELECT char_name FROM characters WHERE AccessLevel = 0 order by Exp desc limit 10");
				break;
			}

			rs = pstm.executeQuery();
			if (number == 1) { // 추가부분입니다
				while (rs.next()) {
					age[i] = rs.getInt(1);
					name[i] = rs.getString(2);
					i++;
				}
			} else if (number == 2) { // 추가부분입니다
				while (rs.next()) {
					age[i] = rs.getInt(1);
					name[i] = rs.getString(2);
					i++;
				}
			} else if (number == 3) { // 추가부분입니다
				while (rs.next()) {
					age[i] = rs.getInt(1);
					name[i] = rs.getString(2);
					i++;
				}
			} else if (number == 4) { // 추가부분입니다
				while (rs.next()) {
					age[i] = rs.getInt(1);
					name[i] = rs.getString(2);
					i++;
				}
			} else {
				while (rs.next()) {
					name[i] = rs.getString(1);
					i++;
				}

				// 레코드가 없거나 5보다 작을때
				while (i < 10) {
					name[i] = "없음.";
					i++;
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "S_Age[]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		return i;
	}

	private static String time() {
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(tz);
		int year = cal.get(Calendar.YEAR) - 2000;
		String year2;
		if (year < 10) {
			year2 = "0" + year;
		} else {
			year2 = Integer.toString(year);
		}
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
		return year2 + "/" + Month2 + "/" + date2;
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_Age;
	}

}
