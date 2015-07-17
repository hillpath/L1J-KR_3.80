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

import java.sql.Timestamp;
import java.util.Calendar;

import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class TreasureBox extends L1ItemInstance {

	public TreasureBox(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = useItem.getItemId();
			// 재사용 체크
			boolean isDelayEffect = false;
			if (useItem.getItem().getType2() == 0) {
				int delayEffect = ((L1EtcItem) useItem.getItem())
						.get_delayEffect();
				if (delayEffect > 0) {
					isDelayEffect = true;
					Timestamp lastUsed = useItem.getLastUsed();
					if (lastUsed != null) {
						Calendar cal = Calendar.getInstance();
						if ((cal.getTimeInMillis() - lastUsed.getTime()) / 1000 <= delayEffect) {
							long sec = delayEffect - (( cal.getTimeInMillis() - lastUsed.getTime()) / 1000);
							long value = sec;
							if(value > 60){
								value /= 60;
								pc.sendPackets(new S_ServerMessage(1139, ""+value));
							}else{
								pc.sendPackets(new S_SystemMessage(value+" 초 동안은 사용할 수 없습니다."));
							}
							return;
						}
					}
				}
			}
			L1TreasureBox box = L1TreasureBox.get(itemId);
			if (box != null) {
				if (box.open(pc)) {
					L1EtcItem temp = (L1EtcItem) useItem.getItem();
					if (temp.get_delayEffect() > 0) {
						isDelayEffect = true;
					} else {
						pc.getInventory().removeItem(useItem.getId(), 1);
					}
				}
			}
			if (isDelayEffect) {
				if (useItem.getChargeCount() > 0) {
					int chargeCount = useItem.getChargeCount();
					Timestamp ts = new Timestamp(System.currentTimeMillis());
					useItem.setChargeCount(useItem.getChargeCount() - 1);
				     if(useItem.getItemId() == 46115){//마빈의주머니
				      if(chargeCount <=1){
				       pc.getInventory().storeItem(46118, 1); // 불완전한 마법 구슬
				      } else {  
				       pc.getInventory().storeItem(46116, 1); // 불완전한 마법 구슬 조각
				      }
				     }
					if (chargeCount <= 1) {
						pc.getInventory().removeItem(useItem, 1);
					}
					useItem.setLastUsed(ts);
					pc.getInventory().updateItem(useItem,
							L1PcInventory.COL_CHARGE_COUNT);
					pc.getInventory().saveItem(useItem,
							L1PcInventory.COL_CHARGE_COUNT);
				} else {
					Timestamp ts = new Timestamp(System.currentTimeMillis());
					useItem.setLastUsed(ts);
					pc.getInventory().updateItem(useItem,
							L1PcInventory.COL_DELAY_EFFECT);
					pc.getInventory().saveItem(useItem,
							L1PcInventory.COL_DELAY_EFFECT);
				}
			}
		}
	}
}
