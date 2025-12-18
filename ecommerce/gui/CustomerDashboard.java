package gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import users.Customer;
import products.Product;
import database.Database;
import orders.Order;

import java.util.ArrayList;

public class CustomerDashboard {

    private Customer customer;

    public CustomerDashboard(Customer c) {
        this.customer = c;
    }

    public void start(Stage stage) {

        ListView<Product> productList = new ListView<>();
        productList.getItems().addAll(Database.getInstance().getProducts());

        ListView<Product> cartList = new ListView<>();

        Button addBtn = new Button("Add to Cart");
        Button checkoutBtn = new Button("Checkout");

        addBtn.setOnAction(e -> {
            Product p = productList.getSelectionModel().getSelectedItem();
            if (p != null && p.getStock() > 0) {
                customer.addToCart(p);
                cartList.getItems().add(p);
            }
        });

        checkoutBtn.setOnAction(e -> {
            double total = customer.getCart()
                    .stream().mapToDouble(p -> p.getPrice()).sum();

            Order order = new Order(
                    customer,
                    new ArrayList<>(customer.getCart()),
                    total
            );

            Database.getInstance().getOrders().add(order);
            customer.addOrder(order);
            customer.clearCart();
            cartList.getItems().clear();

            new Alert(Alert.AlertType.INFORMATION, "Order Placed!").show();
        });

        // ✅ LOGOUT BUTTON (ONLY ADDITION)
        Button logout = new Button("Logout");
        logout.setOnAction(e -> {
            stage.close();
            try {
                new LoginUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(10,
                new Label("Products"), productList,
                addBtn,
                new Label("Cart"), cartList,
                checkoutBtn,
                logout   // 👈 added here
        );

        stage.setScene(new Scene(layout, 400, 500));
        stage.setTitle("Customer Dashboard");
        stage.show();
    }
}
