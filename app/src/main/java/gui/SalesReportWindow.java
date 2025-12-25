package gui;

import database.Database;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import orders.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SalesReportWindow extends Stage {

    public SalesReportWindow() {
        setTitle("Sales Report");
        initModality(Modality.APPLICATION_MODAL);

        Label titleLabel = new Label("Sales Report");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        DatePicker start = new DatePicker(LocalDate.now().minusDays(7));
        start.setPrefHeight(35);
        start.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        DatePicker end = new DatePicker(LocalDate.now());
        end.setPrefHeight(35);
        end.setStyle("-fx-font-size: 13px; -fx-background-radius: 5;");

        Button today = new Button("Today");
        Button week = new Button("Last 7 days");
        Button month = new Button("Last 30 days");
        Button apply = new Button("Apply Filter");

        today.setPrefHeight(35);
        week.setPrefHeight(35);
        month.setPrefHeight(35);
        apply.setPrefHeight(40);

        today.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 5;");
        week.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 5;");
        month.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 5;");
        apply.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;");

        today.setOnMouseEntered(e -> today.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        today.setOnMouseExited(e -> today.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        week.setOnMouseEntered(e -> week.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        week.setOnMouseExited(e -> week.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        month.setOnMouseEntered(e -> month.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        month.setOnMouseExited(e -> month.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        apply.setOnMouseEntered(e -> apply.setStyle("-fx-background-color: #229954; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        apply.setOnMouseExited(e -> apply.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;"));

        Label summary = new Label("Total: $0.00 | Orders: 0");
        summary.setFont(Font.font("System", FontWeight.BOLD, 16));
        summary.setTextFill(Color.web("#27ae60"));

        ListView<Order> orderList = new ListView<>();
        orderList.setPrefHeight(400);
        orderList.setStyle("-fx-font-size: 13px;");

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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Date Range");
                alert.setHeaderText("Invalid Date Range");
                Label content = new Label("Please select a valid date range.\n\n" +
                        "• Start date must be before or equal to end date\n" +
                        "• Both dates must be selected");
                content.setWrapText(true);
                content.setMaxWidth(400);
                content.setStyle("-fx-font-size: 13px; -fx-padding: 10px;");
                ScrollPane scrollPane = new ScrollPane(content);
                scrollPane.setFitToWidth(true);
                alert.getDialogPane().setContent(scrollPane);
                alert.getDialogPane().setPrefWidth(450);
                alert.show();
                return;
            }
            List<Order> orders = Database.getInstance().getOrdersBetween(s, ed);
            // Filter out cancelled orders for display
            List<Order> activeOrders = orders.stream()
                    .filter(o -> !"Cancelled".equalsIgnoreCase(o.getStatus()))
                    .collect(java.util.stream.Collectors.toList());
            double total = Database.getInstance().getTotalSalesBetween(s, ed);
            summary.setText(String.format("Total: $%.2f | Orders: %d (excluding cancelled)", total, activeOrders.size()));
            orderList.getItems().setAll(activeOrders);
        });

        HBox quick = new HBox(10);
        quick.getChildren().addAll(today, week, month, apply);

        GridPane dateGrid = new GridPane();
        dateGrid.setHgap(10);
        dateGrid.setVgap(10);
        dateGrid.add(new Label("Start Date:"), 0, 0);
        dateGrid.add(start, 1, 0);
        dateGrid.add(new Label("End Date:"), 0, 1);
        dateGrid.add(end, 1, 1);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #ffffff;");
        root.getChildren().addAll(
                titleLabel,
                new Separator(),
                dateGrid,
                quick,
                summary,
                new Label("Orders:"),
                orderList
        );

        setScene(new Scene(root, 500, 600));
    }
}
