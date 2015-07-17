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
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class Weapon extends L1ItemInstance{
	
	public Weapon(L1Item item){
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet){
		if(cha instanceof L1PcInstance){
			L1PcInstance pc = (L1PcInstance)cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = this.getItemId();
			/** 인던 변신 안되도록 */
			if (pc.getMapId() == 5302|| pc.getMapId() == 5083|| pc.getMapId() == 5153|| pc.getMapId() == 5490) { // 배틀/인던/낚시터
				if (itemId == 11011 || itemId == 11012 || itemId == 11013) {
				pc.sendPackets(new S_ServerMessage(1170));
				return;
			}
			}
			/** 인던 변신 안되도록 */
			 if (useItem.getItem().getType2() == 1) {
					int min = useItem.getItem().getMinLevel();
					int max = useItem.getItem().getMaxLevel();
					if (min != 0 && min > pc.getLevel()) {
						// 이 아이템은%0레벨 이상이 되지 않으면 사용할 수 없습니다.
						pc.sendPackets(new S_ServerMessage(318, String.valueOf(min)));
					} else if (max != 0 && max < pc.getLevel()) {
						// 이 아이템은%d레벨 이하만 사용할 수 있습니다.
						// S_ServerMessage에서는 인수가 표시되지 않는다
						if (max < 50) { 
							pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_LEVEL_OVER, max));
						} else {
							pc.sendPackets(new S_SystemMessage("이 아이템은" + max + "레벨 이하만 사용할 수 있습니다. "));
						}
					} else {
						if (pc.isGm()){
							UseWeapon(pc, useItem);
						} else if (pc.isCrown() && useItem.getItem().isUseRoyal()
								|| pc.isKnight() && useItem.getItem().isUseKnight()
								|| pc.isElf() && useItem.getItem().isUseElf()
								|| pc.isWizard() && useItem.getItem().isUseMage()
								|| pc.isDarkelf() && useItem.getItem().isUseDarkelf()
								|| pc.isDragonknight() && useItem.getItem().isUseDragonKnight()
								|| pc.isIllusionist() && useItem.getItem().isUseBlackwizard()) {
							UseWeapon(pc, useItem);
						} else {
							// \f1당신의 클래스에서는 이 아이템은 사용할 수 없습니다.
							pc.sendPackets(new S_ServerMessage(264));
						}
					}
			 }
		}
	}
	
	private void UseWeapon(L1PcInstance activeChar, L1ItemInstance weapon) {
		L1PcInventory pcInventory = activeChar.getInventory();		
		if (activeChar.getWeapon() == null
				|| !activeChar.getWeapon().equals(weapon)) { // 지정된 무기가 장비 하고
																// 있는 무기와 다른 경우,
																// 장비 할 수 있을까 확인
			int weapon_type = weapon.getItem().getType();
			int polyid = activeChar.getGfxId().getTempCharGfx();

			if (!L1PolyMorph.isEquipableWeapon(polyid, weapon_type)) { // 그
				activeChar.sendPackets(new S_ServerMessage(2055,weapon.getName()));													// 변신에서는
																		// 장비 불가
				return;
			}		

			if (weapon.getItem().isTwohandedWeapon()
					&& pcInventory.getGarderEquipped(2, 7, 13) >= 1) { // 양손
																		// 무기의
																		// 경우,
																		// 쉴드(shield)
																		// 장비의
																		// 확인
				activeChar.sendPackets(new S_ServerMessage(128)); // \f1쉴드(shield)를
																	// 장비 하고 있을
																	// 때는 양손으로
																	// 가지는 무기를
																	// 사용할 수
																	// 없습니다.
				return;
			}
		}

		activeChar.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제

		if (activeChar.getWeapon() != null) { // 이미 무엇인가를 장비 하고 있는 경우, 전의 장비를
												// 뗀다
			if (activeChar.getWeapon().getItem().getBless() == 2) { // 저주해지고 있었을
																	// 경우
				activeChar.sendPackets(new S_ServerMessage(150)); // \f1 뗄 수가
																	// 없습니다. 저주를
																	// 걸칠 수 있고
																	// 있는 것
																	// 같습니다.
				return;
			}
			if (activeChar.getWeapon().equals(weapon)) {
				// 장비 교환은 아니고 제외할 뿐
				pcInventory.setEquipped(activeChar.getWeapon(), false, false,
						false);
				return;
			} else {
				pcInventory.setEquipped(activeChar.getWeapon(), false, false,
						true);
			}
		}

		if (weapon.getItemId() == 200002) { // 저주해진 다이스다가
			activeChar.sendPackets(new S_ServerMessage(149, weapon
					.getLogName())); // \f1%0이 손에 들러붙었습니다.
		}
		pcInventory.setEquipped(weapon, true, false, false);
	}
}

