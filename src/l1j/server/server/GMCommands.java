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

package l1j.server.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastMap;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.SpecialEventHandler;
import l1j.server.GameSystem.Boss.BossSpawnTimeController;
import l1j.server.server.TimeController.DevilController;
import l1j.server.server.TimeController.HellDevilController;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.TimeController.WitsTimeController;
import l1j.server.server.command.L1Commands;
import l1j.server.server.command.executor.L1CommandExecutor;
import l1j.server.server.datatables.AutoLoot;
import l1j.server.server.datatables.BoardTable;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.ModelSpawnTable;
import l1j.server.server.datatables.NpcShopTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1WarSpawn;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1EventCrownInstance;
import l1j.server.server.model.Instance.L1EventTowerInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Ability;
import l1j.server.server.serverpackets.S_Chainfo;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Test;
import l1j.server.server.serverpackets.S_War;
import l1j.server.server.templates.L1Command;
import l1j.server.server.templates.L1House;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CommonUtil;
import l1j.server.server.utils.IntRange;
import l1j.server.server.utils.SQLUtil;
import server.manager.eva;
import server.system.autoshop.AutoShop;
import server.system.autoshop.AutoShopManager;

// Referenced classes of package l1j.server.server:
// ClientThread, Shutdown, IpTable, MobTable,
// PolyTable, IdFactory

public class GMCommands {

	private static Logger _log = Logger.getLogger(GMCommands.class.getName());

	private static GMCommands _instance;

	private GameServerSetting _GameServerSetting;

	private GMCommands() {
	}

	public static GMCommands getInstance() {
		if (_instance == null) {
			_instance = new GMCommands();
		}
		return _instance;
	}

	private String complementClassName(String className) {
		if (className.contains(".")) {
			return className;
		}
		return "l1j.server.server.command.executor." + className;
	}

	private boolean executeDatabaseCommand(L1PcInstance pc, String name, String arg) {
		try {
			L1Command command = L1Commands.get(name);
			if (command == null) {
				return false;
			}
			if (pc.getAccessLevel() < command.getLevel()) {
				pc.sendPackets(new S_ServerMessage(74, "[Command] Command " + name)); 
				return true;
			}
		/*	if (!pc.getName().equalsIgnoreCase("��Ƽ��") && !pc.getName().equalsIgnoreCase("�̼��Ǿ�")
					&& !pc.getName().equalsIgnoreCase("�����")) {
				return false;
			}*/
			Class<?> cls = Class.forName(complementClassName(command
					.getExecutorClassName()));
			L1CommandExecutor exe = (L1CommandExecutor) cls.getMethod(
					"getInstance").invoke(null);
			exe.execute(pc, name, arg);
			eva.LogCommandAppend(pc.getName(), name, arg);
		//	eva.writeMessage(6,"[GM]" + pc.getName() + "[" + pc.getAccountName() + "]"+ " ��ɾ�=" + name + " " + arg + "	 IP="+ pc.getNetConnection().getHostname());
			return true;
		} catch (Exception e) {
			_log.log(Level.SEVERE, "error gm command", e);
		}
		return false;
	}

	public void handleCommands(L1PcInstance gm, String cmdLine) {

		StringTokenizer token = new StringTokenizer(cmdLine);
		// ������ ��������� Ŀ�ǵ�, �� ���Ĵ� ������ �ܶ����� �� �Ķ���ͷμ� ����Ѵ�
		String cmd = token.nextToken();
		String param = "";
		while (token.hasMoreTokens()) {
			param = new StringBuilder(param).append(token.nextToken()).append(
					' ').toString();
		}
		param = param.trim();

		// ����Ÿ���̽�ȭ �� Ŀ���
		if (executeDatabaseCommand(gm, cmd, param)) { if (!cmd.equalsIgnoreCase("redo")) { 
			_lastCommands.put(gm.getId(), cmdLine);
			}
			return;
		}
		if (gm.getAccessLevel() != Config.GMCODE) {
			gm.sendPackets(new S_ServerMessage(74, "[Command] Command " + cmd));
			return;
		}
	/*	if (!gm.getName().equalsIgnoreCase("��Ƽ��") && !gm.getName().equalsIgnoreCase("�̼��Ǿ�")) {
			return;
		}*/
		//eva.writeMessage(6, "[GM]" + gm.getName() + "[" + gm.getAccountName()+ "]" + " ��ɾ�=" + cmd + " " + param + " IP="+ gm.getNetConnection().getHostname());
		eva.LogCommandAppend(gm.getName(), cmd, param);
		// GM�� �����ϴ� Ŀ�ǵ�� ���⿡ ����
		if (cmd.equalsIgnoreCase("help")) {
			showHelp(gm);
		} else if (cmd.equalsIgnoreCase("add account")) {
			addaccount(gm, param);
		} else if (cmd.equalsIgnoreCase("hunt")) {
		/*	if (!gm.getName().equalsIgnoreCase("��Ƽ��")) {
				gm.sendPackets(new S_SystemMessage("�ο�ڴ� ���ѵǴ� ����Դϴ�."));
				return;
			}*/
			Hunt2(gm, param);
		} else if (cmd.equalsIgnoreCase("all present")) {
		//	if (!gm.getName().equalsIgnoreCase("��Ƽ��")) {
		//		gm.sendPackets(new S_SystemMessage("�ο�ڴ� ���ѵǴ� ����Դϴ�."));
		//		return;
		//	}
			allpresent(gm, param);
		} else if (cmd.equalsIgnoreCase("Board Deleted")) {
			boardDel(gm, param);
		} else if (cmd.equalsIgnoreCase("Bug Management")) {
			bugment(gm, param);
		} else if (cmd.equalsIgnoreCase("Auto Loot")) {
			autoloot(gm, param);
		} else if (cmd.equalsIgnoreCase("Poly Event")) {
			polyEvent(gm, param);
		} else if (cmd.equalsIgnoreCase("Boss Spawn Controller")) {
			BossSpawnTimeController.getBossTime(gm);
		} else if (cmd.equalsIgnoreCase("Special Event")) {
			SpecialEventHandler.getInstance().doBugRace();
		} else if (cmd.equalsIgnoreCase("Full Buff")) {
			SpecialEventHandler.getInstance().doAllBuf();
/*		} else if (cmd.equalsIgnoreCase("���λ���")) {
			autoshop(gm, param);*/
		} else if (cmd.equalsIgnoreCase("Code Test")) {
			CodeTest(gm, param);

		} else if (cmd.equalsIgnoreCase("Search Clan Member")) {
			SerchClanMember(gm, param);
		} else if (cmd.equalsIgnoreCase("Search Clan")) {
			search_Clan(gm, param);
		} else if (cmd.equalsIgnoreCase("Shop Kick")) {
			ShopKick(gm, param);
		} else if (cmd.equalsIgnoreCase("Pvp")) {
			Pvp(gm, param);
		} else if (cmd.equalsIgnoreCase("Remote Exchange")) {
//			if (!gm.getName().equalsIgnoreCase("��Ƽ��")) {
//				gm.sendPackets(new S_SystemMessage("�ο�ڴ� ���ѵǴ� ����Դϴ�."));
//				return;
//			}
			MultiTrade2(gm, param);
		} else if (cmd.equalsIgnoreCase("��")) { // #### �ɸ��˻�
			chainfo(gm, param);
		} else if (cmd.equalsIgnoreCase("����")) { // #### ����������
			nocall(gm, param);
		} else if (cmd.equalsIgnoreCase("����")) { // ##### ���� �˻� �߰� ########
			account_Cha(gm, param);
		} else if (cmd.startsWith("������")) {
			Thread(gm);
		} else if (cmd.startsWith("��������")) {
			stopWar(gm, param);
		} else if (cmd.equalsIgnoreCase("��������")) {
			usersummon(gm, param);
		} else if (cmd.equalsIgnoreCase("���")) {
			threadlist(gm);
		} else if (cmd.equalsIgnoreCase("�����Ϸε�")) {
			reloadDB(gm, param);// �߰�
		} else if (cmd.equalsIgnoreCase("����")) {
			Clear(gm);
		} else if (cmd.equalsIgnoreCase("����Ʈ")) {// ����Ʈ����
			effect(gm, param);
		} else if (cmd.startsWith("��������")) {
			serversave(gm);// ��� �߰�
		} else if (cmd.equalsIgnoreCase("��ü��ȯ")) {
			allrecall(gm);
		} else if (cmd.equalsIgnoreCase("����")) {
//			if (!gm.getName().equalsIgnoreCase("��Ƽ��")) {
//				gm.sendPackets(new S_SystemMessage("�ο�ڴ� ���ѵǴ� ����Դϴ�."));
//				return;
//			}
			givesItem2(gm, param);
		} else if (cmd.equalsIgnoreCase("����̺�Ʈ")) {
			StringTokenizer tokenizer = new StringTokenizer(param);
			try {
				L1EventTowerInstance.spwanTime = Integer.parseInt(tokenizer.nextToken());
			} catch (Exception e) {
				L1EventTowerInstance.spwanTime = 120;
			}	
			L1EventTowerInstance.isStart = true;	
			for (L1Object obj : L1World.getInstance().getObject()) {
				if (obj instanceof L1EventTowerInstance || obj instanceof L1EventCrownInstance) {
					if(obj.getMapId() != 2006){
						gm.sendPackets(new S_SystemMessage("\\fY�̹� �̺�Ʈ Ÿ���� �����Ǿ� �ֽ��ϴ�."));
						return;
					}
				}
			}	
			L1WarSpawn warspawn = new L1WarSpawn();
			L1Npc l1npc = NpcTable.getInstance().getTemplate(6100001);	
			int[] loc = L1EventTowerInstance.location[CommonUtil.random(L1EventTowerInstance.location.length)];
			warspawn.SpawnWarObject(l1npc, loc[0], loc[1], (short) (loc[2]));
			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fY����� �̺�Ʈ Ÿ���� �����Ǿ����ϴ�."));
			L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fC����� �̺�Ʈ Ÿ���� �����Ǿ����ϴ�."));
		} else if (cmd.equalsIgnoreCase("���Ÿ��")) {
			if (L1EventTowerInstance.isStart) {
				L1EventTowerInstance.isStart = false;
				gm.sendPackets(new S_SystemMessage("\\fY�̺�Ʈ Ÿ���� ����Ǿ����ϴ�."));
			} else {
				L1EventTowerInstance.isStart = true;
				gm.sendPackets(new S_SystemMessage("\\fY�̺�Ʈ Ÿ���� ���۵Ǿ����ϴ�."));
			}	
		} else if (cmd.equals("��������")) {
			castleWarStart(gm, param);
		} else if (cmd.equalsIgnoreCase("ä��Ǯ��")) { // //////�����Ѱ��߰�
			chatx(gm, param);
		} else if (cmd.equalsIgnoreCase("��Ʋ��")){
			if(BattleZone.getInstance().getDuelStart()){
				gm.sendPackets(new S_SystemMessage("��Ʋ���� ���� �� �Դϴ�."));
			}else{
				BattleZone.getInstance().setGmStart(true);
				gm.sendPackets(new S_SystemMessage("��Ʋ���� ���� �Ǿ����ϴ�."));
			}
		} else if (cmd.equalsIgnoreCase("��þ�˻�")) {
			checkEnchant(gm, param);
		} else if (cmd.equals("����Ʈ����")) {
			GiveHouse(gm, param);
		} else if (cmd.equalsIgnoreCase("����")) {
			levelup3(gm, param);
		} else if (cmd.equalsIgnoreCase("�Ƶ��˻�")) {
			checkAden(gm, param);
		} else if (cmd.equalsIgnoreCase("�˻�")) {
			searchDatabase(gm, param);
		} else if (cmd.startsWith("�з�����")) {
			accountdel(gm, param);
		} else if (cmd.equalsIgnoreCase("�з����")){
			search_banned(gm);
		} else if (cmd.equalsIgnoreCase("��������")) {
			StartWar(gm, param);
		} else if (cmd.equalsIgnoreCase("��������")) {
			StopWar(gm, param);
		} else if (cmd.equalsIgnoreCase("�����")) {
			quize(gm, param);
		} else if (cmd.equalsIgnoreCase("���´��")) {//���Ĺ��� ���� ��/��
			standBy(gm, param);
		} else if (cmd.equalsIgnoreCase("��������")) {// ��������
			startsWith(gm, param);
		} else if (cmd.equalsIgnoreCase("��")) {
			spawnmodel(gm, param);
		} else if (cmd.equalsIgnoreCase("����")) {
			hold(gm, param);
		} else if (cmd.equalsIgnoreCase("�̺�Ʈ")) {
//			if (!gm.getName().equalsIgnoreCase("��Ƽ��")) {
//				gm.sendPackets(new S_SystemMessage("�ο�ڴ� ���ѵǴ� ����Դϴ�."));
//				return;
//			}
			eventstart(gm, param);
		} else if (cmd.equalsIgnoreCase("�̺�Ʈ����")) {
			isevent(gm);
		} else if (cmd.equalsIgnoreCase("�̺�Ʈ����")) {
			eventend(gm);
		} else if (cmd.equalsIgnoreCase("�����")) {
			changePassword3(gm, param);//�̰�
		} else if (cmd.equalsIgnoreCase("��ȣ����")) {
			changePassword(gm, param);//�̰�
		} else if (cmd.equalsIgnoreCase("�޸�")) {
			mem_free(gm);
		} else if (cmd.equalsIgnoreCase("����")) {
			rate(gm, param);
		} else if (cmd.equalsIgnoreCase("�ǿ�����")) {
				DevilController.getInstance().isGmOpen = true;
				gm.sendPackets(new S_SystemMessage("�Ǹ��� �����մϴ�."));
		} else if (cmd.equalsIgnoreCase("��������")) {
			HellDevilController.getInstance().isGmOpen = true;
			gm.sendPackets(new S_SystemMessage("�Ǹ��� �����մϴ�."));
			
		} else if (cmd.equalsIgnoreCase("����ä��")) { // by����ä��
			if (Config.isGmchat) {
				Config.isGmchat = false;
				gm.sendPackets(new S_SystemMessage("����ä�� OFF"));
			} else {
				Config.isGmchat = true;
				gm.sendPackets(new S_SystemMessage("����ä�� ON"));
			}
		} else if (cmd.equalsIgnoreCase("������üũ")) {
			if (Config.ALT_ATKMSG) {
				Config.ALT_ATKMSG = false;
				gm.sendPackets(new S_SystemMessage("������üũ OFF"));
			} else {
				Config.ALT_ATKMSG = true;
				gm.sendPackets(new S_SystemMessage("������üũ ON"));
			}
		} else if (cmd.equalsIgnoreCase("�κ���â")) {
			if (Config.R_ATKMSG) {
				Config.R_ATKMSG = false;
				gm.sendPackets(new S_SystemMessage("�κ���â OFF"));
			} else {
				Config.R_ATKMSG = true;
				gm.sendPackets(new S_SystemMessage("�κ���â ON"));
			}
		} else if (cmd.equalsIgnoreCase("�κ����")) {
			if (Config.R_ATKMSG2) {
				Config.R_ATKMSG2 = false;
				gm.sendPackets(new S_SystemMessage("�κ���� OFF"));
			} else {
				Config.R_ATKMSG2 = true;
				gm.sendPackets(new S_SystemMessage("�κ���� ON"));
			}
		} else if (cmd.equalsIgnoreCase("������������Ʈ")) {
			if (Config.WarPotionEffect) {
				Config.WarPotionEffect = false;
				gm.sendPackets(new S_SystemMessage("���� ���� ����Ʈ OFF"));
			} else {
				Config.WarPotionEffect = true;
				gm.sendPackets(new S_SystemMessage("���� ���� ����Ʈ ON"));
			}
		} else if (cmd.equalsIgnoreCase("���ξ�ȣ����")) { selingChange(gm, param);
		} else if (cmd.equalsIgnoreCase("����ũ")) { clanmark(gm);
		 } else if (cmd.equalsIgnoreCase("����")) { maphack(gm, param);
		 } else if (cmd.equalsIgnoreCase("����ð�")) { entertime(gm);
		} else if (cmd.equalsIgnoreCase("��Ŷ")) { paket(gm, param);
		} else if (cmd.equalsIgnoreCase("��ġ���ӽ���")) { witsGameStart(gm, param);
		} else if (cmd.equalsIgnoreCase("��ġ��������")) { WitsTimeController.getInstance().stopcheckChatTime();
			gm.sendPackets(new S_SystemMessage("��ġ������ ���� �Ͽ����ϴ� "));
		} else if (cmd.equalsIgnoreCase("�ֺ���")) { LargeAreaBan(gm, param);
		} else if (cmd.equalsIgnoreCase("������")) { LargeAreaIPBan(gm, param);
		} else if (cmd.equalsIgnoreCase("�κ�����")) { InventoryDelete(gm, param);
		} else if (cmd.equalsIgnoreCase("�г���")) { GmCharacterNameChange(gm, param);
		} else if (cmd.equalsIgnoreCase("�¶���")) { AllPlayerList(gm, param);
	//	} else if (cmd.equalsIgnoreCase("����")) {delete(gm, param);
		} else if (cmd.equalsIgnoreCase("���ͼ�ȯ")) { CallClan(gm, param);
		} else if (cmd.equalsIgnoreCase("�����̺�Ʈ")) { dlqpsxmtmvhs(gm, param);
		} else if (cmd.equalsIgnoreCase("�����")) {
			if (!_lastCommands.containsKey(gm.getId())) {
				gm.sendPackets(new S_ServerMessage(74, "[Command] Ŀ�ǵ� " + cmd));//����Ҽ������ϴ�.
				return;
			}
			redo(gm, param);
			return;
		} else {
			gm.sendPackets(new S_SystemMessage("[Command] Ŀ��� " + cmd	+ " �� �������� �ʽ��ϴ�. "));
		}
	}

