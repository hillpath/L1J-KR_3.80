package l1j.server.server.TimeController;

import javolution.util.FastTable;
import java.util.List;
import java.util.Random;

import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.ActionCodes;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1UbPattern;
import l1j.server.server.model.L1UbSpawn;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.L1GroundInventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.datatables.UBSpawnTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.templates.L1Item;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.utils.L1SpawnUtil;

public class InDunController extends Thread {
	// public class GiranController implements Runnable {
	// public class HuntController implements Runnable{
	// private static Logger _log =
	// Logger.getLogger(HuntController.class.getName());
	private static InDunController _instance;

	/** 이벤트 시작알림 * */

	String[] chat = { "첫번째", "두번째 ", "세번째", "네번째", "다섯번째", "여섯번째", "일곱번째",
			"여덟번째", "아홉번째", "열번째", "열한번째", "마지막" };

	L1NpcInstance hadin = null;

	L1NpcInstance hadin1 = null;

	private boolean _InDunStart = false;// 인던 시작여부

	public boolean getInDunStart() {
		return _InDunStart;
	}

	public void setInDunStart(boolean indun) {
		_InDunStart = indun;
	}

	private boolean _InDunOpen = false; // 인던 시작여부

	public boolean getInDunOpen() {
		return _InDunStart;
	}

	public void setInDunOpen(boolean indun) {
		_InDunOpen = indun;
	}

	private boolean _StartOpenDoor = false; // 무한대전 모드 스타트 문이 열렸는지 여부

	public boolean getStartOpenDoor() {
		return _StartOpenDoor;
	}

	public void setStartOpenDoor(boolean indun) {
		_StartOpenDoor = indun;
	}

	private boolean _EndOpenDoor = false; // 마지막 아이템 지급을 위해 문이 열렸는지 체크

	public boolean getEndOpenDoor() {
		return _EndOpenDoor;
	}

	public void setEndOpenDoor(boolean indun) {
		_EndOpenDoor = indun;
	}

	private boolean _EndTrap = false; // 마지막 아이템 지급을 위해 문이 열렸는지 체크

	public boolean getEndTrap() {
		return _EndTrap;
	}

	public void setEndTrap(boolean indun) {
		_EndTrap = indun;
	}

	private boolean _TellTrap = false; // 텔리포트 트랩들

	public boolean getTellTrap() {
		return _TellTrap;
	}

	public void setTellTrap(boolean indun) {
		_TellTrap = indun;
	}

	private boolean _TrapFour = false; // 첫번째 4트랩 열렸는지 유무

	public boolean getTrapFour() {
		return _TrapFour;
	}

	public void setTrapFour(boolean indun) {
		_TrapFour = indun;
	}

	private boolean _TrapFour1 = false; // 두번째 4트랩 열렸는지 유무

	public boolean getTrapFour1() {
		return _TrapFour1;
	}

	public void setTrapFour1(boolean indun) {
		_TrapFour1 = indun;
	}

	private boolean _TrapFour2 = false; // 세번째 4트랩 열렸는지 유무

	public boolean getTrapFour2() {
		return _TrapFour2;
	}

	public void setTrapFour2(boolean indun) {
		_TrapFour2 = indun;
	}

	private boolean _LastTrapFour = false; // 아이템 지급 트랩

	public boolean getLastTrapFour() {
		return _LastTrapFour;
	}

	public void setLastTrapFour(boolean indun) {
		_LastTrapFour = indun;
	}

	private boolean _GiveItem = false; // 아이템 지급됐는지 유무

	public boolean getGiveItem() {
		return _GiveItem;
	}

	public void setGiveItem(boolean indun) {
		_GiveItem = indun;
	}

	private boolean _FirstDoor = false; // 스타트문

	public boolean getFirstDoor() {
		return _FirstDoor;
	}

	public void setFirstDoor(boolean indun) {
		_FirstDoor = indun;
	}

	private boolean _CenterDoor = false; // 해골방,바포방 문

	public boolean getCenterDoor() {
		return _CenterDoor;
	}

	public void setCenterDoor(boolean indun) {
		_CenterDoor = indun;
	}

	private boolean _LastDoor = false; // 마지막 문

	public boolean getLastDoor() {
		return _LastDoor;
	}

	public void setLastDoor(boolean indun) {
		_LastDoor = indun;
	}

	private final List<L1PcInstance> _pushList = new FastTable<L1PcInstance>();

	private final List<L1PcInstance> _pushList1 = new FastTable<L1PcInstance>();

