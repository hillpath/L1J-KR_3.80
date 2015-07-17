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
package l1j.server.server.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

import javolution.util.FastTable;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.Account;
import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Clan.ClanMember;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

/**
 * 스킬 아이콘이나 차단 리스트의 표시 등 복수의 용도에 사용되는 패킷의 클래스
 */
public class S_PacketBox extends ServerBasePacket {
	private static final String S_PACKETBOX = "[S] S_PacketBox";

	private byte[] _byte = null;

	// *** S_107 sub code list ***
	public static final int UPDATE_OLD_PART_MEMBER = 104;

	/** 3.3C */
	public static final int PATRY_UPDATE_MEMBER = 105;

	/** 3.3C  */
	public static final int PATRY_SET_MASTER = 106;

	/** 3.3C  */
	public static final int PATRY_MEMBERS = 110;
	
	public static final int UNLIMITED_ICON = 147;
	
	/** 사운드 재생 */
	 public static final int  PLAYSOUND= 73; 

	/** 혈맹리스트 */
	//public static final int CLAN_LIST = 119;
    public static final int ER_UpDate = 132;
	
	public static final int BOOKMARK_SIZE_PLUS_10 = 141;// 기억 확장
	
	/** 남은시간표시*/
	public static final int MAP_TIMER = 153;  //남은시간표시
	public static final int MAP_TIMER_OUT = 159;
	
	/** 보안버프 */
	public static final int ICON_SECURITY_SERVICES = 125;
	
	/** 혈맹 창고리스트 */
	public static final int CLAN_WAREHOUSE_LIST = 117;// 

	/** pc방 아이콘*/
	public static final int ICON_PC_BUFF = 127; 

	// 1:Kent 2:Orc 3:WW 4:Giran 5:Heine 6:Dwarf 7:Aden 8:Diad 9:성명 9 ...
	/** C(id) H(?): %s의 공성전이 시작되었습니다. */
	public static final int MSG_WAR_BEGIN = 0;

	/** C(id) H(?): %s의 공성전이 종료했습니다. */
	public static final int MSG_WAR_END = 1;

	/** C(id) H(?): %s의 공성전이 진행중입니다. */
	public static final int MSG_WAR_GOING = 2;

	/** -: 성의 주도권을 잡았습니다. (음악이 바뀐다) */
	public static final int MSG_WAR_INITIATIVE = 3;

	/** -: 성을 점거했습니다. */
	public static final int MSG_WAR_OCCUPY = 4;

	/** ?: 결투가 끝났습니다. (음악이 바뀐다) */
	public static final int MSG_DUEL = 5;

	/** C(count): SMS의 송신에 실패했습니다. / 전부%d건송신되었습니다. */
	public static final int MSG_SMS_SENT = 6;

	/** -: 축복안, 2명은 부부로서 연결되었습니다. (음악이 바뀐다) */
	public static final int MSG_MARRIED = 9;

	/** C(weight): 중량(30 단계) */
	public static final int WEIGHT = 10;

	/** C(food): 만복도(30 단계) */
	public static final int FOOD = 11;

	/** C(0) C(level): 이 아이템은%d레벨 이하만 사용할 수 있습니다. (0~49이외는 표시되지 않는다) */
	public static final int MSG_LEVEL_OVER = 12;

	/** UB정보 HTML */
	public static final int HTML_UB = 14;

	/**
	 * C(id)<br>
	 * 1:몸에 담겨져 있던 정령의 힘이 공기안에 녹아 가는 것을 느꼈습니다.<br>
	 * 2:몸의 구석구석에 화의 정령력이 스며들어 옵니다.<br>
	 * 3:몸의 구석구석에 물의 정령력이 스며들어 옵니다.<br>
	 * 4:몸의 구석구석에 바람의 정령력이 스며들어 옵니다.<br>
	 * 5:몸의 구석구석에 땅의 정령력이 스며들어 옵니다.<br>
	 */
	public static final int MSG_ELF = 15;

	/** C(count) S(name)...: 차단 리스트 복수 추가 */
	public static final int ADD_EXCLUDE2 = 17;

	/** S(name): 차단 리스트 추가 */
	public static final int ADD_EXCLUDE = 18;

	/** S(name): 차단 해제 */
	public static final int REM_EXCLUDE = 19;

	/** 스킬 아이콘 */
	public static final int ICONS1 = 20;

	/** 스킬 아이콘 */
	public static final int ICONS2 = 21;

