package products;

public class ClothingFactory implements ProductFactory {

    @Override
    public Product createProduct() {
        return new Clothing("Generic Clothing", 150, 20);
    }
}

