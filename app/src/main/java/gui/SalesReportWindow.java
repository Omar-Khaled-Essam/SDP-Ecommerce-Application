package gui;

import database.Database;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import orders.Order;

import java.time.LocalDate;
import java.util.List;

public class SalesReportWindow extends Stage {

    public SalesReportWindow() {
        setTitle("Sales Report");
        initModality(Modality.APPLICATION_MODAL);

        DatePicker start = new DatePicker(LocalDate.now().minusDays(7));
        DatePicker end = new DatePicker(LocalDate.now());

        Button today = new Button("Today");
        Button week = new Button("Last 7 days");
        Button month = new Button("Last 30 days");
        Button apply = new Button("Apply");

        Label summary = new Label("Total: 0 | Orders: 0");
        ListView<Order> orderList = new ListView<>();

        today.setOnAction(e -> {
            start.setValue(LocalDate.now());
            end.setValue(LocalDate.now());
        });
        week.setOnAction(e -> {
            start.setValue(LocalDate.now().minusDays(7));
            end.setValue(LocalDate.now());
        });
        month.setOnAction(e -> {
            start.setValue(LocalDate.now().minusDays(30));
            end.setValue(LocalDate.now());
        });

        apply.setOnAction(e -> {
            LocalDate s = start.getValue();
            LocalDate ed = end.getValue();
            if (s == null || ed == null || s.isAfter(ed)) {
                new Alert(Alert.AlertType.ERROR, "Invalid date range").show();
                return;
            }
            List<Order> orders = Database.getInstance().getOrdersBetween(s, ed);
            double total = Database.getInstance().getTotalSalesBetween(s, ed);
            summary.setText("Total: " + total + " | Orders: " + orders.size());
            orderList.getItems().setAll(orders);
        });

        HBox quick = new HBox(5, today, week, month, apply);
        VBox root = new VBox(10,
                new Label("Start Date"), start,
                new Label("End Date"), end,
                quick,
                summary,
                orderList
        );
        root.setPadding(new Insets(10));

        setScene(new Scene(root, 400, 500));
    }
}
