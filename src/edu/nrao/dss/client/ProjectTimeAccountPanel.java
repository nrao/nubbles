package edu.nrao.dss.client;

import com.google.gwt.json.client.JSONObject;

public class ProjectTimeAccountPanel extends TimeAccountingPanel {

	public void setValues(JSONObject json) {
		
	    scheduled.setValue(json.get("scheduled").isNumber().doubleValue());
	    notBillable.setValue(json.get("not_billable").isNumber().doubleValue());	
	    shortNotice.setValue(json.get("short_notice").isNumber().doubleValue());	
	    observed.setValue(json.get("observed").isNumber().doubleValue());	
	    timeBilled.setValue(json.get("time_billed").isNumber().doubleValue());	
	    unaccounted.setValue(json.get("unaccounted_time").isNumber().doubleValue());		    
	    
	    lt.setValue(json.get("lost_time").isNumber().doubleValue());	
	    ltw.setValue(json.get("lost_time_weather").isNumber().doubleValue());	
	    ltr.setValue(json.get("lost_time_rfi").isNumber().doubleValue());	
	    lto.setValue(json.get("lost_time_other").isNumber().doubleValue());
	    
	    os.setValue(json.get("other_session").isNumber().doubleValue());	
	    osw.setValue(json.get("other_session_weather").isNumber().doubleValue());	
	    osr.setValue(json.get("other_session_rfi").isNumber().doubleValue());	
	    oso.setValue(json.get("other_session_other").isNumber().doubleValue());
	    
	    desc.setValue(json.get("notes").isString().stringValue());
	    
	    
	    
	}
}
