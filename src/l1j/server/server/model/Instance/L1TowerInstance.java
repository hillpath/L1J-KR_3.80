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

import java.util.Collection;
import javolution.util.FastTable;

import l1j.server.server.ActionCodes;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1WarSpawn;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.templates.L1Npc;

public class L1TowerInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public L1TowerInstance(L1Npc template) {
		super(template);
	}

	private L1Character _lastattacker;

	private int _castle_id;

	private int _crackStatus;

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		if (perceivedFrom == null)
			return;
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
		if (_castle_id == 0) {
			if (isSubTower()) {
				_castle_id = L1CastleLocation.ADEN_CASTLE_ID;
			} else {
				_castle_id = L1CastleLocation.getCastleId(getX(), getY(),
						getMapId());
			}
		}
		if (_castle_id > 0
				&& WarTimeController.getInstance().isNowWar(_castle_id)) {

			if (_castle_id == L1CastleLocation.ADEN_CASTLE_ID && !isSubTower()) {
				int subTowerDeadCount = 0;
				L1TowerInstance lt = null;
				Collection<L1Object> list = null;
				list = L1World.getInstance().getObject();
				for (L1Object l1object : list) {
					if (l1object == null)
						continue;
					if (l1object instanceof L1TowerInstance) {
						lt = (L1TowerInstance) l1object;
						if (lt.isSubTower() && lt.isDead()) {
							subTowerDeadCount++;
							if (subTowerDeadCount == 4) {
								break;
							}
						}
					}

				}
				if (subTowerDeadCount < 3) {
					return;
				}
			}

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
				int clanCastleId = clan.getCastleId();
				if (clanCastleId == _castle_id) {
					existDefenseClan = true;
					break;
				}
			}
			boolean isProclamation = false;
			for (L1War war : L1World.getInstance().getWarList()) {
				if (_castle_id == war.GetCastleId()) {
					isProclamation = war.CheckClanInWar(pc.getClanname());
					break;
				}
			}
			if (existDefenseClan == true && isProclamation == false) {
				return;
			}

			if (getCurrentHp() > 0 && !isDead()) {
				int newHp = getCurrentHp() - damage;
				if (newHp <= 0 && !isDead()) {
					setCurrentHp(0);
					setDead(true);
					setActionStatus(ActionCodes.ACTION_TowerDie);
					_lastattacker = attacker;
					_crackStatus = 0;
					death();
					// Death death = new Death();
					// GeneralThreadPool.getInstance().execute(death);
					// Death(attacker);
				}
				if (newHp > 0) {
					setCurrentHp(newHp);
					if ((getMaxHp() * 1 / 4) > getCurrentHp()) {
						if (_crackStatus != 3) {
							Broadcaster.broadcastPacket(this,
									new S_DoActionGFX(getId(),
											ActionCodes.ACTION_TowerCrack3));
							setActionStatus(ActionCodes.ACTION_TowerCrack3);
							_crackStatus = 3;
						}
					} else if ((getMaxHp() * 2 / 4) > getCurrentHp()) {
						if (_crackStatus != 2) {
							Broadcaster.broadcastPacket(this,
									new S_DoActionGFX(getId(),
											ActionCodes.ACTION_TowerCrack2));
							setActionStatus(ActionCodes.ACTION_TowerCrack2);
							_crackStatus = 2;
						}
					} else if ((getMaxHp() * 3 / 4) > getCurrentHp()) {
						if (_crackStatus != 1) {
							Broadcaster.broadcastPacket(this,
									new S_DoActionGFX(getId(),
											ActionCodes.ACTION_TowerCrack1));
							setActionStatus(ActionCodes.ACTION_TowerCrack1);
							_crackStatus = 1;
						}
					}
				}
			} else if (!isDead()) {
				setDead(true);
				setActionStatus(ActionCodes.ACTION_TowerDie);
				_lastattacker = attacker;
				death();
				// Death death = new Death();
				// GeneralThreadPool.getInstance().execute(death);
				// Death(attacker);
			}
		}
	}

	class Death implements Runnable {
		L1Character lastAttacker = _lastattacker;

		L1Object object = L1World.getInstance().findObject(getId());

		L1TowerInstance npc = (L1TowerInstance) object;

		public void run() {
			setCurrentHp(0);
			setDead(true);
			setActionStatus(ActionCodes.ACTION_TowerDie);
			int targetobjid = npc.getId();

			npc.getMap().setPassable(npc.getLocation(), true);

			Broadcaster.broadcastPacket(npc, new S_DoActionGFX(targetobjid,
					ActionCodes.ACTION_TowerDie));

			if (!isSubTower()) {
				L1WarSpawn warspawn = new L1WarSpawn();
				warspawn.SpawnCrown(_castle_id);
			}
		}
	}

	private void death() {
		try {
			L1Character lastAttacker = _lastattacker;
			L1Object object = L1World.getInstance().findObject(getId());
			L1TowerInstance npc = (L1TowerInstance) object;
			if (this == null)
				return;
			setCurrentHp(0);
			setDead(true);
			setActionStatus(ActionCodes.ACTION_TowerDie);
			int targetobjid = npc.getId();

			npc.getMap().setPassable(npc.getLocation(), true);

			Broadcaster.broadcastPacket(npc, new S_DoActionGFX(targetobjid,
					ActionCodes.ACTION_TowerDie));

			if (!isSubTower()) {
				L1WarSpawn warspawn = new L1WarSpawn();
				warspawn.SpawnCrown(_castle_id);
			}
		} catch (Exception e) {
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
		FastTable<L1PcInstance> list = null;
		list = L1World.getInstance().getRecognizePlayer(this);
		for (L1PcInstance pc : list) {
			if (pc == null)
				continue;
			pc.getNearObjects().removeKnownObject(this);
			pc.sendPackets(new S_RemoveObject(this));
		}
		getNearObjects().removeAllKnownObjects();
	}

	public boolean isSubTower() {
		return (getNpcTemplate().get_npcId() == 81190
				|| getNpcTemplate().get_npcId() == 81191
				|| getNpcTemplate().get_npcId() == 81192 || getNpcTemplate()
				.get_npcId() == 81193);
	}
}
