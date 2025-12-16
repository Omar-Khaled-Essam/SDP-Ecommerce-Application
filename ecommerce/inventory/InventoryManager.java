package inventory;

import products.*;

public class InventoryManager {

    private static InventoryManager instance;

    private InventoryManager() {}

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    public void updateStock(Product product, int quantity) {
        product.reduceStock(quantity);

        if (product.getStock() < 5) {
            System.out.println("[Alert] Low stock for " + product.getName());
        }
    }
}
