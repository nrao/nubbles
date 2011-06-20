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

package edu.nrao.dss.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.data.PeriodEventAdapter;
import edu.nrao.dss.client.util.dssgwtcal.Appointment;
import edu.nrao.dss.client.util.dssgwtcal.CalendarSettings;
import edu.nrao.dss.client.util.dssgwtcal.DayView;

public class TestDayView extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
    public void testOne() {
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date startCalendarDay = dtf.parse("2009-06-01 00:00:00");
    	int numCalendarDays = 3;
		DayView dayView = new DayView();
		dayView.setDate(startCalendarDay); //calendar date, not required
		dayView.setDays((int) numCalendarDays); //number of days displayed at a time, not required
		dayView.setWidth("100%");
		dayView.setHeight("96%");
		dayView.setTitle("Schedule Calendar");
		CalendarSettings settings = new CalendarSettings();
		// this fixes offset issue with time labels
		settings.setOffsetHourLabels(false);
		// 15-min. boundaries!
		settings.setIntervalsPerHour(4);
		settings.setEnableDragDrop(true);
		settings.setPixelsPerInterval(12); // shrink the calendar!
		dayView.setSettings(settings);

		// test
		assertEquals(numCalendarDays, dayView.getDays());
		assertEquals(startCalendarDay, dayView.getDate());
		assertEquals(0, dayView.getAppointmentCount());
		
		// create appointments based off a test period
		// this starts at 2009-06-02 14:00:00 for 2 hours
		PeriodJSON json = PeriodJSON.getTestPeriodJSON_2();
		Period p = Period.parseJSON(json);
		dayView.suspendLayout();
		dayView.clearAppointments();
		ArrayList<Appointment> appts = PeriodEventAdapter.fromPeriod(p).getAppointments();
		dayView.addAppointments(appts);
		dayView.resumeLayout();
		
		// test
		assertEquals(numCalendarDays, dayView.getDays());
		assertEquals(startCalendarDay, dayView.getDate());
		assertEquals(1, dayView.getAppointmentCount());
		assertEquals(p.getSession(), dayView.getAppointmentAtIndex(0).getDescription());
		assertEquals(p.getStart(), dayView.getAppointmentAtIndex(0).getStart());
    }
}
