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

import static l1j.server.server.model.skill.L1SkillId.ADVANCE_SPIRIT;
import static l1j.server.server.model.skill.L1SkillId.BLESS_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Serchdrop2;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;
import server.LineageClient;

// Referenced classes of package l1j.server.server:
// ClientThread, Shutdown, IpTable, MobTable,
// PolyTable, IdFactory

public class UserCommands {
	private static Logger _log = Logger.getLogger(UserCommands.class.getName());

	boolean spawnTF = false;

	private static UserCommands _instance;

	private UserCommands() {
	}

	public static UserCommands getInstance() {
		if (_instance == null) {
			_instance = new UserCommands();
		}
		return _instance;
	}

	public void handleCommands(L1PcInstance pc, String cmdLine) {
		StringTokenizer token = new StringTokenizer(cmdLine);
		String cmd = token.nextToken();
		String param = "";

		while (token.hasMoreTokens()) {
			param = new StringBuilder(param).append(token.nextToken()).append(
					' ').toString();
		}
		param = param.trim();
		try {

			if (cmd.equalsIgnoreCase("도움말")) {
				showHelp(pc);
			} else if (cmd.equalsIgnoreCase("봉줌신청")) {
				LockItem1(pc, param);
			/*} else if (cmd.equalsIgnoreCase("내랭킹")) {
				rank(pc);*/
			} else if (cmd.equalsIgnoreCase(".")) {
				telrek(pc);
/*			} else if (cmd.equalsIgnoreCase("랭킹검색")) {
				rank1(pc, param);*/
			} else if (cmd.equalsIgnoreCase("혈맹파티")) {
				BloodParty(pc);
			// } else if (cmd.equalsIgnoreCase("혈맹보호")) { 
				//  BloodProtect(pc);
			} else if (cmd.equalsIgnoreCase("혈맹버프")) {
					   clanbuff(pc);
			} else if (cmd.equalsIgnoreCase("버프")) {
				buff(pc);
		    } else if (cmd.equalsIgnoreCase("위치추적")) {
				TargetLoc(pc, param);
			 } else if (cmd.equalsIgnoreCase("버경멘트")) {
				bugment(pc, param);
			} else if (cmd.equalsIgnoreCase("수배")) {
				Hunt(pc, param);
			} else if (cmd.equalsIgnoreCase("오토루팅")) {
				autoroot(pc, cmd, param);
			} else if (cmd.equalsIgnoreCase("오토멘트")) {
				ment(pc, cmd, param);
			} else if (cmd.equalsIgnoreCase("깃털합치기")) {
				featherAdd(pc);
			} else if (cmd.equalsIgnoreCase("아덴합치기")) {
				adenaAdd(pc);
			} else if (cmd.equalsIgnoreCase("무인상점")) {
				LetsbeShop(pc);
			} else if (cmd.equalsIgnoreCase("혈맹마크")) {
				MarkView(pc, param);
			} else if (cmd.equalsIgnoreCase("..")) {
				 if (pc.getAccessLevel() != 2) {
						return;
					}
				gjtkd(pc);
			} else if (cmd.equalsIgnoreCase("암호변경")) {
				changePassword(pc, param);
			} else if (cmd.equalsIgnoreCase("퀴즈설정")) {
				quize(pc, param);
			} else if (cmd.equalsIgnoreCase("퀴즈삭제")) {
				quize1(pc, param);
			} else if (cmd.equalsIgnoreCase("나이")) {
				age(pc, param);
			} else if (cmd.equalsIgnoreCase("몹이름")) {
				serchdroplist2(pc, param);
			} else if (cmd.equalsIgnoreCase("캐릭명변경")) {
				changename(pc, param);
			} else if (cmd.equalsIgnoreCase("혈원소환")) {
				CallClan(pc, param);
			} else if (cmd.equalsIgnoreCase("입장시간")) { entertime(pc);
		} else if (cmd.equalsIgnoreCase("고정신청")) {
			phone(pc, param);
			} else {
				String msg = new StringBuilder().append("커멘드：").append(cmd)
						.append(" 가 존재하지 않습니다 .도움말 을처보세요!").toString();
				pc.sendPackets(new S_SystemMessage(msg));
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "UserCommands[handle]Error", e);
		}
	}

