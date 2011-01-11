package edu.nrao.dss.client;

import java.util.Date;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.event.Listener;

// class is responsible for the panel that specifies what part of the rx schedule
// to request, and uses RcvrScheduleGrid for displaying that schedule

public class RcvrSchdGridPanel extends ContentPanel {

	// displays rx schedule
    private RcvrScheduleGrid grid = new RcvrScheduleGrid();
    
    // widgets for sending request for rx schedule
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
		
		FormPanel fp = newFormPanel();
		fp.setLayout(new TableLayout(2));
		
		// button
		FormPanel leftFp = newFormPanel();
		update.setText("Update");
		leftFp.add(update);		
		TableData leftTd = newTableData("100px");
		leftTd.setVerticalAlign(VerticalAlignment.MIDDLE);
		fp.add(leftFp, leftTd);
		
		// dates & such
		FormPanel rightFp = newFormPanel();
		
		// when to start?
		start.setFieldLabel("Start Date");
		rightFp.add(start);
		
		// for how long?
		numMonths.setTriggerAction(TriggerAction.ALL);
		numMonths.setFieldLabel("# Months");
		numMonths.add("1");
		numMonths.add("2");
		numMonths.add("3");
		numMonths.add("4");
		numMonths.add("5");
		numMonths.add("6");		
		rightFp.add(numMonths);
		
		// include maintenance days?
		showMnt.setFieldLabel("Maint. Days");
		rightFp.add(showMnt);
		
		fp.add(rightFp, newTableData("500px"));
		add(fp);
		
	    add(grid);
    
	}
	// helper func for setting up panels
	private FormPanel newFormPanel() {
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		return fp;
	}
	
	// helper func for setting up panels
	private TableData newTableData(String px) {
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.TOP);
		// TODO: why must I do this, just to get the two forms to share space?
		td.setColspan(1);
		td.setWidth(px);		
		return td;
	}
	
	private void initListeners() {
	    update.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
	    		// update the calendar using the controls
	    		getRcvrSchedule();
	    		
	    	}
	    });
	    
	    showMnt.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	public void handleEvent(BaseEvent be) {
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
	
	public void loadSchedule(String[] headers, int rows, int cols, String[][] schedule, String[][] diffSchd) {
		grid.loadSchedule(headers, rows, cols, schedule, diffSchd);
	}
	
	public void setParent(ReceiverSchedule rs) {
		parent = rs;
	}
	
	public void setMaintenanceDays(String[] maintenanceDays) {
		grid.setMaintenanceDays(maintenanceDays);
	}
}
