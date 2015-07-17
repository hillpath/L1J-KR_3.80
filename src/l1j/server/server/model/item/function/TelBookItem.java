package l1j.server.server.model.item.function;

import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1Item;


@SuppressWarnings("serial")
public class TelBookItem extends L1ItemInstance{
 public TelBookItem(L1Item item){ super(item); }

 @Override
 public void clickItem(L1Character cha, ClientBasePacket packet){ 
  if(cha instanceof L1PcInstance){
   L1PcInstance pc = (L1PcInstance)cha;
   L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
   int itemId = useItem.getItemId();
   int BookTel = packet.readH();
   switch(itemId){
   case 560025:
   case 560026:
   final int[][] TownAddBook = {
     { 34060, 32281, 4 },   // 오렌
     { 33079, 33390, 4 },   // 은기사
     { 32750, 32439, 4 },   // 오크숲
     { 32612, 33188, 4 },   // 윈다우드
     { 33720, 32492, 4 },   // 웰던
     { 32872, 32912, 304 }, // 침묵의 동굴
     { 32612, 32781, 4 },   // 글루디오
     { 33067, 32803, 4 },   // 켄트
     { 33933, 33358, 4 },   // 아덴
     { 33601, 33232, 4 },   // 하이네
     { 32574, 32942, 0 },   // 말하는 섬
     { 33430, 32815, 4 },}; // 기란
   int[] TownAddBookList = TownAddBook[BookTel];
   if(TownAddBookList != null){
    L1Teleport.teleport(pc, TownAddBookList[0], TownAddBookList[1], (short)TownAddBookList[2], 3, true);
    pc.getInventory().removeItem(useItem, 1);
   }
   break;
   case 560027:
    final int[][] DungeonAddBook = {
			{ 32791, 32800, 101 }, // 오만1
			{ 32764, 32842, 77 }, // 오렌3
			{ 32676, 32859, 59 }, // 에바왕국
			{ 32750, 32799, 49 }, // 개미굴
			{ 32549, 32801, 400 }, // 대공동
			{ 32925, 32804, 430 }, // 정무
			{ 32929, 32995, 410 }, // 마신
			{ 34267, 32189, 4 }, //  그신
			{ 32760, 33461, 4 }, // 욕망
			{ 32693, 32795, 450 }, // 라스타마드 마을
			{ 32843, 32693, 550 }, // 선박의 무던
	};
    int[] DungeonAddBookList = DungeonAddBook[BookTel];
    if(DungeonAddBookList != null){ 
     L1Teleport.teleport(pc, DungeonAddBookList[0], DungeonAddBookList[1], (short)DungeonAddBookList[2], 3, true); 
     pc.getInventory().removeItem(useItem, 1);
    }
    break;
   case 560028:
    final int[][] OmanTowerAddBook = {
      { 33766, 32863, 106 },    // 오만6
      { 32744, 32862, 116 },    // 오만16
      { 32741, 32854, 126 },    // 오만26
      { 32739, 32864, 136 },    // 오만36
      { 32735, 32869, 146 },    // 오만46
      { 32807, 32805, 156 },    // 오만56
      { 32736, 32802, 166 },    // 오만66
      { 32727, 32789, 176 },    // 오만76
      { 32734, 32794, 186 },    // 오만86
      { 32733, 32787, 196 },};  // 오만96
    int[] OmanTowerAddBookList = OmanTowerAddBook[BookTel];
    if(OmanTowerAddBookList != null){ 
     L1Teleport.teleport(pc, OmanTowerAddBookList[0], OmanTowerAddBookList[1], (short)OmanTowerAddBookList[2], 3, true); 
     pc.getInventory().removeItem(useItem, 1);
    }
    break;
    }
   }
  }
}

