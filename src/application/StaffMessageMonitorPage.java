package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class StaffMessageMonitorPage {

    private final DatabaseHelper db;
    private final User currentUser; // staff
    private ComboBox<String> user1ComboBox;
    private ComboBox<String> user2ComboBox;
    private ListView<String> messageList;

    public StaffMessageMonitorPage(DatabaseHelper db, User currentUser) {
        this.db = db;
        this.currentUser = currentUser;
    }

    public void show(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("📬 Monitor Messages Between Users");

        // ユーザー選択コンボボックス
        user1ComboBox = new ComboBox<>();
        user2ComboBox = new ComboBox<>();
        loadUsers();

        Button loadMessagesBtn = new Button("Load Conversation");
        loadMessagesBtn.setOnAction(e -> loadMessages());

        HBox userSelection = new HBox(10, new Label("User A:"), user1ComboBox, new Label("User B:"), user2ComboBox, loadMessagesBtn);
        userSelection.setAlignment(Pos.CENTER);

        messageList = new ListView<>();
        messageList.setPrefHeight(300);

        layout.getChildren().addAll(title, userSelection, messageList);

        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Staff - Message Monitor");
        stage.show();
    }

    private void loadUsers() {
        try {
            List<String> users = db.getAllUsernames(); // すべてのユーザー名を取得
            user1ComboBox.getItems().addAll(users);
            user2ComboBox.getItems().addAll(users);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading users.");
        }
    }

    private void loadMessages() {
        String user1 = user1ComboBox.getValue();
        String user2 = user2ComboBox.getValue();

        if (user1 == null || user2 == null || user1.equals(user2)) {
            showAlert("Please select two different users.");
            return;
        }

        try {
            List<ChatMessage> messages = db.getMessages(user1, user2);
            messageList.getItems().clear();
            for (ChatMessage msg : messages) {
                messageList.getItems().add("[" + msg.getTimestamp() + "] " + msg.getSender() + ": " + msg.getContent());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading messages.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}