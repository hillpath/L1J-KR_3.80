package l1j.server.GameSystem.MiniGame;

import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;

public class DeathMatch extends MiniGame implements Runnable {
	private static DeathMatch instance;

	L1NpcInstance kusan = null;

	L1NpcInstance datoo = null;

	private static final int LIMIT_MIN_PLAYER_COUNT = 5;

	public static int DEATH_MATCH_PLAY_LEVEL = 1;

	private int playerCount;

	public static DeathMatch getInstance() {
		if (instance == null) {
			instance = new DeathMatch();
		}
		return instance;
	}

	public DeathMatch() {
		L1NpcInstance npc = null;
		for (L1Object obj : L1World.getInstance().getObject()) {
			if (obj instanceof L1NpcInstance) {
				npc = (L1NpcInstance) obj;
				if (npc.getNpcId() == 80086) {
					kusan = npc;
				} else if (npc.getNpcId() == 80087) {
					datoo = npc;
				}
			}
		}
		setMiniGameStatus(Status.REST);
	}

	public void run() {
		try {
			while (true) {
				switch (getMiniGameStatus()) {
				case REST:
					Thread.sleep(60000L);
					setMiniGameStatus(Status.ENTERREADY);
					break;
				case ENTERREADY:
					if (DEATH_MATCH_PLAY_LEVEL == 1)
						Broadcaster
								.broadcastPacket(
										kusan,
										new S_NpcChatPacket(
												datoo,
												"30 or less than 51 Deathmatch position is pending participation please",
												0));
					else
						Broadcaster.broadcastPacket(datoo, new S_NpcChatPacket(
								kusan, "52 or more, please Deathmatch position is pending participation", 0));

					Thread.sleep(240000L);
					if (getEnterMemberCount() < LIMIT_MIN_PLAYER_COUNT) {
						ClearMiniGame();
					} else {
						sendMessage();
						clearEnterMember();
						setMiniGameStatus(Status.READY);
					}
					break;
				case READY:
					Thread.sleep(120000L);
					if (getPlayerMemberCount() < LIMIT_MIN_PLAYER_COUNT) {
						NoReadyMiniGame();
						ClearMiniGame();
					} else {
						ReadyMiniGame();
						Thread.sleep(30000L);
						StartMiniGame();
						setMiniGameStatus(Status.PLAY);
					}
					break;
				case PLAY:
					playerCount = 0;
					loof: for (int i = 0; i < 30; i++) {
						for (int j = 0; j < 20; j++) {
							Thread.sleep(3000L);
							decreaseHp(i * 10);

							if (j % 5 == 0) {
								playerCount = getAlivingPlayerCount();
								sendMessage();
							}

							if (playerCount == 1)
								break loof;
						}
					}
					setMiniGameStatus(Status.END);
					break;
				case END:
					if (playerCount == 1)
						remainOnlyWinner();
					else
						EndMiniGame();
					ClearMiniGame();
					Thread.sleep(3000L);
					break;
				}
			}
		} catch (Exception e) {
		}
	}

