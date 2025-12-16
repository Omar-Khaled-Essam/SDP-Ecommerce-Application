package database;

import java.util.*;
import users.*;
import products.*;
import orders.*;

public class Database {

    private static Database instance;

    private List<User> users = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();

    private Database() {}

    public static Database getInstance() {
        if (instance == null)
            instance = new Database();
        return instance;
    }

    public List<User> getUsers() { return users; }
    public List<Product> getProducts() { return products; }
    public List<Order> getOrders() { return orders; }

    // ----------------------------
    // INITIAL DATA
    // ----------------------------
    public void seedUsers() {
        if (users.isEmpty()) {
            users.add(new Admin("admin", "admin123"));
            users.add(new Customer("omar", "pass"));
        }
    }

    public void seedProducts() {
        if (products.isEmpty()) {
            products.add(new Electronics("Laptop", 1200, 10));
            products.add(new Electronics("Phone", 800, 15));
            products.add(new Clothing("T-Shirt", 25, 50));
            products.add(new Clothing("Jacket", 60, 20));
        }
    }

    // ----------------------------
    // LOGIN AUTH
    // ----------------------------
    public User authenticate(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) &&
                u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    // ----------------------------
    // SALES REPORT
    // ----------------------------
    public double getTotalSales() {
        double total = 0;
        for (Order o : orders) {
            total += o.getTotalAmount();
        }
        return total;
    }
}
