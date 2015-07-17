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
package l1j.server.server.model;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javolution.util.FastMap;
import javolution.util.FastTable;
import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.RaceRecordTable;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1LittleBugInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1RaceTicket;

// Referenced classes of package l1j.server.server.model:
// L1BugBearRace

public class L1BugBearRace {

	public final FastTable<L1LittleBugInstance> _littleBug = new FastTable<L1LittleBugInstance>();

	private final FastTable<L1NpcInstance> _merchant = new FastTable<L1NpcInstance>();

	private final FastMap<Integer, L1RaceTicket> _ticketPrice = new FastMap<Integer, L1RaceTicket>();

	public static final int STATUS_NONE = 0;

	public static final int STATUS_READY = 1;

	public static final int STATUS_PLAYING = 2;

	public boolean buyTickets = true;

	private static final int[] startX = { 33522, 33520, 33518, 33516, 33514 };

	private static final int[] startY = { 32861, 32863, 32865, 32867, 32869 };

	private static final int[][] movingCount = { { 45, 4, 5, 6, 50 },
			{ 42, 6, 5, 7, 50 }, { 39, 8, 5, 8, 50 }, { 36, 10, 5, 9, 50 },
			{ 33, 12, 5, 10, 50 } };

	private static final int[] heading = { 6, 7, 0, 1, 2 };

	private static final Random _random = new Random();

	private int[] _betting = new int[5];

	private int _round;

	private int _roundId;

	private int _bugRaceStatus;

	private boolean _goal;

	private static L1BugBearRace _instance;

	public static L1BugBearRace getInstance() {
		if (_instance == null) {
			_instance = new L1BugBearRace();
		}
		return _instance;
	}

	private L1BugBearRace() {
		for (L1Object obj : L1World.getInstance().getObject()) {
			if (obj instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (npc.getNpcTemplate().get_npcId() == 70041
						|| npc.getNpcTemplate().get_npcId() == 70035
						|| npc.getNpcTemplate().get_npcId() == 70042) {
					_merchant.add(npc);
				}
			}
		}
		loadingGame();
	}

	public void loadingGame() {
		
		/**■■■■■ 경기 시작 시간 계산 부분<START>■■■■■**/
	    Calendar calender;
        calender = Calendar.getInstance();  
        int hour, minute, ampm;
        hour = calender.get(Calendar.HOUR);
        minute = calender.get(Calendar.MINUTE) + Config.BRTIME;
        ampm = calender.get(Calendar.AM_PM);
        String ampmm = "";
        
        if (minute > 60)
        {
            minute = minute - 60;
            hour = hour + 1;
            
        }
        if (hour > 12)
        {
            hour = 1;
            if (ampm == 0)
            {
                ampm = 1;
            }
            if (ampm == 1)
            {
                ampm = 0;
            }
            
        }
        if (0 == ampm)
        {
            ampmm = "오전";
        }
        else if (1 == ampm)
        { 
            ampmm = "오후";
        }
		/**■■■■■ 경기 시작 시간 계산 부분<END>■■■■■**/
        
		clearBug();
		setRoundId(ObjectIdFactory.getInstance().nextId());
		setRound(getRound() + 1);
		clearBetting();
		_goal = false;
		storeBug();
		closeDoor();
		setBugRaceStatus(STATUS_READY);
		broadCastTime("$376 " + Config.BRTIME + " $377");
		L1ReadyThread rt = new L1ReadyThread();
		GeneralThreadPool.getInstance().execute(rt);
	}

