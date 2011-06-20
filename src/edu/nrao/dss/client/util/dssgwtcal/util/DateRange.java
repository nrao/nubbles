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
