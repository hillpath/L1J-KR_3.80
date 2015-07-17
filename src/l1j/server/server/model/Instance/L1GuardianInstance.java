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

import javolution.util.FastTable;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Npc;
import server.controller.Npc.NpcRestController;

public class L1GuardianInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;

	private Random _random = new Random(System.nanoTime());

	public long RestoreTime = 0;

	public L1GuardianInstance(L1Npc template) {
		super(template);
		synchronized (this) {
			if (getNpcTemplate().get_npcId() == 70848
					|| getNpcTemplate().get_npcId() == 70850) { // 엔트
				RestoreTime = System.currentTimeMillis() + RESTORE_MILLISEC;
				NpcRestController.getInstance().addGuardian(this);
			}
		}
	}

	@Override
	public void searchTarget() {
		L1PcInstance targetPlayer = null;
		FastTable<L1PcInstance> list = null;
		list = L1World.getInstance().getVisiblePlayer(this);
		for (L1PcInstance pc : list) {
			if (pc == null || pc.getCurrentHp() <= 0 || pc.isDead()
					|| pc.isGm() || pc.isGhost()) {
				continue;
			}
			if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) { // 인비지체크
				if (!pc.isElf()) { // 요정이아니면
					targetPlayer = pc;
					Broadcaster.wideBroadcastPacket(this, new S_NpcChatPacket(
							this, "$804", 2)); // 그대여.
					// 목숨이
					// 아까우면
					// 빨리
					// 이곳을
					// 떠날지어다.
					// 이곳은
					// 그대같은
					// 자가
					// 더럽히지
					// 못할
					// 신성한
					// 곳이다.
					break;
				}
			}
		}
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
	}

	// 링크의 설정
	@Override
	public void setLink(L1Character cha) {
		if (cha != null && _hateList.isEmpty()) { // 타겟이 없는 경우만 추가
			_hateList.add(cha, 0);
			checkTarget();
		}
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onAction(L1PcInstance player) {
		if (this == null || player == null)
			return;
		if (player.isElf()) {
			L1Attack attack = new L1Attack(player, this);

			if (attack.calcHit()
					&& attack.calcDamage() <= 15
					&& (player.getGfxId().getTempCharGfx() == 37 || player
							.getGfxId().getTempCharGfx() == 138)) {
				if (getNpcTemplate().get_npcId() == 70848) { // 엔트
					int chance = _random.nextInt(100) + 1;
					if (getInventory().checkItem(40499)) { // 버포->껍질
						player.sendPackets(new S_ServerMessage(143, "$755",
								"$770"
										+ " ("
										+ getInventory().findItemId(40499)
												.getCount() + ")")); // \f1%0이%1를
						// 주었습니다
						player.getInventory().storeItem(40505,
								getInventory().findItemId(40499).getCount()); // 버섯포자
						getInventory().consumeItem(40499,
								getInventory().findItemId(40499).getCount());
					} else {
						if (getInventory().checkItem(40507, 6)) { // 엔트줄기
							if (chance <= 20) {
								player.getInventory().storeItem(40507, 6);
								getInventory().consumeItem(40507, 6);
								player.sendPackets(new S_ServerMessage(143,
										"$755", "$763" + " (" + 6 + ")")); // \f1%0이%1를
								// 주었습니다.
							}
						} else {
							if (getInventory().checkItem(40506, 1)) {
								if (chance <= 10) {
									getInventory().consumeItem(40506, 1);
									// 1개를 줘야 하지만 일단 편의상 5개로..
									player.getInventory().storeItem(40506, 5);
									player.sendPackets(new S_ServerMessage(143,
											"$755", "$794")); // \f1%0이%1를
									// 주었습니다.
								}
							} else {
								if (chance <= 40)
									Broadcaster
											.broadcastPacket(this,
													new S_NpcChatPacket(this,
															"$822", 0)); // ...지금.
								// 가지.
								// 껍질.
								// 없다.
								// 나중에.
								// 다시.
								// 와라.
							}
						}
					}
				}
				if (getNpcTemplate().get_npcId() == 70850) { // 판
					int chance = _random.nextInt(100) + 1;
					if (getInventory().checkItem(40519, 5)) {
						if (chance <= 20) {
							getInventory().consumeItem(40519, 5);
							player.getInventory().storeItem(40519, 5);
							player.sendPackets(new S_ServerMessage(143, "$753",
									"$760" + " (" + 5 + ")")); // \f1%0이%1를
							// 주었습니다.
						}
					} else {
						if (chance <= 40)
							Broadcaster.broadcastPacket(this,
									new S_NpcChatPacket(this, "$824", 0)); // 갈기털이
						// 남아나질
						// 않겠다!
						// 좀
						// 있다해!
					}
				}
				if (getNpcTemplate().get_npcId() == 70846) { // 아라크네
					if (getInventory().checkItem(40507, 2)) {
						getInventory().consumeItem(40507, 2);
						player.getInventory().storeItem(40503, 1);
						player.sendPackets(new S_ServerMessage(143, "$752",
								"$769")); // \f1%0이%1를
						// 주었습니다.
					}

				}
				attack.calcDamage();
				attack.addPcPoisonAttack(player, this);
			}
			attack.action();
			attack.commit();
		} else if (getCurrentHp() > 0 && !isDead()) {
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
	public void onTalkAction(L1PcInstance player) {
		if (player == null || this == null)
			return;
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		L1Object object = L1World.getInstance().findObject(getId());
		if (object == null)
			return;
		L1NpcInstance target = (L1NpcInstance) object;
		String htmlid = null;
		String[] htmldata = null;

		if (talking != null) {
			int pcx = player.getX(); // PC의 X좌표
			int pcy = player.getY(); // PC의 Y좌표
			int npcx = target.getX(); // NPC의 X좌표
			int npcy = target.getY(); // NPC의 Y좌표

			int heading = 0;
			if (pcx == npcx && pcy < npcy)
				heading = 0;
			else if (pcx > npcx && pcy < npcy)
				heading = 1;
			else if (pcx > npcx && pcy == npcy)
				heading = 2;
			else if (pcx > npcx && pcy > npcy)
				heading = 3;
			else if (pcx == npcx && pcy > npcy)
				heading = 4;
			else if (pcx < npcx && pcy > npcy)
				heading = 5;
			else if (pcx < npcx && pcy == npcy)
				heading = 6;
			else if (pcx < npcx && pcy < npcy)
				heading = 7;

			getMoveState().setHeading(heading);
			Broadcaster.broadcastPacket(this, new S_ChangeHeading(this));

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
			synchronized (this) {
				if (isRest()) {
					RestTime = System.currentTimeMillis() + REST_MILLISEC;
				} else {
					setRest(true);
					RestTime = System.currentTimeMillis() + REST_MILLISEC;
					NpcRestController.getInstance().addNpc(this);
				}
			}
		}
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) {
		if (this == null || attacker == null)
			return;
		if (attacker instanceof L1PcInstance && damage > 0) {
			L1PcInstance pc = (L1PcInstance) attacker;
			if (pc.getType() == 2 && pc.getCurrentWeapon() == 0) {
			} else {
				if (getCurrentHp() > 0 && !isDead()) {
					if (damage >= 0) {
						setHate(attacker, damage);
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
					serchLink(pc, getNpcTemplate().get_family());
					if (damage > 0) {
						pc.setPetTarget(this);
					}

					int newHp = getCurrentHp() - damage;
					if (newHp <= 0 && !isDead()) {
						setCurrentHp(0);
						setDead(true);
						setActionStatus(ActionCodes.ACTION_Die);
						_lastattacker = attacker;
						death();
						// Death death = new Death();
						// GeneralThreadPool.getInstance().execute(death);
					}
					if (newHp > 0) {
						setCurrentHp(newHp);
					}
				} else if (!isDead()) {
					setDead(true);
					setActionStatus(ActionCodes.ACTION_Die);
					_lastattacker = attacker;
					death();
					// Death death = new Death();
					// GeneralThreadPool.getInstance().execute(death);
				}
			}
		}
	}

	@Override
	public void setCurrentHp(int i) {
		super.setCurrentHp(i);

		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}
	}

	@Override
	public void setCurrentMp(int i) {
		super.setCurrentMp(i);

		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
	}

	private L1Character _lastattacker;

	class Death implements Runnable {
		L1Character lastAttacker = _lastattacker;

		public void run() {
			NpcRestController.getInstance().removeGuardian(
					L1GuardianInstance.this);
			setDeathProcessing(true);
			getInventory().clearItems();
			setCurrentHp(0);
			setDead(true);
			setActionStatus(ActionCodes.ACTION_Die);
			int targetobjid = getId();
			getMap().setPassable(getLocation(), true);
			Broadcaster.broadcastPacket(L1GuardianInstance.this,
					new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));

			setDeathProcessing(false);

			allTargetClear();

			startDeleteTimer();
		}
	}

	public void death() {
		try {
			NpcRestController.getInstance().removeGuardian(this);
			setDeathProcessing(true);
			getInventory().clearItems();
			setCurrentHp(0);
			setDead(true);
			setActionStatus(ActionCodes.ACTION_Die);
			int targetobjid = getId();
			getMap().setPassable(getLocation(), true);
			Broadcaster.broadcastPacket(L1GuardianInstance.this,
					new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
			setDeathProcessing(false);
			allTargetClear();
			startDeleteTimer();
		} catch (Exception e) {
		}
	}

	public void onGetItem(L1ItemInstance item) {
		refineItem();
		getInventory().shuffle();
		if (getNpcTemplate().get_digestitem() > 0) {
			setDigestItem(item);
		}
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
	}

	public void doFinalAction(L1PcInstance player) {
	}

	private static final long REST_MILLISEC = 10000;

	private static final long RESTORE_MILLISEC = 36000;

}
