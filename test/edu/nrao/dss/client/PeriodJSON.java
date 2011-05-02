package edu.nrao.dss.client;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

//{'wstart': '2009-06-01'
//, 'time_billed': 0.0
//, 'observed': 0.0
//, 'lst': '23:34:07'
//, 'session': {}
//, 'duration': 5.0
//, 'lost_time_bill_project': 0.0
//, 'id': 1
//, 'wdefault': True
//, 'moc_ack': False
//, 'receivers': ''
//, 'forecast': None
//, 'state': u'P'
//, 'lost_time_weather': 0.0
//, 'other_session_other': 0.0
//, 'scheduled': 0.0
//, 'handle': u'Low Frequency With No RFI (GBT09A-001) 0', 'description': ''
//, 'end_date': '2009-06-01'
//, 'windowed': True
//, 'date': '2009-06-01'
//, 'lost_time_rfi': 0.0
//, 'unaccounted_time': 0.0
//, 'other_session_rfi': 0.0
//, 'other_session': 0.0
//, 'wend': '2009-06-07'
//, 'other_session_weather': 0.0
//, 'sscore': None
//, 'lost_time_other': 0.0
//, 'end_time': '17:15'
//, 'time': '12:15'
//, 'accounting_id': 1
//, 'short_notice': 0.0
//, 'session_name': u'Low Frequency With No RFI'
//, 'backup': False
//, 'cscore': 0.0
//, 'stype': u'W'
//, 'lost_time': 0.0}

public class PeriodJSON extends TestJSON {
	
	// there are so many fields to this, first just do the most obvious ones,
	// and initialize the rest (like accounting)
	public PeriodJSON(int id
			        , JSONObject session
			        , String handle
			        , String session_name
			        , String date
			        , String time
			        , String end_date
			        , String end_time
			        , Double duration
			        , String state
			        , String stype
			        , boolean windowed
//			        , boolean moc_ack
//			        , boolean backup
//			        , Double sscore
//			        , Double cscore
//			        , String accntDesc
			        ) {
	    add("id", id);
	    add("session", session);
	    add("handle", handle);
	    add("session_name", session_name);
	    add("date", date);
	    add("time", time);
	    add("end_date", date);
	    add("end_time", time);
        add("duration", duration);
        add("state", state);
        add("stype", stype);
        add("windowed", windowed);
        
        // now init these fields that don't matter much and we can always change later
        add("moc_ack", false); //moc_ack);
        add("backup", false); //backup);
        add("sscore", 0.0); //sscore);
        add("cscore", 0.0); //cscore);
        add("description", ""); //accntDesc);
	    initTimeAccounting();
	}
	
	private void initTimeAccounting() {
		String[] fields = {"lost_time", "lost_time_other", "lost_time_rfi", "lost_time_weather"
				          ,"other_session", "other_session_other", "other_session_rfi", "other_session_weather"
				          ,"scheduled", "short_notice", "not_billable", "unaccounted_time", "lost_time_bill_project"
				          ,"time_billed", "observed"};
		for (int i=0; i<fields.length; i++) {
			this.put(fields[i], new JSONNumber(0.0));
		}
	}
	
	public void addWindowedInfo(String wstart, String wend, boolean wdefault) {
		add("wstart", wstart);
		add("wend", wend);
		add("wdefault", wdefault);
	}
	
	static PeriodJSON getTestPeriodJSON_1() {
    	String handle = "Low Frequency With No RFI (GBT09A-001) 0";
    	String session_name = "Low Frequency With No RFI";
    	JSONObject session = new JSONObject();
    	PeriodJSON json = new PeriodJSON(1, session, handle, session_name, "2009-06-01", "12:15", "2009-06-01", "17:15", 5.0, "P", "W", true);
    	// it's windowed, so we need to add on the extras
    	json.addWindowedInfo("2009-06-01","2009-06-07",true);	
    	return json;
	}
	static PeriodJSON getTestPeriodJSON_2() {
    	String handle = "GBT10B (GBT10B-001) 1";
    	String session_name = "GBT10B-001";
    	JSONObject session = new JSONObject();
    	PeriodJSON json = new PeriodJSON(2, session, handle, session_name, "2009-06-02", "14:00", "2009-06-02", "15:00", 1.0, "S", "O", false);
    	return json;
	}
}
