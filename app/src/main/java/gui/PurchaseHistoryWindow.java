package gui;

// SOLID: SRP✅ (purchase history UI only), DIP⚠️ (calls Database singleton directly).
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
import products.Product;

public class PurchaseHistoryWindow extends Stage {

    public PurchaseHistoryWindow(String username) {
        setTitle("Purchase History");
        initModality(Modality.APPLICATION_MODAL);

        Label titleLabel = new Label("Purchase History");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        Label userLabel = new Label("Orders for: " + username);
        userLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        userLabel.setTextFill(Color.web("#34495e"));

        ListView<Order> orders = new ListView<>();
        orders.setPrefHeight(250);
        orders.setStyle("-fx-font-size: 13px;");

        ListView<String> details = new ListView<>();
        details.setPrefHeight(200);
        details.setStyle("-fx-font-size: 13px;");

        Label totalSpent = new Label();
        totalSpent.setFont(Font.font("System", FontWeight.BOLD, 16));
        totalSpent.setTextFill(Color.web("#27ae60"));

        Runnable refresh = () -> {
            var list = Database.getInstance().getOrdersByCustomer(username);
            orders.getItems().setAll(list);
            double total = Database.getInstance().getTotalSpentForCustomer(username);
            totalSpent.setText(String.format("Total Spent: $%.2f", total));
            details.getItems().clear();
        };
        refresh.run();

        orders.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            details.getItems().clear();
            if (newVal != null) {
                details.getItems().add("Order ID: " + newVal.getId());
                details.getItems().add("Status: " + newVal.getStatus());
                details.getItems().add("Total: $" + String.format("%.2f", newVal.getTotal()));
                details.getItems().add("Created: " + (newVal.getCreatedAt() != null ? newVal.getCreatedAt() : "N/A"));
                details.getItems().add("");
                details.getItems().add("Products:");
                for (Product p : newVal.getProducts()) {
                    details.getItems().add("  • " + p.getName() + " - $" + String.format("%.2f", p.getPrice()));
                }
            }
        });

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setPrefHeight(35);
        refreshBtn.setPrefWidth(120);
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;");
        refreshBtn.setOnMouseEntered(e -> refreshBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        refreshBtn.setOnMouseExited(e -> refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        refreshBtn.setOnAction(e -> refresh.run());

        focusedProperty().addListener((obs, was, now) -> {
            if (now) refresh.run();
        });

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.getChildren().addAll(totalSpent, refreshBtn);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #ffffff;");
        root.getChildren().addAll(
                titleLabel,
                new Separator(),
                userLabel,
                new Label("Orders:"),
                orders,
                new Label("Order Details:"),
                details,
                new Separator(),
                actions
        );

        setScene(new Scene(root, 500, 600));
    }
}
