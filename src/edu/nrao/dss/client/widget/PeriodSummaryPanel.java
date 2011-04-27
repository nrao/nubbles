package edu.nrao.dss.client.widget;

import java.util.HashMap;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import edu.nrao.dss.client.Period;
import edu.nrao.dss.client.TimeAccounting;
import edu.nrao.dss.client.util.JSONCallbackAdapter;
import edu.nrao.dss.client.util.JSONRequest;

public class PeriodSummaryPanel extends ContentPanel {
	
	private Period period;
	private TextField<String> label = new TextField<String>();
	private TextField<String> start = new TextField<String>();
	private TextField<String> dur = new TextField<String>();
	private NumberField hscore = new NumberField();
	private NumberField cscore = new NumberField();
	private CheckBox backup = new CheckBox();
	private CheckBox moc_ack = new CheckBox();
	private TextField<String> state = new TextField<String>();
	private PeriodTimeAccountPanel ta = new PeriodTimeAccountPanel();
	private Button save = new Button();

	public SimpleComboBox<String> periods = new SimpleComboBox<String>();
	public HashMap<String, Integer> periodInfo = new HashMap<String, Integer>();
	
    public PeriodSummaryPanel(Period p) {
    	period = p;
    	initLayout();
    	initListeners();
    }
    
    private void initLayout() {
    	
    	setHeading("Period Summary Panel");
    	
    	setLayout(new RowLayout(Orientation.VERTICAL));
    	
    	LayoutContainer lc = new LayoutContainer();
    	TableLayout tl = new TableLayout(2);
    	tl.setWidth("100%");
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.TOP);    	
    	lc.setLayout(tl);
    	
    	FormPanel periodForm = new FormPanel();
    	periodForm.setHeading("Period Form");
        periodForm.setHeaderVisible(false);
        periodForm.setBorders(false);
        periodForm.setBodyBorder(false);
        
		periods.setTriggerAction(TriggerAction.ALL);
		periods.setFieldLabel("Period");
	
        
    	// field per attribute, roughly:
        setReadOnly("Name", label);
    	setReadOnly("Start", start);
    	setReadOnly("Duration (Hrs)", dur);

    	periodForm.add(periods);
    	periodForm.add(label);
    	periodForm.add(start);
    	periodForm.add(dur);
    	
    	moc_ack.setFieldLabel("MOC Acknowledged");
    	periodForm.add(moc_ack);

    	
    	lc.add(periodForm, td);
    	
    	FormPanel periodForm2 = new FormPanel();
    	periodForm2.setHeading("Period Form2");
    	periodForm2.setHeaderVisible(false);
    	periodForm2.setBorders(false);
    	periodForm2.setBodyBorder(false);
    	
    	// score
    	hscore.setFieldLabel("Historical Score");
    	hscore.setReadOnly(true);
    	cscore.setFieldLabel("Current Score");
    	cscore.setReadOnly(true);
    	// NOTE: this doesn't work
    	hscore.setStyleAttribute("color", "grey");
    	periodForm2.add(hscore);
    	cscore.setStyleAttribute("color", "grey");
    	periodForm2.add(cscore);
    	
    	// backup 
    	backup.setFieldLabel("Backup");
    	backup.setReadOnly(true);
    	// Note: this doesn't work
    	backup.setStyleAttribute("color", "grey");
    	periodForm2.add(backup);
    	
    	setReadOnly("State", state);
    	periodForm2.add(state);
    	
    	// what else?
    	
    	// save the changes?
    	//Button save = new Button();
    	save.setText("Save Period Time Accounting");

        periodForm2.add(save);
        
    	lc.add(periodForm2, td);
    	
    	add(lc);
    	
    	ta.collapse();
    	add(ta);
    	
    	setValues(period);
    	
