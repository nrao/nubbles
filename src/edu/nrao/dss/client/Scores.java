package edu.nrao.dss.client;

public class Scores {
	private ScoresControl control;
	private ScoresDisplay display;
	private ScoresAccess access;

	public Scores(ScoresControl c, ScoresDisplay d) {
		display = d;
		control = c;
		control.setDisplay(display);
		control.setAccess(new ScoresAccess());
	}
}
