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

package l1j.server.server.clientpackets;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.BoardTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1BoardInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import server.LineageClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_BoardWrite extends ClientBasePacket {

	private static final String C_BOARD_WRITE = "[C] C_BoardWrite";

	private static Logger _log = Logger.getLogger(C_BoardWrite.class.getName());



	public C_BoardWrite(byte decrypt[], LineageClient client) {
		super(decrypt);
		int id = readD();
		String date = currentTime();
		String title = readS();
		String content = readS();
		L1PcInstance pc = client.getActiveChar();
		L1Object tg = L1World.getInstance().findObject(id);
		if (title.length() > 10) {
			pc.sendPackets(new S_SystemMessage("게시판 제목 글자수가 초과하였습니다."));
			return;
		}
		String temp = "";
		String temp1 = "";
		StringTokenizer values = new StringTokenizer(content, " ");
		StringTokenizer values1 = new StringTokenizer(title, " ");
		while(values.hasMoreElements() || values1.hasMoreElements()){//공백제거
			temp += values.nextToken();
			temp1 += values1.nextToken();
		}
		String f[] = {"ㅅ ㅣㅂ ㅏ","시발","병신","새끼","영자야","미친년","미친놈","개새끼","씨발","젖까","붕신","엿까","씌발"
				,"어미","애비","애미","시1발","창녀","호로","븅신","쓰발","스벌","측근"	,"엿가튼","비리","www"
				,"엄마","kr","gg","오픈"};
		for(int i = 0 ; i < f.length ; i++){
			if (temp.indexOf(f[i]) > -1 || temp1.indexOf(f[i]) > -1){
				pc.sendPackets(new S_SystemMessage("사용할수 없는 언어가 포함되어있습니다."));
				return;
			}
		}
		if(tg instanceof L1BoardInstance){
			L1BoardInstance board = (L1BoardInstance)tg;
			if (board != null) {
				if (board.getNpcId() == 4500309 
						||board.getNpcId() == 4500300 
						||board.getNpcId() == 42000162 
						||board.getNpcId() == 42000163 
						||board.getNpcId() == 4200099 
						|| board.getNpcId() == 42000161) {
					if (pc.isGm()) {
					} else {
						pc.sendPackets(new S_SystemMessage("해당 게시판은 운영자만 사용가능합니다."));
						return;
					}
				}
				
				if (pc.getLevel() < 52 && board.getNpcId() != 4212014) {
					String chatText = "게시판 글쓰기는 레벨 52이상부터 가능하십니다.";
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_NORMALCHAT, 2);

					if (!pc.getExcludingList().contains(pc.getName())) {
						pc.sendPackets(s_chatpacket);
					}
					return;
				}
				
				switch (board.getNpcId()) {
				case 4212014: // 드래곤키 판매알림 게시판
					if (checkdragonkey(pc)) {
						L1ItemInstance dragonkey = pc.getInventory().findItemId(L1ItemId.DRAGON_KEY);

						BoardTable.getInstance().writeDragonKey(pc, dragonkey, date, board.getNpcId());
						pc.sendPackets(new S_ServerMessage(1567)); // 등록되었다
					} else {
						return;
					}
					break;

				case 4200099:	// 패키지 게시판
					if (pc.getAccessLevel() == Config.GMCODE) {
						BoardTable.getInstance().writeTopic1(pc, date, title, content, board.getNpcId());
					} else {
						String chatText = "운영자만 글 등록이 가능합니다.";
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_NORMALCHAT, 2);

						pc.sendPackets(s_chatpacket);
						return;
					}
					break;
					
					
				case 4500309:	// GM 공지 게시판
					if (pc.getAccessLevel() == Config.GMCODE) {
						BoardTable.getInstance().writeTopic(pc, date, title, content, board.getNpcId());
					} else {
						String chatText = "운영자만 글 등록이 가능합니다.";
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_NORMALCHAT, 2);

						pc.sendPackets(s_chatpacket);
						return;
					}
					break;
				
				
				case 42000201:	// 건의 사항^건의 게시판
					pc.getInventory().consumeItem(L1ItemId.ADENA, 300);
					BoardTable.getInstance().writeTopicUser(pc, date,title, content);
					break;
				default:
					BoardTable.getInstance().writeTopic2(pc, date, title, content, board.getNpcId());
				}
			}
		}
	}

				
	private static String currentTime() {
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(tz);
		int year = cal.get(Calendar.YEAR) - 2000;
		String year2;
		if (year < 10) {
			year2 = "0" + year;
		} else {
			year2 = Integer.toString(year);
		}
		int Month = cal.get(Calendar.MONTH) + 1;
		String Month2 = null;
		if (Month < 10) {
			Month2 = "0" + Month;
		} else {
			Month2 = Integer.toString(Month);
		}
		int date = cal.get(Calendar.DATE);
		String date2 = null;
		if (date < 10) {
			date2 = "0" + date;
		} else {
			date2 = Integer.toString(date);
		}
		return year2 + "/" + Month2 + "/" + date2;
	}

	/**
	 * 드래곤키를 등록할 조건을 판단한다.
	 * 
	 * @param pc
	 * @return
	 */
	private boolean checkdragonkey(L1PcInstance pc) {
		if (pc.getInventory().checkItem(L1ItemId.DRAGON_KEY)) {
			if (BoardTable.getInstance().checkExistName(pc.getName(), 4212014)) {
				pc.sendPackets(new S_ServerMessage(1568));// 이미 등록되어 있어
				return false;
			} else {
				return true;
			}
		} else {
			pc.sendPackets(new S_ServerMessage(1566));// 드래곤 키 있어야 해
			return false;
		}
	}

	@Override
	public String getType() {
		return C_BOARD_WRITE;
	}

}
