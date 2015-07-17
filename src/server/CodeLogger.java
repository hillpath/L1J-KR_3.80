package server;

import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_Disconnect;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class CodeLogger { /////////////////매니져추가

	private static Logger _log_server = Logger.getLogger("code.server");
	private static Logger _log_Enchant = Logger.getLogger("code.Enchant");
	private static Logger _log_hack = Logger.getLogger("code.hack");
	private static Logger _log_boss = Logger.getLogger("code.boss");
	private static Logger _log_item = Logger.getLogger("code.item");
	private static Logger _log_chat = Logger.getLogger("code.chat");
	private static Logger _log_trade = Logger.getLogger("code.trade");
	private static Logger _log_login = Logger.getLogger("code.trade");
	private static Logger _log_smart = Logger.getLogger("code.trade");
	private static Logger _log_warehouse = Logger.getLogger("code.warehouse");
	private static Logger _log_tradev = Logger.getLogger("code.tradev");
	private static Logger _log_enchant = Logger.getLogger("code.enchant");
	private static Logger _log_observer = Logger.getLogger("code.observer");
	private static Logger _log_bug = Logger.getLogger("code.bug");
	private static Logger _log_gm = Logger.getLogger("code.gm");
	private static Logger _log_die = Logger.getLogger("code.die");

	private static final String LOG_PROP = "./config/log4j.properties";

	private static CodeLogger ins;

	public static CodeLogger getInstance() {
		if (ins == null) {
			ins = new CodeLogger();
		}
		return ins;
	}

	public CodeLogger() {
		PropertyConfigurator.configure(LOG_PROP);
	}

	public void alllog(String text) {
		StringBuffer temp = new StringBuffer();
		temp.append("	" + text);
		temp.append("\r\n");
		_log_server.info(temp.toString());
		_log_Enchant.info(temp.toString());
		_log_hack.info(temp.toString());
		_log_boss.info(temp.toString());
		_log_item.info(temp.toString());
		_log_trade.info(temp.toString());
		_log_chat.info(temp.toString());
		_log_warehouse.info(temp.toString());
		_log_tradev.info(temp.toString());
		_log_bug.info(temp.toString());
		_log_enchant.info(temp.toString());
		_log_observer.info(temp.toString());
		_log_gm.info(temp.toString());
		_log_die.info(temp.toString());
	}

	public void serverlog(String type, String text) {
		StringBuffer temp = new StringBuffer();
		temp.append("	" + type);
		temp.append("	" + text);
		temp.append("\r\n");
		_log_server.info(temp.toString());
	}

	public void Enchantlog(String type, String text) {
		StringBuffer temp = new StringBuffer();
		temp.append("	" + type);
		temp.append("	" + text);
		temp.append("\r\n");
		_log_Enchant.info(temp.toString());
	}

	public void hacklog(String type, String text) {
		StringBuffer temp = new StringBuffer();
		temp.append("	" + type);
		temp.append("	" + text);
		temp.append("\r\n");
		_log_hack.info(temp.toString());
	}
	
	public void loginlog(String type, String text){
		StringBuffer temp = new StringBuffer();		
		temp.append("	" + type);
		temp.append("	" + text);
		temp.append("\r\n");
		_log_hack.info(temp.toString());
	}
	
	public void smartlog(String type, String text){
		StringBuffer temp = new StringBuffer();		
		temp.append("	" + type);
		temp.append("	" + text);
		temp.append("\r\n");
		_log_hack.info(temp.toString());
	}
	
	public void bosslog(String type, String text){
		StringBuffer temp = new StringBuffer();		
		temp.append("	" + type);
		temp.append("	" + text);
		temp.append("\r\n");
		_log_boss.info(temp.toString());
	}
	
	public void itemlog(String type, String text){
		StringBuffer temp = new StringBuffer();		
		temp.append("	" + type);
		temp.append("	" + text);
		temp.append("\r\n");
		_log_item.info(temp.toString());
	}
	
	public void tradelog(String type, String text){
		StringBuffer temp = new StringBuffer();		
		temp.append("	" + type);
		temp.append("	" + text);
		temp.append("\r\n");
		_log_trade.info(temp.toString());
	}

	public void chatlog(String type, String text) {
		StringBuffer temp = new StringBuffer();
		temp.append("	" + type);
		temp.append("	" + text);
		temp.append("\r\n");
		_log_chat.info(temp.toString());
	}
	
	public void warehouselog(String type, String text, L1ItemInstance item, int count){
		StringBuffer temp = new StringBuffer();
		if ((item.getItemId() != L1ItemId.ADENA && item.getCount() >= 1000) 
				|| (item.getItem().getlogcheckitem() > 0 && item.getItem().getlogcheckitem() <= count)){
			temp.append("	##" + type);
		} else {
			temp.append("	" + type);
		}
		temp.append("	" + text);
		temp.append("	" + infoItem(item));
		temp.append("	수량=" + count);
		temp.append("\r\n");
		_log_warehouse.info(temp.toString());
	}

	public void tradevlog(String type, String text, L1ItemInstance item) {
		StringBuffer temp = new StringBuffer();
		if ((item.getItemId() != L1ItemId.ADENA && item.getCount() >= 1000) 
				|| (item.getItem().getlogcheckitem() > 0 && item.getItem().getlogcheckitem() <= item.getCount())){
			temp.append("	##" + type);
		} else {
			temp.append("	" + type);
		}
		temp.append("	" + text);
		temp.append("	" + infoItem(item));
		temp.append("	수량=" + item.getCount());
		temp.append("\r\n");
		_log_tradev.info(temp.toString());
	}
	public void privateshoplog(String type, String text, L1ItemInstance item, int count, int price){
		StringBuffer temp = new StringBuffer();
		if ((item.getItemId() != L1ItemId.ADENA && item.getCount() >= 1000)
				|| (item.getItem().getlogcheckitem() > 0 && item.getItem().getlogcheckitem() <= count)){
			temp.append("	##" + type);
		} else {
			temp.append("	" + type);
		}
		temp.append("	" + text);
		temp.append("	" + infoItem(item));
		temp.append("	수량=" + count);
		temp.append("	가격=" + price);
		temp.append("\r\n");
		_log_tradev.info(temp.toString());
	}
	public void buglog(String type, L1PcInstance pc, boolean disconnect){
		StringBuffer temp = new StringBuffer();
		temp.append("	"+type);
		temp.append("	"+pc.getName()+"["+pc.getClanname()+":"+pc.getAccountName()+"]");
		temp.append("	X="+pc.getX()+"	Y="+pc.getY()+"	MAP="+pc.getMapId()+"	HEAD="+pc.getMoveState().getHeading());
		temp.append("\r\n");
		_log_bug.info(temp.toString());
		if(disconnect){
			pc.sendPackets(new S_Disconnect());
		}
	}
	public void enchantlog(String type, String text, L1ItemInstance item){
		StringBuffer temp = new StringBuffer();
		if ((item.getItem().getType2() == 1 && item.getEnchantLevel() >= 9) ||				
				(item.getItem().getType2() == 2 && item.getEnchantLevel() >= 7) ||				
				item.getItem().get_safeenchant() == 0){
			temp.append(" " + type);
		} else {
			temp.append("	" + type);
		}
		temp.append("	" + text);
		temp.append("	" + infoItem(item));
		temp.append("\r\n");
		_log_enchant.info(temp.toString());
	}
	
	public void oblog(String type, String text, L1ItemInstance item, int count){
		StringBuffer temp = new StringBuffer();
		if ((item.getItemId() != L1ItemId.ADENA && item.getCount() >= 1000)
				|| (item.getItem().getlogcheckitem() > 0 && item.getItem().getlogcheckitem() <= count)
				|| item.getItem().getType2() == 1 || item.getItem().getType2() == 2){
			temp.append("	" + type);
			temp.append("	" + text);
			temp.append("	" + infoItem(item));
			temp.append("	수량=" + count);
			temp.append("\r\n");
			_log_observer.info(temp.toString());
		}
	}

	public void dielog(String type, String text) {
		StringBuffer temp = new StringBuffer();
		temp.append("	" + type);
		temp.append("	" + text);
		temp.append("\r\n");
		_log_die.info(temp.toString());
	}

	public void gmlog(String type, String text) {
		StringBuffer temp = new StringBuffer();
		temp.append("	" + type);
		temp.append("	" + text);
		temp.append("\r\n");
		_log_gm.info(temp.toString());
	}

	private String infoItem(L1ItemInstance item) {
		StringBuffer s = new StringBuffer();

		if (item.getEnchantLevel() > 0) {
			s.append("+" + item.getEnchantLevel() + " ");
		} else if (item.getEnchantLevel() == 0) {
			s.append("");
		} else if (item.getEnchantLevel() < 0) {
			s.append(String.valueOf(item.getEnchantLevel()) + " ");
		}
		// blessing item
		switch (item.getBless()) {
		case 0:s.append("Blessed ");break;
		case 1:s.append("");break;		
		case 2:s.append("Cursed ");break;
		default: break;
		}
		// enchanting item
		switch(item.getAttrEnchantLevel()){
		case 1: s.append("불의 "); break;
		case 2: s.append("폭발의 "); break;
		case 3: s.append("이그니스의 "); break;
		case 4: s.append("물의 "); break;
		case 5: s.append("해일의 "); break;
		case 6: s.append("운디네의 "); break;
		case 7: s.append("바람의 "); break;
		case 8: s.append("태풍의 "); break;
		case 9: s.append("실프의 "); break;
		case 10: s.append("대지의 "); break;
		case 11: s.append("파괴의 "); break;
		case 12: s.append("클레이의 "); break;
		default: s.append(""); break;
		}

		s.append(item.getName() + "[");
		s.append(item.getId() + "]");
		return s.toString();
	}
}
