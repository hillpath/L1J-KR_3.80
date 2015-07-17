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

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.datatables.NpcTable;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;

@SuppressWarnings("serial")
public class MagicDoll extends L1ItemInstance {

	public MagicDoll(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			int itemId = this.getItemId();
			if (itemId != 437018) {
				useMagicDoll(pc, itemId, this.getId());
			} else {
				if (pc.getLevel() >= 66) { // 소환할수있는 레벨 .55이상은 불가능
					pc.sendPackets(new S_SystemMessage(
							"쫄법사인형은 64레벨이상 사용이 불가능합니다"));
					return;
				} else {
					useMagicDoll(pc, itemId, this.getId());
				}
			}
		}
	}

	private void useMagicDoll(L1PcInstance pc, int itemId, int itemObjectId) {
		if (pc.isInvisble()) {
			return;
		}
		boolean isAppear = true;

		L1DollInstance doll = null;
		Object[] dollList = pc.getDollList().values().toArray();
		for (Object dollObject : dollList) {
			doll = (L1DollInstance) dollObject;
			if (doll.getItemObjId() == itemObjectId) { // 이미 꺼내고 있는 매직 실업 수당
				isAppear = false;
				break;
			}
		}

		if (isAppear) {

			int npcId = 0;
			int dollType = 0;
			int consumecount = 0;
			int dollTime = 0;

			switch (itemId) {
			case L1ItemId.DOLL_PSY_CHAMPION: //싸이
			    npcId = 47738; dollType = L1DollInstance.DOLLTYPE_PSY_CHAMPION; consumecount = 50; dollTime = 1800;
			    pc.sendPackets(new S_SystemMessage("마법인형이 데미지업+2  HP+30 을 높여줍니다. "));
			    break;
			   case L1ItemId.DOLL_PSY_BIRD:
			    npcId = 47739; dollType = L1DollInstance.DOLLTYPE_PSY_BIRD; consumecount = 50; dollTime = 1800;
			    pc.sendPackets(new S_SystemMessage("마법인형이 추타+2  HP+30 을 높여줍니다. "));
			    break;
			   case L1ItemId.DOLL_PSY_GANGNAM_STYLE:
			    npcId = 47740; dollType = L1DollInstance.DOLLTYPE_PSY_GANGNAM_STYLE; consumecount = 50; dollTime = 1800;
			    pc.sendPackets(new S_SystemMessage("마법인형이 SP+1,데미지업+2,추타+2,엠틱 효과를 줍니다. "));
			    break;
			   case L1ItemId.DOLL_RANKING:
					npcId = 6000036; dollType = L1DollInstance.DOLLTYPE_RANKING; consumecount = 50; dollTime = 600; 
					  pc.sendPackets(new S_SystemMessage("마법인형이 Ac-5, 무게10%, HP+100, MP+100, 추타+5, 명중+1, 주술+5, 스턴+5"));
					break;
		
			
				case 6000033:
					npcId = 6000033; dollType = L1DollInstance.DOLLTYPE_RANKING_ONE1; consumecount = 50; dollTime = 600; 
					 pc.sendPackets(new S_SystemMessage("마법인형이 Ac-5, 무게10%, HP+70, MP+70, 추타+5, 명중+1, 주술+3 "));
					break;
	
				case 6000034:
					npcId = 6000034; dollType = L1DollInstance.DOLLTYPE_RANKING_TWO2; consumecount = 50; dollTime = 600; 
					 pc.sendPackets(new S_SystemMessage("마법인형이 Ac-4, 무게7%, HP+50, MP+50, 추타+4, 명중+1, 주술+2 "));
					break;
				
				case 6000035:
					npcId = 6000035; dollType = L1DollInstance.DOLLTYPE_RANKING_THREE3; consumecount = 50; dollTime = 600; 
					 pc.sendPackets(new S_SystemMessage("마법인형이 Ac-3, 무게5%, HP+30, MP+30, 추타+3, 명중+1, 주술+1 "));
					break;	
			
			case L1ItemId.DOLL_BUGBEAR: // 버그베어
				npcId = 80106;
				dollType = L1DollInstance.DOLLTYPE_BUGBEAR;	consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 무게를 덜어 줍니다."));
				break;
			case L1ItemId.DOLL_SUCCUBUS: // 서큐버스
				npcId = 80107;
				dollType = L1DollInstance.DOLLTYPE_SUCCUBUS; consumecount = 50;	dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 MP 회복을 높여 줍니다"));
				break;
			case L1ItemId.DOLL_WAREWOLF: // 늑대인간
				npcId = 80108;
				dollType = L1DollInstance.DOLLTYPE_WAREWOLF; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 추타를 높여 줍니다"));
				break;
			case L1ItemId.DOLL_STONEGOLEM: // 돌골렘
				npcId = 4500150;
				dollType = L1DollInstance.DOLLTYPE_STONEGOLEM; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 회피를 높여 줍니다"));
				break;
			case L1ItemId.DOLL_ELDER: // 장로
				npcId = 4500151;
				dollType = L1DollInstance.DOLLTYPE_ELDER; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 MP 회복을 높여 줍니다"));
				break;
			case L1ItemId.DOLL_CRUSTACEA: // 시안
				npcId = 4500152;
				dollType = L1DollInstance.DOLLTYPE_CRUSTACEA; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 추타를 높여 줍니다"));
				break;
			case L1ItemId.DOLL_SEADANCER: // 시댄서
				npcId = 4500153;
				dollType = L1DollInstance.DOLLTYPE_SEADANCER; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 HP 회복을 높여 줍니다"));
				break;
			case L1ItemId.DOLL_SNOWMAN: // 에티
				npcId = 4500154;
				dollType = L1DollInstance.DOLLTYPE_SNOWMAN; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 AC-3 + 동빙내성을 높여 줍니다"));
				break;
			case L1ItemId.DOLL_COCATRIS: // 코카트리스
				npcId = 4500155;
				dollType = L1DollInstance.DOLLTYPE_COCATRIS; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 추타를 높여 줍니다"));
				break;
			case L1ItemId.DOLL_DRAGON_M: // 해츨링
				npcId = 4500156;
				dollType = L1DollInstance.DOLLTYPE_DRAGON_M; consumecount = 50;	dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 무게를 덜어 줍니다."));
				break;
			case L1ItemId.DOLL_DRAGON_W: // 해츨링
				npcId = 4500157;
				dollType = L1DollInstance.DOLLTYPE_DRAGON_W; consumecount = 50;	dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 무게를 덜어 줍니다.")); 
				break;
			case L1ItemId.DOLL_HIGH_DRAGON_M: // 진화해츨링
				npcId = 4500158;
				dollType = L1DollInstance.DOLLTYPE_HIGH_DRAGON_M; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 무게 + MP 회복을 높여 줍니다."));
				break;
			case L1ItemId.DOLL_HIGH_DRAGON_W: // 진화해츨링
				npcId = 4500159;
				dollType = L1DollInstance.DOLLTYPE_HIGH_DRAGON_W; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 무게 + MP 회복을 높여 줍니다."));
				break;
			case L1ItemId.DOLL_LAMIA:// 라미아
				npcId = 4500160;
				dollType = L1DollInstance.DOLLTYPE_LAMIA; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 독공격 + MP 회복을 높여 줍니다."));
				break;
			case 500400: // 스켈
				npcId = 75022;
				dollType = L1DollInstance.DOLLTYPE_SKELETON; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 타격치 + 스턴내성을 높여 줍니다."));
				break;
			case L1ItemId.DOLL_SCARECROW: // by포비 허수아비
				npcId = 41916;
				dollType = L1DollInstance.DOLLTYPE_SCARECROW; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 추타 + HP/MP 를 높여 줍니다."));
				break;
			case L1ItemId.DOLL_ETTIN:
				npcId = 41917;
				dollType = L1DollInstance.DOLLTYPE_ETTIN; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 AC-2 + 헤이 + 홀드내성을 높여 줍니다."));
				break;
			case 50058://마법인형 : 블레그
				npcId = 50408;
				dollType = L1DollInstance.DOLLTYPE_RANKING_ONE; consumecount = 50; dollTime = 600;
				pc.sendPackets(new S_SystemMessage("마법인형에 의해 추타+2 SP+1 바람속성공격 발동됩니다."));
				break;
			case 50059://마법인형 : 레데그
				npcId = 50407;
				dollType = L1DollInstance.DOLLTYPE_RANKING_TWO; consumecount = 50; dollTime = 600;
				pc.sendPackets(new S_SystemMessage("마법인형에 의해 추타+2 SP+1 불속성공격 발동됩니다."));
				break;
			case 50057://마법인형 : 엘레그
				npcId = 50406;
				dollType = L1DollInstance.DOLLTYPE_RANKING_THREE; consumecount = 50; dollTime = 600;
				pc.sendPackets(new S_SystemMessage("마법인형에 의해 추타+2 SP+1 땅속성공격 발동됩니다."));
				break;
			case 50056://마법인형 : 그레그
				npcId = 50405;
				dollType = L1DollInstance.DOLLTYPE_RANKING_FOR; consumecount = 50; dollTime = 600;
				pc.sendPackets(new S_SystemMessage("마법인형에 의해 추타+2 SP+1 공격 마법 발동됩니다."));
				break;
				
			case 500144: // 눈사람(A)
				npcId = 700196;
				dollType = L1DollInstance.DOLLTYPE_SNOWMAN_A; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 경치10% + 명중을 높여 줍니다."));
				break;
			case 500145: // 눈사람(B)
				npcId = 700197;
				dollType = L1DollInstance.DOLLTYPE_SNOWMAN_B; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 경치10% + MP 회복을 높여 줍니다."));
				break;
			case 500146: // 눈사람(C)
				npcId = 700198;
				dollType = L1DollInstance.DOLLTYPE_SNOWMAN_C; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_SystemMessage("마법인형이 경치10% + HP 회복을 높여 줍니다."));
				break; // 추가
			case 437018:
				npcId = 4000009;
				dollType = L1DollInstance.DOLLTYPE_HELPER; consumecount = 50; dollTime = 1800;
				pc.sendPackets(new S_ChatPacket(pc, ".도움말 을 치면 명령어목록이 나옵니다",Opcodes.S_OPCODE_NORMALCHAT, 2));
				break;
			}

			if (!pc.getInventory().checkItem(41246, consumecount)) {
				pc.sendPackets(new S_ServerMessage(337, "$5240"));
				return;
			}
			if (dollList.length >= Config.MAX_DOLL_COUNT) {
				// \f1 더 이상의 monster를 조종할 수 없습니다.
				pc.sendPackets(new S_ServerMessage(319));
				return;
			}

			L1Npc template = NpcTable.getInstance().getTemplate(npcId);
			doll = new L1DollInstance(template, pc, dollType, itemObjectId,
					dollTime * 1000);
			pc.sendPackets(new S_SkillSound(doll.getId(), 3940));
			pc.sendPackets(new S_SkillSound(doll.getId(), 5935));
			pc.sendPackets(new S_SkillIconGFX(56, dollTime));
			pc.sendPackets(new S_OwnCharStatus(pc));
			pc.getInventory().consumeItem(41246, consumecount);
		} else {
			doll.deleteDoll();
			pc.sendPackets(new S_SkillIconGFX(56, 0));
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
	}
}
