package uk.ac.kcl.dcs.ecaplus;

// 9/2/13 Not used.

public class SimplePolicy {

	private String situation;
	private String target;
	private String action;
	
	static String BLOCK = "block";
	
	SimplePolicy(String event, String target, String action) { situation(situation); target(target); action(action); }
	
	void situation(String event) { this.situation = event; }
	
	String situation() { return situation; }
	
	void target(String target) { this.target = target; }
	
	String target() { return target; }
	
	void action(String action) { this.action = action; }
	
	String action() { return action; }
	
}
