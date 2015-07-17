package l1j.server.server.serverpackets;
 
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
public class S_MapTimer extends ServerBasePacket {
 private static final String S_MAPTIMER = "[S] S_MapTimer";
 public S_MapTimer(L1PcInstance pc){
  buildPacket(pc);
 }
 public void buildPacket(L1PcInstance pc) {
  int entertime = pc.getGdungeonTime() % 1000;
  int IvoryTower = pc.getTkddkdungeonTime() % 1000; 
  int DragonDg = pc.getLdungeonTime() % 1000;
  int DragonDg1 = pc.getDdungeonTime() % 1000;
  
  int a = 180 - entertime;
  int c = 60 - IvoryTower;
  int d = 180 - DragonDg;  
  int e = 120 - DragonDg1; 
  
  writeC(Opcodes.S_OPCODE_PACKETBOX);
  writeC(S_PacketBox.MAP_TIMER_OUT);
	writeD(4);
	writeD(1);
	writeS("$12125");
	writeD(a);
	writeD(2);
	writeS("$6081");
	writeD(c);
	writeD(3);
	writeS("$12126");
	writeD(d);
	writeD(6);
	writeS("$14250");
	writeD(e);
 }
 @Override
 public byte[] getContent() {
  return _bao.toByteArray();
 }
 @Override 
 public String getType() {
  return S_MAPTIMER;
 }
}