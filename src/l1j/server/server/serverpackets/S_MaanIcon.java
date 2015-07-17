/* √‚√≥ :∫ªº∑
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

import l1j.server.server.Opcodes;

public class S_MaanIcon extends ServerBasePacket {
	public static final String S_MAANICON = "[S] S_MaanIcon";
// ∏∂æ» æ∆¿Ãƒ‹
public S_MaanIcon(int j,int time) {
writeC(Opcodes.S_OPCODE_PACKETBOX);
writeC(0x14);
for(int i = 0; i < 78; i++) writeC(0x00);
writeC((time + 16) / 32);
writeC(j);//46:¡ˆ∑Ê, 47:ºˆ∑Ê, 48:«≥∑Ê, 49:»≠∑Ê, 50:¡ˆ∑Êºˆ∑Ê, 51:¡ˆ∑Êºˆ∑Ê«≥∑Ê, 52:¡ˆ∑Êºˆ∑Ê«≥∑Ê»≠∑Ê
}


@Override
public byte[] getContent() {
return getBytes();
}
@Override
public String getType() {
	return S_MAANICON;
}
}
