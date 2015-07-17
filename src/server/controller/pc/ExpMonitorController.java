package server.controller.pc;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Lawful;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SPMR;

public class ExpMonitorController implements Runnable {
	private static Logger _log = Logger.getLogger(ExpMonitorController.class
			.getName());

	private static ExpMonitorController _instance;

	public static ExpMonitorController getInstance() {
		if (_instance == null) {
			_instance = new ExpMonitorController();
		}
		return _instance;
	}

	public ExpMonitorController() {
		GeneralThreadPool.getInstance().execute(this);
	}

	private Collection<L1PcInstance> list = null;

	public void run() {
		while (true) {
			try {
				list = L1World.getInstance().getAllPlayers();
				for (L1PcInstance pc : list) {
					if (pc == null 
							|| pc.getNetConnection() == null
							|| pc.getRobotAi() != null
							|| pc.isPrivateShop()) {
						continue;
					} else {
						if (pc.getold_lawful() != pc.getLawful()) {
							pc.setold_lawful(pc.getLawful());
							S_Lawful s_lawful = new S_Lawful(pc.getId(), pc
									.getold_lawful());

							pc.sendPackets(s_lawful);

							Broadcaster.broadcastPacket(pc, s_lawful);

							/***************************************************
							 * (본섭) 바포메트 서버에 업데이트된 성향치에 따른 AC 및 MR 적용
							 **************************************************/
							LawfulBonus(pc);
						}

						if (pc.getold_exp() != pc.getExp()) {
							pc.setold_exp(pc.getExp());
							pc.onChangeExp();
						}
					}

				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, "ExpMonitorController[]Error", e);

			} finally {
				try {
					list = null;
					Thread.sleep(500);
				} catch (Exception e) {
				}
			}
		}
	}

	private void LawfulBonus(L1PcInstance pc) {
		int ACvalue = 0;
		int MRvalue = 0;
		int SPvalue = 0;
		int ATvalue = 0;
		int bapo = 0;
		if (pc.getLawful() >= 30000 && pc.getLawful() <= 32768) {
			ACvalue = -6;
			MRvalue = 9;
			pc.setOBapoLevell(pc.getNBapoLevel());
			bapo = 2;
			pc.setNBapoLevell(bapo);
		} else if (pc.getLawful() >= 20000 && pc.getLawful() <= 29999) {
			ACvalue = -4;
			MRvalue = 6;
			pc.setOBapoLevell(pc.getNBapoLevel());
			bapo = 1;
			pc.setNBapoLevell(bapo);
		} else if (pc.getLawful() >= 10000 && pc.getLawful() <= 19999) {
			ACvalue = -2;
			MRvalue = 3;
			pc.setOBapoLevell(pc.getNBapoLevel());
			bapo = 0;
			pc.setNBapoLevell(bapo);
		} else if (pc.getLawful() >= -9999 && pc.getLawful() <= 9999) {
			SPvalue = 0;
			ATvalue = 0;
			ACvalue = 0;
			MRvalue = 0;
			pc.setOBapoLevell(pc.getNBapoLevel());
			bapo = 7;
			pc.setNBapoLevell(bapo);
		} else if (pc.getLawful() <= -10000 && pc.getLawful() >= -19999) {
			SPvalue = 1;
			ATvalue = 1;
			pc.setOBapoLevell(pc.getNBapoLevel());
			bapo = 3;
			pc.setNBapoLevell(bapo);
		} else if (pc.getLawful() <= -20000 && pc.getLawful() >= -29999) {
			SPvalue = 2;
			ATvalue = 3;
			pc.setOBapoLevell(pc.getNBapoLevel());
			bapo = 4;
			pc.setNBapoLevell(bapo);
		} else if (pc.getLawful() <= -30000 && pc.getLawful() >= -32768) {
			SPvalue = 3;
			ATvalue = 5;
			pc.setOBapoLevell(pc.getNBapoLevel());
			bapo = 5;
			pc.setNBapoLevell(bapo);
		}

		if (pc.getOBapoLevel() != pc.getNBapoLevel()) {

			pc.sendPackets(new S_PacketBox(S_PacketBox.BAPO,
					pc.getOBapoLevel(), false));
			pc.sendPackets(new S_PacketBox(S_PacketBox.BAPO,
					pc.getNBapoLevel(), true));
			pc.setOBapoLevell(pc.getNBapoLevel());
			if (pc.getLevel() <= 50) {
				pc.sendPackets(new S_PacketBox(S_PacketBox.BAPO, 6, true));
			} else {
				pc.sendPackets(new S_PacketBox(S_PacketBox.BAPO, 6, false));
			}
		}

		if (ACvalue != 0 && MRvalue != 0) {
			if (ACvalue != pc.LawfulAC) {
				if (pc.LawfulAC != 0) {
					pc.getAC().addAc(pc.LawfulAC * -1);
				}
				pc.LawfulAC = ACvalue;
				pc.getAC().addAc(ACvalue);
				pc.sendPackets(new S_OwnCharStatus(pc));
			}
			if (MRvalue != pc.LawfulMR) {
				if (pc.LawfulMR != 0) {
					pc.getResistance().addMr(pc.LawfulMR * -1);
				}
				pc.LawfulMR = MRvalue;
				pc.getResistance().addMr(MRvalue);
				pc.sendPackets(new S_SPMR(pc));
			}
			if (pc.LawfulSP != 0) {
				pc.getAbility().addSp(pc.LawfulSP * -1);
				pc.LawfulSP = 0;
				pc.sendPackets(new S_SPMR(pc));
			}

		} else {
			if (pc.LawfulAC != 0) {
				pc.getAC().addAc(pc.LawfulAC * -1);
				pc.LawfulAC = 0;
				pc.sendPackets(new S_OwnCharStatus(pc));
			}
			if (pc.LawfulMR != 0) {
				pc.getResistance().addMr(pc.LawfulMR * -1);
				pc.LawfulMR = 0;
				pc.sendPackets(new S_SPMR(pc));
			}

			if (ATvalue != 0) {
				if (pc.LawfulAT != 0) {
					pc.setBapodmg(pc.LawfulAT * -1);
				}
				pc.LawfulAT = ATvalue;
				pc.setBapodmg(ATvalue);
				pc.sendPackets(new S_OwnCharStatus(pc));
			} else if (pc.LawfulSP != 0) {
				pc.setBapodmg(pc.LawfulAT * -1);
				pc.LawfulAT = 0;
				pc.sendPackets(new S_OwnCharStatus(pc));
			}

			if (SPvalue != 0) {
				if (pc.LawfulSP != 0) {
					pc.getAbility().addSp(pc.LawfulSP * -1);
				}
				pc.LawfulSP = SPvalue;
				pc.getAbility().addSp(SPvalue);
				pc.sendPackets(new S_SPMR(pc));
			} else if (pc.LawfulSP != 0) {
				pc.getAbility().addSp(pc.LawfulSP * -1);
				pc.LawfulSP = 0;
				pc.sendPackets(new S_SPMR(pc));
			}
		}

	}

}
