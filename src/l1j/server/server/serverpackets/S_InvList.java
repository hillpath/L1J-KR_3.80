/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.serverpackets;

import java.util.List;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_InvList extends ServerBasePacket {

	private static final String S_INV_LIST = "[S] S_InvList";

	/**
	 * 목록에 아이템을 복수개 정리해 추가한다.
	 */
	public S_InvList(L1PcInstance pc) {
		List<L1ItemInstance> items = pc.getInventory().getItems();
		long currentTimeMillis = System.currentTimeMillis();

		for (L1ItemInstance item : items) {
			if (item.getItemId() == L1ItemId.DRAGON_KEY) {
				if (System.currentTimeMillis() > item.getEndTime().getTime()) {
					pc.getInventory().deleteItem(item);
				}
			}
			if (item.getItemId() >= 76767 && item.getItemId() <= 76784) {
				if (item.getEndTime() != null
						&& currentTimeMillis > item.getEndTime().getTime()) {
					pc.getInventory().deleteItem(item);
				    if(pc.getInventory().checkEquipped(item.getItemId())){
						pc.getInventory().setEquipped(item, false);
						     }
				}
			}

		}
		writeC(Opcodes.S_OPCODE_INVLIST);
		writeC(items.size());
		byte[] status = null;
		for (L1ItemInstance item : items) {
			writeD(item.getId());
			writeH(item.getItem().getMagicCatalystType()); //3.53C
			int type = item.getItem().getUseType();
			if (type < 0){
				type = 0;
			}
			writeC(type);
			int count = item.getChargeCount();
			if (count < 0) {
				count = 0;
			}
			writeC(count);
			writeH(item.get_gfxid());
			writeC(item.getBless());
			writeD(item.getCount());
			writeC((item.isIdentified()) ? 1 : 0);
			writeS(item.getViewName());
			if (!item.isIdentified()) {
				// 미감정의 경우 스테이터스를 보낼 필요는 없다
				writeC(0);
			} else {
				status = item.getStatusBytes();
				writeC(status.length);
				for (byte b : status) {
					writeC(b);
				}
			}
			   writeC(10); // 추가 패킷
			   writeD(0); // 추가패킷
			   writeD(0); // 추가패킷
			   writeH(0); // 추가패킷
		}
	}

	@Override
	public byte[] getContent() {
		return _bao.toByteArray();
	}

	@Override
	public String getType() {
		return S_INV_LIST;
	}
}
