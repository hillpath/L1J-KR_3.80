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
package l1j.server.GameSystem;

import javolution.util.FastTable;
import java.util.Random;

import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_Game_GhostHouse;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;

public class GhostHouse implements Runnable {

	enum Message {
		ENTER, WAIT_START, NOT_ENOUGH_STARTMEMBERS, GAMEEND
	};

	enum Status {
		ENTER, READY, PLAY, END, REST
	};

	private static final int GHOSTHOUSE_MAPID = 5140;

	private static final int LIMIT_MIN_PLAYER_COUNT = 2; // 게임 시작에 필요한 인원 (본섭
															// : 2명)

	private static final int LIMIT_MIN_ENTER_PLAYER_COUNT = 5; // 게임 입장에 필요한 인원
																// (본섭 : 5명)

	private L1PcInstance[] rankList;

	private L1PcInstance[] finishMember;

	private L1DoorInstance openDoorId;

	private int winnerCount = 0;

	private int finishMemberCount;

	private boolean isTimeOver;

	public static Status GhostHouseStatus;

	private static GhostHouse instance;

	private L1Location GOAL_LINE = new L1Location(32871, 32830,
			GHOSTHOUSE_MAPID);

	private final FastTable<L1PcInstance> entermember = new FastTable<L1PcInstance>();

	private final FastTable<L1PcInstance> playmember = new FastTable<L1PcInstance>();

	private static final Random _random = new Random(System.nanoTime());

	public static GhostHouse getInstance() {
		if (instance == null) {
			instance = new GhostHouse();
		}
		return instance;
	}

	public void run() {
		try {
			setStatus(Status.REST);
			while (true) {
				switch (GhostHouseStatus) {
				case ENTER:
					Thread.sleep(60000L);
					sendMessage(Message.ENTER);
					setStatus(Status.READY);
					break;
				case READY:
					Thread.sleep(30000L); // 입장한 후 유저 대기 시간
					finalPlayMemberCheck();
					if (isGotEnoughStartMembers()) {
						sendMessage(Message.WAIT_START);
						setStatus(Status.PLAY);
					} else {
						sendMessage(Message.NOT_ENOUGH_STARTMEMBERS);
						getOutGhostHouse();
						setStatus(Status.REST);
					}
					break;
				case PLAY:
					isTimeOver = false;
					Thread.sleep(3000L);
					clearEnterMember(); // 입장대기 삭제
					doPolyPlayGameMember(); // 게임인원 변신
					Thread.sleep(5000L);
					countDownStartGame(); // 5,4,3,2,1 카운트 다운
					Thread.sleep(5000L);
					checkWinnerCount(); // 승자 체크
					startPlayGameMemberGameTime(); // 게임 참가자들 00:00 시간 시작
					GhostHouseStartDoorOpen();
					// 5분 체크 시작
					int j = 0;
					while (j <= 300) {
						if (getStatus() == Status.END) {
							break;
						}
						Thread.sleep(1000L);
						sortRankList();
						refreshRankList();
						++j;
					}
					// 5분 체크 종료
					if (notWinnerGame())
						isTimeOver = true;
					setStatus(Status.END);
					break;
				case END:
					sendMessage(Message.GAMEEND);
					Thread.sleep(10000L);
					playGameMembersDisplayPacketClear();
					getOutGhostHouse();
					if (finalCheckFinishMember()) {
						giveItemToWinnerMember();
					}
					GhostHouseDoorClose();
					allClear();
					clearFinishMember();
					break;
				case REST:
					Thread.sleep(1000L);
					break;
				default:
					Thread.sleep(1000L);
					break;
				}
			}
		} catch (Exception e) {
		}
	}

	private void allClear() {
		winnerCount = 0;
		finishMemberCount = 0;
		clearEnterMember();
		clearPlayMember();
		setStatus(Status.REST);
	}

