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

package l1j.server.server.clientpackets;

import java.util.Random;

import l1j.server.server.datatables.PetTypeTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1PetType;
import server.LineageClient;
import server.system.autoshop.AutoShopManager;

public class C_GiveItem extends ClientBasePacket {
	private static final String C_GIVE_ITEM = "[C] C_GiveItem";

	//private static Random _random = new Random(System.nanoTime());

	public C_GiveItem(byte decrypt[], LineageClient client) {
		super(decrypt);
		int targetId = readD();
		@SuppressWarnings("unused")
		int x = readH();
		@SuppressWarnings("unused")
		int y = readH();
		int itemId = readD();
		int count = readD();

		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}
		// 아이템저장시킴
		pc.saveInventory();
		// 아이템저장시킴
		L1Object object = L1World.getInstance().findObject(targetId);
		L1NpcInstance target = (L1NpcInstance) object;
		L1Inventory targetInv = target.getInventory();
		L1Inventory inv = pc.getInventory();
		L1ItemInstance item = inv.getItem(itemId);

		if (pc.isGhost()) {
			return;
		}
		if (object == null || item == null) {
			return;
		}
		if (isTwoLogin(pc))
			return;
		if (!isNpcItemReceivable(target.getNpcTemplate())) {
			if (!(item.getItem().getItemId() == 40499)
					|| !(item.getItem().getItemId() == 40507)) {
				return;
			}
		}
		 //** 중계기 노딜버그 막아 보자 **//
		  long nowtime = System.currentTimeMillis();
		  if(item.getItemdelay3() >=  nowtime ){	
		   return;
		  }  
		  //** 중계기 노딜버그 막아 보자 **//
		if (item.isEquipped()) {
			pc.sendPackets(new S_ServerMessage(141)); // \f1장비 하고 있는 것은, 사람에게
			// 건네줄 수가 없습니다.
			return;
		}
		if (item.getAcByMagic() > 0) {
			pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
			return;
		}
		if (item.getDmgByMagic() > 0) {
			pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
			return;
		}
		if (item.getHolyDmgByMagic() > 0) {
			pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
			return;
		}
		if (item.getHitByMagic() > 0) {
			pc.sendPackets(new S_SystemMessage("아직 장비에 마법효과가 남아있습니다."));
			return;
		}
		if (item.getBless() >= 128) {// 봉인
			pc.sendPackets(new S_ServerMessage(141));
			return;
		}
		// if (pc.getAccessLevel() == Config.GMCODE){
		// return;
		// }
		if (itemId != item.getId()) {
			pc.sendPackets(new S_Disconnect());
			return;
		}
		if (!item.isStackable() && count != 1) {
			pc.sendPackets(new S_Disconnect());
			return;
		}
		if (item.getCount() <= 0 || count <= 0) {
			pc.sendPackets(new S_Disconnect());
			return;
		}
		if (count >= item.getCount()) {
			count = item.getCount();
		}
		if (item.getItem().getItemId() == 423012
				|| item.getItem().getItemId() == 423013) { // 10주년티
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0은
			// 버리거나
			// 또는
			// 타인에게
			// 양일을
			// 할 수
			// 없습니다.
			return;
		}
		if (!item.getItem().isTradable() || item.getItemId() == 40308
				|| item.getItemId() == 41159) {
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0은
			// 버리거나
			// 또는
			// 타인에게
			// 양일을
			// 할 수
			// 없습니다.
			return;
		}
		L1PetInstance pet = null;
		for (Object petObject : pc.getPetList().values()) {
			if (petObject instanceof L1PetInstance) {
				pet = (L1PetInstance) petObject;
				if (item.getId() == pet.getItemObjId()) {
					// \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
					pc.sendPackets(new S_ServerMessage(210, item.getItem()
							.getName()));
					return;
				}
			}
		}
			if (!pc.isGm() && targetInv.checkAddItem(item, count) != L1Inventory.OK) {
				pc.sendPackets(new S_ServerMessage(942)); // 상대의 아이템이 너무 무겁기
															// (위해)때문에, 더 이상 줄 수
															// 없습니다.
				return;
			}
			 //** 수량성 아이템 딜레이타이머 추가 **// 
			  int delay_time = 5000;
			   if (item.isStackable()) {
			    if(item.getItemdelay3() <=  nowtime ){
			    	item.setItemdelay3( nowtime + delay_time ); 
					pc.sendPackets(new S_SystemMessage("5초후에 아이템을 버릴수 있습니다."));    	
			    	}
			        }
			    //** 수량성 아이템 딜레이타이머 추가 **//

		L1PetType petType = PetTypeTable.getInstance().get(
				target.getNpcTemplate().get_npcId());
		if (petType != null) {
			if ((petType.getBaseNpcId() == 46046 || petType
					.getItemIdForTaming() != 0)
					&& item.getItem().isUseHighPet()) {
				return;
			}
		}

