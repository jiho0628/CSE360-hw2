package application;

import static org.junit.Assert.*;
import org.junit.Test;
import databasePart1.DatabaseHelper;
import ansAndQues.Answer;
import ansAndQues.Question;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;

public class TestHw4 {

    @Test
    public void testViewAllQuestionsAndAnswers() throws SQLException {
        DatabaseHelper db = new DatabaseHelper();
        db.connectToDatabase();

        List<Question> questions = db.getAllQuestions();
        assertNotNull("Questions list should not be null", questions);
        assertTrue("There should be at least one question", questions.size() > 0);

        int firstQId = questions.get(0).getId();
        List<Answer> answers = db.getAnswersByQuestionId(firstQId);
        assertNotNull("Answers list should not be null", answers);
    }

    @Test
    public void testViewPrivateMessagesBetweenUsers() throws SQLException {
        DatabaseHelper db = new DatabaseHelper();
        db.connectToDatabase();

        List<ChatMessage> messages = db.getMessages("user1", "user2");
        assertNotNull("Messages list should not be null", messages);
    }

    @Test
    public void testSendMessageToInstructor() throws SQLException {
        DatabaseHelper db = new DatabaseHelper();
        db.connectToDatabase();

        ChatMessage msg = new ChatMessage(0, -1, "staffUser", "instructorUser", "Test message", LocalDateTime.now());
        db.addMessage(msg);

        List<ChatMessage> messages = db.getMessages("staffUser", "instructorUser");
        boolean found = false;
        for (ChatMessage m : messages) {
            if (m.getContent().equals("Test message")) {
                found = true;
                break;
            }
        }
        assertTrue("Sent message should be found", found);
    }

    @Test
    public void testEditMessage() throws SQLException {
        DatabaseHelper db = new DatabaseHelper();
        db.connectToDatabase();

        ChatMessage msg = new ChatMessage(0, -1, "staffUser", "instructorUser", "Original", LocalDateTime.now());
        db.addMessage(msg);

        List<ChatMessage> messages = db.getMessages("staffUser", "instructorUser");
        ChatMessage last = messages.get(messages.size() - 1);
        db.editMessage(last.getId(), "Edited content");

        List<ChatMessage> afterEdit = db.getMessages("staffUser", "instructorUser");
        ChatMessage updated = null;
        for (ChatMessage m : afterEdit) {
            if (m.getId() == last.getId()) {
                updated = m;
                break;
            }
        }

        assertNotNull("Updated message should exist", updated);
        assertEquals("Edited content", updated.getContent());
    }

    @Test
    public void testDeleteMessage() throws SQLException {
        DatabaseHelper db = new DatabaseHelper();
        db.connectToDatabase();

        ChatMessage msg = new ChatMessage(0, -1, "staffUser", "instructorUser", "To be deleted", LocalDateTime.now());
        db.addMessage(msg);

        List<ChatMessage> messages = db.getMessages("staffUser", "instructorUser");
        ChatMessage last = messages.get(messages.size() - 1);
        db.deleteMessage(last.getId());

        List<ChatMessage> afterDelete = db.getMessages("staffUser", "instructorUser");
        boolean stillExists = false;
        for (ChatMessage m : afterDelete) {
            if (m.getId() == last.getId()) {
                stillExists = true;
                break;
            }
        }

        assertFalse("Message should have been deleted", stillExists);
    }
}