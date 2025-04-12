package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class StaffInstructorMessagingPage {

    private final DatabaseHelper db;
    private final User currentUser;
    private ComboBox<String> instructorComboBox;
    private ListView<ChatMessage> messageListView;
    private TextField messageInput;

    public StaffInstructorMessagingPage(DatabaseHelper db, User currentUser) {
        this.db = db;
        this.currentUser = currentUser;
    }

    public void show(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("📨 Message with Instructor");

        instructorComboBox = new ComboBox<>();
        instructorComboBox.setPromptText("Select an Instructor");
        try {
            instructorComboBox.getItems().addAll(db.getInstructors()); // ← 必須メソッド
        } catch (SQLException e) {
            showAlert("Failed to load instructors.");
        }

        messageListView = new ListView<>();
        messageListView.setPrefHeight(300);
        messageListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ChatMessage msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                } else {
                    setText("[" + msg.getTimestamp() + "] " + msg.getSender() + ": " + msg.getContent());
                }
            }
        });

        instructorComboBox.setOnAction(e -> loadMessages());

        messageInput = new TextField();
        messageInput.setPromptText("Type a message...");

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        Button editButton = new Button("✏ Edit Selected");
        editButton.setOnAction(e -> editSelectedMessage());

        Button deleteButton = new Button("🗑 Delete Selected");
        deleteButton.setOnAction(e -> deleteSelectedMessage());

        HBox controls = new HBox(10, messageInput, sendButton, editButton, deleteButton);
        controls.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(title, instructorComboBox, messageListView, controls);

        Scene scene = new Scene(layout, 600, 450);
        stage.setScene(scene);
        stage.setTitle("Staff ↔ Instructor Messaging");
        stage.show();
    }

    private void loadMessages() {
        String instructor = instructorComboBox.getValue();
        try {
            List<ChatMessage> messages = db.getMessages(currentUser.getUserName(), instructor);
            messageListView.getItems().setAll(messages);
        } catch (SQLException e) {
            showAlert("Failed to load messages.");
        }
    }

    private void sendMessage() {
        String instructor = instructorComboBox.getValue();
        String content = messageInput.getText().trim();
        if (instructor == null || content.isEmpty()) {
            showAlert("Please select an instructor and enter a message.");
            return;
        }

        try {
            ChatMessage msg = new ChatMessage(
                0,
                -1,
                currentUser.getUserName(),
                instructor,
                content,
                LocalDateTime.now()
            );
            db.addMessage(msg);
            messageInput.clear();
            loadMessages();
        } catch (SQLException e) {
            showAlert("Failed to send message.");
        }
    }

    private void editSelectedMessage() {
        ChatMessage selected = messageListView.getSelectionModel().getSelectedItem();
        if (selected == null || !selected.getSender().equals(currentUser.getUserName())) {
            showAlert("Please select your own message to edit.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getContent());
        dialog.setTitle("Edit Message");
        dialog.setHeaderText("Edit your message:");
        dialog.setContentText("Message:");

        dialog.showAndWait().ifPresent(newText -> {
            try {
                db.editMessage(selected.getId(), newText);
                loadMessages();
            } catch (SQLException e) {
                showAlert("Failed to edit message.");
            }
        });
    }

    private void deleteSelectedMessage() {
        ChatMessage selected = messageListView.getSelectionModel().getSelectedItem();
        if (selected == null || !selected.getSender().equals(currentUser.getUserName())) {
            showAlert("Please select your own message to delete.");
            return;
        }

        try {
            db.deleteMessage(selected.getId());
            loadMessages();
        } catch (SQLException e) {
            showAlert("Failed to delete message.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}