package gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import database.Database;
import products.Product;

public class CustomerDashboard {

    public void start(Stage stage) {

        ListView<String> list = new ListView<>();
        list.setPrefHeight(200);

        for (Product p : Database.getInstance().getProducts()) {
            list.getItems().add(p.getName() + " $" + p.getPrice());
        }

        Button logout = new Button("Logout");
        logout.setOnAction(e -> {
            stage.close();
            try { new LoginUI().start(new Stage()); } catch (Exception ex) {}
        });

        VBox root = new VBox(10, list, logout);
        root.setStyle("-fx-padding:20");
        stage.setScene(new Scene(root, 350, 300));
        stage.show();
    }
}
