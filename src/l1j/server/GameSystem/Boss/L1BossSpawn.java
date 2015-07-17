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
package l1j.server.GameSystem.Boss;

/*
 * import java.util.logging.Level; import java.util.logging.Logger; import
 * java.util.Calendar; import java.util.Random; import
 * l1j.server.server.serverpackets.S_SystemMessage; //보스 전창알림
 * 
 * import l1j.server.Config; import l1j.server.server.GeneralThreadPool; import
 * l1j.server.server.templates.L1Npc;
 */

public class L1BossSpawn /* extends L1Spawn */{
	/*
	 * private static Logger _log =
	 * Logger.getLogger(L1BossSpawn.class.getName()); private static Random _rnd =
	 * new Random(System.nanoTime()); private int _spawnCount; private int
	 * _percentage; private String _cycleType; private L1BossCycle _cycle;
	 * private Calendar _activeSpawnTime;
	 * 
	 * private class SpawnTask implements Runnable { private int _spawnNumber;
	 * private int _objectId;
	 * 
	 * private SpawnTask(int spawnNumber, int objectId) { _spawnNumber =
	 * spawnNumber; _objectId = objectId; }
	 * 
	 * @Override public void run() { /////////////보스몹 멘트 띄우기////////
	 * L1world.getlnstance().broadcastPacketToAll( new
	 * S_SystemMessage("\\fYBOSS:"+getName() + "리스폰 되었습니다."));//수정 }} } }
	 * 
	 * public L1BossSpawn(L1Npc mobTemplate) throws SecurityException,
	 * ClassNotFoundException { super(mobTemplate); }
	 * 
	 * /** SpawnTask를 기동한다. @param spawnNumber L1Spawn로 관리되고 있는 번호. 홈 포인트가 없으면
	 * 무엇을 지정해도 좋다.
	 */
	/*
	 * @Override public void executeSpawnTask(int spawnNumber, int objectId) { //
	 * count를 감소 해 전부 죽었는지 체크 if (subAndGetCount() != 0) { return; // 전부 죽지 않다 } //
	 * 전회 출현 시간에 대해서, 다음의 출현 시간을 산출 Calendar spawnTime; Calendar now =
	 * Calendar.getInstance(); // 지금각 Calendar latestStart =
	 * _cycle.getLatestStartTime(now); // 지금각에 대한 최근의 주기의 개시 시간 Calendar
	 * activeStart = _cycle.getSpawnStartTime(_activeSpawnTime); // 액티브했던 주기의 개시
	 * 시간 // 액티브했던 주기의 개시 시간 >= 최근의 주기 개시 시간의 경우, 다음의 출현 if
	 * (!activeStart.before(latestStart)) { spawnTime =
	 * calcNextSpawnTime(activeStart); } else { // 액티브했던 주기의 개시 시간 < 최근의 주기 개시
	 * 시간의 경우는, 최근의 주기에 출현 // 알기 힘들지만 확률 계산하기 위해, 억지로 calcNextSpawnTime를 통하고 있다.
	 * latestStart.add(Calendar.SECOND, -1); spawnTime =
	 * calcNextSpawnTime(_cycle.getLatestStartTime(latestStart)); }
	 * spawnBoss(spawnTime, objectId); }
	 * 
	 * private synchronized int subAndGetCount() { return --_spawnCount; }
	 * 
	 * public void setCycleType(String type) { _cycleType = type; }
	 * 
	 * 
	 * public void setPercentage(int percentage) { _percentage = percentage; }
	 */
	/*
	 * @Override public void init() { if (_percentage <= 0) { return; } _cycle =
	 * L1BossCycle.getBossCycle(_cycleType); if (_cycle == null) { throw new
	 * RuntimeException(_cycleType + " not found"); } Calendar now =
	 * Calendar.getInstance(); Calendar spawnTime; // 출현 시간 if
	 * (Config.INIT_BOSS_SPAWN && _percentage > _rnd.nextInt(100)) { spawnTime =
	 * _cycle.calcSpawnTime(now); } else { spawnTime = calcNextSpawnTime(now); }
	 * spawnBoss(spawnTime, 0); }
	 */
	/*
	 * // 확률 계산해 다음의 출현 시간을 산출 private Calendar calcNextSpawnTime(Calendar cal) {
	 * do { cal = _cycle.nextSpawnTime(cal); } while (!(_percentage >
	 * _rnd.nextInt(100))); return cal; }
	 */

	// 지정된 시간에 보스 출현을 스케줄
	/*
	 * private void spawnBoss(Calendar spawnTime, int objectId) { // 이번 출현 시간을
	 * 보존해 둔다. 같은 글씨, 글귀가 다른 곳에도 지금에 사용. _activeSpawnTime = spawnTime; long
	 * delay = spawnTime.getTimeInMillis() - System.currentTimeMillis();
	 * 
	 * int cnt = _spawnCount; _spawnCount = getAmount(); while (cnt <
	 * getAmount()) { cnt++; GeneralThreadPool.getInstance().schedule(new
	 * SpawnTask(0, objectId), delay); } _log.log(Level.FINE, toString()); }
	 */

	/**
	 * 현재 액티브한 보스에 대한 주기와 출현 시간을 나타낸다.
	 */
	/*
	 * @Override public String toString() { StringBuilder builder = new
	 * StringBuilder(); builder.append("[MOB]npcid:" + getNpcId());
	 * builder.append(" name:" + getName()); builder.append("[Type]" +
	 * _cycle.getName()); builder.append("[현재의 주기]");
	 * builder.append(_cycle.getSpawnStartTime(_activeSpawnTime).getTime());
	 * builder.append(" - ");
	 * builder.append(_cycle.getSpawnEndTime(_activeSpawnTime).getTime());
	 * builder.append("[출현 시간]"); builder.append(_activeSpawnTime.getTime());
	 * return builder.toString(); }
	 */
}
