package l1j.server.GameSystem.Antaras;

import java.util.Timer;
import java.util.TimerTask;

public class AntarasRaidTimer extends TimerTask {

	private final AntarasRaid _ar;

	private final int _type;

	private final int _timeMillis;

	public AntarasRaidTimer(AntarasRaid ar, int type, int timeMillis) {
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