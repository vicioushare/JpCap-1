package uk.ac.kcl.dcs.ecarv;

import java.util.ArrayList;

public class ECA {

	private Event event;
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	private String condition;
	private String action;
	/**/
	
	public ECA(Event event, String condition, String action) {
		
		this.event = event;
		this.condition = condition;
		this.action = action;
		
	}
	
	public String getSummary() {
		
		return event + condition + action;
		
	}
	
	public Trigger getTrigger() {
		
		return new Trigger(event, condition);
		
	}
	
	
}