package edu.nrao.dss.client;

import java.util.Date;
import java.util.HashMap;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
// comment

// This class maps directly to a single window object on the server side.  It replaces 
// what a single row in the window explorer used to cover, before multiple periods and date 
// ranges were introduced.

// TODO: highlight unsaved changes - see TimeAccounting for one way to do this.
// TODO: pretty it up.

public class WindowInfoPanel extends ContentPanel {
	
	
	// window attributes
	private String header;
	private String handle;
	private int id;
	private Date start;
	private int numDays;
	private Date end;
	private Double total_time;
	private Double time_billed;
	private Double time_remaining;
	private Boolean complete;
	
	// window UI widgets
	private DateField dt;
	private DateField end_dt;
	private NumberField days;
	private NumberField total;
	private NumberField billed;
	private NumberField remaining;
	private CheckBox cmp; 
	private Button save;
	private Button cancel;
	private Button delete;
	private WindowedPeriodExplorer wpe;
	
	protected Dialog removeDialog;
	protected Button removeApproval;
	
	public WindowInfoPanel(JSONObject winJson) { 
		translateJson(winJson);
		initLayout();
		initListeners();
	}
	
	private void translateJson(JSONObject winJson) {
		
		handle = winJson.get("handle").isString().stringValue();
		
		id = (int) winJson.get("id").isNumber().doubleValue();
		
		String startStr = winJson.get("start").isString().stringValue();
		start = DateTimeFormat.getFormat("yyyy-MM-dd").parse(startStr);
		
		String endStr = winJson.get("end").isString().stringValue();
		end = DateTimeFormat.getFormat("yyyy-MM-dd").parse(endStr);
		
		numDays = (int) winJson.get("duration").isNumber().doubleValue(); 
		
		total_time = winJson.get("total_time").isNumber().doubleValue();
		time_billed = winJson.get("time_billed").isNumber().doubleValue();
		time_remaining = winJson.get("time_remaining").isNumber().doubleValue();
		
		complete = winJson.get("complete").isBoolean().booleanValue();
		
		String cmpStr = (complete == true) ? "Complete" : "Not Complete";
		
		// the header is a summary: [date range] time, complete (id)
		header = "Window [" + startStr + " - " + endStr + "] " + Double.toString(time_remaining) + " Hrs Left; "+ cmpStr + "; (" + Integer.toString(id) + "): ";
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
		
	    dt = new DateField();
	    dt.setValue(start);
	    dt.setFieldLabel("Start Date");
	    fp.add(dt);
	    
	    days = new NumberField();
	    days.setFieldLabel("Days");
	    days.setValue(numDays);
	    fp.add(days);
	    
	    end_dt = new DateField();
	    end_dt.setValue(end);
	    end_dt.setFieldLabel("End Date");
	    end_dt.setReadOnly(true);
	    fp.add(end_dt);
	    
	    total = new NumberField();
	    total.setValue(total_time);
	    total.setFieldLabel("Total Time (Hrs)");
	    fp.add(total);
	    
	    billed = new NumberField();
	    billed.setValue(time_billed);
	    billed.setFieldLabel("Billed Time (Hrs)");
	    billed.setReadOnly(true);
	    fp.add(billed);
	    
	    remaining = new NumberField();
	    remaining.setValue(time_remaining);
	    remaining.setFieldLabel("Time Remaining (Hrs)");
	    remaining.setReadOnly(true);
	    fp.add(remaining);
	    
	    cmp = new CheckBox();
	    cmp.setFieldLabel("Complete");
	    cmp.setValue(complete);
	    fp.add(cmp);
	    
	    save = new Button();
	    save.setText("Save");
	    fp.add(save);
	    
	    cancel = new Button();
	    cancel.setText("Cancel");
	    fp.add(cancel);
	    
        delete = new Button();
	    delete.setText("Delete");
	    fp.add(delete);
	    
		removeDialog = new Dialog();
		removeDialog.setHeading("Confirmation");
		removeDialog.addText("Remove Window?");
		removeDialog.setButtons(Dialog.YESNO);
		removeDialog.setHideOnButtonClick(true);
		removeApproval = removeDialog.getButtonById(Dialog.YES);
		removeDialog.hide();
		
	    wpe = new WindowedPeriodExplorer(id, handle);
	    wpe.registerObservers(this);
	    fp.add(wpe);
	    
	    add(fp);
	    
	    layout();
	}
	
	
	public void initListeners() {
	    save.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		saveWindow();
	    	}
	    });	
	    cancel.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		loadWindow();
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
			    deleteWindow();
		    }
	    });	    
	}
	
	private void deleteWindow() {
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("_method", "delete");
	    JSONRequest.post("/windows/" + Integer.toString(id), keys, new JSONCallbackAdapter() {
	            @Override
	            public void onSuccess(JSONObject json) {
	            	// reload all the windows again!
	        		((WindowsInfoPanel) getParent()).getWindows();
	            }
	    });			    	
	}
	
	// class attributes -> widgets
	private void loadWindow() {
	    dt.setValue(start);
	    days.setValue(numDays);
	    end_dt.setValue(end);
	    total.setValue(total_time);
	    billed.setValue(time_billed);
	    remaining.setValue(time_remaining);
	    cmp.setValue(complete);
	    updateHeading();
	}
	
	// get window from server -> class attributes -> displayed in widgets
	protected void getWindow() {
	    JSONRequest.get("/windows/" + Integer.toString(id), new JSONCallbackAdapter() {
            @Override
            public void onSuccess(JSONObject json) {
            	JSONObject winJson = json.get("window").isObject();
            	translateJson(winJson);
            	loadWindow();
            	// update the windowed periods
            	wpe.loadData();
            }
        });			    	
	}
	
	// send off the current state of the window to the server
	// then reload the results
	private void saveWindow() {
		HashMap<String, Object> keys = new HashMap<String, Object>();
		String startStr =  DateTimeFormat.getFormat("yyyy-MM-dd").format(dt.getValue());
		keys.put("_method", "put");
		keys.put("start", startStr);
		keys.put("duration", days.getValue().intValue()); //Integer.toString(days.getValue().intValue()));
		keys.put("total_time", total.getValue().doubleValue());
		keys.put("complete", cmp.getValue());
		keys.put("handle", handle);
	    JSONRequest.post("/windows/" + Integer.toString(id), keys, new JSONCallbackAdapter() {
	            @Override
	            public void onSuccess(JSONObject json) {
	            	// get back from the server this window & display it again
	            	getWindow();
	            }
	    });		
	}
	
}
