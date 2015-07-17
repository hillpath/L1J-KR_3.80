package l1j.server.GameSystem.Papoo;

import java.util.Timer;
import java.util.TimerTask;

public class PaPooTimer extends TimerTask {

	private final PaPooRaid _ar;

	private final int _type;

	private final int _timeMillis;

	public PaPooTimer(PaPooRaid ar, int type, int timeMillis) {
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