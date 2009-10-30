package edu.nrao.dss.client;

import com.google.gwt.json.client.JSONObject;

public class SessionTimeAccountPanel extends TimeAccountingPanel {
	
	public void setValues(JSONObject json) {

		// these are all read-only, so there's no change in state; we can just set them		
	    scheduled.setValue(json.get("scheduled").isNumber().doubleValue());
	    observed.setValue(json.get("observed").isNumber().doubleValue());	
	    timeBilled.setValue(json.get("time_billed").isNumber().doubleValue());	
	    unaccounted.setValue(json.get("unaccounted_time").isNumber().doubleValue());	
	    notBillable.setValue(json.get("not_billable").isNumber().doubleValue());	
	    shortNotice.setValue(json.get("short_notice").isNumber().doubleValue());
	    
	    lt.setValue(json.get("lost_time").isNumber().doubleValue());	
	    ltw.setValue(json.get("lost_time_weather").isNumber().doubleValue());	
	    ltr.setValue(json.get("lost_time_rfi").isNumber().doubleValue());	
	    lto.setValue(json.get("lost_time_other").isNumber().doubleValue());
	    
	    os.setValue(json.get("other_session").isNumber().doubleValue());	
	    osw.setValue(json.get("other_session_weather").isNumber().doubleValue());	
	    osr.setValue(json.get("other_session_rfi").isNumber().doubleValue());	
	    oso.setValue(json.get("other_session_other").isNumber().doubleValue());	
	    
	    // Description is NOT read-only, so must set state
	    setDescription(json.get("notes").isString().stringValue());	    

	}

}
