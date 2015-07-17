package server.controller.Robot;

import static l1j.server.server.model.skill.L1SkillId.*;

import javolution.util.FastTable;
import java.util.Random;

import l1j.server.GameSystem.RobotThread;
import l1j.server.GameSystem.bean.RobotLocation;
import l1j.server.server.command.executor.L1Robot;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillSound;

public class RobotControler extends Thread{
	public static boolean _on = true;
	private static FastTable<L1PcInstance> list;
	public static final int RobotRun = 0;
	public static final int RobotRandomMove = 1;
	public static final int RobotAttack = 2;
	private Random _rnd;
	
	private static FastTable<RobotAttackControler> raclist;
	
	public RobotControler(){
		_rnd = new Random();
		list = new FastTable<L1PcInstance>();
		raclist = new FastTable<RobotAttackControler>();
		start();
		for(int i=0; i<5; i++){
			raclist.add(new RobotAttackControler());
		}
	}
	public void run(){
		L1PcInstance[] temp = null;
		int size = 0;
		try{
			while(_on){
				try{
					synchronized(list){
						if((size = list.size()) > 0){
							temp = (L1PcInstance[]) list.toArray(new L1PcInstance[list.size()]);
						}
					}
					if(size > 0){
						for(L1PcInstance pc : temp){
							if(pc == null || !pc.noPlayerCK){
								synchronized(list){
									if(list.contains(pc))
										list.remove(pc);
								}
								continue;
							}
							
							if(CurseCK(pc))
								continue;
							if(!pc.RobotStartCK){
								toSpeedPostion(pc);
								if(pc.getHighLevel() == 56){
									TelePort2(pc);//처음뽑았을때사냥터로텔
								}else if((pc.getHighLevel() == 25 || pc.getHighLevel() == 45))
									HomeTel(pc);
								pc.RobotStartCK = true;
								continue;
							}
							if(pc.isDead()){
								if(pc.RobotDeadDelay == 0){
									pc.RobotDeadDelay = System.currentTimeMillis() + 5000;
								}else if(pc.RobotDeadDelay < System.currentTimeMillis()){
									dead(pc);
									pc.RobotDeadDelay = System.currentTimeMillis() + 5000;
								}
								continue;
							}
							if(pc.RobotDeadDelay != 0){
								if(pc.RobotDeadDelay < System.currentTimeMillis()){
									pc.RobotStartCK = false;
									pc.RobotDeadDelay = 0;
									if(pc.getMapId() ==4 && Math.max(Math.abs(33437 - pc.getX()), Math.abs(32804- pc.getY())) <= 1){
										pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.SHOCK_STUN, 2000);
										homeBuff(pc);
									}
									continue;
								}
								if( pc.getCurrentHp() <=  pc.getMaxHp()/1.3){
									toHealingPostion(pc);
								}
								continue;
							}
							/** Buff **/
							if (pc.getSkillTime() != 0){
								if(pc.getCurrentSkillCount() >= pc.getSkillTime() * 5) {
									RobotBuffControler.addList(pc);
									pc.setCurrentSkillCount(0);
								}else
									pc.setCurrentSkillCount(pc.getCurrentSkillCount() + 1);
							} 
							/** Potion**/
							if(pc.getHighLevel() != 1 && pc.getHighLevel() != 5 && pc.getHighLevel() != 52&& pc.getHighLevel() != 7){
								toHealingPostion(pc);
								toSpeedPostion(pc);
							}
							if(pc.getHighLevel() != 52 && pc.getHighLevel() != 1&& pc.getHighLevel() != 10 && pc.getHighLevel() != 7){
								/** AI **/
								switch(pc.RobotAIType){
									case RobotRandomMove:
										RobotRandomMoveControler.addList(pc);
										break;									
									case RobotAttack:
										boolean ck = false;
										for(RobotAttackControler rc : raclist){
											if(rc.get(pc)){
												pc.RobotAIType = L1PcInstance.RobotRun;
												ck = true;
												break;
											}
										}
										if(ck)
											continue;
										int racsize = 1000;
										RobotAttackControler ra = null;
										for(RobotAttackControler rc : raclist){
											if(racsize > rc.size()){
												racsize = rc.size();
												ra = rc;
											}
										}
										if(ra != null)
											ra.addList(pc);
										else
											pc.RobotAIType = RobotRandomMove;
										break;
									default:
										break;
								}
							}
						}
					}
				}catch(Exception e){
					//System.out.println("- RobotControler Thread While Exception Error -");
					//e.printStackTrace();
				}finally{
					try{
						sleep(1000L);
					}catch(Exception e){}
				}
			}
		} catch (Exception e) {
			//System.out.println("- RobotControler Thread Exception Error -");
			//e.printStackTrace();
		} finally{
			//System.out.println("- RobotControler Thread Restart -");
			new RobotControler();
		}
	}
	
	public static void addList(L1PcInstance pc){
		if(list == null)return;
		synchronized(list){
			if(!list.contains(pc))		
				list.add(pc);
		}
	}
	public static void removeList(L1PcInstance pc){
		if(list == null)return;
		RobotRandomMoveControler.removeList(pc);
		for(RobotAttackControler rc : raclist){
			rc.removeList(pc);
		}
		synchronized(list){
			if(list.contains(pc))		
				list.remove(pc);
		}
	}
	/**
	 * 물약 복용처리 함수.
	 * 
	 * @param direct
	 */
	private void toHealingPostion(L1PcInstance pc) {
		int p = (int) (((double) pc.getCurrentHp() / (double) pc.getMaxHp()) * 100);
		if (p <= 90) { // 물약 회복 70%
			// 디케이포션 상태는 회복 저지.
			if (pc.getSkillEffectTimerSet().hasSkillEffect(71))
				return;
			// 앱솔루트베리어의 해제
			pc.cancelAbsoluteBarrier();
			// 이팩트 처리

			int healHp = 0;
			
			if (p <= 30) { // 물약 회복 30%
				if(pc.getMapId() != 70){
					byte[] s = {-1, 1};
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 169));
					L1Teleport.teleport(pc, 33437+s[_rnd.nextInt(s.length)], 32804+s[_rnd.nextInt(s.length)], (short) 4, 5, false);
					pc.setCurrentHp(pc.getMaxHp()/2);
					pc.RobotDeadDelay = System.currentTimeMillis() + 5000;
				}else{
					healHp = 25 * L1Robot.random(1, 5);
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 197));//말근이이펙트
				}
			} else if (p <= 60) { // 물약 회복 60%
				healHp = 25 * L1Robot.random(1, 5);
				Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 197));//말근이이펙트
				//healHp = 15 * L1Robot.random(1, 5);
				//Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 194));//주홍이이펙트
		    } else if (p <= 90) { // 물약 회복 90%
				healHp = 25 * L1Robot.random(1, 5);
				Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 197));//말근이이펙트
				//healHp = 10 * L1Robot.random(1, 5);
				//Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 189));//빨갱이이펙트
			}
			
			// 포르트워타중은 회복량1/2배
			if (pc.getSkillEffectTimerSet().hasSkillEffect(POLLUTE_WATER))
				healHp /= 2;
			pc.setCurrentHp(pc.getCurrentHp() + healHp);
		}
	}
	/**
	 * 가속도 물약복용 처리 함수.
	 */
	private void toSpeedPostion(L1PcInstance pc) {
		if (pc.getMoveState().getMoveSpeed() == 0) {
			// 촐기떠러졌을경우 복용하기.
			Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 1, 0));
			pc.getMoveState().setMoveSpeed(1);
			pc.getSkillEffectTimerSet().setSkillEffect(STATUS_HASTE, 300 * 1000);
			Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 191));//촐기이펙
			return;
		}
		if (!pc.isWizard()&&!pc.isElf()&&!pc.isDragonknight()&&!pc.isIllusionist()
				&&!pc.isDarkelf() && pc.getMoveState().getBraveSpeed() == 0) {
			// 용기 떠러졌을경우 복용하기.
			Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 1, 0));
			pc.getMoveState().setBraveSpeed(1);
			pc.getSkillEffectTimerSet().setSkillEffect(STATUS_BRAVE, 300 * 1000);
			Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 751));//용기이펙
			return;
			}
		/*if (pc.isWizard() && !pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BLUE_POTION)) {
			// 파랭이떠러졌을경우 복용하기.
			pc.getSkillEffectTimerSet().setSkillEffect(STATUS_BLUE_POTION, 600 * 1000);
			Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 190));//파랭이이펙
			return;
		}*/
	}
	/**
	 * 죽엇을경우  처리 함수.
	 */
	private void dead(L1PcInstance pc){
		int[] loc;
		loc = Getback.GetBack_Location(pc, true);
		pc.getNearObjects().removeAllKnownObjects();
		Broadcaster.broadcastPacket(pc, new S_RemoveObject(pc));
		pc.setCurrentHp(pc.getMaxHp());
		pc.set_food(39); // 죽었을때 겟지? 10%
		pc.setDead(false);
		pc.setActionStatus(0);
		L1World.getInstance().moveVisibleObject(pc, loc[2]);
		pc.setX(loc[0]);
		pc.setY(loc[1]);
		pc.setMap((short) loc[2]);
		pc.RobotTarget = null;
		Broadcaster.broadcastPacket(pc, new S_OtherCharPacks(pc));
	}
	/** 처음뽑았을경우 사냥터 무작위 텔레포트 */
	int[] rxy = { -3, -2, -1, 0, 1, 2, 3 };
	private void TelePort2(L1PcInstance pc) {
		int a = rxy[_rnd.nextInt(rxy.length)];
		RobotLocation location = RobotThread.getLocation();
		L1Teleport.teleport(pc, location.x + a, location.y + a, (short) location.map, 5, true);
		}
	private boolean CurseCK(L1PcInstance pc){
		/** 2011.04.18 고정수 페럴라이즈 상태시 안움직이게 */
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND) || 
				pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHOCK_STUN) || 
				pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ICE_LANCE)  || 
				pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.FREEZING_BLIZZARD) || 
				pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CURSE_PARALYZE) || 
			pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.THUNDER_GRAB) || 
			pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BONE_BREAK)   || 
			pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PHANTASM) || 
			pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PHANTASM) || 
			pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PHANTASM) || 
			pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PHANTASM) || 
			pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_FREEZE)||
			pc.isParalyzed()) {
			return true;
		}
		return false;
	}
	
	private void HomeTel(L1PcInstance pc){
		try{
			if(pc.getMapId() == 350||pc.getMapId() == 34)
				return;
			pc.RobotSleepTime = 2000 + System.currentTimeMillis();
			Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 169));
	        int gn4 = _rnd.nextInt(6) + 1;
	        if (gn4 == 1) {//기란
	        	L1Teleport.teleport(pc, 33439, 32801, (short) 4, 5, false);
	        } else if (gn4 == 2) {//기란
	        	L1Teleport.teleport(pc, 33439, 32796, (short) 4, 5, false);
	        } else if (gn4 == 3) {//기란
	        	L1Teleport.teleport(pc, 33444, 32805, (short) 4, 5, false);//수정
	        } else if (gn4 == 4) {//기란
		         L1Teleport.teleport(pc, 33431, 32822, (short) 4, 5, true);
	        } else if (gn4 == 5) {//기란
		         L1Teleport.teleport(pc, 33439, 32814, (short) 4, 5, true);
            } else if (gn4 == 6) {//버경장
	         L1Teleport.teleport(pc, 33512, 32855, (short) 4, 5, true);
       }
		}catch(Exception e){}
	}
	
	private void homeBuff(L1PcInstance pc){
		int[] allBuffSkill = { HASTE, ADVANCE_SPIRIT,
				NATURES_TOUCH, AQUA_PROTECTER, EARTH_SKIN,
				SHINING_AURA, PATIENCE, CONCENTRATION, INSIGHT,
				FIRE_WEAPON };// /버프 입력
		pc.setBuffnoch(1);
		L1SkillUse l1skilluse = new L1SkillUse();
		for (int i = 0; i < allBuffSkill.length; i++) {
			l1skilluse.handleCommands(pc, allBuffSkill[i], pc
					.getId(), pc.getX(), pc.getY(), null, 0,
					L1SkillUse.TYPE_GMBUFF);
		}
	}
}