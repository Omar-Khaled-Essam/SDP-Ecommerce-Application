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
import gui.SalesReportWindow;

public class AdminDashboard {

    private Admin admin;

    public AdminDashboard(Admin admin) {
        this.admin = admin;
    }

    public void start(Stage stage) {

        // ---------- PRODUCT MANAGEMENT ----------
        ListView<Product> productList = new ListView<>();
        refreshProducts(productList);

        TextField name = new TextField();
        name.setPromptText("Name");

        TextField category = new TextField();
        category.setPromptText("Category");

        TextField price = new TextField();
        price.setPromptText("Price");

        TextField stock = new TextField();
        stock.setPromptText("Stock");

        TextArea description = new TextArea();
        description.setPromptText("Description");
        description.setPrefRowCount(2);

        Button addProduct = new Button("Add Product");
        Button deleteProduct = new Button("Delete Product");
        Button editProduct = new Button("Save Edits");

        addProduct.setOnAction(e -> {
            Product p = createProductFromFields(name, category, price, stock, description);
            Database.getInstance().addProduct(p);
            refreshProducts(productList);
        });

        deleteProduct.setOnAction(e -> {
            Product p = productList.getSelectionModel().getSelectedItem();
            if (p != null) {
                Database.getInstance().deleteProduct(p);
                refreshProducts(productList);
            }
        });

        editProduct.setOnAction(e -> {
            Product p = productList.getSelectionModel().getSelectedItem();
            if (p == null) return;
            updateProductFromFields(p, name, category, price, stock, description);
            Database.getInstance().updateProduct(p);
            refreshProducts(productList);
        });

        productList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                name.setText(newVal.getName());
                category.setText(newVal.getCategory());
                price.setText(String.valueOf(newVal.getPrice()));
                stock.setText(String.valueOf(newVal.getStock()));
                description.setText(newVal.getDescription());
            }
        });

        // ---------- ORDER MANAGEMENT ----------
        ListView<Order> orderList = new ListView<>();
        refreshOrders(orderList);

        ComboBox<OrderStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(OrderStatus.values());
        statusBox.setValue(OrderStatus.Pending);

        Button updateStatus = new Button("Update Status");
        updateStatus.setOnAction(e -> {
            Order o = orderList.getSelectionModel().getSelectedItem();
            if (o != null && statusBox.getValue() != null) {
                Database.getInstance().updateOrderStatus(o, statusBox.getValue().name());
                refreshOrders(orderList);
            }
        });

        // ---------- CUSTOMER MANAGEMENT ----------
        ListView<Customer> customerList = new ListView<>();
        refreshCustomers(customerList);

        Label customerDetails = new Label("Select a customer to view details.");
        ListView<Order> customerOrders = new ListView<>();
        Button viewOrderDetails = new Button("View Selected Order Details");
        Button resetUserPassword = new Button("Reset User Password");
        Button deleteUser = new Button("Delete User");
        Button openReports = new Button("Sales Reports");

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

        resetUserPassword.setOnAction(e -> {
            Customer c = customerList.getSelectionModel().getSelectedItem();
            if (c == null) return;
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Reset Password");
            dialog.setHeaderText("Reset password for " + c.getUsername());
            dialog.setContentText("New password:");
            dialog.showAndWait().ifPresent(pw -> {
                if (!pw.isEmpty()) {
                    Database.getInstance().updatePassword(c.getUsername(), pw);
                    new Alert(Alert.AlertType.INFORMATION, "Password updated.").show();
                }
            });
        });

        deleteUser.setOnAction(e -> {
            Customer c = customerList.getSelectionModel().getSelectedItem();
            if (c == null) return;
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete user " + c.getUsername() + "?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    Database.getInstance().deleteUser(c.getUsername());
                    refreshCustomers(customerList);
                }
            });
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
                productList, name, category, price, stock, description, addProduct, editProduct, deleteProduct,
                new Separator(),
                new Label("Order Management"),
                orderList, statusBox, updateStatus,
                new Separator(),
                new Label("Customer Management"),
                customerList,
                customerDetails,
                new Label("Order History"),
                customerOrders, viewOrderDetails, resetUserPassword, deleteUser, new Separator(), openReports,
                new Separator(),
                logout   // 👈 added here
        );

        layout.setStyle("-fx-padding:20");

        stage.setScene(new Scene(layout, 500, 600));
        stage.setTitle("Admin Dashboard");
        stage.show();
    }

    private void refreshProducts(ListView<Product> productList) {
        productList.getItems().setAll(Database.getInstance().getProducts());
    }

    private void refreshOrders(ListView<Order> orderList) {
        orderList.getItems().setAll(Database.getInstance().getOrders());
    }

    private void refreshCustomers(ListView<Customer> customerList) {
        customerList.getItems().setAll(Database.getInstance().getCustomers());
    }

    private Product createProductFromFields(TextField name, TextField category, TextField price, TextField stock, TextArea description) {
        String cat = category.getText().trim();
        if (cat.equalsIgnoreCase("Electronics")) {
            return new Electronics(
                    name.getText(),
                    cat,
                    Double.parseDouble(price.getText()),
                    Integer.parseInt(stock.getText()),
                    description.getText()
            );
        } else {
            return new Clothing(
                    name.getText(),
                    cat.isEmpty() ? "Clothing" : cat,
                    Double.parseDouble(price.getText()),
                    Integer.parseInt(stock.getText()),
                    description.getText()
            );
        }
    }

    private void updateProductFromFields(Product p, TextField name, TextField category, TextField price, TextField stock, TextArea description) {
        p.setName(name.getText());
        p.setCategory(category.getText());
        p.setPrice(Double.parseDouble(price.getText()));
        p.setStock(Integer.parseInt(stock.getText()));
        p.setDescription(description.getText());
    }
}
