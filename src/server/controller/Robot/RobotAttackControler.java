package server.controller.Robot;

import javolution.util.FastTable;


import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Node;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1RobotAstar;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_SkillSound;

public class RobotAttackControler extends Thread {
	public static boolean _on = true;
	private FastTable<L1PcInstance> list;
	private long curtime = 0;
	private L1RobotAstar aStar;
	private L1Node tail;
	private int iCurrentPath;
	private int[][] iPath;

	public RobotAttackControler() {
		list = new FastTable<L1PcInstance>();
		aStar = new L1RobotAstar();
		iPath = new int[300][2];
		start();
	}

	public void run() {
		L1PcInstance[] temp = null;
		int size = 0;
		try {
			while (_on) {
				try {
					synchronized (list) {
						if ((size = list.size()) > 0) {
							temp = (L1PcInstance[]) list
									.toArray(new L1PcInstance[list.size()]);
						}
					}
					if (size > 0) {
						curtime = System.currentTimeMillis();
						for (L1PcInstance pc : temp) {
							if (pc == null || !pc.noPlayerCK) {
								synchronized (list) {
									if (list.contains(pc))
										list.remove(pc);
								}
								continue;
							}
							if (pc.isDead()) {
								synchronized (list) {
									if (list.contains(pc))
										list.remove(pc);
								}
								pc.RobotAIType = L1PcInstance.RobotRandomMove;
								continue;
							}
							if (CurseCK(pc))
								continue;
							if (curtime > pc.RobotSleepTime) {
								L1Character cha = pc.RobotTarget;
								int lange = 1;
								if (cha != null) {
									if (cha.isDead() || yadCk(pc, cha)) {
										pc.RobotTarget = null;
										if (cha instanceof L1PcInstance) {
											pc.removeRobotTargetlist(cha);
										}
									} else {
										if (cha instanceof L1MonsterInstance) {
											L1MonsterInstance mon = (L1MonsterInstance) cha;
											if (mon.getHiddenStatus() != L1MonsterInstance.HIDDEN_STATUS_NONE) {
												pc.RobotTarget = null;
												continue;
											}
										} else if (cha instanceof L1PcInstance) {
											if (cha.isInvisble()) {
												pc.RobotTarget = null;
												continue;
											}
										}
										if (pc.isElf())
											lange = 8;
										else if (pc.isDragonknight())
											lange = 2;

										if (CharPosUtil.isAttackPosition(pc,
												cha.getX(), cha.getY(), lange)) {
											pc.getMoveState()
													.setHeading(
															CharPosUtil
																	.targetDirection(
																			pc,
																			cha.getX(),
																			cha.getY()));
											try {
												attackTarget(pc, cha);
											} catch (Exception e) {
											}
											pc.RobotTargetMoveCount = 6;
										} else {
											move(pc, cha);
											if (pc.RobotTargetMoveCount <= 0) {
												teleport(pc);
												pc.RobotTargetMoveCount = 6;
											} else if (pc.RobotTargetMoveCount == 3) {
												pc.RobotTarget = targetSerch(pc);
												pc.RobotTargetMoveCount--;
											} else
												pc.RobotTargetMoveCount--;
										}
									}
								} else {
									pc.RobotTarget = targetSerch(pc);
									if (pc.RobotTarget == null) {
										removeList(pc);
										pc.RobotAIType = L1PcInstance.RobotRandomMove;
										if (!(getLineDistance(33437, 32804,
												pc.getX(), pc.getY()) <= 3 && pc
												.getMapId() == 4)) {
											teleport(pc);
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {
					System.out
							.println("- RobotRandomMoveControler Thread While Exception Error -");
					e.printStackTrace();
				} finally {
					try {
						sleep(80L);
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			System.out
					.println("- RobotRandomMoveControler Thread Exception Error -");
			e.printStackTrace();
		} finally {
			System.out.println("- RobotRandomMoveControler Thread Restart -");
			new RobotAttackControler();
		}
	}

	public boolean get(L1PcInstance pc) {
		return list.contains(pc);
	}

	public void addList(L1PcInstance pc) {
		if (list == null)
			return;
		synchronized (list) {
			if (!list.contains(pc)) {
				pc.RobotAIType = L1PcInstance.RobotRun;
				list.add(pc);
			}
		}
	}

	public void removeList(L1PcInstance pc) {
		if (list == null)
			return;
		synchronized (list) {
			if (list.contains(pc)) {
				list.remove(pc);
			}
		}
	}

	public int size() {
		return list.size();
	}

	private L1Character targetSerch(L1PcInstance pc) {
		L1Character target = null;
		double rangeck = 100;
		FastTable<L1Character> charlist = new FastTable<L1Character>();
		FastTable<L1Object> ml = L1World.getInstance().getVisibleObjects(pc);
		for (L1Object obj : ml) {
			if (obj == null)
				continue;
			if (obj instanceof L1MonsterInstance) {
				L1MonsterInstance mon = (L1MonsterInstance) obj;
				if (mon == null || mon.isDead() || yadCk(pc, mon))
					continue;
				if (mon.getNpcTemplate().get_npcId() >= 7000007
						&& mon.getNpcTemplate().get_npcId() <= 7000016)
					continue;
				if (!charlist.contains(mon))
					charlist.add(mon);
			}/*
			 * else if(obj instanceof L1PcInstance ){ L1PcInstance tempPc =
			 * (L1PcInstance) obj; if(tempPc.isInvisble()) continue;
			 * if(pc.getClanid() != 0 && tempPc.getClanid() != pc.getClanid()){
			 * if (CharPosUtil.getZoneType(pc) != 1 &&
			 * CharPosUtil.getZoneType(tempPc) != 1) {
			 * if(!charlist.contains(tempPc)) charlist.add(tempPc); } } }
			 */// 로봇선어택
	}		
		L1PcInstance[] pclist = (L1PcInstance[]) pc.getRobotTargetlist()
				.toArray(new L1PcInstance[pc.getRobotTargetlist().size()]);
		for (L1PcInstance epc : pclist) {
			if (epc == null)
				continue;
			if (epc.isInvisble() || epc.isDead() || yadCk(pc, epc)
					|| CharPosUtil.getZoneType(pc) == 1
					|| CharPosUtil.getZoneType(epc) == 1) {
				pc.removeRobotTargetlist(epc);
			} else {
				if (!charlist.contains(epc))
					charlist.add(epc);
			}
		}
		L1Character[] list = (L1Character[]) charlist
				.toArray(new L1Character[charlist.size()]);
		boolean isPc = false;
		for (L1Character obj : list) {
			if (obj == null || obj.isDead())
				continue;
			if (!isPc && obj instanceof L1PcInstance) {
				isPc = true;
				rangeck = 100;
			}
			if (obj instanceof L1MonsterInstance) {
				L1MonsterInstance mon = (L1MonsterInstance) obj;
				if (isPc
						|| mon.getHiddenStatus() != L1MonsterInstance.HIDDEN_STATUS_NONE)
					continue;
			}

			double r = getLineDistance(obj.getX(), obj.getY(), pc.getX(),
					pc.getY());
			if (r < rangeck) {
				target = obj;
				rangeck = r;
			}
		}

		if (target != null
				&& !CharPosUtil.glanceCheck(pc, target.getX(), target.getY())) {
			isPc = false;
			rangeck = 100;
			for (L1Character obj : list) {
				if (obj == null || obj.isDead())
					continue;
				if (!CharPosUtil.glanceCheck(pc, obj.getX(), obj.getY()))
					continue;
				if (!isPc && obj instanceof L1PcInstance) {
					isPc = true;
					rangeck = 100;
				}
				if (obj instanceof L1MonsterInstance) {
					L1MonsterInstance mon = (L1MonsterInstance) obj;
					if (isPc
							|| mon.getHiddenStatus() != L1MonsterInstance.HIDDEN_STATUS_NONE)
						continue;
				}

				double r = getLineDistance(obj.getX(), obj.getY(), pc.getX(),
						pc.getY());
				if (r < rangeck) {
					target = obj;
					rangeck = r;
				}
			}
		}
		return target;
	}

	private void move(L1PcInstance pc, L1Character mon) {
		try {
			aStar.ResetPath();
			tail = aStar.FindPath(pc, mon.getX(), mon.getY(), mon.getMapId());
			if (tail != null) {
				iCurrentPath = -1;
				while (tail != null) {
					if (tail.x == pc.getX() && tail.y == pc.getY())
						break;
					iPath[++iCurrentPath][0] = tail.x;
					iPath[iCurrentPath][1] = tail.y;
					tail = tail.prev;
				}
				int dir = checkObject2(
						pc,
						pc.getX(),
						pc.getY(),
						pc.getMapId(),
						heading(pc.getX(), pc.getY(), iPath[iCurrentPath][0],
								iPath[iCurrentPath][1]));
				if (dir != -1)
					setDirectionMove(pc, dir);
			}
		} catch (Exception e) {
		}
		pc.RobotSleepTime = calcSleepTime(pc, 640) + System.currentTimeMillis();
	}

	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	public void setDirectionMove(L1PcInstance pc, int dir) {
		if (dir >= 0) {
			int nx = 0;
			int ny = 0;

			int heading = 0;
			nx = HEADING_TABLE_X[dir];
			ny = HEADING_TABLE_Y[dir];
			heading = dir;
			pc.getMap().setPassable(pc.getLocation(), true);
			pc.getMoveState().setHeading(heading);

			int nnx = pc.getX() + nx;
			int nny = pc.getY() + ny;
			pc.setX(nnx);
			pc.setY(nny);
			Broadcaster.broadcastPacket(pc, new S_MoveCharPacket(pc));
			pc.getMap().setPassable(pc.getLocation(), false);
		}
	}

	public double getLineDistance(int tx, int ty, int x, int y) {
		long diffX = tx - x;
		long diffY = ty - y;
		return Math.sqrt((diffX * diffX) + (diffY * diffY));
	}

	private int heading(int myx, int myy, int tx, int ty) {
		if (tx > myx && ty > myy) {
			return 3;
		} else if (tx < myx && ty < myy) {
			return 7;
		} else if (tx > myx && ty == myy) {
			return 2;
		} else if (tx < myx && ty == myy) {
			return 6;
		} else if (tx == myx && ty < myy) {
			return 0;
		} else if (tx == myx && ty > myy) {
			return 4;
		} else if (tx < myx && ty > myy) {
			return 5;
		} else {
			return 1;
		}
	}

	private static boolean yadCk(L1PcInstance pc, L1Object mon) {
		int escapeDistance = 15;
		int calcx = (int) pc.getLocation().getX() - mon.getLocation().getX();
		int calcy = (int) pc.getLocation().getY() - mon.getLocation().getY();
		return mon.getMapId() != pc.getMapId()
				|| Math.abs(calcx) > escapeDistance
				|| Math.abs(calcy) > escapeDistance;
	}

	public void attackTarget(L1PcInstance pc, L1Character target) {
		if (pc == null || target == null)
			return;
		if (target instanceof L1PcInstance) {
			L1PcInstance player = (L1PcInstance) target;
			if (player.isTeleport()) {
				return;
			}
		} else if (target instanceof L1PetInstance) {
			L1PetInstance pet = (L1PetInstance) target;
			L1Character cha = pet.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) {
					return;
				}
			}
		} else if (target instanceof L1SummonInstance) {
			L1SummonInstance summon = (L1SummonInstance) target;
			L1Character cha = summon.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) {
					return;
				}
			}
		}

		boolean isCounterBarrier = false;
		boolean isMortalBody = false;
		L1Attack attack = new L1Attack(pc, target);
		if (attack.calcHit()) {
			if (target.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.COUNTER_BARRIER)) {
				L1Magic magic = new L1Magic(target, pc);
				boolean isProbability = magic
						.calcProbabilityMagic(L1SkillId.COUNTER_BARRIER);
				boolean isShortDistance = attack.isShortDistance();
				if (isProbability && isShortDistance) {
					isCounterBarrier = true;
				}
			} else if (target.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.MORTAL_BODY)) {
				L1Magic magic = new L1Magic(target, pc);
				boolean isProbability = magic
						.calcProbabilityMagic(L1SkillId.MORTAL_BODY);
				boolean isShortDistance = attack.isShortDistance();
				if (isProbability && isShortDistance) {
					isMortalBody = true;
				}
			}
			if (!isCounterBarrier && !isMortalBody) {
				attack.calcDamage();
			}

			if (target instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) target;
				if (player.noPlayerCK)
					player.addRobotTargetlist(pc);
			}
		}
		if (isCounterBarrier) {
			attack.actionCounterBarrier();
			attack.commitCounterBarrier();
		} else if (isMortalBody) {
			attack.actionMortalBody();
			attack.commitMortalBody();
		} else {
			attack.action();
			attack.commit();
		}
		if (pc.isIllusionist()) {
			Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7049));
		}

		pc.RobotSleepTime = AttackTime(pc) + +System.currentTimeMillis();
	}

	private int AttackTime(L1PcInstance pc) {// 칼질속도조절 초반640
		int value = 740;

		value = calcSleepTime(pc, value);
		return value;
	}

	protected int calcSleepTime(L1PcInstance pc, int sleepTime) {
		if (pc == null)
			return sleepTime;
		switch (pc.getMoveState().getMoveSpeed()) {
		case 0:
			break;
		case 1:
			sleepTime -= (sleepTime * 0.25);
			break;
		case 2:
			sleepTime *= 2;
			break;
		}
		if (pc.getMoveState().getBraveSpeed() == 1) {
			sleepTime -= (sleepTime * 0.25);
		}
		return sleepTime;
	}

	private static boolean ObjectCk(L1PcInstance pc, int dir) {
		FastTable<L1Object> ol = L1World.getInstance().getVisibleObjects(pc);
		int size = ol.size();
		if (size <= 0)
			return true;
		L1Object[] list = (L1Object[]) ol.toArray(new L1Object[ol.size()]);
		int nx = pc.getX() + HEADING_TABLE_X[dir];
		int ny = pc.getY() + HEADING_TABLE_Y[dir];
		for (L1Object obj : list) {
			if (obj == null || yadCk(pc, obj))
				continue;
			if (obj instanceof L1Character) {
				L1Character cha = (L1Character) obj;
				if (cha.isDead())
					continue;
			}
			if (obj.getX() == nx && obj.getY() == ny
					&& pc.getMapId() == obj.getMapId())
				return false;
		}
		return true;
	}

	public static int checkObject2(L1PcInstance pc, int x, int y, short m, int d) {
		switch (d) {
		case 1:
			if (ObjectCk(pc, 1)) {
				return 1;
			} else if (ObjectCk(pc, 0)) {
				return 0;
			} else if (ObjectCk(pc, 2)) {
				return 2;
			}
			break;
		case 2:
			if (ObjectCk(pc, 2)) {
				return 2;
			} else if (ObjectCk(pc, 1)) {
				return 1;
			} else if (ObjectCk(pc, 3)) {
				return 3;
			}
			break;
		case 3:
			if (ObjectCk(pc, 3)) {
				return 3;
			} else if (ObjectCk(pc, 2)) {
				return 2;
			} else if (ObjectCk(pc, 4)) {
				return 4;
			}
			break;
		case 4:
			if (ObjectCk(pc, 4)) {
				return 4;
			} else if (ObjectCk(pc, 3)) {
				return 3;
			} else if (ObjectCk(pc, 5)) {
				return 5;
			}
			break;
		case 5:
			if (ObjectCk(pc, 5)) {
				return 5;
			} else if (ObjectCk(pc, 4)) {
				return 4;
			} else if (ObjectCk(pc, 6)) {
				return 6;
			}
			break;
		case 6:
			if (ObjectCk(pc, 6)) {
				return 6;
			} else if (ObjectCk(pc, 5)) {
				return 5;
			} else if (ObjectCk(pc, 7)) {
				return 7;
			}
			break;
		case 7:
			if (ObjectCk(pc, 7)) {
				return 7;
			} else if (ObjectCk(pc, 6)) {
				return 6;
			} else if (ObjectCk(pc, 0)) {
				return 0;
			}
			break;
		case 0:
			if (ObjectCk(pc, 0)) {
				return 0;
			} else if (ObjectCk(pc, 7)) {
				return 7;
			} else if (ObjectCk(pc, 1)) {
				return 1;
			}
			break;
		default:
			break;
		}
		return -1;
	}

	private boolean CurseCK(L1PcInstance pc) {
		/** 2011.04.18 고정수 페럴라이즈 상태시 안움직이게 */
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EARTH_BIND)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.SHOCK_STUN)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.ICE_LANCE)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.FREEZING_BLIZZARD)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.CURSE_PARALYZE)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.THUNDER_GRAB)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.BONE_BREAK)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.PHANTASM)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.PHANTASM)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.PHANTASM)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.PHANTASM)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.STATUS_FREEZE) || pc.isParalyzed()) {
			return true;
		}
		return false;
	}

	private void teleport(L1PcInstance pc) {
		if (pc.getMap().isTeleportable()) {
			L1Location newLocation = pc.getLocation().randomLocation(200, true);
			int newX = newLocation.getX();
			int newY = newLocation.getY();
			short mapId = (short) newLocation.getMapId();
			L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
		}
	}
}
