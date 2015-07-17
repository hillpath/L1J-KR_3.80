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

package l1j.server.server.utils;

import java.util.Random;

import l1j.server.Config;

public class CalcStat {

	private static Random rnd = new Random(System.nanoTime());

	private CalcStat() {
	}

	private static final int[] maxWeight = { // Str+Con
	1500, 1650, 1800, 1800, 1950, 2100, 2100, 2250, 2250, 2400, // 20~30
			2550, 2550, 2700, 2700, 2850, 3000, 3000, 3150, 3150, 3300, // 30~40
			3450, 3450, 3600, 3600, 3750, 3900, 3900, 4050, 4050, 4200, // 40~50
			4350, 4350, 4500, 4500, 4650, 4800, 4800, 4950, 4950, 5100, // 50~60
			5250, 5250, 5400, 5400, 5550, 5700, 5700, 5850, 5850, 6000, // 60~70
			6150, 6150, 6300, 6300, 6450, 6600, 6600, 6750, 6750, 6900, // 70~80
			7050, 7050, 7200, 7200, 7350, 7500, 7500, 7650, 7650 // 80~90
	};

	/**
	 * AC보너스를 돌려준다
	 * 
	 * @param level
	 * @param dex
	 * @return acBonus
	 * 
	 */
	public static int calcAc(int level, int dex) {
        // 버그사용자이다. 보너스 읍다.
        if(dex < 0) return 0;
		int acBonus = 10;
		switch (dex) {
		case 6:
		case 7:
		case 8:
		case 9:
			acBonus -= level / 8;
			break;
		case 10:
		case 11:
		case 12:
			acBonus -= level / 7;
			break;
		case 13:
		case 14:
		case 15:
			acBonus -= level / 6;
			break;
		case 16:
		case 17:
			acBonus -= level / 5;
			break;
		default:
			acBonus -= level / 4;
			break;
		}
		return acBonus;
	}

	/**
	 * 베이스스텟에의한 근거리 명중
	 * 
	 * @param pc
	 * @param str
	 * @return Hitup
	 * 
	 */
	public static int calcBaseHitup(int chartype, int str) {
		int Hitup = 0;
		switch (chartype) {
		case 0:
			if (str >= 16) {
				Hitup += 1;
			}
			if (str >= 19) {
				Hitup += 1;
			}
			break;
		case 1:
			if (str >= 17) {
				Hitup += 2;
			}
			if (str >= 19) {
				Hitup += 2;
			}
			break;
		case 2:
			if (str >= 13) {
				Hitup += 1;
			}
			if (str >= 15) {
				Hitup += 1;
			}
			break;
		case 3:
			if (str >= 11) {
				Hitup += 1;
			}
			if (str >= 13) {
				Hitup += 1;
			}
			break;
		case 4:
			if (str >= 15) {
				Hitup += 1;
			}
			if (str >= 17) {
				Hitup += 1;
			}
			break;
		case 5:
			if (str >= 14) {
				Hitup += 1;
			}
			if (str >= 17) {
				Hitup += 2;
			}
			break;
		case 6:
			if (str >= 12) {
				Hitup += 1;
			}
			if (str >= 14) {
				Hitup += 1;
			}
			if (str >= 16) {
				Hitup += 1;
			}
			if (str >= 17) {
				Hitup += 1;
			}
			break;
		default:
			break;
		}
		return Hitup;
	}

	/**
	 * 베이스 스텟에 의한 근거리 데미지
	 * 
	 * @param pc
	 * @param str
	 * @return Dmgup
	 * 
	 */
	public static int calcBaseDmgup(int chartype, int str) {
		int Dmgup = 0;
		switch (chartype) {
		case 0:
			if (str >= 15) {
				Dmgup += 1;
			}
			if (str >= 18) {
				Dmgup += 1;
			}
			break;
		case 1:
			if (str >= 18) {
				Dmgup += 2;
			}
			if (str >= 20) {
				Dmgup += 2;
			}
			break;
		case 2:
			if (str >= 12) {
				Dmgup += 1;
			}
			if (str >= 14) {
				Dmgup += 1;
			}
			break;
		case 3:
			if (str >= 10) {
				Dmgup += 1;
			}
			if (str >= 12) {
				Dmgup += 1;
			}
			break;
		case 4:
			if (str >= 14) {
				Dmgup += 1;
			}
			if (str >= 18) {
				Dmgup += 1;
			}
			break;
		case 5:
			if (str >= 15) {
				Dmgup += 1;
			}
			if (str >= 18) {
				Dmgup += 2;
			}
			break;
		case 6:
			if (str >= 13) {
				Dmgup += 1;
			}
			if (str >= 15) {
				Dmgup += 1;
			}
			break;
		default:
			break;
		}
		return Dmgup;
	}

