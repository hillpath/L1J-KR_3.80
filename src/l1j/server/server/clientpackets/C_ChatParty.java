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

package l1j.server.server.clientpackets;

import server.LineageClient;
import l1j.server.server.model.L1ChatParty;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Party;
import l1j.server.server.serverpackets.S_ServerMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_ChatParty extends ClientBasePacket {

	private static final String C_CHAT_PARTY = "[C] C_ChatParty";

	public C_ChatParty(byte abyte0[], LineageClient clientthread) {
		super(abyte0);
		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null) {
			return;
		}
		if (pc.isGhost())
			return;

		int type = readC();
		if (type == 0) { // /chatbanish 커멘드
			String name = readS();

			if (!pc.isInChatParty()) {
				// 파티에 가입하고 있지 않습니다.
				pc.sendPackets(new S_ServerMessage(425));
				return;
			}
			if (!pc.getChatParty().isLeader(pc)) {
				// 파티의 리더만을 추방할 수 있습니다.
				pc.sendPackets(new S_ServerMessage(427));
				return;
			}
			L1PcInstance targetPc = L1World.getInstance().getPlayer(name);
			if (targetPc == null) {
				// %0라는 이름의 사람은 없습니다.
				pc.sendPackets(new S_ServerMessage(109));
				return;
			}
			if (pc.getId() == targetPc.getId()) {
				return;
			}

			for (L1PcInstance member : pc.getChatParty().getMembers()) {
				if (member.getName().toLowerCase().equals(name.toLowerCase())) {
					pc.getChatParty().kickMember(member);
					return;
				}
			}
			// 발견되지 않았다
			// %0는 파티 멤버가 아닙니다.
			pc.sendPackets(new S_ServerMessage(426, name));
		} else if (type == 1) { // /chatoutparty 커멘드
			if (pc.isInChatParty()) {
				pc.getChatParty().leaveMember(pc);
			}
		} else if (type == 2) { // /chatparty 커멘드
			L1ChatParty chatParty = pc.getChatParty();
			if (pc.isInChatParty()) {
				pc
						.sendPackets(new S_Party("party", pc.getId(), chatParty
								.getLeader().getName(), chatParty
								.getMembersNameList()));
			} else {
				pc.sendPackets(new S_ServerMessage(425));
				// pc.sendPackets(new S_Party("party", pc.getId()));
			}
		}
	}

	@Override
	public String getType() {
		return C_CHAT_PARTY;
	}

}
