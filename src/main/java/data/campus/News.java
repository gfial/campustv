package data.campus;

import java.util.Date;
import java.util.Collection;

public class News {

	// Id of the news
	private int id;

	// The title, brief and contents of the news.
	private String title;
	private String brief;
	private String content;

	// The image path of the news.
	private String imgPath;

	// The tags of the news.
	private Collection<Tag> tags;

	// If the news author is anonymous.
	private boolean show;
	private int authorId;

	// The ratings of the news.
	private int likes;
	private int reports;
	private int likeWeight;
	private int reportWeight;
	
	private Date eventDate;
	private Date creationDate;

	public News() {
	}

	public News(int id, String title, String brief, String content,
			String imgPath, Collection<Tag> tags, boolean show, int authorId,
			int likes, int reports, int likeWeight, int reportWeight, Date creationDate, Date eventDate) {
		this.id = id;

		this.title = title;
		this.brief = brief;
		this.content = content;

		this.imgPath = imgPath;

		this.tags = tags;

		this.show = show;
		this.authorId = authorId;

		this.likes = likes;
		this.reports = reports;
		this.likeWeight = likeWeight;
		this.reportWeight = reportWeight;
		
		this.eventDate = eventDate;
		this.creationDate = creationDate;
	}

	public int getId() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	public String getBrief() {
		return this.brief;
	}

	public String getContent() {
		return this.content;
	}

	public String getImgPath() {
		return this.imgPath;
	}

	public Collection<Tag> getTags() {
		return this.tags;
	}

	public boolean getShow() {
		return this.show;
	}

	public int getAuthorId() {
		return this.authorId;
	}

	public int getLikes() {
		return this.likes;
	}

	public int getReports() {
		return this.reports;
	}

	public int getLikeWeight() {
		return this.likeWeight;
	}

	public int getReportWeight() {
		return this.reportWeight;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public void setTags(Collection<Tag> tags) {
		this.tags = tags;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public void setReports(int reports) {
		this.reports = reports;
	}

	public void setLikeWeight(int likeWeight) {
		this.likeWeight = likeWeight;
	}

	public void setReportWeight(int reportWeight) {
		this.reportWeight = reportWeight;
	}

	@Override
	public String toString() {
		return "News [id=" + id + ", title=" + title + ", brief="
				+ brief + ", content=" + content + ", imgPath=" + imgPath
				+ ", tags=" + tags + ", show=" + show + ", authorId="
				+ authorId + ", likes=" + likes + ", reports=" + reports
				+ ", likeWeight=" + likeWeight + ", reportWeight="
				+ reportWeight + ", eventDate=" + eventDate + ", creationDate="
				+ creationDate + "]";
	}
	
	
}