	/**
	 * 베이스 스텟에 의한 원거리 명중률
	 * 
	 * @param pc
	 * @param dex
	 * @return BowHitup
	 * 
	 */
	public static int calcBaseBowHitup(int chartype, int dex) {
		int BowHitup = 0;
		switch (chartype) {
		case 0:
			BowHitup = 0;
			break;
		case 1:
			BowHitup = 0;
			break;
		case 2:
			if (dex >= 13) {
				BowHitup += 2;
			}
			if (dex >= 16) {
				BowHitup += 1;
			}
			break;
		case 3:
			BowHitup = 0;
			break;
		case 4:
			if (dex >= 17) {
				BowHitup += 1;
			}
			if (dex >= 18) {
				BowHitup += 1;
			}
			break;
		case 5:
			BowHitup = 0;
			break;
		case 6:
			BowHitup = 0;
			break;
		default:
			break;
		}
		return BowHitup;
	}

	/**
	 * 베이스 스텟에 의한 원거리 데미지
	 * 
	 * @param pc
	 * @param dex
	 * @return BowDmgup
	 * 
	 */
	public static int calcBaseBowDmgup(int chartype, int dex) {
		int BowDmgup = 0;
		switch (chartype) {
		case 0:
			if (dex >= 13) {
				BowDmgup += 1;
			}
			break;
		case 1:
			BowDmgup = 0;
			break;
		case 2:
			if (dex >= 14) {
				BowDmgup += 2;
			}
			if (dex >= 17) {
				BowDmgup += 1;
			}
			break;
		case 3:
			BowDmgup = 0;
			break;
		case 4:
			if (dex >= 18) {
				BowDmgup += 2;
			}
			break;
		case 5:
			BowDmgup = 0;
			break;
		case 6:
			BowDmgup = 0;
			break;
		default:
			break;
		}
		return BowDmgup;
	}

	/**
	 * 
	 * 베이스 스탯에 의한 마법 명중
	 * 
	 * @param chartype
	 * @param baseint
	 * @return magicHit
	 */
	public static int calcBaseMagicHitUp(int chartype, int baseint) {
		int magicHit = 0;
		switch (chartype) {
		case 0:
			if (baseint >= 12) {
				magicHit += 1;
			}
			if (baseint >= 14) {
				magicHit += 1;
			}
			break;
		case 1:
			if (baseint >= 10) {
				magicHit += 1;
			}
			if (baseint >= 12) {
				magicHit += 1;
			}
			break;
		case 2:
			if (baseint >= 13) {
				magicHit += 1;
			}
			if (baseint >= 15) {
				magicHit += 1;
			}
			break;
		case 3:
			if (baseint >= 14) {
				magicHit += 1;
			}
			break;
		case 4:
			if (baseint >= 12) {
				magicHit += 1;
			}
			if (baseint >= 14) {
				magicHit += 1;
			}
			break;
		case 5:
			if (baseint >= 12) {
				magicHit += 1;
			}
			if (baseint >= 14) {
				magicHit += 1;
			}
			break;
		case 6:
			if (baseint >= 13) {
				magicHit += 1;
			}
			break;
		default:
			break;
		}
		return magicHit;
	}

	/**
	 * 
	 * 베이스 스텟에 의한 마법 치명타 (%)
	 * 
	 * @param chartype
	 * @param baseint
	 * @return mc
	 */
	public static int calcBaseMagicCritical(int chartype, int baseint) {
		int mc = 0;
		switch (chartype) {
		case 0:
		case 1:
			mc = 0;
			break;
		case 2:
			if (baseint >= 14) {
				mc += 2;
			}
			if (baseint >= 16) {
				mc += 2;
			}
			break;
		case 3:
			if (baseint >= 15) {
				mc += 2;
			}
			if (baseint >= 16) {
				mc += 2;
			}
			if (baseint >= 17) {
				mc += 2;
			}
			if (baseint >= 18) {
				mc += 2;
			}
			break;
		case 4:
		case 5:
		case 6:
			mc = 0;
			break;
		default:
			break;
		}
		return mc;
	}

