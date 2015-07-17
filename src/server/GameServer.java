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
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.CheckLevelCheck;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.StatBugCheck;
import l1j.server.GameSystem.CrockSystem;
import l1j.server.GameSystem.NpcShopSystem;
import l1j.server.GameSystem.RobotThread;
import l1j.server.GameSystem.Boss.BossSpawnTimeController;
import l1j.server.GameSystem.Boss.L1BossCycle;
import l1j.server.GameSystem.Lastabard.LastabardController;
import l1j.server.server.Announcecycle;
import l1j.server.server.BattleZone;
import l1j.server.server.GMCommandsConfig;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.TimeController.AuctionTimeController;
import l1j.server.server.TimeController.BuffTimeController;
import l1j.server.server.TimeController.ChatTimeController;
import l1j.server.server.TimeController.DevilController;
import l1j.server.server.TimeController.FishingTimeController;
import l1j.server.server.TimeController.HellDevilController;
import l1j.server.server.TimeController.HouseTaxTimeController;
import l1j.server.server.TimeController.LightTimeController;
import l1j.server.server.TimeController.LottoTimeController;
import l1j.server.server.TimeController.NpcChatTimeController;
import l1j.server.server.TimeController.UbTimeController;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.TimeController.WitsTimeController;
import l1j.server.server.datatables.AccessoryEnchantList;
import l1j.server.server.datatables.ArmorEnchantList;
import l1j.server.server.datatables.BossSpawnTable;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ClanWarehouseList;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.DropItemTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.EvaSystemTable;
import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.GetBackRestartTable;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.LightSpawnTable;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.MobGroupTable;
import l1j.server.server.datatables.ModelSpawnTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.datatables.NpcCashShopSpawnTable;
import l1j.server.server.datatables.NpcChatTable;
import l1j.server.server.datatables.NpcShopTable;
import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.PetTypeTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.RaceTable;
import l1j.server.server.datatables.ReportTable;
import l1j.server.server.datatables.ResolventTable;
import l1j.server.server.datatables.RobotTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.SoldierTable;
import l1j.server.server.datatables.SpawnTable;
import l1j.server.server.datatables.SprTable;
import l1j.server.server.datatables.UBSpawnTable;
import l1j.server.server.datatables.WeaponAddDamage;
import l1j.server.server.datatables.WeaponEnchantList;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.ElementalStoneGenerator;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1BugBearRace;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1ClanMatching;
import l1j.server.server.model.L1Cube;
import l1j.server.server.model.L1NpcRegenerationTimer;
import l1j.server.server.model.L1Sys;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.GameTimeClock;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.npc.action.function.L1NpcActionHelper;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.utils.SystemUtil;
import server.controller.AutoUpdateController;
import server.controller.CharacterAutoSaveController;
import server.controller.CharacterQuitCheckController;
import server.controller.HalloweenAHSHRegenController;
import server.controller.ItemDeleteController;
import server.controller.ItemEndTimeCheckThread;
import server.controller.ItemTimerController;
import server.controller.MpDecreaseByScalesController;
import server.controller.SpeedHackController;
import server.controller.SummonMapController;
import server.controller.SummonTimeController;
import server.controller.Doll.DollDeleteController;
import server.controller.Doll.DollHPMPRegenController;
import server.controller.Doll.DollobserverController;
import server.controller.Npc.NpcAIController;
import server.controller.Npc.NpcChatController;
import server.controller.Npc.NpcDeleteController;
import server.controller.Npc.NpcHPController;
import server.controller.Npc.NpcMPController;
import server.controller.Npc.NpcRestController;
import server.controller.Robot.RobotAIThread;
import server.controller.Robot.RobotBuffControler;
import server.controller.Robot.RobotControler;
import server.controller.Robot.RobotRandomMoveControler;
import server.controller.pc.ExpMonitorController;
import server.controller.pc.HpMpRegenController;
import server.controller.pc.ItemEndTimeCheckController;
import server.controller.pc.PcInvisDelayController;
import server.controller.pc.PrimeumControler;
import server.manager.eva;
import server.system.autoshop.AutoShopManager;

