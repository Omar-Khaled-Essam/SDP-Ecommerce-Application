package database;

import orders.Order;
import products.Product;
import users.Customer;
import users.User;

import java.time.LocalDate;
import java.util.List;

public interface DatabaseAdapter {
    void init();

    void seedDefaults();

    void registerUser(User user);

    User authenticate(String username, String password);

    List<Customer> getCustomers();

    List<Product> getProducts();

    void addProduct(Product product);

    void deleteProduct(Product product);

    void updateProduct(Product product);

    void addOrder(Order order);

    List<Order> getOrders();

    List<Order> getOrdersByCustomer(String username);

    List<Order> getOrdersBetween(LocalDate start, LocalDate end);

    void updateOrderStatus(Order order, String status);

    double getTotalSales();

    double getTotalSalesBetween(LocalDate start, LocalDate end);

    double getTotalSpentForCustomer(String username);

    void updatePassword(String username, String newPassword);

    void deleteUser(String username);

    List<User> getAllUsers();

    boolean usernameExists(String username);

    boolean isPasswordResetRequired(String username);

    void resetPasswordByAdmin(String username);

    List<Product> getLowStock(int threshold);
}
