package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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

    private void styleButton(Button btn, String color) {
        btn.setPrefHeight(35);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + darkenColor(color) + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;"));
    }

    private String darkenColor(String color) {
        switch(color) {
            case "#3498db": return "#2980b9";
            case "#27ae60": return "#229954";
            case "#e74c3c": return "#c0392b";
            case "#f39c12": return "#d68910";
            case "#95a5a6": return "#7f8c8d";
            case "#9b59b6": return "#8e44ad";
            default: return color;
        }
    }

    private Alert createFormattedAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        
        // Create a label with wrapping for better text display
        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(400);
        contentLabel.setStyle("-fx-font-size: 13px; -fx-padding: 10px;");
        
        // Create a scrollable area if content is long
        ScrollPane scrollPane = new ScrollPane(contentLabel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(Math.min(200, content.split("\n").length * 25 + 50));
        scrollPane.setMaxHeight(300);
        
        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefWidth(450);
        return alert;
    }

    public void start(Stage stage) {
        stage.setTitle("Admin Dashboard - " + admin.getUsername());

        // Create TabPane for better organization
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: #ecf0f1;");

        // ========== PRODUCT MANAGEMENT TAB ==========
        Tab productsTab = new Tab("Products");
        productsTab.setStyle("-fx-font-size: 14px;");

        ListView<Product> productList = new ListView<>();
        productList.setPrefHeight(300);
        productList.setStyle("-fx-font-size: 13px;");
        refreshProducts(productList);

        Label productTitle = new Label("Product Management");
        productTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        productTitle.setTextFill(Color.web("#2c3e50"));

        TextField name = new TextField();
        name.setPromptText("Product Name");
        name.setPrefHeight(35);
        name.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        TextField category = new TextField();
        category.setPromptText("Category");
        category.setPrefHeight(35);
        category.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        TextField price = new TextField();
        price.setPromptText("Price");
        price.setPrefHeight(35);
        price.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        TextField stock = new TextField();
        stock.setPromptText("Stock Quantity");
        stock.setPrefHeight(35);
        stock.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        TextArea description = new TextArea();
        description.setPromptText("Description");
        description.setPrefRowCount(3);
        description.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        Button addProduct = new Button("Add Product");
        styleButton(addProduct, "#27ae60");

        Button editProduct = new Button("Update Product");
        styleButton(editProduct, "#3498db");

        Button deleteProduct = new Button("Delete Product");
        styleButton(deleteProduct, "#e74c3c");

        addProduct.setOnAction(e -> {
            try {
                Product p = createProductFromFields(name, category, price, stock, description);
                if (p == null) {
                    createFormattedAlert(Alert.AlertType.ERROR, "Error", "Validation Error", 
                        "Please fill all fields correctly.\n\nAll fields are required:\n• Product Name\n• Category (Electronics or Clothing)\n• Price (must be a number ≥ 0)\n• Stock (must be a number ≥ 0)\n• Description").show();
                    return;
                }
                Database.getInstance().addProduct(p);
                refreshProducts(productList);
                clearProductFields(name, category, price, stock, description);
                createFormattedAlert(Alert.AlertType.INFORMATION, "Success", "Product Added", 
                    "Product '" + p.getName() + "' has been added successfully!").show();
            } catch (Exception ex) {
                createFormattedAlert(Alert.AlertType.ERROR, "Error", "Failed to Add Product", 
                    "An error occurred while adding the product:\n\n" + ex.getMessage()).show();
            }
        });

        deleteProduct.setOnAction(e -> {
            Product p = productList.getSelectionModel().getSelectedItem();
            if (p == null) {
                createFormattedAlert(Alert.AlertType.WARNING, "Selection Required", "No Product Selected", 
                    "Please select a product from the list to delete.").show();
                return;
            }
            Alert confirm = createFormattedAlert(Alert.AlertType.CONFIRMATION, "Confirm Deletion", 
                "Delete Product?", "Are you sure you want to delete the following product?\n\n" +
                "Product: " + p.getName() + "\n" +
                "Category: " + p.getCategory() + "\n" +
                "Price: $" + String.format("%.2f", p.getPrice()) + "\n\n" +
                "This action cannot be undone!");
            confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    Database.getInstance().deleteProduct(p);
                    refreshProducts(productList);
                    clearProductFields(name, category, price, stock, description);
                    createFormattedAlert(Alert.AlertType.INFORMATION, "Success", "Product Deleted", 
                        "Product '" + p.getName() + "' has been deleted successfully.").show();
                }
            });
        });

        editProduct.setOnAction(e -> {
            Product p = productList.getSelectionModel().getSelectedItem();
            if (p == null) {
                createFormattedAlert(Alert.AlertType.WARNING, "Selection Required", "No Product Selected", 
                    "Please select a product from the list to edit.").show();
                return;
            }
            try {
                updateProductFromFields(p, name, category, price, stock, description);
                Database.getInstance().updateProduct(p);
                refreshProducts(productList);
                createFormattedAlert(Alert.AlertType.INFORMATION, "Success", "Product Updated", 
                    "Product '" + p.getName() + "' has been updated successfully.").show();
            } catch (Exception ex) {
                createFormattedAlert(Alert.AlertType.ERROR, "Error", "Failed to Update Product", 
                    "An error occurred while updating the product:\n\n" + ex.getMessage()).show();
            }
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

        GridPane productForm = new GridPane();
        productForm.setHgap(10);
        productForm.setVgap(10);
        productForm.setPadding(new Insets(10));
        productForm.add(new Label("Name:"), 0, 0);
        productForm.add(name, 1, 0);
        productForm.add(new Label("Category:"), 0, 1);
        productForm.add(category, 1, 1);
        productForm.add(new Label("Price:"), 0, 2);
        productForm.add(price, 1, 2);
        productForm.add(new Label("Stock:"), 0, 3);
        productForm.add(stock, 1, 3);
        productForm.add(new Label("Description:"), 0, 4);
        productForm.add(description, 1, 4);

        HBox productButtons = new HBox(10);
        productButtons.getChildren().addAll(addProduct, editProduct, deleteProduct);

        VBox productsContent = new VBox(15);
        productsContent.setPadding(new Insets(20));
        productsContent.setStyle("-fx-background-color: #ffffff;");
        productsContent.getChildren().addAll(
            productTitle,
            productList,
            new Separator(),
            productForm,
            productButtons
        );

        productsTab.setContent(productsContent);

        // ========== ORDER MANAGEMENT TAB ==========
        Tab ordersTab = new Tab("Orders");
        ordersTab.setStyle("-fx-font-size: 14px;");

        ListView<Order> orderList = new ListView<>();
        orderList.setPrefHeight(400);
        orderList.setStyle("-fx-font-size: 13px;");
        refreshOrders(orderList);

        Label orderTitle = new Label("Order Management");
        orderTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        orderTitle.setTextFill(Color.web("#2c3e50"));

        ComboBox<OrderStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(OrderStatus.values());
        statusBox.setValue(OrderStatus.Pending);
        statusBox.setPrefHeight(35);
        statusBox.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        Button updateStatus = new Button("Update Order Status");
        styleButton(updateStatus, "#3498db");

        updateStatus.setOnAction(e -> {
            Order o = orderList.getSelectionModel().getSelectedItem();
            if (o == null) {
                createFormattedAlert(Alert.AlertType.WARNING, "Selection Required", "No Order Selected", 
                    "Please select an order from the list to update its status.").show();
                return;
            }
            if (statusBox.getValue() == null) {
                createFormattedAlert(Alert.AlertType.WARNING, "Selection Required", "No Status Selected", 
                    "Please select a status from the dropdown menu.").show();
                return;
            }
            Database.getInstance().updateOrderStatus(o, statusBox.getValue().name());
            refreshOrders(orderList);
            createFormattedAlert(Alert.AlertType.INFORMATION, "Success", "Order Status Updated", 
                "Order #" + o.getId() + " status has been updated to: " + statusBox.getValue().name()).show();
        });

        HBox orderControls = new HBox(10);
        orderControls.getChildren().addAll(new Label("Status:"), statusBox, updateStatus);

        VBox ordersContent = new VBox(15);
        ordersContent.setPadding(new Insets(20));
        ordersContent.setStyle("-fx-background-color: #ffffff;");
        ordersContent.getChildren().addAll(
            orderTitle,
            orderList,
            orderControls
        );

        ordersTab.setContent(ordersContent);

        // ========== CUSTOMER MANAGEMENT TAB ==========
        Tab customersTab = new Tab("Customers");
        customersTab.setStyle("-fx-font-size: 14px;");

        ListView<Customer> customerList = new ListView<>();
        customerList.setPrefHeight(250);
        customerList.setStyle("-fx-font-size: 13px;");
        refreshCustomers(customerList);

        Label customerTitle = new Label("Customer Management");
        customerTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        customerTitle.setTextFill(Color.web("#2c3e50"));

        Label customerDetails = new Label("Select a customer to view details.");
        customerDetails.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        customerDetails.setTextFill(Color.web("#34495e"));

        ListView<Order> customerOrders = new ListView<>();
        customerOrders.setPrefHeight(200);
        customerOrders.setStyle("-fx-font-size: 13px;");

        Button viewOrderDetails = new Button("View Order Details");
        styleButton(viewOrderDetails, "#3498db");

        Button resetUserPassword = new Button("Reset Password");
        styleButton(resetUserPassword, "#f39c12");

        Button deleteUser = new Button("Delete User");
        styleButton(deleteUser, "#e74c3c");

        Button openReports = new Button("Sales Reports");
        styleButton(openReports, "#9b59b6");

        Button refreshOrderHistory = new Button("Refresh Order History");
        styleButton(refreshOrderHistory, "#3498db");

        // Method to refresh the selected customer's order history
        Runnable refreshSelectedCustomerOrders = () -> {
            Customer selected = customerList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                customerOrders.getItems().clear();
                customerDetails.setText("Select a customer to view details.");
                return;
            }
            // Refresh customers list to get updated order data
            refreshCustomers(customerList);
            // Re-select the same customer
            Customer refreshed = null;
            for (Customer c : customerList.getItems()) {
                if (c.getUsername().equals(selected.getUsername())) {
                    refreshed = c;
                    break;
                }
            }
            if (refreshed != null) {
                customerList.getSelectionModel().select(refreshed);
            }
        };

        customerList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            customerOrders.getItems().clear();
            if (newVal == null) {
                customerDetails.setText("Select a customer to view details.");
                return;
            }
            customerDetails.setText(
                    "Username: " + newVal.getUsername() +
                    " | Orders: " + newVal.getOrderHistory().size() +
                    " | Total Spent: $" + String.format("%.2f", newVal.getTotalSpent())
            );
            customerOrders.getItems().addAll(newVal.getOrderHistory());
        });

        refreshOrderHistory.setOnAction(e -> refreshSelectedCustomerOrders.run());

        viewOrderDetails.setOnAction(e -> {
            Order o = customerOrders.getSelectionModel().getSelectedItem();
            if (o == null) {
                createFormattedAlert(Alert.AlertType.INFORMATION, "Selection Required", "No Order Selected", 
                    "Please select an order from the list to view its details.").show();
                return;
            }
            StringBuilder details = new StringBuilder();
            details.append("Order Information\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            details.append("Order ID: ").append(o.getId()).append("\n");
            details.append("Status: ").append(o.getStatus()).append("\n");
            details.append("Total: $").append(String.format("%.2f", o.getTotal())).append("\n");
            details.append("Created: ").append(o.getCreatedAt() != null ? o.getCreatedAt() : "N/A").append("\n\n");
            details.append("Products (").append(o.getProducts().size()).append(" items):\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            for (Product p : o.getProducts()) {
                details.append("  • ").append(p.getName())
                        .append(" - $").append(String.format("%.2f", p.getPrice())).append("\n");
            }
            createFormattedAlert(Alert.AlertType.INFORMATION, "Order Information", "Order Details", details.toString()).show();
        });

        resetUserPassword.setOnAction(e -> {
            Customer c = customerList.getSelectionModel().getSelectedItem();
            if (c == null) {
                createFormattedAlert(Alert.AlertType.WARNING, "Selection Required", "No Customer Selected", 
                    "Please select a customer from the list to reset their password.").show();
                return;
            }
            // Confirm password reset
            Alert confirm = createFormattedAlert(Alert.AlertType.CONFIRMATION, "Reset Password", 
                "Reset Password for " + c.getUsername(),
                "This action will reset the password for user: " + c.getUsername() + "\n\n" +
                "Important:\n" +
                "• The user will be required to reset their password on next login\n" +
                "• The user must use the 'Forgot Password?' feature to set a new password\n" +
                "• The user cannot login until they reset their password\n\n" +
                "Continue with password reset?");
            confirm.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    Database.getInstance().resetPasswordByAdmin(c.getUsername());
                    createFormattedAlert(Alert.AlertType.INFORMATION, "Password Reset", "Password Reset Successful",
                        "The password for user '" + c.getUsername() + "' has been reset successfully.\n\n" +
                        "The user will be required to set a new password using the 'Forgot Password?' feature on their next login attempt.").show();
                }
            });
        });

        deleteUser.setOnAction(e -> {
            Customer c = customerList.getSelectionModel().getSelectedItem();
            if (c == null) {
                createFormattedAlert(Alert.AlertType.WARNING, "Selection Required", "No Customer Selected", 
                    "Please select a customer from the list to delete.").show();
                return;
            }
            Alert confirm = createFormattedAlert(Alert.AlertType.CONFIRMATION, "Delete User", 
                "Delete User Account?",
                "Are you sure you want to delete the following user account?\n\n" +
                "Username: " + c.getUsername() + "\n" +
                "Orders: " + c.getOrderHistory().size() + "\n" +
                "Total Spent: $" + String.format("%.2f", c.getTotalSpent()) + "\n\n" +
                "⚠ WARNING: This action cannot be undone!\n" +
                "All associated data will be permanently deleted.");
            confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    Database.getInstance().deleteUser(c.getUsername());
                    refreshCustomers(customerList);
                    customerDetails.setText("Select a customer to view details.");
                    customerOrders.getItems().clear();
                    createFormattedAlert(Alert.AlertType.INFORMATION, "Success", "User Deleted", 
                        "User '" + c.getUsername() + "' has been deleted successfully.").show();
                }
            });
        });

        openReports.setOnAction(e -> new SalesReportWindow().show());

        HBox customerButtons = new HBox(10);
        customerButtons.getChildren().addAll(viewOrderDetails, resetUserPassword, deleteUser, openReports);

        VBox customersContent = new VBox(15);
        customersContent.setPadding(new Insets(20));
        customersContent.setStyle("-fx-background-color: #ffffff;");
        customersContent.getChildren().addAll(
            customerTitle,
            customerList,
            customerDetails,
            new Label("Order History:"),
            new HBox(10, refreshOrderHistory),
            customerOrders,
            customerButtons
        );

        customersTab.setContent(customersContent);

        // Add tabs to tab pane
        tabPane.getTabs().addAll(productsTab, ordersTab, customersTab);

        // Header with admin info and logout
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #34495e;");

        Label welcomeLabel = new Label("Admin: " + admin.getUsername());
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

        Scene scene = new Scene(mainLayout, 1000, 750);
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(650);
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

    private void clearProductFields(TextField name, TextField category, TextField price, TextField stock, TextArea description) {
        name.clear();
        category.clear();
        price.clear();
        stock.clear();
        description.clear();
    }

    private Product createProductFromFields(TextField name, TextField category, TextField price, TextField stock, TextArea description) {
        try {
            String nameText = name.getText().trim();
            String cat = category.getText().trim();
            double priceVal = Double.parseDouble(price.getText());
            int stockVal = Integer.parseInt(stock.getText());
            String desc = description.getText().trim();

            if (nameText.isEmpty() || cat.isEmpty() || desc.isEmpty() || priceVal < 0 || stockVal < 0) {
                return null;
            }

            if (cat.equalsIgnoreCase("Electronics")) {
                return new Electronics(nameText, cat, priceVal, stockVal, desc);
            } else {
                return new Clothing(nameText, cat.isEmpty() ? "Clothing" : cat, priceVal, stockVal, desc);
            }
        } catch (NumberFormatException e) {
            return null;
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
