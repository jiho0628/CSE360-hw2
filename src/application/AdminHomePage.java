package application;

import java.sql.SQLException;
import databasePart1.DatabaseHelper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */
public class AdminHomePage {
    /**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     * @param databaseHelper The database helper instance to handle database operations.
     */
    public void show(Stage primaryStage, DatabaseHelper databaseHelper) {
        VBox layout = new VBox(10); // Spacing of 10 between elements
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20;");

        // Label to display the welcome message for the admin
        Label adminLabel = new Label("Hello, Admin!");
        adminLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

//        // OTP Generation Section
//        Label otpSectionLabel = new Label("Generate OTP for User:");
//        TextField emailField = new TextField();
//        emailField.setPromptText("Enter user's email");
//        Button generateOTPButton = new Button("Generate OTP");
//
//        // Generate OTP Button Logic
//        generateOTPButton.setOnAction(event -> {
//            String email = emailField.getText().trim();
//            if (email.isEmpty()) {
//                showAlert("Error", "Email field cannot be empty.", Alert.AlertType.ERROR);
//                return;
//            }
//            try {
//                databaseHelper.connectToDatabase();
//                if (!databaseHelper.doesEmailExist(email)) {
//                    showAlert("Error", "Email not found in the system.", Alert.AlertType.ERROR);
//                    return;
//                }
//
//                String otp = databaseHelper.generateOTP();
//                if (databaseHelper.storeOTP(email, otp)) {
//                    showAlert("OTP Generated", "OTP for " + email + " is: " + otp, Alert.AlertType.INFORMATION);
//                } else {
//                    showAlert("Error", "Failed to generate OTP.", Alert.AlertType.ERROR);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                showAlert("Error", "Database error occurred.", Alert.AlertType.ERROR);
//            } finally {
//                databaseHelper.closeConnection();
//            }
//        });

        // Button to View User List
        Button listBtn = new Button("View User List");
        listBtn.setOnAction(e -> {
            try {
                new AdminViewAllUsers().show(primaryStage, databaseHelper);
            } catch (SQLException e1) {
                e1.printStackTrace();
                showAlert("Error", "Failed to load user list.", Alert.AlertType.ERROR);
            }
        });

        // Add all UI elements to layout
//        layout.getChildren().addAll(adminLabel, otpSectionLabel, emailField, generateOTPButton, listBtn);
        layout.getChildren().addAll(adminLabel, listBtn);

        // Create Scene
        Scene adminScene = new Scene(layout, 800, 400);
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
        primaryStage.show();
    }

    /**
     * Helper method to display an alert dialog.
     * @param title The title of the alert.
     * @param message The message content.
     * @param type The type of alert.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
