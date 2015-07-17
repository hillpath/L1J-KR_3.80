package l1j.server.server.serverpackets;

import l1j.server.GameSystem.PetRacing;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_Game_PetRacing extends ServerBasePacket {

	private static final String S_GameList = "S_Game_PetRacing";

	private byte[] _byte = null;

	// 랭킹
	public S_Game_PetRacing(int rankvalue) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(0x42);
		writeH(PetRacing.getInstance().getPlayMemberCount()); // 참여자수
		writeH(rankvalue); // 등수
		for (L1PcInstance pc : PetRacing.getInstance().getRank()) {
			writeS(pc.getName());
		}
	}

	// LAB 갱신
	public S_Game_PetRacing(int maxLab, int nowLab) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(0x43);
		writeH(maxLab); // 최대LAB
		writeH(nowLab); // 현재LAB

	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_GameList;
	}
}
