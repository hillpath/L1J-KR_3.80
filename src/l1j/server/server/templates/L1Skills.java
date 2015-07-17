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
package l1j.server.server.templates;

public class L1Skills {
	public static final int ATTR_NONE = 0;

	public static final int ATTR_EARTH = 1;

	public static final int ATTR_FIRE = 2;

	public static final int ATTR_WATER = 4;

	public static final int ATTR_WIND = 8;

	public static final int ATTR_RAY = 16;

	public static final int TYPE_PROBABILITY = 1;

	public static final int TYPE_CHANGE = 2;

	public static final int TYPE_CURSE = 4;

	public static final int TYPE_DEATH = 8;

	public static final int TYPE_HEAL = 16;

	public static final int TYPE_RESTORE = 32;

	public static final int TYPE_ATTACK = 64;

	public static final int TYPE_OTHER = 128;

	public static final int TARGET_TO_ME = 0;

	public static final int TARGET_TO_PC = 1;

	public static final int TARGET_TO_NPC = 2;

	public static final int TARGET_TO_CLAN = 4;

	public static final int TARGET_TO_PARTY = 8;

	public static final int TARGET_TO_PET = 16;

	public static final int TARGET_TO_PLACE = 32;

	private String _name;

	private String _target;

	private String _nameId;

	private boolean _isThrough;

	private int _skillId;

	private int _skillLevel;

	private int _skillNumber;

	private int _damageValue;

	private int _damageDice;

	private int _damageDiceCount;

	private int _probabilityValue;

	private int _probabilityDice;

	private int _attr;

	private int _type;

	private int _ranged;

	private int _area;

	private int _id;

	private int _actionId;

	private int _castGfx;

	private int _castGfx2;

	private int _sysmsgIdHappen;

	private int _sysmsgIdStop;

	private int _sysmsgIdFail;

	private int _lawful;

	private int _mpConsume;

	private int _hpConsume;

	private int _itmeConsumeId;

	private int _itmeConsumeCount;

	private int _reuseDelay;

	private int _buffDuration;

	private int _targetTo; // ´ë»ó 0:ÀÚ½Å 1:PC 2:NPC 4:Ç÷¸Í 8:ÆÄÆ¼ 16:Æê 32:Àå¼Ò

	public int getSkillId() {
		return _skillId;
	}

	public void setSkillId(int i) {
		_skillId = i;
	}

	public String getName() {
		return _name;
	}

	public void setName(String s) {
		_name = s;
	}

	public int getSkillLevel() {
		return _skillLevel;
	}

	public void setSkillLevel(int i) {
		_skillLevel = i;
	}

	public int getSkillNumber() {
		return _skillNumber;
	}

	public void setSkillNumber(int i) {
		_skillNumber = i;
	}

	public int getMpConsume() {
		return _mpConsume;
	}

	public void setMpConsume(int i) {
		_mpConsume = i;
	}

	public int getHpConsume() {
		return _hpConsume;
	}

	public void setHpConsume(int i) {
		_hpConsume = i;
	}

	public int getItemConsumeId() {
		return _itmeConsumeId;
	}

	public void setItemConsumeId(int i) {
		_itmeConsumeId = i;
	}

	public int getItemConsumeCount() {
		return _itmeConsumeCount;
	}

	public void setItemConsumeCount(int i) {
		_itmeConsumeCount = i;
	}

	public int getReuseDelay() {
		return _reuseDelay;
	}

	public void setReuseDelay(int i) {
		_reuseDelay = i;
	}

	public int getBuffDuration() {
		return _buffDuration;
	}

	public void setBuffDuration(int i) {
		_buffDuration = i;
	}

	public String getTarget() {
		return _target;
	}

	public void setTarget(String s) {
		_target = s;
	}

	public int getTargetTo() {
		return _targetTo;
	}

	public void setTargetTo(int i) {
		_targetTo = i;
	}

	public int getDamageValue() {
		return _damageValue;
	}

	public void setDamageValue(int i) {
		_damageValue = i;
	}

	public int getDamageDice() {
		return _damageDice;
	}

	public void setDamageDice(int i) {
		_damageDice = i;
	}

	public int getDamageDiceCount() {
		return _damageDiceCount;
	}

	public void setDamageDiceCount(int i) {
		_damageDiceCount = i;
	}

	public int getProbabilityValue() {
		return _probabilityValue;
	}

	public void setProbabilityValue(int i) {
		_probabilityValue = i;
	}

	public int getProbabilityDice() {
		return _probabilityDice;
	}

	public void setProbabilityDice(int i) {
		_probabilityDice = i;
	}

	public int getAttr() {
		return _attr;
	}

	public void setAttr(int i) {
		_attr = i;
	}

	public int getType() {
		return _type;
	}

	public void setType(int i) {
		_type = i;
	}

	public int getLawful() {
		return _lawful;
	}

	public void setLawful(int i) {
		_lawful = i;
	}

	public int getRanged() {
		return _ranged;
	}

	public void setRanged(int i) {
		_ranged = i;
	}

	public int getArea() {
		return _area;
	}

	public void setArea(int i) {
		_area = i;
	}

	public boolean isThrough() {
		return _isThrough;
	}

	public void setThrough(boolean flag) {
		_isThrough = flag;
	}

	public int getId() {
		return _id;
	}

	public void setId(int i) {
		_id = i;
	}

	public String getNameId() {
		return _nameId;
	}

	public void setNameId(String s) {
		_nameId = s;
	}

	public int getActionId() {
		return _actionId;
	}

	public void setActionId(int i) {
		_actionId = i;
	}

	public int getCastGfx() {
		return _castGfx;
	}

	public void setCastGfx(int i) {
		_castGfx = i;
	}

	public int getCastGfx2() {
		return _castGfx2;
	}

	public void setCastGfx2(int i) {
		_castGfx2 = i;
	}

	public int getSysmsgIdHappen() {
		return _sysmsgIdHappen;
	}

	public void setSysmsgIdHappen(int i) {
		_sysmsgIdHappen = i;
	}

	public int getSysmsgIdStop() {
		return _sysmsgIdStop;
	}

	public void setSysmsgIdStop(int i) {
		_sysmsgIdStop = i;
	}

	public int getSysmsgIdFail() {
		return _sysmsgIdFail;
	}

	public void setSysmsgIdFail(int i) {
		_sysmsgIdFail = i;
	}
}
