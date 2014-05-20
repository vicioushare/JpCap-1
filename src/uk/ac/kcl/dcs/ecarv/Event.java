package uk.ac.kcl.dcs.ecarv;

public class Event {

	String name;
	String time;
	String source;
	public Event(String name, String time, String source) {
		this.name = name;
		this.time = time;
		this.source = source;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	public String toString() {
		return name;
		
	}
	
}
