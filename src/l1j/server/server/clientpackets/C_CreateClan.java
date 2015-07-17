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
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ServerMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_CreateClan extends ClientBasePacket {

	private static final String C_CREATE_CLAN = "[C] C_CreateClan";

	public C_CreateClan(byte abyte0[], LineageClient clientthread)
			throws Exception {
		super(abyte0);
		String s = readS();
		int numOfNameBytes = 0;
		numOfNameBytes = s.getBytes("EUC-KR").length;

		L1PcInstance l1pcinstance = clientthread.getActiveChar();
		
		if (l1pcinstance.isCrown()) { // 프린스 또는 프린세스
			if (l1pcinstance.getClanid() == 0) {
							//** 혈맹 창설시 아래 2개 문자 못쓰게 **//	By 천국
				for (int o = 0; o < s.length(); o++) {
					if (s.charAt(o) == '/' || s.charAt(o) == ' ') { // 특수문자가 아니라면.. 
						return;
					}
				}
				if (!l1pcinstance.getInventory().checkItem(L1ItemId.ADENA,
						30000)) {
					l1pcinstance.sendPackets(new S_ServerMessage(337, "$4")); // \f1%0이
					// 부족합니다.
					return;
				}
				if(s.length() > 1000){
    			System.out.println("패킷렉공격 ID :"+l1pcinstance.getName());
    			System.out.println("패킷렉공격 IP :"+clientthread.getIp());
    			clientthread.kick();
    			return;
                }
				for (int i = 0; i < s.length(); i++) {
					if (s.charAt(i) == ' ' || s.charAt(i) == 'ㅤ') {
						l1pcinstance.sendPackets(new S_ServerMessage(53)); // 이름이
						// 잘못되었습니다.
						// 다른
						// 이름을
						// 입력하십시오.
						return;
					}
				}
				if (8 < (numOfNameBytes - s.length()) || 16 < numOfNameBytes) {
					l1pcinstance.sendPackets(new S_ServerMessage(98)); // \f1혈맹이름이
					// 너무
					// 깁니다.
				}
				for (L1Clan clan : L1World.getInstance().getAllClans()) { // \f1
					// 같은
					// 이름의
					// 혈맹이
					// 존재합니다.
					if (clan.getClanName().toLowerCase()
							.equals(s.toLowerCase())) {
						l1pcinstance.sendPackets(new S_ServerMessage(99)); // \f1
						// 같은
						// 이름의
						// 혈맹이
						// 존재합니다.
						return;
					}
				}
				L1Clan clan = ClanTable.getInstance().createClan(l1pcinstance,
						s); // 크란
				// 창설
				l1pcinstance.getInventory().consumeItem(L1ItemId.ADENA, 30000);
				if (clan != null) {
					l1pcinstance.sendPackets(new S_ServerMessage(84, s)); // \f1%0
					// 혈맹이
					// 창설되었습니다.
				}
			} else {
				l1pcinstance.sendPackets(new S_ServerMessage(86)); // \f1 벌써
				// 혈맹이 결성되고
				// 있으므로 작성할
				// 수 없습니다.
			}
		} else {
			l1pcinstance.sendPackets(new S_ServerMessage(85)); // \f1프린스와
			// 프린세스만이 혈맹을
			// 창설할 수 있습니다.
		}
	}

	@Override
	public String getType() {
		return C_CREATE_CLAN;
	}

}