	private void storeBug() {
		int arr[] = new int[5];
		for (int i = 0; i < 5; i++) {
			arr[i] = _random.nextInt(20);
			for (int j = 0; j < i; j++) {
				if (arr[i] == arr[j]) {
					arr[i] = _random.nextInt(20);
					i = i - 1;
				}
			}
		}

		L1Npc npcTemp = NpcTable.getInstance().getTemplate(100000);
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);
		for (int i = 0; i < 5; i++) {
			L1LittleBugInstance bug = new L1LittleBugInstance(npcTemp, arr[i],
					startX[i], startY[i]);
			RaceRecordTable.getInstance().getRaceRecord(arr[i], bug);
			float winpoint = 0;
			float record = bug.getWin() + bug.getLose();
			if (record == 0) {
				winpoint = 0;
			} else {
				winpoint = bug.getWin() / record * 100;
			}
			bug.setWinPoint(nf.format(winpoint));
			_littleBug.add(bug);
		}
	}

	private void setSpeed() {
		for (L1LittleBugInstance bug : _littleBug) {
			int pulsSpeed = 0;
			int condition = bug.getCondition();
			if (condition == L1LittleBugInstance.GOOD) {
				pulsSpeed = 30;
			} else if (condition == L1LittleBugInstance.NORMAL) {
				pulsSpeed = 50;
			} else if (condition == L1LittleBugInstance.BAD) {
				pulsSpeed = 80;
			}
			bug.setPassispeed(bug.getPassispeed() + _random.nextInt(pulsSpeed));
		}
	}
		
	//버경멘트 전체챗에 나오는거 주석
	/*	try {
			int searchCount = 0;
			int winIdex = -1;
			int passispeed = 1000;
			String condition = "";

			FastTable<String> msg = new FastTable<String>();
			
			for (L1LittleBugInstance littlebug : L1BugBearRace.getInstance()._littleBug) {
				switch (littlebug.getCondition()) {
				case 0:
					condition = "좋음";
					break;
				case 1:
					condition = "보통";
					break;
				case 2:
					condition = "나쁨";
					break;
				default:
				}

				msg.add("* " + condition + ", " + littlebug.getPassispeed() + ", " + littlebug.getNameId() + ", " + littlebug.getDividend());

				if (littlebug.getPassispeed() <= passispeed) {
					passispeed = littlebug.getPassispeed();
					winIdex = searchCount;
				}

				searchCount++;
			}
			searchCount = 0;
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				if (pc.isGm()) {
					pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
					for (String message : msg) {
						if (searchCount == winIdex) {
							pc.sendPackets(new S_SystemMessage(message + " \\fY(우승후보)"));
						} else {
							pc.sendPackets(new S_SystemMessage(message));
						}
						searchCount++;
					}
					pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
				}
				searchCount = 0;
			}				
		} catch (Exception e) {

		}
	}*/

	public void startRace() {
		setBugRaceStatus(STATUS_PLAYING);
		buyTickets = false;
		calcDividend();
		setSpeed();
		openDoor();
		int i = 0;
		for (L1LittleBugInstance bug : _littleBug) {
			L1BugBearRacing bbr = new L1BugBearRacing(bug, i++);
			GeneralThreadPool.getInstance().execute(bbr);
		}
	}

	private void clearBug() {
		for (L1LittleBugInstance bug : _littleBug) {
			bug.deleteMe();
		}
		_littleBug.clear();
	}

	private void openDoor() {
		for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
			if (door.getGfxId().getGfxId() == 1487) {
				door.open();
			}
		}
	}

	private void closeDoor() {
		for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
			if (door.getGfxId().getGfxId() == 1487) {
				door.close();
			}
		}
	}

	private void broadCastTime(String chat) {
		for (L1NpcInstance npc : _merchant) {
			Broadcaster.wideBroadcastPacket(npc, new S_NpcChatPacket(npc, chat,
					2));
		}
	}

	private void broadCastWinner(String winner, float dividend) {
		String chat = "제 " + getRound() + "$366" + " '" + winner + "("
		+ dividend + ")" + "' " + "$367";
		for (L1NpcInstance npc : _merchant) {
			if (npc.getNpcTemplate().get_npcId() == 70041) { // 세실
				Broadcaster.wideBroadcastPacket(npc, new S_NpcChatPacket(npc,
						chat, 2));
			}
		}
		//L1World.getInstance().broadcastServerMessage("\\fT" + chat);
	}

	private void calcDividend() {//배당
		float[] dividend = new float[5];
		L1LittleBugInstance[] bugs = getBugsArray();
		float allBetting = 0;
		for (int b : _betting) {
			allBetting += b;
		}

		for (int i = 0; i < 5; i++) {
			if (_betting[i] == 0) {
				dividend[i] = 0;
			} else {
				dividend[i] = allBetting / _betting[i];
				if (dividend[i] > 10000) {
					dividend[i] = 10000;
				}
			}
			bugs[i].setDividend(dividend[i]);
		}
	}

	public String getTicketName(int i) {
		L1LittleBugInstance bug = _littleBug.get(i);
		return new StringBuilder().append(getRound()).append("-").append(
				bug.getNumber() + 1).append(" ").append(bug.getName())
				.toString();
	}

	public int[] getTicketInfo(int i) {
		return new int[] { getRoundId(), getRound(),
				_littleBug.get(i).getNumber() };
	}

	public float getTicketPrice(L1ItemInstance item) {
		L1RaceTicket ticket = _ticketPrice.get(item.getSecondId());
		if (ticket == null) {
			return 0;
		}
		if (ticket.getWinner() == item.getTicketId()) {
			return ticket.getDividend();
		}

		return 0;
	}

	public String[] makeStatusString() {
		FastTable<String> status = new FastTable<String>();
		for (L1LittleBugInstance bug : _littleBug) {
			status.add(bug.getName());
			if (bug.getCondition() == L1LittleBugInstance.GOOD) {
				status.add("$368");
			} else if (bug.getCondition() == L1LittleBugInstance.NORMAL) {
				status.add("$369");
			} else if (bug.getCondition() == L1LittleBugInstance.BAD) {
				status.add("$370");
			}
			status.add(bug.getWinPoint() + "%");
		}
		return status.toArray(new String[status.size()]);
	}