	private void sendMessage(Message msg) {
		switch (msg) {
		case ENTER:
			for (L1PcInstance pc : getEnterMemberArray()) {
				if (pc != null) {
					pc.sendPackets(new S_Message_YN(1256, ""));
				}
			}
			break;
		case NOT_ENOUGH_STARTMEMBERS:
			for (L1PcInstance pc : getPlayMemberArray())
				if (pc != null) {
					pc.sendPackets(new S_ServerMessage(1264));
				}
			break;
		case WAIT_START:
			for (L1PcInstance pc : getPlayMemberArray())
				if (pc != null) {
					pc.sendPackets(new S_ServerMessage(1257));
				}
			break;
		case GAMEEND:
			for (L1PcInstance pc : getPlayMemberArray()) {
				if (pc != null) {
					if (isTimeOver)
						pc.sendPackets(new S_ServerMessage(1263));
					else
						pc.sendPackets(new S_PacketBox(
								S_PacketBox.MINIGAME_10SECOND_COUNT));
				}
			}
			break;
		default:
			break;
		}
	}

	public void finalPlayMemberCheck() {
		for (L1PcInstance pc : getPlayMemberArray()) {
			if (pc != null) {
				if (pc.getMapId() != GHOSTHOUSE_MAPID) {
					removePlayMember(pc);
					pc.setHaunted(false);
				}
			}
		}
	}

	private boolean isGotEnoughStartMembers() {
		return (getPlayMembersCount() >= LIMIT_MIN_PLAYER_COUNT) ? true : false;
	}

	private void doPolyPlayGameMember() {
		for (L1PcInstance pc : getPlayMemberArray()) {
			L1PolyMorph.doPoly(pc, 6284, 600, L1PolyMorph.MORPH_BY_LOGIN);
			pc.setHaunted(true);
		}
	}

