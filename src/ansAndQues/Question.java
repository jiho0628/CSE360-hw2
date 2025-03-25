package ansAndQues;

import java.time.LocalDateTime;

public class Question {
    private int id;
    private String text;
    private String author;
    private LocalDateTime createdAt;

    public Question(int id, String text, String author, LocalDateTime createdAt) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.createdAt = createdAt;
    }

    public Question(String text, String author) {
        this.text = text;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public String getText() { return text; }
    public String getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}