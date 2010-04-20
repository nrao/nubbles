package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.core.client.GWT;

public class CalendarControl extends FormPanel {
	
	private Schedule schedule;
	
	// scoring sessions
	private ScoresComboBox scoresComboBox;
	
	public CalendarControl(Schedule sched) {
		schedule = sched;
		initLayout();
	}
	
	@SuppressWarnings("unchecked")
	public void showSessionScores(String session) {
		scoresComboBox.setSimpleValue(session);
		scoresComboBox.getSessionScores(session);
	}
	
	private void initLayout() {
		setHeading("Calendar Control");
		setBorders(true);
		setWidth("100%");
		
		// Date - when this changes, change the start of the calendar view
	    final DateField dt = new DateField();
	    dt.setValue(schedule.startCalendarDay);
	    dt.setFieldLabel("Start Date");
		dt.setToolTip("Set the schedule and display start day");
	    dt.addListener(Events.Valid, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	            schedule.startCalendarDay = dt.getValue();
	            schedule.startVacancyDate = schedule.startCalendarDay;
	            schedule.vacancyControl.vacancyDate.setValue(schedule.startVacancyDate);
	            schedule.updateCalendar();
	    	}
	    });
	    add(dt);

		// Days - when this changes, change the length of the calendar view
		final SimpleComboBox<Integer> days;
		days = new SimpleComboBox<Integer>();
		days.setForceSelection(true);
		days.add(1);
		days.add(2);
		days.add(3);
		days.add(4);
		days.add(5);
		days.add(6);
		days.add(7);
		days.setToolTip("Set the schedule and display duration");

		days.setFieldLabel("Days");
		days.setEditable(false);
		days.setSimpleValue(schedule.numCalendarDays);
		days.setTriggerAction(TriggerAction.ALL);
	    days.addListener(Events.Valid, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		schedule.numCalendarDays = days.getSimpleValue(); 
	            schedule.updateCalendar();
	    	}
	    });
		add(days);
		
		// Timezone - controls the reference for all the date/times in the tab
		final SimpleComboBox<String> tz;
		tz = new SimpleComboBox<String>();
		tz.setForceSelection(true);
		tz.setTriggerAction(TriggerAction.ALL);
		tz.add("UTC");
		tz.add("ET");
		tz.setToolTip("Set the timezone for all dates/times");

		tz.setFieldLabel("TZ");
		tz.setEditable(false);
		tz.setSimpleValue(schedule.timezone);
	    tz.addListener(Events.Valid, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		//schedule.timezone = tz.getSimpleValue();
	    		schedule.setTimezone(tz.getSimpleValue());
	    		schedule.baseUrl = "/periods/" + schedule.timezone;
	        	schedule.scheduleExplorer.pe.setRootURL(schedule.baseUrl);
	            schedule.updateCalendar();
	    	}
	    });
		add(tz);
		
		// Scores
		scoresComboBox = new ScoresComboBox(schedule);
		scoresComboBox.setFieldLabel("Scores");
        add(scoresComboBox);
        schedule.scores = new Scores(scoresComboBox, new ScoresForCalendar(schedule));
		
	}
}