public class GameServer {
private ServerSocket _serverSocket;
	private static Logger _log = Logger.getLogger(GameServer.class.getName());
private int _port;

	public void run() {
		System.out.println("Used Memory: " + SystemUtil.getUsedMemoryMB() + "MB");
		System.out.println("Lineage 3.80 Server starting now...");
		System.out.println("──────────────");
		while (true) {
			try {
				Socket socket = _serverSocket.accept();
				System.out.println("Host Address IP: " + socket.getInetAddress());
				String host = socket.getInetAddress().getHostAddress();
				if (IpTable.getInstance().isBannedIp(host)) 
				{
					_log.info("banned IP(" + host + ")");
				} 
				/*섭폭방지2*/
				else if (socket.getPort() == 0) {
					System.out.println("Port blocking client");
				    System.out.println("[Address]: "+socket.getInetAddress()+" :"+socket.getPort()+" ["+host+"]");
				}
				/*섭폭방지2*/
				else {
					LineageClient client = new LineageClient(socket);
				}
			} catch (IOException ioexception) {
			}
		}
	}

	private static GameServer _instance;
	private GeneralThreadPool _threadPool = GeneralThreadPool.getInstance();

	private boolean _Shutdown = false;

	private GameServer() {
	}

	public static GameServer getInstance() {
		if (_instance == null) {
			synchronized (GameServer.class) {
				if (_instance == null)
					_instance = new GameServer();
			}
		}
		return _instance;
	}

