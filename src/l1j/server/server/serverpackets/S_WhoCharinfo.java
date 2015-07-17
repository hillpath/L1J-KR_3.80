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

import java.util.logging.Logger;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_WhoCharinfo extends ServerBasePacket {
	private static final String S_WHO_CHARINFO = "[S] S_WhoCharinfo";

	private static Logger _log = Logger
			.getLogger(S_WhoCharinfo.class.getName());

	private byte[] _byte = null;

	public S_WhoCharinfo(L1PcInstance pc) {
		_log.fine("Who charpack for : " + pc.getName());

		String lawfulness = "";
		/* Kill & Death 시스템? -by 천국- */

		int lawful = pc.getLawful();
		float win = 0;
		float lose = 0;
		float total = 0;
		float winner = 0;

		win = pc.getKills(); // 승률 넣기위한 변수
		lose = pc.getDeaths();
		total = win + lose;
		winner = ((win * 100) / (total)); // 승률 계산한 변수

		/* Kill & Death 시스템? -by 천국- */
		if (lawful < 0) {
			lawfulness = "(Chaotic)";
		} else if (lawful >= 0 && lawful < 500) {
			lawfulness = "(Neutral)";
		} else if (lawful >= 500) {
			lawfulness = "(Lawful)";
		}

		writeC(Opcodes.S_OPCODE_MSG);
		writeC(0x08);

		String title = "";
		String clan = "";

		if (pc.getTitle().equalsIgnoreCase("") == false) {
			title = pc.getTitle() + " ";
		}

		if (pc.getClanid() > 0) {
			clan = "[" + pc.getClanname() + "]";
		}
		// //////////킬수보이게하기--------
		writeS(title + pc.getName() + " " + lawfulness + " " + clan + "\n\r"/* +"\\fU랩:"+pc.getLevel() */
				+ "\\fV KILL:" + pc.getKills() + "\\fY DEATH:" + pc.getDeaths()
				+ "\\fR 승률:" + winner + "%");
		writeD(0);
	}

	// //////////킬수보이게하기--------

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_WHO_CHARINFO;
	}
}
