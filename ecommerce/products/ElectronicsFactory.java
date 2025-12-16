package products;

public class ElectronicsFactory implements ProductFactory {

    @Override
    public Product createProduct() {
        return new Electronics("Generic Electronics", 500, 10);
    }
}

