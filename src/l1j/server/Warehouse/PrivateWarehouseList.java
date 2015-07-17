package l1j.server.Warehouse;

public class PrivateWarehouseList extends WarehouseList {
	@Override
	protected PrivateWarehouse createWarehouse(String name) {
		return new PrivateWarehouse(name);
	}
}
