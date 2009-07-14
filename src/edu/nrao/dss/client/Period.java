package edu.nrao.dss.client;

import java.util.Calendar;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;

public class Period {
	
    //private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
	
    // TBF
    public static Period parseJSON(JSONObject json) {
    	//try {
            int id = (int) json.get("id").isNumber().doubleValue();
            String desc = "Session"; //int2str(id); TBF: must get session name
            // TBF: start should be a datetime
            String stStr = json.get("start").isString().stringValue();
            //Calendar st = Calendar.getInstance(); //DATE_FORMAT.parse(stStr);
            int dur = 180;
            Period period = new Period(id, desc, stStr, dur);
            return period;
    	//} catch (NullPointerException e) {
        //	return null;
    		
    	//}
    }
    
    public Period(int id, String desc, String st, int dur) {
    	this.id = id;
    	this.sessionDesc = desc;
    	this.start = st; // TBF - should be datetime
    	this.duration = dur;
    }
    
    public String getSessionLabel() {
    	return sessionDesc + "(" + Integer.toString(id) + ")";
    }
    
    public String getStartString() {
    	return start; //start.toString(); // TBF
    }
    
    public int getDuration() {
    	return duration;
    }
    
    public int getId() {
    	return id;
    }
    
    private int      id;
    private String   sessionDesc; // TBF - link to session obj?
    //private Calendar start;
    // TBF - I don't understand how to deal w/ date times
    // in java!!!!
    private String start;
    private int      duration; // minutes
}