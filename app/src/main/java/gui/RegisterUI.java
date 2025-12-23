package gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import database.Database;
import users.*;

public class RegisterUI {

    public void start(Stage stage) {

        TextField user = new TextField();
        user.setPromptText("Username");

        PasswordField pass = new PasswordField();
        pass.setPromptText("Password");

        ChoiceBox<String> role = new ChoiceBox<>();
        role.getItems().addAll("Admin", "Customer");
        role.setValue("Customer");

        Button register = new Button("Register");

        register.setOnAction(e -> {
            User u = UserFactory.createUser(role.getValue(), user.getText(), pass.getText());
            Database.getInstance().registerUser(u);
            stage.close();
            try { new LoginUI().start(new Stage()); } catch (Exception ex) {}
        });

        VBox root = new VBox(10, user, pass, role, register);
        root.setStyle("-fx-padding:20");
        stage.setScene(new Scene(root, 300, 250));
        stage.show();
    }
}
