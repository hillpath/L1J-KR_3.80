package l1j.server.server.model.Instance;

import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastMap;
import javolution.util.FastTable;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.SpecialEventHandler;
import l1j.server.GameSystem.RobotThread;
import l1j.server.GameSystem.bean.RobotMent;
import l1j.server.Warehouse.ClanWarehouse;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.ActionCodes;
import l1j.server.server.BattleZone;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.command.executor.L1HpBar;
import l1j.server.server.command.executor.L1Robot;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.AcceleratorChecker;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1ChatParty;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1EquipmentSlot;
import l1j.server.server.model.L1ExcludingList;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Karma;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1RobotAI;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.ReportDeley;
import l1j.server.server.model.classes.L1ClassFeature;
import l1j.server.server.model.gametime.GameTimeCarrier;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.monitor.L1PcAutoUpdate;
import l1j.server.server.model.monitor.L1PcExpMonitor;
import l1j.server.server.model.monitor.L1PcGhostMonitor;
import l1j.server.server.model.monitor.L1PcHellMonitor;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_BlueMessage;
import l1j.server.server.serverpackets.S_BonusStats;
import l1j.server.server.serverpackets.S_CastleMaster;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharTitle;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DelSkill;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_Exp;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_Lawful;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PinkName;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.CalcStat;
import l1j.server.server.utils.FaceToFace;
import l1j.server.server.utils.SQLUtil;
import server.LineageClient;
import server.controller.HalloweenAHSHRegenController;
import server.controller.MpDecreaseByScalesController;
import server.controller.Doll.DollHPMPRegenController;
import server.controller.pc.PcInvisDelayController;

//Referenced classes of package l1j.server.server.model:
//L1Character, L1DropTable, L1Object, L1ItemInstance,
//L1World
public class L1PcInstance extends L1Character {

	//
	private static final long serialVersionUID = 1L;

	private Random random2 = new Random(System.nanoTime());

	private GeneralThreadPool _threadPool = GeneralThreadPool.getInstance();

	public static final int CLASSID_PRINCE = 0;

	public static final int CLASSID_PRINCESS = 1;

	public static final int CLASSID_KNIGHT_MALE = 61;

	public static final int CLASSID_KNIGHT_FEMALE = 48;

	public static final int CLASSID_ELF_MALE = 138;

	public static final int CLASSID_ELF_FEMALE = 37;

	public static final int CLASSID_WIZARD_MALE = 734;

	public static final int CLASSID_WIZARD_FEMALE = 1186;

	public static final int CLASSID_DARKELF_MALE = 2786;

	public static final int CLASSID_DARKELF_FEMALE = 2796;

	public static final int CLASSID_DRAGONKNIGHT_MALE = 6658;

	public static final int CLASSID_DRAGONKNIGHT_FEMALE = 6661;

	public static final int CLASSID_ILLUSIONIST_MALE = 6671;

	public static final int CLASSID_ILLUSIONIST_FEMALE = 6650;

	public static final int REGENSTATE_NONE = 4;

	public static final int REGENSTATE_MOVE = 2;

	public static final int REGENSTATE_ATTACK = 1;

	private L1ClassFeature _classFeature = null;

	private L1EquipmentSlot _equipSlot;

	private String _accountName;

	private int _classId;

	private int _type;

	private int _exp;

	private int _age;
	
	
	 
	 public FastTable<Integer> speedbookmark_list = new FastTable<Integer>();
	 public FastTable<Integer> normalbookmark_list = new FastTable<Integer>();

	 private int _markcount;

	 public void setMark_count(int i) {
	  _markcount = i;
	 }

	 public int getMark_count() {
	  return _markcount;
	 }


	
	// 불골렘
	public int[] FireGolem = new int [18];
	public int[] FireEnchant = new int [18];

	/** 피어스 리뉴얼 **/
	public int[] PiersItemId = new int [19];
	public int[] PiersEnchant = new int [19];
	
	private short _accessLevel;
	/**안전모드**/
	public boolean Safe_Teleport = false;
	/**안전모드**/
	/** Robot 관련 **/
	public long RobotBuffTime = 0;
	public long RobotTelePortTime = 0;
	public int RobotAIType = 1;
	public static final int RobotRun = 0;
	public static final int RobotRandomMove = 1;
	public static final int RobotAttack = 2;
	public int _randomMoveDistance = 0;
	public int _randomMoveHeading = 0;
	public long RobotSleepTime = 0;
	public int RobotMoveCount = -1;
	public int RobotTelMoveCount = 4;//랜덤워킹타겟없으면텔
	public long  RobotDeadDelay = 0;
	public long RobotTargetMoveCount = 6;//몹인식후경로불가능텔
	public boolean RobotStartCK = false;
	public boolean RobotHomeBuff = false;
	public L1Character RobotTarget;
	private FastTable<L1Character> _robotTargetList = new FastTable<L1Character>();
	
	// 3.63아이템패킷추가
	public boolean isWorld = false;
	// 3.63아이템패킷추가
	
	
	public FastTable<L1Character> getRobotTargetlist(){
		return _robotTargetList;
	}
	public boolean addRobotTargetlist(L1Character cha){
		synchronized(_robotTargetList){
			if(!_robotTargetList.contains(cha)){
				_robotTargetList.add(cha);
				return true;
			}
		}
		return false;
	}
	
	private String _characterName = "";
	public void setCharacterName(String name) {
		_characterName = name;
	}
	public String getCharacterName() {
		return _characterName;
	}
	
	public void removeRobotTargetlist(L1Character cha){
		synchronized(_robotTargetList){
			if(_robotTargetList.contains(cha))
				_robotTargetList.remove(cha);
		}
	}
	
/*	public FastTable<Integer> speedbookmark_list = new FastTable<Integer>();
	 public FastTable<Integer> normalbookmark_list = new FastTable<Integer>();

	private int _markcount;

    public void setMark_count(int i) {
	  _markcount = i;
	 }

	 public int getMark_count() {
	  return _markcount;
	 }*/

	 

	
	
	
	/**코마리뉴얼**/
	private int _deathmatch;
	 public int getDeathMatchPiece() {
	  return _deathmatch;
	 }
	 public void setDeathMatchPiece(int i) {
	  _deathmatch = i;
	 }
	 private int _petrace;
	 public int getPetRacePiece() {
	  return _petrace;
	 }
	 public void setPetRacePiece(int i) {
	  _petrace = i;
	 }
	 private int _ultimatebattle;
	 public int getUltimateBattlePiece() {
	  return _ultimatebattle;
	 }
	 public void setUltimateBattlePiece(int i) {
	  _ultimatebattle = i;
	 }
	 private int _petmatch;
	 public int getPetMatchPiece() {
	  return _petmatch;
	 }
	 public void setPetMatchPiece(int i) {
	  _petmatch = i;
	 }
	 private int _ghosthouse;
	 public int getGhostHousePiece() {
	  return _ghosthouse;
	 }
	 public void setGhostHousePiece(int i) {
	  _ghosthouse = i;
	 }

	/**혈맹마크모두띠우기**/
	public boolean GMCommand_Clanmark = false;
	/**혈맹마크모두띠우기**/
	 /**
	  * 혈맹보호 변수부분
	  */
	 private boolean _bloodprotect = false;
	 
	 public boolean getProtect(){
	  return _bloodprotect;
	 }
	 public void setProtect(boolean A){
	  _bloodprotect = A;
	 }

	// 변수 추가

	private int _knifeHitupBydoll = 0; // 인형에 의한 근거리 공격성공

	private int _knifeDmgupBydoll = 0; // 인형에 의한 근거리 추타

	// 프로퍼티 추가

	public int getKnifeDmgupByDoll() {
		return _knifeDmgupBydoll;
	}

	public void addKnifeDmgupByDoll(int i) {
		_knifeDmgupBydoll += i;
	}

	public int getKnifeHitupByDoll() {
		return _knifeHitupBydoll;
	}

	public void addKnifeHitupByDoll(int i) {
		_knifeHitupBydoll += i;
	}

	private short _baseMaxHp = 0;

	private short _baseMaxMp = 0;

	private int _baseAc = 0;

	private int _bonusStats;

	private int _baseBowDmgup = 0;

	private int _baseDmgup = 0;

	private int _baseHitup = 0;

	private int _baseBowHitup = 0;

	private int _baseMagicHitup = 0; // 베이스 스탯에 의한 마법 명중

	private int _baseMagicCritical = 0; // 베이스 스탯에 의한 마법 치명타(%)

	private int _baseMagicDmg = 0; // 베이스 스탯에 의한 마법 데미지

	private int _baseMagicDecreaseMp = 0; // 베이스 스탯에 의한 마법 데미지

	private int _HitupByArmor = 0; // 방어용 기구에 의한 근접무기 명중율

	private int _bowHitupByArmor = 0; // 방어용 기구에 의한 활의 명중율

	private int _DmgupByArmor = 0; // 방어용 기구에 의한 근접무기 추타율

	private int _bowDmgupByArmor = 0; // 방어용 기구에 의한 활의 추타율

	private int _bowHitupBydoll = 0; // 인형에 의한 원거리 공격성공률

	private int _bowDmgupBydoll = 0; // 인형에 의한 원거리 추타율
	
	private int _MagicHitupByArmor = 0;        // 방어용 기구에 의한 마법 확률 증가

	private int _PKcount;
	 /** 낚시본섭화**/
	public int _fishingX = 0;
	public int _fishingY = 0;
	 /** 낚시본섭화 **/

	private int _clanid;

	private String clanname;

	private int _clanRank;

	private byte _sex;

	private int _returnstat;

	private short _hpr = 0;

	private short _trueHpr = 0;

	private short _mpr = 0;

	private short _trueMpr = 0;

	private int _advenHp;

	private int _advenMp;

	private int _highLevel;

	private boolean _ghost = false;

	private boolean _isReserveGhost = false;

	private boolean _isShowTradeChat = true;

	private boolean _isCanWhisper = true;

	private boolean _isFishing = false;

	private boolean _isFishingReady = false;

	private boolean petRacing = false; // 펫레이싱

	private int petRacingLAB = 1; // 현재 LAB

	private int petRacingCheckPoint = 162; // 현재구간

	private boolean isHaunted = false;

	private boolean isDeathMatch = false;

	private boolean _isShowWorldChat = true;

	private boolean _gm;

	private boolean _monitor;

	private boolean _gmInvis;

	private boolean _isTeleport = false;

	private boolean _isDrink = false;

	private boolean _isGres = false;

	private boolean _isPinkName = false;

	private boolean _banned;

	private boolean _gresValid;

	private boolean _tradeOk;



	private boolean _mpRegenActiveByDoll;

	private boolean _mpDecreaseActiveByScales;



	private boolean _AHRegenActive;

	private boolean _SHRegenActive;

	private boolean _HalloweenRegenActive;

	private boolean _hpRegenActiveByDoll;


	private boolean _hongbo;



	public boolean CHATrade1 = false;

	public boolean CHATrade2 = false;

	public L1PcInstance CHATradePC;

	public boolean FafuArmor = false;

	public boolean AntaArmor = false;

	public boolean BalraArmor = false;

	public boolean LindArmor = false;

	/** **바포레벨이요** */
	private int _nbapoLevel;

	private int _obapoLevel;

	/** **바포레벨이요** */

	/** **바포추타요** */
	private int _bapodmg;

	/** **바포추타요** */
	public boolean MaanDodge = false;

	public boolean MaanMagicIm = false;

	public boolean MaanMagicDmg = false;

	public boolean MaanAddDmg = false;

	public int LawfulAC = 0;

	public int LawfulMR = 0;

	public int LawfulSP = 0;

	public int LawfulAT = 0;
	
	
	/** 바포추타입니다.* */
	public int getBapodmg() {
		return _bapodmg;
	}

	public void setBapodmg(int i) {
		_bapodmg = i;
	}

	/** 바포추타입니다.* */

	/** 바포레벨입니다.* */
	public int getNBapoLevel() {
		return _nbapoLevel;
	}

	public void setNBapoLevell(int i) {
		_nbapoLevel = i;
	}

	public int getOBapoLevel() {
		return _obapoLevel;
	}

	public void setOBapoLevell(int i) {
		_obapoLevel = i;
	}

	/** 바포레벨입니다.* */
	private int invisDelayCounter = 0;

	private Object _invisTimerMonitor = new Object();

	private int _ghostSaveLocX = 0;

	private int _ghostSaveLocY = 0;

	private short _ghostSaveMapId = 0;

	private int _ghostSaveHeading = 0;

	private ScheduledFuture<?> _ghostFuture;

	private ScheduledFuture<?> _hellFuture;

	private ScheduledFuture<?> _autoUpdateFuture;

	private ScheduledFuture<?> _expMonitorFuture;

	private Timestamp _lastPk;

	private Timestamp _deleteTime;

	private int _weightReduction = 0;

	private int _hasteItemEquipped = 0;

	private int _damageReductionByArmor = 0;

	private final L1ExcludingList _excludingList = new L1ExcludingList();

	private final AcceleratorChecker _acceleratorChecker = new AcceleratorChecker(
			this);

	private FastTable<Integer> skillList = new FastTable<Integer>();

	private int _teleportY = 0;

	private int _teleportX = 0;

	private short _teleportMapId = 0;

	private int _teleportHeading = 0;
	
