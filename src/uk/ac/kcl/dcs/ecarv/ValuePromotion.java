package uk.ac.kcl.dcs.ecarv;

public class ValuePromotion {

	private Value value;
	
	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public boolean isPromotion() {
		return promotion;
	}

	public void setPromotion(boolean promotion) {
		this.promotion = promotion;
	}

	private boolean promotion = true;
	
	ValuePromotion(Value value, boolean promotion) {
		
		this.value = value;
		this.promotion = promotion;
		
	}
	
}