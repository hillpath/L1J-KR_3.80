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
package l1j.server.server;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;

public class BugKick {
	private static BugKick _instance;

	private BugKick() {
	}

	public static BugKick getInstance() {
		if (_instance == null) {
			_instance = new BugKick();
		}
		return _instance;
	}

	public void KickPlayer(L1PcInstance pc) {
		try {
			L1Teleport.teleport(pc, 32737, 32796, (short) 99, 5, true);
			pc.sendPackets(new S_Poison(pc.getId(), 2)); // ���� ���°� �Ǿ����ϴ�.
			Broadcaster.broadcastPacket(pc, new S_Poison(pc.getId(), 2)); // ����
																			// ���°�
																			// �Ǿ����ϴ�.
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
			pc.getSkillEffectTimerSet().hasSkillEffect(SHOCK_STUN);
			pc.getSkillEffectTimerSet().setSkillEffect(SHOCK_STUN,
					24 * 60 * 60 * 1000);// ������� ����
			Connection con = null;
			PreparedStatement pstm = null;
			con = L1DatabaseFactory.getInstance().getConnection();
		    pstm = con.prepareStatement("UPDATE accounts SET banned = 0 WHERE login= ?"); //��ó����!
			pstm.setString(1, pc.getAccountName());
			pstm.execute();
			pstm.close();
			con.close();
			L1World.getInstance().broadcastServerMessage(
					"Bugs Attempt: [" + pc.getName() + "]");
		} catch (Exception e) {
			// System.out.println(pc.getName()+" ȭ���� ��� ����");
		}
	}
}
