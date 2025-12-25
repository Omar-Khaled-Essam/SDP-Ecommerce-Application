package products;

// SOLID: SRP✅ (one responsibility: create a Clothing product).
public class ClothingFactory implements ProductFactory {

    @Override
    public Product createProduct() {
        return new Clothing(
                "T-Shirt",
                "Clothing",
                500,
                20,
                "Cotton T-Shirt"
        );
    }
}
