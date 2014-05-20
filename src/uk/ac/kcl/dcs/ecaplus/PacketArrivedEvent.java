package uk.ac.kcl.dcs.ecaplus;

public class PacketArrivedEvent extends Event {

	private String packetType;
	private String target;
	private String time;
	
	PacketArrivedEvent(String packetType, String target, String time) { super("TCPPacket"); packetType(packetType); target(target); time(time); }
	
	void packetType(String type) { this.packetType = type; }
	
	void target(String target) { this.target = target; }
	
	void time(String time) { this.time = time; }
	
	String packetType() { return packetType; }
	
	String target() { return target; }
	
	String time() { return time; }
	
}
