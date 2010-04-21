package edu.nrao.dss.client.util.dssgwtcal;

import java.util.Date;

//import edu.nrao.dss.client.Appointment;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class TestCalendar implements EntryPoint {
    public void onModuleLoad() {
    	GWT.log("TestCalendar", null);
    	Label label = new Label("Hello world");
    	RootPanel.get().add(label);
        //initLayout();
    	
    	Date startCalendarDay = new Date();
    	Integer numCalendarDays = 2;
		DayView dayView = new DayView();
		GWT.log("staring calendar with: " + startCalendarDay.toString(), null);
		//Date day = new Date(startCalendarDay.getYear(), startCalendarDay.getMonth(), startCalendarDay.getMonth());
		//GWT.log("staring calendar at: " + day.toString(), null);
		dayView.setDate(startCalendarDay); //calendar date, not required
		dayView.setDays((int) numCalendarDays); //number of days displayed at a time, not required
		dayView.setWidth("100%");
		//dayView.setHeight("100%");
		dayView.setTitle("Schedule Calendar");
		CalendarSettings settings = new CalendarSettings();
		// this fixes offset issue with time labels
		settings.setOffsetHourLabels(false);
		// 15-min. boundaries!
		settings.setIntervalsPerHour(4);
		settings.setEnableDragDrop(true);
		dayView.setSettings(settings);   	
		RootPanel.get().add(dayView);
		
		// test data - should work
        Appointment appt = new Appointment();
        Date dt = new Date();
        Date start = dt;
        Date end = new Date((long) (dt.getTime() + (60.0 * 60.0 * 1000.0)));
        appt.setStart(start);
        appt.setEnd(end);
        appt.setTitle("Period");
        appt.setDescription("should be an hour long");
        appt.addStyleName("gwt-appointment-blue");
        dayView.addAppointment(appt);
        
        // test data - should wrap around midnight and needs to be fixed
        Appointment appt2 = new Appointment();
        Date end2 = new Date((long) (dt.getTime() + (24.0 * 60.0 * 60.0 * 1000.0)));
        appt2.setStart(start);
        appt2.setEnd(end2);
        appt2.setTitle("Period2");
        appt2.setDescription("should be a day long");
        appt2.addStyleName("gwt-appointment-blue");
        dayView.addAppointment(appt2);
        
    }
}

    