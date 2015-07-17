/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.datatables.IpTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1BanIp implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1BanIp.class.getName());

	private L1BanIp() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1BanIp();
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {

			StringTokenizer stringtokenizer = new StringTokenizer(arg);
			// IP를 지정
			String s1 = stringtokenizer.nextToken();

			// add/del를 지정(하지 않아도 OK)
			String s2 = null;
			try {
				s2 = stringtokenizer.nextToken();
			} catch (Exception e) {
			}

			IpTable iptable = IpTable.getInstance();
			boolean isBanned = iptable.isBannedIp(s1);

			for (L1PcInstance tg : L1World.getInstance().getAllPlayers()) {
				if (s1.equals(tg.getNetConnection().getIp())) {
					String msg = new StringBuilder().append("IP:").append(s1)
							.append(" 로 접속중의 플레이어:").append(tg.getName())
							.toString();
					pc.sendPackets(new S_SystemMessage(msg));
				}
			}

			if ("add".equals(s2) && !isBanned) {
				iptable.banIp(s1); // BAN 리스트에 IP를 더한다
				String msg = new StringBuilder().append("IP:").append(s1)
						.append(" 를 BAN IP에 등록했습니다. ").toString();
				pc.sendPackets(new S_SystemMessage(msg));
			} else if ("del".equals(s2) && isBanned) {
				if (iptable.liftBanIp(s1)) { // BAN 리스트로부터 IP를 삭제한다
					String msg = new StringBuilder().append("IP:").append(s1)
							.append(" 를 BAN IP로부터 삭제했습니다. ").toString();
					pc.sendPackets(new S_SystemMessage(msg));
				}
			} else {
				// BAN의 확인
				if (isBanned) {
					String msg = new StringBuilder().append("IP:").append(s1)
							.append(" 는 BAN IP에 등록되어 있습니다. ").toString();
					pc.sendPackets(new S_SystemMessage(msg));
				} else {
					String msg = new StringBuilder().append("IP:").append(s1)
							.append(" 는 BAN IP에 등록되어 있지 않습니다. ").toString();
					pc.sendPackets(new S_SystemMessage(msg));
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName
					+ " IP [ add | del ]라고 입력해 주세요. "));
		}
	}
}
