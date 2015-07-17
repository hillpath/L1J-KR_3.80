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
package l1j.server.server.model;

import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;

import static l1j.server.server.model.skill.L1SkillId.AREA_OF_SILENCE;
import static l1j.server.server.model.skill.L1SkillId.ARMOR_BRAKE;
import static l1j.server.server.model.skill.L1SkillId.BIRTH_MAAN;
import static l1j.server.server.model.skill.L1SkillId.BONE_BREAK;
import static l1j.server.server.model.skill.L1SkillId.CALL_LIGHTNING;
import static l1j.server.server.model.skill.L1SkillId.CANCELLATION;
import static l1j.server.server.model.skill.L1SkillId.CHILL_TOUCH;
import static l1j.server.server.model.skill.L1SkillId.CONE_OF_COLD;
import static l1j.server.server.model.skill.L1SkillId.CONFUSION;
import static l1j.server.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.COUNTER_MIRROR;
import static l1j.server.server.model.skill.L1SkillId.CURSE_BLIND;
import static l1j.server.server.model.skill.L1SkillId.CURSE_PARALYZE;
import static l1j.server.server.model.skill.L1SkillId.CURSE_POISON;
import static l1j.server.server.model.skill.L1SkillId.DARKNESS;
import static l1j.server.server.model.skill.L1SkillId.DARK_BLIND;
import static l1j.server.server.model.skill.L1SkillId.DECAY_POTION;
import static l1j.server.server.model.skill.L1SkillId.DISEASE;
import static l1j.server.server.model.skill.L1SkillId.DISINTEGRATE;
import static l1j.server.server.model.skill.L1SkillId.DRAGON_SKIN;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_FALL_DOWN;
import static l1j.server.server.model.skill.L1SkillId.ENERGY_BOLT;
import static l1j.server.server.model.skill.L1SkillId.ENTANGLE;
import static l1j.server.server.model.skill.L1SkillId.ERASE_MAGIC;
import static l1j.server.server.model.skill.L1SkillId.FAFU_MAAN;
import static l1j.server.server.model.skill.L1SkillId.FEAR;
import static l1j.server.server.model.skill.L1SkillId.FINAL_BURN;
import static l1j.server.server.model.skill.L1SkillId.FIRE_ARROW;
import static l1j.server.server.model.skill.L1SkillId.FIRE_WALL;
import static l1j.server.server.model.skill.L1SkillId.FOG_OF_SLEEPING;
import static l1j.server.server.model.skill.L1SkillId.FREEZING_BLIZZARD;
import static l1j.server.server.model.skill.L1SkillId.GUARD_BREAK;
import static l1j.server.server.model.skill.L1SkillId.HORROR_OF_DEATH;
import static l1j.server.server.model.skill.L1SkillId.ICE_DAGGER;
import static l1j.server.server.model.skill.L1SkillId.ICE_LANCE;
import static l1j.server.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static l1j.server.server.model.skill.L1SkillId.IllUSION_AVATAR;
import static l1j.server.server.model.skill.L1SkillId.JOY_OF_PAIN;
import static l1j.server.server.model.skill.L1SkillId.LIFE_MAAN;
import static l1j.server.server.model.skill.L1SkillId.LIGHTNING;
import static l1j.server.server.model.skill.L1SkillId.LIGHTNING_STORM;
import static l1j.server.server.model.skill.L1SkillId.LIND_MAAN;
import static l1j.server.server.model.skill.L1SkillId.MANA_DRAIN;
import static l1j.server.server.model.skill.L1SkillId.MASS_SLOW;
import static l1j.server.server.model.skill.L1SkillId.MIND_BREAK;
import static l1j.server.server.model.skill.L1SkillId.MOB_BASILL;
import static l1j.server.server.model.skill.L1SkillId.MOB_COCA;
import static l1j.server.server.model.skill.L1SkillId.PANIC;
import static l1j.server.server.model.skill.L1SkillId.PAP_FIVEPEARLBUFF;
import static l1j.server.server.model.skill.L1SkillId.PAP_MAGICALPEARLBUFF;
import static l1j.server.server.model.skill.L1SkillId.PATIENCE;
import static l1j.server.server.model.skill.L1SkillId.PHANTASM;
import static l1j.server.server.model.skill.L1SkillId.POLLUTE_WATER;
import static l1j.server.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static l1j.server.server.model.skill.L1SkillId.RETURN_TO_NATURE;
import static l1j.server.server.model.skill.L1SkillId.SHAPE_MAAN;
import static l1j.server.server.model.skill.L1SkillId.SHOCK_STUN;
import static l1j.server.server.model.skill.L1SkillId.SILENCE;
import static l1j.server.server.model.skill.L1SkillId.SLOW;
import static l1j.server.server.model.skill.L1SkillId.SMASH;
import static l1j.server.server.model.skill.L1SkillId.SPECIAL_COOKING;
import static l1j.server.server.model.skill.L1SkillId.SPECIAL_COOKING2;
import static l1j.server.server.model.skill.L1SkillId.STALAC;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CURSE_BARLOG;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CURSE_YAHEE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_MITHRIL_POWDER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_WATER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HOLY_WATER_OF_EVA;
import static l1j.server.server.model.skill.L1SkillId.STRIKER_GALE;
import static l1j.server.server.model.skill.L1SkillId.TAMING_MONSTER;
import static l1j.server.server.model.skill.L1SkillId.THUNDER_GRAB;
import static l1j.server.server.model.skill.L1SkillId.TURN_UNDEAD;
import static l1j.server.server.model.skill.L1SkillId.VAMPIRIC_TOUCH;
import static l1j.server.server.model.skill.L1SkillId.WEAKNESS;
import static l1j.server.server.model.skill.L1SkillId.WEAPON_BREAK;
import static l1j.server.server.model.skill.L1SkillId.WIND_CUTTER;
import static l1j.server.server.model.skill.L1SkillId.WIND_SHACKLE;

import java.util.Random;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.CalcStat;

public class L1Magic {

	private int _calcType;

	private final int PC_PC = 1;

	private final int PC_NPC = 2;

	private final int NPC_PC = 3;

	private final int NPC_NPC = 4;

	private L1PcInstance _pc = null;

	private L1PcInstance _targetPc = null;

	private L1NpcInstance _npc = null;

	private L1NpcInstance _targetNpc = null;

	private int _leverage = 13;

	private L1Skills _skill;

	private static Random _random = new Random(System.nanoTime());

	public void setLeverage(int i) {
		_leverage = i;
	}

	private int getLeverage() {
		return _leverage;
	}

	public L1Magic(L1Character attacker, L1Character target) {
		if (attacker instanceof L1PcInstance) {
			if (target instanceof L1PcInstance) {
				_calcType = PC_PC;
				_pc = (L1PcInstance) attacker;
				_targetPc = (L1PcInstance) target;
			} else {
				_calcType = PC_NPC;
				_pc = (L1PcInstance) attacker;
				_targetNpc = (L1NpcInstance) target;
			}
		} else {
			if (target instanceof L1PcInstance) {
				_calcType = NPC_PC;
				_npc = (L1NpcInstance) attacker;
				_targetPc = (L1PcInstance) target;
			} else {
				_calcType = NPC_NPC;
				_npc = (L1NpcInstance) attacker;
				_targetNpc = (L1NpcInstance) target;
			}
		}
	}

