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

import application.ChatMessage;
import application.Review;
import application.ReviewerWithScore;
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
     // Create the Messages table for the messaging feature
        String createMessagesTable = "CREATE TABLE IF NOT EXISTS Messages (\n"
        		+ "    id INT AUTO_INCREMENT PRIMARY KEY,\n"
        		+ "    reviewId INT,\n"
        		+ "    sender VARCHAR(255),\n"
        		+ "    recipient VARCHAR(255),\n"
        		+ "    message TEXT,\n"
        		+ "    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n"
        		+ ")";
        statement.execute(createMessagesTable);
        
        String createReviewsTable = "CREATE TABLE IF NOT EXISTS Reviews (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "reviewerId INT, " +
                "targetId INT, " +
                "targetType VARCHAR(50), " +
                "targetAuthor VARCHAR(50), " +
                "content TEXT, " +
                "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (reviewerId) REFERENCES cse360users(id))";
        statement.execute(createReviewsTable);
        // Create table for storing requests made by student to become reviewer
        String createStudentToReviewerRoleRequest = "CREATE TABLE IF NOT EXISTS ReviewerRoleRequest ("
        		+ "id INT AUTO_INCREMENT PRIMARY KEY,"
        		+ "studentID INT,"
        		+ "FOREIGN KEY (studentID) REFERENCES cse360users(id))";
        statement.execute(createStudentToReviewerRoleRequest);
        
        // Create table for storing reviewer rating by student
        String createReviewerWeight = "CREATE TABLE IF NOT EXISTS ReviewerWeight ("
        	    + "id INT AUTO_INCREMENT PRIMARY KEY, "
        	    + "reviewerID INT, "
        	    + "studentID INT, "
        	    + "score INT, "
        	    + "FOREIGN KEY (reviewerID) REFERENCES cse360users(id), "
        	    + "FOREIGN KEY (studentID) REFERENCES cse360users(id), "
        	    + "CHECK (score >= 0 AND score <= 100)"
        	    + ")";
        	statement.execute(createReviewerWeight);
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

		/////////////STUDENT REQUEST TO BE REVIEWER ////////////
	
		// Return list of all students requesting role of reviewer
		public List<String> getStudentRequests() throws SQLException {
			if (connection == null || connection.isClosed()) {
		        connectToDatabase();
		    }
			List<String> students = new ArrayList<>();
			String query = "SELECT cse360users.name FROM cse360users JOIN ReviewerRoleRequest ON cse360users.id = ReviewerRoleRequest.studentID";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						students.add(rs.getString("name"));
					}
				}
			}
			return students;
		}
		
		public int getStudentIdFromName(String currName) throws SQLException {
			String query = "SELECT id FROM cse360users WHERE name = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1,  currName);
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						return (rs.getInt("id"));
					}
				}
			}
			return -1;
		}
		
		// Remove student from list of requesting role of reviewer
		public boolean removeFromList(String currName) throws SQLException {
			if (connection == null || connection.isClosed()) {
		        connectToDatabase();
		    }
			
			int currID = getStudentIdFromName(currName);
			if (currID == -1) {
				return false;
			}
			
			String query = "DELETE FROM ReviewerRoleRequest WHERE studentID = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setInt(1,  currID);
				return pstmt.executeUpdate() > 0; // If more than 0 rows are updated then user is deleted so return true
 			}
		}
		
		// Add reviewer role to student
		public boolean addReviewerRoleToStudent(String currName) throws SQLException {
			if (connection == null || connection.isClosed()) {
			        connectToDatabase();
			    }
			int currId = getStudentIdFromName(currName);
			String currUserName = getUserNameById(currId);
			
			if (checkIfExists(currId)) {
				String currUserRole = getUserRole(currUserName);
				if (!currUserRole.contains("reviewer")) {
					String newUserRole = ", reviewer";
					newUserRole = currUserRole + newUserRole;
					
					String query = "Update cse360users SET role = ? WHERE id = ?";
					try (PreparedStatement stmt = connection.prepareStatement(query)) {
						stmt.setString(1, newUserRole);
						stmt.setInt(2, currId);
						removeFromList(currName);
						return stmt.executeUpdate() > 0;
					}
				}
			}
			
			return false;
		}
		
		// Only add to list if newID (for studentID) is not already in list to avoid duplicates
		public boolean addToRequestList(String userName) throws SQLException {
		    if (connection == null || connection.isClosed()) {
		        connectToDatabase();
		    }
		    String getIdQuery = "SELECT id FROM cse360users WHERE userName = ?";
		    int newID = -1;
		    try (PreparedStatement getIdStmt = connection.prepareStatement(getIdQuery)) {
		        getIdStmt.setString(1, userName);
		        ResultSet rs = getIdStmt.executeQuery();
		        if (rs.next()) {
		            newID = rs.getInt("id");
		            // Check if studentID already exists in ReviewerRoleRequest
		            if (checkIfExists(newID)) {
		            	return false; // return false because id already exists
		            }
		            // Insert new studentID
		            String insertQuery = "INSERT INTO ReviewerRoleRequest (studentID) VALUES (?)";
		            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
		                insertStmt.setInt(1, newID);
		                return insertStmt.executeUpdate() > 0;
		            }
		        }
		    }
		    return false; // return false because not added to list
		}

		// Checking to make sure that student cannot be added to reviewer role request list twice
		private boolean checkIfExists(int currentId) throws SQLException {
			if (connection == null || connection.isClosed()) {
		        connectToDatabase();
		    }
			String query = "SELECT COUNT(*) FROM ReviewerRoleRequest WHERE studentID = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setInt(1, currentId);
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						int count = rs.getInt(1);
						return count > 0;
					}
				}
			}
			return false;
		}
		
