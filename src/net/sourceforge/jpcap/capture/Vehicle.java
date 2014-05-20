package net.sourceforge.jpcap.capture;

abstract class Vehicle {

	int wheels = 4; 
	
	public void takeOffWheel() {
		
		wheels--;
		
	}
	
	abstract void revEngine();
	
}