	/* ■■■■■■■■■■■■■■■ 마법 공통 함수 ■■■■■■■■■■■■■■ */

	@SuppressWarnings("unused")
	private int getSpellPower() {
		int spellPower = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			spellPower = _pc.getAbility().getSp();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			spellPower = _npc.getAbility().getSp();
		}
		return spellPower;
	}

	private int getMagicLevel() {
		int magicLevel = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			magicLevel = _pc.getAbility().getMagicLevel();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			magicLevel = _npc.getAbility().getMagicLevel();
		}
		return magicLevel;
	}

	private int getMagicBonus() {
		int magicBonus = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			magicBonus = _pc.getAbility().getMagicBonus();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			magicBonus = _npc.getAbility().getMagicBonus();
		}
		return magicBonus;
	}

	private int getLawful() {
		int lawful = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			lawful = _pc.getLawful();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			lawful = _npc.getLawful();
		}
		return lawful;
	}

	private int getTargetMr() {
		int mr = 0;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			mr = _targetPc.getResistance().getEffectedMrBySkill();
		} else {
			mr = _targetNpc.getResistance().getEffectedMrBySkill();
		}
		return mr;
	}

	private int getMagicHitupByArmor() {
		int HitupByArmor = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			HitupByArmor = _pc.getMagicHitupByArmor();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			HitupByArmor = 0;
		}
		return HitupByArmor;
	}

	/* ■■■■■■■■■■■■■■ 성공 판정 ■■■■■■■■■■■■■ */
	// ●●●● 확률계 마법의 성공 판정 ●●●●
	// 계산방법
	// 공격측 포인트：LV + ((MagicBonus * 3) * 마법 고유 계수)
	// 방어측 포인트：((LV / 2) + (MR * 3)) / 2
	// 공격 성공율：공격측 포인트 - 방어측 포인트
	public boolean calcProbabilityMagic(int skillId) {
		int probability = 0;
		boolean isSuccess = false;

		if (_pc != null && _pc.isGm()) {
			return true;
		}
		// 타겟이 GM일경우 무조건 실패
		// if (_targetPc != null && _targetPc.isGm()) {
		// return false;
		// }

		if (_calcType == PC_NPC && _targetNpc != null) {
			int npcId = _targetNpc.getNpcTemplate().get_npcId();
			if (npcId >= 45912
					&& npcId <= 45915
					&& !_pc.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_HOLY_WATER)) {
				return false;
			}
			if (npcId == 45916
					&& !_pc.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_HOLY_MITHRIL_POWDER)) {
				return false;
			}
			if (npcId == 45941
					&& !_pc.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_HOLY_WATER_OF_EVA)) {
				return false;
			}
			if (!_pc.getSkillEffectTimerSet().hasSkillEffect(
					STATUS_CURSE_BARLOG)
					&& (npcId == 45752 || npcId == 45753 || npcId == 7000007 || npcId == 7000008
							 || npcId == 7000009 || npcId == 70000010|| npcId == 7000011 
							 || npcId == 7000012 || npcId == 7000013 || npcId == 7000014
							 || npcId == 7000015 || npcId == 7000016 )) {////무인마법
				return false;
			}
			if (!_pc.getSkillEffectTimerSet()
					.hasSkillEffect(STATUS_CURSE_YAHEE)
					&& (npcId == 45675 || npcId == 81082 || npcId == 45625
							|| npcId == 45674 || npcId == 45685)) {
				return false;
			}

			 if(_pc.getProtect() == true && _targetPc.getProtect() == false){ //혈맹보호 
				   return false;
				   }
			if (npcId >= 46068 && npcId <= 46091
					&& _pc.getGfxId().getTempCharGfx() == 6035) {
				return false;
			}
			if (npcId >= 46092 && npcId <= 46106
					&& _pc.getGfxId().getTempCharGfx() == 6034) {
				return false;
			}
			if (npcId == 4039001
					&& !_pc.getSkillEffectTimerSet().hasSkillEffect(PAP_FIVEPEARLBUFF)) {//오색진주
				return false;
			}
			if (npcId == 4039002
					&& !_pc.getSkillEffectTimerSet().hasSkillEffect(PAP_MAGICALPEARLBUFF)) {//오색진주
				return false;
			}
			if (_targetNpc.getNpcTemplate().get_gfxid() == 7720){ //토르나르
			return false; 
		}
		}

		if (!checkZone(skillId)) {
			return false;
		}

		if (skillId == CANCELLATION) {
			if (_calcType == PC_PC && _pc != null && _targetPc != null) {

				if(_targetPc.getMapId() == 5153 || _targetPc.getMapId() == 5001){
					_pc.sendPackets(new S_SystemMessage("\\fY배틀존 내에서는 캔슬마법이 불가능합니다.")); 
					return false;
				}
				if (_targetPc.isInvisble()){
					return false;
				}
				if (_pc.getId() == _targetPc.getId()) {
					return true;
				}
				if (_pc.getClanid() > 0
						&& (_pc.getClanid() == _targetPc.getClanid())) {
					return true;
				}
				if (_pc.isInParty()) {
					if (_pc.getParty().isMember(_targetPc)) {
						return true;
					}
				}
				// 대상이 인비지 상태일땐 켄슬 무효
				if (_targetPc.isInvisble()) {
					return false;
				}
				if (CharPosUtil.getZoneType(_pc) == 1
						|| CharPosUtil.getZoneType(_targetPc) == 1) {
					_pc.sendPackets(new S_SystemMessage("마을 안에서는 사용 불가능합니다."));
					return false;
				}
			}
			// 대상이 NPC, 사용자가 NPC의 경우는100% 성공
			if (_calcType == PC_NPC || _calcType == NPC_PC
					|| _calcType == NPC_NPC) {
				return true;
			}
		}

		if (_calcType == PC_NPC
				&& _targetNpc.getNpcTemplate().isCantResurrect()) { // 50렙
			// 이상
			// npc
			// 에게
			// 아래
			// 마법
			// 안걸림:즉
			// 보스몬스터에게
			// 사용불가
			if (skillId == WEAPON_BREAK || skillId == SLOW
					|| skillId == CURSE_PARALYZE || skillId == MANA_DRAIN
					|| skillId == WEAKNESS || skillId == SILENCE
					|| skillId == DISEASE || skillId == DECAY_POTION
					|| skillId == MASS_SLOW || skillId == ENTANGLE
					|| skillId == ERASE_MAGIC || skillId == DARKNESS
					|| skillId == AREA_OF_SILENCE || skillId == WIND_SHACKLE
					|| skillId == STRIKER_GALE || skillId == RETURN_TO_NATURE
					|| skillId == FOG_OF_SLEEPING || skillId == ICE_LANCE
					|| skillId == FREEZING_BLIZZARD || skillId == POLLUTE_WATER
					|| skillId == THUNDER_GRAB) {
				return false;
			}
		}

