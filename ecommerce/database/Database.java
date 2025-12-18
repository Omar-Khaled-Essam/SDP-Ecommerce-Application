package database;

import users.*;
import products.*;
import orders.*;
import java.util.*;

public class Database {

    private static Database instance = new Database();

    private List<User> users = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();

    private Database() {}

    public static Database getInstance() {
        return instance;
    }

    // ---------- USERS ----------
    public void registerUser(User u) {
        users.add(u);
    }

    public User authenticate(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) &&
                u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    // ---------- PRODUCTS ----------
    public List<Product> getProducts() {
        return products;
    }

public void seedProducts() {
    if (!products.isEmpty()) return;

    products.add(new Electronics(
            "Laptop",
            "Electronics",
            15000,
            10,
            "High performance laptop"
    ));

    products.add(new Electronics(
            "Phone",
            "Electronics",
            8000,
            15,
            "Smartphone"
    ));

    products.add(new Clothing(
            "T-Shirt",
            "Clothing",
            500,
            20,
            "Cotton T-Shirt"
    ));

    products.add(new Clothing(
            "Jacket",
            "Clothing",
            1200,
            8,
            "Winter jacket"
    ));
}



    // ---------- ORDERS ----------
    public void addOrder(Order o) {
        orders.add(o);
    }

    public List<Order> getOrders() {
        return orders;
    }

    public double getTotalSales() {
        return orders.stream().mapToDouble(Order::getTotal).sum();
    }
}
