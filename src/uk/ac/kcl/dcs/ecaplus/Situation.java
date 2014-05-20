package uk.ac.kcl.dcs.ecaplus;

public class Situation {

	String name = "Normal_activity";
	int timeStart;
	int timeEnd;
	
	Situation() {}
	
	Situation(String name) { name(name); }
	
	void name(String name) { this.name = name; }
	
	String name() { return name; }

	void timeStart(int timeStart) { this.timeStart = timeStart; }
	
	int timeStart() { return timeStart; }
	
	void timeEnd(int timeEnd) { this.timeEnd = timeEnd; }
	
	int timeEnd() { return timeEnd; }
	
	public String toString() { return name; }
	
}
