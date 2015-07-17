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
package l1j.server.server.model.gametime;

public class GameTime extends BaseTime {
	// 2003년 7월 3일 12:00(UTC)이 1월 1일00:00
	protected static final long BASE_TIME_IN_MILLIS_REAL = 1057233600000L;

	@Override
	protected long getBaseTimeInMil() {
		return BASE_TIME_IN_MILLIS_REAL;
	}

	@Override
	protected int makeTime(long timeMillis) {
		long t1 = timeMillis - getBaseTimeInMil();
		if (t1 < 0) {
			throw new IllegalArgumentException();
		}
		int t2 = (int) ((t1 * 6) / 1000L);
		int t3 = t2 % 3; // 시간이 3의 배수가 되도록(듯이) 조정
		return t2 - t3;
	}
}
