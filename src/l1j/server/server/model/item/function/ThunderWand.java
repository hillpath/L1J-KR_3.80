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

import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1ItemDelay;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class ThunderWand extends L1ItemInstance {

	private static Random _random = new Random(System.nanoTime());

	public ThunderWand(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int spellsc_objid = 0;
			int spellsc_x = 0;
			int spellsc_y = 0;
			spellsc_objid = packet.readD();
			spellsc_x = packet.readH();
			spellsc_y = packet.readH();
			pc.cancelAbsoluteBarrier();
			if (pc.isInvisble()) {
				pc.sendPackets(new S_ServerMessage(1003));
				return;
			}

			int delay_id = 0;
			delay_id = ((L1EtcItem) useItem.getItem()).get_delayid();
			if (delay_id != 0) { // 지연 설정 있어
				if (pc.hasItemDelay(delay_id)) {
					return;
				}
			}

			int chargeCount = useItem.getChargeCount();
			if (chargeCount <= 0) {
				pc.sendPackets(new S_ServerMessage(79));
				return;
			}
			if (pc.getMap().isSafetyZone(pc.getLocation())) {
				pc.sendPackets(new S_SystemMessage(
						"마을안에서는 흑단 막대를 사용 할 수 없습니다."));
				return;
			}
			L1Object target = L1World.getInstance().findObject(spellsc_objid);

			int heding = CharPosUtil.targetDirection(pc, spellsc_x, spellsc_y);
			pc.getMoveState().setHeading(heding);
			if (target != null) {
				doWandAction(pc, target);
			} else {
				pc.sendPackets(new S_UseAttackSkill(pc, 0, 10, spellsc_x,
						spellsc_y, 17));
				Broadcaster.broadcastPacket(pc, new S_UseAttackSkill(pc, 0, 10,
						spellsc_x, spellsc_y, 17));
			}
			useItem.setChargeCount(useItem.getChargeCount() - 1);
			pc.getInventory().updateItem(useItem,
					L1PcInventory.COL_CHARGE_COUNT);
			if (useItem.getChargeCount() == 0) {
				pc.getInventory().removeItem(useItem);
			}
			L1ItemDelay.onItemUse(pc, useItem); // 아이템 지연 개시
		}
	}

	private void doWandAction(L1PcInstance user, L1Object target) {

		if (CharPosUtil.glanceCheck(user, target.getX(), target.getY()) == false) {
			return; // 직선상에 장애물이 있다
		}
		// XXX 적당한 데미지 계산, 요점 수정
		int dmg = ((_random.nextInt(11) - 5) + user.getAbility().getTotalStr() / 3);
		dmg = Math.max(1, dmg);

		if (target instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) target;
			/** by포비 데미지 감소가 크면 헛방나게 S */
			dmg -= pc.getDamageReductionByArmor(); // by포비 방어구에 의한 데미지 감소
			if (pc.getSkillEffectTimerSet().hasSkillEffect(SPECIAL_COOKING)) { // by포비
																				// 환상요리
				dmg -= 5;
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(DRAGON_SKIN)) { // by포비
																			// 드래곤
																			// 스킨
				dmg -= 2;
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(PATIENCE)) { // by포비
																		// 페이션스
				dmg -= 2;
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(REDUCTION_ARMOR)) { // by포비
																				// 리덕션
				int PobyLevel = pc.getLevel();
				if (PobyLevel < 50) {
					PobyLevel = 50;
				}
				dmg -= (PobyLevel - 50) / 5 + 1;
			}

			if (dmg <= 0) {

				dmg = 0;
			}
			/** by포비 데미지 감소가 크면 헛방나게 E */

			if (CharPosUtil.getZoneType(pc) == 1 || user.checkNonPvP(user, pc)
					|| CharPosUtil.getZoneType(user) == 1) {
				user.sendPackets(new S_UseAttackSkill(user, 0, 10, pc.getX(),
						pc.getY(), 17));
				Broadcaster.broadcastPacket(user, new S_UseAttackSkill(user, 0,
						10, pc.getX(), pc.getY(), 17));
				return;
			}

			if (pc.getSkillEffectTimerSet().hasSkillEffect(ERASE_MAGIC))
				pc.getSkillEffectTimerSet().removeSkillEffect(ERASE_MAGIC);

			if (isFreeze(pc)) {
				return;
			}

			if (user.getId() == target.getId()) {
				return; // 자기 자신에게 맞혔다
			}

			user.sendPackets(new S_UseAttackSkill(user, pc.getId(), 10, pc
					.getX(), pc.getY(), 17));
			Broadcaster.broadcastPacket(user, new S_UseAttackSkill(user, pc
					.getId(), 10, pc.getX(), pc.getY(), 17));
			L1PinkName.onAction(pc, user);

			int newHp = pc.getCurrentHp() - dmg;
			if (newHp > 0) {
				if (pc.isInvisble()) {
					pc.delInvis();
				}
				pc.setCurrentHp(newHp);
			} else if (newHp <= 0 && pc.isGm()) {
				pc.setCurrentHp(pc.getMaxHp());
			} else if (newHp <= 0 && !pc.isGm()) {
				pc.death(user);
			}
		} else if (target instanceof L1MonsterInstance) {
			L1MonsterInstance mob = (L1MonsterInstance) target;
			user.sendPackets(new S_UseAttackSkill(user, mob.getId(), 10, mob
					.getX(), mob.getY(), 17));
			Broadcaster.broadcastPacket(user, new S_UseAttackSkill(user, mob
					.getId(), 10, mob.getX(), mob.getY(), 17));
			mob.receiveDamage(user, dmg);
		} else if (target instanceof L1NpcInstance) {
			L1NpcInstance npc = (L1NpcInstance) target;
			user.sendPackets(new S_UseAttackSkill(user, npc.getId(), 10, npc
					.getX(), npc.getY(), 17));
			Broadcaster.broadcastPacket(user, new S_UseAttackSkill(user, npc
					.getId(), 10, npc.getX(), npc.getY(), 17));
		}
	}

	public boolean isFreeze(L1PcInstance pc) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_FREEZE)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
				|| pc.getSkillEffectTimerSet()
						.hasSkillEffect(FREEZING_BLIZZARD)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)) {
			return true;
		}
		return false;
	}
}
