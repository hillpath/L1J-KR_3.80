/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.model.item.function;

import java.util.Random;

import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.L1SpawnUtil;

@SuppressWarnings("serial")
public class Special_Other extends L1ItemInstance{
	
	public Special_Other(L1Item item){
		super(item);
	}
	private static Random _random = new Random(System.nanoTime());
	
	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet){
		if(cha instanceof L1PcInstance){
			L1PcInstance pc = (L1PcInstance)cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = useItem.getItemId();
			
			if (itemId == 600000) {  //보스 소환 부적
				int i = L1CastleLocation.getCastleIdByArea(pc);
				if (CharPosUtil.getZoneType(pc) == 0 && i != 0
						|| CharPosUtil.getZoneType(pc) != 0 
						|| !pc.getMap().isUsePainwand()){
					pc.sendPackets(new S_ServerMessage(563));	//여기에서는 사용할수 없습니다.
					return;
				}
				useMobEventSpownWand(pc, useItem);	
			} 
		}
	}
	private void useMobEventSpownWand(L1PcInstance pc, L1ItemInstance item) {
		try {
			int[][] mobArray = {
					// 일반몹
					{ 45008, 45140, 45016, 45021, 45025,
						45033, 45099, 45147, 45123, 45130, 45046,
						45092, 45138, 45098, 45127, 45143, 45149,
						45171, 45040, 45155, 45192, 45173, 45213,
						45079, 45144 },
						// 보스몹 10%
						{ 45488, 45456, 45473, 45497, 45464, 45545,
							45529, 45516 },
							// 보스몹 7%
							{ 45601, 45573, 45583, 46142, 46141, 45955,
								45956, 45957, 45958, 45959, 45960, 45961, 45962, 45617,
								45610, 45600, 45614, 45618, 45649, 45680, 45654, 45674,
								45625, 45675, 45672 },
								// 보스몹 3%
								{ 45753, 45801, 45673 }
			};

			int category = 0;
			int rndcategory = _random.nextInt(100) + 1;
			if (rndcategory <= 80)
				category = 0;
			else if (rndcategory <= 90 )
				category = 1;
			else if (rndcategory <= 97 )
				category = 2;
			else if (rndcategory <= 100 )
				category = 3;

			int rnd = _random.nextInt(mobArray[category].length);
			L1SpawnUtil.spawn(pc, mobArray[category][rnd], 0, 180000, false);
			pc.getInventory().removeItem(item, 1);

		}catch(Exception e){
			e.printStackTrace();
		}
	}


}

