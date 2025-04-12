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

public class ReviewerReviewListPage {
    private final DatabaseHelper db;
    private final User currentUser;

    public ReviewerReviewListPage(DatabaseHelper db, User user) {
        this.db = db;
        this.currentUser = user;
    }

    public void show(Stage stage) {
    	try {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("📝 Your Reviews");
        List<Review> reviews = db.getReviewsByReviewer(currentUser.getUserName()); 
        for (Review review : reviews) {
            VBox reviewBox = new VBox(5);
            reviewBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: gray;");

            Label target = new Label("🔗 Target: " + review.getTargetType() + " #" + review.getTargetId());
            Label content = new Label("💬 " + review.getContent());

            Button messageBtn = new Button("💌 View Messages");
            messageBtn.setOnAction(e -> {
                ReviewerMessageThreadPage threadPage = new ReviewerMessageThreadPage(db, review, currentUser);
                threadPage.show(new Stage());
            });

            reviewBox.getChildren().addAll(target, content, messageBtn);
            layout.getChildren().add(reviewBox);
        }

        Scene scene = new Scene(layout, 500, 600);
        stage.setScene(scene);
        stage.setTitle("Your Reviews");
        stage.show();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
