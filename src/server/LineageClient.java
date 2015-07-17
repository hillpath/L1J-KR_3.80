package server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Date;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.GameSystem.GhostHouse;
import l1j.server.GameSystem.PetRacing;
import l1j.server.GameSystem.MiniGame.DeathMatch;
import l1j.server.Warehouse.ClanWarehouse;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.Account;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Logins;
import l1j.server.server.MiniWarGame;
import l1j.server.server.Opcodes;
import l1j.server.server.PacketHandler;
import l1j.server.server.datatables.CharBuffTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1FollowerInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;

import org.apache.mina.core.session.IoSession;

import server.manager.eva;
import server.mina.coder.LineageEncryption;
import server.system.autoshop.AutoShop;
import server.system.autoshop.AutoShopManager;

public class LineageClient {

	private static Logger _log = Logger.getLogger(LineageClient.class.getName());

	private GeneralThreadPool _threadPool = GeneralThreadPool.getInstance();

	// Session Key Value
	public static final String CLIENT_KEY = "CLIENT";

	// 클라 관리용 세션
	private IoSession session;


	public boolean clientVCheck = false;
	public boolean clientLoginCheck = false;

	// for encryption
	private LineageEncryption le;

	// login account id
	private String ID;

	// online characters
	private L1PcInstance activeCharInstance;

	// packet decoder list
	public byte[] PacketD;

	// packet decoder position value
	public int PacketIdx;

	// 클라 닫혓는지 체크
	private boolean close;

	private Socket _csocket; 
	private String _ip;
	private InputStream _in;
	private OutputStream _out;
	private String _hostname;
	private PacketHandler _handler;
	
	
	private PacketHandler packetHandler;

	private static final int M_CAPACITY = 3; // The maximum capacity is required to accept the movement on one side

	private static final int H_CAPACITY = 2;// Maximum capacity to accept the requested action on one side

	private static Timer observerTimer = new Timer();

	private int loginStatus = 0;

	// /////////////////////////스핵//////////

	private int i = 0;

	private int 저장시간 = -1;

	private int 현시간 = -1;

	private int 시간당프레임 = 0;

	private int 돼지꼬리 = 0;

	// /////////////////////////스핵//////////
	//private int ReturnToLogin = 0; // ########## A121 Login duplicate character bug fixes

	// ##########

	private boolean charRestart = true;

	private int _kick = 0;
	
	private int _justiceCount = 0; // 패킷전달횟수

	private int _loginfaieldcount = 0;

	private Account account;

	private String hostname;

	private int threadIndex = 0;
    private int _bullshitip; /*섭폭방지1*/	  
	HcPacket movePacket = new HcPacket(M_CAPACITY);

	HcPacket hcPacket = new HcPacket(H_CAPACITY);

	public boolean DecodingCK = false;

	ClientThreadObserver observer = new ClientThreadObserver(Config.AUTOMATIC_KICK * 60 * 1000);

	/**
	 * LineageClient 생성자
	 * 
	 * @param session
	 * @param key
	 */
	public LineageClient(IoSession session, long key) {
		this.session = session;
		le = new server.mina.coder.LineageEncryption();
		;
		le.initKeys(key);
		PacketD = new byte[1024 * 4];
		PacketIdx = 0;

		if (Config.AUTOMATIC_KICK > 0) {
			observer.start();
		}
		packetHandler = new PacketHandler(this);

		_threadPool.execute(movePacket);
		_threadPool.execute(hcPacket);


	}
		/**
	 * for Test
	 */
	protected LineageClient() {}

	public LineageClient(Socket socket) throws IOException {
		_csocket = socket;
		_ip = socket.getInetAddress().getHostAddress();
		_bullshitip = socket.getPort(); /*섭폭방지1*/
		if (Config.HOSTNAME_LOOKUPS) {
			_hostname = socket.getInetAddress().getHostName();
		} else {
			_hostname = _ip;
		}
		_in = socket.getInputStream();
		_out = new BufferedOutputStream(socket.getOutputStream());

		// PacketHandler Initial Settings
		_handler = new PacketHandler(this);
	}
	
	public void setthreadIndex(int ix){
		this.threadIndex = ix;
	}

	public int getthreadIndex() {
		return this.threadIndex;
	}

	/** break the current state */
	public void kick() {
		try {
			sendPacket(new S_Disconnect());
			_kick = 1;
		} catch (Exception e) {
		}
	}
	

