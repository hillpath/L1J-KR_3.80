package l1j.server.server.model;

import l1j.server.server.utils.IntRange;

public class BasicProperty {
	private String _name;

	private String _title;

	private int _level;

	private int _exp;

	private int _currentHp;

	private int _trueMaxHp;

	private short _maxHp;

	private int _currentMp;

	private int _trueMaxMp;

	private short _maxMp;

	private int _lawful;

	private int _karma;

	protected L1Character character;

	public BasicProperty(L1Character character) {
		this.character = character;
		_level = 1;
	}

	/**
	 * 캐릭터의 현재의 HP를 돌려준다.
	 * 
	 * @return 현재의 HP
	 */
	public int getCurrentHp() {
		return _currentHp;
	}

	/**
	 * 캐릭터의 HP를 설정한다.
	 * 
	 * @param i
	 *            캐릭터의 새로운 HP
	 */
	public void setCurrentHp(int i) {
		if (i >= getMaxHp()) {
			i = getMaxHp();
		}
		if (i < 0)
			i = 0;

		_currentHp = i;
	}

	/**
	 * 캐릭터의 현재의 MP를 돌려준다.
	 * 
	 * @return 현재의 MP
	 */
	public int getCurrentMp() {
		return _currentMp;
	}

	/**
	 * 캐릭터의 MP를 설정한다.
	 * 
	 * @param i
	 *            캐릭터의 새로운 MP
	 */
	public void setCurrentMp(int i) {
		if (i >= getMaxMp()) {
			i = getMaxMp();
		}
		if (i < 0)
			i = 0;

		_currentMp = i;
	}

	public synchronized int getExp() {
		return _exp;
	}

	public synchronized void setExp(int exp) {
		_exp = exp;
	}

	public String getName() {
		return _name;
	}

	public void setName(String s) {
		_name = s;
	}

	public synchronized int getLevel() {
		return _level;
	}

	public synchronized void setLevel(long level) {
		_level = (int) level;
	}

	public short getMaxHp() {
		return _maxHp;
	}

	public void addMaxHp(int i) {
		setMaxHp(_trueMaxHp + i);
	}

	public void setMaxHp(int hp) {
		_trueMaxHp = hp;
		_maxHp = (short) IntRange.ensure(_trueMaxHp, 1, 32767);
		_currentHp = Math.min(_currentHp, _maxHp);
	}

	public short getMaxMp() {
		return _maxMp;
	}

	public void setMaxMp(int mp) {
		_trueMaxMp = mp;
		_maxMp = (short) IntRange.ensure(_trueMaxMp, 0, 32767);
		_currentMp = Math.min(_currentMp, _maxMp);
	}

	public void addMaxMp(int i) {
		setMaxMp(_trueMaxMp + i);
	}

	public void healHp(int pt) {
		setCurrentHp(getCurrentHp() + pt);
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String s) {
		_title = s;
	}

	public int getLawful() {
		return _lawful;
	}

	public void setLawful(int i) {
		_lawful = i;
	}

	public int _oldlawful;

	public int getold_lawful() {
		return _oldlawful;
	}

	public void setold_lawful(int i) {
		_oldlawful = i;
	}

	public synchronized void addLawful(int i) {
		_lawful += i;
		if (_lawful > 32767) {
			_lawful = 32767;
		} else if (_lawful < -32768) {
			_lawful = -32768;
		}
	}

	public int _oldexp;

	public int getold_exp() {
		return _oldexp;
	}

	public void setold_exp(int i) {
		_oldexp = i;
	}

	/** 캐릭터의 업을 돌려준다. */
	public int getKarma() {
		return _karma;
	}

	/** 캐릭터의 업을 설정한다. */
	public void setKarma(int karma) {
		_karma = karma;
	}
}
