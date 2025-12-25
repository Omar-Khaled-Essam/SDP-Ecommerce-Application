package products;

// SOLID: SRP✅ (product data/behavior only).
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

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getDescription() {
        return description;
    }

    public void reduceStock(int qty) {
        stock -= qty;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name + " | " + category + " | " + price + " | Stock: " + stock;
    }
}
