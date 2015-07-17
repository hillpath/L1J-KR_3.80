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

import java.util.Collection;
import java.util.logging.Logger;

import server.system.autoshop.AutoShopManager;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_WhoAmount;

public class L1Who implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Who.class.getName());

	private L1Who() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Who();
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			//int AutoShopUser = AutoShopManager.getInstance().getShopPlayerCount();
			int CalcUser = L1UserCalc.getClacUser();
			Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();
			int robotcount = 0;
			int playercount = 0;
			int AutoShopUser = 0;
			for (L1PcInstance each : players) {
				if(each.noPlayerCK)
					robotcount++;
				else if(each.isPrivateShop() && each.getNetConnection() == null)
					AutoShopUser++;
				else
					playercount++;
			}
			String amount = String.valueOf(playercount);
			//String amount = String.valueOf(players.size());
			S_WhoAmount s_whoamount = new S_WhoAmount(amount);
			pc.sendPackets(s_whoamount);
			pc.sendPackets(new S_SystemMessage("로봇유저 : " + robotcount));
			pc.sendPackets(new S_SystemMessage("무인상점 : " + AutoShopUser));
			pc.sendPackets(new S_SystemMessage("뻥튀기 : " + CalcUser));

			// 온라인의 플레이어 리스트를 표시
			if (arg.equalsIgnoreCase("전체")) {
				StringBuffer gmList = new StringBuffer();
				StringBuffer playList = new StringBuffer();
				StringBuffer shopList = new StringBuffer();
				StringBuffer robotList = new StringBuffer();

				int countGM = 0, countPlayer = 0, countShop = 0, countRobot = 0;

				for (L1PcInstance each : players) {
					if (each.isGm()) {
						gmList.append(each.getName() + ", ");
						countGM++;
						continue;
					}
					if(each.noPlayerCK){
						robotList.append(each.getName() + ", ");
						countRobot++;
						continue;
					}
					if (!each.isPrivateShop()) {
						playList.append(each.getName() + ", ");
						countPlayer++;
						continue;
					}
					if (each.isPrivateShop()) {
						shopList.append(each.getName() + ", ");
						countShop++;
					}
				}
				if (gmList.length() > 0) {
					pc.sendPackets(new S_SystemMessage("-- 운영자 (" + countGM
							+ "명)"));
					pc.sendPackets(new S_SystemMessage(gmList.toString()));
				}

				if (playList.length() > 0) {
					pc.sendPackets(new S_SystemMessage("-- 플레이어 ("
							+ countPlayer + "명)"));
					pc.sendPackets(new S_SystemMessage(playList.toString()));
				}
				if (robotList.length() > 0) {
					pc.sendPackets(new S_SystemMessage("-- 로봇유저 ("
							+ countRobot + "명)"));
					pc.sendPackets(new S_SystemMessage(robotList.toString()));
				}
				if (shopList.length() > 0) {
					pc.sendPackets(new S_SystemMessage("-- 개인상점 (" + countShop
							+ "명)"));
					pc.sendPackets(new S_SystemMessage(shopList.toString()));
				}
			}
			players = null;
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".누구 [전체] 라고 입력해 주세요. "));
		}
	}
}
