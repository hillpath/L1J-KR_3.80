/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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

import javolution.util.FastTable;

import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Ability;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_DelSkill;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import static l1j.server.server.model.skill.L1SkillId.*;

public class L1EquipmentSlot {

	private L1PcInstance _owner;

	private FastTable<L1ArmorSet> _currentArmorSet;

	private L1ItemInstance _weapon;

	private FastTable<L1ItemInstance> _armors;

	public L1EquipmentSlot(L1PcInstance owner) {
		_owner = owner;

		_armors = new FastTable<L1ItemInstance>();
		_currentArmorSet = new FastTable<L1ArmorSet>();
	}
	
	private void setWeapon(L1ItemInstance weapon) {
		  int Meditate = weapon.getEnchantLevel();
		  if (weapon.getItemId() == 4678) { //명지코드
			   switch(Meditate){
			   case 1: _owner.addMpr(1);break;
			   case 2: _owner.addMpr(2);break;
			   case 3: _owner.addMpr(3);break;
			   case 4: _owner.addMpr(4);break;
			   case 5: _owner.addMpr(5);break;
			   case 6: _owner.addMpr(6);break;
			   case 7: _owner.addMpr(7);break;
			   case 8: _owner.addMpr(8);break;
			   case 9: _owner.addMpr(9);break;
			   case 10: _owner.addMpr(10);break;
			   case 11: _owner.addMpr(11);break;
			   case 12: _owner.addMpr(12);break;
			   case 13: _owner.addMpr(13);break;
			   case 14: _owner.addMpr(14);break;
			   case 15: _owner.addMpr(15);break;
			   default:
			   }
			  }		
		_owner.setWeapon(weapon);
		_owner.setCurrentWeapon(weapon.getItem().getType1());
		weapon.startEquipmentTimer(_owner);
		_weapon = weapon;
		// 수결지 인챈당스펠
		if (weapon.getItemId() == 134 || weapon.getItemId() == 134000) { // 수결지 코드번호
			_owner.getAbility().addSp((weapon.getEnchantLevel() * 1));
			_owner.sendPackets(new S_SPMR(_owner));
		}// 수결지 인챈당 스펠 착용시 sp 증가부분
		if(weapon.getDmgByMagic() > 0){
			_owner.sendPackets(new S_SystemMessage("근거리 추가 타격+2"));
		}
		int itemId = weapon.getItem().getItemId();
		if (itemId >= 11011 && itemId <= 11013) {
			L1PolyMorph.doPoly(_owner, 8768, 0, L1PolyMorph.MORPH_BY_ITEMMAGIC);
		} // 추가
	}

	public L1ItemInstance getWeapon() {
		return _weapon;
	}

