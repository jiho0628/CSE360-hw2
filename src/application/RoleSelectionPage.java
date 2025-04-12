package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import databasePart1.*;

public class RoleSelectionPage {
	
	private final DatabaseHelper databaseHelper;
    public RoleSelectionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
	// create buttons for each role
	Button reviewerBtn;
	Button staffBtn;
	Button studentBtn;
	Button instructorBtn;
	
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, select your role!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    layout.getChildren().add(userLabel);
	    
	    
	    // Initialize buttons for each role
	    reviewerBtn = new Button("Select reviewer role");
	    staffBtn = new Button("Select staff role");
	    studentBtn = new Button("Select student role");
	    instructorBtn = new Button("Select instructor role");
	    
	    // Show buttons for user roles
	    btnShowForRole(user, layout);
	    
	    // Button pressed logic
	    reviewerBtn.setOnAction(e -> {
	        new ReviewerHomePage(databaseHelper, user).show(primaryStage);
	        
	    });
	    
	    staffBtn.setOnAction(e -> {
	        new StaffHomePage(databaseHelper, user).show(primaryStage);
	        
	    });
	    
	    studentBtn.setOnAction(e -> {
	        new StudentHomePage(databaseHelper, user).show(primaryStage);
	        
	    });
	    
	    instructorBtn.setOnAction(e -> {
	        new InstructorHomePage(databaseHelper, user).show(primaryStage);
	        
	    });
	    
	    // Create scene 
	    Scene userScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Role Selection Page");
    }
    
    // Handle logic for checking valid roles
    private void btnShowForRole(User user, VBox selectionPane) {
    	String role = user.getRoles();
    	
    	if (role.contains("student")) {
    		selectionPane.getChildren().add(studentBtn);
    	}
    	if (role.contains("instructor")) {
    		selectionPane.getChildren().add(instructorBtn);
    	}
    	if (role.contains("reviewer")) {
    		selectionPane.getChildren().add(reviewerBtn);
    	}
    	if (role.contains("staff")) {
    		selectionPane.getChildren().add(staffBtn);
    	}
    }
}