	/**
	 * 
	 * 베이스 스텟에 의한 마법 데미지
	 * 
	 * @param chartype
	 * @param baseint
	 * @return md
	 */
	public static int calcBaseMagicDmg(int chartype, int baseint) {
		int md = 0;
		switch (chartype) {
		case 0:
		case 1:
		case 2:
			md = 0;
			break;
		case 3:
			if (baseint >= 13) {
				md += 1;
			}
			break;
		case 4:
			md = 0;
			break;
		case 5:
			if (baseint >= 13) {
				md += 1;
			}
			if (baseint >= 15) {
				md += 1;
			}
			if (baseint >= 17) {
				md += 1;
			}
			break;
		case 6:
			if (baseint >= 16) {
				md += 1;
			}
			if (baseint >= 17) {
				md += 1;
			}
			break;
		default:
			break;
		}
		return md;
	}

	/**
	 * 
	 * 베이스 스텟에 의한 MP 감소량
	 * 
	 * @param chartype
	 * @param baseint
	 * @return dmp
	 */
	public static int calcBaseDecreaseMp(int chartype, int baseint) {
		int dmp = 0;
		switch (chartype) {
		case 0:
			if (baseint >= 11) {
				dmp += 1;
			}
			if (baseint >= 13) {
				dmp += 1;
			}
			break;
		case 1:
			if (baseint >= 9) {
				dmp += 1;
			}
			if (baseint >= 11) {
				dmp += 1;
			}
			break;
		case 2:
			dmp = 0;
			break;
		case 3:
			dmp = 0;
			break;
		case 4:
			if (baseint >= 13) {
				dmp += 1;
			}
			if (baseint >= 15) {
				dmp += 1;
			}
			break;
		case 5:
			dmp = 0;
			break;
		case 6:
			if (baseint >= 14) {
				dmp += 1;
			}
			if (baseint >= 15) {
				dmp += 1;
			}
			break;
		default:
			break;
		}
		return dmp;
	}

	/**
	 * 베이스 스텟에 의한 ER(원거리 회피율)
	 * 
	 * @param pc
	 * @param dex
	 * @return Er
	 * 
	 */
	public static int calcBaseEr(int chartype, int dex) {
		int Er = 0;
		switch (chartype) {
		case 0:
			if (dex >= 14) {
				Er += 1;
			}
			if (dex >= 16) {
				Er += 1;
			}
			if (dex >= 18) {
				Er += 1;
			}
			break;
		case 1:
			if (dex >= 14) {
				Er += 1;
			}
			if (dex >= 16) {
				Er += 2;
			}
			break;
		case 2:
			Er = 0;
			break;
		case 3:
			if (dex >= 9) {
				Er += 1;
			}
			if (dex >= 11) {
				Er += 1;
			}
			break;
		case 4:
			if (dex >= 16) {
				Er += 2;
			}
			break;
		case 5:
			if (dex >= 13) {
				Er += 1;
			}
			if (dex >= 15) {
				Er += 1;
			}
			break;
		case 6:
			if (dex >= 12) {
				Er += 1;
			}
			if (dex >= 14) {
				Er += 1;
			}
			break;
		default:
			break;
		}
		return Er;
	}

	/**
	 * 베이스 스텟에 의한 MR(마법 방어력)
	 * 
	 * @param pc
	 * @param wis
	 * @return MR
	 * 
	 */
	public static int calcBaseMr(int chartype, int wis) {
		int MR = 0;
		switch (chartype) {
		case 0:
			if (wis >= 12)
				MR += 1;
			if (wis >= 14)
				MR += 1;
			break;
		case 1:
			if (wis >= 10)
				MR += 1;
			if (wis >= 12)
				MR += 2;
			break;
		case 2:
			if (wis >= 13)
				MR += 1;
			if (wis >= 16)
				MR += 1;
			break;
		case 3:
			if (wis >= 15)
				MR += 1;
			break;
		case 4:
			if (wis >= 11)
				MR += 1;
			if (wis >= 14)
				MR += 1;
			if (wis >= 15)
				MR += 1;
			if (wis >= 16)
				MR += 1;
			break;
		case 5:
			if (wis >= 14)
				MR += 2;
			break;
		case 6:
			if (wis >= 15)
				MR += 2;
			if (wis >= 18)
				MR += 2;
			break;
		default:
			break;
		}
		return MR;
	}

