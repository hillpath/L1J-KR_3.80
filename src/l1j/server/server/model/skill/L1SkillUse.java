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

package l1j.server.server.model.skill;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastTable;
import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.Opcodes;
import l1j.server.server.clientpackets._random;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Cube;
import l1j.server.server.model.L1CurseParalysis;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1AuctionBoardInstance;
import l1j.server.server.model.Instance.L1BoardInstance;
import l1j.server.server.model.Instance.L1CrownInstance;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1DwarfInstance;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1FieldObjectInstance;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1GuardInstance;
import l1j.server.server.model.Instance.L1HousekeeperInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1LittleBugInstance;
import l1j.server.server.model.Instance.L1MerchantInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.Instance.L1TeleporterInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.poison.L1ParalysisPoison;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_ChangeName;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_RangeSkill;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShowPolyList;
import l1j.server.server.serverpackets.S_ShowSummonList;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillIconShield;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Sound;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_TrueTarget;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.L1SpawnUtil;

public class L1SkillUse {
	public static final int TYPE_NORMAL = 0;

	public static final int TYPE_LOGIN = 1;

	public static final int TYPE_SPELLSC = 2;

	public static final int TYPE_NPCBUFF = 3;

	public static final int TYPE_GMBUFF = 4;

	private L1Skills _skill;

	private int _skillId;

	private int _getBuffDuration;

	private int _shockStunDuration;

	private int _getBuffIconDuration;

	private int _targetID;

	private int _mpConsume = 0;

	private int _hpConsume = 0;

	private int _targetX = 0;

	private int _targetY = 0;

	private String _message = null;

	private int _skillTime = 0;

	private int _type = 0;

	private boolean _isPK = false;

	private int _bookmarkId = 0;

	private int _itemobjid = 0;

	private boolean _checkedUseSkill = false;

	private int _leverage = 10;

	private boolean _isFreeze = false;

	private boolean _isCounterMagic = true;

	private L1Character _user = null;

	private L1Character _target = null;

	private L1PcInstance _player = null;

	private L1NpcInstance _npc = null;

	private L1NpcInstance _targetNpc = null;

	private int _calcType;

	private static final int PC_PC = 1;

	private static final int PC_NPC = 2;

	private static final int NPC_PC = 3;

	private static final int NPC_NPC = 4;

	private Random random = new Random(System.nanoTime());

	private FastTable<TargetStatus> _targetList;

	private boolean _isGlanceCheckFail = false;

	private static Logger _log = Logger.getLogger(L1SkillUse.class.getName());

	private static final int[] CAST_WITH_INVIS = { 1, 2, 3, 5, 8, 9, 12, 13,
			14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55, 57,
			60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79, REDUCTION_ARMOR,
			BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100,
			101, 102, 104, 105, 106, 107, 109, 110, 111, 113, 114, 115, 116,
			117, 118, 129, 130, 131, 133, 134, 137, 138, 146, 147, 148, 149,
			150, 151, 155, 156, 158, 159, 163, 164, 165, 166, 168, 169, 170,
			171, 181, SOUL_OF_FLAME, ADDITIONAL_FIRE };

	// 카운터매직방어할수없는스킬
	private static final int[] EXCEPT_COUNTER_MAGIC = { 1, 2, 3, 5, 8, 9, 12,
			13, 14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55,
			57, 60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79, SHOCK_STUN,
			REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER,
			97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110, 111, 113,
			114, 115, 116, 117, 118, 129, 130, 131, 132,TRIPLE_ARROW, 134, 137, 138, 146,
			147, 148, 149, 150, 151, 155, 156, 158, 159, 161, 163, 164, 165,
			166, 168, 169, 170, 171, 181, SOUL_OF_FLAME, ADDITIONAL_FIRE,
			DRAGON_SKIN, FOU_SLAYER, SCALES_EARTH_DRAGON, SCALES_FIRE_DRAGON,
			SCALES_WATER_DRAGON, MIRROR_IMAGE, IllUSION_OGRE, PATIENCE,
			IllUSION_DIAMONDGOLEM, IllUSION_LICH, IllUSION_AVATAR, INSIGHT,
			10026, 10027, 10028, 10029, 30060, 30000, 30078, 30079, 30011,
			30081, 30082, 30083, 30080, 30084, 30010, 30002, 30086 };

	// 카운터매직방어할수없는스킬
	public L1SkillUse() {
	}

	private static class TargetStatus {
		private L1Character _target = null;

		private boolean _isAction = false;

		private boolean _isSendStatus = false;

		private boolean _isCalc = true;

		public TargetStatus(L1Character _cha) {
			_target = _cha;
		}

		public L1Character getTarget() {
			return _target;
		}

		public TargetStatus(L1Character _cha, boolean _flg) {
			_isCalc = _flg;
		}

		public boolean isCalc() {
			return _isCalc;
		}

		public void isAction(boolean _flg) {
			_isAction = _flg;
		}

		public boolean isAction() {
			return _isAction;
		}

		public void isSendStatus(boolean _flg) {
			_isSendStatus = _flg;
		}

		public boolean isSendStatus() {
			return _isSendStatus;
		}
	}

	public void setLeverage(int i) {
		_leverage = i;
	}

	public int getLeverage() {
		return _leverage;
	}

	private boolean isCheckedUseSkill() {
		return _checkedUseSkill;
	}

	private void setCheckedUseSkill(boolean flg) {
		_checkedUseSkill = flg;
	}
	private short _bookmark_mapid = 0;
	 private int _bookmark_x = 0;
	 private int _bookmark_y = 0;


	public boolean checkUseSkill(L1PcInstance player, int skillid,int target_id, int x, int y, String message, int time, int type,
			L1Character attacker) {
		// ** 아래 버그 체크문 실행하면서 에러 안나게 **// By 도우너


		if (player instanceof L1PcInstance) {
			L1Object l1object = L1World.getInstance().findObject(target_id);
			if (l1object instanceof L1ItemInstance) {
				L1ItemInstance item = (L1ItemInstance) l1object;
				if (item.getX() != 0 && item.getY() != 0) { // 지면상의 아이템은 아니고,
					// 누군가의 소유물
					return false;
				}
			}
			// ** 아래 버그 체크문 실행하면서 에러 안나게 **// By 도우너
			
			// ** 노딜 방지 추가 **// by 도우너
			long nowtime = System.currentTimeMillis();
			if (skillid == 17 && player.getSkilldelay2() >= nowtime
					|| skillid == 25 && player.getSkilldelay2() >= nowtime) {
				return false;
			} else if (player.getSkilldelay2() >= nowtime) {
				return false;
			}
			// ** 노딜 방지 추가 **// by 도우너

		} // ** 위 버그 체크문 실행하면서 에러 안나게 **// By 도우너
		// 존재버그 관련 추가
		if (player instanceof L1PcInstance) {
			L1PcInstance jonje = L1World.getInstance().getPlayer(
					player.getName());
			if (jonje == null && player.getAccessLevel() != Config.GMCODE) {
				player.sendPackets(new S_SystemMessage("존재버그 강제종료! 재접속하세요"));
				player.sendPackets(new S_Disconnect());
				return false;
			}
		}

		setCheckedUseSkill(true);
		_targetList = new FastTable<TargetStatus>();

		_skill = SkillsTable.getInstance().getTemplate(skillid);
		_skillId = skillid;
		_targetX = x;
		_targetY = y;
		_message = message;
		_skillTime = time;
		_type = type;
		boolean checkedResult = true;



		if (attacker == null) {
			// pc
			_player = player;
			_user = _player;
		} else {
			// npc
			_npc = (L1NpcInstance) attacker;
			_user = _npc;
		}

		if (_skill.getTarget().equals("none")) {
			_targetID = _user.getId();
			_targetX = _user.getX();
			_targetY = _user.getY();
		} else {
			_targetID = target_id;
		}

		if (type == TYPE_NORMAL) {
			checkedResult = isNormalSkillUsable();
		} else if (type == TYPE_SPELLSC) {
			checkedResult = isSpellScrollUsable();
		} else if (type == TYPE_NPCBUFF) {
			checkedResult = true;
		}
		if (!checkedResult) {
			return false;
		}

		if (_skillId == FIRE_WALL || _skillId == LIFE_STREAM
				|| _skillId == CUBE_IGNITION || _skillId == CUBE_QUAKE
				|| _skillId == CUBE_SHOCK || _skillId == ANTA_SKILL_6
				|| _skillId == ANTA_SKILL_7 || _skillId == ANTA_SKILL_10

				|| _skillId == CUBE_BALANCE) {
			return true;
		}

		L1Object l1object = L1World.getInstance().findObject(_targetID);
		if (l1object instanceof L1LittleBugInstance) {
			return false;
		}
		if (l1object instanceof L1ItemInstance) {
			_log.fine("skill target item name: "
					+ ((L1ItemInstance) l1object).getViewName());
			return false;
		}
		if (_user instanceof L1PcInstance) {
			if (l1object instanceof L1PcInstance) {
				_calcType = PC_PC;
			} else {
				_calcType = PC_NPC;
				_targetNpc = (L1NpcInstance) l1object;
			}
		} else if (_user instanceof L1NpcInstance) {
			if (l1object instanceof L1PcInstance) {
				_calcType = NPC_PC;
			} else if (_skill.getTarget().equals("none")) {
				_calcType = NPC_PC;
			} else {
				_calcType = NPC_NPC;
				_targetNpc = (L1NpcInstance) l1object;
			}
		}

		if (_skillId == TELEPORT || _skillId == MASS_TELEPORT || _skillId == TRUE_TARGET) {
			_bookmark_mapid = (short) target_id;
			   _bookmark_x = x;
			   _bookmark_y = y;
		}


		if (_skillId == CREATE_MAGICAL_WEAPON || _skillId == BRING_STONE
				|| _skillId == BLESSED_ARMOR || _skillId == ENCHANT_WEAPON
				|| _skillId == SHADOW_FANG) {
			_itemobjid = target_id;
		}
		_target = (L1Character) l1object;

		if (!(_target instanceof L1MonsterInstance)
				&& _skill.getTarget().equals("attack")
				&& _user.getId() != target_id) {
			_isPK = true;
		}
		if (!(l1object instanceof L1Character)) {
			checkedResult = false;
		}

		makeTargetList();

		if (_targetList.size() == 0 && (_user instanceof L1NpcInstance)) {
			checkedResult = false;
		}
		return checkedResult;
	}

	/**
	 * 통상의 스킬 사용시에 사용자 상태로부터 스킬이 사용 가능한가 판단한다
	 * 
	 * @return false 스킬이 사용 불가능한 상태인 경우
	 */
	private boolean isNormalSkillUsable() {
		if (_user instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) _user;
			if (pc.isParalyzed())
				return false;
			if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) {
				return false;
			}

