package products;

public class Electronics implements Product {
    private String name;
    private double price;
    private int stock;

    public Electronics(String name, double price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    public void reduceStock(int qty) {
        stock -= qty;
    }
}
