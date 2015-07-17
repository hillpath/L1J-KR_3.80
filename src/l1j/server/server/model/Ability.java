package l1j.server.server.model;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.IntRange;

public class Ability {
	private static final int LIMIT_MINUS_MIN = -128;

	private static final int LIMIT_MIN = 0;

	private static final int LIMIT_MAX = 127;

	private byte str; // º£ÀÌ½º Èû + ·¹º§¾÷ ¶Ç´Â ¿¤¸¯¼­·Î ÀÎÇØ »ó½ÂÇÑ Èû

	private byte baseStr; // º£ÀÌ½º Èû

	private byte addedStr; // ¸¶¹ý ¶Ç´Â ¾ÆÀÌÅÛÀ¸·Î »ó½ÂÇÑ Èû

	private byte con;

	private byte baseCon;

	private byte addedCon;

	private byte dex;

	private byte baseDex;

	private byte addedDex;

	private byte cha;

	private byte baseCha;

	private byte addedCha;

	private byte intel;

	private byte baseInt;

	private byte addedInt;

	private byte wis;

	private byte baseWis;

	private byte addedWis;

	private int elixirCount;

	private int bonusAbility;

	private int sp; // sp

	private L1Character character;

	Ability(L1Character cha) {
		this.character = cha;
	}

	public void reset() {
		str = baseStr = addedStr = 0;
		dex = baseDex = addedDex = 0;
		con = baseCon = addedCon = 0;
		intel = baseInt = addedInt = 0;
		wis = baseWis = addedWis = 0;
		cha = baseCha = addedCha = 0;
	}

	private byte checkRange(int i) {
		if (i == 0)
			return 0;
		return checkRange(i, 0);
	}

	private byte checkRange(int i, int base) {
		return (byte) IntRange.ensure(i, LIMIT_MIN + base, LIMIT_MAX);
	}

	public int getAmount() {
		return str + con + dex + cha + intel + wis;
	}

	public int getBaseAmount() {
		return baseStr + baseCon + baseDex + baseCha + baseInt + baseWis;
	}

	public byte getStr() {
		return str;
	}

	public void setStr(int i) {
		str = checkRange(i, baseStr);
	}

	public void addStr(int i) {
		setStr(getStr() + i);
	}

	public byte getBaseStr() {
		return baseStr;
	}

	public void addBaseStr(int i) {
		setBaseStr(getBaseStr() + i);
	}

	public void setBaseStr(int i) {
		byte newBaseStr = checkRange(i);
		addStr(newBaseStr - baseStr);
		baseStr = newBaseStr;
	}

	public byte getAddedStr() {
		return addedStr;
	}

	public void addAddedStr(int i) {
		addedStr = checkRange(addedStr + i, LIMIT_MINUS_MIN);
	}

	public byte getTotalStr() {
		return checkRange(getStr() + getAddedStr());
	}

	public byte getCon() {
		return con;
	}

	public void setCon(int i) {
		con = checkRange(i, baseCon);
	}

	public void addCon(int i) {
		setCon(getCon() + i);
	}

	public byte getBaseCon() {
		return baseCon;
	}

	public void addBaseCon(int i) {
		setBaseCon(getBaseCon() + i);
	}

	public void setBaseCon(int i) {
		byte newBaseCon = checkRange(i);
		addCon(newBaseCon - baseCon);
		baseCon = newBaseCon;
	}

	public byte getAddedCon() {
		return addedCon;
	}

	public void addAddedCon(int i) {
		addedCon = checkRange(addedCon + i, LIMIT_MINUS_MIN);
	}

	public byte getTotalCon() {
		return checkRange(getCon() + getAddedCon());
	}

	public byte getDex() {
		return dex;
	}

	public void setDex(int i) {
		dex = checkRange(i, baseDex);
	}

	public void addDex(int i) {
		setDex(getDex() + i);
	}

	public byte getBaseDex() {
		return baseDex;
	}

	public void addBaseDex(int i) {
		setBaseDex(getBaseDex() + i);
	}

	public void setBaseDex(int i) {
		byte newBaseDex = checkRange(i);
		addDex(newBaseDex - baseDex);
		baseDex = newBaseDex;
	}

	public byte getAddedDex() {
		return addedDex;
	}

	public void addAddedDex(int i) {
		addedDex = checkRange(addedDex + i, LIMIT_MINUS_MIN);
	}

