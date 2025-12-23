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
            double total = customer.getCart()
                    .stream().mapToDouble(p -> p.getPrice()).sum();

            Order order = new Order(
                    customer,
                    new ArrayList<>(customer.getCart()),
                    total
            );

            Database.getInstance().addOrder(order);
            customer.addOrder(order);
            customer.clearCart();
            cartList.getItems().clear();
            totalLabel.setText("Total: 0");

            new Alert(Alert.AlertType.INFORMATION, "Order Placed!").show();
            refreshProducts(productList);
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

        VBox layout = new VBox(10,
                new Label("Products"),
                new HBox(5, search, categoryFilter),
                new HBox(5, minPrice, maxPrice, applyFilters),
                productList,
                addBtn,
                new Label("Cart"), cartList,
                removeBtn,
                totalLabel,
                checkoutBtn,
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
}
