package l1j.server.server.TimeController;

import java.util.Collection;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class WitsTimeController {
	public static ConcurrentHashMap<String, L1PcInstance> _witsWinPlayers;
	public static boolean witsGameStarted = false; 
	public static int witsGameCount = 0;
	private static final Timer _timer = new Timer();
	private static WitsTimeController _instance;
	private checkChatTime _ChatRegen;
	public boolean isWits = false;	

	public static synchronized WitsTimeController getInstance() {
		if (_instance == null) {
			_instance = new WitsTimeController();
			_witsWinPlayers = new ConcurrentHashMap<String, L1PcInstance>();
		}
		return _instance;
	}
	
	private Collection<L1PcInstance> _witsWinPlayersValues;
	
	public Collection<L1PcInstance> getWitsWinPlayers() {
		Collection<L1PcInstance> vs = _witsWinPlayersValues;
		return (vs != null) ? vs : (_witsWinPlayersValues = Collections.unmodifiableCollection(_witsWinPlayers.values()));
	}

	public void startcheckChatTime(int count) {
		if (!isWits) {
			witsGameCount = count;
			_ChatRegen = new checkChatTime();
			// 실행 메소드 후 3초후 시작 다음 시작 시간은 1000* 60 1분 * 3
			_timer.scheduleAtFixedRate(_ChatRegen, 3000, 1000 * 60 * 3);
			isWits = true;
		}

	}

	public void stopcheckChatTime() {
		if (isWits) {
			witsGameStarted = false;
			_witsWinPlayers.clear();
			_ChatRegen.cancel();
			isWits = false;

		}
	}
	
	public static int witsCount = 0; 

	private class checkChatTime extends TimerTask {

		// @Override
		public void run() {// 여기가 시작됨
			try {
				if (witsGameCount == 0) {
					stopcheckChatTime();
					return;
				}

				Thread.sleep(1000);
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("[공지] 잠시후 눈치 게임이 진행됩니다."));
				Thread.sleep(1000);
				L1World.getInstance().set_worldChatElabled(false);
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("[공지] 안전하게 중복없이 숫자를 치신 분들은 선물 지급."));
				Thread.sleep(5000);
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("[공지] 자 긴장하시고 3초 카운트합니다."));
				Thread.sleep(1000);
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("[공지] 3"));
				Thread.sleep(1000);
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("[공지] 2"));
				Thread.sleep(1000);
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("[공지] 1"));
				Thread.sleep(1000);
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("[공지] 눈치게임 고고싱~"));
				Thread.sleep(500);
				L1World.getInstance().set_worldChatElabled(true);
				// 시작을 알리는 변수 지정
				witsGameStarted = true;				
				witsGameCount--;
				
			} catch (Exception e) {
				witsGameStarted = false;// 예외를 던지면 종료
			}

		}

	}
}