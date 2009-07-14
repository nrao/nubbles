package edu.nrao.dss.client;


import java.util.Date;
import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
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
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

// This class is the new version of the Beta Test's Scheduling Page.

public class Schedule extends ContentPanel {
	
	//private PeriodExplorer west;
	private ScheduleCalendar west;
	private ContentPanel east;

	private Integer numCalendarDays = 1;
	private Date startCalendarDay = new Date();
	
	public Schedule() {
			super();
			initLayout();
	}	
	
	@SuppressWarnings("unchecked") 
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
	    		DateField dtf = (DateField) be.getSource();
	    		Date date = dtf.getValue();
	    		String dateStr = date.toString();
	    		Window.alert("Will change calendar to start at: " + dateStr);
	    		// alrighty then, get periods starting from this date!
	            startCalendarDay = date;
	            Window.alert("Getting Periods starting at: " + dateStr + " for " + numCalendarDays.toString() + " days.");
	            //west.loadPeriods(startCalendarDay, numCalendarDays);
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
	    Listener<BaseEvent> daysListener;
	    daysListener = new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		SimpleComboBox daysf = (SimpleComboBox) be.getSource();
	    		Integer numDays = (Integer) daysf.getSimpleValue();
	    		Window.alert("Will change calendar to run for " + numDays.toString() + " days");
	    		// OK, get periods now for this number of days!
	    		numCalendarDays = numDays;
	    		String dateStr = startCalendarDay.toString();
	            Window.alert("Getting Periods starting at: " + dateStr + " for " + numCalendarDays.toString() + " days.");
	            // TBF: get period explorer to refresh
	            //west.loadPeriods(startCalendarDay, numCalendarDays);
				//String url = "/periods?startPeriods=2006-06-02&dayPeriods; // + (filterText != null ? "?filterText=" + filterText : "");" +
	    		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(startCalendarDay);
	    		String url = "/periods?startPeriods=" + startStr + "&daysPeriods=" + Integer.toString(numCalendarDays);
				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
				DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy = west.pe.getProxy();
				proxy.setBuilder(builder);
				west.pe.loadData();
	    	}
	    };		
	    days.addListener(Events.Change, daysListener);
		north.add(days);
		
		Button scheduleButton = new Button("Schedule");
		scheduleButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent be) {
	    		Button b = (Button) be.getSource();
	    		HashMap<String, Object> keys = new HashMap<String, Object>();
	    		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(startCalendarDay) + " 00:00:00";
	    		keys.put("start", startStr);
	    		keys.put("duration", numCalendarDays);
				JSONRequest.post("/runscheduler", keys,
						new JSONCallbackAdapter() {
							public void onSuccess(JSONObject json) {
								System.out.println("schedule_algo onSuccess");
								//TBF: get period explorer to refresh
							}
						});
			}
		});
		north.add(scheduleButton);
		
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 150);
		northData.setMargins(new Margins(5,5,5,5));

		// to the left, the calendar
		west = new ScheduleCalendar(startCalendarDay, numCalendarDays);
		//west = new PeriodExplorer();
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 500);
		westData.setSplit(true);

		// to the right, the Bin
		east = new ContentPanel();
		east.setHeading("East: Bin");
		east.setBorders(true);
		east.setCollapsible(true);
		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST);
		eastData.setSplit(true);

		// add all the components to this parent panel
		add(north, northData);
		add(west, westData);
		add(east, eastData);

	}
	

}	
	
