/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful ,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not , write to the Free Software
 * Foundation , Inc., 59 Temple Place - Suite 330, Boston , MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;

import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;

public class CheckLevelCheck implements Runnable {

	private static CheckLevelCheck _instance;

	public static CheckLevelCheck getInstance() {
		if (_instance == null) {
			_instance = new CheckLevelCheck();
		}
		return _instance;
	}

	public void run() {
		try {
			while (true) {
				Thread.sleep(60000);
				Hp();


			}
		} catch (Exception e1) {
		}
	}

	private Calendar getRealTime() {
		TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(_tz);
		return cal;
	}
	private void sendMessage(String msg) {
		  for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
		   pc.sendPackets(new S_ChatPacket(pc, msg, Opcodes.S_OPCODE_MSG, 18));
		  }
		 }

	private void Hp() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		int nowtime = Integer.valueOf(sdf.format(getRealTime().getTime()));
		int time3 = 30;
		if (nowtime % time3 == 0) {
			try {
				  Collection<L1PcInstance> list = null;
				  list = L1World.getInstance().getAllPlayers();
				  for(L1PcInstance pc : list){
					   if(pc == null)
						    continue;
					   
					if (pc.getLevel() > pc.getHighLevel() && !pc.isGm()) {sendMessage("[" + pc.getName()+ "]");
						pc.sendPackets(new S_Disconnect());
						
System.out.println("Please check your bug. Location: The l1j.server / CheckLevelCheck.java.");						
						continue;
					} else if (pc.getLevel() > 99 && !pc.isGm()) {sendMessage("[" + pc.getName()+ "]");
						pc.sendPackets(new S_Disconnect());
						continue;
					}
				}
			} catch (Exception e) {				
				
				System.out.println("CheckLevelCheck[]Errer");
			}
		}
	}
}
