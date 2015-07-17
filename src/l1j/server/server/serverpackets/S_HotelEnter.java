package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_HotelEnter extends ServerBasePacket {

	public S_HotelEnter() {
		writeC(Opcodes.S_OPCODE_HOTELENTER);

	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
