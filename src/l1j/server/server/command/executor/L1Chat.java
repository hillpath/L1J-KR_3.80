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

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Chat implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Chat.class.getName());

	private L1Chat() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Chat();
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			if (st.hasMoreTokens()) {
				String flag = st.nextToken();
				String msg;
				if (flag.compareToIgnoreCase("켬") == 0) {
					L1World.getInstance().set_worldChatElabled(true);
					msg = "월드 채팅을 유효하게 했습니다. ";
				} else if (flag.compareToIgnoreCase("끔") == 0) {
					L1World.getInstance().set_worldChatElabled(false);
					msg = "월드 채팅을 정지했습니다. ";
				} else {
					throw new Exception();
				}
				pc.sendPackets(new S_SystemMessage(msg));
			} else {
				String msg;
				if (L1World.getInstance().isWorldChatElabled()) {
					msg = "현재 월드 채팅은 유효합니다.. 채팅 끔 로 정지할 수 있습니다. ";
				} else {
					msg = "현재 월드 채팅은 정지하고 있습니다.. 채팅 켬 로 유효하게 할 수 있습니다. ";
				}
				pc.sendPackets(new S_SystemMessage(msg));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + " [켬, 끔]"));
		}
	}
}
