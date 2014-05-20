package uk.ac.kcl.dcs.ecaplus;

// Benefits of this over simple policies:
// 1. Flexibility of incorporating ontology
// 2. Decentralised
// 3. General feel of moving towards semantics, goal-orientation and self-awareness which is good
// 4. Delegate decisions about what to do -- `resolve it amongst themselves'.

// 9/2/13 -- would be nice to develop a formal syntax for this

public class SelfAwareStatement implements SelfAwareSyntax {

	// e.g. I am a Private_file_server
	String me;
	// e.g. therefore I must_not_service
	String action;
	// e.g. some 
	String conditionRestriction;
	// e.g. Request_disrupting_activity
	String condition;
	
	SelfAwareStatement(String me, String action, String conditionRestriction, String condition) {
		
		this.me = me;
		this.action = action;
		this.conditionRestriction = conditionRestriction;
		this.condition = condition;
		
	}
	
	String me() { return me; }
	String action() { return action; }
	String conditionRestriction() { return conditionRestriction; }
	String condition() { return condition; }
	
}
