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
package l1j.server.server.datatables;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;
import static l1j.server.server.model.skill.L1SkillId.*;

public class CharBuffTable {
	private CharBuffTable() {
	}

	private static Logger _log = Logger
			.getLogger(CharBuffTable.class.getName());

	private static final int[] buffSkill = {
			999,
			2,
			67, // 드래곤진주,라이트, 셰이프 체인지
			3,
			99,
			151,
			159,
			168, // 쉴드(shield), 그림자 아모, 지구 스킨, 지구 호흡, 아이언 스킨
			43,
			54,
			1000,
			1001,
			STATUS_ELFBRAVE, // 헤이 파업, 그레이터 헤이 파업, 치우침 이브
			// 일부, 그린 일부, 엘븐 와퍼
			52,
			101,
			150, // 호-리 워크, 무빙 악 세레이션, 윈드워크
			26,
			42,
			109,
			110, // PE:DEX, PE:STR, 드레스마이티, 드레스데크스타리티
			114,
			115,
			117, // 그로윙오라, 샤이닝오라, 치우침 이브 아우라
			148,
			155,
			163, // 파이아웨폰, 파이어 호흡, 바닝웨폰
			149,
			156,
			166, // 윈도우 쇼트, 스토무아이, 스톰 쇼트
			1002,
			STATUS_CHAT_PROHIBITED, // 블루 일부, 채팅 금지
			/** 버프저장 패킷 차례 */
			DECREASE_WEIGHT, DECAY_POTION, SILENCE, VENOM_RESIST, WEAKNESS,
			DISEASE, DRESS_EVASION, BERSERKERS, NATURES_TOUCH, WIND_SHACKLE,
			ERASE_MAGIC, ADDITIONAL_FIRE, ELEMENTAL_FALL_DOWN, ELEMENTAL_FIRE,
			STRIKER_GALE, SOUL_OF_FLAME, POLLUTE_WATER, STATUS_TIKAL_BOSSDIE,
			CONCENTRATION, INSIGHT, PANIC, MORTAL_BODY, HORROR_OF_DEATH, FEAR,
			PATIENCE, GUARD_BREAK, DRAGON_SKIN,

			/** 요정 전용 버프 */
			RESIST_MAGIC, CLEAR_MIND, RESIST_ELEMENTAL, ELEMENTAL_PROTECTION,

			/** 컬러풀 패키지 아이템 및 천상의 물약 */
			EXP_POTION, STATUS_BLUE_POTION2, STATUS_FRUIT, STATUS_CASHSCROLL,
			STATUS_CASHSCROLL2, STATUS_CASHSCROLL3, STATUS_COMA_3,
			STATUS_COMA_5,EXP_POTION2,EXP_POTION3,

			/** 용기사 버프 **/
			BLOOD_LUST,
			/** 스페셜 요리 */
			SPECIAL_COOKING,
			/** 드래곤에메랄드   - playeva.net */
			DRAGON_EMERALD_NO, DRAGON_EMERALD_YES,
			/** 드래곤에메랄드   - playeva.net */
			STATUS_BLUE_POTION3,
			STATUS_LUCK_A,
			STATUS_LUCK_B,
			STATUS_LUCK_C,
			STATUS_LUCK_D,
			/** 요리 1단계 효과 재부여 */
			COOKING_1_0_N,
			COOKING_1_0_S,
			COOKING_1_1_N,
			COOKING_1_1_S, // 요리
			COOKING_1_2_N, COOKING_1_2_S, COOKING_1_3_N, COOKING_1_3_S,
			COOKING_1_4_N, COOKING_1_4_S, COOKING_1_5_N, COOKING_1_5_S,
			COOKING_1_6_N, COOKING_1_6_S,

			/** 요리 2단계 효과 재부여 */
			COOKING_1_8_N, COOKING_1_8_S, COOKING_1_9_N, COOKING_1_9_S,
			COOKING_1_10_N, COOKING_1_10_S, COOKING_1_11_N, COOKING_1_11_S,
			COOKING_1_12_N, COOKING_1_12_S, COOKING_1_13_N, COOKING_1_13_S,
			COOKING_1_14_N, COOKING_1_14_S,
			/** 요리 3단계 효과 재부여 */
			COOKING_1_16_N, COOKING_1_16_S, COOKING_1_17_N, COOKING_1_17_S,
			COOKING_1_18_N, COOKING_1_18_S, COOKING_1_19_N, COOKING_1_19_S,
			COOKING_1_20_N, COOKING_1_20_S, COOKING_1_21_N, COOKING_1_21_S,
			COOKING_1_22_N, COOKING_1_22_S,
			
			
			COOKING_NEW_1,COOKING_NEW_2,COOKING_NEW_3,COOKING_NEW_4,
			/**마안버프재부여**/
            ANTA_MAAN, FAFU_MAAN, LIND_MAAN, 
            VALA_MAAN, BIRTH_MAAN, SHAPE_MAAN, 
            LIFE_MAAN, MAAN_TIMER,

			/** 린드레이드혈흔 버프 재부여 */
			DRAGONBLOOD_ANTA, DRAGONBLOOD_PAP, DRAGONBLOOD_RIND };

	private static void StoreBuff(int objId, int skillId, int time, int polyId) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO character_buff SET char_obj_id=?, skill_id=?, remaining_time=?, poly_id=?");
			pstm.setInt(1, objId);
			pstm.setInt(2, skillId);
			pstm.setInt(3, time);
			pstm.setInt(4, polyId);
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "CharBuffTable[:StoreBuff:]Error", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public static void DeleteBuff(L1PcInstance pc) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("DELETE FROM character_buff WHERE char_obj_id=?");
			pstm.setInt(1, pc.getId());
			pstm.execute();
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "CharBuffTable[:DeleteBuff:]Error", e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
	}

	public static void SaveBuff(L1PcInstance pc) {
		for (int skillId : buffSkill) {
			int timeSec = pc.getSkillEffectTimerSet().getSkillEffectTimeSec(
					skillId);
			if (0 < timeSec) {
				int polyId = 0;
				if (skillId == SHAPE_CHANGE) {
					polyId = pc.getGfxId().getTempCharGfx();
				}
				StoreBuff(pc.getId(), skillId, timeSec, polyId);
			}
		}
	}

}
