package application;

import java.sql.SQLException;
import java.util.List;
import databasePart1.DatabaseHelper;

public class Reviews {
    private DatabaseHelper databaseHelper;

    public Reviews(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void addReview(Review review) throws SQLException {
        databaseHelper.connectToDatabase();
        databaseHelper.addReview(review);
         // if targetAuthor is a student, then add review score
         String roles = databaseHelper.getRolesByUsername(review.getTargetAuthor());
        if (roles.contains("student")) {
            databaseHelper.addReviewScore(review);
        }
        databaseHelper.closeConnection();
    }

    public void updateReview(int reviewId, String newContent) throws SQLException {
        databaseHelper.connectToDatabase();
        databaseHelper.updateReview(reviewId, newContent);
        databaseHelper.closeConnection();
    }

    public void deleteReview(int reviewId) throws SQLException {
        databaseHelper.connectToDatabase();
        databaseHelper.deleteReview(reviewId);
        databaseHelper.closeConnection();
    }

    public List<Review> getReviewsByTarget(int targetId, String targetType) throws SQLException {
        databaseHelper.connectToDatabase();
        List<Review> reviews = databaseHelper.getReviewsByTarget(targetId, targetType);
        databaseHelper.closeConnection();
        return reviews;
    }

    public List<Review> getReviewsByAuthor(String author) throws SQLException {
        databaseHelper.connectToDatabase();
        List<Review> reviews = databaseHelper.getReviewsByAuthor(author);
        databaseHelper.closeConnection();
        return reviews;
    }

    public List<Review> getReviewsByReviewer(String reviewer) throws SQLException {
        databaseHelper.connectToDatabase();
        List<Review> reviews = databaseHelper.getReviewsByReviewer(reviewer);
        databaseHelper.closeConnection();
        return reviews;
    }
} 