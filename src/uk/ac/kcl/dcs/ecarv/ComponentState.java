package uk.ac.kcl.dcs.ecarv;

public class ComponentState {

	private Component component;
	public Component getComponent() {
		return component;
	}
	public void setComponent(Component component) {
		this.component = component;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public ComponentState(Component component, String state) {
		super();
		this.component = component;
		this.state = state;
	}
	private String state;
	
	
	
}
