package products;

// SOLID: SRP✅ (one responsibility: create an Electronics product).
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
