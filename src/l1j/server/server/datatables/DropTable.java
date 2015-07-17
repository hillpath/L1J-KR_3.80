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
import javolution.util.FastTable;
import javolution.util.FastMap;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.GameServerSetting;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Drop;
import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server.templates:
// L1Npc, L1Item, ItemTable

public class DropTable {

	private static Logger _log = Logger.getLogger(DropTable.class.getName());

	private static DropTable _instance;

	private final FastMap<Integer, FastTable<L1Drop>> _droplists; // monster

	// 마다의 드롭
	// 리스트

	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	public static DropTable getInstance() {
		if (_instance == null) {
			_instance = new DropTable();
		}
		return _instance;
	}

	private DropTable() {
		_droplists = allDropList();
	}

	public static void reload() {
		DropTable oldInstance = _instance;
		_instance = new DropTable();
		oldInstance._droplists.clear();
	}

	private FastMap<Integer, FastTable<L1Drop>> allDropList() {
		FastMap<Integer, FastTable<L1Drop>> droplistMap = new FastMap<Integer, FastTable<L1Drop>>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from droplist");
			rs = pstm.executeQuery();
			L1Drop drop = null;
			while (rs.next()) {
				int mobId = rs.getInt("mobId");
				int itemId = rs.getInt("itemId");
				int min = rs.getInt("min");
				int max = rs.getInt("max");
				int chance = rs.getInt("chance");

				drop = new L1Drop(mobId, itemId, min, max, chance);

				FastTable<L1Drop> dropList = droplistMap.get(drop.getMobid());
				if (dropList == null) {
					dropList = new FastTable<L1Drop>();
					droplistMap.put(new Integer(drop.getMobid()), dropList);
				}
				dropList.add(drop);
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "DropTable[]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return droplistMap;
	}

	// 인벤트리에 드롭을 설정
	public void setDrop(L1NpcInstance npc, L1Inventory inventory) {
		// 드롭 리스트의 취득
		int mobId = npc.getNpcTemplate().get_npcId();
		FastTable<L1Drop> dropList = _droplists.get(mobId);
		if (dropList == null) {
			return;
		}

		// 레이트 취득
		double droprate = Config.RATE_DROP_ITEMS;
		if (droprate <= 0) {
			droprate = 0;
		}
		double adenarate = Config.RATE_DROP_ADENA;
		if (adenarate <= 0) {
			adenarate = 0;
		}
		if (droprate <= 0 && adenarate <= 0) {
			return;
		}

		int itemId;
		int itemCount;
		int addCount;
		int randomChance;
		L1ItemInstance item;

		/** 환상 이벤트 * */
		L1ItemInstance Fitem;
		L1ItemInstance Citem;
		Random random = new Random(System.nanoTime());

		for (L1Drop drop : dropList) {
			// 드롭 아이템의 취득
			itemId = drop.getItemid();
			if (adenarate == 0 && itemId == L1ItemId.ADENA) {
				continue; // 아데나레이트 0으로 드롭이 아데나의 경우는 스르
			}

			// 드롭 찬스 판정
			randomChance = random.nextInt(0xf4240) + 1;
			double rateOfMapId = MapsTable.getInstance().getDropRate(
					npc.getMapId());
			double rateOfItem = DropItemTable.getInstance().getDropRate(itemId);
			double resultDroprate = drop.getChance() * droprate * rateOfMapId;

			resultDroprate = (int) (resultDroprate * rateOfItem);

			if (droprate == 0 || resultDroprate < randomChance) {
				continue;
			}

			// 드롭 개수를 설정
			double amount = DropItemTable.getInstance().getDropAmount(itemId);
			int min = drop.getMin();
			int max = drop.getMax();
			min = (int) (min * amount);
			max = (int) (max * amount);

			itemCount = min;
			addCount = max - min + 1;

			if (addCount > 1) {
				itemCount += random.nextInt(addCount);
			}
			if (itemId == L1ItemId.ADENA) { // 드롭이 아데나의 경우는 아데나레이트를 건다
				if (npc.getMapId() == 410) {
					itemCount = 0;
				} else {
					itemCount *= adenarate;
				}
			}
			if (itemCount < 0) {
				itemCount = 0;
			}
			if (itemCount > 2000000000) {
				itemCount = 2000000000;
			}

			// 아이템의 생성
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(itemCount);
			// 아이템 격납
			inventory.storeItem(item);
		}
		/** 환상 이벤트 * */
		if (Config.ALT_FANTASYEVENT == true) {
			int itemRandom = random.nextInt(100) + 1;
			int countRandom = random.nextInt(100) + 1;
			int item1Random = random.nextInt(100 + 1);
			int Fcount = 0;
			int Itemnum = 0;
			if (item1Random <= 50) {
				Itemnum = 40127;
			} else {
				Itemnum = 40128;
			}
			if (countRandom <= 90) {
				Fcount = 1;
			} else if (countRandom >= 91) {
				Fcount = 2;
			}
			if (itemRandom <= 40) {
			} else if (itemRandom >= 46 || itemRandom <= 70) {
				Fitem = ItemTable.getInstance().createItem(Itemnum);
				Fitem.setCount(Fcount);
				inventory.storeItem(Fitem);
			} else if (itemRandom >= 96) {
				Fitem = ItemTable.getInstance().createItem(Itemnum);
				Fitem.setCount(Fcount);
				inventory.storeItem(Fitem);
			}
		}
		/* 아덴은 몹당 1번이기에 아덴에서 결정체 이벤트를 관리한다 */

		// GameServerSetting gss = GameServerSetting.getInstance();
		if (GameServerSetting.이벤트) {
			if (GameServerSetting.이벤트아이템맵 == 4) {// 본토
				if (npc.getMapId() == 4) {
					int itemRandom = random.nextInt(100) + 1;
					if (itemRandom <= 3) {
						item = ItemTable.getInstance().createItem(
								GameServerSetting.이벤트아이템코드);
						if (item != null) {
							item.setCount(1);
							inventory.storeItem(item);
						}
					}
				}
			}
			if (GameServerSetting.이벤트아이템맵 == 7) {// 본던1층
				if (npc.getMapId() == 7) {
					int itemRandom = random.nextInt(100) + 1;
					if (itemRandom <= 3) {
						item = ItemTable.getInstance().createItem(
								GameServerSetting.이벤트아이템코드);
						if (item != null) {
							item.setCount(1);
							inventory.storeItem(item);
						}
					}
				}
			}
			if (GameServerSetting.이벤트아이템맵 == 53) {// 기감1층
				if (npc.getMapId() == 53) {
					int itemRandom = random.nextInt(100) + 1;
					if (itemRandom <= 3) {
						item = ItemTable.getInstance().createItem(
								GameServerSetting.이벤트아이템코드);
						if (item != null) {
							item.setCount(1);
							inventory.storeItem(item);
						}
					}
				}
			}
			if (GameServerSetting.이벤트아이템맵 == 303) {// 몽섬
				if (npc.getMapId() == 303) {
					int itemRandom = random.nextInt(100) + 1;
					if (itemRandom <= 3) {
						item = ItemTable.getInstance().createItem(
								GameServerSetting.이벤트아이템코드);
						if (item != null) {
							item.setCount(1);
							inventory.storeItem(item);
						}
					}
				}
			} else {
				int itemRandom = random.nextInt(100) + 1;
				if (itemRandom <= 3) {
					item = ItemTable.getInstance().createItem(
							GameServerSetting.이벤트아이템코드);
					if (item != null) {
						item.setCount(1);
						inventory.storeItem(item);
					}
				}
			}
		}
		/* 아덴은 몹당 1번이기에 아덴에서 결정체 이벤트를 관리한다 */
		/** 추석 이벤트 * */
		if (Config.ALT_CHUSEOKEVENT == true) {
			int itemRandom = random.nextInt(100) + 1;
			if (itemRandom <= 3) {
				Citem = ItemTable.getInstance().createItem(435014);
				inventory.storeItem(Citem);
			}
		}

		/** 테스트 서버 * */
		if (Config.GAME_SERVER_TYPE == 1) {
			short mapid = npc.getMapId();
			if ((mapid >= 450 && mapid <= 478)
					|| (mapid >= 490 && mapid <= 496)
					|| (mapid >= 530 && mapid <= 536)) {
				return;
			}
			int lvl = npc.getLevel();
			int itemRandom = 0;
			if (lvl >= 20) {
				itemRandom = random.nextInt(lvl * 5 + 1) + lvl;
				Citem = ItemTable.getInstance().createItem(L1ItemId.TEST_MARK);
				Citem.setCount(itemRandom);
				inventory.storeItem(Citem);
			}
		}
	}

	public void setDrop(L1NpcInstance npc, L1Inventory inventory,
			double DropPlus) {
		// 드롭 리스트의 취득
		int mobId = npc.getNpcTemplate().get_npcId();
		FastTable<L1Drop> dropList = _droplists.get(mobId);
		if (dropList == null) {
			return;
		}

		// 레이트 취득
		double droprate = Config.RATE_DROP_ITEMS * DropPlus;
		if (droprate <= 0) {
			droprate = 0;
		}
		double adenarate = Config.RATE_DROP_ADENA * DropPlus;
		if (adenarate <= 0) {
			adenarate = 0;
		}
		if (droprate <= 0 && adenarate <= 0) {
			return;
		}

		int itemId;
		int itemCount;
		int addCount;
		int randomChance;
		L1ItemInstance item;

		/** 환상 이벤트 * */
		L1ItemInstance Fitem;
		L1ItemInstance Citem;

		Random random = new Random(System.nanoTime());

		for (L1Drop drop : dropList) {
			// 드롭 아이템의 취득
			itemId = drop.getItemid();
			if (adenarate == 0 && itemId == L1ItemId.ADENA) {
				continue; // 아데나레이트 0으로 드롭이 아데나의 경우는 스르
			}

			// 드롭 찬스 판정
			randomChance = random.nextInt(0xf4240) + 1;
			double rateOfMapId = MapsTable.getInstance().getDropRate(
					npc.getMapId());
			double rateOfItem = DropItemTable.getInstance().getDropRate(itemId);
			double resultDroprate = drop.getChance() * droprate * rateOfMapId;

			resultDroprate = (int) (resultDroprate * rateOfItem);

			if (droprate == 0 || resultDroprate < randomChance) {
				continue;
			}

			// 드롭 개수를 설정
			double amount = DropItemTable.getInstance().getDropAmount(itemId);
			int min = drop.getMin();
			int max = drop.getMax();
			min = (int) (min * amount);
			max = (int) (max * amount);

			itemCount = min;
			addCount = max - min + 1;

			if (addCount > 1) {
				itemCount += random.nextInt(addCount);
			}
			if (itemId == L1ItemId.ADENA) { // 드롭이 아데나의 경우는 아데나레이트를 건다
				if (npc.getMapId() == 410) {
					itemCount = 0;
				} else {
					itemCount *= adenarate;
				}
			}
			if (itemCount < 0) {
				itemCount = 0;
			}
			if (itemCount > 2000000000) {
				itemCount = 2000000000;
			}

			// 아이템의 생성
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(itemCount);
			// 아이템 격납
			inventory.storeItem(item);
		}
		/** 환상 이벤트 * */
		if (Config.ALT_FANTASYEVENT == true) {
			int itemRandom = random.nextInt(100) + 1;
			int countRandom = random.nextInt(100) + 1;
			int item1Random = random.nextInt(100 + 1);
			int Fcount = 0;
			int Itemnum = 0;
			if (item1Random <= 50) {
				Itemnum = 40127;
			} else {
				Itemnum = 40128;
			}
			if (countRandom <= 90) {
				Fcount = 1;
			} else if (countRandom >= 91) {
				Fcount = 2;
			}
			if (itemRandom <= 40) {
			} else if (itemRandom >= 46 || itemRandom <= 70) {
				Fitem = ItemTable.getInstance().createItem(Itemnum);
				Fitem.setCount(Fcount);
				inventory.storeItem(Fitem);
			} else if (itemRandom >= 96) {
				Fitem = ItemTable.getInstance().createItem(Itemnum);
				Fitem.setCount(Fcount);
				inventory.storeItem(Fitem);
			}
		}
		/** 추석 이벤트 * */
		if (Config.ALT_CHUSEOKEVENT == true) {
			int itemRandom = random.nextInt(100) + 1;
			if (itemRandom <= 3) {
				Citem = ItemTable.getInstance().createItem(435014);
				inventory.storeItem(Citem);
			}
		}

		/** 테스트 서버 * */
		if (Config.GAME_SERVER_TYPE == 1) {
			short mapid = npc.getMapId();
			if ((mapid >= 450 && mapid <= 478)
					|| (mapid >= 490 && mapid <= 496)
					|| (mapid >= 530 && mapid <= 536)) {
				return;
			}
			int lvl = npc.getLevel();
			int itemRandom = 0;
			if (lvl >= 20) {
				itemRandom = random.nextInt(lvl * 5 + 1) + lvl;
				Citem = ItemTable.getInstance().createItem(L1ItemId.TEST_MARK);
				Citem.setCount(itemRandom);
				inventory.storeItem(Citem);
			}
		}
	}

	// 드롭을 분배
	public void dropShare(L1NpcInstance npc, FastTable<?> acquisitorList,
			FastTable<?> hateList, L1PcInstance pc) {
					if(npc == null || pc == null)
			return;
		L1Inventory inventory = npc.getInventory();
		/**보스몹 자동분배**/
		int mobId = npc.getNpcTemplate().get_npcId();
		if(mobId == 4039022 || mobId == 4039007 || mobId == 4036016 || mobId == 4036017// || mobId == 65498
		||	mobId == 400016 || mobId == 400017 || mobId == 4039020 || mobId == 460000035 || mobId == 65499
		|| mobId ==4039000 || mobId ==4039021 || mobId ==4039006 || mobId == 460000036
		|| mobId == 9170 || mobId == 9171 || mobId == 9172)//린드레이드
			return;
		/**보스몹자동분배**/
		if (inventory.getSize() <= 0) {
			return;
		}
		if (pc.getRobotAi() != null) {
			return;
		}
		
		if (acquisitorList.size() != hateList.size()) {
			return;
		}
		// 헤이트의 합계를 취득
		int totalHate = 0;
		L1Character acquisitor;
		for (int i = hateList.size() - 1; i >= 0; i--) {
			acquisitor = (L1Character) acquisitorList.get(i);
			if ((Config.AUTO_LOOT == 2) // 오토 루팅 2의 경우는 사몬 및 애완동물은 생략한다
					&& (acquisitor instanceof L1SummonInstance || acquisitor instanceof L1PetInstance)) {
				acquisitorList.remove(i);
				hateList.remove(i);
			} else if (acquisitor != null
					&& acquisitor.getMapId() == npc.getMapId()
					&& acquisitor.getLocation().getTileLineDistance(
							npc.getLocation()) <= Config.LOOTING_RANGE) {
				totalHate += (Integer) hateList.get(i);
			} else { // null였거나 죽기도 하고 멀었으면 배제
				acquisitorList.remove(i);
				hateList.remove(i);
			}
		}
		
		// 드롭의 분배
		L1ItemInstance item;
		L1Inventory targetInventory = null;
		L1PcInstance player;
		Random random = new Random();
		int randomInt;
		int chanceHate;
		int itemId;
		for (int i = inventory.getSize() - 1; i >= 0; i--) {
			item = inventory.getItems().get(i);
			itemId = item.getItem().getItemId();
			boolean isGround = false;
			if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) { // light계
				// 아이템
				item.setNowLighting(false);
			}
			if (!pc.getSkillEffectTimerSet().hasSkillEffect(
					L1SkillId.STATUS_AUTOROOT)
					&& ((Config.AUTO_LOOT != 0)||AutoLoot.getInstance().isAutoLoot(itemId))
							/*|| (item.getItem().getItemId() == L1ItemId.ADENA)
							|| item.getItem().getItemId() == 40710
							|| item.getItem().getItemId() == 430023// 친구의가방
							|| item.getItem().getItemId() == 40308
							|| item.getItem().getItemId() == 420013
							|| item.getItem().getItemId() == 420014
							|| item.getItem().getItemId() == 41159
							|| item.getItem().getItemId() == 430118
							|| item.getItem().getItemId() == 430119
							|| item.getItem().getItemId() == 430120
							|| item.getItem().getItemId() == 430121
							|| item.getItem().getItemId() == 430122
							|| item.getItem().getItemId() == 430123
							|| item.getItem().getItemId() == 430124
							|| item.getItem().getItemId() == 430125
							|| item.getItem().getItemId() == 40014
							|| item.getItem().getItemId() == 40015
							|| item.getItem().getItemId() == 40018
							|| item.getItem().getItemId() == 40044
							|| item.getItem().getItemId() == 40047
							|| item.getItem().getItemId() == 40048
							|| item.getItem().getItemId() == 40049
							|| item.getItem().getItemId() == 40050
							|| item.getItem().getItemId() == 40051
							|| item.getItem().getItemId() == 40052
							|| item.getItem().getItemId() == 40053
							|| item.getItem().getItemId() == 40054
							|| item.getItem().getItemId() == 40055
							|| item.getItem().getItemId() == 40074
							|| item.getItem().getItemId() == 40076
							|| item.getItem().getItemId() == 40077
							|| item.getItem().getItemId() == 40078
							|| item.getItem().getItemId() == 40087
							|| item.getItem().getItemId() == 40088
							|| item.getItem().getItemId() == 40089
							|| item.getItem().getItemId() == 40090
							|| item.getItem().getItemId() == 40091
							|| item.getItem().getItemId() == 40092
							|| item.getItem().getItemId() == 40093
							|| item.getItem().getItemId() == 40094
							|| item.getItem().getItemId() == 40104
							|| item.getItem().getItemId() == 40105
							|| item.getItem().getItemId() == 40106
							|| item.getItem().getItemId() == 40107
							|| item.getItem().getItemId() == 40108
							|| item.getItem().getItemId() == 40109
							|| item.getItem().getItemId() == 40108
							|| item.getItem().getItemId() == 40109
							|| item.getItem().getItemId() == 40110
							|| item.getItem().getItemId() == 40111
							|| item.getItem().getItemId() == 40112
							|| item.getItem().getItemId() == 40129
							|| item.getItem().getItemId() == 40130
							|| item.getItem().getItemId() == 40126
							|| item.getItem().getItemId() == 40304
							|| item.getItem().getItemId() == 40305
							|| item.getItem().getItemId() == 40306
							|| item.getItem().getItemId() == 40307
							|| item.getItem().getItemId() == 40397
							|| item.getItem().getItemId() == 40398
							|| item.getItem().getItemId() == 40399
							|| item.getItem().getItemId() == 40400
							|| item.getItem().getItemId() == 40466
							|| item.getItem().getItemId() == 40491
							|| item.getItem().getItemId() == 40496
							|| item.getItem().getItemId() == 40636
							|| item.getItem().getItemId() == 40643
							|| item.getItem().getItemId() == 40645
							|| item.getItem().getItemId() == 40913
							|| item.getItem().getItemId() == 40914
							|| item.getItem().getItemId() == 40915
							|| item.getItem().getItemId() == 40916
							|| item.getItem().getItemId() == 40967
							|| item.getItem().getItemId() == 40969
							|| item.getItem().getItemId() == 41352
							|| item.getItem().getItemId() == 140018
							|| item.getItem().getItemId() == 140074
							|| item.getItem().getItemId() == 140087
							|| item.getItem().getItemId() == 140088
							|| item.getItem().getItemId() == 140089
							|| item.getItem().getItemId() == 140100
							|| item.getItem().getItemId() == 140119
							|| item.getItem().getItemId() == 140129
							|| item.getItem().getItemId() == 140130
							|| item.getItem().getItemId() == 240074
							|| item.getItem().getItemId() == 240087
							|| item.getItem().getItemId() == 40651
							|| item.getItem().getItemId() == 140014
							|| item.getItem().getItemId() == 140015
							|| item.getItem().getItemId() == 40045
							|| item.getItem().getItemId() == 40046
							|| item.getItem().getItemId() == 40033
							|| item.getItem().getItemId() == 40034
							|| item.getItem().getItemId() == 40035
							|| item.getItem().getItemId() == 40036
							|| item.getItem().getItemId() == 40037
							|| item.getItem().getItemId() == 40038
							|| item.getItem().getItemId() == 40718
							|| item.getItem().getItemId() == 40678
							|| item.getItem().getItemId() == 49026
							|| item.getItem().getItemId() == 40408
							|| item.getItem().getItemId() == 40524
							|| item.getItem().getItemId() == 46111
							|| item.getItem().getItemId() == 40618 || (item
							.getItem().getItemId() >= 40131 && item.getItem()
							.getItemId() <= 40135))*/
					&& totalHate > 0) { // 오토 루팅이나 아데나로 취득자가 있는 경우
				// if (Config.AUTO_LOOT != 0 && totalHate > 0) {
				randomInt = random.nextInt(totalHate);
				chanceHate = 0;
				for (int j = hateList.size() - 1; j >= 0; j--) {
					chanceHate += (Integer) hateList.get(j);
					if (chanceHate > randomInt) {
						acquisitor = (L1Character) acquisitorList.get(j);
						if (itemId >= 40131 && itemId <= 40135) {
							if (!(acquisitor instanceof L1PcInstance)
									|| hateList.size() > 1) {
								targetInventory = null;
								break;
							}
							player = (L1PcInstance) acquisitor;
							if (player.getQuest().get_step(L1Quest.QUEST_LYRA) != 1) {
								inventory.removeItem(item, item.getCount());
								break;
							}
						}
						if (acquisitor.getInventory().checkAddItem(item,
								item.getCount()) == L1Inventory.OK) {

							targetInventory = acquisitor.getInventory();
							if (acquisitor instanceof L1PcInstance) {
								player = (L1PcInstance) acquisitor;
								L1ItemInstance l1iteminstance = player .getInventory().findItemId(L1ItemId.ADENA); // 소지 아데나를 체크
								if (l1iteminstance != null
										&& l1iteminstance.getCount() > 2000000000) {
									targetInventory = L1World.getInstance().getInventory(acquisitor.getX(),acquisitor.getY(),
											acquisitor.getMapId()); // 가질 수없기때문에 발밑에떨어뜨린다
									isGround = true;
									player.sendPackets(new S_ServerMessage(166,"소지하고 있는 아데나", "2,000,000,000을 초과하고 있습니다."));
								} else {
									if (player.isInParty()) { // 파티의 경우
										for (L1PcInstance partymember : player.getParty().getMembers()) {
											if (!partymember.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_MENT)) {
												partymember.sendPackets(new S_SystemMessage(npc.getName()+
														" -> "+ item.getLogName()+ " [ "+ player.getName()+ " ]"+ " 획득"));
														}
										}
									} else if (!player.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.STATUS_MENT)) { // 솔로의 경우
										player.sendPackets(new S_SystemMessage(npc.getName() + " -> "+ item.getLogName()));// 드랍템
									
	
								
									/*	if(item.getItem().getItemId() == L1ItemId.ADENA){
											player.setInforAdena(player.getInforAdena() + item.getCount());
										}

										if (item.getItem().getItemId() == L1ItemId.PREMIUM_ADENA){
											player.setInforWing(player.getInforWing() + item.getCount());
										}*/
									}
								}
							}
						} else {
							targetInventory = L1World.getInstance()
									.getInventory(acquisitor.getX(),
											acquisitor.getY(),
											acquisitor.getMapId()); // 가질 수 없기 때문에발밑에 떨어뜨린다
							isGround = true;
						}
						break;
					}
				}
			} else { // Non 오토 루팅
				item.setDropMobId(mobId);
				item.startItemOwnerTimer(pc);
				List<Integer> dirList = new FastTable<Integer>();
				for (int j = 0; j < 8; j++) {
					dirList.add(j);
				}
				int x = 0;
				int y = 0;
				int dir = 0;
				do {
					if (dirList.size() == 0) {
						x = 0;
						y = 0;
						break;
					}
					randomInt = random.nextInt(dirList.size());
					dir = dirList.get(randomInt);
					dirList.remove(randomInt);
					x = HEADING_TABLE_X[dir];
					y = HEADING_TABLE_Y[dir];
				} while (!npc.getMap().isPassable(npc.getX(), npc.getY(), dir));
				//특정몬스터 드랍 위치설정
			    if(mobId == 460000035 || mobId == 460000036 || mobId == 65498 ){
			    targetInventory = L1World.getInstance().getInventory(pc.getX() + x, pc.getY() + y, npc.getMapId());
			    }else{//여기까지
			    targetInventory = L1World.getInstance().getInventory(npc.getX() + x, npc.getY() + y, npc.getMapId());
			    }
			    isGround = true;
			   }	
			if (itemId >= 40131 && itemId <= 40135) {
				if (isGround || targetInventory == null) {
					inventory.removeItem(item, item.getCount());
					continue;
				}
			}
			inventory.tradeItem(item, item.getCount(), targetInventory);
		}
		npc.getLight().turnOnOffLight();
	}

	public void setPainwandDrop(L1NpcInstance npc, L1Inventory inventory) {
		// 드롭 리스트의 취득
		int mobId = npc.getNpcTemplate().get_npcId();
		FastTable<L1Drop> dropList = _droplists.get(mobId);
		if (dropList == null) {
			return;
		}

		// 레이트 취득
		double droprate = Config.RATE_DROP_ITEMS;
		if (droprate <= 0) {
			droprate = 0;
		}
		double adenarate = Config.RATE_DROP_ADENA;
		if (adenarate <= 0) {
			adenarate = 0;
		}
		if (droprate <= 0 && adenarate <= 0) {
			return;
		}

		int itemId;
		int itemCount;
		int addCount;
		int randomChance;
		L1ItemInstance item;
		Random random = new Random(System.nanoTime());

		for (L1Drop drop : dropList) {
			// 드롭 아이템의 취득
			itemId = drop.getItemid();
			if (adenarate == 0 && itemId == L1ItemId.ADENA) {
				continue; // 아데나레이트 0으로 드롭이 아데나의 경우는 스르
			}
			if (itemId != L1ItemId.ADENA) {
				continue;
			}

			// 드롭 찬스 판정
			randomChance = random.nextInt(0xf4240) + 1;
			double rateOfMapId = MapsTable.getInstance().getDropRate(
					npc.getMapId());
			double rateOfItem = DropItemTable.getInstance().getDropRate(itemId);
			if (droprate == 0
					|| drop.getChance() * droprate * rateOfMapId * rateOfItem < randomChance) {
				continue;
			}

			// 드롭 개수를 설정
			double amount = DropItemTable.getInstance().getDropAmount(itemId);
			int min = drop.getMin();
			int max = drop.getMax();
			if (amount < 0) {
				min = (int) (min / amount);
				max = (int) (max / amount);
			} else {
				min = (int) (min * amount);
				max = (int) (max * amount);
			}

			itemCount = min;
			addCount = max - min + 1;

			if (addCount > 1) {
				itemCount += random.nextInt(addCount);
			}
			if (itemId == L1ItemId.ADENA) { // 드롭이 아데나의 경우는 아데나레이트를 건다
				if (npc.getMapId() == 410) {
					itemCount = 0;
				} else {
					itemCount *= adenarate;
				}
			}
			if (itemCount < 0) {
				itemCount = 0;
			}
			if (itemCount > 2000000000) {
				itemCount = 2000000000;
			}

			// 아이템의 생성
			item = ItemTable.getInstance().createItem(itemId);
			item.setCount(itemCount);
			// 아이템 격납
			inventory.storeItem(item);
		}
	}
	
	
}