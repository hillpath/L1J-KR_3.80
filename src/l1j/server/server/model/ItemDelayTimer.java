package l1j.server.server.model;

public class ItemDelayTimer {
	private int _delayId;

	private L1Character _cha;

	private long _time;

	public ItemDelayTimer(L1Character cha, int id, int time) {
		_cha = cha;
		_delayId = id;
		_time = time + System.currentTimeMillis();
	}

	public void stopDelayTimer() {
		_cha.removeItemDelay(_delayId);
	}

	public long ItemDelayTime() {
		return _time;
	}

	public L1Character getCha() {
		return _cha;
	}
}