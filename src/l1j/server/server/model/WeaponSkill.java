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

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.Random;

import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.templates.L1Skills;

// Referenced classes of package l1j.server.server.model:
// L1PcInstance

public class WeaponSkill {

	private static Random _random = new Random(System.nanoTime());
	
	public static void get천사지팡이Weapon(L1PcInstance pc, L1Character cha,int enchant) {//천사지팡이
		int chance = _random.nextInt(100) + 1;
		if (2 + (enchant * 1) >= chance) {
			new L1SkillUse().handleCommands(pc, TURN_UNDEAD, cha.getId(), cha
					.getX(), cha.getY(), null, 18, L1SkillUse.TYPE_GMBUFF);
		}
	}
	
	public static double get공명Damage(L1PcInstance pc, L1Character cha) {//공명의키링크
		  double dmg = 0;
		  int chance = _random.nextInt(100) + 1;
		  if (10 >= chance) {
		  dmg = 25;//고정데미지
		  pc.sendPackets(new S_SkillSound(cha.getId(), 5201));
		  Broadcaster.broadcastPacket(pc, new S_SkillSound(cha.getId(), 5201));
		  }
		  return dmg;
		  }

	
public static double get광풍도끼Damage(L1PcInstance pc, L1Character cha,int enchant) {//광풍
	double dmg = 0;
	int chance = _random.nextInt(100) + 1;
	if (5 + (enchant * 1) >= chance) {
		L1Magic magic = new L1Magic(pc, cha);
		dmg = magic.calcMagicDamage(L1SkillId.weapon_C);
		if (dmg <= 0) {
			dmg = 0;
		}
		pc.sendPackets(new S_SkillSound(cha.getId(), 5524));
		Broadcaster
				.broadcastPacket(pc, new S_SkillSound(cha.getId(), 5524));
		magic = null;
	}
	return dmg;
}
public static double get뇌신검Damage(L1PcInstance pc, L1Character cha,int enchant) {//뇌신검
	double dmg = 0;
	int chance = _random.nextInt(100) + 1;
	if (5 + (enchant * 1) >= chance) {
		L1Magic magic = new L1Magic(pc, cha);
		dmg = magic.calcMagicDamage(L1SkillId.weapon_A);
		if (dmg <= 0) {
			dmg = 0;
		}
		pc.sendPackets(new S_SkillSound(cha.getId(), 3940));
		Broadcaster
				.broadcastPacket(pc, new S_SkillSound(cha.getId(), 3940));
		magic = null;
	}
	return dmg;
}
public static double get살천의활Damage(L1PcInstance pc, L1Character cha,int enchant) {//살천
	double dmg = 0;
	int chance = _random.nextInt(100) + 1;
	if (3 + (enchant * 1) >= chance) {
		L1Magic magic = new L1Magic(pc, cha);
		dmg = magic.calcMagicDamage(L1SkillId.weapon_B);
		if (dmg <= 0) {
			dmg = 0;
		}
		pc.sendPackets(new S_SkillSound(cha.getId(), 9361));
		Broadcaster
				.broadcastPacket(pc, new S_SkillSound(cha.getId(), 9361));
		magic = null;
	}
	return dmg;
}
public static double get혹한창Damage(L1PcInstance pc, L1Character cha,int enchant) {//혹한
	double dmg = 0;
	int chance = _random.nextInt(100) + 1;
	if (5 + (enchant * 1) >= chance) {
		L1Magic magic = new L1Magic(pc, cha);
		dmg = magic.calcMagicDamage(L1SkillId.weapon_D);
		if (dmg <= 0) {
			dmg = 0;
		}
		pc.sendPackets(new S_SkillSound(cha.getId(), 3704));
		Broadcaster
				.broadcastPacket(pc, new S_SkillSound(cha.getId(), 3704));
		magic = null;
	}
	return dmg;
}
/** 파괴의 이도류 , 파괴의크로우 */
public static double get파괴의이도류Damage(L1PcInstance pc, L1Character cha,int enchant) {//파괴의이도류,파괴의크로우
	double dmg = 0;
	int chance = _random.nextInt(100) + 1;
	if (5 + (enchant * 1) >= chance) {
		L1Magic magic = new L1Magic(pc, cha);
		dmg = magic.calcMagicDamage(L1SkillId.weapon_E);
		if (dmg <= 0) {
			dmg = 0;
		}
		pc.sendPackets(new S_SkillSound(cha.getId(), 4858));
		Broadcaster
				.broadcastPacket(pc, new S_SkillSound(cha.getId(), 4858));
		magic = null;
	}
	return dmg;
}
/** 파괴의 이도류 , 파괴의크로우 */

