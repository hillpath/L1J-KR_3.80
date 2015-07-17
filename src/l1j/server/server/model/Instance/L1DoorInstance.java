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

import javolution.util.FastTable;
import l1j.server.server.ActionCodes;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Door;
import l1j.server.server.serverpackets.S_DoorPack;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.templates.L1Npc;
import server.controller.Npc.NpcRestController;

public class L1DoorInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	public static final int PASS = 0;

	public static final int NOT_PASS = 1;

	private int _doorId = 0;

	private int _direction = 0;

	private int _leftEdgeLocation = 0;

	private int _rightEdgeLocation = 0;

	private int _openStatus = ActionCodes.ACTION_Close;

	private int _passable = NOT_PASS;

	private int _keeperId = 0;

	private int _autostatus = 0;

	public long CloseTime = 0;

	public L1DoorInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance pc) {
		if (getMaxHp() == 0 || getMaxHp() == 1) {
			return;
		}
		if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(pc, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.addPcPoisonAttack(pc, this);
			}
			attack.action();
			attack.commit();
			castledoorAction(pc);
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.getNearObjects().addKnownObject(this);
		perceivedFrom.sendPackets(new S_DoorPack(this));
		sendDoorPacket(perceivedFrom);
		
		if(getDoorId() >= 5000 && getDoorId() <= 5009 ){//지나가게만들 문번호 현재얼던 

		if (getOpenStatus() == ActionCodes.ACTION_Close) {
			isPassibleDoor(false);
			setPassable(L1DoorInstance.NOT_PASS);
			sendDoorPacket(null);
		}
		if (getOpenStatus() == ActionCodes.ACTION_Open) {
			isPassibleDoor(true);
			setPassable(L1DoorInstance.PASS);
			sendDoorPacket(null);
		}
		}
	}

	@Override
	public void deleteMe() {
		setPassable(PASS);
		sendDoorPacket(null);

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

	@Override
	public void receiveDamage(L1Character attacker, int damage) {
		if (getMaxHp() == 0 || getMaxHp() == 1) {
			return;
		}

		if (getCurrentHp() > 0 && !isDead()) {
			int newHp = getCurrentHp() - damage;
			if (newHp <= 0 && !isDead()) {
				setCurrentHp(0);
				setDead(true);
				setActionStatus(ActionCodes.ACTION_DoorDie);
				// Death death = new Death(attacker);
				// GeneralThreadPool.getInstance().execute(death);
				death();
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
				if ((getMaxHp() * 1 / 6) > getCurrentHp()) {
					if (_crackStatus != 5) {
						Broadcaster.broadcastPacket(this, new S_DoActionGFX(
								getId(), ActionCodes.ACTION_DoorAction5));
						setActionStatus(ActionCodes.ACTION_DoorAction5);
						_crackStatus = 5;
					}
				} else if ((getMaxHp() * 2 / 6) > getCurrentHp()) {
					if (_crackStatus != 4) {
						Broadcaster.broadcastPacket(this, new S_DoActionGFX(
								getId(), ActionCodes.ACTION_DoorAction4));
						setActionStatus(ActionCodes.ACTION_DoorAction4);
						_crackStatus = 4;
					}
				} else if ((getMaxHp() * 3 / 6) > getCurrentHp()) {
					if (_crackStatus != 3) {
						Broadcaster.broadcastPacket(this, new S_DoActionGFX(
								getId(), ActionCodes.ACTION_DoorAction3));
						setActionStatus(ActionCodes.ACTION_DoorAction3);
						_crackStatus = 3;
					}
				} else if ((getMaxHp() * 4 / 6) > getCurrentHp()) {
					if (getAutoStatus() == 1) {// 이 상태에서 자동 리페어가 되는지 확실하지는 않다;
						repairGate();
					} else if (_crackStatus != 2) {
						Broadcaster.broadcastPacket(this, new S_DoActionGFX(
								getId(), ActionCodes.ACTION_DoorAction2));
						setActionStatus(ActionCodes.ACTION_DoorAction2);
						_crackStatus = 2;
					}
				} else if ((getMaxHp() * 5 / 6) > getCurrentHp()) {
					if (_crackStatus != 1) {
						Broadcaster.broadcastPacket(this, new S_DoActionGFX(
								getId(), ActionCodes.ACTION_DoorAction1));
						setActionStatus(ActionCodes.ACTION_DoorAction1);
						_crackStatus = 1;
					}
				}
			}
		} else if (!isDead()) {
			setDead(true);
			setActionStatus(ActionCodes.ACTION_DoorDie);
			// Death death = new Death(attacker);
			// GeneralThreadPool.getInstance().execute(death);
			death();
		}
	}

	@Override
	public void setCurrentHp(int i) {
		super.setCurrentHp(i);
	}

	public void death() {
		try {
			setCurrentHp(0);
			setDead(true);
			isPassibleDoor(true);
			setActionStatus(ActionCodes.ACTION_DoorDie);

			getMap().setPassable(getLocation(), true);

			Broadcaster.broadcastPacket(L1DoorInstance.this, new S_DoActionGFX(
					getId(), ActionCodes.ACTION_DoorDie));
			setPassable(PASS);
			sendDoorPacket(null);
		} catch (Exception e) {
		}
	}

	class Death implements Runnable {
		L1Character _lastAttacker;

		public Death(L1Character lastAttacker) {
			_lastAttacker = lastAttacker;
		}

		public void run() {
			setCurrentHp(0);
			setDead(true);
			isPassibleDoor(true);
			setActionStatus(ActionCodes.ACTION_DoorDie);

			getMap().setPassable(getLocation(), true);

			Broadcaster.broadcastPacket(L1DoorInstance.this, new S_DoActionGFX(
					getId(), ActionCodes.ACTION_DoorDie));
			setPassable(PASS);
			sendDoorPacket(null);
		}
	}

	/*
	 * class DoorTimer implements Runnable { @Override public void run() { if
	 * (_destroyed) { // 이미 파기되어 있지 않은가 체크 return; } close(); } }
	 */

	private void sendDoorPacket(L1PcInstance pc) {
		int entranceX = getEntranceX();
		int entranceY = getEntranceY();
		int leftEdgeLocation = getLeftEdgeLocation();
		int rightEdgeLocation = getRightEdgeLocation();

		int size = rightEdgeLocation - leftEdgeLocation;
		if (size == 0) {
			sendPacket(pc, entranceX, entranceY);
		} else {
			if (getDirection() == 0) {
				for (int x = leftEdgeLocation; x <= rightEdgeLocation; x++) {
					sendPacket(pc, x, entranceY);
				}
			} else {
				for (int y = leftEdgeLocation; y <= rightEdgeLocation; y++) {
					sendPacket(pc, entranceX, y);
				}
			}
		}
	}

	private void sendPacket(L1PcInstance pc, int x, int y) {
		S_Door packet = new S_Door(x, y, getDirection(), getPassable());
		if (pc != null) {
			//if (getOpenStatus() == ActionCodes.ACTION_Close) {
				pc.sendPackets(packet);
			//}
		} else {
			Broadcaster.broadcastPacket(this, packet);
		}
	}

	public void open() {
		if (isDead()) {
			return;
		}
		if (getOpenStatus() == ActionCodes.ACTION_Close) {
			isPassibleDoor(true);
			if (this.getDoorId() >= 9049 && this.getDoorId() <= 9060
					|| this.getDoorId() == 121) {
				CloseTime = System.currentTimeMillis() + 5000;
				NpcRestController.getInstance().addDoor(this);
			}
			if (this.getDoorId() >= 7100 && this.getDoorId() <= 7160) {
				CloseTime = System.currentTimeMillis() + 600000;
				NpcRestController.getInstance().addDoor(this);
			}
			if (this.getDoorId() >= 4005 && this.getDoorId() <= 4066) {//라던문				
				CloseTime = System.currentTimeMillis() + 600000;
				NpcRestController.getInstance().addDoor(this);
			}
			
			if (this.getDoorId() >= 8001 && this.getDoorId() <= 8010) {
				CloseTime = System.currentTimeMillis() + 1800000;
				NpcRestController.getInstance().addDoor(this);
			}
			Broadcaster.broadcastPacket(this, new S_DoActionGFX(this.getId(),
					ActionCodes.ACTION_Open));
			setOpenStatus(ActionCodes.ACTION_Open);
			setPassable(L1DoorInstance.PASS);
			sendDoorPacket(null);
		}
	}

	public void close() {
		if (isDead()) {
			return;
		}
		if (getOpenStatus() == ActionCodes.ACTION_Open) {
			isPassibleDoor(false);
			Broadcaster.broadcastPacket(this, new S_DoActionGFX(this.getId(),
					ActionCodes.ACTION_Close));
			setOpenStatus(ActionCodes.ACTION_Close);
			setPassable(L1DoorInstance.NOT_PASS);
			sendDoorPacket(null);
		}
	}

	public void isPassibleDoor(boolean flag) {
		int leftEdgeLocation = this.getLeftEdgeLocation();
		int rightEdgeLocation = this.getRightEdgeLocation();
		int size = rightEdgeLocation - leftEdgeLocation;
		if (size == 0) {
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX(), this.getY() - 1, flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX(), this.getY() + 1, flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX() + 1, this.getY(), flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX() + 1, this.getY() - 1, flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX() + 1, this.getY() + 1, flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX() - 1, this.getY(), flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX() - 1, this.getY() - 1, flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX() - 1, this.getY() + 1, flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX(), this.getY() - 2, flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX(), this.getY() + 2, flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX() + 2, this.getY(), flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX() + 2, this.getY() - 2, flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX() + 2, this.getY() + 2, flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX() - 2, this.getY(), flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX() - 2, this.getY() - 2, flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX() - 2, this.getY() + 2, flag);
			L1WorldMap.getInstance().getMap(this.getMapId()).setPassable(
					this.getX(), this.getY(), flag);
		} else {
			if (this.getDirection() == 0) {
				for (int doorX = leftEdgeLocation; doorX <= rightEdgeLocation; doorX++) {
					L1WorldMap.getInstance().getMap(this.getMapId())
							.setPassable(doorX, this.getY(), flag);
					L1WorldMap.getInstance().getMap(this.getMapId())
							.setPassable(doorX, this.getY() - 1, flag);
					L1WorldMap.getInstance().getMap(this.getMapId())
							.setPassable(doorX, this.getY() + 1, flag);
					L1WorldMap.getInstance().getMap(this.getMapId())
							.setPassable(doorX, this.getY() - 2, flag);
					L1WorldMap.getInstance().getMap(this.getMapId())
							.setPassable(doorX, this.getY() + 2, flag);
				}
			} else {
				for (int doorY = leftEdgeLocation; doorY <= rightEdgeLocation; doorY++) {
					L1WorldMap.getInstance().getMap(this.getMapId())
							.setPassable(this.getX(), doorY, flag);
					L1WorldMap.getInstance().getMap(this.getMapId())
							.setPassable(this.getX() + 1, doorY, flag);
					L1WorldMap.getInstance().getMap(this.getMapId())
							.setPassable(this.getX() - 1, doorY, flag);
					L1WorldMap.getInstance().getMap(this.getMapId())
							.setPassable(this.getX() + 2, doorY, flag);
					L1WorldMap.getInstance().getMap(this.getMapId())
							.setPassable(this.getX() - 2, doorY, flag);
				}
			}
		}
	}

	public void repairGate() {
		if (getMaxHp() > 1) {
			setDead(false);
			setCurrentHp(getMaxHp());
			setActionStatus(0);
			setCrackStatus(0);
			setOpenStatus(ActionCodes.ACTION_Open);
			close();
		}
	}

	private void castledoorAction(L1PcInstance pc) {
		FastTable<L1Object> list = null;
		list = L1World.getInstance().getVisibleObjects(this, 13);
		for (L1Object obj : list) {
			if (obj == null)
				continue;
			if (obj instanceof L1CastleGuardInstance) {
				L1CastleGuardInstance guard = (L1CastleGuardInstance) obj;
				guard.setTarget(pc);
			}
		}
	}

	public int getDoorId() {
		return _doorId;
	}

	public void setDoorId(int i) {
		_doorId = i;
	}

	public int getDirection() {
		return _direction;
	}

	public void setDirection(int i) {
		if (i == 0 || i == 1) {
			_direction = i;
		}
	}

	public int getEntranceX() {
		int entranceX = 0;
		if (getDirection() == 0) {
			entranceX = getX();
		} else {
			entranceX = getX() - 1;
		}
		return entranceX;
	}

	public int getEntranceY() {
		int entranceY = 0;
		if (getDirection() == 0) {
			entranceY = getY() + 1;
		} else {
			entranceY = getY();
		}
		return entranceY;
	}

	public int getLeftEdgeLocation() {
		return _leftEdgeLocation;
	}

	public void setLeftEdgeLocation(int i) {
		_leftEdgeLocation = i;
	}

	public int getRightEdgeLocation() {
		return _rightEdgeLocation;
	}

	public void setRightEdgeLocation(int i) {
		_rightEdgeLocation = i;
	}

	public int getOpenStatus() {
		return _openStatus;
	}

	public void setOpenStatus(int i) {
		if (i == ActionCodes.ACTION_Open || i == ActionCodes.ACTION_Close) {
			_openStatus = i;
		}
	}

	public int getPassable() {
		return _passable;
	}

	public void setPassable(int i) {
		if (i == PASS || i == NOT_PASS) {
			_passable = i;
		}
	}

	public int getKeeperId() {
		return _keeperId;
	}

	public void setKeeperId(int i) {
		_keeperId = i;
	}

	private int _crackStatus;

	public int getCrackStatus() {
		return _crackStatus;
	}

	public void setCrackStatus(int i) {
		_crackStatus = i;
	}

	public int getAutoStatus() {
		return _autostatus;
	}

	public void setAutoStatus(int i) {
		_autostatus = i;
	}

}