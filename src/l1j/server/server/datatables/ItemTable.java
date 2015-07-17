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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastMap;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.item.function.Armor;
import l1j.server.server.model.item.function.Arrow;
import l1j.server.server.model.item.function.BlankScroll;
import l1j.server.server.model.item.function.BlessOfEvaPotion;
import l1j.server.server.model.item.function.BlindPotion;
import l1j.server.server.model.item.function.BluePotion;
import l1j.server.server.model.item.function.BravePotion;
import l1j.server.server.model.item.function.Choiceitem;
import l1j.server.server.model.item.function.DefiniteScroll;
import l1j.server.server.model.item.function.Elixir;
import l1j.server.server.model.item.function.EnchantArmor;
import l1j.server.server.model.item.function.EnchantWeapon;
import l1j.server.server.model.item.function.FieldObject;
import l1j.server.server.model.item.function.FireWand;
import l1j.server.server.model.item.function.Firecracker;
import l1j.server.server.model.item.function.Fishing;
import l1j.server.server.model.item.function.Food;
import l1j.server.server.model.item.function.FurnitureItem;
import l1j.server.server.model.item.function.GMWand;
import l1j.server.server.model.item.function.GreenPotion;
import l1j.server.server.model.item.function.HealingPotion;
import l1j.server.server.model.item.function.Light;
import l1j.server.server.model.item.function.MagicDoll;
import l1j.server.server.model.item.function.MakeCooking;
import l1j.server.server.model.item.function.MobSpawnWand;
import l1j.server.server.model.item.function.PandoraT;
import l1j.server.server.model.item.function.PolyItem;
import l1j.server.server.model.item.function.PolyScroll;
import l1j.server.server.model.item.function.PolyWand;
import l1j.server.server.model.item.function.Resolvent;
import l1j.server.server.model.item.function.ResurrectionScroll;
import l1j.server.server.model.item.function.SealScroll;
import l1j.server.server.model.item.function.Special_Other;
import l1j.server.server.model.item.function.Spellbook;
import l1j.server.server.model.item.function.Sting;
import l1j.server.server.model.item.function.TelBookItem;
import l1j.server.server.model.item.function.TeleportScroll;
import l1j.server.server.model.item.function.ThunderWand;
import l1j.server.server.model.item.function.TreasureBox;
import l1j.server.server.model.item.function.Weapon;
import l1j.server.server.model.item.function.WisdomPotion;
import l1j.server.server.templates.L1Armor;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1RaceTicket;
import l1j.server.server.templates.L1Weapon;
import l1j.server.server.utils.SQLUtil;

public class ItemTable {
	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(ItemTable.class.getName());

	private static final Map<String, Integer> _armorTypes = new FastMap<String, Integer>();

	private static final Map<String, Integer> _weaponTypes = new FastMap<String, Integer>();

	private static final Map<String, Integer> _weaponId = new FastMap<String, Integer>();

	private static final Map<String, Integer> _materialTypes = new FastMap<String, Integer>();

	private static final Map<String, Integer> _etcItemTypes = new FastMap<String, Integer>();

	private static final Map<String, Integer> _raceTypes = new FastMap<String, Integer>();

	private static final Map<String, Integer> _useTypes = new FastMap<String, Integer>();

	private static ItemTable _instance;

	private L1Item _allTemplates[];

	/*private final Map<Integer, L1EtcItem> _etcitems;

	private final Map<Integer, L1Armor> _armors;

	private final Map<Integer, L1Weapon> _weapons;*/
	public final Map<Integer, L1EtcItem> _etcitems;
	 public final Map<Integer, L1Armor> _armors;
	 public final Map<Integer, L1Weapon> _weapons;


	private final Map<Integer, L1RaceTicket> _race;

