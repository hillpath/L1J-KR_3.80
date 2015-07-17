/** Test Server 11년 5월 19일자옵코드 **/
package l1j.server.server;

public class Opcodes {

	public Opcodes() {
	}
	/** 3.80c 옵코드 적용 물개 **/
	public static final int C_OPCODE_TRADE = 2; // [/교환]
	  public static final int C_OPCODE_BOOKMARKDELETE = 3; // [/기억 후 기억목록클릭 delete]
	  public static final int C_OPCODE_BUDDYLIST = 4; // 친구리스트
	  public static final int C_OPCODE_FIGHT = 5; // [/결투]
	  public static final int C_OPCODE_USESKILL = 6; // 스킬 사용 부분
	  public static final int C_OPCODE_RESTART = 7; // 겜중에 리스창으로 빠짐. 대만: C_OPCODE_CHANGECHAR
	  public static final int C_OPCODE_BOARD = 10; // 게시판 읽기
	  public static final int C_OPCODE_AMOUNT = 11; // 수량성 아이템 제작 갯수
	  public static final int C_OPCODE_WAREHOUSEPASSWORD = 13; //창고 비번. 대만: C_OPCODE_WAREHOUSELOCK
	  public static final int C_OPCODE_CLIENTVERSION = 14; //v 클라에서 서버 버전 요청 하는 부분
	  public static final int C_OPCODE_TAXRATE = 19; // 세금 조정
	  public static final int C_OPCODE_SELECTLIST = 20; // 펫리스트에서 펫찾기
	  public static final int C_OPCODE_DROPITEM = 25; // 아이템 떨구기
	  public static final int C_OPCODE_LOGINTOSERVEROK = 26; // [환경설정->전챗켬,끔]
	  public static final int C_OPCODE_MOVECHAR = 29; // 이동요청 부분
	  public static final int C_OPCODE_LEAVEPARTY = 33; // 파티 탈퇴
	  public static final int C_OPCODE_NPCTALK = 34; // Npc와 대화부분
	  public static final int C_OPCODE_TRADEADDITEM = 37; // 교환창에 아이템 추가
	  public static final int C_OPCODE_SHOP = 38; // [/상점 -> OK]
	  public static final int C_OPCODE_SKILLBUY = 39; // 스킬 구입
	  public static final int C_OPCODE_CHATGLOBAL = 40; // 전체채팅
	  public static final int C_OPCODE_DOOR = 41; // 문짝 클릭 부분
	  public static final int C_OPCODE_PARTY = 43; // [/파티]. 대만 : C_OPCODE_PARTYLIST
	  public static final int C_OPCODE_DRAWAL = 44; // 공금 출금[자금을 인출한다]
	  public static final int C_OPCODE_GIVEITEM = 45; // 강제로 아이템 주기
	  public static final int C_OPCODE_PRIVATESHOPLIST = 47; // 개인상점 buy, sell
	  public static final int C_OPCODE_PROPOSE = 50; // [/청혼]
	  public static final int C_OPCODE_CHECKPK = 51; // [/checkpk]
	  public static final int C_OPCODE_TELEPORT = 52; // 텔레포트 사용
	  public static final int C_OPCODE_DEPOSIT = 56; // 성 공금 입금
	  public static final int C_OPCODE_LEAVECLANE = 61; // 혈맹 탈퇴
	  public static final int C_OPCODE_FISHCLICK = 62; // 낚시 입질 클릭
	  public static final int C_OPCODE_RANK = 63; // [/동맹]
	  public static final int C_OPCODE_PLEDGE = 68; // [/혈맹]
	  public static final int C_OPCODE_BANCLAN = 69; // 혈맹 추방 명령어
	  public static final int C_OPCODE_TRADEADDOK = 71; // 교환 OK
	  public static final int C_OPCODE_CREATE_CHARACTER = 84;  // 케릭 생성. C_OPCODE_NEWCHAR
	  public static final int C_OPCODE_TRADEADDCANCEL = 86; // 교환 취소
	  public static final int C_OPCODE_MAIL = 87; // 편지함 클릭후 혈맹편지 왔다갔다
	  public static final int C_OPCODE_TITLE = 90; // 호칭 명령어
	  public static final int C_OPCODE_KEEPALIVE = 95; // 1분마다 한번씩 옴
	  public static final int C_OPCODE_BASERESET = 98; // 스텟 초기화. 대만: C_OPCODE_CHARRESET
	  public static final int C_OPCODE_PETMENU = 103; // 펫 메뉴
	  public static final int C_OPCODE_PICKUPITEM = 112; // 아이템 줍기.저
	  public static final int C_OPCODE_BOARDREAD = 114; // 게시판 읽기
	  public static final int C_OPCODE_FIX_WEAPON_LIST = 118; // 무기수리
	  public static final int C_OPCODE_LOGINPACKET = 119; //v 계정정보가 담긴 패킷.
	  public static final int C_OPCODE_EXTCOMMAND = 120; // <알트+1 ~ 5 까지 액션 >
	  public static final int C_OPCODE_ATTR = 121; // [ Y , N ] 선택 부분
	  public static final int C_OPCODE_QUITGAME = 122; // v로그인창에서 겜 종료할때
	  public static final int C_OPCODE_ARROWATTACK = 123; // 활공격 부분
	  public static final int C_OPCODE_NPCACTION = 125; // Npc 대화 액션 부분
	  public static final int C_OPCODE_CHAT = 136; // 일반 채팅
	  public static final int C_OPCODE_SELECT_CHARACTER = 137; // 리스창에서 케릭 선택 대만: C_OPCODE_LOGINTOSERVER
	  public static final int C_OPCODE_DELETEINVENTORYITEM = 138; // 휴지통에 아이템 삭제
	  public static final int C_OPCODE_BOARDWRITE = 141; // 게시판 쓰기
	  public static final int C_OPCODE_BOARDDELETE = 153; // 게시글 삭제
	  public static final int C_OPCODE_SHOP_N_WAREHOUSE = 161; // 상점 결과 처리. 대만: C_OPCODE_RESULT
	  public static final int C_OPCODE_DELETECHAR = 162; // 케릭터 삭제
	  public static final int C_OPCODE_USEITEM = 164; // 아이템 사용 부분
	  public static final int C_OPCODE_BOOKMARK = 165; // [/기억 OO]
	  public static final int C_OPCODE_EXCLUDE = 171; // [/차단]
	  public static final int C_OPCODE_EXIT_GHOST = 173; // 무한대전 관람모드 탈출
	  public static final int C_OPCODE_RESTART_AFTER_DIE = 177; // 겜중에 죽어서 리셋 눌럿을때. 대만: C_OPCODE_RESTART
	  public static final int C_OPCODE_CHATWHISPER = 184; // 귓속 채팅
	  public static final int C_OPCODE_CALL = 185; // CALL버튼 .감시
	  public static final int C_OPCODE_JOINCLAN = 194; // [/가입]
	  public static final int C_OPCODE_CHATPARTY = 199; // 채팅 파티 리스트. 대만: C_OPCODE_CAHTPARTY
	  public static final int C_OPCODE_DELBUDDY = 202; // 친구삭제. 대만: C_OPCODE_DELETEBUDDY
	  public static final int C_OPCODE_WHO = 206; // [/누구]
	  public static final int C_OPCODE_ADDBUDDY = 207; // 친구추가
	  public static final int C_OPCODE_ENTERPORTAL = 219; // 오른쪽 버튼으로 포탈 진입
	  public static final int C_OPCODE_CREATECLAN = 222; // 혈맹 창설
	  public static final int C_OPCODE_SELECTTARGET = 223; // 펫 공격 목표 지정
	  public static final int C_OPCODE_CHANGEHEADING = 225; // 방향 전환 부분
	  public static final int C_OPCODE_WAR = 227; // 전쟁
	  public static final int C_OPCODE_ATTACK = 229; // 일반공격 부분
	  public static final int C_OPCODE_CREATEPARTY = 230; // 파티 초대
	  public static final int C_OPCODE_SHIP = 231; // 배타서 내릴때 나옴
	  public static final int C_OPCODE_CHARACTERCONFIG = 244; // 캐릭인벤슬롯정보
	  public static final int C_OPCODE_REPORT = 254; // 불량 유저 신고(/신고). 대만: C_OPCODE_SENDLOCATION
	  public static final int C_OPCODE_BANPARTY = 255; // 파티 추방
	  
	  
	   
