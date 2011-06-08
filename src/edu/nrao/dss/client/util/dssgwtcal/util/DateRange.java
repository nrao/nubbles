package edu.nrao.dss.client.util.dssgwtcal.util;

import java.util.Date;

public class DateRange {
	public Date start;
	public Date end;

	public DateRange(Date start, Date end) {
		this.start = start;
		this.end = end;
	}

	public DateRange(String start, String end) {
		this.start = TimeUtils.DATETIME_FORMAT2.parse(start);
		this.end = TimeUtils.DATETIME_FORMAT2.parse(end);
	}

	public boolean dateInRange(Date dt) {
		// falling w/ in a date range uses the convention of an open end: [start, end)
		return ((dt.before(this.end)) && (dt.after(this.start)) || dt.equals(this.start));
	}
}
