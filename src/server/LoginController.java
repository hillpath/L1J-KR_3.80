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
package server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import server.message.ServerMessage;

import l1j.server.server.Account;
import l1j.server.server.AccountAlreadyLoginException;
import l1j.server.server.GameServerFullException;
import l1j.server.server.serverpackets.S_ServerMessage;

public class LoginController {
	private static LoginController uniqueInstance;
	private ConcurrentHashMap<String, LineageClient> accountsMap = new ConcurrentHashMap<String, LineageClient>();
	private int maxAllowedOnlinePlayers;
	private Map<String, Integer> _countCClass = new ConcurrentHashMap<String, Integer>();

	private LoginController() {}

	public static LoginController getInstance() {
		if(uniqueInstance == null) {
			synchronized (LoginController.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new LoginController();
				}
			}
		}
		return uniqueInstance;
	}

	public LineageClient[] getAllAccounts() {
		return accountsMap.values().toArray(new LineageClient[accountsMap.size()]);
	}

	public boolean getAccLoginSearch(String account){
		int i = 0;
		boolean v = false;
		LineageClient[] list = null;
		list = getAllAccounts();
		for(LineageClient MapCl : list){
			if(MapCl == null){
				continue;
			}
			if(MapCl.getAccountName().equals(account)){
				i += 1;
				if(i > 1){
					kickClient(accountsMap.remove(MapCl));
					v = true;
					if(i > 2){
						System.out.println("[LoginController] 동일 계정 "+i+"인 로그인. 계정: "+account);
					}
				}
			}
		}
		return v;
	}

	public int getOnlinePlayerCount() {
		return accountsMap.size();
	}

	public int getMaxAllowedOnlinePlayers() {
		return maxAllowedOnlinePlayers;
	}

	public void setMaxAllowedOnlinePlayers(final int maxAllowedOnlinePlayers) {
		this.maxAllowedOnlinePlayers = maxAllowedOnlinePlayers;
	}

	private void kickClient(final LineageClient client) {
		if (client == null) {
			return;
		}

		if (client.getActiveChar() != null) {
			client.getActiveChar().sendPackets(new S_ServerMessage(ServerMessage.DUPLICATED_IP_CONNECTION));
		}

		try {
			Thread.sleep(1000);
			client.kick();
			client.close();
		} catch (Exception e) {}
	}
	
	public int getCClassCount(String paramString)  {
		if (_countCClass.containsKey(getCClass(paramString)))
		      return ((Integer)_countCClass.get(getCClass(paramString))).intValue();
		    return 0;
		  }
	
	private String getCClass(String paramString) {
	    return paramString.substring(0, paramString.lastIndexOf(46));
	  }

	/** 3.80c 옵코드 적용 물개 **/
	// 3.80c opcode apply seals
	public void login(LineageClient client, Account account)
			throws GameServerFullException, AccountAlreadyLoginException {
		//if (!account.isValid()) {
		//	throw new IllegalArgumentException("인증되지 않은 계정입니다");
		//}
		if ((getMaxAllowedOnlinePlayers() <= getOnlinePlayerCount()) && !account.isGameMaster()) {
			throw new GameServerFullException();
		}
		synchronized (accountsMap) {
		}
		}
		//if (accountsMap.containsKey(account.getName())) {
		//	kickClient(accountsMap.remove(account.getName()));
		//	throw new AccountAlreadyLoginException();
		//}		
	//	accountsMap.put(account.getName(), client);		
		//}
	//}

	public boolean logout(LineageClient client) {
		if (client.getAccountName() == null) {
			return false;
		}
		return accountsMap.remove(client.getAccountName()) != null;
	}

	public boolean checkDuplicatedIP(String ip) {
		LineageClient[] list = null;
		list = getAllAccounts();
		for (LineageClient tempClient : list) {
			if (ip.equalsIgnoreCase(tempClient.getIp())) {
				return true;
			}
		}
		return false;
	}
}
