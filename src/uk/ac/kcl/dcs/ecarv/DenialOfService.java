package uk.ac.kcl.dcs.ecarv;

public class DenialOfService extends Exception {

	private String target;
	public String getTarget() {
		return target;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	DenialOfService(String target) {
		
		this.target = target;
		
	}
	
	
	
}
