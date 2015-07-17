/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1BuffUtil;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Skills;

public class L1AllBuff implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1AllBuff.class.getName());

	private L1AllBuff() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AllBuff();
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		int[] allBuffSkill = { LIGHT, DECREASE_WEIGHT, PHYSICAL_ENCHANT_DEX,
				MEDITATION, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, BERSERKERS,
				IMMUNE_TO_HARM, ADVANCE_SPIRIT, REDUCTION_ARMOR, BOUNCE_ATTACK,
				SOLID_CARRIAGE, ENCHANT_VENOM, BURNING_SPIRIT, VENOM_RESIST,
				DOUBLE_BRAKE, UNCANNY_DODGE, DRESS_EVASION, GLOWING_AURA,
				BRAVE_AURA, RESIST_MAGIC, CLEAR_MIND, ELEMENTAL_PROTECTION,
				AQUA_PROTECTER, BURNING_WEAPON, IRON_SKIN, EXOTIC_VITALIZE,
				WATER_LIFE, ELEMENTAL_FIRE, SOUL_OF_FLAME, ADDITIONAL_FIRE };
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String name = st.nextToken();
			L1PcInstance target = L1World.getInstance().getPlayer(name);
			/* if (!pc.getName().equalsIgnoreCase("메티스")) {
				 pc.sendPackets(new S_SystemMessage("부운영자는 제한되는 기능입니다."));
					return;
				}*/
			if (target == null) {
				pc.sendPackets(new S_ServerMessage(73, name)); // \f1%0은 게임을 하고
				// 있지 않습니다.
				return;
			}

			L1BuffUtil.haste(target, 3600 * 1000);
			L1BuffUtil.brave(target, 3600 * 1000);
			L1PolyMorph.doPoly(target, 5641, 7200, L1PolyMorph.MORPH_BY_GM);
			L1Skills skill = null;
			for (int i = 0; i < allBuffSkill.length; i++) {
				skill = SkillsTable.getInstance().getTemplate(allBuffSkill[i]);
				new L1SkillUse().handleCommands(target, allBuffSkill[i], target
						.getId(), target.getX(), target.getY(), null, skill
						.getBuffDuration(), L1SkillUse.TYPE_GMBUFF);
			}
			pc.sendPackets(new S_SystemMessage(cmdName + "에게 올버프를 줬습니다. "));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(".올버프 [캐릭터명]으로 입력해 주세요. "));
		}
	}
}