	  //
	   
	  
	  
	  public static final int C_OPCODE_NOTICECLICK = -53; // 공지사항 확인 눌럿을때. 대만: C_OPCODE_COMMONCLICK
	  public static final int C_OPCODE_ADDSERVICE = -72; // 부가아이템
	  public static final int C_OPCODE_WHISPERINCLUDE = -102; // [/포함]
	  public static final int C_OPCODE_EMBLEM = -107; // 문장데이타를 서버에 요청함
	  public static final int C_OPCODE_RETURNTOLOGIN = 1000006; // 다시 로긴창으로 넘어갈때
	  public static final int C_OPCODE_BOARDNEXT = -221; // 게시판 next
	  public static final int C_OPCODE_CLANMATCHING = -228; // 좌측 하단 깃발 클릭시.
	  public static final int C_OPCODE_CLAN = -246; // 가시범위의 혈맹 마크 요청[폴더내 emblem삭제]
	 
	  
	  
	  /** 안쓰는 C 옵코드 */
	  public static final int C_OPCODE_SKILLBUYOK = 44444; // 스킬 구입 OK
	  public static final int C_OPCODE_USEPETITEM = 78555; // 펫 인벤토리 아이템 사용
	  public static final int C_OPCODE_WARTIMESET = 1443; // 공성시간 설정.
	  public static final int C_OPCODE_SOLDIERGIVE = -10;
	  public static final int C_OPCODE_SOLDIERGIVEOK = -11;
	  public static final int C_OPCODE_HOTEL_ENTER = -12;
	  public static final int C_OPCODE_SOLDIERBUY = -13;
	  public static final int C_OPCODE_HORUNOK = -176;
	  public static final int C_OPCODE_HORUN = -183; // 호런
	  public static final int C_OPCODE_SECURITYSTATUS = -125;
	  public static final int C_OPCODE_SECURITYSTATUSSET = -149;
	   public static final int C_OPCODE_WARTIMELIST = -151; // 공성시간 리스트


	  
	  
	  
	  /** SERVER OPCODE */
	  
