package ansAndQues;

import java.time.LocalDateTime;

public class Answer {
    private int id;
    private int questionId; 
    private String text;
    private String author;
    private LocalDateTime createdAt;

    public Answer(int id, int questionId, String text, String author, LocalDateTime createdAt) {
        this.id = id;
        this.questionId = questionId;
        this.text = text;
        this.author = author;
        this.createdAt = createdAt;
    }

    public Answer(int questionId, String text, String author) {
        this.questionId = questionId;
        this.text = text;
        this.author = author;
        this.createdAt = LocalDateTime.now(); 
    }


    public int getId() { return id; }
    public int getQuestionId() { return questionId; }
    public String getText() { return text; }
    public String getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}