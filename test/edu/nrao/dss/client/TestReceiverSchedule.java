package edu.nrao.dss.client;

import com.google.gwt.junit.client.GWTTestCase;

public class TestReceiverSchedule extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";
	}
	
	public void testSimple() {
		assertEquals(0, 0);
	}
	
	public void testJsonToRcvrSchedule() {
		ReceiverSchedule rs = new ReceiverSchedule();
		//rs.jsonToRcvrSchedule(null);
	}

}
