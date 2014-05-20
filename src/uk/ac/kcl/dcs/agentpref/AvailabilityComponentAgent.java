package uk.ac.kcl.dcs.agentpref;

public class AvailabilityComponentAgent extends ComponentAgent implements Runnable {
	
	boolean receiveResponse(String response) {
		
		for (GoalPromotion gp : getEffectsOfResponse(response)) {
			
			if (gp.getGoal().toString().equals("Availability") && gp.isPromotion() == false) {
				
				state = false;
				return true;
			}
			
		}
		
		return false;
	}
	
	public String toString() {
		
		return "Web Server";
		
	}
	
	public void run() {
		
		super.run();
		addGoal("Availability");
		addGoal("Confidentiality");
		addGoal("Integrity");
		
	}

}
