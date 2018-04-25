package data.campus;

import java.util.Collection;

public class Channel {

	private int id;
	private String name;
	private int ownerId;
	private Collection<Filter> filter;
	private String filterType;
	private boolean trending;
	
	public Channel() {}
	
	public Channel(int id, String name, int ownerId, Collection<Filter> filter, String filterType, boolean trending) {
		this.id = id;
		this.name = name;
		this.ownerId = ownerId;
		this.filter = filter;
		this.filterType = filterType;
		this.trending = trending;
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
	
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	
	public int getOwnerId() {
		return this.ownerId;
	}
	
	public void setFilter(Collection<Filter> filter) {
		this.filter = filter;
	}
	
	public Collection<Filter> getFilter() {
		return this.filter;
	}
	
	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}
	
	public String getFilterType() {
		return this.filterType;
	}
	
	public boolean isTrending() {
		return trending;
	}

	public void setTrending(boolean trending) {
		this.trending = trending;
	}

	@Override
	public String toString() {
		return "Channel [id=" + id + ", name=" + name
				+ ", ownerId=" + ownerId + ", filter=" + filter
				+ ", filterType=" + filterType + ", trending=" + trending + "]";
	}
}
