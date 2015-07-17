package server;

import java.io.IOException;
import java.util.logging.Logger;

import com.lineage.Authentication;

import l1j.server.Config;
import l1j.server.server.Account;
import l1j.server.server.AccountAlreadyLoginException;
import l1j.server.server.GameServerFullException;
import l1j.server.server.clientpackets.C_AuthLogin;
import l1j.server.server.clientpackets.C_NoticeClick;
import l1j.server.server.serverpackets.S_LoginResult;
import l1j.server.server.serverpackets.S_Notice;

public class Authorization {
	private static Authorization uniqueInstance = null;

	private static Logger _log = Logger.getLogger(C_AuthLogin.class.getName());

	public static Authorization getInstance() {
		if (uniqueInstance == null) {
			synchronized (Authorization.class) {
				if (uniqueInstance == null)
					uniqueInstance = new Authorization();
			}
		}

		return uniqueInstance;
	}

	private Authorization() {
	}

	//public synchronized void auth(LineageClient client, String accountName,String password, String ip, String host) throws IOException {
		
	public void auth(LineageClient client, String accountName, String password, String ip, String host) throws IOException {

		if (checkDuplicatedIPConnection(ip)) {
			_log.info("Duplicate login from the same ip.  account=" + accountName+ " host=" + host);
			client.sendPacket(new S_Notice("He refused to sign a duplicate of the same IP."));
			disconnect(client);
			return;
		}
		
/*		// 위치 대강 봐서 맨 위이거나, 2번째 if 에 넣어주면 될듯..
		if(!Authentication.isAuth(ip)){	// If you're not authenticated by the authentication server.
			client.sendPacket(new S_Notice("Please certificates from.."));
			_log.info("<<린올>> 접속기 미사용 계정! account=" + accountName + " host=" + host);
			disconnect(client);
			return;
		}*/

		Account account = Account.load(accountName);
		if (account == null) {
			if (Config.AUTO_CREATE_ACCOUNTS) {
				if(Account.checkLoginIP(ip)) {
					client.sendPacket(new S_Notice("This account is created with the same IP 2"));
					try {
						Thread.sleep(1500);
						client.kick();
						client.close();
					} catch (Exception e1) {}
				} else {
					// ########## English alphanumeric and six-digit password and over
					try {
						int k = 0;
						for (int i=0;i<password.length();i++) {
							if (Character.isDigit(password.charAt(i))) {
								k++;
							}
						}
						if (k == password.length() || k == 0 || password.length() < 6) {
							client.sendPacket(new S_LoginResult(0x0a));
							client.setLoginFailedCount(client.getLoginFailedCount() + 1);
							if (client.getLoginFailedCount() > 2) disconnect(client);
							return;
						}
						
					} catch (Exception e) {
						_log.info("Check PW Num + Eng : "+ password); 
					}
					// ########## English alphanumeric and six-digit password and over
					if (!(isValidAccount(accountName)))	{
						client.sendPacket(new S_LoginResult(9));
						return;
					}
					account = Account.create(accountName, password, ip, host);
					account = Account.load(accountName);
				}
			} else {
				_log.warning("account missing for user " + accountName);
			}
		}

		if (account == null || !account.validatePassword(accountName, password)) {
			int lfc = client.getLoginFailedCount();
			client.setLoginFailedCount(lfc + 1);
			
			//if (lfc > 10000)//연동망
			if (lfc > 2)//원본
				disconnect(client);
			else
				client.sendPacket(new S_LoginResult(S_LoginResult.REASON_USER_OR_PASS_WRONG));
			return;
		}
		if (account.isBanned()) {
			_log.info("BAN account refused sign in. account=" + accountName + " host="+ host);
			client.sendPacket(new S_Notice("Your account has been banned.  Please send email to fuckyou@dickhead.com."));
			disconnect(client);
			account = null;
			return;
		}
		
		
		
		try {
			LoginController.getInstance().login(client, account);
			Account.updateLastActive(account); // 최종 로그인일을 갱신한다
			client.setAccount(account);
			sendNotice(client);
		} catch (GameServerFullException e) {
			client.sendPacket(new S_Notice("Server is full. \n \n Please try to connect at a later time."));
			disconnect(client);
			_log.info("Server full (" + client.getHostname()+ ")connection terminated.");
			return;
		} catch (AccountAlreadyLoginException e) {
			_log.info("Account already logged in.  account=" + accountName+ " host=" + host);
			client.sendPacket(new S_Notice("Oh shit, your account is already logged in! Connection Terminated!"));
			disconnect(client);
			return;
		} finally {
			account = null;
		}
	}
	
	
	private boolean isValidAccount(String accountName) {
		if (accountName.length() < 5) {
			return false;
		}
		char[] arrayOfChar = accountName.toCharArray();
		for (int i = 0; i < arrayOfChar.length; ++i)
			if (!(Character.isLetterOrDigit(arrayOfChar[i]))) {
				return false;
			}
		return true;
	}

	private void sendNotice(LineageClient client) {
		String accountName = client.getAccountName();
		/** Duplicate login bugs Fix * */
		if (LoginController.getInstance().getAccLoginSearch(client.getAccountName())) {
			return;
		}

		// Check whether the notice should read
		if (S_Notice.NoticeCount(accountName) > 0) {
			client.sendPacket(new S_Notice(accountName, client));
		} else {
			client.setloginStatus(1);
			new C_NoticeClick(client);
			//client.sendPacket(new S_Notice("Safeguard your lineage game account. Register (6-8 digit number) pin."));	

		}
		accountName = null;
	}

	private void disconnect(LineageClient client) throws IOException {
		client.kick();
		client.close();
	}

	@SuppressWarnings("unused")
	private Account loadAccountInfoFromDB(String accountName) {

		return Account.load(accountName);
	}

	private boolean checkDuplicatedIPConnection(String ip) {
		if (!Config.ALLOW_2PC) {
			return LoginController.getInstance().checkDuplicatedIP(ip);
		}
		return false;
	}

}
