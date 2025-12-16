package orders;

import java.util.*;
import products.*;

public class Order implements Subject {

    private String status = "Pending";
    private Product product;
    private int quantity = 1;

    private List<Observer> observers = new ArrayList<>();

    public Order(Product product) {
        this.product = product;
    }

    public void setStatus(String status) {
        this.status = status;
        notifyObservers();
    }

    public String getStatus() {
        return status;
    }

    public double getTotalAmount() {
        return product.getPrice() * quantity;
    }

    @Override
    public void attach(Observer o) {
        observers.add(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(status);
        }
    }
}