	/**
	 * 베이스스텟에의한 AC보너스를 돌려준다.
	 * 
	 * @param pc
	 * @param dex
	 * @return acBonus
	 * 
	 */
	public static int calcBaseAc(int chartype, int dex) {
		int acBonus = 0;
		switch (chartype) {
		case 0:
			if (dex >= 12) {
				acBonus -= 1;
			}
			if (dex >= 15) {
				acBonus -= 1;
			}
			if (dex >= 17) {
				acBonus -= 1;
			}
			break;
		case 1:
			if (dex >= 13) {
				acBonus -= 1;
			}
			if (dex >= 15) {
				acBonus -= 2;
			}
			break;
		case 2:
			if (dex >= 15) {
				acBonus -= 1;
			}
			if (dex >= 18) {
				acBonus -= 1;
			}
			break;
		case 3:
			if (dex >= 8) {
				acBonus -= 1;
			}
			if (dex >= 10) {
				acBonus -= 1;
			}
			break;
		case 4:
			if (dex >= 17) {
				acBonus -= 1;
			}
			break;
		case 5:
			if (dex >= 12) {
				acBonus -= 1;
			}
			if (dex >= 14) {
				acBonus -= 1;
			}
			break;
		case 6:
			if (dex >= 11) {
				acBonus -= 1;
			}
			if (dex >= 13) {
				acBonus -= 1;
			}
			break;
		default:
			break;
		}
		return acBonus;
	}

	/**
	 * 베이스스텟에의한 HP 회복 보너스를 돌려준다.
	 * 
	 * @param chartype(캐릭터타입)
	 * @param con
	 * @return BonusHpr
	 * 
	 */
	public static int calcBaseHpr(int chartype, int con) {
		int BonusHpr = 0;
		switch (chartype) {
		case 0:
			if (con >= 13) {
				BonusHpr += 1;
			}
			if (con >= 15) {
				BonusHpr += 1;
			}
			if (con >= 17) {
				BonusHpr += 1;
			}
			if (con >= 18) {
				BonusHpr += 1;
			}
			break;
		case 1:
			if (con >= 16) {
				BonusHpr += 2;
			}
			if (con >= 18) {
				BonusHpr += 2;
			}
			break;
		case 2:
			if (con >= 14) {
				BonusHpr += 1;
			}
			if (con >= 16) {
				BonusHpr += 1;
			}
			if (con >= 17) {
				BonusHpr += 1;
			}
			break;
		case 3:
			if (con >= 17) {
				BonusHpr += 1;
			}
			if (con >= 18) {
				BonusHpr += 1;
			}
			break;
		case 4:
			if (con >= 11) {
				BonusHpr += 1;
			}
			if (con >= 13) {
				BonusHpr += 1;
			}
			break;
		case 5:
			if (con >= 16) {
				BonusHpr += 1;
			}
			if (con >= 18) {
				BonusHpr += 2;
			}
			break;
		case 6:
			if (con >= 14) {
				BonusHpr += 1;
			}
			if (con >= 16) {
				BonusHpr += 1;
			}
			break;
		default:
			break;
		}
		return BonusHpr;
	}

	/**
	 * 베이스스텟에의한 MP 회복 보너스를 돌려준다.
	 * 
	 * @param chartype(캐릭터타입)
	 * @param con
	 * @return BonusMpr
	 * 
	 */
	public static int calcBaseMpr(int chartype, int wis) {
		int BonusMpr = 0;
		switch (chartype) {
		case 0:
			if (wis >= 13) {
				BonusMpr += 1;
			}
			if (wis >= 15) {
				BonusMpr += 1;
			}
			break;
		case 1:
			if (wis >= 10) {
				BonusMpr += 1;
			}
			if (wis >= 12) {
				BonusMpr += 2;
			}
			break;
		case 2:
			if (wis >= 15) {
				BonusMpr += 1;
			}
			if (wis >= 18) {
				BonusMpr += 1;
			}
			break;
		case 3:
			if (wis >= 14) {
				BonusMpr += 1;
			}
			if (wis >= 16) {
				BonusMpr += 1;
			}
			if (wis >= 18) {
				BonusMpr += 1;
			}
			break;
		case 4:
			if (wis >= 13) {
				BonusMpr += 1;
			}
			break;
		case 5:
			if (wis >= 15) {
				BonusMpr += 1;
			}
			if (wis >= 17) {
				BonusMpr += 1;
			}
			break;
		case 6:
			if (wis >= 14) {
				BonusMpr += 1;
			}
			if (wis >= 17) {
				BonusMpr += 1;
			}
			break;
		default:
			break;
		}
		return BonusMpr;
	}

