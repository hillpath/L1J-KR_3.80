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
package l1j.server.server.model.Instance;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1WarSpawn;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CommonUtil;
import l1j.server.server.utils.L1SpawnUtil;

public class L1EventCrownInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public L1EventCrownInstance(L1Npc template) {
		super(template);
		
		//spot = template.get_Spot();
	}

	@Override
	public void onAction(L1PcInstance player) {
		
		if(player.getMapId() == 2006){
			return;
		}
		if (player.getClanid() == 0) { // 크란미소속
			player.sendPackets(new S_SystemMessage("\\fY혈맹에 가입해야만 이벤트왕관을 클릭하실수 있습니다."));
			return;
		}
		String playerClanName = player.getClanname();
		L1Clan clan = L1World.getInstance().getClan(playerClanName);
		
		if (clan == null) {
			player.sendPackets(new S_SystemMessage("\\fY혈맹에 가입해야만 이벤트왕관을 클릭하실수 있습니다."));
			return;
		}
		
		//player.set_Spot(spot);
		deleteMe();
		L1EventTowerInstance lt = null;		
		
		for (L1Object l1object : L1World.getInstance().getVisibleObjects(this, 5)) {
			if (l1object instanceof L1EventTowerInstance) {
				lt = (L1EventTowerInstance) l1object;				
				lt.deleteMe();
			}
		}
		
		// 클릭한 유저의 선물!
		int chance = 0;
		chance = CommonUtil.random(100) + 1;
		if(chance <= 2){
			player.getInventory().storeItem(61, 1);
			player.sendPackets(new S_SystemMessage("\\fU진명황의 집행검이 지급되었습니다."));
		}else if(chance >= 3 && chance <= 20){
			player.getInventory().storeItem(114, 1);
			player.sendPackets(new S_SystemMessage("\\fU피묻은 군주의 검이 지급되었습니다."));
		}else if(chance >= 21 && chance <= 30){
			player.getInventory().storeItem(58, 1);
			player.sendPackets(new S_SystemMessage("\\fU데스나이트의 불검이 지급되었습니다."));
		}else if(chance >= 31 && chance <= 40){
			player.getInventory().storeItem(205, 1);
			player.sendPackets(new S_SystemMessage("\\fU달의 장궁이 지급되었습니다."));
		}else if(chance >= 41 && chance <= 50){
			player.getInventory().storeItem(191, 1);
			player.sendPackets(new S_SystemMessage("\\fU살천의 활이 지급되었습니다."));
		}else if(chance >= 51 && chance <= 60){
			player.getInventory().storeItem(134, 1);
			player.sendPackets(new S_SystemMessage("\\fU수정 결정체 지팡이가 지급되었습니다."));
		}else if(chance >= 61 && chance <= 70){
			player.getInventory().storeItem(86, 1);
			player.sendPackets(new S_SystemMessage("\\fU붉은 그림자의 이도류가 지급되었습니다."));
		}else if(chance >= 71 && chance <= 80){
			player.getInventory().storeItem(450006, 1);
			player.sendPackets(new S_SystemMessage("\\fU엘모어 키링크가 지급되었습니다."));
		}else if(chance >= 81 && chance <= 90){
			player.getInventory().storeItem(41159, 1000);
			player.sendPackets(new S_SystemMessage("\\fU신비한 날개 깃털(1000)개가 지급되었습니다."));
		}else if(chance >= 91 && chance <= 100){
			player.sendPackets(new S_SystemMessage("\\fU운이 나쁘시네요..아무것도 받지 못하셨습니다."));
		}
		
		
		L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fU<" + player.getName() + ">님이 이벤트 왕관을 클릭하셨습니다."));			
		L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fC<" + player.getName() + ">님이 이벤트 왕관을 클릭하셨습니다."));
		
		for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
			if (target.getEventDamage() > 0) {
					target.getInventory().storeItem(40308,target.getEventDamage() * 1500);
					target.sendPackets(new S_SystemMessage("\\fU용뼈 타워 데미지(" + target.getEventDamage() + "x3000)원을 아덴으로 지급하였습니다!"));					
					target.setEventDamage(0);
			}
		}
		if (L1EventTowerInstance.isBoss) {	
			try {
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fU * 용뼈에 이벤트보스가 출몰하였습니다 *"));	
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fW * 용뼈에 이벤트보스가 출몰하였습니다 *"));
				L1SpawnUtil.spawn1(getX(), getY(), getMapId(), CommonUtil.random(0, 7), 1145496, 3, false);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else{
			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fU * 이번 용뼈이벤트에는 보스몬스터가 출몰하지 않습니다 *"));	
		}
	}

	@Override
	public void deleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		allTargetClear();
		_master = null;
		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.getNearObjects().removeKnownObject(this);
			pc.sendPackets(new S_RemoveObject(this));
		}
		getNearObjects().removeAllKnownObjects();
		
		if (L1EventTowerInstance.isStart) {
			GeneralThreadPool.getInstance().execute(new Respawn());
		}
	}
	
	private class Respawn implements Runnable {
		public void run() {
			try {
				try {
					Thread.sleep(1000 * 60 * L1EventTowerInstance.spwanTime);
					//Thread.sleep(1000 * 10);
				} catch (Exception e) {
					// TODO: handle exception
				}
				for (L1Object obj : L1World.getInstance().getObject()) {
					if (obj instanceof L1EventTowerInstance || obj instanceof L1EventCrownInstance) {
						if(obj.getMapId() != 2006){
							return;
						}
					}
				}
				DeleteTower();
				
				L1WarSpawn warspawn = new L1WarSpawn();
				L1Npc l1npc = NpcTable.getInstance().getTemplate(6100001);				
				int[] loc = L1EventTowerInstance.location[CommonUtil.random(L1EventTowerInstance.location.length)];
				// 홈 좌표 넣어야함....
				warspawn.SpawnWarObject(l1npc, loc[0], loc[1], (short) (loc[2]));
				
				for (L1PcInstance target : L1World.getInstance().getAllPlayers()) {
					if (target.getEventDamage() > 0) {
						target.setEventDamage(0);
					}
				}
				
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fY용뼈에 이벤트 타워가 생성되었습니다."));
				L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fC용뼈에 이벤트 타워가 생성되었습니다."));
			} catch (Exception e) { }
		}
	}
	
	private void DeleteTower() {		
		for (Object obj : L1World.getInstance().getVisibleObjects(4).values()) {			
			if (obj instanceof L1EventTowerInstance){			
				L1EventTowerInstance tower = (L1EventTowerInstance) obj;				
				if(tower.getNpcTemplate().get_npcId() == 6100001){					
					tower.deleteMe();	
					}
				}
			}
	}
}
