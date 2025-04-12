package application;

public class ReviewerWithScore {
	private String name;
	private int score;
	
	public ReviewerWithScore(String name, int score) {
		this.name = name;
		this.score = score;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
}
