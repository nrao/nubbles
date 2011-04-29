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
    	Event e = new Event(1, "title", "description", start, start_day, end, end_day,"yellow");
    	
    	// test - trivial stuff
    	assertEquals("title", e.title);
    	assertEquals(1, e.id);
    	assertEquals(start, e.start);
    	assertEquals(start_day, e.start_day);
    	
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
    	Event e = new Event(1, "", description, start, start_day, end, end_day, "green"); //"default period", "W", "S");

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
}
