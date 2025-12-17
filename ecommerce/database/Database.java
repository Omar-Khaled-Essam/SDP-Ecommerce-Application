package database;

import users.*;
import products.*;
import orders.*;

import java.util.*;

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

    public void registerUser(User u) {
        users.add(u);
    }

    public User authenticate(String u, String p) {
        return users.stream()
                .filter(x -> x.getUsername().equals(u) && x.getPassword().equals(p))
                .findFirst().orElse(null);
    }

    public List<Product> getProducts() { return products; }
    public List<Order> getOrders() { return orders; }

    public void seedProducts() {
        if (products.isEmpty()) {
            products.add(new Electronics("Laptop", 1200, 10));
            products.add(new Clothing("Jacket", 80, 15));
        }
    }
}
