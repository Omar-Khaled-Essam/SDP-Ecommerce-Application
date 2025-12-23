package products;

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
