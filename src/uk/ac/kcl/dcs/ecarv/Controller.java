package uk.ac.kcl.dcs.ecarv;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import net.sourceforge.jpcap.capture.CapturePacketException;

public class Controller {

	private ArrayList<Hashtable<Value, Double>> componentVotes = new ArrayList<Hashtable<Value, Double>>();
	private Hashtable<Value, Double> globalVotes = new Hashtable<Value, Double>();
	private ArrayList<Component> components = new ArrayList<Component>();
	private ArrayList<Value> values = new ArrayList<Value>();
	
	public Controller() throws CapturePacketException {
		
		// Create three new components which are represented in a network
		Component fileServerA = new Component("File Server A", 10, 4, "employeefiles");
		components.add(fileServerA);
		
		Component fileServerB = new Component("File Server B", 10, 4, "employeefiles");
		components.add(fileServerB);
		
		Component webServerA = new Component("Web Server A", 10, 10, "companywebsite");
		components.add(webServerA);
		
		Component webServerB = new Component("Web Server B", 10, 10, "employeewebsites");
		//components.add(webServerB);
		
		// Create two new values
		Value availability = new Value("Public Availability");
		Value accessibility = new Value("Private Accessibility");
		Value profit = new Value("Company Profit");
		
		// Add the appropriate values to the components.
		fileServerA.addValue(availability);
		fileServerB.addValue(availability);
		webServerA.addValue(availability);
		webServerB.addValue(profit);
		
		// In this case, specify a preference ordering. 
		fileServerA.addValue(accessibility, 0);
		fileServerB.addValue(accessibility, 0);
		webServerA.addValue(accessibility);
		webServerB.addValue(accessibility);
		
		// Create a new event.
		Event e = new Event("Receipt_of_packet", "4.00am", "China");
		
		// Create a new ECA rule
		ECA eca = new ECA(e, "Current_unservicable_packet_count > 50", "Shutdown_public_access");
		
		Intention intention = new Intention(eca.getTrigger(), new Context("4.00am", "China", "companywebsite"), false);
		
		ArrayList<ValuePromotion> ecarvValuePromotions = new ArrayList<ValuePromotion>();
		
		// Create Value / Promotion pairs
		ecarvValuePromotions.add(new ValuePromotion(availability, false));
		ecarvValuePromotions.add(new ValuePromotion(accessibility, true));
		ecarvValuePromotions.add(new ValuePromotion(profit, false));
		
		// Add these pairs and other information to the Ecarv rule
		ECARV ecarv = new ECARV(eca, ecarvValuePromotions);
		
		// ~MDC 8/3 Concurrency not fully implemented, one component detecting demonstrates the purpose, however,
		try {
			fileServerA.go();
			//fileServerB.go();
			//webServerA.go();
			//webServerB.go();
		} catch (DenialOfService dos) {
			// Trigger the rule (could be done by examing and matching events, but for now just triggered).
			trigger(ecarv, dos.getTarget());
		}

	}
	
	private void trigger(ECARV ecarv, String name) {
		
		/* 1. Interpet Events: Context and Self Awareness */
		
		for (Component component : components) {
			
			if (component.getName().equals(name)) {
				
				System.out.println(component.act(ecarv.getEca()));
				
			}
			
		}
		
		/*2. Effects of Actions */

		// Loop through each component and ask for their opinions on whether the action
		// should be performed in the rule based on their preference orderings
		for (Component component : components) {
			
			componentVotes.add(component.act(ecarv.getValuePromotion()));
			
		}
		
		// Loop through the component votes
		for (Hashtable<Value, Double> vote: componentVotes){
	
			// Collate the votes for each Value given by each component
			for ( Map.Entry<Value, Double> entry : vote.entrySet() ) {
			
				if (globalVotes.containsKey(entry.getKey())) {
					  
					globalVotes.put(entry.getKey(), globalVotes.get(entry.getKey()) + entry.getValue());
					  
				} else {
				  
					globalVotes.put(entry.getKey(), entry.getValue());
				  
				}
			
			}

		}
		
		System.out.println(globalVotes);
		
		// Decide which the most important Value is to the components
		Map.Entry<Value, Double> highest = null;
		ArrayList<Map.Entry<Value, Double>> highList = new ArrayList<Map.Entry<Value, Double>>();
		
		for( Map.Entry<Value, Double> entry : globalVotes.entrySet()) {
		
			if (highest == null || entry.getValue().compareTo(highest.getValue()) > 0) {
				
				highest = entry;
				highList.clear();
				highList.add(highest);
			
			// If we have two highest, add to list of highest values.
			} else if (highest == null || entry.getValue().compareTo(highest.getValue()) == 0) {
				
				highList.add(entry);
				
			}
		
		}
		
		// If there are multiple high values, select one based on split equal probability:
		if (highList.size() > 1) {
			
			highest = highList.get((int)(Math.random() * highList.size()));
			System.out.println("Multiple winning Values: " + highList + ". Choosing by probability: " + highest.getKey());
		}
	
		String yesno = " -- something went wrong.";
		
		// The value which is most 'valued' dictates whether the action will be performed or not.
		// If the most 'valued value' is decreased, then the action will not be performed. If it
		// is promoted, then the action will be performed.
		for (ValuePromotion valuePromotion : ecarv.getValuePromotion()) {
			
			if (valuePromotion.getValue() == highest.getKey()) {
				
				yesno = valuePromotion.isPromotion() == false ? "not be performed because it demotes a value which is most highly preffered by the majority of components." 
															  : "be performed because it promotes a value which is highly valued by the majority of components.";
				
				System.out.println("The action " + ecarv.getEca().getAction() + " will " + yesno);
				
				//return valuePromotion.isPromotion();
			
			} 
			
		}
		
		//return false;
		
	}
	
	public static void main(String[] args) {
		
		// Create new co-ordinating class
		try {
			new Controller();
		} catch (Exception e) { }
	}
	
}
