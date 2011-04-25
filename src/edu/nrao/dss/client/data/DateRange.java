package edu.nrao.dss.client.data;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;

// simple utility class for handling date ranges

public class DateRange {
	
	public static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");	
    private Date start;
    private Date end;
    private int msPerDay = 1000 * 60 * 60 * 24;
    
    static DateRange parseJSON(JSONObject json) {
        Date start, end;
        start = DATE_FORMAT.parse(json.get("start").isString().stringValue());
        end   = DATE_FORMAT.parse(json.get("end").isString().stringValue());
        return new DateRange(start, end);
    }
    
    public DateRange(Date start, Date end) {
    	this.setStart(start);
    	this.setEnd(end);
    }

    public int getDays() {
    	return (int) ((end.getTime() - start.getTime()) / msPerDay);
    }
    
    public boolean isInRange(Date dt) {
        return ((dt.getTime() >= start.getTime()) && (dt.getTime() <= end.getTime()));
    }
    
	public void setStart(Date start) {
		this.start = start;
	}

	public Date getStart() {
		return start;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Date getEnd() {
		return end;
	}
	
	public String getStartStr() {
		return DATE_FORMAT.format(start);
	}
	
	public String getEndStr() {
		return DATE_FORMAT.format(end);
	}	

}
