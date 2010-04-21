package edu.nrao.dss.client;

import java.util.Date;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.event.Listener;

public class RcvrSchdGridPanel extends ContentPanel {
	
    private RcvrScheduleGrid grid = new RcvrScheduleGrid();
    private DateField start = new DateField();
    private SimpleComboBox<String> numMonths = new SimpleComboBox<String>();
    private CheckBox showMnt = new CheckBox();
    private Button update = new Button();
    
    private ReceiverSchedule parent;
    
	public RcvrSchdGridPanel() {
		initLayout();
		initListeners();
	}
	
	private void initLayout() {	
	
		setLayout(new RowLayout(Orientation.VERTICAL));
	
		//setScrollMode(Scroll.AUTO);
		setBorders(false);
		setHeaderVisible(true);
		setHeading("Receiver Schedule");
		
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		//fp.setLayout(new RowLayout(Orientation.HORIZONTAL));

		// when to start the schedule
		start.setFieldLabel("Start Date");
		fp.add(start);
		
		// for how long?
		numMonths.setTriggerAction(TriggerAction.ALL);
		numMonths.setFieldLabel("# Months");
		numMonths.add("1");
		numMonths.add("2");
		numMonths.add("3");
		numMonths.add("4");
		numMonths.add("5");
		numMonths.add("6");		
		fp.add(numMonths);
		
		// include maintenance days?
		showMnt.setFieldLabel("Show Maintenance Dates");
		fp.add(showMnt);
	
		update.setText("Update");
		fp.add(update);
		
		add(fp);
		
	    add(grid);
    
	}
	
	private void initListeners() {
		
//	    day.addListener(Events.Valid, new Listener<BaseEvent>() {
//	    	public void handleEvent(BaseEvent be) {
//	            //startCalendarDay = dt.getValue();
//	    		Date dt = day.getValue();
//	    		setDay(DATE_FORMAT.format(dt));
//;	    	}
//	    });
	    
	    update.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		// update the calendar using the controls
	    		getRcvrSchedule();
	    		
	    	}
	    });
	    
	    showMnt.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		// update the calendar using the controls
	    		//getRcvrSchedule();
	    		grid.setShowMaintenance(showMnt.getValue());
	    		;
	    		
	    	}
	    });
	}	
	
	public void setupCalendar() {
		
		// set default values for controls
		start.setValue(new Date()); // today
		numMonths.setSimpleValue("1");
		showMnt.setValue(false);
		
		// update the calendar
		getRcvrSchedule();
	}
	
	public void getRcvrSchedule() {
	    // update the calendar using the controls
		int numDays = 30 * Integer.parseInt(numMonths.getSimpleValue());
		parent.getRcvrSchedule(start.getValue(), numDays, showMnt.getValue());
	}
	
	public void loadSchedule(int rows, int cols, String[] headers, String[][] schedule) {
		grid.loadSchedule(rows, cols, headers, schedule);
	}
	
	public void setParent(ReceiverSchedule rs) {
		parent = rs;
	}
	
	public void setMaintenanceDays(String[] maintenanceDays) {
		grid.setMaintenanceDays(maintenanceDays);
	}
}