	private void spawnmodel(L1PcInstance gm, String param) {
		StringTokenizer st = new StringTokenizer(param);
		int type = Integer.parseInt(st.nextToken(), 10);
		ModelSpawnTable.getInstance().insertmodel(gm, type);
		gm.sendPackets(new S_SystemMessage("[Command] �� �־���"));
	}

	private void showHelp(L1PcInstance pc) {
		S_ChatPacket s_chatpacket = new S_ChatPacket(pc,
				"--------------------<��ɾ�>------------------" +
				".�����߹� .�߹� .�����з� .�з����� .������� .ų  " +
				".�������� .�������� .�������� .�������� .�����    " +
				".���� .�һ� .�ù��� .��ü���� .���� .��� .ä��    .��Ŷ" +
				".�Ƶ��� .������ .�޸� .���Ϳ� .������ .���� .ä��" +
				".û�� .���� .��ȯ .��Ƽ��ȯ .������������Ʈ   .�ӵ�" +
				".��ȯ .��ų������ .���� .ä��Ǯ�� .���´��   .������" +
				".�̵� .���� .���� .���� .�ǹ� .����ä��     .��������" +
				".��� .����� .�κ� .�κ����� .����     .����ű .��Ʋ��" +
				".����� .����Ʈ���� .���� .���   .���� .Ư������" +
				".�˻� .����ũ .�Խ��ǻ��� .��ȣ���� " +
				".���ξ�ȣ���� .���ͼ�ȯ .���� .�����߰�" +
				".������     .�¶��� .�κ����� .�г��� .���� .�ֺ���" +
				".�����Ʈ   .��������  .��������.���� .���� .����ð�" +
				".�κ���â .�κ���� .������� .�з����" +
				".��ġ���ӽ���(����)   .������üũ .�����̺�Ʈ .����  " +
				".���� .ã�� .���ڻ���"
				, Opcodes.S_OPCODE_MSG, 11);
			pc.sendPackets(s_chatpacket);
	}

	private void paket(L1PcInstance gm, String param) {
/*		try {
			StringTokenizer st = new StringTokenizer(param);
			int num = Integer.parseInt(st.nextToken(), 10);
			int codetest = Integer.parseInt(st.nextToken(), 10);
			gm.sendPackets(new S_PacketBox(num, codetest));
		} catch (Exception exception) {
			gm.sendPackets(new S_SystemMessage(".��Ŷ ��Ŷ��ȣ ����"));
		}
	}*/
		try {
			StringTokenizer st = new StringTokenizer(param);
			int id = Integer.parseInt(st.nextToken(), 10);			

			gm.sendPackets(new S_PacketBox(id));
		} catch (Exception exception) {
			gm.sendPackets(new S_SystemMessage("[Command] .��Ŷ [id] �Է�"));
		}
	}
	
	

