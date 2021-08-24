package bth.core.model;

/**
 * Used to represent an SQL query
 * @author Matt
 *
 */
public class Query {
	
	private String title;
	private String value;
	
	public Query(String title, String value) {
		super();
		this.title = title;
		this.value = value;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
