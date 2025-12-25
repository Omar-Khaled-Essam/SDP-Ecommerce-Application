package gui;

// SOLID: SRP⚠️ (UI + cart/checkout logic), DIP⚠️ (calls Database singleton directly).
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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

    private void styleButton(Button btn, String color) {
        btn.setPrefHeight(35);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + darkenColor(color) + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;"));
    }

    private String darkenColor(String color) {
        switch (color) {
            case "#3498db":
                return "#2980b9";
            case "#27ae60":
                return "#229954";
            case "#e74c3c":
                return "#c0392b";
            case "#f39c12":
                return "#d68910";
            case "#95a5a6":
                return "#7f8c8d";
            default:
                return color;
        }
    }

    private Alert createFormattedAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(400);
        contentLabel.setStyle("-fx-font-size: 13px; -fx-padding: 10px;");

        ScrollPane scrollPane = new ScrollPane(contentLabel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(Math.min(200, content.split("\n").length * 25 + 50));
        scrollPane.setMaxHeight(300);

        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefWidth(450);
        return alert;
    }

    public void start(Stage stage) {
        stage.setTitle("Customer Dashboard - " + customer.getUsername());

        // Create TabPane for better organization
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: #ecf0f1;");

        // ========== PRODUCTS TAB ==========
        Tab productsTab = new Tab("Products");
        productsTab.setStyle("-fx-font-size: 14px;");

        ListView<Product> productList = new ListView<>();
        productList.setPrefHeight(400);
        productList.setStyle("-fx-font-size: 13px;");
        refreshProducts(productList);

        // Search and filter section
        Label searchLabel = new Label("Search & Filter");
        searchLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        searchLabel.setTextFill(Color.web("#2c3e50"));

        TextField search = new TextField();
        search.setPromptText("Search by name...");
        search.setPrefHeight(35);
        search.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        TextField minPrice = new TextField();
        minPrice.setPromptText("Min price");
        minPrice.setPrefHeight(35);
        minPrice.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        TextField maxPrice = new TextField();
        maxPrice.setPromptText("Max price");
        maxPrice.setPrefHeight(35);
        maxPrice.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        ChoiceBox<String> categoryFilter = new ChoiceBox<>();
        categoryFilter.getItems().add("All");
        Database.getInstance().getProducts().stream()
                .map(Product::getCategory).distinct()
                .forEach(cat -> categoryFilter.getItems().add(cat));
        categoryFilter.setValue("All");
        categoryFilter.setPrefHeight(35);
        categoryFilter.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        Button applyFilters = new Button("Apply Filters");
        styleButton(applyFilters, "#3498db");

        Button addBtn = new Button("Add to Cart");
        styleButton(addBtn, "#27ae60");

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
            if (p == null) {
                createFormattedAlert(Alert.AlertType.WARNING, "Selection Required", "No Product Selected",
                        "Please select a product from the list to add to your cart.").show();
                return;
            }
            if (p.getStock() <= 0) {
                createFormattedAlert(Alert.AlertType.WARNING, "Out of Stock", "Product Unavailable",
                        "Sorry, '" + p.getName() + "' is currently out of stock.\n\nPlease select a different product.").show();
                return;
            }
            customer.addToCart(p);
            createFormattedAlert(Alert.AlertType.INFORMATION, "Added to Cart", "Product Added",
                    "'" + p.getName() + "' has been added to your cart successfully!").show();
            refreshProducts(productList);
        });

        HBox filterRow1 = new HBox(10);
        filterRow1.getChildren().addAll(search, categoryFilter);
        HBox.setHgrow(search, Priority.ALWAYS);

        HBox filterRow2 = new HBox(10);
        filterRow2.getChildren().addAll(minPrice, maxPrice, applyFilters);
        HBox.setHgrow(minPrice, Priority.ALWAYS);
        HBox.setHgrow(maxPrice, Priority.ALWAYS);

        VBox productsContent = new VBox(15);
        productsContent.setPadding(new Insets(20));
        productsContent.setStyle("-fx-background-color: #ffffff;");
        productsContent.getChildren().addAll(
                searchLabel,
                filterRow1,
                filterRow2,
                new Label("Available Products:"),
                productList,
                addBtn
        );

        productsTab.setContent(productsContent);

        // ========== SHOPPING CART TAB ==========
        Tab cartTab = new Tab("Shopping Cart");
        cartTab.setStyle("-fx-font-size: 14px;");

        ListView<Product> cartList = new ListView<>();
        cartList.setPrefHeight(350);
        cartList.setStyle("-fx-font-size: 13px;");

        Label cartTitle = new Label("Your Cart");
        cartTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        cartTitle.setTextFill(Color.web("#2c3e50"));

        Label totalLabel = new Label("Subtotal: $0.00");
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        totalLabel.setTextFill(Color.web("#27ae60"));

        Label taxLabel = new Label("Tax (14%): $0.00");
        taxLabel.setFont(Font.font("System", 13));

        Label deliveryLabel = new Label("Delivery Fee: $50.00");
        deliveryLabel.setFont(Font.font("System", 13));

        Label grandTotalLabel = new Label("Grand Total: $0.00");
        grandTotalLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        grandTotalLabel.setTextFill(Color.web("#e74c3c"));

        ChoiceBox<String> paymentMethod = new ChoiceBox<>();
        paymentMethod.getItems().addAll("Credit Card", "PayPal");
        paymentMethod.setValue("Credit Card");
        paymentMethod.setPrefHeight(35);
        paymentMethod.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        Button removeBtn = new Button("Remove from Cart");
        styleButton(removeBtn, "#e74c3c");

        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setPrefHeight(45);
        checkoutBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;");
        checkoutBtn.setOnMouseEntered(e -> checkoutBtn.setStyle("-fx-background-color: #229954; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        checkoutBtn.setOnMouseExited(e -> checkoutBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 5;"));

        // Update cart display when products are added
        Runnable updateCartDisplay = () -> {
            cartList.getItems().setAll(customer.getCart());
            double subtotal = customer.getCart().stream().mapToDouble(Product::getPrice).sum();
            double tax = subtotal * 0.14;
            double delivery = 50;
            double grand = subtotal + tax + delivery;

            totalLabel.setText(String.format("Subtotal: $%.2f", subtotal));
            taxLabel.setText(String.format("Tax (14%%): $%.2f", tax));
            grandTotalLabel.setText(String.format("Grand Total: $%.2f", grand));
        };

        removeBtn.setOnAction(e -> {
            Product p = cartList.getSelectionModel().getSelectedItem();
            if (p == null) {
                createFormattedAlert(Alert.AlertType.WARNING, "Selection Required", "No Item Selected",
                        "Please select an item from your cart to remove.").show();
                return;
            }
            cartList.getItems().remove(p);
            customer.removeFromCart(p);
            updateCartDisplay.run();
        });

        checkoutBtn.setOnAction(e -> {
            if (customer.getCart().isEmpty()) {
                createFormattedAlert(Alert.AlertType.WARNING, "Empty Cart", "Your Cart is Empty",
                        "Your shopping cart is empty.\n\nPlease add products to your cart before checkout.").show();
                return;
            }

            double subtotal = customer.getCart().stream().mapToDouble(Product::getPrice).sum();
            double tax = subtotal * 0.14;
            double delivery = 50;
            double grand = subtotal + tax + delivery;

            String payMethod = paymentMethod.getValue();
            if (payMethod == null) {
                createFormattedAlert(Alert.AlertType.ERROR, "Payment Method Required", "No Payment Method Selected",
                        "Please select a payment method before completing your order.").show();
                return;
            }

            Order order = new Order(customer, new ArrayList<>(customer.getCart()), grand);
            Database.getInstance().addOrder(order);
            customer.addOrder(order);
            customer.clearCart();
            cartList.getItems().clear();
            updateCartDisplay.run();

            createFormattedAlert(Alert.AlertType.INFORMATION, "Order Placed", "Order Placed Successfully!",
                    String.format("Your order has been placed successfully!\n\n" +
                                    "Order Details:\n" +
                                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                                    "Subtotal: $%.2f\n" +
                                    "Tax (14%%): $%.2f\n" +
                                    "Delivery Fee: $%.2f\n" +
                                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                                    "Grand Total: $%.2f\n\n" +
                                    "Payment Method: %s\n\n" +
                                    "Thank you for your purchase!",
                            subtotal, tax, delivery, grand, payMethod)).show();

            refreshProducts(productList);
        });

        VBox cartContent = new VBox(15);
        cartContent.setPadding(new Insets(20));
        cartContent.setStyle("-fx-background-color: #ffffff;");
        cartContent.getChildren().addAll(
                cartTitle,
                cartList,
                removeBtn,
                new Separator(),
                totalLabel,
                taxLabel,
                deliveryLabel,
                new Separator(),
                grandTotalLabel,
                new Label("Payment Method:"),
                paymentMethod,
                checkoutBtn
        );

        cartTab.setContent(cartContent);

        // ========== ORDERS TAB ==========
        Tab ordersTab = new Tab("My Orders");
        ordersTab.setStyle("-fx-font-size: 14px;");

        ListView<Order> orderHistory = new ListView<>();
        orderHistory.setPrefHeight(400);
        orderHistory.setStyle("-fx-font-size: 13px;");
        refreshOrders(orderHistory);

        Label ordersTitle = new Label("Order History");
        ordersTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        ordersTitle.setTextFill(Color.web("#2c3e50"));

        Button refreshOrdersBtn = new Button("Refresh");
        styleButton(refreshOrdersBtn, "#3498db");

        Button trackStatusBtn = new Button("Track Status");
        styleButton(trackStatusBtn, "#f39c12");

        Button purchaseHistoryBtn = new Button("Purchase History");
        styleButton(purchaseHistoryBtn, "#9b59b6");

        stage.focusedProperty().addListener((obs, was, isNow) -> {
            if (isNow) {
                refreshOrders(orderHistory);
                updateCartDisplay.run();
            }
        });

        refreshOrdersBtn.setOnAction(e -> refreshOrders(orderHistory));
        trackStatusBtn.setOnAction(e -> new OrderTrackingWindow(customer.getUsername()).show());
        purchaseHistoryBtn.setOnAction(e -> new PurchaseHistoryWindow(customer.getUsername()).show());

        HBox orderButtons = new HBox(10);
        orderButtons.getChildren().addAll(refreshOrdersBtn, trackStatusBtn, purchaseHistoryBtn);

        VBox ordersContent = new VBox(15);
        ordersContent.setPadding(new Insets(20));
        ordersContent.setStyle("-fx-background-color: #ffffff;");
        ordersContent.getChildren().addAll(
                ordersTitle,
                orderHistory,
                orderButtons
        );

        ordersTab.setContent(ordersContent);

        // Add tabs to tab pane
        tabPane.getTabs().addAll(productsTab, cartTab, ordersTab);

        // Header with user info and logout
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #34495e;");

        Label welcomeLabel = new Label("Welcome, " + customer.getUsername() + "!");
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        welcomeLabel.setTextFill(Color.WHITE);

        Button logout = new Button("Logout");
        styleButton(logout, "#e74c3c");
        logout.setPrefWidth(100);

        HBox.setHgrow(welcomeLabel, Priority.ALWAYS);
        header.getChildren().addAll(welcomeLabel, logout);

        logout.setOnAction(e -> {
            stage.close();
            try {
                new LoginUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Main layout
        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(header, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Scene scene = new Scene(mainLayout, 900, 700);
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();

        // Initial cart update
        updateCartDisplay.run();
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

    private void refreshOrders(ListView<Order> orderHistory) {
        orderHistory.getItems().setAll(Database.getInstance().getOrdersByCustomer(customer.getUsername()));
    }
}
