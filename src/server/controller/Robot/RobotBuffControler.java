package server.controller.Robot;

import static l1j.server.server.model.skill.L1SkillId.*;

import javolution.util.FastTable;
import java.util.Random;

import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.RobotTable;
import l1j.server.server.datatables.RobotTable.RobotTeleport;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Npc;

public class RobotBuffControler extends Thread{
	public static boolean _on = true;
	private static FastTable<L1PcInstance> list;
	private L1SkillUse skilluse;
	private int[] loc = { -2, -1, 0, 1, 2 }; // 추가
	private static int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, ADVANCE_SPIRIT, IRON_SKIN, DECREASE_WEIGHT};
	private long curtime = 0; 
	private Random _random;

	public RobotBuffControler(){
		list = new FastTable<L1PcInstance>();
		skilluse = new L1SkillUse();
		_random = new Random();
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
						curtime = System.currentTimeMillis() / 1000;
						for(L1PcInstance pc : temp){
							if(pc == null || !pc.noPlayerCK){
								synchronized(list){
									list.remove(pc);
								}
								continue;
							}
							if(pc.RobotBuffTime == 0)pc.RobotBuffTime = curtime + _random.nextInt(10);
							if(pc.isDead() || curtime < pc.RobotBuffTime || CurseCK(pc))continue;
							if(basebuff(pc)) pc.RobotBuffTime = curtime + 5;
							//if(Chatbuff(pc)) pc.RobotBuffTime = curtime + 10;//채팅스레드
							else if (pc.getHighLevel() >= 40) {
								if(pc.isKnight())				KnightBuff(pc);
								else if(pc.isElf())				ElfBuff(pc);
								else if(pc.isWizard())			WizardBuff(pc);
								else if(pc.isDarkelf())			DarkelfBuff(pc);
								else if(pc.isDragonknight())	DragonKnightBuff(pc);					
								else if(pc.isIllusionist()) 	IllusionBuff(pc);
								else if(pc.isCrown()) 			CrownBuff(pc);
							}
							if(curtime < pc.RobotBuffTime)continue;
							synchronized(list){
								list.remove(pc);
							}
							int RTsize = RobotTable.getRobotTeleportList().size();
							if(RTsize > 0){
								RobotTeleport robotTeleport = RobotTable.getRobotTeleportList().get(_random.nextInt(RTsize));
								if(robotTeleport != null)
									L1Teleport.teleport(pc, robotTeleport.x + loc[_random.nextInt(5)], robotTeleport.y + loc[_random.nextInt(5)], (short)robotTeleport.mapid, robotTeleport.heading, 0);
							}
						}	
					}
				}catch(Exception e){
					//System.out.println("- RobotBuffControler Thread While Exception Error -");
					//e.printStackTrace();
				}finally{
					try{
						sleep(1000L);
					}catch(Exception e){}
				}
			}
		} catch (Exception e) {
			//System.out.println("- RobotBuffControler Thread Exception Error -");
			//e.printStackTrace();
		} finally{
			//System.out.println("- RobotBuffControler Thread Restart -");
			new RobotBuffControler();
		}
	}
	
	public static void addList(L1PcInstance pc){
		if(list == null)return;
		synchronized(list){
			if(!list.contains(pc))		
				list.add(pc);
		}
	}
	int[] cookEff = {6390, 6392, 6394, 6390, 6392, 6390};
	/** 사냥터 주변 무작위 텔레포트 */
