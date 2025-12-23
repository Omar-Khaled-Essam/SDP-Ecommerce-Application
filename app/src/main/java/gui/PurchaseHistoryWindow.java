package gui;

import database.Database;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import orders.Order;
import products.Product;

public class PurchaseHistoryWindow extends Stage {

    public PurchaseHistoryWindow(String username) {
        setTitle("Purchase History");
        initModality(Modality.APPLICATION_MODAL);

        ListView<Order> orders = new ListView<>();
        ListView<String> details = new ListView<>();
        Label totalSpent = new Label();

        Runnable refresh = () -> {
            var list = Database.getInstance().getOrdersByCustomer(username);
            orders.getItems().setAll(list);
            double total = Database.getInstance().getTotalSpentForCustomer(username);
            totalSpent.setText("Total Spent: " + total);
            details.getItems().clear();
        };
        refresh.run();

        orders.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            details.getItems().clear();
            if (newVal != null) {
                for (Product p : newVal.getProducts()) {
                    details.getItems().add(p.getName() + " - " + p.getPrice());
                }
            }
        });

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refresh.run());

        focusedProperty().addListener((obs, was, now) -> { if (now) refresh.run(); });

        HBox actions = new HBox(5, refreshBtn, totalSpent);
        VBox root = new VBox(10,
                new Label("Orders for: " + username),
                orders,
                new Label("Order Details"),
                details,
                actions
        );
        root.setPadding(new Insets(10));

        setScene(new Scene(root, 400, 450));
    }
}