	public byte getTotalDex() {
		return checkRange(getDex() + getAddedDex());
	}

	public byte getCha() {
		return cha;
	}

	public void setCha(int i) {
		cha = checkRange(i, baseCha);
	}

	public void addCha(int i) {
		setCha(getCha() + i);
	}

	public byte getBaseCha() {
		return baseCha;
	}

	public void addBaseCha(int i) {
		setBaseCha(getBaseCha() + i);
	}

	public void setBaseCha(int i) {
		byte newBaseCha = checkRange(i);
		addCha(newBaseCha - baseCha);
		baseCha = newBaseCha;
	}

	public byte getAddedCha() {
		return addedCha;
	}

	public void addAddedCha(int i) {
		addedCha = checkRange(addedCha + i, LIMIT_MINUS_MIN);
	}

	public byte getTotalCha() {
		return checkRange(getCha() + getAddedCha());
	}

	public byte getInt() {
		return intel;
	}

	public void setInt(int i) {
		intel = checkRange(i, baseInt);
	}

	public void addInt(int i) {
		setInt(getInt() + i);
	}

	public byte getBaseInt() {
		return baseInt;
	}

	public void addBaseInt(int i) {
		setBaseInt(getBaseInt() + i);
	}

	public void setBaseInt(int i) {
		byte newBaseInt = checkRange(i);
		addInt(newBaseInt - baseInt);
		baseInt = newBaseInt;
	}

	public byte getAddedInt() {
		return addedInt;
	}

	public void addAddedInt(int i) {
		addedInt = checkRange(addedInt + i, LIMIT_MINUS_MIN);
	}

	public byte getTotalInt() {
		return checkRange(getInt() + getAddedInt());
	}

	public byte getWis() {
		return wis;
	}

	public void setWis(int i) {
		wis = checkRange(i, baseWis);
	}

	public void addWis(int i) {
		setWis(getWis() + i);
	}

	public byte getBaseWis() {
		return baseWis;
	}

	public void addBaseWis(int i) {
		setBaseWis(getBaseWis() + i);
	}

	public void setBaseWis(int i) {
		byte newBaseWis = checkRange(i);
		addWis(newBaseWis - baseWis);
		baseWis = newBaseWis;
	}

	public byte getAddedWis() {
		return addedWis;
	}

	public void addAddedWis(int i) {
		addedWis = checkRange(addedWis + i, LIMIT_MINUS_MIN);
	}

	public byte getTotalWis() {
		return checkRange(getWis() + getAddedWis());
	}

	public int getElixirCount() {
		return elixirCount;
	}

	public void setElixirCount(int i) {
		elixirCount = i;
	}

	public int getBonusAbility() {
		return bonusAbility;
	}

	public void setBonusAbility(int i) {
		bonusAbility = i;
	}

