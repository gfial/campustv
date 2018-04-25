package data.campus;

import java.util.Collection;

public class Tag {

	private int id;
	private String name;
	private String brief;
	private String imgPath;
	private boolean authenticated;
	private Collection<Integer> parents;
	
	public Tag() {}
	
	public Tag(int id, String name, String brief, String imgPath, boolean authenticated, Collection<Integer> parents) {
		this.id = id;
		this.name = name;
		this.brief = brief;
		this.imgPath = imgPath;
		this.authenticated = authenticated;
		this.parents = parents;
	}
	
	public Collection<Integer> getParents() {
		return parents;
	}

	public void setParents(Collection<Integer> parents) {
		this.parents = parents;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setBrief(String brief) {
		this.brief = brief;
	}
	
	public String getBrief() {
		return this.brief;
	}
	
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	
	public String getImgPath() {
		return this.imgPath;
	}
	
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
	
	public boolean getAuthenticated() {
		return this.authenticated;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Tag)) return false;
		return ((Tag) other).getId() == this.getId();
	}

	@Override
	public String toString() {
		return "Tag [id=" + id + ", name=" + name + ", brief=" + brief
				+ ", imgPath=" + imgPath + ", authenticated=" + authenticated
				+ ", parents=" + parents + "]";
	}
}
