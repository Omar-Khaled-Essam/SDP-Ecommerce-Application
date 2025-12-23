package orders;

import products.Product;
import users.Customer;
import java.util.*;

public class Order {

    private Customer customer;
    private List<Product> products;
    private double total;
    private String status = "Pending";

    private List<Observer> observers = new ArrayList<>();

    public Order(Customer customer, List<Product> products, double total) {
        this.customer = customer;
        this.products = products;
        this.total = total;
        attach(customer);
    }

    public void attach(Observer o) {
        observers.add(o);
    }

    private void notifyObservers() {
        for (Observer o : observers) {
            o.update(status);
        }
    }

    public void setStatus(String status) {
        this.status = status;
        notifyObservers();
    }

    public String getStatus() { return status; }
    public double getTotal() { return total; }
    public Customer getCustomer() { return customer; }
    public List<Product> getProducts() { return products; }
}
