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

import java.util.logging.Logger;

import l1j.server.server.GMCommandsConfig;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1GMRoom implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1GMRoom.class.getName());

	private L1GMRoom() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1GMRoom();
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			int i = 0;
			try {
				i = Integer.parseInt(arg);
			} catch (NumberFormatException e) {
			}

			if (i == 1) {
				L1Teleport.teleport(pc, 32737, 32796, (short) 99, 5, false); // 영자방
			} else if (i == 2) {
				L1Teleport.teleport(pc, 33052, 32339, (short) 4, 5, false); // 요정숲
			} else if (i == 3) {
				L1Teleport.teleport(pc, 32644, 32955, (short) 0, 5, false); // 판도라
			} else if (i == 4) {
				L1Teleport.teleport(pc, 34055, 32290, (short) 4, 5, false); // 오렌
			} else if (i == 5) {
				L1Teleport.teleport(pc, 33429, 32814, (short) 4, 5, false); // 기란
			} else if (i == 6) {
				L1Teleport.teleport(pc, 33047, 32761, (short) 4, 5, false); // 켄말
			} else if (i == 7) {
				L1Teleport.teleport(pc, 32612, 33191, (short) 4, 5, false); // 윈다우드
			} else if (i == 8) {
				L1Teleport.teleport(pc, 33611, 33253, (short) 4, 5, false); // 하이네
			} else if (i == 9) {
				L1Teleport.teleport(pc, 33082, 33390, (short) 4, 5, false); // 은말
			} else if (i == 10) {
				L1Teleport.teleport(pc, 32572, 32944, (short) 0, 5, false); // 말섬
			} else if (i == 11) {
				L1Teleport.teleport(pc, 33964, 33254, (short) 4, 5, false); // 아덴
			} else if (i == 12) {
				L1Teleport.teleport(pc, 32635, 32818, (short) 303, 5, false); // 몽섬
			} else if (i == 13) {
				L1Teleport.teleport(pc, 32828, 32848, (short) 70, 5, false); // 잊섬
			} else if (i == 14) {
				L1Teleport.teleport(pc, 32736, 32787, (short) 15, 5, false); // 켄성
			} else if (i == 15) {
				L1Teleport.teleport(pc, 32735, 32788, (short) 29, 5, false); // 윈성
			} else if (i == 16) {
				L1Teleport.teleport(pc, 32730, 32802, (short) 52, 5, false); // 기란
			} else if (i == 17) {
				L1Teleport.teleport(pc, 32572, 32826, (short) 64, 5, false); // 하이네성
			} else if (i == 18) {
				L1Teleport.teleport(pc, 32895, 32533, (short) 300, 5, false); // 아덴성
			} else if (i == 19) {
				L1Teleport.teleport(pc, 33167, 32775, (short) 4, 5, false); // 켄성
				// 수호탑
			} else if (i == 20) {
				L1Teleport.teleport(pc, 32674, 33408, (short) 4, 5, false); // 윈성
				// 수호탑
			} else if (i == 21) {
				L1Teleport.teleport(pc, 33630, 32677, (short) 4, 5, false); // 기란
				// 수호탑
			} else if (i == 22) {
				L1Teleport.teleport(pc, 33524, 33394, (short) 4, 5, false); // 하이네
				// 수호탑
			} else if (i == 23) {
				L1Teleport.teleport(pc, 32424, 33068, (short) 440, 5, false); // 해적섬
			} else if (i == 24) {
				L1Teleport.teleport(pc, 32800, 32868, (short) 1001, 5, false); // 베헤모스
			} else if (i == 25) {
				L1Teleport.teleport(pc, 32800, 32856, (short) 1000, 5, false); // 실베리아
			} else if (i == 26) {
				L1Teleport.teleport(pc, 32630, 32903, (short) 780, 5, false); // 테베사막
			} else if (i == 27) {
				L1Teleport.teleport(pc, 32743, 32799, (short) 781, 5, false); // 테베
				// 피라미드
				// 내부
			} else if (i == 28) {
				L1Teleport.teleport(pc, 32735, 32830, (short) 782, 5, false); // 테베
				// 오리시스
				// 제단
			} else if (i == 29) {
				L1Teleport.teleport(pc , 32736, 32799, (short) 34, 5, false); // 감옥
			} else if (i == 30) {
				L1Teleport.teleport(pc , 32760, 32870, (short) 610, 5, false); // 벗꽃마을
			} else if (i == 31) {
			    L1Teleport.teleport(pc, 32645 ,32904, (short) 5153, 5, false);  //배틀존
			} else if (i == 32) {
			    L1Teleport.teleport(pc, 32723 ,32800, (short) 5167, 5, false);  //악영
			} else if (i == 33) {
			    L1Teleport.teleport(pc, 32699 ,32770, (short) 666, 5, false);  //지옥
			} else {
				L1Location loc = GMCommandsConfig.ROOMS.get(arg.toLowerCase());
				if (loc == null) {
					pc.sendPackets(new S_SystemMessage(
							"1.GMroom 2.요숲 3.판도라 4.오렌 5.기란 6.켄말"));
					pc.sendPackets(new S_SystemMessage(
							"7.윈다 8.하이네 9.은말 10.말섬 11.아덴 12.몽섬"));
					pc.sendPackets(new S_SystemMessage(
							"13.잊섬 14.켄성 15.윈성 16.기란성 17.하이네성"));
					pc.sendPackets(new S_SystemMessage(
							"18.아덴성 19.켄성수탑 20.윈성수탑 21.기란수탑"));
					pc.sendPackets(new S_SystemMessage(
							"22.하이네수탑 23.해적섬 24.베헤모스 25.실베리아"));
					pc.sendPackets(new S_SystemMessage(
							"26.테베사막 27.피라미드내부 28.오리시스제단 29.감옥 30.벗꽃마을 31.배틀존 32.악영 33.지옥"));
					return;
				}
				L1Teleport.teleport(pc, loc.getX(), loc.getY(), (short) loc
						.getMapId(), 5, false);
			}

			if (i > 0 && i < 33) {
				pc.sendPackets(new S_SystemMessage("운영자 귀환(" + i
						+ ")번으로 이동했습니다."));
			}
		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage(
					".귀환 [장소명]을 입력 해주세요.(장소명은 GMCommands.xml을 참조)"));
		}
	}
}