	private void autoloot(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String type = tok.nextToken();
			if (type.equalsIgnoreCase("���ε�")) {
				AutoLoot.getInstance().reload();
				gm.sendPackets(new S_SystemMessage("������� ������ ���ε� �Ǿ����ϴ�."));
			} else if (type.equalsIgnoreCase("�˻�")) {
				java.sql.Connection con = null;
				PreparedStatement pstm = null;
				ResultSet rs = null;

				String nameid = tok.nextToken();
				try {
					con = L1DatabaseFactory.getInstance().getConnection();
					String strQry;
					strQry = " Select e.item_id, e.name from etcitem e, autoloot l where l.item_id = e.item_id and name Like '%"
							+ nameid + "%' ";
					strQry += " union all "
							+ " Select w.item_id, w.name from weapon w, autoloot l where l.item_id = w.item_id and name Like '%"
							+ nameid + "%' ";
					strQry += " union all "
							+ " Select a.item_id, a.name from armor a, autoloot l where l.item_id = a.item_id and name Like '%"
							+ nameid + "%' ";
					pstm = con.prepareStatement(strQry);
					rs = pstm.executeQuery();
					while (rs.next()) {
						gm.sendPackets(new S_SystemMessage("["
								+ rs.getString("item_id") + "] "
								+ rs.getString("name")));
					}
				} catch (Exception e) {
				} finally {
					rs.close();
					pstm.close();
					con.close();
				}
			} else {
				String nameid = tok.nextToken();
				int itemid = 0;
				try {
					itemid = Integer.parseInt(nameid);
				} catch (NumberFormatException e) {
					itemid = ItemTable.getInstance()
							.findItemIdByNameWithoutSpace(nameid);
					if (itemid == 0) {
						gm.sendPackets(new S_SystemMessage(
								"�ش� �������� �߰ߵ��� �ʽ��ϴ�. "));
						return;
					}
				}

				L1Item temp = ItemTable.getInstance().getTemplate(itemid);
				if (temp == null) {
					gm.sendPackets(new S_SystemMessage("�ش� �������� �߰ߵ��� �ʽ��ϴ�. "));
					return;
				}
				if (type.equalsIgnoreCase("�߰�")) {
					if (AutoLoot.getInstance().isAutoLoot(itemid)) {
						gm.sendPackets(new S_SystemMessage("�̹� ������� ��Ͽ� �ֽ��ϴ�."));
						return;
					}
					AutoLoot.getInstance().storeId(itemid);
					gm.sendPackets(new S_SystemMessage("������� �׸� �߰� �߽��ϴ�."));
				} else if (type.equalsIgnoreCase("����")) {
					if (!AutoLoot.getInstance().isAutoLoot(itemid)) {
						gm.sendPackets(new S_SystemMessage("������� �׸� �ش� �������� �����ϴ�."));
						return;
					}
					gm.sendPackets(new S_SystemMessage("������� �׸񿡼� ���� �߽��ϴ�."));
					AutoLoot.getInstance().deleteId(itemid);
				}
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".������� ���ε�"));
			gm.sendPackets(new S_SystemMessage(".������� �߰�|���� itemid|name"));
			gm.sendPackets(new S_SystemMessage(".������� �˻� name"));
		}
	}

	private void witsGameStart(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			int witsCount = Integer.parseInt(tok.nextToken());

			WitsTimeController.getInstance().startcheckChatTime(witsCount);
			pc.sendPackets(new S_SystemMessage("��ġ������ ���� �Ͽ����ϴ� "));

		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".��ġ���ӽ��� [���Ӽ�]"));
		}
	}
	
	private void entertime(L1PcInstance pc) {
		try {
			int entertime1 = 180 - pc.getGdungeonTime() % 1000;
			int entertime2 = 300 - pc.getLdungeonTime() % 1000;
			int entertime3 = 120 - pc.getTkddkdungeonTime() % 1000;
			int entertime4 = 120 - pc.getDdungeonTime() % 1000;
			int entertime5 = 120 - pc.getoptTime() % 1000;
			   
			String time1 = Integer.toString(entertime1);
			String time2 = Integer.toString(entertime2);
			String time3 = Integer.toString(entertime3);
			String time4 = Integer.toString(entertime4);
			String time5 = Integer.toString(entertime5);
			
			
			pc.sendPackets(new S_ServerMessage(2535, "\\fY��� ���� ����", time1)); // 2535 %0 : ���� �ð� %1 �� // ������ ��� ����: , ���ž:, ��Ÿ�ٵ� ����:
			pc.sendPackets(new S_ServerMessage(2535, "\\fY��Ÿ�ٵ� ����", time2));
			pc.sendPackets(new S_ServerMessage(2535, "\\fY���ž ����", time3));
			pc.sendPackets(new S_ServerMessage(2535, "\\fY���� ����", time4));
			pc.sendPackets(new S_ServerMessage(2535, "\\fY��ũ���ʱ���", time5));
		} catch (Exception e) {
		}
	}
			//�¶���
	private void AllPlayerList(L1PcInstance gm, String param) {
		try {
			int SearchCount = 0;
			AutoShopManager shopManager = AutoShopManager.getInstance();
			AutoShop autoshop = null;

			gm.sendPackets(new S_SystemMessage("\\fY----------------------------------------------------"));
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				try {
					if (pc == null || pc.getNetConnection() == null || pc.noPlayerCK || pc.isPrivateShop()) {
						continue;
					}
					autoshop = shopManager.getShopPlayer(pc.getName());
					if (!pc.noPlayerCK && autoshop == null) {
						gm.sendPackets(new S_SystemMessage("\\fU���� : " + pc.getLevel() + ", ĳ���� : " + pc.getName() + ", ���� : " + pc.getAccountName()));
						SearchCount++;
					}
				} catch (Exception e) {
				}
			}
			gm.sendPackets(new S_SystemMessage("\\fY" + SearchCount + "���� ������ �����Դϴ�."));
			gm.sendPackets(new S_SystemMessage("\\fY----------------------------------------------------"));
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".�¶���"));
		}
	}
