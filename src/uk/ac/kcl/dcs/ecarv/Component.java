package uk.ac.kcl.dcs.ecarv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Observable;

import net.sourceforge.jpcap.capture.CapturePacketException;
import net.sourceforge.jpcap.capture.PacketListener;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.simulator.PacketCaptureSimulator;
import uk.ac.kcl.dcs.ecaplus.Event;
import uk.ac.kcl.dcs.ecaplus.SelfAwareStatement;

public class Component {

	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public int getProcessSpeed() {
		return processSpeed;
	}

	public void setProcessSpeed(int processSpeed) {
		this.processSpeed = processSpeed;
	}

	public String getContentOnComponent() {
		return contentOnComponent;
	}

	public void setContentOnComponent(String contentOnComponent) {
		this.contentOnComponent = contentOnComponent;
	}

	public ArrayList<Value> getPreferenceOrdering() {
		return preferenceOrdering;
	}

	public void setPreferenceOrdering(ArrayList<Value> preferenceOrdering) {
		this.preferenceOrdering = preferenceOrdering;
	}

	public ArrayList<Intention> getIntentions() {
		return knownIntentions;
	}

	public void setIntentions(ArrayList<Intention> intentions) {
		this.knownIntentions = intentions;
	}

	private int queueSize;
	private int processSpeed;
	private String contentOnComponent;
	private String state = "ANY";
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Component(String name, int queueSize, int processSpeed, String contentOnComponent) {
		
		this.name = name;
		this.queueSize = queueSize;
		this.processSpeed = processSpeed;
		this.contentOnComponent = contentOnComponent;
	}
	
	public void go() throws CapturePacketException, DenialOfService {
		
		PacketCaptureSimulator sim = new PacketCaptureSimulator();
		
		//
		
		TCPPacketQueue queue = new TCPPacketQueue(queueSize);
		
		//
		
		PacketReceiver packetReceiver = new PacketReceiver(queue);
		
		sim.addPacketListener(packetReceiver);
		
		//
		
		PacketHandler packetHandler = new PacketHandler(queue, processSpeed);
		
		packetHandler.start();
		
		//
		
		sim.capture(500);
		
		//

		System.out.println(name + ": Failed to service (in good time) " + packetReceiver.unservicableRequestCount() + " requests.");
		System.out.println("---------------------------------------------------------------------------------");
		
		if (packetReceiver.unservicableRequestCount() > 50) { throw new DenialOfService(name); }
		
	}
	
	// "Producer"
	class PacketReceiver implements PacketListener {

		TCPPacketQueue queue;
		int unservicableRequestCount = 0;
		
		PacketReceiver(TCPPacketQueue queue) { this.queue = queue; }

		int unservicableRequestCount() { return unservicableRequestCount; }
		
		int lastPacketTime;
		
		public void packetArrived(Packet packet) {
			
			if (queue.size() < queue.length()) {
				
				try {
			
					TCPPacket tcpPacket = (TCPPacket)packet;
				    queue.putPacket(tcpPacket);
				    System.out.println(name + ":> TCP packet arrived. Time since last packet: " + ((tcpPacket.getTimeval().getMicroSeconds() / 1000) - lastPacketTime) + "ms <");
				    lastPacketTime = tcpPacket.getTimeval().getMicroSeconds() / 1000;
				   
				}
			    catch( Exception e ) { System.out.println(name + ": Other packet arrived."); }
				
			} else {
				
				unservicableRequestCount++;
				
				/*System.out.println("------------------------------------------------------------------");
				System.out.println(name + ": Packet arrived, queue full. Currently unservicable requests: " + unservicableRequestCount);
				System.out.println("------------------------------------------------------------------");*/
				
				
				
			}
			
		}
		
	}
	
	class TCPPacketQueue {
		
		TCPPacket[] queue;
		int length = 0;
		int size = 0;
		
		TCPPacketQueue(int length) { queue = new TCPPacket[length]; this.length = length; }
		
		TCPPacket getPacket() { size--; return queue[size]; }
		void putPacket(TCPPacket packet) { queue[size] = packet; size++; }
		
		int length() { return length; }
		int size() { return size; }
		TCPPacket[] queue() { return queue; }
		