	/**
	 * 인수의 WIS에 대응하는 MR보너스를 돌려준다
	 * 
	 * @param wis
	 * @return mrBonus
	 */
	public static int calcStatMr(int wis) {
        // 버그 사용자
        if(wis < 0) return 0;
		int mrBonus = 0;
		if (wis <= 14) {
			mrBonus = 0;
		} else if (wis >= 15 && wis <= 16) {
			mrBonus = 3;
		} else if (wis == 17) {
			mrBonus = 6;
		} else if (wis == 18) {
			mrBonus = 10;
		} else if (wis == 19) {
			mrBonus = 15;
		} else if (wis == 20) {
			mrBonus = 21;
		} else if (wis == 21) {
			mrBonus = 28;
		} else if (wis == 22) {
			mrBonus = 37;
		} else if (wis == 23) {
			mrBonus = 47;
		} else if (wis == 24){
			mrBonus = 50;
		} else if (wis == 25){
			mrBonus = 51;
		} else if (wis == 26){
			mrBonus = 52;
		} else if (wis == 27){
			mrBonus = 53;
		} else if (wis == 28){
			mrBonus = 54;
		} else if (wis == 29){
			mrBonus = 55;
		} else if (wis == 30){
			mrBonus = 56;
		} else if (wis == 31){
			mrBonus = 57;
		} else if (wis == 32){
			mrBonus = 58;
		} else if (wis == 33){
			mrBonus = 59;
		} else if (wis == 34){
			mrBonus = 62;
		} else if (wis >= 35){
			mrBonus = 65;
		}
		return mrBonus;
	}

	public static int calcDiffMr(int wis, int diff) {
		return calcStatMr(wis + diff) - calcStatMr(wis);
	}