//	private String[] winment = {"아싸~ 1등~~~", "에헤라디야~~~ 1등~~", "아이 윈~", 
//			"에헤 >.< 제가 1등이네요~", "내가 1등인기라~", "골~ 인~"};
	private synchronized void finish(L1LittleBugInstance bug) {
		if (!_goal) {
			_goal = true;
			L1RaceTicket ticket = new L1RaceTicket(bug.getNumber(),
					bug.getDividend());
			_ticketPrice.put(getRoundId(), ticket);
			broadCastWinner(bug.getNameId(), bug.getDividend());
//			Broadcaster.broadcastPacket(bug, new S_NpcChatPacket(bug, winment[_random.nextInt(winment.length)], 0));
			setBugRaceStatus(STATUS_NONE);
			RaceRecordTable.getInstance().updateRaceRecord(bug.getNumber(),
					bug.getWin() + 1, bug.getLose());
			L1WaitingTimer wt = new L1WaitingTimer();
			wt.begin();
		} else {
			RaceRecordTable.getInstance().updateRaceRecord(bug.getNumber(),
					bug.getWin(), bug.getLose() + 1);
		}
	}

	private L1LittleBugInstance[] getBugsArray() {
		return _littleBug.toArray(new L1LittleBugInstance[_littleBug.size()]);
	}

	public synchronized void addBetting(int num, int count) {
		if (getBugRaceStatus() == STATUS_READY) {
			_betting[num] += count;
		}
	}

	private void clearBetting() {
		_betting = new int[5];
	}

	public void setRound(int i) {
		_round = i;
	}

	public int getRound() {
		return _round;
	}

	public void setRoundId(int i) {
		_roundId = i;
	}

	public int getRoundId() {
		return _roundId;
	}

	private void setBugRaceStatus(int i) {
		_bugRaceStatus = i;
	}

	public int getBugRaceStatus() {
		return _bugRaceStatus;
	}

	class L1WaitingTimer extends TimerTask {
		@Override
		public void run() {
			loadingGame();
		}

		public void begin() {//버경재셋팅시간
			Timer _timer = new Timer();
			_timer.schedule(this, 50 * 1000);
		}
	}
	
	class L1ReadyThread implements Runnable
    {

        public void run()
        {
            buyTickets = true;
            broadCastTime("레이스 표 판매가 시작되었습니다.");
            
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers())
            {
                if (pc.isbugment())
                {
                    //pc.sendPackets(new S_SystemMessage("\\fT버그베어 레이스 표 판매가 시작되었습니다."));
                }
            }
            
            for (int time = Config.BRTIME; time > 0; time--)
            {
                if (time <= Config.BRTIME)
                {
                    broadCastTime("$376 " + time + " $377");
                }
                try
                {
                    Thread.sleep(60000);
                }
                catch (Exception e)
                {
                }
            }
            buyTickets = false;
            broadCastTime("레이스표 판매가 종료되었습니다.");
            
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers())
            {
                if (pc.isbugment())
                {
                   //pc.sendPackets(new S_SystemMessage("\\fT버그베어 레이스 표 판매가 종료되었습니다."));
                }
            }
            L1BroadCastDividend bcd = new L1BroadCastDividend();
            GeneralThreadPool.getInstance().execute(bcd);
        }
    }

	class L1BroadCastDividend implements Runnable {
		private L1NpcInstance _npc;

		public L1BroadCastDividend() {
			for (L1NpcInstance npc : _merchant) {
				if (npc.getNpcTemplate().get_npcId() == 70041) { // 퍼킨
					_npc = npc;
				}
			}
		}

		public void run() {
			Broadcaster.wideBroadcastPacket(_npc, new S_NpcChatPacket(_npc,"$363", 2));
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			Broadcaster.wideBroadcastPacket(_npc, new S_NpcChatPacket(_npc,"$364", 2));
			startRace();
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(1);
			nf.setMinimumFractionDigits(1);
			for (L1LittleBugInstance bug : _littleBug) {
				String chat = bug.getName() + " $402 "
						+ nf.format(bug.getDividend());
				Broadcaster.wideBroadcastPacket(_npc, new S_NpcChatPacket(_npc,
						chat, 2));
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		}
	}

	class L1BugBearRacing implements Runnable {
		private L1LittleBugInstance _bug;

		private int _num;

		public L1BugBearRacing(L1LittleBugInstance bug, int num) {
			_bug = bug;
			_num = num;
		}

//		private String[] missment = {"아...읔..걸려넘어졌다..", "아...읔..밀려넘어졌다..", "아...읔..다리가꼬였다..", 
//			"살좀 빼야지....", "악!! 이.. 이런 돌뿌리..", "헉.."};
		public void run() {
			int i = 0;
			int count = movingCount[_num][i];
			while (true) {
				if (count == 0) {
					count = movingCount[_num][++i];
				}
				if (_random.nextInt(150) == 0) {
//					Broadcaster.broadcastPacket(_bug, new S_NpcChatPacket(_bug, missment[_random.nextInt(missment.length)], 0));
//					Broadcaster.broadcastPacket(_bug, new S_DoActionGFX(_bug.getId(), 30));
					try {
						Thread.sleep(3360);
					} catch (Exception e) {
					}
				} else {
					count--;
					_bug.setDirectionMove(heading[i]);
					try {
						Thread.sleep(_bug.getPassispeed());
					} catch (Exception e) {
					}
				}
				if (_bug.getX() == 33527) {
					finish(_bug);
					break;
				}
			}
		}
	}
}