	public static double getBarlogSwordDamage(L1PcInstance pc, L1Character cha) {
		double dmg = 0;
		int chance = _random.nextInt(100) + 1;
		if (15 >= chance) {

			L1Magic magic = new L1Magic(pc, cha);

			dmg = magic.calcMagicDamage(L1SkillId.METEOR_STRIKE);
			//dmg = magic.calcMrDefense((int) dmg);
			if (dmg <= 0) {
				dmg = 0;
			}
			pc.sendPackets(new S_SkillSound(cha.getId(), 762));
			Broadcaster.broadcastPacket(pc, new S_SkillSound(cha.getId(), 762));
			L1PcInstance targetPc = null;
			L1NpcInstance targetNpc = null;
			L1PcInstance _pc = null;
			if (cha instanceof L1PcInstance) {
				_pc = (L1PcInstance) cha;
			}
			for (L1Object object : L1World.getInstance().getVisibleObjects(cha,
					2)) {
				if (object == null) {
					continue;
				}
				if (!(object instanceof L1Character)) {
					continue;
				}
				if (object.getId() == pc.getId()
						|| object.getId() == cha.getId()) {
					continue;
				}
				if (object instanceof L1PcInstance) {
					targetPc = (L1PcInstance) object;
					if (CharPosUtil.getZoneType(targetPc) == 1) {
						continue;
					}
				}
				if (cha instanceof L1MonsterInstance) {
					if (!(object instanceof L1MonsterInstance)) {
						continue;
					}
				}
				if (cha instanceof L1PcInstance
						|| cha instanceof L1SummonInstance
						|| cha instanceof L1PetInstance) {
					if (!(object instanceof L1PcInstance
							|| object instanceof L1SummonInstance
							|| object instanceof L1PetInstance || object instanceof L1MonsterInstance)) {
						continue;
					}
					if (cha instanceof L1PcInstance) {
						if (_pc.getClanid() > 0) {
							if (pc.getClanid() != _pc.getClanid()) {
								if (pc.getClanid() == targetPc.getClanid())
									continue;
							}
						} else {
							if (pc.getClanid() == targetPc.getClanid())
								continue;
						}
					}
				}
				// dmg = calcDamageReduction((L1Character) object, dmg,
				// L1Skills.ATTR_FIRE);
				if (dmg <= 0) {
					continue;
				}
				if (object instanceof L1PcInstance) {
					targetPc = (L1PcInstance) object;
					targetPc.sendPackets(new S_DoActionGFX(targetPc.getId(),
							ActionCodes.ACTION_Damage));
					Broadcaster.broadcastPacket(targetPc, new S_DoActionGFX(
							targetPc.getId(), ActionCodes.ACTION_Damage));
					targetPc.receiveDamage(pc, (int) dmg, false);
				} else if (object instanceof L1SummonInstance
						|| object instanceof L1PetInstance
						|| object instanceof L1MonsterInstance) {
					targetNpc = (L1NpcInstance) object;
					Broadcaster.broadcastPacket(targetNpc, new S_DoActionGFX(
							targetNpc.getId(), ActionCodes.ACTION_Damage));
					targetNpc.receiveDamage(pc, (int) dmg);
				}
			}
			magic = null;
		}
		return dmg;
	}

