package l1j.server.server.model.skill;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.templates.L1Skills;

public class AttackSkill {

	private static Logger _log = Logger.getLogger(AttackSkill.class.getName());

	private L1Skills _skill;

	private int _skillId;

	private int _targetID;

	private int _mpConsume = 0;

	private int _hpConsume = 0;

	private int _targetX = 0;

	private int _targetY = 0;

	private L1PcInstance _player = null;

	private boolean _isCounterMagic = true;

	private L1Character _target = null;

	private int _calcType;

	private static final int PC_PC = 1;

	private static final int PC_NPC = 2;

	private int[][] intMP = { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 0, 1, 2, 2, 2, 2, 2, 2, 2, 2 },
			{ 0, 1, 2, 3, 3, 3, 3, 3, 3, 3 }, { 0, 1, 2, 3, 4, 4, 4, 4, 4, 4 },
			{ 0, 1, 2, 3, 4, 5, 5, 5, 5, 5 }, { 0, 1, 2, 3, 4, 5, 6, 6, 6, 6 },
			{ 0, 1, 2, 3, 4, 5, 6, 7, 7, 7 } };

	public AttackSkill(L1PcInstance player, int skillId, int targetId, int x,
			int y) {
		_skill = SkillsTable.getInstance().getTemplate(skillId);
		_skillId = skillId;
		_targetX = x;
		_targetY = y;
		_player = player;
		_targetID = targetId;
		L1Object l1object = L1World.getInstance().findObject(_targetID);
		if (!(l1object instanceof L1Character)) {
			return;
		}
		_target = (L1Character) l1object;

		if (_target instanceof L1PcInstance) {
			_calcType = PC_PC;
		} else if (_target instanceof L1NpcInstance) {
			_calcType = PC_NPC;
		}

		// 존재버그 관련 추가
		if (_player instanceof L1PcInstance) {
			L1PcInstance jonje = L1World.getInstance().getPlayer(
					_player.getName());
			if (jonje == null && player.getAccessLevel() != Config.GMCODE) {
				player.sendPackets(new S_SystemMessage("존재버그 강제종료! 재접속하세요"));
				player.sendPackets(new S_Disconnect());
				return;
			}
		}
		if (isSkillUsable()) {
			RunSkill();
			useConsume();
			setDelay();
		}
	}

	private boolean isSkillUsable() {
		if (_player.isInvisble() || _player.isInvisDelay()) {
			return false;
		}
		if (_player.getInventory().getWeight240() >= 200) { // 중량 오버이면 스킬을 사용할 수
			// 없다
			_player.sendPackets(new S_ServerMessage(316));
			return false;
		}
		int polyId = _player.getGfxId().getTempCharGfx();
		L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
		if (poly != null && !poly.canUseSkill()) {
			_player.sendPackets(new S_ServerMessage(285));
			return false;
		}
		if (_player.isSkillDelay()) {
			return false;
		}
		if (_player.getSkillEffectTimerSet().hasSkillEffect(SILENCE)
				|| _player.getSkillEffectTimerSet().hasSkillEffect(
						AREA_OF_SILENCE)
				|| _player.getSkillEffectTimerSet().hasSkillEffect(
						STATUS_POISON_SILENCE)
				|| _player.getSkillEffectTimerSet().hasSkillEffect(CONFUSION)) {
			_player.sendPackets(new S_ServerMessage(285));
			return false;
		}
		if (_skillId == DISINTEGRATE && _player.getLawful() < 500) {
			_player.sendPackets(new S_ServerMessage(352, "$967"));
			return false;
		}
		if (_player.getLocation().getTileLineDistance(_target.getLocation()) > _skill
				.getRanged()) {
			return false;
		}
		if (!CharPosUtil.glanceCheck(_player, _target.getX(), _target.getY())) {
			return false;
		}
		if (!isHPMPConsume()) {
			return false;
		}
		return true;
	}

	private boolean isHPMPConsume() {
		_mpConsume = _skill.getMpConsume();
		_hpConsume = _skill.getHpConsume();
		int currentMp = 0;
		int currentHp = 0;

		currentMp = _player.getCurrentMp();
		currentHp = _player.getCurrentHp();

		int intmpNum = _player.getAbility().getTotalInt() - 12;
		int skilllev = _skill.getSkillLevel() - 1;
		// int값에따른 mp소모량 감소
		if (intmpNum > 7) {
			intmpNum = 7;
		} else if (intmpNum <= 0) {
			intmpNum = 0;
		}
		if (skilllev > 9) {
			skilllev = 0;
		}
		_mpConsume -= intMP[intmpNum][skilllev];

		if (_player.isKnight()) {
			if (_player.getAbility().getTotalInt() > 12
					&& _skillId >= SHOCK_STUN && _skillId <= COUNTER_BARRIER) {
				_mpConsume -= (_player.getAbility().getTotalInt() - 12);
			}
		}

		if (_player.getBaseMagicDecreaseMp() > 0) {
			_mpConsume -= _player.getBaseMagicDecreaseMp();
		}

		if (0 < _skill.getMpConsume()) {
			_mpConsume = Math.max(_mpConsume, 1);
		}

		if (currentHp < _hpConsume + 1) {
			_player.sendPackets(new S_ServerMessage(279));
			return false;
		} else if (currentMp < _mpConsume) {
			_player.sendPackets(new S_ServerMessage(278));
			return false;
		}
		return true;
	}

	private void useConsume() {
		if (_skillId == FINAL_BURN) {
			_player.setCurrentHp(1);
			_player.setCurrentMp(0);
		} else {
			int current_hp = _player.getCurrentHp() - _hpConsume;
			_player.setCurrentHp(current_hp);

			int current_mp = _player.getCurrentMp() - _mpConsume;
			_player.setCurrentMp(current_mp);
		}

		int lawful = _player.getLawful() + _skill.getLawful();
		if (lawful > 32767) {
			lawful = 32767;
		}
		if (lawful < -32767) {
			lawful = -32767;
		}
		_player.setLawful(lawful);
	}

	private void setDelay() {
		if (_skill.getReuseDelay() > 0) {
			L1SkillDelay.onSkillUse(_player, _skill.getReuseDelay());
		}
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

	private boolean isUseCounterMagic(L1Character cha) {
		if (_isCounterMagic
				&& cha.getSkillEffectTimerSet().hasSkillEffect(COUNTER_MAGIC)) {
			cha.getSkillEffectTimerSet().removeSkillEffect(COUNTER_MAGIC);
			int castgfx = SkillsTable.getInstance().getTemplate(COUNTER_MAGIC)
					.getCastGfx();
			Broadcaster.broadcastPacket(cha, new S_SkillSound(cha.getId(),
					castgfx));
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
			}
			return true;
		}
		return false;
	}

	public void RunSkill() {
		try {
			int actionId = _skill.getActionId();
			int castgfx = _skill.getCastGfx();
			if (castgfx == 0) {
				return;
			}

			if (isPcSummonPet(_target)) {
				if (CharPosUtil.getZoneType(_player) == 1
						|| CharPosUtil.getZoneType(_target) == 1
						|| _player.checkNonPvP(_player, _target)) {
					_player.sendPackets(new S_UseAttackSkill(_player, 0,
							castgfx, _targetX, _targetY, actionId));
					Broadcaster.broadcastPacket(_player, new S_UseAttackSkill(
							_player, 0, castgfx, _targetX, _targetY, actionId));
					return;
				}
			}

			_player.sendPackets(new S_UseAttackSkill(_player, _targetID,
					castgfx, _targetX, _targetY, actionId));
			Broadcaster.broadcastPacket(_player, new S_UseAttackSkill(_player,
					_targetID, castgfx, _targetX, _targetY, actionId));
			Broadcaster.broadcastPacketExceptTargetSight(_target,
					new S_DoActionGFX(_targetID, ActionCodes.ACTION_Damage),
					_player);

			if (_skillId == 132 || _skillId == 187) {
				_isCounterMagic = false;
			}

			if (isUseCounterMagic(_target)) {
				return;
			}

			int dmg = 0;
			int drainMana = 0;
			int heal = 0;

			L1Magic _magic = new L1Magic(_player, _target);
			_magic.setLeverage(10);

			dmg = _magic.calcMagicDamage(_skillId);

			// 공격 스킬일때!! 이레이즈 여부 판멸후 제거
			if (_target.getSkillEffectTimerSet().hasSkillEffect(ERASE_MAGIC)) {
				_target.getSkillEffectTimerSet().removeSkillEffect(ERASE_MAGIC);
			}

			switch (_skillId) {
			case CHILL_TOUCH: // 칠터치
			case VAMPIRIC_TOUCH:// 뱀파
				heal = dmg;

				break;
			case TRIPLE_ARROW: { // 트리플
				int weaponType = _player.getWeapon().getItem().getType1();
				if (weaponType != 20)
					return;
				for (int i = 3; i > 0; i--) {
					_target.onAction(_player);
				}
				_player.sendPackets(new S_SkillSound(_player.getId(), 4394));
				Broadcaster.broadcastPacket(_player, new S_SkillSound(_player
						.getId(), 4394));
			}
				break;
			case FOU_SLAYER: { // 포우
				boolean gfxcheck = false;
				int[] FouGFX = { 138, 37, 3860, 3126, 3420, 2284, 3105, 3145,
						3148, 3151, 3871, 4125, 2323, 3892, 3895, 3898, 3901,
						4917, 4918, 4919, 4950, 6140, 6145, 6150, 6155, 6269,
						6272, 6275, 6278, 6826, 6827, 6836, 6837, 6846, 6847,
						6856, 6857, 6866, 6867, 6876, 6877, 6886, 6887, 6400,
						5645, 6399, 7039, 7040, 7041, 7140, 7144, 7148, 6160,
						7152, 7156, 7160, 7164, 7139, 7143, 7147, 7151, 7155,
						7159, 7163, 7959, 7968, 7969, 7970, 9214, 8817, 8774,
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
				_player.sendPackets(new S_SkillSound(_player.getId(), 7020));
				_player.sendPackets(new S_SkillSound(_targetID, 6509));
				Broadcaster.broadcastPacket(_player, new S_SkillSound(_player
						.getId(), 7020));
				Broadcaster.broadcastPacket(_player, new S_SkillSound(
						_targetID, 6509));

				if (_player.getSkillEffectTimerSet().hasSkillEffect(
						STATUS_SPOT1)) {
					dmg += 10;
					  _player.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_SPOT1);
				      _player.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 0)); //추가
				}
				if (_player.getSkillEffectTimerSet().hasSkillEffect(
						STATUS_SPOT2)) {
					dmg += 20;
					   _player.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_SPOT2);
					      _player.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 0)); //추가 
				}
				if (_player.getSkillEffectTimerSet().hasSkillEffect(
						STATUS_SPOT3)) {
					dmg += 30;
					_player.getSkillEffectTimerSet().killSkillEffectTimer(STATUS_SPOT3);
				      _player.sendPackets(new S_PacketBox(S_PacketBox.SPOT, 0)); //추가 
				}
			}
				break;
			}

			if ((_target instanceof L1TowerInstance || _target instanceof L1DoorInstance)
					&& dmg < 0) {
				dmg = 0;
			}

			if (heal > 0) {
				_player.setCurrentHp(heal + _player.getCurrentHp());
			}

			if (dmg != 0 || drainMana != 0) {
				_magic.commit(dmg, drainMana);
			}

			_magic = null;
		} catch (Exception e) {
			// 스킬 오류 발생 부분에 케릭터명, 몹명, 타켓명순으로 출력
			_log.log(Level.SEVERE, "AttackSkill[]Error", e);
		}
	}

}
