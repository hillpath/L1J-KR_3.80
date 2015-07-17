package l1j.server.server.TimeController;

import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;

public class DevilController extends Thread {

	private static DevilController _instance;

	private boolean _DevilStart;

	public boolean getDevilStart() {
		return _DevilStart;
	}

	public void setDevilStart(boolean Devil) {
		_DevilStart = Devil;
	}

	private static long sTime = 0;

	public boolean isGmOpen = false; // 추가

	private String NowTime = "";

	// 시간 간격
	private static final int LOOP = 3;

	private static final SimpleDateFormat s = new SimpleDateFormat("HH",
			Locale.KOREA);

	private static final SimpleDateFormat ss = new SimpleDateFormat(
			"MM-dd HH:mm", Locale.KOREA);

	public static DevilController getInstance() {
		if (_instance == null) {
			_instance = new DevilController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(1000);
				/** 오픈 * */
				if (!isOpen() && !isGmOpen)
					continue;
				if (L1World.getInstance().getAllPlayers().size() <= 0)
					continue;

				isGmOpen = false;

				/** 오픈 메세지 * */
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\f3악마왕의 영토가 열렸습니다."));
				/** 악마왕영토 시작* */
				setDevilStart(true);

				/** 실행 1시간 시작* */

				Thread.sleep(3800000L); // 3800000L 1시간 10분정도

				/** 1시간 후 자동 텔레포트* */
				TelePort();
				close(); //추가
				Thread.sleep(5000L);
				TelePort2();

				/** 종료 * */
				End();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 오픈 시각을 가져온다
	 * 
	 * @return (Strind) 오픈 시각(MM-dd HH:mm)
	 */
	public String OpenTime() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(sTime);
		return ss.format(c.getTime());
	}

	/**
	 * 영토가 열려있는지 확인
	 * 
	 * @return (boolean) 열려있다면 true 닫혀있다면 false
	 */
	private boolean isOpen() {
		NowTime = getTime();
		if ((Integer.parseInt(NowTime) % LOOP) == 0)
			return true;
		return false;
	}

	/**
	 * 실제 현재시각을 가져온다
	 * 
	 * @return (String) 현재 시각(HH:mm)
	 */
	private String getTime() {
		return s.format(Calendar.getInstance().getTime());
	}

	/** 아덴마을로 팅기게* */
	private void TelePort() {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			switch (c.getMap().getId()) {
			case 5167:
				c.stopHpRegenerationByDoll();
				c.stopMpRegenerationByDoll();
				L1Teleport.teleport(c, 33430, 32797, (short) 4, 4, true);
				c.sendPackets(new S_SystemMessage("악마왕의 영토가 닫혔습니다."));
				break;
			default:
				break;
			}
		}
	}
	/**캐릭터가 죽었다면 종료시키기**/
	 private void close() {
	  for(L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
	   if (pc.getMap().getId() == 5167 && pc.isDead()) {
	    pc.stopHpRegenerationByDoll();
	    pc.stopMpRegenerationByDoll();
	    pc.sendPackets(new S_Disconnect());
	   }
	  }
	 }

	/** 아덴마을로 팅기게* */
	private void TelePort2() {
		for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			switch (c.getMap().getId()) {
			case 5167:
				c.stopHpRegenerationByDoll();
				c.stopMpRegenerationByDoll();
				L1Teleport.teleport(c, 33430, 32797, (short) 4, 4, true);
				c.sendPackets(new S_SystemMessage("악마왕의 영토가 닫혔습니다."));
				break;
			default:
				break;
			}
		}
	}

	/** 종료 * */
	private void End() {
		L1World.getInstance().broadcastServerMessage("악마왕의 영토가 사라졌습니다.3시간마다 열립니다.");
		setDevilStart(false);
	}
}
