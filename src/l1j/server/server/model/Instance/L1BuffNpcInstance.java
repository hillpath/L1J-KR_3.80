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
package l1j.server.server.model.Instance;

import java.util.Timer;
import java.util.TimerTask;

import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.FaceToFace;

public class L1BuffNpcInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private int _tradeID;

	private boolean _tradeOk;

	public L1BuffNpcInstance(L1Npc template) {
		super(template);
		if (this.getNpcId() == 4206004) {
			TradeTimer treadtimer = new TradeTimer(this);
			treadtimer.begin();
		}
	}

	public class TradeTimer extends TimerTask {
		private final L1BuffNpcInstance _npc;

		public TradeTimer(L1BuffNpcInstance npc) {
			_npc = npc;
		}

		public void begin() {
			Timer timer = new Timer();
			timer.schedule(this, 0, 4000);
		}

		@Override
		public void run() {
			try {
				if (_npc.getTradeID() != 0) {
					return;
				}
				L1PcInstance target = FaceToFace.faceToFaceForNpc(_npc);
				if (target != null) {
					if (!target.isParalyzed()) {
						target.setTradeID(_npc.getId());
						target
								.sendPackets(new S_Message_YN(252, _npc
										.getName())); // %0%s가
						// 당신과
						// 아이템의
						// 거래를
						// 바라고
						// 있습니다.
						// 거래합니까?
						// (Y/N)c
						Thread.sleep(5000L);
					}
				}
			} catch (Exception exception) {
			}
		}
	}

	public int getTradeID() {
		return _tradeID;
	}

	public void setTradeID(int tradeID) {
		_tradeID = tradeID;
	}

	public void setTradeOk(boolean tradeOk) {
		_tradeOk = tradeOk;
	}

	public boolean getTradeOk() {
		return _tradeOk;
	}
}