	  public static final int S_OPCODE_DEPOSIT = 4; // 공금 입금
	  public static final int S_OPCODE_INVLIST = 5; // 인벤토리의 아이템리스트
	  public static final int S_OPCODE_DETELECHAROK = 6; // 케릭 삭제
	  public static final int S_OPCODE_OWNCHARSTATUS = 8; // 케릭 정보 갱신
	  public static final int S_OPCODE_MOVEOBJECT = 10; // 이동 오브젝트
	  public static final int S_OPCODE_TRUETARGET = 11; // 트루타겟
	  public static final int S_OPCODE_ADDITEM = 15; // 아이템 생성[아이템 떨궜다가먹기]
	  public static final int S_OPCODE_SOUND = 22; // 사운드 이팩트 부분
	  public static final int S_OPCODE_SKILLBUY = 23; // 스킬 구입 창
	  public static final int S_OPCODE_ITEMSTATUS = 24; // 인벤 아이템 갱신
	  public static final int S_OPCODE_ITEMAMOUNT = 24; // 인벤내 아이템 수량 정보 바꾸기(흑단쓰면 ITEMSTATUS와 같이옴)
	  public static final int S_OPCODE_ATTACKPACKET = 30; // 공격 표현 부분
	  public static final int S_OPCODE_MPUPDATE = 33; // MP 업데이트
	  public static final int S_OPCODE_LAWFUL = 34; // 라우풀
	  public static final int S_OPCODE_TRADEADDITEM = 35; // 거래창 아이템 추가 부분
	  public static final int S_OPCODE_ABILITY = 36; // 이반, 소반  인프라 사용
	  public static final int S_OPCODE_SPMR = 37; // sp와 mr변경
	  public static final int S_OPCODE_SHOWHTML = 39; // Npc클릭 Html열람
	  public static final int S_OPCODE_LIGHT = 40; // 밝기
	  public static final int S_OPCODE_RANGESKILLS = 42; // 파톰 어퀘등의 스킬
	  public static final int S_OPCODE_CHANGENAME = 46; // 오브젝트 네임변경시
	  public static final int S_OPCODE_CURSEBLIND = 47; // 눈멀기 효과
	  public static final int S_OPCODE_NOTICE = 48; // 공지 대만: S_OPCODE_COMMONNEWS
	  public static final int S_OPCODE_TRADE = 52; // 거래창 부분
	  public static final int S_OPCODE_SELECTTARGET = 54; // 펫 공격 목표지정
	  public static final int S_OPCODE_SKILLSOUNDGFX = 55; // 이팩트 부분 (헤이스트등)
	  public static final int S_OPCODE_TELEPORT = 566; // 텔레포트
	  public static final int S_OPCODE_DELETEINVENTORYITEM = 57; // 인벤토리 아이템 삭제
	  public static final int S_OPCODE_PINKNAME = 60; // 보라돌이
	  public static final int S_OPCODE_RETURNEDSTAT = 64; //v 스텟 초기화 길이. 대만: S_OPCODE_CHARRESET
	  public static final int S_OPCODE_SHOWSHOPSELLLIST = 65; // 상점에 판매 부분
	  public static final int S_OPCODE_SKILLBRAVE = 67; // 용기
	  public static final int S_OPCODE_BOARD = 68; // 게시판
	  public static final int S_OPCODE_CASTLEMASTER = 69; // 성소유목록 세팅
	  public static final int S_OPCODE_SHOWSHOPBUYLIST = 70; // 상점 구입 부분
	  public static final int S_OPCODE_SERVERMSG = 71; // 서버 메세지[방어구중복으로체크]
	  public static final int S_OPCODE_WHISPERCHAT = 73; // 귓속말
	  public static final int S_OPCODE_POLY = 76; // 변신
	  public static final int S_OPCODE_NORMALCHAT = 81; // 일반 채팅
	  public static final int S_OPCODE_SELECTLIST = 83; // 무기수리
	  public static final int S_OPCODE_WAR = 84; // 전쟁
	  public static final int S_OPCODE_RESURRECTION = 85; // 부활 처리 부분
	  public static final int S_OPCODE_SHOWOBJ = 87; // 오브젝트 그리기. 대만: S_OPCODE_CHARPACK
	  public static final int S_OPCODE_BOOKMARKS = 92; // 기억 리스트
	  public static final int S_OPCODE_CHARLIST = 93; //v 케릭터리스트의 케릭정보
	  public static final int S_OPCODE_NEWCHARWRONG = 98; // 캐릭터 생성시 처리부분
	  public static final int S_OPCODE_ITEMNAME = 100; // 아이템 착용 (E표시)
	  public static final int S_OPCODE_LIQUOR = 103; // 술
	  public static final int S_OPCODE_EFFECTLOCATION = 106; // 트랩 (좌표위 이펙트)
	  public static final int S_OPCODE_TRADESTATUS = 112; // 거래 취소, 완료
	  public static final int S_OPCODE_WEATHER = 115; // 날씨 조작하기
	  public static final int S_OPCODE_EMBLEM = 118; // 클라에 혈문장 요청
	  public static final int S_OPCODE_CHARVISUALUPDATE = 119; // 무기 착,탈 부분
	  public static final int S_OPCODE_REMOVE_OBJECT = 120; // 오브젝트 삭제 (토글etc)
	  public static final int S_OPCODE_CHANGEHEADING = 122; // 방향 전환 부분
	  public static final int S_OPCODE_GAMETIME = 123; // 게임 시간
	  public static final int S_OPCODE_BLESSOFEVA = 126; // 에바 아이콘
	  public static final int S_OPCODE_NEWCHARPACK = 127; // 케릭 새로 만든거 보내기
	  public static final int S_OPCODE_INPUTAMOUNT = 136; // 수량성 아이템 제작 갯수
	  public static final int S_OPCODE_SERVERVERSION = 139; // 서버버전
	  public static final int S_OPCODE_PRIVATESHOPLIST = 140; // 개인상점 물품 열람
	  public static final int S_OPCODE_DRAWAL = 141; // 공금 출금
	  public static final int S_OPCODE_BOARDREAD = 148; // 게시판 읽기
	  public static final int S_OPCODE_INITPACKET = 150; // Key패킷에서의 opcode부분을 차지한다. 에바로 따지면 키 패킷에서  가장 앞에 이 숫자를 두면 된다.
	  public static final int S_OPCODE_OWNCHARSTATUS2 = 155; // 스테이터스 갱신(디크리즈,민투) 
	  public static final int S_OPCODE_HOUSELIST = 156; // 아지트 리스트
	  public static final int S_OPCODE_DOACTIONGFX = 158; // 액션 부분(맞는모습등)
	  public static final int S_OPCODE_DELSKILL = 160; // 스킬 삭제 (정령력 제거)
	  public static final int S_OPCODE_NPCSHOUT = 161; // 샤우팅 글
	  public static final int S_OPCODE_ADDSKILL = 164;  // 스킬 추가[버프패킷박스 다음]
	  public static final int S_OPCODE_POISON = 165; // 독과 굳은 상태 표현
	  public static final int S_OPCODE_STRUP = 166; // 힘업
	  public static final int S_OPCODE_INVIS = 171; // 투명
	  public static final int S_OPCODE_OWNCHARATTRDEF = 174; // AC 및 속성방어 갱신
	  public static final int S_OPCODE_SHOWRETRIEVELIST = 176; // 창고 리스트
	  public static final int S_OPCODE_CHARAMOUNT = 178; //v 해당 계정의 케릭 갯수
	  public static final int S_OPCODE_CHARTITLE = 183; // 호칭 변경
	  public static final int S_OPCODE_TAXRATE = 185; // 세율 조정
	  public static final int S_OPCODE_LETTER = 186; // 편지 읽기. 대만: S_OPCODE_MAIL
	  public static final int S_OPCODE_HOUSEMAP = 187; // 아지트 맵
	  public static final int S_OPCODE_DEXUP = 188; // 덱스업
	  public static final int S_OPCODE_PARALYSIS = 202; // 행동 제한 (커스패럴 상태)
	  public static final int S_OPCODE_MAPID = 206; // 맵 아이디
	  public static final int S_OPCODE_UNDERWATER = 206;
	  public static final int S_OPCODE_ATTRIBUTE = 209; // 위치값을 이동가능&불가능 조작 부분
	  public static final int S_OPCODE_SKILLICONSHIELD = 216; // 쉴드
	  public static final int S_OPCODE_YES_NO = 219; // [ Y , N ] 메세지
	  public static final int S_OPCODE_UNKNOWN1 = 223; // 접속담당 대만: S_OPCODE_LOGINTOGAME
	  public static final int S_OPCODE_HPUPDATE = 225; // HP 업데이트
	  public static final int S_OPCODE_DISCONNECT = 227; // 해당 케릭 강제 종료
	  public static final int S_OPCODE_LOGINRESULT = 233; // 로그인 처리의대한 답변
	  public static final int S_OPCODE_HPMETER = 237; // 미니 HP표현 부분
	  public static final int S_OPCODE_ITEMCOLOR = 240; // 봉인 주문서
	  public static final int S_OPCODE_MSG = 243; // 시스템 메세지 (전챗). 대만: S_OPCODE_GLOBALCHAT
	  public static final int S_OPCODE_IDENTIFYDESC = 245; // 확인주문서
	  public static final int S_OPCODE_PACKETBOX = 250; //v 통합 패킷 관리 담당
	  public static final int S_OPCODE_SKILLHASTE = 255; // 헤이스트
	  
	 
	  
