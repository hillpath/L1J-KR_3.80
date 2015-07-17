/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
/*
 * $Header: /cvsroot/l2j/L2_Gameserver/java/net/sf/l2j/Server.java,v 1.5 2004/11/19 08:54:43 l2chef Exp $
 *
 * $Author: l2chef $
 * $Date: 2004/11/19 08:54:43 $
 * $Revision: 1.5 $
 * $Log: Server.java,v $
 * Revision 1.5  2004/11/19 08:54:43  l2chef
 * database is now used
 *
 * Revision 1.4  2004/07/08 22:42:28  l2chef
 * logfolder is created automatically
 *
 * Revision 1.3  2004/06/30 21:51:33  l2chef
 * using jdk logger instead of println
 *
 * Revision 1.2  2004/06/27 08:12:59  jeichhorn
 * Added copyright notice
 */
package server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.LogManager;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import server.mina.LineageCodecFactory;

public class Server {
	private volatile static Server uniqueInstance;

	// private static Logger _log = Logger.getLogger(Server.class.getName());
	private static final String LOG_PROP = "./config/log.properties";// 로그 설정
																		// 파일

	private Server() {
	}

	public static Server createServer() {
		if (uniqueInstance == null) {
			synchronized (Server.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new Server();
				}
			}
		}
		return uniqueInstance;
	}

	public void start() {
		CodeLogger.getInstance(); /////////////////매니져추가
		initLogManager();
		initDBFactory();
		try {
			PerformanceTimer timer = new PerformanceTimer();
            if(Config.GAME_SERVER_TYPE < 0){//추가.디비삭제 안되게
			clearDB();
			System.out.println("OK! " + timer.get() + " ms");
			timer.reset();
			System.out.print("Delete DB Data - 2...");
			clearDB();
			System.out.println("OK! " + timer.get() + " ms");
			timer = null;
			}else{
			}
		} catch (SQLException e) {}
		startGameServer();
		startLoginServer();
}

	private void addLogger(DefaultIoFilterChainBuilder chain) throws Exception {
		chain.addLast("logger", new LoggingFilter());
		System.out.println("SERVER ON");
	}

	private void startLoginServer() {
		try {
			PerformanceTimer timer = new PerformanceTimer();
			System.out.print("LoginController Loading...");
			LoginController.getInstance().setMaxAllowedOnlinePlayers(
					Config.MAX_ONLINE_USERS);
			System.out.println("OK! " + timer.get() + " ms");
			NioSocketAcceptor acceptor = new NioSocketAcceptor();
			DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

			// 암호화 클레스 등록
			chain.addLast("codec", new ProtocolCodecFilter(
					new LineageCodecFactory()));

			// 로거로 기본 세팅
			if (Config.LOGGER) {
				addLogger(chain);
			} else {
				System.out.println("Logger OFF!");
			}
			System.out.print("SocketAcceptor Binding...");
			// Bind
			acceptor.setReuseAddress(true);
			acceptor.setHandler(new LineageProtocolHandler());
			acceptor.bind(new InetSocketAddress(Config.GAME_SERVER_PORT));
			System.out.println("OK! " + timer.get() + " ms");
			System.out.println("LoginServer Loading Complete!");
			System.out.println("==================================================");
			System.out.println("==Server Port Status [ "+ Config.GAME_SERVER_PORT +" ]Port==");
			System.out.println("==================================================");
			timer = null;
			int num = Thread.activeCount();
			System.out.println("[Activate Threads] : [ 총 "+ num +"개 ]");
			System.out.println("▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩");
			System.out.println(" ::::::: "+ Config.SERVER_NAME +" Starting the server ");
			System.out.println("▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩▩");

		} catch (Exception e) { /* e.printStackTrace(); */
		}
		;
		// FIXME StrackTrace하면 error
	}

	public void shutdown() {
		// loginServer.shutdown();
		GameServer.getInstance().shutdown();
		// System.exit(0);
	}

	private void initLogManager() {
		File logFolder = new File("log");
		logFolder.mkdir();

		try {
			InputStream is = new BufferedInputStream(new FileInputStream(
					LOG_PROP));
			LogManager.getLogManager().readConfiguration(is);
			is.close();
		} catch (IOException e) {
			// _log.log(Level.SEVERE, "Failed to Load " + LOG_PROP + " File.",
			// e);
			System.exit(0);
		}
		try {
			Config.load();
		} catch (Exception e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			System.exit(0);
		}
	}

	private void initDBFactory() {// L1DatabaseFactory 초기설정
		L1DatabaseFactory.setDatabaseSettings(Config.DB_DRIVER, Config.DB_URL,
				Config.DB_LOGIN, Config.DB_PASSWORD);
		try {
			L1DatabaseFactory.getInstance();
		} catch (Exception e) { /* e.printStackTrace(); */
		}
		;
		// FIXME StrackTrace하면 error
	}

	private void startGameServer() {
		try {
			GameServer.getInstance().initialize();
		} catch (Exception e) {
		}
		;
	}

	/*
	 * private void startTelnetServer() { if (Config.TELNET_SERVER) {
	 * TelnetServer.getInstance().start(); } }
	 */
	public void clearDB() throws SQLException{

		Connection c = null;
		PreparedStatement p = null;
		try {
			c = L1DatabaseFactory.getInstance().getConnection();
			p = c.prepareStatement("call deleteData(?)");
			p.setInt(1, Config.DELETE_DB_DAYS);
			p.execute();
		} catch (Exception e) {
		} finally {
			SQLUtil.close(p);
			SQLUtil.close(c);
		}
	}
}