		item = inv.tradeItem(item, count, targetInv);
		target.onGetItem(item);
		target.getLight().turnOnOffLight();
		pc.getLight().turnOnOffLight();
		// 아이템저장시킴
		pc.saveInventory();
		// 아이템저장시킴

		if (petType == null || target.isDead()) {
			return;
		}
/**호랑이리뉴얼*/
		if (item.getItemId() == petType.getItemIdForTaming() && 
				(item.getItemId() ==490026 && target.getNpcTemplate().get_npcId() == 45711) ||
				(item.getItemId() ==490027 && target.getNpcTemplate().get_npcId() == 45313)) {
			Random _rnd = new Random();
			if(item.getItemId() >= 490024 && item.getItemId() <= 490027){
				int value = _rnd.nextInt(100)+1;
				value += item.getItemId() == 490026 || item.getItemId() == 490027 ? 20 : 0;
				if(value > 90)
					tamePet(pc, target);
				//else
					//pc.sendPackets(new S_ServerMessage(324)); // 길들이는데 실패했습니다.
		//	}else
			//	tamePet(pc, target);
		}
		}
/**호랑이리뉴얼**/
		if (item.getItemId() == petType.getItemIdForTaming()) {
			tamePet(pc, target);
		}
		if (item.getItemId() == 40070 && petType.canEvolve() // 진화의열매(당근)
				&& petType.getItemIdForTaming() == 40060) {
			evolvePet(pc, target);
		}

		if (item.getItemId() == 40070 && petType.canEvolve() // 진화의열매(괴고기)
				&& petType.getItemIdForTaming() == 40057||petType.getItemIdForTaming() ==490024
				||petType.getItemIdForTaming() ==490025||petType.getItemIdForTaming() ==490026
				||petType.getItemIdForTaming() ==490027) {
			evolvePet(pc, target);
		}

		if (item.getItemId() == 41310 && petType.canEvolve() // 승리의 열매
				&& petType.getItemIdForTaming() == 0) {
			evolvePet(pc, target);
		}

		if ((item.getItem().getMaterial() == 4 || item.getItemId() == 40060)
				&& item.getItem().getType() == 7) {// 동물성 음식류
			petfoodgive(pc, target, item);
		}

