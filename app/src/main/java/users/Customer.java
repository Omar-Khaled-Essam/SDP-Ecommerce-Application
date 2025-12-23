package users;

import products.Product;
import orders.Order;
import orders.Observer;
import java.util.*;

public class Customer extends User implements Observer {

    private List<Product> cart = new ArrayList<>();
    private List<Order> orderHistory = new ArrayList<>();

    public Customer(String u, String p) {
        super(u, p);
    }

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

    public void addOrder(Order o) {
        orderHistory.add(o);
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }

    public double getTotalSpent() {
        return orderHistory.stream().mapToDouble(Order::getTotal).sum();
    }

    @Override
    public void update(String status) {
        System.out.println("Order updated: " + status);
    }
}