	/** 아우라계의 스킬 아이콘 및 이레이즈매직 아이콘 삭제 */
	public static final int ICON_AURA = 22;

	/** S(name): 타운 리더에게%s가 선택되었습니다. */
	public static final int MSG_TOWN_LEADER = 23;

	/**
	 * D(혈맹원수) (S(혈원이름) C(혈원계급)) 혈맹원 갱신이 된 상태에서의 /혈맹.
	 */
	public static final int PLEDGE_TWO = 24;

	/**
	 * D(혈맹원이름) C(랭크) 혈맹에 추가된 인원이 있을때 보내주는 패킷
	 */
	public static final int PLEDGE_REFRESH_PLUS = 25;

	/**
	 * D(혈맹원이름) C(랭크) 혈맹에 삭제된 인원이 있을때 보내주는 패킷
	 */
	public static final int PLEDGE_REFRESH_MINUS = 26;

	/**
	 * C(id): 당신의 랭크가%s로 변경되었습니다. (1-견습 2-일반 3-수호기사)
	 */
	public static final int MSG_RANK_CHANGED = 27;

	/**
	 * D(혈맹원수) (S(혈원이름) C(혈원계급)) 혈맹원 갱신이 안된 상태에서의 /혈맹.
	 */
	public static final int PLEDGE_ONE = 119;

	/** D(?) S(name) S(clanname): %s혈맹의%s가 라스타바드군을 치웠습니다. */
	public static final int MSG_WIN_LASTAVARD = 30;

	/** -: \f1기분이 좋아졌습니다. */
	public static final int MSG_FEEL_GOOD = 31;

	/** 불명.C_30 패킷이 난다 */
	public static final int SOMETHING1 = 33;

	/** H(time): 블루 일부의 아이콘이 표시된다. */
	public static final int ICON_BLUEPOTION = 34;

	/** H(time): 변신의 아이콘이 표시된다. */
	public static final int ICON_POLYMORPH = 35;

	/** H(time): 채팅 금지의 아이콘이 표시된다. */
	public static final int ICON_CHATBAN = 36;

	/** 펫 아이템 갱신 패킷 */
	public static final int PET_ITEM = 37;

	/** 혈맹 정보의 HTML가 표시된다 */
	public static final int HTML_CLAN1 = 38;

	/** H(time): 이뮤의 아이콘이 표시된다 */
	public static final int ICON_I2H = 40;

	/** 캐릭터의 게임 옵션, 쇼트 컷 정보등을 보낸다 */
	public static final int CHARACTER_CONFIG = 41;

	/** 캐릭터 선택 화면으로 돌아간다 */
	public static final int LOGOUT = 42;

	/** 전투중에 재시 동요할 수 없습니다. */
	public static final int MSG_CANT_LOGOUT = 43;

	/**
	 * C(count) D(time) S(name) S(info):<br>
	 * [CALL] 버튼이 붙은 윈도우가 표시된다. 이름을 더블 클릭 하면(자) C_RequestWho가 날아, 클라이언트의 폴더에
	 * bot_list.txt가 생성된다.이름을 선택해+키를 누르면(자) 새로운 윈도우가 열린다.
	 */
	public static final int CALL_SOMETHING = 45;

	/**
	 * C(id): 배틀 콜롯세움, 카오스 대전이―<br>
	 * id - 1:개시합니다 2:삭제되었던 3:종료합니다
	 */
	public static final int MSG_COLOSSEUM = 49;

	/** 혈맹 정보의 HTML */
	public static final int HTML_CLAN2 = 51;

	/** 요리 윈도우를 연다 */
	public static final int COOK_WINDOW = 52;

	/** C(type) H(time): 요리 아이콘이 표시된다 */
	public static final int ICON_COOKING = 53;

	/** 물고기찌 흔들림포시 */
	public static final int FISHING = 55;

	/** 아이콘 삭제 */
	public static final int DEL_ICON = 59;

	public static final int EXP_POTION3 = 9278;
	
	public static final int EXP_POTION2 = 9279;
	/** 계정 시간 type:예약결제 time:시간 */
	public static final int ACCOUNT_TIME = 61;

	/** 동맹 목록 */
	public static final int PLEDGE_UNION = 62;

	/** 미니게임 : 5,4,3,2,1 카운트 */
	public static final int MINIGAME_START_COUNT = 64;