	public void initialize() throws Exception {
		showGameServerSetting();
		
		// 가급적으로 맨 위에 넣어주면 조흠..
		//Authentication.init();	// 인증서버 초기화 호출

		ObjectIdFactory.createInstance();
		L1WorldMap.createInstance(); // FIXME 부실하다

		initTime();

		CharacterTable.getInstance().loadAllCharName(); // FIXME 굳이 메모리에 띄워둘 필요가
		// 있나
		CharacterTable.clearOnlineStatus();

		AccessoryEnchantList.getInstance();   // 악세사리 인챈 정보 리스트
		ArmorEnchantList.getInstance();	      // 방어구 인챈 정보 리스트
		WeaponEnchantList.getInstance();      // 무기 인챈 정보 리스트
		
		
		                  
		  
		//공지
		L1Sys.getInstance();      
		L1Sys l1Sys = L1Sys.getInstance();                        
		GeneralThreadPool.getInstance().execute(l1Sys);        
		
		// TODO change following code to be more effective
		/** 매크로추가* */

		/** 매크로추가* */
		// UB타임 콘트롤러
		_threadPool.execute(UbTimeController.getInstance());

		// 전쟁 타임 콘트롤러
		_threadPool.execute(WarTimeController.getInstance());

		// 정령의 돌 타임 컨트롤러
		if (Config.ELEMENTAL_STONE_AMOUNT > 0) {
			_threadPool.execute(ElementalStoneGenerator.getInstance());
		}
		if (Config.Use_Show_Announcecycle == true) { //자동공지
            Announcecycle.getInstance();
		}
		
		// 무기 숨겨진 데미지 리스트
		WeaponAddDamage.getInstance();  
		NpcDeleteController.getInstance();
		NpcChatController.getInstance();
		NpcRestController.getInstance();
	    NpcCashShopSpawnTable.getInstance();
	    NpcCashShopSpawnTable.getInstance().Start();
				// 홈 타운
		//HomeTownController.getInstance();
		DevilController.getInstance().start(); // 악마왕영토
		HellDevilController.getInstance().start();//지옥사냥터
		//TimeEventController.getInstance().start();// 상자이벤트 시스템
		
		// 프리미엄타임 콘트롤러 
		  LottoTimeController lottoTimeController = LottoTimeController.getInstance(); 
		  GeneralThreadPool.getInstance().execute(lottoTimeController); 

		
		BuffTimeController buffTimeController = BuffTimeController.getInstance(); 
		GeneralThreadPool.getInstance().execute(buffTimeController); //적당한곳에 추가

		NpcShopTable.getInstance();
		NpcShopSystem.getInstance();
		ChatTimeController chatTimeController = ChatTimeController.getInstance();
		GeneralThreadPool.getInstance().execute(chatTimeController);
		// 아지트 경매 타임 콘트롤러
		_threadPool.execute(AuctionTimeController.getInstance());

		// 아지트 세금 타임 콘트롤러
		_threadPool.execute(HouseTaxTimeController.getInstance());

		// 낚시 타임 콘트롤러
		_threadPool.execute(FishingTimeController.getInstance());


		_threadPool.execute(NpcChatTimeController.getInstance());
		
		//미니공성 컨트롤러
		//MiniWarGame.getInstance().start();
		

		// 자동사냥 방지
		//AutoCheckTimer r = AutoCheckTimer.getInstance();
		//GeneralThreadPool.getInstance().execute(r);

		RobotAIThread.init();
		RobotTable.getInstance();

		NpcHPController.getInstance();
		NpcMPController.getInstance();
		NpcAIController.getInstance();

		NpcTable.getInstance();
		
		//베틀존 시스템
	    BattleZone battleZone = BattleZone.getInstance();
		GeneralThreadPool.getInstance().execute(battleZone);
		
		

		if (!NpcTable.getInstance().isInitialized()) {
			throw new Exception(
					"[GameServer] Could not initialize the npc table");
		}
		
        WitsTimeController.getInstance();
		SpawnTable.getInstance();
		MobGroupTable.getInstance();
		SkillsTable.getInstance();
		PolyTable.getInstance();
		ItemTable.getInstance();
		DropTable.getInstance();
		DropItemTable.getInstance();
		ShopTable.getInstance();
		NPCTalkDataTable.getInstance();
		L1World.getInstance();
		L1WorldTraps.getInstance();
		Dungeon.getInstance();
		NpcSpawnTable.getInstance();
		IpTable.getInstance();
		MapsTable.getInstance();
		UBSpawnTable.getInstance();

		/** 매크로추가* */
		CharacterTable.cleartownfix();// 타운세금 상태 리셋트
		PetTable.getInstance();
		ClanTable.getInstance();
		CastleTable.getInstance();
		L1CastleLocation.setCastleTaxRate(); // CastleTable 초기화 다음 아니면 안 된다
		GetBackRestartTable.getInstance();
		DoorSpawnTable.getInstance();
		GeneralThreadPool.getInstance();
		L1NpcRegenerationTimer.getInstance();
		NpcActionTable.load();
		GMCommandsConfig.load();
		Getback.loadGetBack();
		PetTypeTable.load();
		ReportTable.getInstance();  // 추가
		L1TreasureBox.load();
		SprTable.getInstance();
		RaceTable.getInstance();
		ResolventTable.getInstance();
		FurnitureSpawnTable.getInstance();
		NpcChatTable.getInstance();
		L1Cube.getInstance();
		SoldierTable.getInstance();
		L1BugBearRace.getInstance();
		L1ClanMatching.getInstance().loadClanMatching();
		// 보스 리젠 타임
		L1BossCycle.load();
		BossSpawnTimeController.start();
		if(Config.INIT_BOSS_SPAWN) BossSpawnTable.getInstance();
		ClanWarehouseList.getInstance();
		L1NpcActionHelper.getInstance(); // 베테르랑
		// 라스타바드 던전
		LastabardController.start();
		
		RobotThread.init();//로봇

		// 유령의집, 데스매치
	//	_threadPool.execute(DeathMatch.getInstance());
	//	_threadPool.execute(GhostHouse.getInstance());
	//	_threadPool.execute(PetRacing.getInstance());

		// 횃불
		LightSpawnTable.getInstance();
		LightTimeController.start();
		// 스텟검사
		StatBugCheck statBugCheck = StatBugCheck.getInstance();
		GeneralThreadPool.getInstance().execute(statBugCheck);
		// 레벨검사
		CheckLevelCheck checkLevelCheck = CheckLevelCheck.getInstance();
		GeneralThreadPool.getInstance().execute(checkLevelCheck);

		// 월드내에 모형 넣기(던전내 횟불 등등)
		ModelSpawnTable.getInstance().ModelInsertWorld();
		// 무한대전 공지
		//NoticeSystem.start();
		// 시간의 균열
		CrockSystem.getInstance();
		EvaSystemTable.getInstance();
		// 버경표 삭제
		RaceTicket();
		// 케릭터 자동저장 스케줄러 해당 시간에 맞게 전체 유저를 읽어서 저장시킨다.
		CharacterAutoSaveController chaSave = new CharacterAutoSaveController(
				Config.AUTOSAVE_INTERVAL * 1000);
		chaSave.start();

		CharacterQuitCheckController quick = new CharacterQuitCheckController(
				10000);
		quick.start();
		// 케릭터가 가진 인형의 Action을 전송한다.
		DollobserverController dollAction = new DollobserverController(15000);
		dollAction.start();

		HpMpRegenController regen = new HpMpRegenController(1000);
		regen.start();

		ItemEndTimeCheckController.getInstance().start();// 룬

		SpeedHackController.getInstance();

		PrimeumControler pcontrol = new PrimeumControler(60000);
		pcontrol.start();
		
		new RobotControler();//로봇딜레이
		new RobotBuffControler();
		new RobotRandomMoveControler();
		//new RobotAttackControler();


		ItemDeleteController idel = new ItemDeleteController(30000);
		idel.start();

		/** 20분 마다 서버로그창 저장 -> 지우기 * */
		//ManagerLogDelControler mldc = new ManagerLogDelControler(8400000);
		//mldc.start();

		DecoderManager.getInstance();
		ExpMonitorController.getInstance();
		AutoUpdateController.getInstance();
		ItemEndTimeCheckThread.getInstance();  // 추가 해줍니다.
		DollDeleteController.getInstance();
		SummonMapController.getInstance();
		SummonTimeController.getInstance();
		DollHPMPRegenController.getInstance();
		MpDecreaseByScalesController.getInstance();
		HalloweenAHSHRegenController.getInstance();
		PcInvisDelayController.getInstance();
		ItemTimerController.getInstance();

		// 가비지 컬렉터 실행 (Null) 객체의 해제
        System.gc();
        eva.refreshMemory();
       /* System.out.println("▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩");
		for (String line : getOSInfo())
			System.out.println("" + line);
		for (String line : getCPUInfo())
			System.out.println("" + line);
		MemoryInfo();
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());*/

		// this.start();
	}

