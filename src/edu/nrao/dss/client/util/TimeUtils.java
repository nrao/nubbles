package edu.nrao.dss.client.util;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.google.gwt.core.client.GWT;
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
	
	public static Date toDate(BaseModelData d) {
		return DATETIME_FORMAT.parse(d.get("date").toString() + " " + d.get("time").toString());
	}
}
