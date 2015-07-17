package l1j.server.GameSystem.Rind;

import java.util.Timer;
import java.util.TimerTask;

public class RindRaidTimer extends TimerTask {

	private final RindRaid _ar;

	private final int _type;

	private final int _timeMillis;

	public RindRaidTimer(RindRaid ar, int type, int timeMillis) {
		_ar = ar;
		_type = type;
		_timeMillis = timeMillis;
	}

	@Override
	public void run() {
		_ar.timeOverRun(_type);
		this.cancel();
	}

	public void begin() {
		Timer timer = new Timer();
		timer.schedule(this, _timeMillis);
	}
}