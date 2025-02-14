package ansAndQues;

import java.sql.SQLException;
import java.util.List;
import databasePart1.DatabaseHelper;

public class Questions {
    private DatabaseHelper databaseHelper;

    public Questions(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * データベースからすべての質問を取得する
     */
    public List<Question> getAllQuestions() throws SQLException {
        databaseHelper.connectToDatabase();
        List<Question> questions = databaseHelper.getAllQuestions();
        databaseHelper.closeConnection();
        return questions;
    }

    /**
     * 新しい質問を追加する
     */
    public void addQuestion(String text, String author) throws SQLException {
        databaseHelper.connectToDatabase();
        Question newQuestion = new Question(text, author);
        databaseHelper.addQuestion(newQuestion);
        databaseHelper.closeConnection();
    }

    /**
     * 質問を削除する
     */
    public void deleteQuestion(int questionId) throws SQLException {
        databaseHelper.connectToDatabase();
        databaseHelper.deleteQuestion(questionId);
        databaseHelper.closeConnection();
    }
    // 質問を編集
    public void editQuestion(int questionId, String newText) throws SQLException {
        databaseHelper.updateQuestion(questionId, newText);
    }
    
    public List<Question> searchQuestions(String keyword) throws SQLException {
        return databaseHelper.searchQuestions(keyword);
    }
    
    /**
     * **📝 まだ回答がない質問を取得**
     */
    public List<Question> getUnansweredQuestions() throws SQLException {
        return databaseHelper.getUnansweredQuestions();
    }

    /**
     * **👤 ユーザーの質問を取得**
     */
    public List<Question> getUserQuestions(String username) throws SQLException {
        return databaseHelper.getUserQuestions(username);
    }
}