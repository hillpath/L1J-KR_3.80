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
package l1j.server.server.model.Instance;

import javolution.util.FastTable;

import java.lang.reflect.Constructor;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.serverpackets.S_FollowerPack;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Npc;

public class L1FollowerInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;

	public L1FollowerInstance(L1Npc template, L1QuestInstance target,
			L1PcInstance master) {
		super(template);

		_master = master;
		setId(ObjectIdFactory.getInstance().nextId());
		setMaster(master);
		setX(target.getX());
		setY(target.getY());
		setMap(target.getMapId());
		getMoveState().setHeading(target.getMoveState().getHeading());
		setLightSize(target.getLightSize());
		target.setParalyzed(true);
		target.deleteMe();
		L1World.getInstance().storeObject(this);
		L1World.getInstance().addVisibleObject(this);
		FastTable<L1PcInstance> list = null;
		list = L1World.getInstance().getRecognizePlayer(this);
		for (L1PcInstance pc : list) {
			if (pc != null)
				onPerceive(pc);
		}
		startAI();
		master.addFollower(this);
	}

	@Override
	public boolean noTarget() {
		L1NpcInstance npc = null;
		L1PcInstance pc = null;
		FastTable<L1Object> list = null;
		list = L1World.getInstance().getVisibleObjects(this);
		for (L1Object object : list) {
			if (object == null)
				continue;
			if (object instanceof L1NpcInstance) {
				npc = (L1NpcInstance) object;
				switch (npc.getNpcTemplate().get_npcId()) {
				case 70740:
				case 71093:
					setParalyzed(true);
					pc = (L1PcInstance) _master;
					if (!pc.getInventory().checkItem(40593)) {
						createNewItem(pc, 40593, 1);
					}
					deleteMe();
					return true;
				case 70811:
				case 71094:
					setParalyzed(true);
					pc = (L1PcInstance) _master;
					if (!pc.getInventory().checkItem(40582)) {
						createNewItem(pc, 40582, 1);
					}
					deleteMe();
					return true;
				case 71061:
				case 71062:
					if (getLocation()
							.getTileLineDistance(_master.getLocation()) < 3) {
						pc = (L1PcInstance) _master;
						if ((pc.getX() >= 32448 && pc.getX() <= 32452) // ¸ðÅüÀÌ
								// ¸ð½º ÁÖº¯
								// ÁÂÇ¥
								&& (pc.getY() >= 33048 && pc.getY() <= 33052)
								&& (pc.getMapId() == 440)) {
							setParalyzed(true);
							if (!pc.getInventory().checkItem(40711)) {
								createNewItem(pc, 40711, 1);
								pc.getQuest().set_step(L1Quest.QUEST_CADMUS, 3);
							}
							deleteMe();
							return true;
						}
					}
					break;
				case 71074:
				case 71075:
					if (getLocation()
							.getTileLineDistance(_master.getLocation()) < 3) {
						pc = (L1PcInstance) _master;
						if ((pc.getX() >= 32731 && pc.getX() <= 32735) // ¸®ÀÚµå¸¸
								// Àå·Î ÁÖº¯
								// ÁÂÇ¥
								&& (pc.getY() >= 32854 && pc.getY() <= 32858)
								&& (pc.getMapId() == 480)) {
							setParalyzed(true);
							if (!pc.getInventory().checkItem(40633)) {
								createNewItem(pc, 40633, 1);
								pc.getQuest().set_step(L1Quest.QUEST_LIZARD, 2);
							}
							deleteMe();
							return true;
						}
					}
					break;
				case 70964:
				case 70957:
					if (getLocation()
							.getTileLineDistance(_master.getLocation()) < 3) {
						pc = (L1PcInstance) _master;
						if ((pc.getX() >= 32917 && pc.getX() <= 32921) // ¹å½´ ÁÖº¯
								// ÁÂÇ¥
								&& (pc.getY() >= 32974 && pc.getY() <= 32978)
								&& (pc.getMapId() == 410)) {
							setParalyzed(true);
							createNewItem(pc, 41003, 1);
							pc.getQuest().set_step(L1Quest.QUEST_ROI, 0);
							deleteMe();
							return true;
						}
					}
					break;
				}
			}
		}

		if (_master.isDead()
				|| getLocation().getTileLineDistance(_master.getLocation()) > 10) {
			setParalyzed(true);
			spawn(getNpcTemplate().get_npcId(), getX(), getY(), getMoveState()
					.getHeading(), getMapId());
			deleteMe();
			return true;
		} else if (_master != null && _master.getMapId() == getMapId()) {
			if (getLocation().getTileLineDistance(_master.getLocation()) > 2) {
				setDirectionMove(moveDirection(_master.getX(), _master.getY()));
				setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
			}
		}
		return false;
	}

	@Override
	public synchronized void deleteMe() {
		_master.getFollowerList().remove(getId());
		getMap().setPassable(getLocation(), true);
		super.deleteMe();
	}

	@Override
	public void onAction(L1PcInstance pc) {
		L1Attack attack = new L1Attack(pc, this);
		if (attack.calcHit()) {
			attack.calcDamage();
			attack.addPcPoisonAttack(pc, this);
		}
		attack.action();
		attack.commit();
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		if (isDead()) {
			return;
		}
		switch (getNpcTemplate().get_npcId()) {
		case 71093:
			if (_master.equals(player)) {
				player.sendPackets(new S_NPCTalkReturn(getId(), "searcherk2"));
			} else {
				player.sendPackets(new S_NPCTalkReturn(getId(), "searcherk4"));
			}
			break;
		case 71094:
			if (_master.equals(player)) {
				player.sendPackets(new S_NPCTalkReturn(getId(), "endiaq2"));
			} else {
				player.sendPackets(new S_NPCTalkReturn(getId(), "endiaq4"));
			}
			break;
		case 71062:
			if (_master.equals(player)) {
				player.sendPackets(new S_NPCTalkReturn(getId(), "kamit2"));
			} else {
				player.sendPackets(new S_NPCTalkReturn(getId(), "kamit1"));
			}
			break;
		case 71075:
			if (_master.equals(player)) {
				player.sendPackets(new S_NPCTalkReturn(getId(), "llizard2"));
			} else {
				player.sendPackets(new S_NPCTalkReturn(getId(), "llizard1a"));
			}
			break;
		case 70957:
			if (_master.equals(player)) {
				player.sendPackets(new S_NPCTalkReturn(getId(), "roi2"));
			} else {
				player.sendPackets(new S_NPCTalkReturn(getId(), "roi2"));
			}
			break;
		}

	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.getNearObjects().addKnownObject(this);
		perceivedFrom.sendPackets(new S_FollowerPack(this, perceivedFrom));
	}

	private void createNewItem(L1PcInstance pc, int item_id, int count) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		item.setCount(count);
		if (item != null) {
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else {
				L1World.getInstance().getInventory(pc.getX(), pc.getY(),
						pc.getMapId()).storeItem(item);
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
		}
	}

	public void spawn(int npcId, int X, int Y, int H, short Map) {
		L1Npc l1npc = NpcTable.getInstance().getTemplate(npcId);
		if (l1npc != null) {
			L1NpcInstance mob = null;
			try {
				String implementationName = l1npc.getImpl();
				Constructor<?> _constructor = Class.forName(
						(new StringBuilder()).append(
								"l1j.server.server.model.Instance.").append(
								implementationName).append("Instance")
								.toString()).getConstructors()[0];
				mob = (L1NpcInstance) _constructor
						.newInstance(new Object[] { l1npc });
				mob.setId(ObjectIdFactory.getInstance().nextId());
				mob.setX(X);
				mob.setY(Y);
				mob.setHomeX(X);
				mob.setHomeY(Y);
				mob.setMap(Map);
				mob.getMoveState().setHeading(H);
				L1World.getInstance().storeObject(mob);
				L1World.getInstance().addVisibleObject(mob);
				L1Object object = L1World.getInstance().findObject(mob.getId());
				L1QuestInstance newnpc = (L1QuestInstance) object;
				newnpc.onNpcAI();
				newnpc.getLight().turnOnOffLight();
				newnpc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // Ã¤ÆÃ °³½Ã
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
