package l1j.server.server;

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class ScheduledFutureWrapper implements ScheduledFuture<Object> {
	private final ScheduledFuture<?> future;

	public ScheduledFutureWrapper(ScheduledFuture<?> future) {
		this.future = future;
	}

	public long getDelay(TimeUnit unit) {
		return future.getDelay(unit);
	}

	public int compareTo(Delayed o) {
		return future.compareTo(o);
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		return future.cancel(false);
	}

	public Object get() throws InterruptedException, ExecutionException {
		return future.get();
	}

	public Object get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		return future.get(timeout, unit);
	}

	public boolean isCancelled() {
		return future.isCancelled();
	}

	public boolean isDone() {
		return future.isDone();
	}
}