	private void showHelp(L1PcInstance pc) {
		pc.sendPackets(new S_SystemMessage("------------ User Commands ------------"));
		pc.sendPackets(new S_SystemMessage(" .수배 .나이 .암호변경 ..(텔렉) .버프"));
		pc.sendPackets(new S_SystemMessage(" .몹이름 .아덴(깃털)합치기 .위치추적"));
		pc.sendPackets(new S_SystemMessage(" .고정신청  .무인상점 .캐릭명변경 .버경멘트"));
		pc.sendPackets(new S_SystemMessage(" .오토멘트 .오토루팅 .퀴즈설정 .퀴즈삭제"));
		pc.sendPackets(new S_SystemMessage(" .봉줌신청 .내랭킹 .랭킹검색 .입장시간"));
		pc.sendPackets(new S_SystemMessage(" +군주전용+: .혈원소환 .혈맹파티 .혈맹마크 "));
		pc.sendPackets(new S_SystemMessage("------------ Have a Good Time ---------"));
	}
	private void phone(L1PcInstance pc, String param) {
		try {
			long curtime = System.currentTimeMillis() / 1000;
			if (pc.getQuizTime() + 20 > curtime) {
				long sec = (pc.getQuizTime() + 20) - curtime;
				pc.sendPackets(new S_SystemMessage(sec + "초간의 지연시간이 필요합니다."));
				return;
			}
			
			StringTokenizer tok = new StringTokenizer(param);
			String phone = tok.nextToken();
			Account account = Account.load(pc.getAccountName());

			if (phone.length() < 10) {
				pc.sendPackets(new S_SystemMessage("번호가 잘못 입력되었습니다."));
				pc.sendPackets(new S_SystemMessage(".고정신청 [전화번호]를 입력해주세요."));
				return;
			}

			if (isDisitAlpha(phone) == false) {
				pc.sendPackets(new S_SystemMessage("번호에 허용되지 않는 문자가 포함되었습니다."));
				return;
			}

			if (account.getphone() != null) {
				pc.sendPackets(new S_SystemMessage("이미 고정신청이 설정되어 있습니다."));
				pc.sendPackets(new S_SystemMessage("잘못 입력하셨을 경우 미소피아로 편지주세요."));
				return;
			}

			account.setphone(phone);
			Account.updatePhone(account);
			pc.sendPackets(new S_SystemMessage("번호  (" + phone + ") 가 설정되었습니다."));
			pc.sendPackets(new S_SystemMessage("오픈때 항상 연락 드릴께요!"));
			//pc.getInventory().storeItem(204601, 1);//고정목걸이
			pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_PC_BUFF));
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"[!] PC방버프 적용은 리스후부터 적용됩니다.", Opcodes.S_OPCODE_MSG, 19); 
			pc.sendPackets(s_chatpacket);
			pc.setQuizTime(curtime);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".고정신청 [전화번호(숫자만)]를 입력해주세요."));
		}
	}
	public void bugment(L1PcInstance pc, String param){
        if (param.equalsIgnoreCase("끔")) {
            pc.sendPackets(new S_SystemMessage("[!] 버그베어 레이스 관련 메세지를 해제합니다."));
            pc.isbugment(false);
        }
        else if (param.equalsIgnoreCase("켬")){
            pc.sendPackets(new S_SystemMessage("[!] 버그베어 레이스 관련 메세지를 활성화합니다."));
            pc.isbugment(true);
        }else {
            pc.sendPackets(new S_SystemMessage("\\fY[사용방법] .버경멘트 (켬)or(끔)"));
            if (!pc.isbugment()) {
                pc.sendPackets(new S_SystemMessage("현재 버경메세지 상태 : [OFF]"));
            }else {
                pc.sendPackets(new S_SystemMessage("현재 버경메세지 상태 : [ON]"));
            }
        }
        }

	 public void CallClan(L1PcInstance pc, String param) {
	    	try {
				StringTokenizer tok = new StringTokenizer(param);
				String charName = tok.nextToken();
				int castle_id;

				if (!pc.isCrown()) { // 군주 이외
					pc.sendPackets(new S_ServerMessage(92, pc.getName())); // \f1%0은 프린스나 프린세스가 아닙니다.
					return;
				}
				
				if (pc.getMapId() == 2006 
						|| pc.getMapId() == 88
						|| pc.getMapId() == 89
						|| pc.getMapId() == 90
						|| pc.getMapId() == 5069
						|| pc.getMapId() == 99 
						|| pc.getMapId() == 610 
						|| pc.getMapId() == 666 
						|| pc.getMapId() == 251 || pc.getMapId() == 208 || pc.getMapId() == 37 || pc.getMapId() == 65 || pc.getMapId() == 67 || pc.getMapId() == 784 || pc.getMapId() == 782 || pc.getMapId() == 603 || pc.getMapId() == 244 || pc.getMapId() == 1005 || pc.getMapId() == 1011 || pc.getMapId() == 5153) {
					pc.sendPackets(new S_SystemMessage("소환을 사용할 수 없는 장소입니다."));
					return;
				}

				if (!pc.getInventory().checkItem(40308, 1000000)) {
					pc.sendPackets(new S_SystemMessage("\\fY소환을 사용하기 위해 백만 아데나가 필요합니다."));
					return;
				}

				for (castle_id = 1; castle_id <= 8; castle_id++) {
					if (WarTimeController.getInstance().isNowWar(castle_id) && L1CastleLocation.getCastleIdByArea(pc) == castle_id) {
						pc.sendPackets(new S_SystemMessage("공성전 중에는 소환을 사용할 수 없습니다."));
						return;
					}
				}

				L1PcInstance player = L1World.getInstance().getPlayer(charName);

				if (player != null) {
					if (player.getClanid() == pc.getClanid()) {
					// 99,610,666,251,208,37,65,67,784,782,603,244,1005,1011,5153
					if (player.getMapId() == 99 
							|| player.getMapId() == 88 
							|| player.getMapId() == 89 
							|| player.getMapId() == 90 
							|| player.getMapId() == 5069 
							|| player.getMapId() == 360 
							|| player.getMapId() == 610 
							|| player.getMapId() == 666 
							|| player.getMapId() == 251 || player.getMapId() == 208 || player.getMapId() == 37 || player.getMapId() == 65 || player.getMapId() == 67 || player.getMapId() == 784 || player.getMapId() == 782 || player.getMapId() == 603 || player.getMapId() == 244 || player.getMapId() == 1005 || player.getMapId() == 1011 || player.getMapId() == 5153) {
						pc.sendPackets(new S_SystemMessage("혈맹원 " + player.getName() + "님은 소환할수 없는 위치에 있습니다."));
					} else {
						if (player.isPrivateShop()) {
							
							return;
						}
						L1Teleport.teleportToTargetFront(player, pc, 2);
						pc.sendPackets(new S_SystemMessage("\\fY" + charName + "님을 소환하였습니다."));
						pc.getInventory().consumeItem(40308, 1000000);
					}
				} else {
					pc.sendPackets(new S_SystemMessage("\\fY" + charName + "님은 같은 혈맹원이 아니거나 현재 접속하고 있지 않습니다."));
				  }
				}
			} catch (Exception e) {
				pc.sendPackets(new S_SystemMessage(".혈원소환 [캐릭터명]"));
			}

		}
	
	private void entertime(L1PcInstance pc) {
		try {
			int entertime1 = 180 - pc.getGdungeonTime() % 1000;
			int entertime2 = 180 - pc.getLdungeonTime() % 1000;
			int entertime3 = 120 - pc.getTkddkdungeonTime() % 1000;
			int entertime4 = 120 - pc.getDdungeonTime() % 1000;
			int entertime5 = 120 - pc.getoptTime() % 1000;
			   
			String time1 = Integer.toString(entertime1);
			String time2 = Integer.toString(entertime2);
			String time3 = Integer.toString(entertime3);
			String time4 = Integer.toString(entertime4);
			String time5 = Integer.toString(entertime5);
			
			
			pc.sendPackets(new S_ServerMessage(2535, "\\fY기란 감옥 던전", time1)); // 2535 %0 : 남은 시간 %1 분 // 순서는 기란 감옥: , 상아탑:, 라스타바드 던전:
			pc.sendPackets(new S_ServerMessage(2535, "\\fY라스타바드 던전", time2));
			pc.sendPackets(new S_ServerMessage(2535, "\\fY상아탑 던전", time3));
			pc.sendPackets(new S_ServerMessage(2535, "\\fY용의 던전", time4));
			pc.sendPackets(new S_ServerMessage(2535, "\\fY오크전초기지", time5));
		} catch (Exception e) {
		}
	}
	
	/** 위치추적 */
	private void TargetLoc(L1PcInstance pc, String param) {
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(param);
			String para1 = stringtokenizer.nextToken();
			L1PcInstance target = L1World.getInstance().getPlayer(para1);

			String msg = null;
			// 운영자 위치추적 금지.
			if (para1.equalsIgnoreCase("메티스")  || para1.equalsIgnoreCase("운영자") || para1.equalsIgnoreCase("카시오페아")|| para1.equalsIgnoreCase("도우미") || para1.equalsIgnoreCase("미소피아")){
				pc.sendPackets(new S_SystemMessage(param + "님은 접속중이 아닙니다."));
				return;
			}
			if (target != null) {
				if (pc.getInventory().checkItem(40308, 100000)){
					pc.getInventory().consumeItem(40308, 100000);
					int mapid = target.getMapId();
					if (mapid == 1){ msg = "말하는 섬 던전";
					} else if (target.getMap().isSafetyZone(target.getLocation())){ msg = "마을";
					} else if (mapid == 4 || mapid == 0 && target.getMap().isNormalZone(target.getLocation())){ msg = "필드";
					} else if (target.isPrivateShop()){ msg ="시장";
					} else if (mapid >= 7 && mapid <= 13){ msg = "글루디오 던전";
					} else if (mapid >= 18 && mapid <= 20){ msg = "요정족 던전";
					} else if (mapid >= 25 && mapid <= 28){ msg = "수련 던전";
					} else if (mapid >= 30 && mapid <= 33 || mapid >= 35 && mapid <= 36){ msg = "용의 계곡 던전";
					} else if (mapid >= 43 && mapid <= 51){ msg = "개미 던전";
					} else if (mapid >= 53 && mapid <= 56){ msg = "기란 감옥";
					} else if (mapid >= 59 && mapid <= 63){ msg = "에바 왕국";
					} else if (mapid == 70){ msg = "잊혀진 섬";
					} else if (mapid >= 271 && mapid <= 278){ msg = "수정 동굴";
					} else if (mapid >= 75 && mapid <= 82){ msg = "상아탑";
					} else if (mapid >= 101 && mapid <= 200){ msg = "오만의 탑";
					} else if (mapid == 301){ msg = "지하수로";
					} else if (mapid == 303){ msg = "몽환의 섬";
					} else if (mapid == 304){ msg = "침묵의 동굴";
					} else if (mapid >= 307 && mapid <= 309){ msg = "지하 침공로";
					} else if (mapid == 400){ msg = "대공동 저항군 지역";
					} else if (mapid == 401){ msg = "대공동 은둔자 지역";
					} else if (mapid == 410){ msg = "마족 신전";
					} else if (mapid == 420){ msg = "지저 호수";
					} else if (mapid == 430){ msg = "정령의 무덤";
					} else if (mapid == 5167){ msg = "악마왕의 영토";
					} else if (mapid == 5153){ msg = "배틀존";
					} else if (mapid >= 440 && mapid <= 444){ msg = "해적섬";
					} else if (mapid >= 450 && mapid <= 478 || mapid >= 490 && mapid <= 496 || mapid >= 530 && mapid <= 536 ){ msg = "라스타바드 성";
					} else if (mapid >= 521 && mapid <= 524){ msg = "그림자 신전";
					} else if (mapid >= 600 && mapid <= 607){ msg = "욕망의 동굴";
					} else if (mapid >= 777 && mapid <= 779){ msg = "버림받은 땅";
					} else if (mapid >= 780 && mapid <= 782){ msg = "테베";
					} else if (mapid >= 783 && mapid <= 784){ msg = "티칼";
					} else if (mapid == 5302 && mapid == 5490){ msg = "낚시터";
					} else { msg = "추적이 안되는 곳";
					}
					pc.sendPackets(new S_SystemMessage(target.getName() + "님의 위치를 추적중입니다."));
					Thread.sleep(2000);
					pc.sendPackets(new S_SystemMessage(target.getName()+"님의 위치는 "+msg+"입니다."));
				} else {
					pc.sendPackets(new S_SystemMessage("아데나가 부족합니다."));
				}
			} else {
				pc.sendPackets(new S_SystemMessage(param + "님은 접속중이 아닙니다."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("------------------< 위치추적 >--------------------"));
			pc.sendPackets(new S_SystemMessage("상대방의 위치를 추적하는 시스템입니다."));
			pc.sendPackets(new S_SystemMessage("위치 추적 비용은 10만 아데나가 소모됩니다."));
			pc.sendPackets(new S_SystemMessage(".위치추적 캐릭터명"));
			pc.sendPackets(new S_SystemMessage("--------------------------------------------------"));
		}
	}
	
	private void telrek(L1PcInstance pc) {
		try {
			int castle_id = L1CastleLocation.getCastleIdByArea(pc);
			// /////////////////////// 타임/////////////////////////////////
			long curtime = System.currentTimeMillis() / 1000;
			if (pc.getQuizTime() + 20 > curtime) {
				pc.sendPackets(new S_SystemMessage("20초간의 지연시간이 필요합니다."));
				return;
			}
			// /////////////////////// 타임/////////////////////////////////
			if (pc.getMapId() == 5302 || pc.getMapId() == 5490 ) {
				pc.sendPackets(new S_SystemMessage("이곳에서는 사용할 수 없습니다."));
				return;
			}
			if (CharPosUtil.getZoneType(pc) == 0 && castle_id != 0) {
				pc.sendPackets(new S_SystemMessage("성주변에서는 사용 할 수 없습니다."));
				return;
			}
			if (pc.getMapId() == 350|| pc.getMapId() == 5153) {
				pc.sendPackets(new S_SystemMessage("이곳에서는 사용할 수 없습니다."));
				return;
			}
			if (pc.isPinkName() || pc.isParalyzed() || pc.isSleeped()) {
				pc.sendPackets(new S_SystemMessage("보라중 마비중 잠수중에는 사용할 수 없습니다."));
				return;
			}
			if (pc.isDead()) {
				pc.sendPackets(new S_SystemMessage("죽은 상태에선 사용할 수 없습니다."));
				return;
			}
			if (!(pc.getInventory().checkItem(40308, 1000))) {
				pc.sendPackets(new S_SystemMessage("1000 아데나가 부족합니다."));
				return;
			}
			pc.getInventory().consumeItem(40308, 1000);

			 L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(),pc.getMoveState().getHeading(), false);
			pc.sendPackets(new S_SystemMessage("1000 아데나가 소모 되었습니다."));
			//pc.sendPackets(new S_SystemMessage("주변 오브젝트를 재로딩 하였습니다."));
			pc.setQuizTime(curtime);
	//	} catch (Exception e) {
		} catch (Exception exception35) {
		}
	}

	private void Hunt(L1PcInstance pc, String cmd) {
		try {
			StringTokenizer st = new StringTokenizer(cmd);
			String char_name = st.nextToken();
			int price = Integer.parseInt(st.nextToken());
			String story = st.nextToken();

			L1PcInstance target = null;
			target = L1World.getInstance().getPlayer(char_name);
			if (target != null) {
				if (target.isGm()) {
					return;
				}
			/*	if (char_name.equals(pc.getName())) {
					pc.sendPackets(new S_SystemMessage("자신에게 현상금을 걸수 없습니다."));
					return;
				}*/
				if (target.getHuntCount() == 1) {
					pc.sendPackets(new S_SystemMessage("이미 수배 되어있습니다"));
					return;
				}
				if (price < 1000000) {
					pc.sendPackets(new S_SystemMessage("최소 금액은 100만 아데나입니다"));
					return;
				}
				if (!(pc.getInventory().checkItem(40308, price))) {
					pc.sendPackets(new S_SystemMessage("아데나가 부족합니다"));
					return;
				}
				if (story.length() > 20) {
					pc.sendPackets(new S_SystemMessage("이유는 짧게 20글자로 입력하세요"));
					return;
				}
				target.setHuntCount(1);
				target.setHuntPrice(target.getHuntPrice() + price);
				target.setReasonToHunt(story);
				target.save();
				L1World.getInstance().broadcastServerMessage("\\fYL(" + target.getName() + ")의 목에 현상금이 걸렸습니다.");
				L1World.getInstance().broadcastPacketToAll(
						new S_SystemMessage("\\fY[이유:  " + story + "  ]"));
				L1World.getInstance().broadcastPacketToAll(
						new S_SystemMessage("\\fY[수배자:  " + target.getName() + "  ]"));
				pc.getInventory().consumeItem(40308, price);
			} else {
				pc.sendPackets(new S_SystemMessage("접속중이지 않습니다."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("[금액] 100만 추가타격치 부여"));
			pc.sendPackets(new S_SystemMessage(".수배 [캐릭터명] [금액] [이유]"));
		}
	}

	/** 나이 * */
	private void age(L1PcInstance pc, String cmd) {
		try {
			StringTokenizer tok = new StringTokenizer(cmd);
			String AGE = tok.nextToken();
			int AGEint = Integer.parseInt(AGE);

			if (AGEint > 99) {
				pc.sendPackets(new S_SystemMessage("입력하신 나이는 올바른 값이 아닙니다."));
				return;
			}
			pc.setAge(AGEint);
			pc.save();
			pc.sendPackets(new S_SystemMessage(pc.getName() + "님의 나이가 " + AGEint + "세로 설정되었습니다."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".나이 [숫자]로 입력하세요"));
		}
	}
	private void LockItem1(L1PcInstance pc, String param) {
		try {
			if (pc.getInventory().checkItem(50021, 1)) {
				pc.sendPackets(new S_SystemMessage("봉인해제주문서를 모두다 사용후 신청하세요."));
				return;
			}
			if (!(pc.getInventory().checkItem(40308, 300000))) {
				pc.sendPackets(new S_SystemMessage("봉인 해제 주문서 신청 요금이 부족합니다."));
				return;
			}
			pc.getInventory().consumeItem(40308, 300000);

			pc.getInventory().storeItem(50021, 10);
			pc.sendPackets(new S_SystemMessage("봉인 해제 주문서(10)장이 지급되었습니다."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("사용 예).봉줌신청 "));
		}
	}

	private void changename(L1PcInstance pc, String name) {
		if (BadNamesList.getInstance().isBadName(name)) {
			pc.sendPackets(new S_SystemMessage("생성 금지된 캐릭명입니다"));
			return;
		}
		if (pc.getClanid() > 0) {
			pc.sendPackets(new S_SystemMessage("혈맹탈퇴후 캐릭명을 변경할수 있습니다."));
			return;
		}
		if (CharacterTable.doesCharNameExist(name)) {
			pc.sendPackets(new S_SystemMessage("동일한 캐릭명이 존재 합니다"));
			return;
		}
		if (pc.isCrown()) {
			pc.sendPackets(new S_SystemMessage("군주캐릭은 캐릭명을 변경하실수 없습니다"));
			return;
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(1005)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(2005)) { // 채팅
			pc.sendPackets(new S_SystemMessage("채금중에는 변경하실수 없습니다."));
			return;
		}
		try {
			if (pc.getLevel() >= 55) {
				for (int i = 0; i < name.length(); i++) {
					if (name.charAt(i) == 'ㄱ'
							|| name.charAt(i) == 'ㄲ'
							|| name.charAt(i) == 'ㄴ'
							|| name.charAt(i) == 'ㄷ'
							|| // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㄸ'
							|| name.charAt(i) == 'ㄹ'
							|| name.charAt(i) == 'ㅁ'
							|| name.charAt(i) == 'ㅂ'
							|| // 한문자(char)단위로 비교
							name.charAt(i) == 'ㅃ'
							|| name.charAt(i) == 'ㅅ'
							|| name.charAt(i) == 'ㅆ'
							|| name.charAt(i) == 'ㅇ'
							|| // 한문자(char)단위로 비교
							name.charAt(i) == 'ㅈ'
							|| name.charAt(i) == 'ㅉ'
							|| name.charAt(i) == 'ㅊ'
							|| name.charAt(i) == 'ㅋ'
							|| // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㅌ'
							|| name.charAt(i) == 'ㅍ'
							|| name.charAt(i) == 'ㅎ'
							|| name.charAt(i) == 'ㅛ'
							|| // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㅕ'
							|| name.charAt(i) == 'ㅑ'
							|| name.charAt(i) == 'ㅐ'
							|| name.charAt(i) == 'ㅔ'
							|| // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㅗ'
							|| name.charAt(i) == 'ㅓ'
							|| name.charAt(i) == 'ㅏ'
							|| name.charAt(i) == 'ㅣ'
							|| // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㅠ'
							|| name.charAt(i) == 'ㅜ'
							|| name.charAt(i) == 'ㅡ'
							|| name.charAt(i) == 'ㅒ'
							|| // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㅖ' || name.charAt(i) == 'ㅢ'
							|| name.charAt(i) == 'ㅟ'
							|| name.charAt(i) == 'ㅝ'
							|| // 한문자(char)단위로 비교.
							name.charAt(i) == 'ㅞ' || name.charAt(i) == 'ㅙ'
							|| name.charAt(i) == 'ㅚ' || name.charAt(i) == 'ㅘ'
							|| // 한문자(char)단위로 비교.
							name.charAt(i) == '씹' || name.charAt(i) == '좃'
							|| name.charAt(i) == '좆' || name.charAt(i) == 'ㅤ') {
						pc.sendPackets(new S_SystemMessage("캐릭명이 올바르지 않습니다"));
						return;
					}
				}

				for (int i = 0; i < name.length(); i++) {
					if (!Character.isLetterOrDigit(name.charAt(i))) {

						pc.sendPackets(new S_SystemMessage("특수문자 공백등은 사용하지 못합니다"));
						return;
					}
				}

				int numOfNameBytes = 0;
				numOfNameBytes = name.getBytes("EUC-KR").length;

				if (numOfNameBytes < 2 || numOfNameBytes > 12) {
					pc.sendPackets(new S_SystemMessage("한글 6자 영문 12자 이내에서 입력바랍니다"));
					return;
				}

				if (pc.getInventory().consumeItem(467009, 1)) {// xxxxxxx사용 하실 아이템변호
					Connection con = null;
					PreparedStatement pstm = null;
					try {
						con = L1DatabaseFactory.getInstance().getConnection();
						pstm = con.prepareStatement("UPDATE characters SET char_name =? WHERE char_name = ?");
						pstm.setString(1, name);
						pstm.setString(2, pc.getName());
						pstm.execute();
					} catch (SQLException e) {
					} finally {
						SQLUtil.close(pstm);
						SQLUtil.close(con);
					}

					pc.save(); // 저장
					/** **** 여긴 파일로 캐릭명변경 내용 작성 부분****** */

					/** ****LogDB 라는 폴더를 미리 생성 해두세요****** */
					Calendar rightNow = Calendar.getInstance();
					int date = rightNow.get(Calendar.DATE);
					int hour = rightNow.get(Calendar.HOUR);
					int min = rightNow.get(Calendar.MINUTE);
					String strDate = "";
					String strhour = "";
					String strmin = "";
					strDate = Integer.toString(date);
					strhour = Integer.toString(hour);
					strmin = Integer.toString(min);
					String str = "";
					str = new String("[" + strDate + ":" + strhour + ":"
							+ strmin + "] " + pc.getName() + "  >  " + name
							+ "  [캐릭명변경]");// 로그 작성
					StringBuffer FileName = new StringBuffer(
							"LogDB/ChangPcName.txt");
					PrintWriter out = null;
					try {
						out = new PrintWriter(new FileWriter(FileName
								.toString(), true));
						out.println(str);
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					str = "";// 초기화

					pc.sendPackets(new S_SystemMessage(
							"재접속하시면 새로운 캐릭명으로 변경됩니다."));
					L1World.getInstance().broadcastPacketToAll(
							new S_SystemMessage("("+pc.getName() + ")님이 캐릭터명을("+ name
							+")으로 변경하였습니다."));
					buddys(pc);// 변경된 아이디를 디비에서 삭제....그래야 오류가 없습니다 ㅎㅎ
					Thread.sleep(500);
					pc.sendPackets(new S_Disconnect());

				} else {
					pc.sendPackets(new S_SystemMessage("캐릭명 변경 주문서가 없습니다."));
				}
			} else {
				pc.sendPackets(new S_SystemMessage("55레벨 이상 케릭만 사용가능합니다"));
			}

		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".캐릭명변경 케릭명 으로 입력해주세요"));
		}
	}

	/** *******디비 친구목록에서 변경된 아이디 지우기*********** */

	private void buddys(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;

		String aaa = pc.getName();

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM character_buddys WHERE buddy_name=?");

			pstm.setString(1, aaa);
			pstm.execute();

		} catch (SQLException e) {
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/** 혈맹 파티 신청 명령어 * */
	public void BloodParty (L1PcInstance pc){
		long curtime = System.currentTimeMillis() / 1000;
		if (pc.getQuizTime() + 20 > curtime) {
			pc.sendPackets(new S_SystemMessage("20초간의 지연시간이 필요합니다."));
			return;
		}
		if (pc.isDead()) {
			pc.sendPackets(new S_SystemMessage("죽은 상태에선 사용할 수 없습니다."));
			return;
		}
	
		int ClanId = pc.getClanid();
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		
		if(clan == null){
			pc.sendPackets(new S_SystemMessage("혈맹이 존재하지 않아 사용할 수 없습니다.")); 
			return;
		}
		
		if (pc.getClanRank() == 6 || pc.isCrown()){ //Clan[O] [군주,수호기사]
			for (L1PcInstance SearchBlood : clan.getOnlineClanMember()) {
				if(SearchBlood.getClanid()!= ClanId || SearchBlood.isPrivateShop() || SearchBlood.isInParty()){ // 클랜이 같지않다면[X], 이미파티중이면[X], 상점중[X]
					continue; // 포문탈출
				} else if(SearchBlood.getName() != pc.getName()){
					pc.setPartyType(1); // 파티타입 설정
					SearchBlood.setPartyID(pc.getId()); // 파티아이디 설정
					SearchBlood.sendPackets(new S_Message_YN(954, pc.getName())); // 분패파티 신청
					pc.sendPackets(new S_SystemMessage("당신은 ["+SearchBlood.getName()+"]에게 파티를 신청했습니다."));
				}
			}
			pc.setQuizTime(curtime);
		} else { // 클랜이 없거나 군주 또는 수호기사 [X]
			pc.sendPackets(new S_SystemMessage("혈맹의 [군주], [수호기사]만 사용할수 있습니다."));
		}
	}

	/** 혈맹 파티 신청 명령어 * */
	private void featherAdd(L1PcInstance pc) {
		try {
			long curtime = System.currentTimeMillis() / 1000;
			if (pc.getQuizTime() + 20 > curtime) {
				pc.sendPackets(new S_SystemMessage("20초간의 지연시간이 필요합니다."));
				return;
			}
			if (pc.getMapId() != 4) {
				pc.sendPackets(new S_SystemMessage("아덴 대륙에서만  사용할 수 있습니다."));
				return;
			}
			if (pc.isPinkName() || pc.isParalyzed() || pc.isSleeped()) {
				pc.sendPackets(new S_SystemMessage("보라중 마비중 잠수중에는 사용할 수 없습니다."));
				return;
			}
			if (pc.getMapId() == 5302 || pc.getMapId() == 5490 ) {
				pc.sendPackets(new S_SystemMessage("이곳에서는 사용할 수 없습니다."));
				return;
			}
			if (pc.getMapId() == 350) {
				pc.sendPackets(new S_SystemMessage("이곳에서는 사용할 수 없습니다."));
				return;
			}
			L1ItemInstance[] items = pc.getInventory().findItemsId(41159);
			int count = 0;

			for (L1ItemInstance item : items) {
				count += pc.getInventory().removeItem(item, item.getCount());
			}
			pc.getInventory().storeItem(41159, count, "추가:합치기");
			pc.sendPackets(new S_SystemMessage("총 " + count + "깃털을 합쳤습니다."));
			pc.setQuizTime(curtime);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".깃털합치기 로 입력해주세요."));
		}
	}

	/**
	 * 여기는 유저 커맨드 실행 함수
	 */
	public void ment(L1PcInstance pc, String cmd, String param) {
		if (param.equalsIgnoreCase("끔")) {
			pc.getSkillEffectTimerSet()
					.setSkillEffect(L1SkillId.STATUS_MENT, 0);
			pc.sendPackets(new S_SystemMessage("오토루팅 멘트를 끕니다."));
		} else if (param.equalsIgnoreCase("켬")) {
			pc.getSkillEffectTimerSet()
					.removeSkillEffect(L1SkillId.STATUS_MENT);
			pc.sendPackets(new S_SystemMessage("오토루팅 멘트를 켭니다."));

		} else {
			pc.sendPackets(new S_SystemMessage(cmd + " [켬,끔] 라고 입력해 주세요. "));
		}
	}
	
	private void clanbuff(L1PcInstance pc) {
		   String clanName = pc.getClanname();
		   L1Clan clan = L1World.getInstance().getClan(clanName);
		   int[] allBuffSkill = { 43, 48, 79, 26,42};
		   if (pc.isDead()){ 
		    return;
		   }
		   long curtime = System.currentTimeMillis() / 1000;
		   if (pc.getQuizTime() + 20 > curtime) {
		    pc.sendPackets(new S_SystemMessage("20초간의 지연시간이 필요합니다."));
		    return;
		   }
		   if (pc.getClanid() != 0 && clan.getOnlineClanMember().length >= 8) {
		    try {
		     L1SkillUse l1skilluse = new L1SkillUse();
		     for (int i = 0; i < allBuffSkill.length; i++) {
		      l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(),
		        pc.getX(), pc.getY(), null, 0,
		        L1SkillUse.TYPE_GMBUFF);
		      pc.setQuizTime(curtime);
		     }
		    } catch (Exception e) {
		    }
		   } else {
		    pc.sendPackets(new S_SystemMessage("혈맹이 없거나 혈맹원 (8명)미만인 상태에서는  버프를 받을수 없습니다."));
		   }
		  }

	public void autoroot(L1PcInstance pc, String cmd, String param) {
		if (param.equalsIgnoreCase("끔")) { // 오토루팅 켬끔 명령어
			pc.getSkillEffectTimerSet().setSkillEffect(
					L1SkillId.STATUS_AUTOROOT, 0);
			pc.sendPackets(new S_SystemMessage("오토루팅 을 해제합니다. "));

		} else if (param.equalsIgnoreCase("켬")) { // 오토루팅 켬끔 명령어
			pc.getSkillEffectTimerSet().removeSkillEffect(
					L1SkillId.STATUS_AUTOROOT);
			pc.sendPackets(new S_SystemMessage("오토루팅 을 활성화합니다. "));

		} else { // 오토루팅 켬끔 명령어
			pc.sendPackets(new S_SystemMessage(cmd + " [켬,끔] 라고 입력해 주세요. "));
		}
	}

	private void adenaAdd(L1PcInstance pc) {
		try {
			// /////////////////////// 타임/////////////////////////////////
			long curtime = System.currentTimeMillis() / 1000;
			if (pc.getQuizTime() + 20 > curtime) {
				pc.sendPackets(new S_SystemMessage("20초간의 지연시간이 필요합니다."));
				return;
			}
			// /////////////////////// 타임/////////////////////////////////
			if (pc.getMapId() != 4) {
				pc.sendPackets(new S_SystemMessage("아덴 대륙에서만  사용할 수 있습니다."));
				return;
			}
			if (pc.getMapId() == 5302 || pc.getMapId() == 5490) {
				pc.sendPackets(new S_SystemMessage("이곳에서는 사용할 수 없습니다."));
				return;
			}
			if (pc.isPinkName() || pc.isParalyzed() || pc.isSleeped()) {
				pc.sendPackets(new S_SystemMessage("보라중 마비중 잠수중에는 사용할 수 없습니다."));
				return;
			}
			if (pc.getMapId() == 350) {
				pc.sendPackets(new S_SystemMessage("이곳에서는 사용할 수 없습니다."));
				return;
			}
			L1ItemInstance[] items = pc.getInventory().findItemsId(40308);
			int count = 0;

			for (L1ItemInstance item : items) {
				count += pc.getInventory().removeItem(item, item.getCount());
			}
			pc.getInventory().storeItem(40308, count, "추가:합치기");
			pc.sendPackets(new S_SystemMessage("총 " + count + "아데나를 합쳤습니다."));
			pc.setQuizTime(curtime);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".아덴합치기 로 입력해주세요."));
		}
	}
	/*private void BloodProtect(L1PcInstance pc){ //혈맹보호 유저명령어부분 
		 try{
		  if(pc.getProtect() == true){
		   pc.sendPackets(new S_SystemMessage("혈맹보호 상태를 해제합니다."));
		   pc.setProtect(false);
		  }else{
		   pc.sendPackets(new S_SystemMessage("혈맹보호 상태를 활성화합니다."));
		   pc.setProtect(true);
		  }
		 }catch(Exception e){
		  pc.sendPackets(new S_SystemMessage("혈맹보호 명령어 에러."));
		 }
		}*/
	private void serchdroplist2(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String nameid = tok.nextToken();

			int npcid = 0;
			try {
				npcid = Integer.parseInt(nameid);
			} catch (NumberFormatException e) {
				npcid = NpcTable.getInstance().findNpcIdByName(nameid);
				if (npcid == 0) {
					pc.sendPackets(new S_SystemMessage("해당 몬스터가 발견되지 않았습니다."));
					return;
				}
			}
			pc.sendPackets(new S_Serchdrop2(npcid));
		} catch (Exception e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			pc.sendPackets(new S_SystemMessage(".몹이름 [NPCID]를 입력해 주세요."));
			pc.sendPackets(new S_SystemMessage("몬스터 ID를 공백없이 정확히 입력해야 합니다."));
			pc.sendPackets(new S_SystemMessage("ex) .몹이름 데스나이트 -- > 검색 O"));
			pc.sendPackets(new S_SystemMessage("ex) .몹이름 데스 나이트 -- > 검색 X"));
		}
	}
	
	private void buff(L1PcInstance pc) {
		
		int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, ADVANCE_SPIRIT };
		if (pc.isDead())
			return;

		long curtime = System.currentTimeMillis() / 1000;
		if (pc.getQuizTime() + 20 > curtime) {
			long sec = (pc.getQuizTime() + 20) - curtime;
			pc.sendPackets(new S_SystemMessage(sec + "초간의 지연시간이 필요합니다."));
			return;
		}
		if (pc.getLevel() <= 65) {
			try {
				L1SkillUse l1skilluse = new L1SkillUse();
				for (int i = 0; i < allBuffSkill.length; i++) {
					l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
					pc.setQuizTime(curtime);
				}
			} catch (Exception e) {
			}
		} else {
		//	pc.sendPackets(new S_SystemMessage("60레벨 이후는 버프를 받을수 없습니다."));
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"Lv:65 이상은 버프를 받을수 없습니다.", Opcodes.S_OPCODE_MSG, 15); 
			pc.sendPackets(s_chatpacket);
		}
		pc.setQuizTime(curtime);
	}
	
	private void MarkView(L1PcInstance pc, String param) {//9월26일혈망마크띄우기
		long curtime = System.currentTimeMillis() / 1000;
		if (pc.getQuizTime() + 20 > curtime) {
			long sec = (pc.getQuizTime() + 20) - curtime;
			pc.sendPackets(new S_SystemMessage(sec + "초간의 지연시간이 필요합니다."));
			return;
		}
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String Yn = tok.nextToken();

			String clan_name = pc.getClanname();
			L1Clan clan = L1World.getInstance().getClan(clan_name);
			int castle_id;

			if (Yn.equals("켬")) {
				if (clan == null) {
					return;
				}
				if (pc.getId() != clan.getLeaderId() && pc.getClanRank() < 3){//!(pc.getClanRank() == CLAN_RANK_GUARDIN)) { // 상대가 혈맹주 이외	
					pc.sendPackets(new S_ServerMessage(92, pc.getName())); // \f1%0은 프린스나 프린세스가 아닙니다.
					return;
				}

				for (castle_id = 1; castle_id <= 8; castle_id++) {
					if (WarTimeController.getInstance().isNowWar(castle_id)) {
						pc.sendPackets(new S_SystemMessage("공성전 중에는 혈맹전쟁을 선포 할 수 없습니다."));
						return;
					}
				}

				for (L1War war : L1World.getInstance().getWarList()) {
					if (war.CheckClanInSameWar(clan_name, clan_name) == true) {
						return;
					}
				}
				L1War war = new L1War();

				war.handleCommands(2, clan_name, clan_name); // 모의전 개시
				L1World.getInstance().broadcastServerMessage("\\fY혈맹마크 띄우기 완료");
			} else if (Yn.equals("끔")) {
				for (L1War war : L1World.getInstance().getWarList()) {
					if (war.CheckClanInSameWar(clan_name, clan_name) == true) {
						war.CeaseWar(clan_name, clan_name);
						L1World.getInstance().broadcastServerMessage("\\fY혈맹마크 띄우기 종료");
						return;
					}
				}
			}
			pc.setQuizTime(curtime);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".혈맹마크 [켬,끔]"));
		}
	}

	// 새로운드랍끝
	/** 랭킹 확인 * */
	private void rank1(L1PcInstance pc, String param) {
		Connection con = null;
		int q = 0;
		int i = 0;
		int j = 0;
		String type = null;
		String type1 = null;
		String Clan = null;
		String poly = null;

		try {
			StringTokenizer stringtokenizer = new StringTokenizer(param);
			String para1 = stringtokenizer.nextToken();
			L1PcInstance TargetPc = L1World.getInstance().getPlayer(para1);
			int objid = TargetPc.getId();
			int ClanID = TargetPc.getClanid();
			if (param != null) {

				switch (pc.getType()) {
				case 0:
					type = "군주";
					break;
				case 1:
					type = "기사";
					break;
				case 2:
					type = "요정";
					break;
				case 3:
					type = "마법사";
					break;
				case 4:
					type = "다크엘프";
					break;
				case 5:
					type = "용기사";
					break;
				case 6:
					type = "환술사";
					break;
				}

				if (!(pc.getInventory().checkItem(40308, 30000))) {
					pc
							.sendPackets(new S_SystemMessage(
									"아데나가 부족하여 사용할 수 없습니다."));
					return;
				}
				pc.getInventory().consumeItem(40308, 30000);

				con = L1DatabaseFactory.getInstance().getConnection();
				Statement pstm = con.createStatement();
				ResultSet rs = pstm
						.executeQuery("SELECT objid FROM characters WHERE AccessLevel = 0 order by Exp desc");

				if (TargetPc.getType() == 0) {
					Statement pstm2 = con.createStatement();
					ResultSet rs2 = pstm2
							.executeQuery("SELECT objid FROM characters WHERE type = 0 and AccessLevel = 0 order by Exp desc");
					while (rs2.next()) {
						j++;
						if (objid == rs2.getInt(1))
							break;
					}
					rs2.close();
					pstm2.close();
				} else if (TargetPc.getType() == 1) {
					Statement pstm2 = con.createStatement();
					ResultSet rs2 = pstm2
							.executeQuery("SELECT objid FROM characters WHERE type = 1 and AccessLevel = 0 order by Exp desc");
					while (rs2.next()) {
						j++;
						if (objid == rs2.getInt(1))
							break;
					}
					rs2.close();
					pstm2.close();
				} else if (TargetPc.getType() == 2) {
					Statement pstm2 = con.createStatement();
					ResultSet rs2 = pstm2
							.executeQuery("SELECT objid FROM characters WHERE type = 2 and AccessLevel = 0 order by Exp desc");
					while (rs2.next()) {
						j++;
						if (objid == rs2.getInt(1))
							break;
					}
					rs2.close();
					pstm2.close();
				} else if (TargetPc.getType() == 3) {
					Statement pstm2 = con.createStatement();
					ResultSet rs2 = pstm2
							.executeQuery("SELECT objid FROM characters WHERE type = 3 and AccessLevel = 0 order by Exp desc");
					while (rs2.next()) {
						j++;
						if (objid == rs2.getInt(1))
							break;
					}
					rs2.close();
					pstm2.close();
				} else if (TargetPc.getType() == 4) {
					Statement pstm2 = con.createStatement();
					ResultSet rs2 = pstm2
							.executeQuery("SELECT objid FROM characters WHERE type = 4 and AccessLevel = 0 order by Exp desc");
					while (rs2.next()) {
						j++;
						if (objid == rs2.getInt(1))
							break;
					}
					rs2.close();
					pstm2.close();
				} else if (TargetPc.getType() == 5) {
					Statement pstm2 = con.createStatement();
					ResultSet rs2 = pstm2
							.executeQuery("SELECT objid FROM characters WHERE type = 5 and AccessLevel = 0 order by Exp desc");
					while (rs2.next()) {
						j++;
						if (objid == rs2.getInt(1))
							break;
					}
					rs2.close();
					pstm2.close();
				} else if (TargetPc.getType() == 6) {
					Statement pstm2 = con.createStatement();
					ResultSet rs2 = pstm2
							.executeQuery("SELECT objid FROM characters WHERE type = 6 and AccessLevel = 0 order by Exp desc");
					while (rs2.next()) {
						j++;
						if (objid == rs2.getInt(1))
							break;
					}
					rs2.close();
					pstm2.close();
				}

				while (rs.next()) {

					q++;
					if (objid == rs.getInt(1))
						break;
				}

				if (TargetPc.getType() == 0) {
					type1 = "군주";
				} else if (TargetPc.getType() == 1) {
					type1 = "기사";
				} else if (TargetPc.getType() == 2) {
					type1 = "요정";
				} else if (TargetPc.getType() == 3) {
					type1 = "마법사";
				} else if (TargetPc.getType() == 4) {
					type1 = "다크엘프";
				} else if (TargetPc.getType() == 5) {
					type1 = "용기사";
				} else if (TargetPc.getType() == 6) {
					type1 = "환술사";
				}

				if (ClanID != 0) {
					Clan = TargetPc.getClanname();
				} else {
					Clan = "-";
				}
				if (TargetPc.getLevel() <= 39) {
					poly = "해골";
				} else if (TargetPc.getLevel() >= 40
						&& TargetPc.getLevel() <= 44) {
					poly = "서큐버스";
				} else if (TargetPc.getLevel() >= 45
						&& TargetPc.getLevel() <= 49) {
					poly = "카스파";
				} else if (TargetPc.getLevel() == 50) {
					poly = "바포메트";
				} else if (TargetPc.getLevel() == 51) {
					poly = "커츠";
				} else if (TargetPc.getLevel() >= 52
						&& TargetPc.getLevel() <= 54) {
					poly = "데스나이트";
				} else if (TargetPc.getLevel() >= 55
						&& TargetPc.getLevel() <= 59) {
					poly = "다크나이트";
				} else if (TargetPc.getLevel() >= 60
						&& TargetPc.getLevel() <= 64) {
					poly = "실버나이트";
				} else if (TargetPc.getLevel() >= 65
						&& TargetPc.getLevel() <= 69) {
					poly = "소드마스터";
				} else if (TargetPc.getLevel() >= 70
						&& TargetPc.getLevel() <= 74) {
					poly = "아크나이트";
				} else if (TargetPc.getLevel() >= 75
						&& TargetPc.getLevel() <= 79) {
					poly = "켄라우헬";
				} else if (TargetPc.getLevel() >= 80
						&& TargetPc.getLevel() <= 100) {
					poly = "군터";
				}

				pc.sendPackets(new S_SystemMessage("[" + TargetPc.getName()
						+ "] 서버순위:" + q + "위   클래스순위:" + j + "위 \n변신레벨:" + poly
						+ "   클래스:" + type1 + "   혈맹:" + Clan + ""));
				rs.close();
				pstm.close();
				con.close();
			} else if (param == pc.getName()) {
				pc.sendPackets(new S_SystemMessage("케릭터가 월드내에 존재 하지 않습니다"));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".랭킹검색 [케릭터명]으로 입력해 주십시요."));
		}

	}

	private void rank(L1PcInstance pc) {
		Connection con = null;
		int q = 0;
		int i = 0;
		int j = 0;
		String type = null;
		String type1 = null;
		String Clan = null;
		String poly = null;
		try {
			int objid = pc.getId();
			int ClanID = pc.getClanid();

			switch (pc.getType()) {
			case 0:
				type = "군주";
				break;
			case 1:
				type = "기사";
				break;
			case 2:
				type = "요정";
				break;
			case 3:
				type = "마법사";
				break;
			case 4:
				type = "다크엘프";
				break;
			case 5:
				type = "용기사";
				break;
			case 6:
				type = "환술사";
				break;
			}
			con = L1DatabaseFactory.getInstance().getConnection();
			Statement pstm = con.createStatement();
			ResultSet rs = pstm
					.executeQuery("SELECT objid FROM characters WHERE AccessLevel = 0 order by Exp desc");

			if (pc.getType() == 0) {
				Statement pstm2 = con.createStatement();
				ResultSet rs2 = pstm2
						.executeQuery("SELECT objid FROM characters WHERE type = 0 and AccessLevel = 0 order by Exp desc");
				while (rs2.next()) {
					j++;
					if (objid == rs2.getInt(1))
						break;
				}
				rs2.close();
				pstm2.close();
			} else if (pc.getType() == 1) {
				Statement pstm2 = con.createStatement();
				ResultSet rs2 = pstm2
						.executeQuery("SELECT objid FROM characters WHERE type = 1 and AccessLevel = 0 order by Exp desc");
				while (rs2.next()) {
					j++;
					if (objid == rs2.getInt(1))
						break;
				}
				rs2.close();
				pstm2.close();
			} else if (pc.getType() == 2) {
				Statement pstm2 = con.createStatement();
				ResultSet rs2 = pstm2
						.executeQuery("SELECT objid FROM characters WHERE type = 2 and AccessLevel = 0 order by Exp desc");
				while (rs2.next()) {
					j++;
					if (objid == rs2.getInt(1))
						break;
				}
				rs2.close();
				pstm2.close();
			} else if (pc.getType() == 3) {
				Statement pstm2 = con.createStatement();
				ResultSet rs2 = pstm2
						.executeQuery("SELECT objid FROM characters WHERE type = 3 and AccessLevel = 0 order by Exp desc");
				while (rs2.next()) {
					j++;
					if (objid == rs2.getInt(1))
						break;
				}
				rs2.close();
				pstm2.close();
			} else if (pc.getType() == 4) {
				Statement pstm2 = con.createStatement();
				ResultSet rs2 = pstm2
						.executeQuery("SELECT objid FROM characters WHERE type = 4 and AccessLevel = 0 order by Exp desc");
				while (rs2.next()) {
					j++;
					if (objid == rs2.getInt(1))
						break;
				}
				rs2.close();
				pstm2.close();
			} else if (pc.getType() == 5) {
				Statement pstm2 = con.createStatement();
				ResultSet rs2 = pstm2
						.executeQuery("SELECT objid FROM characters WHERE type = 5 and AccessLevel = 0 order by Exp desc");
				while (rs2.next()) {
					j++;
					if (objid == rs2.getInt(1))
						break;
				}
				rs2.close();
				pstm2.close();
			} else if (pc.getType() == 6) {
				Statement pstm2 = con.createStatement();
				ResultSet rs2 = pstm2
						.executeQuery("SELECT objid FROM characters WHERE type = 6 and AccessLevel = 0 order by Exp desc");
				while (rs2.next()) {
					j++;
					if (objid == rs2.getInt(1))
						break;
				}
				rs2.close();
				pstm2.close();
			}

			while (rs.next()) {

				q++;
				if (objid == rs.getInt(1))
					break;
			}

			if (pc.getType() == 0) {
				type1 = "군주";
			} else if (pc.getType() == 1) {
				type1 = "기사";
			} else if (pc.getType() == 2) {
				type1 = "요정";
			} else if (pc.getType() == 3) {
				type1 = "마법사";
			} else if (pc.getType() == 4) {
				type1 = "다크엘프";
			} else if (pc.getType() == 5) {
				type1 = "용기사";
			} else if (pc.getType() == 6) {
				type1 = "환술사";
			}

			if (ClanID != 0) {
				Clan = pc.getClanname();
			} else {
				Clan = "-";
			}
			if (pc.getLevel() <= 39) {
				poly = "해골";
			} else if (pc.getLevel() >= 40 && pc.getLevel() <= 44) {
				poly = "서큐버스";
			} else if (pc.getLevel() >= 45 && pc.getLevel() <= 49) {
				poly = "카스파";
			} else if (pc.getLevel() == 50) {
				poly = "바포메트";
			} else if (pc.getLevel() == 51) {
				poly = "커츠";
			} else if (pc.getLevel() >= 52 && pc.getLevel() <= 54) {
				poly = "데스나이트";
			} else if (pc.getLevel() >= 55 && pc.getLevel() <= 59) {
				poly = "다크나이트";
			} else if (pc.getLevel() >= 60 && pc.getLevel() <= 64) {
				poly = "실버나이트";
			} else if (pc.getLevel() >= 65 && pc.getLevel() <= 69) {
				poly = "소드마스터";
			} else if (pc.getLevel() >= 70 && pc.getLevel() <= 74) {
				poly = "아크나이트";
			} else if (pc.getLevel() >= 75
					&& pc.getLevel() <= 79) {
				poly = "켄라우헬";
			} else if (pc.getLevel() >= 80
					&& pc.getLevel() <= 100) {
				poly = "군터";
			}
			pc.sendPackets(new S_SystemMessage("랭 킹 조 회 : 서버순위:" + q
					+ "위   클래스순위:" + j + "위 \n변신레벨:" + poly + "   클래스:" + type1
					+ "   혈맹:" + Clan + ""));
			rs.close();
			pstm.close();
			con.close();
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".내랭킹 으로 입력해주세요."));
		}

	}

	/** 랭킹 확인 * */
	private static boolean isDisitAlpha(String str) {
		boolean check = true;
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i)) // 숫자가 아니라면
					&& Character.isLetterOrDigit(str.charAt(i)) // 특수문자라면
					&& !Character.isUpperCase(str.charAt(i)) // 대문자가 아니라면
					&& !Character.isLowerCase(str.charAt(i))) { // 소문자가 아니라면
				check = false;
				break;
			}
		}
		return check;
	}
	 private void to_Change_Passwd(L1PcInstance pc, String passwd) {
			PreparedStatement statement = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			java.sql.Connection con = null;
		  	try {
				String login = null;
				String password = null;
				con = L1DatabaseFactory.getInstance().getConnection();
				password = passwd;
				statement = con.prepareStatement("select account_name from characters where char_name Like '" + pc.getName() + "'");
				rs = statement.executeQuery();

				while (rs.next()){
					login = rs.getString(1);
					pstm = con.prepareStatement("UPDATE accounts SET password=? WHERE login Like '" + login + "'");
					pstm.setString(1, password);
					pstm.execute();
					pc.sendPackets(new S_ChatPacket(pc,"암호변경정보 :(" + passwd + ")가 설정이 완료되었습니다.", Opcodes.S_OPCODE_NORMALCHAT, 2));
					pc.sendPackets(new S_SystemMessage("암호 변경이 성공적으로 완료되었습니다."));
				}
				login = null;
				password = null;
			}catch(Exception e){
				System.out.println("to_Change_Passwd() Error : "+e);
			}finally{
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(statement);
				SQLUtil.close(con);
			}
		 }
	private void changePassword(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String passwd = tok.nextToken();
			Account account = Account.load(pc.getAccountName()); // 추가
			if (account.getquize() != null) {
				pc.sendPackets(new S_SystemMessage("퀴즈를 삭제하지 않으면 변경할 수 없습니다."));
				return;
			} // 암호변경시 퀴즈가 설정되어 있지 않다면 바꿀 수 없도록.
			if (passwd.length() < 4) {
				pc.sendPackets(new S_SystemMessage("입력하신 암호의 자릿수가 너무 짧습니다."));
				pc.sendPackets(new S_SystemMessage("최소 4자 이상 입력해 주십시오."));
				return;
			}

			if (passwd.length() > 12) {
				pc.sendPackets(new S_SystemMessage("입력하신 암호의 자릿수가 너무 깁니다."));
				pc.sendPackets(new S_SystemMessage("최대 12자 이하로 입력해 주십시오."));
				return;
			}

			if (isDisitAlpha(passwd) == false) {
				pc.sendPackets(new S_SystemMessage("암호에 허용되지 않는 문자가 포함 되어 있습니다."));
				return;
			}

			to_Change_Passwd(pc, passwd);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".암호변경 [변경할 암호]를 입력 해주세요."));
		}
	}

	private void quize(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String quize = tok.nextToken();
			Account account = Account.load(pc.getAccountName());

			if (quize.length() < 4) {
				pc.sendPackets(new S_SystemMessage("최소 4자 이상 입력해 주십시오."));
				return;
			}

			if (quize.length() > 12) {
				pc.sendPackets(new S_SystemMessage("최대 12자 이하로 입력해 주십시오."));
				return;
			}
			if (isDisitAlpha(quize) == false) {
				pc.sendPackets(new S_SystemMessage("퀴즈에 허용되지 않는 문자가 포함되었습니다."));
				return;
			}

			if(account.getquize() != null){
				pc.sendPackets(new S_SystemMessage("이미 퀴즈가 설정되어 있습니다."));
				return;
			}
				account.setquize(quize);
				Account.updateQuize(account);
			pc.sendPackets(new S_SystemMessage("\\fT퀴즈암호 (" + quize + ") 가 설정되었습니다.\\fY[잊지마세요!]"));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".퀴즈설정 [설정하실퀴즈]를 입력해주세요."));
		}
	}
	private void quize1(L1PcInstance pc, String cmd) {
		try {
			StringTokenizer tok = new StringTokenizer(cmd);
			String quize2 = tok.nextToken();
			Account account = Account.load(pc.getAccountName());

			if (quize2.length() < 4) {
				pc.sendPackets(new S_SystemMessage("입력하신 퀴즈의 자릿수가 너무 짧습니다."));
				pc.sendPackets(new S_SystemMessage("최소 4자 이상 입력해 주십시오."));
				return;
			}

			if (quize2.length() > 12) {
				pc.sendPackets(new S_SystemMessage("입력하신 퀴즈의 자릿수가 너무 깁니다."));
				pc.sendPackets(new S_SystemMessage("최대 12자 이하로 입력해 주십시오."));
				return;
			}

			if (account.getquize() == null || account.getquize() == "") {
				pc.sendPackets(new S_SystemMessage("퀴즈가 설정되어 있지 않습니다."));
				return;
			}

			if (!quize2.equals(account.getquize())) {
				pc.sendPackets(new S_SystemMessage("설정된 퀴즈와 일치하지 않습니다."));
				return;
			}

			if (isDisitAlpha(quize2) == false) {
				pc.sendPackets(new S_SystemMessage("퀴즈에 허용되지 않는 문자가 포함되었습니다."));
				return;
			}
			account.setquize(null);
			Account.updateQuize(account);
			pc.sendPackets(new S_SystemMessage("퀴즈가 삭제되었습니다."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("사용 예).퀴즈삭제 암호(퀴즈)"));
		}
	}

	/** 좀비 키워드관련 명령어 * */
	private void gjtkd(L1PcInstance pc) {
		try {
			if (pc.isDead()) {
				pc.sendPackets(new S_SystemMessage("죽은 상태에선 실행 할수없습니다."));
				return;
			}
			LineageClient client = pc.getNetConnection();
			client.quitGame(pc);
			synchronized (pc) {
				LetsbeSave(pc);
				pc.stopHpRegenerationByDoll();
				pc.stopMpRegenerationByDoll();
				pc.getNearObjects().removeAllKnownObjects();
				pc.setOnlineStatus(0);
				client.setActiveChar(null);
				client.setloginStatus(1);
				client.CharReStart(true);
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.LOGOUT));//캐릭선택창으로
			pc.setNetConnection(null);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("...로 입력해주세요."));
		}
	}
	/** 계정 키워드, 패스워드 관련 명령어 * */
	private void LetsbeShop(L1PcInstance pc) {
		try {
			if (!pc.isPrivateShop()) {
				pc.sendPackets(new S_SystemMessage("먼저 개인상점을 시작하셔야 합니다."));
				return;
			}
			if (pc.isPinkName() || pc.isParalyzed() || pc.isSleeped()) {
				pc.sendPackets(new S_SystemMessage("보라중 마비중 잠수중에는 사용할 수 없습니다."));
				return;
			}
			if (pc.isDead()) {
				pc.sendPackets(new S_SystemMessage("죽은 상태에선 개인상점을 열수없습니다."));
				return;
			}
			for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
				if (target.getId() != pc.getId()
						&& target.getAccountName().toLowerCase().equals(
								pc.getAccountName().toLowerCase())
						&& target.isPrivateShop()) {
					pc.sendPackets(new S_SystemMessage("무인상점은 한개의 캐릭터만 가능합니다."));
					return;
				}
			}

			LineageClient client = pc.getNetConnection();
			client.quitGame(pc);
			synchronized (pc) {
				LetsbeSave(pc);
				pc.stopHpRegenerationByDoll();
				pc.stopMpRegenerationByDoll();
				pc.getNearObjects().removeAllKnownObjects();
				client.setActiveChar(null);
			}
			pc.sendPackets(new S_Disconnect());
			pc.setNetConnection(null);
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".클라종료 로 입력해주세요."));
		}
	}


	private void LetsbeSave(L1PcInstance pc) {
		try {
			pc.save();
			pc.saveInventory();
		} catch (Exception ex) {

			pc.saveInventory();
		}
	}
}