	public static void getSilenceSword(L1PcInstance pc, L1Character cha) {
		int chance = _random.nextInt(100) + 1;
		if (2 >= chance) {
			new L1SkillUse().handleCommands(pc, SILENCE, cha.getId(), cha
					.getX(), cha.getY(), null, 16, L1SkillUse.TYPE_GMBUFF);
		}
	}
	public static void gettjdrhdghkf(L1PcInstance pc, L1Character cha) {//할로윈이벤트2011
		int chance = _random.nextInt(100) + 1;
		if (5 >= chance) {
			new L1SkillUse().handleCommands(pc, P_WIND_SHACKLE, cha.getId(), cha
					.getX(), cha.getY(), null, 4, L1SkillUse.TYPE_GMBUFF);
		} 
	}
	public static void getPoisonSword(L1PcInstance pc, L1Character cha) {
		int chance = _random.nextInt(100) + 1;
		if (10 >= chance) {
			L1DamagePoison.doInfection(pc, cha, 3000, 10);
		}
	}

	public static void getDiseaseWeapon(L1PcInstance pc, L1Character cha,
			int weaponid) {
		int chance = _random.nextInt(100) + 1;
		int skilltime = weaponid == 412003 ? 64 : 20;
		if (7 >= chance) {
			if (!cha.getSkillEffectTimerSet().hasSkillEffect(56)) {
				cha.addDmgup(-6);
				cha.getAC().addAc(12);
				if (cha instanceof L1PcInstance) {
					L1PcInstance target = (L1PcInstance) cha;
					target.sendPackets(new S_OwnCharAttrDef(target));
				}
			}
			cha.getSkillEffectTimerSet().setSkillEffect(56, skilltime * 1000);
			pc.sendPackets(new S_SkillSound(cha.getId(), 2230));
			Broadcaster
					.broadcastPacket(pc, new S_SkillSound(cha.getId(), 2230));
		}
	}

	public static double getRondeDamage(L1PcInstance pc, L1Character cha) {
		double dmg = 0;
		int chance = _random.nextInt(100) + 1;
		if (15 >= chance) {

			L1Magic magic = new L1Magic(pc, cha);

			dmg = magic.calcMagicDamage(L1SkillId.EARTH_JAIL);
			// dmg = magic.calcMrDefense((int)dmg);
			if (dmg <= 0) {
				dmg = 0;
			}

			pc.sendPackets(new S_SkillSound(cha.getId(), 1805));
			Broadcaster
					.broadcastPacket(pc, new S_SkillSound(cha.getId(), 1805));
			magic = null;
		}
		return dmg;
	}

	public static double getDeathKnightSwordDamage(L1PcInstance pc,
			L1Character cha) {//데스불검
		double dmg = 0;
		int chance = _random.nextInt(100) + 1;
		if (6 >= chance) {
			//L1Magic magic = new L1Magic(pc, cha);
			dmg += 20+_random.nextInt(10)+1;
			//dmg = magic.calcMrDefense((int) dmg);
			if (dmg <= 0) {
				dmg = 0;
			}
			pc.sendPackets(new S_SkillSound(cha.getId(), 7300));
			Broadcaster.broadcastPacket(pc, new S_SkillSound(cha.getId(), 7300));
			//magic = null;
		}
		return dmg;
	}

	public static double getKurtSwordDamage(L1PcInstance pc, L1Character cha) {//커츠검
		double dmg = 0;
		int chance = _random.nextInt(100) + 1;
		if (6 >= chance) {
			L1Magic magic = new L1Magic(pc, cha);

			dmg = magic.calcMagicDamage(L1SkillId.CALL_LIGHTNING);
			//dmg = magic.calcMrDefense((int) dmg);
			if (dmg <= 0) {
				dmg = 0;
			}
			pc.sendPackets(new S_SkillSound(cha.getId(), 10405));
			Broadcaster.broadcastPacket(pc, new S_SkillSound(cha.getId(), 10405));
			magic = null;
		}
		return dmg;
	}
	// /////////
	public static double halloweenCus(L1PcInstance pc, L1Character cha) {// 호지
		double dmg = 0;
		int chance = _random.nextInt(100) + 1;
		if (7 >= chance) {// 조정
			int sp = pc.getAbility().getSp();
			int intel = pc.getAbility().getTotalInt();
			double bsk = 0;
			if (pc.getSkillEffectTimerSet().hasSkillEffect(BERSERKERS)) {
				bsk = 0.1;
			}
			dmg = (intel + sp) * (1 + bsk) + _random.nextInt(intel + sp) * 0.1;
			pc.sendPackets(new S_SkillSound(cha.getId(), 7849));
			Broadcaster
					.broadcastPacket(pc, new S_SkillSound(cha.getId(), 7849));
		}
		return calcDamageReduction(cha, dmg, L1Skills.ATTR_RAY);
	}

