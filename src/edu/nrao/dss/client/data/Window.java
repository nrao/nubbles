package edu.nrao.dss.client.data;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.Period;

// This class represents a translation of the window JSON received by the server.
// Note that we parse and save only the data that is needed.

public class Window {
	//DateTimeFormat.getFormat("yyyy-MM-dd")
	public static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
	
	private String handle;
	private Double total_time;
	private Double time_billed;
	//private Double time_remaining;
	private boolean complete;
	private boolean contigious;
	private Date wStart;
	private Date wEnd;
	private int duration;
	private DateRange[] ranges;
	private Period[] periods;
	
	
	public static Window parseJSON(JSONObject json) {
	    Window w = new Window();
	    
	    w.setHandle(json.get("handle").isString().stringValue());
	    w.setTotal_time(json.get("total_time").isNumber().doubleValue());
	    w.setTime_billed(json.get("time_billed").isNumber().doubleValue());
	    w.setComplete(json.get("complete").isBoolean().booleanValue());
	    
		// are there any gaps in the window (is this window non-contigious?)
		w.setContigious(json.get("contigious").isBoolean().booleanValue());	    

	    
	    // now gather up info about the window ranges and periods:
	    
	    // when does the window start and stop (not taking into account gaps)?
	    String wstartStr = json.get("start").isString().stringValue();
	    String wstopStr  = json.get("end").isString().stringValue();
		w.setwStart(DATE_FORMAT.parse(wstartStr));
		w.setwEnd(DATE_FORMAT.parse(wstopStr));

		w.parseRangesJSON(json.get("ranges").isArray());
		
		w.parsePeriodJSON(json.get("periods").isArray());
		
	    return w;
	}

	public void parseRangesJSON(JSONArray rangesJSON) {
	    ranges = new DateRange[rangesJSON.size()];
	    for (int i=0; i<rangesJSON.size(); i++) {
	    	ranges[i] = DateRange.parseJSON(rangesJSON.get(i).isObject());
	    }
	}
	
	public void parsePeriodJSON(JSONArray periodsJSON) {
	    periods = new Period[periodsJSON.size()];
	    for (int i=0; i<periodsJSON.size(); i++) {
	    	periods[i] = Period.parseJSON(periodsJSON.get(i).isObject());
	    }
	}
	
    public String getLabel() {
	    String cmpStr = complete ? "Cmp." : "Not Cmp.";
	    // Ex: GBT08A-001-01 (GBT08A-001) (8.0/8.0) Cmp."
	    return handle + " (" + total_time.toString() + "/" + time_billed.toString() + ") " + cmpStr;    	
    }

    public boolean isDateInOverallWindow(Date dt) {
		return ((dt.getTime() >= wStart.getTime()) && (dt.getTime() <= wEnd.getTime()));
    }
    
    // this is a simple question for a contigious window - but one with multiple ranges is another case
	public boolean isDateInWindow(Date dt) { 
		boolean partOfWindow = isDateInOverallWindow(dt);
    	if (contigious == false && partOfWindow == true) {
			// we aren't in a gap if we fall into just ONE of the ranges
	    	partOfWindow = false;
	    	for (DateRange r : getRanges()) {
			    //if (isDateInWindow(dates[j], calRangeDates[k][0], calRangeDates[k][1]) == true) {
	    		if (r.isInRange(dt)) {
				   partOfWindow = true;
	    		}   
			}
		}
    	return partOfWindow;
	}
	
	public void setHandle(String handle) {
		this.handle = handle;
	}


	public String getHandle() {
		return handle;
	}


	public void setTotal_time(Double total_time) {
		this.total_time = total_time;
	}


	public Double getTotal_time() {
		return total_time;
	}


	public void setTime_billed(Double time_billed) {
		this.time_billed = time_billed;
	}


	public Double getTime_billed() {
		return time_billed;
	}


	public void setComplete(boolean complete) {
		this.complete = complete;
	}


	public boolean isComplete() {
		return complete;
	}


	public void setwStart(Date wStart) {
		this.wStart = wStart;
	}


	public Date getwStart() {
		return wStart;
	}

    public String getwStartStr() {
    	return 	DATE_FORMAT.format(wStart);
    }
    
    public String getwEndStr() {
    	return 	DATE_FORMAT.format(wEnd);
    }
    
    
	public void setContigious(boolean contigious) {
		this.contigious = contigious;
	}


	public boolean isContigious() {
		return contigious;
	}


	public void setwEnd(Date wEnd) {
		this.wEnd = wEnd;
	}


	public Date getwEnd() {
		return wEnd;
	}


	public void setDuration(int duration) {
		this.duration = duration;
	}


	public int getDuration() {
		return duration;
	}


	public void setPeriods(Period[] periods) {
		this.periods = periods;
	}


	public Period[] getPeriods() {
		return periods;
	}
	
	public void setRanges(DateRange[] ranges) {
		this.ranges = ranges;
	}


	public DateRange[] getRanges() {
		return ranges;
	}	
}