package edu.nrao.dss.client;


import java.util.Date;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.Window;

public class Schedule extends ContentPanel { 
	
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
		// 1. Date - when this changes, change the calendar view
	    DateField dt = new DateField();
	    dt.setFieldLabel("Start Date");
	    Listener<BaseEvent> listener;
	    listener = new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		DateField dtf = (DateField) be.getSource();
	    		Date date = dtf.getValue();
	    		String dateStr = date.toString();
	    		Window.alert("Will change calendar to start at: " + dateStr);
	    		// alrighty then, get periods starting from this date!
	    	    
	    	}
	    };
	    dt.addListener(Events.Change, listener);
	    north.add(dt);
		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 150);
		northData.setMargins(new Margins(5,5,5,5));
				
		// to the left, the calendar
		ContentPanel west = new ContentPanel();
		west.setHeading("West: Calendar");
		west.setBorders(true);
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
	
