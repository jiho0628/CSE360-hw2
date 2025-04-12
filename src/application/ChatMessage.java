package application;

import java.time.LocalDateTime;

public class ChatMessage {
    private int id;
    private int reviewId;
    private String sender;
    private String recipient;
    private String content;
    private LocalDateTime timestamp;

    public ChatMessage(int id, int reviewId, String sender, String recipient, String content, LocalDateTime timestamp) {
        this.id = id;
        this.reviewId = reviewId;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public int getReviewId() { return reviewId; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
