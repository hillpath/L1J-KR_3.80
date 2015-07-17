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

import javolution.util.FastTable;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.templates.L1Npc;
import server.controller.Npc.NpcRestController;

public class L1EffectInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int CubeTime; //큐브시간
	private L1PcInstance CubePc; //큐브사용자
	private int Cube = 20;

	public L1EffectInstance(L1Npc template) {
		super(template);

		if (getNpcTemplate().get_npcId() == 81157) { // FW
			NpcRestController.getInstance().addFW(this);
		}
	}

	/** 큐브다 */
	public void setCubeTime(int CubeTime) {
		this.CubeTime = CubeTime;
	}

	public boolean isCube() {
		return CubeTime-- <= 0;
	}

	public void setCubePc(L1PcInstance CubePc) {
		this.CubePc = CubePc;
	}

	public L1PcInstance CubePc() {
		return CubePc;
	}

	public boolean Cube() {
		return Cube-- <= 0;
	}

	@Override
	public void onAction(L1PcInstance pc) {
	}

	@Override
	public void deleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		allTargetClear();
		_master = null;
		this.setCubePc(null);
		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeObject(this);
		FastTable<L1PcInstance> list = null;
		list = L1World.getInstance().getRecognizePlayer(this);
		for (L1PcInstance pc : list) {
			if (pc == null)
				continue;
			pc.getNearObjects().removeKnownObject(this);
			pc.sendPackets(new S_RemoveObject(this));
		}
		getNearObjects().removeAllKnownObjects();
	}

}
