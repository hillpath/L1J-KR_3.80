package l1j.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l1j.server.Warehouse.PackageWarehouse;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;
import static l1j.server.server.model.skill.L1SkillId.*;

enum SpecialEvent {
	BugRace, AllBuf, InfinityFight, DoNotChatEveryone, DoChatEveryone
};

//���� ��, ��ü �̺�Ʈ�� ���� ó���� ���
public class SpecialEventHandler {

	private static volatile SpecialEventHandler uniqueInstance = null;

	private boolean CheckBugrace = false;

	private SpecialEventHandler() {
	}

	public static SpecialEventHandler getInstance() {
		if (uniqueInstance == null) {
			synchronized (SpecialEventHandler.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new SpecialEventHandler();
				}
			}
		}

		return uniqueInstance;
	}

	public void giveFeather() {
		Connection c = null;
		PreparedStatement p = null;
		PreparedStatement p1 = null;
		ResultSet r = null;
		String accountName;
		int count;
		try {
			c = L1DatabaseFactory.getInstance().getConnection();
			p = c.prepareStatement("SELECT * FROM hongbo");
			r = p.executeQuery();
			while (r.next()) {
				accountName = r.getString("account");
				count = r.getInt("remaincount");
				if (count <= 0)
					continue;
				count *= 500;
				PackageWarehouse.insertItem(accountName, count);
				p1 = c
						.prepareStatement("UPDATE hongbo SET excutecount = excutecount + '"
								+ r.getInt("remaincount")
								+ "',remaincount = 0 WHERE account = '"
								+ accountName + "'");
				p1.execute();
			}
		} catch (Exception e) {
		} finally {
			SQLUtil.close(r);
			SQLUtil.close(p1);
			SQLUtil.close(p);
			SQLUtil.close(c);
		}
	}

	public void doBugRace() {
		if (!CheckBugrace)
			CheckBugrace = true;
		else
			return;
		// BugRaceController.getInstance().BugRaceRestart = true;
	}
	public void clanBuff() {
		int[] allBuffSkill = {DECREASE_WEIGHT, PHYSICAL_ENCHANT_DEX,
				PHYSICAL_ENCHANT_STR, BLESS_WEAPON, ADVANCE_SPIRIT, IRON_SKIN,
				ADVANCE_SPIRIT, NATURES_TOUCH, GLOWING_AURA, SHINING_AURA}; //����
		L1SkillUse l1skilluse = null;
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null) {
		if (clan.getCastleId() == 0 && pc.getClanid() != 0 && clan.getOnlineClanMember().length >= 10) {
		if(pc.isPrivateShop()){
		continue;
		}
		l1skilluse = new L1SkillUse();
		for (int i = 0; i < allBuffSkill.length ; i++) {
		l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
		}
		pc.sendPackets(new S_SystemMessage("Hyeolmaengwon 10 or more connections yiyeoseo He received a buff."));
		}
		}
		}
		}
	public void doAllBuf() {
		int[] allBuffSkill = { DECREASE_WEIGHT, PHYSICAL_ENCHANT_DEX,
				PHYSICAL_ENCHANT_STR, BLESS_WEAPON, ADVANCE_SPIRIT, IRON_SKIN,
				ADVANCE_SPIRIT, NATURES_TOUCH };
		L1SkillUse l1skilluse = null;
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
	
			if(pc == null) {
				continue;
			}
			if(pc.isDead()){
				continue;
			}
			if(pc.isPrivateShop()){
				continue;
			}
			l1skilluse = new L1SkillUse();
			for (int i = 0; i < allBuffSkill.length; i++) {
				l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
			}
			pc.sendPackets(new S_SkillSound(pc.getId(), 4856));
//			pc.sendPackets(new S_SystemMessage("�����Ե� ����ؿ�~v"));
		}
	}

	public void doNotChatEveryone() {
		L1World.getInstance().set_worldChatElabled(false);
		L1World.getInstance().broadcastPacketToAll(
				new S_SystemMessage("The World Chat has been disabled by the GM."));
	}

	public void doChatEveryone() {
		L1World.getInstance().set_worldChatElabled(true);
		L1World.getInstance().broadcastPacketToAll(
				new S_SystemMessage("The World Chat has been enabled by the GM."));
	}

	public void ReturnStats(L1PcInstance pc) {
		
		pc.getAbility().initStat(pc.getClassId());
		L1SkillUse l1skilluse = new L1SkillUse();
		l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc
				.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_LOGIN);

		if (pc.getWeapon() != null) {
			pc.getInventory().setEquipped(pc.getWeapon(), false, false, false);
		}

		pc.sendPackets(new S_CharVisualUpdate(pc));
		pc.sendPackets(new S_OwnCharStatus2(pc));

		for (L1ItemInstance armor : pc.getInventory().getItems()) {
			for (int type = 0; type <= 12; type++) {
				if (armor != null) {
					pc.getInventory().setEquipped(armor, false, false, false);
				}
			}
		}
		pc.setReturnStat(pc.getExp());
		pc.sendPackets(new S_SPMR(pc));
		pc.sendPackets(new S_OwnCharAttrDef(pc));
		pc.sendPackets(new S_OwnCharStatus2(pc));
		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.START));
pc.StatReturnCK = true;
		try {
			pc.save();
		} catch (Exception e) {
		}
	}

}
