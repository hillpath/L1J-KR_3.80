package l1j.server.GameSystem.Lastabard;

import l1j.server.server.model.Getback;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;

public class LastabardData {
	public static int getPos(int mapId, int locX, int locY) {
		int pos = 0;

		if (mapId == 531 && locX == 32757 && locY == 32744)
			pos = 0;
		if (mapId == 531 && locX == 32791 && locY == 32786)
			pos = 1;
		if (mapId == 531 && locX == 32845 && locY == 32857)
			pos = 2;

		if (mapId == 533 && locX == 32859 && locY == 32897)
			pos = 0;
		if (mapId == 533 && locX == 32789 && locY == 32891)
			pos = 1;
		if (mapId == 533 && locX == 32753 && locY == 32811)
			pos = 2;

		return pos;
	}

	public static boolean isFourthFloor(int mobMapId) {
		if (mobMapId == 531 || mobMapId == 533) { // 라던 4층
			return true;
		}
		return false;
	}

	public static int getDelayTime(int mapId) {
		int delayTime = 0;
		switch (mapId) {
		// 20분
		case 452:
		case 454:
		case 455:
		case 456:
		case 471:
		case 472:
		case 475:
		case 476:
		case 477:
		case 478:
		case 492:
		case 495:
		case 531:
			delayTime = 1200; // 60 * 20
			break;
		// 25분
		case 461:
		case 465:
		case 490:
			delayTime = 1500; // 60 * 25
			break;
		// 30분
		case 453:
		case 462:
		case 463:
		case 473:
		case 533:
			delayTime = 1800; // 60 * 30
			break;
		// 35분
		case 466:
		case 474:
		case 493:
		case 494:
		case 496:
			delayTime = 2100; // 60 * 35
			break;
		// 5분
		case 530:
		case 532:
		case 534: // 케이나, 이데아 ,카산드라 죽은 후 시간 제한
			delayTime = 300; // 60 * 5
			break;
		default:
			delayTime = 1200;
		}
		return delayTime;
	}

	public static void doTeleport(int mapId) {
		if (mapId == 0)
			return;
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			if (pc.getMapId() != mapId)
				continue;

			switch (pc.getMapId()) {
			case 534:
				L1Teleport.teleport(pc, 32733, 32872, (short) 468, 5, true); // 장로회의장
				break;
			default:
				// 각 층의 휴식층으로 귀환
				int[] loc = Getback.GetBack_Location(pc, true);
				L1Teleport
						.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
			}
		}
	}

	public static int getPosInMapId(int mobMapId) {
		if (mobMapId == 531)
			return 0;
		if (mobMapId == 533)
			return 1;
		return 0;
	}

	public static int relatedTime(int mapId) {
		if (mapId == 531)
			return 530;
		if (mapId == 533)
			return 532;
		return 0;
	}

}