/////////////STUDENT VIEW REVIEWER SCORE ORDER ////////////
		// retrieve list of reviewers and their scores for specific student
		public List<ReviewerWithScore> getReviewerScoresListByStudent(int currID) throws SQLException {
			if (connection == null || connection.isClosed()) {
		        connectToDatabase();
		    }
			List<ReviewerWithScore> reviewers = new ArrayList<>();
			String query = "SELECT cse360users.name, ReviewerWeight.score "
							+ "FROM cse360users "
							+ "JOIN ReviewerWeight ON cse360users.id = ReviewerWeight.reviewerID "
							+ "WHERE ReviewerWeight.studentID = ? "
							+ "ORDER BY ReviewerWeight.score DESC";
			try (PreparedStatement stmt = connection.prepareStatement(query)) {
				stmt.setInt(1, currID);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						String currName;
						int currScore;
						
						currName = rs.getString("name");
						currScore = rs.getInt("score");
						ReviewerWithScore revWithScore = new ReviewerWithScore(currName, currScore);
						
						reviewers.add(revWithScore);
					}
				}
			}
			return reviewers;
		}
		
	/////////////PRIVATE MESSAGE////////////
	
		 // Retrieves messages exchanged between two users (both directions), ordered by timestamp.
		// Retrieves all reviewers (users with role "reviewer")
		public List<String> getReviewers() throws SQLException {
			if (connection == null || connection.isClosed()) {
		        connectToDatabase();
		    }
		    List<String> reviewers = new ArrayList<>();
		    String query = "SELECT userName FROM cse360users WHERE LOWER(role) LIKE ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, "%reviewer%");
		        try (ResultSet rs = pstmt.executeQuery()) {
		            while (rs.next()) {
		                reviewers.add(rs.getString("userName"));
		            }
		        }
		    }
		    return reviewers;
		}
		public List<String> getStudents() throws SQLException {
		    if (connection == null || connection.isClosed()) {
		        connectToDatabase();
		    }
		    List<String> students = new ArrayList<>();
		    String query = "SELECT userName FROM cse360users WHERE LOWER(role) LIKE ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, "%student%");
		        try (ResultSet rs = pstmt.executeQuery()) {
		            while (rs.next()) {
		                students.add(rs.getString("userName"));
		            }
		        }
		    }
		    return students;
		}


		// Retrieves messages exchanged between two users (both directions), ordered by timestamp.
		public List<ChatMessage> getMessages(String user1, String user2) throws SQLException {
		    List<ChatMessage> messages = new ArrayList<>();
		    String query = "SELECT * FROM Messages " +
		                   "WHERE (sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?) " +
		                   "ORDER BY timestamp ASC";

		    if (connection == null || connection.isClosed()) {
		        connectToDatabase();
		    }

		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, user1);
		        pstmt.setString(2, user2);
		        pstmt.setString(3, user2);
		        pstmt.setString(4, user1);
		        try (ResultSet rs = pstmt.executeQuery()) {
		            while (rs.next()) {
		                ChatMessage msg = new ChatMessage(
		                    rs.getInt("id"),
		                    rs.getInt("reviewId"),
		                    rs.getString("sender"),
		                    rs.getString("recipient"),
		                    rs.getString("message"),
		                    rs.getTimestamp("timestamp").toLocalDateTime()
		                );
		                messages.add(msg);
		            }
		        }
		    }

		    return messages;
		}
		
		public List<ChatMessage> getMessagesByReviewId(int reviewId) throws SQLException {
		    List<ChatMessage> messages = new ArrayList<>();
		    String query = "SELECT * FROM Messages WHERE reviewId = ? ORDER BY timestamp ASC";

		    connectToDatabase();

		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, reviewId);
		        ResultSet rs = pstmt.executeQuery();

		        while (rs.next()) {
		            ChatMessage msg = new ChatMessage(
		                rs.getInt("id"),
		                rs.getInt("reviewId"),
		                rs.getString("sender"),
		                rs.getString("recipient"),
		                rs.getString("message"),
		                rs.getTimestamp("timestamp").toLocalDateTime()
		            );
		            messages.add(msg);
		        }
		    }
		    return messages;
		}


		// Inserts a new message into the Messages table.
		public void addMessage(ChatMessage msg) throws SQLException {
		    String query = "INSERT INTO Messages (reviewId, sender, recipient, message, timestamp) VALUES (?, ?, ?, ?, ?)";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, msg.getReviewId());
		        pstmt.setString(2, msg.getSender());
		        pstmt.setString(3, msg.getRecipient());
		        pstmt.setString(4, msg.getContent());
		        pstmt.setTimestamp(5, Timestamp.valueOf(msg.getTimestamp()));
		        pstmt.executeUpdate();
		    }
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
	
	// add question
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
	
	// delete question
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
 // 
    public void updateQuestion(int questionId, String newText) throws SQLException {
        connectToDatabase(); 
        String updateQuery = "UPDATE Questions SET text = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, newText);
            pstmt.setInt(2, questionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to update question: " + e.getMessage());
            throw e;
        } finally {
            //closeConnection(); 
        }
    }

    public void updateAnswer(int answerId, String newText) throws SQLException {
        connectToDatabase(); //
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
                       "WHERE a.id IS NULL ORDER BY q.created_at DESC"; 

        connectToDatabase(); 

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
            closeConnection(); 
        }
        return questions;
    }
    
    /////// FOR INSTRUCTOR TO REVIEW WHICH STUDENTS TO GRANT REVIEWER ROLE REQUEST TO /////////
    public List<String> getUserQuestionsAndAnswers(String username) throws SQLException {
    	List<String> currList = new ArrayList<>();
    	String query = "SELECT Questions.text FROM Questions WHERE Questions.author = ?";
    	
    	try (PreparedStatement stmt = connection.prepareStatement(query)) {
    		stmt.setString(1, username);
    		
    		try (ResultSet rs = stmt.executeQuery()) {
    			while (rs.next()) {
    				String q = rs.getString("Questions.text");
    				currList.add("Student Question: " + q);
    			}
    		}
    	}
    	
    	query = "SELECT Answers.text FROM Answers WHERE Answers.author = ?";
    	try (PreparedStatement stmt = connection.prepareStatement(query)) {
    		stmt.setString(1, username);
    		
    		try (ResultSet rs = stmt.executeQuery()) {
    			while (rs.next()) {
    				String a = rs.getString("Answers.text");
    				currList.add("Student Answer: " + a);
    			}
    		}
    	}
    	return currList;
    }
    public List<Question> getUserQuestions(String username) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT * FROM Questions WHERE author = ? ORDER BY created_at DESC"; 

        connectToDatabase(); 

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
            closeConnection();
        }
        return questions;
    }
    
    
    //--------------------review list-------------------------
    public List<Review> getReviewsByReviewer(String username) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        
        connectToDatabase(); 

        // reviewerId 
        String getIdQuery = "SELECT id FROM cse360users WHERE userName = ?";
        int reviewerId = -1;
        try (PreparedStatement pstmt = connection.prepareStatement(getIdQuery)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                reviewerId = rs.getInt("id");
                System.out.println(reviewerId);
            } else {
                return reviews; // user not found
            }
        }

        String query = "SELECT * FROM Reviews WHERE reviewerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, reviewerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Review review = new Review(
                    rs.getInt("id"),
                    rs.getInt("reviewerId"),
                    rs.getInt("targetId"),
                    rs.getString("targetType"),
                    rs.getString("targetAuthor"),
                    rs.getString("content"),
                    rs.getTimestamp("createdAt").toLocalDateTime()
                );
                reviews.add(review);
            }
        }
        return reviews;
    }
    
    public String getUserNameById(int userId) throws SQLException {
        connectToDatabase();
        String query = "SELECT userName FROM cse360users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("userName");
            }
        }
        return null;
    }

    // Add a new review
    public void addReview(Review review) throws SQLException {
        String query = "INSERT INTO Reviews (reviewerId, targetId, targetType, targetAuthor, content, createdAt) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, review.getReviewerId());
            pstmt.setInt(2, review.getTargetId());
            pstmt.setString(3, review.getTargetType());
            pstmt.setString(4, review.getTargetAuthor());
            pstmt.setString(5, review.getContent());
            pstmt.setTimestamp(6, Timestamp.valueOf(review.getCreatedAt()));
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                review = new Review(
                    rs.getInt(1),
                    review.getReviewerId(),
                    review.getTargetId(),
                    review.getTargetType(),
                    review.getTargetAuthor(),
                    review.getContent(),
                    review.getCreatedAt()
                );
            }
        }
    }
    
    public void addReviewScore(Review review) throws SQLException {
        if (connection == null || connection.isClosed()) {
            connectToDatabase(); // Ensure DB connection is active
        }

        int reviewerId = review.getReviewerId();
        String targetAuthor = review.getTargetAuthor();

        int studentId = -1;
        String getStudentIdQuery = "SELECT id FROM cse360users WHERE userName = ?";
        try (PreparedStatement stmt = connection.prepareStatement(getStudentIdQuery)) {
            stmt.setString(1, targetAuthor);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    studentId = rs.getInt("id");
                } else {
                    throw new SQLException("Target author not found: " + targetAuthor);
                }
            }
        }

        String checkQuery = "SELECT * FROM ReviewerWeight WHERE reviewerID = ? AND studentID = ?";
        boolean exists = false;
        try (PreparedStatement stmt = connection.prepareStatement(checkQuery)) {
            stmt.setInt(1, reviewerId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                exists = rs.next();
            }
        }

        int score = 1; // Always set score to 1

        if (exists) {
            // Update existing record
            String updateQuery = "UPDATE ReviewerWeight SET score = ? WHERE reviewerID = ? AND studentID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                stmt.setInt(1, score);
                stmt.setInt(2, reviewerId);
                stmt.setInt(3, studentId);
                stmt.executeUpdate();
            }
        } else {
            // Insert new record
            String insertQuery = "INSERT INTO ReviewerWeight (reviewerID, studentID, score) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                stmt.setInt(1, reviewerId);
                stmt.setInt(2, studentId);
                stmt.setInt(3, score);
                stmt.executeUpdate();
            }
        }
    }
    
    public void updateReviewScore(int studentId, int reviewerId, int newScore) throws SQLException {
        if (connection == null || connection.isClosed()) {
            connectToDatabase(); // Ensure DB connection is active
        }

        String checkQuery = "SELECT * FROM ReviewerWeight WHERE reviewerID = ? AND studentID = ?";
        boolean exists = false;
        try (PreparedStatement stmt = connection.prepareStatement(checkQuery)) {
            stmt.setInt(1, reviewerId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                exists = rs.next();
            }
        }

        if (exists) {
            // Update existing record with new score
            String updateQuery = "UPDATE ReviewerWeight SET score = ? WHERE reviewerID = ? AND studentID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                stmt.setInt(1, newScore);
                stmt.setInt(2, reviewerId);
                stmt.setInt(3, studentId);
                stmt.executeUpdate();
            }
        } else {
            // Insert a new record with the score
            String insertQuery = "INSERT INTO ReviewerWeight (reviewerID, studentID, score) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                stmt.setInt(1, reviewerId);
                stmt.setInt(2, studentId);
                stmt.setInt(3, newScore);
                stmt.executeUpdate();
            }
        }
    }

    // Update an existing review
    public void updateReview(int reviewId, String newContent) throws SQLException {
        String query = "UPDATE Reviews SET content = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newContent);
            pstmt.setInt(2, reviewId);
            pstmt.executeUpdate();
        }
    }

    // Delete a review
    public void deleteReview(int reviewId) throws SQLException {
        String query = "DELETE FROM Reviews WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, reviewId);
            pstmt.executeUpdate();
        }
    }

    // Get reviews for a specific target (question or answer)
    public List<Review> getReviewsByTarget(int targetId, String targetType) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM Reviews WHERE targetId = ? AND targetType = ? ORDER BY createdAt DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, targetId);
            pstmt.setString(2, targetType);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Review review = new Review(
                    rs.getInt("id"),
                    rs.getInt("reviewerId"),
                    rs.getInt("targetId"),
                    rs.getString("targetType"),
                    rs.getString("targetAuthor"),
                    rs.getString("content"),
                    rs.getTimestamp("createdAt").toLocalDateTime()
                );
                reviews.add(review);
            }
        }
        return reviews;
    }
    
    // Order reviews by score for student type
    public List<Review> getReviewsOrderedByScore(int targetId, String targetType) throws SQLException {
    	if (connection == null || connection.isClosed()) {
	        connectToDatabase();
	    }
        List<Review> reviews = new ArrayList<>();

        String query = "SELECT r.*, w.score FROM Reviews r " +
                       "JOIN ReviewerWeight w ON w.reviewerID = r.reviewerId AND w.studentID = ( " +
                       "    SELECT id FROM cse360users WHERE userName = r.targetAuthor " +
                       ") " +
                       "WHERE r.targetId = ? AND r.targetType = ? " +
                       "ORDER BY w.score DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, targetId);
            pstmt.setString(2, targetType);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review(
                        rs.getInt("id"),
                        rs.getInt("reviewerId"),
                        rs.getInt("targetId"),
                        rs.getString("targetType"),
                        rs.getString("targetAuthor"),
                        rs.getString("content"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                    );
                    reviews.add(review);
                }
            }
        }

        return reviews;
    }

    
    // Get reviews for a specific author (student)
    public List<Review> getReviewsByAuthor(String author) throws SQLException {
    	if (connection == null || connection.isClosed()) {
	        connectToDatabase();
	    }
    	
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM Reviews WHERE targetAuthor = ? ORDER BY createdAt DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, author);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Review review = new Review(
                    rs.getInt("id"),
                    rs.getInt("reviewerId"),
                    rs.getInt("targetId"),
                    rs.getString("targetType"),
                    rs.getString("targetAuthor"),
                    rs.getString("content"),
                    rs.getTimestamp("createdAt").toLocalDateTime()
                );
                reviews.add(review);
            }
        }
        return reviews;
    }
    
    // Get ID from cse360users table from username
    public int getIdByUsername(String username) throws SQLException {
    	if (connection == null || connection.isClosed()) {
	        connectToDatabase();
	    }
    	
    	String query = "SELECT id FROM cse360users WHERE userName = ?";
    	
    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
    		pstmt.setString(1,  username);
    		ResultSet rs = pstmt.executeQuery();
    		
    		if (rs.next()) {
    			return rs.getInt("id");
    		}
    	}
    	return -1;
    }
    public String getRolesByUsername(String username) throws SQLException {
    	if (connection == null || connection.isClosed()) {
	        connectToDatabase();
	    }
    	
    	String query = "SELECT role FROM cse360users WHERE userName = ?";
    	
    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
    		pstmt.setString(1,  username);
    		ResultSet rs = pstmt.executeQuery();
    		
    		if (rs.next()) {
    			return rs.getString("role");
    		}
    	}
    	return "";
    }
}
