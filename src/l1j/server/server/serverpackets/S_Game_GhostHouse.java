package l1j.server.server.serverpackets;

import l1j.server.GameSystem.GhostHouse;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_Game_GhostHouse extends ServerBasePacket {

	private static final String S_GameList = "[S] S_Game_HauntedHouse";

	private byte[] _byte = null;

	// 랭킹
	public S_Game_GhostHouse(int rankvalue) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(0x42);
		writeH(GhostHouse.getInstance().getPlayMembersCount()); // 참여자수
		writeH(rankvalue); // 등수
		for (L1PcInstance pc : GhostHouse.getInstance().getRank()) {
			writeS(pc.getName());
		}
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
