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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javolution.util.FastMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import static l1j.server.server.ActionCodes.*;
import l1j.server.server.utils.SQLUtil;

public class SprTable {

	private static Logger _log = Logger.getLogger(SprTable.class.getName());

	private static class Spr {
		private final FastMap<Integer, Integer> moveSpeed = new FastMap<Integer, Integer>();

		private final FastMap<Integer, Integer> attackSpeed = new FastMap<Integer, Integer>();
		
		private final FastMap<Integer, Integer> specialSpeed = new FastMap<Integer, Integer>();//가속기 수정및 외부화

		private int nodirSpellSpeed = 1200;

		private int dirSpellSpeed = 1200;
	}

	private static final FastMap<Integer, Spr> _dataMap = new FastMap<Integer, Spr>();

	private static final SprTable _instance = new SprTable();

	private SprTable() {
		loadSprAction();
	}

	public static SprTable getInstance() {
		return _instance;
	}

	/**
	 * spr_action 테이블을 로드한다.
	 */
	public void loadSprAction() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Spr spr = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spr_action");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int key = rs.getInt("spr_id");
				if (!_dataMap.containsKey(key)) {
					spr = new Spr();
					_dataMap.put(key, spr);
				} else {
					spr = _dataMap.get(key);
				}

				int actid = rs.getInt("act_id");
				int frameCount = rs.getInt("framecount");
				int frameRate = rs.getInt("framerate");
				int speed = calcActionSpeed(frameCount, frameRate);

				switch (actid) {
				case ACTION_Walk:
				case ACTION_SwordWalk:
				case ACTION_AxeWalk:
				case ACTION_BowWalk:
				case ACTION_SpearWalk:
				case ACTION_StaffWalk:
				case ACTION_DaggerWalk:
				case ACTION_TwoHandSwordWalk:
				case ACTION_EdoryuWalk:
				case ACTION_ClawWalk:
				case ACTION_ThrowingKnifeWalk:
					spr.moveSpeed.put(actid, speed);
					break;
				case ACTION_SkillAttack:
					spr.dirSpellSpeed = speed;
					break;
				case ACTION_SkillBuff:
					spr.nodirSpellSpeed = speed;
					break;
				case ACTION_Attack:
				case ACTION_SwordAttack:
				case ACTION_AxeAttack:
				case ACTION_BowAttack:
				case ACTION_SpearAttack:
			    case ACTION_AltAttack://가속기 수정및 외부화 
			    case ACTION_SpellDirectionExtra://가속기 수정및 외부화 
				case ACTION_StaffAttack:
				case ACTION_DaggerAttack:
				case ACTION_TwoHandSwordAttack:
				case ACTION_EdoryuAttack:
				case ACTION_ClawAttack:
				case ACTION_ThrowingKnifeAttack:
					spr.attackSpeed.put(actid, speed);
//	가속기 수정및 외부화
					break;
		        case ACTION_Think:
		        case ACTION_Aggress:
		             spr.specialSpeed.put(actid, speed);
		             break;
//가속기 수정및 외부화
				default:
					break;
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.config("SPR 데이터 " + _dataMap.size() + "건 로드");
	}

	/**
	 * 프레임수와 frame rate로부터 액션의 합계 시간(ms)을 계산해 돌려준다.
	 */
	private int calcActionSpeed(int frameCount, int frameRate) {
		return (int) (frameCount * 40 * (24D / frameRate));
	}

	/**
	 * 지정된 spr의 공격 속도를 돌려준다. 만약 spr로 지정된 weapon_type의 데이터가 설정되어 있지 않은 경우는, 1.
	 * attack의 데이터를 돌려준다.
	 * 
	 * @param sprid -
	 *            조사하는 spr의 ID
	 * @param actid -
	 *            무기의 종류를 나타내는 값. L1Item.getType1()의 변환값 +1과 일치한다
	 * @return 지정된 spr의 공격 속도(ms)
	 */
	public int getAttackSpeed(int sprid, int actid) {
		if (_dataMap.containsKey(sprid)) {
			if (_dataMap.get(sprid).attackSpeed.containsKey(actid)) {
				return _dataMap.get(sprid).attackSpeed.get(actid);
			} else if (actid == ACTION_Attack) {
				return 0;
			} else {
				return _dataMap.get(sprid).attackSpeed.get(ACTION_Attack);
			}
		}
		return 0;
	}

	public int getMoveSpeed(int sprid, int actid) {
		if (_dataMap.containsKey(sprid)) {
			if (_dataMap.get(sprid).moveSpeed.containsKey(actid)) {
				return _dataMap.get(sprid).moveSpeed.get(actid);
			} else if (actid == ACTION_Walk) {
				return 0;
			} else {
				return _dataMap.get(sprid).moveSpeed.get(ACTION_Walk);
			}
		}
		return 0;
	}

	public int getDirSpellSpeed(int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid).dirSpellSpeed;
		}
		return 0;
	}

	public int getNodirSpellSpeed(int sprid) {
		if (_dataMap.containsKey(sprid)) {
			return _dataMap.get(sprid).nodirSpellSpeed;
		}
		return 0;
	}
	public int getSpecialSpeed(int sprid, int actid) {
	    if (_dataMap.containsKey(sprid)) {
	      if (_dataMap.get(sprid).specialSpeed.containsKey(actid)) {
	        return _dataMap.get(sprid).specialSpeed.get(actid);
	      }
	      else {
	        return 1200;
	      }
	    }
	    return 0;
	  }
//	가속기 수정및 외부화
	public int getSprSpeed(int sprid, int actid) {
	    switch (actid) {
	      case ACTION_Walk:
	      case ACTION_SwordWalk:
	      case ACTION_AxeWalk:
	      case ACTION_BowWalk:
	      case ACTION_SpearWalk:
	      case ACTION_StaffWalk:
	      case ACTION_DaggerWalk:
	      case ACTION_TwoHandSwordWalk:
	      case ACTION_EdoryuWalk:
	      case ACTION_ClawWalk:
	      case ACTION_ThrowingKnifeWalk:
	           return getMoveSpeed(sprid, actid);
	      case ACTION_SkillAttack:
	           return getDirSpellSpeed(sprid);
	      case ACTION_SkillBuff:
	           return getNodirSpellSpeed(sprid);
	      case ACTION_Attack:
	      case ACTION_SwordAttack:
	      case ACTION_AxeAttack:
	      case ACTION_BowAttack:
	      case ACTION_SpearAttack:
	      case ACTION_AltAttack:
	      case ACTION_SpellDirectionExtra:
	      case ACTION_StaffAttack:
	      case ACTION_DaggerAttack:
	      case ACTION_TwoHandSwordAttack:
	      case ACTION_EdoryuAttack:
	      case ACTION_ClawAttack:
	      case ACTION_ThrowingKnifeAttack:
	           return getAttackSpeed(sprid, actid);
	      case ACTION_Think:
	      case ACTION_Aggress:
	           return getSpecialSpeed(sprid, actid);
	      default:
	        break;
	    }
	    return 0;
	  }
	} 
//가속기 수정및 외부화

