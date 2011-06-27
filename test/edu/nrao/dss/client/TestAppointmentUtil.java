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

import edu.nrao.dss.client.util.dssgwtcal.Appointment;
import edu.nrao.dss.client.util.dssgwtcal.util.AppointmentUtil;

public class TestAppointmentUtil extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
    public void testFilterListByDate() {
    	
    	// BULLSHIT BULLSHIT BULLSHIT!
    	// NOTE: remember!!! Appointments CAN'T OVERLAP days!!!!
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	// AND GET THIS!  Overlap INCLUDES ending at midnight!!! You have to subtract a little
    	// from it!!!!
    	Date start = dtf.parse("2009-06-01 18:00:00");  
    	int apptDurHrs = 2;
    	int numAppts = 24;
    	Date expLastStart = dtf.parse("2009-06-03 16:00:00");
    	String titleBase = "Title";
    	String descBase = "Desc";
    	String suffix = "";
    	Date apptStart, apptEnd;
    	long msPerHour = 1000 * 60 * 60;
    	ArrayList<Appointment> appts = new ArrayList<Appointment>();
    	Date dt;
    	
    	// setup - create a bunch of appointments
    	for (int i=0; i<numAppts; i++) {
    		
			Appointment appt = new Appointment();
			appt.setEventId(i);
			suffix = " (" + Integer.toString(i) + ")";
			appt.setTitle(titleBase + suffix); 
			appt.setDescription(descBase + suffix);
	        //appt.addStyleName(getStyleName());
			apptStart = new Date(start.getTime() + (i*msPerHour*apptDurHrs));
			apptEnd = new Date(apptStart.getTime() + (apptDurHrs*msPerHour));
			// watch out for midnight!
			if (apptEnd.getHours() == 0) {
				apptEnd = new Date(apptEnd.getTime() - (1000 * 60));
			}
			appt.setStart(apptStart);
			appt.setEnd(apptEnd);
			appts.add(appt);
    	}
		
    	// test - first make sure setup is correct
    	assertEquals(numAppts, appts.size());
    	// first appt
    	Appointment a = appts.get(0);
    	assertEquals("Title (0)", a.getTitle());
    	assertEquals(start.getTime(), a.getStart().getTime());
    	assertEquals(start.getTime() + (2*msPerHour), a.getEnd().getTime());
    	// second appt
    	a = appts.get(1);
    	assertEquals((2*msPerHour) + start.getTime(), a.getStart().getTime());
    	assertEquals((4*msPerHour) + start.getTime(), a.getEnd().getTime());
    	// last appt
    	a = appts.get(numAppts - 1);
    	assertEquals(dtf.format(expLastStart), dtf.format(a.getStart()));
    	
    	// now test AppointmentUtil (finally!)
        // Day 1
    	Date filterDt = dtf.parse("2009-06-01 00:00:00");
    	ArrayList<Appointment> filterAppts;
    	filterAppts = AppointmentUtil.filterListByDate(appts, filterDt);
    	assertEquals(3, filterAppts.size());
    	a = filterAppts.get(0);
    	assertEquals(start.getTime(), a.getStart().getTime());
    	assertEquals(start.getTime() + (2*msPerHour), a.getEnd().getTime());
    	assertEquals("Title (0)", a.getTitle());
    	a = filterAppts.get(1);
    	assertEquals((2*msPerHour) + start.getTime(), a.getStart().getTime());
    	assertEquals((4*msPerHour) + start.getTime(), a.getEnd().getTime());
    	assertEquals("Title (1)", a.getTitle());
        // Day 2
    	filterDt = dtf.parse("2009-06-02 00:00:00");
    	filterAppts = AppointmentUtil.filterListByDate(appts, filterDt);
    	assertEquals(12, filterAppts.size());
    	a = filterAppts.get(0);
    	assertEquals(dtf.parse("2009-06-02 00:00:00"), a.getStart());
    	assertEquals(dtf.parse("2009-06-02 02:00:00"), a.getEnd());
    	assertEquals("Title (3)", a.getTitle());
    	a = filterAppts.get(11);
    	assertEquals(dtf.parse("2009-06-02 22:00:00"), a.getStart());
    	assertEquals(dtf.parse("2009-06-02 23:59:00"), a.getEnd());
    	assertEquals("Title (14)", a.getTitle());
    	// Day 3
    	filterDt = dtf.parse("2009-06-03 00:00:00");
    	filterAppts = AppointmentUtil.filterListByDate(appts, filterDt);
    	assertEquals(9, filterAppts.size());
    	
    }
    
}
