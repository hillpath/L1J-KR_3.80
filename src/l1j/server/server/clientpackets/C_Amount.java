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
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;

import server.LineageClient;

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.AuctionBoardTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.npc.action.L1NpcAction;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1AuctionBoard;
import l1j.server.server.templates.L1House;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket, C_Amount

public class C_Amount extends ClientBasePacket {

	private static final String C_AMOUNT = "[C] C_Amount";

	public C_Amount(byte[] decrypt, LineageClient client) throws Exception {
		super(decrypt);
		int objectId = readD();
		int amount = readD();

		// 아지트 경매 게시판 버그 수정
		long _amount = amount;
		if (_amount < 0) {
			return;
		}

		@SuppressWarnings("unused")
		int c = readC();
		String s = readS();

		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}
		L1NpcInstance npc = (L1NpcInstance) L1World.getInstance().findObject(
				objectId);
		String s1 = "";
		String s2 = "";

		// 아지트 경매 게시판 버그 수정
		if (pc.getInventory().findItemId(40308).getCount() < _amount) {
			return;
		}
		if (npc == null) {
			return;
		}

		try {
			StringTokenizer stringtokenizer = new StringTokenizer(s);
			s1 = stringtokenizer.nextToken();
			s2 = stringtokenizer.nextToken();
		} catch (NoSuchElementException e) {
			s1 = "";
			s2 = "";
		}
		if (s1.equalsIgnoreCase("agapply")) { // 경매에 입찰했을 경우
			String pcName = pc.getName();
			AuctionBoardTable boardTable = new AuctionBoardTable();
			for (L1AuctionBoard board : boardTable.getAuctionBoardTableList()) {
				if (pcName.equalsIgnoreCase(board.getBidder())) {
					pc.sendPackets(new S_ServerMessage(523)); // 벌써 다른 집의 경매에
					// 참가하고 있습니다.
					return;
				}
			}
			int houseId = Integer.valueOf(s2);
			L1AuctionBoard board = boardTable.getAuctionBoardTable(houseId);
			if (board != null) {
				int nowPrice = board.getPrice();
				long _nowPrice = nowPrice;

				if (_nowPrice <= 0) {
					return;
				}
				if (pc.getInventory().findItemId(40308).getCount() < _nowPrice) {
					return;
				}

				int nowBidderId = board.getBidderId();
				if (pc.getInventory().consumeItem(L1ItemId.ADENA, amount)) {
					// 경매 게시판을 갱신
					board.setPrice(amount);
					board.setBidder(pcName);
					board.setBidderId(pc.getId());
					boardTable.updateAuctionBoard(board);
					if (nowBidderId != 0) {
						// 입찰자에게 아데나를 환불
						L1PcInstance bidPc = (L1PcInstance) L1World
								.getInstance().findObject(nowBidderId);
						if (bidPc != null) { // 온라인중
							bidPc.getInventory().storeItem(L1ItemId.ADENA,
									nowPrice);
							// 당신이 제시된 금액보다 좀 더 비싼 금액을 제시한 (분)편이 나타났기 때문에,
							// 유감스럽지만 입찰에 실패했습니다. %n
							// 당신이 경매에 맡긴%0아데나를 답례합니다. %n 감사합니다. %n%n
							bidPc.sendPackets(new S_ServerMessage(525, String
									.valueOf(nowPrice)));
						} else { // 오프 라인중
							L1ItemInstance item = ItemTable.getInstance()
									.createItem(L1ItemId.ADENA);
							item.setCount(nowPrice);
							CharactersItemStorage storage = CharactersItemStorage
									.create();
							storage.storeItem(nowBidderId, item);
						}
					}
				} else {
				//	pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 충분치 않습니다.
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아데나가 충분치 않습니다.", Opcodes.S_OPCODE_MSG, 20); 
					pc.sendPackets(s_chatpacket);
				}
			}
		} else if (s1.equalsIgnoreCase("agsell")) { // 가를 팔았을 경우
			int houseId = Integer.valueOf(s2);
			AuctionBoardTable boardTable = new AuctionBoardTable();
			L1AuctionBoard board = new L1AuctionBoard();
			if (board != null) {
				// 경매 게시판에 신규 기입
				board.setHouseId(houseId);
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				board.setHouseName(house.getHouseName());
				board.setHouseArea(house.getHouseArea());
				TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
				Calendar cal = Calendar.getInstance(tz);
				cal.add(Calendar.DATE, 5); // 5일 후
				cal.set(Calendar.MINUTE, 0); // 분 , 초는 잘라서 버림
				cal.set(Calendar.SECOND, 0);
				board.setDeadline(cal);
				board.setPrice(amount);
				board.setLocation(house.getLocation());
				board.setOldOwner(pc.getName());
				board.setOldOwnerId(pc.getId());
				board.setBidder("");
				board.setBidderId(0);
				boardTable.insertAuctionBoard(board);

				house.setOnSale(true); // 경매중으로 설정
				house.setPurchaseBasement(true); // 지하 아지트미구입으로 설정
				HouseTable.getInstance().updateHouse(house); // DB에 기입해
			}
		} else {
			L1NpcAction action = NpcActionTable.getInstance().get(s, pc, npc);
			if (action != null) {
				L1NpcHtml result = action.executeWithAmount(s, pc, npc, amount);
				if (result != null) {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), result));
				}
				return;
			}
		}
	}

	@Override
	public String getType() {
		return C_AMOUNT;
	}
}