	private int ranking = 0;
	private int rankingCount = 0;
	public int getRanking() {
		return ranking;
	}
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}
	public int getRankingCount() {
		return rankingCount;
	}
	public void setRankingCount(int rankingCount) {
		this.rankingCount = rankingCount;
	}

	private int _speedhackCount = 0;

	private int _speedhackX = 0;

	private int _speedhackY = 0;

	private short _speedhackMapid = 0;

	private int _speedhackHeading = 0;

	private int _speedright = 0;

	private int _speedinterval = 0;

	private int _tempCharGfxAtDead;

	private int _fightId;

	private byte _chatCount = 0;

	private long _oldChatTimeInMillis = 0L;

	private int _elfAttr;

	private int _expRes;

	private int _onlineStatus;

	private int _homeTownId;

	private int _contribution;

	private int _food;

	private int _hellTime;

	private int _partnerId;

	private long _fishingTime = 0;
	

	
	
	
	 /** 생존의외침 아이템화 **/
	private long _SurvivalCry; 
	public long getSurvivalCry() {
		return _SurvivalCry;
	}
	public void setSurvivalCry(long SurvivalCry) {
		_SurvivalCry = SurvivalCry;
	}
	 /** 생존의외침 아이템화 **/
	
	 public int  _MonsterKill = 0; 
	 public int getMonsterKill () {return _MonsterKill ; }
	 public void setMonsterKill (int i) { _MonsterKill  = i; 
	 sendPackets(new S_OwnCharStatus(this));
	 }
	
	private int _dessertId = 0;

	private int _callClanId;

	private int _callClanHeading;

	private int _currentWeapon;

	private final L1Karma _karma = new L1Karma();

	private final L1PcInventory _inventory;

	private final L1Inventory _tradewindow;

	private L1ItemInstance _weapon;

	private L1ItemInstance _armor;

	private L1Party _party;

	private L1ChatParty _chatParty;
	
	private boolean bug_ment = true;//버경멘트추가

	private int _cookingId = 0;

	private int _partyID;

	private int _partyType;

	private int _tradeID;

	private int _tempID;

	private int _ubscore;



	private L1Quest _quest;

	// 딜레이주기시간 텔렉풀기 등등
	private long _quiztime = 0;

	public long getQuizTime() {
		return _quiztime;
	}

	public void setQuizTime(long l) {
		_quiztime = l;
	}

	// 딜레이주기시간
	// 무인pc 관련 flag
	public boolean noPlayerCK;
	/** 2011.08.29 고정수 로봇 액션 딜레이 정보 */
    private int teleportTime = 0;
	private int currentTeleportCount = 0;
	private int skillTime = 0;	
	private int currentSkillCount = 0;
	private int moveTime = 0;
	private int currentMoveCount = 0;
	
	private ReportDeley _reportdeley;    // /신고 추가
	 private static Timer _regenTimer = new Timer(true); // /신고 추가
	 //private boolean _mpRegenActive;

	public int getTeleportTime() {
		return teleportTime;
	}

	public void setTeleportTime(int teleportTime) {
		this.teleportTime = teleportTime;
	}

	public int getCurrentTeleportCount() {
		return currentTeleportCount;
	}

	public void setCurrentTeleportCount(int currentTeleportCount) {
		this.currentTeleportCount = currentTeleportCount;
	}

	public int getSkillTime() {
		return skillTime;
	}

	public void setSkillTime(int skillTime) {
		this.skillTime = skillTime;
	}

	public int getCurrentSkillCount() {
		return currentSkillCount;
	}

	public void setCurrentSkillCount(int currentSkillCount) {
		this.currentSkillCount = currentSkillCount;
	}
	
	private int _dragondungeon;//용던 리뉴얼
	public int getDdungeonTime() { return _dragondungeon; }
	public void setDdungeonTime(int i) { _dragondungeon = i; }
	
	private int _outpostdungeon;//전초기지
	public int getoptTime() { return _outpostdungeon; }
	public void setoptTime(int i) { _outpostdungeon = i; }
	
	public int getMoveTime() {
		return moveTime;
	}
	
	public void setMoveTime(int moveTime) {
		this.moveTime = moveTime;
	}

	public int getCurrentMoveCount() {
		return currentMoveCount;
	}

	public void setCurrentMoveCount(int currentMoveCount) {
		this.currentMoveCount = currentMoveCount;
	}
	public int getinstance() {
		// TODO Auto-generated method stub
		return 0;
	}
	private int _birthday = 0;//생일표기


	public boolean Gamble = false;

	public String GambleText;

	public int _gambleAden = 0;

	public boolean _PcNameChange = false;
	

	private long _telldelayTime = 0;
	public long gettelldelayTime() { return _telldelayTime; }	
	public void settelldelayTime(long l) { _telldelayTime = l ; } 

	public L1ItemInstance _PcNameChangeItem = null;
	// 자동사냥체크
	private boolean 참거짓확인용변수 = false; // 오토채킹을 위한 불렌값

	public boolean 오토확인이필요한상태인지함수() {
		return 참거짓확인용변수;
	}// 오토상태확인

	public void 오토확인필요상태로바꾸기() {
		참거짓확인용변수 = true;
	}// 오토상태로 설정

	public void 오토확인불필요상태로바꾸기() {
		참거짓확인용변수 = false;
	}// 오토확인후 정상상태로 리턴

	// 자동사냥체크
	// ////////////////////////////캐릭터교환//////////////////////////////////////////

	private boolean _isChaTradeSlot = false;

	public boolean isChaTradeSlot() {
		return _isChaTradeSlot;
	}

	public void setChaTradeSlot(boolean is) {
		_isChaTradeSlot = is;
	}

	// ////////////////////////////캐릭터교환//////////////////////////////////////////
	private boolean _isTradingInPrivateShop = false;

	private boolean _isPrivateShop = false;

	private int _partnersPrivateShopItemCount = 0;

	public boolean StatReturnCK = false;

	private final FastTable<L1BookMark> _bookmarks;

	private FastTable<L1PrivateShopSellList> _sellList = new FastTable<L1PrivateShopSellList>();

	private FastTable<L1PrivateShopBuyList> _buyList = new FastTable<L1PrivateShopBuyList>();

	private final Map<Integer, L1NpcInstance> _petlist = new FastMap<Integer, L1NpcInstance>();

	private final Map<Integer, L1DollInstance> _dolllist = new FastMap<Integer, L1DollInstance>();

	private final Map<Integer, L1FollowerInstance> _followerlist = new FastMap<Integer, L1FollowerInstance>();

	private byte[] _shopChat;

	private LineageClient _netConnection;

	private static Logger _log = Logger.getLogger(L1PcInstance.class.getName());

	public long SHMoveTime = 0;

	public long SHAttackTime = 0;

	public long DollHPRegenTime = 0;

	public long DollMPRegenTime = 0;

	public long ScalesMpDecreaseTime = 0;

	public long SHRegenTime = 0;

	public long AHRegenTime = 0;

	public long HalloweenRegenTime = 0;

	public long InvisDelayTime = 0;

	public int HongBoCount = 0;

	public boolean CastleClan = false;

	private long lastSavedTime = System.currentTimeMillis();

	private long lastSavedTime_inventory = System.currentTimeMillis();



	public L1PcInstance() {
		super();
		_accessLevel = 0;
		_currentWeapon = 0;
		_inventory = new L1PcInventory(this);
		_tradewindow = new L1Inventory();
		_bookmarks = new FastTable<L1BookMark>();
		_quest = new L1Quest(this);
		_equipSlot = new L1EquipmentSlot(this);
	}

	// /////////////////////// 타임/////////////////////////////////

	// /////////////////////// 타임/////////////////////////////////
	public void setBirthDay(int time) {
		_birthday = time;
	}

	public int getBirthDay() {
		return _birthday;
	}



	public long getlastSavedTime() {
		return lastSavedTime;
	}

	public long getlastSavedTime_inventory() {
		return lastSavedTime_inventory;
	}

	public void setlastSavedTime(long stime) {
		this.lastSavedTime = stime;
	}

	public void setlastSavedTime_inventory(long stime) {
		this.lastSavedTime_inventory = stime;
	}

	public void setSkillMastery(int skillid) {
		if (!skillList.contains(skillid)) {
			skillList.add(skillid);
		}
	}

	public void removeSkillMastery(int skillid) {
		if (skillList.contains((Object) skillid)) {
			skillList.remove((Object) skillid);
		}
	}

	public boolean isSkillMastery(int skillid) {
		return skillList.contains(skillid);
	}

	public void clearSkillMastery() {
		skillList.clear();
	}

	public short getHpr() {
		return _hpr;
	}

	public void addHpr(int i) {
		_trueHpr += i;
		_hpr = (short) Math.max(0, _trueHpr);
	}

	public short getMpr() {
		return _mpr;
	}

	public void addMpr(int i) {
		_trueMpr += i;
		_mpr = (short) Math.max(0, _trueMpr);
	}

	public long RPTime = 0;



	public void startHpRegenerationByDoll() {
		final int INTERVAL_BY_DOLL = 32000;
		boolean isExistHprDoll = false;
		Collection<L1DollInstance> dolllist = null;
		dolllist = getDollList().values();
		for (L1DollInstance doll : dolllist) {
			if (doll.isHpRegeneration()) {
				isExistHprDoll = true;
			}
		}
		if (!_hpRegenActiveByDoll && isExistHprDoll) {
			DollHPRegenTime = INTERVAL_BY_DOLL + System.currentTimeMillis();
			_hpRegenActiveByDoll = true;
			DollHPMPRegenController.getInstance().addHP(this);
		}
	}

	public void startAHRegeneration() {
		final int INTERVAL = 600000;
		if (!_AHRegenActive) {
			_AHRegenActive = true;
			AHRegenTime = INTERVAL + System.currentTimeMillis();
			HalloweenAHSHRegenController.getInstance().addAH(this);
		}
	}

	public void startSHRegeneration() {
		final int INTERVAL = 1800000;
		if (!_SHRegenActive) {
			_SHRegenActive = true;
			SHRegenTime = INTERVAL + System.currentTimeMillis();
			HalloweenAHSHRegenController.getInstance().addSH(this);
		}
	}

	public void startHalloweenRegeneration() {
		final int INTERVAL = 900000;
		if (!_HalloweenRegenActive) {
			_HalloweenRegenActive = true;
			HalloweenRegenTime = INTERVAL + System.currentTimeMillis();
			HalloweenAHSHRegenController.getInstance().addHW(this);
		}
	}

	public void stopHpRegenerationByDoll() {
		if (_hpRegenActiveByDoll) {
			_hpRegenActiveByDoll = false;
			DollHPMPRegenController.getInstance().removeHP(this);
		}
	}

	public void startMpRegenerationByDoll() {
		final int INTERVAL_BY_DOLL = 64000;
		boolean isExistMprDoll = false;
		Collection<L1DollInstance> dolllist = null;
		dolllist = getDollList().values();
		for (L1DollInstance doll : dolllist) {
			if (doll.isMpRegeneration()) {
				isExistMprDoll = true;
			}
		}
		if (!_mpRegenActiveByDoll && isExistMprDoll) {
			DollMPRegenTime = INTERVAL_BY_DOLL + System.currentTimeMillis();
			_mpRegenActiveByDoll = true;
			DollHPMPRegenController.getInstance().addMP(this);
		}
	}

	public void startMpDecreaseByScales() {
		final int INTERVAL_BY_SCALES = 4000;
		_mpDecreaseActiveByScales = true;
		ScalesMpDecreaseTime = INTERVAL_BY_SCALES + System.currentTimeMillis();
		MpDecreaseByScalesController.getInstance().addPc(this);

	}
	public void startReportDeley() {   // /신고 추가
		  _reportdeley = new ReportDeley(this);
		  _regenTimer.schedule(_reportdeley, 100000);  // 딜레이 시간 10분
		 }
	public void stopMpRegenerationByDoll() {
		if (_mpRegenActiveByDoll) {
			_mpRegenActiveByDoll = false;
			DollHPMPRegenController.getInstance().removeMP(this);
		}
	}

	public void stopMpDecreaseByScales() {
		if (_mpDecreaseActiveByScales) {
			_mpDecreaseActiveByScales = false;
			MpDecreaseByScalesController.getInstance().removePc(this);
		}
	}

	public void stopAHRegeneration() {
		if (_AHRegenActive) {
			_AHRegenActive = false;
			HalloweenAHSHRegenController.getInstance().removeAH(this);
		}
	}

	public void stopSHRegeneration() {
		if (_SHRegenActive) {
			_SHRegenActive = false;
			HalloweenAHSHRegenController.getInstance().removeSH(this);
		}
	}

	public void stopHalloweenRegeneration() {
		if (_HalloweenRegenActive) {
			_HalloweenRegenActive = false;
			HalloweenAHSHRegenController.getInstance().removeHW(this);
		}
	}

	public void startObjectAutoUpdate() {
		final long INTERVAL_AUTO_UPDATE = 300;
		getNearObjects().removeAllKnownObjects();
		_autoUpdateFuture = GeneralThreadPool.getInstance()
		.pcScheduleAtFixedRate(new L1PcAutoUpdate(getId()), 0L, INTERVAL_AUTO_UPDATE);
	}
	public void stopEtcMonitor() {
		if (_autoUpdateFuture != null) {
			_autoUpdateFuture.cancel(true);
			_autoUpdateFuture = null;
		}
		if (_expMonitorFuture != null) {
			_expMonitorFuture.cancel(true);
			_expMonitorFuture = null;
		}
		if (_ghostFuture != null) {
			_ghostFuture.cancel(true);
			_ghostFuture = null;
		}

		if (_hellFuture != null) {
			_hellFuture.cancel(true);
			_hellFuture = null;
		}

	}

	public void stopEquipmentTimer() {
		List<L1ItemInstance> allItems = this.getInventory().getItems();
		for (L1ItemInstance item : allItems) {
			if (item == null)
				continue;
			if (item.isEquipped() && item.getRemainingTime() > 0) {
				item.stopEquipmentTimer();
			}
		}
	}

	public void onChangeExp() {
		int level = ExpTable.getLevelByExp(getExp());
		int char_level = getLevel();
		int gap = level - char_level;
		if (gap == 0) {
			sendPackets(new S_Exp(this));
			return;
		}

		if (gap > 0) {
			levelUp(gap);
		} else if (gap < 0) {
			levelDown(gap);
		}
	}

	@Override
	public void onPerceive(L1PcInstance pc) {
		if (isGhost()) {
			return;
		}
		pc.getNearObjects().addKnownObject(this);
		pc.sendPackets(new S_OtherCharPacks(this));

		if (isPinkName())
			pc.sendPackets(new S_PinkName(getId(), getSkillEffectTimerSet()
					.getSkillEffectTimeSec(L1SkillId.STATUS_PINK_NAME)));

		if (isInParty() && getParty().isMember(pc)) {
			pc.sendPackets(new S_HPMeter(this));
		}
		if (isPrivateShop()) {
			if (!(getAccessLevel() > 0))
				pc.sendPackets(new S_DoActionShop(getId(), ActionCodes.ACTION_Shop, getShopChat()));
		}
		 /** 낚시본섭화**/
		if(isFishing()){
			pc.sendPackets(new S_Fishing(getId(),ActionCodes.ACTION_Fishing, _fishingX, _fishingY));
		}
		 /** 낚시본섭화**/

	}

	public void updateObject() {
		List<L1Object> _Alist = null;
		_Alist = getNearObjects().getKnownObjects();
		for (L1Object known : _Alist) {
			if (known == null) {
				continue;
			}
			if (known.getMapId() == 631 && !isGm()) {
				if (known instanceof L1PcInstance) {
					continue;
				}
				
				if (known instanceof L1NpcInstance) {
					L1NpcInstance npc = (L1NpcInstance)known;
					if (getMapId() == 631 && getCashStep() == 0 && !isGm()) {
						continue;
					} else if (getMapId() == 631 && getCashStep() == 1 && !isGm()) {
						if (!(npc.getNpcTemplate().get_npcId() >= 9000001 && npc.getNpcTemplate().get_npcId() <= 9000012)) {
							continue;
						}
					} else if (getMapId() == 631 && getCashStep() == 2 && !isGm()) {
						if (!(npc.getNpcTemplate().get_npcId() >= 9000013 && npc.getNpcTemplate().get_npcId() <= 9000024)) {
							continue;
						}
					} else if (getMapId() == 631 && getCashStep() == 3 && !isGm()) {
						if (!(npc.getNpcTemplate().get_npcId() >= 9000025 && npc.getNpcTemplate().get_npcId() <= 9000036)) {
							continue;
						}
					}
				}
			}
			
			
			if (Config.PC_RECOGNIZE_RANGE == -1) {
				if (!getLocation().isInScreen(known.getLocation())) {
					getNearObjects().removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			} else {
				if (getLocation().getTileLineDistance(known.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
					getNearObjects().removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			}
		}
		FastTable<L1Object> _Vlist = null;
		_Vlist = L1World.getInstance().getVisibleObjects(this,
				Config.PC_RECOGNIZE_RANGE);
		for (L1Object visible : _Vlist) {
			if (visible == null)
				continue;
			if (visible instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance)visible;
				if (getMapId() == 631 && getCashStep() == 0 && !isGm()) {
					continue;
				} else if (getMapId() == 631 && getCashStep() == 1 && !isGm()) {
					if (!(npc.getNpcTemplate().get_npcId() >= 9000001 && npc.getNpcTemplate().get_npcId() <= 9000012)) {
						continue;
					}
				} else if (getMapId() == 631 && getCashStep() == 2 && !isGm()) {
					if (!(npc.getNpcTemplate().get_npcId() >= 9000013 && npc.getNpcTemplate().get_npcId() <= 9000024)) {
						continue;
					}
				} else if (getMapId() == 631 && getCashStep() == 3 && !isGm()) {
					if (!(npc.getNpcTemplate().get_npcId() >= 9000025 && npc.getNpcTemplate().get_npcId() <= 9000036)) {
						continue;
					}
				}
			}

			
			
			
			if (!getNearObjects().knownsObject(visible)) {
				if (visible.getMapId() == 631 && !isGm()) {
					if (visible instanceof L1PcInstance) {
						continue;
					}
				}
				visible.onPerceive(this);
			} else {
				if (visible instanceof L1NpcInstance) {
					L1NpcInstance npc = (L1NpcInstance) visible;
					if (getLocation().isInScreen(npc.getLocation())
							&& npc.getHiddenStatus() != 0) {
						npc.approachPlayer(this);
					}
				}
				if (visible instanceof L1NpcCashShopInstance) {
					L1NpcInstance npc = (L1NpcInstance)visible;
					if (getMapId() == 631 && getCashStep() == 0 && !isGm()) {
						continue;
					} else if (getMapId() == 631 && getCashStep() == 1 && !isGm()) {
						if (!(npc.getNpcTemplate().get_npcId() >= 9000001 && npc.getNpcTemplate().get_npcId() <= 9000012)) {
							continue;
						}
					} else if (getMapId() == 631 && getCashStep() == 2 && !isGm()) {
						if (!(npc.getNpcTemplate().get_npcId() >= 9000013 && npc.getNpcTemplate().get_npcId() <= 9000024)) {
							continue;
						}
					} else if (getMapId() == 631 && getCashStep() == 3 && !isGm()) {
						if (!(npc.getNpcTemplate().get_npcId() >= 9000025 && npc.getNpcTemplate().get_npcId() <= 9000036)) {
							continue;
						}
					}
				}
			}
		/*	if (getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.GMSTATUS_HPBAR)
					&& L1HpBar.isHpBarTarget(visible)) {
				L1Character cha = (L1Character) visible;
				if (cha.HPBAR_currentHp != cha.getCurrentHp()) {
					cha.HPBAR_currentHp = cha.getCurrentHp();
					sendPackets(new S_HPMeter(cha));*/
					
					if (getSkillEffectTimerSet().hasSkillEffect(
							L1SkillId.GMSTATUS_HPBAR)
							&& L1HpBar.isHpBarTarget(visible)) {
						sendPackets(new S_HPMeter((L1Character) visible));
				}
			}
		}

	private void sendVisualEffect() {
		int poisonId = 0;
		if (getPoison() != null) {
			poisonId = getPoison().getEffectId();
		}
		if (getParalysis() != null) {
			poisonId = getParalysis().getEffectId();
		}
		if (poisonId != 0) {
			sendPackets(new S_Poison(getId(), poisonId));
			Broadcaster.broadcastPacket(this, new S_Poison(getId(), poisonId));
		}
	}

	public void sendVisualEffectAtLogin() {
		sendVisualEffect();
	}
	public void sendCastleMaster(){
		if (getClanid() != 0) {
			L1Clan clan = L1World.getInstance().getClan(getClanname());
			if (clan != null) {
				if (isCrown() && getId() == clan.getLeaderId()
						&& clan.getCastleId() != 0) {
					sendPackets(new S_CastleMaster(clan.getCastleId(), getId()));
				}
			}
		}	
	}

	public void sendVisualEffectAtTeleport() {
		if (isDrink()) {
			sendPackets(new S_Liquor(getId()));
		}
		sendVisualEffect();
	}

	@Override
	public void setCurrentHp(int i) {
		if (getCurrentHp() == i) return;
		super.setCurrentHp(i);
		sendPackets(new S_HPUpdate(getCurrentHp(), getMaxHp()));
		if (isInParty()) getParty().updateMiniHP(this);
		/**배틀존**/
		if (get_DuelLine() != 0){
			   for (L1PcInstance member : getNearObjects().getKnownPlayers()){
			    if(member != null){
			     if(get_DuelLine() == member.get_DuelLine()){
			      member.sendPackets(new S_HPMeter(getId(), 100
			       * getCurrentHp() / getMaxHp()));
			     }
			    }
			   }
			  }
		/**배틀존**/
	}


	@Override
	public void setCurrentMp(int i) {
		if (getCurrentMp() == i)
			return;
		if (isGm())
			i = getMaxMp();
		super.setCurrentMp(i);
		sendPackets(new S_MPUpdate(getCurrentMp(), getMaxMp()));
	}

	@Override
	public L1PcInventory getInventory() {
		return _inventory;
	}

	public L1Inventory getTradeWindowInventory() {
		return _tradewindow;
	}

	public boolean isGmInvis() {
		return _gmInvis;
	}

	public void setGmInvis(boolean flag) {
		_gmInvis = flag;
	}

	public int getCurrentWeapon() {
		return _currentWeapon;
	}

	public void setCurrentWeapon(int i) {
		_currentWeapon = i;
	}

	public int getType() {
		return _type;
	}

	public void setType(int i) {
		_type = i;
	}

	public short getAccessLevel() {
		return _accessLevel;
	}

	public void setAccessLevel(short i) {
		_accessLevel = i;
	}

	public int getClassId() {
		return _classId;
	}

	public void setClassId(int i) {
		_classId = i;
		_classFeature = L1ClassFeature.newClassFeature(i);
	}

	public L1ClassFeature getClassFeature() {
		return _classFeature;
	}
	private boolean _castleIn  = false;
	
	public void setCastleIn(boolean fiag){
		_castleIn = fiag;
	}
	public boolean getCastleIn(){
		return _castleIn;
	}

	@Override
	public synchronized int getExp() {
		return _exp;
	}

	@Override
	public synchronized void setExp(int i) {
		_exp = i;
	}

	public synchronized int getReturnStat() {
		return _returnstat;
	}

	public synchronized void setReturnStat(int i) {
		_returnstat = i;
	}

	private L1PcInstance getStat() {
		return null;
	}

	public void reduceCurrentHp(double d, L1Character l1character) {
		getStat().reduceCurrentHp(d, l1character);
	}

	private String _sealingPW; // ● 클랜명

	public String getSealingPW() {
		return _sealingPW;
	}

	public void setSealingPW(String s) {
		_sealingPW = s;
	}

	private void notifyPlayersLogout(List<L1PcInstance> playersArray) {
		for (L1PcInstance player : playersArray) {
			if (player == null)
				continue;
			if (player.getNearObjects().knownsObject(this)) {
				player.getNearObjects().removeKnownObject(this);
				player.sendPackets(new S_RemoveObject(this));
			}
		}
	}

	public void logout() {
		L1World world = L1World.getInstance();
		notifyPlayersLogout(getNearObjects().getKnownPlayers());
		world.removeVisibleObject(this);
		world.removeObject(this);
		notifyPlayersLogout(world.getRecognizePlayer(this));
		_inventory.clearItems();

		WarehouseManager w = WarehouseManager.getInstance();
		w.delSpecialWarehouse(this.getName()); // 특수창고
		w.delPrivateWarehouse(this.getAccountName());
		w.delElfWarehouse(this.getAccountName());
		w.delPackageWarehouse(this.getAccountName());

		getNearObjects().removeAllKnownObjects();
		stopHalloweenRegeneration();
		stopAHRegeneration();
		stopHpRegenerationByDoll();
		stopMpRegenerationByDoll();
		stopSHRegeneration();
		stopMpDecreaseByScales();
		stopEquipmentTimer();
		stopEtcMonitor();
		setDead(true);
	//	eva.updataUserCount();//매니져아이디삭제
	//	eva.updataUserList();//매니져아이디삭제
	}

	public LineageClient getNetConnection() {
		return _netConnection;
	}

	public void setNetConnection(LineageClient clientthread) {
		_netConnection = clientthread;
	}

	public boolean isInParty() {
		return getParty() != null;
	}

	public L1Party getParty() {
		return _party;
	}

	public void setParty(L1Party p) {
		_party = p;
	}

	public boolean isInChatParty() {
		return getChatParty() != null;
	}

	public L1ChatParty getChatParty() {
		return _chatParty;
	}

	public void setChatParty(L1ChatParty cp) {
		_chatParty = cp;
	}

	public int getPartyID() {
		return _partyID;
	}

	public void setPartyID(int partyID) {
		_partyID = partyID;
	}

	public int getPartyType() {
		return _partyType;
	}

	public void setPartyType(int partyType) {
		_partyType = partyType;
	}

	public int getTradeID() {
		return _tradeID;
	}

	public void setTradeID(int tradeID) {
		_tradeID = tradeID;
	}

	public void setTradeOk(boolean tradeOk) {
		_tradeOk = tradeOk;
	}

	public boolean getTradeOk() {
		return _tradeOk;
	}

	public int getTempID() {
		return _tempID;
	}

	public void setTempID(int tempID) {
		_tempID = tempID;
	}

	public boolean isTeleport() {
		return _isTeleport;
	}

	public void setTeleport(boolean flag) {
		_isTeleport = flag;
	}

	public boolean isDrink() {
		return _isDrink;
	}

	public void setDrink(boolean flag) {
		_isDrink = flag;
	}

	public boolean isGres() {
		return _isGres;
	}

	public void setGres(boolean flag) {
		_isGres = flag;
	}

	public boolean isPinkName() {
		return _isPinkName;
	}

	public void setPinkName(boolean flag) {
		_isPinkName = flag;
	}




	
	public FastTable<L1PrivateShopSellList> getSellList() {
		return _sellList;
	}

	public FastTable<L1PrivateShopBuyList> getBuyList() {
		return _buyList;
	}

	public void addSellList(L1PrivateShopSellList ad) {
		_sellList.add(ad);
	}

	public void addBuyList(L1PrivateShopBuyList ad) {
		_buyList.add(ad);
	}

	public void setSellList(int i, L1PrivateShopSellList ad) {
		_sellList.set(i, ad);
	}

	public void setBuyList(int i, L1PrivateShopBuyList ad) {
		_buyList.set(i, ad);
	}

	public L1PrivateShopSellList getSell(int i) {
		return _sellList.get(i);
	}

	public L1PrivateShopBuyList getBuy(int i) {
		return _buyList.get(i);
	}

	public void setShopChat(byte[] chat) {
		_shopChat = chat;
	}

	public byte[] getShopChat() {
		return _shopChat;
	}

	public boolean isPrivateShop() {
		return _isPrivateShop;
	}

	public void setPrivateShop(boolean flag) {
		_isPrivateShop = flag;
	}
	private int _special_size; // 특수창고

	public int get_SpecialSize() {
		return _special_size;
	} // 특수창고

	public void set_SpecialSize(int special_size) {
		_special_size = special_size;
	} // 특수창고

	public boolean isTradingInPrivateShop() {
		return _isTradingInPrivateShop;
	}

	public void setTradingInPrivateShop(boolean flag) {
		_isTradingInPrivateShop = flag;
	}

	public int getPartnersPrivateShopItemCount() {
		return _partnersPrivateShopItemCount;
	}

	public void setPartnersPrivateShopItemCount(int i) {
		_partnersPrivateShopItemCount = i;
	}

	public void sendPackets(ServerBasePacket serverbasepacket) {
		if (getNetConnection() == null) {
			return;
		}

		try {
			getNetConnection().sendPacket(serverbasepacket);
		} catch (Exception e) {
		}
	}

    public boolean TRIPLE = false;

	public boolean FouSlayer = false;
	
		/*private void teleport(){
		if (getMap().isTeleportable()) {
			L1Location newLocation = getLocation().randomLocation(200, true);
			int newX = newLocation.getX();
			int newY = newLocation.getY();
			short mapId = (short) newLocation.getMapId();
			L1Teleport.teleport(this, newX, newY, mapId, 5, true);
		}
	}*/
	
	@Override
	public void onAction(L1PcInstance attacker) {
		/** 2011.11.13 SOCOOL 인공지능 */
		
		if (getRobotAi() != null && (noPlayerCK || isGm())) {
			if (attacker != null && getClanid() != 0 && !getMap().isSafetyZone(getLocation())) {				
				if (!getRobotAi().getAttackList().containsKey(attacker) && getClanid() != attacker.getClanid()) {
					getRobotAi().getAttackList().add(attacker,0);
				}
			} else if (!getMap().isSafetyZone(getLocation())) {
				if (getMap().isTeleportable()) {
					L1Location newLocation = getLocation().randomLocation(200, true);
					int newX = newLocation.getX();
					int newY = newLocation.getY();
					short mapId = (short) newLocation.getMapId();

					L1Teleport.teleport(this, newX, newY, mapId, getMoveState().getHeading(), true);
				}
			}
		}
		if (attacker == null) {
			return;
		}
				/**매입상점 */
		if (isAutoshop()) {
			try {
				L1PcInstance target = FaceToFace.faceToFace(this);
				if (target != null) {
					if (!target.isParalyzed()) {
						target.setTradeID(getId());
						target.sendPackets(new S_Message_YN(252, getName()));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**매입상점 */
		
		if (isTeleport()) {
			return;
		}
		if (TRIPLE) {
			if (65 > random2.nextInt(100) + 1) {
				L1Attack attack_mortion = new L1Attack(attacker, this);
				attack_mortion._isHit = false;
				attack_mortion.action();
				TRIPLE = false;
				return;
			}
			TRIPLE = false;
		} else if (FouSlayer) {
			if (50 < random2.nextInt(100) + 1) {
				L1Attack attack_mortion = new L1Attack(attacker, this);
				attack_mortion._isHit = false;
				attack_mortion.action();
				FouSlayer = false;
				return;
			}
		}
		if (CharPosUtil.getZoneType(this) == 1
				|| CharPosUtil.getZoneType(attacker) == 1) {
			L1Attack attack_mortion = new L1Attack(attacker, this);
			attack_mortion.action();
			return;
		}

		if (checkNonPvP(this, attacker) == true) {
			L1Attack attack_mortion = new L1Attack(attacker, this);
			attack_mortion.action();
			return;
		}

		if (getCurrentHp() > 0 && !isDead()) {
			attacker.delInvis();

			boolean isCounterBarrier = false;
			boolean isMortalBody = false;
			L1Attack attack = new L1Attack(attacker, this);
			L1Magic magic = null;

			if (attack.calcHit()) {
				Random random = new Random();
				   int MuChanc = random.nextInt(100) +1;  // 추가
				if (getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.COUNTER_BARRIER)) {
					magic = new L1Magic(this, attacker);
					boolean isProbability = magic
							.calcProbabilityMagic(L1SkillId.COUNTER_BARRIER);
					boolean isShortDistance = attack.isShortDistance();
					if (isProbability && isShortDistance) {
						isCounterBarrier = true;
					}
				} else if (getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.MORTAL_BODY)) {
					boolean isShortDistance = attack.isShortDistance();
					if (10 >= MuChanc && isShortDistance) {
						isMortalBody = true;
					}
				}
				if (!isCounterBarrier && !isMortalBody) {
					attacker.setPetTarget(this);
					attack.calcDamage();
					attack.addPcPoisonAttack(attacker, this);
				}
			}
			if (isCounterBarrier) {
				attack.actionCounterBarrier();
				attack.commitCounterBarrier();
				/** 맞는 본인도 피달게 수정 * */
				attack.commit();
			} else if (isMortalBody) {
				attack.actionMortalBody();
				attack.commitMortalBody();
				attack.commit();
			} else {
				attack.action();
				attack.commit();
			}
		}
		if (FouSlayer)
			FouSlayer = false;
		if(noPlayerCK){
			if(isDead())
				return;
			if (CharPosUtil.getZoneType(this) == 1
					|| CharPosUtil.getZoneType(attacker) == 1) {
				
			}else{
				if(addRobotTargetlist(attacker)){
				/**혈맹요청**/
					if(getClanid() <= 0) return;
					for(L1PcInstance temppc : L1World.getInstance().getVisiblePlayer(this)){
						if(temppc == null || !temppc.noPlayerCK) continue;
						if(getClanid() == temppc.getClanid())
							temppc.addRobotTargetlist(attacker);
					}
					/**혈맹요청**/
				}
			}
		}
	}

	public boolean checkNonPvP(L1PcInstance pc, L1Character target) {
		L1PcInstance targetpc = null;

		if (target instanceof L1PcInstance)
			targetpc = (L1PcInstance) target;
		else if (target instanceof L1PetInstance)
			targetpc = (L1PcInstance) ((L1PetInstance) target).getMaster();
		else if (target instanceof L1SummonInstance)
			targetpc = (L1PcInstance) ((L1SummonInstance) target).getMaster();

		if (targetpc == null)
			return false;

		if (!Config.ALT_NONPVP) {
			if (getMap().isCombatZone(getLocation()))
				return false;
			List<L1War> _wl = null;
			_wl = L1World.getInstance().getWarList();
			for (L1War war : _wl) {
				if (war == null)
					continue;
				if (pc.getClanid() != 0 && targetpc.getClanid() != 0) {
					boolean same_war = war.CheckClanInSameWar(pc.getClanname(),
							targetpc.getClanname());

					if (same_war == true)
						return false;
				}
			}

			if (target instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) target;
				if (isInWarAreaAndWarTime(pc, targetPc))
					return false;
			}
			return true;
		}

		return false;
	}

	private boolean isInWarAreaAndWarTime(L1PcInstance pc, L1PcInstance target) {
		int castleId = L1CastleLocation.getCastleIdByArea(pc);
		int targetCastleId = L1CastleLocation.getCastleIdByArea(target);
		if (castleId != 0 && targetCastleId != 0 && castleId == targetCastleId) {
			if (WarTimeController.getInstance().isNowWar(castleId)) {
				return true;
			}
		}
		return false;
	}

	public void setPetTarget(L1Character target) {
		Object[] petList = getPetList().values().toArray();
		L1PetInstance pets = null;
		L1SummonInstance summon = null;
		for (Object pet : petList) {
			if (pet == null)
				continue;
			if (pet instanceof L1PetInstance) {
				pets = (L1PetInstance) pet;
				pets.setMasterTarget(target);
			} else if (pet instanceof L1SummonInstance) {
				summon = (L1SummonInstance) pet;
				summon.setMasterTarget(target);
			}
		}
	}

	public void delInvis() {
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INVISIBILITY)) {
			getSkillEffectTimerSet().killSkillEffectTimer(
					L1SkillId.INVISIBILITY);
			sendPackets(new S_Invis(getId(), 0));
			Broadcaster.broadcastPacket(this, new S_Invis(getId(), 0));
			// broadcastPacket(new S_OtherCharPacks(this));
		}
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLIND_HIDING)) {
			getSkillEffectTimerSet().killSkillEffectTimer(
					L1SkillId.BLIND_HIDING);
			sendPackets(new S_Invis(getId(), 0));
			Broadcaster.broadcastPacket(this, new S_Invis(getId(), 0));
			// broadcastPacket(new S_OtherCharPacks(this));
		}
	}

	public void delBlindHiding() {
		getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.BLIND_HIDING);
		sendPackets(new S_Invis(getId(), 0));
		Broadcaster.broadcastPacket(this, new S_Invis(getId(), 0));
		// broadcastPacket(new S_OtherCharPacks(this));
	}

	public void receiveDamage(L1Character attacker, int damage, int attr) {
		if (damage == 0)
			return;
		Random random = new Random(System.nanoTime());
		int player_mr = getResistance().getEffectedMrBySkill();
		int rnd = random.nextInt(100) + 1;
		if (player_mr >= rnd) {
			damage *= 0.5;
		}
		receiveDamage(attacker, damage, false);
	}

	public void receiveManaDamage(L1Character attacker, int mpDamage) {
		if (mpDamage > 0 && !isDead()) {
			delInvis();
			if (attacker instanceof L1PcInstance) {
				L1PinkName.onAction(this, attacker);
			}

			int newMp = getCurrentMp() - mpDamage;
			this.setCurrentMp(newMp);
		}
	}

	public long _oldTime = 0;

	public synchronized void receiveDamage(L1Character attacker, double damage,
			boolean isMagicDamage) {
		if (getCurrentHp() > 0 && !isDead() && !isGhost()) {
			if (attacker != this && !getNearObjects().knownsObject(attacker)
					&& attacker.getMapId() == this.getMapId()) {
				attacker.onPerceive(this);
			}
			if (isMagicDamage == true) {

				if (damage <= 0) {
				}
			}
			if (damage > 0) {
				delInvis();
				if (attacker instanceof L1PcInstance) {
					L1PinkName.onAction(this, attacker);
				}
				if (getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.FOG_OF_SLEEPING)) {
					getSkillEffectTimerSet().removeSkillEffect(
							L1SkillId.FOG_OF_SLEEPING);
				} else if (getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.PHANTASM)) {
					getSkillEffectTimerSet().removeSkillEffect(
							L1SkillId.PHANTASM);
				}
			}
			if (getInventory().checkEquipped(145)
					|| getInventory().checkEquipped(149)) {
				damage *= 1.5;
			}
			int newHp = getCurrentHp() - (int) (damage);
			 /** 파푸리온의 가호 영역 **/
			   Random random = new Random();
			   int chance = random.nextInt(100)+1;
			   int plus_hp = 70+random.nextInt(20);
			   if(getInventory().checkEquipped(20153) || getInventory().checkEquipped(420107)
			     ||getInventory().checkEquipped(420106) || getInventory().checkEquipped(420105)
			     || getInventory().checkEquipped(420104)){
			    if(chance <= 10 && (getCurrentHp() != getMaxHp())){
			     if(getSkillEffectTimerSet().hasSkillEffect(L1SkillId.WATER_LIFE)){
			      plus_hp*=2;
			     }
			     if(getSkillEffectTimerSet().hasSkillEffect(L1SkillId.POLLUTE_WATER)){
			      plus_hp/=2;
			     }  
			     healHp(plus_hp);
			     sendPackets(new S_SkillSound(getId(), 2187));
			     Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 2187));
			    }
			   }
			   /** 파푸리온의 가호 영역 **/
			if (newHp > getMaxHp()) {
				newHp = getMaxHp();
			}
			if (newHp <= 0) {
				if (isGm()) {
					this.setCurrentHp(getMaxHp());
				} else {
					if (isDeathMatch()) {
						/*if (getMapId() == 5153) {
							try {
								this.setCurrentHp(getMaxHp());
								save();
								beginGhost(getX(), getY(), (short) getMapId(),
										true);
								sendPackets(new S_ServerMessage(1271));
							} catch (Exception e) {
								_log.log(Level.SEVERE, e.getLocalizedMessage(),
										e);
							}
							return;
						}*/
					} else {
						// ** pvp시 전체 멘트 밑 매너필드 추가 **//
						if (attacker instanceof L1PcInstance) {
							if (CharPosUtil.getZoneType(L1PcInstance.this) == 0) // 노멀존일
								// 경우
								if (getLevel() >= 50) { // 10랩 이상이라면..[ 승작업할것같아서.. ]
									attacker.setKills(attacker.getKills() + 1); // 이긴넘킬수1
									setDeaths(getDeaths() + 1); // 진넘 데스수 +1
								}
							int price = getHuntPrice();
							if (CharPosUtil.getZoneType(L1PcInstance.this) == 0) // 노멀존일
								// 경우
								if (getHuntCount() > 0) {
									attacker.getInventory().storeItem(40308,price);
									setHuntCount(0);
									setHuntPrice(0);
									setReasonToHunt(null);
									sendMessage("\\fU"+attacker.getName() + "님이 " + this.getName() + "님을 죽여 현상금을 받았습니다.");
								//	sendMessage("W("+ attacker.getName()+ ").... L(" + getName()+ "')의 현상금을 획득....");
									try {
										save();
									} catch (Exception e) {
										_log.log(Level.SEVERE, "L1PcInstance[]Error", e);
									}
								} else {

									if (CharPosUtil.getZoneType(this) == 0
											&& CharPosUtil
													.getZoneType(attacker) == 0) {
										L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fY"+attacker.getName()+ ": 제가 '" +this.getName()+ "'님을 죽였습니다."));  
										//sendMessage("W("+ attacker.getName()+ ").... L("+ this.getName()+ ")의 목을 베다....");
									}
								}
						}

						// ** pvp시 전체 멘트 밑 매너필드 추가 **//
						death(attacker);
						/**배틀존**/
						if(getMapId() == 5153 && attacker.getMapId() == 5153){
				    		 if(get_DuelLine() == 1){
				    			 BattleZone.getInstance().MemberCount1 -= 1;
				    		 }else if(get_DuelLine() == 2){
				    			 BattleZone.getInstance().MemberCount2 -= 1;
				    		 }
				    		//L1World.getInstance().broadcastPacketToAll(
							//   new S_SystemMessage("\\fY배틀존 필드 현황 : "+ attacker.getName() + "\\fY님이 " + "\\fY"+ getName() + "\\fY을 죽임")); 
				    		//L1World.getInstance().broadcastPacketToAll(
							//		   new S_SystemMessage("\\fY배틀존 인원 현황 : [블랙팀"+BattleZone.getInstance().MemberCount1+"\\fY명] vs [실버팀"+BattleZone.getInstance().MemberCount2+"\\fY명]")); 
				    		 return;
				    	   }
						/**배틀존**/
					}
				}
			}
			if (newHp > 0) {
				this.setCurrentHp(newHp);
			}
		} else if (!isDead()) {
			System.out
					.println("[L1PcInstance] 경고：플레이어의 HP감소 처리가 올바르게 행해지지 않은 개소가 있습니다.※혹은 최초부터 HP0");
			death(attacker);
		}
	}

	public void death(L1Character lastAttacker) {
		synchronized (this) {
			if (isDead()) {
				return;
			}
			setDead(true);
			setActionStatus(ActionCodes.ACTION_Die);
		}
		_threadPool.execute(new Death(lastAttacker));

	}
	private class Death implements Runnable {
		L1Character _lastAttacker;
		Death(L1Character cha) {
			_lastAttacker = cha;
		}
		public void run() {
			L1Character lastAttacker = _lastAttacker;
			_lastAttacker = null;
			setCurrentHp(0);
			setGresValid(false);

			while (isTeleport()) {
				try {
					Thread.sleep(300);
				} catch (Exception e) {
				}
			}
				if(isInParty()){//파티추가
					 getParty().memberDie(L1PcInstance.this);
				}
			int targetobjid = getId();
			getMap().setPassable(getLocation(), true);

			int tempchargfx = 0;
			if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
				tempchargfx = getGfxId().getTempCharGfx();
				setTempCharGfxAtDead(tempchargfx);
			} else {
				setTempCharGfxAtDead(getClassId());
			}
			L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(L1PcInstance.this,
					L1SkillId.CANCELLATION, getId(), getX(), getY(), null, 0,
					L1SkillUse.TYPE_LOGIN);

			if (tempchargfx == 5727 || tempchargfx == 5730
					|| tempchargfx == 5733 || tempchargfx == 5736) {
				tempchargfx = 0;
			}
			if (tempchargfx != 0) {
				sendPackets(new S_ChangeShape(getId(), tempchargfx));
				Broadcaster.broadcastPacket(L1PcInstance.this,
						new S_ChangeShape(getId(), tempchargfx));
			} else {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
			sendPackets(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
			Broadcaster.broadcastPacket(L1PcInstance.this, new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
			if (lastAttacker != L1PcInstance.this) {// 세이프티 존, 컴배트 존에서 마지막에 죽인 캐릭터가 // 플레이어 or펫이라면, 패널티 없음
				if (CharPosUtil.getZoneType(L1PcInstance.this) != 0) {
					L1PcInstance player = null;
					if (lastAttacker instanceof L1PcInstance) {
						player = (L1PcInstance) lastAttacker;
					} else if (lastAttacker instanceof L1PetInstance) {
						player = (L1PcInstance) ((L1PetInstance) lastAttacker)
								.getMaster();
					} else if (lastAttacker instanceof L1SummonInstance) {
						player = (L1PcInstance) ((L1SummonInstance) lastAttacker)
								.getMaster();
					}
					if (player != null) {
						if (!isInWarAreaAndWarTime(L1PcInstance.this, player)) {
							return;
						}
					}
				}

				boolean sim_ret = simWarResult(lastAttacker);
				if (sim_ret == true) {
					return;
				}
			}

			if (!getMap().isEnabledDeathPenalty()) {
				return;
			}

			L1PcInstance fightPc = null;
			if (lastAttacker instanceof L1PcInstance) {
				fightPc = (L1PcInstance) lastAttacker;
			}
			if (fightPc != null) {
				if (getFightId() == fightPc.getId()
						&& fightPc.getFightId() == getId()) { // 결투중
					setFightId(0);
					sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
					fightPc.setFightId(0);
					fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL,
							0, 0));
					return;
				}
			}
				// 60레벨 이하일때 10렙높은 유저에게 죽임을 당하면 패널티 없음.
				if (lastAttacker instanceof L1PcInstance && getLevel() <= 50
						&& (lastAttacker.getLevel() - getLevel()) >= 10) {
					sendPackets(new S_SystemMessage("조우의 가호의 보호를 받습니다."));
						return;
				}
				// ////////////////////////바포깃발
				deathPenalty();
			setGresValid(true);

			if (getExpRes() == 0) {///조우의 가호로 경험치 회복
			    if (lastAttacker instanceof L1PcInstance && getLevel() < Config.MAX_LEVEL && (lastAttacker.getLevel() - getLevel()) >= 10) {
			    }else{         
			     setExpRes(1);
			    }
			   }
			if (lastAttacker instanceof L1GuardInstance) {//경비가사람죽이면피케이횟수다운
				if (get_PKcount() > 0) {
					set_PKcount(get_PKcount() - 1);
				}
				setLastPk(null);
			}
			/**사망시 템&마법 드랍확률. 바포시스템화. */
			int lostRate = (int) (((getLawful() + 32768D) / 1000D - 65D) * 6D);

			if (lostRate < 0) {
				lostRate *= -1;
				if (getLawful() < 0) {
					lostRate *= 4;
				}
				Random random = new Random();
				int rnd = random.nextInt(1000) + 1;

				if (rnd <= lostRate) {
					int count = 1;
					int skillcount = 1;

					if (getLawful() <= -20000) {
						count = random.nextInt(4) + 1;
						skillcount = random.nextInt(4) + 1;
					} else if (getLawful() <= -10000) {
						count = random.nextInt(3) + 1;
						skillcount = random.nextInt(3) + 1;
					} else if (getLawful() <= -5000) {
						count = random.nextInt(2) + 1;
						skillcount = random.nextInt(2) + 1;
					} else if (getLawful() < 0) {
						count = random.nextInt(1) + 1;
						skillcount = 0;
					}
					caoPenaltyResult(count);
					caoPenaltySkill(skillcount);
				}
			}
			/**사망시 템&마법 드랍확률. 바포시스템화 */
			boolean castle_ret = castleWarResult(); // 공성전
			if (castle_ret == true) { // 공성 전시중에 기내라면 빨강 네임 패널티 없음
				return;
			}

			L1PcInstance player = null;
			if (lastAttacker instanceof L1PcInstance) {
				player = (L1PcInstance) lastAttacker;
			}
			/**라우풀 .바포시스템적용.*/
			if (player != null) {
			//		if (player.getLawful() < 30000) {
				//		player.set_PKcount(player.get_PKcount() + 1);
						player.setLastPk();
			//		}
					
					// 아라이먼트 처리
					// 공식의 발표 및 각 LV에서의 PK로부터 사리가 맞도록(듯이) 변경
					// (PK측의 LV에 의존해, 고LV(정도)만큼 리스크도 높다)
					// 48당으로―8 k(정도)만큼 DK의 시점에서 10 k강
					// 60으로 약 20 k강 65로 30 k미만
					int lawful;
					if( isPinkName() == true){
						lawful = player.getLawful() - (player.getLevel() * 60) ;	
						setPinkName(false);
					}else if (getLawful() >= 0){
					lawful = player.getLawful() - (player.getLevel() * 120) ;	
					}else{
						lawful = player.getLawful() - (player.getLevel() *  10) ;		
					}
					
					// 만약(원래의 아라이먼트 1000)이 계산 후보다 낮은 경우
					// 원래의 아라이먼트 1000을 아라이먼트치로 한다
					// (연속으로 PK 했을 때에 거의 값이 변함없었던 기억보다)
					// 이것은 위의 식보다 자신도가 낮은 어설픈 기억이므로
					// 분명하게 이러하면 않다!그렇다고 하는 경우는 수정 부탁합니다
			

					if (lawful <= -32767) {
						lawful = -32767;
					}
					if (lawful >= 32767) {
						lawful = 32767;
					}
					player.setLawful(lawful);

					S_Lawful s_lawful = new S_Lawful(player.getId(), player.getLawful());
					player.sendPackets(s_lawful);
					Broadcaster.broadcastPacket(player, s_lawful);
					if (isPinkName()){
						getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_PINK_NAME);
						setPinkName(false);
				}
			}
			/**라우풀.바포시스템적용.*/
		}
	}

	private void caoPenaltyResult(int count) {
		if (getAccessLevel() == Config.GMCODE) {
			return;
		}
		if (noPlayerCK) {
			drop();
			return;
		}
		for (int i = 0; i < count; i++) {
			L1ItemInstance item = getInventory().CaoPenalty();
			if (item != null) {
				if (item.getBless() > 3) {
					getInventory().removeItem(item,
							item.isStackable() ? item.getCount() : 1);
					sendPackets(new S_ServerMessage(158, item.getLogName())); // \f1%0%s
					// 증발되어
					// 사라집니다.
				} else {
					getInventory().tradeItem(
							item,
							item.isStackable() ? item.getCount() : 1,
							L1World.getInstance().getInventory(getX(), getY(),
									getMapId()));
					sendPackets(new S_ServerMessage(638, item.getLogName())); // %0를
					// 잃었습니다

				}
			}
		}
	}
	/**로봇템드랍**/
	int[] dropxy = { -1, 0, 1 };
	int[] itemid = {40010, 40015, 40013, 41245, 40507, 40126, 20085, 20056, 20011, 40056, 40318, 40007, 438000
			,40019,40020,40016,40012,40017,40044,40045,40046,40047,40861,40866,40870,40871,40872
			,40879,40884,40889,41284,41292,41279,140100,436013};
	private void drop(){
		int ran = dropxy[random2.nextInt(dropxy.length)];
		int count = 1;
		L1ItemInstance item = ItemTable.getInstance().createItem(itemid[random2.nextInt(itemid.length)]);
		if(item == null)
			return;
		item.setEnchantLevel(0);
		if(item.getItem().getType2() == 0 && item.getItem().getItemId() != 40007)
			count = random2.nextInt(10)+1;
		item.setCount(count);
		L1World.getInstance().getInventory(getX()+ran, getY()+ran, getMapId()).storeItem(item);
	}
	/**로봇템드랍**/
	private void caoPenaltySkill(int count) {
		int l = 0;
		int lv1 = 0;
		int lv2 = 0;
		int lv3 = 0;
		int lv4 = 0;
		int lv5 = 0;
		int lv6 = 0;
		int lv7 = 0;
		int lv8 = 0;
		int lv9 = 0;
		int lv10 = 0;
		Random random = new Random();
		int lostskilll = 0;
		for (int i = 0; i < count; i++) {
			if (isCrown()) {
				lostskilll = random.nextInt(16) + 1;
			} else if (isKnight()) {
				lostskilll = random.nextInt(8) + 1;
			} else if (isElf()) {
				lostskilll = random.nextInt(48) + 1;
			} else if (isDarkelf()) {
				lostskilll = random.nextInt(23) + 1;
			} else if (isWizard()) {
				lostskilll = random.nextInt(80) + 1;
			}

			if (!SkillsTable.getInstance().spellCheck(getId(), lostskilll)) {
				return;
			}

			L1Skills l1skills = null;
			l1skills = SkillsTable.getInstance().getTemplate(lostskilll);
			if (l1skills.getSkillLevel() == 1) {
				lv1 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 2) {
				lv2 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 3) {
				lv3 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 4) {
				lv4 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 5) {
				lv5 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 6) {
				lv6 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 7) {
				lv7 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 8) {
				lv8 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 9) {
				lv9 |= l1skills.getId();
			}
			if (l1skills.getSkillLevel() == 10) {
				lv10 |= l1skills.getId();
			}

			SkillsTable.getInstance().spellLost(getId(), lostskilll);
			l = lv1 + lv2 + lv3 + lv4 + lv5 + lv6 + lv7 + lv8 + lv9 + lv10;
		}
		if (l > 0) {
			sendPackets(new S_DelSkill(lv1, lv2, lv3, lv4, lv5, lv6, lv7, lv8,
					lv9, lv10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0));
		}
	}

	public boolean castleWarResult() {
		if (getClanid() != 0 && isCrown()) {
			L1Clan clan = L1World.getInstance().getClan(getClanname());
			List<L1War> _wl = null;
			_wl = L1World.getInstance().getWarList();
			for (L1War war : _wl) {
				if (war == null)
					continue;
				int warType = war.GetWarType();
				boolean isInWar = war.CheckClanInWar(getClanname());
				boolean isAttackClan = war.CheckAttackClan(getClanname());
				if (getId() == clan.getLeaderId() && warType == 1 && isInWar
						&& isAttackClan) {
					String enemyClanName = war.GetEnemyClanName(getClanname());
					if (enemyClanName != null) {
						if (war.GetWarType() == 1) {// 공성전일경우
							L1PcInstance clan_member[] = clan
									.getOnlineClanMember();//
							int castle_id = war.GetCastleId();
							int[] loc = new int[3];
							loc = L1CastleLocation.getGetBackLoc(castle_id);
							int locx = loc[0];
							int locy = loc[1];
							short mapid = (short) loc[2];
							for (int k = 0; k < clan_member.length; k++) {
								if (L1CastleLocation.checkInWarArea(castle_id,
										clan_member[k])) {// 기내에 있는 혈원 강제 텔레포트
									L1Teleport.teleport(clan_member[k], locx,
											locy, mapid, 5, true);
								}
							}
						}
						war.CeaseWar(getClanname(), enemyClanName); // 종결
					}
					break;
				}
			}
		}

		int castleId = 0;
		boolean isNowWar = false;
		castleId = L1CastleLocation.getCastleIdByArea(this);
		if (castleId != 0) {
			isNowWar = WarTimeController.getInstance().isNowWar(castleId);
		}
		return isNowWar;
	}

	public boolean simWarResult(L1Character lastAttacker) { //죽었을때 선포풀리는부분인듯
		//이부분에 pc가 현재 스위치가 true라면 return false 해줘야될듯하고
		if(getCastleIn() == true){
			return false;
		}
		if (getClanid() == 0) {
			return false;
		}
		if (Config.SIM_WAR_PENALTY) {
			return false;
		}
		L1PcInstance attacker = null;
		String enemyClanName = null;
		boolean sameWar = false;

		if (lastAttacker instanceof L1PcInstance) {
			attacker = (L1PcInstance) lastAttacker;
		} else if (lastAttacker instanceof L1PetInstance) {
			attacker = (L1PcInstance) ((L1PetInstance) lastAttacker)
					.getMaster();
		} else if (lastAttacker instanceof L1SummonInstance) {
			attacker = (L1PcInstance) ((L1SummonInstance) lastAttacker)
					.getMaster();
		} else {
			return false;
		}
		L1Clan clan = null;
		List<L1War> _wl = null;
		_wl = L1World.getInstance().getWarList();
		for (L1War war : _wl) {
			if (war == null)
				continue;
			clan = L1World.getInstance().getClan(getClanname());

			int warType = war.GetWarType();
			boolean isInWar = war.CheckClanInWar(getClanname());
			if (attacker != null && attacker.getClanid() != 0) {
				sameWar = war.CheckClanInSameWar(getClanname(), attacker
						.getClanname());
			}

			if (getId() == clan.getLeaderId() && warType == 2
					&& isInWar == true) {
				enemyClanName = war.GetEnemyClanName(getClanname());
				if (enemyClanName != null) {
					war.CeaseWar(getClanname(), enemyClanName);
				}
			}

			if (warType == 2 && sameWar) {
				return true;
			}
		}
		return false;
	}

	public void resExp() {
		int oldLevel = getLevel();
		int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;
		double ratio;

		if (oldLevel < 45)
			ratio = 0.05;
		else if (oldLevel >= 49)
			ratio = 0.025;
		else
			ratio = 0.05 - (oldLevel - 44) * 0.005;

		exp = (int) (needExp * ratio);

		if (exp == 0)
			return;

		addExp(exp);
	}

	public void resExpToTemple() {
		int oldLevel = getLevel();
		int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;
		double ratio;

		if (oldLevel < 45)
			ratio = 0.05;
		else if (oldLevel >= 45 && oldLevel < 49)
			ratio = 0.05 - (oldLevel - 44) * 0.005;
		else if (oldLevel >= 49 && oldLevel < 52)
			ratio = 0.025;
		else if (oldLevel == 52)
			ratio = 0.026;
		else if (oldLevel > 52 && oldLevel < 74)
			ratio = 0.026 + (oldLevel - 52) * 0.001;
		else if (oldLevel >= 74 && oldLevel < 79)
			ratio = 0.048 - (oldLevel - 73) * 0.0005;
		else
			/* if (oldLevel >= 79) */ratio = 0.0279; // 79렙부터
		// 2.79%복구

		exp = (int) (needExp * ratio);
		if (exp == 0)
			return;

		addExp(exp);
	}
	 /** 3.80 물개(언노운 오류 임시) **/
    private int _clanMemberId; // 血盟成員Id
    public int getClanMemberId() {
     return _clanMemberId;
    }
    public void setClanMemberId(int i) {
     _clanMemberId = i;
    }

	public void deathPenalty() {
		int oldLevel = getLevel();
		int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;

		if (oldLevel >= 1 && oldLevel < 11)
			exp = 0;
		else if (oldLevel >= 11 && oldLevel < 45)
			exp = (int) (needExp * 0.1);
		else if (oldLevel == 45)
			exp = (int) (needExp * 0.09);
		else if (oldLevel == 46)
			exp = (int) (needExp * 0.08);
		else if (oldLevel == 47)
			exp = (int) (needExp * 0.07);
		else if (oldLevel == 48)
			exp = (int) (needExp * 0.06);
		else if (oldLevel >= 49)
			exp = (int) (needExp * 0.05);

		if (exp == 0)
			return;

		addExp(-exp);
	}

	public int getEr() {
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STRIKER_GALE)) {//게일
			return 0;
		}

		int er = 0;
		if (isKnight()) {
			er = getLevel() / 4;
		} else if (isCrown() || isElf()) {
			er = getLevel() / 8;
		} else if (isDragonknight()) {
			er = getLevel() / 5;
		} else if (isDarkelf()) {
			er = getLevel() / 6;
		} else if (isIllusionist()) {
			er = getLevel() / 7;
		} else if (isWizard()) {
			er = getLevel() / 10;
		}

		er += (getAbility().getTotalDex() - 8) / 2;

		int BaseEr = CalcStat.calcBaseEr(getType(), getAbility().getBaseDex());

		er += BaseEr;

		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRESS_EVASION)) {
			er += 12;
		}
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SOLID_CARRIAGE)) {
			er += 15;
		}
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MIRROR_IMAGE)) {
			er += 20;
		}

		return er;
	}

	public L1BookMark getBookMark(String name) {
		L1BookMark element = null;
		int size = _bookmarks.size();
		for (int i = 0; i < size; i++) {
			element = _bookmarks.get(i);
			if (element == null)
				continue;
			if (element.getName().equalsIgnoreCase(name)) {
				return element;
			}
		}
		return null;
	}

	public L1BookMark getBookMark(int id) {
		L1BookMark element = null;
		int size = _bookmarks.size();
		for (int i = 0; i < size; i++) {
			element = _bookmarks.get(i);
			if (element == null)
				continue;
			if (element.getId() == id) {
				return element;
			}
		}
		return null;
	}

	public int getBookMarkSize() {
		return _bookmarks.size();
	}

	public void addBookMark(L1BookMark book) {
		_bookmarks.add(book);
	}

	public void removeBookMark(L1BookMark book) {
		_bookmarks.remove(book);
	}

	public L1ItemInstance getWeapon() {
		return _weapon;
	}

	public void setWeapon(L1ItemInstance weapon) {
		_weapon = weapon;
	}

	public L1ItemInstance getArmor() {
		return _armor;
	}

	public void setArmor(L1ItemInstance armor) {
		_armor = armor;
	}

	public L1Quest getQuest() {
		return _quest;
	}

	public boolean isCrown() {
		return (getClassId() == CLASSID_PRINCE || getClassId() == CLASSID_PRINCESS);
	}

	public boolean isKnight() {
		return (getClassId() == CLASSID_KNIGHT_MALE || getClassId() == CLASSID_KNIGHT_FEMALE);
	}

	public boolean isElf() {
		return (getClassId() == CLASSID_ELF_MALE || getClassId() == CLASSID_ELF_FEMALE);
	}

	public boolean isWizard() {
		return (getClassId() == CLASSID_WIZARD_MALE || getClassId() == CLASSID_WIZARD_FEMALE);
	}

	public boolean isDarkelf() {
		return (getClassId() == CLASSID_DARKELF_MALE || getClassId() == CLASSID_DARKELF_FEMALE);
	}

	public boolean isDragonknight() {
		return (getClassId() == CLASSID_DRAGONKNIGHT_MALE || getClassId() == CLASSID_DRAGONKNIGHT_FEMALE);
	}

	public boolean isIllusionist() {
		return (getClassId() == CLASSID_ILLUSIONIST_MALE || getClassId() == CLASSID_ILLUSIONIST_FEMALE);
	}

	public String getAccountName() {
		return _accountName;
	}

	public void setAccountName(String s) {
		_accountName = s;
	}

	public short getBaseMaxHp() {
		return _baseMaxHp;
	}

	public void addBaseMaxHp(short i) {
		i += _baseMaxHp;
		if (i >= 32767) {
			i = 32767;
		} else if (i < 1) {
			i = 1;
		}
		addMaxHp(i - _baseMaxHp);
		_baseMaxHp = i;
	}

	public short getBaseMaxMp() {
		return _baseMaxMp;
	}

	public void addBaseMaxMp(short i) {
		i += _baseMaxMp;
		if (i >= 32767) {
			i = 32767;
		} else if (i < 0) {
			i = 0;
		}
		addMaxMp(i - _baseMaxMp);
		_baseMaxMp = i;
	}

	// ///////////////////////////////////스텟버그
	public void setBonusStats(int i) {
		_bonusStats = i;
	}



	// //////////////////////////////////스텟버그
	public int getBaseAc() {
		return _baseAc;
	}

	public int getBaseDmgup() {
		return _baseDmgup;
	}

	public int getBaseBowDmgup() {
		return _baseBowDmgup;
	}

	public int getBaseHitup() {
		return _baseHitup;
	}

	public int getBaseBowHitup() {
		return _baseBowHitup;
	}

	public void setBaseMagicHitUp(int i) {
		_baseMagicHitup = i;
	}

	public int getBaseMagicHitUp() {
		return _baseMagicHitup;
	}

	public void setBaseMagicCritical(int i) {
		_baseMagicCritical = i;
	}

	public int getBaseMagicCritical() {
		return _baseMagicCritical;
	}

	public void setBaseMagicDmg(int i) {
		_baseMagicDmg = i;
	}

	public int getBaseMagicDmg() {
		return _baseMagicDmg;
	}

	public void setBaseMagicDecreaseMp(int i) {
		_baseMagicDecreaseMp = i;
	}

	public int getBaseMagicDecreaseMp() {
		return _baseMagicDecreaseMp;
	}

	public int getAdvenHp() {
		return _advenHp;
	}

	public void setAdvenHp(int i) {
		_advenHp = i;
	}

	public int getAdvenMp() {
		return _advenMp;
	}

	public void setAdvenMp(int i) {
		_advenMp = i;
	}

	public int getHighLevel() {
		return _highLevel;
	}

	public void setHighLevel(int i) {
		_highLevel = i;
	}

	public int getElfAttr() {
		return _elfAttr;
	}

	public void setElfAttr(int i) {
		_elfAttr = i;
	}

	public int getExpRes() {
		return _expRes;
	}

	public void setExpRes(int i) {
		_expRes = i;
	}

	public int getPartnerId() {
		return _partnerId;
	}

	public void setPartnerId(int i) {
		_partnerId = i;
	}

	public int getOnlineStatus() {
		return _onlineStatus;
	}

	public void setOnlineStatus(int i) {
		_onlineStatus = i;
	}

	public int getHomeTownId() {
		return _homeTownId;
	}

	public void setHomeTownId(int i) {
		_homeTownId = i;
	}

	public int getContribution() {
		return _contribution;
	}

	public void setContribution(int i) {
		_contribution = i;
	}

	public int getHellTime() {
		return _hellTime;
	}

	public void setHellTime(int i) {
		_hellTime = i;
	}

	public boolean isBanned() {
		return _banned;
	}

	public void setBanned(boolean flag) {
		_banned = flag;
	}

	public int get_food() {
		return _food;
	}

	public void set_food(int i) {
		_food = i;
	}
	
	/** 회오리 tel 카운터 **/
	private int _teldelay;
	public int getTeldelay() {	return _teldelay;	}
	public void setTeldelay(int i) {	_teldelay = i;	}
	/** 회오리 tel 카운터 **/

	public L1EquipmentSlot getEquipSlot() {
		return _equipSlot;
	}

	public static L1PcInstance load(String charName) {
		L1PcInstance result = null;
		try {
			result = CharacterTable.getInstance().loadCharacter(charName);
		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1PcInstance[]Error2", e);
		}
		return result;
	}

	public void save() throws Exception {
		if (isGhost()) {
			return;
		}
		CharacterTable.getInstance().storeCharacter(this);
	}

	public void saveInventory() {
		if(noPlayerCK)
			return;
		List<L1ItemInstance> list = null;
		list = getInventory().getItems();
		for (L1ItemInstance item : list) {
			if (item != null)
				getInventory().saveItem(item, item.getRecordingColumns());
		}
	}

	public void setRegenState(int state) {
		setHpRegenState(state);
		setMpRegenState(state);
	}

	public void setHpRegenState(int state) {
		if (_HpcurPoint < state)
			return;

		this._HpcurPoint = state;
		// _mpRegen.setState(state);
		// _hpRegen.setState(state);
	}

	public void setMpRegenState(int state) {
		if (_MpcurPoint < state)
			return;

		this._MpcurPoint = state;
		// _mpRegen.setState(state);
		// _hpRegen.setState(state);
	}

	public double getMaxWeight() {
		int str = getAbility().getTotalStr();
		int con = getAbility().getTotalCon();
		int basestr = ability.getBaseStr();
		int basecon = ability.getBaseCon();
		int maxWeight = CalcStat.getMaxWeight(str, con);
		double plusWeight = 0;
		// 베이스스탯에 의한 수치(최대 무게수지 + 1 당 0.04)
		double baseWeight = CalcStat
				.calcBaseWeight(getType(), basestr, basecon);
		// 방어구에 의한 수치
		double armorWeight = getWeightReduction();
		// 인형에 의한 수치
		int dollWeight = 0;
		for (L1DollInstance doll : getDollList().values()) {
			if (doll != null)
				dollWeight = doll.getWeightReductionByDoll();
		}
		// 마법에 의한 수치
		int magicWeight = 0;
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DECREASE_WEIGHT)) {
			magicWeight = 3;
		}
		baseWeight = Math.ceil(maxWeight * (1 + (baseWeight * 0.04)))
				- maxWeight;
		plusWeight = Math.ceil(maxWeight
				* (1 + ((armorWeight + magicWeight + dollWeight) * 0.02)))
				- maxWeight;
		maxWeight += plusWeight + baseWeight;
		maxWeight *= Config.RATE_WEIGHT_LIMIT;

		return maxWeight;
	}

	public boolean isUgdraFruit() {
		return getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_FRUIT);
	}

	public boolean isFastMovable() {
		return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.HOLY_WALK)
				|| getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.MOVING_ACCELERATION) || getSkillEffectTimerSet()
				.hasSkillEffect(L1SkillId.WIND_WALK));
	}

	public boolean isBloodLust() {
		return getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLOOD_LUST);
	}

	public boolean isBrave() {
		return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_BRAVE));
	}
	public boolean isDragonPearl() {
		return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_DRAGONPERL));
	}
	public boolean isWindShackle() { //가속기 수정및 외부화
	    return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.WIND_SHACKLE));
    }
	public boolean isElfBrave() {
		return getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.STATUS_ELFBRAVE);
	}

	public boolean isHaste() {
		return (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_HASTE)
				|| getSkillEffectTimerSet().hasSkillEffect(L1SkillId.HASTE)
				|| getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.GREATER_HASTE) || getMoveState()
				.getMoveSpeed() == 1);
	}

	public boolean isInvisDelay() {
		return (invisDelayCounter > 0);
	}

	public void addInvisDelayCounter(int counter) {
		synchronized (_invisTimerMonitor) {
			invisDelayCounter += counter;
		}
	}

	public void beginInvisTimer() {
		addInvisDelayCounter(1);
		InvisDelayTime = System.currentTimeMillis() + 3000L;
		PcInvisDelayController.getInstance().addPc(this);
	}

	public synchronized void addExp(int exp) {//여기
		/** 2012.11.28 큐르 로긴후획득정보 **/
	//	   this.setInforEXP(this.getInforEXP() + exp);
		   /** **/
		_exp += exp;
		if (_exp > ExpTable.MAX_EXP) {
			_exp = ExpTable.MAX_EXP;
		}
	}

	public synchronized void addContribution(int contribution) {
		_contribution += contribution;
	}

	public void beginExpMonitor() {
		final long INTERVAL_EXP_MONITOR = 500;
		_expMonitorFuture = GeneralThreadPool.getInstance()
		.pcScheduleAtFixedRate(new L1PcExpMonitor(getId()), 0L,	INTERVAL_EXP_MONITOR);
	}
	private void levelUp(int gap) {
	//	if(noPlayerCK)
		//	return;
		resetLevel();
	//	sendPackets(new S_SkillSound(getId(), 8688));
		
		
		if(getLevel() > 50){
			   sendPackets(new S_SystemMessage("       * 현재  스텟을 확인 후 스텟을 선택해주세요 *    "));
			   sendPackets(new S_SystemMessage(" STR: " + getAbility().getStr() + " DEX:" + getAbility().getDex() + " CON:" + getAbility().getCon()+
			     " IMT:" + getAbility().getInt() + " WIS:" + getAbility().getWis() + " CHA:" + getAbility().getCha()));
			  }
		
		
		  if (getLevel() == Config.MAX_LEVEL  && getClanid() == 287720168) {   // 레벨 70 이 되는순간 신규혈맹일경우
			   setClanid(0);
				setTitle("");
			   try {
				 sendPackets(new S_SystemMessage("\\fW레벨달성신규혈맹에서자동탈퇴되었습니다."));
				 save();
			   } catch (Exception e) {
					_log.log(Level.SEVERE, "L1PcInstance[]Error3", e);
			   }
			   
			  }
		if (getLevel() == 99 && Config.ALT_REVIVAL_POTION) {
			try {
				L1Item l1item = ItemTable.getInstance().getTemplate(43000);
				if (l1item != null) {
					getInventory().storeItem(43000, 1);
					sendPackets(new S_ServerMessage(403, l1item.getName()));
				} else {
					sendPackets(new S_SystemMessage("환생의 물약 입수에 실패했습니다."));
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "L1PcInstance[]Error4", e);
				sendPackets(new S_SystemMessage("환생의 물약 입수에 실패했습니다."));
			}
		}

		for (int i = 0; i < gap; i++) {
			short randomHp = CalcStat.calcStatHp(getType(), getBaseMaxHp(),
					getAbility().getCon());
			short randomMp = CalcStat.calcStatMp(getType(), getBaseMaxMp(),
					getAbility().getWis());
			addBaseMaxHp(randomHp);
			addBaseMaxMp(randomMp);
		}
		setCurrentHp(getBaseMaxHp());
		setCurrentMp(getBaseMaxMp());
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();
		if (getLevel() > getHighLevel() && getReturnStat() == 0) {
			setHighLevel(getLevel());
		}

		try {
			save();
		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1PcInstance[]Error5", e);
		}

		L1Quest quest = getQuest();
		L1ItemInstance item = null;
		L1ItemInstance item1 = null;
		int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
		if (getLevel() >= 15 && lv15_step != L1Quest.QUEST_END) {
			switch (getType()) { // <--케릭 클래스 구분

			case 0:// 군주라면
				item = getInventory().storeItem(40226, 1);// 트루타겟
				item1 = getInventory().storeItem(20065, 1);
				if ((item != null) && (item1 != null))
					sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL15);
				break;
			case 1:
				item = getInventory().storeItem(20027, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL15);
				break;
			case 2:
				item = getInventory().storeItem(20021, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL15);
				break;
			case 3:
				item = getInventory().storeItem(20226, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL15);
				break;

			case 4:
				item = getInventory().storeItem(40598, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL15);
				break;

			case 5:
				item = getInventory().storeItem(
						L1ItemId.DRAGONKNIGHTTABLET_DRAGONSKIN, 1);
				item1 = getInventory().storeItem(410002, 1);
				if ((item != null) && (item1 != null))
					sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL15);
				break;

			case 6:
				item = getInventory().storeItem(
						L1ItemId.MEMORIALCRYSTAL_CUBE_IGNITION, 1);
				item1 = getInventory().storeItem(410005, 1);
				if ((item != null) && (item1 != null))
					sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL15);
				break;
			}
		}
		int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
		if (getLevel() >= 30 && lv30_step != L1Quest.QUEST_END) {
			switch (getType()) { // <--케릭 클래스 구분
			case 0:
				item = getInventory().storeItem(40570, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL30);
				break;

			case 1:
				item = getInventory().storeItem(20230, 1);
				item1 = getInventory().storeItem(30, 1);
				if ((item != null) && (item1 != null))
					sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL30);
				break;

			case 2:
				item = getInventory().storeItem(40588, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL30);
				break;
			case 3:
				item = getInventory().storeItem(115, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL30);
				break;

			case 4:
				item = getInventory().storeItem(40545, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL30);
				break;

			case 5:
				item = getInventory().storeItem(
						L1ItemId.DRAGONKNIGHTTABLET_BLOODLUST, 1);
				item1 = getInventory().storeItem(420001, 1);
				if ((item != null) && (item1 != null))
					sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL30);
				break;

			case 6:
				item = getInventory().storeItem(
						L1ItemId.MEMORIALCRYSTAL_CUBE_SHOCK, 1);
				item1 = getInventory().storeItem(420006, 1);
				if ((item != null) && (item1 != null))
					sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL30);
				break;
			}
		}
		int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
		if (getLevel() >= 45 && lv45_step != L1Quest.QUEST_END) {
			switch (getType()) { // <--케릭 클래스 구분
			case 0:
				item = getInventory().storeItem(20287, 1);
				item = getInventory().storeItem(20234, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL45);
				break;

			case 1:
				item = getInventory().storeItem(20318, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL45);
				break;

			case 2:
				item = getInventory().storeItem(40546, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL45);
				break;

			case 3:
				item = getInventory().storeItem(40599, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL45);
				break;

			case 4:
				item = getInventory().storeItem(40553, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL45);
				break;

			case 5:
				item = getInventory().storeItem(420004, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL45);
				break;

			case 6:
				item = getInventory().storeItem(420005, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL45);
				break;
			}
		}
		int lv50_step = quest.get_step(L1Quest.QUEST_LEVEL50);
		if (getLevel() >= 50 && lv50_step != L1Quest.QUEST_END) {
			switch (getType()) { // <--케릭 클래스 구분
			case 0:
				item = getInventory().storeItem(51, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL50);
				break;

			case 1:
				item = getInventory().storeItem(56, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL50);
				break;

			case 2:
				item = getInventory().storeItem(184, 1);
				item1 = getInventory().storeItem(50, 1);
				if ((item != null) && (item1 != null))
					sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL50);
				break;

			case 3:
				item = getInventory().storeItem(20225, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL50);
				break;

			case 4:
				item = getInventory().storeItem(13, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL50);
				break;

			case 5:
				item = getInventory().storeItem(410000, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL50);
				break;

			case 6:
				item = getInventory().storeItem(410003, 1);
				if (item != null)
					sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료 하였습니다."));
				getQuest().set_end(L1Quest.QUEST_LEVEL50);
				break;
			}
		}
		if (getLevel() >= 51
				&& getLevel() - 50 > getAbility().getBonusAbility()
				&& getAbility().getAmount() < 210) {
			sendPackets(new S_BonusStats(getId(), 1));
		}
		if (getLevel() >= 13) {
			if (getMapId() == 2005 || getMapId() == 85 || getMapId() == 86
					|| getMapId() == 87) { // 초보존(던젼)
				L1Teleport.teleport(this, 33082, 33389, (short) 4, 5, true);
			} else if (getMapId() == 68) {
				L1Teleport.teleport(this, 32574, 32941, (short) 0, 5, true);
			}
		}
		if (getLevel() >= 70) { // 버땅지정레벨
			if (getMapId() == 777) { // 버림받은 사람들의 땅(그림자의 신전)
				L1Teleport.teleport(this, 34043, 32184, (short) 4, 5, true); // 상아의
				// 탑전
			} else if (getMapId() == 778 || getMapId() == 779) { // 버림받은 사람들의
				// 땅(욕망의 동굴)
				L1Teleport.teleport(this, 32608, 33178, (short) 4, 5, true); // WB
			}
		}
		if (getLevel() >= 49 && getAinHasad() == 0) {
			setAinHasad(2000000);
			sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, getAinHasad()));
		}
		CheckStatus();
		sendPackets(new S_OwnCharStatus(this));
	}

	private void levelDown(int gap) {
		resetLevel();

		for (int i = 0; i > gap; i--) {
			short randomHp = CalcStat.calcStatHp(getType(), 0, getAbility()
					.getCon());
			short randomMp = CalcStat.calcStatMp(getType(), 0, getAbility()
					.getWis());
			addBaseMaxHp((short) -randomHp);
			addBaseMaxMp((short) -randomMp);
		}
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();
		if (Config.LEVEL_DOWN_RANGE != 0) {
			if (getHighLevel() - getLevel() >= Config.LEVEL_DOWN_RANGE) {
				if(isGm()){
					sendPackets(new S_SystemMessage("\\fY운영자님은 현재 레벨다운 범위를 초과하셨습니다."));
				}else{
				setChaTra(1);
				sendPackets(new S_SystemMessage("렙다(피녹)의 범위인 "+Config.LEVEL_DOWN_RANGE+"을 넘었습니다."));
				sendPackets(new S_Disconnect());
				_log.info(String.format("레벨 다운(피녹)의 허용 범위를 넘었기 때문에 %s를 강제 절단 했습니다.", getName()));
				}
			}
		}
		try {
			save();
		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1PcInstance[]Error6", e);
		}
		sendPackets(new S_OwnCharStatus(this));
	}

	public void beginGameTimeCarrier() {
		new GameTimeCarrier(this).start();
	}

	public boolean isGhost() {
		return _ghost;
	}

	private void setGhost(boolean flag) {
		_ghost = flag;
	}

	public boolean isReserveGhost() {
		return _isReserveGhost;
	}

	private void setReserveGhost(boolean flag) {
		_isReserveGhost = flag;
	}

	public void beginGhost(int locx, int locy, short mapid, boolean canTalk) {
		beginGhost(locx, locy, mapid, canTalk, 0);
	}

	public void beginGhost(int locx, int locy, short mapid, boolean canTalk,
			int sec) {
		if (isGhost()) {
			return;
		}
		setGhost(true);
		_ghostSaveLocX = getX();
		_ghostSaveLocY = getY();
		_ghostSaveMapId = getMapId();
		_ghostSaveHeading = getMoveState().getHeading();
		L1Teleport.teleport(this, locx, locy, mapid, 5, true);
		if (sec > 0) {
			_ghostFuture = _threadPool.pcSchedule(new L1PcGhostMonitor(getId()), sec * 1000);
		}
	}

	public void makeReadyEndGhost() {
		setReserveGhost(true);
		L1Teleport.teleport(this, _ghostSaveLocX, _ghostSaveLocY,
				_ghostSaveMapId, _ghostSaveHeading, true);
	}

	public void DeathMatchEndGhost() {
		endGhost();
		L1Teleport.teleport(this, 32614, 32735, (short) 4, 5, true);
	}

	public void endGhost() {
		setGhost(false);
		setReserveGhost(false);
	}

	public void beginHell(boolean isFirst) {
		if (getMapId() != 666) {
			int locx = 32701;
			int locy = 32777;
			short mapid = 666;
			L1Teleport.teleport(this, locx, locy, mapid, 5, false);
		}

		if (isFirst) {
			if (get_PKcount() <= 10) {
				setHellTime(180);
			} else {
				setHellTime(300 * (get_PKcount() - 100) + 300);
			}
			sendPackets(new S_BlueMessage(552, String.valueOf(get_PKcount()),
					String.valueOf(getHellTime() / 60)));
		} else {
			sendPackets(new S_BlueMessage(637, String.valueOf(getHellTime())));
		}
		if (_hellFuture == null) {
			_hellFuture = GeneralThreadPool.getInstance()
					.pcScheduleAtFixedRate(new L1PcHellMonitor(getId()), 0L,
							1000L);
		}
	}

	public void endHell() {
		if (_hellFuture != null) {
			_hellFuture.cancel(false);
			_hellFuture = null;
		}
		int[] loc = L1TownLocation
				.getGetBackLoc(L1TownLocation.TOWNID_ORCISH_FOREST);
		L1Teleport.teleport(this, loc[0], loc[1], (short) loc[2], 5, true);
		try {
			save();
		} catch (Exception ignore) {
		}
	}

	@Override
	public void setPoisonEffect(int effectId) {
		sendPackets(new S_Poison(getId(), effectId));
		if (!isGmInvis() && !isGhost() && !isInvisble()) {
			Broadcaster.broadcastPacket(this, new S_Poison(getId(), effectId));
		}
	}

	@Override
	public void healHp(int pt) {
		super.healHp(pt);
		sendPackets(new S_HPUpdate(this));
	}

	public int getBonusStats() {
		return _bonusStats;
	}

	@Override
	public int getKarma() {
		return _karma.get();
	}

	@Override
	public void setKarma(int i) {
		_karma.set(i);
	}

	public void addKarma(int i) {
		synchronized (_karma) {
			_karma.add(i);

		}
	}

	// 우호도표기추가
	private int _uhodopercent = 0;

	public void sendUhodo() {
		synchronized (_karma) {
			if (_uhodopercent != _karma.getPercent() || _uhodopercent == 0) {
				// sendPackets(new S_PacketBox(87, _karma.get())); // 우호도
				sendPackets(new S_PacketBox(87, getKarma())); // 우호도getKarma
				_uhodopercent = _karma.getPercent();
			}
		}
	}

	// UI DG표시
	private int _Dg = 0;

	public void addDg(int i) {
		_Dg += i;
		sendPackets(new S_PacketBox(S_PacketBox.UPDATE_DG, _Dg));
	}

	public int getDg() {
		return _Dg;
	}

	public int getKarmaLevel() {
		return _karma.getLevel();
	}

	public int getKarmaPercent() {
		return _karma.getPercent();
	}

	public Timestamp getLastPk() {
		return _lastPk;
	}

	public void setLastPk(Timestamp time) {
		_lastPk = time;
	}

	public void setLastPk() {
		_lastPk = new Timestamp(System.currentTimeMillis());
	}

	public boolean isWanted() {
		if (_lastPk == null) {
			return false;
		} else if (System.currentTimeMillis() - _lastPk.getTime() > 24 * 3600 * 1000) {
			setLastPk(null);
			return false;
		}
		return true;
	}

	public Timestamp getDeleteTime() {
		return _deleteTime;
	}

	public void setDeleteTime(Timestamp time) {
		_deleteTime = time;
	}

	public int getWeightReduction() {
		return _weightReduction;
	}

	public void addWeightReduction(int i) {
		_weightReduction += i;
	}

	public int getHasteItemEquipped() {
		return _hasteItemEquipped;
	}

	public void addHasteItemEquipped(int i) {
		_hasteItemEquipped += i;
	}

	public void removeHasteSkillEffect() {
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.SLOW))
			getSkillEffectTimerSet().removeSkillEffect(L1SkillId.SLOW);
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MASS_SLOW))
			getSkillEffectTimerSet().removeSkillEffect(L1SkillId.MASS_SLOW);
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ENTANGLE))
			getSkillEffectTimerSet().removeSkillEffect(L1SkillId.ENTANGLE);
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.HASTE))
			getSkillEffectTimerSet().removeSkillEffect(L1SkillId.HASTE);
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.GREATER_HASTE))
			getSkillEffectTimerSet().removeSkillEffect(L1SkillId.GREATER_HASTE);
		if (getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_HASTE))
			getSkillEffectTimerSet().removeSkillEffect(L1SkillId.STATUS_HASTE);
	}

	public void resetBaseDmgup() {
		int newBaseDmgup = 0;
		int newBaseBowDmgup = 0;
		int newBaseStatDmgup = CalcStat.calcBaseDmgup(getType(), getAbility()
				.getBaseStr());
		int newBaseStatBowDmgup = CalcStat.calcBaseBowDmgup(getType(),
				getAbility().getBaseDex());
		if (isKnight() || isDarkelf() || isDragonknight()||isIllusionist()) {
			newBaseDmgup = getLevel() / 10;
			newBaseBowDmgup = 0;

		} else if (isElf()) {
			newBaseDmgup = 0;
			newBaseBowDmgup = getLevel() / 10;
		}
		addDmgup((newBaseDmgup + newBaseStatDmgup) - _baseDmgup);
		addBowDmgup((newBaseBowDmgup + newBaseStatBowDmgup) - _baseBowDmgup);
		_baseDmgup = newBaseDmgup + newBaseStatDmgup;
		_baseBowDmgup = newBaseBowDmgup + newBaseStatBowDmgup;
	}

	public void resetBaseHitup() {
		int newBaseHitup = 0;
		int newBaseBowHitup = 0;
		int newBaseStatHitup = CalcStat.calcBaseHitup(getType(), getAbility()
				.getBaseStr());
		int newBaseStatBowHitup = CalcStat.calcBaseBowHitup(getType(),
				getAbility().getBaseDex());

		if (isCrown()) {
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		} else if (isKnight()) {
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (isElf()) {
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		} else if (isDarkelf()) {
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (isDragonknight()) {
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (isIllusionist()) {
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		}
		addHitup((newBaseHitup + newBaseStatHitup) - _baseHitup);
		addBowHitup((newBaseBowHitup + newBaseStatBowHitup) - _baseBowHitup);
		_baseHitup = newBaseHitup + newBaseStatHitup;
		_baseBowHitup = newBaseBowHitup + newBaseStatBowHitup;
	}

	public void resetBaseAc() {
		int newAc = CalcStat.calcAc(getLevel(), getAbility().getDex());
		int newbaseAc = CalcStat.calcBaseAc(getType(), getAbility()
				.getBaseDex());
		this.ac.addAc((newAc + newbaseAc) - _baseAc);
		_baseAc = newAc + newbaseAc;
	}

	public void resetBaseMr() {
		// int newBaseMr = CalcStat.calcBaseMr(getType(),
		// getAbility().getBaseWis());
		int newMr = 0;

		if (isCrown())
			newMr = 10;
		else if (isElf())
			newMr = 22;
		else if (isWizard())
			newMr = 15;
		else if (isDarkelf())
			newMr = 10;
		else if (isDragonknight())
			newMr = 18;
		else if (isIllusionist())
			newMr = 20;
		newMr += CalcStat.calcStatMr(getAbility().getTotalWis());
		newMr += getLevel() / 2;
		resistance.setBaseMr(newMr);
	}

	public void resetLevel() {
		setLevel(ExpTable.getLevelByExp(_exp));
		updateLevel();
	}

	public void updateLevel() {
		final int lvlTable[] = new int[] { 30, 25, 20, 16, 14, 12, 11, 10, 9,
				3, 2 };

		int regenLvl = Math.min(10, getLevel());
		if (30 <= getLevel() && isKnight()) {
			regenLvl = 11;
		}

		synchronized (this) {//피틱속도조절
			setHpregenMax(lvlTable[regenLvl - 1] * 12);
		}
	}

	public void refresh() {
		CheckChangeExp();
		resetLevel();
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseMr();
		resetBaseAc();
		setBaseMagicDecreaseMp(CalcStat.calcBaseDecreaseMp(getType(),
				getAbility().getBaseInt()));
		setBaseMagicHitUp(CalcStat.calcBaseMagicHitUp(getType(), getAbility()
				.getBaseInt()));
		setBaseMagicCritical(CalcStat.calcBaseMagicCritical(getType(),
				getAbility().getBaseInt()));
		setBaseMagicDmg(CalcStat.calcBaseMagicDmg(getType(), getAbility()
				.getBaseInt()));
	}

	public void checkChatInterval() {
		long nowChatTimeInMillis = System.currentTimeMillis();
		if (_chatCount == 0) {
			_chatCount++;
			_oldChatTimeInMillis = nowChatTimeInMillis;
			return;
		}

		long chatInterval = nowChatTimeInMillis - _oldChatTimeInMillis;
		if (chatInterval > 2000) {
			_chatCount = 0;
			_oldChatTimeInMillis = 0;
		} else {
			if (_chatCount >= 3) {
				getSkillEffectTimerSet().setSkillEffect(
						L1SkillId.STATUS_CHAT_PROHIBITED, 120 * 1000);
				sendPackets(new S_SkillIconGFX(36, 120));
				sendPackets(new S_ServerMessage(153));
				_chatCount = 0;
				_oldChatTimeInMillis = 0;
			}
			_chatCount++;
		}
	}

	// 범위외가 된 인식이 끝난 오브젝트를 제거(버경)
	private void removeOutOfRangeObjects(int distance) {
		try {
			List<L1Object> known = getNearObjects().getKnownObjects();
			int size = known.size();
			L1Object obj;
			for (int i = 0; i < size; i++) {
				if (known.get(i) == null) {
					continue;
				}

				obj = known.get(i);
				if (!getLocation().isInScreen(obj.getLocation())) { // 범위외가 되는
					// 거리
					getNearObjects().removeKnownObject(obj);
					sendPackets(new S_RemoveObject(obj));
				}
				obj = null;
			}
		} catch (Exception e) {
			System.out.println("removeOutOfRangeObjects 에러 : " + e);
		}
	}

	// 오브젝트 인식 처리(버경)
	public void UpdateObject() {
		try {
			if (this == null)
				return;
			try {
				removeOutOfRangeObjects(17);
			} catch (Exception e) {
				System.out.println("removeOutOfRangeObjects(17) 에러 : " + e);
			}

			// 화면내의 오브젝트 리스트를 작성
			FastTable<L1Object> visible2 = null;
			visible2 = L1World.getInstance().getVisibleObjects(this);
			L1NpcInstance npc = null;
			for (L1Object visible : visible2) {
				if (this == null) {
					break;
				}
				if (visible == null) {
					continue;
				}
				if (!getNearObjects().knownsObject(visible)) {
					visible.onPerceive(this);
				} else {
					if (visible instanceof L1NpcInstance) {
						npc = (L1NpcInstance) visible;
						if (npc.getHiddenStatus() != 0) {
							npc.approachPlayer(this);
						}
					}

				}
			}
		} catch (Exception e) {
			System.out.println("UpdateObject() 에러 : " + e);
		}
	}

	public void CheckChangeExp() {
		int level = ExpTable.getLevelByExp(getExp());
		int char_level = CharacterTable.getInstance().PcLevelInDB(getId());
		if (char_level == 0) { // 0이라면..에러겟지?
			return; // 그럼 그냥 리턴
		}
		int gap = level - char_level;
		if (gap == 0) {
			sendPackets(new S_Exp(this));
			return;
		}

		// 레벨이 변화했을 경우
		if (gap > 0) {
			levelUp(gap);
		} else if (gap < 0) {
			levelDown(gap);
		}
	}

	public void CheckStatus() {
		if (!getAbility().isNormalAbility(getClassId(), getLevel(),
				getHighLevel(), getAbility().getBaseAmount())
				&& !isGm()) {
			SpecialEventHandler.getInstance().ReturnStats(this);
		}
	}

	public void cancelAbsoluteBarrier() { // 아브소르트바리아의 해제
		if (this.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)) {
			this.getSkillEffectTimerSet()
					.killSkillEffectTimer(ABSOLUTE_BARRIER);
			this.startHpRegenerationByDoll();
			this.startMpRegenerationByDoll();
			/**안전모드**/
			if(Safe_Teleport){
				sendPackets(new S_SystemMessage("안전 모드가 해제 되었습니다."));
				Safe_Teleport = false;
			}
			/**안전모드**/
		}
	}

	public int get_PKcount() {
		return _PKcount;
	}

	public void set_PKcount(int i) {
		_PKcount = i;
	}

	public int getClanid() {
		return _clanid;
	}

	public void setClanid(int i) {
		_clanid = i;
	}

	public String getClanname() {
		return clanname;
	}

	public void setClanname(String s) {
		clanname = s;
	}

	public L1Clan getClan() {
		return L1World.getInstance().getClan(getClanname());
	}

	public int getClanRank() {
		return _clanRank;
	}

	public void setClanRank(int i) {
		_clanRank = i;
		///////////혈맹리뉴얼//////////////
		try{
			if(getClan() != null)
				getClan().setClanRank(getName(), i);
		}catch(Exception e){}
		///////////혈맹리뉴얼//////////////
	}

	public byte get_sex() {
		return _sex;
	}

	public void set_sex(int i) {
		_sex = (byte) i;
	}

	private int huntCount;

	private int huntPrice;

	private String _reasontohunt;

	public String getReasonToHunt() {
		return _reasontohunt;
	}

	public void setReasonToHunt(String s) {
		_reasontohunt = s;
	}

	public int getHuntCount() {
		return huntCount;
	}

	public void setHuntCount(int i) {
		huntCount = i;
	}

	public int getHuntPrice() {
		return huntPrice;
	}

	public void setHuntPrice(int i) {
		huntPrice = i;
	}

	public int getAge() {
		return _age;
	}

	public void setAge(int i) {
		_age = i;
	}

	private boolean GMcurse;
	public boolean isGMcurse(){	return GMcurse;	}
	public boolean setGMcurse(boolean i){	return GMcurse = i;}

	public boolean isGm() {
		return _gm;
	}

	public void setGm(boolean flag) {
		_gm = flag;
	}

	/* 여기부터 드래곤진주 */
	public boolean isThirdSpeed() {
		return (getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.STATUS_DRAGONPERL) || get진주속도() == 1);// ;;;;
	}

	private int _진주속도; // ● 진주 상태 0.통상 1.치우침 이브

	public int get진주속도() {
		return _진주속도;
	}

	public void set진주속도(int i) {
		_진주속도 = i;
	}

	public boolean isMonitor() {
		return _monitor;
	}

	public void setMonitor(boolean flag) {
		_monitor = flag;
	}

	public int getDamageReductionByArmor() {
		return _damageReductionByArmor;
	}

	public void addDamageReductionByArmor(int i) {
		_damageReductionByArmor += i;
	}

	public int getBowHitupByArmor() {
		return _bowHitupByArmor;
	}

	public void addBowHitupByArmor(int i) {
		_bowHitupByArmor += i;
	}

	public int getBowDmgupByArmor() {
		return _bowDmgupByArmor;
	}

	public void addBowDmgupByArmor(int i) {
		_bowDmgupByArmor += i;
	}

	public int getHitupByArmor() {
		return _HitupByArmor;
	}

	public void addHitupByArmor(int i) {
		_HitupByArmor += i;
	}

	public int getDmgupByArmor() {
		return _DmgupByArmor;
	}

	public void addDmgupByArmor(int i) {
		_DmgupByArmor += i;
	}

	public int getBowHitupByDoll() {
		return _bowHitupBydoll;
	}

	public void addBowHitupByDoll(int i) {
		_bowHitupBydoll += i;
	}

	public int getBowDmgupByDoll() {
		return _bowDmgupBydoll;
	}

	public void addBowDmgupByDoll(int i) {
		_bowDmgupBydoll += i;
	}

	private void setGresValid(boolean valid) {
		_gresValid = valid;
	}

	public boolean isGresValid() {
		return _gresValid;
	}
	public int getMagicHitupByArmor() { return _MagicHitupByArmor; }
	public void addMagicHitupByArmor(int i) {	_MagicHitupByArmor += i; }
	public long getFishingTime() {
		return _fishingTime;
	}

	public void setFishingTime(long i) {
		_fishingTime = i;
	}

	public boolean isFishing() {
		return _isFishing;
	}

	public boolean isFishingReady() {
		return _isFishingReady;
	}

	public void setFishing(boolean flag) {
		_isFishing = flag;
	}

	public void setFishingReady(boolean flag) {
		_isFishingReady = flag;
	}

	public int getCookingId() {
		return _cookingId;
	}

	public void setCookingId(int i) {
		_cookingId = i;
	}

	public int getDessertId() {
		return _dessertId;
	}

	public void setDessertId(int i) {
		_dessertId = i;
	}

	public L1ExcludingList getExcludingList() {
		return _excludingList;
	}

	public AcceleratorChecker getAcceleratorChecker() {
		return _acceleratorChecker;
	}

	public int getTeleportX() {
		return _teleportX;
	}

	public void setTeleportX(int i) {
		_teleportX = i;
	}

	public int getTeleportY() {
		return _teleportY;
	}

	public void setTeleportY(int i) {
		_teleportY = i;
	}

	public short getTeleportMapId() {
		return _teleportMapId;
	}

	public void setTeleportMapId(short i) {
		_teleportMapId = i;
	}

	public int getTeleportHeading() {
		return _teleportHeading;
	}

	public void setTeleportHeading(int i) {
		_teleportHeading = i;
	}

	public int getTempCharGfxAtDead() {
		return _tempCharGfxAtDead;
	}

	public void setTempCharGfxAtDead(int i) {
		_tempCharGfxAtDead = i;
	}

	public boolean isCanWhisper() {
		return _isCanWhisper;
	}

	public void setCanWhisper(boolean flag) {
		_isCanWhisper = flag;
	}

	public boolean isShowTradeChat() {
		return _isShowTradeChat;
	}

	public void setShowTradeChat(boolean flag) {
		_isShowTradeChat = flag;
	}

	public boolean isShowWorldChat() {
		return _isShowWorldChat;
	}

	public void setShowWorldChat(boolean flag) {
		_isShowWorldChat = flag;
	}

	public int getFightId() {
		return _fightId;
	}

	public void setFightId(int i) {
		_fightId = i;
	}

	public boolean isPetRacing() {
		return petRacing;
	}

	public void setPetRacing(boolean Petrace) {
		this.petRacing = Petrace;
	}

	public int getPetRacingLAB() {
		return petRacingLAB;
	}

	public void setPetRacingLAB(int lab) {
		this.petRacingLAB = lab;
	}

	public int getPetRacingCheckPoint() {
		return petRacingCheckPoint;
	}

	public void setPetRacingCheckPoint(int p) {
		this.petRacingCheckPoint = p;
	}

	public void setHaunted(boolean i) {
		this.isHaunted = i;
	}

	public boolean isHaunted() {
		return isHaunted;
	}

	public void setDeathMatch(boolean i) {
		this.isDeathMatch = i;
	}

	public boolean isDeathMatch() {
		return isDeathMatch;
	}

	public int getCallClanId() {
		return _callClanId;
	}

	public void setCallClanId(int i) {
		_callClanId = i;
	}

	public int getCallClanHeading() {
		return _callClanHeading;
	}

	public void setCallClanHeading(int i) {
		_callClanHeading = i;
	}
	private boolean _isSummonMonster = false;

	public void setSummonMonster(boolean SummonMonster) {
		_isSummonMonster = SummonMonster;
	}

	public boolean isSummonMonster() {
		return _isSummonMonster;
	}

	private boolean _isShapeChange = false;

	public void setShapeChange(boolean isShapeChange) {
		_isShapeChange = isShapeChange;
	}

	public boolean isShapeChange() {
		return _isShapeChange;
	}
	 // /신고 추가
	private boolean _isReport = true;

	 public void setReport(boolean _isreport) {
	  _isReport = _isreport;
	 }
	 public boolean isReport() {
	  return _isReport;
	 }
	 // /신고 추가
	private boolean _isArchShapeChange = false;

	public void setArchShapeChange(boolean isArchShapeChange) {
		_isArchShapeChange = isArchShapeChange;
	}

	public boolean isArchShapeChange() {
		return _isArchShapeChange;
	}

	private boolean _isArchPolyType = true; // t 1200 f -1

	public void setArchPolyType(boolean isArchPolyType) {
		_isArchPolyType = isArchPolyType;
	}

	public boolean isArchPolyType() {
		return _isArchPolyType;
	}

	public int getUbScore() {
		return _ubscore;
	}

	public void setUbScore(int i) {
		_ubscore = i;
	}

	private int _girandungeon;
	
	private int _lasdungeon;//라던업데이트

	/** 기란던전 입장되어 있던 시간값을 가져 온다. 단위 : 1분 */
	public int getGdungeonTime() {
		return _girandungeon;
	}

	public void setGdungeonTime(int i) {
		_girandungeon = i;
	}
	/** 라바던전 입장되어 있던 시간값을 가져 온다. 단위 : 1분 */
	public int getLdungeonTime() {//라던업데이트
		return _lasdungeon;
	}

	public void setLdungeonTime(int i) {//라던업데이트
		_lasdungeon = i;
	}
    /** 상아 리뉴얼 **/

private int _tkddkdungeon; //추가 

	public int getTkddkdungeonTime() {
		return _tkddkdungeon;
	} //추가 

	public void setTkddkdungeonTime(int i) {
		_tkddkdungeon = i;
	} //추가 

        /** 상아 리뉴얼 **/
	private int _timeCount = 0;

	public int getTimeCount() {
		return _timeCount;
	}

	public void setTimeCount(int i) {
		_timeCount = i;
	}

	private boolean _isPointUser;

	/** 계정 시간이 남은 Pc 인지 판단한다 */
	public boolean isPointUser() {
		return _isPointUser;
	}

	public void setPointUser(boolean i) {
		_isPointUser = i;
	}

	private long _limitPointTime;

	public long getLimitPointTime() {
		return _limitPointTime;
	}

	public void setLimitPointTime(long i) {
		_limitPointTime = i;
	}

	private int _safecount = 0;

	public int getSafeCount() {
		return _safecount;
	}

	public void setSafeCount(int i) {
		_safecount = i;
	}



	private Timestamp _logoutTime;

	public Timestamp getLogOutTime() {
		return _logoutTime;
	}

	public void setLogOutTime(Timestamp t) {
		_logoutTime = t;
	}

	public void setLogOutTime() {
		_logoutTime = new Timestamp(System.currentTimeMillis());
	}
	private int _chaTra; 
	public int getChaTra() {	  return _chaTra;	 }
	public void setChaTra(int i) {	  _chaTra = i;	 }

	private int _ainhasad;

	public int getAinHasad() {
		return _ainhasad;
	}

	public void calAinHasad(int i) {
		int calc = _ainhasad + i;
		if (calc >= 2000000)
			calc = 2000000;
		_ainhasad = calc;
	}

	public void setAinHasad(int i) {
		_ainhasad = i;
	}

	/** 스피드핵 관련 */
	public void addSpeedHackCount(int x) {
		_speedhackCount += x;
		if (_speedhackCount == 3)
			sendPackets(new S_Disconnect());
	}

	public int getSpeedHackCount() {
		return _speedhackCount;
	}

	public void setSpeedHackCount(int x) {
		_speedhackCount = x;
	}

	public int getSpeedHackX() {
		return _speedhackX;
	}

	public void setSpeedHackX(int x) {
		_speedhackX = x;
	}

	public int getSpeedHackY() {
		return _speedhackY;
	}

	public void setSpeedHackY(int y) {
		_speedhackY = y;
	}

	public short getSpeedHackMapid() {
		return _speedhackMapid;
	}

	public void setSpeedHackMapid(short mapid) {
		_speedhackMapid = mapid;
	}

	public int getSpeedHackHeading() {
		return _speedhackHeading;
	}

	public void setSpeedHackHeading(int Heading) {
		_speedhackHeading = Heading;
	}

	public int getSpeedRightInterval() {
		return _speedright;
	}

	public void setSpeedRightInterval(int r) {
		_speedright = r;
	}

	public int getSpeedInterval() {
		return _speedinterval;
	}

	public void setSpeedInterval(int i) {
		_speedinterval = i;
	}

	public void speedHackClear() {
		_speedhackHeading = 0;
		_speedhackMapid = 0;
		_speedhackX = 0;
		_speedhackY = 0;
		_speedright = 0;
		_speedinterval = 0;
	}

	/** 인챈트 버그 예외 처리 */
	private int _enchantitemid = 0;

	public int getLastEnchantItemid() {
		return _enchantitemid;
	}

	public void setLastEnchantItemid(int i, L1ItemInstance item) {
		if (getLastEnchantItemid() == i && i != 0) {
			sendPackets(new S_Disconnect());
			getInventory().removeItem(item, item.getCount());
			return;
		}
		_enchantitemid = i;
	}

	// 크레이 버프 받았는지 유무
	private boolean craybuff = false;

	public boolean iscraybuff() {
		return craybuff;
	}

	public void setcraybuff(boolean b) {
		craybuff = b;
	}

	/** 캐릭터에, pet, summon monster, tame monster, created zombie 를 추가한다. */
	public void addPet(L1NpcInstance npc) {
		_petlist.put(npc.getId(), npc);
	}

	/** 캐릭터로부터, pet, summon monster, tame monster, created zombie 를 삭제한다. */
	public void removePet(L1NpcInstance npc) {
		_petlist.remove(npc.getId());
	}

	/** 캐릭터의 애완동물 리스트를 돌려준다. */
	public Map<Integer, L1NpcInstance> getPetList() {
		return _petlist;
	}

	/** 캐릭터에 doll을 추가한다. */
	public void addDoll(L1DollInstance doll) {
		_dolllist.put(doll.getId(), doll);
	}

	/** 캐릭터로부터 dool을 삭제한다. */
	public void removeDoll(L1DollInstance doll) {
		_dolllist.remove(doll.getId());
	}

	/** 캐릭터의 doll 리스트를 돌려준다. */
	public Map<Integer, L1DollInstance> getDollList() {
		return _dolllist;
	}

	/** 캐릭터에 이벤트 NPC(케릭터를 따라다니는)를 추가한다. */
	public void addFollower(L1FollowerInstance follower) {
		_followerlist.put(follower.getId(), follower);
	}

	/** 캐릭터로부터 이벤트 NPC(케릭터를 따라다니는)를 삭제한다. */
	public void removeFollower(L1FollowerInstance follower) {
		_followerlist.remove(follower.getId());
	}

	/** 캐릭터의 이벤트 NPC(케릭터를 따라다니는) 리스트를 돌려준다. */
	public Map<Integer, L1FollowerInstance> getFollowerList() {
		return _followerlist;
	}

	public void ClearPlayerClanData(L1Clan clan) throws Exception {
		ClanWarehouse clanWarehouse = WarehouseManager.getInstance()
				.getClanWarehouse(clan.getClanName());
		if (clanWarehouse != null)
			clanWarehouse.unlock(getId());
		setClanid(0);
		setClanname("");
		setTitle("");
		if (this != null) {
			sendPackets(new S_CharTitle(getId(), ""));
			Broadcaster.broadcastPacket(this, new S_CharTitle(getId(), ""));
		}
		setClanRank(0);
		save();
	}

	private int _HpregenMax = 0;

	public int getHpregenMax() {
		return _HpregenMax;
	}

	public void setHpregenMax(int num) {
		this._HpregenMax = num;
	}

	private int _HpregenPoint = 0;

	public int getHpregenPoint() {
		return _HpregenPoint;
	}

	public void setHpregenPoint(int num) {
		this._HpregenPoint = num;
	}

	public void addHpregenPoint(int num) {
		this._HpregenPoint += num;
	}

	private int _HpcurPoint = 4;

	public int getHpcurPoint() {
		return _HpcurPoint;
	}

	public void setHpcurPoint(int num) {
		this._HpcurPoint = num;
	}

	private int _MpregenMax = 0;

	public int getMpregenMax() {
		return _MpregenMax;
	}

	public void setMpregenMax(int num) {
		this._MpregenMax = num;
	}

	private int _MpregenPoint = 0;

	public int getMpregenPoint() {
		return _MpregenPoint;
	}

	public void setMpregenPoint(int num) {
		this._MpregenPoint = num;
	}

	public void addMpregenPoint(int num) {
		this._MpregenPoint += num;
	}

	private int _MpcurPoint = 4;

	public int getMpcurPoint() {
		return _MpcurPoint;
	}

	public void setMpcurPoint(int num) {
		this._MpcurPoint = num;
	}

	public void setHongBo(boolean hongbo) {
		_hongbo = hongbo;
	}

	public boolean getHongBo() {
		return _hongbo;
	}
////////////////////// 스피드핵 방지
	private int CashStep = 0;
	
	public int getCashStep() {
		return CashStep;
	}
	public void setCashStep(int cashStep) {
		CashStep = cashStep;
	}
	
	
	
	private int hackTimer = -1;

	private int hackCKtime = -1;

	private int hackCKcount = 0;

	public int get_hackTimer() {
		return hackTimer;
	}

	public void increase_hackTimer() {
		if (hackTimer < 0)
			return;
		hackTimer++;
	}

	public void init_hackTimer() {
		hackTimer = 0;
	}

	public void calc_hackTimer() {
		if (hackTimer < 0)
			return;
		else if (hackTimer <= 30) {
			
			_netConnection.close();
			this.logout();
		} else if (hackTimer <= 57) {
			if (hackCKtime < 0 || hackCKtime < hackTimer - 1
					|| hackCKtime > hackTimer + 1) {
				hackCKtime = hackTimer;
				hackCKcount = 1;
			} else {
				hackCKcount++;


				if (hackCKcount == 3) {
					sendMessage("불량프로그램사용자  : " + this.getName()
								+ " 검출완료...");
					_netConnection.close();
this.logout();
				}
			}
		} else {
			hackCKtime = -1;
			hackCKcount = 0;
		}
		hackTimer = 0;
	}

	private int _AAD;

	private int _AAC;

	private long _AAT = System.currentTimeMillis();

	public boolean isAcceleratorAttack() {
		double HASTE_RATE = 0.745;
		double WAFFLE_RATE = 0.874;
		int interval = _AAD;
		if (isHaste()) {// 헤이스트
			interval *= HASTE_RATE;
		}
		if (getSkillEffectTimerSet().hasSkillEffect(186)) {// 블러드러스트
			interval *= HASTE_RATE;
		}
		if (isBrave()) {
			if (isElf()) {
				interval *= WAFFLE_RATE;
			} else {
				interval *= HASTE_RATE;
			}
		}
		long CT = System.currentTimeMillis() - _AAT;
		CT *= 1.40D;
		if (CT < interval) {
			_AAC++;
		} else {
			_AAC = 0;
		}
		_AAT = System.currentTimeMillis();
		if (_AAC >= 2) {
			_AAC = 0;
			return true;
		}
		return false;
	}
///////////////////생존의 외침
	private int AinState = 0; // 쿨타임 상태

	public int getAinState() {
		return AinState;
	}

	public void setAinState(int AinState) {
		this.AinState = AinState;
	}

	public boolean SurvivalState; // 생존의 외침 상태

	private int SurvivalGauge; // 생존의 외침 게이지

	public int getSurvivalGauge() {
		return SurvivalGauge;
	}

	public void setSurvivalGauge(int SurvivalGauge) {
		this.SurvivalGauge = SurvivalGauge;
	}
///////////////////생존의 외침
	public void setAAD(int i) {
		_AAD = i;
	}

	private int _AMD;

	private int _AMC;

	private long _AMT = System.currentTimeMillis();

	public String TempQuiz = "";
	/**레이드포탈**/
	public int[] DragonPortalLoc = new int[3];

	public boolean isAcceleratorMove() {
		double HASTE_RATE = 0.745;
		double WAFFLE_RATE = 0.874;
		int interval = _AMD;

		if (isHaste()) {
			interval *= HASTE_RATE;
		}
		if (isFastMovable() || getSkillEffectTimerSet().hasSkillEffect(4004)) {
			interval *= HASTE_RATE;
		}
		if (getMapId() == 5143) {
			interval *= HASTE_RATE;
			interval *= HASTE_RATE;
		}
		if (isBrave()) {
			if (isElf()) {
				interval *= WAFFLE_RATE;
			} else {
				interval *= HASTE_RATE;
			}
		}

		long CT = System.currentTimeMillis() - _AMT;
		CT *= 1.40D;
		if (CT < interval) {
			_AMC++;
		} else {
			_AMC = 0;
		}
		_AMT = System.currentTimeMillis();
		if (_AMC >= 2) {
			_AMC = 0;
			return true;
		}
		return false;
	}

	public void setAMD(int i) {
		_AMD = i;
	}
		/**매입상점*/
	private int _shopStep = 0;

	public int getShopStep() {
		return _shopStep;
	}

	public void setShopStep(int _shopStep) {
		this._shopStep = _shopStep;
	}
	private boolean autoshop = false;

	public boolean isAutoshop() {
		return autoshop;
	}

	public void setAutoshop(boolean autoshop) {
		this.autoshop = autoshop;
	}
    /**매입상점**/
	

	
	/**혈맹가입군주여부 */
	private boolean autoKing = false;
	
	public boolean isAutoKing() {
		return autoKing;
	}

	public void setAutoKing(boolean autoKing) {
		this.autoKing = autoKing;
	}
	
	/**혈맹가입군주유저호칭 */
	private String autoKingTitle = "";	

	public String getAutoKingTitle() {
		return autoKingTitle;
	}

	public void setAutoKingTitle(String autoKingTitle) {
		this.autoKingTitle = autoKingTitle;
	}
	/** 2011.11.06 SOCOOL 로봇 인공지능 */
	private L1RobotAI _robotAi = null;

	public L1RobotAI getRobotAi() {
		return _robotAi;
	}

	public void setRobotAi(L1RobotAI ai) {
		_robotAi = ai;
	}
	/** 채팅 제어 대상 캐릭명 */
	private String _chatTarget = "";//9월20일 통자바케릭정보막대기

	public String getChatTarget() {
		return _chatTarget;
	}

	public void setChatTarget(String _chatTarget) {
		this._chatTarget = _chatTarget;
	}
	
	//////////////////////////혈맹스파이//////////////////////
	public boolean clanid_search1(String i,int SS) {  // 캐릭명이 디비에 있는지 검사하고 정보를 넘겨준다. 
    	java.sql.Connection accountObjid = null ; // 이름으로 objid를 검색하기 위해
    	PreparedStatement Query_objid = null ;
    	ResultSet rs = null;
	    try {
	    	int count = 0 ;
	  
	    	accountObjid = L1DatabaseFactory.getInstance().getConnection() ;
	    	Query_objid = accountObjid.prepareStatement("SELECT ClanID FROM characters WHERE account_name = '" + i + "'");
	   
	    	rs = Query_objid.executeQuery() ;
	    	while (rs.next()) {
	    		int aa = rs.getInt("ClanID"); 
	    		if(aa != 0 && aa != SS){
	    			count++ ;
	    			break;
	    		}
	    	}
	    	if(count>0){
	    		return true;
	    	}
	    	return false;
	    } catch (Exception e) {
	    	
	    }finally{
	    	SQLUtil.close(rs);
	    	SQLUtil.close(Query_objid);
	    	SQLUtil.close(accountObjid);
	    }
	    return false ;
	  }
	private void sendMessage(String msg) {
		  for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
		   pc.sendPackets(new S_ChatPacket(pc, msg, Opcodes.S_OPCODE_MSG, 18));
		  }
		 }
