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

public abstract class PeriodGroupInfoPanel extends ContentPanel {
	
    // to be set by child
    protected String groupPeriodType;
    protected String url;
    
	// common attributes to Electives and Windows
	protected String header;
	protected String handle;
	protected int id;
    protected Boolean complete;
    
	protected Button save;
	protected Button cancel;
	protected Button delete;
	
	protected Dialog removeDialog;
	protected Button removeApproval;
	
	protected abstract void translateJson(JSONObject json); 
	protected abstract void loadPeriodGroup(); 
	protected abstract void updateGroupPeriod(JSONObject json);
	protected abstract void savePeriodGroup(); 

	
	public PeriodGroupInfoPanel(JSONObject winJson, String url, String groupPeriodType) {
		this.url = url;
		this.groupPeriodType = groupPeriodType;
		translateJson(winJson);
		initLayout();
		initListeners();
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
	    
        // here we insert widgets exclusive to the particular type (Elective or Window)
	    initFormFields(fp);
	    
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
		removeDialog.addText("Remove " + groupPeriodType + "?");
		removeDialog.setButtons(Dialog.YESNO);
		removeDialog.setHideOnButtonClick(true);
		removeApproval = removeDialog.getButtonById(Dialog.YES);
		removeDialog.hide();
		
	    // Here we init the special period explorer for either Electives or Windows
		initGroupPeriodExplorer(fp);
		
	    add(fp);
	    
	    layout();
	}
	
	
	protected abstract void initFormFields(FormPanel fp);
	protected abstract void initGroupPeriodExplorer(FormPanel fp);

	public void initListeners() {
	    save.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		savePeriodGroup();
	    	}
	    });	
	    cancel.addListener(Events.OnClick, new Listener<BaseEvent>() {
	    	@SuppressWarnings("deprecation")
			public void handleEvent(BaseEvent be) {
	    		loadPeriodGroup();
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
			    deletePeriodGroup();
		    }
	    });	    
	}
	
	private void deletePeriodGroup() {
		HashMap<String, Object> keys = new HashMap<String, Object>();
		keys.put("_method", "delete");
	    JSONRequest.post("/" + url + "/" + Integer.toString(id), keys, new JSONCallbackAdapter() {
	            @Override
	            public void onSuccess(JSONObject json) {
	            	// reload all the electives or windows again!
	        		((PeriodGroupsInfoPanel) getParent()).getPeriodGroups();
	            }
	    });			    	
	}
	
	// get object from server -> class attributes -> displayed in widgets
	protected void getPeriodGroup() {
	    JSONRequest.get("/" + url + "/" + Integer.toString(id), new JSONCallbackAdapter() {
            @Override
            public void onSuccess(JSONObject json) {
            	updateGroupPeriod(json);
            }
        });			    	
	}
	
}

