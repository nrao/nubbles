package edu.nrao.dss.client;

public class Factors {
	private FactorsControl control;
	private FactorsDisplay display;
	private FactorsAccess access;

	public Factors(FactorsControl c, FactorsDisplay d) {
		display = d;
		control = c;
		control.setDisplay(display);
		control.setAccess(new FactorsAccess());
	}
}
