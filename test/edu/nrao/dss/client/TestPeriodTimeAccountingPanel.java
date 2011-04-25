package edu.nrao.dss.client;

import java.util.HashMap;

import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.widget.PeriodTimeAccountPanel;

public class TestPeriodTimeAccountingPanel extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";	
	}
	
	public void testOne() {
		// inputs
		// create a period and save off it's time accounting initial state
		PeriodJSON json = PeriodJSON.getTestPeriodJSON_1();
		Period p = Period.parseJSON(json);
		HashMap<String, Object> pMap = p.toHashMap();
		String taStr = pMap.toString();
		// create the widget we want to test
		PeriodTimeAccountPanel pa = new PeriodTimeAccountPanel();
		pa.setPeriod(p);
		
		// test that the unchanged widgets give back unchanged results
		pa.widgetsToPeriod();
		Period p2 = pa.getPeriod();
		String ta2Str = p2.toHashMap().toString();
		assertEquals(taStr, ta2Str);
	}

}
