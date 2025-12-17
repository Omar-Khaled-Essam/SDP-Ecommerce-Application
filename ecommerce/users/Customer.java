package users;

import orders.OrderStatus;

public class Customer extends User {

    private double totalSpent = 0;

    public Customer(String u, String p) {
        super(u, p);
    }

    public void update(OrderStatus status) {
        System.out.println("Order status changed to: " + status);
    }

    public void addSpent(double amount) {
        totalSpent += amount;
    }
}