	/*public void MemoryInfo() {
		for (String line : getMemoryInfo())
			System.out.println("" + line);
	}

	public static String[] getMemoryInfo() {
		double max = Runtime.getRuntime().maxMemory() / 1024; // maxMemory is
																// the upper
																// limit the jvm
																// can use
		double allocated = Runtime.getRuntime().totalMemory() / 1024; // totalMemory
																		// the
																		// size
																		// of
																		// the
																		// current
																		// allocation
																		// pool
		double nonAllocated = max - allocated; // non allocated memory till jvm
												// limit
		double cached = Runtime.getRuntime().freeMemory() / 1024; // freeMemory
																	// the
																	// unused
																	// memory in
																	// the
																	// allocation
																	// pool
		double used = allocated - cached; // really used memory
		double useable = max - used; // allocated, but non-used and
										// non-allocated memory
		DecimalFormat df = new DecimalFormat(" (0.0000'%')");
		DecimalFormat df2 = new DecimalFormat(" # 'KB'");
		return new String[] { //
				"메모리 정보 - " + getRealTime().toString() + ":", // 
				"허용된 메모리:" + df2.format(max), //
				"할당된 메모리:" + df2.format(allocated)
						+ df.format(allocated / max * 100), //
				"할당되지 않은 메모리:" + df2.format(nonAllocated)
						+ df.format(nonAllocated / max * 100), //
				"할당된 메모리:" + df2.format(allocated), //
				"사용된 메모리:" + df2.format(used) + df.format(used / max * 100), //
				"사용되지 않은 메모리:" + df2.format(cached)
						+ df.format(cached / max * 100), //
				"사용할 수 있는 메모리:" + df2.format(useable)
						+ df.format(useable / max * 100), //
				"▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩" //
		};
	}

	public static String[] getCPUInfo() {
		return new String[] { //
		"프로세서: " + Runtime.getRuntime().availableProcessors(), //
				"프로세서 식별자: " + System.getenv("PROCESSOR_IDENTIFIER") //
		};
	}

	public static String[] getOSInfo() {
		return new String[] { //

				"운영체제: " + System.getProperty("os.name") + " 빌드: "
						+ System.getProperty("os.version"), //
				"아키텍처: " + System.getProperty("os.arch") //
		};
	}*/

