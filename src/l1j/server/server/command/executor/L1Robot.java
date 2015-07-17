package l1j.server.server.command.executor;

import java.util.logging.Logger;
import l1j.server.server.model.Instance.L1PcInstance;

public class L1Robot implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Robot.class.getName());

	private L1Robot() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Robot();
	}

	
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		
	}
	
	/**
	 * ·£´ý ÇÔ¼ö
	 * @param lbound
	 * @param ubound
	 * @return
	 */
	static public int random(int lbound, int ubound) {
		return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
	}

}
