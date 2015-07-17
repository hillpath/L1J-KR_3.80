package l1j.server.server.command.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.SQLUtil;

public class L1SearchNpc implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1SearchNpc.class.getName());

	private L1SearchNpc() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1SearchNpc();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int type = Integer.parseInt(st.nextToken());
			int size = 10;
			try {
				size = Integer.parseInt(st.nextToken());
			} catch (Exception e) {
				size = 10;
			}
			int searchCount = 0;

			switch (type) {
			case 0: // 화면내검색
				pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
				for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, size)) { // 10 범위 내에 오브젝트를 찾아서
					if (obj instanceof L1NpcInstance) { // NPC라면
						if (obj instanceof L1DollInstance){
							L1DollInstance doll = (L1DollInstance) obj;
							L1PcInstance master = (L1PcInstance)doll.getMaster();
							if (master != null)
								pc.sendPackets(new S_SystemMessage("* " + doll.getNpcId() + ", " + doll.getName() + ", " + master.getName()));
							else 
								pc.sendPackets(new S_SystemMessage("* " + doll.getNpcId() + ", " + doll.getName() + ", null"));
							searchCount++;
						} else if (obj instanceof L1MonsterInstance){
							L1MonsterInstance mon = (L1MonsterInstance) obj;
							pc.sendPackets(new S_SystemMessage("* " + mon.getNpcId() + ", " + mon.getName() + ", " + mon.isDead() + ", " + mon.getCurrentHp()));
							searchCount++;
						} else {
							L1NpcInstance npc = (L1NpcInstance) obj;
							pc.sendPackets(new S_SystemMessage("* " + npc.getNpcId() + ", " + npc.getName()));
							searchCount++;
						}
					} else if (obj instanceof L1PcInstance) {
						L1PcInstance player = (L1PcInstance) obj;							
						pc.sendPackets(new S_SystemMessage("\\fY* " + player.getId() + ", " + player.getName()));
					}
				}
				break;
			case 1:
				int x = pc.getX();
				int y = pc.getY();

				pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
				for (int i = x - size; i < x + size; i++) {
					for (int j = y - size; j < y + size; j++) {
						Connection con = null;
						PreparedStatement pstm = null;
						ResultSet rs = null;

						try {
							con = L1DatabaseFactory.getInstance().getConnection();
							pstm = con.prepareStatement("select npc_templateid, location, count from spawnlist_npc where locx=? and locy=? and mapid=? and count='0' and npc_templateid not in ('7000022', '500038', '500039', '500040', '45000107', '45000108', '45000109', '45000110')");
							pstm.setInt(1, i);
							pstm.setInt(2, j);
							pstm.setInt(3, pc.getMapId());
							rs = pstm.executeQuery();
														
							while (rs.next()) {
								pc.sendPackets(new S_SystemMessage("* " + rs.getInt("count") + ", " + rs.getInt("npc_templateid") + ", " + rs.getString("location")));
								searchCount++;
							}
							
						} catch (SQLException e) {
							_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
						} finally {
							SQLUtil.close(rs);
							SQLUtil.close(pstm);
							SQLUtil.close(con);
						}
					}
				}

				break;
			case 2:
				for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, size)) { // 10 범위 내에 오브젝트를 찾아서
					if (obj instanceof L1NpcInstance) { // NPC라면
						if (obj instanceof L1DollInstance){
							L1DollInstance doll = (L1DollInstance) obj;
							L1PcInstance master = (L1PcInstance)doll.getMaster();
							//pc.sendPackets(new S_SystemMessage("* " + doll.getNpcId() + ", " + doll.getName() + ", " + pc.getName()));
							if (master == null) {
								doll.deleteDoll();
							}
							searchCount++;
						} else if (obj instanceof L1MonsterInstance){
							L1MonsterInstance mon = (L1MonsterInstance) obj;
							//pc.sendPackets(new S_SystemMessage("* " + mon.getNpcId() + ", " + mon.getName() + ", " + mon.isDead() + ", " + mon.getCurrentHp()));
							L1World.getInstance().removeObject(mon);
							L1World.getInstance().removeVisibleObject(mon);
							searchCount++;
						} 
					}
				}
				break;
			}

			pc.sendPackets(new S_SystemMessage("총 " + searchCount + "건의 NPC가 검색 되었습니다."));
			pc.sendPackets(new S_SystemMessage("----------------------------------------------------"));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".찾기 [0:월드,1:디비] [반경]"));
		}
	}
}
