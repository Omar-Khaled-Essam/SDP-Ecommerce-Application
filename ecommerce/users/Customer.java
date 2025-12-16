package users;

import java.util.*;
import orders.*;
import products.*;

public class Customer extends User implements orders.Observer {

    private List<Order> orderHistory = new ArrayList<>();
    private List<Product> cart = new ArrayList<>();

    public Customer(String username, String password) {
        super(username, password);
    }

    @Override
    public void update(String status) {
        System.out.println("[Notification] Order status changed to: " + status);
    }

    public void addOrder(Order order) {
        orderHistory.add(order);
    }

    public void addToCart(Product product) {
        cart.add(product);
        System.out.println(product.getName() + " added to cart.");
    }

    public List<Product> getCart() {
        return cart;
    }
}