	public boolean isNormalAbility(final int classId, final int currentLevel,
			final int highestLevel, final int total) {
		boolean result = true;
		final int MaxAbility = 35;
		int minStr, minDex, minCon, minWis, minCha, minInt, remainStats;

		switch (classId) {
		case L1PcInstance.CLASSID_PRINCE:
		case L1PcInstance.CLASSID_PRINCESS:
			minStr = 13;
			minDex = 10;
			minCon = 10;
			minWis = 11;
			minCha = 13;
			minInt = 10;
			remainStats = 8;
			break;
		case L1PcInstance.CLASSID_KNIGHT_MALE:
		case L1PcInstance.CLASSID_KNIGHT_FEMALE:
			minStr = 16;
			minDex = 12;
			minCon = 14;
			minWis = 9;
			minCha = 12;
			minInt = 8;
			remainStats = 4;
			break;
		case L1PcInstance.CLASSID_WIZARD_MALE:
		case L1PcInstance.CLASSID_WIZARD_FEMALE:
			minStr = 8;
			minDex = 7;
			minCon = 12;
			minWis = 12;
			minCha = 8;
			minInt = 12;
			remainStats = 16;
			break;
		case L1PcInstance.CLASSID_ELF_MALE:
		case L1PcInstance.CLASSID_ELF_FEMALE:
			minStr = 11;
			minDex = 12;
			minCon = 12;
			minWis = 12;
			minCha = 9;
			minInt = 12;
			remainStats = 7;
			break;
		case L1PcInstance.CLASSID_DARKELF_MALE:
		case L1PcInstance.CLASSID_DARKELF_FEMALE:
			minStr = 12;
			minDex = 15;
			minCon = 8;
			minWis = 10;
			minCha = 9;
			minInt = 11;
			remainStats = 10;
			break;
		case L1PcInstance.CLASSID_DRAGONKNIGHT_MALE:
		case L1PcInstance.CLASSID_DRAGONKNIGHT_FEMALE:
			minStr = 13;
			minDex = 11;
			minCon = 14;
			minWis = 12;
			minCha = 8;
			minInt = 11;
			remainStats = 6;
			break;
		case L1PcInstance.CLASSID_ILLUSIONIST_MALE:
		case L1PcInstance.CLASSID_ILLUSIONIST_FEMALE:
			minStr = 11;
			minDex = 10;
			minCon = 12;
			minWis = 12;
			minCha = 8;
			minInt = 12;
			remainStats = 10;
			break;
		default:
			return false;
		}
		if (total != minStr + minDex + minCon + minWis + minCha + minInt
				+ remainStats)
			result = false;
		if (getStr() < minStr || getStr() > MaxAbility)
			result = false;
		if (getDex() < minDex || getDex() > MaxAbility)
			result = false;
		if (getCon() < minCon || getCon() > MaxAbility)
			result = false;
		if (getWis() < minWis || getWis() > MaxAbility)
			result = false;
		if (getCha() < minCha || getCha() > MaxAbility)
			result = false;
		if (getInt() < minInt || getInt() > MaxAbility)
			result = false;

		if (currentLevel > 50
				&& getAmount() - (highestLevel - 50 + getElixirCount() + 75) > 0)
			result = false;
		if (highestLevel < 51 && getAmount() > 75 + getElixirCount())
			result = false;
		if (highestLevel > 50 && highestLevel - 50 < getBonusAbility())
			result = false;

		return result;
	}

	public void addSp(int i) {
		sp += i;
	}

	public int getSp() {
		return getTrueSp() + sp;
	}

	public int getTrueSp() {
		return getMagicLevel() + getMagicBonus();
	}

	public int getMagicLevel() {
		if (character instanceof L1PcInstance)
			return ((L1PcInstance) character).getClassFeature().getMagicLevel(
					character.getLevel());
		return character.getLevel() / 4;
	}

	public int getMagicBonus() {
		int i = getTotalInt();
		if (i <= 5)
			return -2;
		else if (i <= 8)
			return -1;
		else if (i <= 11)
			return 0;
		else if (i <= 14)
			return 1;
		else if (i <= 17)
			return 2;
		else if (i <= 24)
			return i - 15;
		else if (i <= 35)
			return 10;
		else if (i <= 42)
			return 11;
		else if (i <= 49)
			return 12;
		else if (i <= 50)
			return 13;
		else
			return i - 25;
	}

	public void initStat(final int classId) {
		int minStr = 0, minDex = 0, minCon = 0, minWis = 0, minCha = 0, minInt = 0;

		switch (classId) {
		case L1PcInstance.CLASSID_PRINCE:
		case L1PcInstance.CLASSID_PRINCESS:
			minStr = 13;
			minDex = 10;
			minCon = 10;
			minWis = 11;
			minCha = 13;
			minInt = 10;
			break;
		case L1PcInstance.CLASSID_KNIGHT_MALE:
		case L1PcInstance.CLASSID_KNIGHT_FEMALE:
			minStr = 16;
			minDex = 12;
			minCon = 14;
			minWis = 9;
			minCha = 12;
			minInt = 8;
			break;
		case L1PcInstance.CLASSID_WIZARD_MALE:
		case L1PcInstance.CLASSID_WIZARD_FEMALE:
			minStr = 8;
			minDex = 7;
			minCon = 12;
			minWis = 12;
			minCha = 8;
			minInt = 12;
			break;
		case L1PcInstance.CLASSID_ELF_MALE:
		case L1PcInstance.CLASSID_ELF_FEMALE:
			minStr = 11;
			minDex = 12;
			minCon = 12;
			minWis = 12;
			minCha = 9;
			minInt = 12;
			break;
		case L1PcInstance.CLASSID_DARKELF_MALE:
		case L1PcInstance.CLASSID_DARKELF_FEMALE:
			minStr = 12;
			minDex = 15;
			minCon = 8;
			minWis = 10;
			minCha = 9;
			minInt = 11;
			break;
		case L1PcInstance.CLASSID_DRAGONKNIGHT_MALE:
		case L1PcInstance.CLASSID_DRAGONKNIGHT_FEMALE:
			minStr = 13;
			minDex = 11;
			minCon = 14;
			minWis = 12;
			minCha = 8;
			minInt = 11;
			break;
		case L1PcInstance.CLASSID_ILLUSIONIST_MALE:
		case L1PcInstance.CLASSID_ILLUSIONIST_FEMALE:
			minStr = 11;
			minDex = 10;
			minCon = 12;
			minWis = 12;
			minCha = 8;
			minInt = 12;
			break;
		default:
			break;
		}
		con = (byte) minCon;
		baseCon = (byte) minCon;
		str = (byte) minStr;
		baseStr = (byte) minStr;
		dex = (byte) minDex;
		baseDex = (byte) minDex;
		wis = (byte) minWis;
		baseWis = (byte) minWis;
		intel = (byte) minInt;
		baseInt = (byte) minInt;
		cha = (byte) minCha;
		baseCha = (byte) minCha;
	}

