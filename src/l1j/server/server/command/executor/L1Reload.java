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

import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.GameSystem.CrockSystem;
import l1j.server.GameSystem.RobotThread;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.datatables.AccessoryEnchantList;
import l1j.server.server.datatables.ArmorEnchantList;
import l1j.server.server.datatables.AutoShopBuyTable;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.DropItemTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.EvaSystemTable;
import l1j.server.server.datatables.GetBackRestartTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MapFixKeyTable;
import l1j.server.server.datatables.MobSkillTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.NpcShopTable;
import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.ResolventTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.WeaponAddDamage;
import l1j.server.server.datatables.WeaponEnchantList;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1Reload implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1Reload.class.getName());

	private L1Reload() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Reload();
	}

	public void execute(L1PcInstance gm, String cmdName, String arg) {
		if (arg.equalsIgnoreCase("드랍")) {
			DropTable.reload();
			gm.sendPackets(new S_SystemMessage("DropTable Update Complete..."));
		} else if (arg.equalsIgnoreCase("드랍아이템")) {
			DropItemTable.reload();
			gm.sendPackets(new S_SystemMessage("DropItemTable Update Complete..."));
		} else if (arg.equalsIgnoreCase("변신")) {
			PolyTable.reload();
			gm.sendPackets(new S_SystemMessage("PolyTable Update Complete..."));
		} else if (arg.equalsIgnoreCase("용해제")) {
			ResolventTable.reload();
			gm.sendPackets(new S_SystemMessage("ResolventTable Update Complete..."));
		} else if (arg.equalsIgnoreCase("박스")) {
			L1TreasureBox.load();
			gm.sendPackets(new S_SystemMessage("TreasureBox Reload Complete..."));
		} else if (arg.equalsIgnoreCase("스킬")) {
			SkillsTable.reload();
			gm.sendPackets(new S_SystemMessage("Skills Reload Complete..."));
		} else if (arg.equalsIgnoreCase("몹스킬")) {
			MobSkillTable.reload();
			gm.sendPackets(new S_SystemMessage("MobSkills Reload Complete..."));
		} else if (arg.equalsIgnoreCase("맵")) {
			MapFixKeyTable.reload();
			gm.sendPackets(new S_SystemMessage("Map Reload Complete..."));	
		} else if (arg.equalsIgnoreCase("아이템")) {
			ItemTable.reload();
			gm.sendPackets(new S_SystemMessage("ItemTable Reload Complete..."));
		} else if (arg.equalsIgnoreCase("매입")) {
			AutoShopBuyTable.reload();
			gm.sendPackets(new S_SystemMessage("AutoShopBuyTable Update Complete..."));
		} else if (arg.equalsIgnoreCase("상점")) {
			ShopTable.reload();
			gm.sendPackets(new S_SystemMessage("ShopTable Reload Complete..."));
			
		} else if (arg.equalsIgnoreCase("무기데미지")) {
			WeaponAddDamage.reload();
			gm.sendPackets(new S_SystemMessage("WeaponAddDamege Update Complete..."));	
			
		} else if (arg.equalsIgnoreCase("영자상점")) {
			NpcShopTable.reloding();
			gm.sendPackets(new S_SystemMessage("NpcShopTable Reload Complete..."));

			
		} else if (arg.equalsIgnoreCase("엔피씨액션")) { 
			NPCTalkDataTable.reload();
			gm.sendPackets(new S_SystemMessage("NpcAction Reload Complete..."));
		} else if (arg.equalsIgnoreCase("스폰리스트")) {
			NpcSpawnTable.reload();
			gm.sendPackets(new S_SystemMessage("spawnlist Reload Complete..."));
		} else if (arg.equalsIgnoreCase("겟백리스")) {
			GetBackRestartTable.reload();
			gm.sendPackets(new S_SystemMessage("GetBackRestartTable Complete..."));
		} else if (arg.equalsIgnoreCase("공성시간")) {
			WarTimeController.getInstance().reload();
			gm.sendPackets(new S_SystemMessage("CastleTable Update Complete..."));
		} else if (arg.equalsIgnoreCase("엔피씨")) {
			NpcTable.reload();
			gm.sendPackets(new S_SystemMessage("NpcTable Update Complete..."));
		} else if (arg.equalsIgnoreCase("성정보")) {
			CastleTable.reload();
			gm.sendPackets(new S_SystemMessage("CastleTable Update Complete..."));
		} else if (arg.equalsIgnoreCase("서버설정")) {
			Config.load();
			gm.sendPackets(new S_SystemMessage("Config Update Complete..."));
		} else if (arg.equalsIgnoreCase("로봇")) {
			RobotThread.reload();
			gm.sendPackets(new S_SystemMessage("Robot Update Complete..."));
		} else if (arg.equalsIgnoreCase("균열")) {
			EvaSystemTable.reload();
			CrockSystem.reload();
			gm.sendPackets(new S_SystemMessage("TimeSystem Reload Complete..."));
		} else if (arg.equalsIgnoreCase("무기인챈")) {
			WeaponEnchantList.reload();
			gm.sendPackets(new S_SystemMessage("WeaponEnchantList Update Complete..."));
		} else if (arg.equalsIgnoreCase("아머인챈")) {
			ArmorEnchantList.reload();
			gm.sendPackets(new S_SystemMessage("ArmorEnchantList Update Complete..."));
		} else if (arg.equalsIgnoreCase("악세인챈")) {
			AccessoryEnchantList.reload();
			gm.sendPackets(new S_SystemMessage("AccessoryEnchantList Update Complete..."));
		} else {
			gm.sendPackets(new S_SystemMessage("\\fY--------------------------------------------------"));
			gm.sendPackets(new S_SystemMessage("   드랍, 드랍아이템, 변신, 용해제, 박스, 상점"));
			gm.sendPackets(new S_SystemMessage("   균열, 스킬, 맵, 아이템, 매입, 서버설정"));
			gm.sendPackets(new S_SystemMessage("   스폰리스트, 엔피씨액션, 엔피씨, 겟백리스"));
			gm.sendPackets(new S_SystemMessage("   몹스킬, 변신 , 공성시간, 매입, 로봇, 성정보"));
			gm.sendPackets(new S_SystemMessage("   로봇, 엔피씨액션, 스폰리스트, 겟백리스"));
			gm.sendPackets(new S_SystemMessage("   무기인챈, 아머인챈, 악세인챈"));
			gm.sendPackets(new S_SystemMessage("\\fY--------------------------------------------------"));
		}
	}
}
