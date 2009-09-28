package edu.nrao.dss.client;

// WTF: I can't fucking use this!!!
//import java.util.GregorianCalendar;
import java.util.Date;


import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.util.TimeUtils;

public class Period {
	
    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormat DAY_FORMAT  = DateTimeFormat.getFormat("yyyy-MM-dd");

    public static Period parseJSON(JSONObject json) {
    	
    	// interpret the fields that are part of the period
        int id = (int) json.get("id").isNumber().doubleValue();
        String handle = (String) json.get("handle").isString().stringValue();
        String date = json.get("date").isString().stringValue();
        String time = json.get("time").isString().stringValue();
        Date st = DATE_FORMAT.parse(date + " " + time + ":00");
        Date day = DAY_FORMAT.parse(date);
        //Date st = DATE_FORMAT.parse(json.get("start").isString().stringValue());
        int dur = (int) hours2minutes(json.get("duration").isNumber().doubleValue());
        
        Period period = new Period(id, handle, st, dur, day, time);
        
        // now set the fields associated with time accounting
        period.setNot_billable(json.get("not_billable").isNumber().doubleValue());
        period.setOther_session_weather(json.get("other_session_weather").isNumber().doubleValue());
        period.setOther_session_rfi(json.get("other_session_rfi").isNumber().doubleValue());
        period.setOther_session_other(json.get("other_session_other").isNumber().doubleValue());
        period.setLost_time_weather(json.get("lost_time_weather").isNumber().doubleValue());
        period.setLost_time_rfi(json.get("lost_time_rfi").isNumber().doubleValue());
        period.setLost_time_other(json.get("lost_time_other").isNumber().doubleValue());
        
        return period;
    }

    private static double hours2minutes(double hours) {
    	return hours * 60.0;
    }
    public Period(int id, String handle, Date start, int dur, Date start_day, String start_time ) {
    	this.id = id;
    	this.handle = handle;
    	this.start = start; 
    	this.duration = dur;
    	this.start_day = start_day;
    	this.start_time = start_time;
    }
    
    public String getHandle() {
    	return handle;
    }
    
    public Date getStart() {
    	return start;
    }
    
    public Date getEnd() {
        // WTF: time arithmetic should not be this hard!
    	// WTF: can't use GregorianCalendar in GWT!
//    	GregorianCalendar cal = new GregorianCalendar(1872, GregorianCalendar.OCTOBER, 2); //GregorianCalendar.getInstance();
//    	cal.setTime(start);
//    	int durMins = (int) ((int) duration * 60.0);
//    	cal.add(GregorianCalendar.MINUTE, durMins);
//    	return cal.getTime();
    	long startSecs = start.getTime();
    	// add the duration (in minutes) to this time in milli-seconds
    	long endSecs = (long) (startSecs + (duration * 60.0 * 1000.0));
    	return new Date(endSecs);
    }
    
    public String getStartString() {
    	return start.toString(); // TBF
    }
    
    public int getDuration() {
    	return duration;
    }
    
    public String getDurationString() {
    	return TimeUtils.min2sex(duration);
    }
    public int getId() {
    	return id;
    }
    
    public String getStartTime() {
    	return start_time;
    }
    
    public int getStartHour() {
    	return Integer.parseInt(start_time.split(":")[0]);
    }

    public int getStartMinute() {
    	return Integer.parseInt(start_time.split(":")[1]);
    }
    
    public Date getStartDay() {
    	return start_day;
    }
    
    public void setNot_billable(double not_billable) {
		this.not_billable = not_billable;
	}

	public double getNot_billable() {
		return not_billable;
	}

	public void setLost_time_weather(double lost_time_weather) {
		this.lost_time_weather = lost_time_weather;
	}

	public double getLost_time_weather() {
		return lost_time_weather;
	}

	public void setLost_time_rfi(double lost_time_rfi) {
		this.lost_time_rfi = lost_time_rfi;
	}

	public double getLost_time_rfi() {
		return lost_time_rfi;
	}

	public void setLost_time_other(double lost_time_other) {
		this.lost_time_other = lost_time_other;
	}

	public double getLost_time_other() {
		return lost_time_other;
	}

	public void setOther_session_weather(double other_session_weather) {
		this.other_session_weather = other_session_weather;
	}

	public double getOther_session_weather() {
		return other_session_weather;
	}

	public void setOther_session_rfi(double other_session_rfi) {
		this.other_session_rfi = other_session_rfi;
	}

	public double getOther_session_rfi() {
		return other_session_rfi;
	}

	public void setOther_session_other(double other_session_other) {
		this.other_session_other = other_session_other;
	}

	public double getOther_session_other() {
		return other_session_other;
	}

	// traditional period attributes
    private int      id;
    private String   handle; 
    private Date     start;
    private int      duration; // minutes
    private Date     start_day;
    private String   start_time;
    // time accounting (all in Hours)
    private double   not_billable;
    private double   lost_time_weather;
    private double   lost_time_rfi;
    private double   lost_time_other;
    private double   other_session_weather;
    private double   other_session_rfi;
    private double   other_session_other;
    
}