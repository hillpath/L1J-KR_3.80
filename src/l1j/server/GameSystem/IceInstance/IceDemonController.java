package l1j.server.GameSystem.IceInstance;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.ActionCodes;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.CommonUtil;
import l1j.server.server.utils.L1SpawnUtil;

public class IceDemonController implements Runnable {
	private static Logger _log = Logger.getLogger(IceDemonController.class
			.getName());
	private static IceDemonController _instance;

	public static IceDemonController getInstance() {
		if (_instance == null) {
			_instance = new IceDemonController();
		}
		return _instance;
	}

	/** ��üũ */
	int[] _MonsterList = { 46119, 46120, 46153, 46154, 46123, 46140 };
	public int _mapid = 0;
	public boolean Start = false;
	private boolean FirstRoom = false;
	private boolean SecondRoom = false;
	private boolean ThirdRoom = false;
	private boolean FourthRoom = false;
	private boolean BossRoom = false;
	private boolean End = false;
	private int Time = 3600;// 900

	public void run() {
		Start = true;
		FirstRoom = true;
		Time = 3600;// 900
		SpawnMonster();
		while (true) {
			try {
				Check();
				if (End) {
					reset();
					L1World.getInstance().broadcastServerMessage(
							"The Ice Demon instance has ended.");
					L1World.getInstance().broadcastServerMessage(
							"Waiting for the next instance entry.");
					break;
				} else if (FirstRoom) {
					First();
				} else if (SecondRoom) {
					Second();
				} else if (ThirdRoom) {
					Third();
				} else if (FourthRoom) {
					Fourth();
				} else if (BossRoom) {
					Boss();
				}
			} catch (Exception e) {
				e.printStackTrace();
				_log.log(Level.SEVERE, "IceDemonController Error", e);
			} finally {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		}
	}

	/** ��ǥ �� ���� �𸣰ٰ� ���� ����������� */
	private void SpawnMonster() {
		int ran = 0;
		/** 1���� �Ѹ��� */
		for (int i = 0; i < 15; i++) {
			ran = CommonUtil.random(_MonsterList.length);
			L1SpawnUtil.spawn5(32765, 32818, (short) 2151, 6,
					_MonsterList[ran], 12, 2, 1);
		}
		L1SpawnUtil.spawn5(32749, 32800, (short) 2151, 6, _MonsterList[ran], 0,
				2, 1);
		L1SpawnUtil.spawn5(32749, 32817, (short) 2151, 6, _MonsterList[ran], 0,
				2, 1);
		L1SpawnUtil.spawn5(32766, 32833, (short) 2151, 6, _MonsterList[ran], 0,
				2, 1);
		L1SpawnUtil.spawn5(32772, 32802, (short) 2151, 6, _MonsterList[ran], 0,
				2, 1);

		/** 2�� �� �Ѹ��� */
		L1SpawnUtil.spawn5(32813, 32805, (short) 2151, 6,
				_MonsterList[CommonUtil.random(_MonsterList.length)], 0, 2, 2);
		L1SpawnUtil.spawn5(32819, 32807, (short) 2151, 6,
				_MonsterList[CommonUtil.random(_MonsterList.length)], 0, 2, 2);
		L1SpawnUtil.spawn5(32819, 32805, (short) 2151, 6,
				_MonsterList[CommonUtil.random(_MonsterList.length)], 0, 2, 2);
		for (int i = 0; i < 15; i++) {
			ran = CommonUtil.random(_MonsterList.length);
			L1SpawnUtil.spawn5(32833, 32806, (short) 2151, 6,
					_MonsterList[ran], 13, 2, 2);
		}

		/** 3�� �� �Ѹ��� */
		L1SpawnUtil.spawn5(32859, 32832, (short) 2151, 6,
				_MonsterList[CommonUtil.random(_MonsterList.length)], 0, 2, 3);
		L1SpawnUtil.spawn5(32857, 32831, (short) 2151, 6,
				_MonsterList[CommonUtil.random(_MonsterList.length)], 0, 2, 3);
		L1SpawnUtil.spawn5(32855, 32834, (short) 2151, 6,
				_MonsterList[CommonUtil.random(_MonsterList.length)], 0, 2, 3);
		for (int i = 0; i < 15; i++) {
			ran = CommonUtil.random(_MonsterList.length);
			L1SpawnUtil.spawn5(32850, 32853, (short) 2151, 6,
					_MonsterList[ran], 10, 2, 3);
		}

		/** 4�� �� �Ѹ��� */
		for (int i = 0; i < 15; i++) {
			ran = CommonUtil.random(_MonsterList.length);
			L1SpawnUtil.spawn5(32767, 32892, (short) 2151, 6,
					_MonsterList[ran], 12, 2, 4);
		}

		/** 5�� �� �Ѹ��� ( ���� ���� �߰� : ���̽� ���� ) */
		for (int i = 0; i < 15; i++) {
			ran = CommonUtil.random(_MonsterList.length);
			L1SpawnUtil.spawn5(32767, 32892, (short) 2151, 6,
					_MonsterList[ran], 12, 2, 5);
		}
		/** ���̽����� */
		L1SpawnUtil.spawn5(32845, 32920, (short) 2151, 6, 46142, 0, 2, 5);
		L1SpawnUtil.spawn5(32833, 32916, (short) 2151, 6, 46119, 0, 1, 5);
		L1SpawnUtil.spawn5(32832, 32926, (short) 2151, 6, 46120, 0, 1, 5);
		L1SpawnUtil.spawn5(32825, 32926, (short) 2151, 6, 46119, 0, 1, 5);
		L1SpawnUtil.spawn5(32819, 32926, (short) 2151, 6, 46120, 0, 1, 5);
		L1SpawnUtil.spawn5(32813, 32927, (short) 2151, 6, 46119, 0, 1, 5);
		L1SpawnUtil.spawn5(32814, 32917, (short) 2151, 6, 46120, 0, 1, 5);
		L1SpawnUtil.spawn5(32819, 32917, (short) 2151, 6, 46119, 0, 1, 5);
		L1SpawnUtil.spawn5(32826, 32914, (short) 2151, 6, 46120, 0, 1, 5);
	}

	private void First() {
		if (_list1.size() > 0) {
			for (int i = _list1.size() - 1; i >= 0; i--) {
				L1NpcInstance npc = _list1.get(i);
				if (npc.getCurrentHp() <= 0) {
					remove(npc, 1);
				}
			}
		} else {
			openDoor(5000);
			/** ù��° �� ����. */
			FirstRoom = false;
			SecondRoom = true;
		}
	}

	private void Second() {
		if (_list2.size() > 0) {
			for (int i = _list2.size() - 1; i >= 0; i--) {
				L1NpcInstance npc = _list2.get(i);
				if (npc.getCurrentHp() <= 0) {
					remove(npc, 2);
				}
			}
		} else {
			openDoor(5001);
			/** �ι�° �� ����. */
			SecondRoom = false;
			ThirdRoom = true;
		}
	}

	private void Third() {
		if (_list3.size() > 0) {
			for (int i = _list3.size() - 1; i >= 0; i--) {
				L1NpcInstance npc = _list3.get(i);
				if (npc.getCurrentHp() <= 0) {
					remove(npc, 3);
				}
			}
		} else {
			openDoor(5002);
			/** ����° �� ����. */
			ThirdRoom = false;
			FourthRoom = true;
		}
	}

	private void Fourth() {
		if (_list4.size() > 0) {
			for (int i = _list4.size() - 1; i >= 0; i--) {
				L1NpcInstance npc = _list4.get(i);
				if (npc.getCurrentHp() <= 0) {
					remove(npc, 4);
				}
			}
		} else {
			openDoor(5003);
			/** �׹�° �� ����. */
			FourthRoom = false;
			BossRoom = true;
		}
	}

	private void Boss() {
		if (_list5.size() > 0) {
			for (int i = _list5.size() - 1; i >= 0; i--) {
				L1NpcInstance npc = _list5.get(i);
				if (npc.getCurrentHp() <= 0) {
					remove(npc, 5);
				}
			}
		} else {
			openDoor(5004);
			/** �ټ���° �� ����. */
			BossRoom = false;
		}
	}

	private void Check() {
		if (Time > 0) {
			Time--;
		}
		if (Time <= 0) {
			End();
		} else if (Time <= 3590) {// 890
			CheckPc();
		}

		if (Time % 60 == 0) {
			int min = Time / 60;
			for (L1Object obj : L1World.getInstance().getVisibleObjects(2151)
					.values()) {
				if (obj instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) obj;
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,min + "Min is forced to go behind the town."));
					CountMob(pc);
				}
			}
		}
	}

	/** ������ ��ȯ���� ������ üũ���� ���� */
	private void CheckPc() {
		int check = 0;
		for (L1Object obj : L1World.getInstance().getVisibleObjects(2151)
				.values()) {
			if (obj instanceof L1PcInstance) {
				check = 1;
			}
		}
		if (check == 0) {
			End();
		}
	}

	/** ������ ��Ʈ�˸��� */
	private void CountMob(L1PcInstance pc) {
		if (FirstRoom) {
			pc.sendPackets(new S_SystemMessage("\\fTThe current number of room remaining mobs are [ "
					+ _list1.size() + " ]The Marie."));
		} else if (SecondRoom) {
			pc.sendPackets(new S_SystemMessage("\\fTThe current number of room remaining mobs are [ "
					+ _list2.size() + " ]The Marie."));
		} else if (ThirdRoom) {
			pc.sendPackets(new S_SystemMessage("\\fTThe current number of room remaining mobs are[ "
					+ _list3.size() + " ]The Marie."));
		} else if (FourthRoom) {
			pc.sendPackets(new S_SystemMessage("\\fTThe current number of room remaining mobs are [ "
					+ _list4.size() + " ]The Marie."));
		} else if (BossRoom & _list5.size() > 0) {
			pc.sendPackets(new S_SystemMessage("\\fTThere is no time. Please come quickly killing the boss"));
		}
	}

	private void End() {
		for (L1Object obj : L1World.getInstance().getVisibleObjects(2151)
				.values()) {
			if (obj instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) obj;
				L1Teleport.teleport(pc, 34052, 32281, (short) 4, 4, true);
			}
		}

		End = true;
	}

	private void reset() {
		Start = false;
		FirstRoom = false;
		SecondRoom = false;
		ThirdRoom = false;
		FourthRoom = false;
		BossRoom = false;
		End = false;
		Time = 0;
		ListClear(1);
		ListClear(2);
		ListClear(3);
		ListClear(4);
		ListClear(5);
		ListClear(6);
		for (L1Object obj : L1World.getInstance().getVisibleObjects(2151)
				.values()) {
			if (obj instanceof L1MonsterInstance) {
				L1MonsterInstance mon = (L1MonsterInstance) obj;
				mon.deleteMe();
			}
		}
		closeDoor();
	}

	private void openDoor(int doorId) {
		L1DoorInstance door = null;
		for (L1Object object : L1World.getInstance().getVisibleObjects(2151)
				.values()) {
			if (object instanceof L1DoorInstance) {
				door = (L1DoorInstance) object;
				if (door.getDoorId() == doorId) {
					if (door.getOpenStatus() == ActionCodes.ACTION_Close) {
						door.open();
					}
				}
			}
		}
	}

	private void closeDoor() {
		L1DoorInstance door = null;
		for (L1Object object : L1World.getInstance().getVisibleObjects(2151)
				.values()) {
			if (object instanceof L1DoorInstance) {
				door = (L1DoorInstance) object;
				if (door.getDoorId() == 5000 || door.getDoorId() == 5001
						|| door.getDoorId() == 5002 || door.getDoorId() == 5003
						|| door.getDoorId() == 5004) {
					if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
						door.close();
					}
				}
			}
		}
	}

	/** ���� ����Ʈ */
	private final ArrayList<L1NpcInstance> _list1 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list2 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list3 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list4 = new ArrayList<L1NpcInstance>();
	private final ArrayList<L1NpcInstance> _list5 = new ArrayList<L1NpcInstance>();

	public void add(L1NpcInstance npc, int type) {
		switch (type) {
		case 1:
			if (npc == null || _list1.contains(npc)) {
				return;
			}
			_list1.add(npc);
			break;
		case 2:
			if (npc == null || _list2.contains(npc)) {
				return;
			}
			_list2.add(npc);
			break;
		case 3:
			if (npc == null || _list3.contains(npc)) {
				return;
			}
			_list3.add(npc);
			break;
		case 4:
			if (npc == null || _list4.contains(npc)) {
				return;
			}
			_list4.add(npc);
			break;
		case 5:
			if (npc == null || _list5.contains(npc)) {
				return;
			}
			_list5.add(npc);
			break;
		}
	}

	private void remove(L1NpcInstance npc, int type) {
		switch (type) {
		case 1:
			if (npc == null || !_list1.contains(npc)) {
				return;
			}
			_list1.remove(npc);
			break;
		case 2:
			if (npc == null || !_list2.contains(npc)) {
				return;
			}
			_list2.remove(npc);
			break;
		case 3:
			if (npc == null || !_list3.contains(npc)) {
				return;
			}
			_list3.remove(npc);
			break;
		case 4:
			if (npc == null || !_list4.contains(npc)) {
				return;
			}
			_list4.remove(npc);
			break;
		case 5:
			if (npc == null || !_list5.contains(npc)) {
				return;
			}
			_list5.remove(npc);
			break;
		}

	}

	private void ListClear(int type) {
		switch (type) {
		case 1:
			_list1.clear();
			break;
		case 2:
			_list2.clear();
			break;
		case 3:
			_list3.clear();
			break;
		case 4:
			_list4.clear();
			break;
		case 5:
			_list5.clear();
			break;
		}
	}
}