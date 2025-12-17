package users;

import products.Product;
import orders.Order;
import orders.Observer;
import orders.OrderStatus;
import java.util.*;

public class Customer extends User implements Observer {

    private List<Product> cart = new ArrayList<>();
    private List<Order> orderHistory = new ArrayList<>();
    private double totalSpent = 0;

    public Customer(String u, String p) {
        super(u, p);
    }

    // CART
    public void addToCart(Product p) {
        cart.add(p);
    }

    public void removeFromCart(Product p) {
        cart.remove(p);
    }

    public List<Product> getCart() {
        return cart;
    }

    public void clearCart() {
        cart.clear();
    }

    // ORDERS
    public void addOrder(Order o) {
        orderHistory.add(o);
        totalSpent += o.getTotal();
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    @Override
    public void update(OrderStatus status) {
        // Handle order status update, e.g., notify user
        System.out.println("Order status updated to: " + status);
    }
}
