package edu.nrao.dss.client;



import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.data.WindowCalendarData;

public class TestWindowCalendarData  extends GWTTestCase {
	
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}

    public void testOne() {
    	DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
    	
    	
    	// get the test input
    	WindowJSON json = WindowJSON.getTestWindowJSON_1();
    	
    	// produce a calendar that is right in the middle of this window's first range: 
    	// 2010-01-01 - 2010-01-07
     	Date dt = DATE_FORMAT.parse("2010-01-02");
    	WindowCalendarData wcd = new WindowCalendarData(dt, 3, json);
    }
}
