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

import java.util.Calendar;
import java.util.StringTokenizer;

import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1WarTime implements L1CommandExecutor {

	private L1WarTime() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1WarTime();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			String name = tok.nextToken();
   
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 1);
   
			WarTimeController.getInstance().setWarStartTime(name, cal);
   
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".공성종료 [켄트,오크,윈다,기란,하이,드워,아덴,디아]"));
			}
		}
	}