	private void countDownStartGame() {
		sortRankList();
		refreshRankList();
		for (L1PcInstance pc : getPlayMemberArray()) {
			pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_COUNT));
		}
	}

	private void checkWinnerCount() {
		int PlayerCount = getPlayMembersCount();
		if (PlayerCount <= 4)
			winnerCount = 1;
		else if (5 >= PlayerCount && PlayerCount <= 7)
			winnerCount = 2;
		else if (8 >= PlayerCount && PlayerCount <= 10)
			winnerCount = 3;
		finishMember = new L1PcInstance[winnerCount];
	}

	private void startPlayGameMemberGameTime() {
		for (L1PcInstance pc : getPlayMemberArray()) {
			pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_START_TIME));
		}
	}

	private void GhostHouseStartDoorOpen() {
		L1DoorInstance door = DoorSpawnTable.getInstance().getDoor(3001);
		if (door != null) {
			if (door.getOpenStatus() == ActionCodes.ACTION_Close) {
				door.open();
			}
		}
	}

	private void GhostHouseDoorClose() {
		L1DoorInstance door = null;
		for (L1Object object : L1World.getInstance().getVisibleObjects(5140)
				.values()) {
			if (object instanceof L1DoorInstance) {
				door = (L1DoorInstance) object;
				if (door.getMapId() == GHOSTHOUSE_MAPID
						&& door.getOpenStatus() == ActionCodes.ACTION_Open) {
					door.close();
				}
			}
		}
	}

	private void sortRankList() {
		rankList = getPlayMemberArray();
		int c = rankList.length - 1;
		L1PcInstance data = null;
		for (int i = 0; i < c; i++) {
			for (int j = 0; j < (c - i); j++) {
				if (rankList[j].getLocation().getLineDistance(GOAL_LINE) > rankList[j + 1]
						.getLocation().getLineDistance(GOAL_LINE)) {
					data = rankList[j];
					rankList[j] = rankList[j + 1];
					rankList[j + 1] = data;
				}
			}
		}
	}

	private void refreshRankList() {
		int i = 0;
		for (L1PcInstance pc : rankList) {
			pc.sendPackets(new S_Game_GhostHouse(i++));
		}
	}

	private boolean notWinnerGame() {
		return isPlayingNow() && getPlayMembersCount() != 0 ? true : false;
	}

	private void playGameMembersDisplayPacketClear() {
		for (L1PcInstance pc : getPlayMemberArray()) {
			pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_END));
			pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_TIME_CLEAR));
			pc.setHaunted(false);
		}
	}

	public void pushOpenDoorTrap(int doorid) {
		openDoorId = DoorSpawnTable.getInstance().getDoor(doorid);
		if (openDoorId != null) {
			if (openDoorId.getOpenStatus() == ActionCodes.ACTION_Close)
				openDoorId.open();
		}
	}

	public void pushFinishLineTrap(L1PcInstance pc) {
		if (isPlayingNow())
			setStatus(Status.END);
		if (finishMemberCount > winnerCount)
			return;
		if (finishMember[finishMemberCount] != pc)
			finishMember[finishMemberCount++] = pc;
	}

	private boolean finalCheckFinishMember() {
		return winnerCount <= finishMemberCount ? true : false;
	}

	private void giveItemToWinnerMember() {
		L1ItemInstance Present = ItemTable.getInstance().createItem(
				L1ItemId.MINIGAME_PRESENT);
		for (L1PcInstance pc : finishMember) {
			if (_random.nextInt(10) <= 2) {
				pc.getInventory()
						.storeItem(L1ItemId.GHOSTHOUSE_WINNER_PIECE, 1);
			}
			pc.getInventory().storeItem(L1ItemId.MINIGAME_PRESENT, 1);
			pc.sendPackets(new S_ServerMessage(403, Present.getViewName()));
		}
	}

	private void getOutGhostHouse() {
		L1SkillUse l1skilluse = null;
		for (L1PcInstance pc : getPlayMemberArray()) {
			if (pc != null) {
				if (getStatus() == Status.READY) {
					pc.getInventory().storeItem(40308, 1000); // 1000 아데나 지급
				}
				l1skilluse = new L1SkillUse();
				l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc
						.getId(), pc.getX(), pc.getY(), null, 0,
						L1SkillUse.TYPE_LOGIN);
				int[] loc = Getback.GetBack_Location(pc, true);
				L1Teleport
						.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
			}
		}
	}

	public void addEnterMember(L1PcInstance pc) {
		if (isReadyNow()) {
			pc.sendPackets(new S_Message_YN(1256, ""));
			return;
		}
		if (!isEnterMember(pc)) {
			entermember.add(pc);
			pc.sendPackets(new S_ServerMessage(1253, Integer
					.toString(getEnterMemberCount())));
			if (getStatus() == Status.REST
					&& getEnterMemberCount() >= LIMIT_MIN_ENTER_PLAYER_COUNT) {
				setStatus(Status.ENTER);
			}
		}
	}

	public boolean isReadyNow() {
		return getStatus() == Status.READY ? true : false;
	}

	public boolean isPlayingNow() {
		return getStatus() == Status.PLAY ? true : false;
	}

	private void clearFinishMember() {
		if (finishMember == null)
			return;
		for (int i = 0, c = finishMember.length; i < c; i++) {
			finishMember[i] = null;
		}
	}

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

	public int getEnterMemberCount() {
		return entermember.size();
	}

	public void removeEnterMember(L1PcInstance pc) {
		entermember.remove(pc);
	}

	public void clearEnterMember() {
		entermember.clear();
	}

	public boolean isEnterMember(L1PcInstance pc) {
		return entermember.contains(pc);
	}

	public L1PcInstance[] getEnterMemberArray() {
		return entermember.toArray(new L1PcInstance[getEnterMemberCount()]);
	}

	public L1PcInstance[] getRank() {
		return rankList;
	}

	private void setStatus(Status i) {
		GhostHouseStatus = i;
	}

	private Status getStatus() {
		return GhostHouseStatus;
	}
}