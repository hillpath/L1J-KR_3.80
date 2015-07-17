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
package l1j.server.server.model.Instance;

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.MiniWarGame;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1WarSpawn;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CommonUtil;

public class L1EventTowerInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int[] loc = { -3, -2, -1, 0, 1, 2, 3 };
	private int[] crownloc = { -2, -1, 0, 1, 2 };
	public static boolean isStart = true;
	public static boolean isBoss = true;
	public static int spwanTime = 120; 
	public static final int[][] location = {
		{ 33384, 32351, 4 }, //11 0
		{ 33384, 32351, 4 }, //11 0
		{ 33384, 32351, 4 }, //11 0
		{ 33384, 32351, 4 }, //11 0
		{ 33384, 32351, 4 }, //11 0
		{ 33384, 32351, 4 }, //11 0
	};

	public L1EventTowerInstance(L1Npc template) {
		super(template);
	}

	private L1Character _lastattacker;
	private boolean _event = true;
	private int _crackStatus;

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.getNearObjects().addKnownObject(this);
		perceivedFrom.sendPackets(new S_NPCPack(this));
	}

	@Override
	public void onAction(L1PcInstance player) {
		if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(player, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.addPcPoisonAttack(player, this);
			}
			attack.action();
			attack.commit();
		}
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) {	
		if (_event) {
			L1PcInstance pc = null;
			if (attacker instanceof L1PcInstance) {
				pc = (L1PcInstance) attacker;
			} else if (attacker instanceof L1PetInstance) {
				pc = (L1PcInstance) ((L1PetInstance) attacker).getMaster();
			} else if (attacker instanceof L1SummonInstance) {
				pc = (L1PcInstance) ((L1SummonInstance) attacker).getMaster();
			}
			if (pc == null) {
				return;
			}

			boolean existDefenseClan = false;
			for (L1Clan clan : L1World.getInstance().getAllClans()) {
//				int clanCastleId = clan.getCastleId();
//				if (clanCastleId == _castle_id) {
//					existDefenseClan = true;
//					break;
//				}
			}
			boolean isProclamation = false;
			for (L1War war : L1World.getInstance().getWarList()) {
//				if (_castle_id == war.GetCastleId()) {
//					isProclamation = war.CheckClanInWar(pc.getClanname());
//					break;
//				}
			}
			if (existDefenseClan == true && isProclamation == false) {
				return;
			}
			L1PcInstance player = (L1PcInstance)attacker;

			if (getCurrentHp() > 0 && !isDead()) {
				damage = damage / CommonUtil.random(15, 20);
				
				attacker.setEventDamage(attacker.getEventDamage() + damage);				
				
				int newHp = getCurrentHp() - damage;				
				
				if (newHp <= 0 && !isDead()) {
					setCurrentHp(0);
					setDead(true);
					setActionStatus(ActionCodes.ACTION_TowerDie);
					_lastattacker = attacker;
					_crackStatus = 0;
					Death death = new Death();
					GeneralThreadPool.getInstance().execute(death);
					// Death(attacker);
				}
				if (newHp > 0) {
					setCurrentHp(newHp);
					if ((getMaxHp() * 1 / 4) > getCurrentHp()) {
						if (_crackStatus != 3) {
							Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), ActionCodes.ACTION_TowerCrack3));
							setActionStatus(ActionCodes.ACTION_TowerCrack3);
							_crackStatus = 3;
							}
					} else if ((getMaxHp() * 2 / 4) > getCurrentHp()) {
						if (_crackStatus != 2) {
							Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), ActionCodes.ACTION_TowerCrack2));
							setActionStatus(ActionCodes.ACTION_TowerCrack2);
							_crackStatus = 2;
							}
					} else if ((getMaxHp() * 3 / 4) > getCurrentHp()) {
						if (_crackStatus != 1) {
							Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), ActionCodes.ACTION_TowerCrack1));
							setActionStatus(ActionCodes.ACTION_TowerCrack1);
							_crackStatus = 1;
							}
					}
				}
			} else if (!isDead()) {
				setDead(true);
				setActionStatus(ActionCodes.ACTION_TowerDie);
				_lastattacker = attacker;
				Death death = new Death();
				GeneralThreadPool.getInstance().execute(death);
				// Death(attacker);
			}
		}
	}

	class Death implements Runnable {
		L1Character lastAttacker = _lastattacker;
		L1Object object = L1World.getInstance().findObject(getId());
		L1EventTowerInstance npc = (L1EventTowerInstance) object;

		@Override
		public void run() {
			setCurrentHp(0);
			setDead(true);
			setActionStatus(ActionCodes.ACTION_TowerDie);
			int targetobjid = npc.getId();

			npc.getMap().setPassable(npc.getLocation(), true);

			Broadcaster.broadcastPacket(npc, new S_DoActionGFX(targetobjid, ActionCodes.ACTION_TowerDie));
			
			if (!isSubTower()) {
				L1WarSpawn warspawn = new L1WarSpawn();
				L1Npc l1npc = NpcTable.getInstance().getTemplate(6100002);				
				//l1npc.set_Spot(getNpcTemplate().get_npcId() - 49100);
				int[] loc = new int[3];
				loc[0] = npc.getX() + crownloc[CommonUtil.random(crownloc.length)];
				loc[1] = npc.getY() + crownloc[CommonUtil.random(crownloc.length)];
				loc[2] = npc.getMapId();
				warspawn.SpawnWarObject(l1npc, loc[0], loc[1], (short) (loc[2]));
			}
			
			if(getNpcTemplate().get_npcId() == 6100001){
					L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fY이벤트 왕관이 모습을 드러냅니다."));			
					L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fC이벤트 왕관이 모습을 드러냅니다."));
			} else if(getNpcTemplate().get_npcId() == 6100008){
					MiniWarGame.getInstance().setTowerDead(true);
					MiniWarGame.getInstance().winLine = 2;
					L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fC[미니공성] A팀의 수호탑이 부셔졌습니다."));
					for (int i = 0; i < MiniWarGame.getInstance()._Members.size(); i++) {
						L1PcInstance pc = MiniWarGame.getInstance()._Members.get(i);
						  if(pc != null){
							  if(pc.get_MiniDuelLine() == MiniWarGame.getInstance().winLine){
								  pc.getInventory().storeItem(55554, 1);
								  pc.getInventory().storeItem(41159, 1000);
								  pc.getInventory().storeItem(40308, 30000000);
								  pc.sendPackets(new S_SystemMessage("\\fT우승팀에게는 각각 무기상자, 깃털 1000개와 아덴3천만 지급완료."));
							  }
						  }
					}
					
					L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "NEW미니공성이 종료되었습니다."));
					
					/** 자동 텔레포트**/
					MiniWarGame.getInstance().TelePort();
					
					/** 종료 **/
					MiniWarGame.getInstance().End(); 
				} else if(getNpcTemplate().get_npcId() == 6100009){
					MiniWarGame.getInstance().setTowerDead2(true);
					MiniWarGame.getInstance().winLine = 1;
					L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fC[미니공성] B팀의 수호탑이 부셔졌습니다."));
					for (int i = 0; i < MiniWarGame.getInstance()._Members.size(); i++) {
						L1PcInstance pc = MiniWarGame.getInstance()._Members.get(i);
						  if(pc != null){
							  if(pc.get_MiniDuelLine() == MiniWarGame.getInstance().winLine){
								  pc.getInventory().storeItem(55554, 1);
								  pc.getInventory().storeItem(41159, 1000);
								  pc.getInventory().storeItem(40308, 30000000);
								  pc.sendPackets(new S_SystemMessage("\\fT우승팀에게는 각각 무기상자,깃털 1000개와 아덴3천만 지급완료."));
							  }
						  }
					}
					
					L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "NEW미니공성이 종료되었습니다."));
					
					/** 자동 텔레포트**/
					MiniWarGame.getInstance().TelePort();
					
					/** 종료 **/
					MiniWarGame.getInstance().End(); 
				}
		}
	}

	@Override
	public void deleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		allTargetClear();
		_master = null;
		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.getNearObjects().removeKnownObject(this);
			pc.sendPackets(new S_RemoveObject(this));
		}
		getNearObjects().removeAllKnownObjects();
	}

	public boolean isSubTower() {
		return (getNpcTemplate().get_npcId() == 81190 || getNpcTemplate().get_npcId() == 81191 || getNpcTemplate().get_npcId() == 81192 || getNpcTemplate().get_npcId() == 81193);
	}
}
