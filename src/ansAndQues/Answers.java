package ansAndQues;

import java.sql.SQLException;
import java.util.List;
import databasePart1.DatabaseHelper;

public class Answers {
    private DatabaseHelper databaseHelper;

    public Answers(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * 質問IDに対するすべての回答を取得する
     */
    public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException {
        databaseHelper.connectToDatabase();
        List<Answer> answers = databaseHelper.getAnswersByQuestionId(questionId);
        databaseHelper.closeConnection();
        return answers;
    }

    /**
     * 回答を追加する
     */
    public void addAnswer(int questionId, String text, String author) throws SQLException {
        databaseHelper.connectToDatabase();
        Answer newAnswer = new Answer(questionId, text, author);
        databaseHelper.addAnswer(newAnswer);
        databaseHelper.closeConnection();
    }

    /**
     * 回答を削除する
     */
    public void deleteAnswer(int answerId) throws SQLException {
        databaseHelper.connectToDatabase();
        databaseHelper.deleteAnswer(answerId);
        databaseHelper.closeConnection();
    }
    
    public void editAnswer(int answerId, String newText) throws SQLException {
        databaseHelper.updateAnswer(answerId, newText);
    }
}