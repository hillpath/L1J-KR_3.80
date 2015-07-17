package l1j.server.server;


public class GameServerSetting extends Thread {
	private static GameServerSetting _instance;

	public static GameServerSetting getInstance() {
		if (_instance == null) {
			_instance = new GameServerSetting();
		}
		return _instance;
	}

	/** Server Manager 1 ���� �κ� * */
	 public static boolean isNormal = false;
	 public static boolean isWishper = false;
	 public static boolean isGlobal = false;
	 public static boolean isClan = false;
	 public static boolean isChatParty = false;
	 public static boolean isShop = false; 
	 public static boolean isParty = false;

	public static boolean Att = false;

	public static boolean NYEvent = false;

	public static boolean ServerDown = false;

	// /////////�̺�Ʈ/////////////////
	public static boolean event = false;

	public static String evenMoment = "";

	public static int eventItemCode = 0;

	public static int eventMapItems = 0;

	// /////////�̺�Ʈ/////////////////
	// ///////////��������/////////////
	private int maxLevel = 100;

	public int get_maxLevel() {
		return maxLevel;
	}

	public void set_maxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	// ///////////��������/////////////
	private GameServerSetting() {
	}

	public void run() {
		while (true) {
			try {
				sleep(1000L);
			} catch (Exception e) {
			}
		}
	}

}
