package data.campus;


public class VotedContent {

	private boolean like;
	private boolean report;
	private News news;

	public VotedContent() {
	}
	
	public VotedContent(News news, boolean like, boolean report) {
		this.news = news;
		this.like = like;
		this.report = report;
	}

	public News getNews() {
		return this.news;
	}

	public boolean getLike() {
		return this.like;
	}

	public boolean getReport() {
		return this.report;
	}

	public void setNews(News news) {
		this.news = news;
	}
	
	public void setReport(boolean report) {
		this.report = report;
	}

	public void setLike(boolean like) {
		this.like = like;
	}
	
	@Override
	public String toString() {
		return "VotedContent [like=" + like + ", report=" + report + ", news="
				+ news.toString() + "]";
	}
}