	/**
	 * 각 클래스의 LVUP시의 HP상승치를 돌려준다
	 * 
	 * @param charType
	 * @param baseMaxHp
	 * @param baseCon
	 * @return HP상승치
	 */
	public static short calcStatHp(int charType, int baseMaxHp, byte baseCon) {
		short randomhp = 0;
		int addCon = 0;

		if (charType == 0) { // 군주
			int calCon = 10;
			switch (baseCon - calCon) {
			case 0:
			case 1:
				addCon = 1 + rnd.nextInt(2);
				break;
			case 2:
			case 3:
				addCon = 2 + rnd.nextInt(2);
				break;
			case 4:
			case 5:
				addCon = 3 + rnd.nextInt(2);
				break;
			case 6:
				addCon = 5 + rnd.nextInt(2);
				break;
			case 7:
				addCon = 6 + rnd.nextInt(2);
				break;
			case 8:
				addCon = 7 + rnd.nextInt(2);
				break;
			default:
				addCon = (baseCon - calCon - 1)
						+ (int) ((baseCon - calCon) / 5) + rnd.nextInt(2);
				break;
			}
			randomhp += calCon + addCon;

			if (baseMaxHp + randomhp > Config.PRINCE_MAX_HP) {
				randomhp = (short) (Config.PRINCE_MAX_HP - baseMaxHp);
			}
		} else if (charType == 1) { // 기사
			int calCon = 14;
			switch (baseCon - calCon) {
			case 0:
				addCon = 4 + rnd.nextInt(2);
				break;
			case 1:
				addCon = 5 + rnd.nextInt(2);
				break;
			case 2:
				addCon = 6 + rnd.nextInt(2);
				break;
			case 3:
				addCon = 7 + rnd.nextInt(2);
				break;
			case 4:
				addCon = 9 + rnd.nextInt(2);
				break;
			default:
				addCon = 9 + rnd.nextInt(2) + (int) ((baseCon - calCon) / 3);
				break;
			}

			randomhp += calCon + addCon;

			if (baseMaxHp + randomhp > Config.KNIGHT_MAX_HP) {
				randomhp = (short) (Config.KNIGHT_MAX_HP - baseMaxHp);
			}
		} else if (charType == 2) { // 요정
			int calCon = 12;
			switch (baseCon - calCon) {
			case 0:
				addCon = 0 + rnd.nextInt(2);
				break;
			case 1:
			case 2:
			case 3:
				addCon = 1 + rnd.nextInt(2);
				break;
			case 4:
				addCon = 2 + rnd.nextInt(2);
				break;
			case 5:
				addCon = 3 + rnd.nextInt(2);
				break;
			case 6:
				addCon = 4 + rnd.nextInt(2);
				break;
			default:
				addCon = 4 + rnd.nextInt(2) + (int) ((baseCon - calCon) / 3);
				break;
			}
			randomhp += calCon - 2 + addCon;

			if (baseMaxHp + randomhp > Config.ELF_MAX_HP) {
				randomhp = (short) (Config.ELF_MAX_HP - baseMaxHp);
			}
		} else if (charType == 3) { // 마법사
			int calCon = 12;
			switch (baseCon - calCon) {
			case 0:
			case 1:
				addCon = 0 + rnd.nextInt(2);
				break;
			case 2:
			case 3:
				addCon = 1 + rnd.nextInt(2);
				break;
			case 4:
				addCon = 2 + rnd.nextInt(2);
				break;
			case 5:
				addCon = 3 + rnd.nextInt(2);
				break;
			case 6:
				addCon = 4 + rnd.nextInt(2);
				break;
			default:
				addCon = 4 + rnd.nextInt(2) + (int) ((baseCon - calCon) / 3);
				break;
			}
			randomhp += calCon - 4 + addCon;

			if (baseMaxHp + randomhp > Config.WIZARD_MAX_HP) {
				randomhp = (short) (Config.WIZARD_MAX_HP - baseMaxHp);
			}
		} else if (charType == 4) { // 다크엘프
			int calCon = 8;
			switch (baseCon - calCon) {
			case 0:
			case 1:
				addCon = 0 + rnd.nextInt(2);
				break;
			case 2:
			case 3:
				addCon = 1 + rnd.nextInt(2);
				break;
			case 4:
			case 5:
			case 6:
			case 7:
				addCon = 2 + rnd.nextInt(2);
				break;
			case 8:
			case 9:
			case 10:
				addCon = 3 + rnd.nextInt(2);
				break;
			default:
				addCon = 4 + rnd.nextInt(2) + (int) ((baseCon - calCon) / 4);
				break;
			}
			randomhp += calCon + 3 + addCon;

			if (baseMaxHp + randomhp > Config.DARKELF_MAX_HP) {
				randomhp = (short) (Config.DARKELF_MAX_HP - baseMaxHp);
			}
		} else if (charType == 5) { // 용기사
			int calCon = 14;
			switch (baseCon - calCon) {
			case 0:
				addCon = 0 + rnd.nextInt(2);
				break;
			case 1:
				addCon = 1 + rnd.nextInt(2);
				break;
			case 2:
				addCon = 2 + rnd.nextInt(2);
				break;
			case 3:
				addCon = 3 + rnd.nextInt(2);
				break;
			case 4:
				addCon = 4 + rnd.nextInt(2);
				break;
			default:
				addCon = 4 + rnd.nextInt(2) + (int) ((baseCon - calCon) / 3);
				break;
			}
			randomhp += calCon - 1 + addCon;

			if (baseMaxHp + randomhp > Config.DRAGONKNIGHT_MAX_HP) {
				randomhp = (short) (Config.DRAGONKNIGHT_MAX_HP - baseMaxHp);
			}
		} else if (charType == 6) { // 환술사
			int calCon = 12;
			switch (baseCon - calCon) {
			case 0:
			case 1:
				addCon = 0 + rnd.nextInt(2);
				break;
			case 2:
			case 3:
				addCon = 2 + rnd.nextInt(2);
				break;
			case 4:
				addCon = 3 + rnd.nextInt(2);
				break;
			default:
				addCon = 3 + rnd.nextInt(2) + (int) ((baseCon - calCon) / 3);
				break;
			}
			randomhp += calCon - 4 + addCon;

			if (baseMaxHp + randomhp > Config.BLACKWIZARD_MAX_HP) {
				randomhp = (short) (Config.BLACKWIZARD_MAX_HP - baseMaxHp);
			}
		}
		if (randomhp < 0) {
			randomhp = 0;
		}

		return randomhp;

	}