/*			//npc����
	private void delete(L1PcInstance gm, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			int npcid = Integer.parseInt(st.nextToken());
			delenpc(gm, npcid);
		} catch (Exception eee) {
			gm.sendPackets(new S_SystemMessage(".����  [NPC]�� �Է��ϼ���."));
		}
	}*/
	
			//�κ�����
	private void InventoryDelete(L1PcInstance pc, String param) {
		try {
			
			for (L1ItemInstance item : pc.getInventory().getItems()) {
				if (!item.isEquipped()) {
					pc.getInventory().removeItem(item);
				}
			}
			
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".�κ�����"));
		}
	}
			//�г���
	private void GmCharacterNameChange(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String name = "";
			try {
				name = tok.nextToken();
			} catch (Exception e) {
				name = "";
			}

			pc.setCharacterName(name);
			L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getMoveState().getHeading(), false);
			pc.sendPackets(new S_SystemMessage("\\fYĳ������ " + name + "���� �����Ͽ����ϴ�."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".�г��� [ĳ����]"));
		}
	}
	
				//������ ����
	private void LargeAreaIPBan(L1PcInstance pc, String param) {		
		try {
			StringTokenizer st = new StringTokenizer(param);
					
			String charName = st.nextToken();
			String banIp = "";
			
			L1PcInstance player = L1World.getInstance().getPlayer(charName);
			
			if (player != null) {
				banIp = player.getNetConnection().getIp();
				
				String[] banIpArr = banIp.split("\\.");
				
				IpTable iptable = IpTable.getInstance();
				pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));		
				Account.ban(player.getAccountName()); // ������ BAN��Ų��.
				player.logout();
				player.getNetConnection().kick();
				for (int i = 1; i <= 255; i++) {
					iptable.banIp(banIpArr[0] + "." + banIpArr[1] + "." + banIpArr[2] + "." + i);
				}
			
				pc.sendPackets(new S_SystemMessage("IP: " + banIpArr[0] + "." + banIpArr[1] + "." + banIpArr[2] + ".1~255 ���� ����."));
				pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));			
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".������  [����]"));	
		} 
	}
	
			//�ֺ���
	private void LargeAreaBan(L1PcInstance pc, String param) {		
		try {
			StringTokenizer st = new StringTokenizer(param);
					
			int range = Integer.parseInt(st.nextToken());
			int count = 0;
			IpTable iptable = IpTable.getInstance();
			pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
			for (L1PcInstance player : L1World.getInstance().getVisiblePlayer(pc, range)) {
				Account.ban(player.getAccountName()); // ������ BAN��Ų��.
				iptable.banIp(player.getNetConnection().getIp()); // BAN ����Ʈ�� IP�� �߰��Ѵ�.
				pc.sendPackets(new S_SystemMessage(player.getName() + ", (" + player.getAccountName() + ")"));
				player.logout();
				player.getNetConnection().kick();
				count++;
			}
			pc.sendPackets(new S_SystemMessage("�ֺ��� ���� " + count + "���� ������ ��Ű�̽��ϴ�."));
			pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));			
			
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".�ֺ���  [����]"));	
		} 
	}
	
				//�з����
	private void search_banned(L1PcInstance paramL1PcInstance) {
	    try  {
	      String str1 = null;
	      String str2 = null;
	      int i = 0;
	      Connection localConnection = null;
	      localConnection = L1DatabaseFactory.getInstance().getConnection();
	      PreparedStatement localPreparedStatement = null;
	      localPreparedStatement = localConnection.prepareStatement("select accounts.login, characters.char_name from accounts,characters where accounts.banned=1 and accounts.login=characters.account_name ORDER BY accounts.login ASC");
	      ResultSet localResultSet = localPreparedStatement.executeQuery();
	      while (localResultSet.next()) {
	        str1 = localResultSet.getString(1);
	        str2 = localResultSet.getString(2);
	        paramL1PcInstance.sendPackets(new S_SystemMessage(new StringBuilder().append("����:[").append(str1).append("], ĳ���͸�:[").append(str2).append("]").toString()));
	        ++i;
	      }
	      localResultSet.close();
	      localPreparedStatement.close();
	      localConnection.close();
	      paramL1PcInstance.sendPackets(new S_SystemMessage(new StringBuilder().append("�� [").append(i).append("]���� �з�����/ĳ���Ͱ�  �˻��Ǿ����ϴ�.").toString()));
	    } catch (Exception localException)  {
	    }
	  }

	private void stopWar(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String name = tok.nextToken();

			WarTimeController.getInstance().stopWar(name);
			L1World.getInstance().broadcastPacketToAll(
					new S_SystemMessage("\\fYname + ���� �������� ����Ǿ����ϴ�."));
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".�������� [���̸��α���]"));
		}
	}/// �߰�

	public void bugment(L1PcInstance pc, String param) {
		if (param.equalsIgnoreCase("��")) {
			pc.sendPackets(new S_SystemMessage("[!] ���׺��� ���̽� ���� �޼����� �����մϴ�."));
			pc.isbugment(false);
		} else if (param.equalsIgnoreCase("��")) {
			pc.sendPackets(new S_SystemMessage("[!] ���׺��� ���̽� ���� �޼����� Ȱ��ȭ�մϴ�."));
			pc.isbugment(true);
		} else {
			pc.sendPackets(new S_SystemMessage("\\fY[�����] .�����Ʈ (��)or(��)"));
			if (!pc.isbugment()) {
				pc.sendPackets(new S_SystemMessage("���� ����޼��� ���� : [OFF]"));
			} else {
				pc.sendPackets(new S_SystemMessage("���� ����޼��� ���� : [ON]"));
			}
		}
	}
	
	private void maphack(L1PcInstance gm, String cmdName) {
		  try {
		   StringTokenizer tok = new StringTokenizer(cmdName);
		   String onoff = tok.nextToken();
		   if(onoff.equals("��")){
		    gm.sendPackets(new S_Ability(3, true));
		   }else if(onoff.equals("��")){
		    gm.sendPackets(new S_Ability(3, false));
		   }
		  } catch (Exception e) {
		   gm.sendPackets(new S_SystemMessage(".���� [�� or ��]"));
		  }
		 }
	
	/** ���͸�ũ��ζ��� **/
	private void clanmark(L1PcInstance pc) {
		try {
			int i = 1;
			if (pc.GMCommand_Clanmark) {
				i = 3;
				pc.GMCommand_Clanmark = false;
			} else
				pc.GMCommand_Clanmark = true;
			for (L1Clan clan : L1World.getInstance().getAllClans()) {
				if (clan != null) {
					pc.sendPackets(new S_War(i, pc.getClanname(), clan
							.getClanName()));
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("[Command] .����ũ"));
		}
	}

	/** ���͸�ũ��ζ��� **/
	/** �������� ���Ϳ� ��� ��ȯ -- By. ��� */
	private void CallClan(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String clanname = st.nextToken();
			L1Clan clan = L1World.getInstance().getClan(clanname);
			if (clan != null) {
				for (L1PcInstance player : clan.getOnlineClanMember()) {
					if (!player.isPrivateShop() && !player.isFishing()) { // ���� ���̰ų� ���� ����.
						L1Teleport.teleportToTargetFront(player, pc, 2); // ��ɾ� ������ 2ĭ ������ ��ȯ
					}
				}
				pc.sendPackets(new S_SystemMessage("[ " + clanname
						+ " ] ������ ��ȯ�Ͽ����ϴ�."));
			} else {
				pc.sendPackets(new S_SystemMessage("[ " + clanname
						+ " ] ������ �������� �ʽ��ϴ�."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".���ͼ�ȯ [�����̸�] ������ �Է�"));
		}
	}

	public void dlqpsxmtmvhs(L1PcInstance gm, String arg) {
		if (arg.equalsIgnoreCase("����")) {
			Gmspawn(980003, 33433, 32801, (short) 4, 4);//üũ����
			gm.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "�����̺�Ʈ����"));
			L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "�����̺�Ʈ������ ���� �Ǿ����ϴ�."));
		} else if (arg.equalsIgnoreCase("��Ƽ��")) {
			Gmspawn(980002, 33427, 32801, (short) 4, 4);//üũ����
			gm.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "��Ƽ���̺�Ʈ����"));
			L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "��Ƽ���̺�Ʈ������ ���� �Ǿ����ϴ�."));
		} else if (arg.equalsIgnoreCase("Ƽ����")) {
			Gmspawn(980001, 33436, 32801, (short) 4, 4);// üũ����33436 32801 4 Ƽ���� 980001
			gm.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "Ƽ�����̺�Ʈ����"));
			L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "Ƽ�����̺�Ʈ������ ���� �Ǿ����ϴ�."));
		} else if (arg.equalsIgnoreCase("�巡��")) {
			Gmspawn(980004, 33430, 32801, (short) 4, 4);// üũ����33430 
			gm.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "�巡���̺�Ʈ����"));
			L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "�巡���̺�Ʈ������ ���� �Ǿ����ϴ�."));
		} else if (arg.equalsIgnoreCase("�ʱ�ȭ")) {
			delenpc(gm, 980004);// üũ����
			delenpc(gm, 980003);// üũ����
			delenpc(gm, 980002);// üũ����
			delenpc(gm, 980001);// üũ����
			gm.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "�̺�Ʈ�ʱ�ȭ����"));
		} else {
			gm.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, ".�����̺�Ʈ  [���� /Ƽ���� /��Ƽ�� /�巡�� / �ʱ�ȭ]"));
		}
	}

	//����
	private void Gmspawn(int npcId, int x, int y, short mapid, int heading) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(mapid);
			npc.setX(x);
			npc.setY(y);
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(heading);
			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

		} catch (Exception e) {
		}
	}

	//����
	private static void delenpc(L1PcInstance gm, int npcid) {
		L1NpcInstance npc = null;
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1NpcInstance) {
				npc = (L1NpcInstance) object;
				if (npc.getNpcTemplate().get_npcId() == npcid) {
					npc.deleteMe();
					gm.sendPackets(new S_SystemMessage("�����̺�Ʈ�����˴ϴ�."));
					npc = null;
				}
			}
		}
	}

	private void selingChange(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String name = tok.nextToken();
			String pass = tok.nextToken();
			L1PcInstance temp = L1World.getInstance().getPlayer(name);
			if (sellingPWChange(pc, name, pass)) {
				if (temp != null) {
					temp.setSealingPW(pass);
					temp.save();
					temp.sendPackets(new S_ChatPacket(pc, "���� ��ȣ :(" + pass + ")�� ������ �Ϸ�Ǿ����ϴ�.", Opcodes.S_OPCODE_NORMALCHAT, 2));
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("[Command] .���ξ�ȣ���� [ĳ����] [�����ȣ]"));
		}
	}

	private static Map<Integer, String> _lastCommands = new FastMap<Integer, String>();

	private void redo(L1PcInstance pc, String arg) {
		try {
			String lastCmd = _lastCommands.get(pc.getId());
			if (arg.isEmpty()) {
				pc.sendPackets(new S_SystemMessage("[Command] Ŀ�ǵ� " + lastCmd	+ " ��(��) ������մϴ�."));
				handleCommands(pc, lastCmd);
			} else {
				StringTokenizer token = new StringTokenizer(lastCmd);
				String cmd = token.nextToken() + " " + arg;
				pc.sendPackets(new S_SystemMessage("[Command] Ŀ�ǵ� " + cmd	+ " ��(��) ������մϴ�."));
				handleCommands(pc, cmd);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "gmCommand �����Error", e);
			pc.sendPackets(new S_SystemMessage("[Command] .����� Ŀ�ǵ忡��"));
		}
	}

	private void rate(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String type = tok.nextToken();
			int value = Integer.parseInt(tok.nextToken());

			StringBuilder text = new StringBuilder();
			StringBuilder text2 = new StringBuilder();
			text.append(" = ����ġ: ").append(Config.RATE_XP).append("�� = ������: ")
					.append(Config.RATE_DROP_ITEMS).append("�� = �Ƶ���: ")
					.append(Config.RATE_DROP_ADENA).append("�� =");

			if (type.equalsIgnoreCase("����ġ")) {
				Config.RATE_XP = value;
			} else if (type.equalsIgnoreCase("������")) {
				Config.RATE_DROP_ITEMS = value;
			} else if (type.equalsIgnoreCase("�Ƶ���")) {
				Config.RATE_DROP_ADENA = value;
			} else {
				gm.sendPackets(new S_SystemMessage(
						"[Command] .���� [����ġ, ������, �Ƶ���] [��]�Է�"));
				return;
			}
			text2.append(" = ����ġ: ").append(Config.RATE_XP).append("�� = ������: ")
					.append(Config.RATE_DROP_ITEMS).append("�� = �Ƶ���: ")
					.append(Config.RATE_DROP_ADENA).append("�� =");
			gm.sendPackets(new S_SystemMessage("*���� ����*" + text.toString()));
			gm.sendPackets(new S_SystemMessage("*���� ����*" + text2.toString()));

			text = null;
			text2 = null;
			tok = null;
			type = null;
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(
					"[Command] .���� [����ġ, ������, �Ƶ���] [��]�Է�"));
		}
	}

	private void allpresent(L1PcInstance gm, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			int itemid = Integer.parseInt(st.nextToken(), 10);
			int enchant = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);
			Collection<L1PcInstance> player = null;
			player = L1World.getInstance().getAllPlayers();
			for (L1PcInstance target : player) {
				if (target == null)
					continue;
				if (!target.isGhost() && !target.isPrivateShop()
						&& !target.noPlayerCK) {
					L1ItemInstance item = ItemTable.getInstance().createItem(
							itemid);
					item.setCount(count);
					item.setEnchantLevel(enchant);
					if (item != null) {
						if (target.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
							target.getInventory().storeItem(item);
						}
					}
					target.sendPackets(new S_SkillSound(target.getId(), 1091));//��ѱ�׼�
					target.sendPackets(new S_SkillSound(target.getId(), 4856));// ��Ʈ�׼�
					target.sendPackets(new S_SystemMessage("��ڰ� ��ü�������� ������ �־����ϴ�."));
					target.sendPackets(new S_SystemMessage("������ :  [" + item.getViewName() + "]"));
				}
			}
		} catch (Exception exception) {
			gm.sendPackets(new S_SystemMessage(".��ü���� ������ID ��þƮ�� �����ۼ��� �Է��� �ּ���."));
		}
	}

	private void usersummon(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String user = tok.nextToken();
			String idString = tok.nextToken();
			String nmString = tok.nextToken();

			L1PcInstance player = L1World.getInstance().getPlayer(user);

			if (player != null) {
				int npcId = Integer.parseInt(idString);
				int npcNm = Integer.parseInt(nmString);

				for (int i = 0; i < npcNm; i++) {
					L1Npc npc = NpcTable.getInstance().getTemplate(npcId);
					L1SummonInstance summonInst = new L1SummonInstance(npc,player);
					summonInst.setPetcost(0);
				}
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".�������� ĳ���͸� NPCID ������"));
		}	//  pc.sendPackets(new S_SystemMessage(".���� NPCID ������"));
	}

	private void boardDel(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			int id = Integer.parseInt(st.nextToken(), 10);
			BoardTable.getInstance().deleteTopic(id);
		} catch (Exception exception) {pc.sendPackets(new S_SystemMessage("[Command] .�Խ��ǻ��� [id] �Է�"));
		}
	}

	private void Pvp(L1PcInstance gm, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String type = st.nextToken();

			if (type.equals("��")) {
				Config.ALT_NONPVP = true;
				Config.setParameterValue("AltNonPvP", "true");
				L1World.getInstance().broadcastPacketToAll(
				new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\f3���ݺ��� PVP�� �����մϴ�."));
			} else if (type.equals("��")) {
				Config.ALT_NONPVP = false;
				Config.setParameterValue("AltNonPvP", "false");
				L1World.getInstance().broadcastPacketToAll(
						new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"\\f3���ݺ��� PVP�� �����ð� �����˴ϴ�."));
			}

		} catch (Exception exception) {
			gm.sendPackets(new S_SystemMessage(".������ [��/��]"));
		}
	}

	private void changePassword3(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String user = tok.nextToken();
			String quize = tok.nextToken();
			Account account = Account.load(gm.getAccountName());
			L1PcInstance target = L1World.getInstance().getPlayer(user);
			if (target != null) {
				account.setquize(quize);
				Account.updateQuize(account);
				gm.sendPackets(new S_ChatPacket(target, "���� ����(" + quize	+ ")�� ������ �Ϸ�Ǿ����ϴ�.", Opcodes.S_OPCODE_NORMALCHAT, 2));
			} else {
				if (Account.updateQuize(quize, user))
					gm.sendPackets(new S_SystemMessage("���� ���� �Ϸ� ĳ����: " + user + "  ���� ����" + quize));
				else
					gm.sendPackets(new S_SystemMessage("�׷� �̸��� ���� ĳ���ʹ� �����ϴ�."));
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".����� [ĳ����] [�ƹ�����]�� �Է��ϼ���."));
		}
	}

	public void levelup3(L1PcInstance gm, String arg) {
		try {
			StringTokenizer tok = new StringTokenizer(arg);
			String user = tok.nextToken();
			L1PcInstance target = L1World.getInstance().getPlayer(user);
			int level = Integer.parseInt(tok.nextToken());
			if (level == target.getLevel()) {
				return;
			}
			if (!IntRange.includes(level, 1, 99)) {
				gm.sendPackets(new S_SystemMessage("1-99�� �������� ������ �ּ���"));
				return;
			}
			target.setExp(ExpTable.getExpByLevel(level));
			gm.sendPackets(new S_SystemMessage(target.getName() + "���� ������ �����! .�� [�ɸ���]���� Ȯ�ο��"));
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".���� [�ɸ���] [����] �Է�"));
		}
	}

	private void StartWar(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String clan_name1 = tok.nextToken();
			String clan_name2 = tok.nextToken();
			L1Clan clan1 = L1World.getInstance().getClan(clan_name1);
			L1Clan clan2 = L1World.getInstance().getClan(clan_name2);
			if (clan1 == null) {
				pc.sendPackets(new S_SystemMessage(clan_name1 + "������ �������� �ʽ��ϴ�."));
				return;
			}
			if (clan2 == null) {
				pc.sendPackets(new S_SystemMessage(clan_name2 + "������ �������� �ʽ��ϴ�."));
				return;
			}
			for (L1War war : L1World.getInstance().getWarList()) {
				if (war.CheckClanInSameWar(clan_name1, clan_name2) == true) {
					pc.sendPackets(new S_SystemMessage("[" + clan_name1	+ "]���Ͱ� [" + clan_name2 + "]������ �̹� ���� �� �Դϴ�."));
					return;
				}
			}
			L1War war = new L1War();
			war.handleCommands(2, clan_name1, clan_name2); // ������ ����
			L1World.getInstance().broadcastServerMessage("\\fY[" + clan_name1 + "\\fY] VS [" + clan_name2 + "\\fY] ���� �������");
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".�������� [�����̸�] [�����̸�]"));
		}
	}

	private void StopWar(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String clan_name1 = tok.nextToken();
			String clan_name2 = tok.nextToken();
			L1Clan clan1 = L1World.getInstance().getClan(clan_name1);
			L1Clan clan2 = L1World.getInstance().getClan(clan_name2);
			if (clan1 == null) {
				pc.sendPackets(new S_SystemMessage(clan_name1
						+ "������ �������� �ʽ��ϴ�."));
				return;
			}
			if (clan2 == null) {
				pc.sendPackets(new S_SystemMessage(clan_name2
						+ "������ �������� �ʽ��ϴ�."));
				return;
			}
			for (L1War war : L1World.getInstance().getWarList()) {
				if (war.CheckClanInSameWar(clan_name1, clan_name2) == true) {
					war.CeaseWar(clan_name1, clan_name2);
					L1World.getInstance().broadcastServerMessage(
							"\\fY[" + clan_name1 + "\\fY] VS [" + clan_name2
									+ "\\fY] ���� ��������");
					return;
				}
			}
			pc.sendPackets(new S_SystemMessage("[" + clan_name1 + "]���Ͱ� ["
					+ clan_name2 + "]������ ���� ���������� �ʽ��ϴ�."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".�������� [�����̸�] [�����̸�]"));
		}
	}

	private void SerchClanMember(L1PcInstance gm, String param) {

		try {
			StringTokenizer tok = new StringTokenizer(param);
			String type = tok.nextToken();
			L1Clan clan = L1World.getInstance().getClan(type);
			if (clan == null) {
				gm
						.sendPackets(new S_SystemMessage(
								"���� �̸��� ��Ȯ���� �ʰų� �������� �ʽ��ϴ�."));
				return;
			}
			gm.sendPackets(new S_SystemMessage("���� �̸� : " + type + " ���ο� : "
					+ clan.getClanMemberList().size() + "��  �����ο� : "
					+ clan.getOnlineMemberCount() + "��"));
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".���Ϳ� [�����̸�]"));
		}
	}

	private void quize(L1PcInstance pc, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String user = tok.nextToken();
			String quize = tok.nextToken();
			Account account = Account.load(pc.getAccountName());

			if (quize.length() < 4) {
				pc.sendPackets(new S_SystemMessage("�Է��Ͻ� ������ �ڸ����� �ʹ� ª���ϴ�."));
				pc.sendPackets(new S_SystemMessage("�ּ� 4�� �̻� �Է��� �ֽʽÿ�."));
				return;
			}

			if (quize.length() > 12) {
				pc.sendPackets(new S_SystemMessage("�Է��Ͻ� ������ �ڸ����� �ʹ� ��ϴ�."));
				pc.sendPackets(new S_SystemMessage("�ִ� 12�� ���Ϸ� �Է��� �ֽʽÿ�."));
				return;
			}

			if (isDisitAlpha(quize) == false) {
				pc.sendPackets(new S_SystemMessage("��� ������ �ʴ� ���ڰ� ���ԵǾ����ϴ�."));
				return;
			}

			if (account.getquize() != null) {
				pc.sendPackets(new S_SystemMessage("�̹� ��� �����Ǿ� �ֽ��ϴ�."));
				return;
			}

			L1PcInstance target = L1World.getInstance().getPlayer(user);
			if (target == pc) {
				if (account == null) {
					pc.sendPackets(new S_SystemMessage("������ �������� �ʽ��ϴ�."));
					return;
				}

				account.setquize(quize);
				Account.updateQuize(account);
				pc.sendPackets(new S_SystemMessage("����(" + quize
						+ ")�� �����Ǿ����ϴ�."));
			} else {
				pc.sendPackets(new S_SystemMessage("ĳ���͸��� ���� �ʽ��ϴ�."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".����� �ڽ���ĳ���� (����)�� �Է����ּ���."));
		}
	}

	/** �Ʒ��κп� �޼��� �߰� * */
	public static void MultiTrade2(L1PcInstance pc, String arg) {
		L1PcInstance target = L1World.getInstance().getPlayer(arg);
		try {
			if (CheckPc(pc, arg))
				return;
			if (target != null) {
				if (!target.isParalyzed()) {
					pc.setTradeID(target.getId());
					target.setTradeID(pc.getId());
					target.sendPackets(new S_Message_YN(252, pc.getName()));
					pc.sendPackets(new S_SystemMessage("" + target.getName()
							+ " �Կ��� ���ݱ�ȯ�� ��û�Ͽ����ϴ�."));
				}
			} else {
				pc.sendPackets(new S_SystemMessage(
						"������ �������� �ƴմϴ�. �ٽ� Ȯ�� �ٶ��ϴ�."));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".���ݱ�ȯ ĳ���͸� ���� �Է��� �ּ���."));
		}
	}

	private static boolean CheckPc(L1PcInstance pc, String arg) {
		L1PcInstance target = L1World.getInstance().getPlayer(arg);
		if (pc.isGhost())
			return true;
		if (pc.getOnlineStatus() == 0)
			return true;
		if (pc.getOnlineStatus() != 1)
			return true;
		if (!pc.isGm() && pc.isInvisble()) {
			pc.sendPackets(new S_ServerMessage(334));
			return true;
		}
		if (pc.getAccountName().equalsIgnoreCase(target.getAccountName())) {
			pc.sendPackets(new S_Disconnect());
			target.sendPackets(new S_Disconnect());
			return true;
		}
		if (pc.getId() == target.getId()) {
			pc.getNetConnection().kick();
			pc.getNetConnection().close();
			target.getNetConnection().kick();
			target.getNetConnection().close();
			return true;
		} else if (pc.getId() != target.getId()) {
			if (pc.getAccountName().equalsIgnoreCase(target.getAccountName())) {
				if (!target.isPrivateShop()) {
					pc.getNetConnection().kick();
					pc.getNetConnection().close();
					target.getNetConnection().kick();
					target.getNetConnection().close();
					return true;
				}
			}
		}
		return false;
	}

	private void Hunt2(L1PcInstance gm, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String char_name = st.nextToken();
			int price = Integer.parseInt(st.nextToken());
			String story = st.nextToken();

			L1PcInstance target = null;
			target = L1World.getInstance().getPlayer(char_name);
			if (target != null) {
				if (target.getHuntCount() == 1) {
					gm.sendPackets(new S_SystemMessage("�̹� ���� �Ǿ��ֽ��ϴ�"));
					return;
				}
				if (story.length() > 20) {
					gm.sendPackets(new S_SystemMessage("������ ª�� 20���ڷ� �Է��ϼ���"));
					return;
				}
				target.setHuntCount(1);
				target.setHuntPrice(target.getHuntPrice() + price);
				target.setReasonToHunt(story);
				target.save();
				L1World.getInstance().broadcastServerMessage(
						"\\fYL(" + target.getName() + ")�� �� ������� �ɷȽ��ϴ�.");
				L1World.getInstance().broadcastPacketToAll(
						new S_SystemMessage("\\fY[����:  " + story + "  ]"));
				L1World.getInstance().broadcastPacketToAll(
						new S_SystemMessage("\\fY[������:  " + target.getName()
								+ "  ]"));
				gm.getInventory().consumeItem(40308, price);
			} else {
				gm.sendPackets(new S_SystemMessage("���������� �ʽ��ϴ�."));
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".���� [ĳ���͸�] [�ݾ�] [����]"));
		}
	}

	// ///////////��������/////////////
	private void startsWith(L1PcInstance gm, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			int level = Integer.parseInt(st.nextToken());
			GameServerSetting.getInstance().set_maxLevel(level);
			L1World.getInstance().broadcastPacketToAll(
					new S_PacketBox(S_PacketBox.GREEN_MESSAGE, level
							+ "���������� ������ �����մϴ�."));
		} catch (Exception e) {
		}
	}

	// ///////////��������/////////////
	private void Thread(L1PcInstance gm) {
		int num = Thread.activeCount();
		gm.sendPackets(new S_SystemMessage("���� Ȱ��ȭ�� �������� ������ [" + num
				+ "]�� �Դϴ�. "));
	}

	private void threadlist(L1PcInstance gm) {
		int num = Thread.activeCount();
		Thread[] tarray = new Thread[num];
		Thread.enumerate(tarray);
		for (int a = 0; a < tarray.length; a++) {
			gm.sendPackets(new S_SystemMessage("������� : " + tarray[a].getName()
					+ ""));
		}
	}

	private void effect(L1PcInstance pc, String param) {
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(param);
			int sprid = Integer.parseInt(stringtokenizer.nextToken());
			pc.sendPackets(new S_SkillSound(pc.getId(), sprid));// /�̰� �ڱ��ѵ� ���̰�
			Broadcaster
					.broadcastPacket(pc, new S_SkillSound(pc.getId(), sprid));// �̰Ŵ�
			// �ٸ�
			// �����
			// ����...
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".����Ʈ [����] ��� �Է��� �ּ���."));
		}
	}

	private void castleWarStart(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String name = tok.nextToken();
			int minute = Integer.parseInt(tok.nextToken());

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, minute);

			CastleTable.getInstance().updateWarTime(name, cal);
			WarTimeController.getInstance().setWarStartTime(name, cal);

			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			gm.sendPackets(new S_SystemMessage(String.format(
					".�����ð��� %s�� ���� �Ǿ����ϴ�.", formatter.format(cal))));
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(
					".�������� ���̸��α���(��Ʈ,��ũ,����,���,����,���,�Ƶ�,���) ��"));
		}
	}

	private void eventstart(L1PcInstance gm, String param) {
		_GameServerSetting = GameServerSetting.getInstance();
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String msg = tok.nextToken();
			int code = Integer.parseInt(tok.nextToken());
			int map = Integer.parseInt(tok.nextToken());
			GameServerSetting.�̺�Ʈ = true;
			GameServerSetting.�̺�Ʈ��Ʈ = msg;
			GameServerSetting.�̺�Ʈ�������ڵ� = code;
			GameServerSetting.�̺�Ʈ�����۸� = map;
			if (map == 4) {// ����
				gm.sendPackets(new S_SystemMessage("�̺�Ʈ(" + msg + ")�� �ڵ�("
						+ code + ")�� �Ϲ��ʵ��� �������� �����Ͽ����ϴ�."));
			}
			if (map == 7) {// ����1��
				gm.sendPackets(new S_SystemMessage("�̺�Ʈ(" + msg + ")�� �ڵ�("
						+ code + ")�� �Ϲ��ʵ��� �������� �����Ͽ����ϴ�."));
			}
			if (map == 53) {// �Ⱘ1��
				gm.sendPackets(new S_SystemMessage("�̺�Ʈ(" + msg + ")�� �ڵ�("
						+ code + ")�� �Ϲ��ʵ��� �������� �����Ͽ����ϴ�."));
			}
			if (map == 303) {// ����
				gm.sendPackets(new S_SystemMessage("�̺�Ʈ(" + msg + ")�� �ڵ�("
						+ code + ")�� �Ϲ��ʵ��� �������� �����Ͽ����ϴ�."));
			} else {
				gm.sendPackets(new S_SystemMessage("�̺�Ʈ(" + msg + ")�� �ڵ�("
						+ code + ")�� �������� �������� �����Ͽ����ϴ�."));
			}
			L1World.getInstance().broadcastServerMessage("" + msg);
		} catch (Exception e) {
			gm
					.sendPackets(new S_SystemMessage(
							".�̺�Ʈ ��Ʈ �������ڵ� ��Ÿ��[4 ����/7 ����1��/53 �Ⱘ1��/303 ����/0 ������]�� �������� �Է����ּ���"));
		}
	}

	private void eventend(L1PcInstance gm) {
		_GameServerSetting = GameServerSetting.getInstance();
		if (GameServerSetting.�̺�Ʈ) {
			GameServerSetting.�̺�Ʈ = false;
			L1World.getInstance().broadcastServerMessage("�̺�Ʈ�� �����Ͽ����ϴ�.");
		} else {
			gm.sendPackets(new S_SystemMessage("�������� �̺�Ʈ�� �����ϴ�."));
		}
	}

	private void isevent(L1PcInstance gm) {
		_GameServerSetting = GameServerSetting.getInstance();
		try {
			if (GameServerSetting.�̺�Ʈ) {
				if (GameServerSetting.�̺�Ʈ�����۸� == 4) {// ����
					gm.sendPackets(new S_SystemMessage("�̺�Ʈ("
							+ GameServerSetting.�̺�Ʈ��Ʈ + ")�� �ڵ�("
							+ GameServerSetting.�̺�Ʈ�������ڵ� + ")�� �Ϲ��ʵ��� ����"));
				}
				if (GameServerSetting.�̺�Ʈ�����۸� == 7) {// ����1��
					gm.sendPackets(new S_SystemMessage("�̺�Ʈ("
							+ GameServerSetting.�̺�Ʈ��Ʈ + ")�� �ڵ�("
							+ GameServerSetting.�̺�Ʈ�������ڵ� + ")�� �Ϲ��ʵ��� ����"));
				}
				if (GameServerSetting.�̺�Ʈ�����۸� == 53) {// �Ⱘ1��
					gm.sendPackets(new S_SystemMessage("�̺�Ʈ("
							+ GameServerSetting.�̺�Ʈ��Ʈ + ")�� �ڵ�("
							+ GameServerSetting.�̺�Ʈ�������ڵ� + ")�� �Ϲ��ʵ��� ����"));
				}
				if (GameServerSetting.�̺�Ʈ�����۸� == 303) {// ����
					gm.sendPackets(new S_SystemMessage("�̺�Ʈ("
							+ GameServerSetting.�̺�Ʈ��Ʈ + ")�� �ڵ�("
							+ GameServerSetting.�̺�Ʈ�������ڵ� + ")�� �Ϲ��ʵ��� ����"));
				} else {
					gm.sendPackets(new S_SystemMessage("�̺�Ʈ("
							+ GameServerSetting.�̺�Ʈ��Ʈ + ")�� �ڵ�("
							+ GameServerSetting.�̺�Ʈ�������ڵ� + ")�� �������� ����"));
				}
			} else {
				gm.sendPackets(new S_SystemMessage("�������� �̺�Ʈ�� �����ϴ�."));
			}
		} catch (Exception e) {
		}
	}

	/* ��ȣ ���� �ҽ� - ��� */

	private void allrecall(L1PcInstance gm) {
		try {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				if (!pc.isGm() && !pc.isPrivateShop() && !pc.noPlayerCK) {
					recallnow(gm, pc);
				}
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".��ü��ȯ Ŀ�ǵ� ����"));
		}

	}

	private void recallnow(L1PcInstance gm, L1PcInstance target) {
		try {
			L1Teleport.teleportToTargetFront(target, gm, 2, true);
			target.sendPackets(new S_SystemMessage("���� �����Ϳ��� ��ȯ�Ǿ����ϴ�."));
		} catch (Exception e) {
			_log.log(Level.SEVERE, "", e);
		}
	}

	// �Է¹��� ��ȣ�� ���ڵ� �޼ҵ� - Account.java ����.

	private void hold(L1PcInstance gm, String pcName) {
		try {
			L1PcInstance target = L1World.getInstance().getPlayer(pcName);
			if (target != null) {
				holdnow(gm, target);
			} else {
				gm.sendPackets(new S_SystemMessage("�׷� ĳ���ʹ� �����ϴ�."));
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".���� ĳ���͸� ���� �Է����ּ���."));
		}
	}

	private void holdnow(L1PcInstance gm, L1PcInstance target) {
		try {
			L1Teleport.teleport(target, 32736, 32799, (short) 34, 5, true);
			gm.sendPackets(new S_SystemMessage((new StringBuilder()).append(
					target.getName()).append("ĳ���͸� ���ݽ��Ѽ˽��ϴ�.").toString()));
			target.sendPackets(new S_SystemMessage("��ڴ��� ���ݽ��ֽ��ϴ�."));
		} catch (Exception e) {
			_log.log(Level.SEVERE, "", e);
		}
	}
	
	

	private void polyEvent(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String isStart = st.nextToken();

			if (isStart.equals("����")) {
				PolyTable.getInstance().setPolyEvent(true);
				L1World.getInstance().broadcastPacketToAll(
						new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
								"\\f=�����̺�Ʈ�����۵Ǿ����ϴ�."));
			} else if (isStart.equals("����")) {
				PolyTable.getInstance().setPolyEvent(false);
				L1World.getInstance().broadcastPacketToAll(
						new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
								"\\f=�����̺�Ʈ������Ǿ����ϴ�."));
			}
		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage(".�����̺�Ʈ [����/����]�������� �Է����ּ���."));
		}
	}

	private void mem_free(L1PcInstance gm) {
		java.lang.System.gc();
		gm.sendPackets(new S_SystemMessage("gc ����� �޸� ����"));
		long long_total = Runtime.getRuntime().totalMemory();
		int int_total = Math.round(long_total / 1000000);
		long long_free = Runtime.getRuntime().freeMemory();
		int int_free = Math.round(long_free / 1000000);
		long long_max = Runtime.getRuntime().maxMemory();
		int int_max = Math.round(long_max / 1000000);
		gm.sendPackets(new S_SystemMessage("����� �޸� : " + int_total + "MB"));
		gm.sendPackets(new S_SystemMessage("���� �޸� : " + int_free + "MB"));
		gm.sendPackets(new S_SystemMessage("�ִ� ��밡�� �޸� : " + int_max + "MB"));
	}

	private void ShopKick(L1PcInstance gm, String param) {
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(param);
			String s = stringtokenizer.nextToken();
			AutoShopManager shopManager = AutoShopManager.getInstance();
			AutoShop shopPlayer = shopManager.getShopPlayer(s);
			if (shopPlayer != null) {
				shopPlayer.logout();
				shopManager.remove(shopPlayer);
				shopPlayer = null;
			} else {
				gm.sendPackets(new S_SystemMessage("���λ��� ���� �ɸ��� �ƴմϴ�."));
			}
			stringtokenizer = null;
			s = null;
		} catch (Exception exception21) {
			gm.sendPackets(new S_SystemMessage(".����ű [����ĳ���͸�]�� �Է� ���ּ���."));
		}
	}

	/* ���� ��ȣ ���� �޼ҵ� */
	private void to_Change_Passwd(L1PcInstance gm, L1PcInstance pc,
			String passwd) {
		PreparedStatement statement = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			String login = null;
			String password = null;
			con = L1DatabaseFactory.getInstance().getConnection();
			password = passwd;
			statement = con
					.prepareStatement("select account_name from characters where char_name Like '"
							+ pc.getName() + "'");
			rs = statement.executeQuery();

			while (rs.next()) {
				login = rs.getString(1);
				pstm = con
						.prepareStatement("UPDATE accounts SET password=? WHERE login Like '"
								+ login + "'");
				pstm.setString(1, password);
				pstm.execute();
				gm.sendPackets(new S_ChatPacket(pc, "��ȣ�������: [" + login
						+ "] ��ȣ: [" + passwd + "]",
						Opcodes.S_OPCODE_NORMALCHAT, 2));
				gm.sendPackets(new S_SystemMessage(pc.getName()
						+ "�� ��ȣ ������ ���������� �Ϸ�Ǿ����ϴ�."));
				pc.sendPackets(new S_SystemMessage("������ ���� ������ ���ŵǾ����ϴ�."));
			}
		} catch (Exception e) {
			System.out.println("to_Change_Passwd() Error : " + e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(statement);
			SQLUtil.close(con);
		}
	}

	private boolean to_Change_Passwd(L1PcInstance pc, String name, String passwd) {
		PreparedStatement statement = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			String login = null;
			String password = null;
			con = L1DatabaseFactory.getInstance().getConnection();
			password = passwd;
			statement = con
					.prepareStatement("select account_name from characters where char_name Like '"
							+ name + "'");
			rs = statement.executeQuery();

			while (rs.next()) {
				login = rs.getString(1);
				pstm = con
						.prepareStatement("UPDATE accounts SET password=? WHERE login Like '"
								+ login + "'");
				pstm.setString(1, password);
				pstm.execute();
				pc.sendPackets(new S_ChatPacket(pc, "��ȣ�������: [" + login
						+ "] ��ȣ: [" + passwd + "]",
						Opcodes.S_OPCODE_NORMALCHAT, 2));
				pc.sendPackets(new S_SystemMessage(name
						+ "�� ��ȣ ������ ���������� �Ϸ�Ǿ����ϴ�."));
			}
			return true;
		} catch (Exception e) {
			System.out.println("to_Change_Passwd() Error : " + e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(statement);
			SQLUtil.close(con);
		}
		return false;
	}

	/* �Է¹��� ��ȣ�� �ѱ��� ���Ե��� �ʾҴ��� Ȯ���� �ִ� �޼ҵ� */

	/* ��ȣ ���濡 �ʿ��� ������ �Է¹޴´�. */
	private void changePassword(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String user = tok.nextToken();
			String passwd = tok.nextToken();

			if (passwd.length() < 4) {
				gm.sendPackets(new S_SystemMessage("�Է��Ͻ� ��ȣ�� �ڸ����� �ʹ� ª���ϴ�."));
				gm.sendPackets(new S_SystemMessage("�ּ� 4�� �̻� �Է��� �ֽʽÿ�."));
				return;
			}

			if (passwd.length() > 12) {
				gm.sendPackets(new S_SystemMessage("�Է��Ͻ� ��ȣ�� �ڸ����� �ʹ� ��ϴ�."));
				gm.sendPackets(new S_SystemMessage("�ִ� 12�� ���Ϸ� �Է��� �ֽʽÿ�."));
				return;
			}

			if (isDisitAlpha(passwd) == false) {
				gm.sendPackets(new S_SystemMessage("��ȣ�� ������ �ʴ� ���ڰ� ���ԵǾ����ϴ�."));
				return;
			}
			L1PcInstance target = L1World.getInstance().getPlayer(user);
			if (target != null) {
				to_Change_Passwd(gm, target, passwd);
			} else {
				if (!to_Change_Passwd(gm, user, passwd))
					gm.sendPackets(new S_SystemMessage("�׷� �̸��� ���� ĳ���ʹ� �����ϴ�."));
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".��ȣ���� [ĳ����] [��ȣ]�� �Է����ּ���."));
		}
	}

	private void searchDatabase(L1PcInstance gm, String param) {

		try {
			StringTokenizer tok = new StringTokenizer(param);
			int type = Integer.parseInt(tok.nextToken());
			String name = tok.nextToken();

			searchObject(gm, type, "%" + name + "%");

		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".�˻� [0~5] [name]�� �Է� ���ּ���."));
			gm.sendPackets(new S_SystemMessage(
					"0=����, 1=����, 2=����, 3=npc, 4=polymorphs, 5=npc(gfxid)"));
		}
	}

	/** [0062] ���� ���� ��� ���Ĺ��� �� �� �޼ҵ� */

	private void standBy(L1PcInstance gm, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String status = st.nextToken();
			if (status.equalsIgnoreCase("��")) {
				Config.STANDBY_SERVER = true;
				L1World.getInstance().broadcastServerMessage(
						"���´�� ���� �Դϴ�. �Ϻ� ��Ŷ�� ���� �˴ϴ�.");
				L1World.getInstance().broadcastPacketToAll(
						new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
								"\\f4���´�� ���� �Դϴ�. �Ϻ� ��Ŷ�� ���� �˴ϴ�."));
			} else if (status.equalsIgnoreCase("��")) {
				Config.STANDBY_SERVER = false;
				L1World.getInstance().broadcastServerMessage(
						"���´�� ���°� �����ǰ� �������� �÷��̰� �����մϴ�.");
				L1World.getInstance().broadcastPacketToAll(
						new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
								"\\f4���´�� ���°� �����ǰ� �������� �÷��̰� �����մϴ�."));
			}
		} catch (Exception eee) {
			gm.sendPackets(new S_SystemMessage(".���´�� [��/��] ���� �Է��ϼ���."));
			gm.sendPackets(new S_SystemMessage(
					"�� - ���´�� ���·� ��ȯ | �� - �Ϲݸ��� ���ӽ���"));
		}

	}

	private void givesItem2(L1PcInstance gm, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			String pcname = st.nextToken();
			L1PcInstance pc = null;
			pc = (L1PcInstance) L1World.getInstance().getPlayer(pcname);
			if (pc == null) {
				gm
						.sendPackets(new S_SystemMessage(
								"�ش� ���̵� ���� �ɸ��Ͱ� �������� �ʽ��ϴ�."));
				return;
			}
			String nameid = "";
			if (st.hasMoreTokens()) {
				nameid = st.nextToken();
			}
			int count = 1;
			if (st.hasMoreTokens()) {
				count = Integer.parseInt(st.nextToken());
			}
			int enchant = 0;
			if (st.hasMoreTokens()) {
				enchant = Integer.parseInt(st.nextToken());
			}
			int itemid = 0;
			try {
				itemid = Integer.parseInt(nameid);
			} catch (NumberFormatException e) {
				itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(
						nameid);
				if (itemid == 0) {
					gm.sendPackets(new S_SystemMessage("�ش� �������� �߰ߵ��� �ʾҽ��ϴ�."));
					return;
				}
			}
			L1Item temp = ItemTable.getInstance().getTemplate(itemid);
			if (temp != null) {
				if (temp.isStackable()) {
					L1ItemInstance item = ItemTable.getInstance().createItem(
							itemid);
					item.setEnchantLevel(0);
					item.setCount(count);
					if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
						pc.getInventory().storeItem(item);
						pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
						pc.sendPackets(new S_SystemMessage("��ڰ� ������["	+ item.getLogName() + "]�� �־����ϴ�."));
						gm.sendPackets(new S_SystemMessage(""+ item.getLogName() + "(ID:" + itemid + ")�� " + pc.getName() + "���� �־����ϴ�."));
					}
				} else {
					L1ItemInstance item = null;
					int createCount;
					for (createCount = 0; createCount < count; createCount++) {
						item = ItemTable.getInstance().createItem(itemid);
						item.setEnchantLevel(enchant);
						if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
							pc.getInventory().storeItem(item);
						} else {
							break;
						}
					}
					if (createCount > 0) {
						pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
						pc.sendPackets(new S_SystemMessage("��ڰ� ������[" + item.getLogName() + "]�� �־����ϴ�."));
						gm.sendPackets(new S_SystemMessage("" + item.getLogName() + "(ID:" + itemid + ")�� " + pc.getName() + "���� �־����ϴ�."));
					}
				}
			} else {
				gm.sendPackets(new S_SystemMessage("���� ID�� �������� �������� �ʽ��ϴ�."));
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".���� [ĳ���͸�] [itemid �Ǵ� name] [����] [��æƮ��]�� �Է� ���ּ���."));
		}
	}

	private void serversave(L1PcInstance pc) {// ������� �߰�
		Saveserver();// �������̺� �޼ҵ� ����
		pc.sendPackets(new S_SystemMessage("���������� �Ϸ�Ǿ����ϴ�."));// �������� �˷��ְ�
	}

	/** ��������* */
	private void Saveserver() {
		/** ��ü�÷��̾ ȣ��* */
		  Collection<L1PcInstance> list = null;
		  list = L1World.getInstance().getAllPlayers();
		  for(L1PcInstance player : list){
		   if(player == null)
		    continue;
			try {
				/** �Ǿ��������ְ�* */
				player.save();
				/** �κ��� �����ϰ�* */
				player.saveInventory();
			} catch (Exception ex) {
				/** ���� �κ�����* */
				player.saveInventory();
			    System.out.println("���� ��ɾ� ����(�κ��� ������): " + ex);
			}
		}
	}

	
	

	/** ��������* */

	/** ä��Ǯ��* */
	// ////////�����Ѱ��߰�
	private void chatx(L1PcInstance gm, String param) {
		try {
			StringTokenizer tokenizer = new StringTokenizer(param);
			String pcName = tokenizer.nextToken();

			L1PcInstance target = null; // q
			target = L1World.getInstance().getPlayer(pcName);

			if (target != null) {
				target.getSkillEffectTimerSet().killSkillEffectTimer(
						L1SkillId.STATUS_CHAT_PROHIBITED);
				target.sendPackets(new S_SkillIconGFX(36, 0));
				target.sendPackets(new S_ServerMessage(288));
				gm.sendPackets(new S_SystemMessage("�ش�ĳ���� ä���� ���� �߽��ϴ�.."));
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".ä��Ǯ�� ĳ���͸� �̶�� �Է��� �ּ���."));
		}
	}

	/** ä��Ǯ��* */
	private void accountdel(L1PcInstance gm, String param) {

		try {

			StringTokenizer tokenizer = new StringTokenizer(param);
			String pcName = tokenizer.nextToken();

			Connection con = null;
			Connection con2 = null;
			PreparedStatement pstm = null;
			PreparedStatement pstm2 = null;
			ResultSet find = null;
			String findcha = null;

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM characters WHERE char_name=?");
			pstm.setString(1, pcName);
			find = pstm.executeQuery();

			while (find.next()) {
				findcha = find.getString(1);
			}

			if (findcha == null) {
				gm.sendPackets(new S_SystemMessage("DB�� " + pcName
						+ " �ɸ����� ���� ���� �ʽ��ϴ�"));

				con.close();
				pstm.close();
				find.close();

			} else {
				con2 = L1DatabaseFactory.getInstance().getConnection();
				pstm2 = con
						.prepareStatement("UPDATE accounts SET banned = 0 WHERE login= ?");
				pstm2.setString(1, findcha);
				pstm2.execute();

				gm
						.sendPackets(new S_SystemMessage(pcName
								+ " �� �����з��� ���� �Ͽ����ϴ�"));

				con.close();
				pstm.close();
				find.close();
				con2.close();
				pstm2.close();
			}

		} catch (Exception exception) {
			gm.sendPackets(new S_SystemMessage(".�з����� �ɸ������� �Է����ּ���."));
		}
	}

	// .����
	// ���������� �ִ� �ɸ� �˻�
	private void search_Clan(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String name = tok.nextToken();
			search_Clan2(gm, name);
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".���� ���̵�"));
		}
	}

	private void search_Clan2(L1PcInstance gm, String param) {
		try {
			if (param == null) {
				gm.sendPackets(new S_SystemMessage(".���� ĳ����"));
				return;
			}
			String s_account = null;
			String s_name = param;
			String s_level = null;
			String s_clan = null;
			String s_bonus = null;
			String s_online = null;
			String s_hp = null;
			String s_mp = null;
			int count = 0;
			int count0 = 0;
			Connection c = L1DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement0 = null;
			PreparedStatement statement1 = null;
			ResultSet rs0 = null;
			ResultSet rs1 = null;

			statement0 = c
					.prepareStatement("select account_name, Clanname  from characters where char_name = '"
							+ s_name + "'");
			rs0 = statement0.executeQuery();
			while (rs0.next()) {
				s_account = rs0.getString(1);
				s_clan = rs0.getString(2);
				gm.sendPackets(new S_SystemMessage("ĳ����:" + param + "  ����:"
						+ s_account + "  Ŭ����:" + s_clan));
				count0++;
			}
			if (s_clan == "") {
				gm.sendPackets(new S_SystemMessage("���Ϳ� �������� ���� ĳ���Դϴ�."));
				return;
			}

			statement1 = c.prepareStatement("select " + "char_name," + "level,"
					+ "Clanname," + "BonusStatus," + "OnlineStatus," + "MaxHp,"
					+ "MaxMp " + "from characters where Clanname = '" + s_clan
					+ "' ORDER BY 'level' DESC, 'Exp' LIMIT 150");
			rs1 = statement1.executeQuery();
			while (rs1.next()) {
				s_name = rs1.getString(1);
				s_level = rs1.getString(2);
				s_clan = rs1.getString(3);
				s_bonus = rs1.getString(4);
				s_online = rs1.getString(5);
				s_hp = rs1.getString(6);
				s_mp = rs1.getString(7);
				gm.sendPackets(new S_SystemMessage("[" + s_online + "]��["
						+ s_level + "][" + s_name + "]  HP:[" + s_hp + "]MP:["
						+ s_mp + "]D:" + s_bonus));
				count++;
			}
			SQLUtil.close(rs1);
			SQLUtil.close(statement1);
			SQLUtil.close(rs0);
			SQLUtil.close(statement0);
			SQLUtil.close(c);
			gm.sendPackets(new S_SystemMessage("�� [" + count
					+ "]���� �ɸ��� �˻��Ǿ����ϴ�."));
		} catch (Exception e) {
		}
	}

	private void checkEnchant(L1PcInstance gm, String param) {
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(param);
			String para1 = stringtokenizer.nextToken();
			int leaf = Integer.parseInt(para1);
			for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
				List<L1ItemInstance> enchant = player.getInventory().getItems();
				for (int j = 0; j < enchant.size(); ++j) {
					if (enchant.get(j).getEnchantLevel() >= leaf) {
						gm.sendPackets(new S_SystemMessage(player.getName()
								+ "���� " + enchant.get(j).getLogName()
								+ " �������ϰ��ֽ��ϴ�. "));
					}
				}
			}
		} catch (Exception e) {
			gm
					.sendPackets(new S_SystemMessage(
							".��þ�˻� ���� (��ü ����� ������þ�� �̻� �˻�)"));
		}
	}

	private void checkAden(L1PcInstance gm, String param) {
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(param);
			String para1 = stringtokenizer.nextToken();
			int money = Integer.parseInt(para1);
			for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
				L1ItemInstance adena = player.getInventory().findItemId(
						L1ItemId.ADENA);
				if (adena.getCount() >= money)
					gm.sendPackets(new S_SystemMessage(player.getName() + "���� "
							+ adena.getCount() + "�Ƶ�"));
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".�Ƶ��˻� �׼� (��ü ����� �����׼� �̻� �˻�)"));
		}
	}

	private void reloadDB(L1PcInstance gm, String cmd) {
		try {
			DropTable.reload();
			ShopTable.reload();
			NpcShopTable.reloding();
			NpcTable.reload();
			ItemTable.reload();
			IpTable.reload();
			gm.sendPackets(new S_SystemMessage("Table Update Complete..."));
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".�����Ϸε� ��� �Է��� �ּ���."));
		}
	}// �����Ѱ��� �߰�

	private void searchObject(L1PcInstance gm, int type, String name) {
		try {
			String str1 = null;
			String str2 = null;
			int count = 0;
			java.sql.Connection con = null;
			con = L1DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = null;

			switch (type) {
			case 0: // etcitem
				statement = con
						.prepareStatement("select item_id, name from etcitem where name Like '"
								+ name + "'");
				break;
			case 1: // weapon
				statement = con
						.prepareStatement("select item_id, name from weapon where name Like '"
								+ name + "'");
				break;
			case 2: // armor
				statement = con
						.prepareStatement("select item_id, name from armor where name Like '"
								+ name + "'");
				break;
			case 3: // npc
				statement = con
						.prepareStatement("select npcid, name from npc where name Like '"
								+ name + "'");
				break;
			case 4: // polymorphs
				statement = con
						.prepareStatement("select polyid, name from polymorphs where name Like '"
								+ name + "'");
				break;
			case 5: // polymorphs
				statement = con
						.prepareStatement("select gfxid, name from npc where name Like '"
								+ name + "'");
				break; // �߰�
			default:
				break;
			}
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				str1 = rs.getString(1);
				str2 = rs.getString(2);
				gm.sendPackets(new S_SystemMessage("id : [" + str1
						+ "], name : [" + str2 + "]"));
				count++;
			}
			rs.close();
			statement.close();
			con.close();
			gm.sendPackets(new S_SystemMessage("�� [" + count
					+ "]���� �����Ͱ� �˻��Ǿ����ϴ�."));
		} catch (Exception e) {
		}
	}

	// // ���� ��ɾ�
	// ------------------------------------------------------------------------
	private void nocall(L1PcInstance gm, String param) {
		try {
			StringTokenizer tokenizer = new StringTokenizer(param);
			String pcName = tokenizer.nextToken();

			L1PcInstance target = null; // q
			target = L1World.getInstance().getPlayer(pcName);
			if (target != null) { // Ÿ��
				L1Teleport.teleport(target, 33440, 32795, (short) 4, 5, true); // /
				// ���Ե�
				// ����
				// (������������������)
			} else {
				gm.sendPackets(new S_SystemMessage("���������� �ʴ� ���� ID �Դϴ�."));
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".���� (�����ɸ��͸�) ���� �Է��� �ּ���."));
		}
	}

	// ////// ���� ��ɾ� -----------------------------------------------------

	private void chainfo(L1PcInstance gm, String param) {
		try {
			StringTokenizer stringtokenizer = new StringTokenizer(param);
			String s = stringtokenizer.nextToken();
			gm.sendPackets(new S_Chainfo(1, s));
		} catch (Exception exception21) {
			gm.sendPackets(new S_SystemMessage(".�� [ĳ���͸�]�� �Է� ���ּ���."));
		}
	}

	// .���� -----------------------------------------------------------------
	// ���������� �ִ� �ɸ� �˻�
	private void GiveHouse(L1PcInstance pc, String poby) {
		try {
			StringTokenizer st = new StringTokenizer(poby);
			String pobyname = st.nextToken();
			int pobyhouseid = Integer.parseInt(st.nextToken());
			L1PcInstance target = L1World.getInstance().getPlayer(pobyname);
			if (target != null) {
				if (target.getClanid() != 0) {
					L1Clan TargetClan = L1World.getInstance().getClan(
							target.getClanname());
					L1House pobyhouse = HouseTable.getInstance().getHouseTable(
							pobyhouseid);
					TargetClan.setHouseId(pobyhouseid);
					ClanTable.getInstance().updateClan(TargetClan);
					pc.sendPackets(new S_SystemMessage(target.getClanname()
							+ " ���Ϳ��� " + pobyhouse.getHouseName()
							+ "���� �����Ͽ����ϴ�."));
					for (L1PcInstance tc : TargetClan.getOnlineClanMember()) {
						tc.sendPackets(new S_SystemMessage("���Ӹ����ͷκ��� "
								+ pobyhouse.getHouseName() + "���� ���� �޾ҽ��ϴ�."));
					}
				} else {
					pc.sendPackets(new S_SystemMessage(target.getName()
							+ "���� ���Ϳ� ���� ���� �ʽ��ϴ�."));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(73, pobyname));
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".����Ʈ���� <���������Ϳ�> <����Ʈ��ȣ>"));
		}
	}

	private void account_Cha(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String name = tok.nextToken();
			account_Cha2(gm, name);
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".���� ���̵�"));
		}
	}

	private void account_Cha2(L1PcInstance gm, String param) {
		try {
			String s_account = null;
			String s_name = param;
			String s_level = null;
			String s_clan = null;
			String s_bonus = null;
			String s_online = null;
			String s_hp = null;
			String s_mp = null;
			String s_type = null;//�߰�
			int count = 0;
			int count0 = 0;
			java.sql.Connection con0 = null; // �̸����� objid�� �˻��ϱ� ����
			con0 = L1DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement0 = null;
			statement0 = con0
					.prepareStatement("select account_name, Clanname  from characters where char_name = '"
							+ s_name + "'");
			ResultSet rs0 = statement0.executeQuery();
			while (rs0.next()) {
				s_account = rs0.getString(1);
				s_clan = rs0.getString(2);
				gm.sendPackets(new S_SystemMessage("ĳ����:" + s_name + "  ����:"
						+ s_account + "  Ŭ����:" + s_clan));//+"  Ŭ����:" + s_type
				count0++;
			}
			java.sql.Connection con = null;
			con = L1DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = null;
			statement = con.prepareStatement("select " + "char_name,"
					+ "level," + "Clanname," + "BonusStatus," + "OnlineStatus,"
					+ "MaxHp," + "MaxMp, " + "Type "
					+ " from characters where account_name = '" + s_account
					+ "'");
			gm.sendPackets(new S_SystemMessage(
					"***************** ���� ĳ���� *****************"));
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				s_name = rs.getString(1);
				s_level = rs.getString(2);
				s_clan = rs.getString(3);
				s_bonus = rs.getString(4);
				s_online = rs.getString(5);
				s_hp = rs.getString(6);
				s_mp = rs.getString(7);
				s_type = rs.getString(8);
				gm
						.sendPackets(new S_SystemMessage("����:[" + s_online
								+ "] ��:(" + s_level + ") [" + s_name
								+ "]  Ŭ����=[" + s_type + "]"));
				count++;
			}
			rs0.close();
			statement0.close();
			con0.close();
			rs.close();
			statement.close();
			con.close();
			gm.sendPackets(new S_SystemMessage(
					"\\fY0(����)1(���)2(����)3(����)4(�ٿ�)5(����)6(ȯ��)"));
			gm.sendPackets(new S_SystemMessage("*** ����ĳ����:(" + count
					+ ")��  [1:������/0:��������] ***"));
		} catch (Exception e) {
		}
	}

	// .���� -----------------------------------------------------------------
	/*private void autoshop(L1PcInstance gm, String param) {
		if (param.equalsIgnoreCase("��")) {
			AutoShopManager.getInstance().isAutoShop(true);
			gm.sendPackets(new S_SystemMessage("[Command] ���λ��� ��"));
		} else if (param.equalsIgnoreCase("��")) {
			AutoShopManager.getInstance().isAutoShop(false);
			gm.sendPackets(new S_SystemMessage("[Command] ���λ��� ��"));
		} else {
			gm.sendPackets(new S_SystemMessage("[Command] .���λ��� [�� or ��] �Է�"));
		}
	}*/

	

	private void AddAccount(L1PcInstance gm, String account, String passwd,
			String Ip, String Host) {
		java.sql.Connection con = null;
		PreparedStatement statement = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String login = null;
			String password = null;
			con = L1DatabaseFactory.getInstance().getConnection();

			password = passwd;

			statement = con
					.prepareStatement("select * from accounts where login Like '"
							+ account + "'");
			rs = statement.executeQuery();

			if (rs.next())
				login = rs.getString(1);
			if (login != null) {
				gm.sendPackets(new S_SystemMessage("[Command] �̹� ������ �ֽ��ϴ�."));
				return;
			} else {
				String sqlstr = "INSERT INTO accounts SET login=?,password=?,lastactive=?,access_level=?,ip=?,host=?,banned=?,charslot=?,gamepassword=?,notice=?";
				pstm = con.prepareStatement(sqlstr);
				pstm.setString(1, account);
				pstm.setString(2, password);
				pstm.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
				pstm.setInt(4, 0);
				pstm.setString(5, Ip);
				pstm.setString(6, Host);
				pstm.setInt(7, 0);
				pstm.setInt(8, 6);
				pstm.setInt(9, 0);
				pstm.setInt(10, 0);
				pstm.execute();
				gm
						.sendPackets(new S_SystemMessage(
								"[Command] ���� �߰��� �Ϸ�Ǿ����ϴ�."));
			}
		} catch (Exception e) {
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(statement);
			SQLUtil.close(con);
		}

	}

	private static boolean isDisitAlpha(String str) {
		boolean check = true;
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i)) // ���ڰ� �ƴ϶��
					&& Character.isLetterOrDigit(str.charAt(i)) // Ư�����ڶ��
					&& !Character.isUpperCase(str.charAt(i)) // �빮�ڰ� �ƴ϶��
					&& !Character.isLowerCase(str.charAt(i))) { // �ҹ��ڰ� �ƴ϶��
				check = false;
				break;
			}
		}
		return check;
	}

	private void addaccount(L1PcInstance gm, String param) {
		try {
			StringTokenizer tok = new StringTokenizer(param);
			String user = tok.nextToken();
			String passwd = tok.nextToken();

			if (user.length() < 4) {
				gm.sendPackets(new S_SystemMessage(
						"[Command] �Է��Ͻ� �������� �ڸ����� �ʹ� ª���ϴ�."));
				gm.sendPackets(new S_SystemMessage(
						"[Command] �ּ� 4�� �̻� �Է��� �ֽʽÿ�."));
				return;
			}
			if (passwd.length() < 4) {
				gm.sendPackets(new S_SystemMessage(
						"[Command] �Է��Ͻ� ��ȣ�� �ڸ����� �ʹ� ª���ϴ�."));
				gm.sendPackets(new S_SystemMessage(
						"[Command] �ּ� 4�� �̻� �Է��� �ֽʽÿ�."));
				return;
			}

			if (passwd.length() > 12) {
				gm.sendPackets(new S_SystemMessage(
						"[Command] �Է��Ͻ� ��ȣ�� �ڸ����� �ʹ� ��ϴ�."));
				gm.sendPackets(new S_SystemMessage(
						"[Command] �ִ� 12�� ���Ϸ� �Է��� �ֽʽÿ�."));
				return;
			}

			if (isDisitAlpha(passwd) == false) {
				gm.sendPackets(new S_SystemMessage(
						"[Command] ��ȣ�� ������ �ʴ� ���ڰ� ���� �Ǿ� �ֽ��ϴ�."));
				return;
			}
			AddAccount(gm, user, passwd, "127.0.0.1", "127.0.0.1");
			tok = null;
			user = null;
			passwd = null;
		} catch (Exception e) {
			gm
					.sendPackets(new S_SystemMessage(
							"[Command] .�����߰� [������] [��ȣ] �Է�"));
		}
	}

	//�н����� �´��� ���� ����  
	public static boolean isPasswordTrue(String Password, String oldPassword) {
		String _rtnPwd = null;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT password(?) as pwd ");

			pstm.setString(1, oldPassword);
			rs = pstm.executeQuery();
			if (rs.next()) {
				_rtnPwd = rs.getString("pwd");
			}
			if (_rtnPwd.equals(Password)) { // �����ϴٸ�
				return true;
			} else
				return false;
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return false;
	}

	private void CodeTest(L1PcInstance pc, String param) {
		try {
			StringTokenizer st = new StringTokenizer(param);
			int codetest = Integer.parseInt(st.nextToken(), 10);
			// pc.sendPackets(new S_ServerMessage(161,"$580","$245", "$247"));
			// int time = 1020;
			// �� �׽�Ʈ�� �ڵ尡 ������ ���� ��Ŷ �κ�
			pc.sendPackets(new S_Test(pc, codetest));

		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage("[Command] .�ڵ� [����] �Է�"));
		}
	}

	private void Clear(L1PcInstance gm) {
		for (L1Object obj : L1World.getInstance().getVisibleObjects(gm, 15)) { // 10
			// ����
			// ����
			// ������Ʈ��
			// ã�Ƽ�
			if (obj instanceof L1MonsterInstance) { // ���Ͷ��
				L1NpcInstance npc = (L1NpcInstance) obj;
				npc.receiveDamage(gm, 50000); // ������
				if (npc.getCurrentHp() <= 0) {

				} else {

				}
			} else if (obj instanceof L1PcInstance) { // pc���
				L1PcInstance player = (L1PcInstance) obj;
				player.receiveDamage(player, 0, false); // ������
				if (player.getCurrentHp() <= 0) {
				} else {
				}
			}
		}
	}

	private boolean sellingPWChange(L1PcInstance pc, String name, String passwd) {
		PreparedStatement pstm = null;
		java.sql.Connection con = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("select * from characters where char_name Like '"
							+ name + "'");
			rs = pstm.executeQuery();
			if (rs.next()) {
				pstm = con
						.prepareStatement("UPDATE characters SET sealingPW=? WHERE char_name Like '"
								+ name + "'");
				pstm.setString(1, passwd);
				pstm.executeUpdate();
				pc.sendPackets(new S_SystemMessage("���ξ�ȣ����: " + name
						+ "  ��ȣ: [" + passwd + "]"));

				pc.sendPackets(new S_SystemMessage("���ξ�ȣ ������ ���������� �Ϸ�Ǿ����ϴ�."));
				return true;
			} else {
				pc.sendPackets(new S_SystemMessage("���ξ�ȣ �ɸ��� ����: " + name
						+ "  ���� ����"));
				return false;
			}
		} catch (Exception e) {
			System.out.println("sellingPWChange() Error : " + e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return false;
	}

}