	/** 미니게임 : 타임(0:00시작) */
	public static final int MINIGAME_START_TIME = 65;

	/** 미니게임 : 게임자 리스트 */
	public static final int MINIGAME_LIST = 66;

	/** 미니게임 : 잠시 후 마을로 이동됩니다(10초 음) * */
	public static final int MINIGAME_10SECOND_COUNT = 69;

	/** 미니게임 : 종료 */
	public static final int MINIGAME_END = 70;

	/** 미니게임 : 타임 */
	public static final int MINIGAME_TIME = 71;

	/** 미니게임 : 타임삭제 */
	public static final int MINIGAME_TIME_CLEAR = 72;

	/** 팔을 다쳐 공격 능력이 하락합니다. */
	public static final int DAMAGE_DOWN = 74;
	/** 용기사 : 약점 노출 */
	public static final int SPOT = 75;

	/** 아인하사드 버프 */
	public static final int AINHASAD = 82;

	public static final int HADIN_DISPLAY = 83;//인던이펙트
	/**
	 * 우호도 UI 표시 + 욕망의 동굴 - 그림자 신전
	 */
	/** 우호도표기추가 */
	public static final int KARMA = 87; // 미니맵파티갱신

	/**
	 * 우호도 다음에 오는 불분명 패킷 2개 ↓
	 */
	public static final int INIT_DG = 88;// UI DG표시

	/** DG */
	public static final int UPDATE_DG = 101;// UI DG표시

	/** 혈흔 패킷 */
	public static final int DRAGONBLOOD = 100;
	/** 드래곤 레이드 포탈 패킷  */
	public static final int DRAGONMENU = 102;
	
	public static final int BAPO = 114; // 바포깃발

	public static final int DRAGONPERL = 60; // 드래곤진주

	/** 인던 녹색 메세지 */
	public static final int GREEN_MESSAGE = 84;

	public static final int YELLOW_MESSAGE = 00000; // 인던 챕터2 대기

	/** 인던 녹색 메세지 */
	public static final int EMERALD_EVA = 86;//드래곤에메랄드

	public S_PacketBox(int subCode) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case MSG_WAR_INITIATIVE:
		case MSG_WAR_OCCUPY:
		case MSG_MARRIED:
		case MSG_FEEL_GOOD:
		case MSG_CANT_LOGOUT:
		case ICON_PC_BUFF:
		case LOGOUT:
			break;

