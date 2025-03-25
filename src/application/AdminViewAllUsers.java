package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class AdminViewAllUsers {
	
	private TableView<User> table = new TableView<User>();
	
	 public void show(Stage primaryStage, DatabaseHelper databaseHelper) throws SQLException {
	    	VBox layout = new VBox();
		    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
		    
		    // Label to display Helvlo user
		    Label userLabel = new Label("Select user from list below:");
		    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		    Label tableLabel = new Label("User Information");
		    
		    table.setEditable(true);
		    
	        TableColumn<User, String> emailCol = new TableColumn<User, String>("Email");
	        TableColumn<User, String> nameCol = new TableColumn<User, String>("Name");
		    TableColumn<User, String> userNameCol = new TableColumn<User, String>("User Name");
		    TableColumn<User, String> passwordCol = new TableColumn<User, String>("Password");
		    TableColumn<User, String> rolesCol = new TableColumn<User, String>("Roles");
		    
	        emailCol.setCellValueFactory(
	        		new PropertyValueFactory<User, String>("email")
	        		);
	        nameCol.setCellValueFactory(
	        		new PropertyValueFactory<User, String>("name")
	        		);
	        userNameCol.setCellValueFactory(
	        		new PropertyValueFactory<User, String>("userName")
	        		);
	        passwordCol.setCellValueFactory(
	        		new PropertyValueFactory<User, String>("password")
	        		);
	        rolesCol.setCellValueFactory(
	        		new PropertyValueFactory<User, String>("roles")
	        		);


	        
	        table.setItems(databaseHelper.getAllUsers());
	        table.getColumns().addAll(emailCol, nameCol, userNameCol, passwordCol, rolesCol);
	        
	        Button btnDelete = new Button("Delete User");
	        btnDelete.setOnAction(e -> btnDelete_Clickled(databaseHelper));
	        
		    layout.getChildren().addAll(userLabel, table, btnDelete);
		    Scene userScene = new Scene(layout, 800, 400);

		    // Set the scene to primary stage
		    primaryStage.setScene(userScene);
		    primaryStage.setTitle("Users List");
	    	
	    }
	 
	 public void btnDelete_Clickled(DatabaseHelper dbhelper) {
		 ObservableList<User> sel, items;
		 items = table.getItems();
		 sel = table.getSelectionModel().getSelectedItems();
		 for (User u : sel) {
			 items.remove(u);
			 dbhelper.deleteUser(u.getUserName());
			 System.out.println(u.getUserName());
		 }
	 }
}
