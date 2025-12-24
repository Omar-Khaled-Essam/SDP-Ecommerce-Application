package gui;

import database.Database;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import orders.Order;

public class OrderTrackingWindow extends Stage {

    public OrderTrackingWindow(String username) {
        setTitle("Order Tracking");
        initModality(Modality.APPLICATION_MODAL);

        Label titleLabel = new Label("Order Tracking");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        Label userLabel = new Label("Orders for: " + username);
        userLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        userLabel.setTextFill(Color.web("#34495e"));

        ListView<Order> orders = new ListView<>();
        orders.setPrefHeight(400);
        orders.setStyle("-fx-font-size: 13px;");

        Runnable refresh = () -> orders.getItems().setAll(Database.getInstance().getOrdersByCustomer(username));
        refresh.run();

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setPrefHeight(35);
        refreshBtn.setPrefWidth(120);
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;");
        refreshBtn.setOnMouseEntered(e -> refreshBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        refreshBtn.setOnMouseExited(e -> refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        refreshBtn.setOnAction(e -> refresh.run());

        // Auto-refresh on focus
        focusedProperty().addListener((obs, was, now) -> { if (now) refresh.run(); });

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.getChildren().addAll(refreshBtn);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #ffffff;");
        root.getChildren().addAll(
            titleLabel,
            new Separator(),
            userLabel,
            orders,
            actions
        );

        setScene(new Scene(root, 500, 550));
    }
}
