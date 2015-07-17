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

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Burf implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Burf.class.getName());

	private L1Burf() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Burf();
	}

	static class Burfskill implements Runnable {
		private L1PcInstance _pc = null;

		private int _sprid;

		private int _count;

		public Burfskill(L1PcInstance pc, int sprid, int count) {
			_pc = pc;
			_sprid = sprid;
			_count = count;
		}

		public void run() {
			for (int i = 0; i < _count; i++) {
				try {
					Thread.sleep(500);
					int num = _sprid + i;
					_pc.sendPackets(new S_SystemMessage("스킬번호: " + num + ""));
					_pc.sendPackets(new S_SkillSound(_pc.getId(), _sprid + i));
					Broadcaster.broadcastPacket(_pc, new S_SkillSound(_pc
							.getId(), _sprid + i));
				} catch (Exception exception) {
					break;
				}
			}

		}

	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int sprid = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);

			Burfskill spr = new Burfskill(pc, sprid, count);
			GeneralThreadPool.getInstance().execute(spr);

			// pc.sendPackets(new S_SkillSound(pc.getId(), sprid));
			// Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(),
			// sprid));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName
					+ " [castgfx] 라고 입력해 주세요. "));
		}
	}
}
