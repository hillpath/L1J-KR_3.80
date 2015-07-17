
package l1j.server.server.serverpackets;

import java.util.logging.Logger;

import l1j.server.server.Opcodes;

public class S_GMMaps extends ServerBasePacket {

	private static final String S_GMMaps = "[C] S_GMMaps";

	private static Logger _log = Logger.getLogger(S_GMMaps.class.getName());

	private byte[] _byte = null;

	public S_GMMaps(int number) {
		buildPacket(number);
	}

	private void buildPacket(int number) {
		writeC(Opcodes.S_OPCODE_BOARDREAD);
		writeD(number);//넘버
		writeS("미소피아");//글쓴이?
		writeS("사냥터 이동 명령어");//날짜?
		writeS("");//제목?
		writeS("[오만의탑  이동]\n" +
				"　1 오만10층　 　 2 오만20층\n" +
				"　3 오만30층　 　 4 오만40층\n" +
				"　5 오만50층　 　 6 오만60층\n" +
				"　7 오만70층　 　 8 오만80층\n" +
				"　9 오만90층　   10 오만100층\n" +
				"\n" +
				"[상아탑 던전]\n" +
				"　11 상아탑4층   12 상아탑7층\n" +
				"\n" +
				"[라스타바드 던전]\n" +
				"　13 어둠결계     14 암흑결계\n" +
				"\n" +
		        "[4대 용리스트]\n" +
				"　15 발라카스　 16 안타라스\n" +
		        "　17 파푸리온　 18 린드비오르");
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_GMMaps;
	}
}

