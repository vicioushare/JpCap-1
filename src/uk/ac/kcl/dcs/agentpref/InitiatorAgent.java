package uk.ac.kcl.dcs.agentpref;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class InitiatorAgent implements Runnable {

	private ArrayList<ComponentAgent> components = new ArrayList<ComponentAgent>();
	
	public ArrayList<ComponentAgent> getComponents() {
		return components;
	}

	public void setComponents(ArrayList<ComponentAgent> components) {
		this.components = components;
	}

	private ArrayList<Hashtable<String, Double>> componentVotes = new ArrayList<Hashtable<String, Double>>();
	private Hashtable<String, Double> globalVotes = new Hashtable<String, Double>();
	// ~MDC The names of these attacks will need to be altered to match what comes out of SNORT
	private Hashtable<String, String> attackToResponse = new Hashtable<String, String>() {{ put(".*(malware|trojan).*", "Quarantine_Hosts, Divert_To_Honeypot"); put(".*(buffer|overflow|executable|code|memory).*", "Restart_Hosts, Snipe_Session"); 
																							put(".*(bruteforce|admin|root|privilege|.exe).*", "Snipe_Session, Block_IP_Address"); put(".*(denial|service|flood).*","Block_IP_Address, Snipe_Session"); 
																							put(".*(spoof|spoofed|probe).*","Divert_To_Honeypot, Restart_Hosts"); }};
	
	private synchronized GoalPromotion collectVotes(String response) {
		
		System.out.println("Proposing to " + response);
		
		// Loop through each component and ask for their opinions on whether the action
		// should be performed in the rule based on their preference orderings
		for (ComponentAgent component : components) {
			
			componentVotes.add(component.act(response));
			
		}
		
		// Loop through the component votes
		for (Hashtable<String, Double> vote: componentVotes){
	
			// Collate the votes for each Goal given by each component
			for ( Map.Entry<String, Double> entry : vote.entrySet() ) {
			
				if (globalVotes.containsKey(entry.getKey())) {
					  
					globalVotes.put(entry.getKey(), globalVotes.get(entry.getKey()) + entry.getValue());
					  
				} else {
				  
					globalVotes.put(entry.getKey(), entry.getValue());
				  
				}
			
			}

		}
		
		// Decide which Goal is most important to the components
		Map.Entry<String, Double> highest = null;
		ArrayList<Map.Entry<String, Double>> highList = new ArrayList<Map.Entry<String, Double>>();
		
		for( Map.Entry<String, Double> entry : globalVotes.entrySet()) {
		
			if (highest == null || entry.getValue().compareTo(highest.getValue()) > 0) {
				
				highest = entry;
				highList.clear();
				highList.add(highest);
			
			// If we have two highest, add to list of highest goals.
			} else if (highest == null || entry.getValue().compareTo(highest.getValue()) == 0) {
				
				highList.add(entry);
				
			}
		
		}
		
		System.out.println("Votes:" + globalVotes);
		
		// If there are multiple high goals, select one based on split equal probability:
		if (highList.size() > 1) {
			
			highest = highList.get((int)(Math.random() * highList.size()));
			System.out.println("Multiple winning Goals: " + highList + ". Choosing by probability: " + highest.getKey());
		}
	
		System.out.println("Winner: " + highest);
		
		// The goal which is most 'valued' dictates whether the action will be performed or not.
		// If the most 'valued goal' is decreased, then the action will not be performed. If it
		// is promoted, then the action will be performed.
		for (GoalPromotion goalPromotion : getEffectsOfResponse(response)) {
			
			if (goalPromotion.getGoal().equals(highest.getKey())) {
				
				goalPromotion.setAmountValued(highest.getValue());
				
				componentVotes.clear();
				globalVotes.clear();
				
				return goalPromotion; 
			
			} 
			
		}
		
		return null;
		
	}
	
	/************************************************************************************/
	private ArrayList<GoalPromotion> getEffectsOfResponse(String response) {
		
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
	
	private int alerts = 0;
	
	private ArrayList<SNORTAlert> readSNORTLog() {
		
		ArrayList<SNORTAlert> attacks = new ArrayList<SNORTAlert>();
		
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader("/var/log/snort/alert"));
 
			Boolean newAlert = true;
			
			while ((sCurrentLine = br.readLine()) != null) {
				
				SNORTAlert currentAttack = new SNORTAlert("", "");
				
				if (sCurrentLine.length() > 4 && sCurrentLine.charAt(2) == '/') {
				
					currentAttack.setTimestamp(sCurrentLine.substring(0, sCurrentLine.indexOf(" ")));
				
				}
				
				for ( Entry<String, String> entry : attackToResponse.entrySet() ) {
					
					if (sCurrentLine.toLowerCase().matches(entry.getKey()) && sCurrentLine.toLowerCase().startsWith("[*")) {
						
						currentAttack.setType(entry.getKey());
						attacks.add(currentAttack);
						newAlert = false;
						
					} else if (newAlert == true && sCurrentLine.toLowerCase().matches(entry.getKey()) && sCurrentLine.toLowerCase().startsWith("[class")) {
						
						currentAttack.setType(entry.getKey());
						attacks.add(currentAttack);
						
					}
					
				}
				
				if (sCurrentLine.equals("")) {
					alerts++;
					newAlert = true;
				}
				
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return attacks;
		
	}
	
	/************************************************************************************/
	
	private QueryOntology queryOntology;
	
	@Override
	public void run() {
		
		queryOntology = new QueryOntology("/Users/Martin/Dropbox/workspace/JpCap/src/uk/ac/kcl/dcs/agentpref/ResponseImpacts.owl");
		queryOntology.loadOntology();
		
		//
		
		ArrayList<String> recordedAttacks = new ArrayList<String>();
		FileWriter writer = null;
		String responses[];
				
		//while(true) {
			
			// TO DO: Read attacks in realtime whilst generated by PCAP
			ArrayList<SNORTAlert> attacks = readSNORTLog();
			
			if(!recordedAttacks.contains(attacks.get(attacks.size() - 1).getTimestamp())) {
				
				for(SNORTAlert alert : attacks) {
				
					recordedAttacks.add(alert.getTimestamp());
					
					System.out.println("SNORT detects attack: " + alert);
					
					final boolean CHECKED = false;
					
					responses = attackToResponse.get(alert.getType()).split(",");
					
					if (CHECKED) {
					
						// 
						
						final class actionToGoal<K, V> implements Map.Entry<K, V> {
						    private final K key;
						    private V value;

						    public actionToGoal(K key, V value) {
						        this.key = key;
						        this.value = value;
						    }

						    @Override
						    public K getKey() {
						        return key;
						    }

						    @Override
						    public V getValue() {
						        return value;
						    }

						    @Override
						    public V setValue(V value) {
						        V old = this.value;
						        this.value = value;
						        return old;
						    }
						    
						    public String toString() {
						    	
						    	return "actionToGoal: " + key + " " + value;
						    	
						    }
						}
						
						ArrayList<actionToGoal<String, GoalPromotion>> actionToGoalPromotion = new ArrayList<actionToGoal<String, GoalPromotion>>();
						
						//
						
						for (String response : responses) {
						
							System.out.println("Proposing to issue " + response);
							actionToGoalPromotion.add(new actionToGoal<String, GoalPromotion>(response, collectVotes(response)));
						
						}
						
						System.out.println("Views on all actions: " + actionToGoalPromotion);
						
						//
						
						actionToGoal<String, GoalPromotion> mostPreferred;
						ArrayList<actionToGoal<String, GoalPromotion>> equalGoals = new ArrayList<actionToGoal<String, GoalPromotion>>();
						
						mostPreferred = new actionToGoal<String, GoalPromotion>("", new GoalPromotion("", true, -1));
						
						for (actionToGoal<String, GoalPromotion> actionGoal : actionToGoalPromotion) {
							
							if (actionGoal.getValue().getAmountValued() > mostPreferred.getValue().getAmountValued() && actionGoal.getValue().isPromotion() != false) {
								
								equalGoals.clear();
								equalGoals.add(actionGoal);
								mostPreferred = actionGoal;
								
							} else if (actionGoal.getValue().getAmountValued() == mostPreferred.getValue().getAmountValued() && actionGoal.getValue().isPromotion() != false) {
								
								equalGoals.add(actionGoal);
								
							}
							
						}
						
						if (equalGoals.size() > 1) { mostPreferred = equalGoals.get((int)Math.random()*equalGoals.size()); }
						
						
						if(mostPreferred.getKey() != "") {
						
							System.out.println("Most preferred goal, " + mostPreferred.getValue().getGoal() + ", linked to action " + mostPreferred.getKey() + ". Applying this action.");
						
							for (ComponentAgent component : components) {
								
								component.receiveResponse(mostPreferred.getKey());
								
							}
						
						}
						
					// Without cost check
					} else {
						
						for (ComponentAgent component : components) {
							
							component.receiveResponse(responses[(int)Math.random()*responses.length]);
							
						}
						
					}
					
					// Logging -- TO DO: Log4J
					
					Date date = new Date();
					
					try {
						writer = new FileWriter("/Users/Martin/Dropbox/University/PhD/2013/19. 6th May - 10th May/ActionsValues/Results/simulation" + CHECKED + date.toString().substring(0, 10) + ".csv", true);
					} catch (IOException e) {}
					
					
					Collections.sort(components, new Comparator<ComponentAgent>() {
											     	public int compare(ComponentAgent o1, ComponentAgent o2) {
													   return o1.toString().compareTo(o2.toString());
													}
												 });
					
					for (ComponentAgent component : components) {
						
						System.out.println(component + " " + component.getState());
						
						try
						{
						    writer.append(component.toString());
						    writer.append(',');
						    writer.append(component.getState());
						    writer.append(',');
						    writer.append(alert.getType());
						    writer.append('\n');
	
						    writer.flush();
						}
						catch(IOException e)
						{
						     e.printStackTrace();
						} 
						
					}
				
				}
				
				try {
					writer.append(alerts + " alerts received. " + attacks.size() + " identified and " + (alerts - attacks.size()) + " unidentified (just logged).");
					writer.append('\n');
					writer.flush();
				} catch (IOException e) {}
			
			}
			
		//}
		
	}
	
}
