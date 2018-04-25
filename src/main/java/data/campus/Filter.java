package data.campus;

public class Filter {

	private Tag tag;
	private int weight;
	
	public Filter() {}
	
	public Filter(Tag tag, int weight) {
		this.tag = tag;
		this.weight = weight;
	}
	
	public Tag getTag() {
		return this.tag;
	}
	
	public int getWeight() {
		return this.weight;
	}
	
	public void setTag(Tag tag) {
		this.tag = tag;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	@Override
	public String toString() {
		return "Filter [tag=" + tag + ", weight=" + weight + "]";
	}
}
