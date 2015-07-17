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
import l1j.server.server.model.Instance.L1ItemInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket, S_SendInvOnLogin

public class S_ItemName extends ServerBasePacket {

	private static final String S_ITEM_NAME = "[S] S_ItemName";

	/**
	 * 아이템의 이름을 변경한다. 장비나 강화 상태가 바뀌었을 때에 보낸다.
	 */
	public S_ItemName(L1ItemInstance item) {
		if (item == null) {
			return;
		}
		// jump를 보는 한, 이 Opcode는 아이템명을 갱신시키는 목적인 만큼 사용되는 모양(장비 후나 OE 후 전용? )
		// 후에 무엇인가 데이터를 계속해 보내도 모두 무시되어 버린다
		writeC(Opcodes.S_OPCODE_ITEMNAME);
		writeD(item.getId());
		writeS(item.getViewName());
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_ITEM_NAME;
	}
}
