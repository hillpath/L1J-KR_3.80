/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
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

import javolution.util.FastTable;

public class L1ExcludingList {

	private FastTable<String> _nameList = new FastTable<String>();

	public void add(String name) {
		_nameList.add(name);
	}

	/**
	 * 지정한 이름의 캐릭터를 차단 리스트로부터 삭제한다
	 * 
	 * @param name
	 *            대상의 캐릭터명
	 * @return 실제로 삭제된, 클라이언트의 차단 리스트상의 캐릭터명. 지정한 이름이 리스트에 발견되지 않았던 경우는 null를
	 *         돌려준다.
	 */
	public String remove(String name) {
		for (String each : _nameList) {
			if (each.equalsIgnoreCase(name)) {
				_nameList.remove(each);
				return each;
			}
		}
		return null;
	}

	/**
	 * 지정한 이름의 캐릭터를 차단하고 있는 경우 true를 돌려준다
	 */
	public boolean contains(String name) {
		for (String each : _nameList) {
			if (each.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 차단 리스트가 상한의 16명에 이르고 있을까를 돌려준다
	 */
	public boolean isFull() {
		return (_nameList.size() >= 16) ? true : false;
	}
}
