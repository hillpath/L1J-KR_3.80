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

package l1j.server.server.templates;

public class L1CharSoldier {

	public L1CharSoldier(int charId) {
		_charId = charId;
	}

	private int _charId;

	public int getCharId() {
		return _charId;
	}

	public void setCharId(int i) {
		_charId = i;
	}

	private int _npcid;

	public int getSoldierNpc() {
		return _npcid;
	}

	public void setSoldierNpc(int i) {
		_npcid = i;
	}

	private int _count;

	public int getSoldierCount() {
		return _count;
	}

	public void setSoldierCount(int i) {
		_count = i;
	}

	private int _castleid;

	public int getSoldierCastleId() {
		return _castleid;
	}

	public void setSoldierCastleId(int i) {
		_castleid = i;
	}

	private int _time;

	public int getSoldierTime() {
		return _time;
	}

	public void setSoldierTime(int i) {
		_time = i;
	}
}