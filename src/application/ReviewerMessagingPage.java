package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReviewerMessagingPage {

    private final DatabaseHelper databaseHelper;
    private final User currentUser; // the reviewer
    private ComboBox<String> studentComboBox;
    private ListView<ChatMessage> conversationListView;
    private TextField messageInput;

    public ReviewerMessagingPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(Stage stage) {
        // Main layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        // Page title
        Label titleLabel = new Label("Messaging with Students");

        // Dropdown to select a student
        studentComboBox = new ComboBox<>();
        studentComboBox.setPromptText("Select a Student");
        loadStudents();
        studentComboBox.setOnAction(e -> loadConversation());

        // Conversation ListView using ChatMessage objects
        conversationListView = new ListView<>();
        conversationListView.setPrefHeight(300);
        conversationListView.setCellFactory(listView -> new ListCell<ChatMessage>() {
            @Override
            protected void updateItem(ChatMessage msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                VBox bubble = new VBox(5);
                Label textLabel = new Label(msg.getContent());
                textLabel.setWrapText(true);
                Label timeLabel = new Label(msg.getTimestamp().format(DateTimeFormatter.ofPattern("MMM d h:mm a")));
                timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
                bubble.getChildren().addAll(textLabel, timeLabel);

                // If the current reviewer sent the message, align right; otherwise, align left.
                if (msg.getSender().equals(currentUser.getUserName())) {
                    bubble.setAlignment(Pos.CENTER_RIGHT);
                    textLabel.setStyle("-fx-padding: 10; -fx-background-color: #dcf8c6; -fx-background-radius: 10; -fx-max-width: 300px;");
                } else {
                    bubble.setAlignment(Pos.CENTER_LEFT);
                    textLabel.setStyle("-fx-padding: 10; -fx-background-color: #ffffff; -fx-background-radius: 10; -fx-max-width: 300px; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 10;");
                }
                setGraphic(bubble);
                setText(null);
            }
        });

        // Input field and send button
        messageInput = new TextField();
        messageInput.setPromptText("Type your message here...");
        messageInput.setPrefWidth(250);

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        HBox inputArea = new HBox(10, messageInput, sendButton);
        inputArea.setAlignment(Pos.CENTER);

        // Assemble the layout
        layout.getChildren().addAll(titleLabel, studentComboBox, conversationListView, inputArea);
        Scene scene = new Scene(layout, 400, 450);
        stage.setScene(scene);
        stage.setTitle("Reviewer Messaging");
        stage.show();
    }

    // Loads all student usernames into the dropdown
    private void loadStudents() {
        try {
            List<String> students = databaseHelper.getStudents();
            studentComboBox.getItems().clear();
            studentComboBox.getItems().addAll(students);
        } catch (SQLException ex) {
            showAlert("Error loading students.");
            ex.printStackTrace();
        }
    }

    // Loads the conversation between the reviewer and the selected student
    private void loadConversation() {
        String student = studentComboBox.getValue();
        if (student == null) {
            return;
        }
        try {
            List<ChatMessage> messages = databaseHelper.getMessages(currentUser.getUserName(), student);
            conversationListView.getItems().clear();
            conversationListView.getItems().addAll(messages);
        } catch (SQLException ex) {
            showAlert("Error loading messages.");
            ex.printStackTrace();
        }
    }

    // Sends a message from the reviewer to the selected student
    private void sendMessage() {
        String student = studentComboBox.getValue();
        if (student == null) {
            showAlert("Please select a student.");
            return;
        }
        String messageText = messageInput.getText().trim();
        if (messageText.isEmpty()) {
            showAlert("Please enter a message.");
            return;
        }
        try {
        	ChatMessage newMsg = new ChatMessage(
        		    0,
        		    -1, // reviewId: using -1 if not related
        		    currentUser.getUserName(),
        		    student,
        		    messageText,
        		    LocalDateTime.now()
        		);

        		databaseHelper.addMessage(newMsg); 
        		messageInput.clear();
            messageInput.clear();
            loadConversation();
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
