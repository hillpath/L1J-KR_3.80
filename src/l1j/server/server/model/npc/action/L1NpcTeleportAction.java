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
package l1j.server.server.model.npc.action;

import org.w3c.dom.Element;

import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.model.map.L1Map;

public class L1NpcTeleportAction extends L1NpcXmlAction {
	private final L1Location _loc;

	private final int _heading;

	private final int _price;

	private final boolean _effect;

	public L1NpcTeleportAction(Element element) {
		super(element);

		int x = L1NpcXmlParser.getIntAttribute(element, "X", -1);
		int y = L1NpcXmlParser.getIntAttribute(element, "Y", -1);
		int mapId = L1NpcXmlParser.getIntAttribute(element, "Map", -1);
		_loc = new L1Location(x, y, mapId);

		_heading = L1NpcXmlParser.getIntAttribute(element, "Heading", 5);

		_price = L1NpcXmlParser.getIntAttribute(element, "Price", 0);
		_effect = L1NpcXmlParser.getBoolAttribute(element, "Effect", true);
	}

	@Override
	public L1NpcHtml execute(String actionName, L1PcInstance pc, L1Object obj,
			byte[] args) {
		if ((_loc.getMapId() == 68 || _loc.getMapId() == 2005)
				&& pc.getLevel() >= 13) {
			// 노섬, 숨계
			return L1NpcHtml.HTML_CLOSE;
		}
		if ((pc.getLevel() < 45 && pc.getLevel() > 51)
				&& (_loc.getMapId() == 777 || _loc.getMapId() == 778 || _loc
						.getMapId() == 779)) {
			// 버땅 이동부분
			return L1NpcHtml.HTML_CLOSE;
		}
		if (!pc.getInventory().checkItem(L1ItemId.ADENA, _price)) {
			pc.sendPackets(new S_ServerMessage(337, "$4"));
			return L1NpcHtml.HTML_CLOSE;
		}
		// / 별자리 랜포 수정|
		L1Map map = _loc.getMap();

		L1Location loc = L1Location.randomLocation2(_loc.getX(), _loc.getY(),
				map, (short) _loc.getMapId(), 1, 3, false);

		pc.getInventory().consumeItem(L1ItemId.ADENA, _price);
		L1Teleport.teleport(pc, loc.getX(), loc.getY(),
				(short) _loc.getMapId(), _heading, _effect);
		/**안전모드**/
		pc.getSkillEffectTimerSet().setSkillEffect(
				L1SkillId.ABSOLUTE_BARRIER, 5000);
		pc.Safe_Teleport = true;
		pc.sendPackets(new S_SystemMessage("안전 모드가 활성화 되었습니다."));
		/**안전모드**/
		return null;

	}
	// / 별자리 랜포 수정|

}
