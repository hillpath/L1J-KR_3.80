package l1j.server.server.model;

import l1j.server.server.model.Instance.L1CastleGuardInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.types.Point;

public class CharPosUtil {

	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	/**
	 * 캐릭터의 정면의 좌표를 돌려준다.
	 * 
	 * @return 정면의 좌표
	 */
	public static int[] getFrontLoc(int x, int y, int heading) {
		int[] loc = new int[2];
		// int x = getX();
		// int y = getY();
		// int heading = getMoveState().getHeading();
		x += HEADING_TABLE_X[heading];
		y += HEADING_TABLE_Y[heading];
		loc[0] = x;
		loc[1] = y;
		return loc;
	}

	/**
	 * 지정된 좌표에 대할 방향을 돌려준다.
	 * 
	 * @param tx
	 *            좌표의 X치
	 * @param ty
	 *            좌표의 Y치
	 * @return 지정된 좌표에 대할 방향
	 */
	public static int targetDirection(L1Character cha, int tx, int ty) {
		float dis_x = Math.abs(cha.getX() - tx); // X방향의 타겟까지의 거리
		float dis_y = Math.abs(cha.getY() - ty); // Y방향의 타겟까지의 거리
		float dis = Math.max(dis_x, dis_y); // 타겟까지의 거리

		if (dis == 0)
			return cha.getMoveState().getHeading();

		int avg_x = (int) Math.floor((dis_x / dis) + 0.59f); // 상하 좌우가 조금 우선인
		// 둥근
		int avg_y = (int) Math.floor((dis_y / dis) + 0.59f); // 상하 좌우가 조금 우선인
		// 둥근

		int dir_x = 0;
		int dir_y = 0;

		if (cha.getX() < tx)
			dir_x = 1;
		if (cha.getX() > tx)
			dir_x = -1;

		if (cha.getY() < ty)
			dir_y = 1;
		if (cha.getY() > ty)
			dir_y = -1;

		if (avg_x == 0)
			dir_x = 0;
		if (avg_y == 0)
			dir_y = 0;

		if (dir_x == 1 && dir_y == -1)
			return 1; // 상
		if (dir_x == 1 && dir_y == 0)
			return 2; // 우상
		if (dir_x == 1 && dir_y == 1)
			return 3; // 오른쪽
		if (dir_x == 0 && dir_y == 1)
			return 4; // 우하
		if (dir_x == -1 && dir_y == 1)
			return 5; // 하
		if (dir_x == -1 && dir_y == 0)
			return 6; // 좌하
		if (dir_x == -1 && dir_y == -1)
			return 7; // 왼쪽
		if (dir_x == 0 && dir_y == -1)
			return 0; // 좌상

		return cha.getMoveState().getHeading();
	}

	/**
	 * 지정된 좌표까지의 직선상에, 장애물이 존재*하지 않는가*를 돌려준다.
	 * 
	 * @param tx
	 *            좌표의 X치
	 * @param ty
	 *            좌표의 Y치
	 * @return 장애물이 없으면 true, 어느 false를 돌려준다.
	 */
	public static boolean glanceCheck(L1Character cha, int tx, int ty) {
		L1Map map = cha.getMap();
		int chx = cha.getX();
		int chy = cha.getY();
		for (int i = 0; i < 15; i++) {
			if ((chx == tx && chy == ty)
					|| (chx == tx && chy + 1 == ty)// 0 0 0 1
					|| (chx == tx && chy - 1 == ty)
					|| (chx + 1 == tx && chy == ty)// 0 -1 1 0
					|| (chx + 1 == tx && chy + 1 == ty)
					|| (chx + 1 == tx && chy - 1 == ty)// 1 1 1 -1
					|| (chx - 1 == tx && chy == ty)
					|| (chx - 1 == tx && chy + 1 == ty)// -1 0 -1 1
					|| (chx - 1 == tx && chy - 1 == ty)) { // -1 -1
				break;
			}

			if (!map.isArrowPassable(chx, chy, targetDirection(cha, tx, ty)))
				return false;

			if (chx < tx && chy == ty) {
				chx++;
			} else if (chx > tx && chy == ty) {
				chx--;
			} else if (chx == tx && chy < ty) {
				chy++;
			} else if (chx == tx && chy > ty) {
				chy--;
			} else if (chx < tx && chy < ty) {
				chx++;
				chy++;
			} else if (chx < tx && chy > ty) {
				chx++;
				chy--;
			} else if (chx > tx && chy < ty) {
				chx--;
				chy++;
			} else if (chx > tx && chy > ty) {
				chx--;
				chy--;
			}
		}

		return true;
	}

	/**
	 * 지정된 좌표에 공격 가능한가를 돌려준다.
	 * 
	 * @param x
	 *            좌표의 X치.
	 * @param y
	 *            좌표의 Y치.
	 * @param range
	 *            공격 가능한 범위(타일수)
	 * @return 공격 가능하면 true, 불가능하면 false
	 */
	public static boolean isAttackPosition(L1Character cha, int x, int y,
			int range) {
		if (range >= 7) {// 원격 무기(7이상의 경우 기울기를 고려하면(자) 화면외에 나온다)
			if (cha.getLocation().getTileDistance(new Point(x, y)) > range)
				return false;
		} else {
			if (cha.getLocation().getTileLineDistance(new Point(x, y)) > range)
				return false;
		}
		if (cha instanceof L1CastleGuardInstance) {
			L1CastleGuardInstance guard = (L1CastleGuardInstance) cha;
			if (guard.getNpcId() == 7000002 || guard.getNpcId() == 4707001) {
				return true;
			}
		}
		return glanceCheck(cha, x, y);
	}

	/**
	 * 캐릭터가 존재하는 좌표가, 어느 존에 속하고 있을까를 돌려준다.
	 * 
	 * @return 좌표의 존을 나타내는 값. 세이프티 존이면 1, 컴배트 존이면―1, 노멀 존이면 0.
	 */
	public static int getZoneType(L1Character cha) {
/*		if(cha.getMapId() == 30
		||cha.getMapId() == 31
		||cha.getMapId() == 32
		||cha.getMapId() == 33
		){ return 0;}*/
		if(cha.getMapId() == 2006){ return -1; }
		 if (cha.getMap().isSafetyZone(cha.getLocation()))
			return 1;
		else if (cha.getMap().isCombatZone(cha.getLocation()))
			return -1;
		else
			return 0;
	}
}