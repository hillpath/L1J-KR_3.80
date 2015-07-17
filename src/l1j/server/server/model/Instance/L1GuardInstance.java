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

import java.util.Random;

import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.types.Point;

public class L1GuardInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;

	public void setTarget(L1PcInstance targetPlayer) {
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
	}

	@Override
	public boolean noTarget() {
		if (getNpcTemplate().get_npcId() == 777838
				|| getNpcTemplate().get_npcId() == 60503
				|| getNpcTemplate().get_npcId() == 60504) {
			Random random = new Random();
			int NewMoveX = 0;
			int NewMoveY = 0;
			int ReturnHome = random.nextInt(100);
			if (ReturnHome == 10) {
				NewMoveX = getHomeX();
				NewMoveY = getHomeY();
			} else {
				NewMoveX = getHomeX()
						+ (random.nextInt(30) - random.nextInt(30));
				NewMoveY = getHomeY()
						+ (random.nextInt(30) - random.nextInt(30));
			}
			int dir = moveDirection(NewMoveX, NewMoveY);
			if (dir != -1) {
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
			}
		} else if (getLocation().getTileLineDistance(
				new Point(getHomeX(), getHomeY())) > 0) {
			int dir = moveDirection(getHomeX(), getHomeY());
			if (dir != -1) {
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
			} else {
				teleport(getHomeX(), getHomeY(), 1);
			}

		} else {

			if (L1World.getInstance().getRecognizePlayer(this).size() == 0) {
				return true;
			}

			if (isDead()) {
				return true;
			}
		}
		return false;

	}

	public L1GuardInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onNpcAI() {
		if (getPassispeed() <= 0)
			return;
		if (isAiRunning()) {
			return;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onAction(L1PcInstance pc) {
		if (pc == null)
			return;
		if (!isDead()) {
			if (getCurrentHp() > 0) {
				L1Attack attack = new L1Attack(pc, this);
				if (attack.calcHit()) {
					attack.calcDamage();
					attack.addPcPoisonAttack(pc, this);
				}
				attack.action();
				attack.commit();
			} else {
				L1Attack attack = new L1Attack(pc, this);
				attack.calcHit();
				attack.action();
			}
		}
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		int npcid = getNpcTemplate().get_npcId();
		String htmlid = null;
		String[] htmldata = null;
		String clan_name = "";
		String pri_name = "";

		if (talking != null) {
			switch (npcid) {
			case 4707000:
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() == L1CastleLocation.KENT_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "ktguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				break;
			case 60560:
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() == L1CastleLocation.OT_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "orcguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				break;
			case 60552:
			case 4711000:
			case 4711001:
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() == L1CastleLocation.WW_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "wdguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				break;
			case 60524:
			case 60525:
			case 60529:
			case 4708001:
			case 4708000:
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() == L1CastleLocation.GIRAN_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "grguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				break;
			case 70857:
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() == L1CastleLocation.HEINE_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "heguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				break;
			case 60530:
			case 60531:
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() == L1CastleLocation.DOWA_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "dcguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				break;
			case 60533:
			case 60534:
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() == L1CastleLocation.ADEN_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "adguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				break;
			case 81156:
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() == L1CastleLocation.DIAD_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "ktguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				break;
			}

			if (htmlid != null) {
				if (htmldata != null) {
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid,
							htmldata));
				} else {
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
				}
			} else {
				if (player.getLawful() < -1000) {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		}
	}

	public void onFinalAction() {

	}

	public void doFinalAction() {

	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) {
		if (attacker == null)
			return;
		if (getCurrentHp() > 0 && !isDead()) {
			if (damage >= 0) {
				if (!(attacker instanceof L1EffectInstance)) {
					setHate(attacker, damage);
				}
			}
			if (damage > 0) {
				if (getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.FOG_OF_SLEEPING)) {
					getSkillEffectTimerSet().removeSkillEffect(
							L1SkillId.FOG_OF_SLEEPING);
				} else if (getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.PHANTASM)) {
					getSkillEffectTimerSet().removeSkillEffect(
							L1SkillId.PHANTASM);
				}
			}

			onNpcAI();

			if (attacker instanceof L1PcInstance && damage > 0) {
				L1PcInstance pc = (L1PcInstance) attacker;
				pc.setPetTarget(this);
			}

			int newHp = getCurrentHp() - damage;
			if (newHp <= 0 && !isDead()) {
				setCurrentHp(0);
				setDead(true);
				setActionStatus(ActionCodes.ACTION_Die);
				death();
				// Death death = new Death(attacker);
				// GeneralThreadPool.getInstance().execute(death);
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
			}
		} else if (getCurrentHp() <= 0 || !isDead()) {
			setDead(true);
			setActionStatus(ActionCodes.ACTION_Die);
			death();
			// Death death = new Death(attacker);
			// GeneralThreadPool.getInstance().execute(death);
		}
	}

	@Override
	public void checkTarget() {
		// System.out.println("오버라이드 했는데 여기 진입할까?");
		if (_target == null
				|| (Math.abs(this.getX() - this.getHomeX())) > 20
				|| (Math.abs(this.getY() - this.getHomeY())) > 20
				|| _target.getMapId() != getMapId()
				|| _target.getCurrentHp() <= 0
				|| _target.isDead()
				|| (_target.isInvisble() && !getNpcTemplate().is_agrocoi() && !_hateList
						.containsKey(_target))) {
			if (_target != null) {
				tagertClear();
				teleport(getHomeX(), getHomeY(), getSpawn().getHeading());

			}
			if (!_hateList.isEmpty()) {
				_target = _hateList.getMaxHateCharacter();
				checkTarget();
			}

		}
	}

	public void death() {
		try {
			setDeathProcessing(true);
			setCurrentHp(0);
			setDead(true);
			setActionStatus(ActionCodes.ACTION_Die);

			getMap().setPassable(getLocation(), true);

			Broadcaster.broadcastPacket(L1GuardInstance.this,
					new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));

			startChat(CHAT_TIMING_DEAD);

			setDeathProcessing(false);

			allTargetClear();

			startDeleteTimer();
		} catch (Exception e) {
		}
	}

	class Death implements Runnable {
		L1Character _lastAttacker;

		public Death(L1Character lastAttacker) {
			_lastAttacker = lastAttacker;
		}

		public void run() {
			setDeathProcessing(true);
			setCurrentHp(0);
			setDead(true);
			setActionStatus(ActionCodes.ACTION_Die);

			getMap().setPassable(getLocation(), true);

			Broadcaster.broadcastPacket(L1GuardInstance.this,
					new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));

			startChat(CHAT_TIMING_DEAD);

			setDeathProcessing(false);

			allTargetClear();

			startDeleteTimer();
		}
	}
}
