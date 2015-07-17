package l1j.server.server.command.executor;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.StringTokenizer;

import l1j.server.Config;
import l1j.server.GameSystem.RobotThread;
import l1j.server.GameSystem.bean.RobotFishing;
import l1j.server.server.ActionCodes;
import l1j.server.server.BadNamesList;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.CommonUtil;
import server.controller.Robot.RobotControler;

public class L1Robot3 implements L1CommandExecutor {

	private static Random _random =  new Random(System.nanoTime());


	private static final int[] MALE_LIST = new int[] { 61, 138, 2786, 48, 37, 2796};
	private static final int[] FEMALE_LIST = new int[] { 61, 138, 2786, 48, 37, 2796 };
	//private static final int[] MALE_LIST = new int[] { 61, 138, 734, 2786, 6658, 48, 37, 1186, 2796, 6661, 6650 };
	//private static final int[] FEMALE_LIST = new int[] { 48, 37, 1186, 2796, 6661, 6650 };
	//private static final int[] WEAPON_LIST = new int[] { 41, 172, 125, 80, 52, 410003 };

	private L1Robot3() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Robot3();
	}

	
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);	
			//CodeLogger.getInstance().gmlog(
					//"GMCOMMAND",
				//	"캐릭=" + pc.getName() + "[" + pc.getAccountName() + "]"
					//		+ "	명령어="  +"  로봇  " + arg + "	IP="
						//	+ pc.getNetConnection().getHostname());
			int robot = Integer.parseInt(tok.nextToken());
			int count = Integer.parseInt(tok.nextToken());
			int isteleport = 0;
			
			try {
				isteleport = Integer.parseInt(tok.nextToken());
			} catch (Exception e) {
				isteleport = 0;
			}
			
			int SearchCount = 0;
			
			L1Map map = pc.getMap();
			
			int x = 0;
			int y = 0;

			int[] loc = { -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8 };
			
			pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
			while(count-- > 0){
				String name = RobotThread.getName();
				if(name == null){
					pc.sendPackets(new S_SystemMessage( "더이상 생성할 이름이 존재하지않습니다." ));
					RobotThread.list_name_idx = 0;
					return;
				}
				
				L1PcInstance player = L1World.getInstance().getPlayer(name);
				
				if (player != null) {
					continue;
				}
				
				L1PcInstance newPc = new L1PcInstance();
				newPc.setAccountName("");
				newPc.setId(ObjectIdFactory.getInstance().nextId());
				newPc.setName(name);

				if (robot == 0) { // 인공지능ON 제자리텔포사용 숨계
					newPc.setHighLevel(10);//레벨구간
					newPc.setLevel(10);
					newPc.setExp(0);
					newPc.setLawful(0);
					newPc.setClanid(0);
					newPc.setClanname("");
//					랜덤 호칭
					int rnd = _random.nextInt(8);
					switch(rnd){
					case 0:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 1:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 2:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 3:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 4:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 5:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 6:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 7:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
                    break;
					}
//			랜덤 호칭
				} else if (robot == 1){ //인공지능ON 변신.스킬.무브.텔포사용 오픈시간흐르고뽑을애들 마을
					int rnd = _random.nextInt(8);
					newPc.setHighLevel(45);//레벨구간
					newPc.setLevel(45);
					newPc.setExp(28877490);
					newPc.setClanid(0);
					newPc.setClanname("");
					switch(rnd){
					case 0:
					newPc.setLawful(32767);
					break;
					case 1:
					newPc.setLawful(2000);
					break;
					case 2:
					newPc.setLawful(3000);
					break;
					case 3:
					newPc.setLawful(4000);
					break;
					case 4:
					newPc.setLawful(5000);
					break;
					case 5:
					newPc.setLawful(6000);
					break;
					case 6:
					newPc.setLawful(7000);
					break;
					case 7:
					newPc.setLawful(7500);
					break;	
					}
					switch(rnd){
					case 0:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 1:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 2:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 3:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 4:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 5:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 6:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 7:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						}
				} else if (robot == 2){ //인공지능ON  변신.스킬.무브.텔포사용오픈시간흐르고뽑을애들 마을
					newPc.setHighLevel(45);//레벨구간
					newPc.setLevel(45);
					newPc.setExp(28877490);
					newPc.setLawful(-20000);
					newPc.setClanid(0);
					newPc.setClanname("");
					int rnd = _random.nextInt(8);
					switch(rnd){
					case 0:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 1:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 2:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 3:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 4:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 5:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 6:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 7:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						}
				} else if (robot == 3) { //인공지능ON  무브.텔포사용 초반오픈용 마을
					newPc.setHighLevel(25);//레벨구간
					newPc.setLevel(25);
					newPc.setExp(0);
					newPc.setLawful(0);
					newPc.setClanid(0);
					newPc.setClanname("");
//					랜덤 호칭
					int rnd = _random.nextInt(8);
					switch(rnd){
					case 0:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 1:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 2:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 3:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 4:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 5:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 6:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						case 7:
							newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
						break;
						}
			} else if (robot == 4) { //인공지능ON  변신.버프  창고세워둘거
				int rnd = _random.nextInt(8);
				newPc.setHighLevel(52);
				newPc.setLevel(52);
				newPc.setExp(0);
				newPc.setClanid(0);
				newPc.setClanname("");
				switch(rnd){
				case 0:
				newPc.setLawful(32767);
				break;
				case 1:
				newPc.setLawful(2000);
				break;
				case 2:
				newPc.setLawful(3000);
				break;
				case 3:
				newPc.setLawful(4000);
				break;
				case 4:
				newPc.setLawful(5000);
				break;
				case 5:
				newPc.setLawful(6000);
				break;
				case 6:
				newPc.setLawful(7000);
				break;
				case 7:
				newPc.setLawful(7500);
				break;	
				}
//				랜덤 호칭
				switch(rnd){
				case 0:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 1:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 2:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 3:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 4:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 5:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 6:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 7:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					}
			} else if (robot == 5) { //인공지능ON  사냥.무브.버프.변신
				int rnd = _random.nextInt(8);
				newPc.setHighLevel(56);
				newPc.setLevel(56);
				newPc.setExp(0);
				newPc.setClanid(0);
				newPc.setClanname("");
				switch(rnd){
				case 0:
				newPc.setLawful(32767);
				break;
				case 1:
				newPc.setLawful(2000);
				break;
				case 2:
				newPc.setLawful(3000);
				break;
				case 3:
				newPc.setLawful(4000);
				break;
				case 4:
				newPc.setLawful(5000);
				break;
				case 5:
				newPc.setLawful(6000);
				break;
				case 6:
				newPc.setLawful(7000);
				break;
				case 7:
				newPc.setLawful(7500);
				break;	
				}
//				랜덤 호칭
				switch(rnd){
				case 0:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 1:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 2:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 3:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 4:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 5:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 6:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					case 7:
						newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
					break;
					}
				////////////////////혈맹로봇/////////////////////////
			} else if (robot == 6) { //인공지능ON  사냥.무브.버프.변신
				int rnd = _random.nextInt(8);
				newPc.setHighLevel(56);
				newPc.setLevel(56);
				newPc.setExp(0);
				newPc.setClanid(298400658);
				newPc.setClanname("기르타스");
				switch(rnd){
				case 0:
				newPc.setLawful(32767);
				break;
				case 1:
				newPc.setLawful(2000);
				break;
				case 2:
				newPc.setLawful(3000);
				break;
				case 3:
				newPc.setLawful(-4000);
				break;
				case 4:
				newPc.setLawful(5000);
				break;
				case 5:
				newPc.setLawful(6000);
				break;
				case 6:
				newPc.setLawful(7000);
				break;
				case 7:
				newPc.setLawful(7500);
				break;	
				}
//				랜덤 호칭
				switch(rnd){
				case 0:
				newPc.setTitle ("\\f<  기르버스타스 ");
				break;
				case 1:
				newPc.setTitle ("\\f<  기르타스");
				break;
				case 2:
				newPc.setTitle ("\\f<  기르택시타스");
				break;
				case 3:
				newPc.setTitle ("\\f<  호칭따윈없다ㅋ");
				break;
				case 4:
				newPc.setTitle ("\\f<  기르커피타스");
				break;
				case 5:
				newPc.setTitle ("\\f<  기르오줌타스");
				break;
				case 6:
				newPc.setTitle ("\\f<  기르용돈타스");
				break;
				case 7:
				newPc.setTitle ("\\f<  기르라면타스");
				break;
				}
			} else if (robot == 7) { //인공지능ON  사냥.무브.버프.변신
				int rnd = _random.nextInt(8);
				newPc.setHighLevel(52);
				newPc.setLevel(52);
				newPc.setExp(0);
				newPc.setClanid(298400658);
				newPc.setClanname("간지");
				switch(rnd){
				case 0:
				newPc.setLawful(32767);
				break;
				case 1:
				newPc.setLawful(2000);
				break;
				case 2:
				newPc.setLawful(3000);
				break;
				case 3:
				newPc.setLawful(-4000);
				break;
				case 4:
				newPc.setLawful(5000);
				break;
				case 5:
				newPc.setLawful(6000);
				break;
				case 6:
				newPc.setLawful(7000);
				break;
				case 7:
				newPc.setLawful(7500);
				break;	
				}
//				랜덤 호칭
				switch(rnd){
				case 0:
				newPc.setTitle ("\\f<  간 지 필 드");
				break;
				case 1:
				newPc.setTitle ("");
				break;
				case 2:
				newPc.setTitle ("\\f<  간 지 필 드");
				break;
				case 3:
				newPc.setTitle ("\\f<  보스존");
				break;
				case 4:
				newPc.setTitle ("\\f<  간 지 필 드");
				break;
				case 5:
				newPc.setTitle ("\\f<  간 지 필 드");
				break;
				case 6:
				newPc.setTitle ("\\f=  혈원받음");
				break;
				case 7:
				newPc.setTitle ("\\f<  간 지 필 드");
				break;
				}
				////////////////////혈맹로봇/////////////////////////
			} else if (robot == 8) { //인공지능ON  사냥.무브.버프.변신
				int rnd = _random.nextInt(8);
				newPc.setHighLevel(56);
				newPc.setLevel(56);
				newPc.setExp(0);
				newPc.setClanid(298400760);
				newPc.setClanname("악마");
				switch(rnd){
				case 0:
				newPc.setLawful(32767);
				break;
				case 1:
				newPc.setLawful(2000);
				break;
				case 2:
				newPc.setLawful(3000);
				break;
				case 3:
				newPc.setLawful(-4000);
				break;
				case 4:
				newPc.setLawful(5000);
				break;
				case 5:
				newPc.setLawful(6000);
				break;
				case 6:
				newPc.setLawful(7000);
				break;
				case 7:
				newPc.setLawful(7500);
				break;	
				}
//				랜덤 호칭
				switch(rnd){
				case 0:
				newPc.setTitle ("\\f3  악 마 군 단");
				break;
				case 1:
				newPc.setTitle ("");
				break;
				case 2:
				newPc.setTitle ("\\f3  악 마 청 단");
				break;
				case 3:
				newPc.setTitle ("");
				break;
				case 4:
				newPc.setTitle ("\\f3  악 마 홍 단");
				break;
				case 5:
				newPc.setTitle ("\\f3  악 마 초 단");
				break;
				case 6:
				newPc.setTitle ("");
				break;
				case 7:
				newPc.setTitle ("\\f3  악 마 피 박");
				break;
				}
			} else if (robot == 9) { //인공지능ON  사냥.무브.버프.변신
				int rnd = _random.nextInt(8);
				newPc.setHighLevel(52);
				newPc.setLevel(52);
				newPc.setExp(0);
				newPc.setClanid(298400760);
				newPc.setClanname("악마");
				switch(rnd){
				case 0:
				newPc.setLawful(32767);
				break;
				case 1:
				newPc.setLawful(2000);
				break;
				case 2:
				newPc.setLawful(3000);
				break;
				case 3:
				newPc.setLawful(-4000);
				break;
				case 4:
				newPc.setLawful(5000);
				break;
				case 5:
				newPc.setLawful(6000);
				break;
				case 6:
				newPc.setLawful(7000);
				break;
				case 7:
				newPc.setLawful(7500);
				break;	
				}
//				랜덤 호칭
				switch(rnd){
				case 0:
				newPc.setTitle ("\\f3  악 마 군 단");
				break;
				case 1:
				newPc.setTitle ("");
				break;
				case 2:
				newPc.setTitle ("\\f3  악 마 청 단");
				break;
				case 3:
				newPc.setTitle ("");
				break;
				case 4:
				newPc.setTitle ("\\f3  악 마 홍 단");
				break;
				case 5:
				newPc.setTitle ("\\f3  악 마 초 단");
				break;
				case 6:
				newPc.setTitle ("");
				break;
				case 7:
				newPc.setTitle ("\\f3  악 마 피 박");
				break;
				}
		} else if (robot == 10) { //인공지능ON  사냥.무브.버프.변신
			int rnd = _random.nextInt(8);
			newPc.setHighLevel(56);
			newPc.setLevel(56);
			newPc.setExp(0);
			newPc.setClanid(298400834);
			newPc.setClanname("금빛");
			switch(rnd){
			case 0:
			newPc.setLawful(32767);
			break;
			case 1:
			newPc.setLawful(2000);
			break;
			case 2:
			newPc.setLawful(3000);
			break;
			case 3:
			newPc.setLawful(-4000);
			break;
			case 4:
			newPc.setLawful(5000);
			break;
			case 5:
			newPc.setLawful(6000);
			break;
			case 6:
			newPc.setLawful(7000);
			break;
			case 7:
			newPc.setLawful(7500);
			break;	
			}
//			랜덤 호칭
			switch(rnd){
			case 0:
			newPc.setTitle ("\\fe  금 빛 최 강");
			break;
			case 1:
			newPc.setTitle ("");
			break;
			case 2:
			newPc.setTitle ("\\fe  금 빛 허 접");
			break;
			case 3:
			newPc.setTitle ("");
			break;
			case 4:
			newPc.setTitle ("\\fe  금 빛 필 드");
			break;
			case 5:
			newPc.setTitle ("\\fe  금 빛 무 필");
			break;
			case 6:
			newPc.setTitle ("");
			break;
			case 7:
			newPc.setTitle ("\\fe  금 빛 보 스");
			break;
			}
		} else if (robot == 11) { //인공지능ON  사냥.무브.버프.변신
			int rnd = _random.nextInt(8);
			newPc.setHighLevel(52);
			newPc.setLevel(52);
			newPc.setExp(0);
			newPc.setClanid(298400834);
			newPc.setClanname("금빛");
			switch(rnd){
			case 0:
			newPc.setTitle ("\\fe  금 빛 최 강");
			break;
			case 1:
			newPc.setTitle ("");
			break;
			case 2:
			newPc.setTitle ("\\fe  금 빛 허 접");
			break;
			case 3:
			newPc.setTitle ("");
			break;
			case 4:
			newPc.setTitle ("\\fe  금 빛 필 드");
			break;
			case 5:
			newPc.setTitle ("\\fe  금 빛 무 필");
			break;
			case 6:
			newPc.setTitle ("");
			break;
			case 7:
			newPc.setTitle ("\\fe  금 빛 보 스");
			break;
			}
//			랜덤 호칭
			switch(rnd){
			case 0:
			newPc.setTitle ("\\fe  금 빛 최 강");
			break;
			case 1:
			newPc.setTitle ("");
			break;
			case 2:
			newPc.setTitle ("\\fe  금 빛 허 접");
			break;
			case 3:
			newPc.setTitle ("");
			break;
			case 4:
			newPc.setTitle ("\\fe  금 빛 필 드");
			break;
			case 5:
			newPc.setTitle ("\\fe  금 빛 무 필");
			break;
			case 6:
			newPc.setTitle ("");
			break;
			case 7:
			newPc.setTitle ("\\fe  금 빛 보 스");
			break;
			}
		} else if (robot == 12) { //인공지능ON  사냥.무브.버프.변신
			int rnd = _random.nextInt(8);
			newPc.setHighLevel(56);
			newPc.setLevel(56);
			newPc.setExp(0);
			newPc.setClanid(299090949);
			newPc.setClanname("일편단심");
			switch(rnd){
			case 0:
			newPc.setLawful(32767);
			break;
			case 1:
			newPc.setLawful(2000);
			break;
			case 2:
			newPc.setLawful(3000);
			break;
			case 3:
			newPc.setLawful(-4000);
			break;
			case 4:
			newPc.setLawful(5000);
			break;
			case 5:
			newPc.setLawful(6000);
			break;
			case 6:
			newPc.setLawful(7000);
			break;
			case 7:
			newPc.setLawful(7500);
			break;	
			}
//			랜덤 호칭
			switch(rnd){
			case 0:
			newPc.setTitle ("개매너혈ㅋㅋ");
			break;
			case 1:
			newPc.setTitle ("\\fY 일 편 단 심");
			break;
			case 2:
			newPc.setTitle ("\\fY 일 편 쌍 편");
			break;
			case 3:
			newPc.setTitle ("우리혈호칭이뭐여");
			break;
			case 4:
			newPc.setTitle ("\\fY 일 편 타 편");
			break;
			case 5:
			newPc.setTitle ("\\fY 일 편 이 편");
			break;
			case 6:
			newPc.setTitle ("\\fY 일 편 삼 편");
			break;
			case 7:
			newPc.setTitle ("\\fY 1위는내꺼여");
			break;
			}
		} else if (robot == 13) { //인공지능ON  사냥.무브.버프.변신
			int rnd = _random.nextInt(8);
			newPc.setHighLevel(52);
			newPc.setLevel(52);
			newPc.setExp(0);
			newPc.setClanid(299090949);
			newPc.setClanname("필드");
			switch(rnd){
			case 0:
			newPc.setLawful(32767);
			break;
			case 1:
			newPc.setLawful(2000);
			break;
			case 2:
			newPc.setLawful(3000);
			break;
			case 3:
			newPc.setLawful(-4000);
			break;
			case 4:
			newPc.setLawful(5000);
			break;
			case 5:
			newPc.setLawful(6000);
			break;
			case 6:
			newPc.setLawful(7000);
			break;
			case 7:
			newPc.setLawful(7500);
			break;	
			}
//			랜덤 호칭
			switch(rnd){
			case 1:
			newPc.setTitle ("매너필드란없다");
			break;
			case 2:
			newPc.setTitle ("매너필드란있다");
			break;
			case 3:
			newPc.setTitle ("왜캐혈거지같냐");
			break;
			case 4:
			newPc.setTitle ("난때리지마쇼");
			break;
			case 5:
			newPc.setTitle ("중립할테야");
			break;
			case 6:
			newPc.setTitle ("판도라이름이쁘구만");
			break;
			case 7:
			newPc.setTitle ("판도라쓰~");
			break;
			}
		} else if (robot == 14) { //낚시
			newPc.setHighLevel(5);
			newPc.setLevel(5);
			newPc.setExp(0);
			newPc.setLawful(0);
			newPc.setClanid(0);
			newPc.setClanname("");
			newPc.setTitle ("\\fC " + Config.SERVER_NAME + " 서버");
			int typeCount = 0;
			for(L1PcInstance tempPc : L1World.getInstance().getAllPlayers()){
				if(tempPc.noPlayerCK && tempPc.getLevel() == 5){
					typeCount++;
				}
			}
			RobotFishing rf = null;
			try{
				rf = RobotThread.getRobotFish().get(typeCount);
			}catch(Exception e){
				continue;
			}
			if(rf == null)continue;
			newPc.setX(rf.x);
			newPc.setY(rf.y);
			newPc.setMap((short)rf.map);
			newPc.getMoveState().setHeading(rf.heading);
			int sex = _random.nextInt(1);
			int type = _random.nextInt(MALE_LIST.length);
			int klass = 0;
					
			switch (sex) {
			case 0:
				klass = MALE_LIST[type];
				break;
			case 1:
				klass = FEMALE_LIST[type];
				break;
			}
			
			newPc.noPlayerCK = true;
			newPc.setClassId(klass);
			newPc.getGfxId().setTempCharGfx(klass);
			newPc.getGfxId().setGfxId(klass);
			newPc.set_sex(sex);
			newPc.setType(type);
			newPc.setFishing(true);
			newPc._fishingX = rf.fishX;
			newPc._fishingY = rf.fishY;
			Broadcaster.broadcastPacket(newPc, new S_Fishing(newPc.getId(),ActionCodes.ACTION_Fishing, rf.fishX, rf.fishY));
			
			L1World.getInstance().storeObject(newPc);
			L1World.getInstance().addVisibleObject(newPc);
			
			newPc.setNetConnection(null);

			SearchCount++;
			continue;
			/*int rnd = _random.nextInt(8);
			newPc.setHighLevel(5);
			newPc.setLevel(5);
			newPc.setExp(0);
			newPc.setLawful(0);
			newPc.setClanid(0);
			newPc.setClanname("");
			switch(rnd){
			case 0:
				newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 1:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 2:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 3:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 4:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 5:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 6:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 7:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				}
			int typeCount = 0;
			for(L1PcInstance tempPc : L1World.getInstance().getAllPlayers()){
				if(tempPc.noPlayerCK && tempPc.getLevel() == 5){
					typeCount++;
				}
			}
			RobotFishing rf = null;
			try{
				rf = RobotThread.getRobotFish().get(typeCount);
			}catch(Exception e){
				continue;
			}
			if(rf == null)continue;
			newPc.setX(rf.x);
			newPc.setY(rf.y);
			newPc.setMap((short)rf.map);
		//	newPc.getMoveState().setHeading(rf.heading);
			int sex = _random.nextInt(1);
			int type = _random.nextInt(MALE_LIST.length);
			int klass = 0;
					
			switch (sex) {
			case 0:
				klass = MALE_LIST[type];
				break;
			case 1:
				klass = FEMALE_LIST[type];
				break;
			}
			
			newPc.noPlayerCK = true;
			newPc.setClassId(klass);
			newPc.getGfxId().setTempCharGfx(klass);
			newPc.getGfxId().setGfxId(klass);
			newPc.set_sex(sex);
			newPc.setType(type);
			newPc.setFishing(true);
			newPc._fishingX = rf.fishX;
			newPc._fishingY = rf.fishY;
			Broadcaster.broadcastPacket(newPc, new S_Fishing(newPc.getId(),ActionCodes.ACTION_Fishing, rf.fishX, rf.fishY));
			
			L1World.getInstance().storeObject(newPc);
			L1World.getInstance().addVisibleObject(newPc);
			
			newPc.setNetConnection(null);

			SearchCount++;
			continue;*/
		} else if (robot == 15) { //요리
			int rnd = _random.nextInt(8);
			newPc.setHighLevel(1);
			newPc.setLevel(1);
			newPc.setExp(0);
			newPc.setLawful(0);
			newPc.setClanid(0);
			newPc.setClanname("");
			switch(rnd){
			case 0:
				newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 1:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 2:
				newPc.setTitle ("돈아끼자");
				break;
				case 3:
				newPc.setTitle ("패키지매입중");
				break;
				case 4:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 5:
				newPc.setTitle ("형 요리한다");
				break;
				case 6:
				newPc.setTitle ("건들건들");
				break;
				case 7:
				newPc.setTitle ("오뎅팔");
				break;
				}
		} else if(robot == 16) { // 인공지능ON 제자리 숨계
				newPc.setHighLevel(7);//레벨구간
				newPc.setLevel(7);
				newPc.setExp(0);
				newPc.setLawful(0);
				newPc.setClanid(0);
				newPc.setClanname("");
//				랜덤 호칭
				int rnd = _random.nextInt(8);
				switch(rnd){
			case 0:
				newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 1:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 2:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 3:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 4:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 5:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 6:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				case 7:
					newPc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");
				break;
				}

		}
				newPc.addBaseMaxHp((short)2300);
				newPc.setCurrentHp(2300);
				newPc.setDead(false);
				newPc.addBaseMaxMp((short)500);
				newPc.setCurrentMp(500);
				newPc.getResistance().addMr(100);
				newPc.getAbility().setBaseStr(35);
				newPc.getAbility().setStr(35);
				newPc.getAbility().setBaseCon(35);
				newPc.getAbility().setCon(35);
				newPc.getAbility().setBaseDex(35);
				newPc.getAbility().setDex(35);
				newPc.getAbility().setBaseCha(35);
				newPc.getAbility().setCha(35);
				newPc.getAbility().setBaseInt(35);
				newPc.getAbility().setInt(35);
				newPc.getAbility().setBaseWis(35);
				newPc.getAbility().setWis(35);
								
				int sex = _random.nextInt(1);
				int type = _random.nextInt(MALE_LIST.length);
				int klass = 0;
								
				switch (sex) {
				case 0:
					klass = MALE_LIST[type];
					break;
				case 1:
					klass = FEMALE_LIST[type];
					break;
				}
				
				newPc.setClassId(klass);
				newPc.getGfxId().setTempCharGfx(klass);
				newPc.getGfxId().setGfxId(klass);
				newPc.set_sex(sex);
				newPc.setType(type);
				
				for(int i=0; i < 17; i++){
					x = loc[_random.nextInt(17)];
					y = loc[_random.nextInt(17)];
					newPc.setX(pc.getX() + x);
					newPc.setY(pc.getY() + y);
					newPc.setMap(pc.getMapId());
					if (map.isPassable(newPc.getX(), newPc.getY())) {
						break;
					}
				}	

				newPc.getMoveState().setHeading(random(0, 7));

				newPc.set_food(39);
				//newPc.setClanid(0);
				//newPc.setClanname("");
				newPc.setClanRank(0);
				newPc.setElfAttr(0);
				newPc.set_PKcount(0);
				newPc.setExpRes(0);
				newPc.setPartnerId(0);
				newPc.setAccessLevel((short)0);
				newPc.setGm(false);
				newPc.setMonitor(false);
				newPc.setHomeTownId(0);
				newPc.setContribution(0);
				newPc.setHellTime(0);
				newPc.setBanned(false);
				newPc.setKarma(0);
				newPc.setReturnStat(0);				
				newPc.setGmInvis(false);
				
				if (robot == 0) {
					newPc.noPlayerCK = true; // 텔레포트 안하는 로보트로 셋팅, 텔레포트 안하는 신규 로보트 생성(뉴트럴, 호칭 있슴)
				} else if (robot == 1 || robot == 2 || robot == 3|| robot == 4|| robot == 5|| robot == 6|| robot == 7
						|| robot == 8|| robot == 9|| robot == 10|| robot == 11|| robot == 12|| robot == 13|| robot == 14|| robot == 15|| robot == 16) {
					newPc.noPlayerCK = true; // 텔레포트 하는 로보트로 셋팅, 텔레포트 하는 신규 로보트 생성(라우풀, 호칭 없슴), 텔레포트 하는 신규 로보트 생성(카오, 호칭 없슴)					
				} else {
					newPc.noPlayerCK = true; // 텔레포트 안하는 로보트로 셋팅, 텔레포트 안하는 신규 로보트 생성(뉴트럴, 호칭 있슴)
				}
				
				if (newPc.isKnight()) {
					newPc.setCurrentWeapon(61);//집행
					newPc.getEquipSlot().set(ItemTable.getInstance().createItem(61));
				} else if (newPc.isCrown()) {
					newPc.setCurrentWeapon(294);//군검
					newPc.getEquipSlot().set(ItemTable.getInstance().createItem(294));
				} else if (newPc.isElf()) {
					newPc.setCurrentWeapon(189);//왕궁
					newPc.getEquipSlot().set(ItemTable.getInstance().createItem(189));
					L1Item temp = ItemTable.getInstance().getTemplate(40748);
					if (temp != null) {
							L1ItemInstance item = ItemTable.getInstance().createItem(40748);
							item.setItemOwner(newPc);
							item.setEnchantLevel(0);
							item.setCount(count);
							newPc.getInventory().storeItem(item);
					}
					newPc.getInventory().setArrow(40748);
				} else if (newPc.isWizard()) {
					newPc.setCurrentWeapon(134);//수결
					newPc.getEquipSlot().set(ItemTable.getInstance().createItem(134));
				} else if (newPc.isDarkelf()) {
					newPc.setCurrentWeapon(86);//붉이
					newPc.getEquipSlot().set(ItemTable.getInstance().createItem(86));
				} else if (newPc.isIllusionist()) {
					newPc.setCurrentWeapon(410004);//흑키
					newPc.getEquipSlot().set(ItemTable.getInstance().createItem(410004));
				} else if (newPc.isDragonknight()) {
					newPc.setCurrentWeapon(410001);//파체
					newPc.getEquipSlot().set(ItemTable.getInstance().createItem(410001));
				}	
					
				
				if (isteleport == 1) {
					int rnd1 = CommonUtil.random(20, 60);
					newPc.setTeleportTime(rnd1);
					
					int rnd2 = CommonUtil.random(5, 60);
					
					if (rnd1 == rnd2) {
						rnd2++;
					}
					newPc.setSkillTime(rnd2);
				}
				
				newPc.setActionStatus(0);				
				L1World.getInstance().storeObject(newPc);
				L1World.getInstance().addVisibleObject(newPc);
				
				newPc.setNetConnection(null);

				SearchCount++;
				newPc.RobotStartCK = true;
				int timernd = _random.nextInt(6) * 1000;
				newPc.RobotDeadDelay = System.currentTimeMillis() + timernd;
				RobotControler.addList(newPc);
			}
			pc.sendPackets(new S_SystemMessage(SearchCount + "명의 로봇 캐릭터가 배치 되었습니다."));
			pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
			
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("-------------------Robot Commands.------------------"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 0:뉴트럴 [제자리텔포사용]숨계용"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 1:라우풀 [인형.변신.스킬.무브.텔포사용]오픈마을"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 2:카오틱 [인형.변신.스킬.무브.텔포사용]오픈마을"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 3:뉴트럴 [인형.변신.무브.텔포사용]처음오픈마을"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 4:라우풀 [인형.스킬.변신사용]창고용"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 5:라우풀 [사냥.스킬.변신사용]사냥용"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 6:라우풀 [기르타스혈맹사냥용]사냥용"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 7:라우풀 [기르타스혈맹창고용]창고용"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 8:라우풀 [악마혈맹사냥용]사냥용"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 9:라우풀 [악마혈맹창고용]창고용"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 10:라우풀[금빛혈맹사냥용]사냥용"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 11:라우풀[금빛혈맹창고용]창고용"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 12:라우풀[필드혈맹사냥용]사냥용"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 13:라우풀[필드혈맹창고용]창고용"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 14:라우풀[낚시터로봇생성]]25명입력"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 15:라우풀[요리로봇생성]마을용"));
			pc.sendPackets(new S_SystemMessage(" 타입 - 16:라우풀[제자리숨계]숨계용"));
			pc.sendPackets(new S_SystemMessage(" 행동 - 0 :끔, 1:켬 [행동은무조건켬으로해주세요]"));
			pc.sendPackets(new S_SystemMessage((new StringBuilder()).append(".로봇 (타입) (수) (행동)").toString()));
			pc.sendPackets(new S_SystemMessage("-------------------Robot Commands.------------------"));
		}
	}
	
	private static boolean isAlphaNumeric(String s) {
		boolean flag = true;
		char ac[] = s.toCharArray();
		int i = 0;
		do {
			if (i >= ac.length) {
				break;
			}
			if (!Character.isLetterOrDigit(ac[i])) {
				flag = false;
				break;
			}
			i++;
		} while (true);
		return flag;
	}
	
	private static boolean isInvalidName(String name) {
		int numOfNameBytes = 0;
		try {
			numOfNameBytes = name.getBytes("EUC-KR").length;
		} catch (UnsupportedEncodingException e) {
			return false;
		}

		if (isAlphaNumeric(name)) {
			return false;
		}

		if (5 < (numOfNameBytes - name.length()) || 12 < numOfNameBytes) {
			return false;
		}

		if (BadNamesList.getInstance().isBadName(name)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 랜덤 함수
	 * @param lbound
	 * @param ubound
	 * @return
	 */
	static public int random(int lbound, int ubound) {
		return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
	}
}