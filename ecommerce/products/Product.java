package products;

public interface Product {
    String getName();
    double getPrice();
    int getStock();
    void reduceStock(int quantity);
}
