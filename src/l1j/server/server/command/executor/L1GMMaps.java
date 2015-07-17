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
import l1j.server.server.serverpackets.S_GMMaps;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1GMMaps implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(S_GMMaps.class.getName());

	private L1GMMaps() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1GMMaps();
	}

	
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			int i = 0;
			try {
				i = Integer.parseInt(arg);
			} catch (NumberFormatException e) {
			}

			if (i == 1) {
				L1Teleport.teleport(pc, 32800, 32799, (short) 110, 5, false); // [오만] 10층 제니스퀸
			} else if (i == 2) {
				L1Teleport.teleport(pc , 32855, 32830, (short) 120, 5, false); //[오만] 20층 시    어
			} else if (i == 3) {
				L1Teleport.teleport(pc , 32799, 32799, (short) 130, 5, false); // [오만] 30층 뱀파이어
			} else if (i == 4) {
				L1Teleport.teleport(pc , 32800, 32797, (short) 140, 5, false); // [오만] 40층 좀비로드
			} else if (i == 5) {
				L1Teleport.teleport(pc , 32794, 32800, (short) 150, 5, false); // [오만] 50층 쿠    거
			} else if (i == 6) {
				L1Teleport.teleport(pc , 32720, 32824, (short) 160, 5, false); // [오만] 60층 머미로드
			} else if (i == 7) {
				L1Teleport.teleport(pc , 32720, 32824, (short) 170, 5, false); // [오만] 70층 아이리스
			} else if (i == 8) {
				L1Teleport.teleport(pc , 32724, 32822, (short) 180, 5, false); // [오만] 80층 나이트발드
			} else if (i == 9) {
				L1Teleport.teleport(pc , 32716, 32817, (short) 190, 5, false); // [오만] 90층 리    치
			} else if (i == 10) {
				L1Teleport.teleport(pc , 32731, 32856, (short) 200, 5, false); // [오만]100층 그림리퍼
				/** 상아탑**/
			} else if (i == 11) {
				L1Teleport.teleport(pc , 32900, 32760, (short) 78, 5, false); // 상아탑 4층
			} else if (i == 12) {
				L1Teleport.teleport(pc , 32801, 32867, (short) 81, 5, false); // 상아탑 7층
				/** 라스타바드**/
			} else if (i == 13) {
				L1Teleport.teleport(pc , 32665, 32852, (short) 457, 5, false); // 1층어둠결계
			} else if (i == 14) {
				L1Teleport.teleport(pc , 32663, 32850, (short) 467, 5, false); // 2층암흑결계
				/** 4대용**/
			} else if (i == 15) {
				L1Teleport.teleport(pc , 32725, 32800, (short) 67, 5, false); // 15 발라카스
			} else if (i == 16) {
				L1Teleport.teleport(pc , 32691, 32820, (short) 37, 5, false); // 16 안타라스
			} else if (i == 17) {
				L1Teleport.teleport(pc , 32771, 32831, (short) 65, 5, false); // 17 파푸리온
			} else if (i == 18) {
				L1Teleport.teleport(pc , 34041, 33007, (short) 4, 5, false); // 18 린드비오르
				
			} else {
				L1Location loc = GMCommandsConfig.ROOMS.get(arg.toLowerCase());
				if (loc == null) {
					pc.sendPackets(new S_GMMaps(1));
                    return;
				}
				L1Teleport.teleport(pc, loc.getX(), loc.getY(), (short) loc.getMapId(), 5, false);
			}
			
			if(i > 0 && i < 18) {
				pc.sendPackets(new S_SystemMessage("운영자 사냥터("+ i + ")맵으로 이동했습니다."));
			}
		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage(
					".사냥터 [장소명]을 입력 해주세요.(장소명은 GMCommands.xml을 참조)"));
		}
	}
}
