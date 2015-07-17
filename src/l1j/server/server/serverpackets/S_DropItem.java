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
// ServerBasePacket

public class S_DropItem extends ServerBasePacket {

	/*
	 * private static final String _S__OB_DropItem = "[S] S_DropItem";
	 * 
	 * private byte[] _byte = null;
	 * 
	 * public S_DropItem(L1ItemInstance item) { buildPacket(item); }
	 * 
	 * private void buildPacket(L1ItemInstance item) {
	 * writeC(Opcodes.S_OPCODE_SHOWOBJ); writeH(item.getX());
	 * writeH(item.getY()); writeD(item.getId());
	 * writeH(item.getItem().getGroundGfxId()); writeC(0); writeC(0); if
	 * (item.isNowLighting()) { writeC(item.getItem().getLightRange()); } else {
	 * writeC(0); } writeC(0); writeD(item.getCount()); writeC(0); writeC(0); if
	 * (item.getCount() > 1) { writeS(item.getItem().getName() + " (" +
	 * item.getCount() + ")"); } else { int itemId = item.getItem().getItemId();
	 * int isId = item.isIdentified() ? 1 : 0; if (itemId == 20383 && isId == 1) {
	 * writeS(item.getItem().getName() + " [" + item.getChargeCount() + "]"); }
	 * else if ((itemId == 40006 || itemId == 40007 || itemId == 40008 || itemId ==
	 * 40009 || itemId == 140006 || itemId == 140008) && isId == 1) {
	 * writeS(item.getItem().getName() + " (" + item.getChargeCount() + ")"); }
	 * else if (item.getItem().getLightRange() != 0 && item .isNowLighting()) {
	 * writeS(item.getItem().getName() + " ($10)"); } else if
	 * (item.getAttrEnchantLevel() != 0 && isId == 1){ String AttrLevel = " ";
	 * switch(item.getAttrEnchantLevel()){ case 1: AttrLevel = "$6115"; break;
	 * case 2: AttrLevel = "$6116"; break; case 3: AttrLevel = "$6117"; break;
	 * case 4: AttrLevel = "$6118"; break; case 5: AttrLevel = "$6119"; break;
	 * case 6: AttrLevel = "$6120"; break; case 7: AttrLevel = "$6121"; break;
	 * case 8: AttrLevel = "$6122"; break; case 9: AttrLevel = "$6123"; break;
	 * case 10: AttrLevel = "$6124"; break; case 11: AttrLevel = "$6125"; break;
	 * case 12: AttrLevel = "$6126"; break; default: break; } writeS(AttrLevel +
	 * "+" + item.getEnchantLevel() + " " + item.getItem().getName()); } else {
	 * //writeS(item.getItem().getName()); writeS(item.getLogName()); // ±³A¼EA
	 * ±×³E ≫c¿e } } writeC(0); writeD(0); writeD(0); writeC(255); writeC(0);
	 * writeC(0); writeC(0); writeH(65535); writeD(0); writeC(8); writeC(0); }
	 * 
	 * @Override public byte[] getContent() { if (_byte == null) { _byte =
	 * _bao.toByteArray(); } return _byte; } @Override public String getType() {
	 * return _S__OB_DropItem; } }
	 */
	private static final String _S__OB_DropItem = "[S] S_DropItem";

	private byte[] _byte = null;

	public S_DropItem(L1ItemInstance item) {
		buildPacket(item);
	}

	private void buildPacket(L1ItemInstance item) {
		writeC(Opcodes.S_OPCODE_SHOWOBJ);
		writeH(item.getX());
		writeH(item.getY());
		writeD(item.getId());
		writeH(item.getItem().getGroundGfxId());
		writeC(0);
		writeC(0);
		if (item.isNowLighting()) {
			writeC(item.getItem().getLightRange());
		} else {
			writeC(0);
		}
		writeC(0);
		writeD(item.getCount());
		writeC(0);
		writeC(0);
		StringBuffer sb = null;
		sb = new StringBuffer();
		if (item.isIdentified()) {
			if (item.getItem().getType2() == 1
					|| item.getItem().getType2() == 2) {
				switch (item.getAttrEnchantLevel()) {
				case 1:
					sb.append("$6115");
					break;
				case 2:
					sb.append("$6116");
					break;
				case 3:
					sb.append("$6117");
					break;
				case 4:
					sb.append("$6118");
					break;
				case 5:
					sb.append("$6119");
					break;
				case 6:
					sb.append("$6120");
					break;
				case 7:
					sb.append("$6121");
					break;
				case 8:
					sb.append("$6122");
					break;
				case 9:
					sb.append("$6123");
					break;
				case 10:
					sb.append("$6124");
					break;
				case 11:
					sb.append("$6125");
					break;
				case 12:
					sb.append("$6126");
					break;
				default:
					sb.append(" ");
					// 이부분 본섭과 동일하게 하실분은 바로윗줄 주석처리
					// 주석안할경우 ALT눌렀을때나 마우스올려놨을때나 인첸표시
					// 주석할경우 ALT눌렀을때만 인첸표시
					break;
				}
				// 인첸 +0 일때도 표기되게 하실분은 밑에 if (item.getEnchantLevel() >= 0) {로 교체
				if (item.getEnchantLevel() > 0) {
					sb.append("+" + item.getEnchantLevel() + " ");
				} else if (item.getEnchantLevel() < 0) {
					sb.append(String.valueOf(item.getEnchantLevel()) + " ");
				}
			}
		}
		sb.append(item.getItem().getNameId());
		if (item.getCount() > 1) {
			sb.append(" (" + item.getCount() + ")");
		} else {
			int itemId = item.getItem().getItemId();
			int isId = item.isIdentified() ? 1 : 0;
			if (itemId == 20383 && isId == 1) {
				sb.append(" [" + item.getChargeCount() + "]");
			} else if ((itemId == 40006 || itemId == 40007 || itemId == 40008 ||itemId == 46091
					|| itemId == 40009 || itemId == 45464 || itemId == 140006 || itemId == 140008)
					&& isId == 1) {
				sb.append(" (" + item.getChargeCount() + ")");
			} else if (item.getItem().getLightRange() != 0
					&& item.isNowLighting()) {
				sb.append(" ($10)");
			}
		}
		writeS(sb.toString());
		writeC(0);
		writeD(0);
		writeD(0);
		writeC(255);
		writeC(0);
		writeC(0);
		writeC(0);
		writeH(65535);
		writeD(0);
		writeC(8);
		writeC(0);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return _S__OB_DropItem;
	}

}
