package edu.nrao.dss.client;


import java.util.Date;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

// This class is the new version of the Beta Test's Scheduling Page.

public class Schedule extends ContentPanel { 

	private Integer numCalendarDays = 2;
	// TBF: why can't I seem to create a Date object?
	//long now = 1000;
	private Date startCalendarDay; // = Date((long) now);
	
	public Schedule() {
			super();
			initLayout();
	}	
	
	@SuppressWarnings("unchecked") 
	protected void initLayout() {
		setHeaderVisible(true);
		setLayout(new BorderLayout());
		
		// bells & whistles for this content panel
		setHeading("Schedule Shit"); 
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
	    dt.setFieldLabel("Start Date");
	    Listener<BaseEvent> dtListener;
	    dtListener = new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		DateField dtf = (DateField) be.getSource();
	    		Date date = dtf.getValue();
	    		String dateStr = date.toString();
	    		Window.alert("Will change calendar to start at: " + dateStr);
	    		// alrighty then, get periods starting from this date!
	            startCalendarDay = date;
	            Window.alert("Getting Periods starting at: " + dateStr + " for " + numCalendarDays.toString() + " days.");
	    	}
	    };
	    dt.addListener(Events.Change, dtListener);
	    north.add(dt);
		// 2. Days - when this changes, change the length of the calendar view
		final SimpleComboBox<Integer> days;
		days = new SimpleComboBox<Integer>();
		days.add(1);
		days.add(2);
		days.add(3);
		days.setFieldLabel("Days");
		days.setEditable(false);
		days.setSimpleValue(2);
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

	    	}
	    };		
	    days.addListener(Events.Change, daysListener);
		north.add(days);
		
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 150);
		northData.setMargins(new Margins(5,5,5,5));

		// to the left, the calendar
		ContentPanel west = new ContentPanel();
		west.setHeading("West: Calendar");
		west.setBorders(true);
		// list the periods in a table
		//LayoutContainer lc = new LayoutContainer();
		TableLayout tl = new TableLayout(3);
		tl.setWidth("100%");
		tl.setHeight("100%");
		tl.setBorder(1);
		west.setLayout(tl);
		// start
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		west.add((Widget) new LabelField("Start"), td);
		// dur
		TableData td1 = new TableData();
		td1.setHorizontalAlign(HorizontalAlignment.CENTER);
		west.add((Widget) new LabelField("Duration"), td1);
		// session
		TableData td2 = new TableData();
		td2.setHorizontalAlign(HorizontalAlignment.CENTER);
		west.add((Widget) new LabelField("Session"), td2);

		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 500);
		westData.setSplit(true);

		// to the right, the Bin
		ContentPanel east = new ContentPanel();
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
	
