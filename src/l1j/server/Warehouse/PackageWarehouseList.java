package l1j.server.Warehouse;

public class PackageWarehouseList extends WarehouseList {
	@Override
	protected PackageWarehouse createWarehouse(String name) {
		return new PackageWarehouse(name);
	}
}
