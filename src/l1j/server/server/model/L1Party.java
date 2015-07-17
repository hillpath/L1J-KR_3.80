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
package l1j.server.server.model;

import javolution.util.FastTable;
import java.util.List;

import l1j.server.Config;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_Party;
import l1j.server.server.serverpackets.S_ServerMessage;

// Referenced classes of package l1j.server.server.model:
// L1Party

public class L1Party {

	private final List<L1PcInstance> _membersList = new FastTable<L1PcInstance>();

	private L1PcInstance _leader = null;

	public void addMember(L1PcInstance pc) {
		if (pc == null) {
			throw new NullPointerException();
		}
		if (_membersList.size() == Config.MAX_PT && !_leader.isGm()
				|| _membersList.contains(pc)) {
			return;
		}

		if (_membersList.isEmpty()) {
			setLeader(pc);
		} else {
			createMiniHp(pc);
		}
		_membersList.add(pc);
		pc.setParty(this);
		showAddPartyInfo(pc);
        pc.sendPackets(new S_Party(0x6e, pc));
	}
	private void removeMember(L1PcInstance pc) {
		if (!_membersList.contains(pc)) {
			return;
		}
		_membersList.remove(pc);
		pc.setParty(null);
		if (!_membersList.isEmpty()) {
			deleteMiniHp(pc);
		}
	}
	public boolean isVacancy() {
		return _membersList.size() < Config.MAX_PT;
	}

	public int getVacancy() {
		return Config.MAX_PT - _membersList.size();
	}

	public boolean isMember(L1PcInstance pc) {
		return _membersList.contains(pc);
	}

	private void setLeader(L1PcInstance pc) {
		_leader = pc;
	}

	public L1PcInstance getLeader() {
		return _leader;
	}

	public boolean isLeader(L1PcInstance pc) {
		return pc.getId() == _leader.getId();
	}

	public boolean isAutoDivision(L1PcInstance pc) {
		return pc.getPartyType() == 1;
	}

	public String getMembersNameList() {
		String _result = new String("");
		for (L1PcInstance pc : _membersList) {
			_result = _result + pc.getName() + " ";
		}
		return _result;
	}
	public void refresh(L1PcInstance pc){//파티추가
		  for (L1PcInstance member : getMembers()) {
		   if (pc.getId() == member.getId()){
		    continue;
		   }
		   if (member.getParty().getLeader() == pc){
		    member.sendPackets(new S_Party(0x6c2, pc));
		   } else {
		    member.sendPackets(new S_Party(0x6c1, pc));
		   }
		  }
		 }
		 
		 public void memberDie(L1PcInstance pc){//파티추가
		  for (L1PcInstance member : getMembers()) {
		   if (pc.getId() == member.getId()){
		    continue;
		   }
		   member.sendPackets(new S_Party(0x6c0, pc));
		  }
		 }
	private void showAddPartyInfo(L1PcInstance pc) {
		for (L1PcInstance member : getMembers()) {
			if (pc.getId() == getLeader().getId() && getNumOfMembers() == 1) {
				continue;
			}
			// 만약 2명이상일경우 리더이면 69아니면 68
			if (pc.getId() == member.getId()) {
				pc.sendPackets(new S_Party(0x68, pc));
			} else {// 리더가 아닌경우
				member.sendPackets(new S_Party(0x69, pc));
			}
			member.sendPackets(new S_Party(0x6e, member));
			createMiniHp(member);
		}
	}
	private void createMiniHp(L1PcInstance pc) {
		for (L1PcInstance member : getMembers()) {
			member.sendPackets(new S_HPMeter(pc.getId(), 100
					* pc.getCurrentHp() / pc.getMaxHp()));
			pc.sendPackets(new S_HPMeter(member.getId(), 100
					* member.getCurrentHp() / member.getMaxHp()));
		}
	}

	private void deleteMiniHp(L1PcInstance pc) {
		for (L1PcInstance member : getMembers()) {
			pc.sendPackets(new S_HPMeter(member.getId(), 0xff));
			member.sendPackets(new S_HPMeter(pc.getId(), 0xff));
		}
	}

	public void updateMiniHP(L1PcInstance pc) {
		for (L1PcInstance member : getMembers()) {
			member.sendPackets(new S_HPMeter(pc.getId(), 100
					* pc.getCurrentHp() / pc.getMaxHp()));
		}
	}

	private void breakup() {

		for (L1PcInstance member : getMembers()) {
			removeMember(member);
			member.sendPackets(new S_ServerMessage(418));
		}
	}

	public void passLeader(L1PcInstance pc) { // 리더위임
		for (L1PcInstance member : getMembers()) {
			member.getParty().setLeader(pc);
			member.sendPackets(new S_Party(0x6A, pc));
		}
	}
	public void leaveMember(L1PcInstance pc) {
		if (isLeader(pc) || getNumOfMembers() == 2) {
			breakup();
		} else {
			removeMember(pc);
			for (L1PcInstance member : getMembers()) {
				sendLeftMessage(member, pc);
			}
			sendLeftMessage(pc, pc);
		}
	}
	public void kickMember(L1PcInstance pc) { // 리더추방
		if (getNumOfMembers() == 2) {
			breakup();
		} else {
			removeMember(pc);
			for (L1PcInstance member : getMembers()) {
				sendLeftMessage(member, pc);
			}
			sendKickMessage(pc);
		}
		pc.sendPackets(new S_ServerMessage(419)); // 파티로부터 추방되었습니다.
	}

	public L1PcInstance[] getMembers() {
		return _membersList.toArray(new L1PcInstance[_membersList.size()]);
	}

	public int getNumOfMembers() {
		return _membersList.size();
	}

	private void sendKickMessage(L1PcInstance kickpc) {
		kickpc.sendPackets(new S_ServerMessage(419));
	}

	private void sendLeftMessage(L1PcInstance sendTo, L1PcInstance left) {
		sendTo.sendPackets(new S_ServerMessage(420, left.getName()));
	}

}