if(_calcType == PC_PC){
      if(_targetPc.getLevel() <= Config.MAX_LEVEL || _pc.getLevel() <= Config.MAX_LEVEL){ //신규보호레벨55까지
    	  _skill = SkillsTable.getInstance().getTemplate(skillId);
			if (skillId != L1Skills.TYPE_CHANGE) { // 버프계
    	  return false;
				}
			}
}
if(_calcType == PC_PC){
if(_targetPc.getMapId() == 777 ||_targetPc.getMapId() == 778||_targetPc.getMapId() == 779 ||_targetPc.getMapId() == 56){ //버땅욕망논피케이로변경
	if (skillId != L1Skills.TYPE_CHANGE) { // 버프계
  	  return false;
	}
}
}
if(_calcType == PC_PC){
if(_pc.getMapId() == 777 ||_pc.getMapId() == 778||_pc.getMapId() == 779 ||_pc.getMapId() == 56){ //버땅욕망논피케이로변경
	if (skillId != L1Skills.TYPE_CHANGE) { // 버프계
  	  return false;
	}
}
}
		// 아스바인드중은 WB, 왈가닥 세레이션 이외 무효
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)) {
				_skill = SkillsTable.getInstance().getTemplate(skillId);
				if (skillId != WEAPON_BREAK && skillId != CANCELLATION // 확률계
						&& _skill.getType() != L1Skills.TYPE_HEAL // 힐 계
						&& _skill.getType() != L1Skills.TYPE_CHANGE) { // 버프계
					return false;
				}
			}
		} else {
			if (_targetNpc.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)) {
				if (skillId != WEAPON_BREAK && skillId != CANCELLATION) {
					return false;
				}
			}
		}
		// 100% 확률을 가지는 스킬
		if (skillId == SMASH || skillId == MIND_BREAK || skillId == IllUSION_AVATAR) {
			return true;
		}
		probability = calcProbability(skillId);
		int rnd = 0;

		switch (skillId) {
		case DECAY_POTION:
		case SILENCE:
		case CURSE_PARALYZE:
		case CANCELLATION:
		case SLOW:
		case DARKNESS:
		case WEAKNESS:
		case CURSE_POISON:
		case CURSE_BLIND:
		case WEAPON_BREAK:
		case MANA_DRAIN:
			if (_calcType == PC_PC) {
				rnd = _random.nextInt(_targetPc.getResistance()
						.getEffectedMrBySkill()) + 1;
			} else if (_calcType == PC_NPC) {
				rnd = _random.nextInt(_targetNpc.getResistance()
						.getEffectedMrBySkill()) + 1;
			} else {
				rnd = _random.nextInt(100) + 1;
			}
			break;
		default:
			rnd = _random.nextInt(100) + 1;
			if (probability > 90)
				probability = 90;
			break;
		}
		
		if (probability + getMagicHitupByArmor() >= rnd) {
			isSuccess = true;
		} else {
			isSuccess = false;
		}

		if (!Config.ALT_ATKMSG) {
			return isSuccess;
		}
		if (Config.ALT_ATKMSG) {
			if ((_calcType == PC_PC || _calcType == PC_NPC) && !_pc.isGm()) {
				return isSuccess;
			}
			if ((_calcType == PC_PC || _calcType == NPC_PC)
					&& !_targetPc.isGm()) {
				return isSuccess;
			}
		}

		  String msg0 = "";
		 // String msg1 = " -> ";
		  String msg2 = "";
		  String msg3 = "";
		  String msg4 = "";

		  if (_calcType == PC_PC || _calcType == PC_NPC) {
		    
		   msg0 = _pc.getName();
		  } else if (_calcType == NPC_PC) { 
		   msg0 = _npc.getName();
		  }

		  msg2 = "확율:" + probability + "%";
		  if (_calcType == NPC_PC || _calcType == PC_PC) { 
		   msg4 = _targetPc.getName();
		  } else if (_calcType == PC_NPC) { 
		   msg4 = _targetNpc.getName();
		  }
		  if (isSuccess == true) {
		   msg3 = "성공";
		  } else {
		   msg3 = "실패";
		  }

		  if (_calcType == PC_PC || _calcType == PC_NPC) { 
		   _pc.sendPackets(new S_SystemMessage("\\fR["+msg0+"->"+msg4+"] "+msg2+" / "+msg3));
		  }
		  if (_calcType == NPC_PC || _calcType == PC_PC) { 
		   _targetPc.sendPackets(new S_SystemMessage("\\fY["+msg0+"->"+msg4+"] "+msg2+" / "+msg3));
		  }

		  return isSuccess;
		 }

	private boolean checkZone(int skillId) {
		if (_pc != null && _targetPc != null) {
			if (CharPosUtil.getZoneType(_pc) == 1
					|| CharPosUtil.getZoneType(_targetPc) == 1) {
				if (skillId == WEAPON_BREAK || skillId == SLOW
						|| skillId == CURSE_PARALYZE || skillId == MANA_DRAIN
						|| skillId == DARKNESS || skillId == WEAKNESS
						|| skillId == DISEASE || skillId == DECAY_POTION
						|| skillId == MASS_SLOW || skillId == ENTANGLE
						|| skillId == ERASE_MAGIC || skillId == EARTH_BIND
						|| skillId == AREA_OF_SILENCE
						|| skillId == WIND_SHACKLE || skillId == STRIKER_GALE
						|| skillId == SHOCK_STUN || skillId == FOG_OF_SLEEPING
						|| skillId == ICE_LANCE || skillId == FREEZING_BLIZZARD
						|| skillId == HORROR_OF_DEATH
						|| skillId == POLLUTE_WATER || skillId == FEAR
						|| skillId == ELEMENTAL_FALL_DOWN
						|| skillId == GUARD_BREAK
						|| skillId == RETURN_TO_NATURE || skillId == PHANTASM
						|| skillId == JOY_OF_PAIN || skillId == CONFUSION
						|| skillId == SILENCE) {
					return false;
				}
			}
		}
		return true;
	}

	private int calcProbability(int skillId) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		int attackLevel = 0;
		int defenseLevel = 0;
		int probability = 0;
		int attackInt = 0;
		int defenseMr = 0;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			attackLevel = _pc.getLevel();
			attackInt = _pc.getAbility().getTotalInt();
		} else {
			attackLevel = _npc.getLevel();
			attackInt = _npc.getAbility().getTotalInt();
		}

		if (_calcType == PC_PC || _calcType == NPC_PC) {
			defenseLevel = _targetPc.getLevel();
			defenseMr = _targetPc.getResistance().getEffectedMrBySkill();
		} else {
			defenseLevel = _targetNpc.getLevel();
			defenseMr = _targetNpc.getResistance().getEffectedMrBySkill();
			if (skillId == RETURN_TO_NATURE) {
				if (_targetNpc instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) _targetNpc;
					defenseLevel = summon.getMaster().getLevel();
				}
			}
		}

		switch (skillId) {
		case ERASE_MAGIC:// 이레
		case EARTH_BIND:// 어바
		case ARMOR_BRAKE:// 아머브레이크
		case STRIKER_GALE:// 게일
			int levelbonus = 0;
			probability = (int) (l1skills.getProbabilityValue() + (attackLevel - defenseLevel) * 2);
			if (attackLevel >= defenseLevel) {
				levelbonus = (attackLevel - defenseLevel) * 3;
			} else {
				levelbonus = -(defenseLevel - attackLevel) * 2;
			}
			probability += levelbonus;
			break;
		case ELEMENTAL_FALL_DOWN:
		case RETURN_TO_NATURE:
		case ENTANGLE:
		case AREA_OF_SILENCE:
		case WIND_SHACKLE:
		case POLLUTE_WATER:
			probability = (int) (30 + (attackLevel - defenseLevel) * 2);
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getBaseMagicHitUp();
			}
			break;
		case SHOCK_STUN:// 스턴확률
			probability = (int) (l1skills.getProbabilityValue() + (attackLevel - defenseLevel) * 2.5);
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2.5 * _pc.getBaseMagicHitUp();
			}
			break;
		case COUNTER_BARRIER:// 카운터배리어고정20%확률
			probability = (int) 20;
			break;
		case THUNDER_GRAB:
			probability = 60;
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getBaseMagicHitUp();
			}
			break;
		case PANIC:
			probability = (int) (((l1skills.getProbabilityDice()) / 10D) * (attackLevel - defenseLevel))
					+ l1skills.getProbabilityValue();
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getBaseMagicHitUp();
			}
			break;
		case CANCELLATION: // 켄슬본섭화
		case DECAY_POTION:// 디케이
		case ICE_LANCE: // 아이스
		case CURSE_PARALYZE:// 커스
			if (attackInt > 25)
				attackInt = 25;
			probability = (int) ((attackInt - (defenseMr / 5.74)) * 4);

			if (_pc.isElf() && (_calcType == PC_PC || _calcType == PC_NPC)) {
				probability -= 30;
			}
			if (probability < 0)
				probability = 0;
			break;
		case SILENCE:
		case SLOW:
		case DARKNESS:
		case WEAKNESS:
		case CURSE_POISON:
		case CURSE_BLIND:
		case WEAPON_BREAK:
			if (attackInt > 25)
				attackInt = 25;
			probability = (int) ((attackInt - (defenseMr / 5.75)) * l1skills
					.getProbabilityValue());
			if (_pc.isElf() && (_calcType == PC_PC || _calcType == PC_NPC)) {
				probability /= 2;
			}
			if (probability < 0)
				probability = 0;
			break;
		case MANA_DRAIN:
			if (attackInt > 25)
				attackInt = 25;
			probability = (int) (attackInt - (defenseMr / 5.7))
					* l1skills.getProbabilityValue();
			if (probability < 0)
				probability = 2;
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += _pc.getBaseMagicHitUp();
			}
			break;
		case TURN_UNDEAD:
			if (attackInt > 25)
				attackInt = 25;
			if (attackLevel > 52)
				attackLevel = 52; // 프리섭화를 위해 52로 변경(기본은 49임)
			probability = (int) ((attackInt * 3 + (attackLevel * 2.5) + _pc
					.getBaseMagicHitUp()) - (defenseMr + (defenseLevel / 2)) - 80);
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				if (!_pc.isWizard()) {
					probability -= 30;
				}
			}
			break; // 추가 턴언데드 본섭화 ^^
		case GUARD_BREAK:
		case FEAR:
		case HORROR_OF_DEATH:
			Random random = new Random();
			int dice = l1skills.getProbabilityDice();
			int value = l1skills.getProbabilityValue();
			int diceCount = 0;
			diceCount = getMagicBonus() + getMagicLevel();

			if (diceCount < 1) {
				diceCount = 1;
			}

			for (int i = 0; i < diceCount; i++) {
				probability += (random.nextInt(dice) + 1 + value);
			}

			probability = probability * getLeverage() / 10;

			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getBaseMagicHitUp();
			}

			if (probability >= getTargetMr()) {
				probability = 100;
			} else {
				probability = 0;
			}
			break;
		case CONFUSION:
		case BONE_BREAK:
		case PHANTASM:
			probability = 80;
			break;
		case JOY_OF_PAIN:
			probability = 80;
			break;
		default: {
			int dice1 = l1skills.getProbabilityDice();
			int diceCount1 = 0;
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				if (_pc.isWizard()) {
					diceCount1 = getMagicBonus() + getMagicLevel() + 1;
				} else if (_pc.isElf()) {
					diceCount1 = getMagicBonus() + getMagicLevel() - 1;
				} else if (_pc.isDragonknight()) {
					diceCount1 = getMagicBonus() + getMagicLevel();
				} else {
					diceCount1 = getMagicBonus() + getMagicLevel() - 1;
				}
			} else {
				diceCount1 = getMagicBonus() + getMagicLevel();
			}
			if (diceCount1 < 1) {
				diceCount1 = 1;
			}
			if (dice1 > 0) {
				for (int i = 0; i < diceCount1; i++) {
					probability += (_random.nextInt(dice1) + 1);
				}
			}
			probability = probability * getLeverage() / 10;
			probability -= getTargetMr();

			if (skillId == TAMING_MONSTER) {
				double probabilityRevision = 1;
				if ((_targetNpc.getMaxHp() * 1 / 4) > _targetNpc.getCurrentHp()) {
					probabilityRevision = 1.3;
				} else if ((_targetNpc.getMaxHp() * 2 / 4) > _targetNpc
						.getCurrentHp()) {
					probabilityRevision = 1.2;
				} else if ((_targetNpc.getMaxHp() * 3 / 4) > _targetNpc
						.getCurrentHp()) {
					probabilityRevision = 1.1;
				}
				probability *= probabilityRevision;
			}
		}
			break;
		}

		// / 내성에 따른 확률 감소 ///
		switch (skillId) {
		case EARTH_BIND:
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistance().getHold();
			}
			break;
		case SHOCK_STUN:
		case 30081:
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= 1 * _targetPc.getResistance().getStun();
			}
			break;
		case CURSE_PARALYZE:
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistance().getPetrifaction();
			}
			break;
		case FOG_OF_SLEEPING:
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistance().getSleep();
			}
			break;
		case ICE_LANCE:
		case FREEZING_BLIZZARD:
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistance().getFreeze();
			}
			break;
		case CURSE_BLIND:
		case DARKNESS:
		case DARK_BLIND:
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				probability -= _targetPc.getResistance().getBlind();
			}
			break;
		// / 내성에 따른 확률 감소 ///
		default:
			break;
		}
		return probability;
	}

	/* ■■■■■■■■■■■■■■ 마법 데미지 산출 ■■■■■■■■■■■■■■ */

	public int calcMagicDamage(int skillId) {
		int damage = 0;
		int rand = 0; // 랜덤데미지 변수
		int disd = 0; // 스펠파워에 따른 랜덤데미지 변수
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			damage = calcPcMagicDamage(skillId);
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			damage = calcNpcMagicDamage(skillId);
			// ////////혈원에게는 범위마법 데미지 0 디플추가////////////
			if (_calcType == PC_PC) {
				if (_pc.getClanid() > 0
						&& (_pc.getClanid() == _targetPc.getClanid())) {
					if (skillId == 17 || skillId == 22 || skillId == 25
							|| skillId == 53 || skillId == 59 || skillId == 62
							|| skillId == 65 || skillId == 70 || skillId == 74
							|| skillId == 80) { // 미티어 포함한
						// 범위마법들..
						damage = 0;
					}
					if (_calcType == PC_PC) {
						if (_targetPc.getLevel() <= Config.MAX_LEVEL
								|| _pc.getLevel() <= Config.MAX_LEVEL) { // 신규보호레벨55까지
							damage = 0;
						}
					}
					if (_calcType == PC_PC) {
						if (_targetPc.getMapId() == 777
								|| _targetPc.getMapId() == 778
										|| _targetPc.getMapId() == 56
								|| _targetPc.getMapId() == 779) { // 버땅욕망논피케이로변경
							damage = 0;
						}
					}
					if (_calcType == PC_PC) {
						if (_pc.getMapId() == 777 || _pc.getMapId() == 778 || _pc.getMapId() == 56
								|| _pc.getMapId() == 779) { // 버땅욕망논피케이로변경
							damage = 0;
						}
					}
					if (_calcType == PC_PC) {
						if (_pc.getProtect() == true
								&& _targetPc.getProtect() == false) { // 혈맹보호
							damage = 0;
						}
					}
				}
			}
			if (skillId == 46 || skillId == 34 || skillId == 45) {// 요정선버.이럽.콜라약화
				if (_pc.isElf() && (_calcType == PC_PC || _calcType == PC_NPC)) {
					damage /= 2;
				}
			}
			// ////////혈원에게는 범위마법 데미지 0 디플추가끝////////////
			// //////// 디스 최소 데미지 보완 ///////////
			if (_calcType == PC_PC) {
				// 상대방의 MR이 160보다 크며 현재 hp가 0보다 클시에 발동됩니다.
				if (_pc.getCurrentHp() > 0
						&& (_targetPc.getResistance().getMr() > 160)) {
					if (skillId == 77) {
						// 랜타감안 소스 추가
						disd = _pc.getAbility().getSp();
						if (disd > 58) {
							disd *= 3.2;
						} else {
							disd *= 1.8;
						}
						Random random5 = new Random();
						rand = random5.nextInt(50) + 1;
						damage = 330 + rand + disd;
						// 고정적 데미지이기 때문에 뮨인지 아닌지 재확인이 필요함
						if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(
								IMMUNE_TO_HARM)) {
							damage -= damage * 0.3;
						}
					}
				}
			}
			// //////// 디스 최소 데미지 보완 ///////////
		}
		/** 파번은 마방 공식 제외 (임시) */
		if (skillId == FINAL_BURN && _targetPc != null) { // final
			// burn's
			// temporary
			// damage
			if (_targetPc.getResistance().getEffectedMrBySkill() <= 50)
				damage = _pc.getCurrentMp()
						+ _random.nextInt(_pc.getCurrentMp() / 2 + 1);
			else if (_targetPc.getResistance().getEffectedMrBySkill() > 50
					&& _targetPc.getResistance().getEffectedMrBySkill() < 100)
				damage = _pc.getCurrentMp()
						- _random.nextInt(_pc.getCurrentMp() / 2 + 1);
			else if (_targetPc.getResistance().getEffectedMrBySkill() > 100)
				damage = _random.nextInt(_pc.getCurrentMp() / 2 + 1);
		}

		if (_calcType == PC_PC || _calcType == NPC_PC) {
			if (damage > _targetPc.getCurrentHp()) {
				damage = _targetPc.getCurrentHp();
			}
		} else {
			if (damage > _targetNpc.getCurrentHp()) {
				damage = _targetNpc.getCurrentHp();
			}
		}
		if (_calcType == PC_PC) {
			if (_targetPc.getRobotAi() != null && (_targetPc.noPlayerCK || _targetPc.isGm())) {
				if (_targetPc != null && _targetPc.getClanid() != 0 && !_targetPc.getMap().isSafetyZone(_targetPc.getLocation())) {
					if (_targetPc.getClanid() != _pc.getClanid()) {
						_targetPc.getRobotAi().getAttackList().add(_pc, 0);
					}
				} else if (!_targetPc.getMap().isSafetyZone(_targetPc.getLocation())) {
					if (_targetPc.getMap().isTeleportable()) {
						L1Location newLocation = _targetPc.getLocation().randomLocation(200, true);
						int newX = newLocation.getX();
						int newY = newLocation.getY();
						short mapId = (short) newLocation.getMapId();

						L1Teleport.teleport(_targetPc, newX, newY, mapId, _targetPc.getMoveState().getHeading(), true);
					}
				}
			}
		}


		return damage;
	}

	// ●●●● 플레이어에의 파이어월의 마법 데미지 산출 ●●●●
	public int calcPcFireWallDamage() {
		int dmg = 0;
		double attrDeffence = calcAttrResistance(L1Skills.ATTR_FIRE);
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(FIRE_WALL);
		dmg = (int) ((1.0 - attrDeffence) * l1skills.getDamageValue());

		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)
				|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
				|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
						FREEZING_BLIZZARD)
				|| _targetPc.getSkillEffectTimerSet()
						.hasSkillEffect(EARTH_BIND)
				|| _targetPc.getSkillEffectTimerSet()
						.hasSkillEffect(MOB_BASILL)
				|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(MOB_COCA)) {
			dmg = 0;
		}

		if (dmg < 0) {
			dmg = 0;
		}

		return dmg;
	}

	// ●●●● NPC 에의 파이어월의 마법 데미지 산출 ●●●●
	public int calcNpcFireWallDamage() {
		int dmg = 0;
		double attrDeffence = calcAttrResistance(L1Skills.ATTR_FIRE);
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(FIRE_WALL);
		dmg = (int) ((1.0 - attrDeffence) * l1skills.getDamageValue());

		if (_targetNpc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
				|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
						FREEZING_BLIZZARD)
				|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
						EARTH_BIND)
				|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
						MOB_BASILL)
				|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(MOB_COCA)) {
			dmg = 0;
		}

		if (dmg < 0) {
			dmg = 0;
		}

		return dmg;
	}

	// ●●●● 플레이어·NPC 로부터 플레이어에의 마법 데미지 산출 ●●●●
	private int calcPcMagicDamage(int skillId) {
		int dmg = 0;
		if (skillId == FINAL_BURN) {
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				dmg = _pc.getCurrentMp();
			} else {
				dmg = _npc.getCurrentMp();
			}
		} else {
			dmg = calcMagicDiceDamage(skillId);
			// if (_calcType == PC_PC) {
			// dmg = (dmg * getLeverage()) / 15; // 플레이어로 부터 받는 마법 데미지
			if (_calcType == NPC_PC) {
				dmg = (dmg * getLeverage()) / 15; // 몬스터로 부터 받는 마법 데미지
			}
		}
		dmg -= _targetPc.getDamageReductionByArmor();

		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(SPECIAL_COOKING)) { // 스페셜요리에
			// 의한
			// 데미지
			// 경감
			dmg -= 5;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(SPECIAL_COOKING2)) {
			// 새로운 요리들 의한 데미지 리덕션효과
			dmg -= 2;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(REDUCTION_ARMOR)) {
			int targetPcLvl = _targetPc.getLevel();
			if (targetPcLvl < 50) {
				targetPcLvl = 50;
			}
			dmg -= (targetPcLvl - 50) / 5 + 2;
		}

		if (_calcType == NPC_PC) {
			boolean isNowWar = false;
			int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
			if (castleId > 0) {
				isNowWar = WarTimeController.getInstance().isNowWar(castleId);
			}
			if (_npc instanceof L1MonsterInstance) {// 보스데미지
				if (_npc.getMaxHp() > 1000)
					dmg -= 0.8;
			}
			if (!isNowWar) {
				if (_npc instanceof L1PetInstance) {
					dmg /= 8;
				}
				if (_npc instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) _npc;
					if (summon.isExsistMaster()) {
						dmg /= 8;
					}
				}
			}
			// Object[] dollList = _targetPc.getDollList().values().toArray();
			// // 마법 인형에 의한 추가 방어
			// L1DollInstance doll = null;
			for (L1DollInstance doll : _targetPc.getDollList().values()) {
				// doll = (L1DollInstance) dollObject;
				dmg -= doll.getDamageReductionByDoll();
			}
		}
		/** 광선류 방어방패를 착용했다면 * */
		if (_targetPc.getInventory().checkEquipped(20230) || // 붉방
				_targetPc.getInventory().checkEquipped(20229)) {// 반사방패

			Random random = new Random(System.nanoTime());
			int rnd = random.nextInt(100) + 1;
			if (rnd < 4) {
				switch (skillId) {
				case ENERGY_BOLT:// 에볼
				case LIGHTNING:// 라이트닝
				case CALL_LIGHTNING:// 콜라
				case LIGHTNING_STORM:// 라톰
				case DISINTEGRATE:// 디스
				}
				dmg -= 100;
				_targetPc
						.sendPackets(new S_SystemMessage("방패로 잠시 마법을 보호받았습니다."));
			}
		}
		/** 광선류 방어방패를 착용했다면 * */
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(IllUSION_AVATAR)) {
			dmg += dmg / 5;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(PATIENCE)) {
			dmg -= 2;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(DRAGON_SKIN)) {
			dmg -= 2;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= 1.4;
		}
		if (_targetPc.getInventory().checkEquipped(420012)
				|| _targetPc.getInventory().checkEquipped(500042)) {// 쿠방.돌장
			dmg -= 1;
		}
		if (_targetPc.getInventory().checkEquipped(20117)) {// 바포갑옷
			dmg -= 2;
		}
		if (_targetPc.getInventory().checkEquipped(500040)) {// 반역자의방패
			dmg -= 3;
		}
		if (_calcType == PC_PC) {
			int chance = _random.nextInt(100);
			if (_targetPc.getInventory().checkEquipped(500040)) { // 반역자의방패
				if (chance <= 2) { // 확율 2
					dmg /= 2; // 데미지 감소 3
				}
			}
		}
		if (_calcType == PC_PC) {
			if (_targetPc.getLevel() <= Config.MAX_LEVEL
					|| _pc.getLevel() <= Config.MAX_LEVEL) { // 신규보호레벨55까지
				dmg = 0;
			}
		}
		if (_calcType == PC_PC) {
			if (_targetPc.getMapId() == 777 || _targetPc.getMapId() == 778 || _targetPc.getMapId() == 56
					|| _targetPc.getMapId() == 779) { // 버땅욕망논피케이로변경
				dmg = 0;
			}
		}
		if (_calcType == PC_PC) {
			if (_pc.getMapId() == 777 || _pc.getMapId() == 778 || _pc.getMapId() == 56
					|| _pc.getMapId() == 779) { // 버땅욕망논피케이로변경
				dmg = 0;
			}
		}
		// [마안에의한 마법뎀감]수룡.생명.형상.탄생
		if (_targetPc != null) {
			if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(FAFU_MAAN)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							LIFE_MAAN)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							SHAPE_MAAN)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							BIRTH_MAAN)) {
				int chance = _random.nextInt(100) + 1;
				if (chance <= 20) {
					dmg /= 1.5;
				}
			}
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(MOB_BASILL)
				|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(MOB_COCA)
				|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
				|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
						FREEZING_BLIZZARD)
				|| _targetPc.getSkillEffectTimerSet()
						.hasSkillEffect(EARTH_BIND)) {
			dmg = 0;
		}
		if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(COUNTER_MIRROR)) {
			if (_calcType == PC_PC) {
				if (_targetPc.getAbility().getTotalWis() >= _random
						.nextInt(100)) {
					_pc.sendPackets(new S_DoActionGFX(_pc.getId(),
							ActionCodes.ACTION_Damage));
					Broadcaster.broadcastPacket(_pc,
							new S_DoActionGFX(_pc.getId(),
									ActionCodes.ACTION_Damage));
					_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(),
							4395));
					Broadcaster.broadcastPacket(_targetPc, new S_SkillSound(
							_targetPc.getId(), 4395));
					dmg = dmg / 2;
					_pc.receiveDamage(_targetPc, dmg, false);
					dmg = 0;
					_targetPc.getSkillEffectTimerSet().killSkillEffectTimer(
							COUNTER_MIRROR);
				}
			} else if (_calcType == NPC_PC) {
				int npcId = _npc.getNpcTemplate().get_npcId();
				if (npcId == 45681 || npcId == 45682 || npcId == 45683
						|| npcId == 45684) {
				} else if (!_npc.getNpcTemplate().get_IsErase()) {
				} else {
					if (_targetPc.getAbility().getTotalWis() >= _random
							.nextInt(100)) {
						Broadcaster.broadcastPacket(_npc, new S_DoActionGFX(
								_npc.getId(), ActionCodes.ACTION_Damage));
						_targetPc.sendPackets(new S_SkillSound(_targetPc
								.getId(), 4395));
						Broadcaster.broadcastPacket(_targetPc,
								new S_SkillSound(_targetPc.getId(), 4395));
						_npc.receiveDamage(_targetPc, dmg);
						dmg = 0;
						_targetPc.getSkillEffectTimerSet()
								.killSkillEffectTimer(COUNTER_MIRROR);
					}
				}
			}
		}

		if (dmg < 0) {
			dmg = 0;
		}

		return dmg;
	}

	// ●●●● 플레이어·NPC 로부터 NPC 에의 데미지 산출 ●●●●
	private int calcNpcMagicDamage(int skillId) {
		int dmg = 0;
		if (skillId == FINAL_BURN) {
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				dmg = _pc.getCurrentMp();
			} else {
				dmg = _npc.getCurrentMp();
			}
		} else {
			dmg = calcMagicDiceDamage(skillId);
			dmg = (dmg * getLeverage()) / 10;
		}
		if (_targetNpc.getNpcId() == 45640) {
			dmg /= 2;
		}
		if (_calcType == PC_NPC) {
			boolean isNowWar = false;
			int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
			if (castleId > 0) {
				isNowWar = WarTimeController.getInstance().isNowWar(castleId);
			}
			if (!isNowWar) {
				if (_targetNpc instanceof L1PetInstance) {
					dmg /= 8;
				}
				if (_targetNpc instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) _targetNpc;
					if (summon.isExsistMaster()) {
						dmg /= 8;
					}
				}
			}
		}

		if (_calcType == PC_NPC && _targetNpc != null) {
			int npcId = _targetNpc.getNpcTemplate().get_npcId();
			if (npcId >= 45912
					&& npcId <= 45915
					&& !_pc.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_HOLY_WATER)) {
				dmg = 0;
			}
			if (npcId == 45916
					&& !_pc.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_HOLY_MITHRIL_POWDER)) {
				dmg = 0;
			}
			if (npcId == 45941
					&& !_pc.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_HOLY_WATER_OF_EVA)) {
				dmg = 0;
			}
			if (!_pc.getSkillEffectTimerSet().hasSkillEffect(
					STATUS_CURSE_BARLOG)
					&& (npcId == 45752 || npcId == 45753 || npcId == 7000007
							|| npcId == 7000008 || npcId == 7000009
							|| npcId == 70000010 || npcId == 7000011
							|| npcId == 7000012 || npcId == 7000013
							|| npcId == 7000014 || npcId == 7000015 || npcId == 7000016)) {
				dmg = 0;
			}
			// //////////////////////////////짭퉁캐릭//////////////////////////////
			if (!_pc.getSkillEffectTimerSet()
					.hasSkillEffect(STATUS_CURSE_YAHEE)
					&& (npcId == 45675 || npcId == 81082 || npcId == 45625
							|| npcId == 45674 || npcId == 45685)) {
				dmg = 0;
			}
			if (npcId >= 46068 && npcId <= 46091
					&& _pc.getGfxId().getTempCharGfx() == 6035) {
				dmg = 0;
			}
			if (npcId >= 46092 && npcId <= 46106
					&& _pc.getGfxId().getTempCharGfx() == 6034) {
				dmg = 0;
			}
			if (npcId == 4039001
					&& !_pc.getSkillEffectTimerSet().hasSkillEffect(
							PAP_FIVEPEARLBUFF)) {
				dmg = 0;
			}// 오색진주
			if (npcId == 4039002
					&& !_pc.getSkillEffectTimerSet().hasSkillEffect(
							PAP_MAGICALPEARLBUFF)) {
				dmg = 0;
			}// 오색진주
		}
		return dmg;
	}

	// ●●●● damage_dice, damage_dice_count, damage_value, SP로부터 마법 데미지를 산출 ●●●●
	private int calcMagicDiceDamage(int skillId) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		int dice = l1skills.getDamageDice();
		int diceCount = l1skills.getDamageDiceCount();
		int value = l1skills.getDamageValue();
		int magicDamage = 0;
		double PowerMr = 0; // 마방
		double coefficient = 0; // PC마법상수

		Random random = new Random();
		for (int i = 0; i < diceCount; i++) {
			magicDamage += (_random.nextInt(dice) + 1);
		}
		magicDamage += value;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int PowerSp = _pc.getAbility().getSp() - getMagicLevel(); // 총SP에서
																		// 매직보너스뺀
																		// 나머지SP구하기
																		// +1은
																		// 수식에서
																		// 바로
																		// 넣었습니다.

			int PowerInt = _pc.getAbility().getTotalInt() - getMagicBonus(); // SP가
																				// 오르지않는
																				// INT
																				// 구하기
																				// -9는
																				// 수식에서
																				// 바로
																				// 넣었습니다.
			coefficient = (1.0 + (PowerSp + 1) * 0.15 + (PowerInt - 9) * 0.2); // PC마법
																				// 상수
																				// 구하기
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			int spByItem = _npc.getAbility().getSp()
					- _npc.getAbility().getTrueSp();
			int charaIntelligence = _npc.getAbility().getTotalInt();
			coefficient = (1.0 + (charaIntelligence - 8) * 0.2 + spByItem * 0.15); // NPC마법
																					// 상수
																					// 구하기
		}

		if (coefficient < 1) {
			coefficient = 1;
		}
		magicDamage *= coefficient; // 기본 마법데미지에 마법상수 곱하기
		/** 치명타 발생 부분 추가 - By 시니 - */
		double criticalCoefficient = 1.4;
		int rnd = random.nextInt(100) + 1;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			switch (skillId) { // 6레벨 이하 광역마법 제외한 공격마법
			case ENERGY_BOLT:
			case ICE_DAGGER:
			case WIND_CUTTER:
			case CHILL_TOUCH:
			case SMASH:
			case FIRE_ARROW:
			case STALAC:
			case VAMPIRIC_TOUCH:
			case CONE_OF_COLD:
			case CALL_LIGHTNING:
				int propCritical = CalcStat.calcBaseMagicCritical(
						_pc.getType(), _pc.ability.getBaseInt()) + 10; // 12치명타
																		// 기본12
																		// + 베이스
																		// 스탯
																		// 보너스
				if (_calcType == PC_PC || _calcType == PC_NPC) {
					if (_pc.getSkillEffectTimerSet().hasSkillEffect(LIND_MAAN) // 풍룡의
																				// 마안
																				// -
																				// 일정확률로
																				// 마법치명타+1
							|| _pc.getSkillEffectTimerSet().hasSkillEffect(
									SHAPE_MAAN) // 형상의 마안 - 일정확률로 마법치명타+1
							|| _pc.getSkillEffectTimerSet().hasSkillEffect(
									LIFE_MAAN)) { // 생명의 마안 - 일정확률로 마법치명타+1
						propCritical += 1; // 마안에 의한 마법치명타 +1
					}
				}
				if (criticalOccur(propCritical)) {
					magicDamage *= 1.5;
				}
				break;
			}
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			if (rnd <= 15) {
				magicDamage *= criticalCoefficient;
			}
		}
		if (getTargetMr() < 101) {
			PowerMr = getTargetMr() / 200; // 마방100되면 10당 (기본데미지*마법상수)의 5% 데미지
											// 줄어들게 설정 총50%
		} else {
			PowerMr = 0.5 + (getTargetMr() - 100) / 1000; // 마방100초과분에 대해 10당
															// (기본데미지*마법상수)의 1%
															// 줄어들게 설정 100당 10%
		} // 마방 600되면 마법데미지 0
		if (skillId == FINAL_BURN) {
			PowerMr = 0;
		}
		magicDamage -= magicDamage * PowerMr; // 먼저 마방에 의한 데미지 감소부터 처리
		double attrDeffence = calcAttrResistance(l1skills.getAttr()); // 속성방어
																		// 100당
																		// 45%줄어듬.
																		// 10당
																		// 4.5%
																		// 초과분에
																		// 대해서
																		// 10당
																		// 0.9%
																		// 줄어들게
																		// 설정
		magicDamage -= magicDamage * attrDeffence; // 마방에 의한 데미지 감소후 속성방어에 의한
													// 데미지 감소 처리
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			magicDamage += _pc.getBaseMagicDmg(); // 베이스 스탯 마법 데미지 보너스 추가
		}
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int weaponAddDmg = 0;
			L1ItemInstance weapon = _pc.getWeapon();
			if (weapon != null) {
				weaponAddDmg = weapon.getItem().getMagicDmgModifier();
			}
			magicDamage += weaponAddDmg; // 무기에 의한 마법 데미지 추가
		}
		return magicDamage;
	}

	// ●●●● 힐 회복량본섭화(대안 데드에는 데미지)을 산출 ●●●●
	public int calcHealing(int skillId) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		int dice = l1skills.getDamageDice();
		int diceCount = l1skills.getDamageDiceCount();
		int value = l1skills.getDamageValue();
		int magicDamage = 0;
		double PowerHeal = 0;
		for (int i = 0; i < diceCount; i++) {
			magicDamage += (_random.nextInt(dice) + 1);
		}
		magicDamage += value;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int magicBonus = getMagicBonus();
			int PowerInt = _pc.getAbility().getTotalInt() - getMagicBonus(); // SP가
																				// 오르지않는
																				// INT
																				// 구하기
			PowerHeal = magicBonus + 1 + (PowerInt * 0.1); // PC마법 상수 구하기
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			int charaIntelligence = _npc.getAbility().getTotalInt();
			PowerHeal = charaIntelligence / 2; // NPC마법 상수 구하기
		}
		magicDamage *= (1 + PowerHeal); // 마법상수만큼 곱하기
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			if (getLawful() > 0) {
				magicDamage *= 1 + (getLawful() / 32768.0);
			}
		}
		magicDamage /= 4; // 애초에 value dice를 4배한것을 원값으로 돌림.
		return magicDamage;
	}

	/**
	 * MR에 의한 마법 데미지 감소를 처리 한다
	 * 
	 * @param dmg
	 * @return dmg
	 */
	// ●●●● MR에 의한 데미지 경감 ●●●●
	public int calcMrDefense(int dmg) {
		double PowerMr = 0;
		if (getTargetMr() < 101) {
			PowerMr = getTargetMr() / 200; // 마방100되면 10당 (기본데미지*마법상수)의 5% 데미지
											// 줄어들게 설정 총50%
		} else {
			PowerMr = 0.5 + (getTargetMr() - 100) / 1000; // 마방100초과분에 대해 10당
															// (기본데미지*마법상수)의 1%
															// 줄어들게 설정 100당 10%
		} // 마방 600되면 마법데미지 0
		dmg -= dmg * PowerMr;
		return dmg;
	}

	private boolean criticalOccur(int prop) {
		boolean ok = false;
		int num = _random.nextInt(100) + 1;

		if (prop == 0) {
			return false;
		}
		if (num <= prop) {
			ok = true;
		}
		return ok;
	}

	private double calcAttrResistance(int attr) {
		int resist = 0;
		int resistFloor = 0;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			switch (attr) {
			case L1Skills.ATTR_EARTH:
				resist = _targetPc.getResistance().getEarth();
				break;
			case L1Skills.ATTR_FIRE:
				resist = _targetPc.getResistance().getFire();
				break;
			case L1Skills.ATTR_WATER:
				resist = _targetPc.getResistance().getWater();
				break;
			case L1Skills.ATTR_WIND:
				resist = _targetPc.getResistance().getWind();
				break;
			}
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
		}
		if (resist < 0) {
			resistFloor = (int) (-0.45 * Math.abs(resist));
		} else if (resist < 101) {
			resistFloor = (int) (0.45 * Math.abs(resist));
		} else {
			resistFloor = (int) (45 + 0.09 * Math.abs(resist)); // 속성100초과분에 대해
																// 0.45의 1/5정도
																// 감소되게 변경
		}
		double attrDeffence = resistFloor / 100;
		return attrDeffence;
	}

	public void commit(int damage, int drainMana) {
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			commitPc(damage, drainMana);
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			commitNpc(damage, drainMana);
		}

		if (!Config.ALT_ATKMSG) {
			return;
		}
		if (Config.ALT_ATKMSG) {
			if ((_calcType == PC_PC || _calcType == PC_NPC) && !_pc.isGm()) {
				return;
			}
			if ((_calcType == PC_PC || _calcType == NPC_PC)
					&& !_targetPc.isGm()) {
				return;
			}
		}
		String msg0 = "";
	//	String msg1 = " -> ";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			msg0 = _pc.getName();
		} else if (_calcType == NPC_PC) {
			msg0 = _npc.getName();
		}

		if (_calcType == NPC_PC || _calcType == PC_PC) {
			msg4 = _targetPc.getName();
			msg2 = "HP:" + _targetPc.getCurrentHp();
		} else if (_calcType == PC_NPC) {
			msg4 = _targetNpc.getName();
			msg2 = "HP:" + _targetNpc.getCurrentHp();
		}

		msg3 = "DMG:" + damage;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			_pc.sendPackets(new S_SystemMessage("\\fR[" + msg0 + "->" + msg4
					+ "] " + msg3 + " / " + msg2));
		}
		if (_calcType == NPC_PC || _calcType == PC_PC) {
			_targetPc.sendPackets(new S_SystemMessage("\\fY[" + msg0 + "->"
					+ msg4 + "] " + msg3 + " / " + msg2));
		}
	}

	private void commitPc(int damage, int drainMana) {
		if (_calcType == PC_PC) {
			if (_targetPc.getSkillEffectTimerSet().hasSkillEffect(
					ABSOLUTE_BARRIER)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							ICE_LANCE)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							FREEZING_BLIZZARD)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							EARTH_BIND)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							MOB_BASILL)
					|| _targetPc.getSkillEffectTimerSet().hasSkillEffect(
							MOB_COCA)) {
				damage = 0;
				drainMana = 0;
			}
			if (drainMana > 0 && _targetPc.getCurrentMp() > 0) {
				if (drainMana > _targetPc.getCurrentMp()) {
					drainMana = _targetPc.getCurrentMp();
				}
				int newMp = _pc.getCurrentMp() + drainMana;
				_pc.setCurrentMp(newMp);
			}
			_targetPc.receiveManaDamage(_pc, drainMana);
			_targetPc.receiveDamage(_pc, damage, true);
		} else if (_calcType == NPC_PC) {
			_targetPc.receiveDamage(_npc, damage, true);
		}
	}

	private void commitNpc(int damage, int drainMana) {
		if (_calcType == PC_NPC) {
			int npcId = _targetNpc.getNpcTemplate().get_npcId();
			if (npcId == 4039001
					&& _pc.getSkillEffectTimerSet().hasSkillEffect(
							PAP_FIVEPEARLBUFF)) {
				damage = 1;
			}// 오색진주
			if (npcId == 4039002
					&& _pc.getSkillEffectTimerSet().hasSkillEffect(
							PAP_MAGICALPEARLBUFF)) {
				damage = 1;
			}// 오색진주
			if (_targetNpc.getNpcTemplate().get_gfxid() == 7720) {
				damage = 1;
			} // 토르나르
			if (_targetNpc.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
							FREEZING_BLIZZARD)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
							EARTH_BIND)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
							MOB_BASILL)
					|| _targetNpc.getSkillEffectTimerSet().hasSkillEffect(
							MOB_COCA)) {
				damage = 0;
				drainMana = 0;
			}
			/** 마법데미지체크 **/
			if (_pc.isGm()) {
				if (npcId == 45001 || npcId == 45002) {
					_pc.sendPackets(new S_SystemMessage("\\fC마법데미지:[" + damage
							+ "]입니다."));
					return;
				}
			}
			/** 마법데미지체크 **/
			if (drainMana > 0) {
				int drainValue = _targetNpc.drainMana(drainMana);
				int newMp = _pc.getCurrentMp() + drainValue;
				_pc.setCurrentMp(newMp);
			}
			_targetNpc.ReceiveManaDamage(_pc, drainMana);
			_targetNpc.receiveDamage(_pc, damage);
		} else if (_calcType == NPC_NPC) {
			_targetNpc.receiveDamage(_npc, damage);
		}
	}
}
