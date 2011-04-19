package edu.nrao.dss.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.junit.client.GWTTestCase;

public class TestReceiverSchedule extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";
	}
	
	public void testJsonToRcvrSchedule() {
		ReceiverSchedule rs = new ReceiverSchedule();
        RcvrScheduleJSON json = new RcvrScheduleJSON();		
		rs.jsonToRcvrSchedule(json);
	}

}
