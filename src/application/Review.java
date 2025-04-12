package application;

import java.time.LocalDateTime;

public class Review {
    private int id;	
    private int reviewerId; //reviewr's user id
    private int targetId; //question or answer id
    private String targetType; // question or answer
    private String targetAuthor; // user name who created the question or answer
    private String content; // content
    private LocalDateTime createdAt;
    
    public Review(int id, int reviewerId, int targetId, String targetType, String targetAuthor, String content, LocalDateTime createdAt) {
        this.id = id;
        this.reviewerId = reviewerId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.targetAuthor = targetAuthor;
        this.content = content;
        this.createdAt = createdAt;
    }
    
    public int getId() { return id; }
    public int getReviewerId() { return reviewerId; }
    public int getTargetId() { return targetId; }
    public String getTargetType() { return targetType; }
    public String getTargetAuthor() { return targetAuthor; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}
