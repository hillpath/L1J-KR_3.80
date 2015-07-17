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

public class S_IdentifyDesc extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 확인 스크롤 사용시의 메세지를 표시한다
	 */
	public S_IdentifyDesc(L1ItemInstance item) {
		buildPacket(item);
	}

	private void buildPacket(L1ItemInstance item) {
		writeC(Opcodes.S_OPCODE_IDENTIFYDESC);
		writeH(item.getItem().getItemDescId());

		StringBuilder name = new StringBuilder();

		if (item.getBless() == 0) {
			name.append("$227 "); // 축복되었다
		} else if (item.getBless() == 2) {
			name.append("$228 "); // 저주해졌다
		}

		name.append(item.getItem().getNameId());
		if (item.getItem().getType2() == 1) { // weapon
			String magicDmg = "";
			if(item._pc != null){
				int dmg = item.getDmgByMagic()+item.getHolyDmgByMagic();
				if(dmg > 0)
					magicDmg = "(+"+dmg+")";//칼업+데미지표기
			}
			writeH(134); // \f1%0：작은 monster 타격%1 큰 monster 타격%2
			writeC(3);
			writeS(name.toString());
			writeS(item.getItem().getDmgSmall() + "+" + item.getEnchantLevel()+magicDmg);
			writeS(item.getItem().getDmgLarge() + "+" + item.getEnchantLevel()+magicDmg);

		} else if (item.getItem().getType2() == 2) { // armor
			if (item.getItem().getItemId() == 20383) { // 기마용 헤룸
				writeH(137); // \f1%0：사용 가능 회수%1［무게%2］
				writeC(3);
				writeS(name.toString());
				writeS(String.valueOf(item.getChargeCount()));
			} else {
				if (item.getEnchantLevel() >= 0) {
					writeH(135); // \f1%0：방어력%1 방어도구
					writeC(2);
					writeS(name.toString());
					if (item.getAcByMagic() > 0) {
						writeS(Math.abs(item.getItem().get_ac()) + "+"
								+ item.getEnchantLevel() + "("
								+ item.getAcByMagic() + ")");
					} else {
						writeS(Math.abs(item.getItem().get_ac()) + "+"
								+ item.getEnchantLevel());
					}
				} else if (item.getEnchantLevel() < 0) {
					writeH(135); // \f1%0：방어력%1 방어도구
					writeC(2);
					writeS(name.toString());
					if (item.getAcByMagic() > 0) {
						writeS(Math.abs(item.getItem().get_ac()) + ""
								+ item.getEnchantLevel() + "("
								+ item.getAcByMagic() + ")");
					} else {
						writeS(Math.abs(item.getItem().get_ac()) + ""
								+ item.getEnchantLevel());
					}
				}
			}

		} else if (item.getItem().getType2() == 0) { // etcitem
			if (item.getItem().getType() == 1) { // wand
				writeH(137); // \f1%0：사용 가능 회수%1［무게%2］
				writeC(3);
				writeS(name.toString());
				writeS(String.valueOf(item.getChargeCount()));
			} else if (item.getItem().getType() == 2) {
				writeH(138);
				writeC(2);
				name.append(": $231 "); // 나머지의 연료
				name.append(String.valueOf(item.getRemainingTime()));
				writeS(name.toString());
			} else if (item.getItem().getType() == 7) { // food
				writeH(136); // \f1%0：만복도%1［무게%2］
				writeC(3);
				writeS(name.toString());
				writeS(String.valueOf(item.getItem().getFoodVolume()));
			} else {
				writeH(138); // \f1%0：［무게%1］
				writeC(2);
				writeS(name.toString());
			}
			writeS(String.valueOf(item.getWeight()));
		} else if (item.getItem().getType2() == 3) { // 레이스 티켓
			writeH(138); // \f1%0：［무게%1］
			writeC(2);
			writeS(name.toString());
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}
}
