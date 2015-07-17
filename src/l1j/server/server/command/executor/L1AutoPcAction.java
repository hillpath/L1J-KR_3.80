/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import server.system.autoshop.AutoShopManager;

public class L1AutoPcAction implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1AutoPcAction.class.getName());
	public static final int[] MALE_LIST = new int[] { 0, 61, 138, 734, 2786, 6658, 6671 };

	private L1AutoPcAction() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AutoPcAction();
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String type = st.nextToken();
			
			if(type.equalsIgnoreCase("1")){
				toAppendShop(pc, st);
				return;
			}
			if(type.equalsIgnoreCase("2")){
				toAppendShop1(pc, st);
				return;
			}
			if(type.equalsIgnoreCase("3")){
				toAppendKing(pc, st);
				return;
			}
			if(type.equalsIgnoreCase("4")){
				toEnd(pc, st);
				return;
			}
			
		} catch (Exception e) { }
		pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
		pc.sendPackets(new S_SystemMessage( ".오토 [옵션]" ));
		pc.sendPackets(new S_SystemMessage( " [1:여군주  2.남군주 3.가입군주 4.종료]"));		
		pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));		
	}
	
	private void toAppendShop(L1PcInstance pc, StringTokenizer st) {
		try {
			String charName = st.nextToken();			
			doesCharNameExist(pc, charName);
			
			AutoShopManager shopManager = AutoShopManager.getInstance(); 
			L1PcInstance auto = new L1PcInstance();
			long curtime = System.currentTimeMillis() / 1000;
			int klass = 0;
			
			auto.setId(ObjectIdFactory.getInstance().nextId());
			auto.setName(charName);						
			auto.set_sex(0);	
			auto.setType(0);
			auto.setClassId(MALE_LIST[auto.getType()]);
			auto.getGfxId().setTempCharGfx(6080);
			auto.getGfxId().setGfxId(auto.getClassId());			
			auto.getMoveState().setHeading(pc.getMoveState().getHeading());
			auto.setLawful(32767);
			auto.setTitle("\\f>  매 입 칼 질");
			auto.setX(pc.getX());
			auto.setY(pc.getY());		
			auto.setMap(pc.getMapId());	
			auto.getMoveState().setHeading(pc.getMoveState().getHeading());
			auto.setHighLevel(1);
			auto.getAbility().setBaseStr(13);
			auto.getAbility().setBaseDex(10);
			auto.getAbility().setBaseCon(18);
			auto.getAbility().setBaseWis(11);
			auto.getAbility().setBaseCha(13);
			auto.getAbility().setBaseInt(10);
			auto.addBaseMaxHp((short)200);
			auto.setCurrentHp(auto.getMaxHp());
			auto.addBaseMaxMp((short)20);
			auto.setCurrentMp(auto.getMaxMp());
			auto.resetBaseAc();
			auto.setClanRank(0);
			auto.setClanid(0);
			auto.setClanname("");
			auto.set_food(39); // 17%
			auto.setGm(false);
			auto.setMonitor(false);
			auto.setGmInvis(false);
			auto.setExp(0);
			auto.setActionStatus(0);
			auto.setAccessLevel((short) 0);
			auto.getAbility().setBonusAbility(0);
			auto.resetBaseMr();
			auto.setElfAttr(0);
			auto.set_PKcount(0);
			auto.setExpRes(0);
			auto.setPartnerId(0);
			auto.setOnlineStatus(0);
			auto.setHomeTownId(0);
			auto.setContribution(0);
			auto.setBanned(false);
			auto.setKarma(0);
			auto.setReturnStat(0);
			auto.setGdungeonTime(0);
			auto.calAinHasad(0);
			auto.setAccountName("");
			auto.noPlayerCK = true;
			auto.setAutoshop(true);
			
			CharacterTable.getInstance().restoreInventory(auto);
			L1World.getInstance().storeObject(auto);
			L1World.getInstance().addVisibleObject(auto);
			
			shopManager.makeAutoShop(auto);
			pc.sendPackets(new S_SystemMessage(auto.getName() + " 매입상점이 시작되었습니다."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage( ".오토 상점 [캐릭명]" ));
		}
	}
	
	private void toAppendShop1(L1PcInstance pc, StringTokenizer st) {
		try {
			String charName = st.nextToken();			
			doesCharNameExist(pc, charName);
			
			AutoShopManager shopManager = AutoShopManager.getInstance(); 
			L1PcInstance auto = new L1PcInstance();
			long curtime = System.currentTimeMillis() / 1000;
			int klass = 0;
			
			auto.setId(ObjectIdFactory.getInstance().nextId());
			auto.setName(charName);						
			auto.set_sex(0);	
			auto.setType(0);
			auto.setClassId(MALE_LIST[auto.getType()]);
			auto.getGfxId().setTempCharGfx(6094);
			auto.getGfxId().setGfxId(auto.getClassId());			
			auto.getMoveState().setHeading(pc.getMoveState().getHeading());
			auto.setLawful(32767);
			auto.setTitle("\\f>  매 입 칼 질");
			auto.setX(pc.getX());
			auto.setY(pc.getY());		
			auto.setMap(pc.getMapId());	
			auto.getMoveState().setHeading(pc.getMoveState().getHeading());
			auto.setHighLevel(1);
			auto.getAbility().setBaseStr(13);
			auto.getAbility().setBaseDex(10);
			auto.getAbility().setBaseCon(18);
			auto.getAbility().setBaseWis(11);
			auto.getAbility().setBaseCha(13);
			auto.getAbility().setBaseInt(10);
			auto.addBaseMaxHp((short)200);
			auto.setCurrentHp(auto.getMaxHp());
			auto.addBaseMaxMp((short)20);
			auto.setCurrentMp(auto.getMaxMp());
			auto.resetBaseAc();
			auto.setClanRank(0);
			auto.setClanid(0);
			auto.setClanname("");
			auto.set_food(39); // 17%
			auto.setGm(false);
			auto.setMonitor(false);
			auto.setGmInvis(false);
			auto.setExp(0);
			auto.setActionStatus(0);
			auto.setAccessLevel((short) 0);
			auto.getAbility().setBonusAbility(0);
			auto.resetBaseMr();
			auto.setElfAttr(0);
			auto.set_PKcount(0);
			auto.setExpRes(0);
			auto.setPartnerId(0);
			auto.setOnlineStatus(0);
			auto.setHomeTownId(0);
			auto.setContribution(0);
			auto.setBanned(false);
			auto.setKarma(0);
			auto.setReturnStat(0);
			auto.setGdungeonTime(0);
			auto.calAinHasad(0);
			auto.setAccountName("");
			auto.noPlayerCK = true;
			auto.setAutoshop(true);
			
			CharacterTable.getInstance().restoreInventory(auto);
			L1World.getInstance().storeObject(auto);
			L1World.getInstance().addVisibleObject(auto);
			
			shopManager.makeAutoShop(auto);
			pc.sendPackets(new S_SystemMessage(auto.getName() + " 매입상점이 시작되었습니다."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage( ".오토 상점 [캐릭명]" ));
		}
	}
	private void toAppendKing(L1PcInstance pc, StringTokenizer st) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1PcInstance king = null;
		
		try {
			String accountName = st.nextToken();			
			String charName = st.nextToken();			
			int level = Integer.parseInt(st.nextToken());
			String clanName = st.nextToken();
			String title = st.nextToken();
			String userTitle = st.nextToken();
			
			String temp;
			
			temp = title.substring(0, 3);			
			for (int i = 3; i < title.length(); i++) {	
				temp += title.substring(i, i + 1);
				if (i != title.length() - 1) {
					temp += " ";
				}
			}
			
			title = temp;
				
			temp = userTitle.substring(0, 3);
			for (int i = 3; i < userTitle.length(); i++) {				
				temp += userTitle.substring(i, i + 1);
				if (i != userTitle.length() - 1) {
					temp += " ";
				}
			}
			
			userTitle = temp;
			
			AutoShopManager shopManager = AutoShopManager.getInstance(); 			
			int klass = 0;
			
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM characters WHERE char_name=?");
			pstm.setString(1, charName);
			rs = pstm.executeQuery();
			
			if (rs.next()) {
				king = L1PcInstance.load(charName);
			}
			
			if (king == null) {
				king = new L1PcInstance();			
			
				king.setId(ObjectIdFactory.getInstance().nextId());
				king.setName(charName);						
				king.set_sex(0);	
				king.setType(0);
				king.setClassId(MALE_LIST[king.getType()]);
				king.getGfxId().setTempCharGfx(klass);
				king.getGfxId().setGfxId(king.getClassId());			
				king.setLevel(level);
				king.setHighLevel(level);
				king.getAbility().setBaseStr(13);
				king.getAbility().setBaseDex(10);
				king.getAbility().setBaseCon(18);
				king.getAbility().setBaseWis(11);
				king.getAbility().setBaseCha(13);
				king.getAbility().setBaseInt(10);
				king.getMoveState().setHeading(pc.getMoveState().getHeading());
				king.setLawful(32767);
				king.addBaseMaxHp((short) 200);
				king.setCurrentHp(pc.getMaxHp());
				king.addBaseMaxMp((short) 20);
				king.setCurrentMp(pc.getMaxMp());
				king.resetBaseAc();
				king.setTitle(title);
				king.setClanRank(0);
				king.setClanid(0);
				king.setClanname("");
				king.set_food(39); // 17%
				king.setGm(false);
				king.setMonitor(false);
				king.setGmInvis(false);
				king.setExp(0);
				king.setActionStatus(0);
				king.setAccessLevel((short) 0);
				king.getAbility().setBonusAbility(0);
				king.resetBaseMr();
				king.setElfAttr(0);
				king.set_PKcount(0);
				king.setExpRes(0);
				king.setPartnerId(0);
				king.setOnlineStatus(0);
				king.setHomeTownId(0);
				king.setContribution(0);
				king.setBanned(false);
				king.setKarma(0);
				king.setReturnStat(0);
				king.setGdungeonTime(0);
				king.calAinHasad(0);
				king.setAccountName(accountName);	
				
				CharacterTable.getInstance().storeNewCharacter(king);
				king.setExp(ExpTable.getExpByLevel(level));
				king.save();
				king.refresh();
			}
			
			king.setX(pc.getX());
			king.setY(pc.getY());		
			king.setMap(pc.getMapId());	
			king.noPlayerCK = true;
			king.setAutoKing(true);
			king.setTitle(title);
			king.setAutoKingTitle(userTitle);
			
			L1Clan clan = ClanTable.getInstance().find(clanName); 
			
			if (clan == null) {
				ClanTable.getInstance().createClan(king, clanName);											
				king.save(); // DB에 캐릭터 정보를 기입한다
			} else {	
				if (king.getClanid() == 0) {
					king.setClanid(clan.getClanId());
					king.setClanname(clan.getClanName());
					king.setClanRank(2);
					clan.addClanMember(king.getName(), king.getClanRank());		
				}
			}
			
			CharacterTable.getInstance().restoreInventory(king);
			L1World.getInstance().storeObject(king);
			L1World.getInstance().addVisibleObject(king);
			
			shopManager.makeAutoShop(king);
			pc.sendPackets(new S_SystemMessage(king.getName() + " 군주오토가 시작되었습니다."));		
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage( ".오토 군주 [계정명] [캐릭명] [레벨] [혈맹명] [군주호칭] [유저호칭]" ));
		}
	}
	private void toEnd(L1PcInstance pc, StringTokenizer st) {
		try {
			String charName = st.nextToken();		
			
			for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
				if (player.getName().equalsIgnoreCase(charName)) {						
					player.logout();
					pc.sendPackets(new S_SystemMessage(player.getName() + " 오토가 종료되었습니다."));
					break;
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage( ".오토 종료 [캐릭명]" ));
		}
	}
	
	private void doesCharNameExist(L1PcInstance pc, String charName) {
		L1PcInstance player = L1World.getInstance().getPlayer(charName);
		if (player != null) {			
			return;
		}
	}
	
	
	
}
