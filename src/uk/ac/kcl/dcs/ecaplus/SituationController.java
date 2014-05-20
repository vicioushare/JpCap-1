package uk.ac.kcl.dcs.ecaplus;

import java.util.ArrayList;

public class SituationController {

	QueryOntology queryOntology = new QueryOntology("/Users/Martin/Dropbox/University/PhD/2013/6. 4th Feburary - 8th Feburary/Ontologies/7-2-13.owl");
	
	Situation currentSituation = new Situation();
	
	SituationController() {	queryOntology.loadOntology();	}
	
	void update(Event event) {
		
		ArrayList<String> results = queryOntology.askQuery(event.details());
		
		String result = results.get(results.size() - 1);
			
		if (result != currentSituation.name() && !(result.equals("Thing") || result.equals("Nothing"))) {
			
			currentSituation = new Situation(result);
			System.out.println("Situation changing to " + currentSituation);
			
		}
	
	}
	
	Situation currentSituation() { return currentSituation; }
	
}
