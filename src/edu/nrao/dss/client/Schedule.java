package edu.nrao.dss.client;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.bradrydzewski.gwt.calendar.client.CalendarSettings;
import com.bradrydzewski.gwt.calendar.client.DayView;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;

// This class is the new version of the Beta Test's Scheduling Page.

public class Schedule extends ContentPanel {
	
	//private PeriodExplorer west;
	public ScheduleCalendar west;
	private ContentPanel east;

	private DayView dayView;
	
	private Integer numCalendarDays = 1;
	private Date startCalendarDay = new Date();
	
	public Schedule() {
			super();
			initLayout();
	}	
	
	protected void initLayout() {
		setHeaderVisible(true);
		setLayout(new BorderLayout());
		
		// bells & whistles for this content panel
		setHeading("Schedule Stuff"); 
		setCollapsible(true);
		setFrame(true);
		setBodyStyle("backgroundColor: white;");
		getHeader().addTool(new ToolButton("x-tool-gear"));
		getHeader().addTool(new ToolButton("x-tool-close"));

		// now for the child panels:
		// At the top, control widgets
		final FormPanel north = new FormPanel();
		north.setHeading("North: Control Widgets");
		north.setBorders(true);
		
		// fields for form
		// 1. Date - when this changes, change the start of the calendar view
	    final DateField dt = new DateField();
	    dt.setValue(startCalendarDay);
	    dt.setFieldLabel("Start Date");
	    dt.addListener(Events.Change, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	            startCalendarDay = dt.getValue();
	            updateCalendar();
	    	}
	    });
	    north.add(dt);

		// 2. Days - when this changes, change the length of the calendar view
		final SimpleComboBox<Integer> days;
		days = new SimpleComboBox<Integer>();
		days.add(1);
		days.add(2);
		days.add(3);

		days.setFieldLabel("Days");
		days.setEditable(false);
		days.setSimpleValue(numCalendarDays);
	    days.addListener(Events.Change, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		numCalendarDays = days.getSimpleValue(); 
	            updateCalendar();
	    	}
	    });
		north.add(days);
		
		Button scheduleButton = new Button("Schedule");
		scheduleButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
	    		HashMap<String, Object> keys = new HashMap<String, Object>();
	    		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(startCalendarDay) + " 00:00:00";
	    		keys.put("start", startStr);
	    		keys.put("duration", numCalendarDays);
				String msg = "Scheduling from " + startStr + " for " + numCalendarDays.toString() + " days.";
				final MessageBox box = MessageBox.wait("Calling Scheduling Algorithm", msg, "Be Patient ...");
				JSONRequest.post("/runscheduler", keys,
						new JSONCallbackAdapter() {
							public void onSuccess(JSONObject json) {
								System.out.println("schedule_algo onSuccess");
								updateCalendar();
								box.close();
							}
						});
			}
		});
		north.add(scheduleButton);
		
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 150);
		northData.setMargins(new Margins(5,5,5,5));

		// to the left, the period explorer
		west = new ScheduleCalendar(startCalendarDay, numCalendarDays);
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 550);
		westData.setSplit(true);

		// to the right, the calendar
		east = new ContentPanel();
		east.setHeading("East: Calendar");
		east.setBorders(true);
		east.setCollapsible(true);
		
		// calendar
		//DayView dayView = new DayView();
		dayView = new DayView();
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
//		dayView.addValueChangeHandler(new ValueChangeHandler<Appointment>(){
//	        public void onValueChange(ValueChangeEvent<Appointment> event) {
//	            String msg = "Details of Period for Session: " + event.getValue().getDescription();   
//	            Window.alert(msg);
//	        }               
//	    });		
		east.add(dayView);
		
		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST);
		eastData.setSplit(true);

		// add all the components to this parent panel
		add(north, northData);
		add(west, westData);
		add(east, eastData);

	}
	
    private void updateCalendar() {
    	
    	// construct the url that gets us our periods for the explorer
		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(startCalendarDay);
		String baseUrl = "/periods";
		String url = baseUrl + "?startPeriods=" + startStr + "&daysPeriods=" + Integer.toString(numCalendarDays);
		
		// get the period explorer to load these
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy = west.pe.getProxy();
		proxy.setBuilder(builder);
		west.pe.loadData();
		
		// now get the calendar to load these
		dayView.setDate(startCalendarDay); //calendar date, not required
		dayView.setDays((int) numCalendarDays);		

		// make the JSON request for the periods so we can make appointments
		// we need the same url in a different format
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("startPeriods", startStr);
		keys.put("daysPeriods", Integer.toString(numCalendarDays));
	    JSONRequest.get(baseUrl, keys, new JSONCallbackAdapter() {
	            @Override
	            public void onSuccess(JSONObject json) {
	            	// JSON periods -> JAVA periods
	                List<Period> periods = new ArrayList<Period>();
	                JSONArray ps = json.get("periods").isArray();
	                for (int i = 0; i < ps.size(); ++i) {
	                	Period period = Period.parseJSON(ps.get(i).isObject());
	                	if (period != null){
	                		periods.add(period);
	                	}
	                }
	                // update the gwt-cal widget
	                loadAppointments(periods);
	            }
	    });
	}		
	    
    // updates the gwt-cal widget w/ given periods
    private void loadAppointments(List<Period> periods) {	    
		dayView.suspendLayout();
		dayView.clearAppointments();
		for(Period p : periods) {
		        Appointment appt = new Appointment();
		        GWT.log(p.getStart().toString() + " - " + p.getEnd().toString(), null);
		        appt.setStart(p.getStart());
		        appt.setEnd(p.getEnd());
		        appt.setTitle("Period");
		        appt.setDescription(p.getHandle());
		        appt.addStyleName("gwt-appointment-blue");
		        dayView.addAppointment(appt);
		}
		dayView.resumeLayout();
    }
}	
	
