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

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.FaceToFace;
import server.LineageClient;
import server.system.autoshop.AutoShopManager;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Trade extends ClientBasePacket {

	private static final String C_TRADE = "[C] C_Trade";

	public C_Trade(byte abyte0[], LineageClient clientthread) throws Exception {
		super(abyte0);

		L1PcInstance player = clientthread.getActiveChar();
		if (player == null) {
			return;
		}
		if (player.isGhost())
			return;
		if (isTwoLogin(player))
			return;
		if (player.getOnlineStatus() == 0) {
			clientthread.kick();
			return;
		}
		if (player.getOnlineStatus() != 1) {
			clientthread.kick();
			return;
		}
		if (player.isInvisble()) {
			player.sendPackets(new S_ServerMessage(334));
			return;
		}

		L1PcInstance target = FaceToFace.faceToFace(player);
		if (target != null) {

			if (player.getAccountName().equalsIgnoreCase(
					target.getAccountName())) {
				player.sendPackets(new S_Disconnect());
				target.sendPackets(new S_Disconnect());
				return;
			}
						if(player.getTradeID() != 0 || target.getTradeID() != 0){
				player.sendPackets(new S_SystemMessage("자신 또는 대상은 거래 중인 상태입니다."));
				return;
			}
			if (player.getLevel() > 4 && target.getLevel() > 4) {
				if (!target.isParalyzed()) {
					player.setTradeID(target.getId()); // 상대의 오브젝트 ID를 보존해 둔다
					target.setTradeID(player.getId());
					target.sendPackets(new S_Message_YN(252, player.getName())); // %0%s가
					// 당신과
					// 아이템의
					// 거래를
					// 바라고
					// 있습니다.
					// 거래합니까?
					// (Y/N)
				}
			} else {
			}
		}
	}

	private boolean isTwoLogin(L1PcInstance c) {
		for(L1PcInstance target : L1World.getInstance().getAllPlayersToArray()){
			if(target.noPlayerCK) continue;
			int count = 0;
			if(c.getId() == target.getId()){
				count++;
				if(count > 1){
					c.getNetConnection().kick();
					c.getNetConnection().close();
					target.getNetConnection().kick();
					target.getNetConnection().close();
					return true;
				}
			}
			else if(c.getId() != target.getId()){
				if(c.getAccountName().equalsIgnoreCase(target.getAccountName())) {
					if(!AutoShopManager.getInstance().isAutoShop(target.getId())){
						c.getNetConnection().kick();
						c.getNetConnection().close();
						target.getNetConnection().kick();
						target.getNetConnection().close();
						return true;
					}			
				}
			}
		}
		return false;
	}
	

	@Override
	public String getType() {
		return C_TRADE;
	}

}
