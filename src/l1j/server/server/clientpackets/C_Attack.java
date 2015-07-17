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

import static l1j.server.server.model.Instance.L1PcInstance.REGENSTATE_ATTACK;
import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.TimeController.FishingTimeController;
import l1j.server.server.model.AcceleratorChecker;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1EventTowerInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1LittleBugInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_UseArrowSkill;
import server.LineageClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Attack extends ClientBasePacket {

	private int _targetX = 0;

	private int _targetY = 0;

	public C_Attack(byte[] decrypt, LineageClient client) {
		super(decrypt);
		int targetId = readD();
		int x = readH();
		int y = readH();
		_targetX = x;
		_targetY = y;

		L1PcInstance pc = client.getActiveChar();
		if(pc == null)
			return;
		L1Object target = L1World.getInstance().findObject(targetId);

		if (pc.isGhost() || pc.isDead() || pc.isTeleport()) { return; }
		if (pc.isInvisble()) { return; }
		if (pc.isInvisDelay()) { return; }
		
		if (pc.getMapId() == 350) {
			pc.sendPackets(new S_SystemMessage("시장 안에서는 공격이 불가능합니다."));
			return;
		}
		if (pc.getInventory().getWeight240() >= 200) {
			pc.sendPackets(new S_ServerMessage(110)); // \f1아이템이 너무 무거워 전투할 수가
			// 없습니다.
			return;
		}
				if (target instanceof L1Character) {
			if (target.getMapId() != pc.getMapId()
					|| pc.getLocation().getLineDistance(target.getLocation()) > 20D) { // 타겟이 이상한 장소에 있으면(자) 종료
				return;
			}
		}
		if (target != null) {
			//미니공성전
			if(pc.get_MiniDuelLine() == 1){
				if (target instanceof L1EventTowerInstance){
					if(((L1EventTowerInstance) target).getNpcId() == 6100008){
					return;
					}
				}
			}
			if(pc.get_MiniDuelLine() == 2){
				if (target instanceof L1EventTowerInstance){
					if(((L1EventTowerInstance) target).getNpcId() == 6100009){
					return;
					}
				}
			}
			
			if (target instanceof L1LittleBugInstance) {
				return;
			}
			if (target instanceof L1NpcInstance) {
				int hiddenStatus = ((L1NpcInstance) target).getHiddenStatus();
				if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK
						|| hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY) {
					return;
				}
			}
		}
		  /**
		   *  @혈맹보호 공격못하게하는부분 
		   */
		  /*if(target instanceof L1PcInstance){
		   L1PcInstance _target = (L1PcInstance) target;
		   if(pc.getProtect() == true && _target.getClanid() == pc.getClanid()){
		    return;
		   }
			
		   
		   if(_target.getLevel() < Config.MAX_LEVEL || pc.getLevel() < Config.MAX_LEVEL){ //신규보호
			 return;
           }
		  }*/
		  if (Config.CHECK_ATTACK_INTERVAL) {//가속기 수정및 외부화
			    int result;
			    result = pc.getAcceleratorChecker().checkInterval(AcceleratorChecker.ACT_TYPE.ATTACK);
			    if (result == AcceleratorChecker.R_DISCONNECTED) {        
			      return;
			    }
			  }
		/** 낚시 중일 경우 취소 안되고 공격 가능시 * */
		if (pc.isFishing()) {
			try {
				pc.setFishing(false);
				pc.setFishingTime(0);
				pc.setFishingReady(false);
				pc.sendPackets(new S_CharVisualUpdate(pc));
				Broadcaster.broadcastPacket(pc, new S_CharVisualUpdate(pc));
				FishingTimeController.getInstance().removeMember(pc);
			} catch (Exception e) {
			}
		}
		// 공격 액션을 취할 수 있는 경우의 처리
		if (pc.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.ABSOLUTE_BARRIER)) { // 아브소르트바리아의
			// 해제
			pc.getSkillEffectTimerSet().killSkillEffectTimer(
					L1SkillId.ABSOLUTE_BARRIER);
			pc.startHpRegenerationByDoll();
			pc.startMpRegenerationByDoll();
			/**안전모드**/
			if(pc.Safe_Teleport){
				pc.sendPackets(new S_SystemMessage("안전 모드가 해제 되었습니다."));
				pc.Safe_Teleport = false;
			}
			/**안전모드**/
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.MEDITATION)) {
			pc.getSkillEffectTimerSet().killSkillEffectTimer(
					L1SkillId.MEDITATION);
		}

		pc.delInvis();
		pc.setRegenState(REGENSTATE_ATTACK);
		boolean ck = false;
		if (target != null)
			ck = !((L1Character) target).isDead();

		if (target != null && ck) {
			target.onAction(pc);
		} else { // 하늘 공격
			// TODO 활로 지면에 하늘 공격했을 경우는 화살이 날지 않으면 안 된다
			int weaponId = 0;
			int weaponType = 0;
			L1ItemInstance weapon = pc.getWeapon();
			L1ItemInstance arrow = null;
			L1ItemInstance sting = null;
			if (weapon != null) {
				weaponId = weapon.getItem().getItemId();
				weaponType = weapon.getItem().getType1();
				if (weaponType == 20) {
					arrow = pc.getInventory().getArrow();
				}
				if (weaponType == 62) {
					sting = pc.getInventory().getSting();
				}
			}
			pc.getMoveState().setHeading(CharPosUtil.targetDirection(pc, x, y)); // 방향세트
			if (weaponType == 20
					&& (weaponId == 190
							|| (weaponId >= 11011 && weaponId <= 11013) || arrow != null)) { // 추가
				calcOrbit(pc.getX(), pc.getY(), pc.getMoveState().getHeading());
				if (arrow != null) {
					if (pc.getGfxId().getTempCharGfx() == 7968) {
						pc.sendPackets(new S_UseArrowSkill(pc, 0, 7972,
								_targetX, _targetY, true));
						Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc,
								0, 7972, _targetX, _targetY, true));
					} else if (pc.getGfxId().getTempCharGfx() == 8842) {
						pc.sendPackets(new S_UseArrowSkill(pc, 0, 8904,
								_targetX, _targetY, true));
						Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc,
								0, 8904, _targetX, _targetY, true));
					} else if (pc.getGfxId().getTempCharGfx() == 8845) {
						pc.sendPackets(new S_UseArrowSkill(pc, 0, 8916,
								_targetX, _targetY, true));
						Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc,
								0, 8916, _targetX, _targetY, true));

					} else {
						pc.sendPackets(new S_UseArrowSkill(pc, 0, 66, _targetX,
								_targetY, true));
						Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc,
								0, 66, _targetX, _targetY, true));
					}
					pc.getInventory().removeItem(arrow, 1);
				} else if (weaponId == 190) {
					pc.sendPackets(new S_UseArrowSkill(pc, 0, 2349, _targetX,
							_targetY, true));
					Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0,
							2349, _targetX, _targetY, true));
				} else if (weaponId >= 11011 && weaponId <= 11013) {
					pc.sendPackets(new S_UseArrowSkill(pc, 0, 8771, _targetX,
							_targetY, true));
					Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0,
							8771, _targetX, _targetY, true));
				} // 추가
			} else if (weaponType == 62 && sting != null) {
				calcOrbit(pc.getX(), pc.getY(), pc.getMoveState().getHeading());
				if (pc.getGfxId().getTempCharGfx() == 7968) {
					pc.sendPackets(new S_UseArrowSkill(pc, 0, 7972, _targetX,
							_targetY, true));
					Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0,
							7972, _targetX, _targetY, true));
				} else if (pc.getGfxId().getTempCharGfx() == 8842) {
					pc.sendPackets(new S_UseArrowSkill(pc, 0, 8904, _targetX,
							_targetY, true));
					Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0,
							8904, _targetX, _targetY, true));
				} else if (pc.getGfxId().getTempCharGfx() == 8845) {
					pc.sendPackets(new S_UseArrowSkill(pc, 0, 8916, _targetX,
							_targetY, true));
					Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0,
							8916, _targetX, _targetY, true));
				} else {
					pc.sendPackets(new S_UseArrowSkill(pc, 0, 2989, _targetX,
							_targetY, true));
					Broadcaster.broadcastPacket(pc, new S_UseArrowSkill(pc, 0,
							2989, _targetX, _targetY, true));
				}
				pc.getInventory().removeItem(sting, 1);
			} else {
				pc.sendPackets(new S_AttackPacket(pc, 0,
						ActionCodes.ACTION_Attack));
				Broadcaster.broadcastPacket(pc, new S_AttackPacket(pc, 0,
						ActionCodes.ACTION_Attack));
			}
		}
		if (pc.getWeapon().getItem().getType() == 17||pc.getWeapon().getItem().getType() == 19) {
			if (pc.getWeapon().getItemId() == 410003) {
				pc.sendPackets(new S_SkillSound(pc.getId(), 6983));
				Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(),
						6983));
			} else {
				pc.sendPackets(new S_SkillSound(pc.getId(), 7049));
				Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(),
						7049));
			}
		}
	}

	private void calcOrbit(int cX, int cY, int head) {
		final byte HEADING_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
		final byte HEADING_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };
		float disX = Math.abs(cX - _targetX);
		float disY = Math.abs(cY - _targetY);
		float dis = Math.max(disX, disY);
		float avgX = 0;
		float avgY = 0;

		if (dis == 0) {
			avgX = HEADING_X[head];
			avgY = HEADING_Y[head];
		} else {
			avgX = disX / dis;
			avgY = disY / dis;
		}

		int addX = (int) Math.floor((avgX * 15) + 0.59f);
		int addY = (int) Math.floor((avgY * 15) + 0.59f);

		if (cX > _targetX) {
			addX *= -1;
		}
		if (cY > _targetY) {
			addY *= -1;
		}

		_targetX = _targetX + addX;
		_targetY = _targetY + addY;
	}
}
