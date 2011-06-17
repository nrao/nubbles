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
import edu.nrao.dss.client.util.dssgwtcal.Event;

public class TestEvent extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
    public void testGetAppointments() {
    	// setup 
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date start = dtf.parse("2006-02-02 12:30:00");
    	Date start_day = dtf.parse("2006-02-02 00:00:00");
    	Date end= dtf.parse("2006-02-02 14:30:00");
    	Date end_day = dtf.parse("2006-02-02 00:00:00");
    	Event e = new Event(1, "title", "description", start, start_day, end, end_day,"yellow");
    	
    	// test - trivial stuff
    	assertEquals("title", e.title);
    	assertEquals(1, e.id);
    	assertEquals(start, e.start);
    	assertEquals(start_day, e.start_day);
    	
    	// test - non-trivial stuff
    	ArrayList<Appointment> appts = e.getAppointments();
    	assertEquals(1, appts.size());
    	Appointment a = appts.get(0);
    	assertEquals("title", a.getTitle());
    	assertEquals("description", a.getDescription());
    	assertEquals(start, a.getStart());
    	assertEquals(end, a.getEnd());
    	assertEquals("gwt-appointment gwt-appointment-yellow", a.getStyleName());
    }
    
    public void testGetAppointments_2() {
    	
    	// setup an event over two days
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date start = dtf.parse("2006-02-02 22:30:00");
    	Date start_day = dtf.parse("2006-02-02 00:00:00");
    	// this is one mintue before 2006-02-03, when the first appointment should end
    	Date start_day_end = dtf.parse("2006-02-02 23:59:00");
    	Date end= dtf.parse("2006-02-03 00:30:00");
    	Date end_day = dtf.parse("2006-02-03 00:00:00");
    	String description = "GBT10A-001";
    	Event e = new Event(1, "", description, start, start_day, end, end_day, "green"); 

    	// an event that spans > 1 day, gets multiple appointments
    	ArrayList<Appointment> appts = e.getAppointments();
    	assertEquals(2, appts.size());
    	// first day
    	Appointment a = appts.get(0);
    	assertEquals("", a.getTitle());
    	assertEquals(description + " (Day 1)", a.getDescription());
    	assertEquals(start, a.getStart());
    	assertEquals(start_day_end, a.getEnd());
    	assertEquals("gwt-appointment gwt-appointment-green", a.getStyleName());
    	// second day
    	a = appts.get(1);
    	assertEquals("", a.getTitle());
    	assertEquals(description + " (Day 2)", a.getDescription());
    	assertEquals(end_day, a.getStart());
    	assertEquals(end, a.getEnd());
    	assertEquals("gwt-appointment gwt-appointment-green", a.getStyleName());    	
    	
    }
    
    public void testGetAppointments_DST() {
    	
    	// setup an event over two days - overlapping with the first day of 
    	// DST - Daylight Savings Time
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date start = dtf.parse("2013-11-02 21:00:00");
    	Date start_day = dtf.parse("2013-11-02 00:00:00");
    	// this is one mintue before the start day ends, when the first appointment should end
    	Date start_day_end = dtf.parse("2013-11-02 23:59:00");
    	Date end= dtf.parse("2013-11-03 03:00:00");
    	Date end_day = dtf.parse("2013-11-03 00:00:00");
    	String description = "GBT10A-001";
    	Event e = new Event(1, "", description, start, start_day, end, end_day, "green"); 

    	// an event that spans > 1 day, gets multiple appointments
    	ArrayList<Appointment> appts = e.getAppointments();
    	assertEquals(2, appts.size());
    	// first day
    	Appointment a = appts.get(0);
    	assertEquals("", a.getTitle());
    	assertEquals(description + " (Day 1)", a.getDescription());
    	assertEquals(start, a.getStart());
    	assertEquals(start_day_end, a.getEnd());
    	assertEquals("gwt-appointment gwt-appointment-green", a.getStyleName());
    	// second day
    	a = appts.get(1);
    	assertEquals("", a.getTitle());
    	assertEquals(description + " (Day 2)", a.getDescription());
    	assertEquals(end_day, a.getStart());
    	assertEquals(end, a.getEnd());
    	assertEquals("gwt-appointment gwt-appointment-green", a.getStyleName());    	
    	
    }   
    
    public void testGetAppointments_DST_2() {
    	
    	// setup an event over two days - overlapping with the end of the first day of 
    	// DST - Daylight Savings Time
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date start = dtf.parse("2013-11-03 21:00:00");
    	Date start_day = dtf.parse("2013-11-03 00:00:00");
    	// this is one minute before the start day ends, when the first appointment should end
    	Date start_day_end = dtf.parse("2013-11-03 23:59:00");
    	Date end= dtf.parse("2013-11-04 03:00:00");
    	Date end_day = dtf.parse("2013-11-04 00:00:00");
    	String description = "GBT10A-001";
    	Event e = new Event(1, "", description, start, start_day, end, end_day, "green"); 

    	// an event that spans > 1 day, gets multiple appointments
    	ArrayList<Appointment> appts = e.getAppointments();
    	assertEquals(2, appts.size());
    	// first day
    	Appointment a = appts.get(0);
    	assertEquals("", a.getTitle());
    	assertEquals(description + " (Day 1)", a.getDescription());
    	assertEquals(start, a.getStart());
    	assertEquals(start_day_end, a.getEnd());
    	assertEquals("gwt-appointment gwt-appointment-green", a.getStyleName());
    	// second day
    	a = appts.get(1);
    	assertEquals("", a.getTitle());
    	assertEquals(description + " (Day 2)", a.getDescription());
    	assertEquals(end_day, a.getStart());
    	assertEquals(end, a.getEnd());
    	assertEquals("gwt-appointment gwt-appointment-green", a.getStyleName());    	
    	
    }  

    // this finally reproduces our bug!  Had to go back to the date used below to
    // see if there were any problems.  Notice that if viewed there is no problem.
    public void testGetAppointments_DST_3() {
    	
    	// setup an event that does NOT overlap > 1 day, but is at the start of 
    	// DST - Daylight Savings Time
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date start = dtf.parse("2010-11-07 00:30:00");
    	Date start_day = dtf.parse("2010-11-07 00:00:00");
    	Date end= dtf.parse("2010-11-07 03:15:00");
    	Date end_day = dtf.parse("2010-11-07 00:00:00");
    	String description = "GBT10A-001";
    	Event e = new Event(1, "", description, start, start_day, end, end_day, "green"); 

    	// Watch the DST boundary Bug!
    	// an event that does not span > 1 day, gets only one appointment.  We get 2!
    	ArrayList<Appointment> appts = e.getAppointments();
    	assertEquals(2, appts.size());
    	// first day
    	Appointment a = appts.get(0);
    	assertEquals("", a.getTitle());
    	assertEquals(description + " (Day 1)", a.getDescription());
    	// here's the bug again!  how fucked up is that!
    	assertEquals(start, a.getStart());
    	assertEquals("2010-11-06 23:59:00", dtf.format(a.getEnd()));
    	assertEquals("gwt-appointment gwt-appointment-green", a.getStyleName());
    	// the next appointment, which shouldn't even be here:
    	a = appts.get(1);
    	assertEquals("", a.getTitle());
    	assertEquals(description + " (Day 2)", a.getDescription());
    	// here's the bug again!  that is some fucked up shit!
    	assertEquals("2010-11-08 00:00:00", dtf.format(a.getStart()));
    	assertEquals(end, a.getEnd());
    	assertEquals("gwt-appointment gwt-appointment-green", a.getStyleName());   	
    }    

    // Watch the DST boundary Bug!
	// an event that does not span > 1 day, should not span any days, but here we do!
    public void testGetDaySpan_DST() {
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date start = dtf.parse("2010-11-07 00:30:00");
    	Date start_day = dtf.parse("2010-11-07 00:00:00");
    	Date end= dtf.parse("2010-11-07 03:15:00");
    	Date end_day = dtf.parse("2010-11-07 00:00:00");
    	String description = "GBT10A-001";
    	Event e = new Event(1, "", description, start, start_day, end, end_day, "green");  	
    	
    	// enter the fucked up shit: this should be zero
    	assertEquals(1, e.getDaySpan());
    }
}
