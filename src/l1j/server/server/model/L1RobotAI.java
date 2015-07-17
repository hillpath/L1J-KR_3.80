/** 
 * 2011.08.29 고정수 로봇 AI
 */

package l1j.server.server.model;

import static l1j.server.server.model.skill.L1SkillId.ADVANCE_SPIRIT;
import static l1j.server.server.model.skill.L1SkillId.BLESS_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.EARTH_SKIN;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;
import static l1j.server.server.model.skill.L1SkillId.POLLUTE_WATER;
import static l1j.server.server.model.skill.L1SkillId.STATUS_BLUE_POTION;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HASTE;

import java.util.Random;

import l1j.server.server.command.executor.L1Robot;
import l1j.server.server.datatables.SprTable;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1ScarecrowInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.utils.CommonUtil;

public class L1RobotAI {		
	
	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };	
	
	private int dieCount = 0;
	private int moveCount = 0;
	private int polyCount = 0;
	private int polyDelayCount = 0;
	private int postionCount = 0;
	private int cancellationCount = 0;
	
	public int getCancellationCount() {
		return cancellationCount;
	}

	public void setCancellationCount(int cancellationCount) {
		this.cancellationCount = cancellationCount;
	}
	
	private L1PcInstance robot; // 로봇대상		
	private L1Character target;
	private L1Astar aStar;
	private L1Node tail;
	private int iCurrentPath;
	private int[][] iPath;
	
	private long ai_start_time; // 인공지능 시작된 시간값
	private long ai_time; // 인공지능 처리에 사용될 프레임참고 값
	
	// 인공지능 상태 변수	
	public static final int AI_STATUS_SETTING = 0;  // 초반 세팅처리
	public static final int AI_STATUS_WALK = 1;     // 랜덤워킹 상태
	public static final int AI_STATUS_ATTACK = 2;   // 공격 상태
	public static final int AI_STATUS_DEAD = 3;     // 죽은 상태
	public static final int AI_STATUS_CORPSE = 4;   // 시체 상태
	public static final int AI_STATUS_SPAWN = 5;    // 스폰 상태
	public static final int AI_STATUS_ESCAPE = 6;   // 도망 상태
	public static final int AI_STATUS_PICKUP = 7;   // 아이템 줍기 상태
	public static final int AI_STATUS_SHOP = 8;     // 상점으로이동처리
	
	// 마을에서 버프 스탭 확인용
	private int buff_step;
	
	public L1RobotAI(L1PcInstance pc) {
		robot = pc;
		attackList = new L1HateList();
		aStar = new L1Astar();
		iPath = new int[300][2];
		postionCount = CommonUtil.random(700, 1000);
	}
	
	private boolean active = false;    // 활성화
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public int type = 1;
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	private int ai_Status = 1; // 인공지능 처리해야할 상태
	
	public int getAiStatus() {
		return ai_Status;
	}

	public void setAiStatus(int aiStatus) {
		this.ai_Status = aiStatus;
	}
	
	private L1HateList attackList;

	public L1HateList getAttackList() {
		return attackList;
	}

	public void setAttackList(L1HateList attackList) {
		this.attackList = attackList;
	}

	
	private int getFrame(int gfx, int gfxmode) {
		int AttackSpeed = 0;
		
		switch (robot.getRobotAi().getType()) { 
		case 1:
			AttackSpeed = 600;
			break;
		case 2: 
			AttackSpeed = 0;
			break;
			
		}
		
		if (robot.getGfxId().getGfxId() == robot.getGfxId().getTempCharGfx()) {
			AttackSpeed = 600;
		} else {
			if (!robot.isElf()) {
				AttackSpeed = 0;
			} else {
				AttackSpeed = 200;
			}
		}
		
		switch (gfxmode) {
		case 0: // 이동
			return 800;
		case 1: // 공격
			return 600 + AttackSpeed;
		}
		return 1000;
	}
	
	private class BuffStart implements Runnable {
		L1PcInstance player;
		L1SkillUse skilluse = new L1SkillUse();
		
		private void buff(L1PcInstance pc) {
			int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, ADVANCE_SPIRIT, EARTH_SKIN };
			if (pc.isDead())
				return;

			long curtime = System.currentTimeMillis() / 1000;

			if (pc.getLevel() <= 65) {
				try {
					L1SkillUse l1skilluse = new L1SkillUse();
					for (int i = 0; i < allBuffSkill.length; i++) {
						l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
						pc.setQuizTime(curtime);
					}
				} catch (Exception e) {
				}
			} 
		}
		
		public void run() {
			try {
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	private boolean isBlankLocation(L1Character cha) {		
		if (L1World.getInstance().getVisibleObjects(cha, 1).size() >= 4) {
			return false;
		}
		
		return true;
	}
	
	public void toAI(long time) {
		try {
			if (!isAi(time)) {
				return;
			}
			
			if (robot.isDead()) {
				toAiDead();
				return;
			}
						
			switch (ai_Status) {
			case AI_STATUS_WALK:
			case AI_STATUS_PICKUP:
				toSearchTarget();
				if (attackList.toHateFastTable().size() > 0) {
					setAiStatus(AI_STATUS_ATTACK);
				}
				break;
			case AI_STATUS_ATTACK:
			case AI_STATUS_ESCAPE:
				if (attackList.toHateFastTable().size() == 0) {
					setAiStatus(AI_STATUS_WALK);				
				}
				break;				
			default:
				break;
			}
			
			if (getAiStatus() != AI_STATUS_DEAD && robot.isDead()) {
				setAiStatus(AI_STATUS_DEAD);
			}
			
			ai_start_time = time;
			
			// 물약 복용 처리.
			toHealingPostion(false);
			
			// 속도 물약 복용 처리.
			if (cancellationCount <= 0) {
				toSpeedPostion();
				
				// 변신 처리
				if (type == 1) {
					int ran = CommonUtil.random(100);
					
					if (ran <= 5 && polyDelayCount <= 0) {
						if (polyCount <= 0 ) {				
							toPolyMorph();
							polyCount = CommonUtil.random(3500, 4500);							
						} 
						if (polyDelayCount <= 0) {
							polyDelayCount = CommonUtil.random(3000, 4000);				
						} else {
							polyDelayCount--;
						}
					} else {
						polyDelayCount--;
					}
					
					
					if (polyCount > 0) {
						polyCount--;
					}
				} else {
					if (polyCount <= 0) {				
						toPolyMorph();
						polyCount = 2500;
					} else {
						polyCount--;
					}
				}
				
				cancellationCount = 0;
			} else {
				cancellationCount--;
				polyCount = 0;
			}
			
			
			
			switch (getAiStatus()) {
			case AI_STATUS_SETTING:
				toAiSetting();	
				setAiStatus(AI_STATUS_WALK);
				break;
			case AI_STATUS_WALK:
				toRandomWalk();
				break;
			case AI_STATUS_ATTACK:
				toAiAttack();
				break;
			case AI_STATUS_DEAD:
				toAiDead();
				break;
			case AI_STATUS_CORPSE:
				//toAiCorpse(time);
				break;
			case AI_STATUS_SPAWN:
				//toAiSpawn(time);
				break;
			case AI_STATUS_ESCAPE:
				//toAiEscape(time);
				break;
			case AI_STATUS_PICKUP:
				//toAiPickup(time);
				break;
			case AI_STATUS_SHOP:
				//toShopMove(time);
				break;
			default:
				ai_time = 1000;
				break;
			}			
			
//			System.out.println(getAiStatus());
			
//			switch (type) {
//			case 1:
//				
//				
//				
//				if (target == null) {
//					for (L1Object obj : L1World.getInstance().getVisibleObjects(robot)) {
//						if (obj instanceof L1ScarecrowInstance) {
//							target = (L1Character)obj;
//							attackList.add((L1Character)obj, 0);
//						}
//					}
//				} else {				
//					
//				}				
//				break;
//			case 2:
//				if (robot.getMap().isSafetyZone(robot.getLocation())) {
//					setAiStatus(AI_STATUS_SETTING);
//				}
//				
//				toPolyMorph();
//				
//				if (attackList.isEmpty()) {
//					
//					
//					
//				} else {
//					if (target != null && !target.isDead()) {				
//						if (isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isElf() ? 8 : 1)) {
//							ai_time = getFrame(robot.getGfxId().getGfxId(), 1);
//							if (target.isInvisble()) {
//								attackList.remove(target);
//							}
//							target.onAction(robot);				
//						} else {
//							ai_time = getFrame(robot.getGfxId().getGfxId(), 0);
//							toMoving(target, target.getX(), target.getY(), 0, true);
//						}
//					} else { 
//						if (target != null) {
//							attackList.remove(target);
//						}
//						target = null;
//						
//						int distance = 20;
//						int temp = 0;
//						for (L1Character cha : attackList.toTargetArrayList()) {						
//							temp = getDistance(robot.getX(), robot.getY(), cha.getX(), cha.getY());		
//							
//							if (temp > 20) {
//								attackList.remove(cha);
//								continue;
//							}
//							
//							if (temp <= distance) {
//								target = cha;
//								distance = temp;
//							}
//						}
//					}
//				}
//			default:
//				break;
//			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 인공지능 활성화할 시간이 됫는지 확인해주는 함수.
	 * 
	 * @param time
	 * @return
	 */
	private boolean isAi(long time) {			
		long speed = ai_time;
		long temp = time - ai_start_time;
		double gab = robot.getMoveState().getMoveSpeed() == 1 && robot.getMoveState().getBraveSpeed() == 1 ? 0.5 : robot.getMoveState().getMoveSpeed() == 1 ? 0.2 : robot.getMoveState().getMoveSpeed() == 2 ? -0.5 : 0;
		speed -= (long) (speed * gab);
		
		/** 2011.04.18 고정수 페럴라이즈 상태시 안움직이게 */
		if (robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND) || 
			robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN) || 
			robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ICE_LANCE)  || 
			robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.FREEZING_BLIZZARD) || 
			robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CURSE_PARALYZE) || 
			robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB) || 
			robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BONE_BREAK)   || 
			robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PHANTASM) || 
			robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_FREEZE)) {
			return false;
		}
		
		int gfxid = robot.getGfxId().getTempCharGfx();
		int weapon = robot.getCurrentWeapon();
		int interval;
		double HASTE_RATE = 0.745;
		double WAFFLE_RATE = 0.874;
		double THIRDSPEED_RATE = 0.874;
		
		interval = SprTable.getInstance().getAttackSpeed(gfxid, weapon + 1);
		
		if (robot.isHaste()) {
			interval *= HASTE_RATE;
		}
		
		if (robot.isBrave()) {
			interval *= HASTE_RATE;
		}
		if (robot.isElfBrave()) {
			interval *= WAFFLE_RATE;
		}
		if (robot.isDragonPearl()) {
			interval *= THIRDSPEED_RATE;
		}
		
		
		
		if (temp < interval) {
			return false;
		} 
		
		if (temp >= interval) {
			ai_start_time = time;
			return true;
		}
		

		if (time == 0 || temp >= speed) {
			ai_start_time = time;
			return true;
		}

		return false;
	}
	
	/**
	 * 마을에서 기본적인 세팅처리할때 사용.
	 * 
	 * @param time
	 */
	private void toAiSetting() {
		ai_time = getFrame(robot.getGfxId().getGfxId(), 0);

		if (robot.getDollList().size() == 0) {
//			if (L1Robot.random(0, 200) == 100) {
//				int dollRnd = L1Robot.random(0, 15);
//				L1Robot.dollStart(this, dollRnd);
//			}
		}

		// 마을로 귀환.
		if (!robot.getMap().isSafetyZone(robot.getLocation())) {
			// 딜레이.
			int ran = L1Robot.random(5, 15);
			ai_time = 1000 * ran;

			if (robot.getMapId() == 450) {
				robot.getMap().setPassable(robot.getLocation(), true);
				for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(robot)) {
					pc.sendPackets(new S_SkillSound(robot.getId(), 169));
					pc.sendPackets(new S_RemoveObject(robot));
					pc.getNearObjects().removeKnownObject(robot);
				}
				L1Teleport.teleport(robot, 33437, 32813, (short) 4, 5, true);
				return;
			}
			
			robot.getMap().setPassable(robot.getLocation(), true);
			for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(robot)) {
				pc.sendPackets(new S_SkillSound(robot.getId(), 169));
				pc.sendPackets(new S_RemoveObject(robot));
				pc.getNearObjects().removeKnownObject(robot);
			}
			int[] loc = Getback.GetBack_Location(robot, true);
			L1Teleport.teleport(robot, loc[0], loc[1], (short) loc[2], 5, true);
			return;
		}

		// hp 회복처리.
		if (robot.getMaxHp() != robot.getCurrentHp()) {
			toHealingPostion(true);
			return;
		}

		// 로봇 변신 처리.		
		if (robot.getGfxId().getGfxId() == robot.getGfxId().getTempCharGfx()) {
			Poly(robot);
			return;
		}

		// 버프시전.
		if (robot.isKnight()) {
			switch (buff_step++) {
			case 0:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.REDUCTION_ARMOR, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 1:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.BOUNCE_ATTACK, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 3:
//				location = RobotAiThread.getLocation();
//				if (location != null)
//					L1Teleport.teleport(this, location.x, location.y, (short) location.map, 5, true);
//				else
//					System.out.println("사냥터가 없습니다.");
//				return;
			default:
				buff_step = 0;
				break;
			}
		} else if (robot.isElf()) {
			switch (buff_step++) {
			case 0:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.STORM_SHOT, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 1:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.BLOODY_SOUL, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 3:
//				location = RobotAiThread.getLocation();
//				if (location != null)
//					L1Teleport.teleport(this, location.x, location.y, (short) location.map, 5, true);
//				else
//					System.out.println("사냥터가 없습니다.");
//				return;
			default:
				buff_step = 0;
				break;
			}
		} else if (robot.isWizard()) {
			switch (buff_step++) {
			case 0:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.ADVANCE_SPIRIT, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 1:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.PHYSICAL_ENCHANT_DEX, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 2:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.PHYSICAL_ENCHANT_STR, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 3:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.BLESS_WEAPON, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 4:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.BERSERKERS, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 5:
//				location = RobotAiThread.getLocation();
//				if (location != null)
//					L1Teleport.teleport(this, location.x, location.y, (short) location.map, 5, true);
//				else
//					System.out.println("사냥터가 없습니다.");
//				return;
			default:
				buff_step = 0;
				break;
			}
		} else if (robot.isDarkelf()) {
			switch (buff_step++) {
			case 0:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.ENCHANT_VENOM, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 1:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.SHADOW_ARMOR, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 2:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.DOUBLE_BRAKE, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 3:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.UNCANNY_DODGE, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 4:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.SHADOW_FANG, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 5:
				Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
				new L1SkillUse().handleCommands(robot, L1SkillId.DRESS_DEXTERITY, robot.getId(), robot.getX(), robot.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				return;
			case 6:
//				location = RobotThread.getLocation();
//				if (location != null)
//					L1Teleport.teleport(this, location.x, location.y, (short) location.map, 5, true);
//				else
//					System.out.println("사냥터가 없습니다.");
//				return;
			default:
				buff_step = 0;
				break;
			}
		} else if (robot.isDragonknight()) {
			//
		} else if (robot.isIllusionist()) {
			//
		} else if (robot.isCrown()) {

		}

		setAiStatus(AI_STATUS_WALK);
	}
	
	/**
	 * 객체가 공격가능한 상태인지 확인해주는 함수.
	 * 
	 * @param o
	 * @param walk
	 * @return
	 */
	public boolean isAttack(L1Character cha, boolean walk) {
		if (//cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE) || 
			//cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECAY_POTION) || 
			//cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN) || 
			//cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB) || 
			//cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MIND_BREAK) || 
			//cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PANIC) || 
			//cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IllUSION_AVATAR) || 
			//cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE) || 
			//cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER) || 
			cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND) || 
			cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ICE_LANCE)) {
			return false;
		}
		if (cha.getMap().isSafetyZone(cha.getLocation()))
			return false;
		if (cha == null)
			return false;
		if (cha.isDead())
			return false;
		// if(cha.isGm())
		// return false;
		// if(cha.isLockHigh())
		// return false;
		if (cha.isInvisble())
			return false;
		if (!isDistance(robot.getX(), robot.getY(), robot.getMapId(), cha.getX(), cha.getY(), cha.getMapId(), 12))
			return false;
		if (!CharPosUtil.glanceCheck(robot, cha.getX(), cha.getY())) 
			return false;

		return true;
	}
	
	private void toAiAttack() {
		// 공격자 확인.
		target = findDangerousObject();
		
		if (target != null && target.isDead()) {
			attackList.remove(target);
			return;
		}
		
		if (type > 1 && !isAttack(target, false)) {
			attackList.remove(target);
			return;
		}
				
		// 객체를 찾지못했다면 랜덤워킹 변경.
		if (target == null) {
			ai_time = 0;
			setAiStatus(AI_STATUS_WALK);
			return;
		}
		
		// 버프 시전 처리.
		//if (toBuff())
		//	return;
		
		// 객체 거리 확인
		if (type > 1 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isElf() ? 8 : 1)) {
			// 사용자는 구분 처리.
			if (target instanceof L1PcInstance) { // PC랑 싸운다면.				
				if (robot.isWizard() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isWizard() ? 5 : 1)) {
					if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECAY_POTION) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MIND_BREAK) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PANIC) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IllUSION_AVATAR) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)) {
						return;

					} else {
						toWizardMagic(target);
						return;
					}
				} else if (robot.isKnight() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isKnight() ? 2 : 1)) {
					if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECAY_POTION) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MIND_BREAK) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PANIC) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IllUSION_AVATAR) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)) {
						return;

					} else {
						toKnightMagic(target);
						return;
					}
				} else if (robot.isDragonknight() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isDragonknight() ? 2 : 1)) {
					if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECAY_POTION) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MIND_BREAK) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PANIC) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IllUSION_AVATAR) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)) {
						return;

					} else {
						toDragonknightMagic(target);
						return;
					}
				} else if (robot.isIllusionist() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isIllusionist() ? 4 : 1)) {
					if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECAY_POTION) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MIND_BREAK) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PANIC) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IllUSION_AVATAR) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)) {
						return;

					} else {
						toIllusionistMagic(target);
						return;
					}
				} else if (robot.isElf() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isElf() ? 7 : 1)) {
					if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE) || 
							target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECAY_POTION) || 
							target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN) || 
							target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB) || 
							target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MIND_BREAK) || 
							target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PANIC) || 
							target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IllUSION_AVATAR) || 
							target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE) || 
							target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER) || 
							target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)) {
						return;

					} else {
						toElfMagic(target);
						return;
					}
				}
			} else { // 몬스터랑 싸운다면.
				if (robot.isWizard() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isWizard() ? 5 : 1)) {
					if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECAY_POTION) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MIND_BREAK) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PANIC) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IllUSION_AVATAR) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)) {
						return;
					} else {
						toWizardMagic(target);
						return;
					}
				} else if (robot.isKnight() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isKnight() ? 2 : 1)) {
					if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECAY_POTION) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MIND_BREAK) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PANIC) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IllUSION_AVATAR) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)) {
						return;
					} else {
						toKnightMagic(target);
						return;
					}
				} else if (robot.isDragonknight() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isDragonknight() ? 2 : 1)) {
					if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECAY_POTION) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MIND_BREAK) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PANIC) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IllUSION_AVATAR) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)) {
						return;
					} else {
						toDragonknightMagic(target);
						return;
					}
				} else if (robot.isIllusionist() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isIllusionist() ? 4 : 1)) {
					if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECAY_POTION) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MIND_BREAK) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PANIC) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IllUSION_AVATAR) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)) {
						return;
					} else {
						toIllusionistMagic(target);
						return;
					}
				} else if (robot.isElf() && robot.getCurrentMp() >= 50 && isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isElf() ? 7 : 1)) {
					if (target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SILENCE) ||
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECAY_POTION) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MIND_BREAK) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PANIC) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.IllUSION_AVATAR) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER) || 
						target.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)) {
						return;
					} else {
						toElfMagic(target);
						return;
					}
				}
			}
		}
		
		if (isDistance(robot.getX(), robot.getY(), robot.getMapId(), target.getX(), target.getY(), target.getMapId(), robot.isElf() ? 8 : 1)) {
			ai_time = getFrame(robot.getGfxId().getGfxId(), 1);						
			if (CharPosUtil.glanceCheck(robot, target.getX(), target.getY())) {
				toAttack(target, 0, 0, robot.isElf(), 1, 0);
			} else {
				toMoving(target, target.getX(), target.getY(), 0, true);				
			}
		} else {		
			/*
			if (!isBlankLocation(target)) {
				attackList.remove(target);
				if (!attackList.isEmpty()) {
					target = attackList.getMaxHateCharacter();
				}							
				return;
			}
			*/			
			ai_time = getFrame(robot.getGfxId().getGfxId(), 0);
			toMoving(target, target.getX(), target.getY(), 0, true);	
		}
		
	}
		
	/**
	 * 각클레스가 마법을 시전하게 한다.
	 * 
	 * @param o
	 */
	private void toWizardMagic(L1Object o) {
		if (robot.isWizard()) {
			Random random = new Random();
			int a = random.nextInt(5);
			switch (a) {
			case 1:
				new L1SkillUse().handleCommands(robot, L1SkillId.SUNBURST, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				robot.setCurrentMp(1);
				break;
			case 2:
				new L1SkillUse().handleCommands(robot, L1SkillId.SILENCE, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				robot.setCurrentMp(1);
				break;
			case 3:
				new L1SkillUse().handleCommands(robot, L1SkillId.ERUPTION, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				robot.setCurrentMp(1);
				break;
			case 4:
				new L1SkillUse().handleCommands(robot, L1SkillId.DECAY_POTION, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				robot.setCurrentMp(1);
				break;
			case 5:
				new L1SkillUse().handleCommands(robot, L1SkillId.WEAPON_BREAK, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				robot.setCurrentMp(1);
				break;

			default:
			}
		}
	}

	private void toKnightMagic(L1Object o) {
		if (robot.isKnight()) {
			new L1SkillUse().handleCommands(robot, L1SkillId.SHOCK_STUN, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
			robot.setCurrentMp(1);
		}
	}

	private void toDragonknightMagic(L1Object o) {
		Random random = new Random();
		int a = random.nextInt(6);
		switch (a) {
		case 1:
			new L1SkillUse().handleCommands(robot, L1SkillId.FOU_SLAYER, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
			robot.setCurrentMp(1);
			break;
		case 2:
			new L1SkillUse().handleCommands(robot, L1SkillId.THUNDER_GRAB, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
			robot.setCurrentMp(1);
			break;
		case 3:
			new L1SkillUse().handleCommands(robot, L1SkillId.FOU_SLAYER, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
			robot.setCurrentMp(1);
			break;
		default:
		}
	}

	private void toIllusionistMagic(L1Object o) {
		if (robot.isIllusionist()) {
			Random random = new Random();
			int a = random.nextInt(4);
			switch (a) {
			case 1:
				new L1SkillUse().handleCommands(robot, L1SkillId.MIND_BREAK, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				robot.setCurrentMp(1);
				break;
			case 2:
				new L1SkillUse().handleCommands(robot, L1SkillId.MIND_BREAK, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				robot.setCurrentMp(1);
				break;
			case 3:
				new L1SkillUse().handleCommands(robot, L1SkillId.PANIC, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				robot.setCurrentMp(1);
				break;
			case 4:
				new L1SkillUse().handleCommands(robot, L1SkillId.IllUSION_AVATAR, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				robot.setCurrentMp(1);
				break;

			default:
			}
		}
	}

	private void toElfMagic(L1Object o) {
		if (robot.isElf()) {
			Random random = new Random();
			int a = random.nextInt(6);
			switch (a) {
			case 1:
				Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 4394));
				for (int i = 0; i < 3; ++i) {
					toAttack(o, 0, 0, robot.isElf(), 1, 0);
				}
				robot.setCurrentMp(1);
				break;
			case 2:
				Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 4394));
				for (int i = 0; i < 3; ++i) {
					toAttack(o, 0, 0, robot.isElf(), 1, 0);
				}
				robot.setCurrentMp(1);
				break;
			case 3:
				new L1SkillUse().handleCommands(robot, L1SkillId.STRIKER_GALE, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				robot.setCurrentMp(1);
				break;
			case 4:
				new L1SkillUse().handleCommands(robot, L1SkillId.POLLUTE_WATER, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				robot.setCurrentMp(1);
				break;
			case 5:
				Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 2178));
				robot.setCurrentMp(1);
				break;
			case 6:
				new L1SkillUse().handleCommands(robot, L1SkillId.EARTH_BIND, o.getId(), o.getX(), o.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				robot.setCurrentMp(1);
				break;

			default:
			}
		}
	}
	
	/**
	 * 공격 처리 함수.
	 * 
	 * @param o
	 * @param x
	 * @param y
	 * @param bow
	 * @param gfxMode
	 * @param alpha_dmg
	 */
	private void toAttack(L1Object o, int x, int y, boolean bow, int gfxMode, int alpha_dmg) {

		// 공격 액션을 취할 수 있는 경우의 처리
		if (robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) { // 아브소르트바리아의 해제
			robot.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.ABSOLUTE_BARRIER);
			// pc.startHpRegeneration();
			// pc.startMpRegeneration();
			robot.startHpRegenerationByDoll();
			robot.startMpRegenerationByDoll();
		}
		/** 2011.04.16 고정수 디텍터 */
		if (robot.isElf()) {
			for (L1Object obj : L1World.getInstance().getVisibleObjects(robot)) {
				if (obj instanceof L1MonsterInstance) {
					if (((L1MonsterInstance) obj).getHiddenStatus() >= 1) {
						Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
						Broadcaster.broadcastPacket((L1Character) o, new S_SkillSound(o.getId(), 749));
						((L1MonsterInstance) obj).setHiddenStatus(0);
						robot.setCurrentMp(robot.getCurrentMp() - 10);
					}
				}
			}
		} else {
			for (L1Object obj : L1World.getInstance().getVisibleObjects(robot, 2)) {
				if (obj instanceof L1MonsterInstance) {
					if (((L1MonsterInstance) obj).getHiddenStatus() >= 1) {
						Broadcaster.broadcastPacket(robot, new S_DoActionGFX(robot.getId(), 19));
						Broadcaster.broadcastPacket((L1Character) o, new S_SkillSound(o.getId(), 749));
						((L1MonsterInstance) obj).setHiddenStatus(0);
						robot.setCurrentMp(robot.getCurrentMp() - 10);
					}
				}
			}
		}

		if (robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MEDITATION)) {
			robot.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.MEDITATION);
		}

		robot.delInvis();		
		o.onAction(robot);
		if (robot.getWeapon().getItem().getType() == 17) {
			if (robot.getWeapon().getItemId() == 410003) {
				robot.sendPackets(new S_SkillSound(robot.getId(), 6983));
				Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 6983));
			} else {
				robot.sendPackets(new S_SkillSound(robot.getId(), 7049));
				Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 7049));
			}
		}
	}
	
	/**
	 * 공격목록이 등록되어 있지 않으면 주변에 공격목록을 검색한다.
	 */
	private void toSearchTarget() {
		int distance = 20;
		int temp = 0;
		
		if (type > 1 && robot.getMap().isSafetyZone(robot.getLocation())) {
			robot.getMap().setPassable(robot.getLocation(), true);
			for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(robot)) {
				pc.sendPackets(new S_SkillSound(robot.getId(), 169));
				pc.sendPackets(new S_RemoveObject(robot));
				pc.getNearObjects().removeKnownObject(robot);
			}
			L1Teleport.teleport(robot, 32807, 32732, (short)53, robot.getMoveState().getHeading(), true); 
		}
		
		switch (type) {
		case 1:
			for (L1Object obj : L1World.getInstance().getVisibleObjects(robot)) {
				if (obj instanceof L1ScarecrowInstance) {					
					L1ScarecrowInstance sca = (L1ScarecrowInstance)obj;
					temp = getDistance(robot.getX(), robot.getY(), sca.getX(), sca.getY());	
					
					attackList.add((L1Character)obj, 0);
					
					if (temp <= distance) {
						target = sca;
						distance = temp;
					}
				}
			}
			break;
		case 2:
			for (L1Object obj : L1World.getInstance().getVisibleObjects(robot, 15)) {
				if (obj instanceof L1MonsterInstance) {
					L1MonsterInstance mon = (L1MonsterInstance)obj;
					
					if (mon.isDead()) {
						continue;
					}
					
					if (attackList.containsKey(mon)) {
						continue;
					}
					
					if (!CharPosUtil.glanceCheck(robot, mon.getX(), mon.getY())) {
						continue;
					}
					
					attackList.add(mon, 0);
					
					if (temp <= distance) {
						target = mon;
						distance = temp;
					}
				}
			}
			break;
		default:
			break;
		}
		
	}
	
	/**
	 * 공격목록에 등록된 객체중 위험한 객체를 우선검색해서 리턴. : 리턴된 객체를 타켓으로 공격함.
	 * 
	 * @return
	 */
	private L1Character findDangerousObject() {
		L1Character o = null;

		// 사용자 우선 검색.
		for (L1Character oo : attackList.toTargetFastTable()) {
			if (oo instanceof L1PcInstance) {
				if (oo.isInvisble()) {
					attackList.remove(oo);
					continue;
				}
				
				if (oo.getMapId() != robot.getMapId()) {
					attackList.remove(oo);
					continue;
				}
				
				if (o == null) {
					o = oo;
				} else if (getDistance(robot.getX(), robot.getY(), oo.getX(), oo.getY()) < getDistance(robot.getX(), robot.getY(), o.getX(), o.getY())) {
					o = oo;
				}
			}
		}

		if (o != null) {
			return o;
		}

		// 몬스터 검색.
		for (L1Character oo : attackList.toTargetFastTable()) {
			if (o == null) {
				o = oo;
			} else if (getDistance(robot.getX(), robot.getY(), oo.getX(), oo.getY()) < getDistance(robot.getX(), robot.getY(), o.getX(), o.getY())) {
				o = oo;
			}
		}
		return o;
	}
	
	/**
	 * 물약 복용처리 함수.
	 * 
	 * @param direct
	 */
	private void toHealingPostion(boolean direct) {
		if (robot.isDead()) {
			return;
		}
		
		int p = (int) (((double) robot.getCurrentHp() / (double) robot.getMaxHp()) * 100);
		if (direct || p <= 70) { // 물약 회복 70%
			// if(direct || p <= 90){ // 물약 회복 90%
			// 디케이포션 상태는 회복 저지.
			if (robot.getSkillEffectTimerSet().hasSkillEffect(71))
				return;
			// 앱솔루트베리어의 해제
			robot.cancelAbsoluteBarrier();
			// 이팩트 처리
			Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 197));

			int healHp = 55 * L1Robot.random(1, 5);
			// 포르트워타중은 회복량1/2배
			if (robot.getSkillEffectTimerSet().hasSkillEffect(POLLUTE_WATER))
				healHp /= 2;
			
			robot.setCurrentHp(robot.getCurrentHp() + healHp);
			postionCount--;
		}
		
		
		
		if (postionCount <= 0) {
			setAiStatus(AI_STATUS_SETTING);
			postionCount = CommonUtil.random(700, 1000);
		}

		// 50퍼센트 미만 되면 마을로 이동하기위해 상태 변경.
		if (p < 15) { // 50% 이상시 베르하기 // 기본 15
			setAiStatus(AI_STATUS_SETTING);
		}
	}
	
	private void toPolyMorph() {
		if (robot.getLevel() < 52) {
			return;
		}
		
		// 로봇 변신 처리.		
		if (robot.getGfxId().getGfxId() == robot.getGfxId().getTempCharGfx()) {
			Poly(robot);
			return;
		}
	}
	
	private void toSpeedPostion() {		
		if (robot.isDead()) {
			return;
		}
		if (robot.getMoveState().getMoveSpeed() == 0) {
			// 촐기떠러졌을경우 복용하기.
			Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 191));
			Broadcaster.broadcastPacket(robot, new S_SkillHaste(robot.getId(), 1, 0));
			robot.getMoveState().setMoveSpeed(1);
			robot.getSkillEffectTimerSet().setSkillEffect(STATUS_HASTE, 300 * 1000);
			return;
		}
		/*
		if (!robot.isWizard() && !robot.isDarkelf() && robot.getMoveState().getBraveSpeed() == 0) {
			// 용기 떠러졌을경우 복용하기.
			Broadcaster.broadcastPacket(robot, new S_SkillBrave(robot.getId(), 1, 0));
			robot.getMoveState().setBraveSpeed(1);
			robot.getSkillEffectTimerSet().setSkillEffect(STATUS_BRAVE, 300 * 1000);
			Broadcaster.broadcastPacket(robot, new S_SkillSound(robot.getId(), 751));
			return;
		}
		*/
		if (robot.isWizard() && !robot.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BLUE_POTION)) {
			// 파랭이떠러졌을경우 복용하기.
			robot.sendPackets(new S_SkillIconGFX(34, 600));
			robot.sendPackets(new S_SkillSound(robot.getId(), 190));
			robot.getSkillEffectTimerSet().setSkillEffect(STATUS_BLUE_POTION, 600 * 1000);
			return;
		}
	}
	
	private void toAiDead() {
		ai_time = getFrame(robot.getGfxId().getGfxId(), 0);
		
		if (dieCount <= 5) {
			dieCount++;
			return;
		}
		
		L1PolyMorph.undoPoly(robot);
		
		int[] loc = Getback.GetBack_Location(robot, true);
		robot.getNearObjects().removeAllKnownObjects();		
		Broadcaster.broadcastPacket(robot, new S_RemoveObject(robot));
		robot.setCurrentHp(robot.getLevel());
		robot.set_food(39); // 죽었을때 겟지? 10%
		robot.setDead(false);
		robot.setActionStatus(0);		
		L1World.getInstance().moveVisibleObject(robot, loc[2]);
		robot.setX(loc[0]);
		robot.setY(loc[1]);
		robot.setMap((short) loc[2]);
		Broadcaster.broadcastPacket(robot, new S_OtherCharPacks(robot));
		attackList.clear();
		target = null;
		dieCount = 0;
		
		setAiStatus(AI_STATUS_SETTING);
	}
	
	private void toRandomWalk() {
		toSearchTarget();
		
		if (attackList.toHateFastTable().size() == 0) {		
			robot.getMap().setPassable(robot.getLocation(), true);
			for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(robot)) {
				pc.sendPackets(new S_SkillSound(robot.getId(), 169));
				pc.sendPackets(new S_RemoveObject(robot));
				pc.getNearObjects().removeKnownObject(robot);
			}
			L1Location newLocation = robot.getLocation().randomLocation(200, true);
			int newX = newLocation.getX();
			int newY = newLocation.getY();
			short mapId = (short) newLocation.getMapId();
			L1Teleport.teleport(robot, newX, newY, mapId, robot.getMoveState().getHeading(), true);
			moveCount = 0;
		}
		
		ai_time = getFrame(robot.getGfxId().getGfxId(), 0);
		
		if (type != 1 && !robot.getMap().isSafetyZone(robot.getLocation())) {
			int dir = checkObject(robot.getX(), robot.getY(), robot.getMapId(), CommonUtil.random(20));
			if (dir != -1) {
				ai_time = getFrame(robot.getGfxId().getGfxId(), 0);
				toRandomMoving(dir);					
			}
		}
		
		moveCount++;
		//System.out.println("로봇이름 : " + robot.getName() + " 상태 : " + getAiStatus() + " 타입 : " + type);
		//System.out.println("몹리스트 : " + attackList.toHateArrayList().size() + " 물약수 : " + postionCount + " 움직임 : " + moveCount);
		if (moveCount >= 10) {
			if (robot.getMap().isTeleportable()) {
				robot.getMap().setPassable(robot.getLocation(), true);
				for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(robot)) {
					pc.sendPackets(new S_SkillSound(robot.getId(), 169));
					pc.sendPackets(new S_RemoveObject(robot));
					pc.getNearObjects().removeKnownObject(robot);
				}
				
				L1Location newLocation = robot.getLocation().randomLocation(200, true);
				int newX = newLocation.getX();
				int newY = newLocation.getY();
				short mapId = (short) newLocation.getMapId();
				
				L1Teleport.teleport(robot, newX, newY, mapId, robot.getMoveState().getHeading(), true);
				moveCount = 0;
			} else {
				moveCount = 0;
			}
		}
	}
	
	private void toRandomMoving(int dir) {		
		if (dir >= 0) {
			int nx = 0;
			int ny = 0;

			int heading = 0;
			nx = HEADING_TABLE_X[dir];
			ny = HEADING_TABLE_Y[dir];
			heading = dir;

			robot.getMoveState().setHeading(heading);
			robot.getMap().setPassable(robot.getLocation(), true);

			int nnx = robot.getX() + nx;
			int nny = robot.getY() + ny;			
			
			robot.setX(nnx);
			robot.setY(nny);
			L1WorldTraps.getInstance().onPlayerMoved(robot);		
			robot.getMap().setPassable(robot.getLocation(), false);

			Broadcaster.broadcastPacket(robot, new S_MoveCharPacket(robot));
		}
	}
	
	private void toMoving(L1Object o, int x, int y, int h, boolean astar) {
		if (astar) {
			aStar.ResetPath();
			tail = aStar.FindPath(robot, x, y, robot.getMapId(), target);
			if (tail != null) {
				iCurrentPath = -1;
				while (tail != null) {
					if (tail.x == robot.getX() && tail.y == robot.getY()) {						
						// 현재위치 라면 종료
						break;
					}
					iPath[++iCurrentPath][0] = tail.x;
					iPath[iCurrentPath][1] = tail.y;
					tail = tail.prev;
				}
				/*
				int dir = checkObject(robot.getX(), robot.getY(), robot.getMapId(), CommonUtil.random(20));
				if (dir != -1) {
					ai_time = getFrame(robot.getGfxId().getGfxId(), 0);
					toRandomMoving(dir);	
					return;
				}
				*/
				toMoving(iPath[iCurrentPath][0], iPath[iCurrentPath][1], calcheading(robot.getX(), robot.getY(), iPath[iCurrentPath][0], iPath[iCurrentPath][1]));
				//int dir = targetReverseDirection(target.getX(), target.getY());
				//dir = checkObject(robot.getX(), robot.getY(), robot.getMapId(), dir);
				//setDirectionMove(dir);
				
			} else {}
		} else {
			toMoving(x, y, h);
		}
	}
	
	public int targetReverseDirection(int tx, int ty) {
		int dir = CharPosUtil.targetDirection(robot, tx, ty);
		dir += 4;
		if (dir > 7) {
			dir -= 8;
		}
		return dir;
	}
	
	public void setDirectionMove(int dir) {
		if (dir >= 0) {
			int nx = 0;
			int ny = 0;

			int heading = 0;
			nx = HEADING_TABLE_X[dir];
			ny = HEADING_TABLE_Y[dir];
			heading = dir;

			robot.getMoveState().setHeading(heading);
			robot.getMap().setPassable(robot.getLocation(), true);

			int nnx = robot.getX() + nx;
			int nny = robot.getY() + ny;
			robot.setX(nnx);
			robot.setY(nny);
			L1WorldTraps.getInstance().onPlayerMoved(robot);		
			robot.getMap().setPassable(robot.getLocation(), false);

			Broadcaster.broadcastPacket(robot, new S_MoveCharPacket(robot));
		}
	}
	
	private void toMoving(final int x, final int y, final int h) {
		try {
			robot.getMap().setPassable(robot.getLocation(), true);
			robot.getLocation().set(x, y);
			robot.getMoveState().setHeading(h);
			L1WorldTraps.getInstance().onPlayerMoved(robot);		
			robot.getMap().setPassable(robot.getLocation(), false);
			Broadcaster.broadcastPacket(robot, new S_MoveCharPacket(robot));
			//L1WorldTraps.getInstance().onPlayerMoved(robot);			
			//robot.sendPackets(new S_MoveCharPacket(robot));
			//L1Teleport.teleport(robot, robot.getX(), robot.getY(), robot.getMapId(), robot.getMoveState().getHeading(), false); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean isDistance(int x, int y, int m, int tx, int ty, int tm, int loc) {
		int distance = getDistance(x, y, tx, ty);
		if (loc < distance)
			return false;
		if (m != tm)
			return false;
		return true;
	}

	private int getDistance(int x, int y, int tx, int ty) {
		long dx = tx - x;
		long dy = ty - y;
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	private int getXY(final int h, final boolean type) {
		int loc = 0;
		switch (h) {
		case 0:
			if (!type)
				loc -= 1;
			break;
		case 1:
			if (type)
				loc += 1;
			else
				loc -= 1;
			break;
		case 2:
			if (type)
				loc += 1;
			break;
		case 3:
			loc += 1;
			break;
		case 4:
			if (!type)
				loc += 1;
			break;
		case 5:
			if (type)
				loc -= 1;
			else
				loc += 1;
			break;
		case 6:
			if (type)
				loc -= 1;
			break;
		case 7:
			loc -= 1;
			break;
		}
		return loc;
	}

	private int calcheading(int myx, int myy, int tx, int ty) {
		if (tx > myx && ty > myy) {
			return 3;
		} else if (tx < myx && ty < myy) {
			return 7;
		} else if (tx > myx && ty == myy) {
			return 2;
		} else if (tx < myx && ty == myy) {
			return 6;
		} else if (tx == myx && ty < myy) {
			return 0;
		} else if (tx == myx && ty > myy) {
			return 4;
		} else if (tx < myx && ty > myy) {
			return 5;
		} else {
			return 1;
		}
	}

	public int checkObject(int x, int y, short m, int d) {
		L1Map map = L1WorldMap.getInstance().getMap(m);
		switch (d) {
		case 1:
			if (map.isPassable(x, y, 1)) {
				return 1;
			} else if (map.isPassable(x, y, 0)) {
				return 0;
			} else if (map.isPassable(x, y, 2)) {
				return 2;
			}
			break;
		case 2:
			if (map.isPassable(x, y, 2)) {
				return 2;
			} else if (map.isPassable(x, y, 1)) {
				return 1;
			} else if (map.isPassable(x, y, 3)) {
				return 3;
			}
			break;
		case 3:
			if (map.isPassable(x, y, 3)) {
				return 3;
			} else if (map.isPassable(x, y, 2)) {
				return 2;
			} else if (map.isPassable(x, y, 4)) {
				return 4;
			}
			break;
		case 4:
			if (map.isPassable(x, y, 4)) {
				return 4;
			} else if (map.isPassable(x, y, 3)) {
				return 3;
			} else if (map.isPassable(x, y, 5)) {
				return 5;
			}
			break;
		case 5:
			if (map.isPassable(x, y, 5)) {
				return 5;
			} else if (map.isPassable(x, y, 4)) {
				return 4;
			} else if (map.isPassable(x, y, 6)) {
				return 6;
			}
			break;
		case 6:
			if (map.isPassable(x, y, 6)) {
				return 6;
			} else if (map.isPassable(x, y, 5)) {
				return 5;
			} else if (map.isPassable(x, y, 7)) {
				return 7;
			}
			break;
		case 7:
			if (map.isPassable(x, y, 7)) {
				return 7;
			} else if (map.isPassable(x, y, 6)) {
				return 6;
			} else if (map.isPassable(x, y, 0)) {
				return 0;
			}
			break;
		case 0:
			if (map.isPassable(x, y, 0)) {
				return 0;
			} else if (map.isPassable(x, y, 7)) {
				return 7;
			} else if (map.isPassable(x, y, 1)) {
				return 1;
			}
			break;
		default:
			break;
		}
		return -1;
	}
	
private void Poly(L1PcInstance pc) {
		
		int polyid = 0;
		int time = 1800;
		if (pc.getWeapon() != null) {
			// 타입별 분류
			switch (pc.getWeapon().getItem().getType()) {
			// 활
			case 4:
			case 13:
				if (pc.getLevel() < 55) {
					int[] polyList = { 2284 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 55 && pc.getLevel() < 60) {
					int[] polyList = { 2284, 3892 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 60 && pc.getLevel() < 65) {
					int[] polyList = { 2284, 3892, 3895 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 65 && pc.getLevel() < 70) {
					int[] polyList = { 2284, 3892, 3895, 6275 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 70 && pc.getLevel() < 75) {
					int[] polyList = { 2284, 3892, 3895, 6275, 6278 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 75 && pc.getLevel() < 80) {
					int[] polyList = { 2284, 3892, 3895, 6275, 6278, 8900 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 80) {
					int[] polyList = { 2284, 3892, 3895, 6275, 6278, 8900, 8913  };
					polyid = polyList[CommonUtil.random(polyList.length)];
				}
				L1PolyMorph.doPoly(pc, polyid, time, 1);
				break;
			// 크로우 이도류
			case 11:
			case 12:
				if (pc.getLevel() < 55) {
					int[] polyList = { 6142 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 55 && pc.getLevel() < 60) {
					int[] polyList = { 6142, 5727 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 60 && pc.getLevel() < 65) {
					int[] polyList = { 6142, 5727, 5730 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 65 && pc.getLevel() < 70) {
					int[] polyList = { 6142, 5727, 5730, 6281 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 70 && pc.getLevel() < 75) {
					int[] polyList = { 6142, 5727, 5730, 6281, 6282 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 75 && pc.getLevel() < 80) {
					int[] polyList = { 6142, 5727, 5730, 6281, 6282, 8851 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 80) {
					int[] polyList = { 6142, 5727, 5730, 6281, 6282, 8851, 8978 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				}
				L1PolyMorph.doPoly(pc, polyid, time, 1);
				break;
			// 지팡이
			case 7:
			case 16:
				if (pc.getLevel() < 55) {
					int[] polyList = { 6142 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 55 && pc.getLevel() < 60) {
					int[] polyList = { 6142, 3890 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 60 && pc.getLevel() < 65) {
					int[] polyList = { 6142, 3890, 3893 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 65 && pc.getLevel() < 70) {
					int[] polyList = { 6142, 3890, 3893, 6274 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 70 && pc.getLevel() < 75) {
					int[] polyList = { 6142, 3890, 3893, 6274, 6277 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 75 && pc.getLevel() < 80) {
					int[] polyList = { 6142, 3890, 3893, 6274, 6277, 8817 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 80) {
					int[] polyList = { 6142, 3890, 3893, 6274, 6277, 8817, 8812 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				}
				L1PolyMorph.doPoly(pc, polyid, time, 1);
				break;
			// 그외..
			default:
				if (pc.getLevel() < 55) {
					int[] polyList = { 6142 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 55 && pc.getLevel() < 60) {
					int[] polyList = { 6142, 3890 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 60 && pc.getLevel() < 65) {
					int[] polyList = { 6142, 3890, 3893 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 65 && pc.getLevel() < 70) {
					int[] polyList = { 6142, 3890, 3893, 6273 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 70 && pc.getLevel() < 75) {
					int[] polyList = { 6142, 3890, 3893, 6273, 6276 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 75 && pc.getLevel() < 80) {
					int[] polyList = { 6142, 3890, 3893, 6273, 6276, 8817 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				} else if (pc.getLevel() >= 80) {
					int[] polyList = { 6142, 3890, 3893, 6273, 6276, 8817, 8812 };
					polyid = polyList[CommonUtil.random(polyList.length)];
				}				
				L1PolyMorph.doPoly(pc, polyid, time, 1);
				break;
			}
		} else {
			if (pc.getLevel() < 55) {
				int[] polyList = { 6142 };
				polyid = polyList[CommonUtil.random(polyList.length)];
			} else if (pc.getLevel() >= 55 && pc.getLevel() < 60) {
				int[] polyList = { 6142, 3890 };
				polyid = polyList[CommonUtil.random(polyList.length)];
			} else if (pc.getLevel() >= 60 && pc.getLevel() < 65) {
				int[] polyList = { 6142, 3890, 3893 };
				polyid = polyList[CommonUtil.random(polyList.length)];
			} else if (pc.getLevel() >= 65 && pc.getLevel() < 70) {
				int[] polyList = { 6142, 3890, 3893, 6273 };
				polyid = polyList[CommonUtil.random(polyList.length)];
			} else if (pc.getLevel() >= 70 && pc.getLevel() < 75) {
				int[] polyList = { 6142, 3890, 3893, 6273, 6276 };
				polyid = polyList[CommonUtil.random(polyList.length)];
			} else if (pc.getLevel() >= 75 && pc.getLevel() < 80) {
				int[] polyList = { 6142, 3890, 3893, 6273, 6276, 8817 };
				polyid = polyList[CommonUtil.random(polyList.length)];
			} else if (pc.getLevel() >= 80) {
				int[] polyList = { 6142, 3890, 3893, 6273, 6276, 8817, 8812 };
				polyid = polyList[CommonUtil.random(polyList.length)];
			}	
			L1PolyMorph.doPoly(pc, polyid, time, 1);
		}		
	}
}