		L1PetInstance pets = (L1PetInstance) L1World.getInstance().findObject(
				targetId);
		if (item.getItem().isUseHighPet() && petType.getItemIdForTaming() == 0) {
			if (item.getItemId() >= 427100 && item.getItemId() <= 427109) {
				pets.usePetWeapon(item);
			} else if (item.getItemId() >= 427000 && item.getItemId() <= 427007) {
				pets.usePetArmor(item);
			}
		}
	}

	private final static String receivableImpls[] = new String[] { "L1Npc", // NPC
			"L1Monster", // monster
			"L1Guardian", // 요정 숲의 수호자
			"L1Teleporter", // 텔레 포터
			"L1Guard" }; // 가이드

	private boolean isNpcItemReceivable(L1Npc npc) {
		for (String impl : receivableImpls) {
			if (npc.getImpl().equals(impl)) {
				return true;
			}
		}
		return false;
	}

	private void tamePet(L1PcInstance pc, L1NpcInstance target) {
		if (target instanceof L1PetInstance
				|| target instanceof L1SummonInstance) {
			return;
		}

		int petcost = 0;
		Object[] petlist = pc.getPetList().values().toArray();
		for (Object pet : petlist) {
			petcost += ((L1NpcInstance) pet).getPetcost();
		}
		int charisma = pc.getAbility().getTotalCha();
		if (pc.isCrown()) { // 군주
			charisma += 6;
		} else if (pc.isElf()) { // 요정
			charisma += 12;
		} else if (pc.isWizard()) { // 마법사
			charisma += 6;
		} else if (pc.isDarkelf()) { // 다크엘프
			charisma += 6;
		} else if (pc.isDragonknight()) { // 용기사
			charisma += 6;
		} else if (pc.isIllusionist()) { // 환술사
			charisma += 6;
		}
		charisma -= petcost;

		L1PcInventory inv = pc.getInventory();
		String npcname = target.getNpcTemplate().get_name();
		if (charisma >= 6 && inv.getSize() < 180) {
			if (isTamePet(pc, target)) {
				L1ItemInstance petamu = inv.storeItem(40314, 1); // 펫의 아뮤렛트
				if (petamu != null) {
					new L1PetInstance(target, pc, petamu.getId());
					pc.sendPackets(new S_ItemName(petamu));
					pc.sendPackets(new S_SystemMessage(npcname
							+ "의 목걸이를 얻었습니다."));

				}
			} else {
				pc.sendPackets(new S_ServerMessage(324)); // 길들이는데 실패했습니다.
			}
		}
	}

	private void evolvePet(L1PcInstance pc, L1NpcInstance target) {
		if (!(target instanceof L1PetInstance)) {
			return;
		}
		L1PcInventory inv = pc.getInventory();
		L1PetInstance pet = (L1PetInstance) target;
		L1ItemInstance petamu = inv.getItem(pet.getItemObjId());
		String npcname = target.getNpcTemplate().get_name();
		if (pet.getLevel() >= 30 && // Lv30 이상
				pc == pet.getMaster() && // 자신의 애완동물
				petamu != null) {
			L1ItemInstance highpetamu = inv.storeItem(40316, 1);
			if (highpetamu != null) {
				pet.evolvePet(highpetamu.getId()); // 진화시킨다
				pc.sendPackets(new S_ItemName(highpetamu));
				inv.removeItem(petamu, 1);
				pc
						.sendPackets(new S_SystemMessage(npcname
								+ "의 진화에 성공 하였습니다."));
			}
		} else {
			pc
					.sendPackets(new S_SystemMessage(npcname
							+ "의 진화조건이 충족돼지 않았습니다."));
		}
	}

	private boolean isTamePet(L1PcInstance pc, L1NpcInstance npc) {
		boolean isSuccess = false;
		int npcId = npc.getNpcTemplate().get_npcId();
		switch (npcId) {
		case 45313: // 호랑이
			isSuccess = true;
			break;
		case 46044: // 아기판다곰
			isSuccess = true;
			break;
		case 46042: // 아기캥거루
			isSuccess = true;
			break;
		case 45044: // 라쿤
			isSuccess = true;
			break;
		case 45711: // 아기진돗개
			isSuccess = true;
			break;
		case 45040: // 곰
			isSuccess = true;
			break;
		case 45049: // 열혈토끼
			isSuccess = true;
			break;
		case 450482: // 여우
			isSuccess = true;
			break;
		case 45039: // 고양이
			isSuccess = true;
			break;
		case 45042: // 도베르만
			isSuccess = true;
			break;
		case 45053: // 허스키
			isSuccess = true;
			break;
		default:
			if (npc.getMaxHp() / 2 > npc.getCurrentHp()) {
				isSuccess = true;
			}
			break;
		}
		return isSuccess;
	}

	private void petfoodgive(L1PcInstance pc, L1NpcInstance target,
			L1ItemInstance item) {
		if (!(target instanceof L1PetInstance)) {
			return;
		}
		L1PetInstance pet = (L1PetInstance) target;
		L1Inventory inv = target.getInventory();

		if ((target.getNpcId() == 46042 || target.getNpcId() == 46043)
				&& item.getItemId() == 41423) {// 캥거루 먹이
			pet.setFood(0);
			target.getSkillEffectTimerSet().setSkillEffect(
					L1SkillId.STATUS_PET_FOOD, pet.getFoodTime() * 1000);
		} else if ((target.getNpcId() == 46044 || target.getNpcId() == 46045)
				&& item.getItemId() == 41424) {// 판다곰 먹이
			inv.removeItem(item, 1);
			pet.setFood(0);
			target.getSkillEffectTimerSet().setSkillEffect(
					L1SkillId.STATUS_PET_FOOD, pet.getFoodTime() * 1000);
		} else if ((target.getNpcId() == 45049 || target.getNpcId() == 45695)
				&& item.getItemId() == 40060) {
			inv.removeItem(item, 1);
			pet.setFood(0);
			target.getSkillEffectTimerSet().setSkillEffect(
					L1SkillId.STATUS_PET_FOOD, pet.getFoodTime() * 1000);
		} else {
			inv.removeItem(item, 1);
			pet.setFood(0);
			target.getSkillEffectTimerSet().setSkillEffect(
					L1SkillId.STATUS_PET_FOOD, pet.getFoodTime() * 1000);
		}
	}
	private boolean isTwoLogin(L1PcInstance c) {
		for(L1PcInstance target : L1World.getInstance().getAllPlayersToArray()){
			if(target.noPlayerCK) continue;
			int count = 0;
			if(c.getId() == target.getId()){
				count++;
				if(count > 1){
					c.getNetConnection().kick();
					c.getNetConnection().close();
					target.getNetConnection().kick();
					target.getNetConnection().close();
					return true;
				}
			}
			else if(c.getId() != target.getId()){
				if(c.getAccountName().equalsIgnoreCase(target.getAccountName())) {
					if(!AutoShopManager.getInstance().isAutoShop(target.getId())){
						c.getNetConnection().kick();
						c.getNetConnection().close();
						target.getNetConnection().kick();
						target.getNetConnection().close();
						return true;
					}			
				}
			}
		}
		return false;
	}
	
	@Override
	public String getType() {
		return C_GIVE_ITEM;
	}
}
