package edu.nrao.dss.client.util.dssgwtcal;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;


// an event represents an 'appointment' in the real world, and can map to one or more appointments.
// appointments map to blocks of time on our calendar.

public class Event {
	public int id;
	public String title;
	public String description;
	public Date start;
	public Date end;
	public boolean selected;
	private ArrayList<Appointment> appointments = new ArrayList<Appointment>();
	private long msInDay = 24 * 60 * 60 * 1000;
	// Nov 1 - March 8, 2009
	//private long msGmtOffset = 5 * 60 * 60 * 1000;
	private String type;
	private String session_type;
	private String state;
	
    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    
    public class DateRange {
        public Date start;
        public Date end;
        
        public DateRange(Date start, Date end) {
        	this.start = start;
        	this.end   = end;
        }
        
        public DateRange(String start, String end) {
    		this.start  = DATE_FORMAT.parse(start);
    		this.end = DATE_FORMAT.parse(end);        	
        }
        
        public boolean dateInRange(Date dt) {
            return ((dt.before(this.end)) && (dt.after(this.start)) );        	
        }
    }
    
	public Event(int id, String title, String description, Date start
			   , Date end, String type, String session_type, String state) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.start = start;
		this.end = getSafeEndDate(end); //don't end at midnight, but 1 min. before
		this.type = type;
		this.session_type = session_type;
		this.state = state;
		createAppointments();
	}
	
	private long getGmtOffsetMs(Date dt) {
		long offset = 4;
		// TODO: how to get this to work forever?
	    // 5 hours between Nov 1 2009, March 14, 2010, otherwise, 4 hours
		//Date nov1_2009  = DATE_FORMAT.parse("2009-11-01 00:00:00");
		//Date mar14_2010 = DATE_FORMAT.parse("2010-03-14 00:00:00");
		// Specify the date ranges where the offset between ET & UT is 5 hours, not 4.
		// Dates according to: http://www.usno.navy.mil/USNO/astronomical-applications/astronomical-information-center/daylight-time
        //		2010 	March 14 	November 7
		//		2011 	March 13 	November 6
		//		2012 	March 11 	November 4
		//		2013 	March 10 	November 3
		//		2014 	March 9 	November 2
		//		2015 	March 8 	November 1
		DateRange fiveHourOffsets[] = {new DateRange("2009-11-01 00:00:00","2010-03-14 00:00:00")
		                             , new DateRange("2010-11-07 00:00:00","2011-03-13 00:00:00")
                                     , new DateRange("2011-11-06 00:00:00","2012-03-11 00:00:00")
                                     , new DateRange("2012-11-04 00:00:00","2013-03-10 00:00:00")
                                     , new DateRange("2013-11-03 00:00:00","2014-03-09 00:00:00")
        							 , new DateRange("2014-11-02 00:00:00","2015-03-08 00:00:00")
		};
		for (int i = 0; i < fiveHourOffsets.length; i++) {
			if (fiveHourOffsets[i].dateInRange(dt)) {
				offset = 5;
			}
		}

	    return offset * 60 * 60 * 1000; // milliseconds
	}
	
	private long getGmtOffsetMs(long day) {
		Date dt = new Date(day * msInDay);
		return getGmtOffsetMs(dt);
	}
	
	private long getDayOffset(Date dt) {
		return (dt.getTime() - getGmtOffsetMs(dt)) % msInDay;
	}
	
	// avoid wrap-around when a time block ends on midnight
	private Date getSafeEndDate(Date end) {
		if (getDayOffset(end) == 0) {
			return new Date(end.getTime() - (60 * 1000)); // loose a minute! 
		} else {
			return end;
		}
	}
	
	// Date -> GMT day number
	private long getDay(Date dt) {
		long time = dt.getTime();
		return (time - getGmtOffsetMs(dt)) / msInDay;
	}
	
	// GMT day number -> Date
	private Date getDayDate(long day) {
		long gmtDay = (msInDay * day) + getGmtOffsetMs(day);
		return new Date(gmtDay);
	}
	


	// GMT day number -> last few seconds of that day as Date
	private Date getEndDayDate(long day) {
		// the next day is from day + i, so get that, then subtract a few seconds
		long gmtDay = (msInDay * (day + 1)) + getGmtOffsetMs(day + 1);
		long msOffset = 60 * 1000; // 1 min. 
		return new Date(gmtDay - msOffset);
		
	}
	private int getDaySpan() {
        long dayStart = getDay(start);
        long dayEnd   = getDay(end);
        return (int) (dayEnd - dayStart);
        
	}
	private void createAppointments() {
		// map this single event to one or more appointments according to whether
		// or not the event spans a GMT day.
		long dayStart = getDay(start);
//		long dayEnd   = getDay(end);
		int daySpan = getDaySpan();
		Date apptStart;
		Date apptEnd;
 		for (int i = 0; i <= daySpan; i++) {
		   // first?	
		   if (i == 0) {
			   apptStart = new Date(start.getTime());
		   } else {
			   // all the next appointments start at the start of the day
			   apptStart = getDayDate(dayStart + i);
		   }
		   // last?
		   if (i == daySpan) {
			   apptEnd = new Date(end.getTime());
		   } else {
			   // all continuing appointments start at the end of the day
			   apptEnd = getEndDayDate(dayStart + i);
		   }
//		   String strStart = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:SS").format(apptStart);
//		   String strEnd   = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:SS").format(apptEnd);

		   // our Event becomes one or more of their Appointments
		   Appointment appt = new Appointment();
		   appt.setEventId(id);
		   appt.setStart(apptStart);
		   appt.setEnd(apptEnd);
		   
		   // TODO: format tittle and description better
		   appt.setTitle(title); //title + " : " + Integer.toString(i));
		   String desc = description;
		   if (daySpan > 0) {
			   desc = desc + " (Day " + Integer.toString(i + 1) + ")";
		   }
		   appt.setDescription(desc);
		   
		   // TODO: need to improve the way we indicate period attributes
		   if (type != "not windowed!") {
			   if (type == "default period") {
				   appt.addStyleName("gwt-appointment-green");
			   } else {
				   appt.addStyleName("gwt-appointment-yellow");
			   }
			   
		   } else {
			   if (session_type.contains("O")){
				   // Open Session
				   appt.addStyleName("gwt-appointment-blue");
			   } else if (session_type.contains("E")) {
				   // Elective Session
				   appt.addStyleName("gwt-appointment-pink");
			   } else {
				   // Fixed Session
				   appt.addStyleName("gwt-appointment-red");
			   }
		   }
		   
		   if (state.contains("P")) {
			   appt.addStyleName("gwt-appointment-orange");
		   }
		   
		   appointments.add(appt);
		}
		 
	}
	public ArrayList<Appointment> getAppointments() {
		return appointments;
	}
}
