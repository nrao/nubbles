package edu.nrao.dss.client.util.dssgwtcal.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

// This class is a collection of methods for thing related to time 
// A lot of this code deals with Daylight Savings Time (DST) issues.

public class TimeUtils {
	
	private DateRange[] dstFiveHourOffsets;
	private long msInDay = 24 * 60 * 60 * 1000;
	
	public TimeUtils() {
		dstFiveHourOffsets = getDSTFiveHourOffsets();
	}

	public static DateTimeFormat DATETIME_FORMAT  = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
	public static DateTimeFormat DATETIME_FORMAT2 = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
	
	private DateRange[] getDSTFiveHourOffsets() {
		DateRange[] drs = {
				new DateRange("2009-11-01 00:00:00", "2010-03-14 00:00:00"),
				new DateRange("2010-11-07 00:00:00", "2011-03-13 00:00:00"),
				new DateRange("2011-11-06 00:00:00", "2012-03-11 00:00:00"),
				new DateRange("2012-11-04 00:00:00", "2013-03-10 00:00:00"),
				new DateRange("2013-11-03 00:00:00", "2014-03-09 00:00:00"),
				new DateRange("2014-11-02 00:00:00", "2015-03-08 00:00:00") };
		return drs;
	}	
	
	// what days are do we spring forward or fall back?
	private Date[] getDSTBoundaries() {
		DateRange[] ranges = getDSTFiveHourOffsets();
		Date[] boundaries = new Date[ranges.length*2];
		int j = 0;
		for (int i=0; i<(ranges.length); i++) {
			boundaries[j] = ranges[i].start;
			j++;
			boundaries[j] = ranges[i].end;
			j++;
		}
		return boundaries;
	}
	
	public boolean isDSTBoundary(Date dt) {
		Date[] boundaries = getDSTBoundaries();
		for (int i=0; i<boundaries.length; i++) {
			if (dt.equals(boundaries[i])) {
				return true;
			}
		}
		return false; // not a DST boundary!
	}
	
	public boolean isToday(Date date) {
		// here we use deprecated methods - but it's copied from working code
		// in the gwt-cal 
		if (new Date().getYear() == date.getYear()
				&& new Date().getMonth() == date.getMonth()
				&& new Date().getDate() == date.getDate()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isInDSTFiveHourOffset(Date dt) {
		for (int i = 0; i < dstFiveHourOffsets.length; i++) {
			if (dstFiveHourOffsets[i].dateInRange(dt)) {
				return true;
			}
		}
		return false;
	}
	
	// Figure out whether DST applies for this date or not
	public long getEstOffsetMs(Date dt) {
        long offset = isInDSTFiveHourOffset(dt) ? 5 : 4;
		return offset * 60 * 60 * 1000; // milliseconds
	}

	public long getEstOffsetMs(long day) {
		Date dt = new Date(day * msInDay);
		return getEstOffsetMs(dt);
	}

	public long getDayOffset(Date dt) {
		return (dt.getTime() - getEstOffsetMs(dt)) % msInDay;
	}	
	
	// Date -> GMT day number
	public long getDay(Date dt) {
		long time = dt.getTime();
		return (time - getEstOffsetMs(dt)) / msInDay;
	}

	// Date -> GMT day number (taking more care w/ UTC offset)
	public long getDay(Date dt, Date offsetDt) {
		long time = dt.getTime();
		return (time - getEstOffsetMs(offsetDt)) / msInDay;
	}
}
