package edu.nrao.dss.client.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class RcvrScheduleData {
	
	// ex: 04/11/2009
    public static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("MM/dd/yyyy");
    // ex: 2009-04-07 12:00:00
    public static final DateTimeFormat DATETIME_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    
	private String[] receiverNames;
	private Date[] maintenanceDays;
	private RcvrScheduleDate[] days;
	
	public static RcvrScheduleData parseJSON(JSONObject json) {
		
		// create a blank data structure
	    RcvrScheduleData rs = new RcvrScheduleData();	
		
	    // parse the json into this structure
		rs.setReceiverNames(rs.json2rcvrs(json.get("receivers").isArray()));
		rs.setMaintenanceDays(rs.json2maintenanceDates(json.get("maintenance").isArray()));
		rs.setDays(rs.json2rcvrSchdDates(json));
		
		return rs;
	}
	
	private String[] json2rcvrs(JSONArray rcvrs) {
		int numRcvrs = rcvrs.size();
		String [] rx = new String[numRcvrs];
		String rcvr;
		for (int i = 0; i < numRcvrs; i++) {
			rcvr = rcvrs.get(i).isString().stringValue();
			rx[i] = rcvr;
		}
		return rx;
	}
	
	private Date[] json2maintenanceDates(JSONArray days) {
		int numDays = days.size();
		Date [] dates = new Date[numDays];
		String dayStr;
		for (int i = 0; i < numDays; i++) {
			dayStr = days.get(i).isString().stringValue();
			// ex: 2009-04-07 12:00:00
			dates[i] = DATETIME_FORMAT.parse(dayStr);
		}
		return dates;
	}	
	
	private RcvrScheduleDate[] json2rcvrSchdDates(JSONObject json) {

		// this method isn't as simple as I'd like due to the odd structure of the JSON.
		
		// get the diff schedule
		JSONArray diff = json.get("diff").isArray();
		
		// get the rx schedule
		JSONObject schedule = json.get("schedule").isObject();

		// the entries in this dictionary are date strings: we need to turn them into
		// Date objects so we can sort them, then use them as keys to get the
		// rcvr schedule in the correct order.
		TreeSet<Date> dates = new TreeSet<Date>(); //schedule.keySet());
		for (String dateStr : schedule.keySet()) {
		    Date dt = DATE_FORMAT.parse(dateStr);
		    dates.add(dt);
		}

	    // allocate the data strucutre we use	
		RcvrScheduleDate[] rxScheduleDates= new RcvrScheduleDate[dates.size()];
		
		int rsi = 0;
		for (Date date : dates) {
			// for each date, allocate a new object to store all this data
			RcvrScheduleDate rsd = new RcvrScheduleDate();
			
			// set the date
			String dtStr = DATE_FORMAT.format(date);
			//rxScheduleDates[rsi].setDate(date);
			//RcvrScheduleDate rsd = rxScheduleDates[rsi];
			rsd.setDate(date);
			
			// use one part of the json to set the available receivers for this date
			rsd.parseRxJson(schedule.get(dtStr).isArray());
			
            // use the diff entry for this date to set what rx's are up and down
			for (int i = 0; i < diff.size(); i++) {
			    JSONObject diffObj = diff.get(i).isObject();
			    String diffDayStr = diffObj.get("day").isString().stringValue();
			    if (diffDayStr.compareTo(dtStr) == 0) {
			    	rsd.parseDiffJson(diffObj);
			    }
			}
			rxScheduleDates[rsi] = rsd;
			rsi++;
		}
            
        return rxScheduleDates;			
	}
	
	public void setReceiverNames(String[] receiverNames) {
		this.receiverNames = receiverNames;
	}
	public String[] getReceiverNames() {
		return receiverNames;
	}
	public void setMaintenanceDays(Date[] maintenanceDays) {
		this.maintenanceDays = maintenanceDays;
	}
	public Date[] getMaintenanceDays() {
		return maintenanceDays;
	}
	public String[] getMaintenanceDayStrs() {
		return dates2strs(maintenanceDays);
	}
    
    private Date[] getMaintenanceDaysBetween(int dayNum) {
    	
    	// if this is the last day, ignore maintenance days
    	if (dayNum+1 >= days.length) {
    		return new Date[0];
    	}
    	
    	// what are the dates we're searching between?
    	Date start = days[dayNum].getDate();
    	Date end   = days[dayNum+1].getDate();
    	
    	// keep a list of all the maintenance days between these dates
    	ArrayList<Date> mdays = new ArrayList<Date>();
    	for (int i = 0; i < maintenanceDays.length; i++) {
    		Date mday = maintenanceDays[i];
    		if (mday.after(start) && mday.before(end)) {
    		    mdays.add(mday);	
    		}
    	}

    	Date[] mds = new Date[mdays.size()];
    	for (int i = 0; i < mdays.size(); i++) {
    	    mds[i] = mdays.get(i);
    	}
    	return mds;
    }
    
    public String[] getMaintenanceDayStrsBetween(int dayNum) {
    	Date[] mdays = getMaintenanceDaysBetween(dayNum);
    	// now that we know what the maintenance days are, we can
    	// convert them to strings
    	return dates2strs(mdays); 
    }
    
    private String[] dates2strs(Date[] dates) {
    	String[] strs = new String[dates.length];
    	for (int i = 0; i < dates.length; i++) {
    		strs[i] = DATE_FORMAT.format(dates[i]);
    	}
    	return strs;
    }
    
	public void setDays(RcvrScheduleDate[] days) {
		this.days = days;
	}
	public RcvrScheduleDate[] getDays() {
		return days;
	}
}
