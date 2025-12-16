package gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import database.Database;
import products.*;

public class CustomerDashboard {

    private ListView<String> productList;

    public void start(Stage stage) {

        Label title = new Label("Customer Dashboard");

        productList = new ListView<>();
        productList.setPrefHeight(200);   // ⭐ CRITICAL (otherwise invisible)

        loadProducts(); // ⭐ MUST be called AFTER ListView creation

        Button placeOrderBtn = new Button("Place Order");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            stage.close();
            try {
                new LoginUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(
                title,
                productList,
                placeOrderBtn,
                logoutBtn
        );
        layout.setStyle("-fx-padding: 20");

        stage.setScene(new Scene(layout, 400, 350));
        stage.setTitle("Customer");
        stage.show();
    }

    // -----------------------------
    // LOAD PRODUCTS FROM DATABASE
    // -----------------------------
    private void loadProducts() {
        productList.getItems().clear();

        for (Product p : Database.getInstance().getProducts()) {
            productList.getItems().add(
                    p.getName() + " | $" + p.getPrice() + " | Stock: " + p.getStock()
            );
        }

        // Debug safety check
        if (productList.getItems().isEmpty()) {
            productList.getItems().add("No products available");
        }
    }
}
