package l1j.server.server.serverpackets;

import java.util.logging.Logger;

import l1j.server.server.Opcodes;

public class S_GMLava extends ServerBasePacket {

	private static final String S_GMLava = "[C] S_GMLava";

	private static Logger _log = Logger.getLogger(S_GMLava.class.getName());

	private byte[] _byte = null;

	public S_GMLava(int number) {
		buildPacket(number);
	}

	private void buildPacket(int number) {
		writeC(Opcodes.S_OPCODE_BOARDREAD);
		writeD(number);//넘버
		writeS("미소피아");//글쓴이?
		writeS("라던 이동 명령어");//날짜?
		writeS("");//제목?
		writeS("[라스타바드 던전]\n" +
				"　1 돌격훈련장 19데빌용병실\n" +
				"　2 마수집무실 20지하결투장\n" +
				"　3 야수조교실 21지하처형장\n" +
				"　4 야수훈련실 22지하통제실\n" +
				"　5 마수소환실 23암살집무실\n" +
				"　6 흑마법훈련 24 지하의통로\n" +
				"　7 흑마법연구 25 지하훈련장\n" +
				"　8 마령집무실 26 출입금지\n" +
				"　9 마령의서재 27 지하결투장\n" +
				"　10 정령소환실 28 케이나의방\n" +
				"　11 정령생식지 29 엔디아스방\n" +
				"　12 정령연구실 30 비아타스방\n" +
				"　13 악령의제단 31 바로메스방\n" +
				"　14 데빌의제단 32 이데아의방\n" +
				"　15 용병훈련장 33 티아메스방\n" +
				"　16 명법훈련장 34 라미아스방\n" +
				"　17 명법집무실 35 바로드의방\n" +
				"　18 중앙통제실 36 재사장의방\n");
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_GMLava;
	}
}