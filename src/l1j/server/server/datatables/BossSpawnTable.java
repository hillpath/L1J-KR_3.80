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
package l1j.server.server.datatables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.Boss.L1BossCycle;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1MobGroupInfo;
import l1j.server.server.model.L1MobGroupSpawn;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.SQLUtil;

public class BossSpawnTable {
	private static Logger _log = Logger.getLogger(BossSpawnTable.class.getName());

	private static BossSpawnTable _instance;
	
	public static BossSpawnTable getInstance() {
		if (_instance == null) {
			_instance = new BossSpawnTable();
		}
		return _instance;
	}
	

	
	
	private BossSpawnTable() {
		for(L1BossCycle b : L1BossCycle.getBossCycleList()) {
			spawnBoss(b.getName());
		}
	}
	
	public static void spawnBoss(String name) {
		Random rnd = new Random(System.nanoTime());
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_boss");
			rs = pstm.executeQuery();
			L1Npc template = null;
			L1NpcInstance npc = null;
			int alreadySpawnCount, maxSpawnCount;
			while (rs.next()) { 
				if(rs.getString("cycle_type").equals(name)) {
					int percent = rs.getInt("percentage");
					if(rnd.nextInt(100) > percent){						
						continue; // 스폰 확율 적용
					}
					
					int npcId = rs.getInt("npc_id");
					
					maxSpawnCount = rs.getInt("count");
					if(maxSpawnCount <= 0) continue; 
					
					alreadySpawnCount = 0;
					
					for (L1Object object : L1World.getInstance().getObject()) {
						if(object instanceof L1NpcInstance){
							npc = (L1NpcInstance)object;
							/*if (npcId == 45488 && npc.getNpcTemplate().get_npcId() == 45488 // 카스파
									&& (npc.getMapId() == 9 || npc.getMapId()==10)
									&& npc.getMapId() != 666){								
								alreadySpawnCount++;					
							}else if (npcId == 45601 && npc.getNpcTemplate().get_npcId() == 45601 // 데스나이트
									&& (npc.getMapId() == 11 || npc.getMapId()==12 || npc.getMapId() == 13)
									&& npc.getMapId() != 666){							
								alreadySpawnCount++;
							}else if (npcId == 4037000 && npc.getNpcTemplate().get_npcId() == 4037000 //산적두목클라인
									&& (npc.getMapId() == 54 || npc.getMapId() == 56)){
								alreadySpawnCount++;
							}else*/ if (npc.getNpcTemplate().get_npcId() == npcId
									&& npc.getMapId() != 666 && npc.getMapId() == rs.getInt("mapid")) {								
								alreadySpawnCount++;
							}
						}
							

					}
					
					if(maxSpawnCount <= alreadySpawnCount) continue;
					
					template = NpcTable.getInstance().getTemplate(npcId);
					if (template == null) {
						_log.warning("mob data for id:" + npcId + " missing in npc table");
					}
					npc = NpcTable.getInstance().newNpcInstance(npcId);
					npc.setId(ObjectIdFactory.getInstance().nextId());
					
					// 랜덤 시작 위치
					int locX = 0, locY = 0;
					switch(rnd.nextInt(2)+1) {
					case 1:
						locX = rs.getInt("locx1");
						locY = rs.getInt("locy1");
						break;
					case 2:
						locX = rs.getInt("locx2");
						locY = rs.getInt("locy2");
						break;
					default: 
						break;
					}
					
					if(locX == 0 || locY == 0) {
						locX = rs.getInt("locx");
						locY = rs.getInt("locy");
					}
					
					if(locX == 0 || locY == 0) {
						return;
					}
					
					int rndX = rs.getInt("randomx");
					int rndY = rs.getInt("randomy");
					
					L1Location loc = new L1Location(); 
					loc.setX(locX);
					loc.setY(locY);
					loc.setMap((short)rs.getInt("mapid"));
					
					// 랜덤 위치 선정
					if(rndX != 0 && rndY != 0) {
						L1Location newLocation = L1Location.randomLocation(loc, 0, rnd.nextInt(2) > 0 ? rndX : rndY, true);
						loc.setX(newLocation.getX());
						loc.setY(newLocation.getY());
					}
					
					npc.setLocation(loc);
					npc.getLocation().forward(rs.getInt("heading"));
					npc.setHomeX(npc.getX());
					npc.setHomeY(npc.getY());
					npc.setMovementDistance(rs.getInt("movement_distance"));
					npc.setRest(rs.getBoolean("rest"));
					
					int groupId = rs.getInt("group_id");
					if( groupId > 0)
						L1MobGroupSpawn.getInstance().doSpawn(npc, groupId, rs.getBoolean("respawn_screen"), false);
					
					if (npcId == 45573 && npc.getMapId() == 2) { // 바포메트
						for (L1PcInstance _pc : L1World.getInstance().getAllPlayers()) {
							if (_pc.getMapId() == 2) {
								L1Teleport.teleport(_pc, 32664, 32797, (short) 2, 0, true);
							}
						}
					}
					if (npcId == 46142 && npc.getMapId() == 73 // 아이스 데몬
							|| npcId == 46141 && npc.getMapId() == 74) {
						for (L1PcInstance pc1 : L1World.getInstance().getAllPlayers()) {
							if (pc1.getMapId() >= 72 && pc1.getMapId() <= 74) {
								L1Teleport.teleport(pc1, 32840, 32833, (short) 72, pc1.getMoveState().getHeading(), true);
							}
						}
					}
					L1World.getInstance().storeObject(npc);
					L1World.getInstance().addVisibleObject(npc);
					
					npc.getLight().turnOnOffLight();
					npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);					
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (SecurityException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} 
		finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		
	}
	
	public static void killBoss(String name) {
		if(name == null){ return; }
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1NpcInstance npc = null;
		L1NpcInstance npc1 = null;
		L1MobGroupInfo info = null;
		int npcId = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_boss");
			rs = pstm.executeQuery();
			while (rs.next()) {
				try{
				if(rs.getString("cycle_type").equalsIgnoreCase(name)) {
					npcId = rs.getInt("npc_id");
					for (L1Object object : L1World.getInstance().getObject()) {
						if(object instanceof L1NpcInstance){
							npc = (L1NpcInstance)object;
							if (npc.getNpcTemplate().get_npcId() == npcId && npc.getMapId() != 666) {
								if (npc.getCurrentHp() != npc.getMaxHp()) return;
								if (rs.getInt("group_id") != 0){
									info = npc.getMobGroupInfo();
									for (int i = 0, a = info.getNumOfMembers(); i<a; i++){
										npc1 = (L1NpcInstance) L1World.getInstance().findObject(info.getIndexMember(i).getId());
										npc1.groupDeleteMe();									
									}
								}else{
									npc.deleteMe();								
								}
								npc = null;
							}
						}
						
					}
				}
				}catch(Exception e){}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (SecurityException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
