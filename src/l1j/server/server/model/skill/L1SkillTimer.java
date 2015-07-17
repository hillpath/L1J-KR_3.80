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
package l1j.server.server.model.skill;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_DRAGONPERL;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_PinkName;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillIconShield;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Skills;

public interface L1SkillTimer {
	public int getRemainingTime();

	public void begin();

	public void end();

	public void kill();
}

class L1SkillStop {
	public static void stopSkill(L1Character cha, int skillId) {
		switch (skillId) {
		case LIGHT:
			if (cha instanceof L1PcInstance) {
				if (!cha.isInvisble()) {
					L1PcInstance pc = (L1PcInstance) cha;
					pc.getLight().turnOnOffLight();
				}
			}
			break;
		case SHIELD:
			cha.getAC().addAc(2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(5, 0));
			}
			break;
			
	/*	case ARMOR_BRAEK:{ //아머브레이크
		    if (cha instanceof L1PcInstance){
		     L1PcInstance pc = (L1PcInstance) cha;
		     pc.sendPackets(new S_SkillIconAura(119, 0));
		    }
		   }
		   break;*/
		   
		    /**아머브레이크*/
		  case ARMOR_BRAKE:{
		      if (cha instanceof L1PcInstance){
		       L1PcInstance pc = (L1PcInstance) cha;
		      // pc.sendPackets(new S_SkillIconAura(119, 0));
		      }
		     }
		     break;
		     /**아머브레이크*/
			
		case ENCHANT_WEAPON:
			cha.addDmgup(-2);
			break;
		case CURSE_BLIND:
		case DARKNESS:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_CurseBlind(0));
			}
			break;
		case BLESSED_ARMOR:
			cha.getAC().addAc(3);
			break;
		case PHYSICAL_ENCHANT_DEX:
			cha.getAbility().addAddedDex((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 1, 0));
			}
			break;
		case SLOW:
		case MASS_SLOW:
		case ENTANGLE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0,
						0));
			}
			cha.getMoveState().setMoveSpeed(0);
			break;
		case CURSE_PARALYZE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				Broadcaster.broadcastPacket(pc, new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_PARALYSIS,
						false));
			}
			break;
		case PHYSICAL_ENCHANT_STR:
			cha.getAbility().addAddedStr((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 1, 0));
			}
			break;
		case HASTE:
		case GREATER_HASTE:
			cha.getMoveState().setMoveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0,
						0));
			}
			break;
		case BUFF_SAEL:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-5);
				pc.addDmgup(-1);
				pc.addBowHitup(-5);
				pc.addBowDmgup(-1);
				pc.addMaxHp(-100);
				pc.addMaxMp(-50);
				pc.addHpr(-3);
				pc.addMpr(-3);
				pc
						.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
				pc
						.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		case SANGA:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-5);
				pc.addDmgup(-10);
				pc.addBowHitup(-5);
				pc.addBowDmgup(-10);
				pc.getAbility().addAddedStr(-3);
				pc.getAbility().addAddedDex(-3);
				pc.getAbility().addAddedInt(-3);
				pc.getAbility().addAddedCon(-3);
				pc.getAbility().addAddedWis(-3);
				pc.getAbility().addSp(-3);
				pc.sendPackets(new S_SPMR(pc));

			}
			break;
		case SANGABUFF:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-5);
				pc.addDmgup(-5);
				pc.addBowHitup(-5);
				pc.addBowDmgup(-5);
				pc.getAbility().addAddedStr(-2);
				pc.getAbility().addAddedDex(-2);
				pc.getAbility().addAddedInt(-2);
				pc.getAbility().addAddedCon(-2);
				pc.getAbility().addAddedWis(-2);
				pc.getAbility().addSp(-1);
				pc.sendPackets(new S_SPMR(pc));

			}
			break;
		// 상아탑 버프
		// 크레이 혈흔
		case BUFF_CRAY:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-5);
				pc.addDmgup(-1);
				pc.addBowHitup(-5);
				pc.addBowDmgup(-1);
				pc.addMaxHp(-100);
				pc.addMaxMp(-50);
				pc.addHpr(-3);
				pc.addMpr(-3);
				pc
						.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
				pc
						.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
				pc.sendPackets(new S_SPMR(pc));

			}
			break;
		case WEAKNESS:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(5);
				pc.addHitup(1);
			}
			break;
		// UI DG표시
		case UNCANNY_DODGE: // 언케니닷지
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDg(5);
			}
			break;
		// UI DG표시
		case BLESS_WEAPON:
			cha.addDmgup(-2);
			cha.addHitup(-2);
			cha.addBowHitup(-2);
			break;
		case MAAN_TIMER: // 마안타이머 //추가
			break;
		case ICE_LANCE:
		case FREEZING_BLIZZARD:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				Broadcaster.broadcastPacket(pc, new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				Broadcaster.broadcastPacket(npc, new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
			break;
		case HOLY_WALK:
		case MOVING_ACCELERATION:
		case WIND_WALK:
			cha.getMoveState().setBraveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0, 0));
			}
			break;
		case BERSERKERS:
			cha.getAC().addAc(-10);
			cha.addDmgup(-5);
			cha.addHitup(-2);
			break;
		case DISEASE:
		case MOB_DISEASE_30:
		case MOB_DISEASE_1:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(6);
				pc.getAC().addAc(-12);
			}
			break;
		case FOG_OF_SLEEPING:
		case PHANTASM:
			cha.setSleeped(false);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, false));
				pc.sendPackets(new S_OwnCharStatus(pc));
			}
			break;
		case SHAPE_CHANGE:
			L1PolyMorph.undoPoly(cha);
			break;
            /**안전모드**/
		case ABSOLUTE_BARRIER:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.startHpRegenerationByDoll();
				pc.startMpRegenerationByDoll();
				if(pc.Safe_Teleport){
					pc.sendPackets(new S_SystemMessage("안전 모드가 해제 되었습니다."));
					pc.Safe_Teleport = false;
				}
			}
			break;
			/**안전모드**/
		case ADVANCE_SPIRIT:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-pc.getAdvenHp());
				pc.addMaxMp(-pc.getAdvenMp());
				pc.setAdvenHp(0);
				pc.setAdvenMp(0);
				pc
						.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc
						.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
			}
			break;
		case SHOCK_STUN:
		case MOB_SHOCKSTUN_30:
		case MOB_RANGESTUN_19:
		case MOB_RANGESTUN_18:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
			break;
		case BLIND_HIDING:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.delBlindHiding();
			}
			break;
		case SHADOW_ARMOR:
			cha.getResistance().addMr(-5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(3, 0));
			}
			break;
		case SHADOW_FANG:
			cha.addDmgup(-5);
			break;
		case DRESS_MIGHTY:
			cha.getAbility().addAddedStr((byte) -2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 1, 0));
			}
			break;
		case DRESS_DEXTERITY:
			cha.getAbility().addAddedDex((byte) -2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 1, 0));
			}
			break;
		case GLOWING_AURA:
			cha.addHitup(-5);
			cha.addBowHitup(-5);
			cha.getResistance().addMr(-20);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_SkillIconAura(113, 0));
			}
			break;
		case SHINING_AURA:
			cha.getAC().addAc(8);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(114, 0));
			}
			break;
		case BRAVE_AURA:
			cha.addDmgup(-5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(116, 0));
			}
			break;
		case RESIST_MAGIC:
			cha.getResistance().addMr(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		case ELEMENTAL_FALL_DOWN:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				int attr = pc.getAddAttrKind();
				int i = 50;
				switch (attr) {
				case 1:
					pc.getResistance().addEarth(i);
					break;
				case 2:
					pc.getResistance().addFire(i);
					break;
				case 4:
					pc.getResistance().addWater(i);
					break;
				case 8:
					pc.getResistance().addWind(i);
					break;
				default:
					break;
				}
				pc.setAddAttrKind(0);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			} else if (cha instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				int attr = npc.getAddAttrKind();
				int i = 50;
				switch (attr) {
				case 1:
					npc.getResistance().addEarth(i);
					break;
				case 2:
					npc.getResistance().addFire(i);
					break;
				case 4:
					npc.getResistance().addWater(i);
					break;
				case 8:
					npc.getResistance().addWind(i);
					break;
				default:
					break;
				}
				npc.setAddAttrKind(0);
			}
			break;
		case CLEAR_MIND:
			cha.getAbility().addAddedWis((byte) -3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.resetBaseMr();
			}
			break;
		case RESIST_ELEMENTAL:
			cha.getResistance().addAllNaturalResistance(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case ELEMENTAL_PROTECTION:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				int attr = pc.getElfAttr();
				if (attr == 1) {
					cha.getResistance().addEarth(-50);
				} else if (attr == 2) {
					cha.getResistance().addFire(-50);
				} else if (attr == 4) {
					cha.getResistance().addWater(-50);
				} else if (attr == 8) {
					cha.getResistance().addWind(-50);
				}
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case FIRE_WEAPON:
			cha.addDmgup(-4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(147, 0));
			}
			break;
		case 999: // 드래곤진주스킬아이디
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_DRAGONPERL(pc.getId(), 0));
				pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONPERL, 0, 0));
				pc.set진주속도(0);
			}

			break;
		case WIND_SHOT:
			cha.addBowHitup(-6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(148, 0));
			}
			break;
		case EARTH_SKIN:
			cha.getAC().addAc(6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(6, 0));
			}
			break;
		case ERASE_MAGIC:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_AURA));
			}
			break;
		case FIRE_BLESS:
			cha.addDmgup(-4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(154, 0));
			}
			break;
		case STORM_EYE:
			cha.addBowHitup(-2);
			cha.addBowDmgup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(155, 0));
			}
			break;
		case EARTH_BIND:
		case MOB_COCA:
		case MOB_BASILL:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				Broadcaster.broadcastPacket(pc, new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				Broadcaster.broadcastPacket(npc, new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
			break;
		case EARTH_BLESS:
			cha.getAC().addAc(7);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(7, 0));
			}
			break;
		case BURNING_WEAPON:
			cha.addDmgup(-6);
			cha.addHitup(-6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(162, 0));
			}
			break;
			
		case MIRROR_IMAGE: {
			L1PcInstance pc = (L1PcInstance) cha;
             pc.addDg(5);
		}
				break;
			
			
		case STORM_SHOT:
			cha.addBowDmgup(-6);
			cha.addBowHitup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(165, 0));
			}
			break;
		case WIND_SHACKLE:
		case MOB_WINDSHACKLE_1:
		case P_WIND_SHACKLE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), 0));
			}
			break;
		case IRON_SKIN:
			cha.getAC().addAc(10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(10, 0));
			}
			break;
		case GUARD_BREAK:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(-15);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case SCALES_EARTH_DRAGON://용기사 안타각성
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(3);
				pc.getResistance().addHold(-10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"안타라스각성: [600]초가 다되어 효과가 사라집니다.", Opcodes.S_OPCODE_MSG, 20);
				pc.sendPackets(s_chatpacket);
			}
			break;
		case BLOOD_LUST:
			cha.getMoveState().setBraveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0, 0));
				pc.sendPackets(new S_SkillBrave(pc.getId(), 1, 0));
			}
			break;
		case SCALES_WATER_DRAGON://용기사 파푸각성
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addFreeze(-10);
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"파푸리온각성: [600]초가 다되어 효과가 사라집니다.", Opcodes.S_OPCODE_MSG, 20);
				pc.sendPackets(s_chatpacket);
			}
			break;
		case HORROR_OF_DEATH:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addAddedStr((byte) 5);
				pc.getAbility().addAddedInt((byte) 5);
			}
			break;
		case SCALES_FIRE_DRAGON://용기사 발라각성
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addStun(-10); //여기까지가 스킬 부분임아
				pc.addHitup(-5);
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"발라카스각성: [600]초가 다되어 효과가 사라집니다.", Opcodes.S_OPCODE_MSG, 20);
				pc.sendPackets(s_chatpacket);
			}
			break;
		case IllUSION_OGRE:// 오우거
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-4);
				pc.addHitup(-4);
			    pc.addBowHitup(-4);
                pc.addBowDmgup(-4);
			}
			break;
		case BONE_BREAK:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
			break;
		case BOUNCE_ATTACK: { // 바운스어택
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addHitup(-6);
		}
			break;
		case IllUSION_LICH:// 리치
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addSp(-2);
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
	/*	case AM_BREAK:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(2);
			}
			break;*/
		case IllUSION_DIAMONDGOLEM:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(20);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case INSIGHT:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addAddedStr((byte) -1);
				pc.getAbility().addAddedDex((byte) -1);
				pc.getAbility().addAddedCon((byte) -1);
				pc.getAbility().addAddedInt((byte) -1);
				pc.getAbility().addAddedWis((byte) -1);
				pc.resetBaseMr();
			}
			break;
		case PANIC:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addAddedStr((byte) 1);
				pc.getAbility().addAddedDex((byte) 1);
				pc.getAbility().addAddedCon((byte) 1);
				pc.getAbility().addAddedInt((byte) 1);
				pc.getAbility().addAddedWis((byte) 1);
				pc.resetBaseMr();
			}
			break;
		case IllUSION_AVATAR:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-10);
				pc.addBowDmgup(-10);
				pc.getAbility().addSp(-6);
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		case STATUS_BRAVE:
		case STATUS_ELFBRAVE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 0,
						0));
			}
			cha.getMoveState().setBraveSpeed(0);
			break;
		case STATUS_FRUIT:
			cha.getMoveState().setBraveSpeed(0);
			break;
		case STATUS_HASTE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0,
						0));
			}
			cha.getMoveState().setMoveSpeed(0);
			break;
		case STATUS_UNDERWATER_BREATH:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 0));
			}
			break;
		case STATUS_WISDOM_POTION:
			if (cha instanceof L1PcInstance) {
				// L1PcInstance pc = (L1PcInstance) cha;
				cha.getAbility().addSp(-2);
				// pc.sendPackets(new S_SkillIconWisdomPotion(0));
			}
			break;
		case STATUS_POISON:
			cha.curePoison();
			break;
		case STATUS_PET_FOOD:
			if (cha instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) cha;
				int foodvalue = pet.getFood() + 1;
				switch (foodvalue) {
				case 1:
				case 2:
				case 3:
				case 4:
					pet.setFood(foodvalue);
					break;
				case 5:
					pet.setFood(5);
					pet.setCurrentPetStatus(3);
					break;
				case 6:
					pet.setFood(5);
					break;
				default:
					break;
				}
				pet.getSkillEffectTimerSet().setSkillEffect(
						L1SkillId.STATUS_PET_FOOD, 1200 * 1000);
			}
			break;
		case STATUS_PINK_NAME:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PinkName(pc.getId(), 0));
				Broadcaster.broadcastPacket(pc, new S_PinkName(pc.getId(), 0));
				pc.setPinkName(false);
			}
			break;
		case STATUS_TIKAL_BOSSJOIN:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-5);
				pc.addDmgup(-10);
				pc.addBowHitup(-5);
				pc.addBowDmgup(-10);
				pc.getAbility().addAddedStr((byte) -3);
				pc.getAbility().addAddedDex((byte) -3);
				pc.getAbility().addAddedCon((byte) -3);
				pc.getAbility().addAddedInt((byte) -3);
				pc.getAbility().addAddedWis((byte) -3);
				pc.getAbility().addSp(-3);
			}
			break;
		case STATUS_TIKAL_BOSSDIE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-5);
				pc.addDmgup(-5);
				pc.addBowHitup(-5);
				pc.addBowDmgup(-5);
				pc.getAbility().addAddedStr((byte) -2);
				pc.getAbility().addAddedDex((byte) -2);
				pc.getAbility().addAddedCon((byte) -2);
				pc.getAbility().addAddedInt((byte) -2);
				pc.getAbility().addAddedWis((byte) -2);
				pc.getAbility().addSp(-1);
			}
			break;

		/** 운세 버프 * */
		case STATUS_LUCK_A:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-2);
				pc.addHitup(-2);
				pc.getAbility().addSp(-2);
				pc.sendPackets(new S_SPMR(pc));
				pc.addHpr(-3);
				pc.addMaxHp(-50);
				pc
						.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.addMpr(-3);
				pc.addMaxMp(-30);
				pc
						.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
			}
			break;
		case STATUS_LUCK_B:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-2);
				pc.getAbility().addSp(-1);
				pc.sendPackets(new S_SPMR(pc));
				pc.addMaxHp(-50);
				pc
						.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.addMaxMp(-30);
				pc
						.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
			}
			break;
		case STATUS_LUCK_C:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc
						.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.addMaxMp(-30);
				pc
						.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
				pc.getAC().addAc(2);
			}
			break;
		case STATUS_LUCK_D:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(1);
			}
			break;
		case STATUS_CHAT_PROHIBITED:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_ServerMessage(288));
			}
			break;
		case STATUS_COMA_3:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(2);
				pc.addHitup(-3);
				pc.getAbility().addAddedStr((byte) -5);
				pc.getAbility().addAddedDex((byte) -5);
				pc.getAbility().addAddedCon((byte) -1);
			}
			break;
		case STATUS_COMA_5:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(8);
				pc.addHitup(-5);
				pc.getAbility().addAddedStr((byte) -5);
				pc.getAbility().addAddedDex((byte) -5);
				pc.getAbility().addAddedCon((byte) -1);
				pc.getAbility().addSp(-1);
			}
			break;
		case LIND_MAAN:// 마안풍룡
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addSleep(-15); // 수면내성3
			}
			break;
		case FAFU_MAAN:// 마안수룡
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addFreeze(-15); // 동빙내성3
			}
			break;
		case ANTA_MAAN:// 마안지룡
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addHold(-15); // 홀드내성
			}
			break;
		case VALA_MAAN:// 마안화룡
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addStun(-15); // 스턴내성3
			}
			break;
		case BIRTH_MAAN:// 마안탄생BIRTH_MAAN
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addHold(-15); // 홀드내성
				pc.getResistance().addFreeze(-15); // 동빙내성3
			}
			break;
		case SHAPE_MAAN:// 마안형상
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addHold(-15); // 홀드내성
				pc.getResistance().addFreeze(-15); // 동빙내성3
				pc.getResistance().addSleep(-15); // 수면내성3
			}
			break;
		case LIFE_MAAN:// 마안생명
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addHold(-15); // 홀드내성
				pc.getResistance().addFreeze(-15); // 동빙내성3
				pc.getResistance().addSleep(-15); // 수면내성3
				pc.getResistance().addStun(-15); // 스턴내성3
			}
			break;
		case COOKING_1_0_N:
		case COOKING_1_0_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addAllNaturalResistance(-10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_PacketBox(53, 0, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_1_N:
		case COOKING_1_1_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc
						.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_PacketBox(53, 1, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_2_N:
		case COOKING_1_2_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMpr(-3);
				pc.sendPackets(new S_PacketBox(53, 2, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_3_N:
		case COOKING_1_3_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(1);
				pc.sendPackets(new S_PacketBox(53, 3, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_4_N:
		case COOKING_1_4_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-20);
				pc
						.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 4, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_5_N:
		case COOKING_1_5_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHpr(-3);
				pc.sendPackets(new S_PacketBox(53, 5, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_6_N:
		case COOKING_1_6_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addMr(-5);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 6, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_7_N:
		case COOKING_1_7_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
			break;
		case COOKING_1_8_N:
		case COOKING_1_8_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-1);
				pc.addDmgup(-1);
				pc.sendPackets(new S_PacketBox(53, 16, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_9_N:
		case COOKING_1_9_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-30);
				pc.addMaxHp(-30);
				pc
						.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
				pc
						.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
				pc.sendPackets(new S_PacketBox(53, 17, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_10_N:
		case COOKING_1_10_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(2);
				pc.sendPackets(new S_OwnCharStatus2(pc));
				pc.sendPackets(new S_PacketBox(53, 18, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_11_N:
		case COOKING_1_11_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addBowHitup(-1);
				pc.addBowDmgup(-1);
				pc.sendPackets(new S_PacketBox(53, 19, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_12_N:
		case COOKING_1_12_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHpr(-2);
				pc.addMpr(-2);
				pc.sendPackets(new S_PacketBox(53, 20, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_13_N:
		case COOKING_1_13_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addMr(-10);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 21, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_14_N:
		case COOKING_1_14_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addSp(-1);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 22, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_NEW_1:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				 pc.addHitup(-1); 
				   pc.addDmgup(-2);
				   pc.addMpr(-2);
				   pc.addHpr(-2);
				   pc.getResistance().addAllNaturalResistance(-10);
				   pc.sendPackets(new S_OwnCharAttrDef(pc));
				   pc.getResistance().addMr(-10);
				   pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
			break;
		case COOKING_NEW_2:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				 pc.addBowHitup(-1); 
				   pc.addBowDmgup(-2);
				   pc.addMpr(-2);
				   pc.addHpr(-2);
				   pc.getResistance().addAllNaturalResistance(-10);
				   pc.sendPackets(new S_OwnCharAttrDef(pc));
				   pc.getResistance().addMr(-10);
				   pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
			break;
		case COOKING_NEW_3:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAbility().addSp(-2);
				   pc.addMpr(-3);
				   pc.addHpr(-2);
				   pc.getResistance().addAllNaturalResistance(-10);
				   pc.sendPackets(new S_OwnCharAttrDef(pc));
				   pc.getResistance().addMr(-10);
				   pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
			break;
		case COOKING_NEW_4:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
			break;
		case COOKING_1_15_N:
		case COOKING_1_15_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
			break;
		case COOKING_1_16_N:
		case COOKING_1_16_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addBowHitup(-2);
				pc.addBowDmgup(-1);
				pc.sendPackets(new S_PacketBox(53, 45, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_17_N:
		case COOKING_1_17_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc.addMaxMp(-50);
				pc
						.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc
						.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 46, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_18_N:
		case COOKING_1_18_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-2);
				pc.addDmgup(-1);
				pc.sendPackets(new S_PacketBox(53, 47, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_19_N:
		case COOKING_1_19_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(3);
				pc.sendPackets(new S_OwnCharStatus2(pc));
				pc.sendPackets(new S_PacketBox(53, 48, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_20_N:
		case COOKING_1_20_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addAllNaturalResistance(-10);
				pc.getResistance().addMr(-15);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_PacketBox(53, 49, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_21_N:
		case COOKING_1_21_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMpr(-2);
				pc.getAbility().addSp(-2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 50, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_22_N:
		case COOKING_1_22_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHpr(-2);
				pc.addMaxHp(-30);
				pc
						.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_PacketBox(53, 51, 0));
				pc.setCookingId(0);
			}
			break;
		case COOKING_1_23_N:
		case COOKING_1_23_S:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
			break;
		case STATUS_CASHSCROLL:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHpr(-4);
				pc.addMaxHp(-50);
				pc
						.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc
						.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
			}
			break;
		case STATUS_CASHSCROLL2:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHpr(-4);
				pc.addMaxMp(-40);
				pc
						.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
			}
			break;
		case STATUS_CASHSCROLL3://전강
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-3);
				pc.addHitup(-3);
				pc.addBowHitup(-3);
			    pc.addBowDmgup(-3);
				pc.getAbility().addSp(-3);
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		case benz_TIMER://아이돌
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-5);
				pc.addHitup(-5);
				pc.addBowHitup(-5);
			    pc.addBowDmgup(-5);
				pc.getAbility().addSp(-3);
				pc.addMaxHp(-200);
				pc.addMaxMp(-100);
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		case benz_TIMER1://아이돌
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(-8);
				pc.addHitup(-8);
				pc.addBowHitup(-8);
			    pc.addBowDmgup(-8);
				pc.getAbility().addSp(-6);
				pc.addMaxHp(-400);
				pc.addMaxMp(-200);
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
			
		case STATUS_FREEZE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
			break;
		case STATUS_IGNITION:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addFire(-30);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case STATUS_QUAKE:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addEarth(-30);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case STATUS_SHOCK:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getResistance().addWind(-30);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
			break;
		case DRAGONBLOOD_ANTA:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(2);
				pc.getResistance().addWater(-50);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 82, 0));
			}
			break;
		case DRAGONBLOOD_PAP:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.getAC().addAc(2);
				pc.getResistance().addWind(-50);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 85, 0));
			}
			break;
               case DRAGONBLOOD_RIND://린드레이드
            if(cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.getResistance().addEarth(-50);
                pc.sendPackets(new S_SPMR(pc));
                pc.sendPackets(new S_OwnCharAttrDef(pc));
                pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 88, 0));
               }
               break;
		default:
			break;
		}

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			sendStopMessage(pc, skillId);
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
	}

	private static void sendStopMessage(L1PcInstance charaPc, int skillid) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillid);
		if (l1skills == null || charaPc == null) {
			return;
		}

		int msgID = l1skills.getSysmsgIdStop();
		if (msgID > 0) {
			charaPc.sendPackets(new S_ServerMessage(msgID));
		}
	}
}

class L1SkillTimerThreadImpl extends Thread implements L1SkillTimer {
	private final L1Character _cha;

	private final int _timeMillis;

	private final int _skillId;

	private int _remainingTime;

	private volatile int timeCount;

	public L1SkillTimerThreadImpl(L1Character cha, int skillId, int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;
	}

	@Override
	public void run() {
		for (timeCount = _timeMillis / 1000; timeCount > 0; timeCount--) {
			try {
				Thread.sleep(1000);
				_remainingTime = timeCount;
			} catch (InterruptedException e) {
				return;
			}
		}
		_cha.getSkillEffectTimerSet().removeSkillEffect(_skillId);
	}

	public int getRemainingTime() {
		return _remainingTime;
	}

	public void begin() {
		GeneralThreadPool.getInstance().execute(this);
	}

	public void end() {
		super.interrupt();
		L1SkillStop.stopSkill(_cha, _skillId);
	}

	public void kill() {
		if (Thread.currentThread().getId() == super.getId()) {
			return;
		}
		timeCount = 0;
	}
}

class L1SkillTimerTimerImpl implements L1SkillTimer, Runnable {
	private static Logger _log = Logger.getLogger(L1SkillTimerTimerImpl.class.getName());
	private ScheduledFuture<?> _future = null;

	public L1SkillTimerTimerImpl(L1Character cha, int skillId, int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;

		_remainingTime = _timeMillis / 1000;
	}

	@Override
	public void run() {
		_remainingTime--;
		if (_remainingTime <= 0) {
			_cha.getSkillEffectTimerSet().removeSkillEffect(_skillId);
		}
	}

	@Override
	public void begin() {
		_future = GeneralThreadPool.getInstance().scheduleAtFixedRate(this, 1000, 1000);
	}

	@Override
	public void end() {
		kill();
		try {
			L1SkillStop.stopSkill(_cha, _skillId);
		} catch (Throwable e) {
		//	_log.log(Level.SEVERE, "L1SkillTimer[]Error", e);
		}
	}

	@Override
	public void kill() {
		if (_future != null) {
			_future.cancel(false);
		}
	}

	@Override
	public int getRemainingTime() {
		return _remainingTime;
	}

	private final L1Character _cha;
	private final int _timeMillis;
	private final int _skillId;
	private int _remainingTime;
}