package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import database.Database;
import users.*;

public class LoginUI extends Application {

    public void start(Stage stage) {

        Database.getInstance().seedProducts();

        TextField user = new TextField();
        user.setPromptText("Username");

        PasswordField pass = new PasswordField();
        pass.setPromptText("Password");

        Label msg = new Label();

        Button login = new Button("Login");
        Button register = new Button("Register");

        login.setOnAction(e -> {
            User u = Database.getInstance().authenticate(user.getText(), pass.getText());
            if (u == null) {
                msg.setText("Invalid credentials");
                return;
            }
            stage.close();
            try {
                if (u instanceof Admin)
                    new AdminDashboard().start(new Stage());
                else
                    new CustomerDashboard().start(new Stage());
            } catch (Exception ex) {}
        });

        register.setOnAction(e -> {
            stage.close();
            try { new RegisterUI().start(new Stage()); } catch (Exception ex) {}
        });

        VBox root = new VBox(10, user, pass, login, register, msg);
        root.setStyle("-fx-padding:20");
        stage.setScene(new Scene(root, 300, 250));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
