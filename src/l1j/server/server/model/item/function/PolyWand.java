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

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.Random;

import l1j.server.server.ActionCodes;
import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShowPolyList;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Skills;

@SuppressWarnings("serial")
public class PolyWand extends L1ItemInstance {

	private static Random _random = new Random(System.nanoTime());

	public PolyWand(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = this.getItemId();
			int spellsc_objid = 0;
			int spellsc_x = 0;
			int spellsc_y = 0;
			spellsc_objid = packet.readD();
			spellsc_x = packet.readH();
			spellsc_y = packet.readH();
			/** 인던 변신 안되도록 */
			if (pc.getMapId() == 5302|| pc.getMapId() == 5083|| pc.getMapId() == 5153 || pc.getMapId() == 5490) { // 배틀/인던/낚시터
				pc.sendPackets(new S_ServerMessage(1170)); // 이곳에서 변신할수 없습니다.
				return;
			}
			/** 인던 변신 안되도록 */
			if (pc.getMapId() == 63 || pc.getMapId() == 552
					|| pc.getMapId() == 555 || pc.getMapId() == 557
					|| pc.getMapId() == 558 || pc.getMapId() == 779) { // HC4f·배의
																		// 묘지
																		// 수중에서는
																		// 사용 불가
				pc.sendPackets(new S_ServerMessage(563));
			} else {
				int heding = CharPosUtil.targetDirection(pc, spellsc_x,
						spellsc_y);
				pc.getMoveState().setHeading(heding);
				pc.sendPackets(new S_AttackPacket(pc, 0,
						ActionCodes.ACTION_Wand));
				Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0,
						ActionCodes.ACTION_Wand));
				int chargeCount = useItem.getChargeCount();
				if (chargeCount <= 0 && itemId != 40410
						|| pc.getGfxId().getTempCharGfx() == 6034
						|| pc.getGfxId().getTempCharGfx() == 6035) {
					pc.sendPackets(new S_ServerMessage(79));
					return;
				}
				// 마을에서 단풍사용금지추가부분
				if (pc.getMap().isSafetyZone(pc.getLocation())) {
					pc.sendPackets(new S_SystemMessage(
							"마을안에서는 단풍 막대를 사용 할 수 없습니다."));
					return;
				}
				L1Object target = L1World.getInstance().findObject(
						spellsc_objid);
				if (target != null) {
					L1Character character = (L1Character) target;
					polyAction(pc, character);
					pc.cancelAbsoluteBarrier();
					if (itemId == 40008 || itemId == 140008 || itemId == 45464) {//픽시의 변신막대
						useItem.setChargeCount(useItem.getChargeCount() - 1);
						pc.getInventory().updateItem(useItem,
								L1PcInventory.COL_CHARGE_COUNT);
					} else {
						pc.getInventory().removeItem(useItem, 1);
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
			}
		}
	}

	private void polyAction(L1PcInstance attacker, L1Character cha) {
		boolean isSameClan = false;
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.getClanid() != 0 && attacker.getClanid() == pc.getClanid()) {
				isSameClan = true;
			}
		}
		if (cha instanceof L1MonsterInstance) {
			return;
		}
		if (attacker.getId() != cha.getId() && !isSameClan) {
			int probability = 3 * (attacker.getLevel() - cha.getLevel())
					- cha.getResistance().getEffectedMrBySkill();
			int rnd = _random.nextInt(100) + 1;
			if (rnd > probability) {
				return;
			}
		}

		int[] polyArray = { 29, 945, 947, 979, 1037, 1039, 3860, 3861, 3862,
				3863, 3864, 3865, 3904, 3906, 95, 146, 2374, 2376, 2377, 2378,
				3866, 3867, 3868, 3869, 3870, 3871, 3872, 3873, 3874, 3875,
				3876 };

		int pid = _random.nextInt(polyArray.length);
		int polyId = polyArray[pid];

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			
			if (pc.getInventory().checkEquipped(20281)) {
				pc.sendPackets(new S_ShowPolyList(pc.getId()));
				if (!pc.isShapeChange()) {
					pc.setShapeChange(true);
				}
				pc.sendPackets(new S_ServerMessage(966)); // string-j.tbl:968행째
				// 마법의 힘에 의해 보호됩니다.
				// 변신때의 메세지는, 타인이 자신을 변신시켰을 때에 나오는 메세지와 레벨이 부족할 때에 나오는 메세지 이외는
				// 없습니다.
			} else {
				L1Skills skillTemp = SkillsTable.getInstance().getTemplate(
						SHAPE_CHANGE);
				L1PolyMorph.doPoly(pc, polyId, skillTemp.getBuffDuration(),
						L1PolyMorph.MORPH_BY_ITEMMAGIC);
				if (attacker.getId() != pc.getId()) {
					pc
							.sendPackets(new S_ServerMessage(241, attacker
									.getName())); // %0가
					// 당신을
					// 변신시켰습니다.
				}
			}
		} else if (cha instanceof L1MonsterInstance) {
			L1MonsterInstance mob = (L1MonsterInstance) cha;
			if (mob.getLevel() < 50) {
				int npcId = mob.getNpcTemplate().get_npcId();
				if (npcId != 45338 && npcId != 45370 && npcId != 45456
						&& npcId != 45464 && npcId != 45473 && npcId != 45488
						&& npcId != 45497 && npcId != 45516 && npcId != 45529
						&& npcId != 45458) {
					L1Skills skillTemp = SkillsTable.getInstance().getTemplate(
							SHAPE_CHANGE);
					L1PolyMorph.doPoly(mob, polyId,
							skillTemp.getBuffDuration(),
							L1PolyMorph.MORPH_BY_ITEMMAGIC);
				}
			}
		}
	}
}
