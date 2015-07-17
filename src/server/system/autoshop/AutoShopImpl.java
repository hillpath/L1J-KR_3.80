package server.system.autoshop;

import l1j.server.server.model.Instance.L1PcInstance;

public class AutoShopImpl implements AutoShop {
	private L1PcInstance shopCharacter;

	public AutoShopImpl(L1PcInstance pc) {
		shopCharacter = pc;
	}

	@Override
	public String getName() {
		return shopCharacter.getName();
	}
	
	@Override
	public String getAccount(){
		return shopCharacter.getAccountName();
	}
	
	@Override
	public int getId(){
		return shopCharacter.getId();
	}
	@Override
	public String getIp(){
		return shopCharacter.getNetConnection().getHostname();
	}
	@Override

	public void logout() {
		shopCharacter.logout();
		shopCharacter = null;

	}

}
