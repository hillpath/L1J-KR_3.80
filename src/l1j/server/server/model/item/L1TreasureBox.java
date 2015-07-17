package l1j.server.server.model.item;

import java.io.File;
import javolution.util.FastMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.utils.PerformanceTimer;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1TreasureBox {

	private static Logger _log = Logger
			.getLogger(L1TreasureBox.class.getName());

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "TreasureBoxList")
	private static class TreasureBoxList implements Iterable<L1TreasureBox> {
		@XmlElement(name = "TreasureBox")
		private List<L1TreasureBox> _list;

		public Iterator<L1TreasureBox> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
		private static class Item {
		@XmlAttribute(name = "ItemId")
		private int _itemId;

		@XmlAttribute(name = "Count")
		private int _count;
		
		@XmlAttribute(name = "Enchant")
		private int _enchant;
		
		@XmlAttribute(name = "Attr")
		private int _attr;
		
		@XmlAttribute(name = "Identi")
		private boolean _identified;
		
		private int _chance;
		
		@SuppressWarnings("unused")
		@XmlAttribute(name = "Chance")
		private void setChance(double chance) {
			_chance = (int) (chance * 10000);
		}

		// 아이템 id
		public int getItemId() {
			return _itemId;
		}

		// 아이템 갯수
		public int getCount() {
			return _count;
		}
		
		// 아이템 인첸트 레벨
		public int getEnchant() {
			return _enchant;
		}
		
		// 속성 인첸트 레벨
		public int getAttr() {
			return _attr;
		}
		
		// 확인 상태
		public boolean getIdentified() {
			return _identified;
		}

		// 챈스 확률
		public double getChance() {
			return _chance;
		}
	}

	private static enum TYPE {
		RANDOM, SPECIFIC
	}

	private static final String PATH = "./data/xml/Item/TreasureBox.xml";

	private static final FastMap<Integer, L1TreasureBox> _dataMap = new FastMap<Integer, L1TreasureBox>();

	public static L1TreasureBox get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _boxId;

	@XmlAttribute(name = "Type")
	private TYPE _type;

	private int getBoxId() {
		return _boxId;
	}

	private TYPE getType() {
		return _type;
	}

	@XmlElement(name = "Item")
	private CopyOnWriteArrayList<Item> _items;

	private List<Item> getItems() {
		return _items;
	}

	private int _totalChance;

	private int getTotalChance() {
		return _totalChance;
	}

	private void init() {
		for (Item each : getItems()) {
			_totalChance += each.getChance();
			if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
				getItems().remove(each);
				_log.warning("아이템 ID " + each.getItemId() + " 의 템플릿이 발견되지 않았습니다.");
			}
		}
		if (getTotalChance() != 0 && getTotalChance() != 1000000) {
			_log.warning("ID " + getBoxId() + "의 확률의 합계가 100%가 되지 않습니다.");
		}
	}

	public static void load() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("[L1TreasureBox] loading TreasureBox...");
		try {
			JAXBContext context = JAXBContext.newInstance(L1TreasureBox.TreasureBoxList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(PATH);
			TreasureBoxList list = (TreasureBoxList) um.unmarshal(file);

			for (L1TreasureBox each : list) {
				each.init();
				_dataMap.put(each.getBoxId(), each);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, PATH + "의 로드에 실패.", e);
			System.exit(0);
		}
		System.out.println("OK! " + timer.get() + "ms");
	}

	public boolean open(L1PcInstance pc) {
		L1ItemInstance item = null;
		Random random = null;
		if (getType().equals(TYPE.SPECIFIC)) {
			for (Item each : getItems()) {
				item = ItemTable.getInstance().createItem(each.getItemId());
				if (item != null && !isOpen(pc)) {
					item.setCount(each.getCount());
					item.setEnchantLevel(each.getEnchant());
					item.setAttrEnchantLevel(each.getAttr());
					item.setIdentified(each.getIdentified());
					storeItem(pc, item);
				} else {
				     return false;
				}
			}

		} else if (getType().equals(TYPE.RANDOM)) {
			random = new Random(System.nanoTime());
			int chance = 0;

			int r = random.nextInt(getTotalChance());

			for (Item each : getItems()) {
				chance += each.getChance();

				if (r < chance) {
					item = ItemTable.getInstance().createItem(each.getItemId());
					if (item != null) {
						item.setCount(each.getCount());
						item.setEnchantLevel(each.getEnchant());
						item.setAttrEnchantLevel(each.getAttr());
						item.setIdentified(each.getIdentified());
						storeItem(pc, item);
					} else {
					      return false;
					}
					break;
				}
			}
		}

		if (item == null) {
			return false;
		} else {
			int itemId = getBoxId();

			if (itemId == 40576 || itemId == 40577 || itemId == 40578
					|| itemId == 40411 || itemId == 49013) {
				//pc.death(null);
			}
			return true;
		}
	}
	private boolean isOpen(L1PcInstance pc) {
		  int totalCount = pc.getInventory().getSize();
		  if (pc.getInventory().getWeight240() == 240 || totalCount > 180) {
		   return true;
		  }
		  return false;
		 }
	private static void storeItem(L1PcInstance pc, L1ItemInstance item) {
		L1Inventory inventory;

		if (pc.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
			inventory = pc.getInventory();
		} else {
			inventory = L1World.getInstance().getInventory(pc.getLocation());
		}
		if (item.getItemId() == L1ItemId.DRAGON_KEY){
			pc.sendPackets(new S_Message_YN(1565, ""));
			L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(2922));
		}
		inventory.storeItem(item);
		pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
	}
}
