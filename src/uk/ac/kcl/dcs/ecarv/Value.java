package uk.ac.kcl.dcs.ecarv;

public class Value {

	private String value;

	Value(String value) { this.value = value; }
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		
		return value;
		
	}
	
}