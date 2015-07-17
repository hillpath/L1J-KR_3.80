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

import server.LineageClient;
import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1BuffNpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_TradeOK extends ClientBasePacket {

	private static final String C_TRADE_CANCEL = "[C] C_TradeOK";

	public C_TradeOK(byte abyte0[], LineageClient clientthread)
			throws Exception {
		super(abyte0);

		L1PcInstance player = clientthread.getActiveChar();
		if (player == null) {
			return;
		}
		L1Object trading_partner = L1World.getInstance().findObject(
				player.getTradeID());
		if (trading_partner != null) {
			if(trading_partner instanceof L1PcInstance){
				L1PcInstance tradepc = (L1PcInstance)trading_partner;
				if (player.getTradeID() == 0) { // 트레이드중이 아닌경우
					return;
				}
				player.setTradeOk(true);
				/**매입상점 */
				if (player.getTradeOk() && tradepc.isAutoshop()) {
					if (player.getTradeWindowInventory().getSize() == 0) {
						L1Trade trade = new L1Trade();
						trade.TradeCancel(player);
						S_ChatPacket s_chatpacket = new S_ChatPacket(tradepc, "물품을 올려 주세요. 다시 거래하실려면 칼질요~", Opcodes.S_OPCODE_NORMALCHAT, 0);			
						for (L1PcInstance listner : L1World.getInstance().getRecognizePlayer(tradepc)) {
							if (!listner.getExcludingList().contains(tradepc.getName())) {
								listner.sendPackets(s_chatpacket);
							}
						}
						return;
					}
					tradepc.setTradeOk(true);
				}
				/**매입상점*/
			if (player.getTradeOk() && tradepc.getTradeOk()){ // 모두 OK를 눌렀다
				if (tradepc.isChaTradeSlot()) {
					player.sendPackets(new S_SystemMessage("거래 대상에게 빈 캐릭터 슬롯이 없습니다."));
					tradepc.sendPackets(new S_SystemMessage("빈 캐릭터 슬롯이 없습니다. 캐릭터 슬롯을 확보하고 다시 시도해주시기 바랍니다."));	
					return;
				}else if (player.isChaTradeSlot()) {
					tradepc.sendPackets(new S_SystemMessage("거래 대상에게 빈 캐릭터 슬롯이 없습니다."));
					player.sendPackets(new S_SystemMessage("빈 캐릭터 슬롯이 없습니다. 캐릭터 슬롯을 확보하고 다시 시도해주시기 바랍니다."));	

					return;
				}
			
				// (180 - 16) 개미만이라면 트레이드 성립.
				// 본래는 겹치는 아이템(아데나등 )을 이미 가지고 있는 경우를 고려하지 않는 차면 안 된다.
				if (player.getInventory().getSize() < (180 - 16)
						&& tradepc.getInventory().getSize() < (180 - 16)){ // 서로의 아이템을 상대에게 건네준다			
					L1Trade trade = new L1Trade();
					trade.TradeOK(player);
				} else {// 서로의 아이템을 수중에 되돌린다
					player.sendPackets(new S_ServerMessage(263)); // \f1한사람의 캐릭터가 가지고 걸을 수 있는 아이템은 최대 180개까지입니다.
					tradepc.sendPackets(new S_ServerMessage(263)); // \f1한사람의 캐릭터가 가지고 걸을 수 있는 아이템은 최대 180개까지입니다.
					return;
			}
			}
					/**매입상점 */
					if (tradepc.isAutoshop()) {		
						tradepc.setTradeOk(false);
						tradepc.setTradeID(0);
						tradepc.getLight().turnOnOffLight();
						S_ChatPacket s_chatpacket = new S_ChatPacket(tradepc, "감사합니다. 담에 또 이용해 주세요.", Opcodes.S_OPCODE_NORMALCHAT, 0);			
						for (L1PcInstance listner : L1World.getInstance().getRecognizePlayer(tradepc)) {
							if (!listner.getExcludingList().contains(tradepc.getName())) {
								listner.sendPackets(s_chatpacket);
							}
						}
						player.setTradeOk(false);
						player.setTradeID(0);
						player.getLight().turnOnOffLight();
					}
			/**매입상점*/
			
		}else if(trading_partner instanceof L1BuffNpcInstance){
			L1BuffNpcInstance target = (L1BuffNpcInstance)trading_partner;
			player.setTradeOk(true);
			if (player.getTradeOk()) // 모두 OK를 눌렀다
			{
				// (180 - 16) 개미만이라면 트레이드 성립.
				// 본래는 겹치는 아이템(아데나등 )을 이미 가지고 있는 경우를 고려하지 않는 차면 안 된다.
				if (player.getInventory().getSize() < (180 - 16)
						&& target.getInventory().getSize() < (180 - 16)) {// 서로의 아이템을 상대에게 건네준다				
					L1Trade trade = new L1Trade();
					trade.TradeOK(player);
				} else {// 서로의 아이템을 수중에 되돌린다				
					player.sendPackets(new S_ServerMessage(263)); // \f1한사람의 캐릭터가 가지고 걸을 수 있는 아이템은 최대 180개까지입니다.
					return;
				}

		}	

	}
		}
	}

	@Override
	public String getType() {
		return C_TRADE_CANCEL;
	}

}
