package ansAndQues;

import java.time.LocalDateTime;

// 回答を表すクラス
public class Answer {
    private int id;
    private int questionId; // どの質問に対する回答か
    private String text;
    private String author;
    private LocalDateTime createdAt;

    // データベース用のコンストラクタ（id あり）
    public Answer(int id, int questionId, String text, String author, LocalDateTime createdAt) {
        this.id = id;
        this.questionId = questionId;
        this.text = text;
        this.author = author;
        this.createdAt = createdAt;
    }

    // id なしのコンストラクタ（新規作成時に使う）
    public Answer(int questionId, String text, String author) {
        this.questionId = questionId;
        this.text = text;
        this.author = author;
        this.createdAt = LocalDateTime.now(); // 現在時刻を設定
    }

    // Getter メソッド
    public int getId() { return id; }
    public int getQuestionId() { return questionId; }
    public String getText() { return text; }
    public String getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}