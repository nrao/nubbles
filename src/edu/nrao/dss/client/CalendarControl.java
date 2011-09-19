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
import com.extjs.gxt.ui.client.widget.layout.MarginData;
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
		
		String leftWidth = "400px";
	    String rightWidth = "300px";
	    String bottomWidth = "100%";

	    /* Table layout for making this a 2x2 format, instead of a single column */
		TableLayout tb = new TableLayout(3);
		//tb.setWidth("50%");
		tb.setBorder(0);
		setLayout(tb);

		TableData tdLeft = new TableData();
		tdLeft.setVerticalAlign(VerticalAlignment.TOP);
		// Question: why must I do this, just to get the two forms to share space?
		tdLeft.setColspan(1);
		tdLeft.setWidth(leftWidth);
		
		TableData tdRight = new TableData();
		tdRight.setVerticalAlign(VerticalAlignment.TOP);
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
	    		if ( !schedule.startCalendarDay.equals(dt.getValue())) {
	    			schedule.startCalendarDay = dt.getValue();
		            schedule.startVacancyDate = schedule.startCalendarDay;
		            schedule.vacancyControl.vacancyDate.setValue(schedule.startVacancyDate);
		            schedule.updateCalendar();
	    		}
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
		days.add(14);
		days.add(31);
		days.setToolTip("Set the schedule and display duration");

		days.setFieldLabel("Days");
		days.setEditable(false);
		days.setSimpleValue(schedule.numCalendarDays);
		days.setTriggerAction(TriggerAction.ALL);
	    days.addListener(Events.Valid, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		if (!schedule.numCalendarDays.equals(days.getSimpleValue())) {
		    		schedule.numCalendarDays = days.getSimpleValue(); 
		            schedule.updateCalendar();
	    		}
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
	    		if (!schedule.timezone.equals(tz.getSimpleValue())) {
		    		//schedule.timezone = tz.getSimpleValue();
		    		schedule.setTimezone(tz.getSimpleValue());
		    		schedule.baseUrl = "/scheduler/periods/" + schedule.timezone;
		        	schedule.scheduleExplorer.pe.setRootURL(schedule.baseUrl);
		            schedule.updateCalendar();
	    		}
	    	}
	    });
		//add(tz);
	    right.add(tz);
	    
	    add(right, tdRight);
		
		CheckBox notcomplete = new CheckBox();
		notcomplete.setFieldLabel("Not Complete");
		notcomplete.setToolTip("Filters for sessions that are not complete when checked.");
		notcomplete.setValue(true);
		left.add(notcomplete);
		
		CheckBox enabled = new CheckBox();
		enabled.setFieldLabel("Enabled");
		enabled.setToolTip("Filters for enabled sessions when checked.");
		enabled.setValue(true);
		left.add(enabled);
		
		// Scores
		scoresComboBox = new ScoresComboBox(schedule, notcomplete, enabled);
		scoresComboBox.setFieldLabel("Scores");
		
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
