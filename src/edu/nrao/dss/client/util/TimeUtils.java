package edu.nrao.dss.client.util;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.google.gwt.i18n.client.DateTimeFormat;

public class TimeUtils {
	
	public TimeUtils() {}

	public static DateTimeFormat DATETIME_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
	
	public static String min2sex(Integer minutes) {
		Integer hrs = minutes / 60;
		Integer mns = minutes % 60;
		// Geez! this is awful, is there not a better way?  Assembly code?
		StringBuilder buf = new StringBuilder();
		if (hrs < 10) {
			buf.append("0");
		}
		buf.append(hrs.toString());
		buf.append(":");
		if (mns < 10) {
			buf.append("0");
		}
		buf.append(mns.toString());
		return buf.toString();
	}
	
	public static Date datePart(Date d) {
		return new Date(d.getYear(), d.getMonth(), d.getDate());
	}
	
	public static Time timePart(Date d) {
		return new Time(d.getHours(), d.getMinutes());
	}
	
	public static Integer msec2minutes(long ms) {
		return (int) (ms/(60*1000));
	}
	
	public static Date toDate(BaseModelData d) {
		return DATETIME_FORMAT.parse(d.get("date").toString() + " " + d.get("time").toString());
	}
	
	public static Double toDuration(BaseModelData d) {
		return d.get("duration");
	}
	
    public static Date getEnd(Date start, Double duration) {
    	long startSecs = start.getTime();
    	// add the duration (in hours) to this time in milli-seconds
    	long endMsecs = (long) (startSecs + (duration * 60.0 * 60.0 * 1000.0));
    	return new Date(endMsecs);
    }
}
