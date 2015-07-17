package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket, S_SendInvOnLogin

public class S_Frame extends ServerBasePacket {

	private static final String S_Frame = "[S] S_Frame";

	/**
	 * 프레임을 띄운다
	 */
	// 언노우2 + 0x66 + 키오브젝트 + 안타 + 파푸 + 린드 + 발라
	public S_Frame(int key,int anta,int papoo,int lind,int bala) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(0x66);
		writeD(key);
		writeC(anta);
		writeC(papoo);
		writeC(lind);
		writeC(bala);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_Frame;
	}
}