	static {

		_etcItemTypes.put("arrow", new Integer(0));
		_etcItemTypes.put("wand", new Integer(1));
		_etcItemTypes.put("light", new Integer(2));
		_etcItemTypes.put("gem", new Integer(3));
		_etcItemTypes.put("totem", new Integer(4));
		_etcItemTypes.put("firecracker", new Integer(5));
		_etcItemTypes.put("potion", new Integer(6));
		_etcItemTypes.put("food", new Integer(7));
		_etcItemTypes.put("scroll", new Integer(8));
		_etcItemTypes.put("questitem", new Integer(9));
		_etcItemTypes.put("spellbook", new Integer(10));
		_etcItemTypes.put("petitem", new Integer(11));
		_etcItemTypes.put("other", new Integer(12));
		_etcItemTypes.put("material", new Integer(13));
		_etcItemTypes.put("event", new Integer(14));
		_etcItemTypes.put("sting", new Integer(15));
		_etcItemTypes.put("treasure_box", new Integer(16));

		_useTypes.put("none", new Integer(-1)); // 사용 불가능
		_useTypes.put("normal", new Integer(0));
		_useTypes.put("weapon", new Integer(1));
		_useTypes.put("armor", new Integer(2));
		// _useTypes.put("wand1", new Integer(3));
		// _useTypes.put("wand", new Integer(4));
		// wand를 거절하는 액션을 잡는다(C_RequestExtraCommand가 보내진다)
		_useTypes.put("spell_long", new Integer(5)); // 지면 / 오브젝트 선택(원거리)
		_useTypes.put("ntele", new Integer(6));
		_useTypes.put("identify", new Integer(7));
		_useTypes.put("res", new Integer(8));
		_useTypes.put("letter", new Integer(12));
		_useTypes.put("letter_w", new Integer(13));
		_useTypes.put("choice", new Integer(14));
		_useTypes.put("instrument", new Integer(15));
		_useTypes.put("sosc", new Integer(16));
		_useTypes.put("spell_short", new Integer(17)); // 지면 / 오브젝트 선택(근거리)
		_useTypes.put("T", new Integer(18));
		_useTypes.put("cloak", new Integer(19));
		_useTypes.put("glove", new Integer(20));
		_useTypes.put("boots", new Integer(21));
		_useTypes.put("helm", new Integer(22));
		_useTypes.put("ring", new Integer(23));
		_useTypes.put("amulet", new Integer(24));
		_useTypes.put("shield", new Integer(25));
		_useTypes.put("garder", new Integer(25));
		_useTypes.put("dai", new Integer(26));
		_useTypes.put("zel", new Integer(27));
		_useTypes.put("blank", new Integer(28));
		_useTypes.put("btele", new Integer(29));
		_useTypes.put("spell_buff", new Integer(30)); // 오브젝트 선택(원거리)
		// Ctrl를 누르지 않는다고 패킷이 날지 않아?
		_useTypes.put("ccard", new Integer(31));
		_useTypes.put("ccard_w", new Integer(32));
		_useTypes.put("vcard", new Integer(33));
		_useTypes.put("vcard_w", new Integer(34));
		_useTypes.put("wcard", new Integer(35));
		_useTypes.put("wcard_w", new Integer(36));
		_useTypes.put("belt", new Integer(37));
		// _useTypes.put("spell_long2", new Integer(39)); // 지면 / 오브젝트 선택(원거리)
		// 5로 같은?
		_useTypes.put("earring", new Integer(40));
		_useTypes.put("fishing_rod", new Integer(42));
		_useTypes.put("rune", new Integer(44));//룬왼쪽칸
		_useTypes.put("acczel", new Integer(46));

		_armorTypes.put("none", new Integer(0));
		_armorTypes.put("helm", new Integer(1));
		_armorTypes.put("armor", new Integer(2));
		_armorTypes.put("T", new Integer(3));
		_armorTypes.put("cloak", new Integer(4));
		_armorTypes.put("glove", new Integer(5));
		_armorTypes.put("boots", new Integer(6));
		_armorTypes.put("shield", new Integer(7));
		_armorTypes.put("amulet", new Integer(8));
		_armorTypes.put("ring", new Integer(9));
		_armorTypes.put("belt", new Integer(10));
		_armorTypes.put("ring2", new Integer(11));
		_armorTypes.put("earring", new Integer(12));
		_armorTypes.put("garder", new Integer(13));
		_armorTypes.put("rune", new Integer(14));

		_weaponTypes.put("sword", new Integer(1));
		_weaponTypes.put("dagger", new Integer(2));
		_weaponTypes.put("tohandsword", new Integer(3));
		_weaponTypes.put("bow", new Integer(4));
		_weaponTypes.put("spear", new Integer(5));
		_weaponTypes.put("blunt", new Integer(6));
		_weaponTypes.put("staff", new Integer(7));
		_weaponTypes.put("throwingknife", new Integer(8));
		_weaponTypes.put("arrow", new Integer(9));
		_weaponTypes.put("gauntlet", new Integer(10));
		_weaponTypes.put("claw", new Integer(11));
		_weaponTypes.put("edoryu", new Integer(12));
		_weaponTypes.put("singlebow", new Integer(13));
		_weaponTypes.put("singlespear", new Integer(14));
		_weaponTypes.put("tohandblunt", new Integer(15));
		_weaponTypes.put("tohandstaff", new Integer(16));
		_weaponTypes.put("keyringku", new Integer(17));
		_weaponTypes.put("chainsword", new Integer(18));
		_weaponTypes.put("twohandkeyringku", new Integer(19));

		_weaponId.put("sword", new Integer(4));
		_weaponId.put("dagger", new Integer(46));
		_weaponId.put("tohandsword", new Integer(50));
		_weaponId.put("bow", new Integer(20));
		_weaponId.put("blunt", new Integer(11));
		_weaponId.put("spear", new Integer(24));
		_weaponId.put("staff", new Integer(40));
		_weaponId.put("throwingknife", new Integer(2922));
		_weaponId.put("arrow", new Integer(66));
		_weaponId.put("gauntlet", new Integer(62));
		_weaponId.put("claw", new Integer(58));
		_weaponId.put("keyringku", new Integer(58));
		_weaponId.put("edoryu", new Integer(54));
		_weaponId.put("singlebow", new Integer(20));
		_weaponId.put("singlespear", new Integer(24));
		_weaponId.put("chainsword", new Integer(24));
		_weaponId.put("tohandblunt", new Integer(11));
		_weaponId.put("tohandstaff", new Integer(40));
		_weaponId.put("twohandkeyringku", new Integer(58));

		_materialTypes.put("none", new Integer(0));
		_materialTypes.put("liquid", new Integer(1));
		_materialTypes.put("web", new Integer(2));
		_materialTypes.put("vegetation", new Integer(3));
		_materialTypes.put("animalmatter", new Integer(4));
		_materialTypes.put("paper", new Integer(5));
		_materialTypes.put("cloth", new Integer(6));
		_materialTypes.put("leather", new Integer(7));
		_materialTypes.put("wood", new Integer(8));
		_materialTypes.put("bone", new Integer(9));
		_materialTypes.put("dragonscale", new Integer(10));
		_materialTypes.put("iron", new Integer(11));
		_materialTypes.put("steel", new Integer(12));
		_materialTypes.put("copper", new Integer(13));
		_materialTypes.put("silver", new Integer(14));
		_materialTypes.put("gold", new Integer(15));
		_materialTypes.put("platinum", new Integer(16));
		_materialTypes.put("mithril", new Integer(17));
		_materialTypes.put("blackmithril", new Integer(18));
		_materialTypes.put("glass", new Integer(19));
		_materialTypes.put("gemstone", new Integer(20));
		_materialTypes.put("mineral", new Integer(21));
		_materialTypes.put("oriharukon", new Integer(22));
	}

	public static ItemTable getInstance() {
		if (_instance == null) {
			_instance = new ItemTable();
		}
		return _instance;
	}

	public static void reload() {
		ItemTable oldInstance = _instance;
		_instance = new ItemTable();
		oldInstance._etcitems.clear();
		oldInstance._weapons.clear();
		oldInstance._armors.clear();
		oldInstance._race.clear();
	}

