package edu.nrao.dss.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.data.Window;

public class TestWindow extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
    public void testParseJSON() {
    	
    	// get the input JSON
    	WindowJSON wjson = WindowJSON.getTestWindowJSON_1();
		
		// now parse!
		Window w = Window.parseJSON(wjson);
		
		// test - first simple checks
		String handle = "Low Frequency With No RFI (GBT09A-001) 0";		
		String expLabel = handle + " (0.0/0.0) Not Cmp.";
		assertEquals(handle, w.getHandle());
		assertEquals(0.0, w.getTime_billed());
		assertEquals(false, w.isComplete());
		assertEquals(false, w.isContigious());
		assertEquals(expLabel, w.getLabel());
		assertEquals("2010-01-01", w.getwStartStr());
		assertEquals(3, w.getRanges().length);
		assertEquals("2010-01-01", w.getRanges()[0].getStartStr());
		assertEquals("2010-01-07", w.getRanges()[0].getEndStr());
		// 6 is the correct duration for a date range - but for window ranges
		// the end date is the 'last day' of the window - so it is really a duration of 7.w
		assertEquals(6, w.getRanges()[0].getDays());
		assertEquals(0, w.getPeriods().length);
    }
    public void testParseJSON_2() {
    	
    	// get the input JSON
    	WindowJSON wjson = WindowJSON.getTestWindowJSON_2();
		
		// now parse!
		Window w = Window.parseJSON(wjson);
		
		// test - first simple checks
		String handle = "Low Frequency With No RFI (GBT09A-001) 0";		
		String expLabel = handle + " (0.0/0.0) Not Cmp.";
		assertEquals(handle, w.getHandle());
		assertEquals(0.0, w.getTime_billed());
		assertEquals(false, w.isComplete());
		assertEquals(true, w.isContigious());
		assertEquals(expLabel, w.getLabel());
		assertEquals("2009-06-01", w.getwStartStr());
		assertEquals(1, w.getRanges().length);
		assertEquals("2009-06-01", w.getRanges()[0].getStartStr());
		assertEquals("2009-06-07", w.getRanges()[0].getEndStr());
		// 6 is the correct duration for a date range - but for window ranges
		// the end date is the 'last day' of the window - so it is really a duration of 7.w
		assertEquals(6, w.getRanges()[0].getDays());
		
		// check out the period
		assertEquals(1, w.getPeriods().length);
		Period p = w.getPeriods()[0];
    	// simple checks first
    	assertEquals(handle, p.getHandle());
    	assertEquals(false, p.getMocAck());
    	assertEquals(0.0, p.getBilled());
    	// more complicated stuff
    	assertEquals(0,p.getWindowDaysAhead());
    	assertEquals(6,p.getWindowDaysAfter());
        assertEquals("2009-06-01 12:15:00", p.getStartString());		
    }
    public void testParseJSON_incompleteJSON() {
    	
    	// create the input JSON - start w/ a complete one
    	WindowJSON wjson = WindowJSON.getTestWindowJSON_2();
    	// make it incomplete
    	wjson.put("start", JSONNull.getInstance());
    	wjson.put("end", JSONNull.getInstance());
    	wjson.put("duration", JSONNull.getInstance());
    	wjson.put("ranges", new JSONArray());
    	
		// now parse!
		Window w = Window.parseJSON(wjson);
		
		// now check it
		String handle = "Low Frequency With No RFI (GBT09A-001) 0";		
		String expLabel = handle + " (0.0/0.0) Not Cmp.";
		assertEquals(handle, w.getHandle());
		assertEquals(0.0, w.getTime_billed());
		assertEquals(false, w.isComplete());
		assertEquals(true, w.isContigious());
		assertEquals(expLabel, w.getLabel());
		assertEquals("?", w.getwStartStr());
		assertEquals(0, w.getRanges().length);		
    }	
    	
}
