package uk.ac.kcl.dcs.ecarv;

public class Intention {

	// If this trigger (event) happens in this context, posneg is the intent.
	public Intention(Trigger ec, Context context, Boolean posneg) {
		
		this.ec = ec; // Trig to ...
		this.context = context; // Context (Context func.). Context to...
		this.posneg = posneg; // Up down (Intent func.)
		
	}
	
	// If this trigger (event) happens towards this component, whilst it is in this state, posneg is the intent.
	public Intention(Trigger ec, ComponentState cs, Boolean posneg) {
		
		this.ec = ec;
		this.cs = cs;
		this.posneg = posneg;
		
	}
	
	private Trigger ec;
	
	public Trigger getEc() {
		return ec;
	}

	public void setEca(Trigger ec) {
		this.ec = ec;
	}

	private Context context = new Context("", "", "");
	
	public Context getContext() {
		return context;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	private ComponentState cs = new ComponentState(new Component("", 0, 0, ""), "");
	
	public ComponentState getCs() {
		return cs;
	}
	
	public void setCS(ComponentState cs) {
		this.cs = cs;
	}
	
	private Boolean posneg;

	public Boolean getPositiveNegative() {
		return posneg;
	}

	public void setPositiveNegative(Boolean posneg) {
		this.posneg = posneg;
	}
	
}
