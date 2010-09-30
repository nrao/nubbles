package edu.nrao.dss.client;

import com.extjs.gxt.ui.client.Style.Orientation;

import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.LayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class CalendarControl extends ContentPanel { //FormPanel {
	
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
		
	    String leftWidth = "300px";
	    String rightWidth = "300px";
	    String bottomWidth = "100%";

	    /* Table layout for making this a 2x2 format, instead of a single column */
		TableLayout tb = new TableLayout(3);
		//tb.setWidth("50%");
		tb.setBorder(0);
		setLayout(tb);

		TableData tdLeft = new TableData();
		tdLeft.setVerticalAlign(VerticalAlignment.TOP);
		// TODO: why must I do this, just to get the two forms to share space?
		tdLeft.setColspan(1);
		tdLeft.setWidth(leftWidth);
		
		TableData tdRight = new TableData();
		tdRight.setVerticalAlign(VerticalAlignment.TOP);
		// TODO: why must I do this, just to get the two forms to share space?
		tdRight.setColspan(1);
		tdRight.setWidth(rightWidth);

		
		FormPanel left = new FormPanel();
		left.setHeaderVisible(false);
		left.setBodyBorder(false);
		
		FormPanel right = new FormPanel();
		right.setHeaderVisible(false);		
		/* end of 2x2 formatting stuff */
		
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
	    right.add(dt);
	    //add(dt);

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
		//add(days);
	    right.add(days);
	    
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
		//add(tz);
	    right.add(tz);
	    
	    add(right, tdRight);
		
		CheckBox complete = new CheckBox();
		complete.setFieldLabel("Completed");
		complete.setToolTip("Filters for completed sessions when checked.");
		left.add(complete);
		
		CheckBox enabled = new CheckBox();
		enabled.setFieldLabel("Enabled");
		enabled.setToolTip("Filters for enabled sessions when checked.");
		enabled.setValue(true);
		left.add(enabled);
		
		// Scores
		scoresComboBox = new ScoresComboBox(schedule, complete, enabled);
		scoresComboBox.setFieldLabel("Scores");
        //add(scoresComboBox);
		
		left.add(scoresComboBox);
        add(left, tdLeft);
        
        FormPanel far = new FormPanel();
		far.setHeaderVisible(false);
		far.setBodyBorder(false);
		
		Button calcScores = new Button("Get Scores");
		calcScores.setToolTip("Get the scores for the select session.");
		calcScores.addSelectionListener(new SelectionListener<ButtonEvent> (){

			@Override
			public void componentSelected(ButtonEvent ce) {
				String session = (String) scoresComboBox.getSimpleValue();
		    	scoresComboBox.getSessionScores(session);
			}
			
		});
        far.add(calcScores);
        
		tdLeft.setVerticalAlign(VerticalAlignment.MIDDLE);
		add(far, tdLeft);
        
        schedule.scores = new Scores(scoresComboBox, new ScoresForCalendar(schedule));
        
	}
}
