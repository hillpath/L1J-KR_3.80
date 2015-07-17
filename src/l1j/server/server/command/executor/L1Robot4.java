package l1j.server.server.command.executor;


import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.clientpackets.C_CreateNewCharacter;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.serverpackets.S_SummonPack;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.CommonUtil;
import l1j.server.server.utils.SQLUtil;
import server.controller.Robot.RobotAIThread;


public class L1Robot4 implements L1CommandExecutor {

	private static Logger _log = Logger.getLogger(L1Robot4.class.getName());
	
	public static final int[] MALE_LIST = new int[] { 61, 138, 734, 2786, 6658, 6671 };
	public static final int[] FEMALE_LIST = new int[] { 48, 37, 1186, 2796, 6661, 6650 };
	private static final int[] loc = { -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	
	private L1Robot4() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Robot4();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String type = st.nextToken();
			
			if(type.equalsIgnoreCase("1")){
				toAppendBot(pc, st);
				return;
			}
			
			if(type.equalsIgnoreCase("2")){
				toAppendBot2(pc, st);
				return;
			}
			
			if(type.equalsIgnoreCase("3")){
				toAppendBot3(pc, st);
				return;
			}
			
			if(type.equalsIgnoreCase("4")){
				toAppendBot4(pc, st);
				return;
			}
			
			if(type.equalsIgnoreCase("5")){
				toAppendBot5(pc, st);
				return;
			}
			
			if(type.equalsIgnoreCase("6")){
				toAppendBot6(pc, st);
				return;
			}
			
			if(type.equalsIgnoreCase("7")){
				toBotStart(pc, st);
				return;
			}
			
			if(type.equalsIgnoreCase("8")){
				toBotEnd(pc, st);
				return;
			}
			
		} catch (Exception e) { }
		pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
		pc.sendPackets(new S_SystemMessage( ".인공지능 [옵션]" ));
		pc.sendPackets(new S_SystemMessage( " [1:로봇무혈    2:로봇혈맹1 3:로봇혈맹2]"));
		pc.sendPackets(new S_SystemMessage( " [4:로봇혈맹3 5:로봇혈맹4 6:로봇혈맹5]"));
		pc.sendPackets(new S_SystemMessage( " [7:캐릭터시작 8:캐릭터종료]"));
		pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
	}	
	
	/**
	 * 로봇 생성처리 함수.
	 * @param pc
	 * @param st
	 */
	private void toAppendBot(L1PcInstance pc, StringTokenizer st){
		try {			
			int count = Integer.valueOf(st.nextToken());			
			
			// 케릭터 생성 디비 등록.
			while(count-- > 0){
				int startPosType = 0; // default
				int startPos = CommonUtil.random(0, 4);
				int init_hp = 200; 
				int init_mp = 20;
				
				String name = RobotAIThread.getName();
				
				if(name == null){
					pc.sendPackets(new S_SystemMessage( "더이상 생성할 이름이 존재하지않습니다." ));
					return;
				}
				
				L1PcInstance robot = new L1PcInstance();				
				
				robot.setAccountName("인공지능");								// 계정명
				robot.setId(ObjectIdFactory.getInstance().nextId());		// 고유 ID
				robot.setName(name);										// 캐릭터명	
				robot.setLevel(1);											// 레벨
				robot.setHighLevel(1);										// 최고레벨
				robot.setExp(0);											// 경험치
				robot.addBaseMaxHp((short)200);								// 최대 HP
				robot.setCurrentHp(init_hp);								// 현재 HP
				robot.addBaseMaxMp((short)20);								// 최대 MP
				robot.setCurrentMp(init_mp);								// 현재 MP
				robot.getAbility().setBaseStr(25);							// 스텟 STR
				robot.getAbility().setBaseDex(25);							// 스텟 DEX
				robot.getAbility().setBaseCon(25);							// 스텟 CON
				robot.getAbility().setBaseWis(25);							// 스텟 WIS
				robot.getAbility().setBaseCha(25);							// 스텟 CHA
				robot.getAbility().setBaseInt(25);							// 스텟 INT
				//robot.setType(CommonUtil.random(1, 6));						// 1~6(군주제외)
				int rnd = CommonUtil.random(100) + 1;
				String[] classlist = Config.ALT_SCARECROW_CLASS_CHANCE.split(",");				
				if (rnd <= Integer.parseInt(classlist[0])) {
					robot.setType(1);
				} else if (rnd > Integer.parseInt(classlist[0]) && rnd <= Integer.parseInt(classlist[1])) {
					robot.setType(2);
				} else if (rnd > Integer.parseInt(classlist[1]) && rnd <= Integer.parseInt(classlist[2])) {
					robot.setType(3);
				} else if (rnd > Integer.parseInt(classlist[2]) && rnd <= Integer.parseInt(classlist[3])) {
					robot.setType(4);
				} else if (rnd > Integer.parseInt(classlist[3]) && rnd <= Integer.parseInt(classlist[4])) {
					robot.setType(5);
				} else if (rnd > Integer.parseInt(classlist[4]) && rnd <= Integer.parseInt(classlist[5])) {
					robot.setType(6);
				} else {
					robot.setType(1);
				}
				
				rnd = CommonUtil.random(100) + 1;
				
				if (rnd < 70) {
					robot.set_sex(0);
				} else {
					robot.set_sex(1);
				}
				
				//robot.set_sex(CommonUtil.random(0, 1));						// 0~1(남,녀)
				// 클래스 이미지
				if (robot.get_sex() == 0) {
					robot.setClassId(C_CreateNewCharacter.MALE_LIST[robot.getType()]);
				} else { 					
					robot.setClassId(C_CreateNewCharacter.FEMALE_LIST[robot.getType()]);
				}
				robot.setX(C_CreateNewCharacter.START_LOC_X[CommonUtil.random(3)]);
				robot.setY(C_CreateNewCharacter.START_LOC_Y[CommonUtil.random(3)]);	
				robot.setMap((short)2005);
				robot.getMoveState().setHeading(0);
				robot.set_food(39); // 17%
				robot.setLawful(0);
				robot.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				robot.setClanid(0);
				robot.setClanname("");
				robot.setClanRank(0);
				robot.resetBaseAc();				
				robot.setGm(false);
				robot.setGmInvis(false);				
				robot.setActionStatus(0);
				robot.setAccessLevel((short)0);				
				robot.getAbility().setBonusAbility(0);
				robot.resetBaseMr();
				robot.setElfAttr(0);
				robot.setExpRes(0);
				robot.setPartnerId(0);
				robot.setOnlineStatus(0);
				robot.setHomeTownId(0);
				robot.setContribution(0);
				robot.setBanned(false);
				robot.setKarma(0);
				robot.setReturnStat(0);
				robot.setGdungeonTime(0);
				robot.calAinHasad(0);				
				
				CharacterTable.getInstance().storeNewCharacter(robot);
				robot.refresh();
			}
			pc.sendPackets(new S_SystemMessage( "인공지능 캐릭터가 생성되었습니다." ));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage( ".인공지능 캐릭터생성 [갯수]" ));
		}
	}
	private void toAppendBot2(L1PcInstance pc, StringTokenizer st){
		try {			
			int count = Integer.valueOf(st.nextToken());			
			
			// 케릭터 생성 디비 등록.
			while(count-- > 0){
				int startPosType = 0; // default
				int startPos = CommonUtil.random(0, 4);
				int init_hp = 200; 
				int init_mp = 20;
				
				String name = RobotAIThread.getName();
				
				if(name == null){
					pc.sendPackets(new S_SystemMessage( "더이상 생성할 이름이 존재하지않습니다." ));
					return;
				}
				
				L1PcInstance robot = new L1PcInstance();				
				
				robot.setAccountName("인공지능");								// 계정명
				robot.setId(ObjectIdFactory.getInstance().nextId());		// 고유 ID
				robot.setName(name);										// 캐릭터명	
				robot.setLevel(1);											// 레벨
				robot.setHighLevel(1);										// 최고레벨
				robot.setExp(0);											// 경험치
				robot.addBaseMaxHp((short)200);								// 최대 HP
				robot.setCurrentHp(init_hp);								// 현재 HP
				robot.addBaseMaxMp((short)20);								// 최대 MP
				robot.setCurrentMp(init_mp);								// 현재 MP
				robot.getAbility().setBaseStr(25);							// 스텟 STR
				robot.getAbility().setBaseDex(25);							// 스텟 DEX
				robot.getAbility().setBaseCon(25);							// 스텟 CON
				robot.getAbility().setBaseWis(25);							// 스텟 WIS
				robot.getAbility().setBaseCha(25);							// 스텟 CHA
				robot.getAbility().setBaseInt(25);							// 스텟 INT
				//robot.setType(CommonUtil.random(1, 6));						// 1~6(군주제외)
				int rnd = CommonUtil.random(100) + 1;
				String[] classlist = Config.ALT_SCARECROW_CLASS_CHANCE.split(",");				
				if (rnd <= Integer.parseInt(classlist[0])) {
					robot.setType(1);
				} else if (rnd > Integer.parseInt(classlist[0]) && rnd <= Integer.parseInt(classlist[1])) {
					robot.setType(2);
				} else if (rnd > Integer.parseInt(classlist[1]) && rnd <= Integer.parseInt(classlist[2])) {
					robot.setType(3);
				} else if (rnd > Integer.parseInt(classlist[2]) && rnd <= Integer.parseInt(classlist[3])) {
					robot.setType(4);
				} else if (rnd > Integer.parseInt(classlist[3]) && rnd <= Integer.parseInt(classlist[4])) {
					robot.setType(5);
				} else if (rnd > Integer.parseInt(classlist[4]) && rnd <= Integer.parseInt(classlist[5])) {
					robot.setType(6);
				} else {
					robot.setType(1);
				}
				
				rnd = CommonUtil.random(100) + 1;
				
				if (rnd < 70) {
					robot.set_sex(0);
				} else {
					robot.set_sex(1);
				}
				
				//robot.set_sex(CommonUtil.random(0, 1));						// 0~1(남,녀)
				// 클래스 이미지
				if (robot.get_sex() == 0) {
					robot.setClassId(C_CreateNewCharacter.MALE_LIST[robot.getType()]);
				} else { 					
					robot.setClassId(C_CreateNewCharacter.FEMALE_LIST[robot.getType()]);
				}
				robot.setX(C_CreateNewCharacter.START_LOC_X[CommonUtil.random(3)]);
				robot.setY(C_CreateNewCharacter.START_LOC_Y[CommonUtil.random(3)]);	
				robot.setMap((short)2005);
				robot.getMoveState().setHeading(0);
				robot.set_food(39); // 17%
				robot.setLawful(0);
				robot.setTitle("");
				robot.setClanid(1);
				robot.setClanname("전투명가");
				robot.setClanRank(0);
				robot.resetBaseAc();				
				robot.setGm(false);
				robot.setGmInvis(false);				
				robot.setActionStatus(0);
				robot.setAccessLevel((short)0);				
				robot.getAbility().setBonusAbility(0);
				robot.resetBaseMr();
				robot.setElfAttr(0);
				robot.setExpRes(0);
				robot.setPartnerId(0);
				robot.setOnlineStatus(0);
				robot.setHomeTownId(0);
				robot.setContribution(0);
				robot.setBanned(false);
				robot.setKarma(0);
				robot.setReturnStat(0);
				robot.setGdungeonTime(0);
				robot.calAinHasad(0);				
				
				CharacterTable.getInstance().storeNewCharacter(robot);
				robot.refresh();
			}
			pc.sendPackets(new S_SystemMessage( "인공지능 캐릭터가 생성되었습니다." ));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage( ".인공지능 캐릭터생성 [갯수]" ));
		}
	}
	private void toAppendBot3(L1PcInstance pc, StringTokenizer st){
		try {			
			int count = Integer.valueOf(st.nextToken());			
			
			// 케릭터 생성 디비 등록.
			while(count-- > 0){
				int startPosType = 0; // default
				int startPos = CommonUtil.random(0, 4);
				int init_hp = 200; 
				int init_mp = 20;
				
				String name = RobotAIThread.getName();
				
				if(name == null){
					pc.sendPackets(new S_SystemMessage( "더이상 생성할 이름이 존재하지않습니다." ));
					return;
				}
				
				L1PcInstance robot = new L1PcInstance();				
				
				robot.setAccountName("인공지능");								// 계정명
				robot.setId(ObjectIdFactory.getInstance().nextId());		// 고유 ID
				robot.setName(name);										// 캐릭터명	
				robot.setLevel(1);											// 레벨
				robot.setHighLevel(1);										// 최고레벨
				robot.setExp(0);											// 경험치
				robot.addBaseMaxHp((short)200);								// 최대 HP
				robot.setCurrentHp(init_hp);								// 현재 HP
				robot.addBaseMaxMp((short)20);								// 최대 MP
				robot.setCurrentMp(init_mp);								// 현재 MP
				robot.getAbility().setBaseStr(25);							// 스텟 STR
				robot.getAbility().setBaseDex(25);							// 스텟 DEX
				robot.getAbility().setBaseCon(25);							// 스텟 CON
				robot.getAbility().setBaseWis(25);							// 스텟 WIS
				robot.getAbility().setBaseCha(25);							// 스텟 CHA
				robot.getAbility().setBaseInt(25);							// 스텟 INT
				//robot.setType(CommonUtil.random(1, 6));						// 1~6(군주제외)
				int rnd = CommonUtil.random(100) + 1;
				String[] classlist = Config.ALT_SCARECROW_CLASS_CHANCE.split(",");				
				if (rnd <= Integer.parseInt(classlist[0])) {
					robot.setType(1);
				} else if (rnd > Integer.parseInt(classlist[0]) && rnd <= Integer.parseInt(classlist[1])) {
					robot.setType(2);
				} else if (rnd > Integer.parseInt(classlist[1]) && rnd <= Integer.parseInt(classlist[2])) {
					robot.setType(3);
				} else if (rnd > Integer.parseInt(classlist[2]) && rnd <= Integer.parseInt(classlist[3])) {
					robot.setType(4);
				} else if (rnd > Integer.parseInt(classlist[3]) && rnd <= Integer.parseInt(classlist[4])) {
					robot.setType(5);
				} else if (rnd > Integer.parseInt(classlist[4]) && rnd <= Integer.parseInt(classlist[5])) {
					robot.setType(6);
				} else {
					robot.setType(1);
				}
				
				rnd = CommonUtil.random(100) + 1;
				
				if (rnd < 70) {
					robot.set_sex(0);
				} else {
					robot.set_sex(1);
				}
				
				//robot.set_sex(CommonUtil.random(0, 1));						// 0~1(남,녀)
				// 클래스 이미지
				if (robot.get_sex() == 0) {
					robot.setClassId(C_CreateNewCharacter.MALE_LIST[robot.getType()]);
				} else { 					
					robot.setClassId(C_CreateNewCharacter.FEMALE_LIST[robot.getType()]);
				}
				robot.setX(C_CreateNewCharacter.START_LOC_X[CommonUtil.random(3)]);
				robot.setY(C_CreateNewCharacter.START_LOC_Y[CommonUtil.random(3)]);	
				robot.setMap((short)2005);
				robot.getMoveState().setHeading(0);
				robot.set_food(39); // 17%
				robot.setLawful(0);
				robot.setTitle("");
				robot.setClanid(2);
				robot.setClanname("헉");
				robot.setClanRank(0);
				robot.resetBaseAc();				
				robot.setGm(false);
				robot.setGmInvis(false);				
				robot.setActionStatus(0);
				robot.setAccessLevel((short)0);				
				robot.getAbility().setBonusAbility(0);
				robot.resetBaseMr();
				robot.setElfAttr(0);
				robot.setExpRes(0);
				robot.setPartnerId(0);
				robot.setOnlineStatus(0);
				robot.setHomeTownId(0);
				robot.setContribution(0);
				robot.setBanned(false);
				robot.setKarma(0);
				robot.setReturnStat(0);
				robot.setGdungeonTime(0);
				robot.calAinHasad(0);				
				
				CharacterTable.getInstance().storeNewCharacter(robot);
				robot.refresh();
			}
			pc.sendPackets(new S_SystemMessage( "인공지능 캐릭터가 생성되었습니다." ));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage( ".인공지능 캐릭터생성 [갯수]" ));
		}
	}
	private void toAppendBot4(L1PcInstance pc, StringTokenizer st){
		try {			
			int count = Integer.valueOf(st.nextToken());			
			
			// 케릭터 생성 디비 등록.
			while(count-- > 0){
				int startPosType = 0; // default
				int startPos = CommonUtil.random(0, 4);
				int init_hp = 200; 
				int init_mp = 20;
				
				String name = RobotAIThread.getName();
				
				if(name == null){
					pc.sendPackets(new S_SystemMessage( "더이상 생성할 이름이 존재하지않습니다." ));
					return;
				}
				
				L1PcInstance robot = new L1PcInstance();				
				
				robot.setAccountName("인공지능");								// 계정명
				robot.setId(ObjectIdFactory.getInstance().nextId());		// 고유 ID
				robot.setName(name);										// 캐릭터명	
				robot.setLevel(1);											// 레벨
				robot.setHighLevel(1);										// 최고레벨
				robot.setExp(0);											// 경험치
				robot.addBaseMaxHp((short)200);								// 최대 HP
				robot.setCurrentHp(init_hp);								// 현재 HP
				robot.addBaseMaxMp((short)20);								// 최대 MP
				robot.setCurrentMp(init_mp);								// 현재 MP
				robot.getAbility().setBaseStr(25);							// 스텟 STR
				robot.getAbility().setBaseDex(25);							// 스텟 DEX
				robot.getAbility().setBaseCon(25);							// 스텟 CON
				robot.getAbility().setBaseWis(25);							// 스텟 WIS
				robot.getAbility().setBaseCha(25);							// 스텟 CHA
				robot.getAbility().setBaseInt(25);							// 스텟 INT
				//robot.setType(CommonUtil.random(1, 6));						// 1~6(군주제외)
				int rnd = CommonUtil.random(100) + 1;
				String[] classlist = Config.ALT_SCARECROW_CLASS_CHANCE.split(",");				
				if (rnd <= Integer.parseInt(classlist[0])) {
					robot.setType(1);
				} else if (rnd > Integer.parseInt(classlist[0]) && rnd <= Integer.parseInt(classlist[1])) {
					robot.setType(2);
				} else if (rnd > Integer.parseInt(classlist[1]) && rnd <= Integer.parseInt(classlist[2])) {
					robot.setType(3);
				} else if (rnd > Integer.parseInt(classlist[2]) && rnd <= Integer.parseInt(classlist[3])) {
					robot.setType(4);
				} else if (rnd > Integer.parseInt(classlist[3]) && rnd <= Integer.parseInt(classlist[4])) {
					robot.setType(5);
				} else if (rnd > Integer.parseInt(classlist[4]) && rnd <= Integer.parseInt(classlist[5])) {
					robot.setType(6);
				} else {
					robot.setType(1);
				}
				
				rnd = CommonUtil.random(100) + 1;
				
				if (rnd < 70) {
					robot.set_sex(0);
				} else {
					robot.set_sex(1);
				}
				
				//robot.set_sex(CommonUtil.random(0, 1));						// 0~1(남,녀)
				// 클래스 이미지
				if (robot.get_sex() == 0) {
					robot.setClassId(C_CreateNewCharacter.MALE_LIST[robot.getType()]);
				} else { 					
					robot.setClassId(C_CreateNewCharacter.FEMALE_LIST[robot.getType()]);
				}
				robot.setX(C_CreateNewCharacter.START_LOC_X[CommonUtil.random(3)]);
				robot.setY(C_CreateNewCharacter.START_LOC_Y[CommonUtil.random(3)]);	
				robot.setMap((short)2005);
				robot.getMoveState().setHeading(0);
				robot.set_food(39); // 17%
				robot.setLawful(0);
				robot.setTitle("");
				robot.setClanid(3);
				robot.setClanname("우리는하나");
				robot.setClanRank(0);
				robot.resetBaseAc();				
				robot.setGm(false);
				robot.setGmInvis(false);				
				robot.setActionStatus(0);
				robot.setAccessLevel((short)0);				
				robot.getAbility().setBonusAbility(0);
				robot.resetBaseMr();
				robot.setElfAttr(0);
				robot.setExpRes(0);
				robot.setPartnerId(0);
				robot.setOnlineStatus(0);
				robot.setHomeTownId(0);
				robot.setContribution(0);
				robot.setBanned(false);
				robot.setKarma(0);
				robot.setReturnStat(0);
				robot.setGdungeonTime(0);
				robot.calAinHasad(0);				
				
				CharacterTable.getInstance().storeNewCharacter(robot);
				robot.refresh();
			}
			pc.sendPackets(new S_SystemMessage( "인공지능 캐릭터가 생성되었습니다." ));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage( ".인공지능 캐릭터생성 [갯수]" ));
		}
	}
	private void toAppendBot5(L1PcInstance pc, StringTokenizer st){
		try {			
			int count = Integer.valueOf(st.nextToken());			
			
			// 케릭터 생성 디비 등록.
			while(count-- > 0){
				int startPosType = 0; // default
				int startPos = CommonUtil.random(0, 4);
				int init_hp = 200; 
				int init_mp = 20;
				
				String name = RobotAIThread.getName();
				
				if(name == null){
					pc.sendPackets(new S_SystemMessage( "더이상 생성할 이름이 존재하지않습니다." ));
					return;
				}
				
				L1PcInstance robot = new L1PcInstance();				
				
				robot.setAccountName("인공지능");								// 계정명
				robot.setId(ObjectIdFactory.getInstance().nextId());		// 고유 ID
				robot.setName(name);										// 캐릭터명	
				robot.setLevel(1);											// 레벨
				robot.setHighLevel(1);										// 최고레벨
				robot.setExp(0);											// 경험치
				robot.addBaseMaxHp((short)200);								// 최대 HP
				robot.setCurrentHp(init_hp);								// 현재 HP
				robot.addBaseMaxMp((short)20);								// 최대 MP
				robot.setCurrentMp(init_mp);								// 현재 MP
				robot.getAbility().setBaseStr(25);							// 스텟 STR
				robot.getAbility().setBaseDex(25);							// 스텟 DEX
				robot.getAbility().setBaseCon(25);							// 스텟 CON
				robot.getAbility().setBaseWis(25);							// 스텟 WIS
				robot.getAbility().setBaseCha(25);							// 스텟 CHA
				robot.getAbility().setBaseInt(25);							// 스텟 INT
				//robot.setType(CommonUtil.random(1, 6));						// 1~6(군주제외)
				int rnd = CommonUtil.random(100) + 1;
				String[] classlist = Config.ALT_SCARECROW_CLASS_CHANCE.split(",");				
				if (rnd <= Integer.parseInt(classlist[0])) {
					robot.setType(1);
				} else if (rnd > Integer.parseInt(classlist[0]) && rnd <= Integer.parseInt(classlist[1])) {
					robot.setType(2);
				} else if (rnd > Integer.parseInt(classlist[1]) && rnd <= Integer.parseInt(classlist[2])) {
					robot.setType(3);
				} else if (rnd > Integer.parseInt(classlist[2]) && rnd <= Integer.parseInt(classlist[3])) {
					robot.setType(4);
				} else if (rnd > Integer.parseInt(classlist[3]) && rnd <= Integer.parseInt(classlist[4])) {
					robot.setType(5);
				} else if (rnd > Integer.parseInt(classlist[4]) && rnd <= Integer.parseInt(classlist[5])) {
					robot.setType(6);
				} else {
					robot.setType(1);
				}
				
				rnd = CommonUtil.random(100) + 1;
				
				if (rnd < 70) {
					robot.set_sex(0);
				} else {
					robot.set_sex(1);
				}
				
				//robot.set_sex(CommonUtil.random(0, 1));						// 0~1(남,녀)
				// 클래스 이미지
				if (robot.get_sex() == 0) {
					robot.setClassId(C_CreateNewCharacter.MALE_LIST[robot.getType()]);
				} else { 					
					robot.setClassId(C_CreateNewCharacter.FEMALE_LIST[robot.getType()]);
				}
				robot.setX(C_CreateNewCharacter.START_LOC_X[CommonUtil.random(3)]);
				robot.setY(C_CreateNewCharacter.START_LOC_Y[CommonUtil.random(3)]);	
				robot.setMap((short)2005);
				robot.getMoveState().setHeading(0);
				robot.set_food(39); // 17%
				robot.setLawful(0);
				robot.setTitle("");
				robot.setClanid(4);
				robot.setClanname("총군연합");
				robot.setClanRank(0);
				robot.resetBaseAc();				
				robot.setGm(false);
				robot.setGmInvis(false);				
				robot.setActionStatus(0);
				robot.setAccessLevel((short)0);				
				robot.getAbility().setBonusAbility(0);
				robot.resetBaseMr();
				robot.setElfAttr(0);
				robot.setExpRes(0);
				robot.setPartnerId(0);
				robot.setOnlineStatus(0);
				robot.setHomeTownId(0);
				robot.setContribution(0);
				robot.setBanned(false);
				robot.setKarma(0);
				robot.setReturnStat(0);
				robot.setGdungeonTime(0);
				robot.calAinHasad(0);				
				
				CharacterTable.getInstance().storeNewCharacter(robot);
				robot.refresh();
			}
			pc.sendPackets(new S_SystemMessage( "인공지능 캐릭터가 생성되었습니다." ));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage( ".인공지능 캐릭터생성 [갯수]" ));
		}
	}
	private void toAppendBot6(L1PcInstance pc, StringTokenizer st){
		try {			
			int count = Integer.valueOf(st.nextToken());			
			
			// 케릭터 생성 디비 등록.
			while(count-- > 0){
				int startPosType = 0; // default
				int startPos = CommonUtil.random(0, 4);
				int init_hp = 200; 
				int init_mp = 20;
				
				String name = RobotAIThread.getName();
				
				if(name == null){
					pc.sendPackets(new S_SystemMessage( "더이상 생성할 이름이 존재하지않습니다." ));
					return;
				}
				
				L1PcInstance robot = new L1PcInstance();				
				
				robot.setAccountName("인공지능");								// 계정명
				robot.setId(ObjectIdFactory.getInstance().nextId());		// 고유 ID
				robot.setName(name);										// 캐릭터명	
				robot.setLevel(1);											// 레벨
				robot.setHighLevel(1);										// 최고레벨
				robot.setExp(0);											// 경험치
				robot.addBaseMaxHp((short)200);								// 최대 HP
				robot.setCurrentHp(init_hp);								// 현재 HP
				robot.addBaseMaxMp((short)20);								// 최대 MP
				robot.setCurrentMp(init_mp);								// 현재 MP
				robot.getAbility().setBaseStr(25);							// 스텟 STR
				robot.getAbility().setBaseDex(25);							// 스텟 DEX
				robot.getAbility().setBaseCon(25);							// 스텟 CON
				robot.getAbility().setBaseWis(25);							// 스텟 WIS
				robot.getAbility().setBaseCha(25);							// 스텟 CHA
				robot.getAbility().setBaseInt(25);							// 스텟 INT
				//robot.setType(CommonUtil.random(1, 6));						// 1~6(군주제외)
				int rnd = CommonUtil.random(100) + 1;
				String[] classlist = Config.ALT_SCARECROW_CLASS_CHANCE.split(",");				
				if (rnd <= Integer.parseInt(classlist[0])) {
					robot.setType(1);
				} else if (rnd > Integer.parseInt(classlist[0]) && rnd <= Integer.parseInt(classlist[1])) {
					robot.setType(2);
				} else if (rnd > Integer.parseInt(classlist[1]) && rnd <= Integer.parseInt(classlist[2])) {
					robot.setType(3);
				} else if (rnd > Integer.parseInt(classlist[2]) && rnd <= Integer.parseInt(classlist[3])) {
					robot.setType(4);
				} else if (rnd > Integer.parseInt(classlist[3]) && rnd <= Integer.parseInt(classlist[4])) {
					robot.setType(5);
				} else if (rnd > Integer.parseInt(classlist[4]) && rnd <= Integer.parseInt(classlist[5])) {
					robot.setType(6);
				} else {
					robot.setType(1);
				}
				
				rnd = CommonUtil.random(100) + 1;
				
				if (rnd < 70) {
					robot.set_sex(0);
				} else {
					robot.set_sex(1);
				}
				
				//robot.set_sex(CommonUtil.random(0, 1));						// 0~1(남,녀)
				// 클래스 이미지
				if (robot.get_sex() == 0) {
					robot.setClassId(C_CreateNewCharacter.MALE_LIST[robot.getType()]);
				} else { 					
					robot.setClassId(C_CreateNewCharacter.FEMALE_LIST[robot.getType()]);
				}
				robot.setX(C_CreateNewCharacter.START_LOC_X[CommonUtil.random(3)]);
				robot.setY(C_CreateNewCharacter.START_LOC_Y[CommonUtil.random(3)]);	
				robot.setMap((short)2005);
				robot.getMoveState().setHeading(0);
				robot.set_food(39); // 17%
				robot.setLawful(0);
				robot.setTitle("");
				robot.setClanid(5);
				robot.setClanname("꽃게천국");
				robot.setClanRank(0);
				robot.resetBaseAc();				
				robot.setGm(false);
				robot.setGmInvis(false);				
				robot.setActionStatus(0);
				robot.setAccessLevel((short)0);				
				robot.getAbility().setBonusAbility(0);
				robot.resetBaseMr();
				robot.setElfAttr(0);
				robot.setExpRes(0);
				robot.setPartnerId(0);
				robot.setOnlineStatus(0);
				robot.setHomeTownId(0);
				robot.setContribution(0);
				robot.setBanned(false);
				robot.setKarma(0);
				robot.setReturnStat(0);
				robot.setGdungeonTime(0);
				robot.calAinHasad(0);				
				
				CharacterTable.getInstance().storeNewCharacter(robot);
				robot.refresh();
			}
			pc.sendPackets(new S_SystemMessage( "인공지능 캐릭터가 생성되었습니다." ));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage( ".인공지능 캐릭터생성 [갯수]" ));
		}
	}
	/**
	 * 로봇 인공지능 활성화.
	 * @param pc
	 * @param st
	 */
	private void toBotStart(L1PcInstance pc, StringTokenizer st){
		try {
			int type = Integer.valueOf(st.nextToken());	
			int count = Integer.valueOf(st.nextToken());
			

			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				con = L1DatabaseFactory.getInstance().getConnection();
				if (type == 1) {
					pstm = con.prepareStatement("SELECT * FROM characters WHERE account_name = '인공지능' and level < 52 order by rand()");
				} else if (type == 2) {
					pstm = con.prepareStatement("SELECT * FROM characters WHERE account_name = '인공지능' and level >= 52  order by rand()");
				} else if (type == 3) {
					pstm = con.prepareStatement("SELECT * FROM characters WHERE account_name = '인공지능' order by rand()");
				}
				
				rs = pstm.executeQuery();
				while (rs.next()) {
					L1PcInstance player = L1World.getInstance().getPlayer(rs.getString("char_name"));
					
					if (player != null) {
						continue;
					}
					
					if (count > 0) {
						L1PcInstance robot = L1PcInstance.load(rs.getString("char_name"));
						
						L1Map map = pc.getMap();
						
						int x = 0;
						int y = 0;
						
						if (type == 1) {
							while (true) {
								x = loc[CommonUtil.random(17)];
								y = loc[CommonUtil.random(17)];
								robot.setX(pc.getX() + x);
								robot.setY(pc.getY() + y);
								robot.setMap(pc.getMapId());
								if (map.isPassable(robot.getX(), robot.getY())) {
									break;
								}
							}
						} else if (type == 2) {
							while (true) {
								x = loc[CommonUtil.random(17)];
								y = loc[CommonUtil.random(17)];
								robot.setX(pc.getX() + x);
								robot.setY(pc.getY() + y);
								robot.setMap(pc.getMapId());
								if (map.isPassable(robot.getX(), robot.getY())) {
									break;
								}
							}
						} else if (type == 3) {
							while (true) {
								x = loc[CommonUtil.random(17)];
								y = loc[CommonUtil.random(17)];
								robot.setX(pc.getX() + x);
								robot.setY(pc.getY() + y);
								robot.setMap(pc.getMapId());
								if (map.isPassable(robot.getX(), robot.getY())) {
									break;
								}
							}
						}
						robot.getMoveState().setHeading(CommonUtil.random(0, 7));
						robot.clearSkillMastery();
						robot.setOnlineStatus(1);						
						robot.setNetConnection(null);						
						robot.beginGameTimeCarrier();
						robot.sendVisualEffectAtLogin();
						robot.setSpeedHackCount(0);
						robot.setDead(false);
						robot.setActionStatus(0);
						robot.noPlayerCK = true;
						
						for (L1SummonInstance summon : L1World.getInstance().getAllSummons()) {
							if (summon.getMaster().getId() == robot.getId()) {
								summon.setMaster(robot);
								robot.addPet(summon);
								for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(summon)) {
									visiblePc.sendPackets(new S_SummonPack(summon, visiblePc));
								}
							}
						}
						WarTimeController.getInstance().checkCastleWar(robot);
						robot.getAC().setAc(-(robot.getLevel()+10));	
						
						L1World.getInstance().storeObject(robot);
						L1World.getInstance().addVisibleObject(robot);
						
						if (robot.getResistance().getMr() <= 145) {
							int mr = 145 - robot.getResistance().getMr();
							robot.getResistance().addMr(mr);
						}

						items(robot);
						
						for (L1ItemInstance item : robot.getInventory().getItems()) {								
							robot.getInventory().removeItem(item);
						}
						
						if (robot.getLevel() >= 51) {
							if (robot.isKnight()) {
								robot.getAbility().addAddedStr(robot.getLevel() - 50);
							} else if (robot.isCrown()) {
								robot.getAbility().addAddedStr(robot.getLevel() - 50);
							} else if (robot.isElf()) {
								robot.getAbility().addAddedDex(robot.getLevel() - 50);
							} else if (robot.isWizard()) {
								robot.getAbility().addAddedInt(robot.getLevel() - 50);
							} else if (robot.isDarkelf()) {
								robot.getAbility().addAddedStr(robot.getLevel() - 50);
							} else if (robot.isIllusionist()) {
								robot.getAbility().addAddedInt(robot.getLevel() - 50);
							} else if (robot.isDragonknight()) {
								robot.getAbility().addAddedStr(robot.getLevel() - 50);
							}	
						}
						
						
						if (type == 3) {
							if (robot.isKnight()) {
								robot.setCurrentWeapon(50);
							} else if (robot.isCrown()) {
								robot.setCurrentWeapon(4);
							} else if (robot.isElf()) {
								robot.setCurrentWeapon(20);
							} else if (robot.isWizard()) {
								robot.setCurrentWeapon(40);
							} else if (robot.isDarkelf()) {
								robot.setCurrentWeapon(54);
							} else if (robot.isIllusionist()) {
								robot.setCurrentWeapon(40);
							} else if (robot.isDragonknight()) {
								robot.setCurrentWeapon(50);
							}	
						} else {						
							if (robot.isKnight()) {
								boolean isWeapon = false;
								
								for (L1ItemInstance item : robot.getInventory().getItems()) {								
									if (item.getItemId() == 62) {
										isWeapon = true;
										if (!item.isEquipped()) {
											robot.getInventory().setEquipped(item, true);
										}
									}
								}	
								if (!isWeapon) {
									L1ItemInstance item = ItemTable.getInstance().createItem(62);
									item.setEnchantLevel(7);
									robot.getInventory().storeItem(item);
									robot.getInventory().setEquipped(item, true);				
								}							
							} else if (robot.isElf()) {
								boolean isBow = false;
								
								for (L1ItemInstance item : robot.getInventory().getItems()) {								
									if (item.getItemId() == 181) {
										isBow = true;
										if (!item.isEquipped()) {
											robot.getInventory().setEquipped(item, true);
										}
									}
								}
								
								if (!isBow) {
									L1ItemInstance item = ItemTable.getInstance().createItem(181);
									item.setEnchantLevel(7);
									robot.getInventory().storeItem(item);
									robot.getInventory().setEquipped(item, true);				
								}
							} else if (robot.isWizard()) {
								boolean isWeapon = false;
								
								for (L1ItemInstance item : robot.getInventory().getItems()) {								
									if (item.getItemId() == 126) {
										isWeapon = true;
										if (!item.isEquipped()) {
											robot.getInventory().setEquipped(item, true);
										}
									}
								}
								
								if (!isWeapon) {
									L1ItemInstance item = ItemTable.getInstance().createItem(126);
									item.setEnchantLevel(7);
									robot.getInventory().storeItem(item);
									robot.getInventory().setEquipped(item, true);				
								}
							} else if (robot.isDragonknight()) {
								boolean isWeapon = false;
								
								for (L1ItemInstance item : robot.getInventory().getItems()) {								
									if (item.getItemId() == 62) {
										isWeapon = true;
										if (!item.isEquipped()) {
											robot.getInventory().setEquipped(item, true);
										}
									}
								}
								
								if (!isWeapon) {
									L1ItemInstance item = ItemTable.getInstance().createItem(62);
									item.setEnchantLevel(7);
									robot.getInventory().storeItem(item);
									robot.getInventory().setEquipped(item, true);				
								}
							} else if (robot.isIllusionist()) {
								boolean isWeapon = false;
								
								for (L1ItemInstance item : robot.getInventory().getItems()) {								
									if (item.getItemId() == 410004) {
										isWeapon = true;
										if (!item.isEquipped()) {
											robot.getInventory().setEquipped(item, true);
										}
									}
								}
								
								if (!isWeapon) {
									L1ItemInstance item = ItemTable.getInstance().createItem(410004);
									item.setEnchantLevel(7);
									robot.getInventory().storeItem(item);
									robot.getInventory().setEquipped(item, true);				
								}
							} else if (robot.isDarkelf()) {
								boolean isWeapon = false;
								int weaponType = 0;
								int[] weaponKind = { 81, 162 };
								
								weaponType = weaponKind[CommonUtil.random(2)];
								
								for (L1ItemInstance item : robot.getInventory().getItems()) {								
									if (item.getItemId() == weaponKind[0]) {
										isWeapon = true;
										if (!item.isEquipped()) {
											robot.getInventory().setEquipped(item, true);
										}
									}
								}	
								if (!isWeapon) {
									L1ItemInstance item = ItemTable.getInstance().createItem(weaponType);
									item.setEnchantLevel(7);
									robot.getInventory().storeItem(item);
									robot.getInventory().setEquipped(item, true);				
								}
							}
						}
						
						if (type == 3) {
							if (CommonUtil.random(100) < 75) {
								int rnd1 = CommonUtil.random(20, 60);
								robot.setTeleportTime(rnd1);
								
								int rnd2 = CommonUtil.random(5, 60);
								
								if (rnd1 == rnd2) {
									rnd2++;
								}
								robot.setSkillTime(rnd2);
							}
						}
						
						
						//if (random(0, 200) == 100) {
							//int dollRnd = L1Robot.random(0, 15);
							//dollStart(bot, dollRnd);
						//}		
						
						if (type <= 2) {
							robot.getRobotAi().setType(type);
							RobotAIThread.append(robot, type);
							
							if (type == 1) {
								robot.getRobotAi().setAiStatus(robot.getRobotAi().AI_STATUS_WALK);
							} else if (type == 2) {
								robot.getRobotAi().setAiStatus(robot.getRobotAi().AI_STATUS_SETTING);
							}
						}
						
						// 기본 레벨 세팅.
						//if(robot.getLevel() < 53){
						//	robot.addExp((ExpTable.getExpByLevel(53)-1) - bot.getExp()-((ExpTable.getExpByLevel(53)-1)/100));
						//	robot.CheckChangeExp();
						//}
						
						count--;
					}
				}
			} catch (SQLException e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
			
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage( ".인공지능 시작 [1:허수아비용 3:마을허상][갯수]" ));
		}
	}
	
	/**
	 * 로봇 인공지능 비활성화.
	 * @param pc
	 * @param st
	 */
	private void toBotEnd(L1PcInstance pc, StringTokenizer st){
		try {
			int type = Integer.parseInt(st.nextToken());
			int count = 0;
			for (Object obj : L1World.getInstance().getAllPlayers()) {
				if (obj instanceof L1PcInstance) {
					L1PcInstance player = (L1PcInstance)obj;
					if (!player.isPrivateShop()) {
						if (player.getRobotAi() != null && player.getRobotAi().type == type) {		
							if (type == 1) {
								count++;
							}
							RobotAIThread.remove(player, type);		
							player.setRobotAi(null);
							player.logout();
							if (type == 1 && count >= 5) {
								pc.sendPackets(new S_SystemMessage( "허수아비 인공지능 " + count + " 캐릭이 종료되었습니다."));
								count = 0;
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage( ".인공지능 종료 [1:허수아비용]" ));
		}
	}
	
	private void items(L1PcInstance pc) {
		// DB로부터 캐릭터와 창고의 아이템을 읽어들인다
		CharacterTable.getInstance().restoreInventory(pc);
	}
}