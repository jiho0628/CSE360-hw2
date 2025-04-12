package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ReviewerMessageThreadPage {
    private final DatabaseHelper db;
    private final Review review;
    private final User currentUser;

    public ReviewerMessageThreadPage(DatabaseHelper db, Review review, User user) {
        this.db = db;
        this.review = review;
        this.currentUser = user;
    }

    public void show(Stage stage) {
    	try {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label title = new Label("📨 Messages for Review #" + review.getId());
        List<ChatMessage> messages = db.getMessagesByReviewId(review.getId());

        VBox messageList = new VBox(5);
        for (ChatMessage msg : messages) {
            Label msgLabel = new Label("[" + msg.getSender() + "] " + msg.getContent());
            messageList.getChildren().add(msgLabel);
        }

        TextField input = new TextField();
        Button send = new Button("Send");
        send.setOnAction(e -> {
        	try {
            String recipient = review.getTargetAuthor().equals(currentUser.getUserName())
                ? db.getUserNameById(review.getReviewerId())
                : review.getTargetAuthor(); // reviewerかauthorか判別

            ChatMessage newMsg = new ChatMessage(
                0,
                review.getId(),
                currentUser.getUserName(),
                recipient,
                input.getText(),
                LocalDateTime.now()
            );

            db.addMessage(newMsg);
            stage.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        layout.getChildren().addAll(title, messageList, input, send);

        Scene scene = new Scene(layout, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Messages");
        stage.show();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    	}
    	

}
