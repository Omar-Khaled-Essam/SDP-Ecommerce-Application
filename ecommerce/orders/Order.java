package orders;

import users.Customer;
import products.Product;
import java.util.List;

public class Order {

    private Customer customer;
    private List<Product> items;
    private OrderStatus status;
    private double total;

    public Order(Customer c, List<Product> items, double total) {
        this.customer = c;
        this.items = items;
        this.total = total;
        this.status = OrderStatus.PENDING;
    }

    public void setStatus(OrderStatus s) {
        status = s;
        customer.update(s); // Observer notification
    }

    public OrderStatus getStatus() { return status; }
    public double getTotal() { return total; }
    public Customer getCustomer() { return customer; }
    public List<Product> getItems() { return items; }
}
