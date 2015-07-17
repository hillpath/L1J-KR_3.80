package l1j.server.server.clientpackets;

import java.util.logging.Logger;

import server.LineageClient;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.GameSystem.Papoo.*;
import l1j.server.GameSystem.Rind.RindRaidSystem;
import l1j.server.GameSystem.Antaras.*;

public class C_Frame extends ClientBasePacket {

	private static final String C_Frame = "[C] C_Frame";

	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(C_Report.class.getName());

	public C_Frame(byte abyte0[], LineageClient clientthread) throws Exception {
		super(abyte0);
		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null) {
			return;
		}
		int a = readC();
		int b = readD();
		int c = readCH();
		int type = readH();
		switch (c) {
		case 0:
			AntarasRaidSystem.getInstance().startRaid(pc);
			if (pc.isGm()) {
			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fU"+ pc.getName()+ "님께서 안타라스 드래곤 포탈을 소환했습니다."));
			}
			break;
		case 1 : 
			PaPooRaidSystem.getInstance().startRaid(pc);
			if (pc.isGm()) {
			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fU"+ pc.getName()+ "님께서 파푸리온 드래곤 포탈을 소환했습니다."));
			}
			break;
		case 2 ://린드레이드 
			RindRaidSystem.getInstance().startRaid(pc);
			if (pc.isGm()) {
			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fU"+ pc.getName()+ "님께서 린드비오르 드래곤 포탈을 소환했습니다."));
			}
			break;
		
		}
		
	}

	@Override
	public String getType() {
		return C_Frame;
	}
}
