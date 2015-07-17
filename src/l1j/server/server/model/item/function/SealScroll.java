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

package l1j.server.server.model.item.function;

import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ItemColor;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class SealScroll extends L1ItemInstance {
	//private static Random _random = new Random(System.nanoTime());

	public SealScroll(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = this.getItemId();
			L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(
					packet.readD());
			if (itemId == 401261) { // 미확인주문서
				if (l1iteminstance1.getBless() >= 128) { // 봉인아이템일경우 미확인불가능하게
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}

				l1iteminstance1.setIdentified(false);
				pc.getInventory().updateItem(l1iteminstance1,
						L1PcInventory.COL_IS_ID);
				pc.sendPackets(new S_ItemColor(l1iteminstance1, 3));
				pc.sendPackets(new S_SystemMessage(l1iteminstance1.getLogName() + "에 어두운 그림자가 스며듭니다."));
				pc.getInventory().removeItem(useItem, 1);
			} else if (itemId == 401262) { // 축복받은 주문서

				if (l1iteminstance1 == null|| l1iteminstance1.getItem().getType2() == 0) { // 무기와
																		// 방어구만
					//pc.sendPackets(new S_ServerMessage(79)); // 아무것도 일어나지
																// 않았습니다.
					pc.sendPackets(new S_SystemMessage(
					"무기와 방어구에만 사용할 수 있습니다."));
					return;
				}
				if (l1iteminstance1.getBless() >= 128 // 봉인템 일경우
						|| l1iteminstance1.getBless() == 0) { // 이미축일경우
					pc.sendPackets(new S_ServerMessage(79)); // 아무것도 일어나지
																// 않았습니다.
					return;
				}
			//	int ran = _random.nextInt(99) + 1;
			//	if (ran < 15) {// 10프로 확률로 축복인첸에 성공한다.
					l1iteminstance1.setBless(0);
					pc.getInventory().updateItem(l1iteminstance1,
							L1PcInventory.COL_BLESS);
					pc.getInventory().saveItem(l1iteminstance1,
							L1PcInventory.COL_BLESS);
					pc.getInventory().removeItem(useItem, 1);
					 pc.sendPackets(new S_SkillSound(pc.getId(), 9268));
					pc.sendPackets(new S_SystemMessage(l1iteminstance1.getLogName() + "에 축복의 기운이 스며듭니다."));
			//	} else {
					//pc
						//	.sendPackets(new S_SystemMessage(
							//		"축복의 기운이 스며들지 못하였습니다."));
					//pc.getInventory().removeItem(useItem, 1);
			//	}
			}
			if (itemId == 50020) { // 봉인줌서
				if(l1iteminstance1.getBless() >= 0 && l1iteminstance1.getBless() <= 3
						&& (l1iteminstance1.getItem().getType2() == 1 
						 || l1iteminstance1.getItem().getType2() == 2
						 || l1iteminstance1.getItem().getType2() == 0 && 
						 l1iteminstance1.getItem().getType() == 17)){
					/*if (!pc.isGm()) {
					pc.sendPackets(new S_SystemMessage("전투 서버에서는 봉인이 불가능 합니다."));
					return;
				    }*/
					int Bless = 0;
					switch (l1iteminstance1.getBless()) {
					case 0: Bless = 128; break; //축
					case 1: Bless = 129; break; //보통
					case 2: Bless = 130; break; //저주
					case 3: Bless = 131; break; //미확인
					}
					l1iteminstance1.setBless(Bless);
					pc.getInventory().updateItem(l1iteminstance1,
							L1PcInventory.COL_BLESS);
					pc.getInventory().saveItem(l1iteminstance1,
							L1PcInventory.COL_BLESS);
					pc.getInventory().removeItem(useItem, 1);
				} else
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
				// 않았습니다.
			} else if (itemId == 50021) { // 봉인해제줌서
				if (l1iteminstance1.getBless() >= 128
						&& l1iteminstance1.getBless() <= 131) {
					/*if (!pc.isGm()) {
						pc.sendPackets(new S_SystemMessage("전투 서버에서는 봉인해제가 불가능 합니다."));
						return;
					    }*/
					//Account account = Account.load(pc.getAccountName()); // 추가
					//if (account.getquize() != null) {
						//pc.sendPackets(new S_SystemMessage("퀴즈를 삭제하지 않으면 해제 할수없습니다."));
						//return;
					//} // 퀴즈가 설정되어 있지 않다면 바꿀 수 없도록.
					int Bless = 0;
					switch (l1iteminstance1.getBless()) {
					case 128: Bless = 0; break;
					case 129: Bless = 1; break;
					case 130: Bless = 2; break;
					case 131: Bless = 3; break;
					}
					l1iteminstance1.setBless(Bless);
					pc.getInventory().updateItem(l1iteminstance1,
							L1PcInventory.COL_BLESS);
					pc.getInventory().saveItem(l1iteminstance1,
							L1PcInventory.COL_BLESS);
					pc.getInventory().removeItem(useItem, 1);
				} else
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지
				// 않았습니다.
			}
		}
	}
}
