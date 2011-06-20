// Copyright (C) 2011 Associated Universities, Inc. Washington DC, USA.
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
// 
// Correspondence concerning GBT software should be addressed as follows:
//       GBT Operations
//       National Radio Astronomy Observatory
//       P. O. Box 2
//       Green Bank, WV 24944-0002 USA

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
    	assertEquals("P", p.getState());
    	assertEquals(false, p.isDeleted());
    	
    	// more complicated stuff
    	assertEquals(0,p.getWindowDaysAhead());
    	assertEquals(6,p.getWindowDaysAfter());
        assertEquals("2009-06-01 12:15:00", p.getStartString());    	
    	
    }

}
