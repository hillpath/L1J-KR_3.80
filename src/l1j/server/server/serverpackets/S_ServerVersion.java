package l1j.server.server.serverpackets;

import server.GameServer;

import l1j.server.Config;

import l1j.server.server.Opcodes;

public class S_ServerVersion extends ServerBasePacket {

 private static final int SERVER_NO = 0x01;

 private static final int CLIENT_LANGUAGE = Config.CLIENT_LANGUAGE;

 private static final int uptime = (int) (System.currentTimeMillis() / 1000);


	/** 3.80c 옵코드 적용 물개 **/




public S_ServerVersion() {

  writeC(Opcodes.S_OPCODE_SERVERVERSION);

  writeC(0x00);       // Auth ok?

  writeC(SERVER_NO);  // Server Id

  writeD(0x07cbf4dd); // server version 3.80C Taiwan Server

  writeD(0x07cbf4dd); // cache version 3.80C Taiwan Server

  writeD(0x77fc692d); // auth version 3.80C Taiwan Server

  writeD(0x07cbf4d9); // npc version 3.80C Taiwan Server

  writeD(GameServer.getInstance().startTime);

  writeC(0x00); // unknown

  writeC(0x00); // unknown

  writeC(CLIENT_LANGUAGE); // Country: 0.US 3.Taiwan 4.Janpan 5.China

  writeD(0x087f7dc2);      // Server Type

  writeD(uptime);          // Uptime

  writeH(0x01);

 }

 @Override

 public byte[] getContent() {

  return getBytes();

 }

}

