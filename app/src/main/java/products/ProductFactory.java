package products;

// SOLID: DIP✅ (creation through abstraction), OCP✅ (new factories can be added without modifying callers).
public interface ProductFactory {
    Product createProduct();
}
