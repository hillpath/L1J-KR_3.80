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

import l1j.server.server.command.executor.L1UserCalc;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_WhoAmount;
import l1j.server.server.serverpackets.S_WhoCharinfo;
import server.LineageClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Who extends ClientBasePacket {

	private static final String C_WHO = "[C] C_Who";

	public C_Who(byte[] decrypt, LineageClient client) {
		super(decrypt);
		String s = readS();
		L1PcInstance find = L1World.getInstance().getPlayer(s);
		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}
		String clan = "";
		if (L1World.getInstance().getNpcShop(s)) {
			pc.sendPackets(new S_SystemMessage(""+ s + " (Lawful)" + " " + clan
					+ "\n\r"/* +"\\fU·¦:"+pc.getLevel() */+ "\\fV KILL:"
					+ pc.getKills() + "\\fY DEATH:" + pc.getDeaths()
					+ "\\fR ½Â·ü:NaN%"));
			return;
		}

		if (find != null && (pc.isGm() || !find.isGm())) {  //¿î¿µÀÚ°Ë»öºÒ°¡´É
			S_WhoCharinfo s_whocharinfo = new S_WhoCharinfo(find);
			pc.sendPackets(s_whocharinfo);
		} else {
			int AddUser = (int) (L1World.getInstance().getAllPlayersToArray().length * 1.2);
			int CalcUser = L1UserCalc.getClacUser();
			int fake = 0;


			int size = L1World.getInstance().getAllPlayers().size();
			if (size >= 5 && size < 10) {
				fake = 1;
			} else if (size >= 10 && size < 20) {
				fake = 2;
			} else if (size >= 20 && size < 30) {
				fake = 4;
			} else if (size >= 30 && size < 40) {
				fake = 6;
			} else if (size >= 40 && size < 50) {
				fake = 8;
			} else if (size >= 50 && size < 60) {
				fake = 10;
			} else if (size >= 60 && size < 70) {
				fake = 12;
			} else if (size >= 70 && size < 90) {
				fake = 14;
			} else if (size >= 90) {
				for (int i = 89; i < size; i++) {
					fake = i - 75;
				}
			}
			AddUser += CalcUser;
			String amount = String.valueOf(AddUser + fake);
			S_WhoAmount s_whoamount = new S_WhoAmount(amount);
			pc.sendPackets(s_whoamount);
		}
	}

	@Override
	public String getType() {
		return C_WHO;
	}
}
