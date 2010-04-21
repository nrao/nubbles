package edu.nrao.dss.client;

import java.util.HashMap;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;

public class RcvrChangePanel extends ContentPanel {

	private SimpleComboBox<String> periods = new SimpleComboBox<String>();
	private TextField<String> finalRcvrs = new TextField<String>();
	private TextField<String> goingUpRcvrs = new TextField<String>();
	private TextField<String> goingDownRcvrs = new TextField<String>();
	private DateField shiftDate = new DateField();
	private Button shift = new Button();
	private Button delete = new Button();
	
	private String[][] diffSchedule;
	
	private ReceiverSchedule parent;
	
	public RcvrChangePanel() {
		initLayout();
		initListeners();
	}
	
	private void initLayout() {
		setLayout(new FlowLayout());
		setHeading("View Details, Shift Date"); //, or Delete.");
		
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		
		periods.setFieldLabel("Change Date");
		periods.setTriggerAction(TriggerAction.ALL);
		fp.add(periods);
		
		finalRcvrs.setFieldLabel("Rcvrs available at end of day");
		finalRcvrs.setReadOnly(true);
		fp.add(finalRcvrs);
		
		goingUpRcvrs.setFieldLabel("Rcvrs going up");
		goingUpRcvrs.setReadOnly(true);
		fp.add(goingUpRcvrs);
		
		goingDownRcvrs.setFieldLabel("Rcvrs going down");
		goingDownRcvrs.setReadOnly(true);
		fp.add(goingDownRcvrs);
		
		shiftDate.setFieldLabel("Shift Change Date");
		fp.add(shiftDate);
		
		shift.setText("Shift Date");
		fp.add(shift);
		
		// TODO
		delete.setText("Delete");
		fp.add(delete);
		
		add(fp);
	}
	
	private void initListeners() {
		periods.addListener(Events.Valid, new Listener<BaseEvent>() {
		  	public void handleEvent(BaseEvent be) {
		  		// go git it
		  		setPeriod(periods.getSimpleValue());
		   	}
		});

		// TODO: would like to do away with the shift button, but this event
		// is firing twice!!!
//		shiftDate.addListener(Events.Valid, new Listener<BaseEvent>() {
//		  	public void handleEvent(BaseEvent be) {
//		  		// TODO: why are we getting called twice???
//		  		// confirm that they want to shift this date
//		  		GWT.log("calling shiftRcvrChangeDate", null);
//		  		shiftRcvrChangeDate();
//		   	}
//		});		

		shift.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
		  		// confirm that they want to shift this date
		  		GWT.log("calling shiftRcvrChangeDate", null);
		  		shiftRcvrChangeDate();
			
			}
		});
		
		delete.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
		  		// confirm that they want to shift this date
		  		GWT.log("calling deleteRcvrChangeDate", null);
		  		deleteRcvrChangeDate();
			
			}
		});		
	}
	
	public void loadSchedule(String[][] diffSchedule) {
		this.diffSchedule = diffSchedule;
		
		periods.clearSelections();
		periods.removeAll();
		for (int i = 0; i < diffSchedule.length; i++) {
			periods.add(diffSchedule[i][0]);
		}
	}
	
	private void setPeriod(String day) {
		// set the up and down text boxes according to the day
		for (int i = 0; i < diffSchedule.length; i++) {
			if (day.compareTo(diffSchedule[i][0]) == 0) {
				goingUpRcvrs.setValue(diffSchedule[i][1]);
				goingDownRcvrs.setValue(diffSchedule[i][2]);
				finalRcvrs.setValue(diffSchedule[i][3]);
			}
		}
		
	}
	
	private void shiftRcvrChangeDate() {

		// make sure we have valid inputs
		String from_date = periods.getSimpleValue();
		if (from_date == null || from_date.compareTo("") == 0) {
			return;
		} else {
			from_date += " 16:00:00";
		}
		String to_date   = DateTimeFormat.getFormat("MM/dd/yyyy").format(shiftDate.getValue())  + " 16:00:00"; //%m/%d/%Y
		
		// don't bother doing anything if the dates haven't changed
		if (from_date.compareTo(to_date) == 0) {
			GWT.log("not shifting: same dates", null);
			return;
		}
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("from", from_date);
		keys.put("to",   to_date);
		
		GWT.log("shifting: " + from_date + " to " + to_date, null);
		JSONRequest.post("/receivers/shift_date", keys  
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				
				GWT.log("rcvr shift date success", null);
				GWT.log(json.toString(), null);
				
				// reload the calendar
				parent.updateRcvrSchedule();
			}
		});	
	}
	
	private void deleteRcvrChangeDate() {

		// make sure we have valid inputs
		String from_date = periods.getSimpleValue();
		if (from_date == null || from_date.compareTo("") == 0) {
			return;
		} else {
			from_date += " 16:00:00";
		}
		
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("startdate", from_date);
		
		GWT.log("deleting: " + from_date, null);
		JSONRequest.post("/receivers/delete_date", keys  
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				
				GWT.log("rcvr delete date success", null);
				GWT.log(json.toString(), null);
				
				// reload the calendar
				parent.updateRcvrSchedule();
			}
		});	
	}	
	public void setParent(ReceiverSchedule rs) {
		parent = rs;
	}
 	
}
