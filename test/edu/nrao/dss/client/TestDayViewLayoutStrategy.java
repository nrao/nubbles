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
    public void testOne() {
    	
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
		
    }
    
    private void assertAdapterEquals(AppointmentAdapter aa, int numBlocks, int cellStart, int cellSpan, int colStart, float left, float top, float width, float height) {
    	float e = 0.001f;
        List<TimeBlock> blocks = aa.getIntersectingBlocks();
        assertEquals(numBlocks, blocks.size());
        assertEquals(cellStart, aa.getCellStart());
        assertEquals(cellSpan, aa.getCellSpan());
        assertEquals(colStart, aa.getColumnStart());
        assertEquals(1, aa.getColumnSpan()); // ever changes?
        assertEquals(100.0, aa.getCellPercentFill(), e); // ever changes?
        assertEquals(0.0, aa.getCellPercentStart(), e); // ever changes?
        AppointmentInterface a = aa.getAppointment();
        assertEquals(left, a.getLeft(), e);
        assertEquals(top, a.getTop(), e);
        assertEquals(width, a.getWidth(), e);
        assertEquals(height, a.getHeight(), e);    	
    	
    }

}
