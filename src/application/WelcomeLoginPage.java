package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show( Stage primaryStage, User user) {
    	
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label welcomeLabel = new Label("Welcome!!");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to navigate to the user's respective page based on their role
	    Button continueButton = new Button("Continue to your Page");
	    continueButton.setOnAction(a -> {
	    	String role =user.getRoles();
	    	System.out.println(role);
	    	
	    	if(role.contains("admin")) {
	    		new AdminHomePage().show(primaryStage, databaseHelper);
	    	}
	    	else if(roleNumCheck(user) > 1) {
	    		new RoleSelectionPage(databaseHelper).show(primaryStage, user);
	    	}
	    	else if (role.contains("student")) {
	    		new StudentHomePage(databaseHelper, user).show(primaryStage);
	    	}
	    	else if (role.contains("instructor")) {
	    		new InstructorHomePage(databaseHelper, user).show(primaryStage);
	    	}
	    	else if (role.contains("staff")) {
	    		new StaffHomePage(databaseHelper, user).show(primaryStage);
	    	}
	    	else if (role.contains("reviewer")) {
	    		new ReviewerHomePage(databaseHelper, user).show(primaryStage);
	    	}
	    });
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });
	    
	    // "Invite" button for admin to generate invitation codes
	    if (user.getRoles().contains("admin")) { // CS toCheck
            Button inviteButton = new Button("Invite");
            inviteButton.setOnAction(a -> {
                new InvitationPage().show(databaseHelper, primaryStage);
            });
            layout.getChildren().add(inviteButton);
        }

	    layout.getChildren().addAll(welcomeLabel,continueButton,quitButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
    
    // CS toCheck
    public int roleNumCheck(User user) {
    	int numRoles = 0;
    	String role = user.getRoles();
    	if (role.contains("student")) {
    		numRoles++;
    	}
    	if (role.contains("instructor")) {
    		numRoles++;
    	}
    	if (role.contains("reviewer")) {
    		numRoles++;
    	}
    	if (role.contains("staff")) {
    		numRoles++;
    	}
    	return numRoles;
    	
    }
}