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

import l1j.server.server.Opcodes;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_War extends ServerBasePacket {

	private static final String S_WAR = "[S] S_War";

	private byte[] _byte = null;

	public S_War(int type, String clan_name1, String clan_name2) {
		buildPacket(type, clan_name1, clan_name2);
	}

	private void buildPacket(int type, String clan_name1, String clan_name2) {
		// 1 : _Ç÷¸ÍÀÌ_Ç÷¸Í¿¡ ¼±ÀüÆ÷°íÇß½À´Ï´Ù.
		// 2 : _Ç÷¸ÍÀÌ_Ç÷¸Í¿¡ Ç×º¹Çß½À´Ï´Ù.
		// 3 : _Ç÷¸Í°ú_Ç÷¸Í°úÀÇ ÀüÀïÀÌ Á¾°áÇß½À´Ï´Ù.
		// 4 : _Ç÷¸ÍÀÌ_Ç÷¸Í°úÀÇ ÀüÀïÀ¸·Î ½Â¸®Çß½À´Ï´Ù.
		// 6 : _Ç÷¸Í°ú_Ç÷¸ÍÀÌ µ¿¸ÍÀ» ¸Î¾ú½À´Ï´Ù.
		// 7 : _Ç÷¸Í°ú_Ç÷¸Í°úÀÇ µ¿¸Í °ü°è°¡ ÇØÁ¦µÇ¾ú½À´Ï´Ù.
		// 8 : ´ç½ÅÀÇ Ç÷¸ÍÀÌ ÇöÀç_Ç÷¸Í°ú ±³ÀüÁßÀÔ´Ï´Ù.

		writeC(Opcodes.S_OPCODE_WAR);
		writeC(type);
		writeS(clan_name1);
		writeS(clan_name2);
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
		return S_WAR;
	}
}
