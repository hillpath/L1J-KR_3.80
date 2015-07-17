/*
 * 2008. 8. 7 by psjump
 * - 서버 구동중 발생하는 다양한 에러들을 문자로 정리하여 이곳에 저장한다.
 * - 1분마다 저장된 정보를 파일에 쓴다.
 *  : 이미 파일이 존재할경우 파일끝 부분에 이어쓴다.
 */
package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javolution.util.FastTable;
import java.util.Timer;
import java.util.TimerTask;

import l1j.server.Config;

public class Logger extends TimerTask {
	public static Logger _instance;

	public static Logger getInstance() {
		if (_instance == null) {
			_instance = new Logger();
			timer = new Timer(false);
			timer.schedule(_instance, 0, 60 * 1000);
		}
		return _instance;
	}

	public Logger() {
		try {
			File f = new File("log");
			if (!f.isDirectory()) {
				f.mkdir();
			}

			f = new File("log/chat");
			if (!f.isDirectory()) {
				f.mkdir();
			}

			f = new File("log/error");
			if (!f.isDirectory()) {
				f.mkdir();
			}

			f = new File("log/system");
			if (!f.isDirectory()) {
				f.mkdir();
			}

			f = new File("log/badplayer");
			if (!f.isDirectory()) {
				f.mkdir();
			}

			f = new File("log/enchant");
			if (!f.isDirectory()) {
				f.mkdir();
			}

			f = new File("log/inventory");
			if (!f.isDirectory()) {
				f.mkdir();
			}

			f = new File("log/system_time");
			if (!f.isDirectory()) {
				f.mkdir();
			}

			_chating = new FastTable<String>();
			_system = new FastTable<String>();
			_error = new FastTable<String>();
			_badplayer = new FastTable<String>();
			_enchant = new FastTable<String>();
			_inventory = new FastTable<String>();
			_time = new FastTable<String>();
			f = null;
		} catch (Exception e) {
			warn("server.log.Logger Logger()\r\n" + e, Config.LOG.system);
		}

	}

	private static Timer timer;

	private FastTable<String> _chating;

	private FastTable<String> _system;

	private FastTable<String> _error;

	private FastTable<String> _badplayer;

	private FastTable<String> _enchant;

	private FastTable<String> _inventory;

	private FastTable<String> _time;

	@Override
	public void run() {
		String ymd = Config.YearMonthDate2();

		// --------------------- chat
		write(_chating, "log/chat/" + ymd + ".log");

		// --------------------- error
		write(_error, "log/error/" + ymd + ".log");

		// --------------------- system
		write(_system, "log/system/" + ymd + ".log");

		// --------------------- badplayer
		write(_badplayer, "log/badplayer/" + ymd + ".log");

		// --------------------- enchant
		write(_enchant, "log/enchant/" + ymd + ".log");

		// --------------------- Inventory
		write(_inventory, "log/inventory/" + ymd + ".log");

		// --------------------- System Time
		write(_time, "log/system_time/" + ymd + ".log");

		ymd = null;
	}

	private void write(FastTable<String> list, String file) {
		try {
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			byte[] data = new byte[bis.available()];
			bis.read(data, 0, data.length);

			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bos.write(data);
			for (String s : list) {
				bos.write(s.getBytes());
			}
			bos.flush();
			bis.close();
			bos.close();
			list.clear();
			bis = null;
			bos = null;
			list = null;
		} catch (Exception e) {
			try {
				BufferedOutputStream bos2 = new BufferedOutputStream(
						new FileOutputStream(file));
				bos2.close();
				bos2 = null;
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	public void info(String text, Config.LOG log) {
		StringBuffer sb = new StringBuffer();
		sb.append("정보: ");
		sb.append(text);
		sb.append("\r\n");
		log(sb.toString(), log);
		sb = null;
	}

	public void error(String text, Config.LOG log) {
		StringBuffer sb = new StringBuffer();
		sb.append("심각: ");
		sb.append(text);
		sb.append("\r\n");
		log(sb.toString(), log);
		System.out.println(text);
		sb = null;
	}

	public void warn(String text, Config.LOG log) {
		StringBuffer sb = new StringBuffer();
		sb.append("경고: ");
		sb.append(text);
		sb.append("\r\n");
		log(sb.toString(), log);
	}

	public void badPalyer(String name, String text) {
		StringBuffer sb = new StringBuffer();
		sb.append("--> ");
		sb.append(name);
		sb.append("\r\n");
		sb.append(text);
		sb.append("\r\n");
		info(sb.toString(), Config.LOG.badplayer);
		sb = null;
	}

	public void en(String text) {
		StringBuffer sb = new StringBuffer();
		sb.append(text);
		sb.append("\r\n");
		log(sb.toString(), Config.LOG.enchant);
		sb = null;
	}

	public void addInventory(String text) {
		StringBuffer sb = new StringBuffer();
		sb.append(text);
		sb.append("\r\n");
		log(sb.toString(), Config.LOG.inventory);
		sb = null;
	}

	public void time(String text) {
		StringBuffer sb = new StringBuffer();
		sb.append(text);
		sb.append("\r\n");
		log(sb.toString(), Config.LOG.time);
		sb = null;
	}

	private void log(String text, Config.LOG log) {
		switch (log) {
		case system:
			_system.add(text);
			break;
		case chat:
			_chating.add(text);
			break;
		case error:
			_error.add(text);
			break;
		case badplayer:
			_badplayer.add(text);
			break;
		case enchant:
			_enchant.add(text);
			break;
		case inventory:
			_inventory.add(text);
			break;
		case time:
			_time.add(text);
			break;
		}
	}
}
