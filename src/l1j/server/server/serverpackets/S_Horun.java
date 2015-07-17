package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_Horun extends ServerBasePacket {
	public S_Horun(int o, L1PcInstance pc) {

		int count = Scount(pc);
		int inCount = 0;
		for (int k = 0; k < count; k++) {
			if (!pc.isSkillMastery((k + 1))) {
				inCount++;
			}
		}
		writeC(Opcodes.S_OPCODE_HORUN);
		writeC(inCount);// 보여줄 리스트 갯수?
		writeC(0);
		for (int k = 0; k < count; k++) {
			if (!pc.isSkillMastery((k + 1))) {
				writeD(k);
			}
		}
		writeC(0);
	}

	public int Scount(L1PcInstance pc) {
		int RC = 0;
		switch (pc.getType()) {
		case 0:
			if (pc.getLevel() > 20 || pc.isGm()) {
				RC = 16;
			} else if (pc.getLevel() > 10) {
				RC = 8;
			}
			break;

		case 1:
			if (pc.getLevel() >= 50 || pc.isGm()) {
				RC = 8;
			}
			break;

		case 2:
			if (pc.getLevel() >= 24 || pc.isGm()) {
				RC = 23;
			} else if (pc.getLevel() >= 16) {
				RC = 16;
			} else if (pc.getLevel() >= 8) {
				RC = 8;
			}
			break;

		case 3: // WIZ
			if (pc.getLevel() >= 12 || pc.isGm()) {
				RC = 23;
			} else if (pc.getLevel() >= 8) {
				RC = 16;
			} else if (pc.getLevel() >= 4) {
				RC = 8;
			}
			break;

		case 4: // DE
			if (pc.getLevel() >= 24 || pc.isGm()) {
				RC = 16;
			} else if (pc.getLevel() >= 12) {
				RC = 8;
			}
			break;

		default:
			break;
		}
		return RC;
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return _S__1B_HORUN;
	}

	private static final String _S__1B_HORUN = "[S] S_Horun";
}