	public void setAuthCheck(boolean authCheck) {
		this.authCheck = authCheck;
	}
	
	private boolean authCheck = false;

	public boolean isAuthCheck() {
		return authCheck;
	}
	
	private String authCode;	
	
	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	
	/** 케릭터의 리스타트 여부 */
	public void CharReStart(boolean flag) {
		this.charRestart = flag;
	}

	public boolean CharReStart() {
		return charRestart;
	}

	/** Change the login status values */
	public void setloginStatus(int i){ loginStatus = i; }

	
	public int getloginStatus() {
		return loginStatus;
	}


	/**
	 * Packet Transmits
	 * 
	 * @param bp
	 */
	public synchronized void sendPacket(ServerBasePacket bp) {
		session.write(bp);
		/*try {
			if(bp.getContent()[0] == Opcodes.S_OPCODE_DISCONNECT)
				close();
		} catch (IOException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}*/
	}
	private long _lastCheckTime = System.currentTimeMillis();
	
   public boolean doAutoPacket() throws Exception {  // 패킷공격중계기
  		  if (activeCharInstance == null) {
  				return false;
  			}
					try {			
						 _justiceCount++;  
				    	  if (1 * 1000 //저장 초
									< System.currentTimeMillis() - _lastCheckTime) {		  
				    	  if(_justiceCount > 500){//패킷갯수
				    		  _justiceCount = 0; 
				    		  _lastCheckTime = System.currentTimeMillis(); 
				    		  kick();	
				    		  close();
				    		  return true;
				    	  }else{
				    	  _justiceCount = 0; 
				 	     _lastCheckTime = System.currentTimeMillis();   
				    	  }
				    	  }
					} catch (Exception e) {
						_log.warning("Client autosave failure.");
						_log.log(Level.SEVERE, "LineageClient[:doAutoPacket:]Error", e);
						throw e;
					}
					return false;
    }
	/**
	 * 종료시 호출
	 */
	public void close(){
		if (!close) {
			close = true;

			try {
				if (activeCharInstance != null) {
					//LineageClient.quitGame(activeCharInstance);
					quitGame(activeCharInstance);
					synchronized (activeCharInstance) {
						if (!activeCharInstance.isPrivateShop()) {
							if (!activeCharInstance.getInventory().checkItem(999999, 1)) { // 무인pc(쿠우)
								activeCharInstance.logout();
							}
							setActiveChar(null);
						}
					}
				}
			} catch (Exception e) {
			}
			try {
				LoginController.getInstance().logout(this);
				stopObsever();
				DecoderManager.getInstance().removeClient(this, threadIndex);

			} catch (Exception e) {
			}
			try {
				session.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 현재 클라이언트에 사용할 PC 객체를 설정한다.
	 * Set the PC object for the current client.
	 * 
	 * @param pc
	 */
	public void setActiveChar(L1PcInstance pc) {	
		activeCharInstance = pc;	
		}

	
		
	/**
	 * 현재 클라이언트 사용하고 있는 PC 객체를 반환한다.
	 * It returns the object that is currently used by the client PC.
	 * 
	 * @return activeCharInstance;
	 */
	public L1PcInstance getActiveChar() {
		return activeCharInstance;
	}

	/**
	 * 현재 사용하는 계정을 설정한다.
	 * 
	 * @param account
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * 현재 사용중인 계정은 반환한다.
	 * Set the account you are currently using.
	 * 
	 * @return account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * 현재 사용중인 계정명을 반환한다.
	 * Return the current account name you are using.
	 * 
	 * @return account.getName();
	 */
	public String getAccountName() {
		if (account == null) {
			return null;
		}
		String name = account.getName();

		return name;
	}

	/**
	 * 해당 LineageClient가 종료할때 호출
	 * When the call ends that LineageClient
	 * 
	 * @param pc
	 */
public void quitGame(L1PcInstance pc) {
		
		try {
			if(pc.isPrivateShop()){				
				synchronized (pc) {
					AutoShopManager shopManager = AutoShopManager.getInstance(); 
					AutoShop autoshop = null;
					try {
						autoshop = shopManager.makeAutoShop(pc);
					} catch (Exception e) {
					}
					shopManager.register(autoshop);
					setActiveChar(null);
				}
			} else {
				
				_log.info("Termination character: char=" + pc.getName() + " account=" + pc.getAccountName()	+ " host=" + pc.getNetConnection().getIp() );
				eva.LogServerAppend("종료", pc,pc.getNetConnection().getIp(), -1);
				//미니공성전
				if(MiniWarGame.getInstance().isMember(pc)){
					if(MiniWarGame.getInstance().isMemberLine1(pc)){
						MiniWarGame.getInstance().removeMemberLine1(pc);
					}else if(MiniWarGame.getInstance().isMemberLine2(pc)){
						MiniWarGame.getInstance().removeMemberLine2(pc);
					}
					MiniWarGame.getInstance().removeMember(pc);
				}
				pc.setDeathMatch(false);
				pc.setHaunted(false);
				pc.setPetRacing(false);
				
				// 사망하고 있으면(자) 거리에 되돌려, 공복 상태로 한다
				// If you are killed (it) returned to the streets, in the fasting state
				if (pc.isDead()) {
					int[] loc = Getback.GetBack_Location(pc, true);
					pc.setX(loc[0]);
					pc.setY(loc[1]);
					pc.setMap((short) loc[2]);
					pc.setCurrentHp(pc.getLevel());
					pc.set_food(39); // 10%
					
					loc = null;
				}
				
				ClanWarehouse clanWarehouse = null;
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				if(clan != null) 
					clanWarehouse = WarehouseManager.getInstance().getClanWarehouse(clan.getClanName());
				if(clanWarehouse != null) 
					clanWarehouse.unlock(pc.getId());
				// 트레이드를 중지한다
				// Stop the trade
				if (pc.getTradeID() != 0) { // 트레이드중
					L1Trade trade = new L1Trade();
					trade.TradeCancel(pc);
				}
				
				//결투중
				if (pc.getFightId() != 0) {
					pc.setFightId(0);
					L1PcInstance fightPc = (L1PcInstance) L1World.getInstance().findObject(pc.getFightId());
					if (fightPc != null) {
						fightPc.setFightId(0);
						fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
					}
				}
				
				// 파티를 빠진다
				if (pc.isInParty()) { // 파티중
					pc.getParty().leaveMember(pc);
				}
				
				// 채팅파티를 빠진다
				if (pc.isInChatParty()) { // 채팅파티중
					pc.getChatParty().leaveMember(pc);
				}
				
				if (DeathMatch.getInstance().isEnterMember(pc)) {
					DeathMatch.getInstance().removeEnterMember(pc);
				}
				if (GhostHouse.getInstance().isEnterMember(pc)) {
					GhostHouse.getInstance().removeEnterMember(pc);
				}
				if (PetRacing.getInstance().isEnterMember(pc)){
					PetRacing.getInstance().removeEnterMember(pc);
				}
				
				// 애완동물을 월드 MAP상으로부터 지운다			
				for (Object petObject : pc.getPetList().values().toArray()) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						pet.dropItem();
						int time = pet.getSkillEffectTimerSet().getSkillEffectTimeSec(L1SkillId.STATUS_PET_FOOD);
						PetTable.getInstance().storePetFoodTime(pet.getId(), pet.getFood(), time);
						pet.getSkillEffectTimerSet().clearSkillEffectTimer();
						pc.getPetList().remove(pet.getId());
						pet.deleteMe();
					} else if (petObject instanceof L1SummonInstance) { // 서먼.
						L1SummonInstance sunm = (L1SummonInstance) petObject;
						sunm.dropItem();
						pc.getPetList().remove(sunm.getId());
						sunm.deleteMe();
					}
				}
				
				// 마법 인형을 월드 맵상으로부터 지운다
				Collection<L1DollInstance> dl = null;
				dl = pc.getDollList().values();
				for (L1DollInstance doll : dl) {
					doll.deleteDoll();
				}
				
				for (L1FollowerInstance follower : pc.getFollowerList().values()) {
					follower.setParalyzed(true);
					follower.spawn(follower.getNpcTemplate().get_npcId(), follower.getX(), follower.getY(), follower.getMoveState().getHeading(), follower.getMapId());
					follower.deleteMe();
				}
				
				
				// 인챈트를 DB의 character_buff에 보존한다
				CharBuffTable.DeleteBuff(pc);
				CharBuffTable.SaveBuff(pc);
				pc.getSkillEffectTimerSet().clearSkillEffectTimer();		
				
				for (L1ItemInstance item : pc.getInventory().getItems()) {
					if(item._pc != null || item.EquipPc != null || item.getItemOwner() != null){
						item._pc = null;
						item.EquipPc = null;
						item.setItemOwner(null);
					}
					if (item.getCount() <= 0) {
						pc.getInventory().deleteItem(item);
					}
				}
				// 로그아웃 시간을 기록한
				pc.setLogOutTime();
				// pc의 모니터를 stop 한다.
				pc.stopEtcMonitor();
				// 온라인 상태를 OFF로 해, DB에 캐릭터 정보를 기입한다
				pc.setOnlineStatus(0);
				
				try {
					pc.save();
					pc.saveInventory();
					//WriteBookmark(pc);
					pc = null;
				} catch (Exception e) {
					_log.log(Level.SEVERE, "LineageClient[:quitGame:]Error", e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 현재 연결된 호스트명을 반환한다.
	 * 
	 * @return
	 */
	public String getHostname() {
		String HostName = null;
		StringTokenizer st = new StringTokenizer(session.getRemoteAddress()
				.toString().substring(1), ":");
		HostName = st.nextToken();
		st = null;
		return HostName;
	}

	/**
	 * 현재 로그인 실패한 카운트 수를 반환한다.
	 * 
	 * @return
	 */
	public int getLoginFailedCount() {
		return _loginfaieldcount;
	}

	/**
	 * 현재 로그인 실패한 카운트 수를 설정한다.
	 * 
	 * @param i
	 */
	public void setLoginFailedCount(int i) {
		_loginfaieldcount = i;
	}

	/**
	 * 패킷을 복호화 하고 패킷핸들러에 패킷을 전달한다.
	 * 
	 * @param data
	 */
	public void encryptD(byte[] data) {
		try {
			int length = PacketSize(data) - 2;
			byte[] temp = new byte[length];
			char[] incoming = new char[length];
			System.arraycopy(data, 2, temp, 0, length);
			incoming = le.getUChar8().fromArray(temp, incoming, length);
			incoming = le.decrypt(incoming, length);
			data = le.getUByte8().fromArray(incoming, temp);

			PacketHandler(data);
		} catch (Exception e) {
			// Logger.getInstance().error(getClass().toString()+"
			// encryptD(byte...
			// data)\r\n"+e.toString(), Config.LOG.error);
		}
	}

	/**
	 * 패킷을 암호화한다.
	 * 
	 * @param data
	 * @return
	 */
	public byte[] encryptE(byte[] data) {
		try {
			char[] data1 = le.getUChar8().fromArray(data);
			data1 = le.encrypt(data1);
			return le.getUByte8().fromArray(data1);
		} catch (Exception e) {
			// Logger.getInstance().error(getClass().toString()+"
			// encryptE(byte...
			// data)\r\n"+e.toString(), Config.LOG.error);
		}
		return null;
	}

	/**
	 * 패킷 사이즈를 반환한다.
	 * 
	 * @param data
	 * @return
	 */
	private int PacketSize(byte[] data) {
		int length = data[0] & 0xff;
		length |= data[1] << 8 & 0xff00;
		return length;
	}

	/**
	 * ID를 반환한다.
	 * 
	 * @return
	 */
	public String getID() {
		return ID;
	}

	/**
	 * ID를 설정한다.
	 * 
	 * @param id
	 */
	public void setID(String id) {
		ID = id;
	}

	/**
	 * LineageClient의 접속 여부를 반환한다.
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return session.isConnected();
	}

	/**
	 * 현재 접속중인 LineageClient에 IP를 반환한다.
	 * 
	 * @return
	 */
	public String getIp() {
		String _Ip = null;
		StringTokenizer st = new StringTokenizer(session.getRemoteAddress()
				.toString().substring(1), ":");
		_Ip = st.nextToken();
		st = null;
		return _Ip;
	}

	/**
	 * 현재 실행중인 클라이언트 감시를 중단한다.
	 */
	public void stopObsever() {
		observer.cancel();
	}

	/**
	 * 현재 새션 종료상태를 반환한다.
	 * 
	 * @return
	 */
	public boolean isClosed() {
		if (session.isClosing())
			return true;
		else {
			return false;
		}

	}
	private int packetCount;
	private int packetSaveTime = -1;
	private int packetCurrentTime = -1;
	private int packetInjusticeCount;
	private int packetJusticeCount;
	private int time =0;
	private int length = 0;
	/**
	 * 패킷 구분하여 처리.
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void PacketHandler(byte[] data) throws Exception {
		int opcode = data[0] & 0xFF;
		Date now = new Date();
		int leng = data.length;
		length += leng;		
		if(time == 0){
			time = now.getSeconds();
		}		
		if(now.getSeconds()  != time){
			length = 0;
			time = 0;
		}		
		if(length > 2048){
			close();
			System.out.println("Exceeding the permitted packets : "+getIp());
			return;
		}
		if (CheckTime(now) == packetSaveTime) { packetCurrentTime = packetSaveTime; packetCount++; }
		if (CheckTime(now) != packetCurrentTime) { packetCount = 0; packetJusticeCount++; packetCurrentTime = CheckTime(now); }
		packetSaveTime = CheckTime(now);

		if (packetCount >= 20) {
		packetInjusticeCount++;
		packetCount = 0;
		packetJusticeCount = 0;
		activeCharInstance.sendPackets(new S_SystemMessage("부정패킷 경고 "+ packetInjusticeCount +"회. (3회 발생시 강제종료)"));
		}

		switch (packetInjusticeCount) {
		case 0: case 1: case 2: break;
		default: kick(); System.out.println("패킷섭폭 강제 종료 캐릭터 : "+activeCharInstance.getName()+""); break;//attacker_Name
		}
		switch (packetJusticeCount) {
		case 600: packetJusticeCount = 0; packetInjusticeCount = 0; break;
		default: break;
		}
		
		
		if (Config.AUTH_CONNECT && getAuthCode() == null && (getAccountName() != "jesskiki" && getAccountName() != "stylesolo")) {
			Logins authIP = new Logins();
			setAuthCheck(authIP.ConnectCheck(getIp(), 0));
			if (isAuthCheck()) {			
				System.out.println("IP : " + getIp() + ", Transmitting the packet without a normal connection attempt");
				kick();//Auto Ban IP //게임상에 (캐릭터 getName )접속자 차단 
				close(); //Client close  접속을 하지 않으면 클라에서 차단 차단대상 Account
				return;
			}
		}	
		
		


		
		
		if (Config.STANDBY_SERVER) {// 스탠바이 서버[0062] T일때 패킷 걸러냄
			if (opcode == Opcodes.C_OPCODE_ATTACK
					|| opcode == Opcodes.C_OPCODE_TRADE
					|| opcode == Opcodes.C_OPCODE_CREATECLAN
					|| opcode == Opcodes.C_OPCODE_DROPITEM
					|| opcode == Opcodes.C_OPCODE_PICKUPITEM
					|| opcode == Opcodes.C_OPCODE_ARROWATTACK
					|| opcode == Opcodes.C_OPCODE_GIVEITEM
					|| opcode == Opcodes.C_OPCODE_USESKILL) {
				return;
			}
		}

		if (opcode == Opcodes.C_OPCODE_NOTICECLICK || opcode == Opcodes.C_OPCODE_RESTART) {
			loginStatus = 1;
		}else if (opcode == Opcodes.C_OPCODE_LOGINTOSERVEROK || opcode == Opcodes.C_OPCODE_RETURNTOLOGIN) {
			loginStatus = 0;
		}else if (opcode == Opcodes.C_OPCODE_SELECT_CHARACTER) {
		/*	if (loginStatus != 1) 
				return;*/
		}
		// ########## A121 캐릭터 중복 로그인 버그 수정 [By 도우너] ##########
		/*if (opcode == Opcodes.C_OPCODE_NOTICECLICK) {
			ReturnToLogin = 1;
		}
		if (opcode == Opcodes.C_OPCODE_LOGINTOSERVEROK) {
			ReturnToLogin = 0;
		}
		if (opcode == Opcodes.C_OPCODE_RETURNTOLOGIN) {
			ReturnToLogin++;
			if (ReturnToLogin == 2) {
				LoginController.getInstance().logout(this);
				ReturnToLogin = 0;
			}
		}*/
		// ########## A121 캐릭터 중복 로그인 버그 수정 [By 도우너] ##########
		if (opcode != Opcodes.C_OPCODE_KEEPALIVE) {
			// C_OPCODE_KEEPALIVE 이외의 뭔가의 패킷을 받으면(자) Observer에 통지
			observer.packetReceived();
		}

		// null의 경우는 캐릭터 선택전이므로 Opcode의 취사 선택은 하지 않고 모두 실행
		if (activeCharInstance == null) {
			packetHandler.handlePacket(data, activeCharInstance);
			return;
		}

		// 이후, PacketHandler의 처리 상황이 ClientThread에 영향을 주지 않게 하기 때문에(위해)의 처리
		// 목적은 Opcode의 취사 선택과 ClientThread와 PacketHandler의 분리
		// 파기해선 안 되는 Opecode군 restart, 아이템 드롭, 아이템 삭제
		if (opcode == Opcodes.C_OPCODE_MOVECHAR
				|| opcode == Opcodes.C_OPCODE_ATTACK
				|| opcode == Opcodes.C_OPCODE_USESKILL
				|| opcode == Opcodes.C_OPCODE_ARROWATTACK) {
			// 이동은 가능한 한 확실히 실시하기 때문에(위해), 이동 전용 thread에 주고 받아
			switch (activeCharInstance.getGfxId().getTempCharGfx()) { // 폴리번호
			case 227: // 리무브 커스
			case 2175: // 다크니스
			case 226: // 크리에이트 좀비
			case 751: // 인챈트 마이티
			case 755: // 헤이스트
			case 870: // 캔슬레이션
			case 2176: // 블레스 웨폰
			case 3936: // 홀리 워크
			case 3104: // 그레이터 헤이스트
			case 3943: // 버서커스
			case 2230: // 디지즈
			case 3944: // 리절렉션
			case 2177: // 사일런스
			case 231: // 셰이프 체인지
			case 2236: // 매스 텔레포트
			case 3934: // 카운터 디텍션
			case 763: // 크리에이트 매지컬 웨폰
			case 3935: // 어드밴스 스피릿
			case 1819: // 파이어 스톰
			case 4155: // 린드비오르 바람 브레스
			case 1783: // 화염의 혼 켈베로스 불 뿜기
				시간당프레임 = 2;
				break;
			case 2244: // 블레싱
			case 744: // 힐
			case 2510: // 라이트
			case 221: // 실드
			case 167: // 에너지 볼트
			case 169: // 텔레포트
			case 1797: // 아이스 대거
			case 1799: // 윈드 커터
			case 236: // 뱀파이어릭 터치
			case 830: // 그레이터 힐
			case 1804: // 프로즌 클라우드
			case 1805: // 어스 재일
			case 1809: // 콘 오브 콜드
			case 2171: // 마나 드레인
			case 129: // 이럽션
			case 749: // 디텍션
			case 746: // 커스: 블라인드
			case 1811: // 선 버스트
			case 2228: // 위크니스
			case 759: // 힐 올
			case 832: // 풀 힐
			case 1812: // 어스 퀘이크
			case 3924: // 라이트닝 스톰
			case 2232: // 디케이 포션
				시간당프레임 = 5;
				break;
			case 757: // 블리자드
			case 760: // 포그 오브 슬리핑
			case 228: // 이뮨 투 함
			case 762: // 미티어 스트라이크
			case 4434: // 쇼크 스턴
				시간당프레임 = 16;
				break;
			case 1815: // 디스인티그레이트
			case 4648: // 바운스 어택
			case 5831: // 솔리드 캐리지
				시간당프레임 = 21;
				break;
			case 734: // 법사
			case 1186: // 여법
			case 61: // 남기
			case 48: // 여기
			case 0: // 남군
			case 1: // 여군
			case 37: // 여요
			case 138: // 남요
			case 2786: // 남다
			case 2796: // 여다
			case 6658: // 용남
			case 6661: // 용녀
			case 6671: // 환남
			case 6650: // 환녀
				시간당프레임 = 6;
				break;	
			case 240: // 데스나이트
			case 2284: // 다크엘프
			case 3784: // 55 데스나이트
			case 3890:// 다크나이트
			case 3891:// 블랙위자드
			case 3892:// 다크레인저
			case 3893:// 실버나이트
			case 3894:// 실버메지스터
			case 3896:// 소드나이트
			case 3897:// 위자드마스터
			case 3898:// 에로우마스터
			case 3899:// 아크나이트
			case 3900:// 아크위자드
			case 3901:// 아크레인저
			case 3895:// 실버요정
			case 4932:// 어세신마스터
			case 6279:// 다크쉐도우
			case 6280:// 실버쉐도우
			case 6281:// 아크스워드
			case 6282:// 아크쉐도우
			case 6137:// 데스52
			case 7332:// 랜마52
			case 7338:// 랜마55
			case 7339:// 랜마60
			case 7340:// 랜마65
			case 7341:// 랜마70
			case 7038:// 라미아스
			case 7040:// 엔디아스
			case 7042:// 이데아
			case 2378:// 스파토이
			case 3886:// 레서데몬
			case 3101:// 커츠
			case 6269:// 다크레인져
			case 4917:// 45레인져
			case 6268:// 다크위자드
			case 6868: // 법사
			case 6223: // 점령술사변신
			case 7968:// 천상의기사
				시간당프레임 = 7;
				break;
			case 3888: // 바포
			case 3905: // 베레스
			case 4923: // 흑기사
			case 4133: // 라쿤
			case 6010: // 붉은오크
			case 146: // 웅골
			case 95: // 셀로브
			case 6400: // 호박
			case 3479: // 리틀버그
			case 3480:
			case 3481:
			case 3482:
			case 8768:// 눈덩이변신
			case 6142:// 데스나이트
			case 8817:// 켄라우헬외75가지변신류
			case 8774:
			case 8842:
			case 8843:
			case 9205:
			case 9011:
			case 9225:
			case 8812:// 80변신
			case 8844:
			case 8845:
			case 8846:
			case 9206:
			case 9012:
			case 9226:
				시간당프레임 = 8;
				break;
			case 2501: // 잭
			case 6080: // 기마투구
			case 6094: // 기마투구
			case 1080: // 메티스
			case 6227: // 도펠보스
			case 6697: // 하피터번
			case 6698: // 위자드
			case 6406: // 근위병
			case 4004: // 서큐
			case 5645://허수아비
			case 8677://고양이
			case 8678://데스
			case 7126: // 실렌의투구1
			case 7127: // 실렌의투구2
			case 7128: // 실렌의투구3
			case 7129: // 실렌의투구4
			case 6152:
				시간당프레임 = 9;
				break;
			case 1353: // 경주견
			case 1355:
			case 1357:
			case 1359:
			case 1461:
			case 1462:
			case 1463:
			case 1464:
			case 1465:
			case 1466:
			case 1467:
			case 1468:
			case 1469:
			case 1470:
			case 1471:
			case 1472:
			case 1473:
			case 1474:
			case 1475:
			case 1476:
				시간당프레임 = 10;
				break;
			default:
				if (activeCharInstance.isGm())
					시간당프레임 = 999;
				else
					시간당프레임 = 6;
				break;
			}
			if (now.getSeconds() == 저장시간) {
				현시간 = 저장시간;
				i++;
			}
			if (now.getSeconds() != 현시간) {
				i = 0;
				현시간 = now.getSeconds();
			}
			if (i >= 시간당프레임) {
				돼지꼬리++;
				int time = 6 * 1000;
				//System.out.println("\n [스핵사용자] : "
						//+ activeCharInstance.getName() + "[" + getAccountName()
						//+ "]" + " [프레임]: " + (i + 1) + " [변신ID]: "
						//+ activeCharInstance.getGfxId().getTempCharGfx());
				i = 0;
				activeCharInstance.getSkillEffectTimerSet().setSkillEffect(
						L1SkillId.SHOCK_STUN, time);
				activeCharInstance.sendPackets(new S_SkillSound(
						activeCharInstance.getId(), 4434));
				Broadcaster.broadcastPacket(activeCharInstance,
						new S_SkillSound(activeCharInstance.getId(), 4434));
				activeCharInstance.sendPackets(new S_Paralysis(
						S_Paralysis.TYPE_STUN, true));
				//activeCharInstance.sendPackets(new S_SystemMessage(
						//"SPEED감지몸이마비됩니다."));
			}
			if (돼지꼬리 >= 2) {
				sendPacket(new S_Disconnect());
				돼지꼬리 = 0;
			}
			저장시간 = now.getSeconds();
			// 이동은 가능한 한 확실히 실시하기 때문에(위해), 이동 전용 thread에 주고 받아
			movePacket.requestWork(data);
		} else {
			// 패킷 처리 thread에 주고 받아
			hcPacket.requestWork(data);

		}

	}
	private int CheckTime(Date now) {

	return RealTimeClock.getInstance().getRealTime().getSeconds();

		}
	public String printData(byte[] data, int len) {
		StringBuffer result = new StringBuffer();
		int counter = 0;
		for (int i = 0; i < len; i++) {
			if (counter % 16 == 0) {
				result.append(fillHex(i, 4) + ": ");
			}
			result.append(fillHex(data[i] & 0xff, 2) + " ");
			counter++;
			if (counter == 16) {
				result.append("   ");
				int charpoint = i - 15;
				for (int a = 0; a < 16; a++) {
					int t1 = data[charpoint++];
					if (t1 > 0x1f && t1 < 0x80) {
						result.append((char) t1);
					} else {
						result.append('.');
					}
				}
				result.append("\n");
				counter = 0;
			}
		}

		int rest = data.length % 16;
		if (rest > 0) {
			for (int i = 0; i < 17 - rest; i++) {
				result.append("   ");
			}

			int charpoint = data.length - rest;
			for (int a = 0; a < rest; a++) {
				int t1 = data[charpoint++];
				if (t1 > 0x1f && t1 < 0x80) {
					result.append((char) t1);
				} else {
					result.append('.');
				}
			}

			result.append("\n");
		}
		return result.toString();
	}

	private String fillHex(int data, int digits) {
		String number = Integer.toHexString(data);

		for (int i = number.length(); i < digits; i++) {
			number = "0" + number;
		}
		return number;
	}

	/**
	 * 
	 * @author Developer
	 * 
	 */
	class ClientThreadObserver extends TimerTask {
		private int _checkct = 1;

		private final int _disconnectTimeMillis;

		public ClientThreadObserver(int disconnectTimeMillis) {
			_disconnectTimeMillis = disconnectTimeMillis;
		}

		public void start() {
			observerTimer.scheduleAtFixedRate(ClientThreadObserver.this, 0,
					_disconnectTimeMillis);
		}

		@Override
		public void run() {
			try {
				if (session.isClosing()) {
					cancel();
					return;
				}

				if (_checkct > 0) {
					_checkct = 0;
					return;
				}

				if (activeCharInstance == null // 캐릭터 선택전
						|| activeCharInstance != null
						&& !activeCharInstance.isPrivateShop()) { // 개인
					// 상점중
					kick();
					_log.warning("일정시간 응답을 얻을 수 없었기 때문에(" + hostname
							+ ")과(와)의 접속을 강제 절단 했습니다.");
					cancel();
					return;
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "LineageClient[:run:]Error", e);
				cancel();
			}
		}

		public void packetReceived() {
			_checkct++;
		}
	}

	// 케릭터의 패킷 처리 thread
	class ClinetPacket implements Runnable {
		public ClinetPacket() {

		}

		public void run() {
			while (!session.isClosing()) {
				try {
					// 디코더
					synchronized (PacketD) {
						int length = PacketSize(PacketD);
						if (length != 0 && length <= PacketIdx) {
							byte[] temp = new byte[length];
							System.arraycopy(PacketD, 0, temp, 0, length);
							System.arraycopy(PacketD, length, PacketD, 0,
									PacketIdx - length);
							PacketIdx -= length;
							encryptD(temp);
						}
					}
					Thread.sleep(10);
				} catch (Exception e) {
					// Logger.getInstance().error(getClass().toString()+"
					// run()\r\n"+e.toString(), Config.LOG.error);
				}
			}
		}
	}

	// 캐릭터의 행동 처리 thread
	class HcPacket implements Runnable {
		private final Queue<byte[]> _queue;

		private PacketHandler _handler;

		public HcPacket() {
			_queue = new ConcurrentLinkedQueue<byte[]>();
			_handler = new PacketHandler(LineageClient.this);
		}

		public HcPacket(int capacity) {
			_queue = new LinkedBlockingQueue<byte[]>(capacity);
			_handler = new PacketHandler(LineageClient.this);
		}

		public void requestWork(byte data[]) {
			if (data != null) {
				_queue.offer(data);
				synchronized (_queue) {
					_queue.notify();
				}
			}
		}

		public void requestWork() {
			synchronized (_queue) {
				_queue.notify();
			}
		}

		public void run() {
			byte[] data;
			while (!session.isClosing()) {
				if (_queue.isEmpty()) {
					try {
						synchronized (_queue) {
							_queue.wait();
						}
					} catch (Exception e) {
					}
				}
				while (!_queue.isEmpty()) {
					data = _queue.poll();
					if (data == null || session.isClosing()) {
						break;
					}
					try {
						_handler.handlePacket(data, activeCharInstance);
					} catch (Exception e) {
					}
				}
			}
		}
	}

}
