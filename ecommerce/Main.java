import database.*;
import users.*;
import products.*;
import orders.*;
import payment.*;
import inventory.*;

public class Main {

    public static void main(String[] args) {

        Database db = Database.getInstance();

        Admin admin = new Admin("admin", "1234");
        Customer customer = new Customer("omar", "pass");

        db.getUsers().add(admin);
        db.getUsers().add(customer);

        ProductFactory factory = new ElectronicsFactory();
        Product product = factory.createProduct();
        db.getProducts().add(product);

        customer.addToCart(product);

        Order order = new Order(product);
        order.attach(customer);
        customer.addOrder(order);
        db.getOrders().add(order);

        PaymentStrategy payment = new CreditCardPayment();
        payment.pay(product.getPrice());

        InventoryManager.getInstance().updateStock(product, 1);

        order.setStatus("Shipped");
        order.setStatus("Delivered");

        System.out.println("Total Sales: " + db.getTotalSales());
    }
}
