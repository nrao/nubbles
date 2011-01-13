package edu.nrao.dss.client;

import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;

// This class holds widgets for changing the rx schedule (displayed in another panel).

public class RcvrChangePanel extends ContentPanel {
	
	// shift
	private SimpleComboBox<String> shiftFromDate = new SimpleComboBox<String>();
	private DateField shiftToDate = new DateField();
	private Button shift = new Button();
	
	// delete
	private Button delete = new Button();
	private SimpleComboBox<String> deleteDate = new SimpleComboBox<String>();
	
	// add
	private Button add = new Button();
	private DateField addDate = new DateField();
	
	// toggle
	private SimpleComboBox<String> receivers = new SimpleComboBox<String>();
	private SimpleComboBox<String> startDate = new SimpleComboBox<String>();
	private SimpleComboBox<String> endDate = new SimpleComboBox<String>();
	private Button toggle = new Button();
	
	private String[][] diffSchedule;
	
	private ReceiverSchedule parent;
	
	public RcvrChangePanel() {
		initLayout();
		initListeners();
	}
	
	private void initLayout() {
		setLayout(new FlowLayout());
		setHeading("Change Receiver Schedule"); //, or Delete.");
		
		String leftWidth = "150px";
		String rightWidth = "400px";
			
		// Add 
		FormPanel addFp = newFormPanel();
		addFp.setLayout(new TableLayout(2));
		TableData tdLeft = newTableData(leftWidth);
		FormPanel addLeftFp = newFormPanel();
		add.setText("Add Rx Change Date");
		addLeftFp.add(add);
		addFp.add(addLeftFp, tdLeft);
		TableData tdRight = newTableData(rightWidth);
		FormPanel addRightFp = newFormPanel();
		addDate.setFieldLabel("New Date");
		addRightFp.add(addDate);
		addFp.add(addRightFp, tdRight);
		add(addFp);
		
		// Delete
		FormPanel deleteFp = newFormPanel();
		deleteFp.setLayout(new TableLayout(2));
		FormPanel deleteLeftFp = newFormPanel();
		delete.setText("Delete Rx Change Date");
		deleteLeftFp.add(delete);
		deleteFp.add(deleteLeftFp, newTableData(leftWidth));
		FormPanel deleteRightFp = newFormPanel();
		deleteDate.setFieldLabel("Delete Date");
		deleteRightFp.add(deleteDate);
		deleteFp.add(deleteRightFp, newTableData(rightWidth));
		add(deleteFp);

		
		// Shift
		FormPanel shiftFp = newFormPanel();
		shiftFp.setLayout(new TableLayout(2));
		// button
		FormPanel shiftLeftFp = newFormPanel();
		shift.setText("Shift Rx Change Date");
		shiftLeftFp.add(shift);
		TableData shiftTd = newTableData(leftWidth);
		shiftTd.setVerticalAlign(VerticalAlignment.MIDDLE);
		shiftFp.add(shiftLeftFp, shiftTd);
		// dates
		FormPanel shiftRightFp = newFormPanel();
		shiftFromDate.setFieldLabel("Shift From");
		shiftRightFp.add(shiftFromDate);
		shiftToDate.setFieldLabel("Shift To");
		shiftRightFp.add(shiftToDate);
		shiftFp.add(shiftRightFp, newTableData(rightWidth));
		add(shiftFp);
		
		// Toggle
		FormPanel toggleFp = newFormPanel();
		toggleFp.setLayout(new TableLayout(2));
		// button
		FormPanel toggleLeftFp = newFormPanel();
		toggle.setText("Toggle Rx");
		toggleLeftFp.add(toggle);
		TableData toggleTd = newTableData(leftWidth);
		toggleTd.setVerticalAlign(VerticalAlignment.MIDDLE);
		toggleFp.add(toggleLeftFp, toggleTd);
		// dates & rcvr
		FormPanel toggleRightFp = newFormPanel();
		startDate.setFieldLabel("From");
		toggleRightFp.add(startDate);
		endDate.setFieldLabel("To");
		toggleRightFp.add(endDate);
		receivers.setFieldLabel("Receiver");
		toggleRightFp.add(receivers);
		toggleFp.add(toggleRightFp, newTableData(rightWidth));
		add(toggleFp);
	}

