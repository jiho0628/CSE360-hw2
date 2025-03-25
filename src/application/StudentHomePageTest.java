package application;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ansAndQues.Answer;
import ansAndQues.Answers;
import ansAndQues.Question;
import ansAndQues.Questions;
import databasePart1.DatabaseHelper;
/**
 * The {@code StudentHomePageTest} class provides a suite of automated tests 
 * for verifying the functionality of the Student Q;A system;
 * <p>
 * It includes tests for:
 * <ul>
 *   <li>Creating and retrieving questions</li>
 *   <li>Submitting and retrieving answers</li>
 *   <li>Searching for questions by keyword</li>
 *   <li>Editing questions and verifying persistence</li>
 * </ul>
 *
 * @author Zhihao Liu
 * @version 1.0
 * @since 2025-03-24
 */
public class StudentHomePageTest {

    private static DatabaseHelper db;
    private static Questions questions;
    private static Answers answers;

    /**
     * Sets up the database connection and initializes the helper classes.
     * Runs once before all tests.
     * 
     * @throws SQLException if a database access error occurs
     */
    @BeforeClass
    public static void setupOnce() throws SQLException {
        db = new DatabaseHelper();
        db.connectToDatabase();
        questions = new Questions(db);
        answers = new Answers(db);
    }

    /**
     * Clears the questions and answers tables before each test.
     * 
     * @throws SQLException if a database access error occurs
     */
    @Before
    public void clearDatabase() throws SQLException {
        db.connectToDatabase();  // Ensure fresh connection
        db.execute("DELETE FROM answers");
        db.execute("DELETE FROM questions");
    }

    /**
     * Tests that a question is stored and retrieved correctly from the database.
     * 
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testQuestionCreationAndRetrieval() throws SQLException {
        String text = "What is polymorphism?";
        String author = "student1";

        questions.addQuestion(text, author);
        List<Question> result = questions.getAllQuestions();

        assertEquals(1, result.size());
        assertEquals(text, result.get(0).getText());
        assertEquals(author, result.get(0).getAuthor());
    }

    /**
     * Tests answer submission and retrieval for a question.
     * 
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testAnswerSubmissionAndRetrieval() throws SQLException {
        questions.addQuestion("What is JavaFX?", "user1");
        Question q = questions.getAllQuestions().get(0);

        answers.addAnswer(q.getId(), "A GUI toolkit.", "user2");
        List<Answer> answerList = answers.getAnswersByQuestionId(q.getId());

        assertEquals(1, answerList.size());
        assertEquals("A GUI toolkit.", answerList.get(0).getText());
        assertEquals("user2", answerList.get(0).getAuthor());
    }

    /**
     * Tests the question search functionality based on keywords.
     * 
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testSearchFunctionality() throws SQLException {
        questions.addQuestion("How does inheritance work?", "student1");
        questions.addQuestion("Explain interfaces in Java.", "student2");

        List<Question> result = questions.searchQuestions("inheritance");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getText().contains("inheritance"));
    }

    /**
     * Tests that a student's answer is stored correctly and linked to the proper question.
     * 
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testStudentResponseStoredCorrectly() throws SQLException {
    	questions.addQuestion("Define encapsulation.", "studentX");
        Question q = questions.getAllQuestions().get(0);

        answers.addAnswer(q.getId(), "Encapsulation hides internal details.", "studentY");
        List<Answer> responses = answers.getAnswersByQuestionId(q.getId());

        assertEquals(1, responses.size());
        assertEquals("studentY", responses.get(0).getAuthor());
    }

    /**
     * Tests whether a question can be edited and the changes persist in the database.
     * 
     * @throws SQLException if a database access error occurs
     */
    @Test
    public void testQuestionEditPersistence() throws SQLException {
        questions.addQuestion("Old text", "student");
        Question q = questions.getAllQuestions().get(0);

        questions.editQuestion(q.getId(), "New edited text");
        Question edited = questions.getAllQuestions().get(0);

        assertEquals("New edited text", edited.getText());
    }
}