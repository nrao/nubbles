package edu.nrao.dss.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.data.PeriodEventAdapter;
import edu.nrao.dss.client.util.dssgwtcal.Event;

public class TestPeriodEventAdapter  extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
	public void testPeriodToCalendarEvent() {
		// setup 
		Period p = Period.parseJSON(PeriodJSON.getTestPeriodJSON_1());
		String name = p.getSession();
		
		Event e = PeriodEventAdapter.fromPeriod(p);
		assertEquals("", e.title);
		assertEquals(name + " +0/-6", e.description);
		assertEquals(p.getStart(), e.start);
		assertEquals(p.getStartDay(), e.start_day);
		assertEquals("orange", e.getColor());
	}	

    public void testGetColor() {
    	// setup 
    	String[] states = {"D","P","S"};
    	String[] sessionTypes = {"O","W","E","F"};
    	
    	// no matter what, all pending events are orange
    	for (String stype : sessionTypes) {
    		for (String type : sessionTypes) {
    			// Note: defaultPeriod == true for non-windowed periods is a pathological case
    			// but test it anyways.
    			assertEquals("orange", PeriodEventAdapter.getColor("P", stype, true));
    			assertEquals("orange", PeriodEventAdapter.getColor("P", stype, false));
    		}
    	}
    	
    	// make sure all non-pending open are blue
		for (String state : new String[] {"D", "S"}) {
			assertEquals("blue",       PeriodEventAdapter.getColor(state, "O", false));
			assertEquals("red",        PeriodEventAdapter.getColor(state, "F", false));
			assertEquals("darkpurple", PeriodEventAdapter.getColor(state, "E", false));
			assertEquals("green",      PeriodEventAdapter.getColor(state, "W", true));
			assertEquals("yellow",     PeriodEventAdapter.getColor(state, "W", false));
    	}
    }
}
