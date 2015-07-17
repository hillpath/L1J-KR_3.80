/* This program is free software; you can redistribute it and/or modify
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

import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_KeepALIVE extends ClientBasePacket {

	private static final String C_KEEP_ALIVE = "[C] C_KeepALIVE";

	public C_KeepALIVE(byte decrypt[], LineageClient client) {
		super(decrypt);

		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}
		/*
		 * ################# Point System #################
		 * if(pc.isPointUser()){ RealTime time =
		 * RealTimeClock.getInstance().getRealTime();
		 * 
		 * if(time.getSeconds() > pc.getLimitPointTime()) {
		 * pc.setPointUser(false);
		 * Account.updatePointAccount(pc.getAccountName(), 0); } }
		 * ################# Point System #################
		 */

		hackFinder(pc);
	}

	private void hackFinder(L1PcInstance pc) {
		if (pc == null)
			return;
		else if (pc.get_hackTimer() < 0)
			pc.init_hackTimer();
		else
			pc.calc_hackTimer();
	}

	@Override
	public String getType() {
		return C_KEEP_ALIVE;
	}
}