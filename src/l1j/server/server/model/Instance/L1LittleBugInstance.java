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
package l1j.server.server.model.Instance;

import java.util.Random;
import javolution.util.FastTable;

import l1j.server.server.ObjectIdFactory;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.templates.L1Npc;

public class L1LittleBugInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;

	private static final Random _random = new Random();

	private static final int FIRST_NAMEID = 1213;

	private static final int[] gfxId = { 3478, 3479, 3480, 3481, 3497, 3498,
			3499, 3500, 3501, 3502, 3503, 3504, 3505, 3506, 3507, 3508, 3509,
			3510, 3511, 3512 

	};

	public static final int GOOD = 0;

	public static final int NORMAL = 1;

	public static final int BAD = 2;

	public L1LittleBugInstance(L1Npc template, int num, int x, int y) {
		super(template);
		setId(ObjectIdFactory.getInstance().nextId());
		setName("$" + (FIRST_NAMEID + num));
		setNameId("#" + (num + 1) + " $" + (FIRST_NAMEID + num));
		gfx.setTempCharGfx(gfxId[num]);
		gfx.setGfxId(gfxId[num]);
		setLocation(x, y, 4);
		getMoveState().setHeading(6);
		setCondition(_random.nextInt(3));
		setNumber(num);
		L1World.getInstance().storeObject(this);
		L1World.getInstance().addVisibleObject(this);
		FastTable<L1PcInstance> list = null;
		list = L1World.getInstance().getRecognizePlayer(this);
		for (L1PcInstance pc : list) {
			if (pc != null)
				onPerceive(pc);
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.getNearObjects().addKnownObject(this);
		perceivedFrom.sendPackets(new S_NPCPack(this));
	}

	private int _number; // ÃâÀü¹øÈ£

	public void setNumber(int i) {
		_number = i;
	}

	public int getNumber() {
		return _number;
	}
	
	public int winRate(){
		if(_number == 0) return 0;
		else return (int)(_win * 100 / _number);
	}

	private int _condition; // »óÅÂ

	public void setCondition(int i) {
		_condition = i;
	}

	public int getCondition() {
		return _condition;
	}

	private int _win; // ½Â¸® È½¼ö

	public void setWin(int i) {
		_win = i;
	}

	public int getWin() {
		return _win;
	}

	private int _lose; // ÆÐ È½¼ö

	public void setLose(int i) {
		_lose = i;
	}

	public int getLose() {
		return _lose;
	}

	private String _winPoint; // ½Â·ü

	public void setWinPoint(String i) {
		_winPoint = i;
	}

	public String getWinPoint() {
		return _winPoint;
	}

	private float _dividend; // ¹è´ç

	public void setDividend(float i) {
		_dividend = i;
	}

	public float getDividend() {
		return _dividend;
	}

}
