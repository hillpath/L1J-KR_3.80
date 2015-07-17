package server.system.autoshop;

import java.util.Vector;

import l1j.server.GameSystem.MiniGame.DeathMatch;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.datatables.CharBuffTable;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class AutoShopManager {
	private static AutoShopManager uniqueInstance;
	private Vector<AutoShop> autoshopList;
	

	
	private AutoShopManager() {
		autoshopList = new Vector<AutoShop>();
	}
	public static AutoShopManager getInstance() {
		if(uniqueInstance == null) {
			synchronized(AutoShopManager.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new AutoShopManager(); 
				}
			}
		}
		
		return uniqueInstance;
	}
	
	public void register(AutoShop shop) {
		if(autoshopList.contains(shop)) return;
		autoshopList.add(shop);
	}
	
	public void remove(AutoShop shop) {
		autoshopList.remove(shop);
	}
	
	public AutoShop getShopPlayer(String charName) {
		AutoShop autoShop = null;
		for(int i = 0 ; i < autoshopList.size() ; i++) {
			autoShop = autoshopList.get(i);			
			if(autoShop.getName().equalsIgnoreCase(charName))	break;
			else autoShop = null;
		}
		return autoShop;
	}

	public boolean isAutoShop(int objid){
		AutoShop autoShop = null;
		for(int i=0; i < autoshopList.size(); i++){
			autoShop = autoshopList.get(i);
			if(autoShop.getId() == objid) {
				return true;
			}
		}
		return false;
	}

	public int getShopPlayerCount()	{
		return this.autoshopList.size();
	}

	public AutoShop makeAutoShop(L1PcInstance pc) throws Exception {
		for (L1ItemInstance item : pc.getInventory().getItems()) {
			if(item._pc != null || item.EquipPc != null || item.getItemOwner() != null){
				item._pc = null;
				item.EquipPc = null;
				item.setItemOwner(null);
			}
			if (item.getCount() <= 0) {
				pc.getInventory().deleteItem(item);
			}
		}
		for (L1DollInstance doll : pc.getDollList().values()) {
				doll.deleteDoll();
		}
		
		pc.setDeathMatch(false);
		pc.setHaunted(false);
		pc.setPetRacing(false);
		
		if (DeathMatch.getInstance().isEnterMember(pc)) {
			DeathMatch.getInstance().removeEnterMember(pc);
		}

		
		CharBuffTable.DeleteBuff(pc);
		CharBuffTable.SaveBuff(pc);
		pc.getSkillEffectTimerSet().clearSkillEffectTimer();
		pc.stopHpRegenerationByDoll();
		pc.stopMpRegenerationByDoll();
		//pc.stopSHRegeneration();
		pc.stopMpDecreaseByScales();
		pc.stopEquipmentTimer();
		
		WarehouseManager w = WarehouseManager.getInstance(); 
		w.delPrivateWarehouse(pc.getAccountName());
		w.delElfWarehouse(pc.getAccountName());
		w.delPackageWarehouse(pc.getAccountName());
		
		pc.saveInventory();
		pc.save();
		pc.setNetConnection(null);
		
		
		return new AutoShopImpl(pc);
	}
}