	/**
	 * 각 클래스의 LVUP시의 MP상승치를 돌려준다
	 * 
	 * @param charType
	 * @param baseMaxMp
	 * @param baseWis
	 * @return MP상승치
	 */
	public static short calcStatMp(int charType, int baseMaxMp, byte baseWis) {
		int randommp = 0;

		if (charType == 0) { // 프린스
			int addWis = 0;
			int calWis = 11;
			switch (baseWis - calWis) {
			case 0:
			case 1:
				addWis = 2 + rnd.nextInt(2);
				break;
			case 2:
			case 3:
				addWis = 3 + rnd.nextInt(2);
				break;
			case 4:
			case 5:
				addWis = 4 + rnd.nextInt(2);
				break;
			case 6:
			case 7:
				addWis = 5 + rnd.nextInt(2);
				break;
			default:
				addWis = 5 + rnd.nextInt(2) + (int) ((baseWis - calWis) / 3);
				break;
			}
			randommp += addWis;

			if (baseMaxMp + randommp > Config.PRINCE_MAX_MP) {
				randommp = Config.PRINCE_MAX_MP - baseMaxMp;
			}
		} else if (charType == 1) { // 나이트
			int addWis = 0;
			int calWis = 9;
			switch (baseWis - calWis) {
			case 0:
			case 1:
				addWis = 1 + rnd.nextInt(2);
				break;
			case 2:
			case 3:
			case 4:
				addWis = 2 + rnd.nextInt(2);
				break;
			case 5:
			case 6:
			case 7:
				addWis = 3 + rnd.nextInt(2);
				break;
			default:
				addWis = 3 + rnd.nextInt(2) + (int) ((baseWis - calWis) / 3);
				break;
			}
			randommp += addWis;

			if (baseMaxMp + randommp > Config.KNIGHT_MAX_MP) {
				randommp = Config.KNIGHT_MAX_MP - baseMaxMp;
			}
		} else if (charType == 2) { // 에르프
			int addWis = 0;
			int calWis = 12;
			switch (baseWis - calWis) {
			case 0:
			case 1:
				addWis = 4 + rnd.nextInt(2);
				break;
			case 2:
			case 3:
				addWis = 5 + rnd.nextInt(2);
				break;
			case 4:
			case 5:
				addWis = 6 + rnd.nextInt(2);
				break;
			case 6:
				addWis = 8 + rnd.nextInt(2);
				break;
			default:
				addWis = 8 + rnd.nextInt(2) + (int) ((baseWis - calWis) / 5);
				break;
			}
			randommp += addWis;

			if (baseMaxMp + randommp > Config.ELF_MAX_MP) {
				randommp = Config.ELF_MAX_MP - baseMaxMp;
			}
		} else if (charType == 3) { // 위저드
			int addWis = 0;
			int calWis = 12;
			switch (baseWis - calWis) {
			case 0:
				addWis = 6 + rnd.nextInt(2);
				break;
			case 1:
			case 2:
				addWis = 7 + rnd.nextInt(2);
				break;
			case 3:
				addWis = 8 + rnd.nextInt(2);
				break;
			case 4:
			case 5:
				addWis = 9 + rnd.nextInt(2);
				break;
			case 6:
				addWis = 10 + rnd.nextInt(2);
				break;
			default:
				addWis = 11 + rnd.nextInt(2) + (int) ((baseWis - calWis) / 5);
				break;
			}
			randommp += addWis;

			if (baseMaxMp + randommp > Config.WIZARD_MAX_MP) {
				randommp = Config.WIZARD_MAX_MP - baseMaxMp;
			}
		} else if (charType == 4) { // 다크 에르프
			int addWis = 0;
			int calWis = 10;
			switch (baseWis - calWis) {
			case 0:
			case 1:
				addWis = 3 + rnd.nextInt(2);
				break;
			case 2:
			case 3:
				addWis = 4 + rnd.nextInt(2);
				break;
			case 4:
			case 5:
				addWis = 5 + rnd.nextInt(2);
				break;
			case 6:
			case 7:
				addWis = 6 + rnd.nextInt(2);
				break;
			case 8:
				addWis = 7 + rnd.nextInt(2);
				break;
			default:
				addWis = 7 + rnd.nextInt(2) + (int) ((baseWis - calWis) / 5);
				break;
			}
			randommp += addWis;

			if (baseMaxMp + randommp > Config.DARKELF_MAX_MP) {
				randommp = Config.DARKELF_MAX_MP - baseMaxMp;
			}
		} else if (charType == 5) { // 용기사
			int addWis = 0;
			int calWis = 12;
			switch (baseWis - calWis) {
			case 0:
			case 1:
				addWis = 1 + rnd.nextInt(2);
				break;
			case 2:
			case 3:
				addWis = 2 + rnd.nextInt(2);
				break;
			case 4:
			case 5:
				addWis = 3 + rnd.nextInt(2);
				break;
			case 6:
				addWis = 4 + rnd.nextInt(2);
				break;
			default:
				addWis = 4 + rnd.nextInt(2) + (int) ((baseWis - calWis) / 5);
				break;
			}
			randommp += addWis;

			if (baseMaxMp + randommp > Config.DRAGONKNIGHT_MAX_MP) {
				randommp = Config.DRAGONKNIGHT_MAX_MP - baseMaxMp;
			}
		} else if (charType == 6) { // 환술사
			int addWis = 0;
			int calWis = 12;
			switch (baseWis - calWis) {
			case 0:
			case 1:
				addWis = 4 + rnd.nextInt(2);
				break;
			case 2:
				addWis = 5 + rnd.nextInt(2);
				break;
			case 3:
				addWis = 6 + rnd.nextInt(2);
				break;
			case 4:
				addWis = 7 + rnd.nextInt(2);
				break;
			case 5:
				addWis = 8 + rnd.nextInt(2);
				break;
			case 6:
				addWis = 9 + rnd.nextInt(2);
				break;
			default:
				addWis = 9 + rnd.nextInt(2) + (int) ((baseWis - calWis) / 5);
				break;
			}
			randommp += addWis;

			if (baseMaxMp + randommp > Config.BLACKWIZARD_MAX_MP) {
				randommp = Config.BLACKWIZARD_MAX_MP - baseMaxMp;
			}
		}
		if (randommp < 0) {
			randommp = 0;
		}
		return (short) randommp;
	}

