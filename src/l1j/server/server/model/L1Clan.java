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


import java.util.logging.Logger;

import javolution.util.FastTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

public class L1Clan {
	static public class ClanMember {
		public String name;

		public int rank;

		public ClanMember(String name, int rank) {
			this.name = name;
			this.rank = rank;
		}
	}

	 public static final int CLAN_RANK_PROBATION = 7;// 수련
	 public static final int CLAN_RANK_SUB_PRINCE = 3;// 부군주
	 public static final int CLAN_RANK_PRINCE = 10;// 군주
	 public static final int CLAN_RANK_PUBLIC = 8;// 일반
	 public static final int CLAN_RANK_GUARDIAN = 9;// 수호기사

	@SuppressWarnings("unused")
	private static final Logger _log = Logger.getLogger(L1Clan.class.getName());

	private int _clanId;

	private String _clanName;

	private int _leaderId;

	private String _leaderName;

	private int _castleId;

	private int _houseId;

	private int _alliance;
	
	private String _clanBirthday;
	
	private int _maxuser;
	
	/**혈맹자동가입*/
	private boolean _bot; 
	private int _bot_style; 
	private int _bot_level; 
	/**혈맹자동가입*/
	private FastTable<ClanMember> clanMemberList = new FastTable<ClanMember>();

	public FastTable<ClanMember> getClanMemberList() {
		return clanMemberList;
	}

	public void addClanMember(String name, int rank) {
		clanMemberList.add(new ClanMember(name, rank));
	}

	public void removeClanMember(String name) {
		for (int i = 0; i < clanMemberList.size(); i++) {
			if (clanMemberList.get(i).name.equals(name)) {
				clanMemberList.remove(i);
				break;
			}
		}
	}
///////////혈맹리뉴얼//////////////
	public void setClanRank(String name, int data){
		for (int i = 0; i < clanMemberList.size(); i++) {
			if (clanMemberList.get(i).name.equals(name)) {
				clanMemberList.get(i).rank = data;
				break;
			}
		}
	}
///////////혈맹리뉴얼//////////////
	public int getOnlineMaxUser() { return _maxuser; }
	public void setOnlineMaxUser(int i) { _maxuser = i; }
	
	//실시간 변경
	public void UpdataClanMember(String name, int rank) {
	    for(int i = 0 ; i < clanMemberList.size() ; i++) {
	            if(clanMemberList.get(i).name.equals(name)) {
	                clanMemberList.get(i).rank = rank;
	                break;
	            }
	        }
	    }
	
	
	public String getClanBirthDay() { return _clanBirthday; }
	public void setClanBirthDay(String t){	_clanBirthday = t; }
	public int getClanId() {
		return _clanId;
	}

	public void setClanId(int clan_id) {
		_clanId = clan_id;
	}

	public String getClanName() {
		return _clanName;
	}

	public void setClanName(String clan_name) {
		_clanName = clan_name;
	}

	public int getLeaderId() {
		return _leaderId;
	}

	public void setLeaderId(int leader_id) {
		_leaderId = leader_id;
	}

	public String getLeaderName() {
		return _leaderName;
	}

	public void setLeaderName(String leader_name) {
		_leaderName = leader_name;
	}

	public int getCastleId() {
		return _castleId;
	}

	public void setCastleId(int hasCastle) {
		_castleId = hasCastle;
	}

	public int getHouseId() {
		return _houseId;
	}

	public void setHouseId(int hasHideout) {
		_houseId = hasHideout;
	}

	public int getAlliance() {
		return _alliance;
	}

	public void setAlliance(int alliance) {
		_alliance = alliance;
	}

	// 온라인중의 혈원수
	public int getOnlineMemberCount() {
		int count = 0;
		for (int i = 0; i < clanMemberList.size(); i++) {
			if (L1World.getInstance().getPlayer(clanMemberList.get(i).name) != null) {
				count++;
			}
		}
		return count;
	}

	public L1PcInstance[] getOnlineClanMember() {
		FastTable<L1PcInstance> onlineMembers = new FastTable<L1PcInstance>();
		L1PcInstance pc = null;
		for (int i = 0; i < clanMemberList.size(); i++) {
			pc = L1World.getInstance().getPlayer(clanMemberList.get(i).name);
			if (pc != null && !onlineMembers.contains(pc)) {
				onlineMembers.add(pc);
			}
		}
		return onlineMembers.toArray(new L1PcInstance[onlineMembers.size()]);
	}

	// 전체 혈원 네임 리스트
	public String getAllMembersFP() {
		String result = "";
		String rank = "";
		for(int i = 0 ; i < clanMemberList.size() ; i++) {
			switch(clanMemberList.get(i).rank){
			case CLAN_RANK_PROBATION:
				rank = "[견습기사]";
				break;
			case CLAN_RANK_PUBLIC:
				rank = "[일반]";
				break;
			case CLAN_RANK_SUB_PRINCE:
				rank = "[부군주]";
				break;
			case CLAN_RANK_GUARDIAN:
				rank = "[수호기사]";
				break;
			case CLAN_RANK_PRINCE:
				rank = "[혈맹군주]";
				break;
			}
			result = result + clanMemberList.get(i).name + rank + " ";
		}
		return result;
	}

	// 온라인중의 혈원 네임 리스트
	public String getOnlineMembersFP() {
		String result = "";
		String rank = "";
		L1PcInstance pc = null;
		for(int i = 0 ; i < clanMemberList.size() ; i++) {
			pc = L1World.getInstance().getPlayer(clanMemberList.get(i).name);
			if (pc != null){
				switch(clanMemberList.get(i).rank){
				case CLAN_RANK_PROBATION:
					rank = "[견습기사]";
					break;
				case CLAN_RANK_PUBLIC:
					rank = "[일반]";
					break;
				case CLAN_RANK_SUB_PRINCE:
					rank = "[부군주]";
					break;
				case CLAN_RANK_GUARDIAN:
					rank = "[수호기사]";
					break;
				case CLAN_RANK_PRINCE:
					rank = "[혈맹군주]";
					break;
				}
				result = result + clanMemberList.get(i).name + rank + " ";
			}
		}
		return result;
	}
	public void announcement_message(L1PcInstance listener, int type, String msg1, String msg2){
		L1PcInstance[] members = getOnlineClanMember();
		for(int i = 0; i < members.length ; i++){
			if (members[i] == listener) {
				continue;
			}
			if (msg2 == null) {
				members[i].sendPackets(new S_ServerMessage(type, msg1));
			} else {
				members[i].sendPackets(new S_ServerMessage(type, msg1, msg2));
			}
		}	
	}

	public void announcement_message(L1PcInstance listener, int type, String msg1, String msg2, String msg3){
		L1PcInstance[] members = getOnlineClanMember();
		for(int i = 0; i < members.length ; i++){
			if (members[i] == listener) {
				continue;
			}
			members[i].sendPackets(new S_ServerMessage(type, msg1, msg2, msg3));
		}	
	}
	/**혈맹자동가입*/
	public boolean isBot() {
		return _bot;
	}
	public void setBot(boolean _bot) {
		this._bot = _bot;
	}

	public int getBotStyle() {
		return _bot_style;
	}
	public void setBotStyle(int _bot_style) {
		this._bot_style = _bot_style;
	}

	public int getBotLevel() {
		return _bot_level;
	}
	public void setBotLevel(int _bot_level) {
		this._bot_level = _bot_level;
	}
	/**혈맹자동가입*/
}
