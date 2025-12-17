package products;

public class ClothingFactory implements ProductFactory {
    public Product createProduct() {
        return new Clothing("T-Shirt", 30, 20);
    }
}