//////////////////////////혈맹스파이//////////////////////
	
	/**
	 * 멘트 처리 함수.
	 * 
	 * @param time
	 * @param pvp
	 */
	public void toMent(int type) {
		List<RobotMent> list = new ArrayList<RobotMent>();
		String text = "";
		switch(type){
			case 0:
				text = "pvp";
			case 1:
				text = "pvp";
				break;
			default:
				break;
		}
		if(text.equals(""))
			return;
		for (RobotMent m : RobotThread.getRobotMent()) {
			if (m.type.equalsIgnoreCase(text))
				list.add(m);
		}
		if(Config.R_ATKMSG){//로봇전창
		// 멘트를 표현할 수 있는 디비상태일경우.
	if (list.size() > 0) {
			L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(this, list.get(L1Robot.random(0, list.size() - 1)).ment, Opcodes.S_OPCODE_MSG,3));
		}
}
	}
	public void toMent2(int type) {
		List<RobotMent> list = new ArrayList<RobotMent>();
		String text = "";
		switch(type){
			case 0:
				text = "die";
			case 1:
				text = "die";
				break;
			default:
				break;
		}
		if(text.equals(""))
			return;
		for (RobotMent m : RobotThread.getRobotMent()) {
			if (m.type.equalsIgnoreCase(text))
				list.add(m);
		}
		if(Config.R_ATKMSG2){//로봇장사
		// 멘트를 표현할 수 있는 디비상태일경우.
	if (list.size() > 0) {
			L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(this, list.get(L1Robot.random(0, list.size() - 1)).ment, Opcodes.S_OPCODE_MSG,12));
		}
}
}
	
	
	
	
	/** ■■■■■■■■■■ 버경멘트 ■■■■■■■■■■ **/
    public boolean isbugment()
    {
        return bug_ment;
    }
    
    public void isbugment(boolean bugment)
    {
        bug_ment = bugment;
    }
	private ArrayList<String> _cmalist = new ArrayList<String>();
	/** 클랜 매칭 신청,요청 목록 
	 * 유저가 사용할땐 배열에 혈맹의 이름을 넣고
	 * 군주가 사용할땐 배열에 신청자의 이름을 넣는다.
	 */
	public void addCMAList(String name) {
		if (_cmalist.contains(name)) {
			return;
		}
		_cmalist.add(name);
	}
	public void removeCMAList(String name) {
		if (!_cmalist.contains(name)) {
			return;
		}
		_cmalist.remove(name);
	}
	public ArrayList<String> getCMAList() {
		return _cmalist;
	}
    
    
}//추가