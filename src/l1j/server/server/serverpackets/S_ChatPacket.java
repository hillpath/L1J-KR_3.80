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
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_ChatPacket extends ServerBasePacket {

	private static final String _S__1F_NORMALCHATPACK = "[S] S_ChatPacket";

	private byte[] _byte = null;

	public S_ChatPacket(String targetname, String chat, int opcode) {
		writeC(opcode);
		writeC(9);
		writeS("-> (" + targetname + ") " + chat);
		writeC(13);
		writeS("\\fW[******] " + chat);
	}

	// 매니저용 귓말
	public S_ChatPacket(String from, String chat) {
		writeC(Opcodes.S_OPCODE_WHISPERCHAT);
		writeS(from);
		writeS(chat);
	}

	public S_ChatPacket(L1PcInstance pc, String chat, int opcode, int type) {
		writeC(opcode);
		switch (type) {
		case 0:

			writeC(type);
			writeD(pc.getId());
			writeS(pc.getName() + ": " + chat);
			break;
		case 2:
			writeC(type);
			if (pc.isInvisble()) {
				writeD(0);
			} else {
				writeD(pc.getId());
			}
			writeS("<" + pc.getName() + "> " + chat);
			writeH(pc.getX());
			writeH(pc.getY());
			break;
		case 3:
			if (pc.getName().equalsIgnoreCase("메티스") || pc.getName().equalsIgnoreCase("미소피아") || pc.getName().equalsIgnoreCase("카시오페아") || pc.getName().equalsIgnoreCase("운영자") || pc.getName().equalsIgnoreCase("레츠비")) {
				writeC(type);
				writeS("\\fY[" + pc.getName() + "]: " + chat);
				
						if (Config.isGmchat)//영자채팅 관련
					L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"[******] " + chat));
			} else if (pc.getAccessLevel() == 1) {
				writeC(type);
				writeS("[" + pc.getName() + "] " + chat);//좀비채팅
			} else {
				writeC(type);
				writeS("[" + pc.getName() + "] " + chat);
			}
			break;
		case 4:
			writeC(type);
			if (pc.getAge() == 0) { //
				writeS("{" + pc.getName() + "} " + chat);
			} else {
				writeS("{" + pc.getName() + "(" + pc.getAge() + ")" + "} " + chat);
			}
			break;
		case 9:
			writeC(type);
			writeS("-> (" + pc.getName() + ") " + chat);
			break;
		case 11:
			writeC(type);
			writeS("(" + pc.getName() + ") " + chat);
			break;
		/*case 12:	// 장사 채팅 $
			writeC(type);
			writeS("[" + pc.getName() + "] " + chat);
			break;*/
		case 12://장사채팅관련
			if (pc.isGm() == true) {
				writeC(15);//색지정
				writeS(chat);
			} else if (pc.getAccessLevel() == 1) {
				writeC(type);
				writeS("[" + pc.getName() + "] " + chat);//좀비채팅
			} else {
				writeC(type);
				writeS("[" + pc.getName() + "] " + chat);
			}
			break;
		case 13:
			writeC(15);
			writeD(pc.getId());
			writeS("\\fW{{" + pc.getName() + "}} " + chat);
			break;
			
		case 14:
			writeC(type);
			writeD(pc.getId());
			writeS("(" + pc.getName() + ") " + chat);
			break;
		case 15:
			writeC(type);
			writeS("[" + pc.getName() + "] " + chat);
			break;
		case 16:
			writeS(pc.getName());
			writeS(chat);
			break;
		case 17:
			writeC(type);
			writeS("{" + pc.getName() + "} " + chat);
			break;
		case 18: // PVP, 채팅퀴즈 , 타임이벤트 
			   writeC(3);    
			   writeS(chat);
			   break;
		case 19: //이름제거
			   writeC(11); //(주황색)
			   writeS(chat);
			   break;	 
		case 20: //이름제거 
			   writeC(4); //(연초록색)
			   writeS(chat);
			   break;
		case 21: //이름제거 
			   writeC(13); //(보라색)
			   writeS(chat);
			   break;
		case 22: //이름제거 
			   writeC(0); //(연갈색)오토방지색
			   writeS(chat);
			   break;
		case 23: //이름제거 
			   writeC(18); //(보라색)
			   writeS(chat);
			   break;
		case 24: //이름제거 
			   writeC(15); //(노랭색 장사)
			   writeS(chat);
			   break;
		default:
			break;
		}
	}

	@Override
	public byte[] getContent() {
		if (null == _byte) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return _S__1F_NORMALCHATPACK;
	}

}