package edu.nrao.dss.client.data;

import edu.nrao.dss.client.Period;
import edu.nrao.dss.client.util.dssgwtcal.Event;

// This class is responsible for converting a Period to the Event class of the dssgwtcal package
// It maps things like period attributes to how they should be displayed in the graphical calendar

public class PeriodEventAdapter {

	public static Event fromPeriod(Period p) {
        // TODO: format title & description better			
	    String title = ""; //Integer.toString(p.getId());
	    String windowInfo = "";
	    String session_type = p.getSessionType();
	    String type = "not windowed!"; // TODO: need better way to indicate period attributes
	    if (p.isWindowed()) {
	    	windowInfo = " +" + Integer.toString(p.getWindowDaysAhead()) + "/-" + Integer.toString(p.getWindowDaysAfter());
	    	type = p.isDefaultPeriod() ? "default period" : "chosen period";
	    }
	    String desc = p.getSession() + windowInfo;
	    String color = PeriodEventAdapter.getColor(type, session_type, p.getState());
	    Event event = new Event(p.getId(), title, desc, p.getStart(), p.getStartDay(), p.getEnd(), p.getEndDay(), color); //, type, session_type, p.getState());
	    //event.setColor(PeriodEventAdapter.getColor(type, session_type, p.getState()));
	    return event;
	}	
	
	// TODO: need to improve the way we indicate period attributes
	public static String getColor(String type, String session_type, String state) {
		String color = new String("");
		if (type != "not windowed!") {
			if (type == "default period") {
				color = "green";
			} else {
				color = "yellow";
			}
		} else {
			if (session_type.contains("O")) {
				// Open Session
				color = "blue";
			} else if (session_type.contains("E")) {
				// Elective Session
				color = "darkpurple";
			} else {
				// Fixed Session
				color = "red";
			}
		}
		// Pending state wins out over everything else
		if (state.contains("P")) {
			color = "orange";
		}	
		return color;
	}
}