	  public static final int S_OPCODE_SPOLY = 908;
	  /////////////////////////
	  
	  
	  
	  public static final int S_OPCODE_DRAGONPERL = 31;
	  public static final int S_OPCODE_BOOKMARK_LOAD                 = 33;
	  public static final int S_OPCODE_PETCTRL = 33; //펫에 대한 정보를 보여주기 
	  public static final int S_OPCODE_NEWMASTER = 66; //서먼뽑고 리스후 재접
	  public static final int S_OPCODE_CLANMATCHING = 192;//혈맹매칭
	  

	  

	  public static final int S_OPCODE_EXP = 1000001; // 경험치 갱신
	  public static final int S_OPCODE_WARTIME = 1000003;
	  public static final int S_OPCODE_BLUEMESSAGE = 1000005; // 피케이 횟수 메시지[REDMESSAGE]
	  public static final int S_OPCODE_HORUN = 1000006; // 호런 마법 배우기창
	  public static final int S_OPCODE_USEMAP = 1000007;
	  public static final int S_OPCODE_HOTELENTER = 1000008;
	  public static final int S_OPCODE_SHORTOFMATERIAL = 1000009;
	  public static final int S_OPCODE_SOLDIERBUYLIST = 1000010; // 성 용병 고용
	  public static final int S_OPCODE_SOLDIERGIVE = 1000011; // 선택한 용병 주기
	  public static final int S_OPCODE_SOLDIERGIVELIST = 1000012; // 고용한 용병을 배치
	  public static final int S_OPCODE_HIRESOLDIER = 22224;





}