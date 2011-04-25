package edu.nrao.dss.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.data.WindowCalendarData;

public class TestWindowCalTable extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
	
	public void testGetHeaders() {
		// setup
		DateTimeFormat dtFormat = DateTimeFormat.getFormat("yyyy-MM-dd");   
		Date dt = dtFormat.parse("2010-06-01");
		WindowCalTable calTable = new WindowCalTable();
		String[] hdrs = calTable.getHeaders(dt, 3);
		// test
		assertEquals(6, hdrs.length);
		assertEquals("Session (total/billed) Complete?", hdrs[0]);
		assertEquals("Start", hdrs[1]);
		assertEquals("2010-06-01", hdrs[2]);
		assertEquals("2010-06-02", hdrs[3]);
		assertEquals("2010-06-03", hdrs[4]);
		assertEquals("End", hdrs[5]);
	}
	
	public void testRenderCalendar() {
		// setup
		DateTimeFormat dtFormat = DateTimeFormat.getFormat("yyyy-MM-dd");   
		Date dt = dtFormat.parse("2009-06-04");
    	WindowJSON json = WindowJSON.getTestWindowJSON_2();
    	int numDays = 6;
    	WindowCalendarData data = new WindowCalendarData(dt, numDays, json);
		WindowCalTable calTable = new WindowCalTable();
		calTable.renderCalendar(new WindowCalendarData[]{data}, dt, numDays);
		
		// test
		assertEquals(2, calTable.getRowCount());
		assertEquals(numDays+3, calTable.getCellCount(0));
		assertEquals(numDays+3, calTable.getCellCount(1));
		// headers row
		assertEquals("Session (total/billed) Complete?", calTable.getHTML(0, 0));
		assertEquals("Start", calTable.getHTML(0, 1));
		assertEquals("2009-06-04", calTable.getHTML(0,2));
		assertEquals("2009-06-05", calTable.getHTML(0,3));		
		assertEquals("End", calTable.getHTML(0, numDays+2));
		// one window - just one row to test
		// first col - window label
		String handle = "Low Frequency With No RFI (GBT09A-001) 0";		
		String expLabel = handle + " (0.0/0.0) Not Cmp.";
		assertEquals(expLabel, calTable.getHTML(1, 0));
		assertEquals("gwt-RcvrSchdGrid-off", calTable.getCellFormatter().getStyleName(1, 0));
		
		// second col - 'Start'
		String startStr = "Starts: 2009-06-01, Default (P) (0.0) on 2009-06-01";		
		assertEquals(startStr, calTable.getHTML(1, 1));
		assertEquals("gwt-RcvrSchdGrid-on", calTable.getCellFormatter().getStyleName(1, 1));
		
		for (int col=2; col < 6; col++) {
			assertEquals("", calTable.getHTML(1,col));
			assertEquals("gwt-RcvrSchdGrid-on", calTable.getCellFormatter().getStyleName(1, col));
			
		}
		for (int col=6; col < numDays + 3; col++) {
			assertEquals("", calTable.getHTML(1, col));		
			assertEquals("gwt-RcvrSchdGrid-off", calTable.getCellFormatter().getStyleName(1, col));
		}

	}
}
