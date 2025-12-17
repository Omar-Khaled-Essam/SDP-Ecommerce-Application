package orders;

import users.Customer;
import products.Product;
import java.util.List;

public class Order {

    private Customer customer;
    private List<Product> products;
    private OrderStatus status;
    private double total;

    public Order(Customer c, List<Product> p, double t) {
        customer = c;
        products = p;
        total = t;
        status = OrderStatus.PENDING;
    }

    public void setStatus(OrderStatus s) {
        status = s;
        customer.update(s);
    }

    public double getTotal() { return total; }
}
