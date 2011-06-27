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

import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.util.dssgwtcal.Event;

public class TestSchedule extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
    public void testJsonToPeriods() {
    	// setup
    	JSONArray psJson = new JSONArray();
    	psJson.set(0, PeriodJSON.getTestPeriodJSON_1());
    	JSONObject json = new JSONObject();
    	json.put("periods", psJson);
    	Schedule sch = new Schedule();
    	
    	// test
    	List<Period> periods = sch.jsonToPeriods(json);
    	assertEquals(1, periods.size());
    	Period p = periods.get(0);
    	String handle = "Low Frequency With No RFI (GBT09A-001) 0";
    	assertEquals(handle, p.getHandle());
    	assertEquals(false, p.getMocAck());
    	assertEquals(0.0, p.getBilled());
    	assertEquals(0,p.getWindowDaysAhead());
    	assertEquals(6,p.getWindowDaysAfter());
        assertEquals("2009-06-01 12:15:00", p.getStartString());
    }
    public void testJsonToPeriods_2() {
    	// setup
    	JSONArray psJson = new JSONArray();
    	psJson.set(0, PeriodJSON.getTestPeriodJSON_1());
    	psJson.set(1, PeriodJSON.getTestPeriodJSON_2());
    	// now add one that shouldn't show up
    	JSONObject pj = PeriodJSON.getTestPeriodJSON_1();
    	pj.put("duration", new JSONNumber(1.0));
    	pj.put("state", new JSONString("D"));
    	pj.put("id", new JSONNumber(3));
    	psJson.set(2, pj);
    	JSONObject json = new JSONObject();
    	json.put("periods", psJson);
    	Schedule sch = new Schedule();
    	
    	// test
    	List<Period> periods = sch.jsonToPeriods(json);
    	assertEquals(2, periods.size());
    	assertEquals(1, periods.get(0).getId());
    	assertEquals(2, periods.get(1).getId());
    	
    }
}
