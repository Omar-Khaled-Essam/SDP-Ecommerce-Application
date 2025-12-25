package products;

// SOLID: ISP⚠️ (includes setters; consumers that only read products still depend on mutation API).
public interface Product {
    String getName();

    String getCategory();

    double getPrice();

    int getStock();

    String getDescription();

    void reduceStock(int qty);

    void setName(String name);

    void setCategory(String category);

    void setPrice(double price);

    void setStock(int stock);

    void setDescription(String description);
}
