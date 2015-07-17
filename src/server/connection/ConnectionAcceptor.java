package server.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


import server.LoginController;

import l1j.server.Config;
import l1j.server.server.datatables.IpTable;

public class ConnectionAcceptor extends Thread /*implements Monitorable*/ {

	private ServerSocket serverSocket;
	private ConnectionCounter connectionCounter;

	private String clientIp;
	
	public ConnectionAcceptor() {
		connectionCounter = new ConnectionCounter();

	}
	
	public void initialize() {
		LoginController.getInstance().setMaxAllowedOnlinePlayers(Config.MAX_ONLINE_USERS);
		
		String serverHostName 	= Config.GAME_SERVER_HOST_NAME;
		int gameServerPort 		= Config.GAME_SERVER_PORT;
		
		if (!"*".equals(serverHostName)) {
			InetAddress inetaddress = null;
			try {
				inetaddress = InetAddress.getByName(serverHostName);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			try {
				serverSocket = new ServerSocket(gameServerPort, 50, inetaddress);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				serverSocket = new ServerSocket(gameServerPort);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.start();
	}

	@Override
	public void run() {
		Socket connection = null;
		

		while (true) {
			try {
				connection = serverSocket.accept();
				clientIp = connection.getInetAddress().getHostAddress();
				
				if(IpTable.getInstance().isBannedIp(clientIp)) continue;
				if (connectionCounter.addIp(clientIp)){

					createClientThread(connection);
					connectionCounter.removeIp(clientIp);
				} else {

					IpTable.getInstance().banIp(clientIp);

				}
			} catch (IOException ioexception) {}
		}
	}

	private void createClientThread(Socket connection) {

	}

	public void shutdown() {
		Thread.interrupted();
	}
}
