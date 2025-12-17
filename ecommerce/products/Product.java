package products;

public abstract class Product {
    protected String name;
    protected String category;
    protected double price;
    protected int stock;
    protected String description;

    public Product(String name, String category, double price, int stock, String desc) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.description = desc;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getDescription() { return description; }

    public void setPrice(double p) { price = p; }
    public void setStock(int s) { stock = s; }

    public void reduceStock(int qty) { stock -= qty; }
}
