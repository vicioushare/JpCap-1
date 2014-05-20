package uk.ac.kcl.dcs.ecaplus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;

import net.sourceforge.jpcap.capture.CapturePacketException;
import net.sourceforge.jpcap.capture.PacketListener;
import net.sourceforge.jpcap.capture.RawPacketListener;
import net.sourceforge.jpcap.net.Packet;
import net.sourceforge.jpcap.net.RawPacket;
import net.sourceforge.jpcap.net.TCPPacket;
import net.sourceforge.jpcap.simulator.PacketCaptureSimulator;
import net.sourceforge.jpcap.util.AsciiHelper;
import net.sourceforge.jpcap.util.Timeval;

public class CapacityServerSim {

	ArrayList<ServerModel_PacketHandler> Servers = new ArrayList<ServerModel_PacketHandler>();
	
	SituationController situationController = new SituationController();
	
	ArrayList<SimplePolicy> policies = new ArrayList<SimplePolicy>();
	
	QueryOntology queryOntology = new QueryOntology("/Users/Martin/Dropbox/University/PhD/2013/6. 4th Feburary - 8th Feburary/Ontologies/7-2-13.owl");
	
	public static void main (String[] args) throws CapturePacketException {
		
		new CapacityServerSim();
		
		// 9/2/13 Could work with policies which are slightly more dynamic and incorporate a 'target'
		// policies.add(new SimplePolicy("Request_disrupting_activity", "Low_capacity_server", SimplePolicy.BLOCK));
	
	}
	
	CapacityServerSim() throws CapturePacketException {
		
		queryOntology.loadOntology();
		
		PacketCaptureSimulator sim = new PacketCaptureSimulator();
		
		TCPPacketQueue queue = new TCPPacketQueue(10);
		
		// ServerModel_PacketReceiver highSpeedServer_PacketReceiver = new ServerModel_PacketReceiver(queue);
	
		// ServerModel_PacketHandler highSpeedServer_PacketHandler = new ServerModel_PacketHandler(queue, 10, "10.0.0.128", "Web_server");
		
		// Servers.add(highSpeedServer_PacketHandler);
		
		// highSpeedServer_PacketHandler.start();
		
		//
		
		ServerModel_PacketHandler privateFileServer_PacketHandler = new ServerModel_PacketHandler(queue, 4, "10.0.0.129", "Private_file_server");
		
		Servers.add(privateFileServer_PacketHandler);
		
		privateFileServer_PacketHandler.start();
		
		//
		
		ServerModel_PacketReceiver server_PacketReceiver = new ServerModel_PacketReceiver(queue);
		
		sim.addPacketListener(server_PacketReceiver);
		
		server_PacketReceiver.start();
		
		//
		
		sim.capture(500);
		
		System.out.println("Failed to service (in good time) " + server_PacketReceiver.unservicableRequestCount() + " requests.");
		System.out.println("---------------------------------------------------------------------------------");
		
	}
	
	// Commonly used queue
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
		
		public String toString() { return "Queue state: (" + size + "/" + length + ")"; }
	}
	
	// "Consumer"
	class ServerModel_PacketHandler extends Thread {
	//implements SelfAwareSyntax
		
		TCPPacketQueue queue;
		// Between 1 - 10 with 10 being fastest
		int processSpeed;
		String IPAddress;
		
		// Consider how this marking up is done? Some services markup?
		String type;
		ArrayList<SelfAwareStatement> selfKnowledge = new ArrayList<SelfAwareStatement>();
		//
		
		ServerModel_PacketHandler(TCPPacketQueue queue, int processSpeed, String IPAddress, String type) { 
			this.queue = queue; 
			this.processSpeed = processSpeed <= 10 ? processSpeed : 5; 
			this.IPAddress = IPAddress;
			this.type = type;

			selfKnowledge.add(new SelfAwareStatement(type, "must_not_service", "some", "Service_disrupting_activity"));
			//selfKnowledge.add(new SelfAwareStatement(type, "popular_target_for", "some", "Criminal"));
			//selfKnowledge.add(new SelfAwareStatement(type, "currently_undergoing", "some", "Mainentance"));
		}
		
		String IPAddress() { return IPAddress; }
		
		@Override
		public void run() {
		
			while(true) {
				
				if (queue.size() != 0) {
					
					TCPPacket packet = queue.getPacket();
					
					if ( examineSelf() ) {
					
						String srcHost = packet.getSourceAddress();
					    String dstHost = packet.getDestinationAddress();
					    System.out.println("-------------------------------------- PROCESSING PACKET. "  + queue);
					    System.out.println("From " + srcHost + " to " + dstHost);
				    
					    // Mimic taking time to process the packet
					    try {
							
							Thread.sleep(100 - (processSpeed * 10));
							
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						
						}
					    
					} else {
						
						System.out.println("-------------------------------------- PACKET BLOCKED.");
						
					}
					
				} else {
					
					// Purely to give other thread a chance to 'put' due to consuming 'while' loop
					try {
						
						Thread.sleep(0);
						
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					
					}
				
				}
				
			}
			
		}
		
		boolean examineSelf() {
			
			for (SelfAwareStatement knowledge: selfKnowledge) {
				
				String queryString = "Server and " + knowledge.action() + " " + knowledge.conditionRestriction() + " " + situationController.currentSituation();
				
				ArrayList<String> results = queryOntology.askQuery(queryString);
				
				for (String individualResult : results) {
				
					if (knowledge.me().equals(individualResult)) {
						
						return false;
						
					}
					
				}
				
			}
			
			return true;
			
		}
		
		// 8/2 May be needed in future
		/*String resolveIPToEntity(String IP) {
			
			
			
		}*/
		
	}
	
	// "Producer"
	class ServerModel_PacketReceiver extends Thread implements PacketListener {

		TCPPacketQueue queue;
		int unservicableRequestCount = 0;
		
		ServerModel_PacketReceiver(TCPPacketQueue queue) { this.queue = queue; }

		int unservicableRequestCount() { return unservicableRequestCount; }
		
		int lastPacketTime;
		
		public void packetArrived(Packet packet) {
			
			if (queue.size() < queue.length()) {
				
				try {
			
					TCPPacket tcpPacket = (TCPPacket)packet;
				    queue.putPacket(tcpPacket);
				    System.out.println("> TCP packet arrived. Time since last packet: " + ((tcpPacket.getTimeval().getMicroSeconds() / 1000) - lastPacketTime) + "ms <");
				    lastPacketTime = tcpPacket.getTimeval().getMicroSeconds() / 1000;
				    situationController.update(new Event("servicing some Packet"));
				   
				}
			    catch( Exception e ) { System.out.println("Other packet arrived."); }
				
			} else {
				
				unservicableRequestCount++;
				
				System.out.println("------------------------------------------------------------------");
				System.out.println(" Packet arrived, queue full. Currently unservicable requests: " + unservicableRequestCount);
				System.out.println("------------------------------------------------------------------");
				
				situationController.update(new Event("unable_to_service some Packet"));
				
			}
			
		}
		
	}
	
}
