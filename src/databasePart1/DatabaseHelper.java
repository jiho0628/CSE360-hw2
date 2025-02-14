package databasePart1;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.security.SecureRandom;
import java.util.Random;
import application.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import ansAndQues.Answer;
import ansAndQues.Question;
/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "email VARCHAR(255),"
				+ "name VARCHAR(255),"
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(50))";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY,"
	            + "isUsed BOOLEAN DEFAULT FALSE,"
	            + "givenRole VARCHAR(50))";
	    statement.execute(invitationCodesTable);
	    
        String createQuestionsTable = "CREATE TABLE IF NOT EXISTS Questions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "text VARCHAR(500), " +
                "author VARCHAR(255), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        statement.execute(createQuestionsTable);

        String createAnswersTable = "CREATE TABLE IF NOT EXISTS Answers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "question_id INT, " +
                "text VARCHAR(500), " +
                "author VARCHAR(255), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (question_id) REFERENCES Questions(id))";
        statement.execute(createAnswersTable);
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (email, name, userName, password, role) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getEmail());
			pstmt.setString(2, user.getName());
			pstmt.setString(3, user.getUserName());
			pstmt.setString(4, user.getPassword());
			pstmt.setString(5, user.getRoles());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRoles());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	public boolean doesEmailExist(String email) throws SQLException {
	    connectToDatabase(); // Ensure the database is connected
	    String query = "SELECT COUNT(*) FROM cse360users WHERE email = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt(1) > 0; // Returns true if email exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume email does not exist
	}

	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Generates a new invitation code and inserts it into the database.
	// Insert user roles in the same tuple
	public String generateInvitationCode(String givenRoles) {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code, isUsed, givenRole) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setBoolean(2,  false);
	        pstmt.setString(3, givenRoles);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	public String generateOneTimePassword(String userName) {
	    String passcode = UUID.randomUUID().toString().substring(0, 8); // Generate a random 8-character code
	    String query = "UPDATE cse360users SET password = ? WHERE userName = ?"; // Update the password, not insert

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, passcode);
	        pstmt.setString(2, userName);
	        
	        int rowsUpdated = pstmt.executeUpdate();
	        if (rowsUpdated > 0) {
	            System.out.println("DEBUG: Password updated successfully for user: " + userName);
	            return passcode;  // Return the generated passcode
	        } else {
	            System.out.println("DEBUG: User not found with username: " + userName);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user was found or an error occurred
	}

	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Retrieve givenRoles for specific invitation code
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRoleFromInvitationCodes(String code) {
	    String query = "SELECT givenRole FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("givenRole"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
////////////////OTP METHODS ////////////////

/**
* Generates a new 6-digit OTP.
*/
		public static final Random random = new SecureRandom();
		
		public String generateOTP() {
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
		}
		
		/**
		* Stores an OTP for password reset along with an expiration time (10 minutes).
		*/
		public boolean storeOTP(String email, String otp) {
	        String query = "UPDATE cse360users SET otp = ?, otp_expiration = ? WHERE email = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	            pstmt.setString(1, otp);
	            pstmt.setString(2, LocalDateTime.now().plusMinutes(10)
	                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	            pstmt.setString(3, email);
	            int rowsUpdated = pstmt.executeUpdate();
	            if (rowsUpdated == 0) {
	                System.out.println("DEBUG: No user found with email " + email);
	                return false;
	            }
	            System.out.println("DEBUG: OTP for " + email + " updated successfully.");
	            return true;
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return false;
	    }



		/**
		* Validates the OTP for a given email.
		*/
		public boolean validateOTP(String email, String otp) {
	        String query = "SELECT otp, otp_expiration FROM cse360users WHERE email = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	            pstmt.setString(1, email);
	            ResultSet rs = pstmt.executeQuery();
	            if (rs.next()) {
	                String storedOTP = rs.getString("otp");
	                LocalDateTime expirationTime = LocalDateTime.parse(
	                        rs.getString("otp_expiration"),
	                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
	                );
	                return storedOTP.equals(otp) && LocalDateTime.now().isBefore(expirationTime);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return false;
	    }
		
		/**
		* Resets the password using a valid OTP.
		*/
		public boolean resetPassword(String email, String newPassword, String otp) {
		if (validateOTP(email, otp)) {
		String query = "UPDATE cse360users SET password = ?, otp = NULL, otp_expiration = NULL WHERE email = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		   pstmt.setString(1, newPassword);
		   pstmt.setString(2, email);
		   return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
		   e.printStackTrace();
		}
		}
		return false;
		}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	
	public ObservableList<User> getAllUsers() throws SQLException {
	    String query = "SELECT * FROM cse360users";
	    ObservableList<User> users = FXCollections.observableArrayList();

	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            String email = rs.getString("email");
	            String name = rs.getString("name");
	            String userName = rs.getString("userName");
	            String password = rs.getString("password");
	            String role = rs.getString("role");

	            // Correct parameter order
	            User user = new User(userName, password, role, name, email);
	            users.add(user);
	        }
	    }
	    return users;
	}

	
	public void deleteUser(String userName) {
	    String query = "DELETE FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("User '" + userName + "' deleted successfully.");
	        } else {
	            System.out.println("User not found.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/*
    public List<User> getAllUsers() {
        // Retrieve all users from the database
        // Return a list of User objects
        return null; // Replace with actual implementation
    }
    
    public boolean deleteUser(String userName) {
        // Delete the user from the database based on userId
        // Return true if deletion was successful
        return true; // Replace with actual implementation
    }
    
    public boolean assignOTPToUser(int userId, String otp) {
        // Store the generated OTP for the given user
        // Return true if successful
        return true; // Replace with actual implementation
    }
    
    public boolean updateUserRoles(int userId, String newRoles) {
        // Update the roles of the specified user in the database
        // Return true if successful
        return true; // Replace with actual implementation
    }
	*/
	// 質問を追加
	public void addQuestion(Question question) throws SQLException {
	    String insertQuery = "INSERT INTO Questions (text, author, created_at) VALUES (?, ?, NOW())";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, question.getText());
	        pstmt.setString(2, question.getAuthor());
	        pstmt.executeUpdate();

	        // 自動生成された ID を取得
	        ResultSet rs = pstmt.getGeneratedKeys();
	        if (rs.next()) {
	            int generatedId = rs.getInt(1);
	            LocalDateTime createdAt = LocalDateTime.now(); // `NOW()` をセット
	            question = new Question(generatedId, question.getText(), question.getAuthor(), createdAt);
	        }
	    }
	}
	
	// 質問を削除するメソッド
	public void deleteQuestion(int questionId) throws SQLException {
	    String deleteAnswersQuery = "DELETE FROM Answers WHERE question_id = ?"; // 先に回答を削除
	    String deleteQuestionQuery = "DELETE FROM Questions WHERE id = ?"; // その後、質問を削除

	    try (PreparedStatement pstmtAnswers = connection.prepareStatement(deleteAnswersQuery);
	         PreparedStatement pstmtQuestion = connection.prepareStatement(deleteQuestionQuery)) {

	        // 1. 先に `Answers` テーブルのデータを削除
	        pstmtAnswers.setInt(1, questionId);
	        pstmtAnswers.executeUpdate();

	        // 2. その後に `Questions` テーブルのデータを削除
	        pstmtQuestion.setInt(1, questionId);
	        int affectedRows = pstmtQuestion.executeUpdate();

	        if (affectedRows == 0) {
	            System.out.println("❌ Question ID " + questionId + " deletion failed.");
	        } else {
	            System.out.println("✅ Question ID " + questionId + " successfully deleted.");
	        }
	    }
	}

	// 質問を削除するメソッドを追加
	public void deleteAnswer(int answerId) throws SQLException {
	    String deleteQuery = "DELETE FROM Answers WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
	        pstmt.setInt(1, answerId);
	        int affectedRows = pstmt.executeUpdate();
	        if (affectedRows == 0) {
	            System.out.println("❌ Answer ID " + answerId + " is failed to be deleted");
	        } else {
	            System.out.println("✅ Answer ID " + answerId + " is successfully deleted");
	        }
	    }
	}

	// 回答を追加
	public void addAnswer(Answer answer) throws SQLException {
	    String insertQuery = "INSERT INTO Answers (question_id, text, author, created_at) VALUES (?, ?, ?, NOW())";

	    try (PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setInt(1, answer.getQuestionId());
	        pstmt.setString(2, answer.getText());
	        pstmt.setString(3, answer.getAuthor());
	        pstmt.executeUpdate();

	        // 自動生成された ID を取得
	        ResultSet rs = pstmt.getGeneratedKeys();
	        if (rs.next()) {
	            int generatedId = rs.getInt(1);
	            LocalDateTime createdAt = LocalDateTime.now(); // `NOW()` の値をセット
	            answer = new Answer(generatedId, answer.getQuestionId(), answer.getText(), answer.getAuthor(), createdAt);
	        }
	    }
	}

    // すべての質問を取得
    public List<Question> getAllQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT * FROM Questions";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // 🔹 `created_at` を `LocalDateTime` に変換
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

                // 修正: `Question(int, String, String, LocalDateTime)` を使う
                Question question = new Question(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getString("author"),
                        createdAt
                );
                questions.add(question);
            }
        }
        return questions;
    }

    // 質問IDに対するすべての回答を取得
    public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException {
        List<Answer> answers = new ArrayList<>();
        String query = "SELECT * FROM Answers WHERE question_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // 🔹 `created_at` を `LocalDateTime` に変換
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

                Answer answer = new Answer(
                        rs.getInt("id"),
                        rs.getInt("question_id"),
                        rs.getString("text"),
                        rs.getString("author"),
                        createdAt // 🔹 ここで `created_at` を追加
                );
                answers.add(answer);
            }
        }
        return answers;
    }
 // 質問を編集するメソッド
    public void updateQuestion(int questionId, String newText) throws SQLException {
        connectToDatabase(); // 🔹 データベース接続を確保
        String updateQuery = "UPDATE Questions SET text = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, newText);
            pstmt.setInt(2, questionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to update question: " + e.getMessage());
            throw e;
        } finally {
            closeConnection(); // 🔹 最後に接続を閉じる
        }
    }

    public void updateAnswer(int answerId, String newText) throws SQLException {
        connectToDatabase(); // データベース接続
        String updateQuery = "UPDATE Answers SET text = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, newText);
            pstmt.setInt(2, answerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to update answer: " + e.getMessage());
            throw e;
        } finally {
            closeConnection(); // 最後に接続を閉じる
        }
    }
    
    public List<Question> searchQuestions(String keyword) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT * FROM Questions WHERE text LIKE ? ORDER BY created_at DESC";

        connectToDatabase(); // 接続を確保

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + keyword + "%"); // 部分一致検索
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    Question question = new Question(
                            rs.getInt("id"),
                            rs.getString("text"),
                            rs.getString("author"),
                            createdAt
                    );
                    questions.add(question);
                }
            }
        } finally {
            closeConnection(); // 確実に接続を閉じる
        }
        return questions;
    }
    
    public List<Question> getUnansweredQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT q.* FROM Questions q " +
                       "LEFT JOIN Answers a ON q.id = a.question_id " +
                       "WHERE a.id IS NULL ORDER BY q.created_at DESC"; // 回答がない質問のみ取得

        connectToDatabase(); // 接続を確保

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                Question question = new Question(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getString("author"),
                        createdAt
                );
                questions.add(question);
            }
        } finally {
            closeConnection(); // 確実に接続を閉じる
        }
        return questions;
    }
    
    public List<Question> getUserQuestions(String username) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT * FROM Questions WHERE author = ? ORDER BY created_at DESC"; // 自分の質問のみ取得

        connectToDatabase(); // 接続を確保

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    Question question = new Question(
                            rs.getInt("id"),
                            rs.getString("text"),
                            rs.getString("author"),
                            createdAt
                    );
                    questions.add(question);
                }
            }
        } finally {
            closeConnection(); // 確実に接続を閉じる
        }
        return questions;
    }
}