			if (pc.getInventory().getWeight240() >= 200) { // 중량 오버이면 스킬을 사용할 수없다
				//pc.sendPackets(new S_ServerMessage(316));
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"짐이 너무 무거워 마법을 사용할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
				pc.sendPackets(s_chatpacket);
				return false;
			}
			if (pc.SkillDelayTime > System.currentTimeMillis()) {
				return false;
			}
			int polyId = pc.getGfxId().getTempCharGfx();
			L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
			if (poly != null && !poly.canUseSkill()) {
				//pc.sendPackets(new S_ServerMessage(285));
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"그 상태로는 마법을 사용할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
				pc.sendPackets(s_chatpacket);
				return false;
			}
			// /// 공성존에서 특정 마법 사용 못하게 -----------------------
			int castle_id = L1CastleLocation.getCastleIdByArea(pc);
			if (castle_id != 0) {
				if (_skillId == 69 || _skillId == 157 || _skillId == 58
						|| _skillId == 50 || _skillId == 59 || _skillId == 80
						|| _skillId == 65 || _skillId == 74 || _skillId == 161
						|| _skillId == 205 || _skillId == 210
						|| _skillId == 215 || _skillId == 220) {
				//	pc.sendPackets(new S_SystemMessage("공성존에서는 사용 할 수 없습니다."));
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"공성존에서는 사용 할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
					pc.sendPackets(s_chatpacket);
					return false;
				}
			}
			if(pc.getMapId() == 1005 || pc.getMapId() == 1011){
						if (_skillId == 69
							|| _skillId == 116
							|| _skillId == 118) { 
					//    pc.sendPackets(new S_SystemMessage("해당마법은 시전이 불가능합니다."));
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"해당마법은 시전이 불가능합니다.", Opcodes.S_OPCODE_MSG, 20); 
						pc.sendPackets(s_chatpacket);
						return false;
						}
					}
	

			
			if(pc.getMapId() == 5302||pc.getMapId() == 5153 ||pc.getMapId() == 5490){//배틀존
				if (_skillId == 69|| _skillId == 78 || _skillId == 71 || _skillId == 39 || _skillId == 20
					|| _skillId == 116|| _skillId == 33 || _skillId == 67 || _skillId == 61 || _skillId == 75
					|| _skillId == 118|| _skillId == 157|| _skillId == 44 || _skillId == 60) { 
			  //  pc.sendPackets(new S_SystemMessage("배틀존에서는 시전이 불가능합니다."));
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"배틀존에서는 시전이 불가능합니다.", Opcodes.S_OPCODE_MSG, 20); 
				pc.sendPackets(s_chatpacket);
				return false;
				}
			}
			// 마을에서 사용금지추가부분
			if (pc.getMap().isSafetyZone(pc.getLocation())) {
				if (pc.getMapId() != 2006){
				if (_skillId == 4 || _skillId == 6 || _skillId == 7
						|| _skillId == 10 || _skillId == 15 || _skillId == 22
						|| _skillId == 25 || _skillId == 30 || _skillId == 34
						|| _skillId == 38 || _skillId == 45 || _skillId == 46
						|| _skillId == 50 || _skillId == 53 || _skillId == 58
						|| _skillId == 59 || _skillId == 62 || _skillId == 63
						|| _skillId == 65 || _skillId == 70 || _skillId == 74
						|| _skillId == 80 || _skillId == 132 || _skillId == 187
						|| _skillId == 192 || _skillId == 220|| _skillId == 77|| _skillId == 28
						|| _skillId == 16) {
				//	pc.sendPackets(new S_SystemMessage("마을안에서는 사용 할 수 없습니다."));
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"마을안에서는 사용 할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
					pc.sendPackets(s_chatpacket);
					return false;
				}
				}
			}
			// /// 공성존에서 특정 마법 사용 못하게 -----------------------
			if (!isAttrAgrees()) {
				return false;
			}
			if (_skillId == SHOCK_STUN) { // 쇼크스턴
				if (pc.getWeapon().getItem().getType() != 3)
					return false;
			}
			if (_skillId == ELEMENTAL_PROTECTION && pc.getElfAttr() == 0) {
			//	pc.sendPackets(new S_ServerMessage(280));
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"마법이 실패했습니다.", Opcodes.S_OPCODE_MSG, 20); 
				pc.sendPackets(s_chatpacket);
				return false;
			}

			if (pc.isSkillDelay()) {
				return false;
			}
			if (pc.getSkillEffectTimerSet().hasSkillEffect(SILENCE)
					|| pc.getSkillEffectTimerSet().hasSkillEffect(
							AREA_OF_SILENCE)
					|| pc.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_POISON_SILENCE)
					|| pc.getSkillEffectTimerSet().hasSkillEffect(CONFUSION)) {
				if (_skillId < SHOCK_STUN || _skillId > COUNTER_BARRIER) {
				//	pc.sendPackets(new S_ServerMessage(285));
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"그 상태로는 마법을 사용할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
					pc.sendPackets(s_chatpacket);
					return false;
				}
			}
			// 글말시장에서 매스텔레포트 사용 불가
			if (pc.isInvisble() && _skillId == CANCELLATION) { // 투명상태켄슬불가
			//	pc.sendPackets(new S_SystemMessage("그 상태로는 시전할 수 없습니다."));
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"그 상태로는 시전할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
				pc.sendPackets(s_chatpacket);
				return false;
			}
			// 세이프티존에서 노멀/컴뱃존 공격불가
			if (_skillId == SOLID_CARRIAGE) { // 솔리드 캐리지
				if (pc.getInventory().checkEquipped(420000)
						|| pc.getInventory().checkEquipped(420001)
						|| pc.getInventory().checkEquipped(420002)
						|| pc.getInventory().checkEquipped(420003)) {
				//	pc.sendPackets(new S_SystemMessage("그 상태로는 시전할 수 없습니다."));
					S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"그 상태로는 시전할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
					pc.sendPackets(s_chatpacket);
					return false;
				}
			}
			if (_skillId == DISINTEGRATE && pc.getLawful() < -32768) {//디스성향
				pc.sendPackets(new S_ServerMessage(352, "$967"));
				return false;
			}

			if (isItemConsume() == false && !_player.isGm()) {
				_player.sendPackets(new S_ServerMessage(299));
				return false;
			}
		} else if (_user instanceof L1NpcInstance) {

			if (_user.getSkillEffectTimerSet().hasSkillEffect(CONFUSION))
				return false;

			if (_user.getSkillEffectTimerSet().hasSkillEffect(SILENCE)) {
				_user.getSkillEffectTimerSet().killSkillEffectTimer(SILENCE);
				return false;
			}
		}

		if (!isHPMPConsume()) {
			return false;
		}
		return true;
	}
	/**
	 * 스펠 스크롤 사용시에 사용자 상태로부터 스킬이 사용 가능한가 판단한다
	 * 
	 * @return false 스킬이 사용 불가능한 상태인 경우
	 */
	private boolean isSpellScrollUsable() {
		L1PcInstance pc = (L1PcInstance) _user;
		if (pc.isParalyzed()) {
			return false;
		}
		if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) {
			return false;
		}

		return true;
	}

	private boolean isInvisUsableSkill() {
		for (int skillId : CAST_WITH_INVIS) {
			if (skillId == _skillId) {
				return true;
			}
		}
		return false;
	}

	public void handleCommands(L1PcInstance player, int skillId, int targetId,
			int x, int y, String message, int timeSecs, int type) {
		L1Character attacker = null;
		handleCommands(player, skillId, targetId, x, y, message, timeSecs,
				type, attacker);
	}

	public void handleCommands(L1PcInstance player, int skillId, int targetId,
			int x, int y, String message, int timeSecs, int type,
			L1Character attacker) {

		try {
			// 사전 체크를 하고 있을까?
			if (!isCheckedUseSkill()) {
				boolean isUseSkill = checkUseSkill(player, skillId, targetId,
						x, y, message, timeSecs, type, attacker);
				if (!isUseSkill) {
					failSkill();
					return;
				}
			}

			switch (type) {
			case TYPE_NORMAL:
                if (!_isGlanceCheckFail || _skill.getArea() > 0
                        || _skill.getTarget().equals("none")) {
                    if (skillId == HOLY_WALK ) { //홀리워크 시간자유자제
                        sendGrfx(true);
                        runSkill();
                        useConsume();
                        sendFailMessageHandle();
                        setDelay();
                        pinkname();
                    } else {
                        runSkill();
                        useConsume();
                        sendGrfx(true);
                        sendFailMessageHandle();
                        setDelay();
                        pinkname();
                    }
                }
                break;
			   case TYPE_LOGIN:
			    runSkill();
			    break;
			   case TYPE_SPELLSC:
			    sendGrfx(true);
			    runSkill();
			    break;
			   case TYPE_GMBUFF:
			    sendGrfx(false);
			    runSkill();
			    break;
			   case TYPE_NPCBUFF:
			    sendGrfx(true);
			    runSkill();
			    break;
			   default:
			    break;
			   }

			setCheckedUseSkill(false);
		} catch (Exception e) {
			System.out.println("skillId : " + skillId + " / attacker : "
					+ attacker.getName());
			_log.log(Level.SEVERE, "L1SkillUse[]Error", e);
		}
	}

	private void pinkname() {
		if ((_skill.getTarget().equals("buff") && _calcType == PC_PC)
				&& CharPosUtil.getZoneType(_user) == 0
				&& CharPosUtil.getZoneType(_target) != 1) {
			if (_skill.getType() == L1Skills.TYPE_HEAL
					|| _skill.getType() == L1Skills.TYPE_CHANGE
					|| _skill.getType() == L1Skills.TYPE_PROBABILITY) {
				if (_target instanceof L1PcInstance) {
					L1PcInstance target = (L1PcInstance) _target;
					if (target.isPinkName()) {
						L1PinkName.onAction(target, _user);
					}
				}
			}
		}
	}

	private void failSkill() {
		setCheckedUseSkill(false);
		if (_skillId == TELEPORT || _skillId == MASS_TELEPORT
				|| _skillId == TELEPORT_TO_MOTHER) {
			_player.sendPackets(new S_Paralysis(
					S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		}
	}

	private boolean isTarget(L1Character cha) throws Exception {
		boolean _flg = false;

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.isGhost() || pc.isGmInvis()) {
				return false;
			}
		}
		if (_calcType == NPC_PC
				&& (cha instanceof L1PcInstance || cha instanceof L1PetInstance || cha instanceof L1SummonInstance)) {
			_flg = true;
		}

		if (cha instanceof L1DoorInstance) {
			if (cha.getMaxHp() == 0 || cha.getMaxHp() == 1) {
				return false;
			}
		}

		if ((_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK)
				&& _calcType == NPC_PC
				&& cha instanceof L1PcInstance
				&& _user instanceof L1SummonInstance) {
			L1SummonInstance summon = (L1SummonInstance) _user;

			if (cha.getId() == summon.getMaster().getId()) {
				return false;
			}
			if (CharPosUtil.getZoneType(cha) == 1) {
				return false;
			}
		}

		if ((_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK)
				&& _calcType == NPC_PC
				&& cha instanceof L1PcInstance
				&& _user instanceof L1PetInstance) {
			L1PetInstance pet = (L1PetInstance) _user;
			if (cha.getId() == pet.getMaster().getId()) {
				return false;
			}
			if (CharPosUtil.getZoneType(cha) == 1) {
				return false;
			}
		}

		if (cha instanceof L1DollInstance && _skillId != HASTE) {
			return false;
		}

		if (_calcType == PC_NPC
				&& _target instanceof L1NpcInstance
				&& !(_target instanceof L1PetInstance)
				&& !(_target instanceof L1SummonInstance)
				&& (cha instanceof L1PetInstance
						|| cha instanceof L1SummonInstance || cha instanceof L1PcInstance)) {
			return false;
		}
		if (_calcType == PC_NPC && _target instanceof L1NpcInstance
				&& !(_target instanceof L1GuardInstance)
				&& cha instanceof L1GuardInstance) {
			return false;
		}
		if ((_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK)
				&& _calcType == NPC_PC
				&& !(cha instanceof L1PetInstance)
				&& !(cha instanceof L1SummonInstance)
				&& !(cha instanceof L1PcInstance)) {
			return false;
		}

		if ((_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK)
				&& _calcType == NPC_NPC
				&& _user instanceof L1MonsterInstance
				&& cha instanceof L1MonsterInstance) {
			return false;
		}

		if (_skill.getTarget().equals("none")
				&& _skill.getType() == L1Skills.TYPE_ATTACK
				&& (cha instanceof L1AuctionBoardInstance
						|| cha instanceof L1BoardInstance
						|| cha instanceof L1CrownInstance
						|| cha instanceof L1DwarfInstance
						|| cha instanceof L1EffectInstance
						|| cha instanceof L1FieldObjectInstance
						|| cha instanceof L1FurnitureInstance
						|| cha instanceof L1HousekeeperInstance
						|| cha instanceof L1MerchantInstance || cha instanceof L1TeleporterInstance)) {
			return false;
		}

		if (_skill.getType() == L1Skills.TYPE_ATTACK
				&& cha.getId() == _user.getId()) {
			return false;
		}

		if (cha.getId() == _user.getId() && _skillId == HEAL_ALL) {
			return false;
		}

		if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC
				|| (_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN || (_skill
				.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY)
				&& cha.getId() == _user.getId() && _skillId != HEAL_ALL) {
			return true;
		}

		if (_user instanceof L1PcInstance
				&& (_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK)
				&& _isPK == false) {
			if (cha instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) cha;
				if (_player.getId() == summon.getMaster().getId()) {
					return false;
				}
			} else if (cha instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) cha;
				if (_player.getId() == pet.getMaster().getId()) {
					return false;
				}
			}
		}

		if ((_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK)
				&& !(cha instanceof L1MonsterInstance)
				&& _isPK == false
				&& _target instanceof L1PcInstance) {
			L1PcInstance enemy = (L1PcInstance) cha;
			if (_skillId == COUNTER_DETECTION
					&& CharPosUtil.getZoneType(enemy) != 1
					&& (cha.getSkillEffectTimerSet().hasSkillEffect(
							INVISIBILITY) || cha.getSkillEffectTimerSet()
							.hasSkillEffect(BLIND_HIDING))) {
				return true;
			}
			if (_player.getClanid() != 0 && enemy.getClanid() != 0) {
				List<L1War> li = L1World.getInstance().getWarList();
				for (L1War war : li) {
					if (war.CheckClanInWar(_player.getClanname())) {
						if (war.CheckClanInSameWar(_player.getClanname(), enemy
								.getClanname())) {
							if (L1CastleLocation.checkInAllWarArea(
									enemy.getX(), enemy.getY(), enemy
											.getMapId())) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}

		if (CharPosUtil.glanceCheck(_user, cha.getX(), cha.getY()) == false
				&& _skill.isThrough() == false) {
			if (!(_skill.getType() == L1Skills.TYPE_CHANGE || _skill.getType() == L1Skills.TYPE_RESTORE)) {
				_isGlanceCheckFail = true;
				return false;
			}
		}

		if ((cha.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
				|| cha.getSkillEffectTimerSet().hasSkillEffect(FREEZING_BLIZZARD))
				&& (_skillId == ICE_LANCE || _skillId == SHOCK_STUN)) {
			return false;
		}

		if ((cha.getSkillEffectTimerSet().hasSkillEffect(MOB_BASILL) && _skillId == MOB_BASILL)
				|| (cha.getSkillEffectTimerSet().hasSkillEffect(MOB_COCA) && _skillId == MOB_COCA)) {
			return false;
		}

		if (cha.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)) {
			if (_skillId != WEAPON_BREAK && _skillId != CANCELLATION // 확률계
					&& _skill.getType() != L1Skills.TYPE_HEAL // 힐 계
					&& _skill.getType() != L1Skills.TYPE_CHANGE) { // 버프 계
				return false;
			}
		}
		if (cha.isInvisble()) { // (투명시에 캔슬 안되게)
			if (_skillId == CANCELLATION) {
				return false;
			}
		}
		if (!(cha instanceof L1MonsterInstance)
				&& (_skillId == TAMING_MONSTER || _skillId == CREATE_ZOMBIE)) {
			return false;
		}
		if (cha.isDead()
				&& (_skillId != CREATE_ZOMBIE && _skillId != RESURRECTION
						&& _skillId != GREATER_RESURRECTION && _skillId != CALL_OF_NATURE)) {
			return false;
		}

		if (!(cha instanceof L1TowerInstance || cha instanceof L1DoorInstance)
				&& cha.isDead() == false
				&& (_skillId == CREATE_ZOMBIE || _skillId == RESURRECTION
						|| _skillId == GREATER_RESURRECTION || _skillId == CALL_OF_NATURE)) {
			return false;
		}

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)) {
				if (_skillId == CURSE_BLIND || _skillId == WEAPON_BREAK
						|| _skillId == DARKNESS || _skillId == WEAKNESS
						|| _skillId == DISEASE || _skillId == FOG_OF_SLEEPING
						|| _skillId == MASS_SLOW || _skillId == SLOW
						|| _skillId == CANCELLATION || _skillId == SILENCE
						|| _skillId == DECAY_POTION
						|| _skillId == MASS_TELEPORT || _skillId == DETECTION || _skillId == AM_BREAK || _skillId == FREEZING_BREATH//테스트
						|| _skillId == HORROR_OF_DEATH
						|| _skillId == COUNTER_DETECTION
						|| _skillId == GUARD_BREAK || _skillId == ERASE_MAGIC
						|| _skillId == ENTANGLE || _skillId == FEAR
						|| _skillId == PHYSICAL_ENCHANT_DEX
						|| _skillId == PHYSICAL_ENCHANT_STR
						|| _skillId == BLESS_WEAPON || _skillId == EARTH_SKIN
						|| _skillId == IMMUNE_TO_HARM
						|| _skillId == REMOVE_CURSE || _skillId == CONFUSION
						|| _skillId == MOB_SLOW_1
						|| _skillId == MOB_SLOW_18
						|| _skillId == MOB_WEAKNESS_1
						|| _skillId == MOB_DISEASE_1
						|| _skillId == MOB_BASILL
						|| _skillId == MOB_SHOCKSTUN_30
						|| _skillId == MOB_RANGESTUN_19
						|| _skillId == MOB_RANGESTUN_18
						|| _skillId == MOB_DISEASE_30
						|| _skillId == MOB_WINDSHACKLE_1
						|| _skillId == MOB_COCA
						|| _skillId == MOB_CURSEPARALYZ_19
						|| _skillId == MOB_CURSEPARALYZ_18
						|| _skillId == TROGIR_MILPITAS
					      || _skillId == TROGIR_MILPITAS1
					      || _skillId == TROGIR_MILPITAS2
					      || _skillId == TROGIR_MILPITAS3
					      || _skillId == TROGIR_MILPITAS4
					      || _skillId == TROGIR_MILPITAS5
					      || _skillId == TROGIR_MILPITAS6
					      || _skillId == TROGIR_MILPITAS7
						
						
						|| _skillId == ANTA_SKILL_1
						|| _skillId == ANTA_SKILL_2
						|| _skillId == ANTA_SKILL_3 // 안타라스 용언
						|| _skillId == ANTA_SKILL_4 || _skillId == ANTA_SKILL_5
						|| _skillId == ANTA_SKILL_6 || _skillId == ANTA_SKILL_7
						|| _skillId == ANTA_SKILL_8 || _skillId == ANTA_SKILL_9
						|| _skillId == ANTA_SKILL_10) {
					return true;
				} else {
					return false;
				}
			}
		}

		if (cha instanceof L1NpcInstance) {
			int hiddenStatus = ((L1NpcInstance) cha).getHiddenStatus();
			if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
				if (_skillId == DETECTION || _skillId == COUNTER_DETECTION) {
					return true;
				} else {
					return false;
				}
			} else if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY) {
				return false;
			}
		}

		if ((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC
				&& cha instanceof L1PcInstance) {
			_flg = true;
		} else if ((_skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC
				&& (cha instanceof L1MonsterInstance
						|| cha instanceof L1NpcInstance
						|| cha instanceof L1SummonInstance || cha instanceof L1PetInstance)) {
			_flg = true;
		} else if ((_skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills.TARGET_TO_PET
				&& _user instanceof L1PcInstance) {
			if (cha instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) cha;
				if (summon.getMaster() != null) {
					if (_player.getId() == summon.getMaster().getId()) {
						_flg = true;
					}
				}
			}
			if (cha instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) cha;
				if (pet.getMaster() != null) {
					if (_player.getId() == pet.getMaster().getId()) {
						_flg = true;
					}
				}
			}
		}

		if (_calcType == PC_PC && cha instanceof L1PcInstance) {

			if ((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN) {
				if (_player.isGm()) {
					return true;
				}
				if (_player.getClanid() != 0
						&& (_player.getClanid() == ((L1PcInstance) cha)
								.getClanid())) {
					return true;
				}
			}
			if ((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY
					&& (_player.getParty().isMember((L1PcInstance) cha) || _player
							.isGm())) {
				return true;
			}
		}

		return _flg;
	}

	private void makeTargetList() {
		try {
			if (_type == TYPE_LOGIN) {
				_targetList.add(new TargetStatus(_user));
				return;
			}
			if (_skill.getTargetTo() == L1Skills.TARGET_TO_ME
					&& (_skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK) {
				_targetList.add(new TargetStatus(_user));
				return;
			}

			if (_skill.getRanged() != -1) {
				if (_user.getLocation().getTileLineDistance(
						_target.getLocation()) > _skill.getRanged()) {
					return;
				}
			} else {
				if (!_user.getLocation().isInScreen(_target.getLocation())) {
					return;
				}
			}

			if (!isTarget(_target) && !(_skill.getTarget().equals("none"))) {
				return;
			}

			if (_skillId == LIGHTNING
					|| _skillId == 10151 // 안타라스(리뉴얼) 큰이럽션입김
					|| _skillId == ANTA_SKILL_8 || _skillId == ANTA_SKILL_9) {
				FastTable<L1Object> lis = L1World.getInstance().getVisibleLineObjects(_user, _target);
				for (L1Object tgobj : lis) {
					if (tgobj == null) {
						continue;
					}
					if (!(tgobj instanceof L1Character)) {
						continue;
					}
					L1Character cha = (L1Character) tgobj;
					if (isTarget(cha) == false) {
						continue;
					}
					_targetList.add(new TargetStatus(cha));
				}
				return;
			}

			if (_skill.getArea() == 0) {
				if (!CharPosUtil.glanceCheck(_user, _target.getX(), _target
						.getY())) {
					if ((_skill.getType() & L1Skills.TYPE_ATTACK) == L1Skills.TYPE_ATTACK) {
						_targetList.add(new TargetStatus(_target, false));
						return;
					}
				}
				_targetList.add(new TargetStatus(_target));
			} else {
				if (!_skill.getTarget().equals("none")) {
					_targetList.add(new TargetStatus(_target));
				}

				if (_skillId != 49
						&& !(_skill.getTarget().equals("attack") || _skill
								.getType() == L1Skills.TYPE_ATTACK)) {
					_targetList.add(new TargetStatus(_user));
				}

				FastTable<L1Object> objects;
				if (_skill.getArea() == -1) {
					objects = L1World.getInstance().getVisibleObjects(_user);
				} else {
					objects = L1World.getInstance().getVisibleObjects(_target,
							_skill.getArea());
				}
				for (L1Object tgobj : objects) {
					if (tgobj == null) {
						continue;
					}
					if (!(tgobj instanceof L1Character)) {
						continue;
					}
					L1Character cha = (L1Character) tgobj;
					if (!isTarget(cha)) {
						continue;
					}
					_targetList.add(new TargetStatus(cha));
				}
				return;
			}

		} catch (Exception e) {
			_log.finest("exception in L1Skilluse makeTargetList" + e);
		}
	}

	private void sendHappenMessage(L1PcInstance pc) {
		int msgID = _skill.getSysmsgIdHappen();
		if (msgID > 0) {
			pc.sendPackets(new S_ServerMessage(msgID));
		}
	}

	private void sendFailMessageHandle() {
		if (_skill.getType() != L1Skills.TYPE_ATTACK
				&& !_skill.getTarget().equals("none")
				&& _targetList.size() == 0) {
			sendFailMessage();
		}
	}

	private void sendFailMessage() {
		int msgID = _skill.getSysmsgIdFail();
		if (msgID > 0 && (_user instanceof L1PcInstance)) {
			_player.sendPackets(new S_ServerMessage(msgID));
		}
	}

	private boolean isAttrAgrees() {
		int magicattr = _skill.getAttr();
		if (_user instanceof L1NpcInstance) {
			return true;
		}

		if ((_skill.getSkillLevel() >= 17 && _skill.getSkillLevel() <= 22 && magicattr != 0)
				&& (magicattr != _player.getElfAttr() && !_player.isGm())) {
			return false;
		}
		return true;
	}

	private boolean isHPMPConsume() {
		_mpConsume = _skill.getMpConsume();
		_hpConsume = _skill.getHpConsume();
		int currentMp = 0;
		int currentHp = 0;

		if (_user instanceof L1NpcInstance) {
			currentMp = _npc.getCurrentMp();
			currentHp = _npc.getCurrentHp();
		} else {
			currentMp = _player.getCurrentMp();
			currentHp = _player.getCurrentHp();

			if (_player.getAbility().getTotalInt() > 12
					&& (_skillId > HOLY_WEAPON && _skillId <= FREEZING_BLIZZARD)) {
				_mpConsume--;
			}
			if (_player.getAbility().getTotalInt() > 13 && _skillId > STALAC
					&& _skillId <= FREEZING_BLIZZARD) {
				_mpConsume--;
			}
			if (_player.getAbility().getTotalInt() > 14
					&& _skillId > WEAK_ELEMENTAL
					&& _skillId <= FREEZING_BLIZZARD) {
				_mpConsume--;
			}
			if (_player.getAbility().getTotalInt() > 15
					&& _skillId > MEDITATION && _skillId <= FREEZING_BLIZZARD) {
				_mpConsume--;
			}
			if (_player.getAbility().getTotalInt() > 16 && _skillId > DARKNESS
					&& _skillId <= FREEZING_BLIZZARD) {
				_mpConsume--;
			}
			if (_player.getAbility().getTotalInt() > 17
					&& _skillId > BLESS_WEAPON && _skillId <= FREEZING_BLIZZARD) {
				_mpConsume--;
			}
			if (_player.getAbility().getTotalInt() > 18 && _skillId > DISEASE
					&& _skillId <= FREEZING_BLIZZARD) {
				_mpConsume--;
			}
			if (_player.getAbility().getTotalInt() > 19 && _skillId > DISEASE
					&& _skillId <= FREEZING_BLIZZARD) {
				_mpConsume--;
			}
			if (_player.getAbility().getTotalInt() > 20 && _skillId > DISEASE
					&& _skillId <= FREEZING_BLIZZARD) {
				_mpConsume--;
			}
			if (_player.getAbility().getTotalInt() > 21 && _skillId > DISEASE
					&& _skillId <= FREEZING_BLIZZARD) {
				_mpConsume--;
			}
			if (_player.getAbility().getTotalInt() > 22 && _skillId > DISEASE
					&& _skillId <= FREEZING_BLIZZARD) {
				_mpConsume--;
			}
			if (_player.getAbility().getTotalInt() > 23 && _skillId > DISEASE
					&& _skillId <= FREEZING_BLIZZARD) {
				_mpConsume--;
			}
			if (_player.getAbility().getTotalInt() > 24 && _skillId > DISEASE
					&& _skillId <= FREEZING_BLIZZARD) {
				_mpConsume--;
			}
			if (_player.getAbility().getTotalInt() > 25 && _skillId > DISEASE
					&& _skillId <= FREEZING_BLIZZARD) {
				_mpConsume--;
			}

			if (_player.getAbility().getTotalInt() > 12
					&& _skillId >= SHOCK_STUN && _skillId <= COUNTER_BARRIER) {
				_mpConsume -= (_player.getAbility().getTotalInt() - 12);
			}

			if ((_skillId == PHYSICAL_ENCHANT_DEX || _skillId == HASTE)
					&& _player.getInventory().checkEquipped(20013)) {
				_mpConsume /= 2;
			}
			if ((_skillId == HEAL || _skillId == EXTRA_HEAL)
					&& _player.getInventory().checkEquipped(20014)) {
				_mpConsume /= 2;
			}
			if ((_skillId == ENCHANT_WEAPON || _skillId == DETECTION) // || _skillId == PHYSICAL_ENCHANT_STR || _skillId == AM_BREAK || _skillId == IOFTHE_DRAGON)//테스트
					&& _player.getInventory().checkEquipped(20015)) {
				_mpConsume /= 2;
			}
			if (_skillId == HASTE
					&& _player.getInventory().checkEquipped(20008)) {
				_mpConsume /= 2;
			}
			if (_skillId == GREATER_HASTE
					&& _player.getInventory().checkEquipped(20023)) {
				_mpConsume /= 2;
			}
			/** 매직 핼맷* */
			if (_skillId == PHYSICAL_ENCHANT_DEX
					&& _player.getInventory().checkEquipped(20754)) { // 신속 헤룸 장비중에 PE:DEX
				_mpConsume /= 2;
			}
			if (_skillId == HASTE
					&& _player.getInventory().checkEquipped(20754)) { // 신속 헤룸 장비중에 헤이 파업
				_mpConsume /= 2;
			}
			if (_skillId == HEAL && _player.getInventory().checkEquipped(20754)) { // 치유 헤룸 장비중에 힐
				_mpConsume /= 2;
			}
			if (_skillId == EXTRA_HEAL
					&& _player.getInventory().checkEquipped(20754)) { // 치유 헤룸장비중에엑스트라 힐
				_mpConsume /= 2;
			}
			if (_skillId == ENCHANT_WEAPON
					&& _player.getInventory().checkEquipped(20754)) { // 력 헤룸 장비중에엔챤트 웨폰
				_mpConsume /= 2;
			}
			if (_skillId == DETECTION
					&& _player.getInventory().checkEquipped(20754)) { // 력 헤룸장비중에 디 텍 숀
				_mpConsume /= 2;
			}
			if (_skillId == PHYSICAL_ENCHANT_STR
					&& _player.getInventory().checkEquipped(20754)) { // 력 헤룸 장비중에PE:STR
				_mpConsume /= 2;
			}
			if (_player.getBaseMagicDecreaseMp() > 0) {
				_mpConsume -= _player.getBaseMagicDecreaseMp();
			}

			if (0 < _skill.getMpConsume()) {
				_mpConsume = Math.max(_mpConsume, 1);
			}
		}

		if (currentHp < _hpConsume + 1) {
			if (_user instanceof L1PcInstance) {
				_player.sendPackets(new S_ServerMessage(279));
			}
			return false;
		} else if (currentMp < _mpConsume) {
			if (_user instanceof L1PcInstance) {
				_player.sendPackets(new S_ServerMessage(278));
			}
			return false;
		}

		return true;
	}

	private boolean isItemConsume() {
		int itemConsume = _skill.getItemConsumeId();
		int itemConsumeCount = _skill.getItemConsumeCount();

		if (itemConsume == 0) {
			return true;
		}
		if (!_player.getInventory().checkItem(itemConsume, itemConsumeCount)) {
			return false;
		}

		return true;
	}

	private void useConsume() {
		if (_user instanceof L1NpcInstance) {
			int current_hp = _npc.getCurrentHp() - _hpConsume;
			_npc.setCurrentHp(current_hp);

			int current_mp = _npc.getCurrentMp() - _mpConsume;
			_npc.setCurrentMp(current_mp);
			return;
		}

		if (isHPMPConsume()) {
			if (_skillId == FINAL_BURN) {
				_player.setCurrentHp(1);
				_player.setCurrentMp(0);
			} else {
				int current_hp = _player.getCurrentHp() - _hpConsume;
				_player.setCurrentHp(current_hp);

				int current_mp = _player.getCurrentMp() - _mpConsume;
				_player.setCurrentMp(current_mp);
			}
		}

		int lawful = _player.getLawful() + _skill.getLawful();
		if (lawful > 32767) {
			lawful = 32767;
		}
		if (lawful < -32767) {
			lawful = -32767;
		}
		_player.setLawful(lawful);

		int itemConsume = _skill.getItemConsumeId();
		int itemConsumeCount = _skill.getItemConsumeCount();

		if (itemConsume == 0) {
			return;
		}

		_player.getInventory().consumeItem(itemConsume, itemConsumeCount);
	}

	private void addMagicList(L1Character cha, boolean repetition) {
		if (_skillTime == 0) {
			_getBuffDuration = _skill.getBuffDuration() * 1000;
			if (_skill.getBuffDuration() == 0) {
				if (_skillId == INVISIBILITY) {
					cha.getSkillEffectTimerSet()
							.setSkillEffect(INVISIBILITY, 0);
				}
				return;
			}
		} else {
			_getBuffDuration = _skillTime * 1000;
		}

		if (_skillId == SHOCK_STUN) {
			_getBuffDuration = _shockStunDuration;
		}

		if (_skillId == CURSE_POISON || _skillId == CURSE_PARALYZE
				|| _skillId == CURSE_PARALYZE2 || _skillId == SHAPE_CHANGE
				|| _skillId == BLESSED_ARMOR || _skillId == HOLY_WEAPON
				|| _skillId == ENCHANT_WEAPON || _skillId == BLESS_WEAPON
				|| _skillId == SHADOW_FANG) {
			return;
		}

		if ((_skillId == ICE_LANCE || _skillId == FREEZING_BLIZZARD)
				&& !_isFreeze) {
			return;
		}
		cha.getSkillEffectTimerSet().setSkillEffect(_skillId, _getBuffDuration);

		if (cha instanceof L1PcInstance && repetition) {
			L1PcInstance pc = (L1PcInstance) cha;
			sendIcon(pc);
		}
	}

	private void sendIcon(L1PcInstance pc) {
		if (_skillTime == 0) {
			_getBuffIconDuration = _skill.getBuffDuration();
		} else {
			_getBuffIconDuration = _skillTime;
		}
		switch (_skillId) {
		case SHIELD:
			pc.sendPackets(new S_SkillIconShield(5, _getBuffIconDuration));
			break;
		case SHADOW_ARMOR:
			pc.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));
			break;
		case DRESS_DEXTERITY:
			pc.sendPackets(new S_Dexup(pc, 2, _getBuffIconDuration));
			break;
		case DRESS_MIGHTY:
			pc.sendPackets(new S_Strup(pc, 2, _getBuffIconDuration));
			break;
		case GLOWING_AURA:
			pc.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
			break;
		case SHINING_AURA:
			pc.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
			break;
		case BRAVE_AURA:
			pc.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
			break;
		case FIRE_WEAPON:
			pc.sendPackets(new S_SkillIconAura(147, _getBuffIconDuration));
			break;
		case WIND_SHOT:
			pc.sendPackets(new S_SkillIconAura(148, _getBuffIconDuration));
			break;
		case FIRE_BLESS:
			pc.sendPackets(new S_SkillIconAura(154, _getBuffIconDuration));
			break;
		case STORM_EYE:
			pc.sendPackets(new S_SkillIconAura(155, _getBuffIconDuration));
			break;
		case EARTH_BLESS:
			pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
			break;
		case BURNING_WEAPON:
			pc.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
			break;
		case STORM_SHOT:
			pc.sendPackets(new S_SkillIconAura(165, _getBuffIconDuration));
			break;
		case IRON_SKIN:
			pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
			break;
		case EARTH_SKIN:
			pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
			break;
		case PHYSICAL_ENCHANT_STR:
			pc.sendPackets(new S_Strup(pc, 5, _getBuffIconDuration));
			break;
		case PHYSICAL_ENCHANT_DEX:
			pc.sendPackets(new S_Dexup(pc, 5, _getBuffIconDuration));
			break;
		case IMMUNE_TO_HARM:
			pc.sendPackets(new S_SkillIconGFX(40, _getBuffIconDuration));
			break;
		case HASTE:
		case GREATER_HASTE:
			pc.sendPackets(new S_SkillHaste(pc.getId(), 1,_getBuffIconDuration));
			Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 1, 0));
			break;
		case HOLY_WALK:
		case MOVING_ACCELERATION:
		case WIND_WALK:
			pc.sendPackets(new S_SkillBrave(pc.getId(), 4,_getBuffIconDuration));
			Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 4, 0));
			break;
		case BLOOD_LUST:
			pc.sendPackets(new S_SkillBrave(pc.getId(), 6,_getBuffIconDuration));
			Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 6, 0));
			pc.sendPackets(new S_SkillBrave(pc.getId(), 1, _getBuffIconDuration));
			break;
		case SLOW:
		case MOB_SLOW_1:
		case MOB_SLOW_18:
		case MASS_SLOW:
		case ENTANGLE:
			pc.sendPackets(new S_SkillHaste(pc.getId(), 2,_getBuffIconDuration));
			Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 2, 0));
			break;
		default:
			break;
		}
		pc.sendPackets(new S_OwnCharStatus(pc));
	}

	private void sendGrfx(boolean isSkillAction) {
		int actionId = _skill.getActionId();
		int castgfx = _skill.getCastGfx();
		if (castgfx == 0) {
			return;
		}
////////추가 독구름(기르타스)///////////////
  if (_skillId == TROGIR_MILPITAS1) {
   int xx = 0;
   int yy = 0;
   int xx1 = 0;
   int yy1 = 0;
   int xx2 = 0;
   int yy2 = 0;
   Random random = new Random(); 
   int randomxy = random.nextInt(8);
   int a1 = 3 + randomxy;
   int a2 = -3 - randomxy;
   int b1 = 2 + randomxy;
   int b2 = -2 - randomxy;
   int heading = _npc.getMoveState().getHeading(); 
   switch (heading){
   case 1: xx = a1; yy = a2; yy1 = a2; xx2 = a1; break;
   case 2: xx = a1; xx1 = b1; yy1 = a2; xx2 = b1; yy2 = a1; break;
   case 3: xx = a1; yy = a1; xx1 = a1; yy2 = a1; break;
   case 4: yy = a1; xx1 = a1; yy1 = b1; xx2 = a2; yy2 = b1; break;
   case 5: xx = a2; yy = a1; yy1 = a1; xx2 = a2; break;
   case 6: xx = a2; xx1 = b2; yy1 = a1; xx2 = b2; yy2 = a2; break;
   case 7: xx = a2; yy = a2; xx1 = a2; yy2 = a2; break;
   case 0: yy = a2; xx1 = a2; yy1 = b2; xx2 = a1; yy2 = b2; break;
   default:break;}
   int x = _npc.getX() + xx; int y = _npc.getY() + yy;
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x, y, _user.getMapId());   
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x, y + 3, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x, y - 4, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x, y - 6, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x - 1, y, _user.getMapId());   
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x - 2, y + 2, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x - 3, y - 4, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x - 4, y - 8, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x + 5, y + 2, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x + 6, y - 5, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x + 5, y, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x + 3, y - 4, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x + 5, y - 6, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x + 7, y - 4, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x + 3, y, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x + 1, y + 3, _user.getMapId());
   int x1 = _npc.getX() + xx1; int y1 = _npc.getY() + yy1;
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1, y1, _user.getMapId());   
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1, y1 + 1, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1, y1 - 2, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1, y1 - 3, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1 - 6, y1, _user.getMapId());   
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1 - 5, y1 + 7, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1 - 4, y1 - 6, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1 - 3, y1 - 1, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1 + 5, y1 + 2, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1 + 4, y1 - 3, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1 + 3, y1, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1 + 2, y1 - 4, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1 + 8, y1 - 5, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1 + 7, y1 - 2, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1 + 6, y1, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x1 + 5, y1 + 3, _user.getMapId());
   int x2 = _npc.getX() + xx2; int y2 = _npc.getY() + yy2;
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2, y2, _user.getMapId());   
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2, y2 + 3, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2, y2 - 4, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2, y2 - 6, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2 - 2, y2, _user.getMapId());   
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2 - 8, y2 + 2, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2 - 6, y2 - 3, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2 - 4, y2 - 7, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2 + 5, y2 + 5, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2 + 3, y2 - 3, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2 + 7, y2, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2 + 6, y2 - 6, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2 + 8, y2 - 5, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2 + 3, y2 - 7, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2 + 4, y2, _user.getMapId());
   L1EffectSpawn.getInstance().spawnEffect(71268, _skill.getBuffDuration() * 1000, x2 + 6, y2 + 2, _user.getMapId());
   return;
  }

		
		if (castgfx != _skill.getCastGfx()) {
			return; // 그래픽 번호가 다르다.
		}
		if (actionId != _skill.getActionId()) {
			return; // 액션 ID가 다르다
		}
		// 발라카스 범위 파이어월, 독구름 추가
		if (_user instanceof L1NpcInstance) {
			if (_skillId == Mob_AREA_FIRE_WALL) {
				L1NpcInstance Npc = (L1NpcInstance) _user;
				S_DoActionGFX gfx = new S_DoActionGFX(Npc.getId(), 18);
				Broadcaster.broadcastPacket(Npc, gfx);
				return;
			}
		}

		if (_user instanceof L1NpcInstance) {
			if (_skillId == Mob_AREA_POISON_18
					|| _skillId == Mob_AREA_POISON_30
					|| _skillId == Mob_AREA_POISON
					|| _skillId == Mob_AREA_POISON_20
					|| _skillId == ANTA_SKILL_6 || _skillId == ANTA_SKILL_7
					|| _skillId == ANTA_SKILL_10) {
				L1NpcInstance Npc = (L1NpcInstance) _user;
				S_DoActionGFX gfx = new S_DoActionGFX(Npc.getId(), actionId);
				Broadcaster.broadcastPacket(Npc, gfx);
				return;
			}
		}
		// 발라카스 범위 파이어월, 독구름 추가 - 몹스킬패턴 추가 : 끝
		if (_user instanceof L1PcInstance) {
			if (_skillId == FIRE_WALL || _skillId == LIFE_STREAM
					|| _skillId == CUBE_IGNITION || _skillId == CUBE_QUAKE
					|| _skillId == CUBE_SHOCK || _skillId == CUBE_BALANCE) {
				L1PcInstance pc = (L1PcInstance) _user;
				if (_skillId == FIRE_WALL) {
					pc.getMoveState().setHeading(CharPosUtil.targetDirection(pc, _targetX, _targetY));
					pc.sendPackets(new S_ChangeHeading(pc));
					Broadcaster.broadcastPacket(pc, new S_ChangeHeading(pc));
				}
				S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), actionId);
				pc.sendPackets(gfx);
				Broadcaster.broadcastPacket(pc, gfx);
				return;
			}

			int targetid = _target.getId();

			if (_skillId == SHOCK_STUN || _skillId == MOB_SHOCKSTUN_30
					|| _skillId == MOB_RANGESTUN_19
					|| _skillId == MOB_RANGESTUN_18 || _skillId == ANTA_SKILL_3
					|| _skillId == ANTA_SKILL_4 || _skillId == ANTA_SKILL_5) {
				if (_targetList.size() == 0) {
					if (_target instanceof L1PcInstance) { // Gn.89
						L1PcInstance pc = (L1PcInstance) _target;
						pc.sendPackets(new S_SkillSound(pc.getId(), 4434));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 4434));
					//	pc.sendPackets(new S_ServerMessage(280));
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"마법이 실패했습니다.", Opcodes.S_OPCODE_MSG, 20); 
						pc.sendPackets(s_chatpacket);
					} else if (_target instanceof L1NpcInstance) {
						Broadcaster.broadcastPacket(_target, new S_SkillSound(_target.getId(), 4434));
					}
					return;
				} else {
					if (_target instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) _target;
						pc.sendPackets(new S_SkillSound(pc.getId(), 4434));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 4434));
					} else if (_target instanceof L1NpcInstance) {
						Broadcaster.broadcastPacket(_target, new S_SkillSound(_target.getId(), 4434));
					}
					return;
				}
			}

			/*	if (_skillId == AM_BREAK) {
			if (_targetList.size() == 0) {
				return;
			} else {
				if (_target instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) _target;
					pc.sendPackets(new S_SkillSound(pc.getId(), 6551));
					Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 6551));
				} else if (_target instanceof L1NpcInstance) {
					Broadcaster.broadcastPacket(_target, new S_SkillSound(_target.getId(), 6551));
				}
				return;
			}
		}*/

		if (_skillId == SMASH) {
			if (_targetList.size() == 0) {
		}
		}

		if (_skillId == AM_BREAK) {
			L1PcInstance pc = (L1PcInstance) _target;
		}

			if (_skillId == LIGHT) {
				L1PcInstance pc = (L1PcInstance) _target;
				pc.sendPackets(new S_Sound(145));
			}

			if (_targetList.size() == 0 && !(_skill.getTarget().equals("none"))) {
				int tempchargfx = _player.getGfxId().getTempCharGfx();
				if (tempchargfx == 5727 || tempchargfx == 5730) {
					actionId = ActionCodes.ACTION_SkillBuff;
				} else if (tempchargfx == 5733 || tempchargfx == 5736) {
					actionId = ActionCodes.ACTION_Attack;
				}
				if (isSkillAction && actionId > 0) {
					S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(),actionId);
					_player.sendPackets(gfx);
					Broadcaster.broadcastPacket(_player, gfx);
				}
				return;
			}

			if (_skill.getTarget().equals("attack") && _skillId != 18) {
				if (isPcSummonPet(_target)) {
					if (CharPosUtil.getZoneType(_player) == 1
							|| CharPosUtil.getZoneType(_target) == 1
							|| _player.checkNonPvP(_player, _target)) {
						_player.sendPackets(new S_UseAttackSkill(_player, 0,castgfx, _targetX, _targetY, actionId));
						Broadcaster.broadcastPacket(_player,new S_UseAttackSkill(_player, 0, castgfx, _targetX, _targetY, actionId));
						return;
					}
				}

				if (_skill.getArea() == 0) {
					_player.sendPackets(new S_UseAttackSkill(_player, targetid, castgfx, _targetX, _targetY, actionId));
					Broadcaster.broadcastPacket(_player, new S_UseAttackSkill(
							_player, targetid, castgfx, _targetX, _targetY, actionId));
					Broadcaster.broadcastPacketExceptTargetSight(_target, new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage), _player);
				} else {
					L1Character[] cha = new L1Character[_targetList.size()];
					int i = 0;
					for (TargetStatus ts : _targetList) {
						cha[i] = ts.getTarget();
						i++;
					}
					_player.sendPackets(new S_RangeSkill(_player, cha, castgfx,
							actionId, S_RangeSkill.TYPE_DIR));
					Broadcaster.broadcastPacket(_player, new S_RangeSkill(
							_player, cha, castgfx, actionId,
							S_RangeSkill.TYPE_DIR));
				}
			} else if (_skill.getTarget().equals("none")
					&& _skill.getType() == L1Skills.TYPE_ATTACK) {
				L1Character[] cha = new L1Character[_targetList.size()];
				int i = 0;
				for (TargetStatus ts : _targetList) {
					cha[i] = ts.getTarget();
					Broadcaster.broadcastPacketExceptTargetSight(cha[i],
							new S_DoActionGFX(cha[i].getId(),
									ActionCodes.ACTION_Damage), _player);
					i++;
				}
				_player.sendPackets(new S_RangeSkill(_player, cha, castgfx,
						actionId, S_RangeSkill.TYPE_NODIR));
				Broadcaster.broadcastPacket(_player, new S_RangeSkill(_player,
						cha, castgfx, actionId, S_RangeSkill.TYPE_NODIR));
			} else {
				if (_skillId != 5 && _skillId != 69 && _skillId != 131 && _skillId != TRUE_TARGET) {
					if (isSkillAction && actionId > 0) {
						S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(),
								_skill.getActionId());
						_player.sendPackets(gfx);
						Broadcaster.broadcastPacket(_player, gfx);
					}
					if (_skillId == COUNTER_MAGIC || _skillId == COUNTER_MIRROR) {
						_player
								.sendPackets(new S_SkillSound(targetid, castgfx));
						Broadcaster.broadcastPacket(_player, new S_SkillSound(
								targetid, castgfx));
					} else if (_skillId == COUNTER_BARRIER) {
						_player.sendPackets(new S_SkillSound(targetid, castgfx));
					} else if (_skillId == TRUE_TARGET) {
						return;
					} else {
						_player
								.sendPackets(new S_SkillSound(targetid, castgfx));
						Broadcaster.broadcastPacket(_player, new S_SkillSound(
								targetid, castgfx));
					}
				}

				for (TargetStatus ts : _targetList) {
					L1Character cha = ts.getTarget();
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_OwnCharStatus(pc));
					}
				}
			}
		} else if (_user instanceof L1NpcInstance) {
			int targetid = _target.getId();

			if (_user instanceof L1MerchantInstance) {
				Broadcaster.broadcastPacket(_user, new S_SkillSound(targetid,
						castgfx));
				return;
			}

			if (_targetList.size() == 0 && !(_skill.getTarget().equals("none"))) {
				S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _skill
						.getActionId());
				Broadcaster.broadcastPacket(_user, gfx);
				return;
			}

			if (_skill.getTarget().equals("attack") && _skillId != 18) {
				if (_skill.getArea() == 0) {
					Broadcaster.broadcastPacket(_user, new S_UseAttackSkill(
							_user, targetid, castgfx, _targetX, _targetY,
							actionId));
					Broadcaster.broadcastPacketExceptTargetSight(_target,
							new S_DoActionGFX(targetid,
									ActionCodes.ACTION_Damage), _user);
				} else {
					L1Character[] cha = new L1Character[_targetList.size()];
					int i = 0;
					for (TargetStatus ts : _targetList) {
						cha[i] = ts.getTarget();
						Broadcaster.broadcastPacketExceptTargetSight(cha[i],
								new S_DoActionGFX(cha[i].getId(),
										ActionCodes.ACTION_Damage), _user);
						i++;
					}
					Broadcaster.broadcastPacket(_user, new S_RangeSkill(_user,
							cha, castgfx, actionId, S_RangeSkill.TYPE_DIR));
				}
			} else if (_skill.getTarget().equals("none")
					&& _skill.getType() == L1Skills.TYPE_ATTACK) {
				L1Character[] cha = new L1Character[_targetList.size()];
				int i = 0;
				for (TargetStatus ts : _targetList) {
					cha[i] = ts.getTarget();
					i++;
				}
				Broadcaster.broadcastPacket(_user, new S_RangeSkill(_user, cha,
						castgfx, actionId, S_RangeSkill.TYPE_NODIR));
			} else {
				if (_skillId != 5 && _skillId != 69 && _skillId != 131) {
					S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _skill
							.getActionId());
					Broadcaster.broadcastPacket(_user, gfx);
					Broadcaster.broadcastPacket(_user, new S_SkillSound(
							targetid, castgfx));
				}
			}
		}
	}

	// 중복 할 수 없는 스킬의 삭제
	// 예：파이아웨폰과 바닝웨폰 등
	private void deleteRepeatedSkills(L1Character cha) {
		final int[][] repeatedSkills = {
				// { HOLY_WEAPON, ENCHANT_WEAPON, BLESS_WEAPON, SHADOW_FANG },
				{ FIRE_WEAPON, WIND_SHOT, FIRE_BLESS, STORM_EYE,
						BURNING_WEAPON, STORM_SHOT },
				{ SHIELD, SHADOW_ARMOR, EARTH_SKIN, EARTH_BLESS, IRON_SKIN },
				{ HOLY_WALK, BLOOD_LUST, MOVING_ACCELERATION, WIND_WALK,
						STATUS_BRAVE, STATUS_ELFBRAVE },
				{ HASTE, GREATER_HASTE, STATUS_HASTE },
				{ PHYSICAL_ENCHANT_DEX, DRESS_DEXTERITY },
				{ PHYSICAL_ENCHANT_STR, DRESS_MIGHTY },
				{ GLOWING_AURA, SHINING_AURA },
				{ FAFU_MAAN, ANTA_MAAN, LIND_MAAN, VALA_MAAN, LIFE_MAAN,BIRTH_MAAN, SHAPE_MAAN },// 마안
{ PAP_FIVEPEARLBUFF, PAP_MAGICALPEARLBUFF }};
		for (int[] skills : repeatedSkills) {
			for (int id : skills) {
				if (id == _skillId) {
					stopSkillList(cha, skills);
				}
			}
		}
	}

	private void stopSkillList(L1Character cha, int[] repeat_skill) {
		for (int skillId : repeat_skill) {
			if (skillId != _skillId) {
				cha.getSkillEffectTimerSet().removeSkillEffect(skillId);
			}
		}
	}

	private void setDelay() {
		_user.SkillDelayTime = System.currentTimeMillis()
				+ _skill.getReuseDelay();
		// if (_skill.getReuseDelay() > 0) {L1SkillDelay.onSkillUse(_user,
		// _skill.getReuseDelay());}
	}

	private void runSkill() {
		  /** 마법 시전시 보라돌이 */
		  if (_calcType == PC_PC) {
		   if (_skillId == ARMOR_BRAKE){
		    L1PcInstance target = (L1PcInstance)_target;
		    L1PinkName.onAction(target, _user);
		   }
		  }
		if (_skillId == CUBE_IGNITION) {
			L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(
					4500501, _skill.getBuffDuration() * 1000, _user.getX(),
					_user.getY(), _user.getMapId());
			_player.getSkillEffectTimerSet().setSkillEffect(CUBE_IGNITION,
					_skill.getBuffDuration() * 1000);
			effect.setCubeTime(4);
			effect.setCubePc(_player);
			L1Cube.getInstance().add(0, effect);
			return;
		}

		if (_skillId == CUBE_QUAKE) {
			L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(
					4500502, _skill.getBuffDuration() * 1000, _user.getX(),
					_user.getY(), _user.getMapId());
			_player.getSkillEffectTimerSet().setSkillEffect(CUBE_QUAKE,
					_skill.getBuffDuration() * 1000);
			effect.setCubeTime(4);
			effect.setCubePc(_player);
			L1Cube.getInstance().add(1, effect);
			return;
		}

		if (_skillId == CUBE_SHOCK) {
			L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(
					4500503, _skill.getBuffDuration() * 1000, _user.getX(),
					_user.getY(), _user.getMapId());
			_player.getSkillEffectTimerSet().setSkillEffect(CUBE_SHOCK,
					_skill.getBuffDuration() * 1000);
			effect.setCubeTime(4);
			effect.setCubePc(_player);
			L1Cube.getInstance().add(2, effect);
			return;
		}

		if (_skillId == CUBE_BALANCE) {
			L1EffectInstance effect = L1EffectSpawn.getInstance().spawnEffect(
					4500504, _skill.getBuffDuration() * 1000, _user.getX(),
					_user.getY(), _user.getMapId());
			_player.getSkillEffectTimerSet().setSkillEffect(CUBE_BALANCE,
					_skill.getBuffDuration() * 1000);
			effect.setCubeTime(5);
			effect.setCubePc(_player);
			L1Cube.getInstance().add(3, effect);
			return;
		}

		if (_skillId == LIFE_STREAM) {
			L1EffectSpawn.getInstance().spawnEffect(81169,
					_skill.getBuffDuration() * 1000, _targetX, _targetY,
					_user.getMapId());
			return;
		}

		if (_skillId == FIRE_WALL) {
			L1EffectSpawn.getInstance().doSpawnFireWall(_user, _targetX,
					_targetY);
			return;
		}
		// 발라카스 범위 파이어월, 독구름 마법 추가 - 몹스킬패턴 추가 : 시작
		if (_skillId == Mob_AREA_FIRE_WALL) { // 범위 파이어월
			// 발라카스 파이어월 본섭 스타일 - 시작
			int xx = 0;
			int yy = 0;
			int xx1 = 0;
			int yy1 = 0;
			int xx2 = 0;
			int yy2 = 0;
			// / 랜덤으로 0-2픽셀 거리변경사용
			Random random = new Random();
			int randomxy = random.nextInt(5);
			int r = random.nextInt(2) + 1;
			int a1 = 3 + randomxy;
			int a2 = -3 - randomxy;
			int b1 = 2 + randomxy;
			int b2 = -2 - randomxy;
			int heading = _npc.getMoveState().getHeading(); // 몹 방향  pc.getMoveState().getHeading()
			switch (heading) {
			case 1:
				xx = a1 - r;
				yy = a2 + r;
				yy1 = a2;
				xx2 = a1;
				break;
			case 2:
				xx = a1 + 1;
				xx1 = b1;
				yy1 = a2;
				xx2 = b1;
				yy2 = a1;
				break;
			case 3:
				xx = a1 - r;
				yy = a1 - r;
				xx1 = a1;
				yy2 = a1;
				break;
			case 4:
				yy = a1 + 1;
				xx1 = a1;
				yy1 = b1;
				xx2 = a2;
				yy2 = b1;
				break;
			case 5:
				xx = a2 + r;
				yy = a1 - r;
				yy1 = a1;
				xx2 = a2;
				break;
			case 6:
				xx = a2 - 1;
				xx1 = b2;
				yy1 = a1;
				xx2 = b2;
				yy2 = a2;
				break;
			case 7:
				xx = a2 + r;
				yy = a2 + r;
				xx1 = a2;
				yy2 = a2;
				break;
			case 0:
				yy = a2 - 1;
				xx1 = a2;
				yy1 = b2;
				xx2 = a1;
				yy2 = b2;
				break;
			default:
				break;
			}
			int x = _npc.getX() + xx;
			int y = _npc.getY() + yy;
			// 마름모 4*4픽셀 모양 (발라카스 기준에서 정면에 출현)
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x, y, _user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(81157, _skill.getBuffDuration() * 1000, x,
							y + 1, _user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(81157, _skill.getBuffDuration() * 1000, x,
							y - 1, _user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(81157, _skill.getBuffDuration() * 1000, x,
							y - 2, _user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(81157, _skill.getBuffDuration() * 1000, x - 1,
							y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x - 1, y + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x - 1, y - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x - 1, y - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x + 1, y + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x + 1, y - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(81157, _skill.getBuffDuration() * 1000, x + 1,
							y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x + 1, y - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x + 2, y - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x + 2, y - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(81157, _skill.getBuffDuration() * 1000, x + 2,
							y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x + 2, y + 1,
					_user.getMapId());
			int x1 = _npc.getX() + xx1;
			int y1 = _npc.getY() + yy1 - 1;
			// 마름모 4*4픽셀 모양 (발라카스 기준에서 좌측에 출현)
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1, y1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1, y1 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1, y1 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1, y1 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1 - 1, y1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1 - 1, y1 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1 - 1, y1 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1 - 1, y1 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1 + 1, y1 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1 + 1, y1 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1 + 1, y1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1 + 1, y1 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1 + 2, y1 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1 + 2, y1 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1 + 2, y1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x1 + 2, y1 + 1,
					_user.getMapId());
			int x2 = _npc.getX() + xx2 + 1;
			int y2 = _npc.getY() + yy2;
			// 마름모 4*4픽셀 모양 (발라카스 기준에서 우측에 출현)
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2, y2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2, y2 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2, y2 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2, y2 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2 - 1, y2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2 - 1, y2 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2 - 1, y2 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2 - 1, y2 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2 + 1, y2 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2 + 1, y2 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2 + 1, y2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2 + 1, y2 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2 + 2, y2 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2 + 2, y2 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2 + 2, y2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157,
					_skill.getBuffDuration() * 1000, x2 + 2, y2 + 1,
					_user.getMapId());
			// 발라카스 파이어월 본섭 스타일 - 끝

			return;
		}
		if (_skillId == Mob_AREA_POISON_18 || _skillId == Mob_AREA_POISON_30
				|| _skillId == Mob_AREA_POISON) {
			// 범위 독구름(안타라스, 제니스퀸류, 머미로드류, 오염엔트, 오염펑거스)
			// 독구름 본섭 스타일(몹 정면으로 3방향) - 시작
			int xx = 0;
			int yy = 0;
			int xx1 = 0;
			int yy1 = 0;
			int xx2 = 0;
			int yy2 = 0;
			// / 랜덤으로 0-2픽셀 거리변경사용
			Random random = new Random();
			int randomxy = random.nextInt(3);
			int r = random.nextInt(2) + 1;
			int a1 = 3 + randomxy;
			int a2 = -3 - randomxy;
			int b1 = 2 + randomxy;
			int b2 = -2 - randomxy;
			int heading = _npc.getMoveState().getHeading(); // 몹 방향
															// pc.getMoveState().getHeading()
			switch (heading) {
			case 1:
				xx = a1 - r;
				yy = a2 + r;
				yy1 = a2;
				xx2 = a1;
				break;
			case 2:
				xx = a1 + 1;
				xx1 = b1;
				yy1 = a2;
				xx2 = b1;
				yy2 = a1;
				break;
			case 3:
				xx = a1 - r;
				yy = a1 - r;
				xx1 = a1;
				yy2 = a1;
				break;
			case 4:
				yy = a1 + 1;
				xx1 = a1;
				yy1 = b1;
				xx2 = a2;
				yy2 = b1;
				break;
			case 5:
				xx = a2 + r;
				yy = a1 - r;
				yy1 = a1;
				xx2 = a2;
				break;
			case 6:
				xx = a2 - 1;
				xx1 = b2;
				yy1 = a1;
				xx2 = b2;
				yy2 = a2;
				break;
			case 7:
				xx = a2 + r;
				yy = a2 + r;
				xx1 = a2;
				yy2 = a2;
				break;
			case 0:
				yy = a2 - 1;
				xx1 = a2;
				yy1 = b2;
				xx2 = a1;
				yy2 = b2;
				break;
			default:
				break;
			}
			int x = _npc.getX() + xx;
			int y = _npc.getY() + yy;
			// 마름모 4*4픽셀 모양 (몹 기준에서 정면에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x, y, _user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(777777, _skill.getBuffDuration() * 1000, x,
							y + 1, _user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(777777, _skill.getBuffDuration() * 1000, x,
							y - 1, _user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(777777, _skill.getBuffDuration() * 1000, x,
							y - 2, _user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(777777, _skill.getBuffDuration() * 1000,
							x - 1, y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x - 1, y + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x - 1, y - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x - 1, y - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x + 1, y + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x + 1, y - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(777777, _skill.getBuffDuration() * 1000,
							x + 1, y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x + 1, y - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x + 2, y - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x + 2, y - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(777777, _skill.getBuffDuration() * 1000,
							x + 2, y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x + 2, y + 1,
					_user.getMapId());
			int x1 = _npc.getX() + xx1;
			int y1 = _npc.getY() + yy1;
			// 마름모 4*4픽셀 모양 (몹 기준에서 좌측에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1, y1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1, y1 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1, y1 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1, y1 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 - 1, y1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 - 1, y1 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 - 1, y1 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 - 1, y1 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 1, y1 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 1, y1 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 1, y1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 1, y1 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 2, y1 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 2, y1 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 2, y1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 2, y1 + 1,
					_user.getMapId());
			int x2 = _npc.getX() + xx2;
			int y2 = _npc.getY() + yy2;
			// 마름모 4*4픽셀 모양 (몹 기준에서 우측에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2, y2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2, y2 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2, y2 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2, y2 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 - 1, y2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 - 1, y2 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 - 1, y2 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 - 1, y2 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 1, y2 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 1, y2 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 1, y2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 1, y2 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 2, y2 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 2, y2 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 2, y2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 2, y2 + 1,
					_user.getMapId());
			// 독구름 본섭 스타일(몹 정면으로 3방향 사용) - 끝

			return;
		}
		/** 용언 * */
		if (_skillId == Mob_AREA_POISON_20 // 범위 독구름 5뭉치(안타라스 리뉴얼)
				|| _skillId == ANTA_SKILL_6
				|| _skillId == ANTA_SKILL_7
				|| _skillId == ANTA_SKILL_10 // 안타라스 용언
		) {
			int npcId = _npc.getNpcTemplate().get_npcId();
			if (npcId == 4039020 || npcId == 4039021 || npcId == 4039022) { // 안타라스(리뉴얼)1,2,3단계
				if (_skillId == ANTA_SKILL_6) { // 안타라스(리뉴얼) - 용언 스킬6
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(	_npc, "$7862", 0));

					// _user.broadcastPacket(new S_NpcChatPacket(_npc, "$7862",
					// 0));
					// 오브 모크! 리라프[웨폰브레이크 + 브레스(독구름)]
					if (_user instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) _user;
						L1ItemInstance weapon = pc.getWeapon();
						Random random = new Random();
						int rnd = random.nextInt(100) + 1;
						if (weapon != null
								&& rnd <= (100 - pc.getAbility().getBaseStr()) / 10) {// 확률:힘영향pc.getAbility().addAddedStr((byte) -5);
							int weaponDamage = random.nextInt(3) + 1;// /int chance =random.nextInt(10) + 5;
							pc.sendPackets(new S_ServerMessage(268, weapon .getLogName()));
							pc.getInventory().receiveDamage(weapon, weaponDamage);
						}
					}
				}
				if (_skillId == ANTA_SKILL_7) { // 안타라스(리뉴얼) - 용언 스킬7
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
							_npc, "$7911", 0));

					// 오브 모크! 켄 로우[독구름 + 브레스]
				}
				if (_skillId == ANTA_SKILL_10) { // 안타라스(리뉴얼) - 용언 스킬10
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
							_npc, "$7863", 0));
					// 오브 모크! 루오 타[독구름 + 이럽브레스]
				}
			}

			// 독구름 본섭 스타일(몹 정면으로 5방향) - 시작
			int xx = 0;
			int yy = 0;
			int xx1 = 0;
			int yy1 = 0;
			int xx2 = 0;
			int yy2 = 0;
			int xx3 = 0;
			int yy3 = 0;
			int xx4 = 0;
			int yy4 = 0;
			int randomxy = random.nextInt(4); // / 랜덤으로 0-4픽셀 거리변경사용
			int r = random.nextInt(2) + 1; // (1~2)
			int a1 = 3 + randomxy;
			int a2 = -3 - randomxy;
			int b1 = 2 + randomxy;
			int b2 = -2 - randomxy;
			int heading = _npc.getMoveState().getHeading(); // 몹 방향 pc.getMoveState().getHeading()
			switch (heading) {
			case 1: // 12시 방향
				xx = a1 - r;
				yy = a2 + r;
				yy1 = a2;
				xx2 = a1;
				xx3 = a2;
				yy3 = b2;
				xx4 = b1;
				yy4 = a1;
				break;
			case 2: // 1-2시 방향
				xx = a1 + 1;
				xx1 = b1;
				yy1 = a2;
				xx2 = b1;
				yy2 = a1;
				xx3 = b1 - 3;
				yy3 = a2 - 2;
				xx4 = b1 - 2;
				yy4 = a1 + 3;
				break;
			case 3: // 3시 방향
				xx = a1 - r;
				yy = a1 - r;
				xx1 = a1;
				yy2 = a1;
				xx3 = a1;
				yy3 = a2;
				xx4 = a2;
				yy4 = b1;
				break;
			case 4: // 4-5시 방향
				yy = a1 + 1;
				xx1 = a1;
				yy1 = b1;
				xx2 = a2;
				yy2 = b1;
				xx3 = a1 + 3;
				yy3 = b1 - 3;
				xx4 = a2 - 3;
				yy4 = b1 - 3;
				break;
			case 5: // 6시 방향
				xx = a2 + r;
				yy = a1 - r;
				yy1 = a1;
				xx2 = a2;
				xx3 = a1;
				yy3 = b1;
				xx4 = b2;
				yy4 = a2;
				break;
			case 6: // 7-8시 방향
				xx = a2 - 1;
				xx1 = b2;
				yy1 = a1;
				xx2 = b2;
				yy2 = a2;
				xx3 = b2 + 3;
				yy3 = a1 + 2;
				xx4 = b2 + 2;
				yy4 = a2 - 3;
				break;
			case 7: // 9시 방향
				xx = a2 + r;
				yy = a2 + r;
				xx1 = a2;
				yy2 = a2;
				xx3 = a2;
				yy3 = a1;
				xx4 = a1;
				yy4 = b2;
				break;
			case 0: // 10-11시 방향
				yy = a2 - 1;
				xx1 = a2;
				yy1 = b2;
				xx2 = a1;
				yy2 = b2;
				xx3 = a2 - 3;
				yy3 = b2 + 3;
				xx4 = a1 + 3;
				yy4 = b2 + 3;
				break;
			default:
				break;
			}
			int x = _npc.getX() + xx;
			int y = _npc.getY() + yy;
			// 마름모 4*4픽셀 모양 (몹 기준에서 정면에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x, y, _user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(777777, _skill.getBuffDuration() * 1000, x,
							y + 1, _user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(777777, _skill.getBuffDuration() * 1000, x,
							y - 1, _user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(777777, _skill.getBuffDuration() * 1000, x,
							y - 2, _user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(777777, _skill.getBuffDuration() * 1000,
							x - 1, y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x - 1, y + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x - 1, y - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x - 1, y - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x + 1, y + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x + 1, y - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(777777, _skill.getBuffDuration() * 1000,
							x + 1, y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x + 1, y - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x + 2, y - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x + 2, y - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance()
					.spawnEffect(777777, _skill.getBuffDuration() * 1000,
							x + 2, y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x + 2, y + 1,
					_user.getMapId());
			int x1 = _npc.getX() + xx1;
			int y1 = _npc.getY() + yy1;
			// 마름모 4*4픽셀 모양 (몹 기준에서 좌측에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1, y1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1, y1 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1, y1 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1, y1 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 - 1, y1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 - 1, y1 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 - 1, y1 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 - 1, y1 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 1, y1 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 1, y1 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 1, y1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 1, y1 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 2, y1 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 2, y1 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 2, y1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x1 + 2, y1 + 1,
					_user.getMapId());
			int x2 = _npc.getX() + xx2;
			int y2 = _npc.getY() + yy2;
			// 마름모 4*4픽셀 모양 (몹 기준에서 우측에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2, y2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2, y2 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2, y2 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2, y2 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 - 1, y2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 - 1, y2 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 - 1, y2 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 - 1, y2 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 1, y2 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 1, y2 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 1, y2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 1, y2 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 2, y2 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 2, y2 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 2, y2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x2 + 2, y2 + 1,
					_user.getMapId());
			int x3 = _npc.getX() + xx3;
			int y3 = _npc.getY() + yy3;
			// 마름모 4*4픽셀 모양 (몹 기준에서 좌측2에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3, y3, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3, y3 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3, y3 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3, y3 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3 - 1, y3,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3 - 1, y3 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3 - 1, y3 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3 - 1, y3 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3 + 1, y3 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3 + 1, y3 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3 + 1, y3,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3 + 1, y3 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3 + 2, y3 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3 + 2, y3 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3 + 2, y3,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x3 + 2, y3 + 1,
					_user.getMapId());
			int x4 = _npc.getX() + xx4;
			int y4 = _npc.getY() + yy4;
			// 마름모 4*4픽셀 모양 (몹 기준에서 우측2에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4, y4, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4, y4 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4, y4 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4, y4 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4 - 1, y4,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4 - 1, y4 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4 - 1, y4 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4 - 1, y4 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4 + 1, y4 + 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4 + 1, y4 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4 + 1, y4,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4 + 1, y4 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4 + 2, y4 - 2,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4 + 2, y4 - 1,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4 + 2, y4,
					_user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777,
					_skill.getBuffDuration() * 1000, x4 + 2, y4 + 1,
					_user.getMapId());
			// 독구름 본섭 스타일(몹 정면으로 5방향 사용) - 끝
			return;
		}
		for (int skillId : EXCEPT_COUNTER_MAGIC) {
			if (_skillId == skillId) {
				_isCounterMagic = false;
				break;
			}
		}
		if (_skillId == SHOCK_STUN/*
									 * || _skillId == BONE_BREAK || _skillId ==
									 * AM_BREAK || _skillId == SMASH
									 */&& _user instanceof L1PcInstance) {
			_target.onAction(_player);
		}
		if (!isTargetCalc(_target)) {
			return;
		}

		try {
			TargetStatus ts = null;
			L1Character cha = null;
			int dmg = 0;
			int drainMana = 0;
			int heal = 0;
			boolean isSuccess = false;
			int undeadType = 0;
			int StartCK = 0;
			for (Iterator<TargetStatus> iter = _targetList.iterator(); iter
					.hasNext();) {
				ts = null;
				cha = null;
				dmg = 0;
				heal = 0;
				isSuccess = false;
				undeadType = 0;

				ts = iter.next();
				cha = ts.getTarget();
				if (!ts.isCalc() || !isTargetCalc(cha)) {
					continue;
				}

				L1Magic _magic = new L1Magic(_user, cha);
				_magic.setLeverage(getLeverage());

				if (cha instanceof L1MonsterInstance) {
					undeadType = ((L1MonsterInstance) cha).getNpcTemplate()
							.get_undead();
				}

				if ((_skill.getType() == L1Skills.TYPE_CURSE || _skill
						.getType() == L1Skills.TYPE_PROBABILITY)
						&& isTargetFailure(cha)) {
					iter.remove();
					continue;
				}

				if (cha instanceof L1PcInstance) {
					if (_skillTime == 0) {
						_getBuffIconDuration = _skill.getBuffDuration();
					} else {
						_getBuffIconDuration = _skillTime;
					}
				}

				deleteRepeatedSkills(cha);

				if (_skill.getType() == L1Skills.TYPE_ATTACK
						&& _user.getId() != cha.getId()) {
					if (isUseCounterMagic(cha)) {
						iter.remove();
						continue;
					}

					if (StartCK == 0)
						dmg = _magic.calcMagicDamage(_skillId);

					if (StartCK == 1 || StartCK == 2)
						dmg = _magic.calcMagicDamage(_skillId);
					else if (StartCK == 3) {
						if (_user instanceof L1PcInstance
								&& cha instanceof L1PcInstance) {
							L1PcInstance _P = (L1PcInstance) _user;
							L1PcInstance _pc = (L1PcInstance) cha;
							if (_P.getClanid() > 0
									&& (_P.getClanid() == _pc.getClanid())) {
								if (_skillId == 17 || _skillId == 22
										|| _skillId == 25 || _skillId == 53
										|| _skillId == 59 || _skillId == 62
										|| _skillId == 65 || _skillId == 70
										|| _skillId == 74 || _skillId == 80) // 미티어
																				// 포함한
																				// 범위마법들..
									dmg = 0;
							} else {
								dmg = _magic.calcMagicDamage(_skillId);
							}
						} else {
							dmg = _magic.calcMagicDamage(_skillId);
						}
					} else if (StartCK == 0) {
						if (_user instanceof L1PcInstance
								&& cha instanceof L1PcInstance) {
							L1PcInstance _P = (L1PcInstance) _user;
							L1PcInstance _pc = (L1PcInstance) cha;
							if (_P.getClanid() > 0
									&& (_P.getClanid() == _pc.getClanid())) {
								if (_skillId == 17 || _skillId == 22
										|| _skillId == 25 || _skillId == 53
										|| _skillId == 59 || _skillId == 62
										|| _skillId == 65 || _skillId == 70
										|| _skillId == 74 || _skillId == 80) { // 미티어
																				// 포함한
																				// 범위마법들..
									StartCK = 1;
								} else
									StartCK = 2;
							} else
								StartCK = 3;
						}
					}
					// 공격 스킬일때!! 이레이즈 여부 판멸후 제거
					 if (_skillId != TRIPLE_ARROW &&_skillId != FOU_SLAYER) {//7월12일추가
					if (cha.getSkillEffectTimerSet().hasSkillEffect(ERASE_MAGIC)) {
						cha.getSkillEffectTimerSet().removeSkillEffect(ERASE_MAGIC);
					}
					 }
				} else if (_skill.getType() == L1Skills.TYPE_CURSE
						|| _skill.getType() == L1Skills.TYPE_PROBABILITY) {
					isSuccess = _magic.calcProbabilityMagic(_skillId);
					
					if (_skillId == L1SkillId.CANCELLATION && isSuccess) {
						/** 2011.11.13 SOCOOL 인공지능 */
						if (_target != null && _target instanceof L1PcInstance) {
							L1PcInstance robot = (L1PcInstance)_target;	
							if (robot.getRobotAi() != null) {
								if ((robot.noPlayerCK || robot.isGm()) && robot.getClanid() != 0 && !robot.getMap().isSafetyZone(robot.getLocation())) {
									robot.getRobotAi().setCancellationCount(5);
									if (!robot.getRobotAi().getAttackList().containsKey(_player) && robot.getClanid() != _player.getClanid()) {
										robot.getRobotAi().getAttackList().add(_player,0);
									}
								} else if (!robot.getMap().isSafetyZone(robot.getLocation())) {
									if (robot.getMap().isTeleportable()) {
										L1Location newLocation = robot.getLocation().randomLocation(200, true);
										int newX = newLocation.getX();
										int newY = newLocation.getY();
										short mapId = (short) newLocation.getMapId();

										L1Teleport.teleport(robot, newX, newY, mapId, robot.getMoveState().getHeading(), true);
									}
								}
							}
						}
					}
					
					// 이레 마법이 아니고 현제 이레중이라면!!!
					if (_skillId != ERASE_MAGIC && _skillId != EARTH_BIND) {
						if (cha.getSkillEffectTimerSet().hasSkillEffect(
								ERASE_MAGIC)) {
							cha.getSkillEffectTimerSet().removeSkillEffect(
									ERASE_MAGIC);
						}
					}
					if (_skillId != FOG_OF_SLEEPING) {
						cha.getSkillEffectTimerSet().removeSkillEffect(
								FOG_OF_SLEEPING);
					}
					if (_skillId != PHANTASM) {
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(PHANTASM);
					}
					if (isSuccess) {
						if (isUseCounterMagic(cha)) {
							iter.remove();
							continue;
						}
					} else {
						if (_skillId == FOG_OF_SLEEPING
								&& cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
						//	pc.sendPackets(new S_ServerMessage(297));
							S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"가벼운 현기증을 느꼈습니다.", Opcodes.S_OPCODE_MSG, 20); 
							pc.sendPackets(s_chatpacket);
						}
						iter.remove();
						continue;
					}
				} else if (_skill.getType() == L1Skills.TYPE_HEAL) {
					dmg = -1 * _magic.calcHealing(_skillId);
					if (cha.getSkillEffectTimerSet().hasSkillEffect(WATER_LIFE)) {
						dmg *= 2;
					}
					if (cha.getSkillEffectTimerSet().hasSkillEffect(
							POLLUTE_WATER)) {
						dmg /= 2;
					}
					if (_user.getSkillEffectTimerSet().hasSkillEffect(10517)) {// 리듀스힐
						dmg /= 2;
						if (cha.getSkillEffectTimerSet().hasSkillEffect(
								WATER_LIFE)) {
							dmg *= 1;
						}
						if (cha.getSkillEffectTimerSet().hasSkillEffect(
								POLLUTE_WATER)) {
							dmg /= 4;
						}
					}
					if (_user.getSkillEffectTimerSet().hasSkillEffect(10518)) {// 데스힐
						dmg = -dmg;
						if (cha.getSkillEffectTimerSet().hasSkillEffect(
								WATER_LIFE)) {
							dmg *= 2;
						}
						if (cha.getSkillEffectTimerSet().hasSkillEffect(
								POLLUTE_WATER)) {
							dmg /= 2;
						}
					}

				}
				// ■■■■ 개별 처리가 있는 스킬만 써 주세요. ■■■■
				if (cha.getSkillEffectTimerSet().hasSkillEffect(_skillId)
						&& _skillId != SHOCK_STUN && _skillId != THUNDER_GRAB) {
					addMagicList(cha, true);
					if (_skillId != SHAPE_CHANGE) {
						continue;
					}
				}

				// ●●●● PC, NPC 양쪽 모두 효과가 있는 스킬 ●●●●
				// GFX Check (Made by HuntBoy)
				switch (_skillId) {
				
				/** 기르타스 관련 */
			    case TROGIR_MILPITAS3:{
			     L1PcInstance pc = (L1PcInstance) cha;
			     pc.sendPackets(new S_Sound(11002));
			     pc.broadcastPacket(new S_Sound(11002));
			     dmg = _magic.calcMagicDamage(_skillId);
			    }
			    break;
			    case TROGIR_MILPITAS4:{
			     L1PcInstance pc = (L1PcInstance) cha;
			     pc.sendPackets(new S_Sound(11003));
			     pc.broadcastPacket(new S_Sound(11003));
			     dmg = _magic.calcMagicDamage(_skillId);
			    }
			    break;
			    case TROGIR_MILPITAS5:{
			     L1PcInstance pc = (L1PcInstance) cha;
			     pc.sendPackets(new S_Sound(11004));
			     pc.broadcastPacket(new S_Sound(11004));
			     dmg = _magic.calcMagicDamage(_skillId);
			    }
			    break;
			    case TROGIR_MILPITAS6:{
			     L1PcInstance pc = (L1PcInstance) cha;
			     pc.sendPackets(new S_Sound(11008));
			     pc.broadcastPacket(new S_Sound(11008));
			     dmg = _magic.calcMagicDamage(_skillId);
			    }
			    break;
			    case TROGIR_MILPITAS7:{
			     L1PcInstance pc = (L1PcInstance) cha;
			     pc.sendPackets(new S_Sound(11007));
			     pc.broadcastPacket(new S_Sound(11007));
			     dmg = _magic.calcMagicDamage(_skillId);
			    }
			    break;
			    case TROGIR_MILPITAS2:{
			     L1PcInstance pc = (L1PcInstance) cha;
			     pc.sendPackets(new S_Sound(11009));
			     pc.broadcastPacket(new S_Sound(11009));
			     int[] mobArray = { 181164 };
			     int rnd = _random.nextInt(mobArray.length);
			     L1SpawnUtil.spawn(pc, mobArray[rnd], 5, 6 * 2000, true);
			     pc.sendPackets(new S_Sound(11006));
			     pc.broadcastPacket(new S_Sound(11006));
			     pc.sendPackets(new S_SkillSound(pc.getId(), 11473));
			     pc.broadcastPacket(new S_SkillSound(pc.getId(), 11473));
			     dmg = _magic.calcMagicDamage(_skillId);
			    }
			    break;

				
				case HASTE: {
					if (cha.getMoveState().getMoveSpeed() != 2) {
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							if (pc.getHasteItemEquipped() > 0) {
								continue;
							}
							pc.setDrink(false);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 1,
									_getBuffIconDuration));
						}
						Broadcaster.broadcastPacket(cha, new S_SkillHaste(cha
								.getId(), 1, 0));
						cha.getMoveState().setMoveSpeed(1);
					} else {
						int skillNum = 0;
						if (cha.getSkillEffectTimerSet().hasSkillEffect(SLOW)) {
							skillNum = SLOW;
						} else if (cha.getSkillEffectTimerSet().hasSkillEffect(
								MASS_SLOW)) {
							skillNum = MASS_SLOW;
						} else if (cha.getSkillEffectTimerSet().hasSkillEffect(
								ENTANGLE)) {
							skillNum = ENTANGLE;
						} else if (cha.getSkillEffectTimerSet().hasSkillEffect(
								MOB_SLOW_1)) {
							skillNum = MOB_SLOW_1;
						} else if (cha.getSkillEffectTimerSet().hasSkillEffect(
								MOB_SLOW_18)) {
							skillNum = MOB_SLOW_18;
						}
						if (skillNum != 0) {
							cha.getSkillEffectTimerSet().removeSkillEffect(
									skillNum);
							cha.getSkillEffectTimerSet().removeSkillEffect(
									HASTE);
							cha.getMoveState().setMoveSpeed(0);
							continue;
						}
					}
				}
					break;
				case CURE_POISON: {
					cha.curePoison();
				}
					break;
				case REMOVE_CURSE: {
					cha.curePoison();
					if (cha.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_CURSE_PARALYZING)
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									STATUS_CURSE_PARALYZED)
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									Mob_RANGESTUN_19)
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									Mob_RANGESTUN_18)
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									Mob_CURSEPARALYZ_19)
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									Mob_CURSEPARALYZ_18)
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									Mob_CURSEPARALYZ_30)
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									Mob_CURSEPARALYZ_SHORT_18)
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									Mob_RANGESTUN_30)
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									CURSE_BLIND)
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									CURSE_PARALYZE)
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									ANTA_SKILL_3)// 안타라스 용언
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									ANTA_SKILL_4)
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									ANTA_SKILL_5)) {
						cha.cureParalaysis();
					}
					if (cha.getSkillEffectTimerSet()
							.hasSkillEffect(CURSE_BLIND)
							|| cha.getSkillEffectTimerSet().hasSkillEffect(
									DARKNESS)) {
						if (cha.getSkillEffectTimerSet().hasSkillEffect(
								CURSE_BLIND)) {
							cha.getSkillEffectTimerSet().removeSkillEffect(
									CURSE_BLIND);
						} else if (cha.getSkillEffectTimerSet().hasSkillEffect(
								DARKNESS)) {
							cha.getSkillEffectTimerSet().removeSkillEffect(
									DARKNESS);
						}
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.sendPackets(new S_CurseBlind(0));
						}
					}
				}
					break;
				case RESURRECTION:
				case GREATER_RESURRECTION: {
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						if (_player.getId() != pc.getId()) {
							if (L1World.getInstance().getVisiblePlayer(pc, 0)
									.size() > 0) {
								for (L1PcInstance visiblePc : L1World
										.getInstance().getVisiblePlayer(pc, 0)) {
									if (!visiblePc.isDead()) {
										_player
												.sendPackets(new S_ServerMessage(
														592));
										return;
									}
								}
							}
							if (pc.getCurrentHp() == 0 && pc.isDead()) {
								if (pc.getMap().isUseResurrection()) {
									if (_skillId == RESURRECTION) {
										pc.setGres(false);
									} else if (_skillId == GREATER_RESURRECTION) {
										pc.setGres(true);
									}
									pc.setTempID(_player.getId());
									pc.sendPackets(new S_Message_YN(322, ""));
								}
							}
						}
					}
					if (cha instanceof L1NpcInstance) {
						if (!(cha instanceof L1TowerInstance)) {
							L1NpcInstance npc = (L1NpcInstance) cha;
							if (npc instanceof L1PetInstance
									&& L1World.getInstance().getVisiblePlayer(
											npc, 0).size() > 0) {
								for (L1PcInstance visiblePc : L1World
										.getInstance().getVisiblePlayer(npc, 0)) {
									if (!visiblePc.isDead()) {
										_player
												.sendPackets(new S_ServerMessage(
														592));
										return;
									}
								}
							}
							if (npc.getCurrentHp() == 0 && npc.isDead()) {
								npc.resurrect(npc.getMaxHp() / 4);
								npc.setResurrect(true);
							}
						}
					}
				}
					break;
				case CALL_OF_NATURE: {
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						if (_player.getId() != pc.getId()) {
							if (L1World.getInstance().getVisiblePlayer(pc, 0)
									.size() > 0) {
								for (L1PcInstance visiblePc : L1World
										.getInstance().getVisiblePlayer(pc, 0)) {
									if (!visiblePc.isDead()) {
										_player
												.sendPackets(new S_ServerMessage(
														592));
										return;
									}
								}
							}
							if (pc.getCurrentHp() == 0 && pc.isDead()) {
								pc.setTempID(_player.getId());
								pc.sendPackets(new S_Message_YN(322, ""));
							}
						}
					}
					if (cha instanceof L1NpcInstance) {
						if (!(cha instanceof L1TowerInstance)) {
							L1NpcInstance npc = (L1NpcInstance) cha;
							if (npc instanceof L1PetInstance
									&& L1World.getInstance().getVisiblePlayer(
											npc, 0).size() > 0) {
								for (L1PcInstance visiblePc : L1World
										.getInstance().getVisiblePlayer(npc, 0)) {
									if (!visiblePc.isDead()) {
										_player
												.sendPackets(new S_ServerMessage(
														592));
										return;
									}
								}
							}
							if (npc.getCurrentHp() == 0 && npc.isDead()) {
								npc.resurrect(cha.getMaxHp());
								npc.resurrect(cha.getMaxMp() / 100);
								npc.setResurrect(true);
							}
						}
					}
				}
					break;
				case FREEZING_BREATH: {//용기사 디텍션
					if (cha instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						int hiddenStatus = npc.getHiddenStatus();
						if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
							if (npc.getNpcId() != 45682)
								npc.appearOnGround(_player);
						}
					}
				}
					break;
				case DETECTION: {//디텍션
					if (cha instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						int hiddenStatus = npc.getHiddenStatus();
						if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
							if (npc.getNpcId() != 45682)
								npc.appearOnGround(_player);
						}
					}
				}
					break;
				case AM_BREAK: {//환술사 디텍션
					if (cha instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						int hiddenStatus = npc.getHiddenStatus();
						if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
							if (npc.getNpcId() != 45682)
								npc.appearOnGround(_player);
						}
					}
				}
					break;
				// UI DG표시
				case UNCANNY_DODGE: // 언케니닷지
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDg(-5);
					}
					break;
				// UI DG표시
				case COUNTER_DETECTION: {
					if (cha instanceof L1PcInstance) {
						dmg = _magic.calcMagicDamage(_skillId);
					} else if (cha instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						int hiddenStatus = npc.getHiddenStatus();
						if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
							if (npc.getNpcId() != 45682)
								npc.appearOnGround(_player);
						} else {
							dmg = 0;
						}
					} else {
						dmg = 0;
					}
				}
					break;
				case JOY_OF_PAIN: {
					int selldmg = _player.getMaxHp() - _player.getCurrentHp();
					dmg = selldmg / 5;
				}
					break;
				case MIND_BREAK: {
					if (_target.getCurrentMp() >= 5) {
						_target.setCurrentMp(_target.getCurrentMp() - 5);
						dmg = 28;
					} else {
						return;
					}
				}
					break;
				case TRUE_TARGET: {
					if (_user instanceof L1PcInstance) {
						L1PcInstance pri = (L1PcInstance) _user;
						pri.sendPackets(new S_TrueTarget(_targetID,_targetX,_targetY, _message));
						for (L1PcInstance pc : L1World.getInstance()
								.getRecognizePlayer(_target)) {
							if (pri.getClanid() == pc.getClanid()) {
								pc.sendPackets(new S_TrueTarget(_targetID,_targetX,_targetY, _message));
							}
						}
					}
				}
					break;
				case ELEMENTAL_FALL_DOWN: {
					if (_user instanceof L1PcInstance) {
						int playerAttr = _player.getElfAttr();
						int i = -50;
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							switch (playerAttr) {
							case 0:
								_player.sendPackets(new S_ServerMessage(79));
								break;
							case 1:
								pc.getResistance().addEarth(i);
								pc.setAddAttrKind(1);
								break;
							case 2:
								pc.getResistance().addFire(i);
								pc.setAddAttrKind(2);
								break;
							case 4:
								pc.getResistance().addWater(i);
								pc.setAddAttrKind(4);
								break;
							case 8:
								pc.getResistance().addWind(i);
								pc.setAddAttrKind(8);
								break;
							default:
								break;
							}
						} else if (cha instanceof L1MonsterInstance) {
							L1MonsterInstance mob = (L1MonsterInstance) cha;
							switch (playerAttr) {
							case 0:
								_player.sendPackets(new S_ServerMessage(79));
								break;
							case 1:
								mob.getResistance().addEarth(i);
								mob.setAddAttrKind(1);
								break;
							case 2:
								mob.getResistance().addFire(i);
								mob.setAddAttrKind(2);
								break;
							case 4:
								mob.getResistance().addWater(i);
								mob.setAddAttrKind(4);
								break;
							case 8:
								mob.getResistance().addWind(i);
								mob.setAddAttrKind(8);
								break;
							default:
								break;
							}
						}
					}
				}
					break;
				case HEAL:
				case EXTRA_HEAL:
				case GREATER_HEAL:
				case FULL_HEAL:
				case HEAL_ALL:
				case NATURES_TOUCH:
				case NATURES_BLESSING: {
					if (cha.getSkillEffectTimerSet().hasSkillEffect(WATER_LIFE)) {
						cha.getSkillEffectTimerSet().killSkillEffectTimer(
								WATER_LIFE);
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc
									.sendPackets(new S_PacketBox(
											S_PacketBox.DEL_ICON));
						}
					}
				}
					break;
				case CHILL_TOUCH: // 칠터치
				case VAMPIRIC_TOUCH: {// 뱀파
					heal = dmg;
				}
					break;
				case TRIPLE_ARROW: { // 트리플애로우
					int weaponType = _player.getWeapon().getItem().getType1();
					if (weaponType != 20)
						return;
					/*if (_calcType == PC_PC) {// 트리플데미지수정할때주석풀기
						dmg = dmg / 20 * 14;
					}*/ // 데미지를 x로나눈값에 y의 값을 곱한값의 트리플 데미지...!?
					// 데미지 / 20 * 18을 한값을 데미지로 넣었다 하면 트리플애로우 타격치의
					// 90%의 타격치를 적용하겠다.. 이부분이고 예를 들어 115%의데미지를 적용하겠다 하면
					// dmg = dmg / 20 * 23 이 되겠죠
					for (int i = 3; i > 0; i--) {
						_target.onAction(_player);
					}
					_player
							.sendPackets(new S_SkillSound(_player.getId(), 4394));
					Broadcaster.broadcastPacket(_player, new S_SkillSound(
							_player.getId(), 4394));
				}
					break;
				case FOU_SLAYER: { // 포우슬레이어
					boolean gfxcheck = false;
					int[] FouGFX = { 138, 37, 3860, 3126, 3420, 2284, 3105,
							3145, 3148, 3151, 3871, 4125, 2323, 3892, 3895,
							3898, 3901, 4917, 4918, 4919, 4950, 6140, 6145,
							6150, 6155, 6269, 6272, 6275, 6278, 6826, 6827,
							6836, 6837, 6846, 6847, 6856, 6857, 6866, 6867,
							6876, 6877, 6886, 6887, 6400, 5645, 6399, 7039,
							7040, 7041, 7140, 7144, 7148, 6160, 7152, 7156,
							7160, 7164, 7139, 7143, 7147, 7151, 7155, 7159,
							7163, 7959, 7968, 7969, 7970, 9214, 8817, 8774,
							8843, 9205, 9011, 8812, 8844, 8846, 9206, 9012 };
					int playerGFX = _player.getGfxId().getTempCharGfx();
					for (int gfx : FouGFX) {
						if (playerGFX != gfx) {
							gfxcheck = true;
							break;
						}
					}
					if (!gfxcheck) {
						return;
					}

					for (int i = 3; i > 0; i--) {
						_target.onAction(_player);
					}
					_player
							.sendPackets(new S_SkillSound(_player.getId(), 7020));
					_player.sendPackets(new S_SkillSound(_targetID, 6509));
					Broadcaster.broadcastPacket(_player, new S_SkillSound(
							_player.getId(), 7020));
					Broadcaster.broadcastPacket(_player, new S_SkillSound(
							_targetID, 6509));

					if (_player.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_SPOT1)) {
						dmg += 8;
					     _player.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_SPOT1);
					      _player.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 0)); //추가
					}
					if (_player.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_SPOT2)) {
						dmg += 17;
					     _player.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_SPOT2);
					      _player.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 0)); //추가 
					}
					if (_player.getSkillEffectTimerSet().hasSkillEffect(
							STATUS_SPOT3)) {
						  _player.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_SPOT3);
					      _player.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 0)); //추가 
						dmg += 25;
					}
				}
					break;
				case 10026:
				case 10027:
				case 10028:
				case 10029: {
					if (_user instanceof L1NpcInstance) {
						Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
								_npc, "$3717", 0));
					} else {
						Broadcaster.broadcastPacket(_player, new S_ChatPacket(
								_player, "$3717", 0, 0));
					}
				}
					break;
				case 10057: {
					L1Teleport.teleportToTargetFront(cha, _user, 1);
				}
					break;
					// 흑장로 데스 힐 / 캔슬레이션
				case BLACKELDER_DEATH_HELL:{
					Random random = new Random();
					int Chance = random.nextInt(100) + 1;
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SkillSound(pc.getId(), 7780));
				//		pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.PAP_DEATH_HELL, 12*1000);
						pc.getSkillEffectTimerSet().setSkillEffect(10518, 12*1000);
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7780));
					}
					if (Chance > 33){
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
								if (isNotCancelable(skillNum) && !pc.isDead()) {
									continue;
								}
								pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
							}
							for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
								pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
							}
							for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
								if (isNotCancelable(skillNum) && !pc.isDead()) {
									continue;
								}
								pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
							}
							pc.curePoison(); 
							pc.cureParalaysis();
							L1PolyMorph.undoPoly(pc);
							pc.sendPackets(new S_CharVisualUpdate(pc));
							Broadcaster.broadcastPacket(pc,new S_CharVisualUpdate(pc));
							if (pc.getHasteItemEquipped() > 0) {
								pc.getMoveState().setMoveSpeed(0);
								pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
								Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0, 0)); }
							if (pc != null && pc.isInvisble()) { 
								if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INVISIBILITY)) {
									pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.INVISIBILITY); 
									pc.sendPackets(new S_Invis(pc.getId(), 0)); 
									Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0)); 
									pc.sendPackets(new S_Sound(147)); }
								if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLIND_HIDING)) {
									pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.BLIND_HIDING);
									pc.sendPackets(new S_Invis(pc.getId(), 0));
									Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
								}
							}
							pc.getSkillEffectTimerSet().removeSkillEffect(STATUS_FREEZE);
							pc.sendPackets(new S_SkillSound(pc.getId(), 870));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 870));
						}
					}
				}
				break;
				// 드레이크 매스텔레포트
				case DRAKE_MASSTELEPORT:{
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.isDead()){
							continue;
						}
						L1Teleport.teleport(pc, 
								pc.getX() + (int) (Math.random() * 5) - (int) (Math.random() * 5), 
								pc.getY() + (int) (Math.random() * 5) - (int) (Math.random() * 5), 
								pc.getMapId(), pc.getMoveState().getHeading());
					}
				}
				break;
				// 드레이크 윈드세클
				case DRAKE_WIND_SHACKLE:{
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.isDead()){
							continue;
						}
						pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.WIND_SHACKLE, 12*1000);
						pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
						pc.sendPackets(new S_SkillSound(pc.getId(), 1799));
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 1799));
					}
				}
				break;
				// 흑장로 데스포션
				case BLACKELDER_DEATH_POTION:{
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SkillSound(pc.getId(), 7781));
						//pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.PAP_DEATH_PORTION, 12*1000);
						pc.getSkillEffectTimerSet().setSkillEffect(10513, 12*1000);
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7781));
					}
				}
				break;
				case CUTS_DEATH_POTION:{
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SkillSound(pc.getId(), 7781));
						pc.getSkillEffectTimerSet().setSkillEffect(10513, 12*1000);
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7781));
					}
				}
				break;
				case CUTS_DEATH_HELL:{
					Random random = new Random();
					int Chance = random.nextInt(100) + 1;
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SkillSound(pc.getId(), 7780));
						pc.getSkillEffectTimerSet().setSkillEffect(10518, 12*1000);
						Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 7780));
					}
					if (Chance > 33){
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
								if (isNotCancelable(skillNum) && !pc.isDead()) {
									continue;
								}
								pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
							}
							for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
								pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
							}
							for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
								if (isNotCancelable(skillNum) && !pc.isDead()) {
									continue;
								}
								pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
							}
							pc.curePoison(); 
							pc.cureParalaysis();
							L1PolyMorph.undoPoly(pc);
							pc.sendPackets(new S_CharVisualUpdate(pc));
							Broadcaster.broadcastPacket(pc,new S_CharVisualUpdate(pc));
							if (pc.getHasteItemEquipped() > 0) {
								pc.getMoveState().setMoveSpeed(0);
								pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
								Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0, 0)); }
							if (pc != null && pc.isInvisble()) { 
								if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INVISIBILITY)) {
									pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.INVISIBILITY); 
									pc.sendPackets(new S_Invis(pc.getId(), 0)); 
									Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0)); 
									pc.sendPackets(new S_Sound(147)); }
								if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLIND_HIDING)) {
									pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.BLIND_HIDING);
									pc.sendPackets(new S_Invis(pc.getId(), 0));
									Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
								}
							}
							pc.getSkillEffectTimerSet().removeSkillEffect(STATUS_FREEZE);
							pc.sendPackets(new S_SkillSound(pc.getId(), 870));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 870));
						}
					}
				}
				break;
				// 이프리트 서먼 몬스터
				case EFRETE_SUMMON_MONSTER:{
					Random _random = new Random(); 
					for (int i = 0; i < 2; i++){
						L1SpawnUtil.spawn4(_npc, 9191, _random.nextInt(3) + 8, 0, false);
					}					
				}
				break;			
				// 피닉스 서먼 몬스터
				case PHOENIX_SUMMON_MONSTER:{
					Random _random = new Random(); 
					for (int i = 0; i < 2; i++){
						L1SpawnUtil.spawn4(_npc, 9190, _random.nextInt(3) + 8, 0, false);
					}
				}
				break;
				// 피닉스 캔슬레이션
				case PHOENIX_CANCELLATION:{
					Random random = new Random();
					int Chance = random.nextInt(100) + 1;
					if (Chance > 33){
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
								if (isNotCancelable(skillNum) && !pc.isDead()) {
									continue;
								}
								pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
							}
							for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
								pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
							}
							for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
								if (isNotCancelable(skillNum) && !pc.isDead()) {
									continue;
								}
								pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
							}
							pc.curePoison(); 
							pc.cureParalaysis();
							L1PolyMorph.undoPoly(pc);
							pc.sendPackets(new S_CharVisualUpdate(pc));
							Broadcaster.broadcastPacket(pc,new S_CharVisualUpdate(pc));
							if (pc.getHasteItemEquipped() > 0) {
								pc.getMoveState().setMoveSpeed(0);
								pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
								Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0, 0)); }
							if (pc != null && pc.isInvisble()) { 
								if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INVISIBILITY)) {
									pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.INVISIBILITY); 
									pc.sendPackets(new S_Invis(pc.getId(), 0)); 
									Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0)); 
									pc.sendPackets(new S_Sound(147)); }
								if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLIND_HIDING)) {
									pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.BLIND_HIDING);
									pc.sendPackets(new S_Invis(pc.getId(), 0));
									Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
								}
							}
							pc.getSkillEffectTimerSet().removeSkillEffect(STATUS_FREEZE);
							pc.sendPackets(new S_SkillSound(pc.getId(), 870));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 870));
						}
					}
				}
				break;
					// 린드레이드
				case RINDVIOR_SUMMON_MONSTER_CLOUD:
				case RINDVIOR_PREDICATE:
				case RINDVIOR_SUMMON_MONSTER:
				case RINDVIOR_SILENCE:
				case RINDVIOR_BOW:
				case RINDVIOR_WIND_SHACKLE:
				case RINDVIOR_WIND_SHACKLE_1:
				case RINDVIOR_PREDICATE_CANCELLATION:
				case RINDVIOR_CANCELLATION:
				case RINDVIOR_WEAPON:
				case RINDVIOR_WEAPON_2:{
					int npcId = _npc.getNpcTemplate().get_npcId();
					if ((npcId >= 9170 && npcId <= 9172) || (npcId >= 9175 && npcId <= 9177)){
						// 구름대정령
						if (_skillId == RINDVIOR_SUMMON_MONSTER_CLOUD){
							L1SpawnUtil.spawn4(_npc, 9187, 10, 0, false);
							Broadcaster.broadcastPacket(_npc, new S_DoActionGFX(_npc.getId(), 1));
						}
						// 광물골렘
						if (_skillId == RINDVIOR_SUMMON_MONSTER){
							Random _random = new Random(); 
							int[] MobId = new int[] { 9178, 9179, 9180, 9181 }; // 광물 골렘
							int rnd = _random.nextInt(100);
							for (int i = 0; i < _random.nextInt(2) + 1; i++){
								L1SpawnUtil.spawn4(_npc, MobId[rnd % MobId.length], _random.nextInt(3) + 8, 300 * 1000, false);
							}
							Broadcaster.broadcastPacket(_npc, new S_DoActionGFX(_npc.getId(), 1));
						}
						// 화살
						if (_skillId == RINDVIOR_BOW){
							if (cha instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) cha;
								if (pc.isDead()){
									continue;
								}
								int SprNum = 0;
								int pcX = pc.getX();
								int pcY = pc.getY();
								switch(npcId){
								case 9175:
									pcY -= 6;
									SprNum = 7987;
									break;
								case 9176:
									pcX += 4;
									pcY -= 4;
									SprNum = 8050;
									break;
								case 9177:
									pcX += 5;
									SprNum = 8051;
									break;
								}
								S_EffectLocation packet = new S_EffectLocation(pcX, pcY, SprNum, 1000);
								Broadcaster.broadcastPacket(pc, packet);
								Broadcaster.broadcastPacket(_npc, new S_DoActionGFX(_npc.getId(), 47));
							}
						}
						// 사일런스
						if (_skillId == RINDVIOR_SILENCE){
							if (cha instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) cha;
								if (pc.isDead()){
									continue;
								}
								pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.SILENCE, 12 * 1000);
								pc.sendPackets(new S_SkillSound(pc.getId(), 2177));
								Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 2177));
							}
						}
						// 윈드 세클
						if(_skillId == RINDVIOR_WIND_SHACKLE || _skillId == RINDVIOR_WIND_SHACKLE_1){
							if (cha instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) cha;
								if (pc.isDead()){
									continue;
								}
								pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.WIND_SHACKLE, 12*1000);
								pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), _getBuffIconDuration));
								pc.sendPackets(new S_SkillSound(pc.getId(), 1799));
								Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 1799));
							}
						}
						// 리콜
						if (_skillId == RINDVIOR_PREDICATE){
							if (cha instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) cha;
								if (_npc.getLocation().getTileLineDistance(pc.getLocation()) > 4) {
									L1Location newLoc = null;
									for (int count = 0; count < 10; count++) {
										newLoc = _npc.getLocation().randomLocation(3, 4, false);
										if (CharPosUtil.glanceCheck(_npc, newLoc.getX(), newLoc.getY())) {
											L1Teleport.teleport(pc, newLoc.getX(), newLoc.getY(), _npc.getMapId(), 5, true);
											break;
										}
									}
								}
							}
						}
						// 리콜 캔슬레이션
						if (_skillId == RINDVIOR_PREDICATE_CANCELLATION){
							Random random = new Random();
							int Chance = random.nextInt(100) + 1;
							if (Chance > 33){
								if (cha instanceof L1PcInstance) {
									L1PcInstance pc = (L1PcInstance) cha;
									if (_npc.getLocation().getTileLineDistance(pc.getLocation()) > 4) {
										L1Location newLoc = null;
										for (int count = 0; count < 10; count++) {
											newLoc = _npc.getLocation().randomLocation(3, 4, false);
											if (CharPosUtil.glanceCheck(_npc, newLoc.getX(), newLoc.getY())) {
												L1Teleport.teleport(pc, newLoc.getX(), newLoc.getY(), _npc.getMapId(), 5, true);
												break;
											}
										}
									}
									for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
										if (isNotCancelable(skillNum)
												&& !pc.isDead()) {
											continue;
										}
										pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
									}
									for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
										pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
									}
									for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
										if (isNotCancelable(skillNum) && !pc.isDead()) {
											continue;
										}
										pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
									}
									pc.curePoison(); pc.cureParalaysis();
									L1PolyMorph.undoPoly(pc);
									pc.sendPackets(new S_CharVisualUpdate(pc));
									Broadcaster.broadcastPacket(pc,new S_CharVisualUpdate(pc));
									if (pc.getHasteItemEquipped() > 0) {
										pc.getMoveState().setMoveSpeed(0);
										pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
										Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0, 0)); }
									if (pc != null && pc.isInvisble()) { 
										if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INVISIBILITY)) {
											pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.INVISIBILITY); 
											pc.sendPackets(new S_Invis(pc.getId(), 0)); 
											Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0)); 
											pc.sendPackets(new S_Sound(147)); }
										if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLIND_HIDING)) {
											pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.BLIND_HIDING);
											pc.sendPackets(new S_Invis(pc.getId(), 0));
											Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
										}
									}
									pc.getSkillEffectTimerSet().removeSkillEffect(STATUS_FREEZE);
									pc.sendPackets(new S_SkillSound(pc.getId(), 870));
									Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 870));
								}
							}
							Broadcaster.broadcastPacket(_npc, new S_DoActionGFX(_npc.getId(), 19));
						}
						// 캔슬레이션
						if (_skillId == RINDVIOR_CANCELLATION){
							Random random = new Random();
							int Chance = random.nextInt(100) + 1;
							if (Chance > 33){
								if (cha instanceof L1PcInstance) {
									L1PcInstance pc = (L1PcInstance) cha;
									for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
										if (isNotCancelable(skillNum) && !pc.isDead()) {
											continue;
										}
										pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
									}
									for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
										pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
									}
									for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
										if (isNotCancelable(skillNum) && !pc.isDead()) {
											continue;
										}
										pc.getSkillEffectTimerSet().removeSkillEffect(skillNum);
									}
									pc.curePoison(); pc.cureParalaysis();
									L1PolyMorph.undoPoly(pc);
									pc.sendPackets(new S_CharVisualUpdate(pc));
									Broadcaster.broadcastPacket(pc,new S_CharVisualUpdate(pc));
									if (pc.getHasteItemEquipped() > 0) {
										pc.getMoveState().setMoveSpeed(0);
										pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
										Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 0, 0)); }
									if (pc != null && pc.isInvisble()) { 
										if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.INVISIBILITY)) {
											pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.INVISIBILITY); 
											pc.sendPackets(new S_Invis(pc.getId(), 0)); 
											Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0)); 
											pc.sendPackets(new S_Sound(147)); }
										if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.BLIND_HIDING)) {
											pc.getSkillEffectTimerSet().killSkillEffectTimer(L1SkillId.BLIND_HIDING);
											pc.sendPackets(new S_Invis(pc.getId(), 0));
											Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(), 0));
										}
									}
									pc.getSkillEffectTimerSet().removeSkillEffect(STATUS_FREEZE);
									pc.sendPackets(new S_SkillSound(pc.getId(), 870));
									Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 870));
								}
							}
							Broadcaster.broadcastPacket(_npc, new S_DoActionGFX(_npc.getId(), 19));
						}
						// 웨폰 브레이커
						if (_skillId == RINDVIOR_WEAPON || _skillId == RINDVIOR_WEAPON_2) { 
							if (cha instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) cha;
								L1ItemInstance weapon = pc.getWeapon();
								Random random = new Random();
								int rnd = random.nextInt(100) + 1;
								if (weapon != null && rnd > 33) {
									int weaponDamage = random.nextInt(3) + 1;
									if (pc.isDead()){
										continue;
									}
									pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
									pc.getInventory().receiveDamage(weapon, weaponDamage);
									pc.sendPackets(new S_SkillSound(pc.getId(), 172));
									Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 172));
									Broadcaster.broadcastPacket(_npc, new S_DoActionGFX(_npc.getId(), 19));
								}
							}
						}
					}
				}
				break;

				case ANTA_SKILL_1: { // 안타라스(리뉴얼) - 용언 스킬1
					if (_user instanceof L1NpcInstance) {
						Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
								_npc, "$7914", 0));
						// 오브 모크! 세이 리라프[무기손상-웨폰브레이크 + 굳기]
					}

					if (_skillId == ANTA_SKILL_2) { // 안타라스(리뉴얼) - 용언 스킬2
						Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
								_npc, "$7948", 0));
						// 오브 모크! 티 세토르[캔슬 + 독굳히기]
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							int time = 300 / pc.getLevel() + 1;
							L1DamagePoison.doInfection(pc, cha, 10000, 5);
							L1ParalysisPoison.doInfection(pc, 10000,
									time * 1000); // 10초 후에 time초간 마비
						}
					}
				}
					break;
				case ANTA_SKILL_8: { // 안타라스(리뉴얼) - 용언 스킬8
					if (_user instanceof L1NpcInstance) {
						Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
								_npc, "$7905", 0));
						// 오브 모크! 티기르[오른손펀치 + 이럽브레스]
					}
					dmg = _magic.calcMagicDamage(_skillId);
				}
					break;
				case ANTA_SKILL_9: { // 안타라스(리뉴얼) - 용언 스킬9
					if (_user instanceof L1NpcInstance) {
						Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
								_npc, "$7907", 0));
						// 오브 모크! 켄 티기르[오른쪽펀치 + 왼쪽펀치 + 브레스]
					}
					dmg = _magic.calcMagicDamage(_skillId);
					dmg = _magic.calcMagicDamage(_skillId);
					// ////////// 나머지 용언은 각각 스킬안에 추가 하였음..
				}
					break;
				case SLOW: //슬로우
				      if (_user instanceof L1PcInstance) {  //보라 되게
							if (cha instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) cha;
				    	  L1PinkName.onAction(pc, _user);  
					      } 
				      }
				case MASS_SLOW://매스슬로우
				      if (_user instanceof L1PcInstance) {  //보라 되게
							if (cha instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) cha;
				    	  L1PinkName.onAction(pc, _user);  
					      } 
				      }
				case ENTANGLE://인탱글
				case MOB_SLOW_1://몹 슬로우
				case MOB_SLOW_18: {
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getHasteItemEquipped() > 0) {
							continue;
						}
					}
					if (cha.getMoveState().getMoveSpeed() == 0) {
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.sendPackets(new S_SkillHaste(pc.getId(), 2,
									_getBuffIconDuration));
						}
						Broadcaster.broadcastPacket(cha, new S_SkillHaste(cha
								.getId(), 2, _getBuffIconDuration));
						cha.getMoveState().setMoveSpeed(2);
					} else if (cha.getMoveState().getMoveSpeed() == 1) {
						int skillNum = 0;
						if (cha.getSkillEffectTimerSet().hasSkillEffect(HASTE)) {
							skillNum = HASTE;
						} else if (cha.getSkillEffectTimerSet().hasSkillEffect(
								GREATER_HASTE)) {
							skillNum = GREATER_HASTE;
						} else if (cha.getSkillEffectTimerSet().hasSkillEffect(
								STATUS_HASTE)) {
							skillNum = STATUS_HASTE;
						}
						if (skillNum != 0) {
							cha.getSkillEffectTimerSet().removeSkillEffect(
									skillNum);
							cha.getSkillEffectTimerSet().removeSkillEffect(
									_skillId);
							cha.getMoveState().setMoveSpeed(0);
							continue;
						}
					}
				}
				break;
				case CURSE_BLIND: //커스블라인드
				case DARKNESS:{ //다크니스
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
 			    	  L1PinkName.onAction(pc, _user);  //보라 되게
						if (pc.getSkillEffectTimerSet().hasSkillEffect(STATUS_FLOATING_EYE)) {
							pc.sendPackets(new S_CurseBlind(2));
						} else {
							pc.sendPackets(new S_CurseBlind(1));
						}
					}
				}
				break;
				case CURSE_POISON:{ //커스포이즌
					L1DamagePoison.doInfection(_user, cha, 3000, 5);
				}
				break;
				case CURSE_PARALYZE: //커스 패럴라이즈
				case CURSE_PARALYZE2:
				case MOB_CURSEPARALYZ_18:
				case MOB_CURSEPARALYZ_19: {
					if (!cha.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
							&& !cha.getSkillEffectTimerSet().hasSkillEffect(ICE_LANCE)
							&& !cha.getSkillEffectTimerSet().hasSkillEffect(FREEZING_BLIZZARD)) {
						if (cha instanceof L1PcInstance) {
							L1CurseParalysis.curse(cha, 4000, 16000);//커스본섭화2012년7월12일
						} else if (cha instanceof L1MonsterInstance) {
							if (cha.getMaxHp() < 4300) {
								L1CurseParalysis.curse(cha, 0, 16000);
							}
						}
					}
				}
				break;
				case WEAKNESS: //위크니스
		      if (_user instanceof L1PcInstance) {  //보라 되게
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
		    	  L1PinkName.onAction(pc, _user);  
			      } 
		      }

				case MOB_WEAKNESS_1:{
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(-5);
						pc.addHitup(-1);
					}
				}
					break;
					
					//아이돌물약
				case benz_TIMER://아이돌
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(5);
						pc.addHitup(5);
						pc.addBowHitup(5);
					    pc.addBowDmgup(5);
						pc.getAbility().addSp(3);
						pc.addMaxHp(200);
						pc.addMaxMp(100);
						pc.sendPackets(new S_SPMR(pc));
					}
					break;
				case benz_TIMER1://아이돌
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(8);
						pc.addHitup(8);
						pc.addBowHitup(8);
					    pc.addBowDmgup(8);
						pc.getAbility().addSp(6);
						pc.addMaxHp(400);
						pc.addMaxMp(200);
						pc.sendPackets(new S_SPMR(pc));
					}
					break;
					
					
				case DISEASE://디지즈
		      if (_user instanceof L1PcInstance) {  //보라 되게
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
		    	  L1PinkName.onAction(pc, _user);  
			      } 
		      }
				case MOB_DISEASE_1:
				case MOB_DISEASE_30: {
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(-6);
						pc.getAC().addAc(12);
					}
				}
				break;
				case GUARD_BREAK:{ //가드 브레이크
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAC().addAc(15);
					}
				}
				break;
				case HORROR_OF_DEATH:{ //호러 오브 데스
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAbility().addAddedStr((byte) -5);
						pc.getAbility().addAddedInt((byte) -5);
					}
				}
				break;
				case PANIC:{ //패닉
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAbility().addAddedStr((byte) -1);
						pc.getAbility().addAddedDex((byte) -1);
						pc.getAbility().addAddedCon((byte) -1);
						pc.getAbility().addAddedInt((byte) -1);
						pc.getAbility().addAddedWis((byte) -1);
						pc.resetBaseMr();
					}
				}
					break;
				case ICE_LANCE://아이스랜스
				case FREEZING_BLIZZARD: {//프리징블리자드
					_isFreeze = _magic.calcProbabilityMagic(_skillId);
					if (_isFreeze) {
						int time = _skill.getBuffDuration() * 1000;
						L1EffectSpawn.getInstance().spawnEffect(81168, time,
								cha.getX(), cha.getY(), cha.getMapId());
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.sendPackets(new S_Poison(pc.getId(), 2));
							Broadcaster.broadcastPacket(pc, new S_Poison(pc
									.getId(), 2));
							pc.sendPackets(new S_Paralysis(
									S_Paralysis.TYPE_FREEZE, true));
						} else if (cha instanceof L1MonsterInstance
								|| cha instanceof L1SummonInstance
								|| cha instanceof L1PetInstance) {
							L1NpcInstance npc = (L1NpcInstance) cha;
							if (npc.getMaxHp() < 4300) {
								Broadcaster.broadcastPacket(npc, new S_Poison(
										npc.getId(), 2));
								npc.setParalyzed(true);
								npc.setParalysisTime(time);
							}
						}
					}
				}
					break;
				case EARTH_BIND://어스바인드
		      if (_user instanceof L1PcInstance) {  //보라 되게
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
		    	  L1PinkName.onAction(pc, _user);  
			      } 
		      }
				case MOB_BASILL:
				case MOB_COCA: {
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Poison(pc.getId(), 2));
						Broadcaster.broadcastPacket(pc, new S_Poison(
								pc.getId(), 2));
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE,
								true));
					} else if (cha instanceof L1MonsterInstance
							|| cha instanceof L1SummonInstance
							|| cha instanceof L1PetInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						if (npc.getMaxHp() < 4300) {
							Broadcaster.broadcastPacket(npc, new S_Poison(npc
									.getId(), 2));
							npc.setParalyzed(true);
							npc
									.setParalysisTime(_skill.getBuffDuration() * 1000);
						}
					}
				}
					break;
				case SHOCK_STUN: {// 스턴확률
					int targetLevel = 0;
					int diffLevel = 0;

					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						targetLevel = pc.getLevel();
					} else if (cha instanceof L1MonsterInstance
							|| cha instanceof L1SummonInstance
							|| cha instanceof L1PetInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						targetLevel = npc.getLevel();
					}

					diffLevel = _user.getLevel() - targetLevel;

					if (diffLevel < -2) {// 1~2렙이상차이
						int[] stunTimeArray = { 1100, 1400, 1700, 2000, 2300,
								2600, 2900, 3200, 3500 };
						_shockStunDuration = stunTimeArray[random
								.nextInt(stunTimeArray.length)];
					} else if (diffLevel >= -2 && diffLevel <= 3) {// 아래2랩위로4렙차이동랩기준
						int[] stunTimeArray = { 1000, 1300, 1600, 1900, 2200,
								2500, 2800, 3100, 3400 };
						_shockStunDuration = stunTimeArray[random
								.nextInt(stunTimeArray.length)];
					} else if (diffLevel >= 4 && diffLevel <= 10) {// 4렙~10렙이하차이
						int[] stunTimeArray = { 1800, 2100, 2400, 2700, 3000,
								3300, 3600, 3900, 4200 };
						_shockStunDuration = stunTimeArray[random
								.nextInt(stunTimeArray.length)];
					} else if (diffLevel > 10) {// 10렙이하케릭차이
						int[] stunTimeArray = { 2300, 2600, 2900, 3200, 3500,
								3800, 4100, 4400, 4700 };
						_shockStunDuration = stunTimeArray[random
								.nextInt(stunTimeArray.length)];
					} else if (diffLevel > -4) {// 4 렙이상케릭차이 스턴넣을시..
						int[] stunTimeArray = { 1100, 1400, 1700, 2000, 2300,
								2600 };// 최고3초
						_shockStunDuration = stunTimeArray[random
								.nextInt(stunTimeArray.length)];
					}
					L1EffectSpawn.getInstance().spawnEffect(81162,
							_shockStunDuration, cha.getX(), cha.getY(),
							cha.getMapId());
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN,
								true));
					} else if (cha instanceof L1MonsterInstance
							|| cha instanceof L1SummonInstance
							|| cha instanceof L1PetInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.setParalyzed(true);
						npc.setParalysisTime(_shockStunDuration);
					}
				}
					break;
				case MOB_RANGESTUN_18:
				case MOB_RANGESTUN_19:
				case ANTA_SKILL_3:
				case ANTA_SKILL_4:
				case ANTA_SKILL_5: { // 안타라스 용언
					int npcId = _npc.getNpcTemplate().get_npcId();
					if (npcId == 4039020 || npcId == 4039021
							|| npcId == 4039022) { // 안타라스(리뉴얼)1,2,3단계
						if (_skillId == ANTA_SKILL_3) { // 안타라스(리뉴얼) - 용언 스킬3
							Broadcaster.broadcastPacket(_user,
									new S_NpcChatPacket(_npc, "$7903", 0));
							// 오브 모크! 뮤즈 삼[범위스턴 + 점프공격]
							dmg = _magic.calcMagicDamage(_skillId);
						}
						if (_skillId == ANTA_SKILL_4) { // 안타라스(리뉴얼) - 용언 스킬4
							Broadcaster.broadcastPacket(_user,
									new S_NpcChatPacket(_npc, "$7909", 0));
							// 오브 모크! 너츠 삼[범위스턴 + 전체운석]
							dmg = _magic.calcMagicDamage(_skillId);
						}
						if (_skillId == ANTA_SKILL_5) { // 안타라스(리뉴얼) - 용언 스킬5
							Broadcaster.broadcastPacket(_user,
									new S_NpcChatPacket(_npc, "$7915", 0));
							// 오브 모크! 티프 삼[범위스턴 + 점프공격 + 전체운석]
							dmg = _magic.calcMagicDamage(_skillId);
							dmg = _magic.calcMagicDamage(_skillId);
						}
					}
				}
					break;
				/**
				 * 파푸리온 리뉴얼
				 */

				case PAPOO_SKILL: // 리오타! 누스건 카푸

					if (_player != null && _player.isInvisble()) {
						_player.delInvis();
					}

					if (!(cha instanceof L1PcInstance)) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.getMoveState().setMoveSpeed(0);
						npc.getMoveState().setBraveSpeed(0);
						Broadcaster.broadcastPacket(npc, new S_SkillHaste(cha
								.getId(), 0, 0));
						Broadcaster.broadcastPacket(npc, new S_SkillBrave(cha
								.getId(), 0, 0));
						npc.setWeaponBreaked(false);
						npc.setParalyzed(false);
						npc.setParalysisTime(0);
					}

					for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
						if (isNotCancelable(skillNum) && !cha.isDead()) {
							continue;
						}
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					cha.curePoison();
					cha.cureParalaysis();

					if (cha instanceof L1PcInstance) {
					}

					for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
						if (isNotCancelable(skillNum) && !cha.isDead()) {
							continue;
						}
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;

						L1PolyMorph.undoPoly(pc);
						pc.sendPackets(new S_CharVisualUpdate(pc));
						Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(
								pc));

						if (pc.getHasteItemEquipped() > 0) {
							pc.getMoveState().setMoveSpeed(0);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
							Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc
									.getId(), 0, 0));
						}
						if (pc != null && pc.isInvisble()) {
							if (pc.getSkillEffectTimerSet().hasSkillEffect(
									L1SkillId.INVISIBILITY)) {
								pc.getSkillEffectTimerSet()
										.killSkillEffectTimer(
												L1SkillId.INVISIBILITY);
								pc.sendPackets(new S_Invis(pc.getId(), 0));
								Broadcaster.broadcastPacket(pc, new S_Invis(pc
										.getId(), 0));
								pc.sendPackets(new S_Sound(147));
							}
							if (pc.getSkillEffectTimerSet().hasSkillEffect(
									L1SkillId.BLIND_HIDING)) {
								pc.getSkillEffectTimerSet()
										.killSkillEffectTimer(
												L1SkillId.BLIND_HIDING);
								pc.sendPackets(new S_Invis(pc.getId(), 0));
								Broadcaster.broadcastPacket(pc, new S_Invis(pc
										.getId(), 0));
							}
						}
					}
					cha.getSkillEffectTimerSet().removeSkillEffect(
							STATUS_FREEZE);
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
							_npc, "리오타! 누스건 카푸", 0));
					L1SpawnUtil.spawn3(_npc, 45943, 6, 0, false);

					break;
				case PAPOO_SKILL2:

					if (_player != null && _player.isInvisble()) {
						_player.delInvis();
					}

					if (!(cha instanceof L1PcInstance)) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.getMoveState().setMoveSpeed(0);
						npc.getMoveState().setBraveSpeed(0);
						Broadcaster.broadcastPacket(npc, new S_SkillHaste(cha
								.getId(), 0, 0));
						Broadcaster.broadcastPacket(npc, new S_SkillBrave(cha
								.getId(), 0, 0));
						npc.setWeaponBreaked(false);
						npc.setParalyzed(false);
						npc.setParalysisTime(0);
					}

					for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
						if (isNotCancelable(skillNum) && !cha.isDead()) {
							continue;
						}
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					cha.curePoison();
					cha.cureParalaysis();

					if (cha instanceof L1PcInstance) {
					}

					for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
						if (isNotCancelable(skillNum) && !cha.isDead()) {
							continue;
						}
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;

						L1PolyMorph.undoPoly(pc);
						pc.sendPackets(new S_CharVisualUpdate(pc));
						Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(
								pc));

						if (pc.getHasteItemEquipped() > 0) {
							pc.getMoveState().setMoveSpeed(0);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
							Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc
									.getId(), 0, 0));
						}
						if (pc != null && pc.isInvisble()) {
							if (pc.getSkillEffectTimerSet().hasSkillEffect(
									L1SkillId.INVISIBILITY)) {
								pc.getSkillEffectTimerSet()
										.killSkillEffectTimer(
												L1SkillId.INVISIBILITY);
								pc.sendPackets(new S_Invis(pc.getId(), 0));
								Broadcaster.broadcastPacket(pc, new S_Invis(pc
										.getId(), 0));
								pc.sendPackets(new S_Sound(147));
							}
							if (pc.getSkillEffectTimerSet().hasSkillEffect(
									L1SkillId.BLIND_HIDING)) {
								pc.getSkillEffectTimerSet()
										.killSkillEffectTimer(
												L1SkillId.BLIND_HIDING);
								pc.sendPackets(new S_Invis(pc.getId(), 0));
								Broadcaster.broadcastPacket(pc, new S_Invis(pc
										.getId(), 0));
							}
						}
					}
					cha.getSkillEffectTimerSet().removeSkillEffect(
							STATUS_FREEZE);
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
							_npc, "리오타! 나나 폰폰..", 0));
					S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), 5);
					Broadcaster.broadcastPacket(_user, gfx);
					dmg = _magic.calcMagicDamage(_skillId);
					break;
				case PAPOO_SKILL3:
					if (_calcType == PC_PC || _calcType == NPC_PC) {
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							L1ItemInstance weapon = pc.getWeapon();
							if (weapon != null) {
								int weaponDamage = random.nextInt(_user
										.getAbility().getTotalInt() / 3) + 1;
								pc.sendPackets(new S_ServerMessage(268, weapon
										.getLogName()));
								pc.getInventory().receiveDamage(weapon,
										weaponDamage);
							}
						}
					} else {
						((L1NpcInstance) cha).setWeaponBreaked(true);
					}
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
							_npc, "리오타! 레포 폰폰..", 0));
					S_DoActionGFX leo = new S_DoActionGFX(_user.getId(), 12);
					Broadcaster.broadcastPacket(_user, leo);
					dmg = _magic.calcMagicDamage(_skillId);
					break;
				case PAPOO_SKILL4:
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
							_npc, "리오타! 테나 론디르", 0));
					S_DoActionGFX tena = new S_DoActionGFX(_user.getId(), 25);
					S_DoActionGFX tena2 = new S_DoActionGFX(_user.getId(), 18);
					Broadcaster.broadcastPacket(_user, tena);
					Broadcaster.broadcastPacket(_user, tena2);
					dmg = _magic.calcMagicDamage(_skillId);
					dmg = _magic.calcMagicDamage(_skillId);
					break;
				case PAPOO_SKILL5:
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
							_npc, "리오타! 네나 론디르", 0));
					S_DoActionGFX lena = new S_DoActionGFX(_user.getId(), 1);
					S_DoActionGFX lena2 = new S_DoActionGFX(_user.getId(), 18);
					Broadcaster.broadcastPacket(_user, lena);
					Broadcaster.broadcastPacket(_user, lena2);
					dmg = _magic.calcMagicDamage(_skillId);
					dmg = _magic.calcMagicDamage(_skillId);
					break;
				case PAPOO_SKILL6:
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
							_npc, "리오타! 라나 오이므", 0));
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						Collection<L1PcInstance> players = null;
						players = new FastTable<L1PcInstance>();
						players.add(pc);
						for (L1PcInstance tg : players) {
							new L1SkillUse().handleCommands(pc, 10513, tg
									.getId(), tg.getX(), tg.getY(), null, 12,
									L1SkillUse.TYPE_SPELLSC);
						}
					}

					S_DoActionGFX lana = new S_DoActionGFX(_user.getId(), 5);
					Broadcaster.broadcastPacket(_user, lana);
					dmg = _magic.calcMagicDamage(_skillId);
					break;
				case PAPOO_SKILL7:
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
							_npc, "리오타! 레포 오이므", 0));
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						Collection<L1PcInstance> players = null;
						players = new FastTable<L1PcInstance>();
						players.add(pc);
						for (L1PcInstance tg : players) {
							new L1SkillUse().handleCommands(pc, 10513, tg
									.getId(), tg.getX(), tg.getY(), null, 12,
									L1SkillUse.TYPE_SPELLSC);
						}
					}
					S_DoActionGFX repo = new S_DoActionGFX(_user.getId(), 19);
					Broadcaster.broadcastPacket(_user, repo);
					dmg = _magic.calcMagicDamage(_skillId);
					break;
				case PAPOO_SKILL8:
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
							_npc, "리오타! 테나 웨인라크", 0));
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						Collection<L1PcInstance> players = null;
						players = new FastTable<L1PcInstance>();
						players.add(pc);
						for (L1PcInstance tg : players) {
							new L1SkillUse().handleCommands(pc, 10518, tg
									.getId(), tg.getX(), tg.getY(), null, 12,
									L1SkillUse.TYPE_SPELLSC);
						}
					}
					S_DoActionGFX tenas = new S_DoActionGFX(_user.getId(), 18);
					Broadcaster.broadcastPacket(_user, tenas);
					dmg = _magic.calcMagicDamage(_skillId);
					break;
				case PAPOO_SKILL9:
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
							_npc, "리오타! 네나 우누스", 0));
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						Collection<L1PcInstance> players = null;
						players = new FastTable<L1PcInstance>();
						players.add(pc);
						for (L1PcInstance tg : players) {
							new L1SkillUse().handleCommands(pc, 10517, tg
									.getId(), tg.getX(), tg.getY(), null, 12,
									L1SkillUse.TYPE_SPELLSC);
						}
					}
					S_DoActionGFX nean = new S_DoActionGFX(_user.getId(), 18);
					Broadcaster.broadcastPacket(_user, nean);
					dmg = _magic.calcMagicDamage(_skillId);
					break;
				case PAPOO_SKILL10:
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
							_npc, "리오타! 오니즈 웨인라크", 0));
					if (_player != null && _player.isInvisble()) {
						_player.delInvis();
					}

					if (!(cha instanceof L1PcInstance)) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.getMoveState().setMoveSpeed(0);
						npc.getMoveState().setBraveSpeed(0);
						Broadcaster.broadcastPacket(npc, new S_SkillHaste(cha
								.getId(), 0, 0));
						Broadcaster.broadcastPacket(npc, new S_SkillBrave(cha
								.getId(), 0, 0));
						npc.setWeaponBreaked(false);
						npc.setParalyzed(false);
						npc.setParalysisTime(0);
					}

					for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
						if (isNotCancelable(skillNum) && !cha.isDead()) {
							continue;
						}
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					cha.curePoison();
					cha.cureParalaysis();

					if (cha instanceof L1PcInstance) {
					}

					for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
						if (isNotCancelable(skillNum) && !cha.isDead()) {
							continue;
						}
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;

						L1PolyMorph.undoPoly(pc);
						pc.sendPackets(new S_CharVisualUpdate(pc));
						Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(
								pc));

						if (pc.getHasteItemEquipped() > 0) {
							pc.getMoveState().setMoveSpeed(0);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
							Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc
									.getId(), 0, 0));
						}
						if (pc != null && pc.isInvisble()) {
							if (pc.getSkillEffectTimerSet().hasSkillEffect(
									L1SkillId.INVISIBILITY)) {
								pc.getSkillEffectTimerSet()
										.killSkillEffectTimer(
												L1SkillId.INVISIBILITY);
								pc.sendPackets(new S_Invis(pc.getId(), 0));
								Broadcaster.broadcastPacket(pc, new S_Invis(pc
										.getId(), 0));
								pc.sendPackets(new S_Sound(147));
							}
							if (pc.getSkillEffectTimerSet().hasSkillEffect(
									L1SkillId.BLIND_HIDING)) {
								pc.getSkillEffectTimerSet()
										.killSkillEffectTimer(
												L1SkillId.BLIND_HIDING);
								pc.sendPackets(new S_Invis(pc.getId(), 0));
								Broadcaster.broadcastPacket(pc, new S_Invis(pc
										.getId(), 0));
							}
						}
					}

					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						Collection<L1PcInstance> players = null;
						players = new FastTable<L1PcInstance>();
						players.add(pc);
						for (L1PcInstance tg : players) {
							new L1SkillUse().handleCommands(pc, 10518, tg
									.getId(), tg.getX(), tg.getY(), null, 12,
									L1SkillUse.TYPE_SPELLSC);
						}
					}
					dmg = _magic.calcMagicDamage(_skillId);
					break;
				case PAPOO_SKILL11:
					Broadcaster.broadcastPacket(_user, new S_NpcChatPacket(
							_npc, "리오타! 오니즈 쿠스온 웨인라크", 0));
					if (_player != null && _player.isInvisble()) {
						_player.delInvis();
					}

					if (!(cha instanceof L1PcInstance)) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.getMoveState().setMoveSpeed(0);
						npc.getMoveState().setBraveSpeed(0);
						Broadcaster.broadcastPacket(npc, new S_SkillHaste(cha
								.getId(), 0, 0));
						Broadcaster.broadcastPacket(npc, new S_SkillBrave(cha
								.getId(), 0, 0));
						npc.setWeaponBreaked(false);
						npc.setParalyzed(false);
						npc.setParalysisTime(0);
					}

					for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
						if (isNotCancelable(skillNum) && !cha.isDead()) {
							continue;
						}
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					cha.curePoison();
					cha.cureParalaysis();

					if (cha instanceof L1PcInstance) {
					}

					for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
						if (isNotCancelable(skillNum) && !cha.isDead()) {
							continue;
						}
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;

						L1PolyMorph.undoPoly(pc);
						pc.sendPackets(new S_CharVisualUpdate(pc));
						Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(
								pc));

						if (pc.getHasteItemEquipped() > 0) {
							pc.getMoveState().setMoveSpeed(0);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
							Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc
									.getId(), 0, 0));
						}
						if (pc != null && pc.isInvisble()) {
							if (pc.getSkillEffectTimerSet().hasSkillEffect(
									L1SkillId.INVISIBILITY)) {
								pc.getSkillEffectTimerSet()
										.killSkillEffectTimer(
												L1SkillId.INVISIBILITY);
								pc.sendPackets(new S_Invis(pc.getId(), 0));
								Broadcaster.broadcastPacket(pc, new S_Invis(pc
										.getId(), 0));
								pc.sendPackets(new S_Sound(147));
							}
							if (pc.getSkillEffectTimerSet().hasSkillEffect(
									L1SkillId.BLIND_HIDING)) {
								pc.getSkillEffectTimerSet()
										.killSkillEffectTimer(
												L1SkillId.BLIND_HIDING);
								pc.sendPackets(new S_Invis(pc.getId(), 0));
								Broadcaster.broadcastPacket(pc, new S_Invis(pc
										.getId(), 0));
							}
						}
					}

					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						Collection<L1PcInstance> players = null;
						players = new FastTable<L1PcInstance>();
						players.add(pc);
						for (L1PcInstance tg : players) {
							new L1SkillUse().handleCommands(pc, 10518, tg
									.getId(), tg.getX(), tg.getY(), null, 12,
									L1SkillUse.TYPE_SPELLSC);
						}
					}
					S_DoActionGFX onian = new S_DoActionGFX(_user.getId(), 30);
					Broadcaster.broadcastPacket(_user, onian);
					dmg = _magic.calcMagicDamage(_skillId);
					break;
				case MOB_SHOCKSTUN_30: {
					int[] stunTimeArray = { 500, 700, 1000, 1200, 1500, 1700,
							2000, 2500, 3000, 3500, 4000 };

					int rnd = random.nextInt(stunTimeArray.length);
					_shockStunDuration = stunTimeArray[rnd];

					L1EffectSpawn.getInstance().spawnEffect(81162,
							_shockStunDuration, cha.getX(), cha.getY(),
							cha.getMapId());
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN,
								true));
					} else if (cha instanceof L1MonsterInstance
							|| cha instanceof L1SummonInstance
							|| cha instanceof L1PetInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.setParalyzed(true);
						npc.setParalysisTime(_shockStunDuration);
					}
				}
					break;
				case THUNDER_GRAB: {
					_isFreeze = _magic.calcProbabilityMagic(_skillId);
					if (_isFreeze) {
						int time = _skill.getBuffDuration() * 1000;
						L1EffectSpawn.getInstance().spawnEffect(81182, time,
								cha.getX(), cha.getY(), cha.getMapId());
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.getSkillEffectTimerSet().setSkillEffect(
									L1SkillId.STATUS_FREEZE, time);
							pc.sendPackets(new S_SkillSound(pc.getId(), 4184));
							Broadcaster.broadcastPacket(pc, new S_SkillSound(pc
									.getId(), 4184));
							pc.sendPackets(new S_Paralysis(
									S_Paralysis.TYPE_BIND, true));
						} else if (cha instanceof L1MonsterInstance
								|| cha instanceof L1SummonInstance
								|| cha instanceof L1PetInstance) {
							L1NpcInstance npc = (L1NpcInstance) cha;
							npc.getSkillEffectTimerSet().setSkillEffect(
									L1SkillId.STATUS_FREEZE, time);
							Broadcaster.broadcastPacket(npc, new S_SkillSound(
									npc.getId(), 4184));
							npc.setParalyzed(true);
						}
					}
				}
					break;
				case BONE_BREAK: {
					int bonetime = 1000; // 1초
					L1EffectSpawn.getInstance().spawnEffect(4500500, bonetime,
							cha.getX(), cha.getY(), cha.getMapId());
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN,
								true));
					} else if (cha instanceof L1MonsterInstance
							|| cha instanceof L1SummonInstance
							|| cha instanceof L1PetInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.setParalyzed(true);
						npc.setParalysisTime(bonetime);
					}
				}
					break;
			/*	case AM_BREAK: {
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(-2);
					}
				}
					break;*/
				case PHANTASM: {
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP,
								true));
					}
					cha.setSleeped(true);
				}
					break;
				case WIND_SHACKLE:
				case MOB_WINDSHACKLE_1:
				case P_WIND_SHACKLE:
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SkillIconWindShackle(pc.getId(),
								_getBuffIconDuration));
					}
					break;
				case CANCELLATION: //캔슬레이션
				      if (_user instanceof L1PcInstance) {  //보라 되게
							if (cha instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) cha;
				    	  L1PinkName.onAction(pc, _user);  
					      } 
				      }
				case Mob_AREA_CANCELLATION_19:
				case ANTA_SKILL_2: {
					if (cha instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						int npcId = npc.getNpcTemplate().get_npcId();
						if (npcId == 71092) {// 조사원
							if (npc.getGfxId().getGfxId() == npc.getGfxId()
									.getTempCharGfx()) {
								npc.getGfxId().setTempCharGfx(1314);
								Broadcaster.broadcastPacket(npc,
										new S_ChangeShape(npc.getId(), 1314));
								return;
							} else {
								return;
							}
						} else if (npcId == 45640) {// 유니콘
							if (npc.getGfxId().getGfxId() == npc.getGfxId()
									.getTempCharGfx()) {
								npc.setCurrentHp(npc.getMaxHp());
								npc.setName("$2103");
								npc.setNameId("$2103");
								Broadcaster.broadcastPacket(npc,
										new S_ChangeName(npc.getId(), "$2103"));
								npc.getGfxId().setTempCharGfx(2332);
								Broadcaster.broadcastPacket(npc,
										new S_ChangeShape(npc.getId(), 2332));
								return;
							} else if (npc.getGfxId().getTempCharGfx() == 2332) {
								npc.setCurrentHp(npc.getMaxHp());
								npc.setName("$2488");
								npc.setNameId("$2488");
								Broadcaster.broadcastPacket(npc,
										new S_ChangeName(npc.getId(), "$2488"));
								npc.getGfxId().setTempCharGfx(2755);
								Broadcaster.broadcastPacket(npc,
										new S_ChangeShape(npc.getId(), 2755));
								return;
							}
						} else if (npcId == 81209) {
							if (npc.getGfxId().getGfxId() == npc.getGfxId()
									.getTempCharGfx()) {
								npc.getGfxId().setTempCharGfx(4310);
								Broadcaster.broadcastPacket(npc,
										new S_ChangeShape(npc.getId(), 4310));
								return;
							} else {
								return;
							}
						}
					}

					if (_player != null && _player.isInvisble()) {
						_player.delInvis();
					}

					if (!(cha instanceof L1PcInstance)) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.getMoveState().setMoveSpeed(0);
						npc.getMoveState().setBraveSpeed(0);
						Broadcaster.broadcastPacket(npc, new S_SkillHaste(cha
								.getId(), 0, 0));
						Broadcaster.broadcastPacket(npc, new S_SkillBrave(cha
								.getId(), 0, 0));
						npc.setWeaponBreaked(false);
						npc.setParalyzed(false);
						npc.setParalysisTime(0);
					}

					for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
						if (isNotCancelable(skillNum) && !cha.isDead()) {
							continue;
						}
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_CANCLEEND; skillNum++) {
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}

					cha.curePoison();
					cha.cureParalaysis();

					if (cha instanceof L1PcInstance) {
					}
					// 요리의 해제
					for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
						if (isNotCancelable(skillNum) && !cha.isDead()) {
							continue;
						}
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}
					// 케릭터 버프의 해제
					for (int skillNum = CHAR_BUFF_START; skillNum <= CHAR_BUFF_END; skillNum++) {
						if (isNotCancelable(skillNum)) {
							continue;
						}
						cha.getSkillEffectTimerSet()
								.removeSkillEffect(skillNum);
					}
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;

						L1PolyMorph.undoPoly(pc);
						pc.sendPackets(new S_CharVisualUpdate(pc));
						Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(
								pc));

						if (pc.getHasteItemEquipped() > 0) {
							pc.getMoveState().setMoveSpeed(0);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
							Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc
									.getId(), 0, 0));
						}
						if (pc != null && pc.isInvisble()) {
							if (pc.getSkillEffectTimerSet().hasSkillEffect(
									L1SkillId.INVISIBILITY)) {
								pc.getSkillEffectTimerSet()
										.killSkillEffectTimer(
												L1SkillId.INVISIBILITY);
								pc.sendPackets(new S_Invis(pc.getId(), 0));
								Broadcaster.broadcastPacket(pc, new S_Invis(pc
										.getId(), 0));
								pc.sendPackets(new S_Sound(147));
							}
							if (pc.getSkillEffectTimerSet().hasSkillEffect(
									L1SkillId.BLIND_HIDING)) {
								pc.getSkillEffectTimerSet()
										.killSkillEffectTimer(
												L1SkillId.BLIND_HIDING);
								pc.sendPackets(new S_Invis(pc.getId(), 0));
								Broadcaster.broadcastPacket(pc, new S_Invis(pc
										.getId(), 0));
							}
						}
					}
					cha.getSkillEffectTimerSet().removeSkillEffect(
							STATUS_FREEZE);
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_CharVisualUpdate(pc));
						Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(
								pc));
						if (pc.isPrivateShop()) {
							pc.sendPackets(new S_DoActionShop(pc.getId(),
									ActionCodes.ACTION_Shop, pc.getShopChat()));
							Broadcaster.broadcastPacket(pc, new S_DoActionShop(
									pc.getId(), ActionCodes.ACTION_Shop, pc
											.getShopChat()));
						}
					}
				}
					break;
				case TURN_UNDEAD: {
					if (undeadType == 1 || undeadType == 3) {
						dmg = cha.getCurrentHp();
					}
				}
					break;
				case MANA_DRAIN: {
					int chance = random.nextInt(10) + 5;
					drainMana = chance + (_user.getAbility().getTotalInt() / 2);
					if (cha.getCurrentMp() < drainMana) {
						drainMana = cha.getCurrentMp();
					}
					_player
							.sendPackets(new S_SkillSound(_player.getId(), 2171));
					Broadcaster.broadcastPacket(_player, new S_SkillSound(
							_player.getId(), 2171));
				}
					break;
					
    
				     
				     
					
				case WEAPON_BREAK: {
					if (_calcType == PC_PC || _calcType == NPC_PC) {
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							L1ItemInstance weapon = pc.getWeapon();
							if (weapon != null) {
								int weaponDamage = random.nextInt(_user
										.getAbility().getTotalInt() / 3) + 1;
								pc.sendPackets(new S_ServerMessage(268, weapon
										.getLogName()));
								pc.getInventory().receiveDamage(weapon,
										weaponDamage);
							}
						}
					} else {
						((L1NpcInstance) cha).setWeaponBreaked(true);
					}
				}
					break;
				case FOG_OF_SLEEPING: {
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP,
								true));
					}
					cha.setSleeped(true);
				}
					break;
				case STATUS_FREEZE: {
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND,
								true));
					}
				}
					break;
				default:
					break;
				}

				if (_calcType == PC_PC || _calcType == NPC_PC) { // 여기부터
					switch (_skillId) {
					case TELEPORT:
					case MASS_TELEPORT: {
						L1PcInstance pc = (L1PcInstance) cha;
						Random random = new Random();
						L1BookMark bookm = pc.getBookMark(_bookmarkId);
						if (bookm != null) {
							if (pc.getMap().isEscapable() || pc.isGm()) {
								int newX = bookm.getLocX();
								int newY = bookm.getLocY();
								short mapId = bookm.getMapId();
								L1Map map = L1WorldMap.getInstance().getMap(
										mapId);
								if (_skillId == MASS_TELEPORT) { // 매스 텔레포트
									         List<L1PcInstance>  clanMember = L1World.getInstance().getVisiblePlayer(pc);
									   for (L1PcInstance member : clanMember) {
									   
									   if (pc.getLocation().getTileLineDistance(member.getLocation()) >= 3){
									             continue;
									   }
									   if(pc.getMapId() !=  member.getMapId()){
									            continue;
									   }
									   if ( member.getClanid() != pc.getClanid()) {
									      continue;
									   } 
									   if ( member.getClanid() == 0) {
									      continue;
									   } 
									   member.sendPackets(new S_SystemMessage("혈맹원 '"+ pc.getName()+"' 이(가) 매스텔레포트를 사용하였습니다."));
									   int newX2 = bookm.getLocX();
									   int newY2 = bookm.getLocY();
									   short mapId2 = bookm.getMapId();

									  int rndX,rndY;
									  int ckbb = random.nextInt(2);
									     if(ckbb ==1){rndX= random.nextInt(4)*-1;
									  }else{rndX= random.nextInt(4); 
									  }
									     if(ckbb ==1){rndY= random.nextInt(4)*-1;
									     }else{ rndY= random.nextInt(4);
									  }
									     newX2 = newX+rndX;
									  newY2 = newY+rndY;
									  if ( map.isInMap(newX2, newY2) && map.isPassable(newX2, newY2)) {
									  L1Teleport.teleport( member, newX2, newY2, mapId2, 5, true);
									     } else {
									  L1Teleport.teleport( member, newX, newY, mapId, 5, true);
									            }
									        }
									  }
									 L1Teleport.teleport(pc, newX, newY, mapId, 5,true);
									 } else { // 텔레포트 불가 MAP에의 이동 제한
									  L1Teleport.teleport(pc, pc.getX(), pc.getY(),pc.getMapId(), pc.getMoveState().getHeading(), false);
								//	  pc.sendPackets(new S_ServerMessage(79));
										S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아무일도 일어나지 않았습니다.", Opcodes.S_OPCODE_MSG, 20); 
										pc.sendPackets(s_chatpacket);
									   }
									  } else { // 북마크를 취득 할 수 없었다, 혹은 「임의의 장소」를 선택했을 경우의 처리
									     if (pc.getMap().isTeleportable() || pc.isGm()) {
									        L1Location newLocation = pc.getLocation()
									        .randomLocation(200, true);
									        int newX = newLocation.getX();
									        int newY = newLocation.getY();
									        short mapId = (short) newLocation.getMapId();
									   if (_skillId == MASS_TELEPORT) { // 매스 텔레포트
									         List<L1PcInstance>  clanMember = L1World.getInstance().getVisiblePlayer(pc);
									   for (L1PcInstance member : clanMember) {
									   member.sendPackets(new S_SystemMessage("혈맹원 '"+ pc.getName()+"' 이(가) 매스텔레포트를 사용하였습니다."));
									   if (pc.getLocation().getTileLineDistance(member.getLocation()) >= 3){
									             continue;
									   }
									   if(pc.getMapId() !=  member.getMapId()){
									            continue;
									   }
									   if ( member.getClanid() != pc.getClanid()) {
									      continue;
									   } 
									   if ( member.getClanid() == 0) {
									      continue;
									   } 
									   int newX2 = newLocation.getX();
									   int newY2 = newLocation.getY();
									   short mapId2 = (short) newLocation.getMapId();
									    int rndX,rndY;
									  int ckbb = random.nextInt(2);
									     if(ckbb ==1){rndX= random.nextInt(4)*-1;
									     }else{rndX= random.nextInt(4);
									     }
									     if(ckbb ==1){rndY= random.nextInt(4)*-1;
									     }else{rndY= random.nextInt(4);
									     }
									     newX2 = newX+rndX;
									  newY2 = newY+rndY;
									  L1Map map = L1WorldMap.getInstance().getMap(mapId);
									 
									  if ( map.isInMap(newX2, newY2) && map.isPassable(newX2, newY2)) {
									  L1Teleport.teleport( member, newX2, newY2, mapId2, 5, true);
									       } else {
									 L1Teleport.teleport( member, newX, newY, mapId, 5, true);
									         }
									     }
									 }
									 L1Teleport.teleport(pc, newX, newY, mapId, 5,true);
									  } else {
									//  pc.sendPackets(new S_ServerMessage(276));
									  S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"이곳에서는 무작위 텔레포트를 사용할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
									  pc.sendPackets(s_chatpacket);
									  L1Teleport.teleport(pc, pc.getX(), pc.getY(),pc.getMapId(), pc.getMoveState().getHeading(), false);
									       }
									  }
					}
									     
					break;
					case TELEPORT_TO_MOTHER: {
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getMap().isEscapable() || pc.isGm()) {
							L1Teleport.teleport(pc, 33051, 32337, (short) 4, 5, true);
						} else {
							pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						//	pc.sendPackets(new S_ServerMessage(647));
							S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"주변의 에너지가 순간 이동을 방해하고 있습니다. 여기에서 순간 이동은 사용할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
							pc.sendPackets(s_chatpacket);
						}
					}
						break;
					case CALL_CLAN: {
						L1PcInstance pc = (L1PcInstance) cha;
						L1PcInstance clanPc = (L1PcInstance) L1World
								.getInstance().findObject(_targetID);
						if (clanPc != null) {
							clanPc.setTempID(pc.getId());
							clanPc.sendPackets(new S_Message_YN(729, ""));
						}
					}
						break;
					case RUN_CLAN: {
						L1PcInstance pc = (L1PcInstance) cha;
						L1PcInstance clanPc = (L1PcInstance) L1World
								.getInstance().findObject(_targetID);
						if (clanPc != null) {
							if (pc.getMap().isEscapable() || pc.isGm()) {
								boolean castle_area = L1CastleLocation.checkInAllWarArea(clanPc.getX(),
												clanPc.getY(), clanPc.getMapId());
								if ((clanPc.getMapId() == 0
										|| clanPc.getMapId() == 4 || clanPc
										.getMapId() == 304)
										&& castle_area == false) {
									L1Teleport.teleport(pc, clanPc.getX(),clanPc.getY(), clanPc.getMapId(),5, true);
								} else {
								//	pc.sendPackets(new S_ServerMessage(547));
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"당신의 파트너는 지금 당신이 갈 수 없는 곳에서 플레이를 하고 있습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
								}
							} else {
								pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
								//pc.sendPackets(new S_ServerMessage(647));
								S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"주변의 에너지가 순간 이동을 방해하고 있습니다. 여기에서 순간 이동은 사용할 수 없습니다.", Opcodes.S_OPCODE_MSG, 20); 
								pc.sendPackets(s_chatpacket);
							}
						}
					}
						break;
					case CREATE_MAGICAL_WEAPON: {
						L1PcInstance pc = (L1PcInstance) cha;
						L1ItemInstance item = pc.getInventory().getItem(
								_itemobjid);
						if (item != null && item.getItem().getType2() == 1) {
							int item_type = item.getItem().getType2();
							int safe_enchant = item.getItem().get_safeenchant();
							int enchant_level = item.getEnchantLevel();
							String item_name = item.getName();
							if (safe_enchant < 0) {
							//	pc.sendPackets(new S_ServerMessage(79));
								S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아무일도 일어나지 않았습니다.", Opcodes.S_OPCODE_MSG, 20); 
								pc.sendPackets(s_chatpacket);
							} else if (safe_enchant == 0) {
							//	pc.sendPackets(new S_ServerMessage(79));
								S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아무일도 일어나지 않았습니다.", Opcodes.S_OPCODE_MSG, 20); 
								pc.sendPackets(s_chatpacket);
							} else if (item_type == 1 && enchant_level == 0) {
								if (!item.isIdentified()) {
									pc.sendPackets(new S_ServerMessage(161,	item_name, "$245", "$247"));
								} else {
									item_name = "+0 " + item_name;
									pc.sendPackets(new S_ServerMessage(161, "+0 " + item_name, "$245", "$247"));
								}
								item.setEnchantLevel(1);
								pc.getInventory().updateItem(item,
										L1PcInventory.COL_ENCHANTLVL);
							} else {
							//	pc.sendPackets(new S_ServerMessage(79));
								S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아무일도 일어나지 않았습니다.", Opcodes.S_OPCODE_MSG, 20); 
								pc.sendPackets(s_chatpacket);
							}
						} else {
						//	pc.sendPackets(new S_ServerMessage(79));
							S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아무일도 일어나지 않았습니다.", Opcodes.S_OPCODE_MSG, 20); 
							pc.sendPackets(s_chatpacket);
						}
					}
						break;
					case BRING_STONE: {
						L1PcInstance pc = (L1PcInstance) cha;
						Random random = new Random();
						L1ItemInstance item = pc.getInventory().getItem(
								_itemobjid);
						if (item != null) {
							int dark = (int) (10 + (pc.getLevel() * 0.8) + (pc
									.getAbility().getTotalWis() - 6) * 1.2);
							int brave = (int) (dark / 2.1);
							int wise = (int) (brave / 2.0);
							int kayser = (int) (wise / 1.9);
							int chance = random.nextInt(100) + 1;
							if (item.getItem().getItemId() == 40320) {
								pc.getInventory().removeItem(item, 1);
								if (dark >= chance) {
									pc.getInventory().storeItem(40321, 1);
									pc.sendPackets(new S_ServerMessage(403,"$2475"));
								} else {
								//	pc.sendPackets(new S_ServerMessage(280));
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"마법이 실패했습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
								}
							} else if (item.getItem().getItemId() == 40321) {
								pc.getInventory().removeItem(item, 1);
								if (brave >= chance) {
									pc.getInventory().storeItem(40322, 1);
									pc.sendPackets(new S_ServerMessage(403,"$2476"));
								} else {
									//pc.sendPackets(new S_ServerMessage(280));
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"마법이 실패했습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
								}
							} else if (item.getItem().getItemId() == 40322) {
								pc.getInventory().removeItem(item, 1);
								if (wise >= chance) {
									pc.getInventory().storeItem(40323, 1);
									pc.sendPackets(new S_ServerMessage(403,"$2477"));
								} else {
								//	pc.sendPackets(new S_ServerMessage(280));
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"마법이 실패했습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
								}
							} else if (item.getItem().getItemId() == 40323) {
								pc.getInventory().removeItem(item, 1);
								if (kayser >= chance) {
									pc.getInventory().storeItem(40324, 1);
									pc.sendPackets(new S_ServerMessage(403,
											"$2478"));
								} else {
									//pc.sendPackets(new S_ServerMessage(280));
									S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"마법이 실패했습니다.", Opcodes.S_OPCODE_MSG, 20); 
									pc.sendPackets(s_chatpacket);
								}
							}
						}
					}
						break;
					case SUMMON_MONSTER: {
						L1PcInstance pc = (L1PcInstance) cha;
						int level = pc.getLevel();
						int[] summons;
						if (pc.getMap().isRecallPets() || pc.isGm()) {
							if (pc.getInventory().checkEquipped(20284)) {
								pc
										.sendPackets(new S_ShowSummonList(pc
												.getId()));
								if (!pc.isSummonMonster()) {
									pc.setSummonMonster(true);
								}
							} else {
								summons = new int[] { 81083, 81084, 81085,
										81086, 81087, 81088, 81089 };
								int summonid = 0;
								int summoncost = 6;
								int levelRange = 32;
								for (int i = 0; i < summons.length; i++) {
									if (level < levelRange
											|| i == summons.length - 1) {
										summonid = summons[i];
										break;
									}
									levelRange += 4;
								}

								int petcost = 0;
								Object[] petlist = pc.getPetList().values()
										.toArray();
								for (Object pet : petlist) {
									petcost += ((L1NpcInstance) pet)
											.getPetcost();
								}
								int charisma = pc.getAbility().getTotalCha()
										+ 6 - petcost;
								int summoncount = charisma / summoncost;
								L1Npc npcTemp = NpcTable.getInstance()
										.getTemplate(summonid);
								for (int i = 0; i < summoncount; i++) {
									L1SummonInstance summon = new L1SummonInstance(
											npcTemp, pc);
									summon.setPetcost(summoncost);
								}
							}
						} else {
							//pc.sendPackets(new S_ServerMessage(79));
							S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아무일도 일어나지 않았습니다.", Opcodes.S_OPCODE_MSG, 20); 
							pc.sendPackets(s_chatpacket);
						}
					}
						break;
					case LESSER_ELEMENTAL:
					case GREATER_ELEMENTAL: {
						L1PcInstance pc = (L1PcInstance) cha;
						int attr = pc.getElfAttr();
						if (attr != 0) {
							if (pc.getMap().isRecallPets() || pc.isGm()) {
								int petcost = 0;
								Object[] petlist = pc.getPetList().values()
										.toArray();
								for (Object pet : petlist) {
									petcost += ((L1NpcInstance) pet)
											.getPetcost();
								}

								if (petcost == 0) {
									int summonid = 0;
									int summons[];
									if (_skillId == LESSER_ELEMENTAL) {
										summons = new int[] { 45306, 45303,
												45304, 45305 };
									} else {
										summons = new int[] { 81053, 81050,
												81051, 81052 };
									}
									int npcattr = 1;
									for (int i = 0; i < summons.length; i++) {
										if (npcattr == attr) {
											summonid = summons[i];
											i = summons.length;
										}
										npcattr *= 2;
									}
									if (summonid == 0) {
										Random random = new Random();
										int k3 = random.nextInt(4);
										summonid = summons[k3];
									}

									L1Npc npcTemp = NpcTable.getInstance()
											.getTemplate(summonid);
									L1SummonInstance summon = new L1SummonInstance(
											npcTemp, pc);
									summon.setPetcost(pc.getAbility()
											.getTotalCha() + 7);
								}
							} else {
								//pc.sendPackets(new S_ServerMessage(79));
								S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아무일도 일어나지 않았습니다.", Opcodes.S_OPCODE_MSG, 20); 
								pc.sendPackets(s_chatpacket);
							}
						}
					}
						break;
					// 이부분은 전체적으로 처리를 하기때문에 제외함

					case ABSOLUTE_BARRIER: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.stopMpRegenerationByDoll();
						pc.stopHpRegenerationByDoll();
					}
						break;

					case LIGHT:
						break;
					case GLOWING_AURA: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addHitup(5);
						pc.addBowHitup(5);
						pc.getResistance().addMr(20);
						pc.sendPackets(new S_SPMR(pc));
						pc.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
					}
						break;
					case SHINING_AURA: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAC().addAc(-8);
						pc.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
					}
						break;
					case BRAVE_AURA: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(5);
						pc.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
					}
						break;
					case SHIELD: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAC().addAc(-2);
						pc.sendPackets(new S_SkillIconShield(5, _getBuffIconDuration));
					}
						break;
					case SHADOW_ARMOR: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getResistance().addMr(5);
						pc.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));
					}
						break;
					case DRESS_DEXTERITY: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAbility().addAddedDex((byte) 2);
						pc.sendPackets(new S_Dexup(pc, 2, _getBuffIconDuration));
					}
						break;
					case DRESS_MIGHTY: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAbility().addAddedStr((byte) 2);
						pc.sendPackets(new S_Strup(pc, 2, _getBuffIconDuration));
					}
						break;
					case SHADOW_FANG: {
						L1PcInstance pc = (L1PcInstance) cha;
						L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
						if (item != null && item.getItem().getType2() == 1) {
							item.setSkillWeaponEnchant(pc, _skillId, _skill .getBuffDuration() * 1000);
						} else {
							//pc.sendPackets(new S_ServerMessage(79));
							S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아무일도 일어나지 않았습니다.", Opcodes.S_OPCODE_MSG, 20); 
							pc.sendPackets(s_chatpacket);
						}
					}
						break;
					case ENCHANT_WEAPON: {//인챈트웨폰
						L1PcInstance pc = (L1PcInstance) cha;
						L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
						if (item != null && item.getItem().getType2() == 1) {
							pc.sendPackets(new S_ServerMessage(161, String .valueOf(item.getLogName()).trim(), "$245",
									"$247"));
							item.setSkillWeaponEnchant(pc, _skillId, _skill .getBuffDuration() * 1000);
						} else {
						//	pc.sendPackets(new S_ServerMessage(79));
							S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아무일도 일어나지 않았습니다.", Opcodes.S_OPCODE_MSG, 20); 
							pc.sendPackets(s_chatpacket);
						}
					}
						break;
					case HOLY_WEAPON:
					case BLESS_WEAPON: {
						if (!(cha instanceof L1PcInstance)) {
							return;
						}
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getWeapon() == null) {
						//	pc.sendPackets(new S_ServerMessage(79));
							S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아무일도 일어나지 않았습니다.", Opcodes.S_OPCODE_MSG, 20); 
							pc.sendPackets(s_chatpacket);
							return;
						}
						for (L1ItemInstance item : pc.getInventory().getItems()) {
							if (pc.getWeapon().equals(item)) {
								pc.sendPackets(new S_ServerMessage(161, String
										.valueOf(item.getLogName()).trim(),
										"$245", "$247"));
								item.setSkillWeaponEnchant(pc, _skillId, _skill
										.getBuffDuration() * 1000);
								return;
							}
						}
					}
						break;
					case BLESSED_ARMOR: { // 브레스트아머
						L1PcInstance pc = (L1PcInstance) cha;
						L1ItemInstance item = pc.getInventory().getItem(
								_itemobjid);
						if (item != null && item.getItem().getType2() == 2
								&& item.getItem().getType() == 2) {
							pc.sendPackets(new S_ServerMessage(161, String
									.valueOf(item.getLogName()).trim(), "$245",
									"$247"));
							item.setSkillArmorEnchant(pc, _skillId, _skill
									.getBuffDuration() * 1000);
						} else {
						//	pc.sendPackets(new S_ServerMessage(79));
							S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"아무일도 일어나지 않았습니다.", Opcodes.S_OPCODE_MSG, 20); 
							pc.sendPackets(s_chatpacket);
						}
					}
						break;
					case EARTH_BLESS: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAC().addAc(-7);
						pc.sendPackets(new S_SkillIconShield(7,
								_getBuffIconDuration));
					}
						break;
					case RESIST_MAGIC: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getResistance().addMr(10);
						pc.sendPackets(new S_SPMR(pc));
					}
						break;
					case CLEAR_MIND: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAbility().addAddedWis((byte) 3);
						pc.resetBaseMr();
					}
						break;
					case RESIST_ELEMENTAL: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getResistance().addAllNaturalResistance(10);
						pc.sendPackets(new S_OwnCharAttrDef(pc));
					}
						break;
					case BODY_TO_MIND: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.setCurrentMp(pc.getCurrentMp() + 2);
					}
						break;
					case BLOODY_SOUL: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.setCurrentMp(pc.getCurrentMp() + 15);
					}
						break;
					case ELEMENTAL_PROTECTION: {
						L1PcInstance pc = (L1PcInstance) cha;
						int attr = pc.getElfAttr();
						if (attr == 1) {
							pc.getResistance().addEarth(50);
						} else if (attr == 2) {
							pc.getResistance().addFire(50);
						} else if (attr == 4) {
							pc.getResistance().addWater(50);
						} else if (attr == 8) {
							pc.getResistance().addWind(50);
						}
					}
						break;
					case INVISIBILITY:
					case BLIND_HIDING: {
						L1PcInstance pc = (L1PcInstance) cha;

						for (L1DollInstance doll : pc.getDollList().values()) {
							doll.deleteDoll();
							pc.sendPackets(new S_SkillIconGFX(56, 0));
							pc.sendPackets(new S_OwnCharStatus(pc));
						}
						pc.sendPackets(new S_Invis(pc.getId(), 1));
						Broadcaster.broadcastPacket(pc, new S_Invis(pc.getId(),
								1));
						pc.sendPackets(new S_Sound(147));
					}
						break;
					case IRON_SKIN: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAC().addAc(-10);
						pc.sendPackets(new S_SkillIconShield(10,
								_getBuffIconDuration));
					}
						break;
					case EARTH_SKIN: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAC().addAc(-6);
						pc.sendPackets(new S_SkillIconShield(6,
								_getBuffIconDuration));
					}
						break;
					case PHYSICAL_ENCHANT_STR: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAbility().addAddedStr((byte) 5);
						pc
								.sendPackets(new S_Strup(pc, 5,
										_getBuffIconDuration));
					}
						break;
					case PHYSICAL_ENCHANT_DEX: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAbility().addAddedDex((byte) 5);
						pc
								.sendPackets(new S_Dexup(pc, 5,
										_getBuffIconDuration));
					}
						break;
					case FIRE_WEAPON: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(4);
						pc.sendPackets(new S_SkillIconAura(147,
								_getBuffIconDuration));
					}
						break;
					case FIRE_BLESS: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(4);
						pc.sendPackets(new S_SkillIconAura(154,
								_getBuffIconDuration));
					}
						break;
					case BURNING_WEAPON: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(6);
						pc.addHitup(6);
						pc.sendPackets(new S_SkillIconAura(162,_getBuffIconDuration));
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"버닝웨폰: 960초 동안 (대미지+6, 근거리 명중+6) 효과부여", Opcodes.S_OPCODE_MSG, 20);
						pc.sendPackets(s_chatpacket);

					}
						break;
						
					case MIRROR_IMAGE: {
						L1PcInstance pc = (L1PcInstance) cha;
	                     pc.addDg(-5);
					}
							break;
	
						
						
					case WIND_SHOT: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowHitup(6);
						pc.sendPackets(new S_SkillIconAura(148,
								_getBuffIconDuration));
					}
						break;
					case STORM_EYE: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowHitup(2);
						pc.addBowDmgup(3);
						pc.sendPackets(new S_SkillIconAura(155,
								_getBuffIconDuration));
					}
						break;
					case STORM_SHOT: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowDmgup(6);
						pc.addBowHitup(3);
						pc.sendPackets(new S_SkillIconAura(165,
								_getBuffIconDuration));
					}
						break;
					case BERSERKERS: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAC().addAc(10);
						pc.addDmgup(5);
						pc.addHitup(2);
					}
						break;
					case SCALES_EARTH_DRAGON: { //용기사 안타각성 ac-3 홀드내성 +10 
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAC().addAc(-3);
						pc.getResistance().addHold(10);
						pc.sendPackets(new S_OwnCharAttrDef(pc));
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"안타각성:600초 동안 (방어-3,홀드+10) 효과가  부여됩니다.", Opcodes.S_OPCODE_MSG, 20);
						pc.sendPackets(s_chatpacket);
					}
					break;
					case SCALES_WATER_DRAGON: {//용기사 파푸각성 동빙+10 바이탈라이즈 효과
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getResistance().addFreeze(10);
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"파푸각성:600초 동안 (바이탈라이즈,동빙+10) 효과가  부여됩니다.", Opcodes.S_OPCODE_MSG, 20);
						pc.sendPackets(s_chatpacket);
					}
					break;
					case SCALES_FIRE_DRAGON: {//용기사 발라각성 스턴내성 +10 공성 +5
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getResistance().addStun(10);
						pc.addHitup(5);
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc,"발라각성:600초 동안 (공성+5,스턴+10) 효과가  부여됩니다.", Opcodes.S_OPCODE_MSG, 20);
						pc.sendPackets(s_chatpacket);
					}
					break;
					case IllUSION_OGRE: { // 일루젼 오거
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(4);
						pc.addHitup(4);
					    pc.addBowHitup(4);
		                pc.addBowDmgup(4);
					}
						break;
					case BOUNCE_ATTACK: { // 바운스어택
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addHitup(6);
					}
						break;
					case IllUSION_LICH: { // 일루젼 리치
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAbility().addSp(2);
						pc.sendPackets(new S_SPMR(pc));
					}
						break;
					case IllUSION_DIAMONDGOLEM: { // 일루젼 다이아골렘
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAC().addAc(-20);
						pc.sendPackets(new S_OwnCharAttrDef(pc));
					}
						break;
					case IllUSION_AVATAR: { // 일루젼 아바타
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(10);
						pc.addBowDmgup(10);
						pc.getAbility().addSp(6);
						pc.sendPackets(new S_SPMR(pc));
					}
						break;

					// 상아탑 버프
					case SANGA: {
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.addHitup(5);
							pc.addDmgup(10);
							pc.addBowHitup(5);
							pc.addBowDmgup(10);
							pc.getAbility().addAddedStr(3);
							pc.getAbility().addAddedDex(3);
							pc.getAbility().addAddedInt(3);
							pc.getAbility().addAddedCon(3);
							pc.getAbility().addAddedWis(3);
							pc.getAbility().addSp(3);
							pc.sendPackets(new S_SPMR(pc));
						}
					}
						break;
					case SANGABUFF: {
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.addHitup(5);
							pc.addDmgup(5);
							pc.addBowHitup(5);
							pc.addBowDmgup(5);
							pc.getAbility().addAddedStr(2);
							pc.getAbility().addAddedDex(2);
							pc.getAbility().addAddedInt(2);
							pc.getAbility().addAddedCon(2);
							pc.getAbility().addAddedWis(2);
							pc.getAbility().addSp(1);
							pc.sendPackets(new S_SPMR(pc));
						}
					}
						break;
					case BUFF_SAEL: {
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.addHitup(5);
							pc.addDmgup(1);
							pc.addBowHitup(5);
							pc.addBowDmgup(1);
							pc.addExp(30);
							pc.addMaxHp(100);
							pc.addMaxMp(50);
							pc.addHpr(3);
							pc.addMpr(3);
							pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
									.getMaxHp()));
							pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
									.getMaxMp()));
							pc.sendPackets(new S_SPMR(pc));
						}
					}
						break;
					// 상아탑 버프
					// 크레이 혈흔
					case BUFF_CRAY: {
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.addHitup(5);
							pc.addDmgup(1);
							pc.addBowHitup(5);
							pc.addBowDmgup(1);
							pc.addExp(30);
							pc.addMaxHp(100);
							pc.addMaxMp(50);
							pc.addHpr(3);
							pc.addMpr(3);
							pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
									.getMaxHp()));
							pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
									.getMaxMp()));
							pc.sendPackets(new S_SPMR(pc));
						}
					}
						break;
					case ANTA_BLOOD: {
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.getAC().addAc(-2);
							pc.sendPackets(new S_SPMR(pc));
						}
					}
						break;
					// 크레이 혈흔

					case INSIGHT: { // 인사이트
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAbility().addAddedStr((byte) 1);
						pc.getAbility().addAddedDex((byte) 1);
						pc.getAbility().addAddedCon((byte) 1);
						pc.getAbility().addAddedInt((byte) 1);
						pc.getAbility().addAddedWis((byte) 1);
						pc.resetBaseMr();
					}
						break;
					case SHAPE_CHANGE: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_ShowPolyList(pc.getId()));
						if (!pc.isShapeChange()) {
							pc.setShapeChange(true);
						}
					}
						break;
					case ADVANCE_SPIRIT: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.setAdvenHp(pc.getBaseMaxHp() / 5);
						pc.setAdvenMp(pc.getBaseMaxMp() / 5);
						pc.addMaxHp(pc.getAdvenHp());
						pc.addMaxMp(pc.getAdvenMp());
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
						pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
						if (pc.isInParty()) {
							pc.getParty().updateMiniHP(pc);
						}
					}
						break;
					case GREATER_HASTE: {
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getHasteItemEquipped() > 0) {
							continue;
						}
						if (pc.getMoveState().getMoveSpeed() != 2) {
							pc.setDrink(false);
							pc.getMoveState().setMoveSpeed(1);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 1,
									_getBuffIconDuration));
							Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc
									.getId(), 1, 0));
						} else {
							int skillNum = 0;
							if (pc.getSkillEffectTimerSet()
									.hasSkillEffect(SLOW)) {
								skillNum = SLOW;
							} else if (pc.getSkillEffectTimerSet()
									.hasSkillEffect(MASS_SLOW)) {
								skillNum = MASS_SLOW;
							} else if (pc.getSkillEffectTimerSet()
									.hasSkillEffect(ENTANGLE)) {
								skillNum = ENTANGLE;
							} else if (pc.getSkillEffectTimerSet()
									.hasSkillEffect(MOB_SLOW_1)) {
								skillNum = MOB_SLOW_1;
							} else if (pc.getSkillEffectTimerSet()
									.hasSkillEffect(MOB_SLOW_18)) {
								skillNum = MOB_SLOW_18;
							}
							if (skillNum != 0) {
								pc.getSkillEffectTimerSet().removeSkillEffect(
										skillNum);
								pc.getSkillEffectTimerSet().removeSkillEffect(
										GREATER_HASTE);
								pc.getMoveState().setMoveSpeed(0);
								continue;
							}
						}
					}
						break;
					case HOLY_WALK:
					case MOVING_ACCELERATION:
					case WIND_WALK: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getMoveState().setBraveSpeed(4);
						pc.sendPackets(new S_SkillBrave(pc.getId(), 4, _getBuffIconDuration));
						Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 4, 0));
					}
						break;
					case BLOOD_LUST: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SkillBrave(pc.getId(), 6, _getBuffIconDuration));
						Broadcaster.broadcastPacket(pc, new S_SkillBrave(pc.getId(), 6, 0));
						pc.getMoveState().setBraveSpeed(1);
						pc.sendPackets(new S_SkillBrave(pc.getId(), 1, _getBuffIconDuration));
					}
						break;
					case STATUS_TIKAL_BOSSJOIN: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addHitup(5);
						pc.addDmgup(10);
						pc.addBowHitup(5);
						pc.addBowDmgup(10);
						pc.getAbility().addAddedStr((byte) 3);
						pc.getAbility().addAddedDex((byte) 3);
						pc.getAbility().addAddedCon((byte) 3);
						pc.getAbility().addAddedInt((byte) 3);
						pc.getAbility().addAddedWis((byte) 3);
						pc.getAbility().addSp(3);
					}
						break;
					/** 운세 버프 * */
					case STATUS_LUCK_A: {// 운세버프 (매우 좋은)
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(2);
						pc.addHitup(2);
						pc.getAbility().addSp(2);
						pc.sendPackets(new S_SPMR(pc));
						pc.addHpr(3);
						pc.addMaxHp(50);
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
						if (pc.isInParty()) {
							pc.getParty().updateMiniHP(pc);
						}
						pc.addMpr(3);
						pc.addMaxMp(30);
						pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
					}
						break;
					case STATUS_LUCK_B: {// 운세버프 (좋은)
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addHitup(2);
						pc.getAbility().addSp(1);
						pc.sendPackets(new S_SPMR(pc));
						pc.addMaxHp(50);
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
						if (pc.isInParty()) {
							pc.getParty().updateMiniHP(pc);
						}
						pc.addMaxMp(30);
						pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
					}
						break;
					case STATUS_LUCK_C: {// 운세버프 (보통)
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addMaxHp(50);
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
						if (pc.isInParty()) {
							pc.getParty().updateMiniHP(pc);
						}
						pc.addMaxMp(30);
						pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
						pc.getAC().addAc(-2);
					}
						break;
					case STATUS_LUCK_D: {// 운세버프 (나쁜)
						L1PcInstance pc = (L1PcInstance) cha;
						pc.getAC().addAc(-1);
					}
						break;

					case STATUS_TIKAL_BOSSDIE: {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addHitup(5);
						pc.addDmgup(5);
						pc.addBowHitup(5);
						pc.addBowDmgup(5);
						pc.getAbility().addAddedStr((byte) 2);
						pc.getAbility().addAddedDex((byte) 2);
						pc.getAbility().addAddedCon((byte) 2);
						pc.getAbility().addAddedInt((byte) 2);
						pc.getAbility().addAddedWis((byte) 2);
						pc.getAbility().addSp(1);
					}
						break;
					case STATUS_COMA_3: {
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_COMA_5))
						pc.getSkillEffectTimerSet().removeSkillEffect(	L1SkillId.STATUS_COMA_5);
						pc.getAC().addAc(-2);
						pc.addHitup(3);
						pc.getAbility().addAddedStr((byte) 5);
						pc.getAbility().addAddedDex((byte) 5);
						pc.getAbility().addAddedCon((byte) 1);
					}
						break;
					case STATUS_COMA_5: {
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getSkillEffectTimerSet().hasSkillEffect(
								L1SkillId.STATUS_COMA_3))
							pc.getSkillEffectTimerSet().removeSkillEffect(
									L1SkillId.STATUS_COMA_3);
						pc.getAC().addAc(-8);
						pc.addHitup(5);
						pc.getAbility().addAddedStr((byte) 5);
						pc.getAbility().addAddedDex((byte) 5);
						pc.getAbility().addAddedCon((byte) 1);
						pc.getAbility().addSp(1);
					}
						break;
					case DRAGONBLOOD_ANTA: { // 안타라스의 혈흔
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGONBLOOD_ANTA)){
							pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.DRAGONBLOOD_ANTA);
						}
						pc.getAC().addAc(-2);
						pc.getResistance().addWater(50);
						pc.sendPackets(new S_OwnCharAttrDef(pc));
						pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 82, 1440 ));
					}
						break;

					case DRAGONBLOOD_PAP: { // 파푸리온의 혈흔
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGONBLOOD_PAP)){
							pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.DRAGONBLOOD_PAP);
						}
						pc.getAC().addAc(-2);
						pc.getResistance().addWind(50);
						pc.sendPackets(new S_OwnCharAttrDef(pc));
						pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 85, 1440 ));	
					}
						break;
                      case DRAGONBLOOD_RIND: {//린드레이드
                        L1PcInstance pc = (L1PcInstance) cha;
                           if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGONBLOOD_RIND)){
                            pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.DRAGONBLOOD_RIND);
                           }
                        pc.getResistance().addEarth(50);
                        pc.sendPackets(new S_SPMR(pc));
                        pc.sendPackets(new S_OwnCharAttrDef(pc));
                        pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 88, 1440 ));
                       }
                       break;
					case LIND_MAAN: {// 마안풍룡
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getSkillEffectTimerSet().hasSkillEffect(
								LIND_MAAN)) {
							pc.getSkillEffectTimerSet().removeSkillEffect(
									LIND_MAAN);
						}
						pc.getResistance().addSleep(15); // 수면내성3
					}
						break;
					case FAFU_MAAN: {// 마안수룡
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getSkillEffectTimerSet().hasSkillEffect(
								FAFU_MAAN)) {
							pc.getSkillEffectTimerSet().removeSkillEffect(
									FAFU_MAAN);
						}
						pc.getResistance().addFreeze(15); // 동빙내성3
					}
						break;
					case ANTA_MAAN: {// 마안지룡
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getSkillEffectTimerSet().hasSkillEffect(
								ANTA_MAAN)) {
							pc.getSkillEffectTimerSet().removeSkillEffect(
									ANTA_MAAN);
						}
						pc.getResistance().addHold(15); // 홀드내성
					}
						break;
					case VALA_MAAN: {// 마안화룡
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getSkillEffectTimerSet().hasSkillEffect(
								VALA_MAAN)) {
							pc.getSkillEffectTimerSet().removeSkillEffect(
									VALA_MAAN);
						}
						pc.getResistance().addStun(15); // 스턴내성3
					}
						break;
					case BIRTH_MAAN: {// 마안탄생
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getSkillEffectTimerSet().hasSkillEffect(
								BIRTH_MAAN)) {
							pc.getSkillEffectTimerSet().removeSkillEffect(
									BIRTH_MAAN);
						}
						pc.getResistance().addHold(15); // 홀드내성
						pc.getResistance().addFreeze(15); // 동빙내성3
					}
						break;
					case SHAPE_MAAN: {// 마안형상
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getSkillEffectTimerSet().hasSkillEffect(
								SHAPE_MAAN)) {
							pc.getSkillEffectTimerSet().removeSkillEffect(
									SHAPE_MAAN);
						}
						pc.getResistance().addHold(15); // 홀드내성
						pc.getResistance().addFreeze(15); // 동빙내성3
						pc.getResistance().addSleep(15); // 수면내성3
					}
						break;
					case LIFE_MAAN: {// 마안생명
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getSkillEffectTimerSet().hasSkillEffect(LIFE_MAAN)) {
							pc.getSkillEffectTimerSet().removeSkillEffect(LIFE_MAAN);
						}
						pc.getResistance().addHold(15); // 홀드내성
						pc.getResistance().addFreeze(15); // 동빙내성3
						pc.getResistance().addSleep(15); // 수면내성3
						pc.getResistance().addStun(15); // 스턴내성3
					}
						break;
					case MAAN_TIMER:
						break;
					default:
						break;
					}
				}

				if (_calcType == PC_NPC || _calcType == NPC_NPC) {
					if (_skillId == TAMING_MONSTER
							&& ((L1MonsterInstance) cha).getNpcTemplate()
									.isTamable()) {
						int petcost = 0;
						Object[] petlist = ((L1PcInstance) _user).getPetList()
								.values().toArray();
						for (Object pet : petlist) {
							petcost += ((L1NpcInstance) pet).getPetcost();
						}
						int charisma = _user.getAbility().getTotalCha();
						if (_player.isElf()) {
							charisma += 12;
						} else if (_player.isWizard()) {
							charisma += 6;
						}
						charisma -= petcost;
						if (charisma >= 6) {
							L1SummonInstance summon = new L1SummonInstance(
									_targetNpc, (L1PcInstance) _user, false);
							_target = summon;
						} else {
							_player.sendPackets(new S_ServerMessage(319));
						}
					} else if (_skillId == CREATE_ZOMBIE) {
						int petcost = 0;
						Object[] petlist = ((L1PcInstance) _user).getPetList()
								.values().toArray();
						for (Object pet : petlist) {
							petcost += ((L1NpcInstance) pet).getPetcost();
						}
						int charisma = _user.getAbility().getTotalCha();
						if (_player.isElf()) {
							charisma += 12;
						} else if (_player.isWizard()) {
							charisma += 6;
						}
						charisma -= petcost;
						if (charisma >= 6) {
							L1SummonInstance summon = new L1SummonInstance(
									_targetNpc, (L1PcInstance) _user, true);
							_target = summon;
						} else {
							_player.sendPackets(new S_ServerMessage(319));
						}
					} else if (_skillId == WEAK_ELEMENTAL) {
						if (cha instanceof L1MonsterInstance) {
							L1Npc npcTemp = ((L1MonsterInstance) cha)
									.getNpcTemplate();
							int weakAttr = npcTemp.get_weakAttr();
							if ((weakAttr & 1) == 1) {
								Broadcaster.broadcastPacket(cha,
										new S_SkillSound(cha.getId(), 2169));
							}
							if ((weakAttr & 2) == 2) {
								Broadcaster.broadcastPacket(cha,
										new S_SkillSound(cha.getId(), 2167));
							}
							if ((weakAttr & 4) == 4) {
								Broadcaster.broadcastPacket(cha,
										new S_SkillSound(cha.getId(), 2166));
							}
							if ((weakAttr & 8) == 8) {
								Broadcaster.broadcastPacket(cha,
										new S_SkillSound(cha.getId(), 2168));
							}
						}
					} else if (_skillId == RETURN_TO_NATURE) {
						if (Config.RETURN_TO_NATURE
								&& cha instanceof L1SummonInstance) {
							L1SummonInstance summon = (L1SummonInstance) cha;
							Broadcaster.broadcastPacket(summon,
									new S_SkillSound(summon.getId(), 2245));
							summon.returnToNature();
						} else {
							if (_user instanceof L1PcInstance) {
								_player.sendPackets(new S_ServerMessage(79));
							}
						}
					}
				}

				if (_skill.getType() == L1Skills.TYPE_HEAL
						&& _calcType == PC_NPC && undeadType == 1) {
					dmg *= -1;
				}

				if (_skill.getType() == L1Skills.TYPE_HEAL
						&& _calcType == PC_NPC && undeadType == 3) {
					dmg = 0;
				}

				if ((cha instanceof L1TowerInstance || cha instanceof L1DoorInstance)
						&& dmg < 0) {
					dmg = 0;
				}

				if (dmg != 0 || drainMana != 0) {
					_magic.commit(dmg, drainMana);
				}

				if (heal > 0) {
					if ((heal + _user.getCurrentHp()) > _user.getMaxHp()) {
						_user.setCurrentHp(_user.getMaxHp());
					} else {
						_user.setCurrentHp(heal + _user.getCurrentHp());
					}
				}

				if (cha instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) cha;
					pc.getLight().turnOnOffLight();
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
					sendHappenMessage(pc);
				}

				addMagicList(cha, false);
				if (cha instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) cha;
					pc.getLight().turnOnOffLight();
				}
			}

			if (_skillId == DETECTION || _skillId == COUNTER_DETECTION) {
				detection(_player);
			}

		} catch (Exception e) {
			// 스킬 오류 발생 부분에 케릭터명, 몹명, 타켓명순으로 출력

			System.out.println("오류 발생 : " + _player.getAccountName() + " | "
					+ _npc.getName() + " | " + _target.getName());
			_log.log(Level.SEVERE, "L1SkillUse[]Error1", e);
		}
	}
	private boolean isNotCancelable(int skillNum) {// 켄슬버프안사라짐
		return skillNum == ENCHANT_WEAPON || skillNum == BLESSED_ARMOR
				|| skillNum == ABSOLUTE_BARRIER || skillNum == ADVANCE_SPIRIT
				|| skillNum == SHOCK_STUN || skillNum == SHADOW_FANG
				|| skillNum == REDUCTION_ARMOR || skillNum == SOLID_CARRIAGE
				|| skillNum == COUNTER_BARRIER
				|| skillNum == THUNDER_GRAB
				|| skillNum == BONE_BREAK
				|| skillNum == ANTA_SKILL_3
				|| skillNum == ANTA_SKILL_4
				// ////////////////////////////////////////
				|| skillNum ==ANTA_MAAN|| skillNum ==FAFU_MAAN|| skillNum ==LIND_MAAN
				|| skillNum ==VALA_MAAN|| skillNum ==BIRTH_MAAN|| skillNum ==SHAPE_MAAN
				|| skillNum ==LIFE_MAAN|| skillNum == MAAN_TIMER
				|| skillNum == STATUS_CASHSCROLL
				|| skillNum == STATUS_CASHSCROLL2
				|| skillNum == STATUS_CASHSCROLL3 || skillNum == EXP_POTION
				|| skillNum == DRAGON_EMERALD_YES
				// /////////////////////////////////////////
				|| skillNum == ANTA_SKILL_5 || skillNum == DRAGONBLOOD_ANTA
				|| skillNum == DRAGONBLOOD_PAP || skillNum == STATUS_LUCK_A
                || skillNum == STATUS_LUCK_B || skillNum == STATUS_LUCK_C
                || skillNum == STATUS_LUCK_D;
	}

	private void detection(L1PcInstance pc) {
		if (!pc.isGmInvis() && pc.isInvisble()) {
			pc.delInvis();
			pc.beginInvisTimer();
		}
		FastTable<L1PcInstance> list = L1World.getInstance().getVisiblePlayer(
				pc);
		for (L1PcInstance tgt : list) {
			if (!tgt.isGmInvis() && tgt.isInvisble()) {
				tgt.delInvis();
			}
		}
		L1WorldTraps.getInstance().onDetection(pc);
	}

	private boolean isTargetCalc(L1Character cha) {
		if (_skill.getTarget().equals("attack") && _skillId != 18) {
			if (isPcSummonPet(cha)) {
				if (CharPosUtil.getZoneType(_player) == 1
						|| CharPosUtil.getZoneType(cha) == 1
						|| _player.checkNonPvP(_player, cha)) {
					return false;
				}
			}
		}

		if (_skillId == FOG_OF_SLEEPING && _user.getId() == cha.getId()) {
			return false;
		}

		if (_skillId == MASS_SLOW) {
			if (_user.getId() == cha.getId()) {
				return false;
			}
			if (cha instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) cha;
				if (_user.getId() == summon.getMaster().getId()) {
					return false;
				}
			} else if (cha instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) cha;
				if (_user.getId() == pet.getMaster().getId()) {
					return false;
				}
			}
		}

		if (_skillId == MASS_TELEPORT) {
			if (_user.getId() != cha.getId()) {
				return false;
			}
		}

		return true;
	}

	private boolean isPcSummonPet(L1Character cha) {
		if (_calcType == PC_PC) {
			return true;
		}

		if (_calcType == PC_NPC) {
			if (cha instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) cha;
				if (summon.isExsistMaster()) {
					return true;
				}
			}
			if (cha instanceof L1PetInstance) {
				return true;
			}
		}
		return false;
	}

	// 카운터 매직이 발동했는지 돌려준다
	private boolean isUseCounterMagic(L1Character cha) {
		// 카운터 매직 유효한 스킬로 카운터 매직중
		if (_isCounterMagic
				&& cha.getSkillEffectTimerSet().hasSkillEffect(COUNTER_MAGIC)) {
			cha.getSkillEffectTimerSet().removeSkillEffect(COUNTER_MAGIC);
			int castgfx = SkillsTable.getInstance().getTemplate(COUNTER_MAGIC).getCastGfx();
			Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(),castgfx));
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
			}
			return true;
		}
		return false;
	}

	private boolean isTargetFailure(L1Character cha) {
		boolean isTU = false;
		boolean isErase = false;
		boolean isManaDrain = false;
		int undeadType = 0;

		if (cha instanceof L1TowerInstance || cha instanceof L1DoorInstance) {
			return true;
		}

		if (cha instanceof L1PcInstance) {
			if (_calcType == PC_PC && _player.checkNonPvP(_player, cha)) {
				L1PcInstance pc = (L1PcInstance) cha;
				if (_player.getId() == pc.getId()
						|| (pc.getClanid() != 0 && _player.getClanid() == pc
								.getClanid())) {
					return false;
				}
				return true;
			}
			return false;
		}

		if (cha instanceof L1MonsterInstance) {
			isTU = ((L1MonsterInstance) cha).getNpcTemplate().get_IsTU();
			isErase = ((L1MonsterInstance) cha).getNpcTemplate().get_IsErase();
			undeadType = ((L1MonsterInstance) cha).getNpcTemplate()
					.get_undead();
			isManaDrain = true;
		}
		if ((_skillId == TURN_UNDEAD && (undeadType == 0 || undeadType == 2))
				|| (_skillId == TURN_UNDEAD && isTU == false)
				|| ((_skillId == ERASE_MAGIC || _skillId == SLOW
						|| _skillId == MOB_SLOW_1 || _skillId == MOB_SLOW_18
						|| _skillId == MANA_DRAIN || _skillId == MASS_SLOW
						|| _skillId == ENTANGLE || _skillId == WIND_SHACKLE) && isErase == false)
				|| (_skillId == MANA_DRAIN && isManaDrain == false)) {
			return true;
		}
		return false;
	}

	public static L1SkillUse getInstance() {
		// TODO Auto-generated method stub
		return null;
	}
}
