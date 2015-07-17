package l1j.server.server.serverpackets;




import l1j.server.server.Opcodes;

import l1j.server.server.model.Instance.L1PcInstance;




/** 3.80c ¿ÉÄÚµå Àû¿ë ¹°°³ **/

public class S_Unknown1 extends ServerBasePacket {

public S_Unknown1(L1PcInstance pc) {

 writeC(Opcodes.S_OPCODE_UNKNOWN1);

 writeC(0x03);  // åÞÍ§

 if(pc.getClanid() > 0){

  writeD(pc.getClanMemberId());

 } else {

  writeC(0x53);

  writeC(0x01);

  writeC(0x00);

  writeC(0x8b);

 }

 writeC(0x9c);

 writeC(0x1f);

}

@Override

public byte[] getContent() {

 return getBytes();

}

}

