package data.campus;

public class SimpleResponse {

	private String response;
	
	public SimpleResponse() {}
	
	public SimpleResponse(String response) {
		this.response = response;
	}
	
	public void setResponse(String response) {
		this.response = response;
	}
	
	public String getResponse() {
		return this.response;
	}
	
	@Override
	public String toString() {
		return "SimpleResponse [response=" + response + "]";
	}
}
