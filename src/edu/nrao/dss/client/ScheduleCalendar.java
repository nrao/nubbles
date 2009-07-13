package edu.nrao.dss.client;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.Widget;

public class ScheduleCalendar extends ContentPanel { 

	// temporary class used for populating calendar table
    public class CalendarRow {
    	public String start; // datetime
    	public String duration; // minutes
    	public String session;
    	
		public CalendarRow(String st, String dur, String sess) {
    		start = st;
    		duration = dur;
    		session = sess;
    	}
    }
	
	public ScheduleCalendar() {
			super();
			initLayout();
	}	
	
	@SuppressWarnings("unchecked") 
	protected void initLayout() {
		
		setHeading("West: Calendar");
		setBorders(true);
		
		// list the periods in a table of 5 columns
		TableLayout tl = new TableLayout(5);
		tl.setWidth("100%");
		tl.setHeight("20px");
		tl.setBorder(1);
		setLayout(tl);

		// fake the calendar data
		List<CalendarRow> periods = new ArrayList<CalendarRow>();
		periods.add(new CalendarRow("2009-06-01 00:00:00", "60", "Session(8): Cygnis X-1"));
		periods.add(new CalendarRow("2009-06-01 01:00:00", "60", "Session(34): Uranus"));
		
		populateCalendar(periods);
	}
	
	protected void populateCalendar(List<CalendarRow> periods) {
		for (CalendarRow period : periods) {
			addButton("Edit");
			addButton("Delete");
			addLabel(period.start);
			addLabel(period.duration);
			addLabel(period.session);
		}
	}
	
	@SuppressWarnings("unchecked") 
	protected void addButton(String data){
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		add((Widget) new Button(data), td);	
	}
	
	@SuppressWarnings("unchecked") 
	protected void addLabel(String data){
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		add((Widget) new LabelField(data), td);	
	}
}	
	