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

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.client.GWTTestCase;

import edu.nrao.dss.client.util.dssgwtcal.Appointment;
import edu.nrao.dss.client.util.dssgwtcal.AppointmentAdapter;
import edu.nrao.dss.client.util.dssgwtcal.AppointmentInterface;
import edu.nrao.dss.client.util.dssgwtcal.CalendarSettings;
import edu.nrao.dss.client.util.dssgwtcal.DayView;
import edu.nrao.dss.client.util.dssgwtcal.DayViewLayoutStrategy;
import edu.nrao.dss.client.util.dssgwtcal.TimeBlock;


public class TestDayViewLayoutStrategy extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "edu.nrao.dss.Nubbles";		
	}
    public void testDoLayoutSimple() {
    	
    	long msPerHour = 1000 * 60 * 60;
    	
    	// setup test
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date startCalendarDay = dtf.parse("2009-06-01 00:00:00");
    	int numCalendarDays = 3;
		DayView dayView = new DayView();
		CalendarSettings settings = new CalendarSettings();
		
		// this fixes offset issue with time labels
		settings.setOffsetHourLabels(false);
		// 15-min. boundaries!
		settings.setIntervalsPerHour(4);
		settings.setEnableDragDrop(true);
		settings.setPixelsPerInterval(12);
		dayView.setSettings(settings);
		
		// construct the strategy using the dayView simply as a wrapper
		// for the settings
		DayViewLayoutStrategy s = new DayViewLayoutStrategy(dayView);
		
		// prepare the appointments to layout
		ArrayList<AppointmentInterface> firstDayAppts = new ArrayList<AppointmentInterface>();
		Date apptEnd = new Date(startCalendarDay.getTime() + msPerHour);
		Appointment appt = new Appointment(0, "0", "0", startCalendarDay, apptEnd);
		firstDayAppts.add(appt);
		
		// test 1 - just one appointment at the start of the day
        ArrayList<AppointmentAdapter> appointmentAdapters =
            s.doLayout(firstDayAppts, 0, numCalendarDays);

        float oneColWidth = 32.333f;
        float twoColWidth = 15.666f;
        float oneHrHeight = 46.0f;
        float firstColLeft = 0.5f;
        float secColLeft = 17.166f;
        float startTop = 0.0f;
        
        assertEquals(1, appointmentAdapters.size());
        AppointmentAdapter aa = appointmentAdapters.get(0);
        assertAdapterEquals(aa, 4, 0, 4, 0, firstColLeft, startTop, oneColWidth, oneHrHeight);
        
        // now see the affects of an identical appointment overlapping this one.
		appt = new Appointment(1, "1", "1", startCalendarDay, apptEnd);
		firstDayAppts.add(appt);
		
		// test 2 - 2 identical appointments
        appointmentAdapters = s.doLayout(firstDayAppts, 0, numCalendarDays);
        
        assertEquals(2, appointmentAdapters.size());
        // first appt
        aa = appointmentAdapters.get(0);
        assertAdapterEquals(aa, 4, 0, 4, 0, firstColLeft, startTop, twoColWidth, oneHrHeight);

        // second appt
        aa = appointmentAdapters.get(1);
        assertAdapterEquals(aa, 4, 0, 4, 1, secColLeft, startTop, twoColWidth, oneHrHeight);
        
        // now see what happens when you push the second appt. ahead by 1/2 hour, and shrink duration
        // get rid of the last one
        AppointmentInterface ai = firstDayAppts.remove(1);
        // modify it: add 1/2 hour and shrink duration by 1/4 hour
        ai.setStart(new Date(startCalendarDay.getTime() + (msPerHour/2)));
        ai.setEnd(new Date(ai.getStart().getTime() + (3*msPerHour/4)));
        // put it back again
        firstDayAppts.add(ai);
        
		// test 3 - 2 overlapping appointments
        appointmentAdapters = s.doLayout(firstDayAppts, 0, numCalendarDays);
        
        assertEquals(2, appointmentAdapters.size());
        // first appt
        aa = appointmentAdapters.get(0);
        assertAdapterEquals(aa, 4, 0, 4, 0, firstColLeft, startTop, twoColWidth, oneHrHeight);
        
        // second appt
        aa = appointmentAdapters.get(1);
        assertAdapterEquals(aa, 3, 2, 3, 1, secColLeft, 24.0f, twoColWidth, 34.0f);
        
        // now add a third appointment that does NOT overlap
        Date thirdStart = new Date(startCalendarDay.getTime() + (2*msPerHour));
        Date thirdEnd   = new Date(thirdStart.getTime() + (3*msPerHour));
		appt = new Appointment(2, "2", "2", thirdStart, thirdEnd);
		firstDayAppts.add(appt);        
		
		// test 4 - 2 overlapping + one non-overlapping
        appointmentAdapters = s.doLayout(firstDayAppts, 0, numCalendarDays);
        
        assertEquals(3, appointmentAdapters.size());
        // first appt
        aa = appointmentAdapters.get(0);
        assertAdapterEquals(aa, 4, 0, 4, 0, firstColLeft, startTop, twoColWidth, oneHrHeight);
        
        // second appt
        aa = appointmentAdapters.get(1);
        assertAdapterEquals(aa, 3, 2, 3, 1, secColLeft, 24.0f, twoColWidth, 34.0f);

        // third appt
        aa = appointmentAdapters.get(2);
        assertAdapterEquals(aa, 12, 8, 12, 0, 0.5f, 96.0f, oneColWidth, 142.0f);
        
		// test 5 - there's nothing the next day
		ArrayList<AppointmentInterface> secondDayAppts = new ArrayList<AppointmentInterface>();        
        appointmentAdapters = s.doLayout(secondDayAppts, 1, numCalendarDays);
        assertEquals(0, appointmentAdapters.size());
    }
    
    public void testDoLayoutComplex() {
    	
    	long msPerHour = 1000 * 60 * 60;

    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date startCalendarDay = dtf.parse("2009-06-01 00:00:00");
    	int numCalendarDays = 3;
		DayView dayView = new DayView();
		CalendarSettings settings = new CalendarSettings();
		
		// this fixes offset issue with time labels
		settings.setOffsetHourLabels(false);
		// 15-min. boundaries!
		settings.setIntervalsPerHour(4);
		settings.setEnableDragDrop(true);
		settings.setPixelsPerInterval(12);
		dayView.setSettings(settings);
		
		// construct the strategy using the dayView simply as a wrapper
		// for the settings
		DayViewLayoutStrategy s = new DayViewLayoutStrategy(dayView);
    	
		int durationHrs = 2;
		
		// Goal: to create a schedule that looks like this:
		// 2 hour appointments starting from 00:00 - 12:00, w/ an overlap from 3:30 - 6:30.
		// 2 hour appointments starting from 14:00 - 23:59:00, w/ an overlap from 14:00 - 14:30
		String[][] expAppts = {{"2009-06-01 00:00:00", "2009-06-01 02:00:00"}
                             , {"2009-06-01 02:00:00", "2009-06-01 04:00:00"}
                             , {"2009-06-01 03:30:00", "2009-06-01 06:30:00"} // overlap 1
                             , {"2009-06-01 04:00:00", "2009-06-01 06:00:00"}
                             , {"2009-06-01 06:00:00", "2009-06-01 08:00:00"}
                             , {"2009-06-01 08:00:00", "2009-06-01 10:00:00"}
                             , {"2009-06-01 10:00:00", "2009-06-01 12:00:00"} // after this there is a gap
                             , {"2009-06-01 14:00:00", "2009-06-01 14:30:00"} // overlap 2
                             , {"2009-06-01 14:00:00", "2009-06-01 16:00:00"}
                             , {"2009-06-01 16:00:00", "2009-06-01 18:00:00"}
                             , {"2009-06-01 18:00:00", "2009-06-01 20:00:00"}
                             , {"2009-06-01 20:00:00", "2009-06-01 22:00:00"}
                             , {"2009-06-01 22:00:00", "2009-06-01 23:59:00"}
		};
		Date aStart, aEnd;
		Appointment a;
		
		// this is the temporary, unsorted list
		ArrayList<AppointmentInterface> appts = new ArrayList<AppointmentInterface>();
    	// create a solid block of appointments spanning the whole day
		for (int i=0; i<expAppts.length; i++) {
			aStart = dtf.parse(expAppts[i][0]);
			aEnd   = dtf.parse(expAppts[i][1]);
			a = new Appointment(i, Integer.toString(i), Integer.toString(i), aStart, aEnd);
			appts.add(a);
		}	
		
		// test!!!
        ArrayList<AppointmentAdapter> appointmentAdapters = s.doLayout(appts, 0, numCalendarDays);
        
        // first the obvious stuff
        assertEquals(expAppts.length, appointmentAdapters.size());
        AppointmentInterface ai;
		for (int i=0; i<expAppts.length; i++) {
			aStart = dtf.parse(expAppts[i][0]);
			aEnd   = dtf.parse(expAppts[i][1]);
			ai = appointmentAdapters.get(i).getAppointment();
			assertEquals(ai.getStart(), aStart);
			assertEquals(ai.getEnd(), aEnd);
			assertEquals(ai.getTitle(), Integer.toString(i));
			assertEquals(ai.getDescription(), Integer.toString(i));
		}	 
		
		// now the less obvious stuff
        float oneColWidth = 32.333f;
        float twoColWidth = 15.666f;
        float halfHrHeight = 22.0f;
        float oneHrHeight = 46.0f;
        float twoHrHeight = 94.0f;
        float threeHrHeight = 142.0f;
        float firstColLeft = 0.5f;
        float secColLeft = 17.166f;
        float startTop = 0.0f;
        float twoHourStarts = 96.0f;
        
        AppointmentAdapter aa;
        aa = appointmentAdapters.get(0); // 00:00 - 02:00
        assertAdapterEquals(aa, 8, 0, 8, 0, firstColLeft, startTop, oneColWidth, twoHrHeight);
        aa = appointmentAdapters.get(1); // 02:00 - 04:00
        assertAdapterEquals(aa, 8, 8, 8, 0, firstColLeft, twoHourStarts, twoColWidth, twoHrHeight);		
        aa = appointmentAdapters.get(2); // 03:30 - 06:30
        assertAdapterEquals(aa, 12,14, 12, 1, secColLeft, 168.0f, twoColWidth, threeHrHeight);		
        aa = appointmentAdapters.get(3); // 04:00 - 06:00
        assertAdapterEquals(aa, 8,16, 8, 0, firstColLeft, (twoHourStarts*2.0f), twoColWidth, twoHrHeight);		
        aa = appointmentAdapters.get(4); // 06:00 - 08:00
        assertAdapterEquals(aa, 8,24, 8, 0, firstColLeft, (twoHourStarts*3.0f), twoColWidth, twoHrHeight);		
        aa = appointmentAdapters.get(5); // 08:00 - 10:00
        assertAdapterEquals(aa, 8,32, 8, 0, firstColLeft, (twoHourStarts*4.0f), oneColWidth, twoHrHeight);		
        aa = appointmentAdapters.get(6); // 10:00 - 12:00
        assertAdapterEquals(aa, 8,40, 8, 0, firstColLeft, (twoHourStarts*5.0f), oneColWidth, twoHrHeight);		
        // gap!
        aa = appointmentAdapters.get(7); // 14:00 - 14:30
        assertAdapterEquals(aa, 2,56, 2, 0, firstColLeft, (twoHourStarts*7.0f), twoColWidth, halfHrHeight);		
        aa = appointmentAdapters.get(8); // 14:00 - 16:00
        assertAdapterEquals(aa, 8,56, 8, 1, secColLeft, (twoHourStarts*7.0f), twoColWidth, twoHrHeight);
        // The rest we can check programmatically: 16:00 - 18:00 .. 20:00 - 22:00 
        for (int j=9; j<12; j++) {
            aa = appointmentAdapters.get(j); 
            assertAdapterEquals(aa, 8,(j-1)*8, 8, 0, firstColLeft, (twoHourStarts*(j-1)), oneColWidth, twoHrHeight);		
        }
        // the last appointment, 22:00:00 - 23:59:00, doesn't fill the cell one hundred percent
        aa = appointmentAdapters.get(12); 
        List<TimeBlock> blocks = aa.getIntersectingBlocks();
        assertEquals(8, blocks.size());
        assertEquals(8*11, aa.getCellStart());
        assertEquals(8, aa.getCellSpan());
        assertEquals(0, aa.getColumnStart());
    	float e = 0.001f;
        assertEquals(99.166, aa.getCellPercentFill(), e); 
        assertEquals(0.0, aa.getCellPercentStart(), e); // ever changes?
        AppointmentInterface i = aa.getAppointment();
        assertEquals(firstColLeft, i.getLeft(), e);
        assertEquals(twoHourStarts*11.0f, i.getTop(), e);
        assertEquals(oneColWidth, i.getWidth(), e);
        assertEquals(twoHrHeight, i.getHeight(), e);         
        
    }
    
    private AppointmentInterface findAppt(String start, String end, ArrayList<AppointmentInterface> appts) {
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date st = dtf.parse(start);
    	Date ed = dtf.parse(end);
    	for (int i=0; i<appts.size(); i++) {
    		if (st.equals(appts.get(i).getStart()) && ed.equals(appts.get(i).getEnd()) ) {
    			return appts.get(i);
    		}
    	}
    	return null; // couldn't find it!
    }
    private void assertAdapterEquals(AppointmentAdapter aa, int numBlocks, int cellStart, int cellSpan, int colStart, float left, float top, float width, float height) {
    	float e = 0.001f;
        List<TimeBlock> blocks = aa.getIntersectingBlocks();
        assertEquals(numBlocks, blocks.size());
        assertEquals(cellStart, aa.getCellStart());
        assertEquals(cellSpan, aa.getCellSpan());
        assertEquals(colStart, aa.getColumnStart());
        assertEquals(100.0, aa.getCellPercentFill(), e); 
        assertEquals(0.0, aa.getCellPercentStart(), e); // ever changes?
        AppointmentInterface a = aa.getAppointment();
        assertEquals(left, a.getLeft(), e);
        assertEquals(top, a.getTop(), e);
        assertEquals(width, a.getWidth(), e);
        assertEquals(height, a.getHeight(), e);    	
    	
    }

}
