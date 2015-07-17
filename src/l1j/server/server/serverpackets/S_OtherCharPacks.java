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

import l1j.server.server.MiniWarGame;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket, S_OtherCharPacks

public class S_OtherCharPacks extends ServerBasePacket {

	private static final String S_OTHER_CHAR_PACKS = "[S] S_OtherCharPacks";

	private static final int STATUS_POISON = 1;

	private static final int STATUS_INVISIBLE = 2;

	private static final int STATUS_PC = 4;

	private static final int STATUS_FREEZE = 8;

	private static final int STATUS_BRAVE = 16;

	private static final int STATUS_ELFBRAVE = 32;

	private static final int STATUS_FASTMOVABLE = 64;

	private static final int BLOOD_LUST = 16;

	private static final int STATUS_GHOST = 128;

	private byte[] _byte = null;

	public S_OtherCharPacks(L1PcInstance pc) {
		int status = STATUS_PC;

		if (pc.getPoison() != null) {//독상태
			if (pc.getPoison().getEffectId() == 1) {
				status |= STATUS_POISON;
			}
		}

		if (pc.isInvisble()) {
			status |= STATUS_INVISIBLE;
		}
		if (pc.isBrave()) {
			status |= STATUS_BRAVE;
		}
		if (pc.isElfBrave()) {
			status |= STATUS_BRAVE;
			status |= STATUS_ELFBRAVE;
		}
		if (pc.isBloodLust()) {
			status |= BLOOD_LUST;
		}
		if (pc.isFastMovable()) {
			status |= STATUS_FASTMOVABLE;
		}
		if (pc.isGhost()) {
			status |= STATUS_GHOST;
		}
		if (pc.isParalyzed()) {
			status |= STATUS_FREEZE;
		}

		// int addbyte = 0;
		// int addbyte1 = 1;

		writeC(Opcodes.S_OPCODE_SHOWOBJ);
		if (pc.isGm() && pc.isGmInvis()) {//인비지 추가
			writeH(0);
			writeH(0);
		} else {
		writeH(pc.getX());
		writeH(pc.getY());
		writeD(pc.getId());
		}
		if (pc.isDead()) {
			writeH(pc.getTempCharGfxAtDead());
		} else {
			writeH(pc.getGfxId().getTempCharGfx());
		}
		if (pc.isDead()) {
			writeC(pc.getActionStatus());
		} else {
			writeC(pc.getCurrentWeapon());
		}
		writeC(pc.getMoveState().getHeading());
		// writeC(0); // makes char invis (0x01), cannot move. spells display
		writeC(pc.getLight().getChaLightSize());
		writeC(pc.getMoveState().getMoveSpeed());
		writeD(0x0000); // exp
		// writeC(0x00);
		writeH(pc.getLawful());
		if (pc.getHuntCount() == 1) { // 현상범 이름 다르게 나오게 설정
			writeS(pc.getName() + "\\fe(수배중)");
	       } else if(pc.getMapId() == 2006){
				if(MiniWarGame.getInstance().getBattleStart() == true){
					if(MiniWarGame.getInstance().isMemberLine1(pc)){
						writeS("공성A팀");
					} else if(MiniWarGame.getInstance().isMemberLine2(pc)){
						writeS("공성B팀");
					}
				}
		} else {
			writeS(pc.getName());
		}
		writeS(pc.getTitle());
		writeC(status);
		writeD(pc.getClanid());
		writeS(pc.getClanname()); // 크란명
		writeS(null); // 펫호팅?
		writeC(0); // ?
		/*
		 * if(pc.is_isInParty()) // 파티중 { writeC(100 * pc.get_currentHp() /
		 * pc.get_maxHp()); } else { writeC(0xFF); }
		 */

		writeC(0xFF);
		writeC(0); // 타르쿡크 거리(대로)
		writeC(0); // PC = 0, Mon = Lv
		writeC(0); // 
		writeC(0xFF);
		writeC(0xFF);
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
		return S_OTHER_CHAR_PACKS;
	}

}