	public static int getMaxWeight(int str, int con) {
		return calcMaxWeight(str + con);
	}

	private static int calcMaxWeight(int value) {
		int weight = 0;
		if (value < 20) {
			return 1500;
		} else {
			weight = maxWeight[(value - 20)];
		}
		return weight;
	}

	public static int calcBaseWeight(int charType, int str, int con) {
		int weightReductionByBaseStatus = 0;
		switch (charType) {
		case 0:
			if (str >= 14) {
				weightReductionByBaseStatus += 1;
			}
			if (str >= 17) {
				weightReductionByBaseStatus += 1;
			}
			if (str >= 20) {
				weightReductionByBaseStatus += 1;
			}
			if (con >= 11) {
				weightReductionByBaseStatus += 1;
			}
			break;
		case 1:
			if (con >= 15) {
				weightReductionByBaseStatus += 1;
			}
			break;
		case 2:
			if (str >= 16) {
				weightReductionByBaseStatus += 2;
			}
			if (con >= 15) {
				weightReductionByBaseStatus += 2;
			}
			break;
		case 3:
			if (str >= 9) {
				weightReductionByBaseStatus += 1;
			}
			if (con >= 13) {
				weightReductionByBaseStatus += 1;
			}
			if (con >= 15) {
				weightReductionByBaseStatus += 1;
			}
			break;
		case 4:
			if (str >= 13) {
				weightReductionByBaseStatus += 2;
			}
			if (str >= 16) {
				weightReductionByBaseStatus += 1;
			}
			if (con >= 9) {
				weightReductionByBaseStatus += 1;
			}
			break;
		case 5:
			if (str >= 16) {
				weightReductionByBaseStatus += 1;
			}
			break;
		case 6:
			if (str >= 18) {
				weightReductionByBaseStatus += 1;
			}
			if (con >= 17) {
				weightReductionByBaseStatus += 1;
			}
			if (con >= 18) {
				weightReductionByBaseStatus += 1;
			}
			break;
		default:
			break;
		}
		return weightReductionByBaseStatus;
	}

}
