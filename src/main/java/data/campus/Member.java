package data.campus;

import java.util.Collection;

public class Member {

	private int id;
	private String name;
	private String email;
	private int reputation;
	private String imgPath;
	private String gender;
	
	private Channel smartTv;
	private Collection<Channel> channels;
	
	public Member(int id, String name, String email, int reputation,
			String imgPath, String gender, Channel smartTv, Collection<Channel> channels) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.reputation = reputation;
		this.imgPath = imgPath;
		this.gender = gender;
		this.smartTv = smartTv;
		this.channels = channels;
	}
	
	public Member(){}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getReputation() {
		return reputation;
	}

	public void setReputation(int reputation) {
		this.reputation = reputation;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Channel getSmartTv() {
		return smartTv;
	}

	public void setSmartTv(Channel smartTv) {
		this.smartTv = smartTv;
	}

	public Collection<Channel> getChannels() {
		return channels;
	}

	public void setChannels(Collection<Channel> channels) {
		this.channels = channels;
	}

	@Override
	public String toString() {
		return "Member [id=" + id + ", name=" + name + ", email=" + email
				+ ", reputation=" + reputation + ", imgPath=" + imgPath
				+ ", gender=" + gender + ", smartTv=" + smartTv + ", channels="
				+ channels + "]";
	}
}
