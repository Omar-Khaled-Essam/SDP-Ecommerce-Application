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
    public List<User> getAllUsers() { return adapter.getAllUsers(); }
    public void updatePassword(String username, String newPassword) { adapter.updatePassword(username, newPassword); }
    public void deleteUser(String username) { adapter.deleteUser(username); }
    public boolean usernameExists(String username) { return adapter.usernameExists(username); }
    public boolean isPasswordResetRequired(String username) { return adapter.isPasswordResetRequired(username); }
    public void resetPasswordByAdmin(String username) { adapter.resetPasswordByAdmin(username); }

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
    public void updateProduct(Product product) { adapter.updateProduct(product); }

    // ---------- ORDERS ----------
    public void addOrder(Order o) {
        adapter.addOrder(o);
    }

    public List<Order> getOrders() {
        return adapter.getOrders();
    }
    public List<Order> getOrdersBetween(java.time.LocalDate start, java.time.LocalDate end) {
        return adapter.getOrdersBetween(start, end);
    }
    public double getTotalSalesBetween(java.time.LocalDate start, java.time.LocalDate end) {
        return adapter.getTotalSalesBetween(start, end);
    }
    public List<Order> getOrdersByCustomer(String username) {
        return adapter.getOrdersByCustomer(username);
    }
    public double getTotalSpentForCustomer(String username) {
        return adapter.getTotalSpentForCustomer(username);
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

    public List<Product> getLowStock(int threshold) {
        return adapter.getLowStock(threshold);
    }
}
