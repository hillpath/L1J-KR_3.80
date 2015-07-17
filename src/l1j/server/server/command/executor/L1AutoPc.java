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

import server.controller.Robot.RobotControler;

import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1AutoPc implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1AutoPc.class.getName());
	
//	private Random _random = new Random();

//	private boolean isChk = false;
	
	private static L1AutoPc _instance;

	private L1AutoPc() {
	}

	public static L1CommandExecutor getInstance() {
		if (_instance == null) {
			_instance = new L1AutoPc();
		}
		return _instance;
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			String pcName = tok.nextToken();
			int type = Integer.parseInt(tok.nextToken());

			int locType = 0;

			try {
				locType = Integer.parseInt(tok.nextToken());
			} catch (Exception ex) {
				locType = 0;
			}
			
			switch (type) {
			case 0:
				for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
					if (player.getName().equalsIgnoreCase(pcName)) {
						player.save();
						player.logout();
						RobotControler.removeList(player);
						break;
					}
				}
				break;			
			case 1:
				for (L1PcInstance obj : L1World.getInstance().getAllPlayers()) {
					if (!obj.isPrivateShop() && obj.noPlayerCK) {
						obj.logout();
						RobotControler.removeList(obj);
					}
				}
				break;
				/**·Îº¿Å¸ÀÔ**/
			case 2:
				for (L1PcInstance obj : L1World.getInstance().getAllPlayers()) {
					if(obj.getHighLevel() == 45||obj.getHighLevel() == 25||obj.getHighLevel() == 1){
						obj.logout();
						RobotControler.removeList(obj);
					}
				}
				break;
			case 3:
				for (L1PcInstance obj : L1World.getInstance().getAllPlayers()) {
					if(obj.getHighLevel() == 56){
						obj.logout();
						RobotControler.removeList(obj);
					}
				}
				break;
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + " [·Îº¿¾ÆÀÌµð] [0:²û/1:ÀüÃ¼²û/2:¸¶À»²û/3:»ç³É²û] ¶ó°í ÀÔ·ÂÇØ ÁÖ¼¼¿ä. "));
		} 
	}
}
