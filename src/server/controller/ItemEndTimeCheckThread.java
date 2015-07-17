package server.controller;

import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class ItemEndTimeCheckThread extends Thread {
 private static ItemEndTimeCheckThread _instance = null;
 
 public static ItemEndTimeCheckThread getInstance() {
  if (_instance == null) {
   _instance = new ItemEndTimeCheckThread();
   _instance.start();
  }
  return _instance;
 }

 public ItemEndTimeCheckThread(){}
 
 public void run() {
  while (true) {
   long currentTimeMillis = System.currentTimeMillis();
   try {
    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
     if (pc == null) continue;
     L1Inventory pcInventory = pc.getInventory();
     for (L1ItemInstance item : pcInventory.getItems()) {
      if (item == null) continue;
      
      if (item.getEndTime() == null) continue;
      
      if (currentTimeMillis > item.getEndTime().getTime()) {
       pcInventory.removeItem(item);
       int itemId = item.getItem().getItemId();
       if(itemId >= L1ItemId.MOPO_JUiCE_CONTRACT1 && itemId <= L1ItemId.MOPO_JUiCE_CONTRACT6|| itemId == L1ItemId.KILLTON_CONTRACT || itemId == L1ItemId.MERIN_CONTRACT) {
        int Contract[] = { L1ItemId.MOPO_JUiCE_CONTRACT1, L1ItemId.MOPO_JUiCE_CONTRACT2, L1ItemId.MOPO_JUiCE_CONTRACT3, 
             L1ItemId.MOPO_JUiCE_CONTRACT4, L1ItemId.MOPO_JUiCE_CONTRACT5, L1ItemId.MOPO_JUiCE_CONTRACT6, L1ItemId.KILLTON_CONTRACT,
             L1ItemId.MERIN_CONTRACT};
        int Pipe[] = { L1ItemId.MOPO_JUiCE_PIPE1, L1ItemId.MOPO_JUiCE_PIPE2, L1ItemId.MOPO_JUiCE_PIPE3, 
            L1ItemId.MOPO_JUiCE_PIPE4, L1ItemId.MOPO_JUiCE_PIPE5, L1ItemId.MOPO_JUiCE_PIPE6, 
            L1ItemId.KILLTON_PIPE,
            L1ItemId.MERIN_PIPE};
        for (int i = 0; i <= 7; i++) if(itemId == Contract[i]) pc.getInventory().storeItem(Pipe[i], 1);
        pc.sendPackets(new S_ServerMessage(1823));//약속했던 시간이되어 새로운 아이템이 생겼습니다. 인벤토리를 확인해주세요.	

       } else {
        pc.sendPackets(new S_SystemMessage(item.getName() + "의 사용시간이 만료 되어 소멸되었습니다."));
       }
      }
     }
    }
   } catch (Exception e) {e.printStackTrace();}
   try {
    Thread.sleep(60000);
   } catch (Exception e) {}
  }
 }
}

