/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.templates;

public class L1RaceTicket extends L1Item {

private static final long serialVersionUID = 1L;
	private int _winner;

	private float _dividend;

	public L1RaceTicket() {
	}

	public L1RaceTicket(int winner, float dividend) {
		_winner = winner;
		_dividend = dividend;
	}

	public int getWinner() {
		return _winner;
	}

	public float getDividend() {
		return _dividend;
	}

	private int _itemid;

	public void setItemId(int i) {
		_itemid = i;
	}

	public int getItemId() {
		return _itemid;
	}

	private String _name;

	public void setName(String s) {
		_name = s;
	}

	public String getName() {
		return _name;
	}

	private String _nameid;

	public void setNameId(String s) {
		_nameid = s;
	}

	public String getNameId() {
		return _nameid;
	}

	private int _type;

	public void setType(int i) {
		_type = i;
	}

	public int getType() {
		return _type;
	}

	private int _material;

	public void setMaterial(int i) {
		_material = i;
	}

	public int getMaterial() {
		return _material;
	}

	private int _weight;

	public void setWeight(int i) {
		_weight = i;
	}

	public int getWeight() {
		return _weight;
	}

	private int _gfxid;

	public void setGfxId(int i) {
		_gfxid = i;
	}

	public int getGfxId() {
		return _gfxid;
	}

	private int _grdgfxid;

	public void setGroundGfxId(int i) {
		_grdgfxid = i;
	}

	public int getGroundGfxId() {
		return _grdgfxid;
	}

	private int _price;

	public void set_price(int i) {
		_price = i;
	}

	public int get_price() {
		return _price;
	}

	private boolean _stackable;

	public boolean isStackable() {
		return _stackable;
	}

	public void set_stackable(boolean stackable) {
		_stackable = stackable;
	}

}