	public int[] getBaseStatDiff(int[] value) {
		int[] returnValue = new int[6];
		returnValue[0] = getBaseStr() - value[0];
		returnValue[1] = getBaseDex() - value[1];
		returnValue[2] = getBaseCon() - value[2];
		returnValue[3] = getBaseWis() - value[3];
		returnValue[4] = getBaseCha() - value[4];
		returnValue[5] = getBaseInt() - value[5];

		return returnValue;
	}

	public int[] getMinStat(final int classId) {

		int[] minabllity = new int[6];
		// int minStr, minDex, minCon, minWis, minCha, minInt, remainStats;
		switch (classId) {
		case L1PcInstance.CLASSID_PRINCE:
		case L1PcInstance.CLASSID_PRINCESS:
			minabllity[0] = 13;
			minabllity[1] = 10;
			minabllity[2] = 10;
			minabllity[3] = 11;
			minabllity[4] = 13;
			minabllity[5] = 10;
			break;
		case L1PcInstance.CLASSID_KNIGHT_MALE:
		case L1PcInstance.CLASSID_KNIGHT_FEMALE:
			minabllity[0] = 16;
			minabllity[1] = 12;
			minabllity[2] = 14;
			minabllity[3] = 9;
			minabllity[4] = 12;
			minabllity[5] = 8;
			break;
		case L1PcInstance.CLASSID_WIZARD_MALE:
		case L1PcInstance.CLASSID_WIZARD_FEMALE:
			minabllity[0] = 8;
			minabllity[1] = 7;
			minabllity[2] = 12;
			minabllity[3] = 12;
			minabllity[4] = 8;
			minabllity[5] = 12;
			break;
		case L1PcInstance.CLASSID_ELF_MALE:
		case L1PcInstance.CLASSID_ELF_FEMALE:
			minabllity[0] = 11;
			minabllity[1] = 12;
			minabllity[2] = 12;
			minabllity[3] = 12;
			minabllity[4] = 9;
			minabllity[5] = 12;
			break;
		case L1PcInstance.CLASSID_DARKELF_MALE:
		case L1PcInstance.CLASSID_DARKELF_FEMALE:
			minabllity[0] = 12;
			minabllity[1] = 15;
			minabllity[2] = 8;
			minabllity[3] = 10;
			minabllity[4] = 9;
			minabllity[5] = 11;
			break;
		case L1PcInstance.CLASSID_DRAGONKNIGHT_MALE:
		case L1PcInstance.CLASSID_DRAGONKNIGHT_FEMALE:
			minabllity[0] = 13;
			minabllity[1] = 11;
			minabllity[2] = 14;
			minabllity[3] = 12;
			minabllity[4] = 8;
			minabllity[5] = 11;
			break;
		case L1PcInstance.CLASSID_ILLUSIONIST_MALE:
		case L1PcInstance.CLASSID_ILLUSIONIST_FEMALE:
			minabllity[0] = 11;
			minabllity[1] = 10;
			minabllity[2] = 12;
			minabllity[3] = 12;
			minabllity[4] = 8;
			minabllity[5] = 12;
			break;
		default:

		}
		minabllity = getBaseStatDiff(minabllity);
		/*
		 * for(int i =0 ; i<minabllity.length; i++){
		 * System.out.println(minabllity[i]); }
		 */
		return minabllity;
	}
}
