package l1j.server.server.serverpackets;//드래곤진주

import java.util.logging.Logger;

import l1j.server.server.Opcodes;

public class S_DRAGONPERL extends ServerBasePacket {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(S_DRAGONPERL.class.getName());

	private static final String S_DRAGONPERL = "[S] S_DRAGONPERL";
	private byte[] _byte = null;

	public S_DRAGONPERL(int i, int type) {
		writeC(Opcodes.S_OPCODE_DRAGONPERL);
		writeD(i); // 케릭터 객체 아이디
		writeC(type); // 1~7 술취한 효과 8 드진
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}

		return _byte;
	}

	@Override
	public String getType() {
		return S_DRAGONPERL;
	}
}
