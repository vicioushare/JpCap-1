package uk.ac.kcl.dcs.agentpref;

public class ConfidentialityComponentAgent extends ComponentAgent implements Runnable {

	@Override
	boolean receiveResponse(String response) {
		
		for (GoalPromotion gp : getEffectsOfResponse(response)) {
			
			if (gp.getGoal().toString().equals("Confidentiality") && gp.isPromotion() == false) {
				
				state = false;
				return true;
			}
			
		}
		
		return false;
	}
	
	public String toString() {
		
		return "Mail Server"; //As a prime requirement (highest in pref) for confidentiality is represented by our Mail Server.
		
	}
	
	public void run() {
		
		super.run();
		addGoal("Confidentiality");
		addGoal("Availability");
		addGoal("Integrity");
		
	}

}
