package uk.ac.kcl.dcs.agentpref;

public class IntegrityComponentAgent extends ComponentAgent implements Runnable {

	@Override
	boolean receiveResponse(String response) {
		
		for (GoalPromotion gp : getEffectsOfResponse(response)) {
			
			if (gp.getGoal().toString().equals("Integrity") && gp.isPromotion() == false) {
				
				state = false;
				return true;
			}
			
		}
		
		return false;
	}
	
	public String toString() {
		
		return "Database Server";
		
	}
	
	public void run() {
		
		super.run();
		addGoal("Integrity");
		addGoal("Confidentiality");
		addGoal("Availability");
		
	}

}
