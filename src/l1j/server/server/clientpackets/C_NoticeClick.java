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
package l1j.server.server.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

import java.util.Locale;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_CharAmount;
import l1j.server.server.serverpackets.S_CharPacks;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.utils.SQLUtil;
import server.LineageClient;
import server.LoginController;

public class C_NoticeClick {

	private static final String C_NOTICE_CLICK = "[C] C_NoticeClick";

	private static Logger _log = Logger
			.getLogger(C_NoticeClick.class.getName());

	private static final int LIMIT_MIN = 1;

	private static final int LIMIT_MAX = 32767;

	public C_NoticeClick(LineageClient client) {
		if (LoginController.getInstance().getAccLoginSearch(
				client.getAccountName())) {
			return;
		}
		deleteCharacter(client);
		int amountOfChars = client.getAccount().countCharacters();
		int slot = client.getAccount().getCharSlot();
		client.sendPacket(new S_CharAmount(amountOfChars, slot));
		if (amountOfChars > 0)
			sendCharPacks(client);
		accountTimeCheck(client);
	}

	private void accountTimeCheck(LineageClient client) {
		int time = client.getAccount().getAccountTime() + 1;
		int count = 0;

		if (time == 1)
			time = 0;
		if (client.getAccount().getAccountTimeReady() > 0)
			count = 1;

		client.sendPacket(new S_PacketBox(S_PacketBox.ACCOUNT_TIME, count,
				time * 60));
	}

	private void deleteCharacter(LineageClient client) {

		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			conn = L1DatabaseFactory.getInstance().getConnection();
			pstm = conn
					.prepareStatement("SELECT * FROM characters WHERE account_name=? ORDER BY objid");
			pstm.setString(1, client.getAccountName());
			rs = pstm.executeQuery();
			Timestamp deleteTime = null;
			Calendar cal = null;
			L1Clan clan = null;
			while (rs.next()) {
				String name = rs.getString("char_name");
				String clanname = rs.getString("Clanname");
				deleteTime = rs.getTimestamp("DeleteTime");
				if (deleteTime != null) {
					cal = Calendar.getInstance();
					long checkDeleteTime = ((cal.getTimeInMillis() - deleteTime
							.getTime()) / 1000) / 3600;
					if (checkDeleteTime >= 0) {
						clan = L1World.getInstance().getClan(clanname);
						if (clan != null) {
							clan.removeClanMember(name);
						}
						CharacterTable.getInstance().deleteCharacter(
								client.getAccountName(), name);
					}
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "C_NoticeClick[deleteCharacter]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}

	private void sendCharPacks(LineageClient client) {

		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {

			conn = L1DatabaseFactory.getInstance().getConnection();
			pstm = conn
					.prepareStatement("SELECT * FROM characters WHERE account_name=? ORDER BY objid");
			pstm.setString(1, client.getAccountName());
			rs = pstm.executeQuery();
			S_CharPacks cpk = null;
			while (rs.next()) {
				String name = rs.getString("char_name");
				String clanname = rs.getString("Clanname");
				int type = rs.getInt("Type");
				byte sex = rs.getByte("Sex");
				int lawful = rs.getInt("Lawful");

				int currenthp = rs.getInt("CurHp");
				currenthp = checkRange(currenthp);

				int currentmp = rs.getInt("CurMp");
				currentmp = checkRange(currentmp);

				int lvl;
				if (Config.CHARACTER_CONFIG_IN_SERVER_SIDE) {
					lvl = rs.getInt("level");
					if (lvl < 1)
						lvl = 1;
					else if (lvl > 127)
						lvl = 127;
				} else {
					lvl = 1;
				}

				int ac;
				if (rs.getInt("Ac") < -128) {
					ac = (byte) -128;
				} else {
					ac = rs.getByte("Ac");
				}
				int str = rs.getByte("Str");
				int dex = rs.getByte("Dex");
				int con = rs.getByte("Con");
				int wis = rs.getByte("Wis");
				int cha = rs.getByte("Cha");
				int intel = rs.getByte("Intel");
				int accessLevel = rs.getShort("AccessLevel");
				int birthday = rs.getInt("BirthDay");
				if (birthday == 0) {
					SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd",
							Locale.KOREA);

					birthday = Integer.parseInt(s.format(Calendar.getInstance()
							.getTime()));
				}

				cpk = new S_CharPacks(name, clanname, type, sex, lawful,
						currenthp, currentmp, ac, lvl, str, dex, con, wis, cha,
						intel, accessLevel, birthday);

				client.sendPacket(cpk);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "C_NoticeClick[sendCharPacks]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}

	private int checkRange(int i) {
		if (i < LIMIT_MIN)
			return LIMIT_MIN;
		else if (i > LIMIT_MAX)
			return LIMIT_MAX;
		return i;
	}

	public String getType() {
		return C_NOTICE_CLICK;
	}
}