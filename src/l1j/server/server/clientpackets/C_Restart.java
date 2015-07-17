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

import java.util.logging.Logger;

import l1j.server.server.BattleZone;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import server.LineageClient;
import server.LoginController;
import server.system.autoshop.AutoShop;
import server.system.autoshop.AutoShopManager;

public class C_Restart extends ClientBasePacket {
	private static final String C_OPCODE_RESTART = "[C] C_Restart";
	private static Logger _log = Logger.getLogger(C_Restart.class.getName());	

	public C_Restart(byte[] decrypt, LineageClient client) throws Exception {
		super(decrypt);
		
		client.CharReStart(true);
		client.sendPacket(new S_PacketBox(S_PacketBox.LOGOUT));
		LoginController.getInstance().getAccLoginSearch(client.getAccountName());

		if (client.getActiveChar() != null) {
			L1PcInstance pc = client.getActiveChar();
			if (pc == null) {
				return;
			}
			//eva.LogServerAppend("종료", pc, client.getIp(), -1);
			//3.63아이템패킷처리
			pc.isWorld = false;
			//3.63아이템패킷처리
			
			pc.save();
			pc.saveInventory();
			if(pc.get_DuelLine() != 0){
				BattleZone.getInstance().remove라인1(pc);
				BattleZone.getInstance().remove배틀존유저(pc);
				BattleZone.getInstance().remove라인2(pc);
				BattleZone.getInstance().remove배틀존유저(pc);
				pc.set_DuelLine(100);
			}
			if(pc.isPrivateShop()){				
				synchronized (pc) {
					AutoShopManager shopManager = AutoShopManager.getInstance(); 
					AutoShop autoshop = shopManager.makeAutoShop(pc);
					shopManager.register(autoshop);
					client.setActiveChar(null);
				}
			} else {
				if (pc.isDead()) {
					return;
				}
				_log.fine("Disconnect from: " + pc.getName());
				synchronized (pc) {
					
					client.quitGame(pc);
					pc.logout();
					pc.noPlayerCK = true; 
					client.setActiveChar(null);
				}
			}
		} else {
			_log.fine("Disconnect Request from Account : " + client.getAccountName());

		}
	}

	@Override
	public String getType() {
		return C_OPCODE_RESTART;
	}
}