	private final List<L1PcInstance> _pushList2 = new FastTable<L1PcInstance>();

	private final List<L1PcInstance> _pushList3 = new FastTable<L1PcInstance>();

	private final FastTable<L1PcInstance> playmember = new FastTable<L1PcInstance>();

	public void addPlayMember(L1PcInstance pc) {
		playmember.add(pc);
	}

	public int getPlayMembersCount() {
		return playmember.size();
	}

	public void removePlayMember(L1PcInstance pc) {
		playmember.remove(pc);
	}

	public void clearPlayMember() {
		playmember.clear();
	}

	public boolean isPlayMember(L1PcInstance pc) {
		return playmember.contains(pc);
	}

	public L1PcInstance[] getPlayMemberArray() {
		return playmember.toArray(new L1PcInstance[getPlayMembersCount()]);
	}

	private boolean Close;

	private static Random _random = new Random(System.nanoTime());

	public static InDunController getInstance() {
		if (_instance == null) {
			_instance = new InDunController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {

			setInDunOpen(true);
			setInDunStart(true);
			NpcMSG();
			try {
				Thread.sleep(3000);
			} catch (Exception e) {
			}

			Hadin1MSG(1);
			StarZone(0);
			setTellTrap(true);
			try {
				Thread.sleep(15000);
			} catch (Exception e) {
			}
			MSG(1);
			setTellTrap(false);
			OpenDoor(9000);
			setFirstDoor(true);
			int j = 0;
			while (j <= 1200) {
				if (getStartOpenDoor() == true) {
					break;
				}
				Thread.sleep(1000L);
				StartDoorOpen();
				PushTrap();
				++j;
			}
			if (getStartOpenDoor() == false) {
				End();
			} else {
				try {
					Thread.sleep(60000L);
				} catch (Exception e) {
				}
				MSG(5);
				OpenTheDoor();
				MSG(6);
				HadinMSG(1);
				L1UbPattern pattern = null;
				FastTable<L1UbSpawn> spawnList = null;
				for (int round = 1; round <= 12; round++) {
					if (round == 4) {
						HadinMSG(2);
					}
					if (round == 8) {
						HadinMSG(3);
					}
					if (round == 12) {
						HadinMSG(4);
					}
					try {
						Thread.sleep(30000L);
					} catch (Exception e) {
					}
					pattern = UBSpawnTable.getInstance().getPattern(10, 1);
					spawnList = pattern.getSpawnList(round);
					Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(
							hadin, chat[round - 1] + " 봉인이 해체 되었습니다.[" + round
									+ "/12]", 2));
					Broadcaster.broadcastPacket(hadin, new S_PacketBox(
							S_PacketBox.GREEN_MESSAGE, chat[round - 1]
									+ " 봉인이 해체 되었습니다.[" + round + "/12]"));
					Effect();
					for (L1UbSpawn spawn : spawnList) {
						spawn.spawnAll();
						Thread.sleep(spawn.getSpawnDelay() * 1000);
					}
				}
				Boss();
				try {
					Thread.sleep(5000L);
				} catch (Exception e) {
				}
				Kereness();
				HadinMSG(5);
				LastDoorOpen();
				MSG(7);
				int k = 0;
				while (k <= 300) {
					if (getLastTrapFour() == true) {
						break;
					}
					LastPushTrap();
					Thread.sleep(1000L);
					++k;
				}
				if (getLastTrapFour() == false) {
					End();
				} else {
					MSG(8);
					try {
						Thread.sleep(60000L);
					} catch (Exception e) {
					}
				}
				End();
			}
			_instance = null; // 비정상 종료에 대비해서 인스턴스 초기화
			_InDunStart = false; // 비정상 종료에 대비해서 다시 false로 초기화
			_InDunOpen = false; // 마찬가지죠?
		} catch (Exception e1) {
		}
	}

	private int PatternChoice() {
		int i = _random.nextInt(1);
		return i;
	}

	public void OpenDoor(int i) {
		L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(i);
		if (door != null) {
			if (door.getOpenStatus() == ActionCodes.ACTION_Close) {
				door.open();
			}
		}
	}

	private void OpenTheDoor() { // 바포방 해골방 문열기.
		setCenterDoor(true);
		int[] Door = { 9017, 9016 };
		int j = Door.length;
		for (int i = 0; i < j; i++) {
			OpenDoor(Door[i]);
		}
	}

