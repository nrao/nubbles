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
    	
    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
    	Date startCalendarDay = dtf.parse("2009-06-01 00:00:00");
    	int numCalendarDays = 3;
		DayView dayView = new DayView();
//		dayView.setDate(startCalendarDay); //calendar date, not required
//		dayView.setDays((int) numCalendarDays); //number of days displayed at a time, not required
//		dayView.setWidth("100%");
//		dayView.setHeight("96%");
//		dayView.setTitle("Schedule Calendar");
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
		
		// test - just one appointment at the start of the day
        ArrayList<AppointmentAdapter> appointmentAdapters =
            s.doLayout(firstDayAppts, 0, numCalendarDays);
        
        assertEquals(1, appointmentAdapters.size());
        AppointmentAdapter aa = appointmentAdapters.get(0);
        List<TimeBlock> blocks = aa.getIntersectingBlocks();
        assertEquals(4, blocks.size());
        assertEquals(0, aa.getCellStart());
        assertEquals(4, aa.getCellSpan());
        assertEquals(0, aa.getColumnStart());
        assertEquals(1, aa.getColumnSpan());
        assertEquals(100.0, aa.getCellPercentFill(), 0.001);
        assertEquals(0.0, aa.getCellPercentStart(), 0.001);
        AppointmentInterface a = aa.getAppointment();
        assertEquals(0.5, a.getLeft(), 0.001);
        assertEquals(0.0, a.getTop(), 0.001);
        float width = 32.333f;
        assertEquals(width, a.getWidth(), 0.001);
        assertEquals(46.0, a.getHeight(), 0.001);
        
        // now see the affects of an identical appointment overlapping this one.
		appt = new Appointment(1, "1", "1", startCalendarDay, apptEnd);
		firstDayAppts.add(appt);
		
		// test - just one appointment at the start of the day
        appointmentAdapters = s.doLayout(firstDayAppts, 0, numCalendarDays);
        
        assertEquals(2, appointmentAdapters.size());
        // first
        aa = appointmentAdapters.get(0);
        blocks = aa.getIntersectingBlocks();
        assertEquals(4, blocks.size());
        assertEquals(0, aa.getCellStart());
        assertEquals(4, aa.getCellSpan());
        assertEquals(0, aa.getColumnStart());
        assertEquals(1, aa.getColumnSpan());
        assertEquals(100.0, aa.getCellPercentFill(), 0.001);
        assertEquals(0.0, aa.getCellPercentStart(), 0.001);
        a = aa.getAppointment();
        assertEquals(0.5, a.getLeft(), 0.001);
        assertEquals(0.0, a.getTop(), 0.001);
        assertEquals(15.666, a.getWidth(), 0.001);
        assertEquals(46.0, a.getHeight(), 0.001);
        // second
        aa = appointmentAdapters.get(1);
        blocks = aa.getIntersectingBlocks();
        assertEquals(4, blocks.size());
        assertEquals(0, aa.getCellStart());
        assertEquals(4, aa.getCellSpan());
        assertEquals(1, aa.getColumnStart());
        assertEquals(1, aa.getColumnSpan());
        assertEquals(100.0, aa.getCellPercentFill(), 0.001);
        assertEquals(0.0, aa.getCellPercentStart(), 0.001);
        a = aa.getAppointment();
        assertEquals(17.166, a.getLeft(), 0.001);
        assertEquals(0.0, a.getTop(), 0.001);
        assertEquals(15.666, a.getWidth(), 0.001);
        assertEquals(46.0, a.getHeight(), 0.001);        
    }

}
