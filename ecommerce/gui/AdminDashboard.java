package gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import database.Database;
import products.*;

public class AdminDashboard {

    public void start(Stage stage) {

        Label title = new Label("Admin Dashboard");

        Button addElectronics = new Button("Add Electronics");
        Button addClothing = new Button("Add Clothing");

        addElectronics.setOnAction(e -> {
            Product p = new ElectronicsFactory().createProduct();
            Database.getInstance().getProducts().add(p);
            showAlert("Electronics added");
        });

        addClothing.setOnAction(e -> {
            Product p = new ClothingFactory().createProduct();
            Database.getInstance().getProducts().add(p);
            showAlert("Clothing added");
        });

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
                addElectronics,
                addClothing,
                logoutBtn
        );
        layout.setStyle("-fx-padding: 20");

        stage.setScene(new Scene(layout, 300, 250));
        stage.setTitle("Admin");
        stage.show();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
