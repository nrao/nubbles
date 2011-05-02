package edu.nrao.dss.client.data;

import java.util.HashMap;

import edu.nrao.dss.client.Period;
import edu.nrao.dss.client.util.dssgwtcal.Event;

// This class is responsible for converting a Period to the Event class of the dssgwtcal package
// It maps things like period attributes to how they should be displayed in the graphical calendar

public class PeriodEventAdapter {

	public static Event fromPeriod(Period p) {
		// not using a title saves valuable real estate in the UI
	    String title = "";
	    // the description is the session name, plus optional window info
	    String windowInfo = "";
	    if (p.isWindowed()) {
	    	windowInfo = " +" + Integer.toString(p.getWindowDaysAhead()) + "/-" + Integer.toString(p.getWindowDaysAfter());
	    } 
	    String desc = p.getSession() + windowInfo;
	    // color is a rather complicated mapping of period attributes
	    String color = PeriodEventAdapter.getColor(p.getState(), p.getSessionType(), p.isDefaultPeriod());
	    // now that we've translated the period info, create the event
	    return new Event(p.getId(), title, desc, p.getStart(), p.getStartDay(), p.getEnd(), p.getEndDay(), color); 
	}	
	

	public static String getColor(String periodState, String sessionType, boolean defaultPeriod) {
		// w/ only two exceptions, we can simply map session type to a color
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("O", "blue");
		map.put("F", "red");
		map.put("E", "darkpurple");
		// but here come the exceptions:
		String color = "";
		// 1. all pending periods are orange
		if (periodState.equals("P")) {
			color = "orange";
		} else {
			// other wise, it's based on the session type;
			//2.  windows being the other exception
			if (sessionType.equals("W")) {
				color = defaultPeriod ? "green" : "yellow";
			} else {
				color = map.get(sessionType);
			}
		}
		return color;
	}
}
