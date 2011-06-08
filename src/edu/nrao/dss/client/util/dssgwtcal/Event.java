package edu.nrao.dss.client.util.dssgwtcal;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

import edu.nrao.dss.client.util.dssgwtcal.util.DateRange;
import edu.nrao.dss.client.util.dssgwtcal.util.TimeUtils;

// an event represents an 'appointment' in the real world, and can map to one or more appointments.
// appointments map to blocks of time on our calendar.

public class Event {
	public int id;
	public String title;
	public String description;
	public Date start;
	public Date end;
	public Date start_day;
	public Date end_day;
	public boolean selected;
	private ArrayList<Appointment> appointments = new ArrayList<Appointment>();
	private long msInHour = 60 * 60 * 1000;
	private long msInDay = 24 * 60 * 60 * 1000;
	private String color;
	private TimeUtils tu;

	public Event(int id, String title, String description, Date start,
			Date start_day, Date end, Date end_day, String color) {
		
		tu = new TimeUtils();
		
		//this.dstFiveHourOffsets = tu.getDSTFiveHourOffsets();
		this.id = id;
		this.title = title;
		this.description = description;
		this.start = start;
		this.start_day = start_day;
		this.end = getSafeEndDate(end); // don't end at midnight, but 1 min.
										// before
		this.end_day = end_day;
		this.color = color;
		createAppointments();
	}

	// Total fucking kluge: when we calculate the next day using
	// Date.getTime() + milliseconds
	// the Date class will take DST into account, so watch for these offsets
	private Date[] getDSTPositiveOffsets() {
		Date[] dts = { TimeUtils.DATETIME_FORMAT2.parse("2010-03-15 01:00:00"),
				TimeUtils.DATETIME_FORMAT2.parse("2011-03-14 01:00:00"),
				TimeUtils.DATETIME_FORMAT2.parse("2012-03-12 01:00:00"),
				TimeUtils.DATETIME_FORMAT2.parse("2013-03-11 01:00:00"),
				TimeUtils.DATETIME_FORMAT2.parse("2014-03-10 01:00:00"),
				TimeUtils.DATETIME_FORMAT2.parse("2015-03-09 01:00:00") };
		return dts;
	}

	private Date[] getDSTNegativeOffsets() {
		Date[] dts = { TimeUtils.DATETIME_FORMAT2.parse("2009-11-01 23:00:00"),
				TimeUtils.DATETIME_FORMAT2.parse("2010-11-07 23:00:00"),
				TimeUtils.DATETIME_FORMAT2.parse("2011-11-06 23:00:00"),
				TimeUtils.DATETIME_FORMAT2.parse("2012-11-04 23:00:00"),
				TimeUtils.DATETIME_FORMAT2.parse("2013-11-03 23:00:00"),
				TimeUtils.DATETIME_FORMAT2.parse("2014-11-02 23:00:00") };
		return dts;
	}

	// avoid wrap-around when a time block ends on midnight
	private Date getSafeEndDate(Date end) {
		if (tu.getDayOffset(end) == 0) {
			return new Date(end.getTime() - (60 * 1000)); // loose a minute!
		} else {
			return end;
		}
	}

	public int getDaySpan() {
		long dayStart = tu.getDay(start);
		long dayEnd = tu.getDay(end, start);
		return (int) (dayEnd - dayStart);

	}

	private Date getNextDay(Date day, int days) {
		// here's what you do if you wouldn't have to worry about DST
		Date nextDay = new Date(day.getTime() + (days * msInDay));
		// but you do have to worry about; viva la Kluge.
		for (Date dt : getDSTPositiveOffsets()) {
			if (nextDay.equals(dt)) {
				nextDay = new Date(nextDay.getTime() - msInHour);
			}
		}
		for (Date dt : getDSTNegativeOffsets()) {
			if (nextDay.equals(dt)) {
				nextDay = new Date(nextDay.getTime() + msInHour);
			}
		}
		return nextDay;
	}

	private void createAppointments() {
		// map this single event to one or more appointments according to
		// whether or not the event spans more then one day.
		int daySpan = getDaySpan();
		Date apptStart;
		Date apptEnd;
		for (int i = 0; i <= daySpan; i++) {
			// first?
			if (i == 0) {
				apptStart = new Date(start.getTime());
			} else {
				// all the next appointments start at the start of the day
				apptStart = getNextDay(start_day, i);
			}
			// last?
			if (i == daySpan) {
				apptEnd = new Date(end.getTime());
			} else {
				// all continuing appointments start at the end of the day
				// apptEnd = getEndDayDate(dayStart + i);
				Date nextDay = new Date(end_day.getTime() + (i * msInDay));
				long msOffset = 60 * 1000; // 1 min.
				apptEnd = new Date(nextDay.getTime() - msOffset);
			}

			// our Event becomes one or more of their Appointments
			Appointment appt = new Appointment();
			appt.setEventId(id);
			appt.setTitle(title); 
			appt.setDescription(getAppointmentDescription(daySpan, i));
            appt.addStyleName(getStyleName());
			appt.setStart(apptStart);
			appt.setEnd(apptEnd);
			appointments.add(appt);
		}

	}

	private String getAppointmentDescription(int daySpan, int day) {
		if (daySpan > 0) {
			return description + " (Day " + Integer.toString(day + 1) + ")";
		} else {
			return description;
		}

	}
	
	private String getStyleName() {
		return "gwt-appointment-" + color;
	}
	
	public ArrayList<Appointment> getAppointments() {
		return appointments;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
}