		case FISHING:
		case MINIGAME_START_TIME:
		case MINIGAME_TIME_CLEAR:
			break;
		case CALL_SOMETHING:
			callSomething();
			break;
		case DEL_ICON:
			writeH(0);
			break;
		case ICON_AURA:
			writeC(0x98);
			writeC(0);
			writeC(0);
			writeC(0);
			writeC(0);
			writeC(0);
			break;
		case MINIGAME_10SECOND_COUNT:
			writeC(10);
			writeC(109);
			writeC(85);
			writeC(208);
			writeC(2);
			writeC(220);
			break;
		case MINIGAME_END:
			writeC(147);
			writeC(92);
			writeC(151);
			writeC(220);
			writeC(42);
			writeC(74);
			break;
		case MINIGAME_START_COUNT:
			writeC(5);
			writeC(129);
			writeC(252);
			writeC(125);
			writeC(110);
			writeC(17);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, int value) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ICON_BLUEPOTION:
		case ICON_CHATBAN:
		case ICON_I2H:
		case ICON_POLYMORPH:
		case MINIGAME_TIME:
		case BOOKMARK_SIZE_PLUS_10:
			   writeC(value);
			   break;
			
		case INIT_DG:// UI DG표시
			writeH(value); // time
			break;
		 case MAP_TIMER:
			   writeH(value); // time
			   break;
		case MSG_WAR_BEGIN:
		case MSG_WAR_END:
		case MSG_WAR_GOING:
			writeC(value); // castle id
			writeH(0); // ?
			break;
		case MSG_SMS_SENT:
		case WEIGHT:
		case FOOD:
		case UPDATE_DG: // UI DG표시
			writeC(value);
			break;
		case KARMA: // 우호도표기추가
			writeD(value);
			break;
		case MSG_ELF:
		case MSG_RANK_CHANGED:
		case MSG_COLOSSEUM:
		case SPOT:
		case ER_UpDate:
			writeC(value); // msg id
			break;
		case MSG_LEVEL_OVER:
			writeC(0); // ?
			writeC(value); // 0-49이외는 표시되지 않는다
			break;
		case COOK_WINDOW:
			writeC(0xdb); // ?
			writeC(0x31);
			writeC(0xdf);
			writeC(0x02);
			writeC(0x01);
			writeC(value); // level
			break;
		case AINHASAD:
			value /= 10000;
			writeD(value);// % 수치 1~200
		case HADIN_DISPLAY://인던이펙트
			writeC(value);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, int type, boolean show){

		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case UNLIMITED_ICON:
			writeC(show ? 0x01 : 0x00); // On Off
			writeC(type); // 
		   break;
		
		case BAPO:
			writeD(type); // 1~7 깃발
			writeD(show ? 0x01 : 0x00);
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, int type, int time) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case DRAGONPERL:// 드래곤진주
			writeC(time);
			writeC(type);
			break;
		case EXP_POTION2:
			   writeC(time);
			   writeC(type);
			   break;
		case EXP_POTION3:
			   writeC(time);
			   writeC(type);
			   break;
		case ACCOUNT_TIME:
			writeD(time);
			writeC(type);
		case ICON_COOKING:
			if (type != 7) {
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x12);
				writeC(0x0c);
				writeC(0x09);
				writeC(0x00);
				writeC(0x00);
				writeC(type);
				writeC(0x24);
				writeH(time);
				writeH(0x00);
			} else {
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x0c);
				writeC(0x12);
				writeC(0x0c);
				writeC(0x09);
				writeC(0xc8);
				writeC(0x00);
				writeC(type);
				writeC(0x26);
				writeH(time);
				writeC(0x3e);
				writeC(0x87);
			}
			break;
		case ICON_AURA:
			writeC(0xdd);
			writeH(time);
			writeC(type);
			break;
		case DRAGONBLOOD:
			writeC(type);
			writeD(time);
			break;
		case MSG_DUEL:
			writeD(type);
			writeD(time);
			break;
		case EMERALD_EVA://드래곤에메랄드
			writeC(0x70);
			writeC(1);
			writeC(type);// 2: 2배로적용 아니라면:사용불가
			writeH(time);// 초로 적용
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, int type, int petid, int ac) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case PET_ITEM:
			writeC(type);
			writeD(petid); // pet objid
			writeH(ac);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, String name) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ADD_EXCLUDE:
		case REM_EXCLUDE:
		case MSG_TOWN_LEADER:
