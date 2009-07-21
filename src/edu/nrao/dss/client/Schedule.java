package edu.nrao.dss.client;



import java.util.Date;
import java.util.HashMap;

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
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;

// This class is the new version of the Beta Test's Scheduling Page.

public class Schedule extends ContentPanel {
	
	//private PeriodExplorer west;
	public ScheduleCalendar west;
	private ContentPanel east;

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
		dt.setToolTip("Set the schedule and display start day");
	    dt.addListener(Events.OnFocus, new Listener<BaseEvent>() {
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
		days.setToolTip("Set the schedule and display duration");

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

	    final Button updateBtn;
	    updateBtn = new Button("Update");
		updateBtn.setToolTip("Modify the schedule and display range of calendar");
	    updateBtn.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	            updateCalendar();
	    	}
	    });
		north.add(updateBtn);
		
		Button scheduleButton = new Button("Schedule");
		scheduleButton.setToolTip("Schedule the telescope over the specified calendar range");
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

		// to the left, the calendar
		west = new ScheduleCalendar(startCalendarDay, numCalendarDays);
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 550);
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
	
    private void updateCalendar() {
		String startStr = DateTimeFormat.getFormat("yyyy-MM-dd").format(startCalendarDay);
		String url = "/periods?startPeriods=" + startStr + "&daysPeriods=" + Integer.toString(numCalendarDays);
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		DynamicHttpProxy<BasePagingLoadResult<BaseModelData>> proxy = west.pe.getProxy();
		proxy.setBuilder(builder);
		west.pe.loadData();
    }
}	
	
