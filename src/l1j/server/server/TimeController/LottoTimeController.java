/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful ,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not , write to the Free Software
 * Foundation , Inc., 59 Temple Place - Suite 330, Boston , MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.TimeController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import l1j.server.Config;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class LottoTimeController implements Runnable {
 private static LottoTimeController _instance;

 public static LottoTimeController getInstance() {
  if (_instance == null) {
   _instance = new LottoTimeController();
  }
  return _instance;
 }

 @Override
 public void run() {
  try {
   while (true) {
    checkLottoTime();     // 추가
    Thread.sleep(60000);
   }
  } catch (Exception e1) {
  }
 }
 private Calendar getRealTime() {
    TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
    Calendar cal = Calendar.getInstance(_tz);
    return cal;
   }
   
  private void checkLottoTime() {
    SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
    int nowtime = Integer.valueOf(sdf.format(getRealTime().getTime()));

    int LottoTime = Config.RATE_LOTTO_TIME;
    int LottoNumber = Config.RATE_LOTTO_NUMBER;
    int LottoItem = Config.RATE_LOTTO_ITEM;
    
     if (nowtime % LottoTime == 0) { 

    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
 		if (pc.getMapId() != 350 && pc.getMapId() != 340
				&& pc.getMapId() != 360 && pc.getMapId() != 370 && !pc.isDead()
				&& !pc.isPrivateShop()&& !pc.noPlayerCK) {
     pc.getInventory().storeItem(LottoItem,LottoNumber); //
     pc.sendPackets(new S_SystemMessage("\\fV----------- 이벤트아이템("+LottoNumber+")개를 얻었습니다. -----------")); //
   }
    }

   } else {
      return;
     } // end if - else 
    //} // end for 
    
   } // end checkLotto method


}
