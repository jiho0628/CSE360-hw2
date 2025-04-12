package ansAndQues;

import java.sql.SQLException;
import java.util.List;
import databasePart1.DatabaseHelper;

public class Questions {
    private DatabaseHelper databaseHelper;

    public Questions(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }


    public List<Question> getAllQuestions() throws SQLException {
        databaseHelper.connectToDatabase();
        List<Question> questions = databaseHelper.getAllQuestions();
        databaseHelper.closeConnection();
        return questions;
    }

    public void addQuestion(String text, String author) throws SQLException {
        databaseHelper.connectToDatabase();
        Question newQuestion = new Question(text, author);
        databaseHelper.addQuestion(newQuestion);
        databaseHelper.closeConnection();
    }

    public void deleteQuestion(int questionId) throws SQLException {
        databaseHelper.connectToDatabase();
        databaseHelper.deleteQuestion(questionId);
        databaseHelper.closeConnection();
    }
    public void editQuestion(int questionId, String newText) throws SQLException {
        databaseHelper.updateQuestion(questionId, newText);
    }
    
    public List<Question> searchQuestions(String keyword) throws SQLException {
        return databaseHelper.searchQuestions(keyword);
    }
    

    public List<Question> getUnansweredQuestions() throws SQLException {
        return databaseHelper.getUnansweredQuestions();
    }

 
    public List<Question> getUserQuestions(String username) throws SQLException {
        return databaseHelper.getUserQuestions(username);
    }
}