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

import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;

import javolution.util.FastTable;
import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.Dead;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1MobSkillUse;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1UltimateBattle;
import l1j.server.server.model.L1World;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CalcExp;
import l1j.server.server.utils.L1SpawnUtil;

public class L1MonsterInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(L1MonsterInstance.class
			.getName());

	public static int[][] _classGfxId = { { 0, 1 }, { 48, 61 }, { 37, 138 },
			{ 734, 1186 }, { 2786, 2796 }, { 6658, 6661 }, { 6671, 6650 } };

	private static Random _random = new Random(System.nanoTime());

	private int _storeDroped;
	
	private boolean seeon = false;// 안타라스

	private Dead dead = new Dead(this, null);

	private L1MobSkillUse mob;

	private L1NpcInstance _attacker = null;

	private int hprsize;

	@Override
	public void onItemUse() {
		if (!isActived() && _target != null) {
			if (getLevel() <= 45) {
				useItem(USEITEM_HASTE, 40);

             }
			try {
                if (getNpcTemplate().get_npcId() == 45590) { // 엔피씨 번호
				String chat = "그림리퍼님에게 드릴 제물이로구나.";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45513) { // 엔피씨 번호
				String chat = "가소롭구나 먹이주제에!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 46140) { //시녀
				String chat = "여왕님의 혼란을 방해할 수는 없다!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 46141) { // 여왕
				String chat = "네게 명한다. 이 자리에서 얼어 죽어라";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));			
			}else if (getNpcTemplate().get_npcId() == 46142) { //데몬
				String chat = "하찮은 인간들이여, 얼어 죽여주마!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45547) { // 엔피씨 번호
				String chat = "네 이놈들! 네 놈들을 모두 녹여버릴테다!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45606) { // 엔피씨 번호
				String chat = "레스 레네 레그 도프!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45650) { // 엔피씨 번호
				String chat = "이익..노예 주제에 건방지구나! 죽어라!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45652) { // 엔피씨 번호
				String chat = "나..의..종이 될지어다..";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45653) { // 엔피씨 번호
				String chat = "네 놈들에게 특별히 극한의 공포를 느끼게 해주마!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45654) { // 엔피씨 번호
				String chat = "내게서 도망칠 수 있을 것 같으냐!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45618) { // 엔피씨 번호
				String chat = "용기는 가상하구나!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45672) { // 엔피씨 번호
				String chat = "진정한 힘을 보여주마! 버텨보아라!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 45673) { // 엔피씨 번호
				String chat = "영생을 얻는 나에게 도전하는가!!!";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}else if (getNpcTemplate().get_npcId() == 4036016){ 
				String chat = "제브 레퀴 : 스으으으으으....스으으으으....";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 4036017){
				String chat = "제브 레퀴 : 휘이이이익....휘이이이익...";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 400016){
				String chat = "아누비스 : 너희에게 죽음을....";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 400017){
				String chat = "호루스 : 자비는 없다....";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			} else if (getNpcTemplate().get_npcId() == 4039020) {
				String chat = "안타라스 : 감히 여기가 어디라고! 어리석은 인간들이란...";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 4039021) { // 지룡 안타라스 2차
				String chat = "안타라스 : 어리석은 자여! 나의 분노를 자극하는 구나.!";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 4039022) { // 지룡 안타라스 3차
				String chat = "안타라스 : 감히 나를 상대하려 하다니..그러고도 너희가 살길 바라느냐?";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			} else if (getNpcTemplate().get_npcId() == 4039000) { // 엔피씨 번호
				String chat = "파푸리온 : 감히 나의 영역에 들어오다니...용기가 가상하구나..";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 4039006) { // 엔피씨 번호
				String chat = "파푸리온 : 봉인을 풀 때 네가 큰 도움이 되었지만..나에게 두 번의 자비는 없다..";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 4039007) { // 엔피씨 번호
				String chat = "파푸리온 : 가소롭구나! 저들이 너와 함께 이승을 떠돌게 될 나의 제물들인 것이냐!";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			} else if (getNpcTemplate().get_npcId() == 9170) {//{린드레이드1
				String chat = "린드비오르 : 가소롭구나! 너희들의 어리석음을 뼈속 깊이 후회하게 만들어주겠다!";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 9171) { //린드레이드2
				String chat = "린드비오르 : 오랫동안 몸을 풀지 못했는데, 놀아보도록 하자!!";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 9172) { //린드레이드3
				String chat = "린드비오르 : 너희의 그 오만이 어떠한 결과를 가져 오는지 몸소 보여주도록 하겠다.";
				Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			}else if (getNpcTemplate().get_npcId() == 9187) { //린드레이드4
				String chat = "린드비오르님의 성소에 침입한 자 누구인가?";
				Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
						chat, 0));
			}
                if (getNpcTemplate().get_npcId() == 45956) { // 라던4층 보스 멘트
				for (L1PcInstance pc : L1World.getInstance()
						.getAllPlayers()) {
					if (pc.getMapId() == 531) {
						pc.sendPackets(new S_SystemMessage(
								"비아타스 : 감히 이곳이 어딘줄 알고 그 더러운 발을 들이느냐!"));
					}
				}
				Thread.sleep(100L);
				for (L1PcInstance pc : L1World.getInstance()
						.getAllPlayers()) {
					if (pc.getMapId() == 531) {
						pc.sendPackets(new S_SystemMessage(
								"비아타스 : 마지막으로 기회를 줄테니 내 내눈에 띄기 전에 당장 사라져라."));
					}
				}
				if (getNpcTemplate().get_npcId() == 45957) {
					for (L1PcInstance pc : L1World.getInstance()
							.getAllPlayers()) {
						if (pc.getMapId() == 531) {
							pc.sendPackets(new S_SystemMessage(
									"바로메스 : 나의 안식을 방해하는 겁 없는 자가 누구더냐?"));
						}
					}
					Thread.sleep(100L);
					for (L1PcInstance pc : L1World.getInstance()
							.getAllPlayers()) {
						if (pc.getMapId() == 531) {
							pc.sendPackets(new S_SystemMessage(
									"바로메스 : 죽음을 원하거든 나에게로 오너라."));
						}
					}
					if (getNpcTemplate().get_npcId() == 45958) {
						for (L1PcInstance pc : L1World.getInstance()
								.getAllPlayers()) {
							if (pc.getMapId() == 531) {
								pc.sendPackets(new S_SystemMessage(
										"엔디아스 : 그랑카인이시여! 이 어리석은 자들에게 진정한 어둠의 힘을 보여주소서!"));
							}
						}
					}
				}
			}
			} catch (Exception exception) {
			}
			if (getNpcTemplate().is_doppel() && _target instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) _target;
				setName(_target.getName());
				setNameId(_target.getName());
				setTitle(_target.getTitle());
				setTempLawful(_target.getLawful());
				getGfxId().setTempCharGfx(targetPc.getClassId());
				getGfxId().setGfxId(targetPc.getClassId());
				setPassispeed(640);
				setAtkspeed(900);
				FastTable<L1PcInstance> list = null;
				list = L1World.getInstance().getRecognizePlayer(this);
				for (L1PcInstance pc : list) {
					if (pc == null)
						continue;
					pc.sendPackets(new S_RemoveObject(this));
					pc.getNearObjects().removeKnownObject(this);
					pc.updateObject();
			}
			}
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) {
			useItem(USEITEM_HEAL, 50);
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.getNearObjects().addKnownObject(this);
		if (0 < getCurrentHp()) {
			if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Hide));
			} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Moveup));
			}
			onNpcAI();
		}
		perceivedFrom.sendPackets(new S_NPCPack(this));
		if (getNpcTemplate().get_npcId() == 4039020 && !seeon){
			Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
			seeon = true;
		}
		else if (getNpcTemplate().get_npcId() == 4039000 && !seeon){
		      Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
		   seeon = true;      
		}
	    else if (getNpcTemplate().get_npcId() == 9170 && !seeon){//린드레이드
	      Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
	   seeon = true;      
	}
	    else if (getNpcTemplate().get_npcId() == 9187 && !seeon){//린드레이드
		      Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
		   seeon = true;      
		}
	}

	@Override
	public void searchTarget() {
		L1PcInstance targetPlayer = null;
		/**허수아비패기**/
		L1ScarecrowInstance targetScarecrow = null;
		/**허수아비패기**/
		FastTable<L1PcInstance> list = null;
		list = L1World.getInstance().getVisiblePlayer(this);
		for (L1PcInstance pc : list) {
			if (pc == null)
				continue;
			if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm()
					|| pc.isMonitor() || pc.isGhost()) {
				continue;
			}

			int mapId = getMapId();
			if (mapId == 88 || mapId == 98 || mapId == 92 || mapId == 91
					|| mapId == 95) {
				if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) {
					targetPlayer = pc;
					break;
				}
			}

			if (getNpcId() == 45600) {
				if (pc.isCrown() || pc.isDarkelf()
						|| pc.getGfxId().getTempCharGfx() != pc.getClassId()) {
					targetPlayer = pc;
					break;
				}
			}
			/**카오선공장로**/
			if (getNpcId() == 45215) {
				if (pc.getLawful() <= -1) {
					targetPlayer = pc;
					break;
				}
			}

			if ((getNpcTemplate().getKarma() < 0 && pc.getKarmaLevel() >= 1)
					|| (getNpcTemplate().getKarma() > 0 && pc.getKarmaLevel() <= -1)) {
				continue;
			}

			// 버땅 퀘스트의 변신, 각 진영의 monster로부터 선제 공격받지 않는다
			if (pc.getGfxId().getTempCharGfx() == 6034
					&& getNpcTemplate().getKarma() < 0
					|| pc.getGfxId().getTempCharGfx() == 6035
					&& getNpcTemplate().getKarma() > 0
					|| pc.getGfxId().getTempCharGfx() == 6035
					&& getNpcTemplate().get_npcId() == 46070
					|| pc.getGfxId().getTempCharGfx() == 6035
					&& getNpcTemplate().get_npcId() == 46072) {
				continue;
			}

			if (!getNpcTemplate().is_agro() && !getNpcTemplate().is_agrososc()
					&& getNpcTemplate().is_agrogfxid1() < 0
					&& getNpcTemplate().is_agrogfxid2() < 0) {
				if (pc.getLawful() < -1000) { // -1000
					targetPlayer = pc;
					break;
				}
				continue;
			}

			if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(67)) {
					if (getNpcTemplate().is_agrososc()) {
						targetPlayer = pc;
						break;
					}
				} else if (getNpcTemplate().is_agro()) {
					targetPlayer = pc;
					break;
				}

				if (getNpcTemplate().is_agrogfxid1() >= 0
						&& getNpcTemplate().is_agrogfxid1() <= 4) {
					if (_classGfxId[getNpcTemplate().is_agrogfxid1()][0] == pc
							.getGfxId().getTempCharGfx()
							|| _classGfxId[getNpcTemplate().is_agrogfxid1()][1] == pc
									.getGfxId().getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getGfxId().getTempCharGfx() == getNpcTemplate()
						.is_agrogfxid1()) {
					targetPlayer = pc;
					break;
				}

				if (getNpcTemplate().is_agrogfxid2() >= 0
						&& getNpcTemplate().is_agrogfxid2() <= 4) {
					if (_classGfxId[getNpcTemplate().is_agrogfxid2()][0] == pc
							.getGfxId().getTempCharGfx()
							|| _classGfxId[getNpcTemplate().is_agrogfxid2()][1] == pc
									.getGfxId().getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getGfxId().getTempCharGfx() == getNpcTemplate()
						.is_agrogfxid2()) {
					targetPlayer = pc;
					break;
				}
			}
		} //요기부터 허수아비 패기
		for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
	        if (obj instanceof L1ScarecrowInstance) {
	        	L1ScarecrowInstance mon = (L1ScarecrowInstance) obj;
	         if(mon.getHiddenStatus() != 0 || mon.isDead()){
	         continue;
	         } 
	if(this.getNpcTemplate().get_npcId()==7000007|| getNpcTemplate().get_npcId()==7000008 
			|| getNpcTemplate().get_npcId()==7000009 || getNpcTemplate().get_npcId()==7000010 
			|| getNpcTemplate().get_npcId()==7000011 ){ //적을 인식할 몬스터 
	    if(mon.getNpcTemplate().get_npcId() == 45002){
	    	targetScarecrow = mon;
	    break;
	    }
	}
		if(this.getNpcTemplate().get_npcId()==7000012 || getNpcTemplate().get_npcId()==7000013
				|| getNpcTemplate().get_npcId()==7000014 || getNpcTemplate().get_npcId()==7000015 
				|| getNpcTemplate().get_npcId()==7000016){ //적을 인식할 몬스터 
				    if(mon.getNpcTemplate().get_npcId() == 45001){
				    	targetScarecrow = mon;
				    break;
	         }
	   } 
	}
	} //<<추가
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
		if(targetScarecrow != null){ 
		      _hateList.add(targetScarecrow, 0);
		      _target = targetScarecrow;
		      } //<<허수아비 패기
	}

	@Override
	public void setLink(L1Character cha) {
		if (cha != null) {
			if (_hateList.isEmpty()) {
				_hateList.add(cha, 0);
				checkTarget();
			}
		}
	}

	public L1MonsterInstance(L1Npc template) {
		super(template);
		_storeDroped = 1;
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		if (_storeDroped == 1) {
			DropTable.getInstance().setDrop(this, getInventory());
			getInventory().shuffle();
			_storeDroped = 0;
		} else if (_storeDroped == 2) {
			DropTable.getInstance().setPainwandDrop(this, getInventory());
			getInventory().shuffle();
			_storeDroped = 0;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		if (pc == null)
			return;
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		String htmlid = null;
		String[] htmldata = null;

		// html 표시 패킷 송신
		if (htmlid != null) { // htmlid가 지정되고 있는 경우
			if (htmldata != null) { // html 지정이 있는 경우는 표시
				pc.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
			} else {
				pc.sendPackets(new S_NPCTalkReturn(objid, htmlid));
			}
		} else {
			if (pc.getLawful() < -1000) { // -1000플레이어가 카오틱
				pc.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
			} else {
				pc.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
			}
		}
	}

	@Override
	public void onAction(L1PcInstance pc) {
		if (pc == null)
			return;
		if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(pc, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.addPcPoisonAttack(pc, this);
			}
			attack.action();
			attack.commit();
		}
	}

	@Override
	public void ReceiveManaDamage(L1Character attacker, int mpDamage) {
		if (attacker == null)
			return;
		if (mpDamage > 0 && !isDead()) {
			setHate(attacker, mpDamage);

			onNpcAI();

			if (attacker instanceof L1PcInstance) {
				serchLink((L1PcInstance) attacker, getNpcTemplate()
						.get_family());
			}

			int newMp = getCurrentMp() - mpDamage;
			if (newMp < 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}

	public int testss(L1Character pc) {
		if (pc.getSkillEffectTimerSet().hasSkillEffect(7836)) {
			return 1;
		} else
			return 2;
	}
	private int transRiperid(int mapid) {
	       int id = (mapid - 100) / 10;
	      int mobid = 0;
	      switch (id) {
	       case 0: 
	    	   mobid = 45513; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 1층대 - 10층보스
	       case 1: 
	    	   mobid = 45547; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 10층대 - 20층보스
	       case 2: 
	    	   mobid = 45606; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 20층대 - 30층보스
	      case 3: 
	    	  mobid = 45650; 
	    	  Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	  break; // 30층대 - 40층보스
	       case 4: 
	    	   mobid = 45652; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 40층대 - 50층보스
	       case 5: 
	    	   mobid = 45653; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 50층대 - 60층보스
	       case 6: 
	    	   mobid = 45654; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 60층대 - 70층보스
	      case 7: 
	    	  mobid = 45618; 
	    	  Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	  break; // 70층대 - 80층보스
	      case 8: 
	    	  mobid = 45672; 
	    	  Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	  break; // 80층대 - 90층보스
	       case 9: 
	    	   mobid = 45673; 
	    	   Broadcaster.broadcastPacket(this, new S_SkillSound(getId(), 4784));
	    	   break; // 90층대 - 100층보스
	       }
	      return mobid;
	    }
	 
	   private void dead(L1Character attacker) {
	      setCurrentHp(0);
	       setDead(true);
	       setActionStatus(ActionCodes.ACTION_Die);
	      dead.setAttacker(attacker);
	      dead.run();  
	    }
	@Override
	public void receiveDamage(L1Character attacker, int damage) {
		if (attacker == null)
			return;
		if (getCurrentHp() > 0 && !isDead()) {

			if (getHiddenStatus() != HIDDEN_STATUS_NONE
					|| getHiddenStatus() == HIDDEN_STATUS_FLY) {
				return;
			}
			if (damage >= 0) {
				if (!(attacker instanceof L1EffectInstance)) { 
					setHate(attacker, damage);
				}
			}
			if (damage > 0) {
				if (getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.FOG_OF_SLEEPING)) {
					getSkillEffectTimerSet().removeSkillEffect(
							L1SkillId.FOG_OF_SLEEPING);
				} else if (getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.PHANTASM)) {
					getSkillEffectTimerSet().removeSkillEffect(
							L1SkillId.PHANTASM);
				}
			}

			onNpcAI();

			if (attacker instanceof L1PcInstance) {
				serchLink((L1PcInstance) attacker, getNpcTemplate()
						.get_family());
			}

			if (attacker instanceof L1PcInstance && damage > 0) {
				L1PcInstance player = (L1PcInstance) attacker;
				player.setPetTarget(this);
							//보스몹강제소환
				if (getNpcTemplate().get_npcId() == 45681
						|| getNpcTemplate().get_npcId() == 45601
						|| getNpcTemplate().get_npcId() == 45682
						|| getNpcTemplate().get_npcId() == 45683
						|| getNpcTemplate().get_npcId() == 500060
						|| getNpcTemplate().get_npcId() == 500061
						|| getNpcTemplate().get_npcId() == 81163
						|| getNpcTemplate().get_npcId() == 4036016
						|| getNpcTemplate().get_npcId() == 4036017
						|| getNpcTemplate().get_npcId() == 45684
						|| getNpcTemplate().get_npcId() == 45617
						|| getNpcTemplate().get_npcId() == 45516
						|| getNpcTemplate().get_npcId() == 45545
						|| getNpcTemplate().get_npcId() == 460000035 //대왕오징어
						|| getNpcTemplate().get_npcId() == 460000036 //대왕오징어 다리
						//|| getNpcTemplate().get_npcId() == 65498 //샌드웜
						|| getNpcTemplate().get_npcId() == 65499 //샌드웜 분신
						|| getNpcTemplate().get_npcId() == 45529) {
					recall(player);
				}
}
				if (getNpcTemplate().get_npcId() == 4039000) {
					sendPackets(new S_SkillSound(this.getId(), 761));
					Broadcaster.broadcastPacket(this, new S_SkillSound(this
							.getId(), 761));
				}
				if (getNpcTemplate().get_npcId() == 4039003) {
					damage = 0;
					return;

				}
			
			int newHp = getCurrentHp() - damage;
			if (newHp <= 0 && !isDead()) { 
				
		        if ((getNpcTemplate().get_npcId() == 5000115) && 
		                ((attacker instanceof L1PcInstance))) {
		                L1PcInstance pc = (L1PcInstance)attacker;
		                if ((pc != null) && (pc.getMapId() >= 807) && (pc.getMapId() <= 813)) {
		                  L1Location newLocation = pc.getLocation().randomLocation(200, true);
		                  int x = newLocation.getX();
		                  int y = newLocation.getY();
		                  short mapid = (short)newLocation.getMapId();
		                  int heading = pc.getMoveState().getHeading();
		                  L1Teleport.teleport(pc, x, y, mapid, heading, true);
		                }
		              }

		              if ((getNpcTemplate().get_npcId() == 5000116) && 
		                ((attacker instanceof L1PcInstance))) {
		                L1PcInstance pc = (L1PcInstance)attacker;
		                if ((pc != null) && (pc.getMapId() >= 807) && (pc.getMapId() <= 813)) {
		                  L1Location newLocation = pc.getLocation().randomLocation(200, true);
		                  int x = newLocation.getX();
		                  int y = newLocation.getY();
		                  short mapid = (short)newLocation.getMapId();
		                  int heading = pc.getMoveState().getHeading();
		                  if (pc.getMapId() == 807)
		                    L1Teleport.teleport(pc, x, y, mapid, heading, true);
		                  else {
		                    L1Teleport.teleport(pc, x, y, (short)(mapid - 1), heading, true);
		                  }
		                }
		              }
				
				Random random = new Random();    // 추가
				int chance1 = random.nextInt(100) + 1;
				int chance2 = random.nextInt(100) + 1; 
				int rnd = 25 * random.nextInt(100); //서브퀘스트 추가
					// 퀘스트 드랍 부분체크
				if (Config.RATE_SILVER > chance1) { //서브퀘스트 빛나는 구슬 드랍
				     if (attacker.getInventory().checkItem(5437)) { //빛나는구슬 드랍 자동 ( Db배치노 )
				      if (getNpcTemplate().get_npcId() >= 123478 //몬스터번호
					    	  || getNpcTemplate().get_npcId() == 123466	//은기사 필드 리뉴얼
					    	  || getNpcTemplate().get_npcId() == 123467	//은기사 필드 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123468	//은기사 필드 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123469	//은기사 필드 리뉴얼
						      || getNpcTemplate().get_npcId() == 123470//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123471//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123472	//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123473//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123474//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123475//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123476	//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123477//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123478//은기사 필드 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123479)//은기사 필드 리뉴얼
				       getInventory().storeItem(5439, 1); // 토벌의증표
				     }
				    }
				    if (Config.RATE_SILVER > chance1) { //서브퀘스트 뼈수집꾼
					     if (attacker.getInventory().checkItem(5456)) { //영롱한구슬 번호
						      if (getNpcTemplate().get_npcId() >= 980076 //변종 스컬드래곤
						    	  || getNpcTemplate().get_npcId() == 980072	//변종 유니드래곤
						    	  || getNpcTemplate().get_npcId() == 980073	//변종 유니드래곤
						    	  || getNpcTemplate().get_npcId() == 980074	//변종 유니드래곤
					    	  	  || getNpcTemplate().get_npcId() == 980075	//변종 유니드래곤
					    	  	  || getNpcTemplate().get_npcId() == 980076	//변종 유니드래곤
							      || getNpcTemplate().get_npcId() == 4500124//변종 유니드래곤
								  || getNpcTemplate().get_npcId() == 4500125//변종 유니드래곤
					    	  	  || getNpcTemplate().get_npcId() == 4200123)//드래곤 수호자
						       getInventory().storeItem(5440, 1); // 변종드래곤의뼈 
						     }
						    }
				    if (Config.RATE_SILVER > chance2) { //여기 부터 서브퀘시작
				     if (attacker.getsub() == 1) {
				      if (getNpcTemplate().get_npcId() >= 123550 //수련 던전 리뉴얼
					    	  || getNpcTemplate().get_npcId() == 123551	//은기사 필드 리뉴얼
					    	  || getNpcTemplate().get_npcId() == 123552	//은기사 필드 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123553	//은기사 필드 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123450	//은기사 필드 리뉴얼
						      || getNpcTemplate().get_npcId() == 123451//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123452//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123453	//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123454//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123455//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123456//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123457	//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123458//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123459//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123460//은기사 필드 리뉴얼
							  || getNpcTemplate().get_npcId() == 123461//은기사 필드 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123462)//은기사 필드 리뉴얼
				       getInventory().storeItem(5440, 1); // 몬스터발톱
				     }
				    }
				    if (Config.RATE_SILVER > chance2) {
				     if (attacker.getsub() == 2) {
				    	 if (getNpcTemplate().get_npcId() >= 123550 //수련 던전 리뉴얼
						    	  || getNpcTemplate().get_npcId() == 123551	//수련 던전 리뉴얼
						    	  || getNpcTemplate().get_npcId() == 123552	//수련 던전 리뉴얼
					    	  	  || getNpcTemplate().get_npcId() == 123553	//수련 던전 리뉴얼
					    	  	  || getNpcTemplate().get_npcId() == 123450	//수련 던전 리뉴얼
							      || getNpcTemplate().get_npcId() == 123451//수련 던전 리뉴얼
								  || getNpcTemplate().get_npcId() == 123452//수련 던전 리뉴얼
								  || getNpcTemplate().get_npcId() == 123453	//수련 던전 리뉴얼
								  || getNpcTemplate().get_npcId() == 123454//수련 던전 리뉴얼
								  || getNpcTemplate().get_npcId() == 123455//수련 던전 리뉴얼
								  || getNpcTemplate().get_npcId() == 123456//수련 던전 리뉴얼
								  || getNpcTemplate().get_npcId() == 123457	//수련 던전 리뉴얼
								  || getNpcTemplate().get_npcId() == 123458//수련 던전 리뉴얼
								  || getNpcTemplate().get_npcId() == 123459//수련 던전 리뉴얼
								  || getNpcTemplate().get_npcId() == 123460//수련 던전 리뉴얼
								  || getNpcTemplate().get_npcId() == 123461//수련 던전 리뉴얼
					    	  	  || getNpcTemplate().get_npcId() == 123462)//수련 던전 리뉴얼
				       getInventory().storeItem(5441, 1); // 몬스터이빨
				     }
				    }
				    if (Config.RATE_SILVER > chance2) {
				     if (attacker.getsub() == 3) {
				      if (getNpcTemplate().get_npcId() >= 45119 //1층
					    	  || getNpcTemplate().get_npcId() == 123465	//수련 던전 리뉴얼
					    	  || getNpcTemplate().get_npcId() == 123466	//수련 던전 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123467	//수련 던전 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123468	//수련 던전 리뉴얼
						      || getNpcTemplate().get_npcId() == 123469//수련 던전 리뉴얼
							  || getNpcTemplate().get_npcId() == 123470//수련 던전 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123471)//수련 던전 리뉴얼
				       getInventory().storeItem(5442, 1); // 녹슨투구
				     }
				    }
				    if (Config.RATE_SILVER > chance2) {
				     if (attacker.getsub() == 4) {
				      if (getNpcTemplate().get_npcId() >= 45119 //2층
					    	  || getNpcTemplate().get_npcId() == 123468	//수련 던전 리뉴얼
					    	  || getNpcTemplate().get_npcId() == 123470	//수련 던전 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123472	//수련 던전 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123473	//수련 던전 리뉴얼
						      || getNpcTemplate().get_npcId() == 123474//수련 던전 리뉴얼
							  || getNpcTemplate().get_npcId() == 123471//수련 던전 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123475)//수련 던전 리뉴얼
				       getInventory().storeItem(5443, 1); // 녹슨장갑
				     }
				    }
				    if (Config.RATE_SILVER > chance2) {
				     if (attacker.getsub() == 6) {
				      if (getNpcTemplate().get_npcId() >= 45082 //수련던전 3~4층몬스터번호들
					    	  || getNpcTemplate().get_npcId() == 123475	//수련 던전 리뉴얼
					    	  || getNpcTemplate().get_npcId() == 123477	//수련 던전 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123478	//수련 던전 리뉴얼
				    	  	  || getNpcTemplate().get_npcId() == 123479)//수련 던전 리뉴얼
				       getInventory().storeItem(5444, 1); // 녹슨장화
				     }
				    }
			
				if(Config.RATE_DREAM > chance1){ 
					getInventory().storeItem(438015, 1); // 만월의정기 모든몹드랍
				}
				if(Config.RATE_DREAM > chance2){ 
					getInventory().storeItem(438015, 1); // 위와 마찬가지
				}
				
				int npcid = getNpcTemplate().get_npcId();
					if (npcid == 45956) {
					for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
						if (pc.getMapId() == 531) {
						pc.sendPackets(new S_SystemMessage("비아타스 : 라스타바드의 영광이여, 영원 하라!"));
						}
					}
				}
				if (npcid == 45957) {
					for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
						if (pc.getMapId() == 531) {
						pc.sendPackets(new S_SystemMessage("바로메스 : 이대로 무너질 라스타바드가 아니다. 죽어서도 잠들지 않으리라."));
						}
					}
				}
				if (npcid == 45958) {
					for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
						if (pc.getMapId() == 531) {
						pc.sendPackets(new S_SystemMessage("엔디아스 : 신이시여! 어찌하여 저희를 버리십니까?"));
						}
					}
				}
				if (npcid == 46141 || npcid == 46142) { //얼던공략얼녀
					if (attacker instanceof L1PcInstance) { 
						GeneralThreadPool.getInstance().schedule(new KillBossBuffTimer(this), 10 * 1000);
					}
				}
				if (npcid == 4036016 || npcid == 4036017// 제브레퀴 공략후
						|| npcid == 400016 || npcid == 400017||//테베
						npcid == 4039020||npcid == 4039000 ||npcid == 4039021||npcid == 4039006
						|| npcid == 9170 || npcid == 9171) { //1~2차용린드레이드
					if (attacker instanceof L1PcInstance) { 
						GeneralThreadPool.getInstance().schedule(new KillBossBuffTimer(this), 10 * 1000);
					}
				}
				 if (npcid == 4039000) { // 엔피씨 번호
						String chat = "파푸리온 : 놀잇감으로는 충분하구나! 흐흐흐...";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
				} 
				 if (npcid == 4039006) { // 엔피씨 번호
						String chat = "파푸리온 : 뼈 속까지 파고드는 두려움이 무엇인지 이 몸이 알게 해주마!";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
				}
				if (npcid == 4039007) { // 엔피씨 번호
						String chat = "파푸리온 : 사엘..네 녀석이..어떻게...나의 어머니..실렌이시여 나의 숨을..거두소서...";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
						L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("레이드 파푸리온이 공략 당했습니다."));
						 dragonportalspawn(4212013,33726,32506,(short)4,6,720);
						 L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("난쟁이의 외침 : 웰던 마을에 숨겨진 용들의 땅으로 가는 문이 열렸습니다."));
						if (attacker instanceof L1PcInstance) {
							GeneralThreadPool.getInstance().schedule(new KillBossBuffTimer(this), 10 * 1000);
					}
					}
				 if (npcid == 4039020) { // 
						String chat = "안타라스 : 이제 맛있는 식사를 해볼까? 너희 피냄새가 나를 미치게 하는구나.";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
				}
				 if (npcid == 4039021) { // 엔피씨 번호 
						String chat = "안타라스 : 나의 분노가 하늘에 닿았다. 이제 곧 나의 아버지가 나설 것이다.";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
				}
				 if (npcid == 4039022) { // 엔피씨 번호 
						String chat = "안타라스 : 황혼의 저주가 그대들에게 있을 지어다! 실렌이여. 나의 어머니여. 나의 숨을.. 거두소서...";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
						L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("레이드 안타라스가 공략 당했습니다."));
						 dragonportalspawn(4212013,33726,32506,(short)4,6,720);
					    L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("난쟁이의 외침 : 웰던 마을에 숨겨진 용들의 땅으로 가는 문이 열렸습니다."));
						if (attacker instanceof L1PcInstance) {
							GeneralThreadPool.getInstance().schedule(
									new KillBossBuffTimer(this), 10 * 1000);
					}
					}
				 if (npcid == 9170) { //린드레이드1 
						String chat = "린드비오르 : 그래도 제법이구나! 하지만 언제까지 버틸 수 있을까?";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
			    }
				 if (npcid == 9171) { //린드레이드2 
						String chat = "린드비오르 : 조금 얕보았던 것같군. 이번엔 어떨지 궁금하군.";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
				}
				 if (npcid == 9172) { //린드레이드3 
						String chat = "린드비오르 : 아아~!! 나의 어머니 실렌이여 나를 붙잡아 주소서....";
						Broadcaster.broadcastPacket(this, new S_SystemMessage(chat));
						L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("레이드 린드비오르가 공략 당했습니다."));
						 dragonportalspawn(4212013,33726,32506,(short)4,6,720);
						 L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("난쟁이의 외침 : 웰던 마을에 숨겨진 용들의 땅으로 가는 문이 열렸습니다."));
						if (attacker instanceof L1PcInstance) {
							GeneralThreadPool.getInstance().schedule(
									new KillBossBuffTimer(this), 10 * 1000);
					}
					}
					if (npcid == 9187) { //린드레이드
						String chat = "우우..감히 린드비오르님의 심기를 불편하게 하다니!";
						Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
								chat, 0));
					}
				if (npcid == 45590) { // 엔피씨 번호
					String chat = "크윽..인간들 치고는 제법이구나..";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45513) { //  
					String chat = "으아악! 이런 말도 안되는 일이!";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45547) { // 엔피씨 번호
					String chat = "에보루타인... 당신..어디 계신가요...";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45606) { // 엔피씨 번호
					String chat = "뀌에에에엑!...";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45650) { // 엔피씨 번호
					String chat = "이..이럴수가..내 마성의 힘을 이겨내다니..";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45652) { // 엔피씨 번호
					String chat = "내..내...오른손만..있었어도..부..분하다..";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45653) { // 엔피씨 번호
					String chat = "크르르르르르...";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45654) { // 엔피씨 번호
					String chat = "아..아버지...어디 계십니까..?";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45618) { // 엔피씨 번호
					String chat = "나..나의 제국은 어떻게..되었는가?..";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45672) { // 엔피씨 번호
					String chat = "나를 쓰러트린 것이 그대들인가? 나의 왕을 구해다오..부탁하지..";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 45673) { // 엔피씨 번호
					String chat = "발드..발드 그는 무사한가? 제니스..그녀는?";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 46141) { //얼녀
					String chat = "혹한의 바람이여 이들의 숨결조차 얼어붙게 하라!";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}else if (npcid == 46142) { //데몬
					String chat = "얼음 칼날들이여 저들을 모두 베어버려라!";
					Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this,
							chat, 0));
				}
				int transformId = getNpcTemplate().getTransformId();
				int mapid = getMapId();
				 if ((getMapId() >= 101 && getMapId() <= 200)
				      && mapid % 10 != 0) {
				   int rnd_Reper = _random.nextInt(1000);//rnd_Reper
				   if (rnd < 20) { // 0.5%
				      int rid = transRiperid(mapid);
				      if (getNpcTemplate().get_npcId() != 45590 && getNpcTemplate().get_npcId() != rid)
				         transform(45590); // 감시자 리퍼
				      else if (getNpcTemplate().get_npcId() == 45590) { // 감시자 리퍼
				        transform(rid);
				      }
				    } else   dead(attacker);
				} else if (transformId == -1) {
					setCurrentHp(0);
					setDead(true);
					setActionStatus(ActionCodes.ACTION_Die);

					dead.setAttacker(attacker);
				dead.run();
					if(ismarble()){
					
			    		setCurrentHp(0);
						setDead(true);
						attacker.marble.remove("오색구슬");
						hprsize = attacker.marble.size();
						setActionStatus(ActionCodes.ACTION_Die);
						dead.setAttacker(attacker);
						dead.run();
						if(attacker.marble.size() == 0 && attacker.marble2.size()==0){
						}
					}
					if (ismarble2()) {
						setCurrentHp(0);
						setDead(true);
						attacker.marble2.remove("신비한오색구슬");

						if (attacker.marble2.size() < 1) {
							sendPackets(new S_SkillHaste(this.getId(), 0, 0));
							Broadcaster.broadcastPacket(this, new S_SkillHaste(
									this.getId(), 0, 0));
							getMoveState().setMoveSpeed(0);
						}
						setActionStatus(ActionCodes.ACTION_Die);
						dead.setAttacker(attacker);
						dead.run();
					}
				} else {

					if (isAntharas()) {
						setCurrentHp(0);
						setDead(true);
						dieAntharas(attacker);
					}else if (isPapoo()) {
						setCurrentHp(0);
						setDead(true);
						diePaPoo(attacker);
					}else if (isPind()) {
						setCurrentHp(0);
						setDead(true);
						diePind(attacker);
					}

					else {
						transform(transformId);
					}
				}
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
				hide();
			}
		} else if (!isDead()) {
			setDead(true);
			setActionStatus(ActionCodes.ACTION_Die);
			dead.setAttacker(attacker);
			dead.run();
		}
	}
	public void setDeath(Dead d) {
		dead = d;
	}

	private void recall(L1PcInstance pc) {
		if (pc == null || getMapId() != pc.getMapId()) {
			return;
		}
		if (getLocation().getTileLineDistance(pc.getLocation()) > 4) {
			L1Location newLoc = null;
			for (int count = 0; count < 10; count++) {
				newLoc = getLocation().randomLocation(3, 4, false);
				if (CharPosUtil.glanceCheck(this, newLoc.getX(), newLoc.getY())) {
					L1Teleport.teleport(pc, newLoc.getX(), newLoc.getY(),
							getMapId(), 5, true);
					break;
				}
			}
		}
	}

	@Override
	public void setCurrentHp(int i) {
		super.setCurrentHp(i);

		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}
	}

	@Override
	public void setCurrentMp(int i) {
		super.setCurrentMp(i);

		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
	}

	public void die(L1Character lastAttacker) {
		try {
	
			setDeathProcessing(true);
			setCurrentHp(0);
			setDead(true);
			setActionStatus(ActionCodes.ACTION_Die);
			getMap().setPassable(getLocation(), true);
			Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(),ActionCodes.ACTION_Die));
			startChat(CHAT_TIMING_DEAD);
			distributeExpDropKarma(lastAttacker);
			giveUbSeal();
			setDeathProcessing(false);
			setExp(0);
			setKarma(0);
			setLawful(0);
			allTargetClear();
			startDeleteTimer();
		} catch (Exception e) {
		}
	}
	public void die3() {
		try {
			setDeathProcessing(true);
			setCurrentHp(0);
			setDead(true);
			getMap().setPassable(getLocation(), true);
			startChat(CHAT_TIMING_DEAD);
			setDeathProcessing(false);
			setExp(0);
			setKarma(0);
			setLawful(0);
			allTargetClear();
			deleteMe();
		} catch (Exception e) {
		}
	}

	private void die2(L1Character lastAttacker) {
		try {
			setDeathProcessing(true);
			setCurrentHp(0);
			setDead(true);
			getMap().setPassable(getLocation(), true);
			startChat(CHAT_TIMING_DEAD);
			setDeathProcessing(false);
			setExp(0);
			setKarma(0);
			setLawful(0);
			allTargetClear();
			int transformGfxId = getNpcTemplate().getTransformGfxId();
			if (transformGfxId > 0)
				Broadcaster.broadcastPacket(this, new S_SkillSound(getId(),
						transformGfxId));
			deleteMe();
			GeneralThreadPool.getInstance().schedule(new GiranTransTimer(this),
					400);
		} catch (Exception e) {
		}
	}
	private void dieAntharas(L1Character lastAttacker) {
		setDeathProcessing(true);
		setCurrentHp(0);
		setDead(true);
		getMap().setPassable(getLocation(), true);
		startChat(CHAT_TIMING_DEAD);
		setDeathProcessing(false);
		setExp(0);
		setKarma(0);
		allTargetClear();
		int transformGfxId = getNpcTemplate().getTransformGfxId();
		if (transformGfxId > 0)
			Broadcaster.broadcastPacket(this, new S_SkillSound(getId(),
					transformGfxId));
		setActionStatus(ActionCodes.ACTION_Die);
		Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(),
				ActionCodes.ACTION_Die));
		deleteMe();
		int npcid = getNpcTemplate().get_npcId();
		switch (npcid) {
		case 4039020:
			GeneralThreadPool.getInstance().schedule(
					new AntharasTransTimer(this), 20 * 1000);
			break;
		case 4039021:
			GeneralThreadPool.getInstance().schedule(
					new AntharasTransTimer2(this), 20 * 1000);
			break;
		}
	}
	private static class AntharasTransTimer implements Runnable{//extends TimerTask {
		L1NpcInstance _npc;

		private AntharasTransTimer(L1NpcInstance some) {
			_npc = some;
		}

		public void run() {
			L1SpawnUtil.spawn2(_npc.getX(), _npc.getY(), (short) _npc.getMap().getId(), 4039021, 0, 0, 0);
		}
	}
	private static class AntharasTransTimer2 implements Runnable{
		L1NpcInstance _npc;

		private AntharasTransTimer2(L1NpcInstance some) {
			_npc = some;
		}

		public void run() {
			L1SpawnUtil.spawn2(_npc.getX(), _npc.getY(), (short) _npc.getMap().getId(), 4039022, 0, 0, 0);
		}
	}
	private static class KillBossBuffTimer implements Runnable{
		int _bosstype = 0;
		L1SkillUse l1skilluse = new L1SkillUse();
		L1MonsterInstance mon = null;

		private KillBossBuffTimer(L1MonsterInstance npc) {
			mon = npc;
			_bosstype = npc.getNpcTemplate().get_npcId();
		}

		public void run() {
			try {
				if (_bosstype != 0) {
					if (_bosstype == 4039022) { // 안타라스의경우
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(pc == null)
								continue;
							pc.setBuffnoch(1);
			                if(pc.getMapId() == 1005){//이맵유저만
			                	pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
			                	l1skilluse.handleCommands(pc,L1SkillId.DRAGONBLOOD_ANTA, pc.getId(), pc.getX(), pc.getY(), null, 0,L1SkillUse.TYPE_GMBUFF);//버프주고
			                	pc.sendPackets(new S_ServerMessage(1628));//메세지
			                	pc.setBuffnoch(0);
			                }
						}
						Mapdrop(mon);
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			                if(pc.getMapId() == 1005){//이맵유저만
			                	pc.getInventory().storeItem(408986, 1);//표식주기
			                	pc.sendPackets(new S_SystemMessage("지룡의 표식이 지급 되었습니다."));//메세지
			                //	pc.sendPackets(new S_ServerMessage(1477));
			                	S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"20초 후 마을로 자동 귀환 됩니다.", Opcodes.S_OPCODE_MSG, 20); 
			                	pc.sendPackets(s_chatpacket);
			                	pc.sendPackets(new S_ServerMessage(1581));
			                }
						}
	                	Thread.sleep(20000);//20초후마을텔
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			                if(pc!=null && pc.getMapId() == 1005){//이맵유저만
			                	L1Teleport.teleport(pc, 33700, 32505, (short) 4, 5, true); // 웰던 마을
			                }
						}
					}
					if (_bosstype == 4039007) { // 파푸경우
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(pc == null)
								continue;
							pc.setBuffnoch(1);
							if(pc.getMapId() == 1011){//이맵유저만
								pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
								l1skilluse.handleCommands(pc, L1SkillId.DRAGONBLOOD_PAP, pc.getId(), pc.getX(), pc.getY(), null, 0,L1SkillUse.TYPE_GMBUFF);
								pc.sendPackets(new S_ServerMessage(1644));
								pc.setBuffnoch(0);
							}
						}
						Mapdrop(mon);
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			                if(pc.getMapId() == 1011){//이맵유저만
								pc.getInventory().storeItem(408987, 1);//표식주기
								pc.sendPackets(new S_SystemMessage("수룡의 표식이 지급 되었습니다."));//메세지
							//	pc.sendPackets(new S_ServerMessage(1477));
			                	S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"20초 후 마을로 자동 귀환 됩니다.", Opcodes.S_OPCODE_MSG, 20); 
			                	pc.sendPackets(s_chatpacket);
								pc.sendPackets(new S_ServerMessage(1669));
			                }
			                }
	                	Thread.sleep(20000);//20초후마을텔
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			                if(pc != null && pc.getMapId() == 1011){//이맵유저만
			                	L1Teleport.teleport(pc, 33700, 32505, (short) 4, 5, true); // 웰던 마을
			                }
						}
					}
					if (_bosstype == 9172) { // 린드레이드
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(pc == null)
								continue;
							pc.setBuffnoch(1);
			                if(pc.getMapId() == 1017){//이맵유저만
			                	pc.sendPackets(new S_SkillSound(pc.getId(), 7783));
			                	l1skilluse.handleCommands(pc,L1SkillId.DRAGONBLOOD_RIND, pc.getId(), pc.getX(), pc.getY(), null, 0,L1SkillUse.TYPE_GMBUFF);//버프주고
			                	pc.sendPackets(new S_SystemMessage("린드비오르의 혈흔에 의해 강해진 느낌이 듭니다."));//메세지
			                	pc.setBuffnoch(0);
			                }
						}
						Mapdrop(mon);
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			                if(pc.getMapId() == 1017){//이맵유저만
			                	pc.getInventory().storeItem(408989, 1);//표식주기
			                	pc.sendPackets(new S_SystemMessage("풍룡의 표식이 지급 되었습니다."));//메세지
			                	//pc.sendPackets(new S_ServerMessage(1477));
			                	S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"20초 후 마을로 자동 귀환 됩니다.", Opcodes.S_OPCODE_MSG, 20); 
			                	pc.sendPackets(s_chatpacket);
			                	pc.sendPackets(new S_SystemMessage("난쟁이의 외침 : 린드비오르의 날개를 꺾은 용사들이 탄생 하였습니다.!!"));
			                }
			                }
	                	Thread.sleep(20000);//20초후마을텔
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			                if(pc!=null && pc.getMapId() == 1017){//이맵유저만
			                	L1Teleport.teleport(pc, 33700, 32505, (short) 4, 5, true); // 웰던 마을
			                }
						}
					}
					if (_bosstype == 46142 || _bosstype == 46141) { // 데몬
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(pc == null)
								continue;
							if(pc.getMapId() == 2101 || pc.getMapId() == 2151){//이맵유저만
								pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"보스방 몬스터를 다잡으신후 벽 뒤에 있는 스빈을 만나십시오."));//메세지
							}
						}
					}
					if (_bosstype == 4036016) { // 재브래퀴 남,여
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(pc == null)
								continue;
							pc.setBuffnoch(1);
							if(pc.getMapId() == 784){//이맵유저만
							l1skilluse.handleCommands(pc,
									L1SkillId.STATUS_TIKAL_BOSSDIE, pc.getId(),
									pc.getX(), pc.getY(), null, 0,
									L1SkillUse.TYPE_GMBUFF);
							pc.sendPackets(new S_SystemMessage("제브레퀴가 공략당해 상아탑의 축복이 60분 유지됩니다."));
							pc.setBuffnoch(0);
						}
						}
						Mapdrop(mon);
					}
					if (_bosstype == 4036017) { // 재브래퀴 남,여
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(pc == null)
								continue;
							pc.setBuffnoch(1);
							if(pc.getMapId() == 784){//이맵유저만
							l1skilluse.handleCommands(pc,
									L1SkillId.STATUS_TIKAL_BOSSDIE, pc.getId(),
									pc.getX(), pc.getY(), null, 0,
									L1SkillUse.TYPE_GMBUFF);
							pc.sendPackets(new S_SystemMessage("제브레퀴가 공략당해 상아탑의 축복이 60분 유지됩니다."));
							pc.setBuffnoch(0);
						}
						}
						Mapdrop(mon);
					}
					if(_bosstype == 400016 || _bosstype == 400017){//테베보스
						Mapdrop(mon);
					}

					 if(_bosstype == 4039020||_bosstype == 4039000 ||_bosstype == 4039021||_bosstype == 4039006
							 ||_bosstype == 9170 ||_bosstype == 9171){//1~2차용린드레이드
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if(mon.getMapId() == pc.getMapId()){
								pc.getInventory().storeItem(439115, 1);
								pc.sendPackets(new S_SystemMessage("달아난 드래곤의 흔적이 지급 되었습니다."));//메세지
							}
						}
					 }
				}		
			} catch (Exception e) {
				System.out.println(e);
			}
		}	
					/**보스맵자동분배**/
		private void Mapdrop(L1NpcInstance npc){
			L1Inventory inventory = npc.getInventory();
			L1ItemInstance item;
			L1Inventory targetInventory = null;
			L1PcInstance player;
			Random random = new Random();
			L1PcInstance acquisitor;
			FastTable<L1PcInstance> acquisitorList = new FastTable<L1PcInstance>();
			L1PcInstance[] pclist = L1World.getInstance().getAllPlayersToArray();
			for(L1PcInstance temppc : pclist){
				if(temppc.getMapId() == npc.getMapId())
					acquisitorList.add(temppc);
			}
			for (int i = inventory.getSize(); i > 0; i--) {
				item = inventory.getItems().get(0);

				if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) {
					item.setNowLighting(false);
				}
				acquisitor = acquisitorList.get(random.nextInt(acquisitorList.size()));
				if (acquisitor.getInventory().checkAddItem(item,item.getCount()) == L1Inventory.OK) {
						targetInventory = acquisitor.getInventory();
						player = acquisitor;
						L1ItemInstance l1iteminstance = player.getInventory().findItemId(L1ItemId.ADENA); // 소지
						if (l1iteminstance != null&& l1iteminstance.getCount() > 2000000000) {
								targetInventory = L1World.getInstance().getInventory(acquisitor.getX(),acquisitor.getY(),acquisitor.getMapId()); // 가질 수
								player.sendPackets(new S_ServerMessage(166,"소지하고 있는 아데나","2,000,000,000을 초과하고 있습니다."));
						}else{
							for(L1PcInstance temppc : acquisitorList){
									temppc.sendPackets(new S_ServerMessage(813, npc.getName(), item.getLogName(), player.getName()));
							}
						}
				} else {
						targetInventory = L1World.getInstance().getInventory(acquisitor.getX(),acquisitor.getY(),acquisitor.getMapId()); // 가질 수
				}
				inventory.tradeItem(item, item.getCount(), targetInventory);
			}
			npc.getLight().turnOnOffLight();
		}
	}
	/**보스맵자동분배**/
	public void dragonportalspawn(int npcId, int x, int y, short mapid, int heading, int 
			   timeMinToDelete) {
			     try {
			      L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			      npc.setId(ObjectIdFactory.getInstance().nextId());
			      npc.setMap(mapid);
			      npc.setX(x);
			      npc.setY(y);
			      npc.setHomeX(npc.getX());
			      npc.setHomeY(npc.getY());
			      npc.getMoveState().setHeading(heading);
			      L1World.getInstance(). storeObject(npc);
			      L1World.getInstance(). addVisibleObject(npc);
			      if (0 < timeMinToDelete) {
			       L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc,
			         timeMinToDelete * 60 * 1000);
			       timer.begin();
			      }
			     } catch (Exception e) {
			      _log.log(Level.SEVERE, "L1MonsterInstance[]Error", e);
			     }
			    }
	/** 리뉴얼 파프
	 **/
	private void diePaPoo(L1Character lastAttacker) {
		setDeathProcessing(true);
		setCurrentHp(0);
		setDead(true);
		getMap().setPassable(getLocation(), true);
		startChat(CHAT_TIMING_DEAD);
		setDeathProcessing(false);
		setExp(0);
		setKarma(0);
		allTargetClear();
		int transformGfxId = getNpcTemplate().getTransformGfxId();
		if (transformGfxId > 0)
			Broadcaster.broadcastPacket(this, new S_SkillSound(getId(),
					transformGfxId));
		setActionStatus(ActionCodes.ACTION_Die);
		Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(),
				ActionCodes.ACTION_Die));
		deleteMe();
		GeneralThreadPool.getInstance().schedule(new PaPooTransTimer(this),
				20 * 1000);

	}
	private static class PaPooTransTimer implements Runnable {
		L1NpcInstance _npc;

		private PaPooTransTimer(L1NpcInstance some) {
			_npc = some;
		}

		public void run() {
			L1SpawnUtil.spawn2(_npc.getX(), _npc.getY(), (short) _npc.getMap().getId(), _npc.getNpcTemplate().getTransformId(), 0, 0, 0);
		}
	}
	private static class GiranTransTimer implements Runnable{
		L1NpcInstance _npc;

		private GiranTransTimer(L1NpcInstance some) {
			_npc = some;
		}

		public void run() {
			L1SpawnUtil.spawn2(_npc.getX(), _npc.getY(), (short) _npc.getMap().getId(), _npc.getNpcTemplate().getTransformId(), 0, 0, 0);
		}
	}
	/**린드레이드*/
	private void diePind(L1Character lastAttacker) {
		//System.out.println("c opcode");
		setDeathProcessing(true);
		setCurrentHp(0);
		setDead(true);
		getMap().setPassable(getLocation(), true);
		startChat(CHAT_TIMING_DEAD);
		setDeathProcessing(false);
		setExp(0);
		setKarma(0);
		allTargetClear();
		int transformGfxId = getNpcTemplate().getTransformGfxId();
		if (transformGfxId > 0)
			Broadcaster.broadcastPacket(this, new S_SkillSound(getId(),
					transformGfxId));
		setActionStatus(ActionCodes.ACTION_Die);
		Broadcaster.broadcastPacket(this, new S_DoActionGFX(getId(),
				ActionCodes.ACTION_Die));
		deleteMe();
		GeneralThreadPool.getInstance().schedule(new PindTransTimer(this),
				20 * 1000);

	}
	private static class PindTransTimer implements Runnable {
		L1NpcInstance _npc;

		private PindTransTimer(L1NpcInstance some) {
			_npc = some;
		}

		public void run() {
			L1SpawnUtil.spawn2(_npc.getX(), _npc.getY(), (short) _npc.getMap().getId(), _npc.getNpcTemplate().getTransformId(), 0, 0, 0);
		}
	}
	/**린드레이드*/
	private boolean isGDMonster() {
		int id = getNpcTemplate().get_npcId();
		if ((id >= 4037100 && id <= 4037102)
				|| (id >= 4037200 && id <= 4037202)
				|| (id >= 4037400 && id <= 4037403))
			return true;
		return false;
	}
	private boolean isAntharas() {
		int id = getNpcTemplate().get_npcId();
		if (id == 4039020 || id == 4039021) return true;
			return false;
	}
	private boolean isPapoo() {
		int id = getNpcTemplate().get_npcId();
		if (id == 4039000 || id == 4039006)
			return true;
		return false;
	}
	private boolean isPind() {//린드레이드
		int id = getNpcTemplate().get_npcId();
		if (id == 9170 || id == 9171)
			return true;
		return false;
	}

	private boolean ismarble() {
		int id = getNpcTemplate().get_npcId();
		if (id == 4039001)
			return true;
		return false;
	}

	private boolean ismarble2() {
		int id = getNpcTemplate().get_npcId();
		if (id == 4039002)
			return true;
		return false;
	}
	private void distributeExpDropKarma(L1Character lastAttacker) {
		if (lastAttacker == null) {
			return;
		}
		L1PcInstance pc = null;
		if (lastAttacker instanceof L1PcInstance) {
			pc = (L1PcInstance) lastAttacker;
		} else if (lastAttacker instanceof L1PetInstance) {
			pc = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
		} else if (lastAttacker instanceof L1SummonInstance) {
			pc = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
		}

		if (pc != null) {
			FastTable<L1Character> targetList = _hateList.toTargetFastTable();
			FastTable<Integer> hateList = _hateList.toHateFastTable();
			if (pc != null && pc.getRobotAi() == null) {
				int exp = getExp();
				CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				if (isDead()) {
					distributeDrop(pc);
					giveKarma(pc);
				}
			}
		} else if (lastAttacker instanceof L1EffectInstance) {
			FastTable<L1Character> targetList = _hateList.toTargetFastTable();
			FastTable<Integer> hateList = _hateList.toHateFastTable();
			if (hateList.size() != 0) {
				int maxHate = 0;
				for (int i = hateList.size() - 1; i >= 0; i--) {
					if (maxHate < ((Integer) hateList.get(i))) {
						maxHate = (hateList.get(i));
						lastAttacker = targetList.get(i);
					}
				}
				if (lastAttacker instanceof L1PcInstance) {
					pc = (L1PcInstance) lastAttacker;
				} else if (lastAttacker instanceof L1PetInstance) {
					pc = (L1PcInstance) ((L1PetInstance) lastAttacker)
							.getMaster();
				} else if (lastAttacker instanceof L1SummonInstance) {
					pc = (L1PcInstance) ((L1SummonInstance) lastAttacker)
							.getMaster();
				}
				int exp = getExp();
				CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				if (isDead()) {
					distributeDrop(pc);
					giveKarma(pc);
				}
			}
		}
	}

	private void distributeDrop(L1PcInstance pc) {
		FastTable<L1Character> dropTargetList = _dropHateList
				.toTargetFastTable();
		FastTable<Integer> dropHateList = _dropHateList.toHateFastTable();
		try {
			int npcId = getNpcTemplate().get_npcId();
			if (npcId != 45640
					|| (npcId == 45640 && getGfxId().getTempCharGfx() == 2332)) {
				DropTable.getInstance().dropShare(L1MonsterInstance.this,
						dropTargetList, dropHateList, pc);
			}
		} catch (Exception e) {
			//_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	private void giveKarma(L1PcInstance pc) {
		int karma = getKarma();
		if (karma != 0) {
			int karmaSign = Integer.signum(karma);
			int pcKarmaLevel = pc.getKarmaLevel();
			int pcKarmaLevelSign = Integer.signum(pcKarmaLevel);
			if (pcKarmaLevelSign != 0 && karmaSign != pcKarmaLevelSign) {
				karma *= 5;
			}
			pc.addKarma((int) (karma * Config.RATE_KARMA));
		}
	}

	private void giveUbSeal() {
		if (getUbSealCount() != 0) {
			L1UltimateBattle ub = UBTable.getInstance().getUb(getUbId());
			if (ub != null) {
				for (L1PcInstance pc : ub.getMembersArray()) {
					if (pc != null && !pc.isDead() && !pc.isGhost()) {
						if (_random.nextInt(10) <= 2) {
							pc.getInventory().storeItem(
									L1ItemId.UB_WINNER_PIECE, 1);
						}
						pc.getInventory().storeItem(Config.UB_SEAL_ITEM,
								getUbSealCount());
						pc.sendPackets(new S_ServerMessage(403, "$5448"));
					}
				}
			}
		}
	}

	public int get_storeDroped() {
		return _storeDroped;
	}

	public void set_storeDroped(int i) {
		_storeDroped = i;
	}

	private int _ubSealCount = 0;

	public int getUbSealCount() {
		return _ubSealCount;
	}

	public void setUbSealCount(int i) {
		_ubSealCount = i;
	}

	private int _ubId = 0; // UBID

	public int getUbId() {
		return _ubId;
	}

	public void setUbId(int i) {
		_ubId = i;
	}

	private void hide() {
		int npcid = getNpcTemplate().get_npcId();
		if (npcid == 45061 || npcid == 45161 || npcid == 45181
				|| npcid == 45455) {
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					Broadcaster.broadcastPacket(this, new S_DoActionGFX(
							getId(), ActionCodes.ACTION_Hide));
					setActionStatus(13);
					Broadcaster.broadcastPacket(this, new S_NPCPack(this));
				}
			}
		} else if (npcid == 45682) {
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					Broadcaster.broadcastPacket(this, new S_DoActionGFX(
							getId(), ActionCodes.ACTION_AntharasHide));
					setActionStatus(20);
					Broadcaster.broadcastPacket(this, new S_NPCPack(this));
				}
			}
		} else if (npcid == 45067 || npcid == 45264 || npcid == 45452
				|| npcid == 45090 || npcid == 45321 || npcid == 45445) {
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					Broadcaster.broadcastPacket(this, new S_DoActionGFX(
							getId(), ActionCodes.ACTION_Moveup));
					setActionStatus(4);
					Broadcaster.broadcastPacket(this, new S_NPCPack(this));
				}
			}
		} else if (npcid == 45681) {
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					Broadcaster.broadcastPacket(this, new S_DoActionGFX(
							getId(), ActionCodes.ACTION_Moveup));
					setActionStatus(11);
					Broadcaster.broadcastPacket(this, new S_NPCPack(this));
				}
			}
		}
	}

	public void initHide() {
		int npcid = getNpcTemplate().get_npcId();
		if (npcid == 45061 || npcid == 45161 || npcid == 45181
				|| npcid == 45455 || npcid == 400000 || npcid == 400001) {
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setActionStatus(13);
			}
		} else if (npcid == 45045 || npcid == 45126 || npcid == 45134
				|| npcid == 45281) {
			int rnd = _random.nextInt(3);
			if (1 > rnd) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setActionStatus(4);
			}
		} else if (npcid == 45067 || npcid == 45264 || npcid == 45452
				|| npcid == 45090 || npcid == 45321 || npcid == 45445) {
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
			setActionStatus(4);
		} else if (npcid == 45681) {
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
			setActionStatus(11);
		}
	}

	public void initHideForMinion(L1NpcInstance leader) {
		int npcid = getNpcTemplate().get_npcId();
		if (leader.getHiddenStatus() == L1NpcInstance.HIDDEN_STATUS_SINK) {
			if (npcid == 45061 || npcid == 45161 || npcid == 45181
					|| npcid == 45455 || npcid == 400000 || npcid == 400001) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setActionStatus(13);
			} else if (npcid == 45045 || npcid == 45126 || npcid == 45134
					|| npcid == 45281) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setActionStatus(4);
			}
		} else if (leader.getHiddenStatus() == L1NpcInstance.HIDDEN_STATUS_FLY) {
			if (npcid == 45067 || npcid == 45264 || npcid == 45452
					|| npcid == 45090 || npcid == 45321 || npcid == 45445) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
				setActionStatus(4);
			} else if (npcid == 45681) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
				setActionStatus(11);
			}
		}
	}

	public void getMarble(L1NpcInstance npc) {
		_attacker = npc;
		_attacker.marble.remove("오색구슬");
		_attacker.marble2.remove("신비한오색구슬");
	}

	@Override
	protected void transform(int transformId) {
		super.transform(transformId);
		getInventory().clearItems();
		DropTable.getInstance().setDrop(this, getInventory());
		getInventory().shuffle();
	}
}
