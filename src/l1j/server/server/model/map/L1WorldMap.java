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
package l1j.server.server.model.map;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.MapReader;
import l1j.server.server.utils.PerformanceTimer;

public class L1WorldMap {
	private static Logger _log = Logger.getLogger(L1WorldMap.class.getName());

	private static L1WorldMap uniqueInstance;

	private Map<Integer, L1Map> _maps;

	public static L1WorldMap getInstance() {
		if (uniqueInstance != null)
			return uniqueInstance;
		return createInstance();
	}

	public static L1WorldMap createInstance() {
		if (uniqueInstance == null) {
			synchronized (L1WorldMap.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new L1WorldMap();
				}
			}
		}
		return uniqueInstance;
	}

	private L1WorldMap() {
		init();
	}

	private void init() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("[L1WorldMap] loading map...");

		MapReader in = MapReader.getDefaultReader();

		try {
			_maps = in.read();
			if (_maps == null) {
				throw new RuntimeException("MAP의 read에 실패");
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1WorldMap[]Error", e);

			System.exit(0);
		}

		System.out.println("OK! " + timer.get() + "ms");
	}

	public L1Map getMap(short mapId) {
		L1Map map = _maps.get((int) mapId);
		if (map == null) {
			map = L1Map.newNull();
		}
		return map;
	}

	/**
	 * 기존 맵을 가져와서 새로운 id로 해서 넣기
	 * 
	 * @param targetId
	 *            가져올 맵 아이디
	 * @param newId
	 *            새로 만들 맵 아이디
	 */
	public void cloneMap(int targetId, int newId) {
		L1Map copymap = null;
		copymap = _maps.get(targetId).copyMap(newId);
		_maps.put(newId, copymap);
	}
}
