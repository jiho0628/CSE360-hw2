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

public class StudentHomePage {
    
    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    private VBox questionContainer;
    private TextField answerInput;
    private TextField questionInput;
    private Questions questions;
    private Answers answers;
    private Reviews reviews;
    TextField searchInput = new TextField();

//    public StudentHomePage(DatabaseHelper databaseHelper, User user) {
//        this.databaseHelper = databaseHelper;
//        this.currentUser = user;
//    }
    public StudentHomePage(DatabaseHelper databaseHelper, User user) {
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

        Label titleLabel = new Label("🐦 Student Feed");
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
        
        Button messageReviewerButton = new Button("Message Reviewer");
        messageReviewerButton.setOnAction(e -> {
            MessagingPage messagingPage = new MessagingPage(databaseHelper, currentUser);
            Stage messagingStage = new Stage();
            messagingPage.show(messagingStage);
        });
        layout.getChildren().add(messageReviewerButton); // Add the button to the layout
        
        ////// Button for setting reviewer score
        Button setReviewerScore = new Button("Set a Reviewer's Score");
        setReviewerScore.setOnAction(e -> reviewerScoreHandler());
       
        ////// New button for STUDENTS playing role of REVIEWER request
        Button reviewerRoleRequestButton = new Button("Request Role of Reviewer");
        Label succRequestLabel = new Label("Press button to request Reviewer role");
        reviewerRoleRequestButton.setOnAction(e -> {
        	try {
				boolean isSucc = databaseHelper.addToRequestList(currentUser.getUserName());
				if (isSucc) {
					succRequestLabel.setText("You have been successfully added to list!");
				}
				else {
					succRequestLabel.setText("OOPS! Looks like you already are in the list!");
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch bloc
				succRequestLabel.setText("ERROR: An error occurred while trying to add user to list");
				e1.printStackTrace();
			}
        });
        VBox reviewerRequestBox = new VBox(10);
        reviewerRequestBox.getChildren().addAll( reviewerRoleRequestButton, succRequestLabel);

        
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

        layout.getChildren().addAll(titleLabel, reviewerRequestBox, searchBox, loadQuestionsButton, questionBox, questionContainer, setReviewerScore, backButton);
        loadQuestions();

        Scene studentScene = new Scene(layout, 800, 600);
        primaryStage.setScene(studentScene);
        primaryStage.setTitle("🐦 Student Feed");
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

    private void reviewerScoreHandler() {
        // Create a new stage (window)
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Create TextFields for reviewer name and score
        TextField reviewerNameField = new TextField();
        reviewerNameField.setPromptText("Enter reviewer's username");

        TextField scoreField = new TextField();
        scoreField.setPromptText("Enter score (0-100)");

        // Create a Button to submit the score
        Button submitButton = new Button("Submit Score");
        
        // Set up button action when clicked
        submitButton.setOnAction(e -> {
            String reviewerName = reviewerNameField.getText();
            String scoreText = scoreField.getText();

            // Ensure score is an integer and within range (0-100)
            try {
                int score = Integer.parseInt(scoreText);
                if (score < 0 || score > 100) {
                    showAlert("Invalid score", "Score must be between 0 and 100.");
                    return;
                }
                try {
					updateReviewerScore(reviewerName, score);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

                showAlert("Success", "Score updated successfully!");
                reviewerNameField.clear();
                scoreField.clear();
            } catch (NumberFormatException e1) {
                showAlert("Invalid input", "Please enter a valid integer for the score.");
            }
        });

        // Add the text fields and button to the layout
        layout.getChildren().addAll(reviewerNameField, scoreField, submitButton);

        // Set the scene and show the window
        Scene scene = new Scene(layout, 500, 200);
        stage.setScene(scene);
        stage.setTitle("Update a Reviewer Score");
        stage.show();
    }
    
    private void updateReviewerScore(String reviewerUserName, int score) throws SQLException {
    	String username = currentUser.getUserName();
    	
    	int studentID = databaseHelper.getIdByUsername(username);
    	int reviewerID = databaseHelper.getIdByUsername(reviewerUserName);
    	
    	databaseHelper.updateReviewScore(studentID, reviewerID, score);
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        
        HBox actionBox = new HBox(10, replyButton, editButton, deleteButton);
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
                editAnswerButton.setDisable(!currentUser.getUserName().equals(answer.getAuthor()));                 
                Button deleteAnswerButton = new Button("🗑");
                deleteAnswerButton.setStyle("-fx-background-color: #E0245E; -fx-text-fill: white; -fx-font-weight: bold;");
                deleteAnswerButton.setOnAction(e -> {
                    deleteAnswer(answer);
                    loadAnswers(questionId, answerContainer); 
                });
                deleteAnswerButton.setDisable(!currentUser.getUserName().equals(answer.getAuthor())); 

                // Add reviews section for each answer
                VBox answerReviewsContainer = new VBox(5);
                answerReviewsContainer.setStyle("-fx-padding: 5; -fx-background-color: #F5F8FA; -fx-background-radius: 10px;");
                loadReviews(answer.getId(), "answer", answerReviewsContainer);

                answerBox.getChildren().addAll(answerText, answeredBy, editAnswerButton, deleteAnswerButton, answerReviewsContainer);
                answerContainer.getChildren().add(answerBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadReviews(int targetId, String targetType, VBox container) {
        container.getChildren().clear();
        try {
        	List<Review> reviewList;
        	if (!currentUser.getRoles().contains("student")) {
        		 reviewList = reviews.getReviewsByTarget(targetId, targetType);
        	 }
        	else {
        		reviewList = databaseHelper.getReviewsOrderedByScore(targetId, targetType); // TODO Changed, check for bugs
        	}
            for (Review review : reviewList) {
                VBox reviewBox = new VBox(5);
                reviewBox.setStyle("-fx-padding: 5; -fx-background-color: white; -fx-background-radius: 5px;");
                
                String reviewerUsername = databaseHelper.getUserNameById(review.getReviewerId());
                Label reviewerLabel = new Label("👤 Reviewer: " + reviewerUsername);
                Label contentLabel = new Label("💬 " + review.getContent());
                contentLabel.setWrapText(true);

                reviewBox.getChildren().addAll(reviewerLabel, contentLabel);
                container.getChildren().add(reviewBox);
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
}