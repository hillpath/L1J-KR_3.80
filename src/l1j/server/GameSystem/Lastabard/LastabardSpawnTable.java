package l1j.server.GameSystem.Lastabard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javolution.util.FastMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.NumberUtil;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

public class LastabardSpawnTable {
	private static Logger log = Logger.getLogger(LastabardSpawnTable.class
			.getName());

	private static LastabardSpawnTable UniqueInstance;

	private Map<Integer, LastabardSpawn> spawnTable = new FastMap<Integer, LastabardSpawn>();

	private LastabardSpawnTable() {
	}

	public static LastabardSpawnTable getInstance() {
		if (UniqueInstance == null) {
			UniqueInstance = new LastabardSpawnTable();
		}
		return UniqueInstance;
	}

	public void Init() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("[Lastabard] spawning Mobs...");
		fillSpawnTable();
		log.config("[Lastabard] SpawnList " + spawnTable.size() + "Articles ohm load");
		System.out.println("OK! " + timer.get() + " ms");
	}

	private void fillSpawnTable() {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int spawnCount = 0;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_lastabard");
			rs = pstm.executeQuery();

			LastabardSpawn spawnDat;
			L1Npc npcTemplate;
			double amount_rate;
			int npcTemplateId, count;

			while (rs.next()) {
				if (rs.getInt("count") == 0)
					continue;

				npcTemplateId = rs.getInt("npc_templateid");
				npcTemplate = NpcTable.getInstance().getTemplate(npcTemplateId);

				if (npcTemplate == null) {
					log.warning("[Lastabard] missing mob data for id:"
							+ npcTemplateId + " in npc table");
					continue;
				}

				amount_rate = MapsTable.getInstance().getMonsterAmount(
						rs.getShort("mapid"));
				count = calcCount(npcTemplate, rs.getInt("count"), amount_rate);

				if (count == 0)
					continue;

				spawnDat = new LastabardSpawn(npcTemplate);
				spawnDat.setId(rs.getInt("id"));
				spawnDat.setAmount(count);
				spawnDat.setGroupId(rs.getInt("group_id"));
				spawnDat.setLocX(rs.getInt("locx"));
				spawnDat.setLocY(rs.getInt("locy"));
				spawnDat.setRandomx(rs.getInt("randomx"));
				spawnDat.setRandomy(rs.getInt("randomy"));
				spawnDat.setLocX1(rs.getInt("locx1"));
				spawnDat.setLocY1(rs.getInt("locy1"));
				spawnDat.setLocX2(rs.getInt("locx2"));
				spawnDat.setLocY2(rs.getInt("locy2"));
				spawnDat.setHeading(rs.getInt("heading"));
				spawnDat.setMinRespawnDelay(rs.getInt("min_respawn_delay"));
				spawnDat.setMaxRespawnDelay(rs.getInt("max_respawn_delay"));
				spawnDat.setMapId(rs.getShort("mapid"));
				spawnDat.setRespawnScreen(rs.getBoolean("respawn_screen"));
				spawnDat.setMovementDistance(rs.getInt("movement_distance"));
				spawnDat.setRest(rs.getBoolean("rest"));
				// spawnDat.setSpawnType(rs.getInt("near_spawn"));
				spawnDat.setName(npcTemplate.get_name());
				spawnDat.setDoorId(rs.getInt("spawnlist_door"));
				spawnDat.setCountMapId(rs.getInt("count_map"));

				if (count > 1 && spawnDat.getLocX1() == 0) {
					// �ټ��� ���� �������� ��ü�� * 6 �� �������� (���� 30 ����)
					int range = Math.min(count * 6, 30);
					spawnDat.setLocX1(spawnDat.getLocX() - range);
					spawnDat.setLocY1(spawnDat.getLocY() - range);
					spawnDat.setLocX2(spawnDat.getLocX() + range);
					spawnDat.setLocY2(spawnDat.getLocY() + range);
				}

				// start the spawning
				spawnDat.init(true);
				spawnCount += spawnDat.getAmount();

				spawnTable.put(new Integer(spawnDat.getId()), spawnDat);
			}
		} catch (SQLException e) {
			log.log(Level.SEVERE, "LastabardSpawnTable error occurs", e);
		} catch (SecurityException e) {
			log.log(Level.SEVERE, "LastabardSpawnTable error1", e);
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "LastabardSpawnTable error2", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		log.fine("[Lastabard] Gun " + spawnCount + "Marie");
	}

	private static int calcCount(L1Npc npc, int count, double rate) {
		if (rate == 0)
			return 0;
		if (rate == 1 || npc.isAmountFixed())
			return count;
		else
			return NumberUtil.randomRound((count * rate));
	}

	public void spawnMobs(int mapId) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM spawnlist_lastabard WHERE mapid = "
							+ mapId);
			rs = pstm.executeQuery();

			LastabardSpawn spawnDat;
			L1Npc npcTemplate;
			double amount_rate;
			int npcTemplateId, count;

			while (rs.next()) {
				if (rs.getInt("count") == 0)
					continue;

				npcTemplateId = rs.getInt("npc_templateid");
				npcTemplate = NpcTable.getInstance().getTemplate(npcTemplateId);

				if (npcTemplate == null) {
					log.warning("[Lastabard] missing mob data for id:"
							+ npcTemplateId + " in npc table");
					continue;
				}

				amount_rate = MapsTable.getInstance().getMonsterAmount(
						rs.getShort("mapid"));
				count = calcCount(npcTemplate, rs.getInt("count"), amount_rate);

				if (count == 0)
					continue;

				spawnDat = new LastabardSpawn(npcTemplate);
				spawnDat.setId(rs.getInt("id"));
				spawnDat.setAmount(count);
				spawnDat.setGroupId(rs.getInt("group_id"));
				spawnDat.setLocX(rs.getInt("locx"));
				spawnDat.setLocY(rs.getInt("locy"));
				spawnDat.setRandomx(rs.getInt("randomx"));
				spawnDat.setRandomy(rs.getInt("randomy"));
				spawnDat.setLocX1(rs.getInt("locx1"));
				spawnDat.setLocY1(rs.getInt("locy1"));
				spawnDat.setLocX2(rs.getInt("locx2"));
				spawnDat.setLocY2(rs.getInt("locy2"));
				spawnDat.setHeading(rs.getInt("heading"));
				spawnDat.setMinRespawnDelay(rs.getInt("min_respawn_delay"));
				spawnDat.setMaxRespawnDelay(rs.getInt("max_respawn_delay"));
				spawnDat.setMapId(rs.getShort("mapid"));
				spawnDat.setRespawnScreen(rs.getBoolean("respawn_screen"));
				spawnDat.setMovementDistance(rs.getInt("movement_distance"));
				spawnDat.setRest(rs.getBoolean("rest"));
				spawnDat.setDoorId(rs.getInt("spawnlist_door"));
				spawnDat.setCountMapId(rs.getInt("count_map"));
				// spawnDat.setSpawnType(rs.getInt("near_spawn"));
				spawnDat.setName(npcTemplate.get_name());

				if (count > 1 && spawnDat.getLocX1() == 0) {
					// �ټ��� ���� �������� ��ü�� * 6 �� �������� (���� 30 ����)
					int range = Math.min(count * 6, 30);
					spawnDat.setLocX1(spawnDat.getLocX() - range);
					spawnDat.setLocY1(spawnDat.getLocY() - range);
					spawnDat.setLocX2(spawnDat.getLocX() + range);
					spawnDat.setLocY2(spawnDat.getLocY() + range);
				}

				spawnDat.init(false); // start the spawning
			}

		} catch (SQLException e) {
			log.log(Level.SEVERE, "LastabardSpawnTable error3", e);
		} catch (SecurityException e) {
			log.log(Level.SEVERE, "LastabardSpawnTable error4", e);
		} catch (ClassNotFoundException e) {
			log.log(Level.SEVERE, "LastabardSpawnTable error5", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
