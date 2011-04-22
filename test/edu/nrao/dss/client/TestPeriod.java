package edu.nrao.dss.client;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.junit.client.GWTTestCase;

public class TestPeriod extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
    public void testParseJSON() {

    	
    	// set up the test JSON period
    	String handle = "Low Frequency With No RFI (GBT09A-001) 0";
    	String session_name = "Low Frequency With No RFI";
//    	JSONObject session = new JSONObject();
//    	PeriodJSON json = new PeriodJSON(1, session, handle, session_name, "2009-06-01", "12:15", "2009-06-01", "17:15", 5.0, "P", "W", true);
//    	// it's windowed, so we need to add on the extras
//    	json.addWindowedInfo("2009-06-01","2009-06-07",true);
    	PeriodJSON json = PeriodJSON.getTestPeriodJSON_1();
    	
    	// use it!
    	Period p = Period.parseJSON(json);
    	
    	// simple checks first
    	assertEquals(handle, p.getHandle());
    	assertEquals(false, p.getMocAck());
    	assertEquals(0.0, p.getBilled());
    	
    	// more complicated stuff
    	assertEquals(0,p.getWindowDaysAhead());
    	assertEquals(6,p.getWindowDaysAfter());
        assertEquals("2009-06-01 12:15:00", p.getStartString());    	
    	
    }

}
