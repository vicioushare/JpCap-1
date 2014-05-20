package uk.ac.kcl.dcs.ecarv;

import java.util.Calendar;

public class Context {

	private String time = "";
	
	public Context(String time, String souceLocation, String contentOnComponent) {
		
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	private String contentOnComponent = "";
	
	public String getContentOnComponent() {
		return contentOnComponent;
	}

	public void setContentOnComponent(String contentOnComponent) {
		this.contentOnComponent = contentOnComponent;
	}

	private String sourceLocation = "";
	
	public String getSourceLocation() {
		return sourceLocation;
	}
	
	public void setSourceLocation(String sourceLocation) {
		this.sourceLocation = sourceLocation;
	}
	
	public String getSummary() {
		
		return time + contentOnComponent + sourceLocation;
		
	}

}
