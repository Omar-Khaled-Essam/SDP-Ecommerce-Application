package products;

public class Electronics implements Product {

    private String name;
    private String category;
    private double price;
    private int stock;
    private String description;

    public Electronics(String name, String category,
                       double price, int stock, String description) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getDescription() { return description; }

    public void reduceStock(int qty) {
        stock -= qty;
    }

    @Override
    public String toString() {
        return name + " | " + category + " | " + price + " | Stock: " + stock;
    }
}
