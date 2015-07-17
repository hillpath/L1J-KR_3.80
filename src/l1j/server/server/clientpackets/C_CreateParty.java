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
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_CreateParty extends ClientBasePacket {

	private static final String C_CREATE_PARTY = "[C] C_CreateParty";

	public C_CreateParty(byte decrypt[], LineageClient client) throws Exception {
		super(decrypt);

		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}
		int type = readC();
		if (type == 0 || type == 1) {// 0.일반 1.분배
			int targetId = readD();
			L1Object temp = L1World.getInstance().findObject(targetId);
			if (temp instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) temp;

				if (pc.getId() == targetPc.getId())
					return;

				if (targetPc.isInParty()) {
					// 벌써 다른 파티에 소속해 있기 (위해)때문에 초대할 수 없습니다
					pc.sendPackets(new S_ServerMessage(415));
					return;
				}

				if (pc.isInParty()) {
					if (pc.getParty().isLeader(pc)) {
						targetPc.setPartyID(pc.getId());
						// \f2%0\f>%s로부터 \fU파티 \f> 에 초대되었습니다. 응합니까? (Y/N)
						targetPc
								.sendPackets(new S_Message_YN(953, pc.getName()));
					} else {
						// 파티의 리더만을 초대할 수 있습니다.
						pc.sendPackets(new S_ServerMessage(416));
					}
				} else {
					pc.setPartyType(type);
					targetPc.setPartyID(pc.getId());
					switch (type) {
					case 0:
						// \f2%0\f>%s로부터 \fU파티 \f> 에 초대되었습니다. 응합니까? (Y/N)
						targetPc
								.sendPackets(new S_Message_YN(953, pc.getName()));
						break;
					case 1:
						// \f2%0\f>%s \fU자동분배파티\f> 초대하였습니다. 허락하시겠습니까? (Y/N)
						targetPc
								.sendPackets(new S_Message_YN(954, pc.getName()));
						break;
					}
				}
			}
		} else if (type == 2) { // 채팅 파티
			String name = readS();
			L1PcInstance targetPc = L1World.getInstance().getPlayer(name);
			 if(!pc.isGm() && ((name.compareTo("메티스")==0)
			          || (name.compareTo("미소피아")==0))){ //운영자 채팅초대 
				 pc.sendPackets(new S_SystemMessage(targetPc + "님은 접속중이 아닙니다."));
			    return;
			   }
			if (targetPc == null) {
				// %0라는 이름의 사람은 없습니다.
				pc.sendPackets(new S_ServerMessage(109));
				return;
			}
			if (pc.getId() == targetPc.getId())
				return;

			if (targetPc.isInChatParty()) {
				// 벌써 다른 파티에 소속해 있기 (위해)때문에 초대할 수 없습니다
				pc.sendPackets(new S_ServerMessage(415));
				return;
			}

			if (pc.isInChatParty()) {
				if (pc.getChatParty().isLeader(pc)) {
					targetPc.setPartyID(pc.getId());
					// \f2%0\f>%s로부터\fU채팅 파티 \f>에 초대되었습니다. 응합니까? (Y/N)
					targetPc.sendPackets(new S_Message_YN(951, pc.getName()));
				} else {
					// 파티의 리더만을 초대할 수 있습니다.
					pc.sendPackets(new S_ServerMessage(416));
				}
			} else {
				targetPc.setPartyID(pc.getId());
				// \f2%0\f>%s로부터\fU채팅 파티 \f>에 초대되었습니다. 응합니까? (Y/N)
				targetPc.sendPackets(new S_Message_YN(951, pc.getName()));
			}
		} else if (type == 3) {
			int targetId = readD();
			L1Object temp = L1World.getInstance().findObject(targetId);
			if (temp instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) temp;
				if (pc.getId() == targetPc.getId()) {
					return;
				}

				if (pc.isInParty()) {
					if (targetPc.isInParty()) {
						if (pc.getParty().isLeader(pc)) {
							if (pc.getLocation().getTileLineDistance(targetPc.getLocation()) < 16) {
								pc.getParty().passLeader(targetPc);
							} else {
								// 파티장을 위임시킬 동료가 근처에 없습니다
								pc.sendPackets(new S_ServerMessage(1695));
							}
						} else {
							// 파티장이 아니어서 권한을 행사할 수 없습니다
							pc.sendPackets(new S_ServerMessage(1697));
						}
					} else {
							// 현재 파티에 있는 구성원이 아닙니다
							pc.sendPackets(new S_ServerMessage(1696));
					}
				}
			}
		}
	}

	@Override
	public String getType() {
		return C_CREATE_PARTY;
	}

}
