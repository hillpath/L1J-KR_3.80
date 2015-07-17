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

import l1j.server.server.model.L1Attack;

import l1j.server.server.templates.L1Npc;

public class L1PrivateNpcInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;

	public L1PrivateNpcInstance(L1Npc template) {
		super(template);
	}

	public void onAction(L1PcInstance pc) {
		L1Attack attack = new L1Attack(pc, this);
		attack.action();
	}

}
