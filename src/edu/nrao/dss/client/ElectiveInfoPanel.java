package edu.nrao.dss.client;

import java.util.Date;
import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
// comment
import com.google.gwt.json.client.JSONString;

// This class maps directly to a single elective object on the server side.  It replaces 
// what a single row in the elective explorer used to cover, before multiple periods and date 
// ranges were introduced.

// TODO: highlight unsaved changes - see TimeAccounting for one way to do this.
// TODO: pretty it up.

public class ElectiveInfoPanel extends ContentPanel {
	
	
	// elective attributes
	private String header;
	private String handle;
	private int id;
//	private Date start;
//	private int numDays;
//	private Date end;
//	private Double total_time;
//	private Double time_billed;
//	private Double time_remaining;
	private Boolean complete;
	
	// elective UI widgets
//	private DateField dt;
//	private DateField end_dt;
//	private NumberField days;
//	private NumberField total;
//	private NumberField billed;
//	private NumberField remaining;
	private CheckBox cmp; 
	private Button save;
	private Button cancel;
	private Button delete;
	private ElectivePeriodExplorer epe;
	
	protected Dialog removeDialog;
	protected Button removeApproval;
	
	public ElectiveInfoPanel(JSONObject winJson) { 
		translateJson(winJson);
		initLayout();
		initListeners();
	}
	
	private void translateJson(JSONObject winJson) {
		String startDate, endDate;
		
		handle = winJson.get("handle").isString().stringValue();
		
		id = (int) winJson.get("id").isNumber().doubleValue();
		
		complete = winJson.get("complete").isBoolean().booleanValue();
		
		// what are the dates that this elective spans?
		JSONString firstPeriod = winJson.get("firstPeriod").isString();
		if (firstPeriod != null) {
    		String startStr = firstPeriod.stringValue(); 
	    	Date start = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").parse(startStr);
		    startDate = DateTimeFormat.getFormat("yyyy-MM-dd").format(start);
		} else {
			startDate = "None";
		}
		
		JSONString lastPeriod = winJson.get("lastPeriod").isString();
		if (lastPeriod != null) {
    		String endStr = lastPeriod.stringValue(); 
    		Date end = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").parse(endStr);
	    	endDate = DateTimeFormat.getFormat("yyyy-MM-dd").format(end);
		} else {
			endDate = "None";
		}
		
		// the header is a summary: [date range] time, complete (id)
		//header = "Elective [" + startStr + " - " + endStr + "] " + Double.toString(time_remaining) + " Hrs Left; "+ cmpStr + "; (" + Integer.toString(id) + "): ";
		header = "Elective [" + startDate + " - " + endDate + "] (" + Integer.toString(id) + "): ";

	}
	
	private void updateHeading() {
		setHeading(header);
		String color = (complete == true) ? "green" : "red";
		getHeader().setStyleAttribute("color", color);
		if (complete == false) {
			getHeader().setStyleAttribute("font-weight", "bold");
		}
	}
	
	public void initLayout() {
		setLayout(new FitLayout());
		
		setCollapsible(true);
		collapse();
		
		// header
		setHeaderVisible(true);
        updateHeading();
        
		FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
	    
	    cmp = new CheckBox();
	    cmp.setFieldLabel("Complete");
	    cmp.setValue(complete);
	    fp.add(cmp);
	
	    // save, delete, cancel buttons all in a horizontal row
	    FormPanel buttonFp = new FormPanel();
		buttonFp.setLayout(new RowLayout(Orientation.HORIZONTAL));
		buttonFp.setSize(350, 50);
		buttonFp.setHeaderVisible(false);
		buttonFp.setBodyBorder(false);

	    save = new Button();
	    save.setText("Save");
	    buttonFp.add(save, new RowData(0.33, 1, new Margins(0, 4, 0, 4)));
	    
	    cancel = new Button();
	    cancel.setText("Cancel");
	    buttonFp.add(cancel, new RowData(0.33, 1, new Margins(0, 4, 0, 4)));
	    
        delete = new Button();
	    delete.setText("Delete");
	    buttonFp.add(delete, new RowData(0.33, 1, new Margins(0, 4, 0, 4)));
	    
	    fp.add(buttonFp);
	    
		removeDialog = new Dialog();
		removeDialog.setHeading("Confirmation");
		removeDialog.addText("Remove Elective?");
		removeDialog.setButtons(Dialog.YESNO);
		removeDialog.setHideOnButtonClick(true);
		removeApproval = removeDialog.getButtonById(Dialog.YES);
		removeDialog.hide();
		
	    epe = new ElectivePeriodExplorer(id, handle);
	    epe.registerObservers(this);
	    fp.add(epe);
	    
	    add(fp);
	    
	    layout();
	}
	
	
	public void initListeners() {
	    save.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		saveElective();
	    	}
	    });	
	    cancel.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		loadElective();
	    	}
	    });	
	    delete.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		removeDialog.show();
	    	}
	    });	
		removeApproval.addSelectionListener(new SelectionListener<ButtonEvent>() {
		    @Override
		    public void componentSelected(ButtonEvent ce) {
			    deleteElective();
		    }
	    });	    
	}
	
	private void deleteElective() {
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("_method", "delete");
	    JSONRequest.post("/electives/" + Integer.toString(id), keys, new JSONCallbackAdapter() {
	            @Override
	            public void onSuccess(JSONObject json) {
	            	// reload all the electives again!
	        		((ElectivesInfoPanel) getParent()).getElectives();
	            }
	    });			    	
	}
	
	// class attributes -> widgets
	private void loadElective() {
//	    dt.setValue(start);
//	    days.setValue(numDays);
//	    end_dt.setValue(end);
//	    total.setValue(total_time);
//	    billed.setValue(time_billed);
//	    remaining.setValue(time_remaining);
	    cmp.setValue(complete);
	    updateHeading();
	}
	
	// get elective from server -> class attributes -> displayed in widgets
	protected void getElective() {
		GWT.log("getElective");
	    JSONRequest.get("/electives/" + Integer.toString(id), new JSONCallbackAdapter() {
            @Override
            public void onSuccess(JSONObject json) {
            	JSONObject winJson = json.get("elective").isObject();
            	translateJson(winJson);
            	loadElective();
            	// update the electiveed periods
            	epe.loadData();
            }
        });			    	
	}
	
	// send off the current state of the elective to the server
	// then reload the results
	private void saveElective() {
		HashMap<String, Object> keys = new HashMap<String, Object>();
//		String startStr =  DateTimeFormat.getFormat("yyyy-MM-dd").format(dt.getValue());
		keys.put("_method", "put");
//		keys.put("start", startStr);
//		keys.put("duration", days.getValue().intValue()); //Integer.toString(days.getValue().intValue()));
//		keys.put("total_time", total.getValue().doubleValue());
		keys.put("complete", cmp.getValue());
		keys.put("handle", handle);
	    JSONRequest.post("/electives/" + Integer.toString(id), keys, new JSONCallbackAdapter() {
	            @Override
	            public void onSuccess(JSONObject json) {
	            	// get back from the server this elective & display it again
	            	getElective();
	            }
	    });		
	}
	
}
