package products;

public class ElectronicsFactory implements ProductFactory {

    @Override
    public Product createProduct() {
        return new Electronics(
                "Default Laptop",
                "Electronics",
                15000,
                10,
                "Factory default electronics product"
        );
    }
}
