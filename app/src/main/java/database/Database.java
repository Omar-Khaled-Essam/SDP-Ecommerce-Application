package database;

import users.*;
import products.*;
import orders.*;
import java.util.List;

public class Database {

    private static Database instance = new Database();
    private final DatabaseAdapter adapter;

    private Database() {
        adapter = new SQLiteDatabaseAdapter("data/ecommerce.db");
        adapter.init();
        adapter.seedDefaults();
    }

    public static Database getInstance() {
        return instance;
    }

    // ---------- USERS ----------
    public void registerUser(User u) {
        adapter.registerUser(u);
    }

    public User authenticate(String username, String password) {
        return adapter.authenticate(username, password);
    }

    public List<Customer> getCustomers() {
        return adapter.getCustomers();
    }

    // ---------- PRODUCTS ----------
    public List<Product> getProducts() {
        return adapter.getProducts();
    }

    public void addProduct(Product product) {
        adapter.addProduct(product);
    }

    public void deleteProduct(Product product) {
        adapter.deleteProduct(product);
    }

    // ---------- ORDERS ----------
    public void addOrder(Order o) {
        adapter.addOrder(o);
    }

    public List<Order> getOrders() {
        return adapter.getOrders();
    }

    public void updateOrderStatus(Order order, String status) {
        adapter.updateOrderStatus(order, status);
        order.setStatus(status);
    }

    public double getTotalSales() {
        return adapter.getTotalSales();
    }

    public void seedDefaults() {
        adapter.seedDefaults();
    }
}
