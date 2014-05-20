package uk.ac.kcl.dcs.agentpref;

public class GoalPromotion {

	private String goal;
	
	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public Boolean isPromotion() {
		return promotion;
	}

	public void setPromotion(boolean promotion) {
		this.promotion = promotion;
	}

	private Boolean promotion = true;
	
	GoalPromotion(String result, Boolean promotion) {
		
		this.goal = result;
		this.promotion = promotion;
		
	}
	
	GoalPromotion(String result, Boolean promotion, int amountValued) {
		
		this.goal = result;
		this.promotion = promotion;
		this.amountValued = amountValued;
		
	}

	private double amountValued;
	
	public double getAmountValued() {
		return amountValued;
	}

	public void setAmountValued(double amountValued) {
		this.amountValued = amountValued;
	}

	public String toString() {
		
		return goal + " " + promotion + " " + amountValued;
		
	}

}