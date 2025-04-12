package application;

import java.sql.SQLException;
import java.util.List;
import databasePart1.DatabaseHelper;
import ansAndQues.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class ReviewerHomePage {
    
    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    private VBox questionContainer;
    private TextField answerInput;
    private TextField questionInput;
    private Questions questions;
    private Answers answers;
    private Reviews reviews;
    private Review currentReview;
    TextField searchInput = new TextField();

//    public StudentHomePage(DatabaseHelper databaseHelper, User user) {
//        this.databaseHelper = databaseHelper;
//        this.currentUser = user;
//    }
    public ReviewerHomePage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.currentUser = user;
        this.questions = new Questions(databaseHelper);
        this.answers = new Answers(databaseHelper);
        this.reviews = new Reviews(databaseHelper);
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #F5F8FA;");

        Label titleLabel = new Label("🐦 Reviewer Feed");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Button loadQuestionsButton = new Button("🔄 Refresh");
        loadQuestionsButton.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white; -fx-font-weight: bold;");
        loadQuestionsButton.setOnAction(e -> loadQuestions());
        
        TextField searchInput = new TextField();
        searchInput.setPromptText("🔍 Search...");
        searchInput.setPrefWidth(300);

        Button searchButton = new Button("🔍 Search");
        searchButton.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white; -fx-font-weight: bold;");
        searchButton.setOnAction(e -> {
            String keyword = searchInput.getText().trim();
            searchQuestions(keyword); 
        });
        
        ComboBox<String> filterOptions = new ComboBox<>();
        filterOptions.getItems().addAll("All Questions", "Unanswered Questions", "My Questions");
        filterOptions.setValue("All Questions"); 
        filterOptions.setOnAction(e -> filterQuestions(filterOptions.getValue()));

        questionContainer = new VBox(10);
        questionContainer.setAlignment(Pos.CENTER);
        questionContainer.setStyle("-fx-padding: 10;");

        questionInput = new TextField();
        questionInput.setPromptText("What's on your mind?");
        questionInput.setPrefWidth(400);
        questionInput.setStyle("-fx-padding: 10; -fx-background-radius: 20px;");

        Button addQuestionButton = new Button("Post 💬");
        addQuestionButton.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white; -fx-font-weight: bold;");
        addQuestionButton.setOnAction(e -> addQuestion());

        HBox questionBox = new HBox(10, questionInput, addQuestionButton);
        questionBox.setAlignment(Pos.CENTER);

        Button backButton = new Button("⬅️ Back");
        backButton.setOnAction(a -> new WelcomeLoginPage(databaseHelper).show(primaryStage, currentUser));
        
        HBox searchBox = new HBox(10, searchInput, filterOptions, searchButton);
        searchBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(titleLabel, searchBox, loadQuestionsButton, questionBox, questionContainer, backButton);
        loadQuestions();
        
        Button messageStudentButton = new Button("Message Student");
        messageStudentButton.setOnAction(e -> {
            ReviewerMessagingPage messagingPage = new ReviewerMessagingPage(databaseHelper, currentUser);
            Stage messagingStage = new Stage();
            messagingPage.show(messagingStage);
        });
        layout.getChildren().add(messageStudentButton);
        
        //-------List-------------
        Button viewReviewsButton = new Button("📋 My Reviews");
        viewReviewsButton.setOnAction(e -> {
        	System.out.print("home page");
        	System.out.println(currentUser);
            ReviewerReviewListPage reviewPage = new ReviewerReviewListPage(databaseHelper, currentUser);
            Stage reviewStage = new Stage();
            reviewPage.show(reviewStage);
        });
        layout.getChildren().add(viewReviewsButton);



        Scene studentScene = new Scene(layout, 800, 600);
        primaryStage.setScene(studentScene);
        primaryStage.setTitle("🐦 Reviewer Feed");
        primaryStage.show();
    }
    private void loadQuestions() {
        questionContainer.getChildren().clear();
        try {
            List<Question> questionList = questions.getAllQuestions();
            for (Question question : questionList) {
                VBox questionCard = createQuestionCard(question);
                questionContainer.getChildren().add(questionCard);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load questions.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
 
    private VBox createQuestionCard(Question question) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 10px; "
                + "-fx-border-color: lightgray; -fx-border-radius: 10px;");
        card.setPadding(new Insets(10));
        card.setPrefWidth(600);

        Label authorLabel = new Label("👤 " + question.getAuthor());
        authorLabel.setStyle("-fx-font-weight: bold;");

        Label textLabel = new Label("💬 " + question.getText());

        VBox answerContainer = new VBox(5);
        answerContainer.setStyle("-fx-padding: 5; -fx-background-color: #E1E8ED; -fx-background-radius: 10px;");
        loadAnswers(question.getId(), answerContainer);

        Button replyButton = new Button("💬 Reply");
        replyButton.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white; -fx-font-weight: bold;");
        replyButton.setOnAction(e -> showReplyPopup(question, answerContainer));
        
        Button editButton = new Button("✏️ Edit");
        editButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-weight: bold;");
        editButton.setOnAction(e -> showEditQuestionPopup(question));

        Button deleteButton = new Button("🗑 Delete");
        deleteButton.setStyle("-fx-background-color: #E0245E; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setOnAction(e -> deleteQuestion(question));
        deleteButton.setDisable(!currentUser.getUserName().equals(question.getAuthor()));
        
        Button reviewButton = new Button("📝 Review");
        reviewButton.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white; -fx-font-weight: bold;");
        reviewButton.setOnAction(e -> showReviewPopup(question, "question"));

        HBox actionBox = new HBox(10, replyButton, editButton, deleteButton, reviewButton);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        // Add reviews section
        VBox reviewsContainer = new VBox(5);
        reviewsContainer.setStyle("-fx-padding: 5; -fx-background-color: #F5F8FA; -fx-background-radius: 10px;");
        loadReviews(question.getId(), "question", reviewsContainer);

        card.getChildren().addAll(authorLabel, textLabel, actionBox, answerContainer, reviewsContainer);
        return card;
    }

    private void loadAnswers(int questionId, VBox answerContainer) {
        answerContainer.getChildren().clear();
        try {
            List<Answer> answerList = answers.getAnswersByQuestionId(questionId);
            for (Answer answer : answerList) {
                HBox answerBox = new HBox(10);
                answerBox.setAlignment(Pos.CENTER_LEFT);
                answerBox.setStyle("-fx-padding: 5px;");

                Label answerText = new Label("💬 " + answer.getText());
                Label answeredBy = new Label("👤 " + answer.getAuthor());
                answeredBy.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

                Button editAnswerButton = new Button("✏️");
                editAnswerButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-weight: bold;");
                editAnswerButton.setOnAction(e -> showEditAnswerPopup(answer, answerContainer));
                editAnswerButton.setDisable(!currentUser.getUserName().equals(answer.getAuthor())); // 自分の回答のみ編集可能
                
                Button deleteAnswerButton = new Button("🗑");
                deleteAnswerButton.setStyle("-fx-background-color: #E0245E; -fx-text-fill: white; -fx-font-weight: bold;");
                deleteAnswerButton.setOnAction(e -> {
                    deleteAnswer(answer);
                    loadAnswers(questionId, answerContainer); // 削除後に再ロード
                });
                deleteAnswerButton.setDisable(!currentUser.getUserName().equals(answer.getAuthor())); // 自分の回答のみ削除可能

                Button reviewAnswerButton = new Button("📝 Review");
                reviewAnswerButton.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white; -fx-font-weight: bold;");
                reviewAnswerButton.setOnAction(e -> showReviewPopup(answer, "answer"));

                answerBox.getChildren().addAll(answerText, answeredBy, editAnswerButton, deleteAnswerButton, reviewAnswerButton);
                answerContainer.getChildren().add(answerBox);

                // Add reviews section for each answer
                VBox answerReviewsContainer = new VBox(5);
                answerReviewsContainer.setStyle("-fx-padding: 5; -fx-background-color: #F5F8FA; -fx-background-radius: 10px;");
                loadReviews(answer.getId(), "answer", answerReviewsContainer);
                answerBox.getChildren().add(answerReviewsContainer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showReplyPopup(Question question, VBox answerContainer) {
        Stage replyStage = new Stage();
        VBox replyLayout = new VBox(10);
        replyLayout.setAlignment(Pos.CENTER);
        replyLayout.setPadding(new Insets(20));

        Label promptLabel = new Label("Reply to: " + question.getText());

        answerInput = new TextField();
        answerInput.setPromptText("Enter your reply...");
        answerInput.setPrefWidth(400);

        Button sendReplyButton = new Button("💬 Reply");
        sendReplyButton.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white; -fx-font-weight: bold;");
        sendReplyButton.setOnAction(e -> replyToQuestion(question, answerContainer, replyStage));

        replyLayout.getChildren().addAll(promptLabel, answerInput, sendReplyButton);

        Scene replyScene = new Scene(replyLayout, 500, 200);
        replyStage.setScene(replyScene);
        replyStage.setTitle("Reply to Question");
        replyStage.show();
        
    }
    

    private void addQuestion() {
        String questionText = questionInput.getText().trim();

        if (questionText.isEmpty()) {
            showAlert("Input Error", "Please enter a question.", Alert.AlertType.WARNING);
            return;
        }

        try {
            questions.addQuestion(questionText, currentUser.getUserName());
            showAlert("Success", "Question added successfully!", Alert.AlertType.INFORMATION);
            questionInput.clear();
            loadQuestions();
        } catch (SQLException e) {
            showAlert("Error", "Failed to add question.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void deleteQuestion(Question question) {
        try {
            questions.deleteQuestion(question.getId());
            showAlert("Success", "Question deleted successfully!", Alert.AlertType.INFORMATION);
            loadQuestions();
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete question.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void deleteAnswer(Answer answer) {
        try {
            answers.deleteAnswer(answer.getId());
            showAlert("Success", "Answer deleted successfully!", Alert.AlertType.INFORMATION);
            loadQuestions();
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete answer.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    

    private void showEditQuestionPopup(Question question) {
        Stage editStage = new Stage();
        VBox editLayout = new VBox(10);
        editLayout.setAlignment(Pos.CENTER);
        editLayout.setPadding(new Insets(20));

        Label promptLabel = new Label("Edit Question:");
        TextField editInput = new TextField(question.getText());
        editInput.setPrefWidth(400);

        Button saveButton = new Button("💾 Save");
        saveButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setOnAction(e -> {
            try {
                questions.editQuestion(question.getId(), editInput.getText());
                showAlert("Success", "Question updated successfully!", Alert.AlertType.INFORMATION);
                loadQuestions();
                editStage.close();
            } catch (SQLException ex) {
                showAlert("Error", "Failed to update question.", Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        });

        editLayout.getChildren().addAll(promptLabel, editInput, saveButton);

        Scene editScene = new Scene(editLayout, 500, 200);
        editStage.setScene(editScene);
        editStage.setTitle("Edit Question");
        editStage.show();
    }

 
    private void replyToQuestion(Question question, VBox answerContainer, Stage replyStage) {
        String answerText = answerInput.getText().trim();
        if (answerText.isEmpty()) {
            showAlert("Input Error", "Please enter a reply.", Alert.AlertType.WARNING);
            return;
        }

        try {
            answers.addAnswer(question.getId(), answerText, currentUser.getUserName());
            showAlert("Success", "Reply added successfully!", Alert.AlertType.INFORMATION);
            replyStage.close();
            loadAnswers(question.getId(), answerContainer);
        } catch (SQLException e) {
            showAlert("Error", "Failed to add reply.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void showEditAnswerPopup(Answer answer, VBox answerContainer) {
        Stage editStage = new Stage();
        VBox editLayout = new VBox(10);
        editLayout.setAlignment(Pos.CENTER);
        editLayout.setPadding(new Insets(20));

        Label promptLabel = new Label("Edit Answer:");
        TextField editInput = new TextField(answer.getText());
        editInput.setPrefWidth(400);

        Button saveButton = new Button("💾 Save");
        saveButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setOnAction(e -> {
            try {
                answers.editAnswer(answer.getId(), editInput.getText());
                showAlert("Success", "Answer updated successfully!", Alert.AlertType.INFORMATION);
                loadAnswers(answer.getQuestionId(), answerContainer);
                editStage.close();
            } catch (SQLException ex) {
                showAlert("Error", "Failed to update answer: " + ex.getMessage(), Alert.AlertType.ERROR);
                ex.printStackTrace();
            }
        });

        editLayout.getChildren().addAll(promptLabel, editInput, saveButton);

        Scene editScene = new Scene(editLayout, 500, 200);
        editStage.setScene(editScene);
        editStage.setTitle("Edit Answer");
        editStage.show();
    }
    
    private void searchQuestions(String keyword) {
        questionContainer.getChildren().clear();
        try {
            List<Question> filteredQuestions = questions.searchQuestions(keyword);
            for (Question question : filteredQuestions) {
                VBox questionCard = createQuestionCard(question);
                questionContainer.getChildren().add(questionCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void filterQuestions(String filterType) {
        questionContainer.getChildren().clear();
        try {
            List<Question> filteredQuestions = switch (filterType) {
                case "Unanswered Questions" -> questions.getUnansweredQuestions();
                case "My Questions" -> questions.getUserQuestions(currentUser.getUserName());
                default -> questions.getAllQuestions(); // "All Questions"
            };

            for (Question question : filteredQuestions) {
                VBox questionCard = createQuestionCard(question);
                questionContainer.getChildren().add(questionCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadReviews(int targetId, String targetType, VBox container) {
        container.getChildren().clear();
        try {
            List<Review> reviewList = reviews.getReviewsByTarget(targetId, targetType);
            for (Review review : reviewList) {
                VBox reviewBox = new VBox(5);
                reviewBox.setStyle("-fx-padding: 5; -fx-background-color: white; -fx-background-radius: 5px;");

                Label reviewerLabel = new Label("👤 Reviewer: " + review.getReviewerId());
                Label contentLabel = new Label("💬 " + review.getContent());
                contentLabel.setWrapText(true);

                Button editReviewButton = new Button("✏️ Edit");
                editReviewButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white; -fx-font-weight: bold;");
                editReviewButton.setOnAction(e -> showEditReviewPopup(review));
                editReviewButton.setDisable(!currentUser.getUserName().equals(databaseHelper.getUserNameById(review.getReviewerId())));

                Button deleteReviewButton = new Button("🗑 Delete");
                deleteReviewButton.setStyle("-fx-background-color: #E0245E; -fx-text-fill: white; -fx-font-weight: bold;");
                deleteReviewButton.setOnAction(e -> deleteReview(review));
                deleteReviewButton.setDisable(!currentUser.getUserName().equals(databaseHelper.getUserNameById(review.getReviewerId())));

                HBox reviewActions = new HBox(10, editReviewButton, deleteReviewButton);
                reviewActions.setAlignment(Pos.CENTER_RIGHT);

                reviewBox.getChildren().addAll(reviewerLabel, contentLabel, reviewActions);
                container.getChildren().add(reviewBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showReviewPopup(Object target, String targetType) {
        Stage reviewStage = new Stage();
        VBox reviewLayout = new VBox(10);
        reviewLayout.setAlignment(Pos.CENTER);
        reviewLayout.setPadding(new Insets(20));

        Label promptLabel = new Label("Write your review:");
        TextArea reviewInput = new TextArea();
        reviewInput.setPrefWidth(400);
        reviewInput.setPrefHeight(200);

        Button submitButton = new Button("Submit Review");
        submitButton.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white; -fx-font-weight: bold;");
        submitButton.setOnAction(e -> {
            String content = reviewInput.getText().trim();
            if (!content.isEmpty()) {
                try {
                    int targetId = target instanceof Question ? ((Question) target).getId() : ((Answer) target).getId();
                    String targetAuthor = target instanceof Question ? ((Question) target).getAuthor() : ((Answer) target).getAuthor();
                    
                    Review newReview = new Review(
                        0, // id will be set by database
                        databaseHelper.getIdByUsername(currentUser.getUserName()), // CREATED NEW DB METHOD B/C getID is not method for User
                        targetId,
                        targetType,
                        targetAuthor,
                        content,
                        LocalDateTime.now()
                    );
                    
                    reviews.addReview(newReview);
                    reviewStage.close();
                    loadQuestions(); // Refresh to show new review
                } catch (SQLException ex) {
                    showAlert("Error", "Failed to submit review.", Alert.AlertType.ERROR);
                    ex.printStackTrace();
                }
            } else {
                showAlert("Input Error", "Please enter a review.", Alert.AlertType.WARNING);
            }
        });

        reviewLayout.getChildren().addAll(promptLabel, reviewInput, submitButton);

        Scene reviewScene = new Scene(reviewLayout, 500, 300);
        reviewStage.setScene(reviewScene);
        reviewStage.setTitle("Write Review");
        reviewStage.show();
    }

    private void showEditReviewPopup(Review review) {
        Stage editStage = new Stage();
        VBox editLayout = new VBox(10);
        editLayout.setAlignment(Pos.CENTER);
        editLayout.setPadding(new Insets(20));

        Label promptLabel = new Label("Edit your review:");
        TextArea editInput = new TextArea(review.getContent());
        editInput.setPrefWidth(400);
        editInput.setPrefHeight(200);

        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setOnAction(e -> {
            String newContent = editInput.getText().trim();
            if (!newContent.isEmpty()) {
                try {
                    reviews.updateReview(review.getId(), newContent);
                    editStage.close();
                    loadQuestions(); // Refresh to show updated review
                } catch (SQLException ex) {
                    showAlert("Error", "Failed to update review.", Alert.AlertType.ERROR);
                    ex.printStackTrace();
                }
            } else {
                showAlert("Input Error", "Review cannot be empty.", Alert.AlertType.WARNING);
            }
        });

        editLayout.getChildren().addAll(promptLabel, editInput, saveButton);

        Scene editScene = new Scene(editLayout, 500, 300);
        editStage.setScene(editScene);
        editStage.setTitle("Edit Review");
        editStage.show();
    }

    private void deleteReview(Review review) {
        try {
            reviews.deleteReview(review.getId());
            showAlert("Success", "Review deleted successfully!", Alert.AlertType.INFORMATION);
            loadQuestions(); // Refresh to remove deleted review
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete review.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}