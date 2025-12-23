package gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import users.Customer;
import products.Product;
import database.Database;
import orders.Order;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CustomerDashboard {

    private Customer customer;

    public CustomerDashboard(Customer c) {
        this.customer = c;
    }

    public void start(Stage stage) {

        ListView<Product> productList = new ListView<>();
        refreshProducts(productList);

        ListView<Product> cartList = new ListView<>();
        ListView<Order> orderHistory = new ListView<>();
        refreshOrders(orderHistory);

        TextField search = new TextField();
        search.setPromptText("Search name");

        TextField minPrice = new TextField();
        minPrice.setPromptText("Min price");

        TextField maxPrice = new TextField();
        maxPrice.setPromptText("Max price");

        ChoiceBox<String> categoryFilter = new ChoiceBox<>();
        categoryFilter.getItems().add("All");
        Database.getInstance().getProducts().stream()
                .map(Product::getCategory).distinct()
                .forEach(cat -> categoryFilter.getItems().add(cat));
        categoryFilter.setValue("All");

        Button applyFilters = new Button("Filter");

        Button addBtn = new Button("Add to Cart");
        Button removeBtn = new Button("Remove from Cart");
        Button checkoutBtn = new Button("Checkout");
        Label totalLabel = new Label("Total: 0");


        ChoiceBox<String> paymentMethod = new ChoiceBox<>();
        paymentMethod.getItems().addAll("Credit Card", "PayPal");
        paymentMethod.setValue("Credit Card");

        applyFilters.setOnAction(e -> {
            double min = parseOrDefault(minPrice.getText(), Double.MIN_VALUE);
            double max = parseOrDefault(maxPrice.getText(), Double.MAX_VALUE);
            String cat = categoryFilter.getValue() == null ? "All" : categoryFilter.getValue();
            String term = search.getText().trim().toLowerCase();
            productList.getItems().setAll(
                    Database.getInstance().getProducts().stream().filter(p ->
                            p.getPrice() >= min && p.getPrice() <= max &&
                            ("All".equalsIgnoreCase(cat) || p.getCategory().equalsIgnoreCase(cat)) &&
                            (term.isEmpty() || p.getName().toLowerCase().contains(term))
                    ).collect(Collectors.toList())
            );
        });

        addBtn.setOnAction(e -> {
            Product p = productList.getSelectionModel().getSelectedItem();
            if (p != null && p.getStock() > 0) {
                customer.addToCart(p);
                cartList.getItems().add(p);
                updateTotal(cartList, totalLabel);
            }
        });

        removeBtn.setOnAction(e -> {
            Product p = cartList.getSelectionModel().getSelectedItem();
            if (p != null) {
                cartList.getItems().remove(p);
                customer.removeFromCart(p);
                updateTotal(cartList, totalLabel);
            }
        });

        checkoutBtn.setOnAction(e -> {
            double subtotal = customer.getCart()
                    .stream().mapToDouble(p -> p.getPrice()).sum();
            double tax = subtotal * 0.14;
            double delivery = 50;
            double grand = subtotal + tax + delivery;

            String payMethod = paymentMethod.getValue();
            if (payMethod == null) {
                new Alert(Alert.AlertType.ERROR, "Select a payment method.").show();
                return;
            }

            Order order = new Order(
                    customer,
                    new ArrayList<>(customer.getCart()),
                    grand
            );

            Database.getInstance().addOrder(order);
            customer.addOrder(order);
            customer.clearCart();
            cartList.getItems().clear();
            totalLabel.setText("Total: 0");

            new Alert(Alert.AlertType.INFORMATION, "Order Placed! Total: " + grand + " (tax + delivery included)\nPayment: " + payMethod).show();
            refreshProducts(productList);
            refreshOrders(orderHistory);
        });

        // ? LOGOUT BUTTON (ONLY ADDITION)
        Button logout = new Button("Logout");
        logout.setOnAction(e -> {
            stage.close();
            try {
                new LoginUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Order tracking: auto-refresh when window gains focus
        stage.focusedProperty().addListener((obs, was, isNow) -> {
            if (isNow) refreshOrders(orderHistory);
        });
        Button refreshOrdersBtn = new Button("Refresh Orders");
        Button trackStatusBtn = new Button("Track Status");
        refreshOrdersBtn.setOnAction(e -> refreshOrders(orderHistory));
        trackStatusBtn.setOnAction(e -> new OrderTrackingWindow(customer.getUsername()).show());

        VBox layout = new VBox(10,
                new Label("Products"),
                new HBox(5, search, categoryFilter),
                new HBox(5, minPrice, maxPrice, applyFilters),
                productList,
                addBtn,
                new Label("Cart"), cartList,
                removeBtn,
                totalLabel,
                new Label("Payment Method"), paymentMethod,
                checkoutBtn,
                new Label("Order History"),
                orderHistory,
                new HBox(5, refreshOrdersBtn, trackStatusBtn),
                logout   // ?? added here
        );

        stage.setScene(new Scene(layout, 500, 600));
        stage.setTitle("Customer Dashboard");
        stage.show();
    }

    private void refreshProducts(ListView<Product> productList) {
        productList.getItems().setAll(Database.getInstance().getProducts());
    }

    private double parseOrDefault(String text, double fallback) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private void updateTotal(ListView<Product> cartList, Label totalLabel) {
        double total = cartList.getItems().stream().mapToDouble(Product::getPrice).sum();
        totalLabel.setText("Total: " + total);
    }

    private void refreshOrders(ListView<Order> orderHistory) {
        orderHistory.getItems().setAll(Database.getInstance().getOrdersByCustomer(customer.getUsername()));
    }
}
