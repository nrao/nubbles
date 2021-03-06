// Copyright (C) 2011 Associated Universities, Inc. Washington DC, USA.
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
// 
// Correspondence concerning GBT software should be addressed as follows:
//       GBT Operations
//       National Radio Astronomy Observatory
//       P. O. Box 2
//       Green Bank, WV 24944-0002 USA

package edu.nrao.dss.client.data;

import java.util.Date;

import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.Period;

// an instance of this class represents one row in the Window Calendar.
// it consumes the JSON for a single window, creating a window object
// and using that object for configuring the display info.
// That display info is held in arrays: an element for each cell (day) in the row

// One row looks something like this:
// For given time range of: start = 2010/1/1, days = 2:
// where Start & End give info on the window that goes outside the given time range
// | Window Info | Start | 2010/1/1 | 2010/1/2 | End |

public class WindowCalendarData {
	
	private Window window;
	private Date start;
	private int days;
	private int calendarSize;
	private Date end;
	private Date[] dates;
	long msPerDay = 1000 * 60 * 60 * 24;
	private boolean on[];
	private String info[];
	
	public WindowCalendarData(Date start, int days, JSONObject json) {
		this.start = start;
		this.days  = days;
		computeDates();
		// Calendar displays number of days, plus a 'start' and 'end' column
		calendarSize = days + 2;
		on = new boolean[calendarSize ];
		info = new String[calendarSize];
		window = Window.parseJSON(json);
		computeDisplayInfo();
	}

	private void computeDates() {
		long offset;
		end = new Date(start.getTime() + (((long) days) * msPerDay));
		dates = new Date[days];
        for (int i=0; i<days; i++) {		
		    offset = ((long) i) * msPerDay;
		    dates[i] = new Date(start.getTime() + offset);
        }    
	}
	
	// use the window object for figuring out what to display for
	// each day in the calendar
	private void computeDisplayInfo() {
		
		Date wstart = window.getwStart();
		Date wstop = window.getwEnd();
        int endIndex = calendarSize - 1;
        
		// the second & last days of the window contain info on what happens to this window
		// outside of the calendar's range.  
		// The Start Column:
		String value, sep;
		if (start.after(wstart) || start.equals(wstart)) {
			// we're in the window!
			on[0] = true; 
			value = "";
			// window ranges?
			for (DateRange r : window.getRanges()) {
				sep = value.compareTo("") == 0 ? "" : ", ";
				if (start.after(r.getStart())) {
					if (start.after(r.getEnd())) {
						value += sep + r.getStartStr() + " - " + r.getEndStr();
					} else {
							value += sep + "Starts: " + r.getStartStr();
					}
				}
			}
    		// periods?
			for (Period p : window.getPeriods()) {
    			if (p.getStart().before(start)) {
    				value += ", " + calPeriodToText(p) + " on "  + p.getStartDayString();
    			}
    		}
			// this is what get's displayed!
			info[0] = value;
		} else {
			on[0] = false;
			info[0] = "";
		}
		
		// The End Column:
		if (end.before(wstop) || end.equals(wstop)) {
			on[endIndex] = true; // in the window!
			value = "";
			// window ranges?
			for (DateRange r : window.getRanges()) {
				sep = value.compareTo("") == 0 ? " " : ", ";
				if (end.before(r.getEnd())) {
					if (end.before(r.getStart())) {
						value += sep + r.getStartStr() + " - " + r.getEndStr();
					} else {
							value += sep + "Ends: " + r.getEndStr();
					}
				}
			}
    		// periods?
			for (Period p : window.getPeriods()) {
    			if (p.getStart().after(end)) {
    				value += ", " + calPeriodToText(p) + " on "  + p.getStartDayString();
    			}
    		}
			// this is what get's displayed!
			info[endIndex] = value;
		} else {
			on[endIndex] = false;
			info[endIndex] = "";
		}

		// now cover the dates between
		for (int i=0; i<dates.length; i++) {
			String text = "";
			Date dt = dates[i];
	    	boolean partOfWindow = window.isDateInWindow(dt);
	    	// if part of the window, might be more info to add, like:
	    	// * what day does the default & chosen fall on?
	    	if (partOfWindow) {
	    		// any periods on this day?
	    		for (Period p : window.getPeriods()) {
	    			if (p.getStartDay().equals(dt)) {
	    				text += calPeriodToText(p);
	    			}
	    		}
	    	}
	    	// save results in the buffer
	    	on[i+1] = partOfWindow;
	    	info[i+1] = text;
        	
        }
	}

	private String calPeriodToText(Period p) {
		String state = "(" +  p.getState() + ")";
		String billed = "(" + Double.toString(p.getBilled()) + ")";
		String def = p.isDefaultPeriod() ? "Default" : "Chosen";
		// Ex: Default (P) (8.0)
		return def + " " + state + " " + billed;
	}
	
	public boolean isDateInWindow(Date dt) {
		return window.isDateInWindow(dt);
	}
	
	public boolean[] getDisplayFlags() {
		return on;
	}
	
	public boolean isDayNumberInWindow(int day) {
		return on[day];
	}
	
	public String getDayNumberInfo(int day) {
		return info[day];
	}
	
	public Date[] getDates() {
		return dates;
	}
	
	public String getLabel() {
		return window.getLabel();
	}
}
