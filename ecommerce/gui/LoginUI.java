package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import database.Database;
import users.*;

public class LoginUI extends Application {

    @Override
    public void start(Stage stage) {

        Database db = Database.getInstance();
        db.seedUsers();
        db.seedProducts();

        Label title = new Label("E-Commerce Login");

        TextField userField = new TextField();
        userField.setPromptText("Username");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Label message = new Label();

        Button loginBtn = new Button("Login");

        loginBtn.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            User user = db.authenticate(username, password);

            if (user == null) {
                message.setText("Invalid username or password");
                return;
            }

            stage.close();

            try {
                if (user instanceof Admin) {
                    new AdminDashboard().start(new Stage());
                } else if (user instanceof Customer) {
                   new CustomerDashboard().start(new Stage());

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(10, title, userField, passField, loginBtn, message);
        root.setStyle("-fx-padding: 20");

        stage.setScene(new Scene(root, 300, 250));
        stage.setTitle("Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
