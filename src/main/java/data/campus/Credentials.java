package data.campus;

public class Credentials {
	private String email;
	private String username;
	private String password;
	private String imgPath;

	public Credentials() {
	}

	public Credentials(String email, String username, String password,
			String imgPath) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.imgPath = imgPath;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	@Override
	public String toString() {
		return "Credentials [email=" + email + ", username=" + username
				+ ", password=" + password + ", imgPath=" + imgPath + "]";
	}

}
