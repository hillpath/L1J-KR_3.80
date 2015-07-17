package l1j.server.server.serverpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

public class S_LetterList extends ServerBasePacket{
	private static Logger _log = Logger.getLogger(S_Letter.class.getName());
	private static final String S_LETTERLIST = "[S] S_LetterList";
	private byte[] _byte = null;

	public S_LetterList(L1PcInstance pc, int type, int count) {
		buildPacket(pc,type,count);
	}
	private void buildPacket(L1PcInstance pc, int type, int count) {
		Connection con = null;
		PreparedStatement pstm = null;
		PreparedStatement pstm1 = null;

		ResultSet rs = null;
		ResultSet rs1 = null;
		int cnt = 0;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM letter WHERE receiver=? AND template_id = ? order by date limit ?  ");

			pstm.setString(1, pc.getName());
			pstm.setInt(2, type);
			pstm.setInt(3, count);
			rs = pstm.executeQuery();

			pstm1 = con.prepareStatement(" SELECT count(*) as cnt FROM letter WHERE receiver=? AND template_id = ? order by date limit ?  ");
			pstm1.setString(1, pc.getName());
			pstm1.setInt(2, type);
			pstm1.setInt(3, count);
			rs1 = pstm1.executeQuery();
			if (rs1.next()){
				cnt = rs1.getInt(1);
			}
			writeC(Opcodes.S_OPCODE_LETTER);
			writeC(type); // 0:메일함 1:혈맹메일함 2:보관함

			writeH(cnt);
			//writeH(count); //표현할 글 게수 (보관함 10 일반 편지 20 혈편지 50개)

			int day = 0;
			while (rs.next()) {
				writeD(rs.getInt(1));
				writeC(rs.getInt(9));
				try {
					Date Data = new SimpleDateFormat("yyyyMMdd").parse(rs.getString(5));
					Calendar Day = new GregorianCalendar();
					Day.setTime(Data);
					day = (int)(Day.getTimeInMillis()/1000);
				} catch (Exception e) {}
				writeD(day);
				writeC(0);
				writeS(rs.getString(3));
				writeSS(rs.getString(7));
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "S_LetterList[]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(rs1);
			SQLUtil.close(pstm);
			SQLUtil.close(pstm1);
			SQLUtil.close(con);
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
		return S_LETTERLIST;
	}
}
