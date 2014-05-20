package uk.ac.kcl.dcs.agentpref;

import java.util.ArrayList;
import java.util.Hashtable;

import uk.ac.kcl.dcs.ecaplus.Situation;

public abstract class ComponentAgent implements Runnable {
	
	// An indicator of whether the component is able to carry out its primary function
	protected boolean state = true;
	
	
	public String getState() {
		
		if (state == true) { 
			return "True";
		} else {	
			return "False";
		}
		
	}
	
	/************************************************************************************/
	
	public Hashtable<String, Double> act(String response) {
		
		state = true;
		
		// Map each Goal to vote
		Hashtable<String, Double> votes = new Hashtable<String, Double>();
		
		// Loop through all the goal / promotion pairs from Ontology
		for (GoalPromotion gp : getEffectsOfResponse(response)) {
			
			// If the goal is in this component's preference ordering (they are concerned about it):
			if (preferenceOrdering.contains(gp.getGoal())) {
				
				// Give a score to that goal based on its position in the preference ordering.
				// The score is based on 1 / N with N being the position in the preference
				// In this way, preferences lower in the ordering have weighting but it is reduced.
				votes.put(gp.getGoal(), (1.0 / (preferenceOrdering.indexOf(gp.getGoal()) + 1)));
				
			}
			
		}
	
		return votes;
		
	}

	/************************************************************************************/
	// Depreciated use of Ontology 23/5/13
	protected ArrayList<GoalPromotion> getEffectsOfResponse(String response) {
		
		ArrayList<GoalPromotion> goalPromotion = new ArrayList<GoalPromotion>();
		
		ArrayList<String> results = queryOntology.askQuery("Goal and increased_by some " + response);
		
		for(String result : results) {
			
			goalPromotion.add(new GoalPromotion(result, true));
			
		}
	
		results = queryOntology.askQuery("Goal and reduced_by some " + response);
		
		for(String result : results) {
			
			goalPromotion.add(new GoalPromotion(result, false));
			
		}
		
		results = queryOntology.askQuery("Goal and not_affected_by some " + response);
		
		for(String result : results) {
			
			goalPromotion.add(new GoalPromotion(result, true));
			
		}
	
		return goalPromotion;
	
	}

	/************************************************************************************/
	
	abstract boolean receiveResponse(String response);
	
	/************************************************************************************/
	
	protected ArrayList<String> preferenceOrdering = new ArrayList<String>();
	
	public void addGoal(String g) { 
		
		preferenceOrdering.add(g); 
		
	}
	
	public void addGoal(String g, int position) {
		
		preferenceOrdering.add(position, g);
		
	}
	
	public void topPriority(String g) {
		
		preferenceOrdering.add(g);
	
	}
	
	public void lowestPriority(String g) {
		
		preferenceOrdering.add(preferenceOrdering.size(), g);
	
	}
	
	/************************************************************************************/
	
	private QueryOntology queryOntology;
	
	@Override
	public void run() {
		
		queryOntology = new QueryOntology("/Users/Martin/Dropbox/workspace/JpCap/src/uk/ac/kcl/dcs/agentpref/ResponseImpacts.owl");
		queryOntology.loadOntology();
		
	}
	
	/************************************************************************************/
	
}
