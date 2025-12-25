package gui;

// SOLID: SRP⚠️ (UI + registration logic), DIP⚠️ (calls Database singleton directly).
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

public class RegisterUI {

    public void start(Stage stage) {
        stage.setTitle("Register - E-Commerce Application");

        // Title
        Label titleLabel = new Label("Create Account");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));

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

        // Role selection
        Label roleLabel = new Label("Role:");
        roleLabel.setStyle("-fx-font-weight: bold;");
        ChoiceBox<String> role = new ChoiceBox<>();
        role.getItems().addAll("Admin", "Customer");
        role.setValue("Customer");
        role.setPrefHeight(35);
        role.setStyle("-fx-font-size: 14px; -fx-background-radius: 5;");

        // Error message label
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-font-size: 12px;");
        errorLabel.setVisible(false);

        // Success message label
        Label successLabel = new Label();
        successLabel.setTextFill(Color.GREEN);
        successLabel.setStyle("-fx-font-size: 12px;");
        successLabel.setVisible(false);

        // Register button
        Button register = new Button("Register");
        register.setPrefWidth(200);
        register.setPrefHeight(40);
        register.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;");
        register.setOnMouseEntered(e -> register.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        register.setOnMouseExited(e -> register.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;"));

        // Back to login button
        Button backToLogin = new Button("Back to Login");
        backToLogin.setPrefWidth(200);
        backToLogin.setPrefHeight(40);
        backToLogin.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;");
        backToLogin.setOnMouseEntered(e -> backToLogin.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;"));
        backToLogin.setOnMouseExited(e -> backToLogin.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;"));

        register.setOnAction(e -> {
            errorLabel.setVisible(false);
            successLabel.setVisible(false);

            String username = user.getText().trim();
            String password = pass.getText();
            String selectedRole = role.getValue();

            // Validation
            if (username.isEmpty()) {
                errorLabel.setText("Username cannot be empty!");
                errorLabel.setVisible(true);
                return;
            }

            if (password.isEmpty()) {
                errorLabel.setText("Password cannot be empty!");
                errorLabel.setVisible(true);
                return;
            }

            // Check if username already exists
            if (Database.getInstance().usernameExists(username)) {
                errorLabel.setText("Username already exists! Please choose a different username.");
                errorLabel.setVisible(true);
                return;
            }

            // Register user
            try {
                User u = UserFactory.createUser(selectedRole, username, password);
                Database.getInstance().registerUser(u);
                successLabel.setText("Registration successful! Redirecting to login...");
                successLabel.setVisible(true);

                // Close and open login after a short delay
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(() -> {
                            stage.close();
                            try {
                                new LoginUI().start(new Stage());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            } catch (Exception ex) {
                errorLabel.setText("Registration failed: " + ex.getMessage());
                errorLabel.setVisible(true);
            }
        });

        backToLogin.setOnAction(e -> {
            stage.close();
            try {
                new LoginUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Form layout
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(30));
        formBox.setMaxWidth(400);
        formBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");

        VBox fieldsBox = new VBox(10);
        fieldsBox.getChildren().addAll(
                usernameLabel, user,
                passwordLabel, pass,
                roleLabel, role
        );

        formBox.getChildren().addAll(
                titleLabel,
                new Separator(),
                fieldsBox,
                errorLabel,
                successLabel,
                register,
                backToLogin
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
}
