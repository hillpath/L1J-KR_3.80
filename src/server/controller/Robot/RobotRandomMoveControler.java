package server.controller.Robot;


import static l1j.server.server.model.skill.L1SkillId.ADVANCE_SPIRIT;
import static l1j.server.server.model.skill.L1SkillId.AQUA_PROTECTER;
import static l1j.server.server.model.skill.L1SkillId.CONCENTRATION;
import static l1j.server.server.model.skill.L1SkillId.EARTH_SKIN;
import static l1j.server.server.model.skill.L1SkillId.FIRE_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.HASTE;
import static l1j.server.server.model.skill.L1SkillId.INSIGHT;
import static l1j.server.server.model.skill.L1SkillId.NATURES_TOUCH;
import static l1j.server.server.model.skill.L1SkillId.PATIENCE;
import static l1j.server.server.model.skill.L1SkillId.SHINING_AURA;

import javolution.util.FastTable;

import java.util.List;
import java.util.Random;

import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_SkillSound;


public class RobotRandomMoveControler extends Thread{
	public static boolean _on = true;
	private static FastTable<L1PcInstance> list;
	private Random _rnd;
	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };	
	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };
	private int MoveSpeed = 640;
	private long curtime = 0;
	public RobotRandomMoveControler(){
		list = new FastTable<L1PcInstance>();
		_rnd = new Random();
		start();
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
						curtime = System.currentTimeMillis();
						for(L1PcInstance pc : temp){
							if(pc == null || !pc.noPlayerCK){
								synchronized(list){
									if(list.contains(pc))
										list.remove(pc);
								}
								continue;
							}
							if(pc.isDead() || CurseCK(pc))continue;
							if(pc.RobotHomeBuff){
								if(curtime > pc.RobotSleepTime){
									pc.RobotHomeBuff = false;
									HomeTel(pc);
								}
								continue;
							}
							if(curtime > pc.RobotSleepTime){
								if(pc.RobotMoveCount <= 0){
								     if(pc.RobotMoveCount != -1 && (pc.getHighLevel() == 25 || pc.getHighLevel() == 45)){
											HomeTel(pc);//마을귀환
									     	setDirectionMove(pc, _rnd.nextInt(8));
									     	pc.RobotSleepTime = calcSleepTime(pc, MoveSpeed) + System.currentTimeMillis();
									}
									pc.RobotMoveCount = 150 + _rnd.nextInt(100);
								}else{//텔사냥가능한곳에서는  랜덤워킹하다가 2초정도 주위에몹을 인식못하면텔
										move(pc);
										
										if(pc.getMapId() != 4 && pc.getMap().isTeleportable()){
											if(pc.RobotTelMoveCount <= 0){
												teleport(pc);
												pc.RobotTelMoveCount = 4;
												continue;
											}else{
												pc.RobotTelMoveCount--;
											}
										}
										if(pc.RobotMoveCount % 3 == 0){
											if(pc.getMapId() ==4 && Math.max(Math.abs(33437 - pc.getX()), Math.abs(32804- pc.getY())) <= 1){
												pc.RobotSleepTime = 2000 + System.currentTimeMillis();
												pc.RobotHomeBuff = true;
												homeBuff(pc);
											}else if(pc.getHighLevel() == 56 && !KnownCk(pc)){
												removeList(pc);
												pc.RobotAIType = L1PcInstance.RobotAttack;
											}
										}
										pc.RobotMoveCount--;
								}
							}
						}
					}
				}catch(Exception e){
					//System.out.println("- RobotRandomMoveControler Thread While Exception Error -");
					//e.printStackTrace();
				}finally{
					try{
						sleep(80L);
					}catch(Exception e){}
				}
			}
		} catch (Exception e) {
			//System.out.println("- RobotRandomMoveControler Thread Exception Error -");
			//e.printStackTrace();
		} finally{
			//System.out.println("- RobotRandomMoveControler Thread Restart -");
			new RobotRandomMoveControler();
		}
	}
	
	public static void addList(L1PcInstance pc){
		if(list == null)return;
		synchronized(list){
			if(!list.contains(pc)){
				pc.RobotAIType = L1PcInstance.RobotRun;
				list.add(pc);
			}
		}
	}
	public static void removeList(L1PcInstance pc){
		if(list == null)return;
		synchronized(list){
			if(list.contains(pc)){
				list.remove(pc);
			}
		}
	}
	
	private void move(L1PcInstance pc){
		try{
		if(pc == null)
			return;
		if (pc._randomMoveDistance <= 0) {
			pc._randomMoveDistance = _rnd.nextInt(3) + 1;
			pc._randomMoveHeading = _rnd.nextInt(8);//20
		} else {
			pc._randomMoveDistance--;
		}
		int dir = checkObject(pc, pc.getX(), pc.getY(), pc.getMapId(), pc._randomMoveHeading);
		if (dir != -1)
			setDirectionMove(pc, dir);
		pc.RobotSleepTime = calcSleepTime(pc, MoveSpeed) + System.currentTimeMillis();
		}catch(Exception e){}
	}
	
	public void setDirectionMove(L1PcInstance pc, int dir) {
		if (dir >= 0) {
			int nx = 0;
			int ny = 0;

			int heading = 0;
			nx = HEADING_TABLE_X[dir];
			ny = HEADING_TABLE_Y[dir];
			heading = dir;
			pc.getMap().setPassable(pc.getLocation(), true);
			pc.getMoveState().setHeading(heading);

			int nnx = pc.getX() + nx;
			int nny = pc.getY() + ny;
			pc.setX(nnx);
			pc.setY(nny);
			Broadcaster.broadcastPacket(pc, new S_MoveCharPacket(pc));
			//pc.getMap().setPassable(pc.getLocation(), false);
		}
	}
	
	public static int checkObject(L1PcInstance pc, int x, int y, short m, int d) { 
		if(pc == null)
			return -1;
		L1Map map = L1WorldMap.getInstance().getMap(m);
		switch(d){
		case 1:
			if (map.isPassable(x, y, 1) && ObjectCk(pc, 1)) {
				return 1;
			} else if (map.isPassable(x, y, 0) && ObjectCk(pc, 0)) {
				return 0;
			} else if (map.isPassable(x, y, 2) && ObjectCk(pc, 2)) {
				return 2;
			}
			break;
		case 2:
			if (map.isPassable(x, y, 2) && ObjectCk(pc, 2)) {
				return 2;
			} else if (map.isPassable(x, y, 1) && ObjectCk(pc, 1)) {
				return 1;
			} else if (map.isPassable(x, y, 3) && ObjectCk(pc, 3)) {
				return 3;
			}
			break;
		case 3:
			if (map.isPassable(x, y, 3) && ObjectCk(pc, 3)) {
				return 3;
			} else if (map.isPassable(x, y, 2) && ObjectCk(pc, 2)) {
				return 2;
			} else if (map.isPassable(x, y, 4) && ObjectCk(pc, 4)) {
				return 4;
			}
			break;
		case 4:
			if (map.isPassable(x, y, 4) && ObjectCk(pc, 4)) {
				return 4;
			} else if (map.isPassable(x, y, 3) && ObjectCk(pc, 3)) {
				return 3;
			} else if (map.isPassable(x, y, 5) && ObjectCk(pc, 5)) {
				return 5;
			}
			break;
		case 5:
			if (map.isPassable(x, y, 5) && ObjectCk(pc, 5)) {
				return 5;
			} else if (map.isPassable(x, y, 4) && ObjectCk(pc, 4)) {
				return 4;
			} else if (map.isPassable(x, y, 6) && ObjectCk(pc, 6)) {
				return 6;
			}
			break;
		case 6:
			if (map.isPassable(x, y, 6) && ObjectCk(pc, 6)) {
				return 6;
			} else if (map.isPassable(x, y, 5) && ObjectCk(pc, 5)) {
				return 5;
			} else if (map.isPassable(x, y, 7) && ObjectCk(pc, 7)) {
				return 7;
			}
			break;
		case 7:
			if (map.isPassable(x, y, 7) && ObjectCk(pc, 7)) {
				return 7;
			} else if (map.isPassable(x, y, 6) && ObjectCk(pc, 6)) {
				return 6;
			} else if (map.isPassable(x, y, 0) && ObjectCk(pc, 0)) {
				return 0;
			}
			break;
		case 0:
			if (map.isPassable(x, y, 0) && ObjectCk(pc, 0)) {
				return 0;
			} else if (map.isPassable(x, y, 7) && ObjectCk(pc, 7)) {
				return 7;
			} else if (map.isPassable(x, y, 1) && ObjectCk(pc, 1)) {
				return 1;
			}
			break;
		default:
			break;
		}
		return -1;
	}
	
	protected int calcSleepTime(L1PcInstance pc, int sleepTime) {
		switch (pc.getMoveState().getMoveSpeed()) {
		case 0:  break;
		case 1:  sleepTime -= (sleepTime * 0.25); break;
		case 2:  sleepTime *= 2; break;
		}
		if (pc.getMoveState().getBraveSpeed() == 1) {
			sleepTime -= (sleepTime * 0.25);
		}
		return sleepTime;
	}
	
	private static boolean ObjectCk(L1PcInstance pc, int dir){
		if(pc == null)
			return false;
		FastTable<L1Object> ol = L1World.getInstance().getVisibleObjects(pc);
		int size = ol.size();
		if(size <= 0)
			return true;
		L1Object[] list = (L1Object[]) ol.toArray(new L1Object[ol.size()]);
		int nx = pc.getX() + HEADING_TABLE_X[dir];
		int ny = pc.getY() + HEADING_TABLE_Y[dir];
		for(L1Object obj : list){
			if(obj == null || yadCk(pc, obj))
				continue;
			if(obj instanceof L1Character){
				L1Character cha = (L1Character)obj;
				if(cha.isDead())
					continue;
			}
			if(obj.getX() == nx && obj.getY() == ny && pc.getMapId() == obj.getMapId())
				return false;
		}
		return true;
	}
	
	private static boolean KnownCk(L1PcInstance pc){
		if(pc == null)
			return true;
		FastTable<L1Object> mol = L1World.getInstance().getVisibleObjects(pc);
		List<L1Character> cl = pc.getRobotTargetlist();
		int clsize = cl.size();
		int mlsize = mol.size();
		if(clsize <= 0 && mlsize <= 0)
			return true;
		L1Character[] list = (L1Character[]) cl.toArray(new L1Character[cl.size()]);
		for(L1Character obj : list){
			if(obj == null || obj.isDead() || yadCk(pc, obj))
				continue;
			else
				return false;
		}
		L1Object[] list2 = (L1Object[]) mol.toArray(new L1Object[mol.size()]);
		for(L1Object obj : list2){
			if(obj == null || !(obj instanceof L1MonsterInstance))
				continue;
			else if(yadCk(pc, obj))
				continue;
			else{
				if(obj instanceof L1Character){
					L1Character cha = (L1Character)obj;
					if(cha.isDead())
						continue;
					if(obj instanceof L1MonsterInstance){
						L1MonsterInstance mon = (L1MonsterInstance) obj;
						if(mon.getNpcTemplate().get_npcId() >= 7000007 && mon.getNpcTemplate().get_npcId() <= 7000016)
							continue;
					}
				}
				return false;
			}
		}
		return true;
	}
	
	private static boolean yadCk(L1PcInstance pc, L1Object mon){
		int escapeDistance = 15;
		int calcx = (int) pc.getLocation().getX() - mon.getLocation().getX();
		int calcy = (int) pc.getLocation().getY() - mon.getLocation().getY();
		return mon.getMapId() != pc.getMapId()|| Math.abs(calcx) > escapeDistance || Math.abs(calcy) > escapeDistance;
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
			pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_FREEZE) ||
			pc.isParalyzed()) {
			return true;
		}
		return false;
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
	
	private void teleport(L1PcInstance pc){
		if (pc.getMap().isTeleportable()) {
			L1Location newLocation = pc.getLocation().randomLocation(200, true);
			int newX = newLocation.getX();
			int newY = newLocation.getY();
			short mapId = (short) newLocation.getMapId();
			L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
		}
	}
}
	