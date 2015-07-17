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

import java.lang.reflect.Constructor;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

public class L1GfxId implements L1CommandExecutor {
	@SuppressWarnings("unused")
	private static Logger _log = Logger.getLogger(L1GfxId.class.getName());

	private L1GfxId() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1GfxId();
	}

	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			int gfxid = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);
			int pcX = pc.getX();
			int pcY = pc.getY();
			L1Npc l1npc = null;
			Constructor<?> constructor = null;
			L1NpcInstance npc = null;
			for (int i = 0; i < count; i++) {
				l1npc = NpcTable.getInstance().getTemplate(45001);
				if (l1npc != null) {
					String s = l1npc.getImpl();
					constructor = Class.forName(
							"l1j.server.server.model.Instance." + s
									+ "Instance").getConstructors()[0];
					Object aobj[] = { l1npc };
					npc = (L1NpcInstance) constructor.newInstance(aobj);
					npc.setId(ObjectIdFactory.getInstance().nextId());
					npc.getGfxId().setGfxId(gfxid + i);
					npc.getGfxId().setTempCharGfx(0);
					npc.setNameId("" + (gfxid + i) + "");
					npc.setMap(pc.getMapId());
					int e = i % 5;
					if (e == 0 && i > 0) {
						pcX -= 2;
						pcY += 2;
					}
					npc.setX(pcX + e * 2);
					npc.setY(pcY + e * 2);
					npc.setHomeX(npc.getX());
					npc.setHomeY(npc.getY());
					npc.getMoveState().setHeading(4);

					L1World.getInstance().storeObject(npc);
					L1World.getInstance().addVisibleObject(npc);
				}
			}
		} catch (Exception exception) {
			pc.sendPackets(new S_SystemMessage(cmdName
					+ " [id] [출현시키는 수]로 입력해 주세요. "));
		}
	}
}
