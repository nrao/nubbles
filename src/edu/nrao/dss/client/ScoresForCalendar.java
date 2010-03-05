package edu.nrao.dss.client;

public class ScoresForCalendar implements ScoresDisplay {

	Schedule schedulePanel;
	
	public ScoresForCalendar(Schedule schedulePanel) {
	    this.schedulePanel = schedulePanel;
	}
	
	@Override
	public void show(String sessionName, float[] scores) {
		
		// display to the user what scores we're displaying
		String label = "Calendar (Scoring Session " + sessionName + " )";
		schedulePanel.setCalendarHeader(label);
		
	    // set the list of scores
		schedulePanel.setCalendarScores(scores);
		
		// update the calendar
		schedulePanel.updateCalendar();
	}

}
