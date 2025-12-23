package gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import database.Database;
import products.*;
import orders.*;
import users.Admin;
import users.Customer;

public class AdminDashboard {

    private Admin admin;

    public AdminDashboard(Admin admin) {
        this.admin = admin;
    }

    public void start(Stage stage) {

        // ---------- PRODUCT MANAGEMENT ----------
        ListView<Product> productList = new ListView<>();
        productList.getItems().addAll(Database.getInstance().getProducts());

        TextField name = new TextField();
        name.setPromptText("Name");

        TextField price = new TextField();
        price.setPromptText("Price");

        TextField stock = new TextField();
        stock.setPromptText("Stock");

        Button addProduct = new Button("Add Product");
        Button deleteProduct = new Button("Delete Product");

        addProduct.setOnAction(e -> {
            Product p = new Electronics(
                    name.getText(),
                    "Electronics",
                    Double.parseDouble(price.getText()),
                    Integer.parseInt(stock.getText()),
                    "Admin Added"
            );
            Database.getInstance().getProducts().add(p);
            productList.getItems().add(p);
        });

        deleteProduct.setOnAction(e -> {
            Product p = productList.getSelectionModel().getSelectedItem();
            if (p != null) {
                Database.getInstance().getProducts().remove(p);
                productList.getItems().remove(p);
            }
        });

        // ---------- ORDER MANAGEMENT ----------
        ListView<Order> orderList = new ListView<>();
        orderList.getItems().addAll(Database.getInstance().getOrders());

        ComboBox<OrderStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(OrderStatus.values());
        statusBox.setValue(OrderStatus.Pending);

        Button updateStatus = new Button("Update Status");
        updateStatus.setOnAction(e -> {
            Order o = orderList.getSelectionModel().getSelectedItem();
            if (o != null && statusBox.getValue() != null) {
                o.setStatus(statusBox.getValue().name());
            }
        });

        // ---------- CUSTOMER MANAGEMENT ----------
        ListView<Customer> customerList = new ListView<>();
        customerList.getItems().addAll(Database.getInstance().getCustomers());

        Label customerDetails = new Label("Select a customer to view details.");
        ListView<Order> customerOrders = new ListView<>();
        Button viewOrderDetails = new Button("View Selected Order Details");

        customerList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            customerOrders.getItems().clear();
            if (newVal == null) {
                customerDetails.setText("Select a customer to view details.");
                return;
            }
            customerDetails.setText(
                    "Username: " + newVal.getUsername() +
                    " | Orders: " + newVal.getOrderHistory().size() +
                    " | Total Spent: " + newVal.getTotalSpent()
            );
            customerOrders.getItems().addAll(newVal.getOrderHistory());
        });

        viewOrderDetails.setOnAction(e -> {
            Order o = customerOrders.getSelectionModel().getSelectedItem();
            if (o == null) {
                new Alert(Alert.AlertType.INFORMATION, "Select an order first.").show();
                return;
            }
            StringBuilder details = new StringBuilder();
            details.append("Status: ").append(o.getStatus()).append("\n");
            details.append("Total: ").append(o.getTotal()).append("\n");
            details.append("Products:\n");
            for (Product p : o.getProducts()) {
                details.append("- ").append(p.getName())
                        .append(" (").append(p.getPrice()).append(")\n");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, details.toString());
            alert.setHeaderText("Order Details");
            alert.show();
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
                new Label("Product Management"),
                productList, name, price, stock, addProduct, deleteProduct,
                new Separator(),
                new Label("Order Management"),
                orderList, statusBox, updateStatus,
                new Separator(),
                new Label("Customer Management"),
                customerList,
                customerDetails,
                new Label("Order History"),
                customerOrders,
                viewOrderDetails,
                new Separator(),
                logout   // 👈 added here
        );

        layout.setStyle("-fx-padding:20");

        stage.setScene(new Scene(layout, 500, 600));
        stage.setTitle("Admin Dashboard");
        stage.show();
    }
}
