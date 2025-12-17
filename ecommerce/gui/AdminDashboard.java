package gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import database.Database;
import products.*;

public class AdminDashboard {

    public void start(Stage stage) {

        Button addE = new Button("Add Electronics");
        Button addC = new Button("Add Clothing");

        addE.setOnAction(e ->
            Database.getInstance().getProducts().add(new ElectronicsFactory().createProduct())
        );

        addC.setOnAction(e ->
            Database.getInstance().getProducts().add(new ClothingFactory().createProduct())
        );

        Button logout = new Button("Logout");
        logout.setOnAction(e -> {
            stage.close();
            try { new LoginUI().start(new Stage()); } catch (Exception ex) {}
        });

        VBox root = new VBox(10, addE, addC, logout);
        root.setStyle("-fx-padding:20");
        stage.setScene(new Scene(root, 300, 250));
        stage.show();
    }
}
