package uk.ac.kcl.dcs.ecarv;

public class Trigger {

	Event event;
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
	public Trigger(Event event, String condition) {
		this.event = event;
		this.condition = condition;
	}
	String condition;
	
	public String getSummary() {
		
		return event + condition;
		
	}

}
