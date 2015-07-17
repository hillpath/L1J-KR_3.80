package server;

import java.util.StringTokenizer;

import javolution.util.FastMap;
import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.serverpackets.KeyPacket;
import l1j.server.server.serverpackets.ServerBasePacket;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class LineageProtocolHandler extends IoHandlerAdapter {
	private FastMap<String, ip> _list = new FastMap<String, ip>();

	// 클라이언트가 서버에 접속하여 통신을 시작하기 직전임
	// The client contacts the server being about to start communication
	@Override
	public void sessionCreated(IoSession session) {
		try {
			StringTokenizer st = new StringTokenizer(session.getRemoteAddress().toString().substring(1), ":");
			String ip = st.nextToken();
			
			if(IpTable.getInstance().isBannedIp(ip)){
                System.out.printf("BAN IP attempts to connect:", ip);
				session.close(true);
			}
			
			
			
			System.out.println("******  IP:("+ip+") has access to the server ******");
			// 0포트 필터링 (ddos공격)
			// 0 port filtering (ddos attack)
			if(st.nextToken().startsWith("0")){
				session.close(true);
			}
			// 무한접속형 섭폭 필터링 (dos공격)
			// (dos attack)
			ip IP = _list.get(ip);
			if (IP == null) {
				IP = new ip();
				IP.ip = ip;
				IP.time = System.currentTimeMillis();
				_list.put(IP.ip, IP);
			} else {
				if (IP.block) {
					session.close(true);
				} else {
						if(System.currentTimeMillis()<IP.time+3000){
						    if(IP.count>3){
							IP.block = true;
							session.close(IP.block);
						} else {
							IP.count++;
						}
					} else {
						IP.count = 0;
					}
					IP.time = System.currentTimeMillis();
				}
			}
		} catch (Exception e) {
			// Logger.getInstance().error(getClass().toString()+"
			// sessionCreated(IoSession session)\r\n"+e.toString(),
			// Config.LOG.error);
		}

	}

	/***************************************************************************
	 * 
	 */
	@Override
	public void sessionOpened(IoSession session) {
		try {
			if (!Config.shutdown && !session.isClosing()) {
				GeneralThreadPool.getInstance().schedule(new sessionOpen(session), 1);

			} else {
				session.close();
			}
		} catch (Exception e) {
			// Logger.getInstance().error(getClass().toString()+"
			// sessionOpened(IoSession session)\r\n"+e.toString(),
			// Config.LOG.error);
		}
	}

	private static final long seed = 0x00000000L;
	class sessionOpen implements Runnable{
		private IoSession session;
		public sessionOpen(IoSession s){
			session = s;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub

			KeyPacket key = new KeyPacket();

			LineageClient lc = new LineageClient(session, seed);
			int rowIndex = DecoderManager.getInstance().getRowIndex();
			lc.setthreadIndex(rowIndex);

			DecoderManager.getInstance().putClient(lc, lc.getthreadIndex());

			session.write(key);
			session.setAttribute(LineageClient.CLIENT_KEY, lc);
			GeneralThreadPool.getInstance().schedule(new ck(lc), 1500);
			GeneralThreadPool.getInstance().schedule(new loginck(lc), 50000);
			CheckGamePort(session);
			
		}
		
	}
	
	class ck implements Runnable{
		private LineageClient client;
		public ck(LineageClient c){
			client = c;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(client != null && !client.clientVCheck){
				try{
					System.out.println("Connect CV Miss Check > "+client.getIp());
					client.close();
				}catch(Exception e){}
			}
		}
	}
	
	class loginck implements Runnable{
		private LineageClient client;
		public loginck(LineageClient c){
			client = c;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(client != null && !client.clientLoginCheck){
				try{
					System.out.println("No login attempts in 1 minute > "+client.getIp());
					client.close();
				}catch(Exception e){}
			}
		}
	}

	// 전송 요청한 메시지 개체가 바이트 버퍼로 변환되어 클라이언트에 전송되었음
	// The requested message transfer object is converted to a byte buffer was sent to the client
	@Override
	public void messageSent(IoSession session, Object message) {
		if (Config.PACKET) {
			ServerBasePacket bp = (ServerBasePacket) message;
			byte[] data = bp.getBytes();
			System.out.println("[server]\r\n" + printData(data, data.length));
		}
		((ServerBasePacket) message).close();
	}

	// 클라이언트가 데이터를 전송하여 그 내용이 메시지 개체로 변환 되었음
	// The contents of which was converted to the client object to send the message data
	@Override
	public void messageReceived(IoSession session, Object message) {
		// 디코더 처리후 바로 패킷 처리로 가기 때문에 여긴 필요 없음.
	}

	// 클라이언트의 서버 사이의 접속이 해제되었음
	// Under the connection between the client the server release
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		LineageClient lc = (LineageClient) session
				.getAttribute(LineageClient.CLIENT_KEY);
		if (lc != null) {
			lc.close();
			lc.movePacket.requestWork();
			lc.hcPacket.requestWork();
			lc = null;
		}
	}

	// 클라이언트와 접속이 된 상태이나 I/O가 없는 유휴 상태임
	// The client and the connection state or the I / O is not idle, being
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		// sessionClosed(session);
		// Logger.getInstance().error(getClass().toString()+" sessionIdle(
		// IoSession session, IdleStatus status )\r\n"+status.toString(),
		// Config.LOG.system);
	}

	// 통신 도중 또는 이벤트 처리 도중 예외가 발생했음
	// The event has occurred during treatment or during communication exceptions
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		try {
			sessionClosed(session);
			// Logger.getInstance().error(getClass().toString()+"
			// exceptionCaught(IoSession session, Throwable
			// cause)\r\n"+cause.toString(), Config.LOG.system);
		} catch (Exception e) {
		}
	}
	
	private void CheckGamePort(IoSession session) {
		 try {
			StringTokenizer st1 = new StringTokenizer(session.getRemoteAddress()
					.toString().substring(1), ":");
			String ip1 = st1.nextToken();
			IpTable iptable1 = IpTable.getInstance();
			
			StringTokenizer st2 = new StringTokenizer(session.getRemoteAddress()
					.toString().substring(1), ":");
			String ip2 = st2.nextToken();
			IpTable iptable2 = IpTable.getInstance();
			
			StringTokenizer st3 = new StringTokenizer(session.getRemoteAddress()
					.toString().substring(1), ":");
			String ip3 = st3.nextToken();
			IpTable iptable3 = IpTable.getInstance();
			
			StringTokenizer st4 = new StringTokenizer(session.getRemoteAddress()
					.toString().substring(1), ":");
			String ip4 = st4.nextToken();
			
			StringTokenizer st5 = new StringTokenizer(session.getRemoteAddress()
					.toString().substring(1), ":");
			String ip5 = st5.nextToken();
			IpTable iptable5 = IpTable.getInstance();
			
			// 0포트 필터링 (ddos공격)
			// 0 port filtering (ddos attack)
			if (st1.nextToken().startsWith("0")) {
				iptable1.banIp(ip1);
				System.out.println("O 포트 공격 자동 차단: "+ip1);
				session.close(true);
			}
			
			// null 공격
			// null attack
			if (st2.nextToken().startsWith("null")) {
				iptable2.banIp(ip2);
				System.out.println("NULL attack, automatic shut-off: "+ip2);
				session.close(true);
			}
			
			//비었을경우
			// empty case
			if(st3.nextToken().isEmpty()){
				iptable3.banIp(ip3);
				System.out.println("Empty attac, automatic shut-off: "+ip3);
				session.close(true);
			}
			
			if(st5.nextToken().length() <= 0){
				iptable5.banIp(ip5);
				System.out.println("length attack, automatic shut-off: "+ip5);
				session.close(true);
			}
			
			//System.out.println("[접속 시도] 아이피 : "+ip4+ " / 포트 : "+st4.nextToken());
		 } catch (Exception e) {
			 
			}
	 }
	

	public String printData(byte[] data, int len) {
		StringBuffer result = new StringBuffer();
		int counter = 0;
		for (int i = 0; i < len; i++) {
			if (counter % 16 == 0) {
				result.append(fillHex(i, 4) + ": ");
			}
			result.append(fillHex(data[i] & 0xff, 2) + " ");
			counter++;
			if (counter == 16) {
				result.append("   ");
				int charpoint = i - 15;
				for (int a = 0; a < 16; a++) {
					int t1 = data[charpoint++];
					if (t1 > 0x1f && t1 < 0x80) {
						result.append((char) t1);
					} else {
						result.append('.');
					}
				}
				result.append("\n");
				counter = 0;
			}
		}

		int rest = data.length % 16;
		if (rest > 0) {
			for (int i = 0; i < 17 - rest; i++) {
				result.append("   ");
			}

			int charpoint = data.length - rest;
			for (int a = 0; a < rest; a++) {
				int t1 = data[charpoint++];
				if (t1 > 0x1f && t1 < 0x80) {
					result.append((char) t1);
				} else {
					result.append('.');
				}
			}

			result.append("\n");
		}
		return result.toString();
	}

	private String fillHex(int data, int digits) {
		String number = Integer.toHexString(data);

		for (int i = number.length(); i < digits; i++) {
			number = "0" + number;
		}
		return number;
	}

	class ip {
		public String ip;

		public int count;

		public long time;

		public boolean block;
	}
}