	// helper func for setting up panels
	private FormPanel newFormPanel() {
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		fp.setPadding(3);
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

		shift.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
		  		shiftRcvrChangeDate();
			
			}
		});
		
		delete.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
		  		deleteRcvrChangeDate();
			
			}
		});		
		
		add.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
		  		addRcvrChangeDate();
			
			}
		});		
		
		toggle.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
		  		toggleRcvr();
			
			}
		});	
		
		// when startDate is set, default endDate to the same value
		startDate.addListener(Events.Valid, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				// TODO: don't do this until you can figure out how to allow the user to select from options
				// even after a this call to setValue
			    //endDate.setValue(startDate.getValue());
			}
		});			
	}
	
	public void loadSchedule(String[][] diffSchedule) {
		this.diffSchedule = diffSchedule;
		
		// TODO: how to condense this with a function call?
		startDate.clearSelections();
		endDate.clearSelections();
		deleteDate.clearSelections();
		shiftFromDate.clearSelections();
		startDate.removeAll();
		endDate.removeAll();
		deleteDate.removeAll();
		shiftFromDate.removeAll();
		
		for (int i = 0; i < diffSchedule.length; i++) {
			String dt = diffSchedule[i][0];
			startDate.add(dt);
			endDate.add(dt);
			deleteDate.add(dt);
			shiftFromDate.add(dt);
		}
	}
	
	public void loadRcvrs(String[] rcvrs) {
	    receivers.clearSelections();
	    receivers.removeAll();
	    for (int i = 0; i < rcvrs.length; i++) {
	    	String rcvr = rcvrs[i];
	    	receivers.add(rcvr);
	    }
	}
	
	// utility for formatting the drop down date values before sending via JSON
	private String getDropDownDate(SimpleComboBox<String> dropDown) {
		// make sure we have valid inputs
		String from_date = dropDown.getSimpleValue();
		if (from_date == null || from_date.compareTo("") == 0) {
			return null;
		}
		// TODO: rcvr dts in DB are datetimes - the time is set to this.  WTF.
        from_date += " 16:00:00";
        return from_date;
	}
	
	// utilitly for formatting the date picker values before sending via JSON
	private String getCalendarDate(DateField dt) {
		if (dt.getValue() == null) {
			return null;
		}
		String date = DateTimeFormat.getFormat("MM/dd/yyyy").format(dt.getValue());
		// TODO: rcvr dts in DB are datetimes - the time is set to this.  WTF.
		return date  + " 16:00:00";
	}
	
	private void shiftRcvrChangeDate() {

		// make sure we have valid inputs
		String from_date = getDropDownDate(shiftFromDate);
		String to_date = getCalendarDate(shiftToDate);
		if (from_date == null || to_date == null) {
			return;
		}
		// don't bother doing anything if the dates haven't changed or inputs arent valid
		if (from_date.compareTo(to_date) == 0) {
			return;
		}
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("from", from_date);
		keys.put("to",   to_date);
		
		JSONRequest.post("/receivers/shift_date", keys  
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// reload the calendar
				parent.updateRcvrSchedule();
			}
		});	
	}
	
	private void toggleRcvr() {
		String startStr = getDropDownDate(startDate); //startDate.getSimpleValue() + " 16:00:00";
        String endStr = getDropDownDate(endDate); //endDate.getSimpleValue() + " 16:00:00";
        
        // check for valid inputs
        if (startStr == null) {
        	return;
        }
        if (endStr == null) {
        	// make it a range of one day
        	endStr = startStr;
        }
        
   		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("from", startStr);
		keys.put("to", endStr);
        keys.put("rcvr", receivers.getSimpleValue());	
		JSONRequest.post("/receivers/toggle_rcvr", keys  
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// reload the calendar
				parent.updateRcvrSchedule();
			}
		});	       
	}
	
	private void addRcvrChangeDate() {

		String to_date   = DateTimeFormat.getFormat("MM/dd/yyyy").format(addDate.getValue())  + " 16:00:00"; //%m/%d/%Y
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("startdate",   to_date);
		
		GWT.log("adding: " + to_date, null);
		JSONRequest.post("/receivers/add_date", keys  
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// reload the calendar
				parent.updateRcvrSchedule();
			}
		});	
	}
	
	private void deleteRcvrChangeDate() {

		// make sure we have valid inputs
        String from_date = getDropDownDate(deleteDate);
        if (from_date == null) {
        	return;
        }
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("startdate", from_date);
		
		JSONRequest.post("/receivers/delete_date", keys  
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// reload the calendar
				parent.updateRcvrSchedule();
			}
		});	
	}
	
	public void setParent(ReceiverSchedule rs) {
		parent = rs;
	}
 	
}
