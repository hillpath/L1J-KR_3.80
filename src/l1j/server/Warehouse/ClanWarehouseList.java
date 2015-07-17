package l1j.server.Warehouse;

public class ClanWarehouseList extends WarehouseList {
	@Override
	protected ClanWarehouse createWarehouse(String name) {
		return new ClanWarehouse(name);
	}
}