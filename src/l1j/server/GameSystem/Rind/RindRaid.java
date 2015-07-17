/* Eva Pack -http://eva.gg.gg
 * 본섭 리뉴얼된 안타라스 레이드 시스템
 * Eva ShaSha
 */

package l1j.server.GameSystem.Rind;

import java.sql.Timestamp;
import javolution.util.FastTable;

import l1j.server.GameSystem.Rind.RindRaidSystem.RindMsgTimer;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1PcInstance;

public class RindRaid {

	private final FastTable<L1PcInstance> _roomlist1 = new FastTable<L1PcInstance>();

	private final FastTable<L1PcInstance> _roomlist2 = new FastTable<L1PcInstance>();

	private final FastTable<L1PcInstance> _roomlist3 = new FastTable<L1PcInstance>();

	private final FastTable<L1PcInstance> _roomlist4 = new FastTable<L1PcInstance>();

	private final FastTable<L1PcInstance> _antalist = new FastTable<L1PcInstance>();

	private int _step = 0;

	private int _id;

	private Timestamp _endtime;

	private boolean _isRind = false;

	public RindRaid(int id) {
		_id = id;
		_endtime = new Timestamp(System.currentTimeMillis() + 7200000);// 2시간 후
	}

	public void timeOverRun(int type) {
		switch (type) {
		case 0:// 4개 방 20분 타임 종료시
			tel4roomOut();
			break;
		case 1:// 1번째 방 2분 오버 몹 소환
			_step = 1;
			RindMsgTimer room1 = new RindMsgTimer(_id, 1);
			GeneralThreadPool.getInstance().execute(room1);
			break;
		case 2:// 2번째 방 2분 오버 몹 소환
			_step = 2;
			RindMsgTimer room2 = new RindMsgTimer(_id, 2);
			GeneralThreadPool.getInstance().execute(room2);
			break;
		case 3:// 3번째 방 2분 오버 몹 소환
			_step = 3;
			RindMsgTimer room3 = new RindMsgTimer(_id, 3);
			GeneralThreadPool.getInstance().execute(room3);
			break;
		case 4:// 4번째 방 2분 오버 몹 소환
			_step = 4;
			RindMsgTimer room4 = new RindMsgTimer(_id, 4);
			GeneralThreadPool.getInstance().execute(room4);
			break;
		case 5:// 린드
			_isRind = true;
			// 몹 소환
			break;
		case 6:// 방에 있는 사람 모두 텔시킨다
			break;
		}
	}

	private void tel4roomOut() {
		for (int i = 0; i > _roomlist1.size(); i++) {
			L1PcInstance pc = _roomlist1.get(i);
			// 방 아닌곳 좌표 잡아서 마을로텔 시키자
		}
	}

	/** 입장 가능한 방 을 가져온다 */
	public int getRoomNum() {
		int room = 5;
		if (_roomlist1.size() <= 0 && _step < 1) {
			room = 0;
		} else if (_roomlist1.size() <= 8 && _step < 1) {
			room = 1;
		} else if (_roomlist2.size() <= 8 && _step < 2) {
			room = 2;
		} else if (_roomlist3.size() <= 8 && _step < 3) {
			room = 3;
		} else if (_roomlist4.size() <= 8 && _step < 4) {
			room = 4;
		}
		return room;
	}

	/** 레이드 진행 단계를 설정한다 */
	public void setStep(int step) {
		_step = step;
	}

	/** 동굴안에 있는 유저 리스트에 더한다 */
	public void addUser4room(L1PcInstance pc, int room) {
		switch (room) {
		case 0:
		case 1:
			_roomlist1.add(pc);
			break;
		case 2:
			_roomlist2.add(pc);
			break;
		case 3:
			_roomlist3.add(pc);
			break;
		case 4:
			_roomlist4.add(pc);
			break;
		default:
			break;
		}
	}

	/** 해당 리스트를 넘겨준다 */
	public FastTable<L1PcInstance> getRoomList(int num) {
		switch (num) {
		case 1:
			return _roomlist1;
		case 2:
			return _roomlist2;
		case 3:
			return _roomlist2;
		case 4:
			return _roomlist2;
		default:
			return null;
		}
	}

	/** 레어에 진입한 유저 수를 가져온다 */
	public int countLairUser() {
		return _antalist.size();
	}

	/** 레어에 진입할 유저를 넣는다 */
	public void addLairUser(L1PcInstance pc) {
		_antalist.add(pc);
	}

	/** 떴는지 알려준다 */
	public boolean isRind() {
		return _isRind;
	}

	public int getAntaId() {
		return _id;
	}

	public Timestamp getEndTime() {
		return _endtime;
	}
}
