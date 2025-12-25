package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import database.Database;
import users.*;

class AlertHelper {
    static Alert createFormattedAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(400);
        contentLabel.setStyle("-fx-font-size: 13px; -fx-padding: 10px;");

        ScrollPane scrollPane = new ScrollPane(contentLabel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(Math.min(200, content.split("\n").length * 25 + 50));
        scrollPane.setMaxHeight(300);

        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefWidth(450);
        return alert;
    }
}

public class LoginUI extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Login - E-Commerce Application");

        Database.getInstance().seedDefaults();

        // Title
        Label titleLabel = new Label("Welcome Back");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        Label subtitleLabel = new Label("Sign in to your account");
        subtitleLabel.setFont(Font.font("System", 14));
        subtitleLabel.setTextFill(Color.web("#7f8c8d"));

        // Username field
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-weight: bold;");
        TextField user = new TextField();
        user.setPromptText("Enter username");
        user.setPrefHeight(35);
        user.setStyle("-fx-font-size: 14px; -fx-background-radius: 5;");

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        PasswordField pass = new PasswordField();
        pass.setPromptText("Enter password");
        pass.setPrefHeight(35);
        pass.setStyle("-fx-font-size: 14px; -fx-background-radius: 5;");

        // Error message label
        Label msg = new Label();
        msg.setTextFill(Color.RED);
        msg.setStyle("-fx-font-size: 12px;");
        msg.setVisible(false);

        // Login button
        Button login = new Button("Login");
        login.setPrefWidth(200);
        login.setPrefHeight(40);
        login.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;");
        login.setOnMouseEntered(e -> login.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        login.setOnMouseExited(e -> login.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;"));

        // Register button
        Button register = new Button("Create Account");
        register.setPrefWidth(200);
        register.setPrefHeight(40);
        register.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;");
        register.setOnMouseEntered(e -> register.setStyle("-fx-background-color: #229954; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        register.setOnMouseExited(e -> register.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;"));

        // Forgot password button
        Button recover = new Button("Forgot Password?");
        recover.setStyle("-fx-background-color: transparent; -fx-text-fill: #3498db; -fx-font-size: 12px; -fx-underline: true;");
        recover.setOnMouseEntered(e -> recover.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9; -fx-font-size: 12px; -fx-underline: true;"));
        recover.setOnMouseExited(e -> recover.setStyle("-fx-background-color: transparent; -fx-text-fill: #3498db; -fx-font-size: 12px; -fx-underline: true;"));

        login.setOnAction(e -> {
            msg.setVisible(false);
            String username = user.getText().trim();

            // Check if password reset is required first
            if (!username.isEmpty() && Database.getInstance().isPasswordResetRequired(username)) {
                Alert resetAlert = AlertHelper.createFormattedAlert(Alert.AlertType.WARNING,
                        "Password Reset Required", "Password Reset Required",
                        "Your password has been reset by an administrator.\n\n" +
                                "You must reset your password using the 'Forgot Password?' feature before you can login.\n\n" +
                                "Steps to reset:\n" +
                                "1. Click the 'Forgot Password?' button\n" +
                                "2. Enter your username\n" +
                                "3. Solve the math challenge\n" +
                                "4. Enter your new password");
                resetAlert.showAndWait();
                msg.setText("Password reset required. Please use 'Forgot Password?'");
                msg.setVisible(true);
                return;
            }

            User u = Database.getInstance().authenticate(username, pass.getText());
            if (u == null) {
                msg.setText("Invalid username or password");
                msg.setVisible(true);
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
            try {
                new RegisterUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        recover.setOnAction(e -> {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Password Recovery");
            dialog.setHeaderText("Password Recovery");

            int a = (int) (Math.random() * 10) + 1;
            int b = (int) (Math.random() * 10) + 1;
            Label prompt = new Label("Solve to reset password: " + a + " + " + b + " = ?");
            prompt.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            TextField answer = new TextField();
            answer.setPromptText("Answer");
            answer.setPrefHeight(35);
            answer.setStyle("-fx-font-size: 14px; -fx-background-radius: 5;");

            PasswordField newPass = new PasswordField();
            newPass.setPromptText("New Password");
            newPass.setPrefHeight(35);
            newPass.setStyle("-fx-font-size: 14px; -fx-background-radius: 5;");

            Label usernameLabel2 = new Label("Username:");
            usernameLabel2.setStyle("-fx-font-weight: bold;");
            TextField usernameField = new TextField();
            usernameField.setPromptText("Enter username");
            usernameField.setPrefHeight(35);
            usernameField.setStyle("-fx-font-size: 14px; -fx-background-radius: 5;");

            ButtonType resetType = new ButtonType("Reset", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(resetType, ButtonType.CANCEL);

            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            content.getChildren().addAll(usernameLabel2, usernameField, prompt, answer, newPass);
            dialog.getDialogPane().setContent(content);

            dialog.setResultConverter(btn -> {
                if (btn == resetType) {
                    try {
                        int val = Integer.parseInt(answer.getText());
                        String username = usernameField.getText().trim();
                        if (username.isEmpty()) {
                            AlertHelper.createFormattedAlert(Alert.AlertType.ERROR, "Input Required",
                                    "Username Required", "Please enter your username to reset your password.").show();
                            return null;
                        }
                        if (val == a + b && !newPass.getText().isEmpty()) {
                            Database.getInstance().updatePassword(username, newPass.getText());
                            AlertHelper.createFormattedAlert(Alert.AlertType.INFORMATION, "Success",
                                    "Password Updated", "Your password has been updated successfully!\n\nYou can now login with your new password.").show();
                        } else {
                            AlertHelper.createFormattedAlert(Alert.AlertType.ERROR, "Error",
                                    "Password Update Failed", "Incorrect answer or empty password.\n\nPlease:\n• Solve the math challenge correctly\n• Enter a new password").show();
                        }
                    } catch (NumberFormatException ex) {
                        AlertHelper.createFormattedAlert(Alert.AlertType.ERROR, "Invalid Input",
                                "Invalid Answer", "Please enter a valid number for the math challenge.").show();
                    }
                }
                return null;
            });
            dialog.showAndWait();
        });

        // Form layout
        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(40));
        formBox.setMaxWidth(400);
        formBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");

        VBox fieldsBox = new VBox(10);
        fieldsBox.getChildren().addAll(
                usernameLabel, user,
                passwordLabel, pass
        );

        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);

        formBox.getChildren().addAll(
                titleBox,
                new Separator(),
                fieldsBox,
                msg,
                login,
                register,
                recover
        );

        // Main container with background
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #ecf0f1, #bdc3c7);");
        root.getChildren().add(formBox);

        Scene scene = new Scene(root, 500, 600);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
