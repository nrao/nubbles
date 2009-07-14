package edu.nrao.dss.client;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;


public class ScheduleCalendar extends ContentPanel { 

	private Date startCalendarDay = new Date();
	private int numCalendarDays = 1;
	
	public ScheduleCalendar(Date start, int days) {
			super();
			startCalendarDay = start;
			numCalendarDays = days;
			initLayout(start, days);
	}	
	
	@SuppressWarnings("unchecked") 
	protected void initLayout(Date start, int days) {
		
		setHeading("West: Calendar");
		setBorders(true);
		
		// list the periods in a table of 5 columns
		TableLayout tl = new TableLayout(5);
		tl.setWidth("100%");
		tl.setHeight("20px");
		tl.setBorder(1);
		setLayout(tl);

		// get the data to list
		loadPeriods(start, days);
	}
	
	protected void populateCalendar() {
		// TBF: need to clear previously created
		// table data
		///this.removeAll();
	    for (Period period : periods) {
	    	addButton("Edit");
	    	addDeleteButton(period);
	    	addLabel(period.getStartString());
	    	addLabel(Integer.toString(period.getDuration()));
	    	addLabel(period.getSessionLabel());
	    }
	}
	
	@SuppressWarnings("unchecked") 
	protected void addButton(String data){
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		add((Widget) new Button(data), td);	
	}
	
	@SuppressWarnings("unchecked") 
	protected void addDeleteButton(Period p){
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		Button b = new Button("Delete");
		b.setItemId(Integer.toString(p.getId()));
		b.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
	    		Button b = (Button) be.getSource();
				String id = b.getItemId();
				JSONRequest.delete("/periodJSON/" + id,
						new JSONCallbackAdapter() {
							public void onSuccess(JSONObject json) {
					    		loadPeriods(startCalendarDay, numCalendarDays);
							}
						});
			}
		});		

		add((Widget) b);
	}	
	@SuppressWarnings("unchecked") 
	protected void addLabel(String data){
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		add((Widget) new LabelField(data), td);	
	}
	
	public void loadPeriods(Date start, int numDays) {
		String startStr = "2009-06-01"; //DateTimeFormat.getFormat("yyy-MM-dd").format(start);
		String uri = "/periodJSON?startPeriods=" + startStr + "&daysPeriods=" + Integer.toString(numDays);
		JSONRequest.getWithKeywords(uri, new JSONCallbackAdapter() {
			@Override
		    public void onSuccess(JSONObject json) {
		        periods = new ArrayList<Period>();
		        JSONArray ps = json.get("periods").isArray();
		        for (int i = 0; i < ps.size(); ++i) {
		        	Period period = Period.parseJSON(ps.get(i).isObject());
		        	if (period != null) {
		        		periods.add(period);
		        	}
		        }
		        populateCalendar();	
		    }
		});
	}
	
    private List<Period> periods;

}	
	