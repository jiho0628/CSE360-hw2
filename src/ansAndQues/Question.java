package ansAndQues;

import java.time.LocalDateTime;

// 質問を表すクラス
public class Question {
    private int id;
    private String text;
    private String author;
    private LocalDateTime createdAt;

    // データベース用のコンストラクタ（idあり）
    public Question(int id, String text, String author, LocalDateTime createdAt) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.createdAt = createdAt;
    }

    // idなしのコンストラクタ（新規作成時に使う）
    public Question(String text, String author) {
        this.text = text;
        this.author = author;
        this.createdAt = LocalDateTime.now(); // 現在時刻を設定
    }

    // Getter メソッド
    public int getId() { return id; }
    public String getText() { return text; }
    public String getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}