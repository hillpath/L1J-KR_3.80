package l1j.server.server;

/**
 * @author NB4L1
 */
public class ExecuteWrapper implements Runnable {
	private final Runnable runnable;

	public ExecuteWrapper(Runnable runnable) {
		this.runnable = runnable;
	}

	public final void run() {
		ExecuteWrapper.execute(runnable,
				getMaximumRuntimeInMillisecWithoutWarning());
	}

	protected long getMaximumRuntimeInMillisecWithoutWarning() {
		return Long.MAX_VALUE;
	}

	public static void execute(Runnable runnable) {
		execute(runnable, Long.MAX_VALUE);
	}

	public static void execute(Runnable runnable,
			long maximumRuntimeInMillisecWithoutWarning) {
		long begin = System.nanoTime();

		try {
			runnable.run();
		} catch (RuntimeException e) {
		} finally {
		}
	}
}