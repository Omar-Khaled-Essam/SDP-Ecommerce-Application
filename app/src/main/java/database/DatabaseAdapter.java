package database;

import orders.Order;
import products.Product;
import users.Customer;
import users.User;

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

    void addOrder(Order order);

    List<Order> getOrders();

    void updateOrderStatus(Order order, String status);

    double getTotalSales();
}
