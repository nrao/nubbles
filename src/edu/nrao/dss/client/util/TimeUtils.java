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