	private void setArmor(L1ItemInstance armor) {
		L1Item item = armor.getItem();
		int itemlvl = armor.getEnchantLevel();
		int itemtype = armor.getItem().getType();
		int itemId = armor.getItem().getItemId();
		int itemgrade = armor.getItem().getGrade();
		int RegistLevel = armor.getRegistLevel();// 인나티

		if (itemtype >= 8 && itemtype <= 12) {
			_owner.getAC().addAc(item.get_ac() - armor.getAcByMagic());
		} else {
			_owner.getAC().addAc(
					item.get_ac() - armor.getEnchantLevel()
							- armor.getAcByMagic());
		}
		/**안타가호*/
		if (itemId == 20130 || itemId == 420100
				|| itemId == 420101 || itemId == 420102
				|| itemId == 420103) {// /아이템번호
			_owner.sendPackets(new S_SkillSound(_owner.getId(), 4902));// 이펙
			_owner.broadcastPacket(new S_SkillSound(_owner.getId(), 4902));// 이펙
		}
		/**안타가호*/
		/**린드가호*/
		if (itemId == 20108 || itemId == 420108
				|| itemId == 420109 || itemId == 420110
				|| itemId == 420111) {// /아이템번호
			_owner.sendPackets(new S_SkillSound(_owner.getId(), 5528));// 이펙
			_owner.broadcastPacket(new S_SkillSound(_owner.getId(), 5528));// 이펙
		}
		/**린드가호*/
		/**발라가호*/
		if (itemId == 20119 || itemId == 420114
				|| itemId == 420113 || itemId == 420112
				|| itemId == 420115) {// /아이템번호
			_owner.sendPackets(new S_SkillSound(_owner.getId(), 2753));// 이펙
			_owner.broadcastPacket(new S_SkillSound(_owner.getId(), 2753));// 이펙
		}
		/**발라가호*/
		_owner.addDamageReductionByArmor(item.getDamageReduction());
		_owner.addWeightReduction(item.getWeightReduction());
		_owner.addHitupByArmor(item.getHitup());
		_owner.addDmgupByArmor(item.getDmgup());
		_owner.addBowHitupByArmor(item.getBowHitup());
		_owner.addBowDmgupByArmor(item.getBowDmgup());
		_owner.getResistance().addEarth(item.get_defense_earth());
		_owner.getResistance().addWind(item.get_defense_wind());
		_owner.getResistance().addWater(item.get_defense_water());
		_owner.getResistance().addFire(item.get_defense_fire());
		_owner.getResistance().addStun(item.get_regist_stun());
		_owner.getResistance().addPetrifaction(item.get_regist_stone());
		_owner.getResistance().addSleep(item.get_regist_sleep());
		_owner.getResistance().addFreeze(item.get_regist_freeze());
		_owner.getResistance().addHold(item.get_regist_sustain());
		_owner.getResistance().addBlind(item.get_regist_blind());

		_armors.add(armor);

		for (L1ArmorSet armorSet : L1ArmorSet.getAllSet()) {
			if (armorSet.isPartOfSet(itemId) && armorSet.isValid(_owner)) {
				if (armor.getItem().getType2() == 2
						&& armor.getItem().getType() == 9) {
					if (!armorSet.isEquippedRingOfArmorSet(_owner)) {
						armorSet.giveEffect(_owner);
						_currentArmorSet.add(armorSet);
					}
				} else {
					armorSet.giveEffect(_owner);
					_currentArmorSet.add(armorSet);
				}
			}
		}
//		 판도라 티셔츠
		if (itemtype == 3 ){
			switch(armor.getPandoraT()){
			case 1: // 스턴 문양
				_owner.getResistance().addStun(10);
				break;
			case 2: // 홀드 문양
				_owner.getResistance().addHold(10);
				break;
			case 3: // 회복 문양
				_owner.addHpr(1);
				_owner.addMpr(1);
				break;
			case 4: // 강철 문양 (ac-1)
				_owner.getAC().addAc(-1);
				break;
			case 5: // 멸마 문양
				_owner.getResistance().addMr(10);
				break;
			case 6: // 체력 문양
				_owner.addMaxHp(50);
				break;
			case 7: //  마나 문양
				_owner.addMaxMp(30);
				break;
			case 8: // 정령 문양 (4대 속성 10)
				_owner.getResistance().addAllNaturalResistance(10);
				break;
			case 9: // 석화 문양
				_owner.getResistance().addEarth(10);
				break;
			}
		}
		if (itemId == 425108 || itemId == 425107 || itemId == 425106
				|| itemId == 21028 || itemId == 21029 || itemId == 21030 || itemId == 21031
				|| itemId == 21032 || itemId == 21033 ||( itemId >= 490000 && itemId <= 490017)) {// 인나티
			_owner.getResistance().addFire(RegistLevel * 2);
			_owner.getResistance().addWind(RegistLevel * 2);
			_owner.getResistance().addEarth(RegistLevel * 2);
			_owner.getResistance().addWater(RegistLevel * 2);
	}else if(itemId == 500007){ //룸티스의 붉은빛귀걸이
		switch(itemlvl){ 
		case 1:
		_owner.addMaxHp(20);
		break;
		case 2:
		_owner.addMaxHp(30); 
		break;
		case 3:
		_owner.addMaxHp(40);
		_owner.addDamageReductionByArmor(1);
		break;
		case 4:
		_owner.addMaxHp(45);
		_owner.addDamageReductionByArmor(1);
		break;
		case 5:
		_owner.addMaxHp(50);
		_owner.addDamageReductionByArmor(2);
		break;
		case 6:
		_owner.addMaxHp(55);
		_owner.addDamageReductionByArmor(2);
		break;
		case 7:
		_owner.addMaxHp(60);
		_owner.addDamageReductionByArmor(3);
		break;
		case 8:
		_owner.addMaxHp(65);
		_owner.addDamageReductionByArmor(3);
		break; 
		default:
		}
		}else if(itemId == 500008){ //룸티스의 푸른빛귀걸이
		switch(itemlvl){
		case 5: 
		_owner.getAC().addAc(-1); 
		break;
		case 6: 
		_owner.getAC().addAc(-2); 
		break;
		case 7: 
		_owner.getAC().addAc(-2); 
		break;
		case 8: 
		_owner.getAC().addAc(-3); 
		break; 
		default:
		}
		}else if(itemId == 500009){ //룸티스의 보랏빛귀걸이
		switch(itemlvl){
		case 1:
		_owner.addMaxMp(10);
		_owner.getResistance().addMr(3);
		break;
		case 2:
		_owner.addMaxMp(15);
		_owner.getResistance().addMr(4); 
		break;
		case 3:
		_owner.addMaxMp(20);
		_owner.getResistance().addMr(5);
		break;
		case 4:
		_owner.addMaxMp(24);
		_owner.getResistance().addMr(6);
		break;
		case 5:
		_owner.addMaxMp(28);
		_owner.getResistance().addMr(7);
		_owner.getAbility().addSp(1);
		break;
		case 6:
		_owner.addMaxMp(31);
		_owner.getResistance().addMr(8);
		_owner.getAbility().addSp(1);
		break;
		case 7:
		_owner.addMaxMp(34);
		_owner.getResistance().addMr(9);
		_owner.getAbility().addSp(2);
		break;
		case 8:
		_owner.addMaxMp(36);
		_owner.getResistance().addMr(10);
		_owner.getAbility().addSp(2);
		break; 
		default:
		}
		}
		//////////////////룸티스///////////////////
		if (itemId == 423014) {
			_owner.startAHRegeneration();
		}
		if (itemId == 423015) {
			_owner.startSHRegeneration();
		}
		if (itemId == 20380) {
			_owner.startHalloweenRegeneration();
		}
		if (itemId == 20077 || itemId == 20062 || itemId == 120077) {
			if (!_owner.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.INVISIBILITY)) {
				for (L1DollInstance doll : _owner.getDollList().values()) {
					doll.deleteDoll();
					_owner.sendPackets(new S_SkillIconGFX(56, 0));
					_owner.sendPackets(new S_OwnCharStatus(_owner));
				}
				_owner.getSkillEffectTimerSet().killSkillEffectTimer(
						L1SkillId.BLIND_HIDING);
				_owner.getSkillEffectTimerSet().setSkillEffect(
						L1SkillId.INVISIBILITY, 0);
				_owner.sendPackets(new S_Invis(_owner.getId(), 1));
				Broadcaster.broadcastPacket(_owner, new S_Invis(_owner.getId(),
						1));
			}
		}
		if (itemId == 20288) {
			_owner.sendPackets(new S_Ability(1, true));
		}
		if (itemId == 20036) {
			_owner.sendPackets(new S_Ability(3, true));
		}
		if (itemId == 20207) {
			_owner.sendPackets(new S_SkillIconBlessOfEva(_owner.getId(), -1));
		}
		if (itemId == 20383) {
			if (armor.getChargeCount() != 0) {
				armor.setChargeCount(armor.getChargeCount() - 1);
				_owner.getInventory().updateItem(armor,
						L1PcInventory.COL_CHARGE_COUNT);
			}
		}
		if (itemtype >= 8 && itemtype <= 12) {
			if (itemlvl > 0) {//[본섭화]하급악세사리류2012년7월12일
					    if(itemgrade == 2){ 
					     _owner.addMaxMp(itemlvl);
					     if(itemlvl > 5){
					      _owner.getAbility().addSp(1);
					     }
					    /* if(itemlvl == 7){
					      _owner.getAbility().addSp(2);
					     }
					     if(itemlvl == 8){
					      _owner.getAbility().addSp(3);
					     }
					     if(itemlvl == 9){
					      _owner.getAbility().addSp(4);
					     }*/
					    }else if(itemgrade == 1){
					     _owner.addMaxHp(itemlvl * 2);     
					     if(itemlvl > 5){
					      _owner.getResistance().addMr(itemlvl-5); 
					     }
					    }else if(itemgrade == 0){
					     _owner.getResistance().addAllNaturalResistance(itemlvl);
					     if(itemlvl > 5) { 
					      _owner.addHpr(1); 
					      _owner.addMpr(1); 
					     }
					     if(itemlvl > 6) { 
						      _owner.addHpr(2); 
						      _owner.addMpr(2); 
						     }
					     if(itemlvl > 7) { 
						      _owner.addHpr(3); 
						      _owner.addMpr(3); 
						     }
					/////////순백의반지////////////
				} else if (itemgrade == 3) {
					switch (itemlvl) {
					case 1:
						_owner.addMaxHp(15);
						break;
					case 2:
						_owner.addMaxHp(20);
						_owner.getAC().addAc(-1);
						break;
					case 3:
						_owner.addMaxHp(25);
						_owner.getAC().addAc(-2);
						break;
					case 4:
						_owner.addMaxHp(30);
						_owner.getAC().addAc(-3);
						break;
					case 5:
						_owner.addMaxHp(35);
						_owner.getAC().addAc(-3);
						_owner.addDmgup(1);
						_owner.addBowDmgup(1);
						break;
					case 6:
						_owner.addMaxHp(40);
						_owner.getAC().addAc(-3);
						_owner.addDmgup(2);
						_owner.addBowDmgup(2);
						break;
					case 7:
						_owner.addMaxHp(45);
						_owner.getAC().addAc(-3);
						_owner.addDmgup(3);
						_owner.addBowDmgup(3);
						break;
					case 8:
						_owner.addMaxHp(50);
						_owner.getAC().addAc(-3);
						_owner.addDmgup(4);
						_owner.addBowDmgup(4);
						break;
					default:
					}
					// //////순백의반지/////////////
				}
			}
		}
		armor.startEquipmentTimer(_owner);
	}
	public FastTable<L1ItemInstance> getArmors() {
		return _armors;
	}
	
	private void removeWeapon(L1ItemInstance weapon) {
		 /**명상의지팡이*/
		  int Meditate = weapon.getEnchantLevel();
		  if (weapon.getItemId() == 4678) { //명지코드
		   switch(Meditate){
		   case 1: _owner.addMpr(-1);break;
		   case 2: _owner.addMpr(-2);break;
		   case 3: _owner.addMpr(-3);break;
		   case 4:_owner.addMpr(-4);break;
		   case 5: _owner.addMpr(-5);break;
		   case 6: _owner.addMpr(-6);break;
		   case 7:_owner.addMpr(-7);break;
		   case 8: _owner.addMpr(-8);break;
		   case 9:_owner.addMpr(-9);break;
		   case 10:_owner.addMpr(-10);break;
		   case 11:_owner.addMpr(-11);break;
		   case 12:_owner.addMpr(-12);break;
		   case 13:_owner.addMpr(-13);break;
		   case 14:_owner.addMpr(-14);break;
		   case 15:_owner.addMpr(-15);break;
		   default:
		   }
		  }
		  /**명상의지팡이*/
		_owner.setWeapon(null);
		_owner.setCurrentWeapon(0);
		weapon.stopEquipmentTimer();
		_weapon = null;
		// 수결지 인챈당스펠
		if (weapon.getItemId() == 134 || weapon.getItemId() == 134000 ) { // 수결지 코드번호
			_owner.getAbility().addSp(-(weapon.getEnchantLevel() * 1));
			_owner.sendPackets(new S_SPMR(_owner));
		}
		// 수결지 인챈당 스펠
		// 착용해제시 sp 회수부분
		if (_owner.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.COUNTER_BARRIER)) {
			_owner.getSkillEffectTimerSet().removeSkillEffect(
					L1SkillId.COUNTER_BARRIER);
		}
		int itemId = weapon.getItem().getItemId();
		if (itemId >= 11011 && itemId <= 11013) {
			L1PolyMorph.undoPoly(_owner);
		}// 추가
	}

	private void removeArmor(L1ItemInstance armor) {
		L1Item item = armor.getItem();
		int itemId = armor.getItem().getItemId();
		int itemlvl = armor.getEnchantLevel();
		int itemtype = armor.getItem().getType();
		int itemgrade = armor.getItem().getGrade();
		int RegistLevel = armor.getRegistLevel();// 인나티

		if (itemtype >= 8 && itemtype <= 12) {
			_owner.getAC().addAc(-(item.get_ac() - armor.getAcByMagic()));
		} else {
			_owner.getAC().addAc(
					-(item.get_ac() - armor.getEnchantLevel() - armor
							.getAcByMagic()));
		}
		/**안타가호*/
		if (itemId == 20130 || itemId == 420100
				|| itemId == 420101 || itemId == 420102
				|| itemId == 420103) {// /아이템번호
		}
		/**안타가호*/
		/**린드가호*/
		if (itemId == 20108 || itemId == 420108
				|| itemId == 420109 || itemId == 420110
				|| itemId == 420111) {// /아이템번호
		}
		/**린드가호*/
		/**발라가호*/
		if (itemId == 20119 || itemId == 420114
				|| itemId == 420113 || itemId == 420112
				|| itemId == 420115) {// /아이템번호
		}
		/**발라가호*/
		_owner.addDamageReductionByArmor(-item.getDamageReduction());
		_owner.addWeightReduction(-item.getWeightReduction());
		_owner.addHitupByArmor(-item.getHitup());
		_owner.addDmgupByArmor(-item.getDmgup());
		_owner.addBowHitupByArmor(-item.getBowHitup());
		_owner.addBowDmgupByArmor(-item.getBowDmgup());
		_owner.getResistance().addEarth(-item.get_defense_earth());
		_owner.getResistance().addWind(-item.get_defense_wind());
		_owner.getResistance().addWater(-item.get_defense_water());
		_owner.getResistance().addFire(-item.get_defense_fire());
		_owner.getResistance().addStun(-item.get_regist_stun());
		_owner.getResistance().addPetrifaction(-item.get_regist_stone());
		_owner.getResistance().addSleep(-item.get_regist_sleep());
		_owner.getResistance().addFreeze(-item.get_regist_freeze());
		_owner.getResistance().addHold(-item.get_regist_sustain());
		_owner.getResistance().addBlind(-item.get_regist_blind());

		for (L1ArmorSet armorSet : L1ArmorSet.getAllSet()) {
			if (armorSet.isPartOfSet(itemId)
					&& _currentArmorSet.contains(armorSet)
					&& !armorSet.isValid(_owner)) {
				armorSet.cancelEffect(_owner);
				_currentArmorSet.remove(armorSet);
			}
		}
//		 판도라 티셔츠
		if (itemtype == 3 ){
				switch(armor.getPandoraT()){
				case 1: // 스턴 문양
					_owner.getResistance().addStun(-10);
					break;
				case 2: // 홀드 문양
					_owner.getResistance().addHold(-10);
					break;
				case 3: // 회복 문양
					_owner.addHpr(-1);
					_owner.addMpr(-1);
					break;
				case 4: // 강철 문양 (ac-1)
					_owner.getAC().addAc(1);
					break;
				case 5: // 멸마 문양
					_owner.getResistance().addMr(-10);
					break;
				case 6: // 체력 문양
					_owner.addMaxHp(-50);
					break;
				case 7: //  마나 문양
					_owner.addMaxMp(-30);
					break;
				case 8: // 정령 문양 (4대 속성 10)
					_owner.getResistance().addAllNaturalResistance(-10);
					break;
				case 9: // 석화 문양
					_owner.getResistance().addEarth(-10);
					break;
				}
			}
		if (itemId == 425108 || itemId == 425107 || itemId == 425106
				|| itemId == 21028 || itemId == 21029 || itemId == 21030 || itemId == 21031
				|| itemId == 21032 || itemId == 21033 ||( itemId >= 490000 && itemId <= 490017)) { // 인나티
			_owner.getResistance().addFire(-RegistLevel * 2);
			_owner.getResistance().addWind(-RegistLevel * 2);
			_owner.getResistance().addEarth(-RegistLevel * 2);
			_owner.getResistance().addWater(-RegistLevel * 2);
	}else if(itemId == 500007){ //룸티스의 붉은빛귀걸이
		switch(itemlvl){ 
		case 1:
		_owner.addMaxHp(-20);
		break;
		case 2:
		_owner.addMaxHp(-30); 
		break;
		case 3:
		_owner.addMaxHp(-40);
		_owner.addDamageReductionByArmor(-1);
		break;
		case 4:
		_owner.addMaxHp(-45);
		_owner.addDamageReductionByArmor(-1);
		break;
		case 5:
		_owner.addMaxHp(-50);
		_owner.addDamageReductionByArmor(-2);
		break;
		case 6:
		_owner.addMaxHp(-55);
		_owner.addDamageReductionByArmor(-2);
		break;
		case 7:
		_owner.addMaxHp(-60);
		_owner.addDamageReductionByArmor(-3);
		break;
		case 8:
		_owner.addMaxHp(-65);
		_owner.addDamageReductionByArmor(-3);
		break; 
		default:
		}
		}else if(itemId == 500008){ //룸티스의 푸른빛귀걸이
		switch(itemlvl){
		case 5: 
		_owner.getAC().addAc(1); 
		break;
		case 6: 
		_owner.getAC().addAc(2); 
		break;
		case 7: 
		_owner.getAC().addAc(2); 
		break;
		case 8: 
		_owner.getAC().addAc(3);
		break; 
		default:
		}
		}else if(itemId == 500009){ //룸티스의 보랏빛귀걸이
		switch(itemlvl){
		case 1:
		_owner.addMaxMp(-10);
		_owner.getResistance().addMr(-3);
		break;
		case 2:
		_owner.addMaxMp(-15);
		_owner.getResistance().addMr(-4); 
		break;
		case 3:
		_owner.addMaxMp(-20);
		_owner.getResistance().addMr(-5);
		break;
		case 4:
		_owner.addMaxMp(-24);
		_owner.getResistance().addMr(-6);
		break;
		case 5:
		_owner.addMaxMp(-28);
		_owner.getResistance().addMr(-7);
		_owner.getAbility().addSp(-1);
		break;
		case 6:
		_owner.addMaxMp(-31);
		_owner.getResistance().addMr(-8);
		_owner.getAbility().addSp(1);
		break;
		case 7:
		_owner.addMaxMp(-34);
		_owner.getResistance().addMr(-9);
		_owner.getAbility().addSp(-2);
		break;
		case 8:
		_owner.addMaxMp(-36);
		_owner.getResistance().addMr(-10);
		_owner.getAbility().addSp(-2);
		break; 
		default:
		}
		}
		/////////////////////룸티스귀걸이///////////////
		if (itemId == 423014) {
			_owner.stopAHRegeneration();
		}
		if (itemId == 423015) {
			_owner.stopSHRegeneration();
		}
		if (itemId == 20380) {
			_owner.stopHalloweenRegeneration();
		}
		if (itemId == 20077 || itemId == 20062 || itemId == 120077) {
			_owner.delInvis();
		}
		if (itemId == 20288) {
			_owner.sendPackets(new S_Ability(1, false));
		}
		if (itemId == 20036) {
			_owner.sendPackets(new S_Ability(3, false));
		}
		if (itemId == 20207) {
			_owner.sendPackets(new S_SkillIconBlessOfEva(_owner.getId(), 0));
		}


		if (itemtype >= 8 && itemtype <= 12) {
			if (itemlvl > 0) {//[본섭화]하급악세사리류2012년7월12일
					    if(itemgrade == 2){ //하급
					     _owner.addMaxMp(-itemlvl);
					     if(itemlvl > 5){
					      _owner.getAbility().addSp(-1);
					     } 
					    /* if(itemlvl == 7){
					      _owner.getAbility().addSp(-2);     
					     }
					     if(itemlvl == 8){
					      _owner.getAbility().addSp(-3);     
					     }
					     if(itemlvl == 9){
					      _owner.getAbility().addSp(-4);     
					     }*/
					    }else if(itemgrade == 1){ //중급
					     _owner.addMaxHp(-(itemlvl * 2));     
					     if(itemlvl > 5){
					      _owner.getResistance().addMr(-(itemlvl-5));
					     }
					    }else if(itemgrade == 0){ //상급
					     _owner.getResistance().addAllNaturalResistance(-itemlvl);   
					     if(itemlvl > 5) {
					      _owner.addHpr(-1);
					      _owner.addMpr(-1);
					     }
					     if(itemlvl > 6) { 
						      _owner.addHpr(-2); 
						      _owner.addMpr(-2); 
						     }
					     if(itemlvl > 7) { 
						      _owner.addHpr(-3); 
						      _owner.addMpr(-3); 
						     }
					// ///////순백의반지////////////
				} else if (itemgrade == 3) {
					switch (itemlvl) {
					case 1:
						_owner.addMaxHp(-15);
						break;
					case 2:
						_owner.addMaxHp(-20);
						_owner.getAC().addAc(1);
						break;
					case 3:
						_owner.addMaxHp(-25);
						_owner.getAC().addAc(2);
						break;
					case 4:
						_owner.addMaxHp(-30);
						_owner.getAC().addAc(3);
						break;
					case 5:
						_owner.addMaxHp(-35);
						_owner.getAC().addAc(3);
						_owner.addDmgup(-1);
						_owner.addBowDmgup(-1);
						break;
					case 6:
						_owner.addMaxHp(-40);
						_owner.getAC().addAc(3);
						_owner.addDmgup(-2);
						_owner.addBowDmgup(-2);
						break;
					case 7:
						_owner.addMaxHp(-45);
						_owner.getAC().addAc(3);
						_owner.addDmgup(-3);
						_owner.addBowDmgup(-3);
						break;
					case 8:
						_owner.addMaxHp(-50);
						_owner.getAC().addAc(3);
						_owner.addDmgup(-4);
						_owner.addBowDmgup(-4);
						break;
					default:
					}
					// //////순백의반지/////////////
				}
			}
		}
		armor.stopEquipmentTimer();

		_armors.remove(armor);
	}

	public void set(L1ItemInstance equipment) {
		L1Item item = equipment.getItem();

		if (item.getType2() == 0) {
			return;
		}

		if (item.get_addhp() != 0) {
			_owner.addMaxHp(item.get_addhp());
		}
		if (item.get_addmp() != 0) {
			_owner.addMaxMp(item.get_addmp());
		}
		_owner.getAbility().addAddedStr(item.get_addstr());
		_owner.getAbility().addAddedCon(item.get_addcon());
		_owner.getAbility().addAddedDex(item.get_adddex());
		_owner.getAbility().addAddedInt(item.get_addint());
		_owner.getAbility().addAddedWis(item.get_addwis());
		if (item.get_addwis() != 0) {
			_owner.resetBaseMr();
		}
		_owner.getAbility().addAddedCha(item.get_addcha());

		int addMr = 0;
		addMr += equipment.getMr();
		if (item.getItemId() == 20236 && _owner.isElf()) {
			addMr += 5;
		}
		if (addMr != 0) {
			_owner.getResistance().addMr(addMr);
			_owner.sendPackets(new S_SPMR(_owner));
		}
		if (item.get_addsp() != 0) {
			_owner.getAbility().addSp(item.get_addsp());
			_owner.sendPackets(new S_SPMR(_owner));
		}
		if (item.isHasteItem()) {
			_owner.addHasteItemEquipped(1);
			_owner.removeHasteSkillEffect();
			if (_owner.getMoveState().getMoveSpeed() != 1) {
				_owner.getMoveState().setMoveSpeed(1);
				_owner.sendPackets(new S_SkillHaste(_owner.getId(), 1, -1));
				Broadcaster.broadcastPacket(_owner, new S_SkillHaste(_owner
						.getId(), 1, 0));
			}
		}
		if (item.getItemId() == 20383) {
			if (_owner.getSkillEffectTimerSet().hasSkillEffect(STATUS_BRAVE)) {
				_owner.getSkillEffectTimerSet().killSkillEffectTimer(
						STATUS_BRAVE);
				_owner.sendPackets(new S_SkillBrave(_owner.getId(), 0, 0));
				Broadcaster.broadcastPacket(_owner, new S_SkillBrave(_owner
						.getId(), 0, 0));
				_owner.getMoveState().setBraveSpeed(0);
			}
		}
		_owner.getEquipSlot().setMagicHelm(equipment);

		if (item.getType2() == 1) {
			setWeapon(equipment);
		} else if (item.getType2() == 2) {
			setArmor(equipment);
			_owner.sendPackets(new S_SPMR(_owner));
		}
	}

	public void remove(L1ItemInstance equipment) {
		L1Item item = equipment.getItem();
		if (item.getType2() == 0) {
			return;
		}

		if (item.get_addhp() != 0) {
			_owner.addMaxHp(-item.get_addhp());
		}
		if (item.get_addmp() != 0) {
			_owner.addMaxMp(-item.get_addmp());
		}
		_owner.getAbility().addAddedStr((byte) -item.get_addstr());
		_owner.getAbility().addAddedCon((byte) -item.get_addcon());
		_owner.getAbility().addAddedDex((byte) -item.get_adddex());
		_owner.getAbility().addAddedInt((byte) -item.get_addint());
		_owner.getAbility().addAddedWis((byte) -item.get_addwis());
		if (item.get_addwis() != 0) {
			_owner.resetBaseMr();
		}
		_owner.getAbility().addAddedCha((byte) -item.get_addcha());

		int addMr = 0;
		addMr -= equipment.getMr();
		if (item.getItemId() == 20236 && _owner.isElf()) {
			addMr -= 5;
		}
		if (addMr != 0) {
			_owner.getResistance().addMr(addMr);
			_owner.sendPackets(new S_SPMR(_owner));
		}
		if (item.get_addsp() != 0) {
			_owner.getAbility().addSp(-item.get_addsp());
			_owner.sendPackets(new S_SPMR(_owner));
		}
		if (item.isHasteItem()) {
			_owner.addHasteItemEquipped(-1);
			if (_owner.getHasteItemEquipped() == 0) {
				_owner.getMoveState().setMoveSpeed(0);
				_owner.sendPackets(new S_SkillHaste(_owner.getId(), 0, 0));
				Broadcaster.broadcastPacket(_owner, new S_SkillHaste(_owner
						.getId(), 0, 0));
			}
		}
		_owner.getEquipSlot().removeMagicHelm(_owner.getId(), equipment);

		if (item.getType2() == 1) {
			removeWeapon(equipment);
		} else if (item.getType2() == 2) {
			removeArmor(equipment);
		}
	}

	public void setMagicHelm(L1ItemInstance item) {
		switch (item.getItemId()) {
		case 20008:
			_owner.setSkillMastery(HASTE);
			_owner.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			break;
		case 20013:
			_owner.setSkillMastery(PHYSICAL_ENCHANT_DEX);
			_owner.setSkillMastery(HASTE);
			_owner.sendPackets(new S_AddSkill(0, 0, 0, 2, 0, 4, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			break;
		case 20014:
			_owner.setSkillMastery(HEAL);
			_owner.setSkillMastery(EXTRA_HEAL);
			_owner.sendPackets(new S_AddSkill(1, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			break;
		case 20015:
			_owner.setSkillMastery(ENCHANT_WEAPON);
			_owner.setSkillMastery(DETECTION);	
			_owner.setSkillMastery(FREEZING_BREATH);//용기사 디텍션
			_owner.setSkillMastery(AM_BREAK);//환술사 디텍션
			_owner.setSkillMastery(PHYSICAL_ENCHANT_STR);
			_owner.sendPackets(new S_AddSkill(0, 24, 0, 0, 0, 2, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			break;
		case 20023:
			_owner.setSkillMastery(GREATER_HASTE);
			_owner.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			break;
		}
	}

	public void removeMagicHelm(int objectId, L1ItemInstance item) {
		switch (item.getItemId()) {
		case 20008:
			if (!SkillsTable.getInstance().spellCheck(objectId, HASTE)) {
				_owner.removeSkillMastery(HASTE);
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			break;
		case 20013:
			if (!SkillsTable.getInstance().spellCheck(objectId,
					PHYSICAL_ENCHANT_DEX)) {
				_owner.removeSkillMastery(PHYSICAL_ENCHANT_DEX);
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 2, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId, HASTE)) {
				_owner.removeSkillMastery(HASTE);
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			break;
		case 20014:
			if (!SkillsTable.getInstance().spellCheck(objectId, HEAL)) {
				_owner.removeSkillMastery(HEAL);
				_owner.sendPackets(new S_DelSkill(1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId, EXTRA_HEAL)) {
				_owner.removeSkillMastery(EXTRA_HEAL);
				_owner.sendPackets(new S_DelSkill(0, 0, 4, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			break;
		case 20015:
			if (!SkillsTable.getInstance().spellCheck(objectId, ENCHANT_WEAPON)) {
				_owner.removeSkillMastery(ENCHANT_WEAPON);
				_owner.sendPackets(new S_DelSkill(0, 8, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId, DETECTION)) {
				_owner.removeSkillMastery(DETECTION);//마법사 디텍션
				_owner.sendPackets(new S_DelSkill(0, 16, 0, 0, 0, 0, 0, 0, 0,
								0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
								0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId,FREEZING_BREATH)) {
				_owner.removeSkillMastery(FREEZING_BREATH);//용기사 디텍션
				_owner.sendPackets(new S_DelSkill(0, 16, 0, 0, 0, 0, 0, 0, 0,
								0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
								0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId,AM_BREAK)) {
				_owner.removeSkillMastery(AM_BREAK);//환술사 디텍션
				_owner.sendPackets(new S_DelSkill(0, 16, 0, 0, 0, 0, 0, 0, 0,
								0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
								0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId,
					PHYSICAL_ENCHANT_STR)) {
				_owner.removeSkillMastery(PHYSICAL_ENCHANT_STR);
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 2, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			break;
		case 20023:
			if (!SkillsTable.getInstance().spellCheck(objectId, GREATER_HASTE)) {
				_owner.removeSkillMastery(GREATER_HASTE);
				_owner
						.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 0, 32, 0, 0,
								0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
								0, 0, 0));
			}
			break;
		}
	}

}