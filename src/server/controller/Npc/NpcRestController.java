package server.controller.Npc;

import javolution.util.FastTable;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.ActionCodes;
import l1j.server.server.TimeController.WarTimeController;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1GuardianInstance;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.GeneralThreadPool;

public class NpcRestController implements Runnable {
	private static Logger _log = Logger.getLogger(NpcRestController.class
			.getName());

	private static NpcRestController _instance;

	private FastTable<L1NpcInstance> list;

	private FastTable<L1DoorInstance> DoorCloselist;

	private FastTable<L1GuardianInstance> Restorelist;

	private FastTable<L1EffectInstance> fwlist;

	public static NpcRestController getInstance() {
		if (_instance == null)
			_instance = new NpcRestController();
		return _instance;
	}

	public NpcRestController() {
		list = new FastTable<L1NpcInstance>();
		DoorCloselist = new FastTable<L1DoorInstance>();
		Restorelist = new FastTable<L1GuardianInstance>();
		fwlist = new FastTable<L1EffectInstance>();
		GeneralThreadPool.getInstance().execute(this);
	}

	private FastTable<L1NpcInstance> li = null;

	private FastTable<L1DoorInstance> Dli = null;

	private FastTable<L1GuardianInstance> Rli = null;

	private FastTable<L1EffectInstance> Fli = null;

	public void run() {
		int mpr = 0;
		while (true) {
			try {

				li = list;
				for (L1NpcInstance _npc : li) {
					if (_npc == null) {
						removeNpc(_npc);
						continue;
					}
					if (_npc.RestTime <= System.currentTimeMillis()) {
						_npc.setRest(false);
						removeNpc(_npc);
					}
				}
				Dli = DoorCloselist;
				for (L1DoorInstance _npc : Dli) {
					if (_npc == null || _npc._destroyed) {
						removeDoor(_npc);
						continue;
					}
					if (_npc.CloseTime <= System.currentTimeMillis()) {
						_npc.close();
						removeDoor(_npc);
					}
				}
				Rli = Restorelist;

				for (L1GuardianInstance _npc : Rli) {
					if (_npc == null) {
						removeGuardian(_npc);
						continue;
					}
					if (_npc.RestoreTime <= System.currentTimeMillis()) {
						if (_npc.getNpcTemplate().get_npcId() == 70848) { // ¿£Æ®
							if (!_npc.getInventory().checkItem(40506, 1)) {
								_npc.getInventory().storeItem(40506, 1);
							}
							if (!_npc.getInventory().checkItem(40507, 96)) {
								_npc.getInventory().storeItem(40507, 1);
							}
						}
						if (_npc.getNpcTemplate().get_npcId() == 70850) { // ÆÇ
							if (!_npc.getInventory().checkItem(40519, 60)) {
								_npc.getInventory().storeItem(40519, 1);
							}
						}
					}
				}
				Fli = fwlist;

				for (L1EffectInstance _npc : Fli) {
					if (_npc == null || _npc._destroyed) {
						removeFW(_npc);
						continue;
					}
					FW(_npc);
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "NpcRestController[]Error", e);
			} finally {
				try {
					mpr = 0;
					li = null;
					Dli = null;
					Rli = null;
					Fli = null;
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		}
	}

	public void addNpc(L1NpcInstance npc) {
		if (!list.contains(npc))
			list.add(npc);
	}

	public void removeNpc(L1NpcInstance npc) {
		if (list.contains(npc)) {
			list.remove(npc);
			if (npc != null)
				npc.RestTime = 0;
		}
	}

	public void addDoor(L1DoorInstance npc) {
		if (!DoorCloselist.contains(npc))
			DoorCloselist.add(npc);
	}

	public void removeDoor(L1DoorInstance npc) {
		if (DoorCloselist.contains(npc)) {
			DoorCloselist.remove(npc);
			if (npc != null)
				npc.CloseTime = 0;
		}
	}

	public void addGuardian(L1GuardianInstance npc) {
		if (!Restorelist.contains(npc))
			Restorelist.add(npc);
	}

	public void removeGuardian(L1GuardianInstance npc) {
		if (Restorelist.contains(npc)) {
			Restorelist.remove(npc);
			if (npc != null)
				npc.RestoreTime = 0;
		}
	}

	public void addFW(L1EffectInstance npc) {
		if (!fwlist.contains(npc))
			fwlist.add(npc);
	}

	public void removeFW(L1EffectInstance npc) {
		if (fwlist.contains(npc))
			fwlist.remove(npc);
	}

	public int getSize() {
		return list.size();
	}

	private L1PcInstance pc = null;

	private L1Magic magic = null;

	private L1MonsterInstance mob = null;

	private FastTable<L1Object> list2 = null;

	private boolean isNowWar = false;

	private int castleId = 0;

	private int damage = 0;

	private void FW(L1EffectInstance _effect) {
		try {
			list2 = L1World.getInstance().getVisibleObjects(_effect, 0);
			for (L1Object objects : list2) {
				if (objects == null)
					continue;
				if (objects instanceof L1PcInstance) {
					pc = (L1PcInstance) objects;
					if (pc.isDead()) {
						continue;
					}
					if (pc.getId() == _effect.CubePc().getId()) {
						continue;
					}
					if (CharPosUtil.getZoneType(pc) == 1) {
						isNowWar = false;
						castleId = L1CastleLocation.getCastleIdByArea(pc);
						if (castleId > 0) {
							isNowWar = WarTimeController.getInstance()
									.isNowWar(castleId);
						}
						if (!isNowWar) {
							continue;
						}
					}
					magic = new L1Magic(_effect, pc);
					damage = magic.calcPcFireWallDamage();
					if (damage == 0) {
						continue;
					}
					pc.sendPackets(new S_DoActionGFX(pc.getId(),
							ActionCodes.ACTION_Damage));
					Broadcaster.broadcastPacket(pc, new S_DoActionGFX(pc
							.getId(), ActionCodes.ACTION_Damage));
					pc.receiveDamage(_effect, damage, false);
				} else if (objects instanceof L1MonsterInstance) {
					mob = (L1MonsterInstance) objects;
					if (mob.isDead()) {
						continue;
					}
					magic = new L1Magic(_effect, mob);
					damage = magic.calcNpcFireWallDamage();
					if (damage == 0) {
						continue;
					}
					Broadcaster.broadcastPacket(mob, new S_DoActionGFX(mob
							.getId(), ActionCodes.ACTION_Damage));
					mob.receiveDamage(_effect, damage);
				}
			}
		} catch (Exception e) {
		} finally {
			pc = null;
			magic = null;
			mob = null;
			list2 = null;
			isNowWar = false;
			castleId = 0;
			damage = 0;
		}
	}

}