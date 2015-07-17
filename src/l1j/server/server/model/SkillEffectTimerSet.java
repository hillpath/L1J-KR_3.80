package l1j.server.server.model;

import java.util.Collection;
import javolution.util.FastMap;
import java.util.Map;

import l1j.server.server.model.skill.L1SkillTimer;
import l1j.server.server.model.skill.L1SkillTimerCreator;

public class SkillEffectTimerSet {
	private final Map<Integer, L1SkillTimer> _skillEffect = new FastMap<Integer, L1SkillTimer>();

	private L1Character cha;

	public SkillEffectTimerSet(L1Character cha) {
		this.cha = cha;
	}

	/**
	 * 캐릭터에, 새롭게 스킬 효과를 추가한다.
	 * 
	 * @param skillId
	 *            추가하는 효과의 스킬 ID.
	 * @param timeMillis
	 *            추가하는 효과의 지속 시간. 무한의 경우는 0.
	 */
	private void addSkillEffect(int skillId, int timeMillis) {
		L1SkillTimer timer = null;
		if (0 < timeMillis) {
			timer = L1SkillTimerCreator.create(cha, skillId, timeMillis);
			timer.begin();
		}
		_skillEffect.put(skillId, timer);
	}

	/**
	 * 캐릭터에, 스킬 효과를 설정한다. <br>
	 * 중복 하는 스킬이 없는 경우는, 새롭게 스킬 효과를 추가한다. <br>
	 * 중복 하는 스킬이 있는 경우는, 나머지 효과 시간과 파라미터의 효과 시간의 긴 (분)편을 우선해 설정한다.
	 * 
	 * @param skillId
	 *            설정하는 효과의 스킬 ID.
	 * @param timeMillis
	 *            설정하는 효과의 지속 시간. 무한의 경우는 0.
	 */
	public void setSkillEffect(int skillId, int timeMillis) {
		if (hasSkillEffect(skillId)) {
			int remainingTimeMills = getSkillEffectTimeSec(skillId) * 1000;

			if (remainingTimeMills >= 0
					&& (remainingTimeMills < timeMillis || timeMillis == 0)) {
				killSkillEffectTimer(skillId);
				addSkillEffect(skillId, timeMillis);
			}
		} else {
			addSkillEffect(skillId, timeMillis);
		}
	}

	/**
	 * 캐릭터로부터, 스킬 효과를 삭제한다.
	 * 
	 * @param skillId
	 *            삭제하는 효과의 스킬 ID
	 */
	public void removeSkillEffect(int skillId) {
		L1SkillTimer timer = _skillEffect.remove(skillId);
		if (timer != null) {
			timer.end();
		}
	}

	/**
	 * 캐릭터로부터, 스킬 효과의 타이머를 삭제한다. 스킬 효과는 삭제되지 않는다.
	 * 
	 * @param skillId
	 *            삭제하는 타이머의 스킬 ID
	 */
	public void killSkillEffectTimer(int skillId) {
		L1SkillTimer timer = _skillEffect.remove(skillId);
		if (timer != null) {
			timer.kill();
		}
	}

	/**
	 * 캐릭터로부터, 모든 스킬 효과 타이머를 삭제한다. 스킬 효과는 삭제되지 않는다.
	 */
	public void clearSkillEffectTimer() {
		Collection<L1SkillTimer> list = _skillEffect.values();
		for (L1SkillTimer timer : list) {
			if (timer != null) {
				timer.kill();
			}
		}
	}

	/**
	 * 캐릭터에, 해당 스킬 효과가 걸려있는지 알려줌
	 * 
	 * @param skillId
	 *            스킬 ID
	 * @return 마법 효과가 있으면 true, 없으면 false.
	 */
	public boolean hasSkillEffect(int skillId) {
		return _skillEffect.containsKey(skillId);
	}

	/**
	 * 캐릭터의 스킬 효과의 지속 시간을 돌려준다.
	 * 
	 * @param skillId
	 *            조사하는 효과의 스킬 ID
	 * @return 스킬 효과의 남은 시간(초). 스킬이 걸리지 않은가 효과 시간이 무한의 경우,-1.
	 */
	public int getSkillEffectTimeSec(int skillId) {
		L1SkillTimer timer = _skillEffect.get(skillId);
		if (timer == null) {
			return -1;
		}
		return timer.getRemainingTime();
	}

}
