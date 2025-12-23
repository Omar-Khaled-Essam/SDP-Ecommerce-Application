package products;

public interface Product {
    String getName();
    String getCategory();
    double getPrice();
    int getStock();
    String getDescription();
    void reduceStock(int qty);
}
