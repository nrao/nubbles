package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.util.dssgwtcal.Appointment;
import edu.nrao.dss.client.util.dssgwtcal.Event;

public class TestEvent extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
    public void testGetAppointments() {
    	// setup 
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date start = dtf.parse("2006-02-02 12:30:00");
    	Date start_day = dtf.parse("2006-02-02 00:00:00");
    	Date end= dtf.parse("2006-02-02 14:30:00");
    	Date end_day = dtf.parse("2006-02-02 00:00:00");
    	Event e = new Event(1, "title", "description", start, start_day, end, end_day, "type", "session_type", "state");
    	
    	// test - trivial stuff
    	assertEquals("title", e.title);
    	assertEquals(1, e.id);
    	assertEquals(start, e.start);
    	assertEquals(start_day, e.start_day);
    	assertEquals("type", e.getType());
    	assertEquals("session_type", e.getSessionType());
    	
    	// test - non-trivial stuff
    	ArrayList<Appointment> appts = e.getAppointments();
    	assertEquals(1, appts.size());
    	Appointment a = appts.get(0);
    	assertEquals("title", a.getTitle());
    	assertEquals("description", a.getDescription());
    	assertEquals(start, a.getStart());
    	assertEquals(end, a.getEnd());
    	// yellow is the color that some unrecognized type, like 'type' will get
    	assertEquals("gwt-appointment gwt-appointment-yellow", a.getStyleName());
    }
    
    public void testGetAppointments_2() {
    	
    	// setup a windowed event, over two days
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date start = dtf.parse("2006-02-02 22:30:00");
    	Date start_day = dtf.parse("2006-02-02 00:00:00");
    	Date start_day_end = dtf.parse("2006-02-02 23:59:00");
    	Date end= dtf.parse("2006-02-03 00:30:00");
    	Date end_day = dtf.parse("2006-02-03 00:00:00");
    	String description = "GBT10A-001";
    	Event e = new Event(1, "", description, start, start_day, end, end_day, "default period", "W", "S");

    	// an event that spans > 1 day, gets multiple appointments
    	ArrayList<Appointment> appts = e.getAppointments();
    	assertEquals(2, appts.size());
    	// first day
    	Appointment a = appts.get(0);
    	assertEquals("", a.getTitle());
    	assertEquals(description + " (Day 1)", a.getDescription());
    	assertEquals(start, a.getStart());
    	assertEquals(start_day_end, a.getEnd());
    	// default periods for a window are green
    	assertEquals("gwt-appointment gwt-appointment-green", a.getStyleName());
    	// second day
    	a = appts.get(1);
    	assertEquals("", a.getTitle());
    	assertEquals(description + " (Day 2)", a.getDescription());
    	assertEquals(end_day, a.getStart());
    	assertEquals(end, a.getEnd());
    	// default periods for a window are green
    	assertEquals("gwt-appointment gwt-appointment-green", a.getStyleName());    	
    	
    }
    
    public void testGetStyleName() {
    	// setup 
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date start = dtf.parse("2006-02-02 12:30:00");
    	Date start_day = dtf.parse("2006-02-02 00:00:00");
    	Date end= dtf.parse("2006-02-02 14:30:00");
    	Date end_day = dtf.parse("2006-02-02 00:00:00");
    	Event e = new Event(1, "title", "description", start, start_day, end, end_day, "type", "session_type", "state");
    	
    	// now cycle through all the different combos and test the style attribute created
    	String[] states = {"D","P","S"};
    	String[] session_types = {"O","W","E","F"};
    	String[] types = {"not windowed!", "default period", "chosen period"};
    	
    	// no matter what, all pending events are orange
    	for (String stype : session_types) {
    		for (String type : types) {
    			e.setState("P");
    			e.setSessionType(stype);
    			e.setType(type);
    			assertEquals(true, styleIsColor("orange", e.getStyleName()));
    		}
    	}
    	
    	// make sure all non-pending open are blue
		for (String state : new String[] {"D", "S"}) {
			e.setState(state);
			e.setSessionType("O");
			e.setType("not windowed!");
			assertEquals(true, styleIsColor("blue", e.getStyleName()));
    	}
		
    	// make sure all non-pending fixed are red
		for (String state : new String[] {"D", "S"}) {
			e.setState(state);
			e.setSessionType("F");
			e.setType("not windowed!");
			assertEquals(true, styleIsColor("red", e.getStyleName()));
    	}	
		
    	// make sure all non-pending electives are darkpurple
		for (String state : new String[] {"D", "S"}) {
			e.setState(state);
			e.setSessionType("E");
			e.setType("not windowed!");
			assertEquals(true, styleIsColor("darkpurple", e.getStyleName()));
    	}  
		
		// make sure all non-pending windowed default periods are green
		for (String state : new String[] {"D", "S"}) {
			e.setState(state);
			e.setSessionType("W");
			e.setType("default period");
			assertEquals(true, styleIsColor("green", e.getStyleName()));
    	}
		
		// make sure all non-pending windowed chosen periods are yellow
		for (String state : new String[] {"D", "S"}) {
			e.setState(state);
			e.setSessionType("W");
			e.setType("chosen period");
			assertEquals(true, styleIsColor("yellow", e.getStyleName()));
    	}
		
    }
    
    private boolean styleIsColor(String color, String style) {
    	return style.contains(color);
    }
}
