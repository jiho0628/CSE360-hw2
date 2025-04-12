package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        // Labels to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Label newPasswordErrorLabel = new Label();
        newPasswordErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");


        Button loginButton = new Button("Login");
        Button newPasswordBtn = new Button("Request new password");
        
        loginButton.setOnAction(a -> {
        	// Retrieve user inputs
            String userName = userNameField.getText();
            String password = passwordField.getText();
            
            try {
            	User user=new User(userName, password, "", "", "");
            	WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
            	
            	// Retrieve the user's role from the database using userName
            	String role = databaseHelper.getUserRole(userName);
            	
            	if(role!=null) {
            		user.setRole(role);
            		if(databaseHelper.login(user)) {
            			welcomeLoginPage.show(primaryStage,user);
            		}
            		else {
            			// Display an error if the login fails
                        errorLabel.setText("Error logging in");
            		}
            	}
            	else {
            		// Display an error if the account does not exist
                    errorLabel.setText("user account doesn't exists");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            } 
        });

        newPasswordBtn.setOnAction(e -> {
        	newPasswordErrorLabel.setText("");
        	
        	// if user name field is invalid, prompt user to enter valid user name
        	if (!databaseHelper.doesUserExist(userNameField.getText())) {
        		newPasswordErrorLabel.setText("Please enter a valid username");
        	}
        	// else user name is valid so generate one time password
        	else {
        		
        	}
        });
        
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
//        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel, newPasswordBtn, newPasswordErrorLabel);
        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel, newPasswordErrorLabel);


        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}
