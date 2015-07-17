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

package l1j.server.server.model.item.function;

import java.util.Random;

import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1ItemDelay;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.model.map.L1Map;

@SuppressWarnings("serial")
public class TeleportScroll extends L1ItemInstance {

	public TeleportScroll(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			@SuppressWarnings("unused")
			int bmapid = 0;
			bmapid = packet.readH();
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK,
					false));
			int itemId = useItem.getItemId();
			int delay_id = 0;
			if (useItem.getItem().getType2() == 0) { // 종별：그 외의 아이템
				delay_id = ((L1EtcItem) useItem.getItem()).get_delayid();
			}
			if (delay_id != 0) { // 지연 설정 있어
				if (pc.hasItemDelay(delay_id) == true) {
					return;
				}
			}

			if (itemId == 140100 || itemId == 40100 || itemId == 40099
					|| itemId == 40086 || itemId == 40863) {
				L1BookMark bookm = pc.getBookMark(packet.readD());
				if (bookm != null) { // 북마크를 취득 할 수 있으면(자) 텔레포트
					if (bookm.getRandomX() > 0 || bookm.getRandomY() > 0) {
						if (pc.getMap().isEscapable() || pc.isGm()) {
							L1Teleport.randomBookmarkTeleport(pc, bookm, pc
									.getMoveState().getHeading(), true);
							pc.getInventory().removeItem(useItem, 1);
						} else {
							pc.sendPackets(new S_Paralysis(
									S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
							pc.sendPackets(new S_ServerMessage(79));
						}
					} else if (pc.getMap().isEscapable() || pc.isGm()) {
						int newX = bookm.getLocX();
						int newY = bookm.getLocY();
						short mapId = bookm.getMapId();
						if (itemId == 40086) { // 매스 텔레포트 주문서
							for (L1PcInstance member : L1World.getInstance()
									.getVisiblePlayer(pc)) {
								if (pc.getLocation().getTileLineDistance(
										member.getLocation()) <= 3
										&& member.getClanid() == pc.getClanid()
										&& pc.getClanid() != 0
										&& member.getId() != pc.getId()
										&& !member.isPrivateShop()) {
									L1Teleport.teleport(member, newX, newY,
											mapId, member.getMoveState()
													.getHeading(), true);
								}
							}
						}
						L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_Paralysis(
								S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(79));
					}
				} else {
					if (pc.getMap().isTeleportable() || pc.isGm()) {
						L1Location newLocation = pc.getLocation()
								.randomLocation(200, true);
						int newX = newLocation.getX();
						int newY = newLocation.getY();
						short mapId = (short) newLocation.getMapId();

						if (itemId == 40086) { // 매스텔레포트주문서
							for (L1PcInstance member : L1World.getInstance()
									.getVisiblePlayer(pc)) {
								if (pc.getLocation().getTileLineDistance(
										member.getLocation()) <= 3
										&& member.getClanid() == pc.getClanid()
										&& pc.getClanid() != 0
										&& member.getId() != pc.getId()
										&& !member.isPrivateShop()) {
									L1Teleport.teleport(member, newX, newY,
											mapId, member.getMoveState()
													.getHeading(), true);
								}
							}
						}
						L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
						pc.getInventory().removeItem(useItem, 1);
					} else {
						pc.sendPackets(new S_Paralysis(
								S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(276));
					}
				}
				pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
			} else if (itemId == 240100) { // 저주해진 텔레포트 스크롤(오리지날 아이템)
				L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc
						.getMoveState().getHeading(), true);
				pc.getInventory().removeItem(useItem, 1);
				pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
			
			/*} else if (itemId == 41159) { // 픽시의 깃털
				if (pc.getMap().isEscapable() || pc.isGm()) {
					int[] loc = L1TownLocation.getGetBackLoc(20);
					L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5,
							true);
					pc.getInventory().removeItem(useItem, 1);
					pc.cancelAbsoluteBarrier();
				} else {
					pc.sendPackets(new S_ServerMessage(647));
				}*/
			} else if (itemId == 40117) {// 은말
				// 귀환 스크롤

				if (pc.getMap().isEscapable() || pc.isGm()) {

					Random random = new Random();
					int gn4 = random.nextInt(4) + 1;
					if (gn4 == 1) {
						L1Location _loc = new L1Location(33080, 33392,
								(short) 4);
						L1Map map = _loc.getMap();
						L1Location loc = L1Location.randomLocation2(33080,
								33392, map, (short) _loc.getMapId(), 1, 5,
								false);
						L1Teleport.teleport(pc, loc.getX(), loc.getY(),
								(short) 4, 5, true);

					} else if (gn4 == 2) {
						L1Location _loc = new L1Location(33071, 33402,
								(short) 4);
						L1Map map = _loc.getMap();
						L1Location loc = L1Location.randomLocation2(33071,
								33402, map, (short) _loc.getMapId(), 1, 5,
								false);
						L1Teleport.teleport(pc, loc.getX(), loc.getY(),
								(short) 4, 5, true);

					} else if (gn4 == 3) {
						L1Location _loc = new L1Location(33072, 33392,
								(short) 4);
						L1Map map = _loc.getMap();
						L1Location loc = L1Location.randomLocation2(33072,
								33392, map, (short) _loc.getMapId(), 1, 5,
								false);
						L1Teleport.teleport(pc, loc.getX(), loc.getY(),
								(short) 4, 5, true);

					} else if (gn4 == 4) {
						L1Location _loc = new L1Location(33097, 33366,
								(short) 4);
						L1Map map = _loc.getMap();
						L1Location loc = L1Location.randomLocation2(33097,
								33366, map, (short) _loc.getMapId(), 1, 5,
								false);
						L1Teleport.teleport(pc, loc.getX(), loc.getY(),
								(short) 4, 5, true);

					} else {
						pc.sendPackets(new S_ServerMessage(647));
					}
					pc.getInventory().removeItem(useItem, 1);
				} else {
					pc.sendPackets(new S_ServerMessage(647));

				}
				pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제

			} else if (itemId == 40081) {// 기란
				// 귀환 스크롤
				if (pc.getMap().isEscapable() || pc.isGm()) {
					Random random = new Random();
			        int gn4 = random.nextInt(5) + 1;
			        if (gn4 == 1) {
			         L1Teleport.teleport(pc, 33439, 32801, (short) 4, 5, true);
			        } else if (gn4 == 2) {
			         L1Teleport.teleport(pc, 33439, 32796, (short) 4, 5, true);
			        } else if (gn4 == 3) {
			         L1Teleport.teleport(pc, 33444, 32805, (short) 4, 5, true);//수정
			        } else if (gn4 == 4) {
				         L1Teleport.teleport(pc, 33431, 32822, (short) 4, 5, true);
			        } else if (gn4 == 5) {
				         L1Teleport.teleport(pc, 33439, 32814, (short) 4, 5, true);
					/*	int gn4 = random.nextInt(4) + 1;
						if (gn4 == 1) {
							L1Location _loc = new L1Location(33442, 32797,
									(short) 4);
							L1Map map = _loc.getMap();
							L1Location loc = L1Location.randomLocation2(33442,
									32797, map, (short) _loc.getMapId(), 1, 5,
									false);
							L1Teleport.teleport(pc, loc.getX(), loc.getY(),
									(short) 4, 5, true);
						} else if (gn4 == 2) {
							L1Location _loc = new L1Location(33440, 32809,
									(short) 4);
							L1Map map = _loc.getMap();
							L1Location loc = L1Location.randomLocation2(33440,
									32809, map, (short) _loc.getMapId(), 1, 5,
									false);
							L1Teleport.teleport(pc, loc.getX(), loc.getY(),
									(short) 4, 5, true);


						} else if (gn4 == 3) {
							L1Location _loc = new L1Location(33418, 32818,
									(short) 4);
							L1Map map = _loc.getMap();
							L1Location loc = L1Location.randomLocation2(33418,
									32818, map, (short) _loc.getMapId(), 1, 5,
									false);
							L1Teleport.teleport(pc, loc.getX(), loc.getY(),
									(short) 4, 5, true);


						} else if (gn4 == 4) {
							L1Location _loc = new L1Location(33432, 32824,
									(short) 4);
							L1Map map = _loc.getMap();
							L1Location loc = L1Location.randomLocation2(33432,
									32824, map, (short) _loc.getMapId(), 1, 5,
									false);
							L1Teleport.teleport(pc, loc.getX(), loc.getY(),
									(short) 4, 5, true);*/

					} else {
						pc.sendPackets(new S_ServerMessage(647));
					}
					pc.getInventory().removeItem(useItem, 1);
				} else {
					pc.sendPackets(new S_ServerMessage(647));

				}
				pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
			} else if (itemId == 40102) {// 기란 33966 33253 33965 3255
				Random random = new Random();

				if (pc.getMap().isEscapable() || pc.isGm()) {
					int gn4 = random.nextInt(4) + 1;
					if (gn4 == 1) {
						L1Location _loc = new L1Location(33966, 33253,
								(short) 4);
						L1Map map = _loc.getMap();
						L1Location loc = L1Location.randomLocation2(33966,
								33253, map, (short) _loc.getMapId(), 1, 5,
								false);
						L1Teleport.teleport(pc, loc.getX(), loc.getY(),
								(short) pc.getMapId(), 5, true);


					} else if (gn4 == 2) {
						L1Location _loc = new L1Location(33975, 33246,
								(short) 4);
						L1Map map = _loc.getMap();
						L1Location loc = L1Location.randomLocation2(33975,
								33246, map, (short) _loc.getMapId(), 1, 5,
								false);
						L1Teleport.teleport(pc, loc.getX(), loc.getY(),
								(short) 4, 5, true);

					} else if (gn4 == 3) {
						L1Location _loc = new L1Location(33962, 33239,
								(short) 4);
						L1Map map = _loc.getMap();
						L1Location loc = L1Location.randomLocation2(33962,
								33239, map, (short) _loc.getMapId(), 1, 5,
								false);
						L1Teleport.teleport(pc, loc.getX(), loc.getY(),
								(short) 4, 5, true);

					} else if (gn4 == 4) {
						L1Location _loc = new L1Location(33972, 33249,
								(short) 4);
						L1Map map = _loc.getMap();
						L1Location loc = L1Location.randomLocation2(33972,
								33249, map, (short) _loc.getMapId(), 1, 5,
								false);
						L1Teleport.teleport(pc, loc.getX(), loc.getY(),
								(short) 4, 5, true);

					} else {
						pc.sendPackets(new S_ServerMessage(647));
					}
					pc.getInventory().removeItem(useItem, 1);
				} else {
					pc.sendPackets(new S_ServerMessage(647));

				}
				pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제

			} else if (itemId == 40079 || itemId == 40095 || itemId == 40521) {
				if (pc.getMap().isEscapable() || pc.isGm()) {

					int[] loc = Getback.GetBack_Location(pc, true);
					L1Location _loc = new L1Location(loc[0], loc[1],
							(short) loc[2]);
					L1Map map = _loc.getMap();

					L1Location loc2 = L1Location.randomLocation2(loc[0],
							loc[1], map, (short) loc[2], 1, 5, false);
					L1Teleport.teleport(pc, loc2.getX(), loc2.getY(),
							(short) loc2.getMapId(), 5, true);
					pc.getInventory().removeItem(useItem, 1);
				} else {
					pc.sendPackets(new S_ServerMessage(647));

				}
				pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
			} else if (itemId == 40124) { // 혈맹 귀환 스크롤
				if (pc.getMap().isEscapable() || pc.isGm()) {
					int castle_id = 0;
					int house_id = 0;
					if (pc.getClanid() != 0) { // 크란 소속
						L1Clan clan = L1World.getInstance().getClan(
								pc.getClanname());
						if (clan != null) {
							castle_id = clan.getCastleId();
							house_id = clan.getHouseId();
						}
					}
					if (castle_id != 0) { // 성주 크란원
						if (pc.getMap().isEscapable() || pc.isGm()) {
							int[] loc = new int[3];
							loc = L1CastleLocation.getCastleLoc(castle_id);
							int locx = loc[0];
							int locy = loc[1];
							short mapid = (short) (loc[2]);
							L1Teleport.teleport(pc, locx, locy, mapid, 0, true);
							pc.getInventory().removeItem(useItem, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(647));
						}
					} else if (house_id != 0) { // 아지트 소유 크란원
						if (pc.getMap().isEscapable() || pc.isGm()) {
							int[] loc = new int[3];
							loc = L1HouseLocation.getHouseLoc(house_id);
							int locx = loc[0];
							int locy = loc[1];
							short mapid = (short) (loc[2]);
							L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
							pc.getInventory().removeItem(useItem, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(647));
						}
					} else {
						if (pc.getHomeTownId() > 0) {
							int[] loc = L1TownLocation.getGetBackLoc(pc
									.getHomeTownId());
							int locx = loc[0];
							int locy = loc[1];
							short mapid = (short) (loc[2]);
							L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
							pc.getInventory().removeItem(useItem, 1);
						} else {
							int[] loc = Getback.GetBack_Location(pc, true);
							L1Teleport.teleport(pc, loc[0], loc[1],
									(short) loc[2], 5, true);
							pc.getInventory().removeItem(useItem, 1);
						}
					}
				} else {
					pc.sendPackets(new S_ServerMessage(647));
				}
				pc.cancelAbsoluteBarrier(); // 아브소르트바리아의 해제
			}
			L1ItemDelay.onItemUse(pc, useItem); // 아이템 지연 개시
		}
	}
}
