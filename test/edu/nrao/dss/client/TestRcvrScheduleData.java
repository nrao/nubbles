package edu.nrao.dss.client;


import org.junit.After;
import org.junit.Before;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.data.RcvrScheduleData;
import edu.nrao.dss.client.data.RcvrScheduleDate;


public class TestRcvrScheduleData extends GWTTestCase {
	
	// HACK, WTF: why do I have to role my own?
	private void assertStringArraysEqual(String[] exp, String[] values) {
		for (int i=0; i<exp.length; i++) {
		    assertEquals(exp[i], values[i]);
		}
	}
	
	public void testParseJSON() {
		//setThisUp();
		RcvrScheduleJSON json = new RcvrScheduleJSON();
		RcvrScheduleData rsd = RcvrScheduleData.parseJSON(json);
		
		// simple checks
		assertEquals(json.rx.length, rsd.getReceiverNames().length);
		assertStringArraysEqual(json.rx, rsd.getReceiverNames());
		assertEquals(RcvrScheduleData.DATETIME_FORMAT.parse(json.maintenanceDay), rsd.getMaintenanceDays()[0]);
		assertStringArraysEqual(new String[] {"04/07/2009"}, rsd.getMaintenanceDayStrsBetween(0));
		assertStringArraysEqual(new String[] {}, rsd.getMaintenanceDayStrsBetween(1));
		
		// date objects are more complicated
		RcvrScheduleDate[] days = rsd.getDays();
		assertEquals(2, days.length);
		
		assertEquals(RcvrScheduleData.DATE_FORMAT.parse("04/06/2009"), days[0].getDate());
		assertStringArraysEqual(new String[] {"RRI", "342", "450"}, days[0].getUp());
		assertStringArraysEqual(new String[] {}, days[0].getDown());
		assertStringArraysEqual(new String[] {"RRI", "342", "450"}, days[0].getAvailableRx());
		
		assertEquals(RcvrScheduleData.DATE_FORMAT.parse("04/11/2009"), days[1].getDate());
		assertStringArraysEqual(new String[] {"600"}, days[1].getUp());
		assertStringArraysEqual(new String[] {"RRI"}, days[1].getDown());
		assertStringArraysEqual(new String[] {"342", "450", "600"}, days[1].getAvailableRx());
		
		// these should be in TestRcvrScheduleDate:
		assertEquals("RRI, 342, 450", days[0].getUpStr());
		assertEquals("",              days[0].getDownStr());
		assertEquals(false, days[0].isRcvrAvailable("L"));
		assertEquals(true,  days[0].isRcvrAvailable("RRI"));
		
	}

	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";
	}
}