	public void EndMiniGame() {
		L1PcInstance pc;
		for (int i = 0; i < getPlayerMemberCount(); i++) {
			pc = playmembers.get(i);
			if (pc.isGhost()) {
				pc.DeathMatchEndGhost();
			} else {
				L1Teleport.teleport(pc, 32624, 32813, (short) 4, 5, true);
			}
			pc.setDeathMatch(false);
			pc.getInventory().storeItem(L1ItemId.DEATHMATCH_SOUVENIR_BOX, 1);
			pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_TIME_CLEAR));
			pc.sendPackets(new S_ServerMessage(1275, null));
		}
	}

	public void ClearMiniGame() {
		clearEnterMember();
		clearPlayerMember();
		setMiniGameStatus(Status.REST);

		DEATH_MATCH_PLAY_LEVEL *= -1;
	}

	public void StartMiniGame() {
		L1Map map = L1WorldMap.getInstance().getMap((short) DEATHMATCH_MAPID);
		L1PcInstance pc;
		for (int i = 0; i < getPlayerMemberCount(); i++) {
			pc = playmembers.get(i);
			pc.setDeathMatch(true);
			int sx = 32625 + _random.nextInt(27);
			int sy = 32885 + _random.nextInt(27);

			if (map.isInMap(sx, sy) && map.isPassable(sx, sy)) {
				L1Teleport.teleport(pc, sx, sy, (short) DEATHMATCH_MAPID, 5,
						true);
			} else {
				L1Teleport.teleport(pc, 32639, 32897, (short) DEATHMATCH_MAPID,
						5, true);
			}
			pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_TIME, 1800));
		}
	}

	public void sendMessage() {
		L1PcInstance pc;
		switch (getMiniGameStatus()) {
		case ENTERREADY:
			for (int i = 0; i < getEnterMemberCount(); i++) {
				pc = entermembers.get(i);
				// ������ġ�� �����Ͻðڽ��ϱ�? (Y/N)
				pc.sendPackets(new S_Message_YN(1268, ""));
			}
			break;
		case PLAY:
			for (int i = 0; i < getPlayerMemberCount(); i++) {
				pc = playmembers.get(i);
				pc.sendPackets(new S_ServerMessage(1274, String
						.valueOf(playerCount)));
			}
			break;
		}
	}

	private void decreaseHp(int j) {
		L1PcInstance pc;
		for (int i = 0; i < getPlayerMemberCount(); i++) {
			pc = playmembers.get(i);
			if (pc.isGhost())
				continue;

			if (pc.getCurrentHp() <= j) {
				pc
						.beginGhost(pc.getX(), pc.getY(),
								(short) pc.getMapId(), true);
				pc.setCurrentHp(pc.getMaxHp());
				pc.sendPackets(new S_ServerMessage(1271));
				continue;
			}
			pc.setCurrentHp(pc.getCurrentHp() - j);
		}
	}

	private int getAlivingPlayerCount() {
		int playerCount = 0;
		L1PcInstance pc;
		for (int i = 0; i < getPlayerMemberCount(); i++) {
			pc = playmembers.get(i);
			if (pc.isGhost())
				continue;

			playerCount++;
		}
		return playerCount;
	}

	public void remainOnlyWinner() {
		L1PcInstance pc;
		for (int i = 0; i < getPlayerMemberCount(); i++) {
			pc = playmembers.get(i);
			if (pc.isGhost()) {
				pc.DeathMatchEndGhost();
			} else {
				pc.sendPackets(new S_ServerMessage(1272, pc.getName()));
				pc.getInventory().storeItem(41402, _random.nextInt(6) + 1);
				if (_random.nextInt(10) <= 2) {
					pc.getInventory().storeItem(
							L1ItemId.DEATHMATCH_WINNER_PIECE, 1);
				}
				L1Teleport.teleport(pc, 32624, 32813, (short) 4, 5, true);
			}
			pc.setDeathMatch(false);
			pc.getInventory().storeItem(L1ItemId.DEATHMATCH_SOUVENIR_BOX, 1);
			pc.sendPackets(new S_PacketBox(S_PacketBox.MINIGAME_TIME_CLEAR));
			pc.sendPackets(new S_ServerMessage(1275, null));
		}
	}

	public void NoReadyMiniGame() {
		L1PcInstance pc;
		for (int i = 0; i < getPlayerMemberCount(); i++) {
			pc = playmembers.get(i);
			// ��� �ּ� �ο��� 5���� �������� �ʾ� ��⸦ ���� ���� �մϴ�. 1000 �Ƶ����� ���� ��Ƚ��ϴ�.
			pc.sendPackets(new S_ServerMessage(1270));
			pc.getInventory().storeItem(40308, 1000); // 1000 �Ƶ��� ����
			L1Teleport.teleport(pc, 32624, 32813, (short) 4, 5, true);
		}
	}

	public void ReadyMiniGame() {
		L1PcInstance pc;
		for (int i = 0; i < getPlayerMemberCount(); i++) {
			pc = playmembers.get(i);
			// ������ġ �������� - ����
			pc.getInventory().storeItem(L1ItemId.DEATHMATCH_POTION_BOX, 1);
			pc.sendPackets(new S_ServerMessage(1269));
		}
	}

	public void addWaitListMember(L1PcInstance pc) {
		if (getMiniGameStatus() == Status.READY) {
			pc.sendPackets(new S_Message_YN(1268, ""));
			return;
		}
		if (!isEnterMember(pc)) {

			if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
				pc.getInventory().consumeItem(L1ItemId.ADENA, 1000);

				addEnterMember(pc);
				pc.sendPackets(new S_ServerMessage(1265, Integer
						.toString(getEnterMemberCount())));
			} else {
				return;
			}

			/*
			 * addEnterMember(pc); ���� �κ� �ּ� pc.sendPackets(new
			 * S_ServerMessage(1265, Integer.toString(getEnterMemberCount())));
			 */
		} else {
			// �̹� ������ġ ���� ����Ǿ��ֽ��ϴ�.
			pc.sendPackets(new S_ServerMessage(1266));
		}
	}

	public void addPlayMember(L1PcInstance pc) {
		if (pc.isInParty()) { // ��Ƽ��
			pc.getParty().leaveMember(pc);
		}

		addPlayerMember(pc);

		L1SkillUse l1skilluse = new L1SkillUse();
		l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc
				.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_LOGIN);

		L1Teleport
				.teleport(pc, 32658, 32899, (short) DEATHMATCH_MAPID, 2, true); // ��
	}

	public void giveBackAdena(L1PcInstance pc) {

		if (isEnterMember(pc)) {
			pc.getInventory().storeItem(40308, 1000); // 1000 �Ƶ��� ����
			removeEnterMember(pc);
			return;
		}


		// pc.getInventory().storeItem(40308, 1000); // 1000 �Ƶ��� ���� / �����ּ�
	}
}
