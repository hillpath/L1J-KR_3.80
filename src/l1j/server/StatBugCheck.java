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
import java.util.TimeZone;

import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;
import server.GameServer;

public class StatBugCheck implements Runnable {

	private static StatBugCheck _instance;

	public static StatBugCheck getInstance() {
		if (_instance == null) {
			_instance = new StatBugCheck();
		}
		return _instance;
	}

	public void run() {
		try {
			while (true) {
				Thread.sleep(50000);
				Base();
				exp();

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

	private void exp() {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (pc.getClanid() != 0 && clan.getOnlineClanMember().length >= 5) {
				pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON , 91 , true));
			} else {
				if (pc.getClanid() != 0 && clan.getOnlineClanMember().length <= 5) {
					pc.sendPackets(new S_PacketBox(S_PacketBox.UNLIMITED_ICON , 91 , false));
				}
			}
		}
	}

	
	
	
	private void Base() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		int nowtime = Integer.valueOf(sdf.format(getRealTime().getTime()));
		int time2 = 30;
		if (nowtime % time2 == 0) {
			try {
				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
		
					
					if (pc.getAbility().getBaseStr() >= 36 && !pc.isGm()) {
						sendMessage("Try Bug: [" + pc.getName()
										+ "]");
						pc.sendPackets(new S_Disconnect());
						continue;
					} else if (pc.getAbility().getBaseCon() >= 36 && !pc.isGm()) {
						sendMessage("Try Bug: [" + pc.getName()
										+ "]");
						pc.sendPackets(new S_Disconnect());
						continue;
					} else if (pc.getAbility().getBaseDex() >= 36 && !pc.isGm()) {
						sendMessage("Try Bug: [" + pc.getName()
										+ "]");
						pc.sendPackets(new S_Disconnect());
						continue;
					} else if (pc.getAbility().getBaseInt() >= 36 && !pc.isGm()) {
						sendMessage("Try Bug: [" + pc.getName()
										+ "]");
						pc.sendPackets(new S_Disconnect());
						continue;
					} else if (pc.getAbility().getBaseWis() >= 36 && !pc.isGm()) {
						sendMessage("Try Bug: [" + pc.getName()
										+ "]");
						pc.sendPackets(new S_Disconnect());
						continue;
					}
				}
				////////////////////////////////////////////////////////////////////////////////////////////
                               GameServer.getInstance().saveAllCharInfo();//��������
                                //System.out.println("[GameServer] ���׻���ڰ˻缭������Ϸ�..");
               ////////////////////////////////////////////////////////////////////////////////////////////
				
                               
                               Thread.sleep(10000);

			} catch (Exception e) {
			}
		}
	}
}