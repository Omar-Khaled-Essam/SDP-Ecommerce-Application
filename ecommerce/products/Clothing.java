package products;

public class Clothing implements Product {
    private String name;
    private double price;
    private int stock;

    public Clothing(String name, double price, int stock) {
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