	public static String getRealTime() {
		SimpleDateFormat String = new SimpleDateFormat("H:mm:ss");
		return String.format(new Date());
	}

	private void initTime() {
		GameTimeClock.init(); // 게임 시간 시계
		RealTimeClock.init(); // 현재 시간 시계
	}

	private void showGameServerSetting() {
		double rateXp = Config.RATE_XP;
		double rateLawful = Config.RATE_LAWFUL;
		double rateKarma = Config.RATE_KARMA;
		double rateDropItems = Config.RATE_DROP_ITEMS;
		double rateDropAdena = Config.RATE_DROP_ADENA;

		System.out.println("XP Rate: x" + rateXp + " / Law Rate: x" + rateLawful
				+ " / Drop Rate: x" + rateDropAdena);
		System.out.println("Karma Rate: x" + rateKarma + " / ItemDrop Rate: x" + rateDropItems);
		System.out.println("Global Chat Level: " + Config.GLOBAL_CHAT_LEVEL);
		System.out.println("Max Players: " + Config.MAX_ONLINE_USERS + "인");
		System.out.print("PvP mode: ");
		if (Config.ALT_NONPVP)
			System.out.println("On");
		else
			System.out.println("Off");
	}

	/**
	 * 온라인중의 플레이어 모두에 대해서 kick, 캐릭터 정보의 보존을 한다.
	 */
	public void disconnectAllCharacters() {
		Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();
		// 모든 캐릭터 끊기
		for (L1PcInstance pc : players) {
			if(pc == null) continue;
			if (!AutoShopManager.getInstance().isAutoShop(pc.getId())) {
				try {
					pc.save();
					pc.saveInventory();
					pc.getNetConnection().setActiveChar(null);
					pc.getNetConnection().kick();
					pc.logout();
				} catch (Exception e) {
					System.out.println("disconnectAllCharacters error");
				}

			} else {
				try {
					pc.save();
					pc.saveInventory();
					pc.logout();
				} catch (Exception e) {
					System.out.println("disconnectAllCharacters error");
				}
			}
		}
	}

	public int saveAllCharInfo() {
		// exception 발생하면 -1 리턴, 아니면 저장한 인원 수 리턴
		int cnt = 0;
		try {
			Collection<L1PcInstance> pList = null;
			pList = L1World.getInstance().getAllPlayers();
			for (L1PcInstance pc : pList) {
				try {
					if (pc == null) {
						continue;
					}
					cnt++;
					pc.save();
					pc.saveInventory();
				} catch (Exception e) {
					cnt -= 1;
					System.out.println("saveAllCharInfo Error : " + e);
				}
			}
		} catch (Exception e) {
			return -1;
		}

		return cnt;
	}

	/**
	 * 온라인중의 플레이어에 대해서 kick , 캐릭터 정보의 보존을 한다.
	 */
	
