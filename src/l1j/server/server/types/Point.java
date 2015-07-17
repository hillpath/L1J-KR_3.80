/* This program is free software; you can redistribute it and/or modify
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
package l1j.server.server.types;

// import java.util.logging.Logger;

public class Point {
	// private static Logger _log = Logger.getLogger(Point.class.getName());

	protected int _x = 0;

	protected int _y = 0;

	public Point() {
	}

	public Point(int x, int y) {
		_x = x;
		_y = y;
	}

	public Point(Point pt) {
		_x = pt._x;
		_y = pt._y;
	}

	public int getX() {
		return _x;
	}

	public void setX(int x) {
		_x = x;
	}

	public int getY() {
		return _y;
	}

	public void setY(int y) {
		_y = y;
	}

	public void set(Point pt) {
		_x = pt._x;
		_y = pt._y;
	}

	public void set(int x, int y) {
		_x = x;
		_y = y;
	}

	private static final int HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final int HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	/**
	 * 지정된 방향으로 이 좌표를 하나 진행한다.
	 * 
	 * @param heading
	 *            방향(0~7)
	 */
	public void forward(int heading) {
		_x += HEADING_TABLE_X[heading];
		_y += HEADING_TABLE_Y[heading];
	}

	/**
	 * 지정된 방향과 역방향으로 이 좌표를 하나 진행한다.
	 * 
	 * @param heading
	 *            방향(0~7)
	 */
	public void backward(int heading) {
		_x -= HEADING_TABLE_X[heading];
		_y -= HEADING_TABLE_Y[heading];
	}

	/**
	 * 지정된 좌표에의 직선 거리를 돌려준다.
	 * 
	 * @param pt
	 *            좌표를 보관 유지하는 Point 오브젝트
	 * @return 좌표까지의 직선 거리
	 */
	public double getLineDistance(Point pt) {
		long diffX = pt.getX() - this.getX();
		long diffY = pt.getY() - this.getY();
		return Math.sqrt((diffX * diffX) + (diffY * diffY));
	}

	/**
	 * 지정된 좌표까지의 직선 타일수를 돌려준다.
	 * 
	 * @param pt
	 *            좌표를 보관 유지하는 Point 오브젝트
	 * @return 지정된 좌표까지의 직선 타일수.
	 */
	public int getTileLineDistance(Point pt) {
		return Math.max(Math.abs(pt.getX() - getX()), Math.abs(pt.getY()
				- getY()));
	}

	/**
	 * 지정된 좌표까지의 타일수를 돌려준다.
	 * 
	 * @param pt
	 *            좌표를 보관 유지하는 Point 오브젝트
	 * @return 지정된 좌표까지의 타일수.
	 */
	public int getTileDistance(Point pt) {
		return Math.abs(pt.getX() - getX()) + Math.abs(pt.getY() - getY());
	}

	/**
	 * 지정된 좌표가 화면내로 보일까를 돌려주는 플레이어의 좌표를(0,0)이라고 하면 보이는 범위의 좌표는 좌상(2,-15)
	 * 우상(15,-2) 좌하(-15,2) 우하(-2,15)된다. 채팅란에 숨어 안보이는 부분도 화면내에 포함된다.
	 * 
	 * @param pt
	 *            좌표를 보관 유지하는 Point 오브젝트
	 * @return 지정된 좌표가 화면내로 보이는 경우는 true.그렇지 않은 경우는 false.
	 */
	/*
	 * public boolean isInScreen(Point pt) { int dist =
	 * this.getTileDistance(pt); if (dist > 24) { return false; } else if (dist <=
	 * 20) { return true; } else { // 좌우의 화면 외부분을 제외 // 플레이어의 좌표를(15, 15)로 했을
	 * 경우에(0, 0)에 해당하는 좌표로부터의 거리로 판단 // Point pointZero = new Point(this.getX() -
	 * 15, this.getY() - 15); // int dist2 = pointZero.getTileDistance(pt); int
	 * dist2 = Math.abs(pt.getX() - (this.getX() - 15)) + Math.abs(pt.getY() -
	 * (this.getY() - 15)); if (24 <= dist2 && dist2 <= 48) { return true; }
	 * return false; } }
	 */
	public boolean isInScreen(Point pt) {
		int dist = this.getTileDistance(pt);
		if (dist > 22) {
			return false;
		} else if (dist <= 19) {
			return true;
		} else {
			// 좌우의 화면 외부분을 제외
			// 플레이어의 좌표를(15, 15)로 했을 경우에(0, 0)에 해당하는 좌표로부터의 거리로 판단
			// Point pointZero = new Point(this.getX() - 15, this.getY() - 15);
			// int dist2 = pointZero.getTileDistance(pt);
			int dist2 = Math.abs(pt.getX() - (this.getX() - 20))
					+ Math.abs(pt.getY() - (this.getY() - 20));
			if (23 <= dist2 && dist2 <= 56) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 지정된 좌표와 같은 좌표인지를 돌려준다.
	 * 
	 * @param pt
	 *            좌표를 보관 유지하는 Point 오브젝트
	 * @return 지정된 좌표와 같은 좌표인가.
	 */
	public boolean isSamePoint(Point pt) {
		return (pt.getX() == getX() && pt.getY() == getY());
	}

	@Override
	public int hashCode() {
		return 7 * getX() + getY();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Point)) {
			return false;
		}
		Point pt = (Point) obj;
		return (this.getX() == pt.getX()) && (this.getY() == pt.getY());
	}

	@Override
	public String toString() {
		return String.format("(%d, %d)", _x, _y);
	}
}
