package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import inputValidation.PasswordEvaluator;
import inputValidation.UserNameRecognizer;
import inputValidation.EmailEvaluator;
import inputValidation.NameEvaluator;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {

    private final DatabaseHelper databaseHelper;
	CheckBox chkStudent, chkReviewer, chkInstructor, chkStaff;
	private String userRoles = "";
	//private int numRoles = 0;

    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	
    public void show(Stage primaryStage) {
    	Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        // Input fields for email, name, username, password, and invitation code
    	TextField emailField = new TextField();
    	emailField.setPromptText("Enter email address");
    	emailField.setMaxWidth(250);
    	
    	TextField nameField = new TextField();
    	nameField.setPromptText("Enter your name");
    	nameField.setMaxWidth(250);
    	
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter UserName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter Invitation Code");
        inviteCodeField.setMaxWidth(250);

        // Labels to display validation errors for username and password
        Label emailErrorLabel = new Label();
        emailErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Label nameErrorLabel = new Label();
        nameErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Label userNameErrorLabel = new Label();
        userNameErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Label passwordErrorLabel = new Label();
        passwordErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Label to display general error messages for invitation code or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        //TODO: Add error labels for email and name fields

        Button setupButton = new Button("Setup");
	    
        setupButton.setOnAction(event -> {
            // Retrieve user input
        	String email = emailField.getText();
        	String name = nameField.getText();
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();

            // Validate username and password
            String emailValidationResult = EmailEvaluator.checkForValidEmail(email);
            String nameValidationResult = NameEvaluator.checkForValidName(name);
            String usernameValidationResult = UserNameRecognizer.checkForValidUserName(userName);
            String passwordValidationResult = PasswordEvaluator.evaluatePassword(password);

            // Clear previous error messages
            emailErrorLabel.setText("");
            nameErrorLabel.setText("");
            userNameErrorLabel.setText("");
            passwordErrorLabel.setText("");
            errorLabel.setText("");

            boolean isEmailValid = emailValidationResult.isEmpty();
            boolean isNameValid = nameValidationResult.isEmpty();
            boolean isUsernameValid = usernameValidationResult.isEmpty();
            boolean isPasswordValid = passwordValidationResult.isEmpty();

            if (!isEmailValid) {
            	emailErrorLabel.setText("Invalid Email: " + emailValidationResult.trim());
                System.out.println(emailErrorLabel);
            }

            if (!isNameValid) {
                nameErrorLabel.setText("Invalid Name: " + nameValidationResult.trim());
            }
            
            if (!isUsernameValid) {
                userNameErrorLabel.setText("Invalid Username: " + usernameValidationResult.trim());
            }

            if (!isPasswordValid) {
                passwordErrorLabel.setText("Invalid Password: " + passwordValidationResult.trim());
            }

            // If both username and password are valid, proceed with registration
            if (isUsernameValid && isPasswordValid && isEmailValid && isNameValid) {
                try {
                    if (!databaseHelper.doesUserExist(userName)) {
                        if (databaseHelper.validateInvitationCode(code)) {
                        	// Set user roles as dictated by admin
                        	userRoles = databaseHelper.getUserRoleFromInvitationCodes(code);
                        	
                            User user = new User(userName, password, "user," + userRoles, name, email);
                        	databaseHelper.register(user);

                            // Navigate to the Welcome Login Page
                            new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
                        } else {
                        	errorLabel.setText("Invalid Invitation Code.");
                        }
                    } else {
                    	errorLabel.setText("Username is already taken.");
                    }
                } catch (SQLException e) {
                	errorLabel.setText("Database error occurred.");
                    e.printStackTrace();
                }
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(backButton,
        		emailField,
        		nameField,
        		userNameField, 
        		passwordField, 
        		inviteCodeField, 
        		setupButton, 
        		errorLabel,  
        		emailErrorLabel,
        		nameErrorLabel,
        		userNameErrorLabel, 
        		passwordErrorLabel);
        backButton.setTranslateX(-100);
        backButton.setTranslateY(-15);


        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}