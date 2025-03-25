package ansAndQues;

import java.sql.SQLException;
import java.util.List;
import databasePart1.DatabaseHelper;

public class Answers {
    private DatabaseHelper databaseHelper;

    public Answers(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException {
        databaseHelper.connectToDatabase();
        List<Answer> answers = databaseHelper.getAnswersByQuestionId(questionId);
        databaseHelper.closeConnection();
        return answers;
    }

    public void addAnswer(int questionId, String text, String author) throws SQLException {
        databaseHelper.connectToDatabase();
        Answer newAnswer = new Answer(questionId, text, author);
        databaseHelper.addAnswer(newAnswer);
        databaseHelper.closeConnection();
    }

    public void deleteAnswer(int answerId) throws SQLException {
        databaseHelper.connectToDatabase();
        databaseHelper.deleteAnswer(answerId);
        databaseHelper.closeConnection();
    }
    
    public void editAnswer(int answerId, String newText) throws SQLException {
        databaseHelper.updateAnswer(answerId, newText);
    }
}