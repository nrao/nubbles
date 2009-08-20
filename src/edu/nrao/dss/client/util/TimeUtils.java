package edu.nrao.dss.client.util;

public class TimeUtils {
	
	public TimeUtils() {}

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
}
