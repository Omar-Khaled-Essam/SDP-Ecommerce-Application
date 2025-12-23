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

        Database.getInstance().seedDefaults();

        TextField user = new TextField();
        user.setPromptText("Username");

        PasswordField pass = new PasswordField();
        pass.setPromptText("Password");

        Label msg = new Label();

        Button login = new Button("Login");
        Button register = new Button("Register");
        Button recover = new Button("Forgot Password");

        login.setOnAction(e -> {
            User u = Database.getInstance().authenticate(user.getText(), pass.getText());
            if (u == null) {
                msg.setText("Invalid credentials");
                return;
            }
            stage.close();
            try {
                if (u instanceof Admin)
                    new AdminDashboard((Admin) u).start(new Stage());
                else
                    new CustomerDashboard((Customer) u).start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        register.setOnAction(e -> {
            stage.close();
            try { new RegisterUI().start(new Stage()); } catch (Exception ex) {}
        });

        recover.setOnAction(e -> {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Password Recovery");
            int a = (int)(Math.random() * 10) + 1;
            int b = (int)(Math.random() * 10) + 1;
            Label prompt = new Label("Solve to reset password: " + a + " + " + b + " = ?");
            TextField answer = new TextField();
            answer.setPromptText("Answer");
            TextField newPass = new TextField();
            newPass.setPromptText("New Password");
            ButtonType resetType = new ButtonType("Reset", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(resetType, ButtonType.CANCEL);
            dialog.getDialogPane().setContent(new VBox(10, prompt, answer, newPass));
            dialog.setResultConverter(btn -> {
                if (btn == resetType) {
                    try {
                        int val = Integer.parseInt(answer.getText());
                        if (val == a + b && !newPass.getText().isEmpty()) {
                            Database.getInstance().updatePassword(user.getText(), newPass.getText());
                            new Alert(Alert.AlertType.INFORMATION, "Password updated.").show();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Incorrect answer or empty password.").show();
                        }
                    } catch (NumberFormatException ex) {
                        new Alert(Alert.AlertType.ERROR, "Please enter a number.").show();
                    }
                }
                return null;
            });
            dialog.showAndWait();
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