	public void disconnectChar(String name) {
		L1PcInstance pc = L1World.getInstance().getPlayer(name);
		L1PcInstance Player = pc;
		synchronized (pc) {
			pc.getNetConnection().kick();
			Player.logout();
			pc.getNetConnection().quitGame(Player);
		}
	}
	
	
	
	
	
	
	

	private class ServerShutdownThread extends Thread {
		private final int _secondsCount;

		public ServerShutdownThread(int secondsCount) {
			_secondsCount = secondsCount;
		}

		@Override
		public void run() {
			L1World world = L1World.getInstance();
			try {
				int secondsCount = _secondsCount;
				System.out.println("The server will shutdown in few seconds.");
				System.out.println("Please log out in a safe place.");
				world.broadcastServerMessage("The server will shutdown in few seconds.");
				world.broadcastServerMessage("Please log out in a safe place.");
				while (0 < secondsCount) {
					if (secondsCount <= 20) {
						System.out.println("In " + secondsCount
								+ "gameserver will shutdown. Please find safe spot and logout.");
						world.broadcastServerMessage("In " + secondsCount
								+ "gameserver will shutdown. Please find safe spot and logout.");
					} else {
						if (secondsCount % 60 == 0) {
							System.out.println("In " + secondsCount / 60
									+ "gameserver will shutdown.");
							world.broadcastServerMessage("In " + secondsCount
									/ 60 + "gameserver will shutdown.");
						}
					}
					Thread.sleep(1000);
					secondsCount--;
				}
				shutdown();
			} catch (InterruptedException e) {
				System.out.println("Gameserver shutdown has been cancalled.");
				world.broadcastServerMessage("Gameserver shutdown has been cancalled. Please resume your normal activites.");
				return;
			}
		}
	}

	private ServerShutdownThread _shutdownThread = null;
	
	public int startTime; //추가 이건 맞는지 모르겠습니다

	public synchronized void shutdownWithCountdown(int secondsCount) {
		if (_shutdownThread != null) {
			RobotAIThread.close();
			// 이미 슛다운 요구를 하고 있다
			// TODO 에러 통지가 필요할지도 모른다
			return;
		}
		_shutdownThread = new ServerShutdownThread(secondsCount);
		_threadPool.execute(_shutdownThread);
	}

	public void shutdown() {
		disconnectAllCharacters();
		System.exit(0);
		eva.savelog();
		CodeLogger.getInstance().alllog("--------------------");
		CodeLogger.getInstance().alllog("... Server Close ...");
		CodeLogger.getInstance().alllog("--------------------");
	}

	public synchronized void abortShutdown() {
		if (_shutdownThread == null) {
			// 슛다운 요구를 하지 않았다
			// TODO 에러 통지가 필요할지도 모른다
			return;
		}

		_shutdownThread.interrupt();
		_shutdownThread = null;
	}

	public void Halloween() {
		Connection con = null;
		PreparedStatement pstm = null;
		PreparedStatement pstm1 = null;
		PreparedStatement pstm2 = null;
		PreparedStatement pstm3 = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM character_items WHERE item_id IN (20380, 21060, 256) AND enchantlvl < 8");
			pstm1 = con
					.prepareStatement("DELETE FROM character_elf_warehouse WHERE item_id IN (20380, 21060, 256) AND enchantlvl < 8");
			pstm2 = con
					.prepareStatement("DELETE FROM clan_warehouse WHERE item_id IN (20380, 21060, 256) AND enchantlvl < 8");
			pstm3 = con
					.prepareStatement("DELETE FROM character_warehouse WHERE item_id IN (20380, 21060, 256) AND enchantlvl < 8");
			pstm3.execute();
			pstm2.execute();
			pstm1.execute();
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "GameServer[:Halloween:]Error", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(pstm3);
			SQLUtil.close(con);
		}
	}

	public void RaceTicket() {
		Connection con = null;
		PreparedStatement pstm = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM character_items WHERE item_id >= 8000000");
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "GameServer[:RaceTicket:]Error", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
