package l1j.server.server.clientpackets;

import server.LineageClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_HotelEnter extends ClientBasePacket {

	private static final String C_ENTER_PORTAL = "[C] C_EnterPortal";

	public C_HotelEnter(byte abyte0[], LineageClient client) throws Exception {
		super(abyte0);
	}

	@Override
	public String getType() {
		return C_ENTER_PORTAL;
	}
}