	private void LastDoorOpen() {
		setLastDoor(true);
		Effect();// 문을 연다.
		int[] Door = { 9033, 9034, 9035, 9036, 9037, 9038, 9039, 9040, 9041,
				9042, 9043, 9045, 9046, 9047, 9048 };
		int j = Door.length;
		for (int i = 0; i < j; i++) {
			OpenDoor(Door[i]);
			try {
				Thread.sleep(500L);
			} catch (Exception e) {
			}
		}
	}

	private void StartDoorOpen() { // 문이 열렸는지 체크
		L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(9015);
		if (door != null) {
			if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				setStartOpenDoor(true);
				Effect();
			}
		}
	}

	private void DoorClose() { // 전체 문닫기
		L1DoorInstance door = null;
		for (L1Object object : L1World.getInstance().getVisibleObjects(9000)
				.values()) {
			if (object instanceof L1DoorInstance) {
				door = (L1DoorInstance) object;
				if (door.getMapId() == 9000
						&& door.getOpenStatus() == ActionCodes.ACTION_Open) {
					setStartOpenDoor(false);
					setEndOpenDoor(false);
					door.close();
				}
			}
		}
	}

	/** 인던 종료로 몬스터 클리어 */
	private void InDunclear() {
		L1MonsterInstance mob = null;
		for (Object obj : L1World.getInstance().getVisibleObjects(9000)
				.values()) {
			if (obj instanceof L1MonsterInstance) {
				mob = (L1MonsterInstance) obj;
				if (!mob.isDead()) {
					mob.setDead(true);
					mob.setActionStatus(ActionCodes.ACTION_Die);
					mob.setCurrentHp(0);
					mob.deleteMe();
				}
			}
		}
	}

	private void HadinMSG(int i) {
		switch (i) {
		case 1:
			try {
				Thread.sleep(15000L);
			} catch (Exception e) {
			}
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"어서들 들어 오게나 시간이 별로 없네.", 0));
			sendMessage("하딘: 어서들 들어 오게나 시간이 별로 없네.");
			try {
				Thread.sleep(40000L);
			} catch (Exception e) {
			}
			setCenterDoor(false);// 여기서 문을 완전히 닫아주자.
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"차원의 경계가 불안정해진 틈을 타. 악한 것들이 몰려 올것이네.", 0));
			sendMessage("하딘: 차원의 경계가 불안정해진 틈을 타. 악한 것들이 몰려 올것이네.");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"모두 준비해 주게나!.", 0));
			sendMessage("하딘: 모두 준비해 주게나!.");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"예상보다 빠르게 다가오고 있네! 조심들 하게!", 0));
			sendMessage("하딘: 예상보다 빠르게 다가오고 있네! 조심들 하게!");
			break;
		case 2:
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"예상보다 빠르게 다가오고 있네! 조심들 하게!", 0));
			sendMessage("하딘: 예상보다 빠르게 다가오고 있네! 조심들 하게!");
			break;
		case 3:
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"악한것들이 또 몰려오고 있네! 준비들하게!", 0));
			sendMessage("하딘: 악한것들이 또 몰려오고 있네! 준비들하게!");
			break;
		case 4:
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"모두들 긴장하게! 거대한 어둠이 다가오네!", 0));
			sendMessage("하딘: 모두들 긴장하게! 거대한 어둠이 다가오네!");
			break;
		case 5:
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"케레니스! 저 마물의 이목을 끌어주게!", 0));
			sendMessage("하딘: 케레니스! 저 마물의 이목을 끌어주게!");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"모두들 수고 많았네.", 0));
			sendMessage("하딘: 모두들 수고 많았네.");
			try {
				Thread.sleep(10000L);
			} catch (Exception e) {
			}
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"케레니스! 뒤는 내게 맡겨도 되네.", 0));
			sendMessage("하딘: 케레니스! 뒤는 내게 맡겨도 되네.");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"케레니스! 어서 정신차려!", 0));
			sendMessage("하딘: 케레니스! 어서 정신차려!");
			try {
				Thread.sleep(10000L);
			} catch (Exception e) {
			}
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"케레니스를 기절시켜라!", 0));
			sendMessage("하딘: 케레니스를 기절시켜라!");
			try {
				Thread.sleep(30000L);
			} catch (Exception e) {
			}
			Effect();
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"이건 무슨 현상이지!", 0));
			sendMessage("하딘: 이건 무슨 현상이지!");
			try {
				Thread.sleep(10000L);
			} catch (Exception e) {
			}
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"이곳이 무너지려 하는것인가? 이제 다 성공했는데!!!", 0));
			sendMessage("하딘: 이곳이 무너지려 하는것인가? 이제 다 성공했는데!!!");
			try {
				Thread.sleep(10000L);
			} catch (Exception e) {
			}
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"모두들 대피하라! 이곳을 봉인하겠다!", 0));
			sendMessage("하딘: 남쪽 출구를 곧 막을테니 어서 빠져나가게!");
			try {
				Thread.sleep(10000L);
			} catch (Exception e) {
			}
			Broadcaster.wideBroadcastPacket(hadin, new S_NpcChatPacket(hadin,
					"남쪽 출구를 곧 막을테니 어서 빠져나가게!", 0));
			sendMessage("하딘: 남쪽 출구를 곧 막을테니 어서 빠져나가게!");

			break;
		}
	}

	private void Hadin1MSG(int i) {
		switch (i) {
		case 1:
			Broadcaster.broadcastPacket(hadin1, new S_NpcChatPacket(hadin1,
					"오 왔는가? 잠시 기다리게나 준비할 것이 있네.", 0));
			sendMessage("하딘: 오 왔는가? 잠시 기다리게나 준비할 것이 있네.");
			try {
				Thread.sleep(10000L);
			} catch (Exception e) {
			}
			Broadcaster.broadcastPacket(hadin1, new S_NpcChatPacket(hadin1,
					"최종 점검을 시작해볼까?.", 0));
			sendMessage("하딘: 최종 점검을 시작해볼까?.");
			try {
				Thread.sleep(10000L);
			} catch (Exception e) {
			}
			Broadcaster.broadcastPacket(hadin1, new S_NpcChatPacket(hadin1,
					"자! 그럼 시작해볼까?", 0));
			sendMessage("하딘: 자! 그럼 시작해볼까?");
			try {
				Thread.sleep(10000L);
			} catch (Exception e) {
			}
			Broadcaster.broadcastPacket(hadin1, new S_NpcChatPacket(hadin1,
					"서두르게. 마법진은 금방 사라질껄세.", 0));
			sendMessage("하딘: 서두르게. 마법진은 금방 사라질껄세.");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			Broadcaster.broadcastPacket(hadin1, new S_NpcChatPacket(hadin1,
					"난 목적지에 먼저 가있도록 하겠네.", 0));
			sendMessage("하딘: 난 목적지에 먼저 가있도록 하겠네.");
			break;
		}
	}

	public void NpcMSG() {
		L1NpcInstance npc = null;
		for (L1Object obj : L1World.getInstance().getObject()) {
			if (obj instanceof L1NpcInstance) {
				npc = (L1NpcInstance) obj;
				if (npc.getNpcId() == 4100037) {
					hadin = npc;
				} else if (npc.getNpcId() == 4100032) {
					hadin1 = npc;
				}
			}
		}
	}

	private void Boss() {
		int i = PatternChoice();
		switch (i) {
		case 0:
			L1SpawnUtil.spawn1(32707, 32846, (short) 9000, 4, 420026, 1, false);
			break;
		case 1:
			L1SpawnUtil.spawn1(32707, 32846, (short) 9000, 4, 420022, 1, false);
			break;
		}
	}

	private void Kereness() {
		L1SpawnUtil.spawn1(32707, 32846, (short) 9000, 4, 4100033, 1, false);
	}

	private void StarZone(int i) {
		switch (i) {
		case 0: // 입장시
			L1SpawnUtil.spawn3(32726, 32725, (short) 9000, 4, 4100039, 1,
					false, 30000);
			break;
		case 1: // 첫번째 법진
			L1SpawnUtil.spawn3(32667, 32818, (short) 9000, 4, 4100039, 1,
					false, 300000);
			break;
		case 2: // 두번째 법진
			L1SpawnUtil.spawn3(32808, 32838, (short) 9000, 4, 4100039, 1,
					false, 300000);
			L1SpawnUtil.spawn3(32789, 32822, (short) 9000, 4, 4100039, 1,
					false, 300000);
			break;
		case 3: // 마지막 검은 법진
			L1SpawnUtil.spawn3(32802, 32868, (short) 9000, 4, 4100044, 1,
					false, 60000);
			break;
		}
	}

	private void sendMessage(String msg) {
		for (L1PcInstance pc : getPlayMemberArray()) {
			pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, msg));
		}
	}

	private void MSG(int i) {
		switch (i) {
		case 1:
			sendMessage("하딘: 이곳은 위험한 곳이니 외부인이 접근하지 못하게");
			sendMessage("하딘: 장치를 해주길 바란다.");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			sendMessage("하딘: 너희들만 볼 수 있는 장치를 해두었으니 잘해주길 바라네");
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			sendMessage("하딘: 그럼  내가 있는곳 까지 최대한 빨리 와주게나.");
			Effect();
			break;
		case 2:
			sendMessage("하딘: 어서 화살 트랩을 설치하고 이쪽으로 와주길");
			sendMessage("하딘: 바라네.너희들에겐 간단한 일 일거네.");
			Effect();
			break;
		case 3:
			sendMessage("하딘: 수고했다! 더 서둘러 주길 바란다 이번것은 더 간단하네.");
			sendMessage("하딘: 이쪽으로 넘어오는 마법진을 만들게.");
			Effect();
			break;
		case 4:
			sendMessage("하딘: 좋아! 이제 문지기를들 처치하고 이쪽으로 넘어오게");
			Effect();
			break;
		case 5:
			sendMessage("하딘: 이제 곧 의식이 시작되네.어서들 이쪽으로 오게.");
			Effect();
			break;
		case 6:
			try {
				Thread.sleep(5000L);
			} catch (Exception e) {
			}
			sendMessage("하딘: 시간이 별로없네. 악한 기운이 지상으로 빠져 나가지 못하게 해야하네");
			break;
		case 7:
			try {
				Thread.sleep(30000L);
			} catch (Exception e) {
			}
			sendMessage("최종지역 중앙 발판을 밟으시면 보상아이템이 지급됩니다.");
			break;
		case 8:
			sendMessage("1분후 과거 여행이 종료됩니다.");
			break;
		}
	}

	private void EndTelePort() { // 퇴장 시킨다.
		for (L1PcInstance c : getPlayMemberArray()) {
			L1Teleport.teleport(c, 32596, 32916, (short) 0, 4, true);
		}
	}

	private void Effect() { // 화면 떨림 이펙트.
		for (L1PcInstance c : getPlayMemberArray()) {
			c.sendPackets(new S_PacketBox(83, 1)); // 보라색
			c.sendPackets(new S_PacketBox(83, 2)); // 떨림
		}
	}

	public void spawnGroundItem(int itemId, int stackCount, int count) {
		L1Item temp = ItemTable.getInstance().getTemplate(itemId);
		if (temp == null) {
			return;
		}
		L1Location loc = null;
		L1ItemInstance item = null;
		L1GroundInventory ground = null;
		short mapid = 9000;
		for (int i = 0; i < count; i++) {
			if (temp.isStackable()) {
				item = ItemTable.getInstance().createItem(itemId);
				item.setEnchantLevel(0);
				item.setCount(stackCount);
				int x = 32796 + _random.nextInt(8);
				int y = 32865 + _random.nextInt(11);
				ground = L1World.getInstance().getInventory(x, y, mapid);
				if (ground.checkAddItem(item, stackCount) == L1Inventory.OK) {
					ground.storeItem(item);
				}
			} else {
				item = null;
				for (int createCount = 0; createCount < stackCount; createCount++) {
					item = ItemTable.getInstance().createItem(itemId);
					item.setEnchantLevel(0);
					int x = 32796 + _random.nextInt(8);
					int y = 32865 + _random.nextInt(11);
					ground = L1World.getInstance().getInventory(x, y, mapid);
					if (ground.checkAddItem(item, stackCount) == L1Inventory.OK) {
						ground.storeItem(item);
					}
				}
			}
		}
	}

	public void addpc(L1PcInstance pc) {
		if (pc == null || _pushList.contains(pc)) {
			return;
		}
		_pushList.add(pc);
	}

	public void removepc(L1PcInstance pc) {
		if (pc == null || !_pushList.contains(pc)) {
			return;
		}
		_pushList.remove(pc);
	}

	public void addpc1(L1PcInstance pc) {
		if (pc == null || _pushList1.contains(pc)) {
			return;
		}
		_pushList1.add(pc);
	}

	public void removepc1(L1PcInstance pc) {
		if (pc == null || !_pushList1.contains(pc)) {
			return;
		}
		_pushList1.remove(pc);
	}

	public void addpc2(L1PcInstance pc) {
		if (pc == null || _pushList2.contains(pc)) {
			return;
		}
		_pushList2.add(pc);
	}

	public void removepc2(L1PcInstance pc) {
		if (pc == null || !_pushList2.contains(pc)) {
			return;
		}
		_pushList2.remove(pc);
	}

	public void addpc3(L1PcInstance pc) {
		if (pc == null || _pushList3.contains(pc)) {
			return;
		}
		_pushList3.add(pc);
	}

	public void removepc3(L1PcInstance pc) {
		if (pc == null || !_pushList3.contains(pc)) {
			return;
		}
		_pushList3.remove(pc);
	}

	public void PushPc(L1PcInstance pc) {
		if (_pushList.size() >= 4 && getTrapFour() == false) {
			StarZone(1);
			OpenDoor(9002); // 4트랩 첫번째
			MSG(2);
			setTrapFour(true);
		}
		if (_pushList1.size() >= 4 && getTrapFour1() == false) {
			int[] Door = { 9022, 9023 };
			int j = Door.length;
			for (int i = 0; i < j; i++) {
				OpenDoor(Door[i]);
			}
			MSG(3);
			setTrapFour1(true);
		}
		if (_pushList2.size() >= 4 && getTrapFour2() == false) {
			StarZone(2);
			setTellTrap(true);
			MSG(4);
			setTrapFour2(true);
		}
	}

	private void PushTrap() {
		for (L1PcInstance pc : getPlayMemberArray()) {
			PushPc(pc);
			if ((pc.getX() == 32666 && pc.getY() == 32819)
					|| (pc.getX() == 32666 && pc.getY() == 32817)
					|| (pc.getX() == 32668 && pc.getY() == 32817)
					|| (pc.getX() == 32668 && pc.getY() == 32819)) {
				addpc(pc);
			} else {
				removepc(pc);
			}
			if ((pc.getX() == 32703 && pc.getY() == 32800)
					|| (pc.getX() == 32703 && pc.getY() == 32791)
					|| (pc.getX() == 32712 && pc.getY() == 32793)
					|| (pc.getX() == 32710 && pc.getY() == 32803)) {
				addpc1(pc);
			} else {
				removepc1(pc);
			}
			if ((pc.getX() == 32809 && pc.getY() == 32839)
					|| (pc.getX() == 32807 && pc.getY() == 32839)
					|| (pc.getX() == 32807 && pc.getY() == 32837)
					|| (pc.getX() == 32809 && pc.getY() == 32837)) {
				addpc2(pc);
			} else {
				removepc2(pc);
			}
		}
	}

	public void LastPushPc(L1PcInstance pc) {
		if (_pushList3.size() >= 4 && getLastTrapFour() == false) {
			StarZone(3);// 법진 스폰
			setLastTrapFour(true);
		}
	}

	private void LastPushTrap() {
		for (L1PcInstance pc : getPlayMemberArray()) {
			LastPushPc(pc);
			if ((pc.getX() == 32798 && pc.getY() == 32872)
					|| (pc.getX() == 32800 && pc.getY() == 32873)
					|| (pc.getX() == 32806 && pc.getY() == 32872)
					|| (pc.getX() == 32807 && pc.getY() == 32870)
					|| (pc.getX() == 32799 && pc.getY() == 32866)
					|| (pc.getX() == 32800 && pc.getY() == 32864)
					|| (pc.getX() == 32808 && pc.getY() == 32864)
					|| (pc.getX() == 32806 && pc.getY() == 32863)) {
				addpc3(pc);
			} else {
				removepc3(pc);
			}
		}
	}

	/** 인던 종료 * */
	private void End() {
		setInDunOpen(false);// 닫는다.
		setInDunStart(false); // 닫는다
		EndTelePort(); // 마을로 텔리포트
		InDunclear(); // 몬스터 처리한다.
		DoorClose(); // 전체 문닫기
		setTellTrap(false);// 텔리포트 트렙 리셋
		setTrapFour(false);// 4개 트랩들 리셋
		setTrapFour1(false);// 4개 트랩들 리셋
		setTrapFour2(false);// 4개 트랩들 리셋
		setLastTrapFour(false); // 마지막 트랩 리셋
		setFirstDoor(false);// 첫번째 문 오픈여부
		setLastDoor(false);// 마지막문 오픈여부
		setCenterDoor(false);// 바포방 해골방..
		clearPlayMember();// 플레이어 리스트 삭제
		Close = false; // 닫는다
L1World.getInstance().broadcastServerMessage("과거의 말하는 섬 던전 2층이 초기화 되었습니다.");
	}
}