package products;

public class ElectronicsFactory implements ProductFactory {
    public Product createProduct() {
        return new Electronics("Laptop", 1200, 10);
    }
}
