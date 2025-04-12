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

public class InstructorHomePage {
    
    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    private VBox questionContainer;
    private TextField answerInput;
    private TextField questionInput;
    private Questions questions;
    private Answers answers;
    TextField searchInput = new TextField();

//    public StudentHomePage(DatabaseHelper databaseHelper, User user) {
//        this.databaseHelper = databaseHelper;
//        this.currentUser = user;
//    }
    public InstructorHomePage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.currentUser = user;
        this.questions = new Questions(databaseHelper);
        this.answers = new Answers(databaseHelper);
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #F5F8FA;");

        Label titleLabel = new Label("🐦 Instructor Feed");
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
        
        Button reqButton = new Button("View Requests");
        Label reqLabel = new Label("Press button to view all students requesting to be reviewers");
        
        reqButton.setOnAction(e -> {
        	try {
				reqButtonHandler();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });
        
        VBox reqBox = new VBox(10);
        
        reqBox.getChildren().addAll(reqButton, reqLabel);
        layout.getChildren().addAll(titleLabel, reqBox, searchBox, loadQuestionsButton, questionBox, questionContainer, backButton);
        loadQuestions();

        Scene studentScene = new Scene(layout, 800, 600);
        primaryStage.setScene(studentScene);
        primaryStage.setTitle("🐦 Instructor Feed");
        primaryStage.show();
    }
    private TextArea loadStudentList(TextArea studentTextArea) throws SQLException {
    	studentTextArea.clear();
        List<String> reqList = databaseHelper.getStudentRequests();
        for (String student : reqList) {
        	studentTextArea.appendText(student + "\n");
        }
        return studentTextArea;
    }
    ///// FOR VIEWING STUDENT REQUEST
    private void viewStudentInfoHandler(String name) throws SQLException {
    	int id = databaseHelper.getStudentIdFromName(name);
    	String username = databaseHelper.getUserNameById(id);
    	List<String> list = databaseHelper.getUserQuestionsAndAnswers(username);
    	
    	Stage stage = new Stage();
    	VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        
        for (String prompt : list) {
        	textArea.appendText(prompt + "\n");
        }
        
        layout.getChildren().addAll(textArea);

        Scene scene = new Scene(layout, 500, 200);
        stage.setScene(scene);
        stage.setTitle("List of questions and answers for student");
        stage.show();
    }
    
    private void reqButtonHandler() throws SQLException {
    	Stage reqStage = new Stage();
        VBox reqLayout = new VBox(10);
        reqLayout.setAlignment(Pos.CENTER);
        reqLayout.setPadding(new Insets(20));

        Label promptLabel = new Label("List of Student Requests");
        
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        loadStudentList(textArea);
        
        TextField textField = new TextField("Enter student name");
        textField.setPrefWidth(200);
        
        VBox denyBox = new VBox(20);
        Button denyBtn = new Button("Deny Request");
        Label denyLabel = new Label("");
        denyBox.getChildren().addAll(denyBtn, denyLabel);
        denyBtn.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white; -fx-font-weight: bold;");
        denyBtn.setOnAction(e -> {
        	String currStudentName = textField.getText();
        	try {
				if (databaseHelper.removeFromList(currStudentName)) {
					denyLabel.setText("Successfully removed student from list");
					loadStudentList(textArea);
				}
				else {
					denyLabel.setText("OOPS! Student does not exist in list");
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				denyLabel.setText("ERROR: Encountered an error when trying to remove from list");
				e1.printStackTrace();
			}
        });
        VBox acceptBox = new VBox(20);
        Button acceptBtn = new Button("Accept Request");
        acceptBtn.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white; -fx-font-weight: bold;");
        Label acceptLabel = new Label("");
        acceptBox.getChildren().addAll(acceptBtn, acceptLabel);
        
        acceptBtn.setOnAction(e -> {
        	String currStudentName = textField.getText();
        	try {
				if (databaseHelper.addReviewerRoleToStudent(currStudentName)) {
					acceptLabel.setText("Successfully added reviewer role to student!");
					loadStudentList(textArea);
				}
				else {
					denyLabel.setText("OOPS! Student does not exist in list");
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				denyLabel.setText("ERROR: Encountered an error when trying to remove from list");
				e1.printStackTrace();
			}
        });
        
        Button viewStudentInfo = new Button("View Student Info");
        viewStudentInfo.setStyle("-fx-background-color: #1DA1F2; -fx-text-fill: white; -fx-font-weight: bold;");
        viewStudentInfo.setOnAction(e -> {
        	try {
				viewStudentInfoHandler(textField.getText());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });
        
        HBox btnBox = new HBox(20);
        btnBox.getChildren().addAll(acceptBox, denyBox, viewStudentInfo);

        reqLayout.getChildren().addAll(promptLabel, textArea, textField, btnBox);

        Scene reqScene = new Scene(reqLayout, 500, 200);
        reqStage.setScene(reqScene);
        reqStage.setTitle("Approve or deny student request");
        reqStage.show();
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
        
        HBox actionBox = new HBox(10, replyButton, editButton, deleteButton);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(authorLabel, textLabel, actionBox, answerContainer);
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
                    loadAnswers(questionId, answerContainer); 
                });
                deleteAnswerButton.setDisable(!currentUser.getUserName().equals(answer.getAuthor())); 

                answerBox.getChildren().addAll(answerText, answeredBy, editAnswerButton, deleteAnswerButton);
                answerContainer.getChildren().add(answerBox);
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