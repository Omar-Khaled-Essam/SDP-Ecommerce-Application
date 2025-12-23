package orders;

import products.Product;
import users.Customer;
import java.util.*;

public class Order {

    private Integer id;
    private Customer customer;
    private List<Product> products;
    private double total;
    private String status = "Pending";
    private String createdAt;

    private List<Observer> observers = new ArrayList<>();

    public Order(Customer customer, List<Product> products, double total) {
        this.customer = customer;
        this.products = products;
        this.total = total;
        attach(customer);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

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

    @Override
    public String toString() {
        String idPart = (id != null) ? ("#" + id + " ") : "";
        String datePart = (createdAt != null) ? (" | " + createdAt) : "";
        return idPart + "Order - " + status + " - Total: " + total + datePart;
    }
}
