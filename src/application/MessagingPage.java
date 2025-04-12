package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import application.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class MessagingPage {

	private final DatabaseHelper databaseHelper;
    private final User currentUser; // the student
    private ComboBox<String> reviewerComboBox;
    // Change the ListView type from String to ChatMessage
    private ListView<ChatMessage> conversationListView;
    private TextField messageInput;

    public MessagingPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(Stage stage) {
        // Main layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        // Label for the page title
        Label titleLabel = new Label("Private Messaging");

        // Dropdown list to select a reviewer
        reviewerComboBox = new ComboBox<>();
        reviewerComboBox.setPromptText("Select a Reviewer");
        loadReviewers(); // This loads either reviewers or students based on role.
        reviewerComboBox.setOnAction(e -> loadConversation());

        // Conversation area: using a ListView to display messages
        conversationListView = new ListView<>();
        conversationListView.setPrefHeight(300);
        
        // **Add custom cell factory for iMessage-style bubbles**
        conversationListView.setCellFactory(listView -> new ListCell<ChatMessage>() {
            @Override
            protected void updateItem(ChatMessage msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                // Create a VBox for the message bubble
                VBox bubble = new VBox(5);
                Label textLabel = new Label(msg.getContent());
                textLabel.setWrapText(true);
                Label timeLabel = new Label(msg.getTimestamp().format(DateTimeFormatter.ofPattern("MMM d h:mm a")));
                timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
                bubble.getChildren().addAll(textLabel, timeLabel);

                // Align bubbles based on sender
                if (msg.getSender().equals(currentUser.getUserName())) {
                    // Outgoing message: right aligned and styled differently
                    bubble.setAlignment(Pos.CENTER_RIGHT);
                    textLabel.setStyle("-fx-padding: 10; -fx-background-color: #dcf8c6; -fx-background-radius: 10; -fx-max-width: 300px;");
                } else {
                    // Incoming message: left aligned
                    bubble.setAlignment(Pos.CENTER_LEFT);
                    textLabel.setStyle("-fx-padding: 10; -fx-background-color: #ffffff; -fx-background-radius: 10; -fx-max-width: 300px; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 10;");
                }
                setGraphic(bubble);
                setText(null);
            }
        });

        // Input field and send button for new messages
        messageInput = new TextField();
        messageInput.setPromptText("Type your message here...");
        messageInput.setPrefWidth(250);

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        HBox inputArea = new HBox(10, messageInput, sendButton);
        inputArea.setAlignment(Pos.CENTER);

        // Assemble the layout
        layout.getChildren().addAll(titleLabel, reviewerComboBox, conversationListView, inputArea);

        // Create and show the scene
        Scene scene = new Scene(layout, 400, 450);
        stage.setScene(scene);
        stage.setTitle("Private Messaging");
        stage.show();
    }

    // Loads all reviewers from the database into the dropdown
    private void loadReviewers() {
        try {
            List<String> reviewers = databaseHelper.getReviewers();
            reviewerComboBox.getItems().clear();
            reviewerComboBox.getItems().addAll(reviewers);
        } catch (SQLException ex) {
            showAlert("Error loading reviewers.");
            ex.printStackTrace();
        }
    }




    // Loads the conversation between the student and the selected reviewer
    private void loadConversation() {
        String reviewer = reviewerComboBox.getValue();
        if (reviewer == null) {
            return;
        }
        try {
            // This method should return a list of formatted message strings between the two users.
        	List<ChatMessage> messages = databaseHelper.getMessages(currentUser.getUserName(), reviewer);
        	conversationListView.getItems().clear();
        	conversationListView.getItems().addAll(messages);
        } catch (SQLException ex) {
            showAlert("Error loading messages.");
            ex.printStackTrace();
        }
    }

    // Sends a new message from the student to the selected reviewer
    private void sendMessage() {
        String reviewer = reviewerComboBox.getValue();
        if (reviewer == null) {
            showAlert("Please select a reviewer.");
            return;
        }
        String messageText = messageInput.getText().trim();
        if (messageText.isEmpty()) {
            showAlert("Please enter a message.");
            return;
        }
        try {
            // Inserts the new message into the database.
        	ChatMessage newMsg = new ChatMessage(
        		    0, // id は自動生成なので 0
        		    -1, // reviewId: 関連しない場合は -1 や 0 を使う
        		    currentUser.getUserName(),
        		    reviewer,
        		    messageText,
        		    LocalDateTime.now()
        		);

    		databaseHelper.addMessage(newMsg);
            messageInput.clear();
            loadConversation(); // Reload conversation to display the new message
        } catch (SQLException ex) {
            showAlert("Error sending message.");
            ex.printStackTrace();
        }
    }

    // Utility method to show alerts
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
}
