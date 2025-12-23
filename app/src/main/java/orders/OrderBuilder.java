package orders;

import products.Product;
import users.Customer;
import java.util.*;

public class OrderBuilder {

    private Customer customer;
    private List<Product> products = new ArrayList<>();

    public OrderBuilder setCustomer(Customer c) {
        this.customer = c;
        return this;
    }

    public OrderBuilder addProduct(Product p) {
        products.add(p);
        return this;
    }

    public Order build() {
        double total = products.stream().mapToDouble(Product::getPrice).sum();
        return new Order(customer, products, total);
    }
}
