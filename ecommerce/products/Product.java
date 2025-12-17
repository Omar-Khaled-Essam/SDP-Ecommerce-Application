package products;

public abstract class Product {
    protected String name;
    protected double price;
    protected int stock;

    public Product(String n, double p, int s) {
        name = n;
        price = p;
        stock = s;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    public void reduceStock() { stock--; }
}