	// ////////

	public static double getEffectSwordDamage(L1PcInstance pc, L1Character cha,
			int effectid) {
		double dmg = 0;
		int chance = _random.nextInt(100) + 1;
		if (8 >= chance) {
			L1Magic magic = new L1Magic(pc, cha);

			dmg = magic.calcMagicDamage(L1SkillId.CALL_LIGHTNING);
			//dmg = magic.calcMrDefense((int) dmg);
			if (dmg <= 0) {
				dmg = 0;
			}
			pc.sendPackets(new S_SkillSound(cha.getId(), effectid));
			Broadcaster.broadcastPacket(pc, new S_SkillSound(cha.getId(),
					effectid));
			magic = null;
		}
		return dmg;
	}

	public static double getIceQueenStaffDamage(L1PcInstance pc, L1Character cha,int enchant) {//얼지
		double dmg = 0;
		int chance = _random.nextInt(100) + 1;
		if (5 + (enchant * 2) >= chance) {
			L1Magic magic = new L1Magic(pc, cha);
			dmg = magic.calcMagicDamage(L1SkillId.CONE_OF_COLD);
			if (dmg <= 0) {
				dmg = 0;
			}
			pc.sendPackets(new S_SkillSound(cha.getId(), 1810));
			Broadcaster
					.broadcastPacket(pc, new S_SkillSound(cha.getId(), 1810));
			magic = null;
		}
		return dmg;
	}
	public static double getMoonBowDamage(L1PcInstance pc, L1Character cha,
			int enchant) {// 달장데미지
		double dmg = 0;
		int chance = _random.nextInt(100) + 1;
		if (15 >= chance) {
			dmg = 0.8 + _random.nextInt(enchant);
			S_UseAttackSkill packet = new S_UseAttackSkill(pc, cha.getId(),
					6288, cha.getX(), cha.getY(), ActionCodes.ACTION_Attack,
					false);
			pc.sendPackets(packet);
			Broadcaster.broadcastPacket(pc, packet);
		}
		return dmg;
	}

	public static double getBaphometStaffDamage(L1PcInstance pc, L1Character cha) {// 바지
		double dmg = 0;
		int chance = _random.nextInt(100) + 1;
		if (20 >= chance) {
			int locx = cha.getX();
			int locy = cha.getY();
			L1Magic magic = new L1Magic(pc, cha);

			dmg = magic.calcMagicDamage(L1SkillId.ERUPTION);//어렵
			//dmg = magic.calcMrDefense((int) dmg);
			dmg *= 1.0;
			if (dmg <= 0) {
				dmg = 0;
			}
			S_EffectLocation packet = new S_EffectLocation(locx, locy, 129);
			pc.sendPackets(packet);
			Broadcaster.broadcastPacket(pc, packet);
			magic = null;
		}
		return dmg;
	}

	public static double getDiceDaggerDamage(L1PcInstance pc,
			L1PcInstance targetPc, L1ItemInstance weapon) {
		double dmg = 0;
		int chance = _random.nextInt(100) + 1;
		if (3 >= chance) {
			dmg = targetPc.getCurrentHp() * 1 / 2;
			if (targetPc.getCurrentHp() - dmg < 0) {
				dmg = 0;
			}
			String msg = weapon.getLogName();
			pc.sendPackets(new S_ServerMessage(158, msg));
			pc.getInventory().removeItem(weapon, 1);
		}
		return dmg;
	}

