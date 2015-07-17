/**
 * 타이머 관련 맵에 대한 컨트롤러
 * 2008. 12. 04
 */

package server.controller;

import javolution.util.FastTable;

import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1TimeMap;

public class TimeMapController extends Thread {

	private FastTable<L1TimeMap> mapList;
	private static TimeMapController instance;	

	public static TimeMapController getInstance() {
		if (instance == null)
			instance = new TimeMapController();
		return instance;
	}

	private TimeMapController() {
		super("TimeMapController");
		mapList = new FastTable<L1TimeMap>();
	}

	@Override
	public void run() {
		try {
			while (true) {
				for (L1TimeMap timeMap : array()) {
					if (timeMap.count()) {
						for (L1PcInstance pc : L1World.getInstance()
								.getAllPlayers()) {
							switch (pc.getMapId()) {
							case 73:
							case 74:
								L1Teleport.teleport(pc, 34056, 32279,
										(short) 4, 5, true);
								break;
							case 460:
							case 461:
							case 462:
							case 463:
							case 464:
							case 465:
							case 466:
								L1Teleport.teleport(pc, 32664, 32855,
										(short) 457, 5, true);
								break;
							case 470:
							case 471:
							case 472:
							case 473:
							case 474:
								L1Teleport.teleport(pc, 32663, 32853,
										(short) 467, 5, true);
								break;
							case 475:
							case 476:
							case 477:
							case 478:
								L1Teleport.teleport(pc, 32660, 32876,
										(short) 468, 5, true);
								break;
							default:
								break;
							}
						}
						DoorSpawnTable.getInstance().getDoor(timeMap.getDoor())
								.close();
						remove(timeMap);
					}
				}
				Thread.sleep(1000L);
			}
		} catch (Exception e) {
			TimeMapController sTemp = new TimeMapController();
			copy(FastTable(), sTemp.FastTable());
			clear();
			sTemp.start();
			e.printStackTrace();
		}
	}

	/**
	 * 타임 이벤트가 있는 맵 등록 중복 등록이 되지 않도록 이미 등록된 맵 아이디와 비교 없다면 등록 사이즈가 0 이라면 즉 초기라면
	 * 비교대상이 없기때문에 무조건 등록
	 * 
	 * @param (TimeMap)
	 *            등록할 맵 객체
	 */
	public void add(L1TimeMap map) {
		if (mapList.size() > 0) {
			for (L1TimeMap m : array()) {
				if (m.getId() != map.getId()) {
					mapList.add(map);
					break;
				}
			}
		} else
			mapList.add(map);
	}

	/**
	 * 타임 이벤트가 있는 맵 삭제 중복 삭제 또는 IndexOutOfBoundsException이 되지 않도록 이미 등록된 맵 아이디와
	 * 비교 있다면 삭제
	 * 
	 * @param (TimeMap)
	 *            삭제할 맵 객체
	 */
	private void remove(L1TimeMap map) {
		for (L1TimeMap m : array()) {
			if (m.getId() == map.getId()) {
				mapList.remove(map);
				break;
			}
		}
		map = null;
	}

	/**
	 * 컨트롤러 리스트 초기화 게임서버 종료시 요청(가급적으로 사용중지)
	 */
	private void clear() {
		mapList.clear();
	}

	/**
	 * 등록된 이벤트 맵 배열 리턴
	 * 
	 * @return (TimeMap[]) 맵 객체 배열
	 */
	private L1TimeMap[] array() {
		return mapList.toArray(new L1TimeMap[mapList.size()]);
	}

	/**
	 * 컨트롤러 리스트 객체(Exception 오류시 복사용)
	 * @return	(FastTable<TimeMap>)	맵 저장 리스트
	*/
	private FastTable<L1TimeMap> FastTable(){
		return mapList;
	}

	/**
	 * 컨트롤러 예외 처리시 등록된 맵 이벤트를 유지시키기 위해 리스트 객체 복사 향상된 for 문을 이용하되 예외 발생시 기존 for
	 * 문을 이용하여 복사
	 * 
	 * @param (FastTable
	 *            <TimeMap>) src 원본 리스트
	 * @param (FastTable
	 *            <TimeMap>) desc 복사될 리스트
	 */
	private void copy(FastTable<L1TimeMap> src, FastTable<L1TimeMap> desc){
		try {
			for (L1TimeMap map : src.toArray(new L1TimeMap[mapList.size()])) {
				if (!desc.contains(map))
					desc.add(map);
			}
		} catch (Exception e) {
			L1TimeMap map = null;
			int size = src.size();
			for (int i = 0; i < size; i++) {
				map = src.get(i);
				if (!desc.contains(map))
					desc.add(map);
			}
		}
	}
}