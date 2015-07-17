package l1j.server.Warehouse;

public class ElfWarehouseList extends WarehouseList {
	@Override
	protected ElfWarehouse createWarehouse(String name) {
		return new ElfWarehouse(name);
	}
}
