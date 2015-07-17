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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class L1GMcurse implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1GMcurse.class.getName());

	private L1GMcurse() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1GMcurse();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			StringTokenizer tok = new StringTokenizer(arg) ;
			String name = tok.nextToken();
			String why = tok.nextToken();
			L1PcInstance target = L1World.getInstance().getPlayer(name);
			String t = null;
			int lv = target.getLevel();
			int type = target.getType();
			switch(type){
			case 0:	t = "군주";	break;
			case 1:	t = "기사";	break;
			case 2:	t = "요정";	break;
			case 3:	t = "법사";	break;
			case 4:	t = "다크엘프";	break;
			case 5:	t = "용기사";break;
			case 6:	t = "환술사";break;
			}
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO user_gmcurse SET id=?,char_name=?, level=? , class=?, why =?");
			pstm.setInt(1, ObjectIdFactory.getInstance().nextId());
			pstm.setString(2, name);
			pstm.setInt(3, lv);
			pstm.setString(4, t);
			pstm.setString(5, why);
			target.setGMcurse(true);
			pstm.execute();
			pc.sendPackets(new S_SystemMessage("캐릭명: "+ name +"에게 저주를 내렸습니다.")) ;
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".저주 [캐릭명] [사유] 을 입력해주세요.")) ;
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
