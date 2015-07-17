package l1j.server.GameSystem;

import java.util.Calendar;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1World;
import l1j.server.server.model.gametime.BaseTime;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.gametime.TimeListener;
import l1j.server.server.serverpackets.S_SystemMessage;

public class NoticeSystem implements TimeListener {
	private static NoticeSystem _instance;

	public static void start() {
		if (_instance == null) {
			_instance = new NoticeSystem();
		}
		_instance.some();
		RealTimeClock.getInstance().addListener(_instance);
	}

	private void some() {
	}

	private final int ubMsg = 1;

	static class NoticeTimer implements Runnable {
		private int _type = 0;

		private String _msg = null;

		public NoticeTimer(int type, String MSG) {
			_type = type;
			_msg = MSG;
		}

		public void run() {
			try {
				switch (_type) {
				case 1:
					L1World.getInstance().set_worldChatElabled(false);
				//	L1World.getInstance().broadcastPacketToAll(
					//		new S_SystemMessage("�ȳ��ϼ���. ������ �Դϴ�."));
					//Thread.sleep(1000);
					L1World.getInstance().broadcastPacketToAll(
							new S_SystemMessage("After " + _msg + "war of endless town begins."));
					Thread.sleep(1000);
				//	L1World.getInstance().broadcastPacketToAll(
						//	new S_SystemMessage("���Ѵ����� ����ǿ��� ���� ���� �ٶ��ϴ�."));
					//Thread.sleep(1000);
				//	L1World.getInstance().broadcastPacketToAll(
						//	new S_SystemMessage("�����մϴ�."));
					//Thread.sleep(1000);
					L1World.getInstance().set_worldChatElabled(true);
					break;
				case 2:
					break;
				default:
					break;
				}
			} catch (Exception exception) {
			}
		}
	}

	public void onDayChanged(BaseTime time) {
	}

	public void onHourChanged(BaseTime time) {
	}

	public void onMinuteChanged(BaseTime time) {
		int rm = time.get(Calendar.MINUTE);
		int rh = time.get(Calendar.HOUR_OF_DAY);

		if (rm == 55)
			ubStartMSG(rh);
	}

	public void onMonthChanged(BaseTime time) {
	}

	private void ubStartMSG(int hour) {
		String MSG = null;

		switch (hour) {
		case 0:
			MSG = "Talking Island, $2931, $1242";
			break;
		case 2:
		case 8:
		case 14:
		case 20:
			MSG = "$1242";
			break;
		case 3:
		case 6:
		case 9:
		case 12:
		case 15:
		case 18:
		case 21:
			MSG = "Talking Island, $2931";
			break;
		case 1:
		case 4:
		case 7:
		case 10:
		case 13:
		case 16:
		case 19:
			MSG = "Articles, Geulrudin";
			break;
		case 22:
			MSG = "Articles, Geulrudin, $1242";
			break;
		default:
			return;
		}
		NoticeTimer nt = new NoticeTimer(ubMsg, MSG);
		GeneralThreadPool.getInstance().execute(nt);
	}

}
