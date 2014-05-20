package uk.ac.kcl.dcs.agentpref;

import java.util.ArrayList;

public class Controller {

	private ArrayList<ComponentAgent> componentAgents = new ArrayList<ComponentAgent>();
	private InitiatorAgent initiatorAgent; 
	
	private final static int NUMBEROFCOMPONENTS = 20;
	
	public Controller() {
		
		int MS = 0;
		int DS = 0;
		int WS = 0;
		
		for (int a = 0; a < NUMBEROFCOMPONENTS; a++) {
			
			int random = (int) (Math.random()*3);
		
			switch(random){
			
				case 0: componentAgents.add(new ConfidentialityComponentAgent()); MS++; break;
				
				case 1: componentAgents.add(new IntegrityComponentAgent()); DS++; break;
			
				case 2: componentAgents.add(new AvailabilityComponentAgent()); WS++; break;
			
			}
			
		}
		
		System.out.println("Component Composition: " + componentAgents);
		
		System.out.println("Mail Servers: " + MS + ", Database Servers: " + DS + ", Web Servers: " + WS);
		
		initiatorAgent = new InitiatorAgent();
		initiatorAgent.setComponents(componentAgents);
		
		for (ComponentAgent agent : componentAgents) {
			
			agent.run();
			
		}
		
		initiatorAgent.run();
		
	}
	
	public static void main(String[] args) {
		
		new Controller();
		
	}
	
}
