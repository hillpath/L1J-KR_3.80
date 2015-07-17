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

import static l1j.server.server.model.skill.L1SkillId.*;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.AcceleratorChecker;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import server.LineageClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_UseSkill extends ClientBasePacket {

	public C_UseSkill(byte abyte0[], LineageClient client) throws Exception {
		super(abyte0);
		try {
			int row = readC();
			int column = readC();
			int skillId = (row * 8) + column + 1;
			String charName = null;
			String message = null;
			int targetId = 0;
			int targetX = 0;
			int targetY = 0;
			L1PcInstance pc = client.getActiveChar();
			if (pc == null) {
				return;
			}
L1Object target2 = L1World.getInstance().findObject(targetId); // 추가
  

			if (pc.isTeleport() || pc.isDead()) {
				return;
			}
			if (!pc.isSkillMastery(skillId)) {
				return;
			}
			if (!pc.getMap().isUsableSkill()) {
				pc.sendPackets(new S_ServerMessage(563)); // \f1 여기에서는 사용할 수
															// 없습니다.
				return;
			}
		if (target2 instanceof L1Character) { // 추가
			if (target2.getMapId() != pc.getMapId()
					|| pc.getLocation().getLineDistance(target2.getLocation()) > 20D) { // 타겟이 이상한 장소에 있으면 종료
				return;
			}
		}
			if (pc.getMapId() == 350) {
				pc.sendPackets(new S_SystemMessage("시장 안에서는 마법이 불가능합니다."));
				return;
			}
			// 요구 간격을 체크한다
			if (Config.CHECK_SPELL_INTERVAL && !(skillId == FOU_SLAYER)) {
				int result;
				// FIXME 어느 스킬이 dir/no dir일까의 판단이 적당
				if (SkillsTable.getInstance().getTemplate(skillId)
						.getActionId() == ActionCodes.ACTION_SkillAttack) {
					result = pc.getAcceleratorChecker().checkInterval(
							AcceleratorChecker.ACT_TYPE.SPELL_DIR);
				} else {
					result = pc.getAcceleratorChecker().checkInterval(
							AcceleratorChecker.ACT_TYPE.SPELL_NODIR);
				}
				if (result == AcceleratorChecker.R_DISCONNECTED) {
					return;
				}
			}
			

			if (skillId == CUBE_IGNITION) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(CUBE_IGNITION)) {
					pc.sendPackets(new S_ServerMessage(1412));
					return;
				}
			}
			if (skillId == CUBE_QUAKE) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(CUBE_QUAKE)) {
					pc.sendPackets(new S_ServerMessage(1412));
					return;
				}
			}
			if (skillId == CUBE_SHOCK) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(CUBE_SHOCK)) {
					pc.sendPackets(new S_ServerMessage(1412));
					return;
				}
			}
			if (skillId == CUBE_BALANCE) {
				if (pc.getSkillEffectTimerSet().hasSkillEffect(CUBE_BALANCE)) {
					pc.sendPackets(new S_ServerMessage(1412));
					return;
				}
			}

			if (abyte0.length > 4) {
				try {
					switch (skillId) {
					case CALL_CLAN:
					case RUN_CLAN:
						charName = readS();
						break;
					case TRUE_TARGET:
						targetId = readD();
						targetX = readH();
						targetY = readH();
						message = readS();
						break;
					case TELEPORT:
					case MASS_TELEPORT:
						//readH(); // MapID
						//targetId = readD(); // Bookmark ID
						targetId = readH();
					      targetX = readH();
					      targetY = readH();
						break;
					case FIRE_WALL:
					case LIFE_STREAM:
						targetX = readH();
						targetY = readH();
						break;
					default:
						targetId = readD();
						targetX = readH();
						targetY = readH();
						break;
					}
				} catch (Exception e) {
					// _log.log(Level.SEVERE, "", e);
				}
			}

			if (pc.getSkillEffectTimerSet().hasSkillEffect(ABSOLUTE_BARRIER)) { // 아브소르트바리아의
																				// 해제
				pc.getSkillEffectTimerSet().killSkillEffectTimer(
						ABSOLUTE_BARRIER);
				pc.startHpRegenerationByDoll();
				pc.startMpRegenerationByDoll();
				/**안전모드**/
				if(pc.Safe_Teleport){
					pc.sendPackets(new S_SystemMessage("안전 모드가 해제 되었습니다."));
					pc.Safe_Teleport = false;
				}
				/**안전모드**/
			}

			pc.getSkillEffectTimerSet().killSkillEffectTimer(MEDITATION);

			try {
				if (skillId == CALL_CLAN || skillId == RUN_CLAN) {
					if (charName.isEmpty()) {
						return;
					}

					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < charName.length(); i++) {
						if (charName.charAt(i) == '[') {
							break;
						}
						sb.append(charName.charAt(i));
					}

					L1PcInstance target = L1World.getInstance().getPlayer(
							sb.toString());

					if (target == null) {
						pc.sendPackets(new S_ServerMessage(73, charName));
						return;
					}
					if (pc.getClanid() != target.getClanid()) {
						pc.sendPackets(new S_ServerMessage(414));
						return;
					}
					targetId = target.getId();
					if (skillId == CALL_CLAN) {
						int callClanId = pc.getCallClanId();
						if (callClanId == 0 || callClanId != targetId) {
							pc.setCallClanId(targetId);
							pc.setCallClanHeading(pc.getMoveState()
									.getHeading());
						}
					}
				}

				L1SkillUse l1skilluse = new L1SkillUse();
				l1skilluse.handleCommands(pc, skillId, targetId, targetX,
						targetY, message, 0, L1SkillUse.TYPE_NORMAL);
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}
	}
}
