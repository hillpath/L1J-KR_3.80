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
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_OwnCharPack extends ServerBasePacket {

	private static final String S_OWN_CHAR_PACK = "[S] S_OwnCharPack";

	private static final int STATUS_INVISIBLE = 2;

	private static final int STATUS_PC = 4;

	private static final int STATUS_FREEZE = 8;

	private static final int STATUS_BRAVE = 16;

	private static final int STATUS_ELFBRAVE = 32;

	private static final int STATUS_FASTMOVABLE = 64;

	private static final int STATUS_GHOST = 128;

	private static final int BLOOD_LUST = 16;

	private byte[] _byte = null;

	public S_OwnCharPack(L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(L1PcInstance pc) {
		int status = STATUS_PC;

		// 굴독같은 초록의 독
		// if (pc.isPoison()) {
		// status |= STATUS_POISON;
		// }

		if (pc.isInvisble() || pc.isGmInvis()) {
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
		writeC(Opcodes.S_OPCODE_SHOWOBJ);
		writeH(pc.getX());
		writeH(pc.getY());
		writeD(pc.getId());
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
		// writeC(addbyte);
		writeC(pc.getLight().getOwnLightSize());
		writeC(pc.getMoveState().getMoveSpeed());
		writeD(pc.getExp());
		writeH(pc.getLawful());
		writeS(pc.getName());
		writeS(pc.getTitle());
		writeC(status);
		writeD(pc.getClanid());
		writeS(pc.getClanname()); // 크란명
		writeS(null); // 펫호팅?
		if (pc.getClanid() == 0) writeC(176); // 왼쪽 깃발 클릭시..
		else 
			switch(pc.getClanRank()) {
			case 3: writeC(48); break; // 부군주
			case 7: writeC(20); break; // 수련
			case 8: writeC(80); break; // 일반
			case 9: writeC(96); break; // 수호기사
			case 10: writeC(160); break; // 혈맹군주
			}
		if (pc.isInParty()) // 파티중
		{
			writeC(100 * pc.getCurrentHp() / pc.getMaxHp());
		} else {
			writeC(0xFF);
		}
		writeC(0); // 타르쿡크 거리(대로)
		writeC(0); // PC = 0, Mon = Lv
		writeC(0); // ?
		writeC(0xFF);
		writeC(0xFF);
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
		return S_OWN_CHAR_PACK;
	}

}