	public static void giveFettersEffect(L1PcInstance pc, L1Character cha) {
		int fettersTime = 8000;
		if (isFreeze(cha)) {
			return;
		}
		if ((_random.nextInt(100) + 1) <= 2) {
			L1EffectSpawn.getInstance().spawnEffect(81182, fettersTime,
					cha.getX(), cha.getY(), cha.getMapId());
			if (cha instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) cha;
				targetPc.getSkillEffectTimerSet().setSkillEffect(STATUS_FREEZE,
						fettersTime);
				targetPc.sendPackets(new S_SkillSound(targetPc.getId(), 4184));
				Broadcaster.broadcastPacket(targetPc, new S_SkillSound(targetPc
						.getId(), 4184));
				targetPc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND,
						true));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.getSkillEffectTimerSet().setSkillEffect(STATUS_FREEZE,
						fettersTime);
				Broadcaster.broadcastPacket(npc, new S_SkillSound(npc.getId(),
						4184));
				npc.setParalyzed(true);
			}
		}
	}

	public static int getKiringkuDamage(L1PcInstance pc, L1Character cha) {// 키링크
		int dmg = 0;
		int dice = 6;
		int diceCount = 2;
		int value = 15;
		int KiringkuDamage = 0;
		int charaIntelligence = 0;

		for (int i = 0; i < diceCount; i++) {
			KiringkuDamage += (_random.nextInt(dice) + 1);
		}
		KiringkuDamage += value;

		int spByItem = pc.getAbility().getSp() - pc.getAbility().getTrueSp();
		charaIntelligence = pc.getAbility().getTotalInt() + spByItem - 12;
		if (charaIntelligence < 1) {
			charaIntelligence = 1;
		}
		double KiringkuCoefficientA = (1.0 + charaIntelligence * 3.0 / 32.0);

		KiringkuDamage *= KiringkuCoefficientA;

		double Mrfloor = 0;
		if (cha.getResistance().getEffectedMrBySkill() <= 100) {
			Mrfloor = Math
					.floor((cha.getResistance().getEffectedMrBySkill() - pc
							.getBaseMagicHitUp()) / 2);
		} else if (cha.getResistance().getEffectedMrBySkill() >= 100) {
			Mrfloor = Math
					.floor((cha.getResistance().getEffectedMrBySkill() - pc
							.getBaseMagicHitUp()) / 10);
		}

		double KiringkuCoefficientB = 0;
		if (cha.getResistance().getEffectedMrBySkill() <= 100) {
			KiringkuCoefficientB = 1 - 0.01 * Mrfloor;
		} else if (cha.getResistance().getEffectedMrBySkill() > 100) {
			KiringkuCoefficientB = 0.6 - 0.01 * Mrfloor;
		}

		double Kiringkufloor = Math.floor(KiringkuDamage);

		dmg += Kiringkufloor + (pc.getWeapon().getEnchantLevel() * 2);

		dmg *= KiringkuCoefficientB;

		return dmg;
	}

	public static int getChaserDamage(L1PcInstance pc, L1Character target,
			int effect) {//테베체이서
		int dmg = 0;
		double plusdmg = 0;
		int chance = _random.nextInt(100) + 1;
		if (chance <= 8) {
			int pcInt = pc.getAbility().getTotalInt();
			int targetMr = target.getResistance().getEffectedMrBySkill();
			int randmg = _random.nextInt((pcInt * 1));

			dmg = pcInt * 1 + randmg;

			int ran = Math.abs(targetMr - 100);

			if (ran == 0) {
				return dmg;
			} else if ((targetMr - 100) < 0) {
				plusdmg = 1 + (_random.nextInt(ran) * 0.02);
				dmg *= plusdmg;
			} else {
				plusdmg = 1 - (_random.nextInt(ran) * 0.02);
				dmg *= plusdmg;
			}

			if (dmg < 0)
				dmg = 0;

			pc.sendPackets(new S_SkillSound(target.getId(), effect));
			Broadcaster.broadcastPacket(pc, new S_SkillSound(target.getId(),
					effect));
		}
		return dmg;
	}

	public static int getChainSwordDamage(L1PcInstance pc, L1Character cha) {//체인소드
		int dmg = 0;
		int chance = _random.nextInt(100) + 1;
		if (8 >= chance) {
			if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_SPOT1)) {
				pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_SPOT1);
				pc.getSkillEffectTimerSet().setSkillEffect(STATUS_SPOT2,15 * 1000);
				pc.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 2));
			} else if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_SPOT2)) {
				pc.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_SPOT2);
				pc.getSkillEffectTimerSet().setSkillEffect(STATUS_SPOT3,15 * 1000);
				pc.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 3));
			} else if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_SPOT3)) {
			} else {
				pc.getSkillEffectTimerSet().setSkillEffect(STATUS_SPOT1,15 * 1000);
				pc.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 1));
			}
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_SPOT1)) {
			dmg += 10;
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_SPOT2)) {
			dmg += 20;
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_SPOT3)) {
			dmg += 30;
		}
		return dmg;
	}
	public static double getMindBreak(L1PcInstance pc, L1Character cha) {//냉한의키링크
		double dmg = 10;
		int chance = _random.nextInt(100) + 1;
		if (10 >= chance) {
		L1Magic magic = new L1Magic(pc,cha);
		dmg = magic.calcMagicDamage(L1SkillId.MIND_BREAK);
		if(dmg <=0){
		dmg += 11;
		}
		pc.sendPackets(new S_SkillSound(cha.getId(), 6553));
		Broadcaster.broadcastPacket(pc, new S_SkillSound(cha.getId(), 6553));
		magic = null;
		}
		return dmg;
		}
	private static double calcDamageReduction(L1Character cha, double dmg,
			int attr) {
		if (isFreeze(cha)) {
			return 0;
		}
		int MagicResistance = 0; // 마법저항
		int RealMagicResistance = 0; // 적용되는 마법저항값
		double calMr = 0.00D; // 마방계산
		double baseMr = 0.00D;
		if (cha instanceof L1PcInstance) {
			baseMr = (_random.nextInt(1000) + 98000) / 100000D;

			if (MagicResistance <= 100) {
				calMr = baseMr - (MagicResistance * 470) / 100000D;
			} else if (MagicResistance > 100) {
				calMr = baseMr - (MagicResistance * 470) / 100000D
						+ ((MagicResistance - 100) * 0.004);
			}
		} else {
			calMr = (200 - RealMagicResistance) / 250.00D;
		}

		dmg *= calMr;

		int resist = 0;
		if (attr == L1Skills.ATTR_EARTH) {
			resist = cha.getResistance().getEarth();
		} else if (attr == L1Skills.ATTR_FIRE) {
			resist = cha.getResistance().getFire();
		} else if (attr == L1Skills.ATTR_WATER) {
			resist = cha.getResistance().getWater();
		} else if (attr == L1Skills.ATTR_WIND) {
			resist = cha.getResistance().getWind();
		}
		int resistFloor = (int) (0.32 * Math.abs(resist));
		if (resist >= 0) {
			resistFloor *= 1;
		} else {
			resistFloor *= -1;
		}
		double attrDeffence = resistFloor / 32.0;
		dmg = (1.0 - attrDeffence) * dmg;

		if (dmg <= 0) {
			dmg = 0;
		} else {
			if (cha.getSkillEffectTimerSet().hasSkillEffect(ERASE_MAGIC)) {
				cha.getSkillEffectTimerSet().removeSkillEffect(ERASE_MAGIC);
			}
		}
		return dmg;
	}

	private static boolean isFreeze(L1Character cha) {
		if (cha.getSkillEffectTimerSet().hasSkillEffect(STATUS_FREEZE)) {
			return true;
		}
		if (cha.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)) {
			return true;
		}
		if (cha.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)) {
			return true;
		}
		if (cha.getSkillEffectTimerSet().hasSkillEffect(FREEZING_BLIZZARD)) {
			return true;
		}
		if (cha.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)) {
			return true;
		}

		if (cha.getSkillEffectTimerSet().hasSkillEffect(COUNTER_MAGIC)) {
			cha.getSkillEffectTimerSet().removeSkillEffect(COUNTER_MAGIC);
			int castgfx = SkillsTable.getInstance().getTemplate(COUNTER_MAGIC)
					.getCastGfx();
			Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(),
					castgfx));
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
			}
			return true;
		}
		return false;
	}
}
