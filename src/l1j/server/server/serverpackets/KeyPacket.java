package l1j.server.server.serverpackets;

public class KeyPacket extends ServerBasePacket {
	private byte[] _byte = null;
	
	/** 3.80c 옵코드 적용 물개 **/
	
	public KeyPacket() {
		
		byte[] _byte1 = 
		{      (byte) 0x96, // id
			    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // key     
			       (byte) 0x9d, (byte) 0xd1, (byte) 0xd6, (byte) 0x7a, (byte) 0xf4, 
			       (byte) 0x62, (byte) 0xe7, (byte) 0xa0, (byte) 0x66, (byte) 0x02, 
			       (byte) 0xfa };  

		
		for (int i = 0; i < _byte1.length; i++) {
			writeC(_byte1[i]);
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}
}