//		case PLEDGE_UNION:
			writeS(name);
			break;
		case PLAYSOUND:  //?
			   writeH(Integer.parseInt(name));
			   break;

		/** 인던 녹색 메세지 */
		case GREEN_MESSAGE: // 인던추가
			writeC(2);
			writeS(name);
			break;
		case YELLOW_MESSAGE:
			/** 인던 녹색 메세지 */
			break;
		default:
			break;
		}
	}

	public S_PacketBox(int subCode, Object[] names) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case ADD_EXCLUDE2:
			writeC(names.length);
			for (Object name : names) {
				writeS(name.toString());
			}
			break;
		default:
			break;
		}
	}
	
	

	
	

	public S_PacketBox(int subCode, int id, String name, String clanName) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		case MSG_WIN_LASTAVARD:
			writeD(id); // 크란 ID인가 무엇인가?
			writeS(name);
			writeS(clanName);
			break;
		default:
			break;
		}
	}

	public S_PacketBox(L1PcInstance pc, int subCode) {
		String clanName = pc.getClanname();
		L1Clan clan = L1World.getInstance().getClan(clanName);

		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);

		switch (subCode) {
		 case CLAN_WAREHOUSE_LIST:
			   int count = 0;
			   Connection con = null;
			   PreparedStatement pstm = null;
			   PreparedStatement pstm2 = null;
			   PreparedStatement pstm3 = null;
			   ResultSet rs = null;
			   ResultSet rs3 = null;
			   try {
			    con = L1DatabaseFactory.getInstance().getConnection();
			    pstm = con.prepareStatement("SELECT id, time FROM clan_warehouse_log WHERE clan_name='" + pc.getClanname() + "'");
			    rs = pstm.executeQuery();
			    while (rs.next()) {
			     if (System.currentTimeMillis() - rs.getTimestamp(2).getTime() > 4320000) {// 3일
			      pstm2 = con.prepareStatement("DELETE FROM clan_warehouse_log WHERE id='" + rs.getInt(1) + "'");
			      pstm2.execute();
			     } else
			      count++;
			    }
			    writeD(count);
			    pstm3 = con.prepareStatement("SELECT name, item_name, item_count, type, time FROM clan_warehouse_log WHERE clan_name='"
			      + pc.getClanname() + "'");
			    rs3 = pstm3.executeQuery();
			    while (rs3.next()) {
			     writeS(rs3.getString(1));
			     writeC(rs3.getInt(4));// 0:맡김 1:찾음
			     writeS(rs3.getString(2));
			     writeD(rs3.getInt(3));
			     writeD((int) (System.currentTimeMillis() - rs3.getTimestamp(5).getTime()) / 60000);// 경과시간 분
			    }
			   } catch (SQLException e) {
			   } finally {
			    SQLUtil.close(rs, pstm, con);
			    SQLUtil.close(pstm2);
			    SQLUtil.close(rs3);
			    SQLUtil.close(pstm3);
			   }
			   break;

				
		
		case PLEDGE_ONE:
			writeD(clan.getOnlineMemberCount());
			   for (L1PcInstance targetPc : clan.getOnlineClanMember()) { 
			    writeS(targetPc.getName());
			    writeC(targetPc.getClanRank());
			   }
			   if (clan.getClanBirthDay() != null){
				   String.valueOf(clan.getClanBirthDay());
				   
			   } else { //필드추가시 null값이므로.. 임시적용
			    writeD((int)(System.currentTimeMillis() / 1000L));
			   }
			   
			   writeS(clan.getLeaderName());
				break;
			
			
		case PLEDGE_TWO:
			writeD(clan.getClanMemberList().size());

			ClanMember member;
			FastTable<ClanMember> clanMemberList = clan.getClanMemberList(); // 모든
			// 혈맹원의
			// 이름과
			// 등급
			for (int i = 0; i < clanMemberList.size(); i++) {
				member = clanMemberList.get(i);
				writeS(member.name);
				writeC(member.rank);
			}

			writeD(clan.getOnlineMemberCount());
			for (L1PcInstance targetPc : clan.getOnlineClanMember()) { // 온라인
				writeS(targetPc.getName());
			}
			break;
		case PLEDGE_REFRESH_PLUS:
		case PLEDGE_REFRESH_MINUS:
			writeS(pc.getName());
			writeC(pc.getClanRank());
			writeH(0);
			break;
		case KARMA:
			writeD(pc.getKarma());
			writeH(0); // 필요할까?
			break;
		default:
			break;
		}
	}
	public S_PacketBox(int subCode, L1ItemInstance Key){ //드래곤 키
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(subCode);
		  switch (subCode) {
		  case DRAGONMENU :
			  writeD(Key.getId());
			  writeC(1);  // 안타
			  writeC(1);  // 파푸
			  writeC(1);  // 린드레이드
			  writeC(0);  // 발라
			  break; default: break; }
		  }
	public S_PacketBox(String name, int mapid, int x, int y, int Mid) { //매니맵위치전송
          writeC(Opcodes.S_OPCODE_PACKETBOX);
		  writeC(0x6F);
		  writeS(name); 
		  writeH(mapid);
		  writeH(x);
		  writeH(y);
		  writeH(Mid);
		  writeH(0);
		 } 
	private void callSomething() {
		Iterator<L1PcInstance> itr = L1World.getInstance().getAllPlayers()
				.iterator();

		writeC(L1World.getInstance().getAllPlayers().size());
		L1PcInstance pc = null;
		Account acc = null;
		Calendar cal = null;
		while (itr.hasNext()) {
			pc = itr.next();
			acc = Account.load(pc.getAccountName());

			// 시간 정보 우선 로그인 시간을 넣어 본다655

			if (acc == null) {
				writeD(0);
			} else {
				cal = Calendar.getInstance(TimeZone
						.getTimeZone(Config.TIME_ZONE));
				long lastactive = acc.getLastActive().getTime();
				cal.setTimeInMillis(lastactive);
				cal.set(Calendar.YEAR, 1970);
				int time = (int) (cal.getTimeInMillis() / 1000);
				writeD(time); // JST 1970 1/1 09:00 이 기준
			}

			// 캐릭터 정보
			writeS(pc.getName()); // 반각 12자까지
			writeS(pc.getClanname()); // []내에 표시되는 캐릭터 라인.반각 12자까지
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}

		return _byte;
	}

	@Override
	public String getType() {
		return S_PACKETBOX;
	}
}