/*	private boolean Chatbuff(L1PcInstance pc) {
		int rnd = _random.nextInt(2);
		switch(rnd){
		case 0:
		pc.toMent(0);
	//	pc.toMent2(0);
		break;
		case 1:
		pc.toMent2(0);
		break;
		}
		pc.RobotBuffTime = curtime + 60;
		return true;
	}*/
	private boolean basebuff(L1PcInstance pc) {
		if(pc.getHighLevel() >= 20 && pc.getHighLevel() <= 30){
			int rnd = _random.nextInt(2);
			switch(rnd){
			case 0:
				HelpDoll(pc, 437018);
			break;
			case 1:
				HelpDoll(pc, 437018);
			break;
		}
		}
		if(pc.getHighLevel() >= 20){
		int rnd = _random.nextInt(2);
		switch(rnd){
		case 0:
		pc.toMent(0);//주석
	//	pc.toMent2(0);
		break;
		case 1:
		pc.toMent2(0);
		break;
		}
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PHYSICAL_ENCHANT_STR) && pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PHYSICAL_ENCHANT_DEX))
			return false;
		try{
			if (pc.getHighLevel() >= 40) {
				for (int i = 0; i < allBuffSkill.length; i++)
					skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				pc.setQuizTime(curtime);
			}else if (pc.getHighLevel() == 10){
				L1Teleport.teleport(pc, pc.getX(), pc.getY(),pc.getMapId(),pc.getMoveState().getHeading(), true);
			}else if (pc.getHighLevel() == 1){
				boolean fireck = false;
				for (L1Object object : L1World.getInstance().getVisibleObjects(pc, 3)) {
					if (object instanceof L1EffectInstance) {
						if (((L1NpcInstance) object).getNpcTemplate().get_npcId() == 81170) {
							fireck = true;
							break;
						}
					}
				}
				if(fireck)
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), cookEff[_random.nextInt(cookEff.length)]));
				else{
					int[] loc = new int[2];
					loc = CharPosUtil.getFrontLoc(pc.getX(), pc.getY(), pc.getMoveState().getHeading());
					L1EffectSpawn.getInstance().spawnEffect(81170, 600000,loc[0], loc[1], pc.getMapId());
				}
			}else if (pc.getHighLevel() >= 20 && pc.getHighLevel() <= 30){
	        	if(pc.isKnight())
	        	L1PolyMorph.doPoly(pc, 1612, 50, L1PolyMorph.MORPH_BY_ITEMMAGIC);//랫맨
	        	if(pc.isIllusionist())
	        		L1PolyMorph.doPoly(pc, 1112, 7200, L1PolyMorph.MORPH_BY_ITEMMAGIC);//서큐버스
	        	if(pc.isDragonknight())
	        	L1PolyMorph.doPoly(pc, 1108, 7200, L1PolyMorph.MORPH_BY_ITEMMAGIC);//라이칸스로프
			}
		}catch(Exception e){}
		return true;
	}

	private boolean DragonKnightBuff(L1PcInstance pc){
		if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLOOD_LUST)) {
			L1PolyMorph.doPoly(pc, 7332, 7200, L1PolyMorph.MORPH_BY_ITEMMAGIC);//랜마
			skilluse.handleCommands(pc, L1SkillId.BLOOD_LUST, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		}else if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGON_SKIN)) {
			skilluse.handleCommands(pc, L1SkillId.DRAGON_SKIN, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		//}else if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MORTAL_BODY)) {
		//	skilluse.handleCommands(pc, L1SkillId.MORTAL_BODY, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		}else
			return false;
		Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_SkillBuff));
		pc.RobotBuffTime = curtime + 7;
		return true;
	}
	
	private boolean CrownBuff(L1PcInstance pc){
		if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GLOWING_AURA)) {
			L1PolyMorph.doPoly(pc, 6137, 7200, L1PolyMorph.MORPH_BY_ITEMMAGIC);//데스나이트
			skilluse.handleCommands(pc, L1SkillId.GLOWING_AURA, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		}else if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHINING_AURA) && pc.getLevel() >= 55) {
			skilluse.handleCommands(pc, L1SkillId.SHINING_AURA, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		}else
			return false;
		Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_SkillBuff));
		pc.RobotBuffTime = curtime + 7;
		return true;
	}
	
	private boolean WizardBuff(L1PcInstance pc){
		L1PolyMorph.doPoly(pc, 6698, 7200, L1PolyMorph.MORPH_BY_ITEMMAGIC);//흑마법사터번
		pc.RobotBuffTime = curtime + 7;
		return true;
	}
	private boolean IllusionBuff(L1PcInstance pc){
	if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.CONCENTRATION)) {
		L1PolyMorph.doPoly(pc, 6137, 7200,L1PolyMorph.MORPH_BY_ITEMMAGIC);//데스
		Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_SkillBuff));
		skilluse.handleCommands(pc, L1SkillId.CONCENTRATION, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
	}else if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.PATIENCE)) {
		Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_SkillBuff));
		skilluse.handleCommands(pc, L1SkillId.PATIENCE, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		}else if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INSIGHT)) {
		Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_SkillBuff));
		skilluse.handleCommands(pc, L1SkillId.INSIGHT, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		}else
			return false;
		Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_SkillBuff));
		pc.RobotBuffTime = curtime + 7;
		return true;
	}
	
	private boolean DarkelfBuff(L1PcInstance pc){
		if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MOVING_ACCELERATION)) {
			L1PolyMorph.doPoly(pc, 6137, 7200,L1PolyMorph.MORPH_BY_ITEMMAGIC);//데스나이트
			skilluse.handleCommands(pc, L1SkillId.MOVING_ACCELERATION, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		}else if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.UNCANNY_DODGE)) {
			skilluse.handleCommands(pc, L1SkillId.UNCANNY_DODGE, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		}else if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BURNING_SPIRIT)) {
			skilluse.handleCommands(pc, L1SkillId.BURNING_SPIRIT, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		}else if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DOUBLE_BRAKE)) {
			skilluse.handleCommands(pc, L1SkillId.DOUBLE_BRAKE, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		}else if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHADOW_FANG)) {
			pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.SHADOW_FANG, 300000);
			skilluse.handleCommands(pc, L1SkillId.SHADOW_FANG, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		}else
			return false;
		Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_SkillBuff));
		pc.RobotBuffTime = curtime + 7;
		return true;
	}
	
	private boolean ElfBuff(L1PcInstance pc){
		if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLOODY_SOUL)) {
			L1PolyMorph.doPoly(pc, 2284, 7200,L1PolyMorph.MORPH_BY_ITEMMAGIC);//다크엘프
			pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.BLOODY_SOUL, 15000);
			skilluse.handleCommands(pc, L1SkillId.BLOODY_SOUL, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		}else
			return false;
		Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_SkillBuff));
		pc.RobotBuffTime = curtime + 7;
		return true;
	}
	
	private boolean KnightBuff(L1PcInstance pc){
		if (!pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.REDUCTION_ARMOR)) {
			L1PolyMorph.doPoly(pc, 6137, 7200, L1PolyMorph.MORPH_BY_ITEMMAGIC);//데스나이트
			skilluse.handleCommands(pc, L1SkillId.REDUCTION_ARMOR, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
			pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.REDUCTION_ARMOR, 100000);
		}else
			return false;
		Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_SkillBuff));
		pc.RobotBuffTime = curtime + 7;
		return true;
	}
	private void HelpDoll(L1PcInstance pc, int dollid){
		Object[] dollList = pc.getDollList().values().toArray();
		if(dollList.length > 0){
			return;
		}
			int npcId = 0;
			int dollType = 0;
			int dollTime = 0;

			switch (dollid) {
			case L1ItemId.DOLL_BUGBEAR: // 버그베어
				npcId = 80106;
				dollType = L1DollInstance.DOLLTYPE_BUGBEAR;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_SUCCUBUS: // 서큐버스
				npcId = 80107;
				dollType = L1DollInstance.DOLLTYPE_SUCCUBUS;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_WAREWOLF: // 늑대인간
				npcId = 80108;
				dollType = L1DollInstance.DOLLTYPE_WAREWOLF;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_STONEGOLEM: // 돌골렘
				npcId = 4500150;
				dollType = L1DollInstance.DOLLTYPE_STONEGOLEM;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_ELDER: // 장로
				npcId = 4500151;
				dollType = L1DollInstance.DOLLTYPE_ELDER;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_CRUSTACEA: // 시안
				npcId = 4500152;
				dollType = L1DollInstance.DOLLTYPE_CRUSTACEA;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_SEADANCER: // 시댄서
				npcId = 4500153;
				dollType = L1DollInstance.DOLLTYPE_SEADANCER;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_SNOWMAN: // 에티
				npcId = 4500154;
				dollType = L1DollInstance.DOLLTYPE_SNOWMAN;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_COCATRIS: // 코카트리스
				npcId = 4500155;
				dollType = L1DollInstance.DOLLTYPE_COCATRIS;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_DRAGON_M: // 해츨링
				npcId = 4500156;
				dollType = L1DollInstance.DOLLTYPE_DRAGON_M;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_DRAGON_W: // 해츨링
				npcId = 4500157;
				dollType = L1DollInstance.DOLLTYPE_DRAGON_W;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_HIGH_DRAGON_M: // 진화해츨링
				npcId = 4500158;
				dollType = L1DollInstance.DOLLTYPE_HIGH_DRAGON_M;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_HIGH_DRAGON_W: // 진화해츨링
				npcId = 4500159;
				dollType = L1DollInstance.DOLLTYPE_HIGH_DRAGON_W;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_LAMIA:// 라미아
				npcId = 4500160;
				dollType = L1DollInstance.DOLLTYPE_LAMIA;
				dollTime = 1800;
				break;
			case 500400: // 스켈
				npcId = 75022;
				dollType = L1DollInstance.DOLLTYPE_SKELETON;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_SCARECROW: // by포비 허수아비
				npcId = 41916;
				dollType = L1DollInstance.DOLLTYPE_SCARECROW;
				dollTime = 1800;
				break;
			case L1ItemId.DOLL_ETTIN: // 에틴
				npcId = 41917;
				dollType = L1DollInstance.DOLLTYPE_ETTIN;
				dollTime = 1800;
				break;
			case 500144: // 눈사람(A)
				npcId = 700196;
				dollType = L1DollInstance.DOLLTYPE_SNOWMAN_A;
				dollTime = 1800;
				break;
			case 500145: // 눈사람(B)
				npcId = 700197;
				dollType = L1DollInstance.DOLLTYPE_SNOWMAN_B;
				dollTime = 1800;
				break;
			case 500146: // 눈사람(C)
				npcId = 700198;
				dollType = L1DollInstance.DOLLTYPE_SNOWMAN_C;

				dollTime = 1800;
				break; // 추가
			case 437018:
				npcId = 4000009;
				dollType = L1DollInstance.DOLLTYPE_HELPER;
				dollTime = 1800;
				break;
			}
			L1Npc template = NpcTable.getInstance().getTemplate(npcId);
			new L1DollInstance(template, pc, dollType, 0, dollTime * 1000);
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
}
	