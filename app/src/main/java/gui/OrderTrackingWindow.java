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

public class OrderTrackingWindow extends Stage {

    public OrderTrackingWindow(String username) {
        setTitle("Order Tracking");
        initModality(Modality.APPLICATION_MODAL);

        ListView<Order> orders = new ListView<>();
        Runnable refresh = () -> orders.getItems().setAll(Database.getInstance().getOrdersByCustomer(username));
        refresh.run();

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refresh.run());

        // Auto-refresh on focus
        focusedProperty().addListener((obs, was, now) -> { if (now) refresh.run(); });

        HBox actions = new HBox(5, refreshBtn);
        VBox root = new VBox(10,
                new Label("Orders for: " + username),
                orders,
                actions
        );
        root.setPadding(new Insets(10));

        setScene(new Scene(root, 400, 400));
    }
}
