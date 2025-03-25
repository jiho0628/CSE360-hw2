package application;


import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class InvitationPage {

	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
	
	// Create Check Boxes for easy access
	CheckBox chkStudent, chkReviewer, chkInstructor, chkStaff;
	
    public void show(DatabaseHelper databaseHelper,Stage primaryStage) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display the title of the page
	    Label userLabel = new Label("Invite ");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to generate the invitation code
	    Button showCodeButton = new Button("Generate Invitation Code");
	    
	    // Label to display the generated invitation code
	    Label inviteCodeLabel = new Label(""); ;
        inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        
        
        /*
         * 
         * IMPLEMENTATION OF DIFFERENT ROLES *
         * 
         */
        
        // Initialize Check Boxes
        chkStudent = new CheckBox("Student");
	    chkReviewer = new CheckBox("Reviewer");
	    chkInstructor = new CheckBox("Instructor");
	    chkStaff = new CheckBox("Staff");
        
	    // Add check boxes to vbox
	    VBox paneRoles = new VBox(userLabel, chkStudent, chkReviewer, chkInstructor, chkStaff);
	    paneRoles.setStyle("-fx-alignment: center; -fx-padding: 20; -fx-padding: 20;");
	    
	    // Label to display the generated invitation code
	    Label inviteRolesLabel = new Label();
	    inviteRolesLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-padding: 20;");
	    
	    
        showCodeButton.setOnAction(a -> {
        	// Obtain role message
        	String roleLabel = "The selected roles are: ";
        	String selectedRoles = generateInvitationMessage();
        	
        	// Make sure admin selects at least one role
            if (selectedRoles.equals("")) {
            	inviteRolesLabel.setText("Please select 1 or more role(s)");
            	inviteCodeLabel.setText("");
            }
            else {
            	// Generate the invitation code and set user roles using the databaseHelper
            	System.out.println(selectedRoles);
                String invitationCode = databaseHelper.generateInvitationCode(selectedRoles);
                inviteCodeLabel.setText("Invitation code: " + invitationCode);
                inviteRolesLabel.setText(roleLabel + selectedRoles);
            }
        	

        });
	    
        // Add to layout
        layout.getChildren().addAll(paneRoles, showCodeButton, inviteRolesLabel, inviteCodeLabel);
	    Scene inviteScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(inviteScene);
	    primaryStage.setTitle("Invite Page");
    	
    }
    
    // Generate roles
    private String generateInvitationMessage() {
    	String assignedRole = "";
    	
    	if (chkStudent.isSelected()) {
    		assignedRole += "student, ";
    	}
    	if (chkReviewer.isSelected()) {
    		assignedRole += "reviewer, ";
    	}
    	if (chkInstructor.isSelected()) {
    		assignedRole += "instructor, ";
    	}
    	if (chkStaff.isSelected()) {
    		assignedRole += "staff, ";
    	}
    	return assignedRole;
    }
    
}