	private ItemTable() {
		_etcitems = allEtcItem();
		_weapons = allWeapon();
		_armors = allArmor();
		_race = allrace();
		buildFastLookupTable();
	}

	public int raceSize() {
		return _race.size();
	}

	private Map<Integer, L1RaceTicket> allrace() {
		Map<Integer, L1RaceTicket> result = new FastMap<Integer, L1RaceTicket>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1RaceTicket item = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from items_race");

			rs = pstm.executeQuery();
			while (rs.next()) {
				item = new L1RaceTicket();
				item.setItemId(rs.getInt("item_id"));
				item.setName(rs.getString("name"));
				item.setNameId(rs.getString("name_id"));
				item.setType((_raceTypes.get(rs.getString("item_type")))
						.intValue());
				item.setUseType(_useTypes.get("normal").intValue());
				item.setMaterial((_materialTypes.get(rs.getString("material")))
						.intValue());
				item.setType2(0);
				item.setWeight(rs.getInt("weight"));
				item.setGfxId(rs.getInt("inv_gfxid"));
				item.setGroundGfxId(rs.getInt("ground_gfxid"));
				item.set_stackable(true);
				item.setBless(1);
				item.set_price(rs.getInt("price"));
				item.setDmgSmall(0);
				item.setDmgLarge(0);
				result.put(new Integer(item.getItemId()), item);
			}
		} catch (NullPointerException e) {
			_log.log(Level.SEVERE, new StringBuilder().append(item.getName())
					.append("(" + item.getItemId() + ")").append(
							"의 읽어 들이기에 실패했습니다.").toString());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "ItemTable[]Error", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	private Map<Integer, L1EtcItem> allEtcItem() {
		Map<Integer, L1EtcItem> result = new FastMap<Integer, L1EtcItem>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1EtcItem item = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from etcitem");

			rs = pstm.executeQuery();
			while (rs.next()) {
				item = new L1EtcItem();
				item.setItemId(rs.getInt("item_id"));
				item.setName(rs.getString("name"));
				item.setNameId(rs.getString("name_id"));
				item.setType((_etcItemTypes.get(rs.getString("item_type")))
						.intValue());
				item.setUseType(_useTypes.get(rs.getString("use_type"))
						.intValue());
				// item.setType1(0); // 사용하지 않는다
				item.setType2(0);
				item.setMaterial((_materialTypes.get(rs.getString("material")))
						.intValue());
				item.setWeight(rs.getInt("weight"));
				item.setGfxId(rs.getInt("invgfx"));
				item.setGroundGfxId(rs.getInt("grdgfx"));
				item.setItemDescId(rs.getInt("itemdesc_id"));
				item.setMinLevel(rs.getInt("min_lvl"));
				item.setMaxLevel(rs.getInt("max_lvl"));
				item.setBless(rs.getInt("bless"));
				item.setTradable(rs.getInt("trade") == 0 ? true : false);
				item
						.setCantDelete(rs.getInt("cant_delete") == 1 ? true
								: false);
				item.setDmgSmall(rs.getInt("dmg_small"));
				item.setDmgLarge(rs.getInt("dmg_large"));
				item.set_stackable(rs.getInt("stackable") == 1 ? true : false);
				item.setMaxChargeCount(rs.getInt("max_charge_count"));
				item.set_locx(rs.getInt("locx"));
				item.set_locy(rs.getInt("locy"));
				item.set_mapid(rs.getShort("mapid"));
				item.set_delayid(rs.getInt("delay_id"));
				item.set_delaytime(rs.getInt("delay_time"));
				item.set_delayEffect(rs.getInt("delay_effect"));
				item.setFoodVolume(rs.getInt("food_volume"));
				item.setToBeSavedAtOnce((rs.getInt("save_at_once") == 1) ? true
						: false);
				item.setlogcheckitem(rs.getInt("checkLog"));

				result.put(new Integer(item.getItemId()), item);
			}
		} catch (NullPointerException e) {
			_log.log(Level.SEVERE, new StringBuilder().append(item.getName())
					.append("(" + item.getItemId() + ")").append(
							"의 읽어 들이기에 실패했습니다.").toString());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "ItemTable[]Error1", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}

	private Map<Integer, L1Weapon> allWeapon() {
		Map<Integer, L1Weapon> result = new FastMap<Integer, L1Weapon>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1Weapon weapon = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from weapon");

			rs = pstm.executeQuery();
			while (rs.next()) {
				weapon = new L1Weapon();
				weapon.setItemId(rs.getInt("item_id"));
				weapon.setName(rs.getString("name"));
				weapon.setNameId(rs.getString("name_id"));
				weapon.setType((_weaponTypes.get(rs.getString("type")))
						.intValue());
				weapon.setType1((_weaponId.get(rs.getString("type")))
						.intValue());
				weapon.setType2(1);
				weapon.setUseType(1);
				weapon.setMaterial((_materialTypes
						.get(rs.getString("material"))).intValue());
				weapon.setWeight(rs.getInt("weight"));
				weapon.setGfxId(rs.getInt("invgfx"));
				weapon.setGroundGfxId(rs.getInt("grdgfx"));
				weapon.setItemDescId(rs.getInt("itemdesc_id"));
				weapon.setDmgSmall(rs.getInt("dmg_small"));
				weapon.setDmgLarge(rs.getInt("dmg_large"));
				weapon.setRange(rs.getInt("range"));
				weapon.set_safeenchant(rs.getInt("safenchant"));
				weapon.setUseRoyal(rs.getInt("use_royal") == 0 ? false : true);
				weapon
						.setUseKnight(rs.getInt("use_knight") == 0 ? false
								: true);
				weapon.setUseElf(rs.getInt("use_elf") == 0 ? false : true);
				weapon.setUseMage(rs.getInt("use_mage") == 0 ? false : true);
				weapon.setUseDarkelf(rs.getInt("use_darkelf") == 0 ? false
						: true);
				weapon
						.setUseDragonKnight(rs.getInt("use_dragonknight") == 0 ? false
								: true);
				weapon
						.setUseBlackwizard(rs.getInt("use_blackwizard") == 0 ? false
								: true);
				weapon.setHitModifier(rs.getInt("hitmodifier"));
				weapon.setDmgModifier(rs.getInt("dmgmodifier"));
				weapon.set_addstr(rs.getByte("add_str"));
				weapon.set_adddex(rs.getByte("add_dex"));
				weapon.set_addcon(rs.getByte("add_con"));
				weapon.set_addint(rs.getByte("add_int"));
				weapon.set_addwis(rs.getByte("add_wis"));
				weapon.set_addcha(rs.getByte("add_cha"));
				weapon.set_addhp(rs.getInt("add_hp"));
				weapon.set_addmp(rs.getInt("add_mp"));
				weapon.set_addhpr(rs.getInt("add_hpr"));
				weapon.set_addmpr(rs.getInt("add_mpr"));
				weapon.set_addsp(rs.getInt("add_sp"));
				weapon.set_mdef(rs.getInt("m_def"));
				weapon.setDoubleDmgChance(rs.getInt("double_dmg_chance"));
				weapon.setMagicDmgModifier(rs.getInt("magicdmgmodifier"));
				weapon.set_canbedmg(rs.getInt("canbedmg"));
				weapon.setMinLevel(rs.getInt("min_lvl"));
				weapon.setMaxLevel(rs.getInt("max_lvl"));
				weapon.setBless(rs.getInt("bless"));
				weapon.setTradable(rs.getInt("trade") == 0 ? true : false);
				weapon.setCantDelete(rs.getInt("cant_delete") == 1 ? true
						: false);
				weapon
						.setHasteItem(rs.getInt("haste_item") == 0 ? false
								: true);
				weapon.setMaxUseTime(rs.getInt("max_use_time"));
				weapon.set_addDmg(rs.getInt("addDmg"));
				
				result.put(new Integer(weapon.getItemId()), weapon);
			}
		} catch (NullPointerException e) {
			_log.log(Level.SEVERE, new StringBuilder().append(weapon.getName())
					.append("(" + weapon.getItemId() + ")").append(
							"의 읽어 들이기에 실패했습니다.").toString());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "ItemTable[]Error2", e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
		return result;
	}

	private Map<Integer, L1Armor> allArmor() {
		Map<Integer, L1Armor> result = new FastMap<Integer, L1Armor>();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		L1Armor armor = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from armor");

			rs = pstm.executeQuery();
			while (rs.next()) {
				armor = new L1Armor();
				armor.setItemId(rs.getInt("item_id"));
				armor.setName(rs.getString("name"));
				armor.setNameId(rs.getString("name_id"));
				armor.setType((_armorTypes.get(rs.getString("type")))
						.intValue());
				// armor.setType1((_armorId
				// .get(rs.getString("armor_type"))).intValue()); // 사용하지 않는다
				armor.setType2(2);
				armor.setUseType((_useTypes.get(rs.getString("type")))
						.intValue());
				armor.setGrade(rs.getInt("grade"));
				armor
						.setMaterial((_materialTypes.get(rs
								.getString("material"))).intValue());
				armor.setWeight(rs.getInt("weight"));
				armor.setGfxId(rs.getInt("invgfx"));
				armor.setGroundGfxId(rs.getInt("grdgfx"));
				armor.setItemDescId(rs.getInt("itemdesc_id"));
				armor.set_ac(rs.getInt("ac"));
				armor.set_safeenchant(rs.getInt("safenchant"));
				armor.setUseRoyal(rs.getInt("use_royal") == 0 ? false : true);
				armor.setUseKnight(rs.getInt("use_knight") == 0 ? false : true);
				armor.setUseElf(rs.getInt("use_elf") == 0 ? false : true);
				armor.setUseMage(rs.getInt("use_mage") == 0 ? false : true);
				armor.setUseDarkelf(rs.getInt("use_darkelf") == 0 ? false
						: true);
				armor
						.setUseDragonKnight(rs.getInt("use_dragonknight") == 0 ? false
								: true);
				armor
						.setUseBlackwizard(rs.getInt("use_blackwizard") == 0 ? false
								: true);
				armor.setUseHighPet(rs.getInt("use_HighPet") == 0 ? false
						: true);
				armor.set_addstr(rs.getByte("add_str"));
				armor.set_addcon(rs.getByte("add_con"));
				armor.set_adddex(rs.getByte("add_dex"));
				armor.set_addint(rs.getByte("add_int"));
				armor.set_addwis(rs.getByte("add_wis"));
				armor.set_addcha(rs.getByte("add_cha"));
				armor.set_addhp(rs.getInt("add_hp"));
				armor.set_addmp(rs.getInt("add_mp"));
				armor.set_addhpr(rs.getInt("add_hpr"));
				armor.set_addmpr(rs.getInt("add_mpr"));
				armor.set_addsp(rs.getInt("add_sp"));
				armor.setMinLevel(rs.getInt("min_lvl"));
				armor.setMaxLevel(rs.getInt("max_lvl"));
				armor.set_mdef(rs.getInt("m_def"));
				armor.setDamageReduction(rs.getInt("damage_reduction"));
				armor.setWeightReduction(rs.getInt("weight_reduction"));
				armor.setHitup(rs.getInt("hit_rate")); // 공성
				armor.setDmgup(rs.getInt("dmg_rate")); // 추타
				armor.setBowHitup(rs.getInt("bow_hit_rate"));// 활명중
				armor.setBowDmgup(rs.getInt("bow_dmg_rate"));// 활타격치
				armor.setHasteItem(rs.getInt("haste_item") == 0 ? false : true);
				armor.setBless(rs.getInt("bless"));
				armor.setTradable(rs.getInt("trade") == 0 ? true : false);
				armor.setCantDelete(rs.getInt("cant_delete") == 1 ? true
						: false);
				armor.set_defense_earth(rs.getInt("defense_earth"));
				armor.set_defense_water(rs.getInt("defense_water"));
				armor.set_defense_wind(rs.getInt("defense_wind"));
				armor.set_defense_fire(rs.getInt("defense_fire"));
				armor.set_regist_stun(rs.getInt("regist_stun"));
				armor.set_regist_stone(rs.getInt("regist_stone"));
				armor.set_regist_sleep(rs.getInt("regist_sleep"));
				armor.set_regist_freeze(rs.getInt("regist_freeze"));
				armor.set_regist_sustain(rs.getInt("regist_sustain"));
				armor.set_regist_blind(rs.getInt("regist_blind"));
				armor.setMaxUseTime(rs.getInt("max_use_time"));

				result.put(new Integer(armor.getItemId()), armor);
			}
		} catch (NullPointerException e) {
			_log.log(Level.SEVERE, new StringBuilder().append(armor.getName())
					.append("(" + armor.getItemId() + ")").append(
							"의 읽어 들이기에 실패했습니다.").toString());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, "ItemTable[]Error3", e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
		return result;
	}

	private void buildFastLookupTable() {
		int highestId = 0;

		Collection<L1EtcItem> items = _etcitems.values();
		for (L1EtcItem item : items) {
			if (item.getItemId() > highestId) {
				highestId = item.getItemId();
			}
		}

		Collection<L1Weapon> weapons = _weapons.values();
		for (L1Weapon weapon : weapons) {
			if (weapon.getItemId() > highestId) {
				highestId = weapon.getItemId();
			}
		}

		Collection<L1Armor> armors = _armors.values();
		for (L1Armor armor : armors) {
			if (armor.getItemId() > highestId) {
				highestId = armor.getItemId();
			}
		}

		Collection<L1RaceTicket> races = _race.values();
		for (L1RaceTicket race : races) {
			if (race.getItemId() > highestId) {
				highestId = race.getItemId();
			}
		}

		_allTemplates = new L1Item[highestId + 1];
		L1EtcItem etcitem = null;
		for (Iterator<Integer> iter = _etcitems.keySet().iterator(); iter
				.hasNext();) {
			Integer id = iter.next();
			etcitem = _etcitems.get(id);
			_allTemplates[id.intValue()] = etcitem;
		}
		L1Weapon Weaponitem = null;
		for (Iterator<Integer> iter = _weapons.keySet().iterator(); iter
				.hasNext();) {
			Integer id = iter.next();
			Weaponitem = _weapons.get(id);
			_allTemplates[id.intValue()] = Weaponitem;
		}

		L1Armor Amoritem = null;
		for (Iterator<Integer> iter = _armors.keySet().iterator(); iter
				.hasNext();) {
			Integer id = iter.next();
			Amoritem = _armors.get(id);
			_allTemplates[id.intValue()] = Amoritem;
		}
		L1RaceTicket raceitem = null;
		for (Iterator<Integer> iter = _race.keySet().iterator(); iter.hasNext();) {
			Integer id = iter.next();
			raceitem = _race.get(id);
			_allTemplates[id.intValue()] = raceitem;
		}
	}

	public L1Item getTemplate(int id) {
		return _allTemplates[id];
	}

	public L1ItemInstance FunctionItem(L1Item temp) {
		L1ItemInstance item = null;
		if (temp.getType2() == 1) {
			item = new Weapon(temp);
			item.setWorking(true);
		} else if (temp.getType2() == 2) {
			item = new Armor(temp);
			item.setWorking(true);
		} else if (temp.getType2() == 3) {
			item.setWorking(true);
		} else if (temp.getType2() == 0) {
			switch (temp.getType()) {
			case Config.etc_arrow:
				item = new Arrow(temp);
				item.setWorking(true);
				break;
			case Config.etc_wand:
				switch (temp.getItemId()) {
				 case 40006:
				 case 140006:
						/** 생일 시스템 **/
					case 5000121:
						/** 생일 시스템 **/
				 item = new MobSpawnWand(temp);
				 item.setWorking(true);
				 break;
					case 5000683: // 9월20일gmwand.java 캐릭정보 검사막대
					case 5000684: // 캐릭장비 검사막대
					case 5000685: // 캐릭계정 검사막대
					case 5000686: // 채팅캐릭 지정막대
						item = new GMWand(temp);
						item.setWorking(true);
						break;
					case 46160: //10월1일엔피씨제거막대
						item = new FieldObject(temp);
						item.setWorking(true);
						break;
				 
				 
				case 40007:
					item = new ThunderWand(temp);
					item.setWorking(true);
					break;
				case 46091:
					item = new FireWand(temp);
					item.setWorking(true);
					break;
				case 40008:
				case 140008:
				case 45464://픽시의 변신막대
					item = new PolyWand(temp);
					item.setWorking(true);
					break;
				case 41401:
					item = new FurnitureItem(temp);
					item.setWorking(true);
					break;
				}
				break;
			case Config.etc_light:
				item = new Light(temp);
				item.setWorking(true);
				break;
			case Config.etc_gem:
				if (temp.getItemId() >= 40931 && temp.getItemId() <= 40958) {
					item = new Choiceitem(temp);
					item.setWorking(true);
				}
				break;
			case Config.etc_firecracker:
				item = new Firecracker(temp);
				item.setWorking(true);
				break;
			case Config.etc_potion:
				switch (temp.getItemId()) {
				case 40010:
				case 40011:
				case 40012:
				case 40019:
				case 40020:
				case 40021:
				case 40022:
				case 40023:
				case 40024:
				case 404081:
				case 40026:
				case 40027:
				case 40028:
				case 40029:
				case 40043:
				case 50006:
				case 40058:
				case 40071:
				case 40734:
				case 40930:
				case 41141:
				case 41298:
				case 41299:
				case 41300:
				case 41337:
				case 41403:
				case 140010:
				case 140011:
				case 140012:
				case 240010:
				case 410003:
				case 435000:
				case L1ItemId.MOPO_JUiCE1:   // 추가
				case L1ItemId.MOPO_JUiCE2:   // 추가
					item = new HealingPotion(temp);
					item.setWorking(true);
					break;
				case 40015:
				case 40736:
				case 41142:
				case 140015:
				case 50017:// 복지마나물약
				case 404082://픽시의 마나 포션
					item = new BluePotion(temp);
					item.setWorking(true);
					break;
				case 40033:
				case 40034:
				case 40035:
				case 40036:
				case 40037:
				case 40038:
				case 55700://픽시의엘릭서
					item = new Elixir(temp);
					item.setWorking(true);
					break;
				case 40013:
				case 40018:
				case 50018:// 복지속도물약
				case 40030:
				case 40039:
				case 40040:
				case 41261:
				case 41262:
				case 41268:
				case 41269:
				case 41271:
				case 41272:
				case 41273:
				case 41338:
				case 140013:
				case 140018:
					item = new GreenPotion(temp);
					item.setWorking(true);
					break;
				case 40014:
				case 40031:
				case 40068:
				case 41415:
				case 50014:// 복지용기물약
				case 140014:
				case 140068:
				case 50015:// 복지집중물약
				case 430006:
					item = new BravePotion(temp);
					item.setWorking(true);
					break;
				case 40032:
				case 50019:// 복지호흡물약
				case 40041:
				case 41344:
					item = new BlessOfEvaPotion(temp);
					item.setWorking(true);
					break;
				case 40016:
				case 140016:
				case 50016:// 복지지혜물약
					item = new WisdomPotion(temp);
					item.setWorking(true);
					break;
				case 40025:
					item = new BlindPotion(temp);
					item.setWorking(true);
					break;
				}
				break;
			case Config.etc_food:
				item = new Food(temp);
				item.setWorking(true);
				break;
			case Config.etc_scroll:
				switch (temp.getItemId()) {
				case 40098:
				case 40126:
					item = new DefiniteScroll(temp);
					item.setWorking(true);
					break;
				case 40090:
				case 40091:
				case 40092:
				case 40093:
				case 40094:
					item = new BlankScroll(temp);
					item.setWorking(true);
					break;
				case 40079:
				case 40081:
				case 40117:
				case 40086:
				case 40095:
				case 40099:
				case 40100:
				case 40124:
				case 40521:
				case 40863:
				case 41159:
				case 140100:
				case 240100:
					item = new TeleportScroll(temp);
					item.setWorking(true);
					break;
				case L1ItemId.SCROLL_OF_ENCHANT_ARMOR_WEAPON:// 고대의 강화 주문서
					item = new EnchantArmor(temp);
					item = new EnchantWeapon(temp);
					item.setWorking(true);
					break;
				case 40074:
				case 40078:
				case 40127:
				case 40129:
				case 140074:
				case 140129:
				case 240074:
				case 430014:
				case 437006:
				case 540341:
				case 437027:
				case 5000145: //룸티스강화주문서
				case L1ItemId.Inadril_T_ScrollA:// 인나티
				case L1ItemId.Inadril_T_ScrollB:// 인나티
				case L1ItemId.Inadril_T_ScrollC:// 인나티
				case L1ItemId.Inadril_T_ScrollD:// 인나티
				case L1ItemId.TEST_ENCHANT_ARMOR:
				case L1ItemId.HALLOWEEN_2011_ACCESSORY_ENCHANT_SCROLL://할로윈이벤트
					item = new EnchantArmor(temp);
					item.setWorking(true);
					break;
				case 40077:
				case 40087:
				case 40128:
				case 40130:
				case 140087:
				case 140130:
				case 240087:
				case 430010:
				case 430011:
				case 430012:
				case 430013:
				case 437007:
				case 540342:
				case 600006:	//이그니스
				case 600007:	//운디네
				case 600008:	//실프
				case 600009:	//클레이
				case L1ItemId.TEST_ENCHANT_WEAPON:
					item = new EnchantWeapon(temp);
					item.setWorking(true);
					break;
				case 40088:
				case 40096:
				case 140088:
				case 50022:// 복지변신물약
					item = new PolyScroll(temp);
					item.setWorking(true);
					break;
				case 40089:
				case 140089:
					item = new ResurrectionScroll(temp);
					item.setWorking(true);
					break;
				case 50020:
				case 50021:
				case 401262:
				case 401261:
					item = new SealScroll(temp);
					item.setWorking(true);
					break;
				case 430015:
				case 430016:
				case 430017:
				case 430018:
				case 430019:
				case 430020:
				case 430021:
				case 435016:
				case 435017:
					item = new PolyItem(temp);
					item.setWorking(true);
					break;
				}
				break;
			case Config.etc_questitem:
				switch (temp.getItemId()) {
				case 41342:
					item = new GreenPotion(temp);
					item.setWorking(true);
					break;
				case 40660:
					item = new EnchantWeapon(temp);
					item.setWorking(true);
					break;
				case 41048:
				case 41049:
				case 41050:
				case 41051:
				case 41052:
				case 41053:
				case 41054:
				case 41055:
				case 41056:
				case 41057:
				case L1ItemId.MOPO_CLCOK1:   // 추가
				case L1ItemId.MOPO_CLCOK2:   // 추가
					item = new Choiceitem(temp);
					item.setWorking(true);
					break;
				}
				break;
			case Config.etc_spellbook:
				item = new Spellbook(temp);
				item.setWorking(true);
				break;
			case Config.etc_other:
				switch (temp.getItemId()) {
				case 560025:
			    case 560026:
			    case 560027:
			    case 560028:
			     item = new TelBookItem(temp);
			     item.setWorking(true);
			     break;
				case 41245:
					item = new Resolvent(temp);
					item.setWorking(true);
					break;
				case 41255:
				case 41256:
				case 41257:
				case 41258:
				case 41259:
					item = new MakeCooking(temp);
					item.setWorking(true);
					break;
				case 41293:
				case 41294:
					item = new Fishing(temp);
					item.setWorking(true);
					break;
				case 41248:
				case 41249:
				case 41250:
				case 430000:
				case 430001:
				case 430002:
				case 430003:
				case 430004:
				case 500400: // 스켈
				case L1ItemId.DOLL_COCATRIS:
				case L1ItemId.DOLL_DRAGON_M:
				case L1ItemId.DOLL_DRAGON_W:
				case L1ItemId.DOLL_HIGH_DRAGON_M:
				case L1ItemId.DOLL_HIGH_DRAGON_W:
				case L1ItemId.DOLL_LAMIA:
				case 437018:
				case L1ItemId.DOLL_SCARECROW: 
				case L1ItemId.DOLL_ETTIN: 
				case L1ItemId.DOLL_RANKING_ONE:
				case L1ItemId.DOLL_RANKING_TWO:
				case L1ItemId.DOLL_RANKING_THREE:
				case L1ItemId.DOLL_RANKING_FOR:
					
				case L1ItemId.DOLL_RANKING_ONE1:
				case L1ItemId.DOLL_RANKING_TWO2:
				case L1ItemId.DOLL_RANKING_THREE3:
				case L1ItemId.DOLL_RANKING:	
				case 500144:// 눈사람인형
				case 500145:// 눈사람인형
				case 500146: // 눈사람인형
					
					/*** 싸이 인형 ****/
				   case L1ItemId.DOLL_PSY_CHAMPION:
			       case L1ItemId.DOLL_PSY_BIRD:
			       case L1ItemId.DOLL_PSY_GANGNAM_STYLE:

					
					item = new MagicDoll(temp);
					item.setWorking(true);
					break;
				case 41383:
				case 41384:
				case 41385:
				case 41386:
				case 41387:
				case 41388:
				case 41389:
				case 41390:
				case 41391:
				case 41392:
				case 41393:
				case 41394:
				case 41395:
				case 41396:
				case 41397:
				case 41398:
				case 41399:
				case 41400:
					item = new FurnitureItem(temp);
					item.setWorking(true);
					break;
				case 600000:
				    item = new Special_Other(temp);
					item.setWorking(true);
					break;
					/**
					 *  2012. 2. 29 본섭 판도라 티셔츠 
					 */
					case 89530: // 천연 비누:판도라 향기티
					case 89531: // 판도라 향수 89531 ~ 89536
					case 89532:
					case 89533:
					case 89534:
					case 89535:
					case 89536:
					case 89550: // 판도라 문양 89550 ~ 89558
					case 89551: 
					case 89552:
					case 89553:
					case 89554:
					case 89555:
					case 89556:
					case 89557:
					case 89558:
					case 46030: // 변환석
					case 46031:
					case 46032:
					case 46033:
					case 46034:
					case 46035:
					case 46036:
					case 46037:
					case 46038:
					case 46039:
					case 46040:
					case 46041:
						item = new PandoraT(temp);
						item.setWorking(true);
						break;
				}
				break;
			case Config.etc_material:
				switch (temp.getItemId()) {
				case 40506:
				case 140506:
					item = new HealingPotion(temp);
					item.setWorking(true);
					break;
				case 40410:
					item = new PolyWand(temp);
					item.setWorking(true);
					break;
				case 40412:
					item = new MobSpawnWand(temp);
					item.setWorking(true);
					break;
				case 41143:
				case 41144:
				case 41145:
				case 41154:
				case 41155:
				case 41156:
				case 41157:
					item = new PolyItem(temp);
					item.setWorking(true);
					break;
				}
				break;
			case Config.etc_sting:
				item = new Sting(temp);
				item.setWorking(true);
				break;
			case Config.etc_treasurebox:
				item = new TreasureBox(temp);
				item.setWorking(true);
				break;
			default:
			}
		}
		if (item != null)
			return item;
		else
			return item = new L1ItemInstance(temp);
	}

	public L1ItemInstance createItem(int itemId) {
		L1Item temp = getTemplate(itemId);
		if (temp == null) {
			return null;
		}
		L1ItemInstance item = FunctionItem(temp);
		item.setId(ObjectIdFactory.getInstance().nextId());
		item.setItem(temp);
		item.setBless(temp.getBless());
		L1World.getInstance().storeObject(item);
		return item;
	}

	public int findItemIdByName(String name) {
		int itemid = 0;
		for (L1Item item : _allTemplates) {
			if (item != null && item.getName().equals(name)) {
				itemid = item.getItemId();
				break;
			}
		}
		return itemid;
	}

	public int findItemIdByNameWithoutSpace(String name) {
		int itemid = 0;
		for (L1Item item : _allTemplates) {
			if (item != null && item.getName().replace(" ", "").equals(name)) {
				itemid = item.getItemId();
				break;
			}
		}
		return itemid;
	}

	/** 새로운 Template 객체를 생성(복사) */
	public L1Item clone(L1Item item, String name) {
		// 이름때문에..
		// 무기
		if (item.getType2() == 1) {
			L1Weapon weapon = new L1Weapon();
			weapon.setItemId(item.getItemId());
			weapon.setName(item.getName());
			weapon.setNameId(item.getNameId());
			weapon.setType(item.getType());
			weapon.setType1(item.getType1());
			weapon.setType2(1);
			weapon.setUseType(1);
			weapon.setMaterial(item.getMaterial());
			weapon.setWeight(item.getWeight());
			weapon.setGfxId(item.getGfxId());
			weapon.setGroundGfxId(item.getGroundGfxId());
			weapon.setItemDescId(item.getItemDescId());
			weapon.setDmgSmall(item.getDmgSmall());
			weapon.setDmgLarge(item.getDmgLarge());
			weapon.set_safeenchant(item.get_safeenchant());
			weapon.setUseRoyal(item.isUseRoyal());
			weapon.setUseKnight(item.isUseKnight());
			weapon.setUseElf(item.isUseElf());
			weapon.setUseMage(item.isUseMage());
			weapon.setUseDarkelf(item.isUseDarkelf());
			weapon.setUseDragonKnight(item.isUseDragonKnight());
			weapon.setUseBlackwizard(item.isUseBlackwizard());
			weapon.setHitModifier(item.getHitModifier());
			weapon.setDmgModifier(item.getDmgModifier());
			weapon.set_addstr(item.get_addstr());
			weapon.set_adddex(item.get_adddex());
			weapon.set_addcon(item.get_addcon());
			weapon.set_addint(item.get_addint());
			weapon.set_addwis(item.get_addwis());
			weapon.set_addcha(item.get_addcha());
			weapon.set_addhp(item.get_addhp());
			weapon.set_addmp(item.get_addmp());
			weapon.set_addhpr(item.get_addhpr());
			weapon.set_addmpr(item.get_addmpr());
			weapon.set_addsp(item.get_addsp());
			weapon.set_mdef(item.get_mdef());
			weapon.setDoubleDmgChance(item.getDoubleDmgChance());
			weapon.setMagicDmgModifier(item.getMagicDmgModifier());
			weapon.set_canbedmg(item.get_canbedmg());
			weapon.setMinLevel(item.getMinLevel());
			weapon.setMaxLevel(item.getMaxLevel());
			weapon.setBless(item.getBless());
			weapon.setTradable(item.isTradable());
			weapon.setCantDelete(item.isCantDelete());
			weapon.setHasteItem(item.isHasteItem());
			weapon.setMaxUseTime(item.getMaxUseTime());
			weapon.set_addDmg(item.getaddDmg());
			return weapon;
			// 방어구
		} else if (item.getType2() == 2) {
			L1Armor armor = new L1Armor();
			armor.setItemId(item.getItemId());
			armor.setName(item.getName());
			armor.setNameId(item.getNameId());
			armor.setType(item.getType());
			armor.setType2(2);
			armor.setUseType(item.getUseType());
			armor.setMaterial(item.getMaterial());
			armor.setWeight(item.getWeight());
			armor.setGfxId(item.getGfxId());
			armor.setGroundGfxId(item.getGroundGfxId());
			armor.setItemDescId(item.getItemDescId());
			armor.set_ac(item.get_ac());
			armor.set_safeenchant(item.get_safeenchant());
			armor.setUseRoyal(item.isUseRoyal());
			armor.setUseKnight(item.isUseKnight());
			armor.setUseElf(item.isUseElf());
			armor.setUseMage(item.isUseMage());
			armor.setUseDarkelf(item.isUseDarkelf());
			armor.setUseDragonKnight(item.isUseDragonKnight());
			armor.setUseBlackwizard(item.isUseBlackwizard());
			armor.setUseHighPet(item.isUseHighPet());
			armor.set_addstr(item.get_addstr());
			armor.set_addcon(item.get_addcon());
			armor.set_adddex(item.get_adddex());
			armor.set_addint(item.get_addint());
			armor.set_addwis(item.get_addwis());
			armor.set_addcha(item.get_addcha());
			armor.set_addhp(item.get_addhp());
			armor.set_addmp(item.get_addmp());
			armor.set_addhpr(item.get_addhpr());
			armor.set_addmpr(item.get_addmpr());
			armor.set_addsp(item.get_addsp());
			armor.setMinLevel(item.getMinLevel());
			armor.setMaxLevel(item.getMaxLevel());
			armor.set_mdef(item.get_mdef());
			armor.setDamageReduction(item.getDamageReduction());
			armor.setWeightReduction(item.getWeightReduction());
			armor.setBowHitup(item.getBowHitup());
			armor.setHasteItem(item.isHasteItem());
			armor.setBless(item.getBless());
			armor.setTradable(item.isTradable());
			armor.setCantDelete(item.isCantDelete());
			armor.set_defense_earth(item.get_defense_earth());
			armor.set_defense_water(item.get_defense_water());
			armor.set_defense_wind(item.get_defense_wind());
			armor.set_defense_fire(item.get_defense_fire());
			armor.set_regist_stun(item.get_regist_stun());
			armor.set_regist_stone(item.get_regist_stone());
			armor.set_regist_sleep(item.get_regist_sleep());
			armor.set_regist_freeze(item.get_regist_freeze());
			armor.set_regist_sustain(item.get_regist_sustain());
			armor.set_regist_blind(item.get_regist_blind());
			armor.setMaxUseTime(item.getMaxUseTime());
			return armor;
		} else if (item.getType2() == 0) {
			L1EtcItem etc = new L1EtcItem();
			etc.setItemId(item.getItemId());
			etc.setName(name);
			etc.setNameId(item.getNameId());
			etc.setType(item.getType());
			etc.setUseType(item.getUseType());
			etc.setType2(0);
			etc.setMaterial(item.getMaterial());
			etc.setWeight(item.getWeight());
			etc.setGfxId(item.getGfxId());
			etc.setGroundGfxId(item.getGroundGfxId());
			etc.setItemDescId(item.getItemDescId());
			etc.setMinLevel(item.getMinLevel());
			etc.setMaxLevel(item.getMaxLevel());
			etc.setBless(item.getBless());
			etc.setTradable(item.isTradable());
			etc.setCantDelete(item.isCantDelete());
			etc.setDmgSmall(item.getDmgSmall());
			etc.setDmgLarge(item.getDmgLarge());
			etc.set_stackable(item.isStackable());
			etc.setMaxChargeCount(item.getMaxChargeCount());
			etc.set_locx(item.get_locx());
			etc.set_locy(item.get_locy());
			etc.set_mapid(item.get_mapid());
			etc.set_delayid(item.get_delayid());
			etc.set_delaytime(item.get_delaytime());
			etc.set_delayEffect(item.get_delayEffect());
			etc.setFoodVolume(item.getFoodVolume());
			etc.setToBeSavedAtOnce(item.isToBeSavedAtOnce());
			return etc;
		} else if (item.getType2() == 3) {
			L1RaceTicket race = new L1RaceTicket();
			race.setItemId(item.getItemId());
			race.setName(item.getName());
			race.setNameId(item.getNameId());
			race.setType(item.getType());
			race.setMaterial(item.getMaterial());
			race.setWeight(item.getWeight());
			race.setGfxId(item.getGfxId());
			race.setGroundGfxId(item.getGroundGfxId());
			race.set_stackable(true);
			race.set_price(item.get_price());
			return item;
		}
		return null;
	}
}
