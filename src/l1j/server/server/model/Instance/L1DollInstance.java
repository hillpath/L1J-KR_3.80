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

package l1j.server.server.model.Instance;

import static l1j.server.server.model.skill.L1SkillId.STATUS_HASTE;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1World;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_DollPack;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Npc;
import server.controller.Doll.DollDeleteController;

public class L1DollInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;

	private GeneralThreadPool _threadPool = GeneralThreadPool.getInstance();
	public static final int DOLLTYPE_BUGBEAR = 0;// 버그

	public static final int DOLLTYPE_SUCCUBUS = 1;// 서큐

	public static final int DOLLTYPE_WAREWOLF = 2;// 늑인

	public static final int DOLLTYPE_STONEGOLEM = 3;// 골램

	public static final int DOLLTYPE_ELDER = 4;// 장로

	public static final int DOLLTYPE_CRUSTACEA = 5;// 시안

	public static final int DOLLTYPE_SEADANCER = 6;// 시댄서

	public static final int DOLLTYPE_SNOWMAN = 7;// 에티

	public static final int DOLLTYPE_COCATRIS = 8;// 코카

	public static final int DOLLTYPE_DRAGON_M = 9;

	public static final int DOLLTYPE_DRAGON_W = 10;

	public static final int DOLLTYPE_HIGH_DRAGON_M = 11;

	public static final int DOLLTYPE_HIGH_DRAGON_W = 12;

	public static final int DOLLTYPE_LAMIA = 13;// 라미아

	public static final int DOLLTYPE_HELPER = 20;// 쫄법사

	public static final int DOLLTYPE_SKELETON = 21; // 스켈레톤

	public static final int DOLLTYPE_SCARECROW = 16; // by허수아비

	public static final int DOLLTYPE_ETTIN = 17; // b판도라 에틴

	public static final int DOLLTYPE_SNOWMAN_A = 24; // 눈사람(A)

	public static final int DOLLTYPE_SNOWMAN_B = 25; // 눈사람(B)

	public static final int DOLLTYPE_SNOWMAN_C = 26; // 눈사람(C) //추가
	
	public static final int DOLLTYPE_RANKING_ONE = 27; //마법인형 : 블레그
	
	public static final int DOLLTYPE_RANKING_TWO = 28; //마법인형 : 레데그
	
	public static final int DOLLTYPE_RANKING_THREE = 29; //마법인형 : 엘레그
	
	public static final int DOLLTYPE_RANKING_FOR = 30; //마법인형 : 그레그
	
	public static final int DOLLTYPE_PSY_CHAMPION = 34; //싸이

	public static final int DOLLTYPE_PSY_BIRD = 35;//싸이
	
	public static final int DOLLTYPE_PSY_GANGNAM_STYLE = 36;//싸이
	
	public static final int DOLLTYPE_RANKING_ONE1 = 37;  
	public static final int DOLLTYPE_RANKING_TWO2 = 38;  
	public static final int DOLLTYPE_RANKING_THREE3 = 39;   
	public static final int DOLLTYPE_RANKING = 40;   


    //public static final int DOLL_TIME = 1800000;

	private static Random _random = new Random(System.nanoTime());

	private int _dollType;

	private int _itemObjId;

	private ScheduledFuture<?> _future = null;

	private static int Buff[] = { 26, 42, 43, 79 }; // 덱스, 힘, 헤이, 어벤

	public long DollTime = 0;

	// 타겟이 없는 경우의 처리
	@Override
	public boolean noTarget() {
		if (_master.isDead()
				|| _master.isInvisble()
				|| _master.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.INVISIBILITY)
				|| _master.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.BLIND_HIDING)) {
			_master.sendPackets(new S_SkillIconGFX(56, 0));//버프아이콘추가
			_master.sendPackets(new S_OwnCharStatus(_master));
			deleteDoll();
			return true;
		} else if (_master != null && _master.getMapId() == getMapId()) {
			if (getLocation().getTileLineDistance(_master.getLocation()) > 2) {
				int dir = moveDirection(_master.getX(), _master.getY());

				if (dir == -1) { // 이동 가능 지역
					teleport(_master.getX(), _master.getY(), getMoveState()
							.getHeading());
					setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
				} else {
					setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
				}
			}
			if (getLocation().getTileLineDistance(_master.getLocation()) > 5) { // 추가
				teleport(_master.getX(), _master.getY(), getMoveState()
						.getHeading());

			}
		} else {
			deleteDoll();
			return true;
		}
		return false;
	}

	// 시간 계측용
	class DollTimer implements Runnable {

		public void run() {
			if (_destroyed) { // 이미 파기되어 있지 않은가 체크
				return;
			}
			deleteDoll();
		}
	}

	class HelpTimer implements Runnable {

		public void run() {
			if (_destroyed) { // 이미 파기되어 있지 않은가 체크
				return;
			}
			getHelperAction();
		}
	}

	public L1DollInstance(L1Npc template, L1PcInstance master, int dollType,int itemObjId, int dollTime) {
		super(template);
		setId(ObjectIdFactory.getInstance().nextId());

		setDollType(dollType);
		setItemObjId(itemObjId);
		DollTime = System.currentTimeMillis() + dollTime;
		DollDeleteController.getInstance().addNpcDelete(this);

		setMaster(master);
		setX(master.getX() + _random.nextInt(5) - 2);
		setY(master.getY() + _random.nextInt(5) - 2);
		setMap(master.getMapId());
		getMoveState().setHeading(5);
		setLightSize(template.getLightSize());

		L1World.getInstance().storeObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		master.addDoll(this);
		if (!isAiRunning()) {
			startAI();
		}
		if (isMpRegeneration()) {
			master.startMpRegenerationByDoll();
		}
		if (isHpRegeneration()) {
			master.startHpRegenerationByDoll();
		}

		int type = getDollType();
		
		
		
		////  싸이    /////
		if (type == DOLLTYPE_PSY_CHAMPION) {
		   _master.addDmgup(2);
		   _master.addMaxHp(30);
		   _master.sendPackets(new S_OwnCharStatus2(_master));
		}
		if (type == DOLLTYPE_PSY_BIRD) {
		   _master.addBowDmgupByDoll(2);
		   _master.addMaxHp(30);
		   _master.sendPackets(new S_OwnCharStatus2(_master));
		}
		if (type == DOLLTYPE_PSY_GANGNAM_STYLE) {
		   _master.getAbility().addSp(1);
		   _master.addMaxHp(30);
		   _master.sendPackets(new S_SPMR(_master));
		   _master.sendPackets(new S_OwnCharStatus2(_master));
		}

		
		
		
		
		if (type == DOLLTYPE_SNOWMAN) {
			master.getAC().addAc(-3);
			_master.getResistance().addFreeze(7);
		}
		
		
		
		
		if (type == DOLLTYPE_SNOWMAN_A) {// 눈사람
			_master.addBowHitupByDoll(5);
		}

		if (type == DOLLTYPE_SCARECROW) { // by포비 허수아비
			_master.addBowHitupByDoll(2);
			_master.addMaxHp(50);
			_master.addMaxMp(30);
		}
		
		
		/** 픽시의 마법인형 **/
		if (type == DOLLTYPE_RANKING_ONE
				|| type == DOLLTYPE_RANKING_TWO
				|| type == DOLLTYPE_RANKING_THREE
				|| type == DOLLTYPE_RANKING_FOR) {
			_master.getAbility().addSp(1);
			_master.addDmgup(2);
			_master.addBowDmgup(2);
			_master.sendPackets(new S_SPMR(_master));
		}
		/** 픽시의 마법인형 **
		/*if (type == DOLLTYPE_RANKING_ONE) { // 블레그
			_master.addBowHitupByDoll(2);
			_master.addBowDmgupByDoll(2);
				_master.getAbility().addSp(1);
		}
		if (type == DOLLTYPE_RANKING_TWO) {
				_master.addHitup(2);
				_master.addBowHitupByDoll(2);
				_master.getAbility().addSp(1);
		}
		if (type == DOLLTYPE_RANKING_THREE) {
				_master.addHitup(2);
				_master.addBowHitupByDoll(2);
				_master.getAbility().addSp(1);
		}
		if (type == DOLLTYPE_RANKING_FOR) {
				_master.addHitup(2);
				_master.addBowHitupByDoll(2);
				_master.getAbility().addSp(1);
		}*/
		
		if (type == DOLLTYPE_ETTIN) {
			_master.getResistance().addHold(10);
			master.getAC().addAc(-2);
			   master.removeHasteSkillEffect();
			   if (master.getMoveSpeed() != 1) {
			    master.setMoveSpeed(1);
			    master.sendPackets(new S_SkillHaste(master.getId(), 1, -1));
			    master.broadcastPacket(new S_SkillHaste(master.getId(), 1, 0));
			   }
			   master.getSkillEffectTimerSet().setSkillEffect(STATUS_HASTE, dollTime * 1000);
		} // 추가     
		if (type == DOLLTYPE_SKELETON) { // 스켈
			_master.addKnifeDmgupByDoll(2);//데미지
			_master.getResistance().addStun(5);//스턴내성
		}
		if (type == DOLLTYPE_COCATRIS) {
			_master.addBowHitupByDoll(5);
			_master.addBowDmgupByDoll(5);
		}
		if (type == DOLLTYPE_LAMIA) {
			_master.addMpr(4);
		}
		
		if (type == DOLLTYPE_RANKING_ONE1) {
			_master.getAC().addAc(-5);
			_master.addMaxHp(70);
		    _master.addMaxMp(70);
		    _master.addDmgup(5);
		    _master.addBowDmgup(5);
		    _master.addHitup(1);
		    _master.addBowHitupByDoll(1);
		    //_master.getResistance().addStun(3);
		    _master.getAbility().addSp(3);
		    _master.sendPackets(new S_SPMR(_master));
		}
		
		if (type == DOLLTYPE_RANKING_TWO2) {
			_master.getAC().addAc(-4);
			_master.addMaxHp(50);
		    _master.addMaxMp(50);
		    _master.addDmgup(4);
		    _master.addBowDmgup(4);
		    _master.addHitup(1);
		    _master.addBowHitupByDoll(1);
		   // _master.getResistance().addStun(3);
		    _master.getAbility().addSp(2);
		    _master.sendPackets(new S_SPMR(_master));
		}
		
		if (type == DOLLTYPE_RANKING_THREE3) {
			_master.getAC().addAc(-3);
			_master.addMaxHp(30);
		    _master.addMaxMp(30);
		    _master.addDmgup(3);
		    _master.addBowDmgup(3);
		    _master.addHitup(1);
		    _master.addBowHitupByDoll(1);
		  //  _master.getResistance().addStun(3);
		    _master.getAbility().addSp(1);
		    _master.sendPackets(new S_SPMR(_master));
		}
		if (type == DOLLTYPE_RANKING) {
			_master.getAC().addAc(-5);
			_master.addMaxHp(100);
		    _master.addMaxMp(100);
		    _master.addDmgup(5);
		    _master.addBowDmgup(5);
		    _master.addHitup(1);
		    _master.addBowHitupByDoll(1);
		    _master.getResistance().addStun(5);
		    _master.getAbility().addSp(5);
		    _master.sendPackets(new S_SPMR(_master));
		}
		
		startHelpTimer();
	}
	
	
	

	public void deleteDoll() {
		if (isMpRegeneration()) {
			((L1PcInstance) _master).stopMpRegenerationByDoll();
		} else if (isHpRegeneration()) {
			((L1PcInstance) _master).stopHpRegenerationByDoll();
		}
		int type = getDollType();
		
		if (type == DOLLTYPE_RANKING_ONE1) {
			_master.getAC().addAc(5);
			_master.addMaxHp(-70);
		    _master.addMaxMp(-70);
		    _master.addDmgup(-5);
		    _master.addBowDmgup(-5);
		    _master.addHitup(-1);
		    _master.addBowHitupByDoll(-1);
		   // _master.getResistance().addStun(-3);
		    _master.getAbility().addSp(-3);
		    _master.sendPackets(new S_SPMR(_master));
		}
		
		if (type == DOLLTYPE_RANKING_TWO2) {
			_master.getAC().addAc(4);
			_master.addMaxHp(-50);
		    _master.addMaxMp(-50);
		    _master.addDmgup(-4);
		    _master.addBowDmgup(-4);
		    _master.addHitup(-1);
		    _master.addBowHitupByDoll(-1);
		   // _master.getResistance().addStun(-3);
		    _master.getAbility().addSp(-2);
		    _master.sendPackets(new S_SPMR(_master));
		}
		
		if (type == DOLLTYPE_RANKING_THREE3) {
			_master.getAC().addAc(3);
			_master.addMaxHp(-30);
		    _master.addMaxMp(-30);
		    _master.addDmgup(-3);
		    _master.addBowDmgup(-3);
		    _master.addHitup(-1);
		    _master.addBowHitupByDoll(-1);
		   // _master.getResistance().addStun(-3);
		    _master.getAbility().addSp(-1);
		    _master.sendPackets(new S_SPMR(_master));
		}
		if (type == DOLLTYPE_RANKING) {
			_master.getAC().addAc(5);
			_master.addMaxHp(-100);
		    _master.addMaxMp(-100);
		    _master.addDmgup(-5);
		    _master.addBowDmgup(-5);
		    _master.addHitup(-1);
		    _master.addBowHitupByDoll(-1);
		    _master.getResistance().addStun(-5);
		    _master.getAbility().addSp(-5);
		    _master.sendPackets(new S_SPMR(_master));
		}
		
		
		if (type == DOLLTYPE_PSY_CHAMPION) {
		   _master.addDmgup(-2);
		   _master.addMaxHp(-30);
		   _master.sendPackets(new S_OwnCharStatus2(_master));
		}
		if (type == DOLLTYPE_PSY_BIRD) {
		   _master.addBowDmgupByDoll(-2);
		   _master.addMaxHp(-30);
		   _master.sendPackets(new S_OwnCharStatus2(_master));
		}
		if (type == DOLLTYPE_PSY_GANGNAM_STYLE) {
		   _master.getAbility().addSp(-1);
		   _master.addMaxHp(-30);
		   _master.sendPackets(new S_SPMR(_master));
		   _master.sendPackets(new S_OwnCharStatus2(_master));
		}

		
		
		
		
		if (type == DOLLTYPE_SNOWMAN) {
			_master.getAC().addAc(3);
			_master.getResistance().addFreeze(-7);
		}
		if (type == DOLLTYPE_COCATRIS) {
			_master.addBowHitupByDoll(-5);
			_master.addBowDmgupByDoll(-5);
		}
		if (type == DOLLTYPE_SNOWMAN_A) {// 눈사람
			_master.addBowHitupByDoll(-5);
		}

		if (type == DOLLTYPE_SCARECROW) { // by포비 허수아비
			_master.addBowHitupByDoll(-2);
			_master.addMaxHp(-50);
			_master.addMaxMp(-30);
		}
		if (type == DOLLTYPE_RANKING_ONE
				|| type == DOLLTYPE_RANKING_TWO
				|| type == DOLLTYPE_RANKING_THREE
				|| type == DOLLTYPE_RANKING_FOR) {
			_master.getAbility().addSp(-1);
			_master.addDmgup(-2);
			_master.addBowDmgup(-2);
			_master.sendPackets(new S_SPMR(_master));
		}
		
		/*if (type == DOLLTYPE_RANKING_ONE) { //마법인형 : 블레그
		
			_master.addBowHitupByDoll(-2);
			_master.addBowDmgupByDoll(-2);
				_master.getAbility().addSp(-1);
			
		}
		if (type == DOLLTYPE_RANKING_TWO) {
			_master.addHitup(-2);
			_master.addBowHitupByDoll(-2);
			_master.getAbility().addSp(-1);
		}
		if (type == DOLLTYPE_RANKING_THREE) {
			_master.addHitup(-2);
			_master.addBowHitupByDoll(-2);
			_master.getAbility().addSp(-1);
		}
		if (type == DOLLTYPE_RANKING_FOR) {
			_master.addHitup(-2);
			_master.addBowHitupByDoll(-2);
			_master.getAbility().addSp(-1);
		}*/
		
		if (type == DOLLTYPE_ETTIN) {
			_master.getResistance().addHold(-10);
			_master.getAC().addAc(2);
			    _master.setMoveSpeed(0);
			    ((L1PcInstance) _master).sendPackets(new S_SkillHaste(_master.getId(), 0, 0));
			    _master.broadcastPacket(new S_SkillHaste(_master.getId(), 0, 0));
			    _master.getSkillEffectTimerSet().removeSkillEffect(STATUS_HASTE);
			  }     
		if (type == DOLLTYPE_SKELETON) { // 스켈
			_master.addKnifeDmgupByDoll(-2);
			_master.getResistance().addStun(-5);
		}

		if (type == DOLLTYPE_LAMIA) {
			_master.addMpr(-4);
		}
		stopHelpTimer();
		_master.sendPackets(new S_SkillSound(getId(), 5936));
		_master.sendPackets(new S_SkillSound(getId(), 3940));
		_master.getDollList().remove(getId());
		deleteMe();
		setMaster(null);
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.getNearObjects().addKnownObject(this);
		perceivedFrom.sendPackets(new S_DollPack(this, perceivedFrom));
	}

	@Override
	public void onItemUse() {
		if (!isActived()) {
			// 100%의 확률로 헤이 파업 일부 사용
			useItem(USEITEM_HASTE, 100);
		}
	}

	@Override
	public void onGetItem(L1ItemInstance item) {
		if (getNpcTemplate().get_digestitem() > 0) {
			setDigestItem(item);
		}
		if (Arrays.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
			useItem(USEITEM_HASTE, 100);
		}
	}

	public int getDollType() {
		return _dollType;
	}

	public void setDollType(int i) {
		_dollType = i;
	}

	public int getItemObjId() {
		return _itemObjId;
	}

	public void setItemObjId(int i) {
		_itemObjId = i;
	}

	public int getDamageByDoll() {
		int damage = 0;
		int type = getDollType();
		if (type == DOLLTYPE_WAREWOLF || type == DOLLTYPE_CRUSTACEA) {
			int chance = _random.nextInt(100) + 1;
			if (chance <= 3) {
				damage = 15;
				if (_master instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) _master;
					pc.sendPackets(new S_SkillSound(_master.getId(), 6319));
				}
				Broadcaster.broadcastPacket(_master, new S_SkillSound(_master
						.getId(), 6319));
			}
		}
		return damage;
	}

	/** 코카 인형 * */
	public int getBowhitRateByDoll() {
		int damage = 0;
		int type = getDollType();
		if (type == DOLLTYPE_COCATRIS) { // 코카트리스
			int chance = _random.nextInt(100) + 1;
			if (chance <= 3) {
				damage = 7;
				if (_master instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) _master;
					pc.sendPackets(new S_SkillSound(_master.getId(), 6319));
				}
				Broadcaster.broadcastPacket(_master, new S_SkillSound(_master
						.getId(), 6319));
			}
		}
		return damage;
	}

	/** 코카 인형 * */
	public void attackPoisonDamage(L1PcInstance pc, L1Character cha) {
		int type = getDollType();
		if (type == DOLLTYPE_LAMIA) {
			int chance = _random.nextInt(100) + 1;
			if (10 >= chance) {
				L1DamagePoison.doInfection(pc, cha, 3000, 10);
			}
		}
	}

	public int getDamageReductionByDoll() {
		int DamageReduction = 0;
		if (getDollType() == DOLLTYPE_STONEGOLEM) {
			int chance = _random.nextInt(100) + 1;
			if (chance <= 4) {
				DamageReduction = 30;
				if (_master instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) _master;
					pc.sendPackets(new S_SkillSound(_master.getId(), 6320));
				}
				Broadcaster.broadcastPacket(_master, new S_SkillSound(_master
						.getId(), 6320));
			}
		}
		return DamageReduction;
	}

	public boolean isMpRegeneration() {
		boolean isMpRegeneration = false;
		int type = getDollType();
		switch (type) {
		case DOLLTYPE_SUCCUBUS:
		case DOLLTYPE_ELDER:
		case DOLLTYPE_SNOWMAN_B: // 눈사람
		case DOLLTYPE_PSY_CHAMPION: //싸이3종
	  case DOLLTYPE_PSY_BIRD:
	  case DOLLTYPE_PSY_GANGNAM_STYLE:

			isMpRegeneration = true;
			break;
		}
		return isMpRegeneration;
	}

	public boolean isHpRegeneration() {
		boolean isHpRegeneration = false;
		int type = getDollType();
		switch (type) {
		case DOLLTYPE_SEADANCER:
		case DOLLTYPE_SNOWMAN_C:// 눈사람
			isHpRegeneration = true;
			break;
		}
		return isHpRegeneration;
	}

	public int getWeightReductionByDoll() {
		int weightReduction = 0;
		int type = getDollType();
		switch (type) {
		case DOLLTYPE_RANKING_ONE1:
		case DOLLTYPE_RANKING:
			weightReduction = 10;
			break;
		case DOLLTYPE_RANKING_TWO2:
			weightReduction = 7;
			break;
		case DOLLTYPE_RANKING_THREE3:
			weightReduction = 5;
			break;
		case DOLLTYPE_BUGBEAR:
		case DOLLTYPE_DRAGON_M:
		case DOLLTYPE_DRAGON_W:
		case DOLLTYPE_HIGH_DRAGON_M:
		case DOLLTYPE_HIGH_DRAGON_W:
			weightReduction = 10;
			break;
		}
		return weightReduction;
	}

	public int getMpRegenerationValues() {
		int regenMp = 0;
		int type = getDollType();
		switch (type) {
		case DOLLTYPE_SUCCUBUS:
		case DOLLTYPE_ELDER:
		case DOLLTYPE_PSY_CHAMPION: //싸이3종
		  case DOLLTYPE_PSY_BIRD:
		  case DOLLTYPE_PSY_GANGNAM_STYLE:

			regenMp = 15;
			break;
		case DOLLTYPE_HIGH_DRAGON_M:
		case DOLLTYPE_HIGH_DRAGON_W:
			regenMp = 5;
			break;
		case DOLLTYPE_SNOWMAN_B:
			regenMp = 18;
			break; // 추가 눈사람
		}
		return regenMp;
	}

	public int getHpRegenerationValues() { // 눈사람
		int regenHp = 0;
		int type = getDollType();
		switch (type) {
		case DOLLTYPE_SNOWMAN_C:
			regenHp = 60;
			break;
		}
		return regenHp;
	}

	private void getHelperAction() {
		if (_master.getCurrentHp() < _master.getMaxHp() / 2) {
			new L1SkillUse().handleCommands(null, 35, _master.getId(), _master
					.getX(), _master.getY(), null, 0, L1SkillUse.TYPE_NORMAL,
					this);
			return;
		}
		for (int i = 0; i < Buff.length; i++) {
			if (!_master.getSkillEffectTimerSet().hasSkillEffect(Buff[i])) {
				new L1SkillUse().handleCommands(null, Buff[i], _master.getId(),
						_master.getX(), _master.getY(), null, 0,
						L1SkillUse.TYPE_NORMAL, this);
				break;
			}
		}
	}

	public void startHelpTimer() {
		if (getDollType() != DOLLTYPE_HELPER)
			return;
		_future = _threadPool.scheduleAtFixedRate(new HelpTimer(), 4000, 4000);
	}

	public void stopHelpTimer() {
		if (getDollType() != DOLLTYPE_HELPER)
			return;
		if (_future != null) {
			_future.cancel(false);
		}
	}
}
