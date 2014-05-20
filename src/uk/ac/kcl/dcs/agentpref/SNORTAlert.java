package uk.ac.kcl.dcs.agentpref;

public class SNORTAlert {

	private String timestamp;
	
	public SNORTAlert(String timestamp, String type) {
		this.timestamp = timestamp;
		this.type = type;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private String type;
	
	public String toString() {
		
		return getType();
		
	}
	
}
