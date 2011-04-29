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
    	String[] session_types = {"O","W","E","F"};
    	String nw = "not windowed!";
    	String[] types = {nw, "default period", "chosen period"};
    	
    	// no matter what, all pending events are orange
    	for (String stype : session_types) {
    		for (String type : types) {
    			assertEquals("orange", PeriodEventAdapter.getColor(type, stype, "P"));
    		}
    	}
    	
    	// make sure all non-pending open are blue
		for (String state : new String[] {"D", "S"}) {
			assertEquals("blue", PeriodEventAdapter.getColor(nw, "O", state));
			assertEquals("red", PeriodEventAdapter.getColor(nw, "F", state));
			assertEquals("darkpurple", PeriodEventAdapter.getColor(nw, "E", state));
			assertEquals("green", PeriodEventAdapter.getColor("default period", "W", state));
			assertEquals("yellow", PeriodEventAdapter.getColor("chosen period", "W", state));
    	}
    }
}