		public String toString() { return name + ": Queue state: (" + size + "/" + length + ")"; }
	}
	
	// "Consumer"
	class PacketHandler extends Thread {
	//implements SelfAwareSyntax
		
		TCPPacketQueue queue;
		// Between 1 - 10 with 10 being fastest
		int processSpeed;
		
		PacketHandler(TCPPacketQueue queue, int processSpeed) { 
			this.queue = queue; 
			this.processSpeed = processSpeed <= 10 ? processSpeed : 5; 
		}
		
		@Override
		public void run() {
		
			while(true) {
				
				if (queue.size() != 0) {
					
					TCPPacket packet = queue.getPacket();
					
					String srcHost = packet.getSourceAddress();
				    String dstHost = packet.getDestinationAddress();
				  
				    try {
						
						Thread.sleep(100 - (processSpeed * 10));
						
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					
					}
					  
				} else {
					
					try {
						
						Thread.sleep(0);
						
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					
					}
				
				}
				
			}
			
		}
		
	}
		
	/************************************************************************************/
	
	ArrayList<Value> preferenceOrdering = new ArrayList<Value>();
	
	public void addValue(Value v) { 
		
		preferenceOrdering.add(v); 
		System.out.println(name + "'s new preference ordering is: " + preferenceOrdering);
		
	}
	
	public void addValue(Value v, int position) {
		
		preferenceOrdering.add(position, v);
		System.out.println(name + "'s new preference ordering is: " + preferenceOrdering);
		
	}
	
	public void topPriority(Value v) {
		
		preferenceOrdering.add(v);
		System.out.println(name + "'s new preference ordering is: " + preferenceOrdering);
		
	}
	
	public void lowestPriority(Value v) {
		
		preferenceOrdering.add(preferenceOrdering.size(), v);
		System.out.println(name + "'s new preference ordering is: " + preferenceOrdering);
		
	}
	
	public Hashtable<Value, Double> act(ArrayList<ValuePromotion> valuePromotion) {
		
		// Map each Value to vote
		Hashtable<Value, Double> votes = new Hashtable<Value, Double>();
		
		// Loop through all the value / promotion pairs from the ECARV rule
		for (ValuePromotion vp : valuePromotion) {
			
			// If the value is in this component's preference ordering (they are concerned about it):
			if (preferenceOrdering.contains(vp.getValue())) {
				
				// Give a score to that value based on its position in the preference ordering.
				// The score is based on 1 / N with N being the position in the preference
				// In this way, preferences lower in the ordering have weighting but it is reduced.
				votes.put(vp.getValue(), (1.0 / (preferenceOrdering.indexOf(vp.getValue()) + 1)));
				
			}
			
		}
		
		return votes;
		
	}
	
	/************************************************************************************/
	
	ArrayList<Intention> knownIntentions = new ArrayList<Intention>();
	
	public void addIntention(Intention intention) {
		
		knownIntentions.add(intention);
		
	}
	
	public boolean act(ECA eca) {
		
		System.out.println(knownIntentions);
		
		for (Intention intention : knownIntentions) {

			System.out.println(intention.getEc().getSummary() +"| |"+ eca.getEvent() + eca.getCondition() + "|");
			
			// If eca contains a known event...
			if (intention.getEc().getSummary().trim().equals((eca.getEvent() + eca.getCondition()).trim())) {
				
				// ...and we are in a particular known context, linked to this event.
				if(intention.getContext().getSourceLocation().equals(eca.getEvent().getSource()) && // If the event source matches a known source
				   intention.getContext().getTime().equals(eca.getEvent().getTime()) && // and if the event time matches a known time 
				   intention.getContext().getContentOnComponent().equals(contentOnComponent)) { // and if the content on the component matches known content
					
					// ...the proposal should not proceed.
					return intention.getPositiveNegative();
				
				// ...towards a component in a particular state, linked to the event.
				} else if(intention.getCs().getComponent().getName().equals(name) &&
						  intention.getCs().getState().equals(state)) {

					// ...the proposal should not proceed.
					return intention.getPositiveNegative();
					
				}
				
				
			}
			
		}
		
		// No known knoweldge of there being anything wrong with this event and its context and affect on components
		return true;
		
	}
	
}