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
package l1j.server.server.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.GameSystem.IceInstance.IceDemonController;
import l1j.server.GameSystem.IceInstance.IceQueenController;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCPack;
import server.controller.Npc.NpcDeleteController;

public class L1SpawnUtil {
	private static Logger _log = Logger.getLogger(L1SpawnUtil.class.getName());

	public static void spawn(L1PcInstance pc, int npcId, int randomRange,
			int timeMillisToDelete, boolean isUsePainwand) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(pc.getMapId());
			if (randomRange == 0) {
				npc.getLocation().set(pc.getLocation());
				npc.getLocation().forward(pc.getMoveState().getHeading());
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(pc.getX() + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					npc.setY(pc.getY() + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation())
							&& npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);

				if (tryCount >= 50) {
					npc.getLocation().set(pc.getLocation());
					npc.getLocation().forward(pc.getMoveState().getHeading());
				}
			}

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(pc.getMoveState().getHeading());

			if(isUsePainwand) {
				if(npc instanceof L1MonsterInstance) {
					L1MonsterInstance mon = (L1MonsterInstance)npc;
					mon.set_storeDroped(2); //소막 드랍
					if (npc.isNormalBoss() 
							|| npc.isOmanBoss() 
							|| npc.isLastabardBoss()) { 
						npc.setMovementDistance(20);
					}
				}
			}

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);
			
			if (npcId == 181164) { //기르타스 큰화염
			    npc.broadcastPacket(new S_NPCPack(npc));
			    npc.broadcastPacket(new S_DoActionGFX(npc.getId(), 4));
			    npc.setActionStatus(4); 
			    npc.broadcastPacket(new S_NPCPack(npc));
			    npc.broadcastPacket(new S_DoActionGFX(npc.getId(), 0));
			    npc.setActionStatus(0);
			    npc.broadcastPacket(new S_NPCPack(npc));
			    npc.broadcastPacket(new S_DoActionGFX(npc.getId(), 8));
			    npc.setActionStatus(8);
			   }

			

			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
			if (0 < timeMillisToDelete) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc,
						timeMillisToDelete);
				timer.begin();
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1SpawnUtil[]Error", e);
		}
	}

	public static void spawn3(L1NpcInstance pc, int npcId, int randomRange,
			int timeMillisToDelete, boolean isUsePainwand) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(pc.getMapId());

			if (randomRange == 0) {
				npc.getLocation().set(pc.getLocation());
				npc.getLocation().forward(pc.getMoveState().getHeading());
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(pc.getX() + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					npc.setY(pc.getY() + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation())
							&& npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);

				if (tryCount >= 50) {
					npc.getLocation().set(pc.getLocation());
					npc.getLocation().forward(pc.getMoveState().getHeading());
				}
			}
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(pc.getMoveState().getHeading());
			if (isUsePainwand) {
				if (npc instanceof L1MonsterInstance) {
					L1MonsterInstance mon = (L1MonsterInstance) npc;
					mon.set_storeDroped(2);
				}
			}
			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);
			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
			if (0 < timeMillisToDelete) {
				npc.NpcDeleteTime = System.currentTimeMillis()
						+ timeMillisToDelete;
				NpcDeleteController.getInstance().addNpcDelete(npc);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1SpawnUtil[]Error2", e);
		}
	}

	/**
	 * 엔피씨를 스폰한다
	 * 
	 * @param x
	 * @param y
	 * @param map
	 * @param npcId
	 * @param randomRange
	 * @param timeMillisToDelete
	 * @param movemap
	 *            (이동시킬 맵을 설정한다 - 안타레이드)
	 */
	public static void spawn1(int x, int y, short MapId, int Heading,
			int npcId, int randomRange, boolean isUsePainwand) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(MapId);
			if (randomRange == 0) {
				// npc.getLocation().set(pc.getLocation());
				npc.getLocation().forward(Heading);
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(x + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					npc.setY(y + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation())
							&& npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);

				if (tryCount >= 50) {
					// npc.getLocation().set(pc.getLocation());
					npc.getLocation().forward(Heading);
				}
			}

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(Heading);
			if (isUsePainwand) {
				if (npc instanceof L1MonsterInstance) {
					L1MonsterInstance mon = (L1MonsterInstance) npc;
					mon.set_storeDroped(2);
				}
			}

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
			/*
			 * if (0 < timeMillisToDelete) { L1NpcDeleteTimer timer = new
			 * L1NpcDeleteTimer(npc, timeMillisToDelete); timer.begin(); }
			 */
		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1SpawnUtil[]Error3", e);
		}
	}

	/**
	 * 엔피씨를 스폰한다
	 * 
	 * @param x
	 * @param y
	 * @param map
	 * @param npcId
	 * @param randomRange
	 * @param timeMillisToDelete
	 * @param movemap
	 *            (이동시킬 맵을 설정한다 - 안타레이드)
	 */
	public static void spawn2(int x, int y, short map, int npcId,
			int randomRange, int timeMillisToDelete, int movemap) {
		try {
			if (npcId <= 0) {
				return;
			}
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(map);
			if (randomRange == 0) {
				npc.getLocation().set(x, y, map);
				npc.getLocation().forward(5);
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(x + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					npc.setY(y + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation())
							&& npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);

				if (tryCount >= 50) {
					npc.getLocation().set(x, y, map);
					npc.getLocation().forward(5);
				}
			}

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(5);
			if (npcId == 4212015 || npcId == 4212016|| npcId == 4212017) {//린드레이드
				L1FieldObjectInstance fobj = (L1FieldObjectInstance) npc;
				fobj.setMoveMapId(movemap);
			}

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			if (npcId == 4039020 || npcId == 4039021 || npcId == 4039022) { 
			    npc.broadcastPacket(new S_DoActionGFX(npc.getId(), 11));
			    npc.setActionStatus(11);
			    npc.broadcastPacket(new S_NPCPack(npc));
			    npc.broadcastPacket(new S_DoActionGFX(npc.getId(), 11));
			    npc.setActionStatus(0);
			    npc.broadcastPacket(new S_NPCPack(npc));
			   }
			   else if (npcId == 4039000 || npcId == 4039006 || npcId == 4039007) { 

			    npc.broadcastPacket(new S_DoActionGFX(npc.getId(), 11));
			    npc.setActionStatus(11);
			    npc.broadcastPacket(new S_NPCPack(npc));
			    npc.broadcastPacket(new S_DoActionGFX(npc.getId(), 11));
			    npc.setActionStatus(0);
			    npc.broadcastPacket(new S_NPCPack(npc));
			   }else if (npcId == 9170 || npcId == 9171 || npcId == 9172) { //린드레이드

				    npc.broadcastPacket(new S_DoActionGFX(npc.getId(), 11));
				    npc.setActionStatus(11);
				    npc.broadcastPacket(new S_NPCPack(npc));
				    npc.broadcastPacket(new S_DoActionGFX(npc.getId(), 11));
				    npc.setActionStatus(0);
				    npc.broadcastPacket(new S_NPCPack(npc));
				   }
			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
			if (0 < timeMillisToDelete) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc,
						timeMillisToDelete);
				timer.begin();
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1SpawnUtil[]Error4", e);
		}
	}
	//린드레이드
	public static void spawn4(L1NpcInstance pc, int npcId, int randomRange, int timeMillisToDelete, boolean isUsePainwand) {
		  try {
		   L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
		   npc.setId(ObjectIdFactory.getInstance().nextId());
		   npc.setMap(pc.getMapId());

		   if (randomRange == 0) {
		    npc.getLocation().set(pc.getLocation());
		    npc.getLocation().forward(pc.getMoveState().getHeading());
		   } else {
		    int tryCount = 0;
		    do {
		     tryCount++;
		     npc.setX(pc.getX() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
		     npc.setY(pc.getY() + (int) (Math.random() * randomRange) - (int) (Math.random() * randomRange));
		     if (npc.getMap(). isInMap(npc.getLocation()) && npc.getMap(). isPassable(npc.getLocation())) {
		      break;
		     }
		     Thread.sleep(1);
		    } while (tryCount < 50);

		    if (tryCount >= 50) {
		     npc.getLocation().set(pc.getLocation());
		     npc.getLocation().forward(pc.getMoveState().getHeading());
		    }
		   }
		   npc.setHomeX(npc.getX());
		   npc.setHomeY(npc.getY());
		   npc.getMoveState().setHeading(pc.getMoveState().getHeading());
		   if(isUsePainwand) {
		    if(npc instanceof L1MonsterInstance) {
		     L1MonsterInstance mon = (L1MonsterInstance)npc;
		     mon.set_storeDroped(2);
		    }
		   }
		   L1World.getInstance().storeObject(npc);
		   L1World.getInstance().addVisibleObject(npc);
		   npc.getLight().turnOnOffLight();
		   npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
		   if (0 < timeMillisToDelete) {
		    L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, timeMillisToDelete);
		    timer.begin();
		   }
		  } catch (Exception e) {
		   _log.log(Level.SEVERE, "L1SpawnUtil[]Error5", e);
		  }
		 }
	/**
	 * 엔피씨를 스폰한다
	 * 
	 * @param x
	 * @param y
	 * @param map
	 * @param npcId
	 * @param randomRange
	 * @param timeMillisToDelete
	 * @param movemap
	 *            (이동시킬 맵을 설정한다 - 안타레이드)
	 */
	public static void spawn3(int x, int y, short MapId, int Heading,
			int npcId, int randomRange, boolean isUsePainwand,
			int timeMillisToDelete) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(MapId);
			if (randomRange == 0) {
				npc.getLocation().forward(Heading);
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(x + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					npc.setY(y + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation())
							&& npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);

				if (tryCount >= 50) {
					npc.getLocation().forward(Heading);
				}
			}

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(Heading);
			if (isUsePainwand) {
				if (npc instanceof L1MonsterInstance) {
					L1MonsterInstance mon = (L1MonsterInstance) npc;
					mon.set_storeDroped(2);
				}
			}

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
			if (0 < timeMillisToDelete) {
				L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc,
						timeMillisToDelete);
				timer.begin();
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1SpawnUtil[]Error6", e);
		}
	}
	/** 얼음 수정동굴 인스턴스 전용 스폰 */
	public static void spawn5(int x, int y, short MapId, int Heading,
			int npcId, int randomRange, int type, int roomnumber) {
		try {
			L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			npc.setId(ObjectIdFactory.getInstance().nextId());
			npc.setMap(MapId);
			if (randomRange == 0) {
				npc.getLocation().set(x, y, MapId);
				npc.getLocation().forward(Heading);
			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX(x + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					npc.setY(y + (int) (Math.random() * randomRange)
							- (int) (Math.random() * randomRange));
					if (npc.getMap().isInMap(npc.getLocation())
							&& npc.getMap().isPassable(npc.getLocation())) {
						break;
					}
					Thread.sleep(1);
				} while (tryCount < 50);
				if (tryCount >= 50) {
					npc.getLocation().forward(Heading);
				}
			}
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.getMoveState().setHeading(Heading);

			L1World.getInstance().storeObject(npc);
			L1World.getInstance().addVisibleObject(npc);

			switch (type) {
			case 1: // 얼음여왕
				IceQueenController iceq = IceQueenController.getInstance();
				if (roomnumber == 1) {
					iceq.add(npc, 1);
				} else if (roomnumber == 2) {
					iceq.add(npc, 2);
				} else if (roomnumber == 3) {
					iceq.add(npc, 3);
				} else if (roomnumber == 4) {
					iceq.add(npc, 4);
				} else if (roomnumber == 5) {
					iceq.add(npc, 5);
				}
				break;
			case 2: // 아이스데몬
				IceDemonController iced = IceDemonController.getInstance();
				if (roomnumber == 1) {
					iced.add(npc, 1);
				} else if (roomnumber == 2) {
					iced.add(npc, 2);
				} else if (roomnumber == 3) {
					iced.add(npc, 3);
				} else if (roomnumber == 4) {
					iced.add(npc, 4);
				} else if (roomnumber == 5) {
					iced.add(npc, 5);
				}
				break;
			}
			npc.getLight().turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시

		} catch (Exception e) {
			_log.log(Level.SEVERE, "L1SpawnUtil[]Error7", e);
		}
	}

}
