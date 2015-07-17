package server.monitor;

public interface Monitorable {
	public void registerMonitor(Monitor m);
	public void removeMonitor(Monitor m);
	public void notifyMonitors();
}
