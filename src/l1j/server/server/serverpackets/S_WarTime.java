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

import l1j.server.Config;
import l1j.server.server.Opcodes;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_WarTime extends ServerBasePacket {
	private static final String S_WAR_TIME = "[S] S_WarTime";

	public S_WarTime(int time) {

		writeC(Opcodes.S_OPCODE_WARTIME);
		writeH(6); // 리스트의 수(6이상은 무효)
		writeS(Config.TIME_ZONE); // 시간의 뒤() 중에 표시되는 캐릭터 라인
		writeH(1);// 순번
		writeC(136);
		writeH(time);// 6:00
		writeH(2);// 순번
		writeC(178);
		writeH(time);// 6:30
		writeH(3);// 순번
		writeC(220);
		writeH(time);// 7:00
		writeH(4);// 순번
		writeC(218);
		writeH(time + 1);// 10:00
		writeH(5);// 순번
		writeC(4);
		writeH(time + 2);// 10:30
		writeH(6);// 순번
		writeC(46);// 11:00
		writeD(time + 2);
		writeC(0);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_WAR_TIME;
	}
}
