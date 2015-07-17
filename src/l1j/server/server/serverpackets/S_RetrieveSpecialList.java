package l1j.server.server.serverpackets;

import java.io.IOException;

import l1j.server.Warehouse.SpecialWarehouse;
import l1j.server.Warehouse.WarehouseManager;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;


public class S_RetrieveSpecialList extends ServerBasePacket {
	public S_RetrieveSpecialList(int objid, L1PcInstance pc) {
		if (pc.getInventory().getSize() < 180) {
			SpecialWarehouse warehouse = WarehouseManager.getInstance()
					.getSpecialWarehouse(pc.getName());
			int size = warehouse.getSize();
			if (size > 0) {
				writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
				writeD(objid);
				writeH(size);
				writeC(18);
				L1ItemInstance item = null;
				for (Object itemObject : warehouse.getItems()) {
					item = (L1ItemInstance) itemObject;
					writeD(item.getId());
					writeC(item.getItem().getType2());
					writeH(item.get_gfxid());
					writeC(item.getBless());
					writeD(item.getCount());
					writeC(item.isIdentified() ? 1 : 0);
					writeS(item.getViewName());
				}
				writeC(0);//?추가
			    writeD(pc.get_SpecialSize());//추가

			}		
			else {
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "noitemret")); 

			}
		} else {
			pc.sendPackets(new S_ServerMessage(263)); // \f1한사람의 캐릭터가 가지고 걸을 수 있는 아이템은 최대 180개까지입니다.
		}
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}
}