    	// Time Accounting get's its own form
    	ta.setHeading("Period Time Accounting");
    	ta.setPeriod(period);
    }
    
    private void initListeners() {
		periods.addListener(Events.Valid, new Listener<BaseEvent>() {
		  	public void handleEvent(BaseEvent be) {
		  			GWT.log("period Events.Valid");
		  			// a period has been picked! display it!
	       			updatePeriod();
		  		}
			});    	
    	moc_ack.addListener(Events.OnClick, new Listener<BaseEvent>() {
    		public void handleEvent(BaseEvent be) {
    			//GWT.log("Updating period's moc_ack field", null);
    			String url = "/scheduler/period/" + Integer.toString(period.getId()) + "/toggle_moc";
    			HashMap<String, Object> keys = new HashMap<String, Object>();
    			JSONRequest.post(url, keys,
    					new JSONCallbackAdapter() {
    						public void onSuccess(JSONObject json) {
    						}
    					});		
    		}
    	});    	
    	save.addListener(Events.OnClick, new Listener<BaseEvent>() {
    		public void handleEvent(BaseEvent be) {
    			// The time accounting panel has the only editable widgets
    			// so we leave it up to it.
    			ta.sendUpdates();
    		}
    	});   	
    }
    
    public void setSaveButtonVisible(boolean visible) {
    	save.setVisible(visible);
    }
    
    public void hidePeriodPicker() {
    	periods.setVisible(false);
    }
    
    public void setNewPeriods(String pcode, String sessionName) {
    	clearAll();
        updatePeriodOptions(pcode, sessionName);
    }
    
    private void clearAll() {
    	label.clear();
    	start.clear();
    	dur.clear();
    	hscore.clear();
    	cscore.clear();
    	backup.clear();
    	moc_ack.clear();
    	state.clear();
    	ta.clearAll();     	
    }
    
    public void updatePeriodForm(int periodId) {
    	// get this period from the server and populate the form
        //GWT.log("updatePeriodForm", null);
        // TODO - should pick up timezone from Schedule
    	JSONRequest.get("/scheduler/periods/UTC/" + Integer.toString(periodId)
    		      , new JSONCallbackAdapter() {
    		public void onSuccess(JSONObject json) {
            	// JSON period -> JAVA period
             	Period period = Period.parseJSON(json.get("period").isObject());
             	setPeriod(period);
                //GWT.log("period onSuccess", null);          
    		}
    	});    		
    	
    }
    
	// a session has been selected, so now what are the periods that we can choose from?
	public void updatePeriodOptions(final String pcode, final String sessionName) {
	    //GWT.log("updatePeriodOptions", null);
		JSONRequest.get("/scheduler/sessions/options"
			      , new HashMap<String, Object>() {{
			    	  put("mode", "periods");
			    	  put("pcode", pcode);
			    	  put("session_name", sessionName);
			        }}
			      , new JSONCallbackAdapter() {
			public void onSuccess(JSONObject json) {
				// get ready to populate the sessions codes list
				periods.clearSelections();
				periods.removeAll();
				periodInfo.clear();
				//project_codes.clear();
				JSONArray ps = json.get("periods").isArray();
				JSONArray ids = json.get("period ids").isArray();
				for (int i = 0; i < ps.size(); ++i){
					// the periods drop down is populated w/ descriptions of the periods
					String p = ps.get(i).toString().replace('"', ' ').trim();
					String id = ids.get(i).toString().replace('"', ' ').trim();
					// the labels displayed need to be unique, so we add the period id at the end
					String label = p + " (" + id + ")";
					periods.add(label);
					// we need to save the mapping from 'description' to 'id'
					periodInfo.put(label, Integer.parseInt(id));
					
				}
				//session.setVisible(false);
			}
		});    	
	}
	
	// a period has been selected - so update the period summary panel
	protected void updatePeriod() {
		//GWT.log("updatePeriod", null);
		// what's the period id for chosen period (displayed using time info)?
		String name = periods.getSimpleValue();
		int periodId = periodInfo.get(name);
		// get this period from the server and update this panel
		updatePeriodForm(periodId);
		//setVisible(true);
	}
	
    private void setReadOnly(String label, TextField<String> tf) {
    	tf.setFieldLabel(label);
    	tf.setReadOnly(true);
    	// Note: this doesn't work
    	tf.setStyleAttribute("color", "grey");
    }
    
	public void setParent(TimeAccounting p) {
		ta.setParent(p);
	}  
	
    public void setPeriod(Period period) {
    	setValues(period);
    	ta.setPeriod(period);
    }
    
    private void setValues(Period period) {
        if (period != null) {
        	label.setValue(period.getHandle());
        	start.setValue(period.getStartString());
        	dur.setValue(period.getDurationString());
        	hscore.setValue(period.getHScore());
        	cscore.setValue(period.getCScore());
        	moc_ack.setValue(period.getMocAck());
        	backup.setValue(period.isBackup());
        	state.setValue(period.getState());
        }
    	
    }
    
    public boolean hasChanged() {
    	return ta.hasChanged();
    }
}
