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

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DeleteCharOK;
import l1j.server.server.serverpackets.S_Notice;
import server.LineageClient;
import server.system.autoshop.AutoShop;
import server.system.autoshop.AutoShopManager;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket, C_DeleteChar

public class C_DeleteChar extends ClientBasePacket {

	private static final String C_DELETE_CHAR = "[C] RequestDeleteChar";

	private static Logger _log = Logger.getLogger(C_DeleteChar.class.getName());

	public C_DeleteChar(byte decrypt[], LineageClient client) throws Exception {
		super(decrypt);
		String name = readS();
		if(client == null || name == null)
			return;
		try {
			AutoShopManager shopManager = AutoShopManager.getInstance();
			AutoShop shopPlayer = shopManager.getShopPlayer(name);
			if (shopPlayer != null) {
				shopPlayer.logout();
				shopManager.remove(shopPlayer);
				shopPlayer = null;
			}

			//if (DisconnectCharacter(name)) {
			//	client.kick();
				//client.close();
				//return;
			//}

			L1PcInstance pc = CharacterTable.getInstance().restoreCharacter(name);
		/*	if (pc != null && pc.getLevel() >= 30 && Config.DELETE_CHARACTER_AFTER_7DAYS) {
				over30lv(pc);
				client.sendPacket(new S_DeleteCharOK(S_DeleteCharOK.DELETE_CHAR_AFTER_7DAYS));
				return;
			}*/
			if (pc.getLevel() < 99){ // 추가
			       client.sendPacket(new S_Notice("저희서버는 케릭터삭제가 불가능합니다."));
			       return;
			      }


			if (pc != null) {
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				if (clan != null) {
					clan.removeClanMember(name);
				}
				CharacterTable.getInstance().deleteCharacter(client.getAccountName(), name);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "C_DeleteChar[]Error", e);
			client.close();
			return;
		}

		client.sendPacket(new S_DeleteCharOK(S_DeleteCharOK.DELETE_CHAR_NOW));
	}

/*	private boolean DisconnectCharacter(String name) {
		Collection<L1PcInstance> pcs = L1World.getInstance().getAllPlayers();
		for (L1PcInstance delpc : pcs) {
			if(delpc.getName().equals(name)) {
				delpc.sendPackets(new S_Disconnect());
				delpc.logout();
				pcs = null;
				return true;
			}
		}
		pcs = null;
		return false;
	}*/

	public void over30lv(L1PcInstance pc) throws Exception {
		Timestamp deleteTime = null;
		if (pc.getType() < 32) {
			if (pc.isCrown())
				pc.setType(32);
			else if (pc.isKnight())
				pc.setType(33);
			else if (pc.isElf())
				pc.setType(34);
			else if (pc.isWizard())
				pc.setType(35);
			else if (pc.isDarkelf())
				pc.setType(36);
			else if (pc.isDragonknight())
				pc.setType(37);
			else if (pc.isIllusionist())
				pc.setType(38);
			deleteTime = new Timestamp(System.currentTimeMillis() + 86400000);// 하루
		} else {
			if (pc.isCrown())
				pc.setType(0);
			else if (pc.isKnight())
				pc.setType(1);
			else if (pc.isElf())
				pc.setType(2);
			else if (pc.isWizard())
				pc.setType(3);
			else if (pc.isDarkelf())
				pc.setType(4);
			else if (pc.isDragonknight())
				pc.setType(5);
			else if (pc.isIllusionist())
				pc.setType(6);
		}
		pc.setDeleteTime(deleteTime);
		pc.save();
	}

	@Override
	public String getType() {
		return C_DELETE_CHAR;
	}
}
