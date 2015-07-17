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
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class ResurrectionScroll extends L1ItemInstance {

	public ResurrectionScroll(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = useItem.getItemId();
			L1Character resobject = (L1Character) L1World.getInstance()
					.findObject(packet.readD());
			if (resobject != null) {
				if (resobject instanceof L1PcInstance) {
					L1PcInstance target = (L1PcInstance) resobject;
					if (pc.getId() == target.getId()) {
						return;
					}
					if (L1World.getInstance().getVisiblePlayer(target, 0)
							.size() > 0) {
						for (L1PcInstance visiblePc : L1World.getInstance()
								.getVisiblePlayer(target, 0)) {
							if (!visiblePc.isDead()) {
								// \f1그 자리소에 다른 사람이 서 있으므로 부활시킬 수가 없습니다.
								pc.sendPackets(new S_ServerMessage(592));
								return;
							}
						}
					}
					if (target.getCurrentHp() == 0 && target.isDead() == true) {
						if (pc.getMap().isUseResurrection()) {
							target.setTempID(pc.getId());
							if (itemId == 40089) {
								// 또 부활하고 싶습니까? (Y/N)
								target.sendPackets(new S_Message_YN(321, ""));
							} else if (itemId == 140089) {
								// 또 부활하고 싶습니까? (Y/N)
								target.sendPackets(new S_Message_YN(322, ""));
							}
						} else {
							return;
						}
					}
				} else if (resobject instanceof L1NpcInstance) {
					if (!(resobject instanceof L1TowerInstance)) {
						L1NpcInstance npc = (L1NpcInstance) resobject;
						if (npc.getNpcTemplate().isCantResurrect()) {
							pc.getInventory().removeItem(useItem, 1);
							return;
						}
						if (npc instanceof L1PetInstance
								&& L1World.getInstance().getVisiblePlayer(npc,
										0).size() > 0) {
							for (L1PcInstance visiblePc : L1World.getInstance()
									.getVisiblePlayer(npc, 0)) {
								if (!visiblePc.isDead()) {
									// \f1그 자리소에 다른 사람이 서 있으므로 부활시킬 수가 없습니다.
									pc.sendPackets(new S_ServerMessage(592));
									return;
								}
							}
						}
						if (npc.getCurrentHp() == 0 && npc.isDead()) {
							npc.resurrect(npc.getMaxHp() / 4);
							npc.setResurrect(true);
						}
					}
				}
			}
			pc.getInventory().removeItem(useItem, 1);
		}
	}
}
