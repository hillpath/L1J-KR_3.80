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

import java.util.Random;

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1NpcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_NPCPack extends ServerBasePacket {

	private static final String S_NPC_PACK = "[S] S_NPCPack";

	private static final int STATUS_POISON = 1;

	private static final int STATUS_PC = 4;

	private static final int STATUS_FREEZE = 8;

	private static final int STATUS_BRAVE = 16;
	
	private static Random _random = new Random();

	private byte[] _byte = null;

	public S_NPCPack(L1NpcInstance npc) {

		/**
		 * 세팅 - 0:mob,item(atk pointer), 1:poisoned(), 2:invisable(), 4:pc,
		 * 8:cursed(), 16:brave(), 32:??, 64:??(??), 128:invisable but name
		 */
		int status = 0;
		if (npc.getPoison() != null) {
			if (npc.getPoison().getEffectId() == 1) {
				status |= STATUS_POISON;
			}
		}
		if (npc.getMoveState().getBraveSpeed() == 1)
			status |= STATUS_BRAVE;

		if (npc.getNpcTemplate().is_doppel()) {
			// PC속성이라면 에바의 축복을 건네줄 수 없기 때문에 WIZ 퀘스트의 돕펠은 예외
			if (npc.getNpcTemplate().get_npcId() != 81069) {
				status |= STATUS_PC;
			}
		}
		if (npc.isParalyzed()) {
			status |= STATUS_FREEZE;
		}

		writeC(Opcodes.S_OPCODE_SHOWOBJ);
		writeH(npc.getX());
		writeH(npc.getY());
		writeD(npc.getId());
		if (npc.getGfxId().getTempCharGfx() == 0) {
			writeH(npc.getGfxId().getGfxId());
		} else {
			writeH(npc.getGfxId().getTempCharGfx());
		}
		if ((npc.getNpcTemplate().is_doppel() && npc.getGfxId().getGfxId() != 31) // 슬라임의
				// 모습을
				// 하고
				// 있지
				// 않으면
				// 돕펠
				|| npc.getGfxId().getGfxId() == 6632
				|| npc.getGfxId().getGfxId() == 6634 // 얼음던전
				|| npc.getGfxId().getGfxId() == 6636
				|| npc.getGfxId().getGfxId() == 6638) { // 얼음던전
			writeC(4); // 장검
		} else if (npc.getGfxId().getGfxId() == 51 || npc.getNpcId() == 60519) { // 창
			// 경비병
			// ,
			// 청상어단
			writeC(24);
		} else if (npc.getGfxId().getGfxId() == 816) { // 오성 오크스카우트
			writeC(20);
		} else if (npc.getGfxId().getGfxId() == 986 || npc.getGfxId().getGfxId() == 727) { //  //아논망치
			   writeC (4);
		} else {
			writeC(npc.getActionStatus());
		}
		writeC(npc.getMoveState().getHeading());
		writeC(npc.getLight().getChaLightSize());
		writeC(npc.getMoveState().getMoveSpeed());
		writeD(1);// npc.getExp 지만 이제 보내지 않는다.
		writeH(npc.getTempLawful());
		writeS(npc.getNameId());
		if (npc instanceof L1FieldObjectInstance) { // SIC의 벽자, 간판 등
			L1NpcTalkData talkdata = NPCTalkDataTable.getInstance()
					.getTemplate(npc.getNpcTemplate().get_npcId());
			if (talkdata != null) {
				writeS(talkdata.getNormalAction()); // 타이틀이 HTML명으로서 해석된다
			} else {
				writeS(null);
			}
		} else {
			
         if(npc.getNpcTemplate().get_npcId() >=
        	 8000001 && npc.getNpcTemplate().get_npcId() <= 8000484
        	 ||npc.getNpcTemplate().get_npcId() >=
        		 7000007 && npc.getNpcTemplate().get_npcId() <= 7000042){//무인상점엔피씨호칭 
     		int rnd = _random.nextInt(8);
    		switch(rnd){
    		case 0:
    			    writeS("");
    			break;
    			case 1:
    				writeS("");
    			break;
    			case 2:
    				writeS("");
    			break;
    			case 3:
    				writeS("");
    			break;
    			case 4:
    				writeS("");
    			break;
    			case 5:
    				writeS("");
    			break;
    			case 6:
    				writeS("");
    			break;
    			case 7:
    				writeS("");
    			break;
			}
         }
        else
			 
			writeS(npc.getTitle());
		}

		writeC(status);
		writeD(0); // 0이외에 하면(자) C_27이 난다
		writeS(null);
		writeS(null); // 마스터명?
		writeC(0);
		writeC(0xFF); // HP
		writeC(0);
		writeC(npc.getLevel());
		writeC(0);
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
		return S_NPC_PACK;
	}

}
