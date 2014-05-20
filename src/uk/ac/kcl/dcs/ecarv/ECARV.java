package uk.ac.kcl.dcs.ecarv;

import java.util.ArrayList;

public class ECARV {

	private ECA eca;
	public ECA getEca() {
		return eca;
	}
	public void setEca(ECA eca) {
		this.eca = eca;
	}
	public ArrayList<ValuePromotion> getValuePromotion() {
		return valuePromotion;
	}
	public void setValuePromotion(ArrayList<ValuePromotion> valuePromotion) {
		this.valuePromotion = valuePromotion;
	}
	/**/
	private String repercussion;
	private ArrayList<ValuePromotion> valuePromotion = new ArrayList<ValuePromotion>();
	
	public ECARV(ECA eca, ArrayList<ValuePromotion> valuePromotion) {
		
		this.eca = eca;
		this.valuePromotion = valuePromotion;
		
	}
	
	
}